package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.InterrupcaoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InterrupcaoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;


/**
 *    <p>Contém as regras de negócio para cadastrar interrupções para as bibliotecas do sistema.<br/>
 *    Interrupções são períodos em que a biblioteca não vai funcionar e nenhum empréstimo já feito 
 *    ou que venha a ser criado, pode vencer na data da interrupção.</p>
 *
 *    <p>É importante observar que podem ocorrer problemas de concorrência se for cadastrada uma 
 *    interrução no mesmo momento em que um empréstimo estaja sendo devolvido ou renovado. A solução 
 *    foi desativar o serviço de empréstimos das bibliotecas cuja interrupção será gerada.</p>
 *
 *    <p><strong> ***** I M P O R T A N T E ***** </strong>: Esse processador é bastante pesado de ser executado. A quantidade de empréstimos ativos é no ordem de N = 1000.
 *    Então são alterados O(1000) empréstimos, criadas O(1000) prorrogações, enviados O(1000) e-mails, etc...
 *    
 *    Tem que ser bastante otimizado, o hibernate não dá nem para o rastro.  
 *    </p>
 *
 * @author jadson
 * @since 10/12/2009
 * @version 1.0 criacao da classe
 * @version 1.1 Jadson 07/08/2012 - Alteração das regras para bloquear novos empréstimos e renovação durante o cadastramento da interrução.
 * @version 2.0 Jadson 03/09/2013 - Otimização dástrica desse processador. O código de antes era inviável para a quantidade de empréstimos que precisavam ser prorrogados.
 */
