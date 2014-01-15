/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 26/02/2005
 */
package br.ufrn.arq.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Tag para a geração de decriçao pop-up. <br>
 * Uso: <sipac:help imagem="URLIMG" link="URLLINK"> DESCRIÇÃO </sipac:help> <br>
 * <br>
 *
 * DESCRIÇÃO: qualquer conteúdo válido para uma JSP. <br>
 * img (opcional): url da imagem para ativação do pop-up (padrão {@link #IMG_PADRAO}). <br>
 * link (opcional): Link da imagem de ativação <br>
 *
 * @author Rafael Borja
 * @author Gleydson Lima
 *
 */
public class HelpTag extends TagSupport {
    /** Imagem para ativar o menu pop-up */
    private String img;

    /** Link para da imagem */
    private String link;

    /** Action para link */
    private String action;

    /** Parametros para a action */
    private String param;

    /** Atributo para target para link e action */
    private String target;

    /** Ação javascript onClick */
    private String onclick;

    /** Ancora de text do link */
    private String ancora;

    /** Tamanho em pixeis do menu pop-up */
    private int width;

    /**
     *  Indica que a descrição pop-up deve continuar sendo exibida
     * mesmo quando o mouse está sobre a descrição.
     *  Útil quando a descrição possui um link.
     *  Padrão false.
     */
    private boolean over;

    /** Localização da descrição pop-up */
    private String top;

    /** Localização da descrição pop-up */
    private String left;

    /** Definição do id do elemento ao qual a pop-up será associada */
    private String forId;

    /** Atributo utilizado internamente para se ter visibilidade da função fora dos métodos */
    private String functionForId;

    /**
     * Atributo que define se a img deve ou não ser exibida.
     * Caso valor seja nulo ou true, a img será exibida.
     * Padrão null;
     */
    private Boolean showImage;


    /** Imagem padrão */
    private static final String IMG_PADRAO = "/shared/img/geral/ajuda.gif";

    public String getImg() {
        return img;
    }

    public void setImg(String imagem) {
        this.img = imagem;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public String getOnclick() {
        return onclick;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int doStartTag() throws JspException {
        if (img==null)
            img = IMG_PADRAO;
        else {
        	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        	img = request.getContextPath() + img;
        }

        String id = "ajuda" + String.valueOf((long)(Math.random()*1000000));

        String functionMouseOut = "document.getElementById('"+id+"').style.visibility = 'hidden';";
        String functionMouseOver = "document.getElementById('"+id+"').style.visibility = 'visible';";

        String mouseOver = " onMouseOver=\""+ functionMouseOver +"\"";
        String mouseOut = " onMouseOut=\""+ functionMouseOut +"\"";
        String click = "";
        if (onclick != null && !onclick.trim().equals(""))
            click = " onclick=\"" + onclick + "\" ";

        // Estilos
        StringBuffer style = new StringBuffer();
        if (width != 0)
        	style.append("width: " + width + "px; ");

    	if (left != null)
    		style.append("left: " + left + "; ");
    	else if (over == true)
    		style.append("left: 15; ");

    	if (top != null)
    		style.append("top: " + top + "; ");

    	if (style.length() != 0) {
    		style.insert(0, "style=\"");
    		style.append("\"");
    	}

        StringBuffer saida = new StringBuffer(80);


		if (action != null) {
			if (link != null)
				throw new IllegalArgumentException(
						"atributo action e link setados em help tag!");

			link = action + ".do";
		}

		if (param != null)
			link += "?" + param;

        if (link != null) {
        	target = (target != null) ? (target + " target=\"" + target + "\"")
					: "";
            saida.append("<a href=\"" + link + "\"" + click + target + ">");
        }
        else if (!click.equals(""))
        	saida.append("<a href=\"#" + ((ancora != null) ? ancora : "")
					+ "\" " + click + ">");

        if(showImage == null || showImage){
        	saida.append("\t<img src=\"" + img + "\"" + mouseOver + mouseOut + " border=\"0\">\n");
        }
        if (link != null || !click.equals(""))
            saida.append("</a>");

        saida.append("<span style=\"position: absolute;\">\n");

        String div = "\t<div class=\"popUp\"" + style + mouseOut
                + ((over == true) ? mouseOver : "")
                + "id=\"" + id + "\">\n\t\t";

        functionForId = "<script> var elHelp = document.getElementById('"+ forId +"');" +
    		" elHelp.onmouseover = new Function(\"" + functionMouseOver + "\"); " +
    		" elHelp.onmouseout = new Function(\"" + functionMouseOut + "\");  </script>";

        saida.append(div);

        try {
            pageContext.getOut().println(saida);
        }
        catch (IOException e) {
            throw new JspException(e);
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        String saida = "\t</div>\n" + "</span>" + ((forId != null) ? functionForId : "");
        try {
            pageContext.getOut().println(saida);
        }
        catch (IOException e) {
            throw new JspException(e);
        }

        release();

        return EVAL_PAGE;
    }

    /**
     * Libera recursos adiquiridos pela tag e seta
     * os atributos novamente para null.
     */
    public void release() {
        super.release();

        this.img = null;
        this.link = null;
        this.onclick = null;
        this.ancora = null;
        this.over = false;
        this.width = 0;
        this.action = null;
        this.target = null;
        this.forId = null;
        this.functionForId = null;
        this.showImage = null;
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

	public String getAncora() {
		return ancora;
	}

	public void setAncora(String ancora) {
		this.ancora = ancora;
	}

	public String getForId() {
		return forId;
	}

	public void setForId(String forId) {
		this.forId = forId;
	}

	public Boolean getShowImage() {
		return showImage;
	}

	public void setShowImage(Boolean showImage) {
		this.showImage = showImage;
	}
}
