/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 20/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.administracao.jsf;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.timer.AtualizaEstatisticasBibliotecaTimer;
import br.ufrn.sigaa.biblioteca.timer.BaixarMultasPagasAutomaticamenteBibliotecaTimer;
import br.ufrn.sigaa.biblioteca.timer.EnviaInformativoNovasAquisicoesTimer;
import br.ufrn.sigaa.biblioteca.timer.NotificaUsuariosInteressadosDSITimer;
import br.ufrn.sigaa.biblioteca.timer.NotificacaoEmprestimoPertoDeVencerTimer;
import br.ufrn.sigaa.biblioteca.timer.NotificacaoEmprestimosEmAtrasoTimer;
import br.ufrn.sigaa.biblioteca.timer.VerificaReservasMaterialBibliotecaVencidasTimer;

/**
 *
 * <p>MBean respons�vel por rodar manualmente os times do sub sistema da bibiblioteca </p>
 *
 * <p>
 * <strong>Todas as tarefas aqui est�o programadas para serem executadas automaticamente pelo sistema, essa classe 
 * serve para executar alguma thread manualmente caso a execu��o dela falhe ou para testes durante o desenvolvimento.
 * </strong>
 * </p>
 *
 * @author jadson
 *
 */
@Component("executaTimersBibliotecaMBean")
@Scope("request")
public class ExecutaTimersBibliotecaMBean extends SigaaAbstractController<CacheEntidadesMarc> {

	/**
	 * Inicia a thread que envia notifica��o aos usu�rios que est�o com empr�stimos vencendo
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/administracao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String executaNotificacaoEmprestimos() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		Executors.newSingleThreadExecutor().execute(new NotificacaoEmprestimoPertoDeVencerTimer());
		
		addMensagemInformation("Notifica��o de vencimento dos empr�stimos iniciada.");
		return null;
	}
	
	
	
	/**
	 * 
	 * Inicia a thread que envia notifica��o aos usu�rios que est�o com empr�stimos em atraso
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/administracao.jsp</li>
	 *   </ul>
	 *   
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String executaNotificacaoEmprestimosEmAtraso() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		Executors.newSingleThreadExecutor().execute(new NotificacaoEmprestimosEmAtrasoTimer());
		
		addMensagemInformation("Notifica��o dos empr�stimos atrasados iniciada.");
		return null;
	}
	
	
	
	
	/**
	 * 
	 * Inicia a thread que verifica as reservas em espera vencidas, cancelando-as, e colocando as 
	 * pr�ximas reservas para EM ESPERA.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/administracao.jsp</li>
	 *   </ul>
	 *   
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String executaVerificacaoReservasEmEsperaVencidas() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		Executors.newSingleThreadExecutor().execute(new VerificaReservasMaterialBibliotecaVencidasTimer());
		
		addMensagemInformation("Verifica��o das reservas EM ESPERA vencidas iniciada.");
		return null;
	}
	
	
	/**
	 * 
	 * Inicia a thread que gerar e envia o informativo de novas aquici��es para os usu�rios que cadastraram 
	 * interesse em receber esse informativo.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/administracao.jsp</li>
	 *   </ul>
	 *   
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String executaEnvioInformativoNovasAquisicoes() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		Executors.newSingleThreadExecutor().execute(new EnviaInformativoNovasAquisicoesTimer());
		
		addMensagemInformation("Gera��o e Envio do Informativo de Novas Aquisi��es Iniciados.");
		return null;
	}
	
	
	/**
	 * 
	 * Inicia a thread que atualiza as estat�scas da biblioteca no cache das buscas, para melhorar a visualiza��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/administracao.jsp</li>
	 *   </ul>
	 *   
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String executaAtualizacaoEstatisticasBiblioteca() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		Executors.newSingleThreadExecutor().execute(new AtualizaEstatisticasBibliotecaTimer());
		
		addMensagemInformation("Atualiza��o das estat�sticas de consulta da biblioteca iniciada.");
		return null;
	}
	
	
	/**
	 * 
	 * Inicia a thread que envia um email para os usu�rios que cadastraram interesse em algum assunto ou autor da biblioteca.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/administracao.jsp</li>
	 *   </ul>
	 *   
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String executaNotificacaoUsuariosInteressados() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		Executors.newSingleThreadExecutor().execute(new NotificaUsuariosInteressadosDSITimer());
		
		addMensagemInformation("Envio de avisos aos usu�rios interessados iniciada.");
		return null;
	}
	
	
	/**
	 * 
	 * Inicia a thread que envia um email para os usu�rios que cadastraram interesse em algum assunto ou autor da biblioteca.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/administracao.jsp</li>
	 *   </ul>
	 *   
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String executaBaixaMultasPagasAutomaticamente() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		Executors.newSingleThreadExecutor().execute(new BaixarMultasPagasAutomaticamenteBibliotecaTimer());
		
		addMensagemInformation("Verifica��o das multas pagas automaticamente na biblioteca iniciada.");
		return null;
	}
	
}
