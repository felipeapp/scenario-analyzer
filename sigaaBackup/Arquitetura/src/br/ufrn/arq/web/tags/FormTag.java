/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

/**
 * Tag para exibir um fomulário html com campos hidden
 * adicionais para navegação em struts.
 *  
 * @author Gleydson Lima
 *
 */
public class FormTag extends org.apache.struts.taglib.html.FormTag {

	@Override
	protected String renderFormStartElement() {
		String pai = super.renderFormStartElement();
		String dispatch = "<input type=\"hidden\" name=\"dispatch\"  id=\"dispatch\">";
		String view = "<input type=\"hidden\" name=\"view\" value=\"\" id=\"view\">";

		return pai + dispatch + view;
	}

}
