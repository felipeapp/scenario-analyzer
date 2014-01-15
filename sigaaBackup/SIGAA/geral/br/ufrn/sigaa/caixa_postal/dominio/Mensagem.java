/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.caixa_postal.dominio;

import java.text.SimpleDateFormat;
import java.util.Collection;

import br.ufrn.arq.email.MailBody;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe utilizada para representar mensagens no SIGAA
 *
 * @author Juliano Rafael
 *
 */

public class Mensagem extends br.ufrn.arq.caixa_postal.Mensagem {
	

	/** Usuário que se destina a mensagem */
	private Usuario usuarioDestino;
	
	/** Remetente da mensagem */
	private UsuarioGeral remetente;

	/** Mensagem relacionada com uma outra */
	private Mensagem replyFrom;
	
	/** Mensagens de respostas */
	private Collection<Mensagem> respostas;
	
	public Mensagem() {

	}
	
	public MailBody toMailBody() {

		MailBody mail = new MailBody();
		mail.setAssunto("[SIGAA]" + getTitulo());
		String msg = getMensagem()
				+ "\n\n"
				+ "Mensagem Gerada pelo SIGAA -  Sistema Integrado de Gestão de Atividades Acadêmicas";
		mail.setMensagem(msg);
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setReplyTo("sigaa@info.ufrn.br");

		return mail;
	}

	public void setDescricao(String descricao) {
		setTitulo(descricao);
	}

	public String getDescricao() {
		return getTitulo();
	}

	public String getDataCadastroStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(dataCadastro);
	}

	public String getTituloResumido() {
		StringBuffer tituloResumido = new StringBuffer();

		if (isChamado())
			tituloResumido.append(getNumChamado() + " - ");

		if (titulo.length() > MAX_TITLE_LENGTH)
			tituloResumido
					.append(titulo.substring(0, MAX_TITLE_LENGTH) + "...");
		else
			tituloResumido.append(titulo);

		return tituloResumido.toString();
	}

	public UsuarioGeral getRemetente() {
		return remetente;
	}

	public void setRemetente(UsuarioGeral remetente) {
		this.remetente = remetente;
	}

	public Mensagem getReplyFrom() {
		return replyFrom;
	}

	public void setReplyFrom(Mensagem replyFrom) {
		this.replyFrom = replyFrom;
	}

	public Usuario getUsuarioDestino() {
		return usuarioDestino;
	}

	public void setUsuarioDestino(Usuario usuarioDestino) {
		this.usuarioDestino = usuarioDestino;
	}

	public Collection<Mensagem> getRespostas() {
		return respostas;
	}

	public void setRespostas(Collection<Mensagem> respostas) {
		this.respostas = respostas;
	}

	
}
