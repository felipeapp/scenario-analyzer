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
 *    <p>Cont�m as regras de neg�cio para cadastrar interrup��es para as bibliotecas do sistema.<br/>
 *    Interrup��es s�o per�odos em que a biblioteca n�o vai funcionar e nenhum empr�stimo j� feito 
 *    ou que venha a ser criado, pode vencer na data da interrup��o.</p>
 *
 *    <p>� importante observar que podem ocorrer problemas de concorr�ncia se for cadastrada uma 
 *    interru��o no mesmo momento em que um empr�stimo estaja sendo devolvido ou renovado. A solu��o 
 *    foi desativar o servi�o de empr�stimos das bibliotecas cuja interrup��o ser� gerada.</p>
 *
 *    <p><strong> ***** I M P O R T A N T E ***** </strong>: Esse processador � bastante pesado de ser executado. A quantidade de empr�stimos ativos � no ordem de N = 1000.
 *    Ent�o s�o alterados O(1000) empr�stimos, criadas O(1000) prorroga��es, enviados O(1000) e-mails, etc...
 *    
 *    Tem que ser bastante otimizado, o hibernate n�o d� nem para o rastro.  
 *    </p>
 *
 * @author jadson
 * @since 10/12/2009
 * @version 1.0 criacao da classe
 * @version 1.1 Jadson 07/08/2012 - Altera��o das regras para bloquear novos empr�stimos e renova��o durante o cadastramento da interru��o.
 * @version 2.0 Jadson 03/09/2013 - Otimiza��o d�strica desse processador. O c�digo de antes era invi�vel para a quantidade de empr�stimos que precisavam ser prorrogados.
 */
