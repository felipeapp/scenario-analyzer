/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/04/2010
 */
package br.ufrn.arq.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Tag com temporizador ajax para chamar a servlet
 * KeepAliveServlet e manter a sessão do usuário ativa.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class KeepAliveTag extends TagSupport {

	private int tempo = 10;
	
	@Override
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		
		try { 
			out.println("<script type=\"text/javascript\">");
			out.println("new Ajax.PeriodicalUpdater('tempoSessao', '" + req.getContextPath() + "/keepAlive', { method: 'get', frequency: " + (tempo * 60) + " })");
			out.println("</script>");
		} catch(IOException e) {
			throw new JspException(e);
		}
		
		return super.doStartTag();
	}

	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}
	
}
