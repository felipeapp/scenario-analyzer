package br.ufrn.sigaa.arq.tags;

public class FormTag extends org.apache.struts.taglib.html.FormTag {

	@Override
	protected String renderFormStartElement() {
		String pai = super.renderFormStartElement();

		if (getAction() != null && getAction().indexOf("dispatch=") == -1) {
			String dispatch = "<input type=\"hidden\" name=\"dispatch\"  id=\"dispatch\">";
			String view = "<input type=\"hidden\" name=\"view\" value=\"adicionarHorario\" id=\"view\">";
			return pai + dispatch + view;
		} else {
			return pai;
		}

	}

}