/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/07/2010
 *
 */
package br.ufrn.sigaa.avaliacao.timer;

import java.util.Arrays;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Thread que roda periodicamente, respons�vel por atualizar a estat�stica do
 * preenchimento da Avalia��o Institucional atual.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class EstatisticasAvaliacaoInstitucionalTimer extends TarefaTimer {

	/** Chama o processador para atualizar os dados estat�sticos.
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ATUALIZAR_ESTATISTICAS_AVALIACAO_INSTITUCIONAL);
			facade.execute(mov, new Usuario(Usuario.TIMER_SISTEMA), Sistema.SIGAA);
		} catch (Exception e) {
			notificarErro(e);
		}
	}

	/**
	 * Envia um e-mail pra administra��o em caso de erro na rotina dessa classe.
	 * 
	 * @param e
	 */
	private void notificarErro(Exception e) {
		e.printStackTrace();
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "Erro SIGAA - ESTAT�STICAS DA AVALIA��O INSTITUCIONAL: " + e.getMessage();
		String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br>" +
			e.getMessage() + "<br><br><br>" + Arrays.toString(e.getStackTrace()).replace(",", "\n") +
			(e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");

		// Enviando email para administra��o do sistema para notificar do erro
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
	}
}
