/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.jsf;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.apache.myfaces.custom.calendar.HtmlCalendarRenderer.DateConverter;

import br.ufrn.arq.util.Formatador;

/**
 * Conversor de String para Hora
 * 
 * @author Gleydson Lima
 *
 */
public class HoraConverter implements DateConverter {

	public Date getAsDate(FacesContext context, UIComponent ui) {
		System.out.println("As Date");
		System.out.println(ui);
		return new Date();
	}

	public Object getAsObject(FacesContext context, UIComponent ui, String str) throws ConverterException {
		return Formatador.getInstance().parseHora(str);
	}

	public String getAsString(FacesContext context, UIComponent ui, Object obj) throws ConverterException {
		if (obj != null) {

			if (obj instanceof Date) {
				return Formatador.getInstance().formatarHora((Date) obj);
			} else {
				return obj.toString();
			}
		} else {
			return "";
		}
	}

}
