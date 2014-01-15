/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.arq.util.Step;
import br.ufrn.comum.dominio.SubSistema;

/**
 * Tag para exibir informações do passo atual em
 * um caso de uso que é realizado em diversos passos.
 * 
 * @author Gleydson Lima
 *
 */
public class StepTag extends TagSupport {

	@Override
	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {

		try {
			SubSistema sub = (SubSistema) pageContext.getSession()
					.getAttribute("subsistema");
			pageContext.getOut().print(
					"<A href=\"" + sub.getLink() + "\">" + sub.getNome()
							+ "</a>");

			ArrayList<Step> steps = (ArrayList<Step>) pageContext.getSession().getAttribute("steps");

			Integer atual = (Integer) pageContext.getSession().getAttribute("step");
			if ( atual == null ) {
				atual = 1;
			}

			String context = ((HttpServletRequest)pageContext.getRequest()).getContextPath();

			if (steps != null) {
				for ( int a = 0; a < atual ; a++ ) {
					Step step = steps.get(a);
					pageContext.getOut().print(
							" > " + "<A href=\"" + context + step.getView() + "\">"
									+ step.getNome() + "</a>");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.doStartTag();
	}

}
