/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 24/11/2005
 */
package br.ufrn.arq.web.tags;

import java.util.Collection;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;
import org.apache.struts.taglib.html.Constants;

import br.ufrn.arq.util.EstadosHelper;
import br.ufrn.comum.dominio.Estado;

/**
 * Tag para mostrar um select com os estados brasileiros. 
 * Uso: <selectEstado name="bean" property="campo"/>. Popula
 * o atributo "campo" de "bean".
 *
 * @author David Ricardo
 * @author Gleydson Lima
 *
 */
public class SelectEstadoTag extends BaseHandlerTag {

	private String name = Constants.BEAN_KEY;

    private String property;
    
    /** Indica que a sigla deve ser usada como value. Caso contrário usa o id do estado (padrão).*/ 
    private boolean valueSigla;
    
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
    
    public boolean isValueSigla() {
		return valueSigla;
	}

	public void setValueSigla(boolean valueSigla) {
		this.valueSigla = valueSigla;
	}

    public int doStartTag() throws JspException {
        
        TagUtils tagUtils = TagUtils.getInstance();
        Object obj = tagUtils.lookup(pageContext, name, property, null);
        
        Object estado = 0;
        if (obj != null) {
        	if (valueSigla)
        		estado = String.valueOf(obj);
        	else
        		estado = Integer.parseInt(String.valueOf(obj));
        } else {
        	// Seleciona estado padrão
        	if (valueSigla)
        		estado = String.valueOf("RN");
        	else
        		estado = EstadosHelper.sigla2Id("RN");
        }
        

        Collection<Estado> estados = EstadosHelper.getEstados();
        
        StringBuffer saida = new StringBuffer(100);
        saida.append("<select name=\"");
        if (name != Constants.BEAN_KEY)
            saida.append(name + ((property!= null)? ".": ""));
        
        if (property != null)
            saida.append(property);
        saida.append("\"");
        
        // Submete o form ao taclar enter
        saida.append(" onkeyup=\"");
        saida.append("if (((window.Event) ? event.which : event.keyCode) == 13) ");
        saida.append("this.form.submit();");
        saida.append("\"");
        
        if (getStyleId() != null)
        	saida.append(" id=\"" + getStyleId() + "\"");
        
        if (getTabindex() != null)
        	saida.append(" tabindex=\"" + getTabindex() + "\"");
        
        if (getStyle()!= null)
        	saida.append(" style=\"" + getStyle() + "\"");
        
        saida.append(">\n");
        
        for (Estado e : estados) {
        	Object value;
        	if (valueSigla)
        		value = e.getDenominacao();
        	else
        		value = e.getId();
        	
        	
            saida.append("<option value=\"" + value + "\"");
            saida.append((value.equals(estado))? " SELECTED" : "");
            saida.append(">\n\t");
            saida.append(e.getDenominacao());
            saida.append("\n</option>\n");
        }
        saida.append("</select>");
        
        tagUtils.write(pageContext, saida.toString());
            
        return (SKIP_BODY);
    }
    
    public void release() {
        super.release();
        
        name = Constants.BEAN_KEY;
        property = null;
        valueSigla = false;
    }
}
