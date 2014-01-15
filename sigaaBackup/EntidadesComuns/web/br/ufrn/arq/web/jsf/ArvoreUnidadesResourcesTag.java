/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.arq.web.jsf;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Tag para renderizar scripts necessários para o funcionamento da árvore de unidades.
 * @author David Pereira
 *
 */
public class ArvoreUnidadesResourcesTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		
		String scripts = "<script type=\"text/javascript\" src=\"/shared/javascript/jquery/jquery-1.4.4.min.js\"></script>\n"
			+ "<!--[if IE]>\n" 
			+ "<script type=\"text/javascript\" src=\"/shared/javascript/jquery/jquery.bgiframe.min.js\"></script>\n"
			+ "<![endif]-->\n"
			+ "<script type=\"text/javascript\">\n"
		 	+ "jQuery.noConflict();\n"
		  	+ "JAWR.loader.style('/javascript/ext-1.1/resources/css/tree.css');\n"
		 	+ "JAWR.loader.style('/javascript/jquery/jquery.autocomplete.css');\n"
		 	+ "JAWR.loader.script('/javascript/ext-1.1/build/tree/ext.js');\n"
		 	+ "JAWR.loader.script('/javascript/ext-1.1/ext-all.js');\n"
		 	+ "JAWR.loader.script('/javascript/arvore-unidades.js');\n"
			+ "JAWR.loader.script('/javascript/jquery/jquery.autocomplete.js');\n"
		 	+ "$(document.body).addClassName('background');\n"
		 	+ "_SARISSA_XMLHTTP_PROGID = Sarissa.pickRecentProgID([\"MSXML2.XMLHTTP\", \"Microsoft.XMLHTTP\"]);\n"
		 	+ "</script>\n";
		
		String styles = "<style type=\"text/css\">\n"
			+ "ul.x-tree-node-ct { padding-left: 0 }\n"
			+ "</style>\n";
		
		try {
            pageContext.getOut().println(scripts + "\n" + styles);
        } catch (IOException e) {
            throw new JspException(e);
        }

		return super.doStartTag();
	}
	
}
