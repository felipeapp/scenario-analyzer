/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/08/2007
 *
 */
package br.ufrn.sigaa.ensino.timer;

import java.rmi.RemoteException;
import java.util.Arrays;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.dominio.TrancamentoMatriculaMov;

/**
 * Thread utilizada para consolidar os trancamentos pendentes em 7 dias
 * (tempo parametrizado nos par�metros da gestora acad�mica).
 *
 * @author Victor Hugo
 *
 */
public class TrancamentoTimer extends TarefaTimer {

	/** Executa a thread.
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			consolidarTrancamentosPendentes();
		} catch (Exception e) {
			notificarErro(e);
		}
	}

	/**
	 * Este m�todo tranca as matr�culas que est�o com solicita��es pendentes.
	 * Ou seja, tranca as matr�culas cuja solicita��o de trancamento foi feita 7 (sete) dias atr�s.
	 * Ou ent�o, tranca todas as matr�culas com solicita��es pendentes caso seja o �ltimo dia do trancamento.
	 * @throws NamingException
	 * @throws CreateException
	 * @throws RemoteException
	 * @throws DAOException
	 */
	public void consolidarTrancamentosPendentes() throws NamingException, RemoteException, CreateException {

		TrancamentoMatriculaMov mov = new TrancamentoMatriculaMov();
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_SOLICITACOES_TRANCAMENTO);

		try {
			facade.execute(mov, new Usuario(Usuario.TIMER_SISTEMA), Sistema.SIGAA);
		} catch (Exception e) {
			notificarErro(e);
		}

	}
	
	/**
	 * M�todo que manda email pra administra��o em caso de erro na rotina dessa classe.
	 * 
	 * @param e
	 */
	private void notificarErro(Exception e) {
		e.printStackTrace();
		
		Throwable cause = null;
		if (e.getCause() != null)
			cause = e.getCause();
		else
			cause = e;

		StringBuilder sbErro = new StringBuilder();
		
		sbErro.append("===DADOS DA EXCE��O DISPARADA=== ");
		sbErro.append("<br>");
		sbErro.append("Exce��o: ");
		sbErro.append(e.toString());
		sbErro.append("<br><br>");
		
		
		while (cause != null) {
			sbErro.append("Cause: ");
			sbErro.append(String.valueOf(cause) + "\n");
			sbErro.append("CAUSE STACK TRACE:");
			sbErro.append("\n");
			String trace = Arrays.toString(cause.getStackTrace());
			trace = trace.replace(",", "\n");
			sbErro.append(trace);
			sbErro.append("\n\n");

			cause = cause.getCause();
		}
		
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		//String email = "sigaa@info.ufrn.br";
		String assunto = "Erro SIGAA - TRANCAMENTO TIMER: " + e.getMessage();
		String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br><br>" + sbErro.toString();
		// Enviando email para administra��o do sistema para notificar do erro
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		mail.setContentType(MailBody.TEXT_PLAN);		
		
		Mail.send(mail);
	}
	
}