public class ProcessadorCadastraInterrupcaoBiblioteca extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastraInterrupcaoBiblioteca personalMov = (MovimentoCadastraInterrupcaoBiblioteca) mov;
		
		/// Contém dos dados da interrupção a ser cadastrada  ///
		InterrupcaoBiblioteca interrupcaoOriginal = personalMov.getInterrupcao();
		Date inicioInterrupcao = personalMov.getDataInicio();
		Date fimInterrupcao = personalMov.getDataFim();
		
		InterrupcaoBibliotecaDao interrupcaoDao  = null;
		
		try {
			
			interrupcaoDao = getDAO(InterrupcaoBibliotecaDao.class, personalMov);
			
			
			/// crias as interruções, essa é a parte fácil ! ( a partir desse momento nenhum empréstimo vai cair mais nesses dias)///
			List<InterrupcaoBiblioteca> interrupcoesCriadas = criaInterrupcoesDoPeriodo(personalMov, interrupcaoOriginal, inicioInterrupcao, fimInterrupcao, interrupcaoDao);	
			
			
			// Agora vem a parte difícil ....
			if ( interrupcoesCriadas.size() > 0) {
			
				List <Emprestimo> emprestimos = interrupcaoDao.findAllEmprestimosAtivosByBibliotecasEPeriodo(interrupcaoOriginal.getBibliotecas()
						, CalendarUtils.configuraTempoDaData(inicioInterrupcao, 0, 0, 0, 0), CalendarUtils.configuraTempoDaData(fimInterrupcao, 23, 59, 59, 999));
	
				
				/* ****************************************************************************
				 * Otimização para transformar M^2 em 2M updates no banco, onde M = nº de bibliotecas do sistema //
				 * ****************************************************************************/
				Set<Biblioteca> bibliotecasInterrupcaoNaoRepetidas = new HashSet<Biblioteca>();
				
				for (InterrupcaoBiblioteca interrupcaoCriada : interrupcoesCriadas) {
					bibliotecasInterrupcaoNaoRepetidas.addAll(interrupcaoCriada.getBibliotecas());
				}
				
				
				
				/* ****************************************************************************
				 * Para diminuir a quantidade consultas ao banco mantém as interrupções futuras das bibliotecas em memorária
				 * ****************************************************************************/
				Map<Integer, List <InterrupcaoBiblioteca>> cacheInterrupcoesFuturas = new TreeMap<Integer, List <InterrupcaoBiblioteca>>();
				
				populaCacheInterrupcoesPorBiblioteca(interrupcaoDao, interrupcoesCriadas, bibliotecasInterrupcaoNaoRepetidas, cacheInterrupcoesFuturas);
				
				
				
				/* ****************************************************************************
				 *  Prorroga a data dos empréstimos individualmente para cada biblioteca      *
				 *  É normal retornar aqui em torno de N = 10.000 empréstimo                  *
				 *  São realizados O(M) atualizações no banco, onde M = número de bibliotecas * 
				 *  do sistema. em torno de 20 bibliotecas                                    *
				 *  Em cada atualização são atualizadas O(N/M) linhas da tabela empréstimos   *
				 *  Ex.:  10.000/20 = 500 empréstimso em média, porém é mediana vai está bem  *
				 *  longe da média, pois os empréstimos da central vão ser sempre em uma      *
				 *  quantidade bem maior                                                      * 
				 * ****************************************************************************/			
				prorrogaEmpretimosDoPeriodo(interrupcaoDao, inicioInterrupcao, fimInterrupcao, interrupcoesCriadas, bibliotecasInterrupcaoNaoRepetidas, cacheInterrupcoesFuturas);
				
				
				/* *****************************************************************************
				 *  Cria as prorrogações para saber porque a data do empréstimo mudou          *
				 *                                                                             *
				 *  São realizados O(N) "inserts" no banco, onde N = número de empréstimos       * 
				 *  prorrogáveis. em torno de 10.000. Porém em Batch para otimizar, inserir    * 
				 *  objetinhos com hibernate passa longe daqui.                                *
				 * *****************************************************************************/		
				criaAsProrrogacoesEmprestimo(mov, interrupcaoDao, emprestimos, fimInterrupcao, interrupcoesCriadas, bibliotecasInterrupcaoNaoRepetidas, cacheInterrupcoesFuturas);
				
				return emprestimos; // para mandar e-mail para o usuário, o prazo já foi atualizado para o novo depois da prorrogação.
				
			} 
			
		} finally {
			if (interrupcaoDao != null) interrupcaoDao.close();  
		}

		return null;
	}




	/**
	 * Cria as interrupções da data de início até a data final passa pelo usuário.
	 * 
	 * @param personalMov
	 * @param interrupcaoOriginal
	 * @param inicioInterrupcao
	 * @param fimInterrupcao
	 * @param mensagemRetorno
	 * @param interrupcaoDao
	 * @return
	 * @throws DAOException
	 *  
	 * <p> Criado em:  29/08/2013  </p>
	 *
	 * 
	 */
	private List<InterrupcaoBiblioteca> criaInterrupcoesDoPeriodo(MovimentoCadastraInterrupcaoBiblioteca personalMov, InterrupcaoBiblioteca interrupcaoOriginal, Date inicioInterrupcao,
			Date fimInterrupcao, InterrupcaoBibliotecaDao interrupcaoDao) throws DAOException {
		
		System.out.println("Criando as interruções  ........................................................... ");
		
		List<InterrupcaoBiblioteca> interrupcoesCriadas = new ArrayList<InterrupcaoBiblioteca> ();
		
		// as bibliotecas escolhidas pelo usuário, não necessariamente vão ser cradas porque já pode existir interruções para elas
		List<Integer> idsBiblitocasASeremCriadasInterrupcoes = new ArrayList<Integer>();
		
		for (Biblioteca biblioteca : interrupcaoOriginal.getBibliotecas()) {
			idsBiblitocasASeremCriadasInterrupcoes.add(biblioteca.getId());
		}
		
		/* *****************************************************
		 * Retorna um array em que:
		 * [0] data da interrupção
		 * [1] id da biblioteca da interrupção
		 */
		List<Object[]> interrupcoesJaCadastradas = interrupcaoDao.findAllDatasInterrupcoesCadastradasParaAsBibliotecasNoPerioco (idsBiblitocasASeremCriadasInterrupcoes, inicioInterrupcao, fimInterrupcao);
		
		// Data de interação no laço
		Calendar dataInteracao = Calendar.getInstance();
		dataInteracao.setTime(inicioInterrupcao);
		
		boolean estaDentroDoPeriodo = true;
		
		while (estaDentroDoPeriodo){  // para dataIniciao até dataFim
		
			List<Integer> idBibliotecaJaPossuemInterrupcao = retornaBibliotecasOndeJaExisteInterrupcaoCadastrada(interrupcoesJaCadastradas, idsBiblitocasASeremCriadasInterrupcoes, dataInteracao.getTime() );
			
			List<Biblioteca> bibliotecasSemInterrupcoes	= new ArrayList<Biblioteca>();		
			
			for (Biblioteca biblioteca : interrupcaoOriginal.getBibliotecas()) {
				if( ! idBibliotecaJaPossuemInterrupcao.contains(biblioteca.getId()) ){
					bibliotecasSemInterrupcoes.add(biblioteca);
				}
			} 
			
			
			////////////////////////////////////////////////////////////////////////////////
			
			
			// cria interrupão na data digitada pelo usuário para a biblioteca que ainda não possuem
			
			InterrupcaoBiblioteca interrupcaoCriada = new InterrupcaoBiblioteca();
			interrupcaoCriada.setBibliotecas(bibliotecasSemInterrupcoes);
			interrupcaoCriada.setMotivo(interrupcaoOriginal.getMotivo());
			interrupcaoCriada.setData(dataInteracao.getTime());
			
			interrupcaoDao.createNoFlush(interrupcaoCriada); // salva a nova interrupção no banco
			
			interrupcoesCriadas.add(interrupcaoCriada);
			
			dataInteracao.add(Calendar.DAY_OF_MONTH, 1); // PULA PARA O PRÓXIMO DIA ( DIA++ )
			
			// Condição de parada do while:  Se já chegou na data fim das interrupões
			if (personalMov.getDataFim() == null || CalendarUtils.estorouPrazo(fimInterrupcao, dataInteracao.getTime()))
				estaDentroDoPeriodo = false;
		}
		
		interrupcaoDao.getSession().flush(); // persite no banco as interruções
		
		return interrupcoesCriadas;
	}





	/**
	 * Prorroga os empréstimos no banco, todos de uma única vez e com um único, sql por biblioteca
	 * 
	 * Por bibliotecas pode ter dias diferentes de funcionamento, então o calculo não pode ser feito para todas de uma vez.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 *  
	 * <p> Criado em:  29/08/2013  </p>
	 *
	 *
	 */
	private void prorrogaEmpretimosDoPeriodo(InterrupcaoBibliotecaDao interrupcaoDao, Date inicioInterrupcao, Date fimInterrupcao,  List<InterrupcaoBiblioteca> interrupcoesASeremCriadas,
			Set<Biblioteca> bibliotecasInterrupcaoNaoRepetidas, Map<Integer, List <InterrupcaoBiblioteca>> cacheInterrupcoesFuturas) throws DAOException {
		
		System.out.println("Prorrogando os empréstimos ........................................................... ");
		
		
		for (Biblioteca biblioteca : bibliotecasInterrupcaoNaoRepetidas) {
			
			List <InterrupcaoBiblioteca> interrupcoesFuturasBiblioteca = cacheInterrupcoesFuturas.get(biblioteca.getId());
			
			interrupcaoDao.update(" UPDATE biblioteca.emprestimo emprestimo set prazo = ? "+ 
					" FROM ( "+
					"	SELECT e.id_emprestimo  "+ 
					"	FROM biblioteca.emprestimo e  "+ 
					"	INNER JOIN biblioteca.material_informacional m ON m.id_material_informacional = e.id_material  "+ 
					"	WHERE e.situacao = "+Emprestimo.EMPRESTADO+" AND m.id_biblioteca = ? AND e.prazo between ? and ?  "+ 
					") interna  "+ 
					" WHERE interna.id_emprestimo = emprestimo.id_emprestimo ", new Object[]{ 
						CalendarUtils.configuraTempoDaData(  CirculacaoUtil.calculaProximoDiaUtilInterrupcao(fimInterrupcao, biblioteca, interrupcoesFuturasBiblioteca) , 23, 59, 59, 999)
						, biblioteca.getId()
						, CalendarUtils.configuraTempoDaData(inicioInterrupcao, 0, 0, 0, 0), CalendarUtils.configuraTempoDaData(fimInterrupcao, 23, 59, 59, 999) });
		}
		
	}


	

	
	
	
	
	
	
	/**
	 * <p> Cria as prorrogações para sabemos porque a data de empréstimo foi alterada. </p>
	 * 
	 * @param mov
	 * @param personalMov
	 * @param interrupcaoDao
	 * @param interrupcoesASeremCriadas
	 * @throws DAOException
	 *  
	 * <p> Criado em:  29/08/2013  </p>
	 *
	 * 
	 */
	private List<Object[]> criaAsProrrogacoesEmprestimo(Movimento mov, InterrupcaoBibliotecaDao interrupcaoDao, List <Emprestimo> emprestimosParaSeremProrrogados, Date fimInterrupcao
			, List<InterrupcaoBiblioteca> interrupcoesASeremCriadas
			, Set<Biblioteca> bibliotecasInterrupcaoNaoRepetidas, Map<Integer, List <InterrupcaoBiblioteca>> cacheInterrupcoesFuturas) throws DAOException {
		
		System.out.println("Criando as prorrogações ........................................................... ");
		
		String sqlCreateCadastro = " INSERT INTO biblioteca.prorrogacao_emprestimo(id_prorrogacao_emprestimo, id_emprestimo, id_registro_cadastro, data_cadastro, data_anterior, data_atual, tipo) "
				+" VALUES ( nextval('biblioteca.prorrogacao_emprestimo_sequence'), ?, ?, ?, ?, ?, ?); ";
		
		for (Biblioteca biblioteca : bibliotecasInterrupcaoNaoRepetidas) {
			
			List <InterrupcaoBiblioteca> interrupcoesFuturasBiblioteca = cacheInterrupcoesFuturas.get(biblioteca.getId());
			
			
			// Calcula o prazo geral para cada biblioteca, em vez de 1 emprétimos por vez, é menos preciso mas vai ficar bem mais rápido.
			
			Date fimInterrupcaoParaBiblioteca = CalendarUtils.configuraTempoDaData(CirculacaoUtil.calculaProximoDiaUtilInterrupcao(fimInterrupcao, biblioteca, interrupcoesFuturasBiblioteca), 23, 59, 59, 999);
			
			List<Emprestimo> emprestimosDaBiblioteca = getEmprestimosDaBiblioteca(emprestimosParaSeremProrrogados, biblioteca);
			
			Connection connection = null;
			try {
				
				connection = interrupcaoDao.getConnection();
				PreparedStatement psCreateCadastro = connection.prepareStatement(sqlCreateCadastro);
			
				int contador = 0;
				
				for (Emprestimo emp : emprestimosDaBiblioteca){
				

					
					psCreateCadastro.setInt( 1, emp.getId());
					psCreateCadastro.setInt( 2, mov.getUsuarioLogado().getRegistroEntrada().getId());
					psCreateCadastro.setTimestamp( 3, new Timestamp(new Date().getTime()) );
					psCreateCadastro.setTimestamp( 4, new Timestamp(emp.getPrazo().getTime()) );
					psCreateCadastro.setTimestamp( 5, new Timestamp(fimInterrupcaoParaBiblioteca.getTime()) );
					psCreateCadastro.setInt( 6, TipoProrrogacaoEmprestimo.INTERRUPCAO_BIBLIOTECA);
					psCreateCadastro.addBatch();

					emp.setPrazo(fimInterrupcaoParaBiblioteca); // atualiza o novo prazo na memorária para enviar o e-mail para o usuário depois
					
					if(contador % 100 == 0 && contador > 0 )
						psCreateCadastro.executeBatch();
					
					contador++;
				}
				
				psCreateCadastro.executeBatch();
				
			} catch (SQLException se) {
				throw new DAOException(se);
			}finally{
				if(connection != null)
					try {
						connection.close();
					} catch (SQLException sqlEx) {
						throw new DAOException(sqlEx);
					}
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * Buscas as interrupções de todas as bibliotecas e mantém em memorária, para diminuir a qtd de buscas.
	 * 
	 * @param interrupcaoDao
	 * @param interrupcoesCriadas
	 * @param bibliotecasInterrupcaoNaoRepetidas
	 * @param cacheInterrupcoesFuturas
	 * @throws DAOException
	 *  
	 * <p> Criado em:  03/09/2013  </p>
	 * 
	 */
	private void populaCacheInterrupcoesPorBiblioteca(InterrupcaoBibliotecaDao interrupcaoDao,
			List<InterrupcaoBiblioteca> interrupcoesASerCriadas, Set<Biblioteca> bibliotecasInterrupcaoNaoRepetidas, Map<Integer, List<InterrupcaoBiblioteca>> cacheInterrupcoesFuturas) throws DAOException {
		
		for (Biblioteca biblioteca : bibliotecasInterrupcaoNaoRepetidas) {
			
			
			if(! cacheInterrupcoesFuturas.containsKey(biblioteca.getId()) ){
				
				List <InterrupcaoBiblioteca> interrupcoesFuturasBiblioteca = interrupcaoDao.findInterrupcoesAtivasFuturasByBiblioteca(biblioteca);
				
				// Adicionar também as que estão sendo criadas agora.
				if (interrupcoesASerCriadas != null){
					for (InterrupcaoBiblioteca interrupcaoBibliotecaASerCriada : interrupcoesASerCriadas) {
						interrupcaoBibliotecaASerCriada.getBibliotecas().contains( biblioteca );
						interrupcoesFuturasBiblioteca.add(interrupcaoBibliotecaASerCriada);
					}
				}
				
				cacheInterrupcoesFuturas.put(biblioteca.getId(), interrupcoesFuturasBiblioteca);
			}
		}
	}
	
	
	/**
	 * Retorna da lista apenas os empréstimos da biblioteca passada.
	 * 
	 * @param emprestimosParaSeremProrrogados
	 * @param biblioteca
	 * @return
	 *  
	 * <p> Criado em:  03/09/2013  </p>
	 *
	 */
	private List<Emprestimo> getEmprestimosDaBiblioteca(List<Emprestimo> emprestimosParaSeremProrrogados, Biblioteca biblioteca) {
		List<Emprestimo> temp = new ArrayList<Emprestimo>();
		
		for (Emprestimo emprestimo : emprestimosParaSeremProrrogados) {
			if(emprestimo.getMaterial().getBiblioteca().equals(biblioteca))
				temp.add(emprestimo);
		}
		
		return temp;
	}





	/*
	 * Método que retorna um lista com os ids das biblioteca onde jáexiste um interrupção na data que se deseja criar.
	 * As interrupções dessas bibliotecas não serão criadas.
	 * 
	 */
	private List<Integer> retornaBibliotecasOndeJaExisteInterrupcaoCadastrada(List<Object[]> interrupcoesCadastradas, List<Integer> idBibliotecas, Date dataInterrucao){
		
		List<Integer> idBibliotecasJaPossuemInterrupcoes = new ArrayList<Integer>();
		
		// Para todas as datas cadastras
		for (Object[] interrupcaoCadastrada : interrupcoesCadastradas) {
			
			if( dataInterrucao.equals(interrupcaoCadastrada[0])){ // Se a data da interrupção é igual a uma data cadastrada
				
				// Para todas as biblioteca da interrupçao
				for (Integer idBiblioteca : idBibliotecas) {
					
					if( idBiblioteca.equals(interrupcaoCadastrada[1])){ // se é para a mesma biblioteca
						idBibliotecasJaPossuemInterrupcoes.add(idBiblioteca);
					}
				}
			}
		} 
		
		return idBibliotecasJaPossuemInterrupcoes; // Não existe interrupção cadastras para a data e biblioteca informada.
		
	}
	
	
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		// Não é uma regra de negócio é só para não correr o risco de travar o sistema caso o período informado seja muito grande.
		final int PERIODO_MAXIMO_PRORROGACAO = 7; 
		
		ListaMensagens mensagens = new ListaMensagens ();
		
		MovimentoCadastraInterrupcaoBiblioteca personalMov = (MovimentoCadastraInterrupcaoBiblioteca) mov;
		
		InterrupcaoBiblioteca interrupcao = personalMov.getInterrupcao();
		Date dataInicio = personalMov.getDataInicio();
		Date dataFinal = personalMov.getDataFim();
		
		mensagens.addAll(interrupcao.validate());
		
		// Valida se o período digitado pelo usuário é válido //
		if (dataInicio != null && dataFinal != null && CalendarUtils.estorouPrazo(dataFinal, dataInicio))
			mensagens.addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Período");
		
		if(dataFinal != null){
			int qtdDias = CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(dataInicio, dataFinal);
		
			if(qtdDias > PERIODO_MAXIMO_PRORROGACAO){ // Para não ficar muito pessado a atualização de todos os empréstimo nesse período
				mensagens.addErro(" Por questões de performance dessa operação, o período máximo para cadastrar uma interrupção é de "+PERIODO_MAXIMO_PRORROGACAO+" dias por vez.");
			}
		}
		
		checkValidation(mensagens);
	}
	
}