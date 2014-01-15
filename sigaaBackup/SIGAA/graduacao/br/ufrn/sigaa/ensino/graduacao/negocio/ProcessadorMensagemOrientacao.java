/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/04/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.DestinatarioMensagemOrientacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MensagemOrientacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.NotificacaoOrientacaoAcademica;

/**
 * Procesador responsável por operações referentes às {@link MensagemOrientacao}.
 * 
 * @author bernardo
 *
 */
public class ProcessadorMensagemOrientacao extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		MovimentoCadastro movimento = (MovimentoCadastro) mov;

		if( mov.getCodMovimento() == SigaaListaComando.CADASTRAR_MENSAGEM_ORIENTACAO )
			return enviarECadastrar(movimento);
		else if( mov.getCodMovimento() == SigaaListaComando.CADASTRAR_MENSAGEM_TODOS_ORIENTANDOS )
			return cadastrarMultiplas(movimento);
		
		return null;
	}

	/**
	 * Cadastra múltiplas mensagens enviadas no banco de dados.
	 * 
	 * @param movimento
	 * @return
	 * @throws DAOException 
	 */
	private Object cadastrarMultiplas(MovimentoCadastro movimento) throws DAOException {
		NotificacaoOrientacaoAcademica not = (NotificacaoOrientacaoAcademica) movimento.getObjMovimentado();
		GenericDAO dao = getGenericDAO(movimento);
		
		try {
			for (DestinatarioMensagemOrientacao destinatario : not.getDestinatariosMensagemOrientacao()) {
				MensagemOrientacao mensagemOrientacao = new MensagemOrientacao();
				
				mensagemOrientacao.setAssunto(not.getTitulo());
				mensagemOrientacao.setMensagem(not.getMensagem());
				mensagemOrientacao.setOrientacaoAcademica(destinatario.getOrientacaoAcademica());
				
				dao.createNoFlush(mensagemOrientacao);
			}
		} finally {
			dao.close();
		}
		
		return null;
	}

	/**
	 * Envia a mensagem e realiza seu cadastro no banco de dados.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException 
	 */
	private Object enviarECadastrar(MovimentoCadastro mov) throws DAOException {
		enviar(mov);
		cadastrar(mov);
		
		return null;
	}

	/**
	 * Envia a mensagem ao seu destinatário.
	 * 
	 * @param objMovimentado
	 */
	private void enviar(MovimentoCadastro mov) {
		MensagemOrientacao mensagemOrientacao = (MensagemOrientacao) mov.getObjMovimentado();
		
		MailBody mail = new MailBody();
		
		mail.setAssunto(mensagemOrientacao.getAssunto());
		mail.setFromName(mensagemOrientacao.getOrientacaoAcademica().getServidor().getNome());
		mail.setEmail(mensagemOrientacao.getOrientacaoAcademica().getDiscente().getPessoa().getEmail());
		mail.setMensagem(mensagemOrientacao.getMensagem());
		
		Mail.send(mail);
	}
	
	/**
	 * Realiza o cadastro da mensagem enviada.
	 * 
	 * @param mensagemOrientacao
	 * @throws DAOException 
	 */
	private void cadastrar(MovimentoCadastro mov) throws DAOException {
		getGenericDAO(mov).create(mov.getObjMovimentado());
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
