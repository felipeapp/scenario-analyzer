/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/24
 */
package br.ufrn.sigaa.ead.negocio;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;

/**
 * Movimento para cadastro de um usuário
 * para um coordenador de pólo.
 * 
 * @author David Pereira
 *
 */
public class MovimentoUsuarioCoordPolo extends AbstractMovimentoAdapter {

	private CoordenacaoPolo coordenador;
	
	private Usuario usuario;
	
	private HttpServletRequest request;

	public CoordenacaoPolo getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(CoordenacaoPolo coordenador) {
		this.coordenador = coordenador;
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
