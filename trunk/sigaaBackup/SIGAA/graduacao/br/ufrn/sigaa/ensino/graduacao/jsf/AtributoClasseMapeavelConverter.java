/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 31/07/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

/**
 * Este converter � respons�vel por converter um item selecionado no
 * autocomplete de equival�ncia de valores entre o dado a ser importado e a
 * classe correspondente.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
public class AtributoClasseMapeavelConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		int id = Integer.parseInt(submittedValue);
		return new SelectItem(id);
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		if (value == null)
			return null;
		else
			return value.toString();
	}

}
