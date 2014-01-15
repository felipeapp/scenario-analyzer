/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 13/03/2008
 */
package br.ufrn.arq.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import br.ufrn.arq.util.StringUtils;

/**
 * Conversor para campos associados a atributos do tipo java.lang.String para
 * remover espaços em branco adicionais
 * 
 * @author Ricardo Wendell
 * @author Gleydson Lima
 * 
 */
public class StringConverter implements Converter {
	public static final String CONVERTER_ID = "STRING_CONVERTER";

	private static final String INPUT_TEXT_RENDERER_TYPE = "javax.faces.Text";

	public Object getAsObject(FacesContext context, UIComponent c, String s) {

		if (s != null) {
			if (c.getRendererType().equals(INPUT_TEXT_RENDERER_TYPE)  && c.getAttributes().get("permiteEspacos") == null ) {
				// conversão para latin9 é necessária para buscar no banco eliminando os acentos
				return StringUtils.toLatin9(s.trim());
			}
		}
		return s;
	}

	public String getAsString(FacesContext context, UIComponent c, Object o) {
		if ( o != null )
			return o.toString();
		return null;
	
	}

}
