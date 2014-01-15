/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.arq.web.jsf;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

/**
 * Classe Tag Handler para o componente JavaServer Faces
 * da �rvore de unidades.
 * 
 * @author David Pereira
 * 
 */
public class ArvoreUnidadesTag extends UIComponentELTag {

	private static final String COMP_TYPE = "ARVORE_UNIDADES_COMPONENT";

	private static final String RENDER_TYPE = "ARVORE_UNIDADES_RENDERER";

	/** Valor da �rvore de unidades. Aponta para um atributo do tipo UnidadeGeral ou suas classes filhas */
	private ValueExpression value;
	
	/** Unidade a ser usada como raiz da �rvore de unidades. Se null, o padr�o � a gestora global. */
	private ValueExpression raiz;
	
	/** Unidades a serem usada como raizes da �rvore de unidades. Se null, o padr�o � a gestora global. */
	private ValueExpression raizes;
	
	/** Modo de carregamento dos itens da �rvore. Pode ser client ou server. */
	private String modo;
	
	/** Tipo de �rvore: orcamentaria, academica, organizacional */
	private String tipo;
	
	/** Tamanho do componente de texto renderizado para o autocomplete */
	private String size;
	
	/** Javascript a ser executado quando uma unidade for selecionada */
	private String onselect;
	
	@Override
	protected void setProperties(UIComponent component) {
		super.setProperties(component);
		component.setValueExpression("value", value);
		if (raiz != null) component.getAttributes().put("raiz", raiz.getValue(getELContext()));
		if (raizes != null) component.getAttributes().put("raizes", raizes.getValue(getELContext()));
		if (size != null) component.getAttributes().put("size", size);
		if (modo != null) component.getAttributes().put("modo", modo);
		if (tipo != null) component.getAttributes().put("tipo", tipo);
		if (onselect != null) component.getAttributes().put("onselect", onselect);
	}

	public String getComponentType() {
		return COMP_TYPE;
	}

	public String getRendererType() {
		return RENDER_TYPE;
	}

	public ValueExpression getValue() {
		return value;
	}

	public void setValue(ValueExpression value) {
		this.value = value;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getModo() {
		return modo;
	}

	public void setModo(String modo) {
		this.modo = modo;
	}

	public ValueExpression getRaiz() {
		return raiz;
	}

	public void setRaiz(ValueExpression raiz) {
		this.raiz = raiz;
	}

	public String getOnselect() {
		return onselect;
	}

	public void setOnselect(String onselect) {
		this.onselect = onselect;
	}

	public ValueExpression getRaizes() {
		return raizes;
	}

	public void setRaizes(ValueExpression raizes) {
		this.raizes = raizes;
	}
	
}
