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
 *    <p>Cont�m as regras de neg�cio para cadastrar interrup��es para as bibliotecas do sistema.<br/>
 *    Interrup��es s�o per�odos em que a biblioteca n�o vai funcionar e nenhum empr�stimo j� feito 
 *    ou que venha a ser criado pode vencer na data da interrup��o.</p>
 *
 *    <p>� importante observar que pode ocorrer problemas de concorr�ncia se for cadastrada uma 
 *    interru��o no mesmo momento em que um empr�stimo estaja sendo devolvido ou renovado. A solu��o 
 *    foi fazer lock nas tabelas empr�stimo e material.</p>
 *
 *    <p>IMPORTANTE: Esse processador s� deve ser chamado se os empr�stimos e renova��es estiverem 
 *    desativados para as bibliotecas da interrup��o.</p>
 *
 * @author jadson
 * @since 10/12/2009
 * @version 1.0 criacao da classe
 * @version 1.1 Jadson 07/08/2012 - Altera��o das regras para bloquear novos empr�stimos e renova��o durante o cadastramento da interru��o. 
 */
public class ProcessadorCadastraInterrupcaoBiblioteca extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate(mov);
		
		MovimentoCadastraInterrupcaoBiblioteca personalMov = (MovimentoCadastraInterrupcaoBiblioteca) mov;
		
		StringBuilder mensagemRetorno = new StringBuilder(); // retorna para o usu�rio as interrup��es que n�o foram criadas.
		
		InterrupcaoBibliotecaDao interrupcaoDao  = null;
		
		try {
			
			interrupcaoDao = getDAO(InterrupcaoBibliotecaDao.class, personalMov);
			
			// SALVA UMA NOVA INTERRUP��O PARA CADA DIA DO PER�ODO ESCOLHIDO PELO USU�RIO //
			
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
			 * [0] data da interrup��o
			 * [1] id da biblioteca da interrup��o
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
				
					// Gera a mensagem para o usu�rio daquelas interrup��es que n�o foram criadas //
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); 
					mensagemRetorno.append(" <span style=\"color:red;\">Interrup��o na data: "+format.format(dataInicioTemp.getTime())+" para a(s) biblioteca(s): ");
					
					for (Biblioteca bibliotecaComInterrupcao : bibliotecaComInterrupcoes) {
						mensagemRetorno.append(" "+bibliotecaComInterrupcao.getDescricao()+", ");
					}
					
					mensagemRetorno.append(" n�o p�de ser criada pois ela j� existia.</span> <br/>");
				}
				
				////////////////////////////////////////////////////////////////////////////////
				
				
				// cria interrup�o na data digitada pelo usu�rio para a biblioteca que ainda n�o possuem
				
				InterrupcaoBiblioteca interrupcao = new InterrupcaoBiblioteca();
				interrupcao.setBibliotecas(bibliotecaSemInterrupcoes);
				interrupcao.setMotivo(personalMov.getInterrupcao().getMotivo());
				interrupcao.setData(dataInicioTemp.getTime());
				
				interrupcaoDao.createNoFlush(interrupcao); // salva a nova interrup��o
				
				interrupcoesFeitas.add(interrupcao);
				
				dataInicioTemp.add(Calendar.DAY_OF_MONTH, 1); // PULA PARA O PR�XIMO DIA
				
				if (personalMov.getDataFim() == null || CalendarUtils.estorouPrazo(personalMov.getDataFim(), dataInicioTemp.getTime()))
					estaDentroDoPeriodo = false;
			}
			
			// Para cada interrup��o que foi realizada
			if ( interrupcoesFeitas.size() > 0) {
				
				
				
				/* ******************************************************************************************************
				 * Adianta os prazos dos empr�stimos existentes que possuem o prazo para a data das interrup��es.
				 *
				 * ******************************************************************************************************/
				List <Emprestimo> emprestimos = interrupcaoDao.findAllEmprestimosAtivosByBibliotecasEPeriodo(personalMov.getInterrupcao().getBibliotecas(), personalMov.getDataInicio(), personalMov.getDataFim());
				
				List <ProrrogacaoEmprestimo> prorrogacoes = new ArrayList <ProrrogacaoEmprestimo> ();
				
				if (emprestimos != null){
					// Atualiza o prazo dos empr�stimos.
					for (Emprestimo emp : emprestimos){
						
						Date prazoAntigo = emp.getPrazo();
						
						prorrogacoes.addAll(CirculacaoUtil.geraProrrogacoesEmprestimo(emp, interrupcoesFeitas));
						
						
						/* ***********************************************************************************
						 * IMPORTANTE: Tenta garantir que s� empr�stimos emprestados ser�o alterados por esse caso de uso
						 * 
						 * Caso algu�m devolva o material ao mesmo tempo que uma interrup��o esteja sendo cadastrada
						 * 
						 * ***********************************************************************************/
						
						interrupcaoDao.update(" UPDATE biblioteca.emprestimo SET prazo = ? WHERE id_emprestimo = ? AND situacao = ? AND ativo = ? ", new Object[]{ emp.getPrazo(), emp.getId(), Emprestimo.EMPRESTADO, true});
						
						/* *************************************************************************************
						 * ENVIA UM E-MAIL PARA INFORMAR AO USU�RIO QUE O SEU EMPR�TIMO FOI PRORROGADO.
						 ***************************************************************************************/
						enviarEmailProrrogacaoPrazo(mov, emp.getUsuarioBiblioteca(), emp.getMaterial(), emp.getId(), personalMov.getInterrupcao().getMotivo(), prazoAntigo, emp.getPrazo());
						
						
						interrupcaoDao.detach(emp); // *** IMPORTANTE *** Para quando atualizar a prorroga��o, n�o atualizar o empr�stimo e bagun�ar as datas
					}
					
					// Salva as prorroga��es.
					for (ProrrogacaoEmprestimo p : prorrogacoes)
						interrupcaoDao.createNoFlush(p);	      // CUIDADO PARA O HIBERNATE N�O FAZER UM UPDATE DO EMPR�STIMOS AQUI, SEN�O D� BRONCA !!!!!
				}
				
				
				
			} 
			
		} finally {
			if (interrupcaoDao != null) interrupcaoDao.close();  
		}

		return mensagemRetorno.toString();
	}

	
	/*
	 * Envia um email informando ao usu�rio que os prazos dos empr�stimo dele foram prorrogados
	 */
	private void enviarEmailProrrogacaoPrazo(Movimento mov, UsuarioBiblioteca usuarioBiblioteca, MaterialInformacional materialEmprestado, int idEmprestimo
							, String motivo, Date prazoAntigo, Date novoPrazo) throws DAOException{

		// informacoesUsuario[0] == nome Usuario
		// informacoesUsuario[1] == email Usuario
		Object[] informacoesUsuario = getDAO( UsuarioBibliotecaDao.class, mov).findNomeEmailUsuarioBiblioteca(usuarioBiblioteca);
		
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		String assunto = " Aviso de Prorroga��o do prazo do Empr�stimo ";
		String titulo = " Prorroga��o do prazo do seu Empr�stimo ";
		String mensagemUsuario = "O empr�stimo do material: <i>"+BibliotecaUtil.obtemDadosMaterialInformacional(materialEmprestado.getId())+"</i> que venceria dia: "+formatador.format(prazoAntigo);
		
		String mensagemNivel1Email =  " Foi prorrogado para o dia: <strong>"+formatador.format(novoPrazo)+"</strong> , devido ao motivo: ";
		String mensagemNivel3Email =  motivo;
		
		String codigoAutenticacao = BibliotecaUtil.geraNumeroAutenticacaoComprovantes(idEmprestimo, novoPrazo);
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_PRORROGACAO_EMPRESTIMO, mensagemUsuario, mensagemNivel1Email, null, mensagemNivel3Email, null
				, null, null,  codigoAutenticacao, null);
	}
	
	
	
	/*
	 * M�todo que retorna um lista com os ids das biblioteca onde j�existe um interrup��o na data que se deseja criar.
	 * As interrup��es dessas bibliotecas n�o ser�o criadas.
	 * 
	 */
	private List<Integer> retornaBibliotecasOndeJaExisteInterrupcaoCadastrada(List<Object[]> interrupcoesCadastradas, List<Biblioteca> bibliotecas, Date dataInterrucao){
		
		List<Integer> idBibliotecasJaPossuemInterrupcoes = new ArrayList<Integer>();
		
		// Para todas as datas cadastras
		for (Object[] interrupcaoCadastrada : interrupcoesCadastradas) {
			
			if( dataInterrucao.equals(interrupcaoCadastrada[0])){ // Se a data da interrup��o � igual a uma data cadastrada
				
				// Para todas as biblioteca da interrup�ao
				for (Biblioteca biblio : bibliotecas) {
					
					if( new Integer(biblio.getId()).equals(interrupcaoCadastrada[1])){ // se � para a mesma biblioteca
						idBibliotecasJaPossuemInterrupcoes.add(biblio.getId());
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
		final int PERIODO_MAXIMO_PRORROGACAO = 15; 
		
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
				mensagens.addErro(" O per�odo m�ximo para cadastrar uma interrup��o � de "+PERIODO_MAXIMO_PRORROGACAO+" dias por vez.");
			}
		}
		
		checkValidation(mensagens);
	}
	
}