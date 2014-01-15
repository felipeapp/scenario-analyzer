package br.ufrn.sigaa.arq.tags;

public class ImageTag extends org.apache.struts.taglib.html.ImageTag {

	private String dispatch;

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	@Override
	public void setProperty(String propriedade) {

		setOnclick("remover('" + dispatch + "', '" + propriedade + "','" + getValue() + "')");
		setStyle("border: none");
		if (getPage() == null)
			setPage("/img/delete.gif");

	}



}
