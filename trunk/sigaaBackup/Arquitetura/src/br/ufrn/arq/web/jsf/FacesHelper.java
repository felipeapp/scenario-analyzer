/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 07/05/2009
 */
package br.ufrn.arq.web.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Classe para auxiliar na exibição das mensagens de erros de validação dos campos JSF
 * 
 * @author Diogo Souto
 * @author Gleydson Lima
 */
public class FacesHelper{

	/**
	 * Retorna o ApplicationContext com base no ServletContext.
	 * @param servletContext
	 * @return
	 */
	public static ApplicationContext getApplicationContext(ServletContext servletContext){
		return WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}

	/**
	 * Retorna o ApplicationContext com base no FacesContext.
	 * @param context
	 * @return
	 */
	public static ApplicationContext getApplicationContext(FacesContext context){
		return getApplicationContext((ServletContext) context.getExternalContext().getContext());
	}

	/**
	 * Retorna o ApplicationContext com base na instância corrente do FacesContext.
	 * @return
	 */
	public static ApplicationContext getCurrentApplicationContext(){
		FacesContext context = FacesContext.getCurrentInstance();
		return getApplicationContext(context);
	}

	/**
	 * Retorna o resultado da expressão passada como parâmetro.
	 * @return
	 */
	public static Object getValueExpression(ValueExpression ve){
		if (ve == null)
			return null;
		
		FacesContext context = FacesContext.getCurrentInstance();
		return ve.getValue(context.getELContext());
	}

	/**
	 * Retorna os atributos que foram passados através de parâmetros
	 * de request dentro de um determinado componente em forma de HashMap
	 * @param context
	 * @param component
	 * @return
	 * @throws ConverterException
	 */
	public static Map<String, Object> getRequestParameterMap(FacesContext context, UIComponent component) throws ConverterException {

		HashMap<String, Object> request = new HashMap<String, Object>();

		List<UIComponent> filhos = component.getChildren();

		if(!isEmpty(filhos)){
			for(UIComponent filho : filhos){
				if(filho instanceof UIParameter){
					UIParameter parametros = (UIParameter) filho;
					request.put(parametros.getName(), parametros.getValue());
				}
			}
		}

		return request;
	}

	/**
	 * Retorna o valor de um atributo específico que tenha sido
	 * passado como parâmetro dentro do componente especificado
	 * @param context
	 * @param component
	 * @param nameParameter
	 * @return
	 * @throws ConverterException
	 */
	public static Object getParameter(FacesContext context, UIComponent component, String nameParameter) throws ConverterException {
		return getRequestParameterMap(context, component).get(nameParameter);
	}

	/**
	 * Adiciona uma mensagem de erro ao FacesContext.
	 * @param context
	 * @param component
	 * @param erro
	 * @throws ConverterException
	 */
	public static void addErrorMessage(FacesContext context, UIComponent component, String erro) throws ConverterException {
		String title = getTitle(component);
		if(title == null)
			erro = component.getId() + ": " + erro;
		else
			erro = title + ": " + erro;

		context.addMessage(component.getId(), new FacesMessage(erro));
	}

	/**
	 * Retorna o título especificado para o componente na view
	 * @param component
	 * @return
	 */
	public static String getTitle(UIComponent component){

		try{
			Class<?> classe = component.getClass();
			Method metodo = classe.getMethod("getTitle");
			return (String) metodo.invoke(component);
		}catch (Exception e) {

		}

		return null;
	}

}
