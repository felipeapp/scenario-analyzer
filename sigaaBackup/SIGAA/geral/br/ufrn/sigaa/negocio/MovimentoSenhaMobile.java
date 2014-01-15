/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '20/06/2008'
 *
 */
package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.dominio.UsuarioMobile;

public class MovimentoSenhaMobile extends AbstractMovimentoAdapter {
	
	private UsuarioMobile usuarioMobile;

	public UsuarioMobile getUsuarioMobile() {
		return usuarioMobile;
	}

	public void setUsuarioMobile(UsuarioMobile usuarioMobile) {
		this.usuarioMobile = usuarioMobile;
	}
}
