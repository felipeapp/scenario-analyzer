/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import br.ufrn.arq.web.struts.AbstractForm;

/**
 * Formul�rio utilizado na notifica��o de consultores
 * de projetos de pesquisa por email
 *
 * @author Ricardo Wendell
 *
 */
public class NotificacaoConsultoresForm extends AbstractForm {

	private String template;

	public NotificacaoConsultoresForm() {

	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
