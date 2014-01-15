/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/02/2007'
 *
 */
package br.ufrn.sigaa.form;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Representa o formulário de dados do usuário
 * @author Raphaela Galhardo
 *
 */
public class UsuarioForm extends SigaaForm {

	public static final int FIND_BY_LOGIN = 1;
	public static final int FIND_BY_UNIDADE = 2;
	public static final int FIND_BY_NOME = 3;

	private int tipoBusca;
	private String nomeBusca;
	private Usuario usuario;

	public UsuarioForm() {
		usuario = new Usuario();
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

}
