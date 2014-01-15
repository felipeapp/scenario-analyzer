/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.arq.web.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;

/**
 * Renderer para o componente da Árvore de Unidades.
 * Renderiza um campo hidden, um campo de texto, um autocomplete e uma árvore
 * de unidades. 
 *  
 * @author David Pereira
 * 
 */
public class ArvoreUnidadesRenderer extends Renderer {

	@Override
	@SuppressWarnings("unchecked")
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		Class<?> targetClass = (Class<?>) component.getValueExpression("value").getType(context.getELContext());
		String idStr = getValue(component).toString();
		
		if (!isEmpty(idStr)) {
			
			int id = Integer.parseInt(idStr);
			
			if (PersistDB.class.isAssignableFrom(targetClass)) {
				GenericDAOImpl dao = DAOFactory.getInstance().getDAO(GenericDAOImpl.class, req);
				try {
					return dao.findByPrimaryKey(id, (Class<? extends PersistDB>) targetClass);
				} catch (DAOException e) {
					return null;
				}				
			} else {
				return id;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		if (component instanceof UIInput) {
			UIInput input = (UIInput) component;
			String clientId = input.getClientId(context);
			Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();
			String newValue = requestMap.get(clientId.split(":")[1]);
			if (newValue != null) {
				input.setSubmittedValue(newValue);
			}
		}
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ArvoreUnidades arvore = ArvoreUnidades.getInstance();
		String listaArvore = null;
		
		Integer raiz = (Integer) component.getAttributes().get("raiz");
		Integer[] raizes = (Integer[]) component.getAttributes().get("raizes");
		String tipo = (String) component.getAttributes().get("tipo");
		String modo = (String) component.getAttributes().get("modo");
		String onselect = (String) component.getAttributes().get("onselect");
		
		int tipoInt = 0;
		boolean ajax = false;
		if (tipo == null) tipo = "organizacional";
		if (modo != null && ("server".equals(modo) || "ajax".equals(modo))) ajax = true;
		
		if (raizes == null) {
			if (raiz == null) {
				raizes = new Integer[] { UnidadeGeral.UNIDADE_DIREITO_GLOBAL };
			} else {
				raizes = new Integer[] { raiz };
			}
		}

		if ("orcamentaria".equals(tipo)) {
			listaArvore = arvore.getArvoreOrcamentaria(raizes, ajax);
			tipoInt = ArvoreUnidades.ORCAMENTARIA;
		} else if ("organizacional".equals(tipo)) {
			listaArvore = arvore.getArvoreOrganizacional(raizes, ajax);
			tipoInt = ArvoreUnidades.ORGANIZACIONAL;
		} else {
			listaArvore = arvore.getArvoreAcademica(raizes, ajax);
			tipoInt = ArvoreUnidades.ACADEMICA;
		}
			
		String id = component.getClientId(context);
		id = id.split(":")[1];
		
		Object currentValue = getValue(component);
		UnidadeGeral unidadeAtual = null;
		
		if (!isEmpty(currentValue)) {
			
			if (currentValue instanceof PersistDB) {
				PersistDB unidade = (PersistDB) currentValue;
				if (!isEmpty(unidade))
					unidadeAtual = arvore.getUnidade(unidade.getId(), tipoInt);
			} else {
				unidadeAtual = arvore.getUnidade((Integer) currentValue, tipoInt);
			}
		}
		
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", "type");
		writer.write("var " + id + "_listaArvore = new ArvoreUnidade();");
		writer.endElement("script");
		
		writer.startElement("input", component);
		writer.writeAttribute("type", "hidden", "hidden");
		writer.writeAttribute("id", id, "id");
		writer.writeAttribute("name", id, "id");
		writer.writeAttribute("value", unidadeAtual == null ? null : unidadeAtual.getId(), "value"); // Pegar o ID
		writer.endElement("input");
		
		writer.startElement("input", component);
		writer.writeAttribute("type", "text", "text");
		writer.writeAttribute("id", id + "_codigo", "id");
		writer.writeAttribute("name", id + "_codigo", "id");
		writer.writeAttribute("size", 10, "size");
		writer.writeAttribute("value", unidadeAtual == null ? null : unidadeAtual.getCodigoFormatado(), "value"); // Pegar o Código
		writer.writeAttribute("onkeyup", id + "_listaArvore.selecionar();", "onkeyup");
		writer.writeAttribute("onkeypress", "return(formataUnidade(this, event));", "onkeypress");
		writer.writeAttribute("onblur", "removeNonNumbersCharacters(this);", "onblur");
		writer.writeAttribute("style", "float: left; width: 75px;", "style");
		writer.endElement("input"); 
		
		writer.write("&nbsp;");
		
		writer.startElement("input", component);
		writer.writeAttribute("type", "text", "text");
		writer.writeAttribute("id", id + "_nome", "id");
		writer.writeAttribute("name", id + "_nome", "id");
		
		String size = (String) component.getAttributes().get("size");
		if (null != size) {
			writer.writeAttribute("size", size, "size");
		}
		
		writer.writeAttribute("value", unidadeAtual == null ? null : unidadeAtual.getCodigoNome(), "value"); // Pegar o Nome
		writer.endElement("input");
		
		writer.startElement("span", component);
		writer.writeAttribute("id", id + "_indicator", "id");
		writer.writeAttribute("style", "display: none", "style");
		writer.write("<img src=\"/shared/img/websnapr/loading.gif\" alt=\"Working...\"/>");
		writer.endElement("span");
		
		writer.startElement("div", component);
		writer.writeAttribute("id", id + "_autocomplete", "id");
		writer.endElement("div");
		
		writer.startElement("div", component);
		writer.writeAttribute("id", id + "_divorigem", "id");
		writer.writeAttribute("style", "width:0px; height:0px; display: none;", "style");
		writer.write(listaArvore);
		writer.endElement("div");
		
		writer.startElement("div", component);
		writer.writeAttribute("id", id + "_divdestino", "id");
		writer.writeAttribute("style", "width:600px; height:200px; overflow: auto;", "style");
		writer.endElement("div");
		
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", "type");
		writer.write(id + "_listaArvore.init('" + id + "_divorigem', '" + id + "_divdestino', '" + id + "_codigo', '" + id + "', '" + id + "_nome', " + ajax + ", " + tipoInt + ", \"" + (onselect == null ? "" : onselect) + "\");");
		writer.write(id + "_listaArvore.selecionar();");
		writer.write("jQuery('#" + id + "_nome').keyup(function() { if (jQuery(this).val().length == 0) { jQuery('#" + id + "_codigo').val(''); " + id + "_listaArvore.expandir(); } });");
		writer.write("jQuery('#" + id + "_nome').autocomplete('/admin/buscaUnidade', { minChars : 3, extraParams: { tipo: " + tipoInt + " }}).result(function(e,d) { jQuery('#" + id + "').val(d[1]); selecionaUnidadeAutocomplete('" + id + "_codigo', '" + id + "_nome', '"+id+"', " + id + "_listaArvore, \"" + (onselect == null ? "" : onselect) + "\"); });");
		writer.endElement("script");
	}
	
	protected Object getValue(UIComponent component) {
		Object value = null;

		if (component instanceof UIInput) {
	    	value = ((UIInput) component).getSubmittedValue();
	    }
	    
		if (value == null && component instanceof ValueHolder) {
			value = ((ValueHolder) component).getValue();
	    }

	    return value;
	}
	
}
