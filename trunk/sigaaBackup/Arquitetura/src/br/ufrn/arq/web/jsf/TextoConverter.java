/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 22/01/2009
 */
package br.ufrn.arq.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

import br.ufrn.arq.util.StringUtils;

/**
 * Conversor para campos que contém quebras de linhas para serem apresentados
 * em sua formatação correta nas JSPs.
 * 
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
@Component("textoConverter")
public class TextoConverter implements Converter{

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return value;
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		component.getAttributes().put("escape", false);
		
		String texto = value.toString();
		
		if (component.getAttributes().get("lineWrap") != null) {
			int lineWrap = StringUtils.extractInteger(component.getAttributes().get("lineWrap").toString());
			StringBuilder textoSB = new StringBuilder(texto);
			
			int numLinhas = texto.length() / lineWrap;
			
			for (int i = 0; i < numLinhas; i++) {
				textoSB.insert(i * lineWrap + lineWrap, "<br/>");
			}
			
			texto = textoSB.toString();
		}
		
		if (component.getAttributes().get("length") != null) {
			int length = StringUtils.extractInteger(component.getAttributes().get("length").toString());
			if (texto.length() > length)
				texto = texto.substring(0, length) + "...";
		}
		
		return texto.replaceAll("\n", "<br />");
	}

}
