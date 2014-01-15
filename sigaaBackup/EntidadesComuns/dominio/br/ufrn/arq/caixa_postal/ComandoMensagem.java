package br.ufrn.arq.caixa_postal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;


/**
 *
 * Comando que é recebido pelo Message Driven-Bean para enviar mensagens no
 * sistema de acordo com a condição específica.
 *
 * @author Gleydson Lima
 *
 */
public class ComandoMensagem implements Serializable {

	public static final int MENSAGEM_PAPEL = 1;

	/** Solicitação de Cadastro */
	public static final int MENSAGEM_GRUPO_MATERIAL_CADASTRO = 2;

	public static final int MENSAGEM_TODOS = 3;

	// Mensagem template de envio
	private Mensagem mensagem;

	private UsuarioGeral solicitante;
	
	private Collection<UsuarioGeral> destinatarios;

	private int tipo;

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	public UsuarioGeral getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(UsuarioGeral solicitante) {
		this.solicitante = solicitante;
	}
	
	
	public Collection<UsuarioGeral> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(Collection<UsuarioGeral> destinatarios) {
		this.destinatarios = destinatarios;
	}

	/**
	 * Envia mensagem a todos os usuários que tiverem o papel selecionado
	 *
	 * @throws DAOException
	 */
	public void enviaMensagemUsuarios() throws DAOException {
		
		MensagemDAO msgDAO = DAOFactory.getInstance().getDAO(MensagemDAO.class);
		msgDAO.setSistema(Sistema.COMUM);
		
		try {
			
			for (UsuarioGeral user : destinatarios) {

				mensagem.setId(0);
				mensagem.setDataCadastro(new Date());
				mensagem.setUsuarioDestino(user);
				mensagem.setRemetente(solicitante);
				mensagem.setTipo(br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM);
				mensagem.setReplyFrom(null);				
				mensagem.setLida(false);
				
				msgDAO.create(mensagem);
				msgDAO.detach(mensagem);
				
				if (!mensagem.isEnviarEmail()){
					MailBody mail = mensagem.toMailBody(null);
					mail.setEmail(user.getEmail());
					Mail.send(mail);
				}
			}

		} finally {
			msgDAO.close();
		}
	}
}
