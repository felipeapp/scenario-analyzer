/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 13/10/2004
 */
package br.ufrn.arq.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * Tag Library para inserir botão para actions sem form na view
 *
 * @author Gleydson Lima
 *
 */
public class ButtonTag extends TagSupport {

    private String action;

    private String value;

    private String param;

    private String tabindex;

    private String styleId;
    /** Indica se o contexto vai ser pego automaticamente ou não*/
    private String desconsideraContexto;

    private boolean disabled;

    private String onclick;

    private boolean confirma;

    /** Texto para ser exibido na confirmação */
    private String textoConfirma;

    /** data ou valor * */


    public ButtonTag() {
        super();
    }

    /**
     * @return Retorna action.
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action
     *            The action to set.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
	 * @return Returns the disabled.
	 */
	public boolean isDisabled() {
		return disabled;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	/**
	 * @param disabled The disabled to set.
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}


	public String getDesconsideraContexto() {
		return desconsideraContexto;
	}

	public void setDesconsideraContexto(String desconsideraContexto) {
		this.desconsideraContexto = desconsideraContexto;
	}

	/**
     * @return Retorna value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return Retorna param.
     */
    public String getParam() {
        return param;
    }

    /**
     * @param param
     *            The param to set.
     */
    public void setParam(String param) {
        this.param = param;
    }


    public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	public int doStartTag() throws JspException {

        StringBuffer link = new StringBuffer();

        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

        if (desconsideraContexto != null && desconsideraContexto.equals("true")){
        	//Não pega o contexto automaticamente.
        	//Existem casos de estar no SCO e chamar contexto do sipac
        }else{
        	link.append(req.getContextPath() + "/");
        }
        link.append(action);

        link.append(".do");

        if (param != null && !param.trim().equals("")) {
            link.append("?").append(param);
        }

        String index = (tabindex == null)? "": " tabindex=\"" + tabindex + "\"";
        StringBuffer button = new StringBuffer("<input type=\"button\" onclick=\"");

        if (confirma) {
        	button.append("if (confirm('");
        	if (textoConfirma != null)
        		button.append(textoConfirma);
        	else
        		button.append("Todos os dados informados serão perdidos. Deseja realmente cancelar a operação?");
        	button.append(" ')) {");
        }

        if (onclick != null && !onclick.equals("")){
        	button.append(onclick + ";");
        }

        button.append("location.href='" + link.toString() + "'");
        if (confirma) {
        	 button.append("}\"");
        } else {
        	 button.append("\"");
        }

        button.append("value=\"" + value + "\"" + index + (disabled? " disabled=disabled": "") );

        if (styleId != null && !styleId.equals("")){
        	button.append(" id=\""+styleId+"\" >");
        }else{
        	button.append(" >");
        }

        try {
            pageContext.getOut().println(button);
        } catch (IOException e) {
            throw new JspException(e);
        }

        return super.doStartTag();

    }


	@Override
	public void release() {
		action = null;
		disabled = false;
		id = null;
		param = null;
		tabindex = null;
		value = null;
		textoConfirma = null;
		styleId = null;
		onclick = null;

		super.release();
	}

	public String getTabindex() {
		return tabindex;
	}

	public void setTabindex(String tabindex) {
		this.tabindex = tabindex;
	}

	/**
	 * @return Returns the confirma.
	 */
	public boolean isConfirma() {
		return confirma;
	}

	/**
	 * @param confirma The confirma to set.
	 */
	public void setConfirma(boolean confirma) {
		this.confirma = confirma;
	}

	/**
	 * @return the textoConfirma
	 */
	public String getTextoConfirma() {
		return textoConfirma;
	}

	/**
	 * @param textoConfirma the textoConfirma to set
	 */
	public void setTextoConfirma(String textoConfirma) {
		this.textoConfirma = textoConfirma;
	}




}