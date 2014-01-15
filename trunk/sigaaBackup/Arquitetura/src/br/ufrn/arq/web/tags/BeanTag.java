/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import javax.servlet.jsp.JspException;

import br.ufrn.arq.util.Formatador;

/**
 * 
 * Reescrita do BeanWrite para permitir formatar os valores de Float que não
 * tiverem casas decimais
 * 
 * @author Gleydson Lima
 * 
 */
public class BeanTag extends org.apache.struts.taglib.bean.WriteTag {

	protected String formatValue(Object value) throws JspException {

		try {

			if (value.toString().indexOf(".") != -1) {
				float fValue = Float.parseFloat(value.toString());
				int iValue = (int) fValue;
				if (fValue == iValue) {
					return Formatador.getInstance().formatarDecimalInt(fValue);
				}
			}
		} catch (Exception e) {
			return super.formatValue(value);
		}

		return super.formatValue(value);
	}

}
