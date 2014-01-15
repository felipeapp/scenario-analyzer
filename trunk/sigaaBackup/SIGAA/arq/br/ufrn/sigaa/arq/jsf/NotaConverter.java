/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 29/11/2006
 *
 */
package br.ufrn.sigaa.arq.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import br.ufrn.arq.util.Formatador;

/**
 * Conversor para notas em disciplinas
 *
 * @author David Ricardo
 *
 */
public class NotaConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) throws ConverterException {

		if (value == null || value.trim().equals("")){
			return null;
		}
		
		Double valor;
		try {
			valor = Formatador.getInstance().parseValor(value);
		} catch (Exception e) {
			return null;
		}
		return valor;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) throws ConverterException {

		if (value == null){
			value = "0";
		}
		return Formatador.getInstance().formatarDecimal1(Double.valueOf(value.toString()));
	}
	
}
