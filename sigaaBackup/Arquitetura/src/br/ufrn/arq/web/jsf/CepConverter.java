/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;


/**
 * Conversor de CEP em int ou um int no formato CEP
 * 
 * @author Raphaela Galhardo
 * @author Gleydson Lima
 */
public class CepConverter implements Converter{

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) throws ConverterException {

		if (value == null || value.trim().equals("")){
			value = "0";
		}
		return StringUtils.extractInteger(value);
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) throws ConverterException {

		if (value != null && !(String.valueOf(value)).trim().equals("0") && !(String.valueOf(value)).trim().equals("")){
			String cepLimpo = Formatador.getInstance().parseStringCPFCNPJ(value.toString());
			
			return Formatador.getInstance().formatarCep(StringUtils.extractInteger(cepLimpo));
		}
		
		return "";
	}

}


