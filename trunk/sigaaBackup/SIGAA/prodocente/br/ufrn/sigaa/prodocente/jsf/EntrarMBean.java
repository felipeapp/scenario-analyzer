/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.jsf;

import br.ufrn.arq.web.jsf.AbstractController;

public class EntrarMBean extends AbstractController {

	public String entrarSistema() {

		getCurrentSession().setAttribute("dirBase", "/prodocente/producao/");

		return forward("/prodocente/producao/index.jsp");
	}

}
