/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 28/04/2008
 */
package br.ufrn.arq.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import br.ufrn.arq.util.Formatador;


/**
 * Conversor de valor monetário em double
 * 
 * @author David Pereira
 * @author Gleydson Lima
 */
public class ValorMoedaConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent component, String value) throws ConverterException {
		
		
		if (value == null || value.trim().equals("")){
			if(component.getAttributes().get("valor_vazio") != null && component.getAttributes().get("valor_vazio").equals("true"))
				return null;
			return 0.0;
		}
		
		try {
			return Formatador.getInstance().parseValor(value);
		}  catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public String getAsString(FacesContext arg0, UIComponent component, Object value) throws ConverterException {

		if (value == null ){
			return "";
		}
		
		if(component.getAttributes().get("casasDecimais") != null && component.getAttributes().get("casasDecimais").equals("3")){
			return Formatador.getInstance().formatarMoeda3(Double.parseDouble(value.toString()));
		}
		
		if(component.getAttributes().get("casasDecimais") != null && component.getAttributes().get("casasDecimais").equals("4")){
			return Formatador.getInstance().formatarMoeda4(Double.parseDouble(value.toString()));
		}

		return Formatador.getInstance().formatarMoeda(Double.parseDouble(value.toString()));
	}

}


