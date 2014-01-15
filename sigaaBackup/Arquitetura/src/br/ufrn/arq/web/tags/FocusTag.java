/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Tag para a geração de script para focus. <br>
 * Script gerado chama a função com um delay de 50 ms para evitar que um
 * outro script remova o focus do input selecionado.
 * <br>
 * 
 * @author Rafael Borja 
 * @author Gleydson Lima
 *
 */
public class FocusTag extends TagSupport {

	private String name;
	
	private String id;
	
	private String form = "0";
	
    private boolean isEmpty (String arg) {
    	return (((arg == null) || (arg.trim().length() <= 0) || (arg.trim()
				.equals("null"))));
    }
                              
	public int doStartTag() throws JspException {
		if (isEmpty(id) && isEmpty(name))
			return SKIP_BODY;
		
        StringBuffer s = new StringBuffer();
        s.append("<script type=\"text/javascript\" language=\"JavaScript\">");
        s.append("  <!--\n");
        
        StringBuffer focusControl = new StringBuffer(50);
        if (!isEmpty(name)) {
        	focusControl.append("document.forms[\"");
        	focusControl.append(form);
        	focusControl.append("\"].elements[\"");
        	focusControl.append(name);
        	focusControl.append("\"]");
        }
        else if (!isEmpty(id)) {
        	focusControl.append("document.getElementById(\"");
        	focusControl.append(id);
        	focusControl.append("\");");
        }
        else
        	throw new JspException("Um dos atributos, id ou name deve ser informado");

        s.append(" try {\n");
        s.append("  var focusControl = ");
        s.append(focusControl.toString());
        s.append(";\n");
        s.append("  if (focusControl != null && focusControl.type != \"hidden\" && !focusControl.disabled) {\n");
        s.append(" focusControl.focus(); \n");
        s.append(" setTimeout(\"focusControl.focus();\", 50); \n }");
        s.append("\n }");
        s.append(" catch ( e ) { e = null;  }\n");
        s.append("  // -->");
        s.append("</script>");
	    
        try {
			pageContext.getOut().append(s);
		} catch (IOException e) {
			throw new JspException("Erro ao escrever na página", e);
		}

		return SKIP_BODY;
	}

	
	@Override
	public void release() {
		name = null;
		id = null;
		form = "0";
		
		super.release();
	}


	public String getForm() {
		return form;
	}


	public void setForm(String form) {
		this.form = form;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}
