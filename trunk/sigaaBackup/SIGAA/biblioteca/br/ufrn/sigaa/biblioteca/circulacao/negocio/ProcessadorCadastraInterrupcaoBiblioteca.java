package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.InterrupcaoBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InterrupcaoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;


/**
 *    <p>Contém as regras de negócio para cadastrar interrupções para as bibliotecas do sistema.<br/>
 *    Interrupções são períodos em que a biblioteca não vai funcionar e nenhum empréstimo já feito 
 *    ou que venha a ser criado pode vencer na data da interrupção.</p>
 *
 *    <p>É importante observar que pode ocorrer problemas de concorrência se for cadastrada uma 
 *    interrução no mesmo momento em que um empréstimo estaja sendo devolvido ou renovado. A solução 
 *    foi fazer lock nas tabelas empréstimo e material.</p>
 *
 *    <p>IMPORTANTE: Esse processador só deve ser chamado se os empréstimos e renovações estiverem 
 *    desativados para as bibliotecas da interrupção.</p>
 *
 * @author jadson
 * @since 10/12/2009
 * @version 1.0 criacao da classe
 * @version 1.1 Jadson 07/08/2012 - Alteração das regras para bloquear novos empréstimos e renovação durante o cadastramento da interrução. 
 */
public class ProcessadorCadastraInterrupcaoBiblioteca extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate(mov);
		
		MovimentoCadastraInterrupcaoBiblioteca personalMov = (MovimentoCadastraInterrupcaoBiblioteca) mov;
		
		StringBuilder mensagemRetorno = new StringBuilder(); // retorna para o usuário as interrupções que não foram criadas.
		
		InterrupcaoBibliotecaDao interrupcaoDao  = null;
		
		try {
			
			interrupcaoDao = getDAO(InterrupcaoBibliotecaDao.class, personalMov);
			
			// SALVA UMA NOVA INTERRUPÇÃO PARA CADA DIA DO PERÍODO ESCOLHIDO PELO USUÁRIO //
			
			List<InterrupcaoBiblioteca> interrupcoesFeitas = new ArrayList<InterrupcaoBiblioteca> ();
			
			Calendar dataInicioTemp = Calendar.getInstance();
			dataInicioTemp.setTime(personalMov.getDataInicio());
			
			boolean estaDentroDoPeriodo = true;
			
			List<Integer> idsBiblitocas = new ArrayList<Integer>();
			
			for (Biblioteca b : personalMov.getInterrupcao().getBibliotecas()) {
				idsBiblitocas.add(b.getId());
			}
			
			/* *****************************************************
			 * Retorna um array em que:
			 * [0] data da interrupção
			 * [1] id da biblioteca da interrupção
			 */
			List<Object[]> interrupcoesCadastradas = interrupcaoDao.findAllDatasInterrupcoesCadastradasParaAsBibliotecasNoPerioco
																(idsBiblitocas, personalMov.getDataInicio(), personalMov.getDataFim());
			
			while (estaDentroDoPeriodo){
				
				List<Integer> idBibliotecaJaPossuemInterrupcao = retornaBibliotecasOndeJaExisteInterrupcaoCadastrada(
						interrupcoesCadastradas, personalMov.getInterrupcao().getBibliotecas(), dataInicioTemp.getTime() );
				
				List<Biblioteca> bibliotecaSemInterrupcoes	= new ArrayList<Biblioteca>();	
				List<Biblioteca> bibliotecaComInterrupcoes	= new ArrayList<Biblioteca>();		
				
				for (Biblioteca biblioteca : personalMov.getInterrupcao().getBibliotecas()) {
					
					if( ! idBibliotecaJaPossuemInterrupcao.contains(biblioteca.getId()) ){
						bibliotecaSemInterrupcoes.add(biblioteca);
					}else{
						bibliotecaComInterrupcoes.add(biblioteca);
					}
					
				} 
				
				if(bibliotecaComInterrupcoes.size() > 0){
				
					// Gera a mensagem para o usuário daquelas interrupções que não foram criadas //
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); 
					mensagemRetorno.append(" <span style=\"color:red;\">Interrupção na data: "+format.format(dataInicioTemp.getTime())+" para a(s) biblioteca(s): ");
					
					for (Biblioteca bibliotecaComInterrupcao : bibliotecaComInterrupcoes) {
						mensagemRetorno.append(" "+bibliotecaComInterrupcao.getDescricao()+", ");
					}
					
					mensagemRetorno.append(" não pôde ser criada pois ela já existia.</span> <br/>");
				}
				
				////////////////////////////////////////////////////////////////////////////////
				
				
				// cria interrupão na data digitada pelo usuário para a biblioteca que ainda não possuem
				
				InterrupcaoBiblioteca interrupcao = new InterrupcaoBiblioteca();
				interrupcao.setBibliotecas(bibliotecaSemInterrupcoes);
				interrupcao.setMotivo(personalMov.getInterrupcao().getMotivo());
				interrupcao.setData(dataInicioTemp.getTime());
				
				interrupcaoDao.createNoFlush(interrupcao); // salva a nova interrupção
				
				interrupcoesFeitas.add(interrupcao);
				
				dataInicioTemp.add(Calendar.DAY_OF_MONTH, 1); // PULA PARA O PRÓXIMO DIA
				
				if (personalMov.getDataFim() == null || CalendarUtils.estorouPrazo(personalMov.getDataFim(), dataInicioTemp.getTime()))
					estaDentroDoPeriodo = false;
			}
			
			// Para cada interrupção que foi realizada
			if ( interrupcoesFeitas.size() > 0) {
				
				
				
				/* ******************************************************************************************************
				 * Adianta os prazos dos empréstimos existentes que possuem o prazo para a data das interrupções.
				 *
				 * ******************************************************************************************************/
				List <Emprestimo> emprestimos = interrupcaoDao.findAllEmprestimosAtivosByBibliotecasEPeriodo(personalMov.getInterrupcao().getBibliotecas(), personalMov.getDataInicio(), personalMov.getDataFim());
				
				List <ProrrogacaoEmprestimo> prorrogacoes = new ArrayList <ProrrogacaoEmprestimo> ();
				
				if (emprestimos != null){
					// Atualiza o prazo dos empréstimos.
					for (Emprestimo emp : emprestimos){
						
						Date prazoAntigo = emp.getPrazo();
						
						prorrogacoes.addAll(CirculacaoUtil.geraProrrogacoesEmprestimo(emp, interrupcoesFeitas));
						
						
						/* ***********************************************************************************
						 * IMPORTANTE: Tenta garantir que só empréstimos emprestados serão alterados por esse caso de uso
						 * 
						 * Caso alguém devolva o material ao mesmo tempo que uma interrupção esteja sendo cadastrada
						 * 
						 * ***********************************************************************************/
						
						interrupcaoDao.update(" UPDATE biblioteca.emprestimo SET prazo = ? WHERE id_emprestimo = ? AND situacao = ? AND ativo = ? ", new Object[]{ emp.getPrazo(), emp.getId(), Emprestimo.EMPRESTADO, true});
						
						/* *************************************************************************************
						 * ENVIA UM E-MAIL PARA INFORMAR AO USUÁRIO QUE O SEU EMPRÉTIMO FOI PRORROGADO.
						 ***************************************************************************************/
						enviarEmailProrrogacaoPrazo(mov, emp.getUsuarioBiblioteca(), emp.getMaterial(), emp.getId(), personalMov.getInterrupcao().getMotivo(), prazoAntigo, emp.getPrazo());
						
						
						interrupcaoDao.detach(emp); // *** IMPORTANTE *** Para quando atualizar a prorrogação, não atualizar o empréstimo e bagunçar as datas
					}
					
					// Salva as prorrogações.
					for (ProrrogacaoEmprestimo p : prorrogacoes)
						interrupcaoDao.createNoFlush(p);	      // CUIDADO PARA O HIBERNATE NÃO FAZER UM UPDATE DO EMPRÉSTIMOS AQUI, SENÃO DÁ BRONCA !!!!!
				}
				
				
				
			} 
			
		} finally {
			if (interrupcaoDao != null) interrupcaoDao.close();  
		}

		return mensagemRetorno.toString();
	}

	
	/*
	 * Envia um email informando ao usuário que os prazos dos empréstimo dele foram prorrogados
	 */
	private void enviarEmailProrrogacaoPrazo(Movimento mov, UsuarioBiblioteca usuarioBiblioteca, MaterialInformacional materialEmprestado, int idEmprestimo
							, String motivo, Date prazoAntigo, Date novoPrazo) throws DAOException{

		// informacoesUsuario[0] == nome Usuario
		// informacoesUsuario[1] == email Usuario
		Object[] informacoesUsuario = getDAO( UsuarioBibliotecaDao.class, mov).findNomeEmailUsuarioBiblioteca(usuarioBiblioteca);
		
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		String assunto = " Aviso de Prorrogação do prazo do Empréstimo ";
		String titulo = " Prorrogação do prazo do seu Empréstimo ";
		String mensagemUsuario = "O empréstimo do material: <i>"+BibliotecaUtil.obtemDadosMaterialInformacional(materialEmprestado.getId())+"</i> que venceria dia: "+formatador.format(prazoAntigo);
		
		String mensagemNivel1Email =  " Foi prorrogado para o dia: <strong>"+formatador.format(novoPrazo)+"</strong> , devido ao motivo: ";
		String mensagemNivel3Email =  motivo;
		
		String codigoAutenticacao = BibliotecaUtil.geraNumeroAutenticacaoComprovantes(idEmprestimo, novoPrazo);
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_PRORROGACAO_EMPRESTIMO, mensagemUsuario, mensagemNivel1Email, null, mensagemNivel3Email, null
				, null, null,  codigoAutenticacao, null);
	}
	
	
	
	/*
	 * Método que retorna um lista com os ids das biblioteca onde jáexiste um interrupção na data que se deseja criar.
	 * As interrupções dessas bibliotecas não serão criadas.
	 * 
	 */
	private List<Integer> retornaBibliotecasOndeJaExisteInterrupcaoCadastrada(List<Object[]> interrupcoesCadastradas, List<Biblioteca> bibliotecas, Date dataInterrucao){
		
		List<Integer> idBibliotecasJaPossuemInterrupcoes = new ArrayList<Integer>();
		
		// Para todas as datas cadastras
		for (Object[] interrupcaoCadastrada : interrupcoesCadastradas) {
			
			if( dataInterrucao.equals(interrupcaoCadastrada[0])){ // Se a data da interrupção é igual a uma data cadastrada
				
				// Para todas as biblioteca da interrupçao
				for (Biblioteca biblio : bibliotecas) {
					
					if( new Integer(biblio.getId()).equals(interrupcaoCadastrada[1])){ // se é para a mesma biblioteca
						idBibliotecasJaPossuemInterrupcoes.add(biblio.getId());
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
		final int PERIODO_MAXIMO_PRORROGACAO = 15; 
		
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
				mensagens.addErro(" O período máximo para cadastrar uma interrupção é de "+PERIODO_MAXIMO_PRORROGACAO+" dias por vez.");
			}
		}
		
		checkValidation(mensagens);
	}
	
}