public class ProcessadorCadastraInterrupcaoBiblioteca extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastraInterrupcaoBiblioteca personalMov = (MovimentoCadastraInterrupcaoBiblioteca) mov;
		
		/// Cont�m dos dados da interrup��o a ser cadastrada  ///
		InterrupcaoBiblioteca interrupcaoOriginal = personalMov.getInterrupcao();
		Date inicioInterrupcao = personalMov.getDataInicio();
		Date fimInterrupcao = personalMov.getDataFim();
		
		InterrupcaoBibliotecaDao interrupcaoDao  = null;
		
		try {
			
			interrupcaoDao = getDAO(InterrupcaoBibliotecaDao.class, personalMov);
			
			
			/// crias as interru��es, essa � a parte f�cil ! ( a partir desse momento nenhum empr�stimo vai cair mais nesses dias)///
			List<InterrupcaoBiblioteca> interrupcoesCriadas = criaInterrupcoesDoPeriodo(personalMov, interrupcaoOriginal, inicioInterrupcao, fimInterrupcao, interrupcaoDao);	
			
			
			// Agora vem a parte dif�cil ....
			if ( interrupcoesCriadas.size() > 0) {
			
				List <Emprestimo> emprestimos = interrupcaoDao.findAllEmprestimosAtivosByBibliotecasEPeriodo(interrupcaoOriginal.getBibliotecas()
						, CalendarUtils.configuraTempoDaData(inicioInterrupcao, 0, 0, 0, 0), CalendarUtils.configuraTempoDaData(fimInterrupcao, 23, 59, 59, 999));
	
				
				/* ****************************************************************************
				 * Otimiza��o para transformar M^2 em 2M updates no banco, onde M = n� de bibliotecas do sistema //
				 * ****************************************************************************/
				Set<Biblioteca> bibliotecasInterrupcaoNaoRepetidas = new HashSet<Biblioteca>();
				
				for (InterrupcaoBiblioteca interrupcaoCriada : interrupcoesCriadas) {
					bibliotecasInterrupcaoNaoRepetidas.addAll(interrupcaoCriada.getBibliotecas());
				}
				
				
				
				/* ****************************************************************************
				 * Para diminuir a quantidade consultas ao banco mant�m as interrup��es futuras das bibliotecas em memor�ria
				 * ****************************************************************************/
				Map<Integer, List <InterrupcaoBiblioteca>> cacheInterrupcoesFuturas = new TreeMap<Integer, List <InterrupcaoBiblioteca>>();
				
				populaCacheInterrupcoesPorBiblioteca(interrupcaoDao, interrupcoesCriadas, bibliotecasInterrupcaoNaoRepetidas, cacheInterrupcoesFuturas);
				
				
				
				/* ****************************************************************************
				 *  Prorroga a data dos empr�stimos individualmente para cada biblioteca      *
				 *  � normal retornar aqui em torno de N = 10.000 empr�stimo                  *
				 *  S�o realizados O(M) atualiza��es no banco, onde M = n�mero de bibliotecas * 
				 *  do sistema. em torno de 20 bibliotecas                                    *
				 *  Em cada atualiza��o s�o atualizadas O(N/M) linhas da tabela empr�stimos   *
				 *  Ex.:  10.000/20 = 500 empr�stimso em m�dia, por�m � mediana vai est� bem  *
				 *  longe da m�dia, pois os empr�stimos da central v�o ser sempre em uma      *
				 *  quantidade bem maior                                                      * 
				 * ****************************************************************************/			
				prorrogaEmpretimosDoPeriodo(interrupcaoDao, inicioInterrupcao, fimInterrupcao, interrupcoesCriadas, bibliotecasInterrupcaoNaoRepetidas, cacheInterrupcoesFuturas);
				
				
				/* *****************************************************************************
				 *  Cria as prorroga��es para saber porque a data do empr�stimo mudou          *
				 *                                                                             *
				 *  S�o realizados O(N) "inserts" no banco, onde N = n�mero de empr�stimos       * 
				 *  prorrog�veis. em torno de 10.000. Por�m em Batch para otimizar, inserir    * 
				 *  objetinhos com hibernate passa longe daqui.                                *
				 * *****************************************************************************/		
				criaAsProrrogacoesEmprestimo(mov, interrupcaoDao, emprestimos, fimInterrupcao, interrupcoesCriadas, bibliotecasInterrupcaoNaoRepetidas, cacheInterrupcoesFuturas);
				
				return emprestimos; // para mandar e-mail para o usu�rio, o prazo j� foi atualizado para o novo depois da prorroga��o.
				
			} 
			
		} finally {
			if (interrupcaoDao != null) interrupcaoDao.close();  
		}

		return null;
	}




	/**
	 * Cria as interrup��es da data de in�cio at� a data final passa pelo usu�rio.
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
		
		System.out.println("Criando as interru��es  ........................................................... ");
		
		List<InterrupcaoBiblioteca> interrupcoesCriadas = new ArrayList<InterrupcaoBiblioteca> ();
		
		// as bibliotecas escolhidas pelo usu�rio, n�o necessariamente v�o ser cradas porque j� pode existir interru��es para elas
		List<Integer> idsBiblitocasASeremCriadasInterrupcoes = new ArrayList<Integer>();
		
		for (Biblioteca biblioteca : interrupcaoOriginal.getBibliotecas()) {
			idsBiblitocasASeremCriadasInterrupcoes.add(biblioteca.getId());
		}
		
		/* *****************************************************
		 * Retorna um array em que:
		 * [0] data da interrup��o
		 * [1] id da biblioteca da interrup��o
		 */
		List<Object[]> interrupcoesJaCadastradas = interrupcaoDao.findAllDatasInterrupcoesCadastradasParaAsBibliotecasNoPerioco (idsBiblitocasASeremCriadasInterrupcoes, inicioInterrupcao, fimInterrupcao);
		
		// Data de intera��o no la�o
		Calendar dataInteracao = Calendar.getInstance();
		dataInteracao.setTime(inicioInterrupcao);
		
		boolean estaDentroDoPeriodo = true;
		
		while (estaDentroDoPeriodo){  // para dataIniciao at� dataFim
		
			List<Integer> idBibliotecaJaPossuemInterrupcao = retornaBibliotecasOndeJaExisteInterrupcaoCadastrada(interrupcoesJaCadastradas, idsBiblitocasASeremCriadasInterrupcoes, dataInteracao.getTime() );
			
			List<Biblioteca> bibliotecasSemInterrupcoes	= new ArrayList<Biblioteca>();		
			
			for (Biblioteca biblioteca : interrupcaoOriginal.getBibliotecas()) {
				if( ! idBibliotecaJaPossuemInterrupcao.contains(biblioteca.getId()) ){
					bibliotecasSemInterrupcoes.add(biblioteca);
				}
			} 
			
			
			////////////////////////////////////////////////////////////////////////////////
			
			
			// cria interrup�o na data digitada pelo usu�rio para a biblioteca que ainda n�o possuem
			
			InterrupcaoBiblioteca interrupcaoCriada = new InterrupcaoBiblioteca();
			interrupcaoCriada.setBibliotecas(bibliotecasSemInterrupcoes);
			interrupcaoCriada.setMotivo(interrupcaoOriginal.getMotivo());
			interrupcaoCriada.setData(dataInteracao.getTime());
			
			interrupcaoDao.createNoFlush(interrupcaoCriada); // salva a nova interrup��o no banco
			
			interrupcoesCriadas.add(interrupcaoCriada);
			
			dataInteracao.add(Calendar.DAY_OF_MONTH, 1); // PULA PARA O PR�XIMO DIA ( DIA++ )
			
			// Condi��o de parada do while:  Se j� chegou na data fim das interrup�es
			if (personalMov.getDataFim() == null || CalendarUtils.estorouPrazo(fimInterrupcao, dataInteracao.getTime()))
				estaDentroDoPeriodo = false;
		}
		
		interrupcaoDao.getSession().flush(); // persite no banco as interru��es
		
		return interrupcoesCriadas;
	}





	/**
	 * Prorroga os empr�stimos no banco, todos de uma �nica vez e com um �nico, sql por biblioteca
	 * 
	 * Por bibliotecas pode ter dias diferentes de funcionamento, ent�o o calculo n�o pode ser feito para todas de uma vez.
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
		
		System.out.println("Prorrogando os empr�stimos ........................................................... ");
		
		
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
	 * <p> Cria as prorroga��es para sabemos porque a data de empr�stimo foi alterada. </p>
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
		
		System.out.println("Criando as prorroga��es ........................................................... ");
		
		String sqlCreateCadastro = " INSERT INTO biblioteca.prorrogacao_emprestimo(id_prorrogacao_emprestimo, id_emprestimo, id_registro_cadastro, data_cadastro, data_anterior, data_atual, tipo) "
				+" VALUES ( nextval('biblioteca.prorrogacao_emprestimo_sequence'), ?, ?, ?, ?, ?, ?); ";
		
		for (Biblioteca biblioteca : bibliotecasInterrupcaoNaoRepetidas) {
			
			List <InterrupcaoBiblioteca> interrupcoesFuturasBiblioteca = cacheInterrupcoesFuturas.get(biblioteca.getId());
			
			
			// Calcula o prazo geral para cada biblioteca, em vez de 1 empr�timos por vez, � menos preciso mas vai ficar bem mais r�pido.
			
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

					emp.setPrazo(fimInterrupcaoParaBiblioteca); // atualiza o novo prazo na memor�ria para enviar o e-mail para o usu�rio depois
					
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
	 * Buscas as interrup��es de todas as bibliotecas e mant�m em memor�ria, para diminuir a qtd de buscas.
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
				
				// Adicionar tamb�m as que est�o sendo criadas agora.
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
	 * Retorna da lista apenas os empr�stimos da biblioteca passada.
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
	 * M�todo que retorna um lista com os ids das biblioteca onde j�existe um interrup��o na data que se deseja criar.
	 * As interrup��es dessas bibliotecas n�o ser�o criadas.
	 * 
	 */
	private List<Integer> retornaBibliotecasOndeJaExisteInterrupcaoCadastrada(List<Object[]> interrupcoesCadastradas, List<Integer> idBibliotecas, Date dataInterrucao){
		
		List<Integer> idBibliotecasJaPossuemInterrupcoes = new ArrayList<Integer>();
		
		// Para todas as datas cadastras
		for (Object[] interrupcaoCadastrada : interrupcoesCadastradas) {
			
			if( dataInterrucao.equals(interrupcaoCadastrada[0])){ // Se a data da interrup��o � igual a uma data cadastrada
				
				// Para todas as biblioteca da interrup�ao
				for (Integer idBiblioteca : idBibliotecas) {
					
					if( idBiblioteca.equals(interrupcaoCadastrada[1])){ // se � para a mesma biblioteca
						idBibliotecasJaPossuemInterrupcoes.add(idBiblioteca);
					}
				}
			}
		} 
		
		return idBibliotecasJaPossuemInterrupcoes; // N�o existe interrup��o cadastras para a data e biblioteca informada.
		
	}
	
	
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		// N�o � uma regra de neg�cio � s� para n�o correr o risco de travar o sistema caso o per�odo informado seja muito grande.
		final int PERIODO_MAXIMO_PRORROGACAO = 7; 
		
		ListaMensagens mensagens = new ListaMensagens ();
		
		MovimentoCadastraInterrupcaoBiblioteca personalMov = (MovimentoCadastraInterrupcaoBiblioteca) mov;
		
		InterrupcaoBiblioteca interrupcao = personalMov.getInterrupcao();
		Date dataInicio = personalMov.getDataInicio();
		Date dataFinal = personalMov.getDataFim();
		
		mensagens.addAll(interrupcao.validate());
		
		// Valida se o per�odo digitado pelo usu�rio � v�lido //
		if (dataInicio != null && dataFinal != null && CalendarUtils.estorouPrazo(dataFinal, dataInicio))
			mensagens.addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Per�odo");
		
		if(dataFinal != null){
			int qtdDias = CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(dataInicio, dataFinal);
		
			if(qtdDias > PERIODO_MAXIMO_PRORROGACAO){ // Para n�o ficar muito pessado a atualiza��o de todos os empr�stimo nesse per�odo
				mensagens.addErro(" Por quest�es de performance dessa opera��o, o per�odo m�ximo para cadastrar uma interrup��o � de "+PERIODO_MAXIMO_PRORROGACAO+" dias por vez.");
			}
		}
		
		checkValidation(mensagens);
	}
	
}