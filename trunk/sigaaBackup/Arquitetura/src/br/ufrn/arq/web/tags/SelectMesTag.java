/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 13/04/2005
 */
package br.ufrn.arq.web.tags;

import java.util.Calendar;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;
import org.apache.struts.taglib.html.Constants;

/**
 * Tag para geração de combo box com os mêses do ano. (Janeiro=0, Dezembro=11).
 * Popula automaticamente o mês de acordo com o bean e propriedade possador por
 * nome e property.
 * 
 * @author Rafael Borja
 * @author Gleydson Lima
 * 
 */
public class SelectMesTag extends BaseHandlerTag {

	private String name = Constants.BEAN_KEY;

	private String property;
	
	private boolean disabled;
	
	/** Indica que o mes atual deve ser marcado como default */
	private boolean mesAtual;

	private String onchange;

	private String onclick;

	private String onfocus;

	private String styleId;
	
	private String[] meses = { "Janeiro", "Fevereiro", "Março", "Abril",
			"Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro",
			"Novembro", "Dezembro" };

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

	public int doStartTag() throws JspException {

		TagUtils tagUtils = TagUtils.getInstance();
		Object obj = tagUtils.lookup(pageContext, name, property, null);

		int mes = 0;
		if (obj != null)
			mes = Integer.parseInt(String.valueOf(obj));
		else if (mesAtual)
			mes = Calendar.getInstance().get(Calendar.MONTH);

		if (mes < 0)
			throw new JspTagException("mes é inferior a 0");
		else if (mes > 11)
			throw new JspTagException("mes é superior a 11.");

		StringBuilder saida = new StringBuilder(100);
		saida.append("<select name=\"");
		if (name != Constants.BEAN_KEY)
			saida.append(name + ((property != null) ? "." : ""));

		if (property != null)
			saida.append(property);

		saida.append("\"");
		
		if (disabled == true)
			saida.append("disabled=disabled");

		if (onclick != null)
			saida.append("onclick=\"" + onclick + "\"");

		if (onfocus != null)
			saida.append("onfocus=\"" + onfocus + "\"");

		if (onchange != null)
			saida.append("onchange=\"" + onchange + "\"");

		if (styleId != null)
			saida.append("id=\"" + styleId + "\"");

		// Submete o form ao taclar enter
		saida.append(" onkeyup=\"");
		saida
				.append("if (((window.Event) ? event.which : event.keyCode) == 13) ");
		saida.append("this.form.submit();");
		saida.append("\"");

		saida.append(">\n");

		for (int i = 0; i < meses.length; i++) {
			saida.append("<option value=\"" + i + "\"");
			saida.append((i == mes) ? " SELECTED" : "");
			saida.append(">\n\t");
			saida.append(meses[i]);
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
		this.disabled = false;
		this.mesAtual = false;

		this.onchange = null;
		this.onclick = null;
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

		/**
		 * @return Returns the styleId.
		 */
		public String getStyleId() {
			return styleId;
		}

		/**
		 * @param styleId The styleId to set.
		 */
		public void setStyleId(String styleId) {
			this.styleId = styleId;
		}

		public boolean isMesAtual() {
			return mesAtual;
		}

		public void setMesAtual(boolean mesAtual) {
			this.mesAtual = mesAtual;
		}
	   
}

