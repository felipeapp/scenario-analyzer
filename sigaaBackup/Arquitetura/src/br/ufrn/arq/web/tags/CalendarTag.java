/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 23/06/2006
 */
package br.ufrn.arq.web.tags;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;
import org.apache.struts.taglib.html.Constants;

/**
 * Tag para geração de um campo de data com um calendário javascript associado.
 *
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
public class CalendarTag extends BaseHandlerTag {

    private String name = Constants.BEAN_KEY;

    private String property;

    private String titulo;

    /** Localização da descrição pop-up */
    private String top;

    /** Localização da descrição pop-up */
    private String left;

    private String onchange;

    private String onblur;

    private String onselected;

    @Override
	public int doStartTag() throws JspException {
        TagUtils tagUtils = TagUtils.getInstance();

        if (titulo == null || titulo.trim().equals("")) {
            titulo = "Selecione a data desejada: ";
        }

        if (left == null) {
            left = "0";
        }

        if (top == null) {
            top = "0";
        }

        if (onchange == null) {
            onchange = "";
        }

        // Buscar objeto
        String dataString = null;
        try {
            dataString = (String) tagUtils
            	.lookup(pageContext, name, property, null);
        } catch (Exception e) {}

        // Construir nome do campo
        StringBuffer fieldName = new StringBuffer();
        if (name != Constants.BEAN_KEY)
            fieldName.append(name + ((property != null) ? "." : ""));
        if (property != null)
            fieldName.append(property);

        String linkId = "link_" + fieldName;

        /* Campo de texto com a data */
        StringBuilder calendar = new StringBuilder();

        // Incluir bibliotecas necessárias
        String ctx = "/shared";

        calendar.append("<input type=\"text\" maxlength=\"10\" size=\"11\" ");
        calendar.append("name=\"" + fieldName + "\" ");
        calendar.append("id= \"" + fieldName + "\" ");
        if (dataString != null)
        	calendar.append("value= \"" + dataString + "\" ");
        if (getTabindex() != null)
        	calendar.append(" tabindex=\"" + getTabindex() + "\" ");
        if (onblur != null && !onblur.trim().equals("") ){
        	calendar.append(" onblur=\"" + onblur + "\" ");
        }
        if (onchange != null && !onchange.trim().equals("") ){
        	calendar.append(" onchange=\"" + onchange + "\" ");
        }
        if (onselected != null && !onselected.trim().equals("") ){
        	calendar.append(" onselected=\"" + onselected + "\" ");
        }

        calendar.append("onkeypress=\"return(formataData(this, event))\"> \n");

        /* Imagem para chamar o popup do calendario */
        calendar.append("<img id=\"" + linkId
                + "\" src=\"" + ctx
                + "/javascript/calendar/calendario.gif\" style=\"vertical-align:middle;margin:0px;cursor:pointer\"/>");

        /* Criar objeto do calendario */
        StringBuilder script = new StringBuilder();
        script.append("<script>\n");
        script.append("Calendar.setup({");
        script.append(" inputField  : '" + fieldName.toString() + "', "); // Campo com a data
        script.append(" button      : '" + linkId + "', "); // Botão ou imagem para abrir o popup
        script.append(" ifFormat    : '%d/%m/%Y', "); // Formato da data
        script.append(" weekNumbers : false, "); // Não mostrar números das semanas
        script.append(" showOthers  : true "); // Mostrar dias dos outros meses

        if (onselected != null && !onselected.trim().equals("")) {
        	 script.append(" , onSelect  : " + onselected  );
        }

        script.append("});");
        script.append("</script>");

        calendar.append(script);

        // Retornar
        tagUtils.write(pageContext, calendar.toString());
        release();
        return (SKIP_BODY);
    }

    @Override
	public void release() {
        super.release();
        name = Constants.BEAN_KEY;
        property = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    @Override
	public String getOnchange() {
        return onchange;
    }

    @Override
	public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

	@Override
	public String getOnblur() {
		return onblur;
	}

	@Override
	public void setOnblur(String onblur) {
		this.onblur = onblur;
	}

	public String getOnselected() {
		return onselected;
	}

	public void setOnselected(String onselected) {
		this.onselected = onselected;
	}


}
