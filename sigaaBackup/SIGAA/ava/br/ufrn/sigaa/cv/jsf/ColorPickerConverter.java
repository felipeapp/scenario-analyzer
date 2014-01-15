package br.ufrn.sigaa.cv.jsf;

import javax.faces.component.UIComponent;  
import javax.faces.context.FacesContext;  
import javax.faces.convert.Converter;  

/**
 * Converter utilizado para permitir que o docente escolha uma cor para o plano de fundo dos tópicos.
 * 
 * @author Fred_Castro
 *
 */

public class ColorPickerConverter implements Converter {  

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {  
		return submittedValue;
	}  

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
		if (value == null)  
			return null;  
		return value.toString();  
	}  
}  