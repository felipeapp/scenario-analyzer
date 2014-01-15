/* 
 * Projeto: SIGAA
 * Autor: Andre M Dantas
 * Criação: 11/07/2006
 * MensagemTag: [descrição]
 *
 */

/**
 *
 */
package br.ufrn.sigaa.arq.tags;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.taglib.TagUtils;

/**
 * @author Andre M Dantas
 * 
 */
public class MensagemTag extends TagSupport {

	protected String	bundle	= null;
	protected String	locale	= Globals.LOCALE_KEY;

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		ActionMessages messages = null;
		try {
			messages = TagUtils.getInstance().getActionMessages(pageContext,
					Globals.MESSAGE_KEY);
		} catch (JspException e) {
			TagUtils.getInstance().saveException(pageContext, e);
			throw e;
		}
		Iterator iterator = messages.get();
		StringBuffer saida = new StringBuffer();
		if (!iterator.hasNext()) { 
			return SKIP_BODY; 
		} else {
			String tipoMsg = (String) pageContext.getRequest().getAttribute("TIPO_MENSAGEM");
			if (tipoMsg.equals("erro")) {
				saida.append("<div class=\"msgErro\">");
			} else if (tipoMsg.equals("aviso")) {
				saida.append("<div class=\"msgAviso\">");
			}
			saida.append("<ul style=\"vertical-align: middle;\">");
		}
		
		ActionMessage report = null;
		String msg = null;
		
		do {
			report = (ActionMessage) iterator.next();
			saida.append("<il>");
			msg = TagUtils.getInstance().message(pageContext, bundle, locale, report.getKey(), report.getValues());
			saida.append(msg);
			saida.append("</il><br>");
		} while (iterator.hasNext());
		
		saida.append("</ul>");
		Object excecao = pageContext.getRequest().getAttribute("EXCECAO");
		if ((excecao != null) && (excecao instanceof Exception)) {
			Exception ex = (Exception) excecao;
			saida.append("<div class=\"msgExcecao\">"+ex+"</div>");
		}
		saida.append("</div>");
		
		if (messages.size() > 0) {
			saida.append("<br>");
		}

		try {
            pageContext.getOut().println(saida);
        } 
        catch (IOException e) {
            throw new JspException(e);
        }
		return EVAL_PAGE;
	}

}
