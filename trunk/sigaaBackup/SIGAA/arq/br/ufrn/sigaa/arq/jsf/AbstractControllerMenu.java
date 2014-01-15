/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Criado em: 06/11/2007
 * 
 */
package br.ufrn.sigaa.arq.jsf;

import javax.faces.component.UICommand;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.event.ActionEvent;

import br.ufrn.sigaa.dominio.Unidade;

@SuppressWarnings("unchecked")
public class AbstractControllerMenu extends SigaaAbstractController {

	/**
	 * Retorna o tipo de unidade acadêmica do usuário
	 * @return
	 */
	public int getTipoUnidade() {
		return getUsuarioLogado().getUnidade().getTipoAcademica() != null ? getUsuarioLogado().getVinculoAtivo().getUnidade().getTipoAcademica() : Unidade.UNIDADE_DIREITO_GLOBAL;
	}

	/**
	 * Redirecionar no menu de acordo com o valor passado no menu
	 * @param evt
	 */
	public void redirecionar(ActionEvent evt) {
		UICommand itemMenu = (UICommand) evt.getSource();
		
		if (itemMenu instanceof HtmlCommandLink)
			redirect(getParameter("url"));
		else
			redirect((String) itemMenu.getValue());
	}

}
