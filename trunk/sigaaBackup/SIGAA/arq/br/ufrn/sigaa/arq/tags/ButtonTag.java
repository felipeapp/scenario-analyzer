package br.ufrn.sigaa.arq.tags;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

/**
 * Heran�a de tag Button e adicionar funcionalidades
 * de navega��o e chamada de m�todos para auxiliar
 * o UFRN Form
 *
 * @author Gleydson
 *
 */
public class ButtonTag extends org.apache.struts.taglib.html.ButtonTag {

	private String dispatch;

	private String view;
	
	private boolean cancelar;

	@Override
	public String getOnclick() {
		StringBuilder onclick = new StringBuilder();
		
		if (cancelar) {
			onclick.append("if (confirm('Deseja cancelar a opera��o? Todos os dados digitados ser�o perdidos!')) { ");
		}
		
		if (!isEmpty(super.getOnclick())) onclick.append(super.getOnclick());
		if (!isEmpty(dispatch)) onclick.append(" submitMethod('" + dispatch + "', this); ");
		if (!isEmpty(view)) onclick.append(" navegateTo('" + view + "',this); ");
		
		if (cancelar) {
			onclick.append(" } ");
		}
		
		return onclick.toString();
	}
	
	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public boolean isCancelar() {
		return cancelar;
	}

	public void setCancelar(boolean cancelar) {
		this.cancelar = cancelar;
	}

}
