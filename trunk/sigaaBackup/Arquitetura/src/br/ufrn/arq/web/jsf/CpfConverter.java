/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;

/**
 * Conversor de CPF em long ou um long no formato CPF
 * 
 * @author Raphaela Galhardo
 * @author Gleydson LIma
 */
public class CpfConverter implements Converter{

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) throws ConverterException {
		if (value == null || value.trim().equals("")){
			value = "0";
		}

		return StringUtils.extractLong(value);
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) throws ConverterException {

		if (value != null && !(String.valueOf(value)).trim().equals("0") && !(String.valueOf(value)).trim().equals("")){

			Formatador fmt = Formatador.getInstance();
			Long cpf_cnpj = StringUtils.extractLong(value.toString());

			Object type = FacesHelper.getParameter(arg0, arg1, "type");
			if(type != null && "cpf".equalsIgnoreCase(String.valueOf(type)))
					return fmt.formatarCPF(cpf_cnpj);

			return fmt.formatarCPF_CNPJ(cpf_cnpj);
		}
		return "";
	}

}


