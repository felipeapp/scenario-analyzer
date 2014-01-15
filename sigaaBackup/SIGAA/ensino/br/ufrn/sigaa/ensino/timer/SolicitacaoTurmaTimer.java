/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/06/2010
 *
 */
package br.ufrn.sigaa.ensino.timer;

import java.rmi.RemoteException;
import java.util.Arrays;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Thread utilizada para notificar aos Chefes e Secretários de departamentos sobre 
 * solicitações de turmas pendentes.

 * @author Nilber Joaquim
 *
 */
public class SolicitacaoTurmaTimer extends TarefaTimer {

	/** Executa a thread.
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			notificarSolicitacaoTurma();
		} catch (Exception e) {
			notificarErro(e);
		}
	}

	/**
	 *
	 * @throws NamingException
	 * @throws CreateException
	 * @throws RemoteException
	 * @throws DAOException
	 */
	public void notificarSolicitacaoTurma() throws NamingException, RemoteException, CreateException {

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.NOTIFICAR_SOLICITACAO_TURMA);
		
		try {
			facade.execute(mov, new Usuario(Usuario.TIMER_SISTEMA), Sistema.SIGAA);
		} catch (Exception e) {
			notificarErro(e);
		}

	}
	
	/**
	 * Método que manda email pra administração em caso de erro na rotina dessa classe.
	 * 
	 * @param e
	 */
	private void notificarErro(Exception e) {
		e.printStackTrace();
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "Erro SIGAA - NOTIFICAR SOLICITAÇÃO TURMA: " + e.getMessage();
		String mensagem =  "Server: " +  NetworkUtils.getLocalName() + "<br>" +
			e.getMessage() + "<br><br><br>" + Arrays.toString(e.getStackTrace()).replace(",", "\n") +
			(e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");

		// Enviando email para administração do sistema para notificar do erro
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
	}
	
	
	public static void main(String[] args) throws RemoteException, NamingException, CreateException {

		System.out.println("\n\n\n INVOCANDO TRANCAMENTO TIMER \n\n\n ");

		SolicitacaoTurmaTimer timer = new SolicitacaoTurmaTimer();
		timer.notificarSolicitacaoTurma();

		System.out.println("\n\n\n TRANCAMENTO TIMER REALIZADO COM SUCESSO \n\n\n ");

	}
	
	

}
