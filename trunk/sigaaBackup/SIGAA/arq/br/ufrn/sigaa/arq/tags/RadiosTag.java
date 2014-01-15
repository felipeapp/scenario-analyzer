/**
 *
 */
package br.ufrn.sigaa.arq.tags;

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;

/**
 * Tag que renderiza Radio Buttons a partir de uma colecao de objetos
 * @author Andre M Dantas
 *
 */
public class RadiosTag extends TagSupport {


	private String property;

	private Collection<?> collection;

	private String valueProperty;

	private String labelProperty;

	public Collection<?> getCollection() {
		return collection;
	}

	public void setCollection(Collection<?> collection) {
		this.collection = collection;
	}

	public String getLabelProperty() {
		return labelProperty;
	}

	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValueProperty() {
		return valueProperty;
	}

	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}

	@Override
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();

		try {
			Object beanValue = TagUtils.getInstance().lookup(pageContext, Constants.BEAN_KEY, property, null);
			for (Object obj : collection) {
				Object value = PropertyUtils.getProperty(obj, valueProperty);
				Object label = PropertyUtils.getProperty(obj, labelProperty);
				boolean check = (value.equals(beanValue));
				out.println("<input type=\"radio\" name=\""+property+"\" value=\""+value+"\" " +
						((check)?" checked=\"checked\" ":"")+"id=\"check"+value+"\" />");
				out.println("<label for=\"check"+value+"\">"+label+"</label>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SKIP_BODY;

	}

}
