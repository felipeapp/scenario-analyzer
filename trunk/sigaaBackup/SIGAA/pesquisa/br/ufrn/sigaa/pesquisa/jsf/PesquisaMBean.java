/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/09/2012'
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import javax.faces.component.UICommand;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Managed-Bean com operações gerais de Pesquisa.
 * 
 * @author Leonardo
 *
 */
@Component("pesquisaMBean") @Scope("session")
public class PesquisaMBean extends SigaaAbstractController<Object> {

	/** Indica qual subaba está ativa na view. */
	private String subAbaProjetosAtiva = "tabPesquisa";

	/**
	 * Construtor padrão.
	 */
	public PesquisaMBean() {
	}
	
	/**
	 * Altera sub-aba ativa.
	 * @param listener
	 */
	public void changeActiveProjSubTab(ActionEvent event) {
		setSubAbaProjetosAtiva( getParameter("selectedTab") );
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
	
	public String getSubAbaProjetosAtiva() {
		return subAbaProjetosAtiva;
	}

	public void setSubAbaProjetosAtiva(String subAbaProjetosAtiva) {
		this.subAbaProjetosAtiva = subAbaProjetosAtiva;
	}


	
}
