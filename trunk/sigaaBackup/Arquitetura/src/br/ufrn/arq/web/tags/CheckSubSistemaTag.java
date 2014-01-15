/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 07/08/2008
 */
package br.ufrn.arq.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.comum.dominio.SubSistema;

/**
 * Esta é usada para renderizar o seu corpo somente se o usuário tiver no
 * subsistema indicado.
 * 
 * @author Gleydson Lima
 * 
 */
public class CheckSubSistemaTag extends TagSupport {

	private int subsistema;

	@Override
	public int doStartTag() throws JspException {

		// verifica se o usuário está logado
		if (pageContext.getSession().getAttribute("usuario") == null)
			return SKIP_BODY;

		Object subsistemaObj = pageContext.getSession().getAttribute(
				"subsistema");

		// verifica se está no subsistema
		boolean estaNoSubSistema = false;

		// no caso do SIPAC o sistema está um inteiro
		if (subsistemaObj instanceof Integer ) {
			Integer subInteger = (Integer) subsistemaObj;
			if (subInteger.intValue() == subsistema) {
				estaNoSubSistema = true;
			}
		}
		
		if (subsistemaObj instanceof String ) {
			Integer subInteger = Integer.parseInt( (String)subsistemaObj);
			if (subInteger.intValue() == subsistema) {
				estaNoSubSistema = true;
			}
		}

		// no caso do SIGAA e SIGRH.
		if (subsistemaObj instanceof SubSistema) {
			SubSistema sub = (SubSistema) pageContext.getSession()
					.getAttribute("subsistema");
			if (sub != null && sub.getId() == subsistema) {
				estaNoSubSistema = true;
			}
		}

		if (estaNoSubSistema) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}

	}

	@Override
	public void release() {
		subsistema = 0;
		super.release();
	}

	public int getSubsistema() {
		return subsistema;
	}

	public void setSubsistema(int subsistema) {
		this.subsistema = subsistema;
	}

}