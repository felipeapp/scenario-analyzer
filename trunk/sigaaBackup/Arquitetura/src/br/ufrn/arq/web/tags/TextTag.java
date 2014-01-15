/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import javax.servlet.jsp.JspException;

/**
 * Reescrita da Tag Text Tag do Struts
 *
 * @author Gleydson Lima
 *
 */
public class TextTag extends org.apache.struts.taglib.html.TextTag {

	private boolean truncDecimal;

	public void setValue(String value) {

		try {
			float valueFloat = Float.parseFloat(value);
			int valueInt = (int) valueFloat;
			if (valueInt == valueFloat) {
				super.setValue(String.valueOf(valueInt));
			}
		} catch (Exception e) {
			super.setValue(value);

		}

	}

	public boolean isTruncDecimal() {
		return truncDecimal;
	}

	public void setTruncDecimal(boolean truncDecimal) {
		this.truncDecimal = truncDecimal;
	}

	@Override
	protected String renderInputElement() throws JspException {
		return super.renderInputElement();
	}

}
