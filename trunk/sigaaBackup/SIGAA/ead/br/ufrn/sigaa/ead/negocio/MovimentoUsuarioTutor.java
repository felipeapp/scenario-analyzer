/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/24
 */
package br.ufrn.sigaa.ead.negocio;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;

/**
 * Movimento para cadastro de um usuário
 * para um tutor orientador.
 * 
 * @author David Pereira
 *
 */
public class MovimentoUsuarioTutor extends AbstractMovimentoAdapter {

	private TutorOrientador tutor;
	
	private Usuario usuario;

	private HttpServletRequest request;
	
	public TutorOrientador getTutor() {
		return tutor;
	}

	public void setTutor(TutorOrientador tutor) {
		this.tutor = tutor;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
