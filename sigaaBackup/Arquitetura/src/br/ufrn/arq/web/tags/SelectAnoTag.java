/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;
import org.apache.struts.taglib.html.Constants;

import br.ufrn.arq.util.ValidatorUtil;
/**
 * 
 * Custom tag para a geração dinâmica de um select com a opção pré-selecionada
 * para o ano atual ou para o parâmetro {@see #padrao} .
 *
 * @author Rafael Borja
 * @author Gleydson Lima
 *
 */
public class SelectAnoTag extends BaseHandlerTag {

    public final static int ANO_INCIO_PADRAO = 2000;

    private int anoFim;

    private int anoInicio;
    
    private String name = Constants.BEAN_KEY;

    private int padrao;
    
    private boolean disabled;
    
    private String onchange;
    
    private String onclick;
    
    private String onfocus;

    private String property;
    
    /** 
     * Atributo que representa a opção inicial, geralmente usada para informar ao usuário que precisa selecionar uma opção.
     * Sempre assumirá o valor zero.
     * 
     * Ex.:
     * "SELECIONE UM ANO"
     * "SELECIONAR"
     * "TODOS"
     * 
     * @author Weinberg Souza
     * */
    private String opcaoSelecionar;
    


	public int doStartTag() throws JspException {

        TagUtils tagUtils = TagUtils.getInstance();
        
        if (name != null || property != null) {
            Object anoBean = tagUtils.lookup(pageContext, name, property, null);
            if (anoBean != null) {
                padrao = Integer.parseInt(String.valueOf(anoBean));
            }
        }
        
        // Ano atual
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int anoAtual = c.get(Calendar.YEAR);
        
        if (anoInicio == 0)
            anoInicio = ANO_INCIO_PADRAO;
        if (anoFim == 0)
            anoFim = anoAtual;
        if (padrao == 0 && ValidatorUtil.isEmpty(getOpcaoSelecionar()))
            padrao = anoAtual;

        if (anoFim < anoInicio)
            throw new JspTagException("anoFim é inferior a anoInicio.");
        if (name==null && property==null)
            throw new JspTagException("name ou property devem ser indicados.");

        tagUtils.write(pageContext, "<select name=\"");
        if (name != Constants.BEAN_KEY)
            tagUtils.write(pageContext, name + (property!=null? ".": "\""));
        
        if (property != null)
            tagUtils.write(pageContext, property + "\"");
        
        if (disabled == true)
            tagUtils.write(pageContext, "disabled=disabled");
        
        if (onclick != null)
            tagUtils.write(pageContext, "onclick=\"" + onclick + "\"");
        
        if (onfocus!= null)
            tagUtils.write(pageContext, "onfocus=\"" + onfocus + "\"");
        
        if (onchange != null)
            tagUtils.write(pageContext, "onchange=\"" + onchange + "\"");
        
        // Submete o form ao teclar enter
        tagUtils.write(pageContext, "onkeyup=\"");
        tagUtils.write(pageContext, "if (((window.Event) ? event.which : event.keyCode) == 13) ");
        tagUtils.write(pageContext, "this.form.submit();");
        tagUtils.write(pageContext, "\"");
        
        
        tagUtils.write(pageContext, ">\n");
        
        // opcaoSelecionar
        if(!ValidatorUtil.isEmpty(getOpcaoSelecionar())) {
        	//padrao = 0;
        	tagUtils.write(pageContext, "<option value=\"0\">" + getOpcaoSelecionar() + "</option>\n");
        }
        
        
        for (int ano = anoInicio; ano <= anoFim + 1; ano++) {
            tagUtils.write(pageContext, "<option value=\""
                    + String.valueOf(ano) + (ano == padrao ? "\" SELECTED>" : "\">") 
                    + String.valueOf(ano) + "</option>\n");
        }
        tagUtils.write(pageContext, "</select>");

        return (SKIP_BODY);
    }
    
    public void release() {
        this.anoFim = 0;
        this.anoInicio = 0;
        this.padrao = 0;
        this.disabled = false;
        
        this.name = Constants.BEAN_KEY;
        this.property = null;
        this.onchange = null;
		this.onclick = null;
		this.onfocus = null;
		
		this.opcaoSelecionar = null;
        
        super.release();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int getAnoFim() {
        return anoFim;
    }

    public void setAnoFim(int anoFim) {
        this.anoFim = anoFim;
    }

    public int getAnoInicio() {
        return anoInicio;
    }

    public void setAnoInicio(int anoInicio) {
        this.anoInicio = anoInicio;
    }

    public int getPadrao() {
        return padrao;
    }

    public void setPadrao(int padrao) {
        this.padrao = padrao;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getOnchange() {
        return onchange;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public String getOnfocus() {
        return onfocus;
    }

    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }
    
    public String getOpcaoSelecionar() {
		return opcaoSelecionar;
	}

	public void setOpcaoSelecionar(String opcaoSelecionar) {
		this.opcaoSelecionar = opcaoSelecionar;
	}    
    
}