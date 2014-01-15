/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 20/10/2010
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import br.ufrn.academico.dominio.NivelEnsino;

/** Converter utilizado para exibir o nível de ensino por extenso nas JSPs.
 * @author Édipo Elder F. Melo
 *
 */
public class NivelEnsinoConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Character nivel = ' ';
		for (Character key : NivelEnsino.tabela.keySet())
			if (NivelEnsino.getDescricao(key).equalsIgnoreCase(arg2))
				nivel = key;
		return nivel;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (arg2 != null) {
			char nivel = ' ';
			if (arg2 instanceof Character)
				nivel = (Character) arg2;
			if (arg2 instanceof String)
				nivel = ((String) arg2).charAt(0);
			return NivelEnsino.getDescricao(nivel);
		}
		return null;
	}

}
