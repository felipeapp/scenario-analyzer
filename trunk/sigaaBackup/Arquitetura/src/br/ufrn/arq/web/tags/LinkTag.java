/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 13/10/2004
 */
package br.ufrn.arq.web.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;

import br.ufrn.comum.dominio.UsuarioGeral;

/**
 *
 * Tag Library para inserir link para actions sem form na view
 *
 * @author Gleydson Lima
 *
 */
public class LinkTag extends BodyTagSupport {

	private String action;

	private String value;

	private String param;

	private String target;

	private int[] roles;

	private boolean disabled;

	private boolean linkDesabilitado;

	private String style;

	private String aba;

	public String getAba() {
		return aba;
	}

	public void setAba(String aba) {
		this.aba = aba;
	}

	public LinkTag() {
		super();
	}

	/**
	 * @return Retorna target.
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            The target to set.
	 */
	public void setTarget(String target) {
		this.target = target;
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

	/**
	 * @param disabled
	 *            The disabled to set.
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
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

	/**
	 * @return Retorna roles.
	 */
	public int[] getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            The roles to set.
	 */
	public void setRoles(int[] roles) {
		this.roles = roles;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int doStartTag() throws JspException {

		StringBuffer link = new StringBuffer();
		StringBuffer tag = new StringBuffer();

		boolean allowed = false;

		if (roles != null) {
			UsuarioGeral usuario = (UsuarioGeral) pageContext.getSession()
					.getAttribute("usuario");

			int i = 0;

			while (i < roles.length && !allowed) {

				try {
					allowed = usuario.isUserInRole(roles[i]);
					i++;
				} catch (Exception e) {
					throw new JspException(e);
				}
			}
		}

		if ((roles == null || allowed) && !disabled) {
			HttpServletRequest req = (HttpServletRequest) pageContext
					.getRequest();
			link.append(req.getContextPath()).append("/" + action);

			if (!action.endsWith(".jsf") && !action.endsWith("*.jsp")) {
				link.append(".do");
			}

			if (param != null && !param.trim().equals("")) {
				link.append("?").append(param);
				if ( aba != null ) {
					link.append("&aba=" + aba);
				}
			} else {
				if ( aba != null ) {
					link.append("?aba=" + aba);
				}
			}

			tag.append("<a href=\"" + link.toString() + "\"");

			if (style != null && !style.trim().equals(""))
				tag.append(" style=\"" + style + "\" ");

			if (target != null && !target.trim().equals("")) {
				tag.append(" target=\"").append(target).append("\"");
			}

			tag.append(">");
			if (value != null)
				tag.append(value);

		} else {
			tag.append("<a href=\"#\">");
			tag
					.append("<span class=\"tooltip\" title=\"Você não possui permissão para acessar esta opção\" >");
			if (value != null)
				tag.append(value);
			linkDesabilitado = true;
		}

		TagUtils.getInstance().write(pageContext, tag.toString());

		return EVAL_BODY_INCLUDE;

	}

	@Override
	public void release() {
		action = null;
		disabled = false;
		linkDesabilitado = false;
		param = null;
		roles = null;
		target = null;
		value = null;
		style = null;

		super.release();
	}

	@Override
	public int doEndTag() throws JspException {
		StringBuilder tag = new StringBuilder();
		if (linkDesabilitado)
			tag.append("</span>");
		tag.append("</a>");

		TagUtils.getInstance().write(pageContext, tag.toString());

		release();

		return super.doEndTag();
	}
}