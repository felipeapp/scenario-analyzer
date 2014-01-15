/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean responsável por rodar manualmente os times do sub sistema da bibiblioteca </p>
 *
 * <p>
 * <strong>Todas as tarefas aqui estão programadas para serem executadas automaticamente pelo sistema, essa classe 
 * serve para executar alguma thread manualmente caso a execução dela falhe ou para testes durante o desenvolvimento.
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
	 * Inicia a thread que envia notificação aos usuários que estão com empréstimos vencendo
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		addMensagemInformation("Notificação de vencimento dos empréstimos iniciada.");
		return null;
	}
	
	
	
	/**
	 * 
	 * Inicia a thread que envia notificação aos usuários que estão com empréstimos em atraso
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		addMensagemInformation("Notificação dos empréstimos atrasados iniciada.");
		return null;
	}
	
	
	
	
	/**
	 * 
	 * Inicia a thread que verifica as reservas em espera vencidas, cancelando-as, e colocando as 
	 * próximas reservas para EM ESPERA.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		addMensagemInformation("Verificação das reservas EM ESPERA vencidas iniciada.");
		return null;
	}
	
	
	/**
	 * 
	 * Inicia a thread que gerar e envia o informativo de novas aquicições para os usuários que cadastraram 
	 * interesse em receber esse informativo.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		addMensagemInformation("Geração e Envio do Informativo de Novas Aquisições Iniciados.");
		return null;
	}
	
	
	/**
	 * 
	 * Inicia a thread que atualiza as estatíscas da biblioteca no cache das buscas, para melhorar a visualização.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		addMensagemInformation("Atualização das estatísticas de consulta da biblioteca iniciada.");
		return null;
	}
	
	
	/**
	 * 
	 * Inicia a thread que envia um email para os usuários que cadastraram interesse em algum assunto ou autor da biblioteca.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		addMensagemInformation("Envio de avisos aos usuários interessados iniciada.");
		return null;
	}
	
	
	/**
	 * 
	 * Inicia a thread que envia um email para os usuários que cadastraram interesse em algum assunto ou autor da biblioteca.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		addMensagemInformation("Verificação das multas pagas automaticamente na biblioteca iniciada.");
		return null;
	}
	
}
