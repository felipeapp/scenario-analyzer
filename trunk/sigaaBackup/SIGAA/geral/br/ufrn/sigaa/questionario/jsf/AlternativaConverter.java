/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '28/10/2008'
 *
 */
package br.ufrn.sigaa.questionario.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import br.ufrn.sigaa.questionario.dominio.Alternativa;

/**
 * Converter utilizado para recuperar as seleções de alternativas
 * realizadas pelo usuário durante o preenchimento de um questionário
 * 
 * @author wendell
 *
 */
public class AlternativaConverter implements Converter{

	public Object getAsObject(FacesContext context, UIComponent c, String s) {
		
		Alternativa alternativa; 
		if (s == null || s.trim().equals("")){
			alternativa = null;
		} else {
			alternativa = new Alternativa(Integer.valueOf(s));
		}
		
		return alternativa;
	}

	public String getAsString(FacesContext context, UIComponent c, Object o) {
		if ( o != null )
			return String.valueOf( ((Alternativa) o).getId() );
		return null;
	}

}
