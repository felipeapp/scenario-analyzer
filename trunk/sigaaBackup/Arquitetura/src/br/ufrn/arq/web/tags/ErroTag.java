/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Tag para a geração padronizada de mensagem de erro. <br>
 * Uso: <sipac:erro name="ATRIBUTO"/> <br>
 * <br>
 * 
 * @author Rafael Borja
 * @author Gleydson Lima
 *
 */
public class ErroTag extends TagSupport {

	private String name;
	
	private String width = "90%";
	
    /** 
     * Retorna atributo ou parametro, busando em parameter, page, request e session.
     * Caso hajam vários parameters, uma Collection com os parametros é retornada.
     * 
     * @param name
     * @return Collection com os parametros é retornada.
     */
    private Object buscaParametroAtributo(String name) {
    	//  Busca parametro em parameter, page, request e session. 
		Object parametro = pageContext.getRequest().getParameter(name);
		if (parametro == null) {
			parametro = pageContext.getRequest().getParameterValues(name);
			if (parametro != null && parametro instanceof String[]) {
				parametro = Arrays.asList((String[]) parametro);
			}
		}
		if (parametro == null)
			parametro = pageContext.getAttribute(name);
		if (parametro == null)
			parametro = pageContext.getRequest().getAttribute(name);
		if (parametro == null)
			parametro = pageContext.getSession().getAttribute(name);
		
		return parametro;
    }
    
	public int doStartTag() throws JspException {
		Object mensagem = buscaParametroAtributo(name);
		
		if (mensagem == null)
			return SKIP_BODY;
		else if (mensagem instanceof Iterable<?>) {
			Iterator<?> iterator = ((Iterable<?>) mensagem).iterator();
			if (!iterator.hasNext())
				return SKIP_BODY;
		}
		
		
		StringBuffer s = new StringBuffer(100);
		s.append("<table width=\"" + width + "\" align=\"center\" class=\"formulario\">");
	    s.append("<tr><td width=\"10%\" align=\"center\">");
	    s.append("<img src=\"../../images/error.gif\"></td>");
	    s.append("<td class=\"form_item\">");
	    s.append("<b> ERRO! </b><span style=\"color:red\">");
	    
	    // Escreve mensagens ou mensagem
	    if (mensagem instanceof Iterable<?>) {
			for (Object m : (Iterable<?>) mensagem) {
				s.append("<li><b>\n");
				s.append(String.valueOf(m));
				s.append("</b></li>\n");
			}
		}
	    else {
	    	s.append("<li><b>\n");
			s.append(String.valueOf(mensagem));
			s.append("</b></li>\n");
	    }
	    
	    s.append("</span>");
	    
	    s.append("</td>");
	    s.append("</tr>");
	    s.append("</table>");
	    
	    try {
			pageContext.getOut().append(s);
		} catch (IOException e) {
			throw new JspException("Erro ao escrever na página", e);
		}
	    
		return EVAL_BODY_INCLUDE;
	}

	
	@Override
	public void release() {
		name = null;
		width = "90%";
		super.release();
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
