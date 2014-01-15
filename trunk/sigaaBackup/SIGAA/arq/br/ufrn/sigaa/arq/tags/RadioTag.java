/**
 *
 */
package br.ufrn.sigaa.arq.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * Acrescenta um "label" para a opção;
 * @author Andre M Dantas
 *
 */
public class RadioTag extends org.apache.struts.taglib.html.RadioTag {

	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int doEndTag() throws JspException {
		int ret = super.doEndTag();
		JspWriter out = pageContext.getOut();

		try {
			if (label != null && !label.trim().equals(""))
				out.print("<label for=\"check"+value+"_"+property+"\">"+label+"</label>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}


	@Override
	public String getStyleId() {
		if (super.getStyleId() == null && label != null && !label.trim().equals(""))
			return "check"+value+"_"+property;
		else
			return super.getStyleId();
	}

	@Override
	public String getStyleClass() {
		if (super.getStyleClass() == null)
			return "noborder";
		else
			return super.getStyleClass() + " noborder";
	}

}
