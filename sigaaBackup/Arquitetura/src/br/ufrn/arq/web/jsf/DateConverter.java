/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 14/04/2008
 */
package br.ufrn.arq.web.jsf;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.servlet.http.HttpSession;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;

/**
 * Conversor de String para Datas
 *
 * @author David Pereira
 * @author Gleydson Lima
 */
public class DateConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {

		if (value == null || value.trim().equals("")) {
			return null;
		}
		
		if(!CalendarUtils.isFormatoDataValida(value)){
			addMensagem(context, component);
			return null;
		}
		
		Date data = Formatador.getInstance().parseDate(value);
		
		if(data == null){
			addMensagem(context, component);
			return null;
		}
		
		if (CalendarUtils.getAno(data) < 1900) {
			addMensagem(context, component);
			return null;
		}
			

		return data;
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {

		if (value == null || (String.valueOf(value)).trim().equals("")) {

			return "";
		}

		return Formatador.getInstance().formatarData((Date) value);
	}

	/**
	 * Adiciona uma {@link ListaMensagens} no {@link HttpSession}
	 * @param context
	 * @param component
	 */
	private void addMensagem(FacesContext context, UIComponent component) {
		String title = FacesHelper.getTitle(component);
		/*Defina a tag title no input do campo de data na JSP, 
		 *para o nome do campo ser exibido na mensagem de erro.*/
		String erro = (title == null ? "Campo de Data" : title) + ": Formato inválido.";
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		ListaMensagens listaMensagens = (ListaMensagens) session.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
		
		if (listaMensagens == null)
			listaMensagens = new ListaMensagens();
		
		listaMensagens.addErro(erro);
		session.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, listaMensagens);
	}
}
