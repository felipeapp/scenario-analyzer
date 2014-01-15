package br.ufrn.sigaa.arq.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.web.struts.AbstractForm;
import br.ufrn.sigaa.dominio.Usuario;

public class LogonForm extends AbstractForm {

	private Usuario user = new Usuario();

	private int width;

	private int height;

	private boolean passaporte;

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		return null;
	}

	public boolean isPassaporte() {
		return passaporte;
	}

	public void setPassaporte(boolean passaporte) {
		this.passaporte = passaporte;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}



}
