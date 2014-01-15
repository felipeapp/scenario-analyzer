/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 26/11/2007
 */
package br.ufrn.arq.web.jsf;

import javax.faces.component.UICommand;

/**
 * Managed Bean utilitário para auxiliar na integração de JSF com JSTL.
 *
 * @deprecated A partir da versão 1.2 do JSF.
 * @author Gleydson Lima
 *
 */
@Deprecated
public class UtilMBean {

	private int id;

	private int id2;

	private String str;

	// usado para renderizaçoes quando o atributo de controle se encontra em um bean acessado por JSTL
	private boolean renderizado;

	private UICommand uiCommand = new UICommand();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId2() {
		return this.id2;
	}

	public void setId2(int id2) {
		this.id2 = id2;
	}

	public String getCreate() {
		return "";
	}

	public UICommand getUiCommand() {
		return uiCommand;
	}

	public void setUiCommand(UICommand uiCommand) {
		this.uiCommand = uiCommand;
	}

	public boolean isRenderizado() {
		return renderizado;
	}

	public void setRenderizado(boolean renderizado) {
		this.renderizado = renderizado;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

}
