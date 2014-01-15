/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 05/02/2009
 */
package br.ufrn.arq.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

/**
 * Conversor para inteiros. Permite que se "atribua"
 * null a campos int, evitando erros de conversão do JSF. 
 * Atribui 0 se for null.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Component("intConverter")
public class IntegerConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {
		try {
			return Integer.parseInt(value);
		} catch(Exception e) {
			return 0;
		}
	}

	public String getAsString(FacesContext ctx, UIComponent comp, Object value) {
		return String.valueOf(value);
	}

}
