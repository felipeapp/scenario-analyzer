/**
 *
 */
package br.ufrn.comum.dominio.notificacoes;

import java.util.Comparator;

import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe de domínio que modela um destinatário de uma notificação
 *
 * @author Ricardo Wendell
 *
 */
public class Destinatario{

	public static final Comparator<Destinatario> EMAIL_COMPARATOR = new EmailComparator();
	public static final Comparator<Destinatario> USUARIO_COMPARATOR = new UsuarioComparator();

	// Utilizado para o envio de mensagens para a caixa postal
	private UsuarioGeral usuario;

	private String nome;

	private String email;

	public Destinatario() {

	}

	public Destinatario(String nome, String email) {
		this.nome = nome;
		this.email = email;
	}

	public Destinatario(UsuarioGeral usuario) {
		this(usuario.getNome(), usuario.getEmail());
		this.usuario = usuario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Destinatario other = (Destinatario) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public void setIdusuario(int idUsuario) {
		usuario = new UsuarioGeral(idUsuario);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Comparador de Destinatarios a partir de seus emails.
	 * Utilizado para definir quais serão notificados por email.
	 *
	 * @author Ricardo Wendell
	 *
	 */
	private static class EmailComparator implements Comparator<Destinatario> {
		public int compare(Destinatario d1, Destinatario d2) {
			if (d1.getEmail() != null && d2.getEmail() != null ) {
				return d1.getEmail().compareTo(d2.getEmail());
			}
			return 0;
		}
	}

	/**
	 * Comparador de Destinatarios a partir de seus usuarios.
	 * Utilizado para definir quais serão notificados por email.
	 *
	 * @author Ricardo Wendell
	 *
	 */
	private static class UsuarioComparator implements Comparator<Destinatario> {
		public int compare(Destinatario d1, Destinatario d2) {
			if (d1.getUsuario() != null && d2.getUsuario() != null ) {
				return d1.getUsuario().getId() - d2.getUsuario().getId();
			}
			return 0;
		}
	}

}
