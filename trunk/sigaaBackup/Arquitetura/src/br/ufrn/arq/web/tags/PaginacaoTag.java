/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.util.RequestUtils;

/**
 * Custom tag para geração de links para paginação
 * 
 * @author Rafael Borja
 * @author Gleydson Lima
 */
public class PaginacaoTag extends TagSupport {

    /*
     * 		PARAMETROS DA TAG
     */
    /** Nome da action responsável pela listagem (nome no mapeamento) */
    protected String action = null;
    
    /** Denominação do iten buscado (para visualização do usuario) */
    private String denominacao;
    
    /** Genero do item encontrado (para visualização do usuario)<br>
     *  masculino/feminio
     */
    private String genero;
    
    /** The request method used when submitting this form. */
    protected String method = null;
    

    /*
     *	ATRIBUTOS DA PAGINAÇÃO 
     */
    /** Página atual */
    private Integer pageNum;
    
    /** Quantidade de páginas encontradas */
    private Integer pageMax;
    
    /** Quantidade de itens encontrados */
    private Integer qtdItens;
    
    /** Tamanho máximo da página */
    private int pageSize;
    
    /*
     * 	VARIÁVEIS DE INSTÂNCIA
     */
    /** The module configuration for our module. */
    private ModuleConfig moduleConfig = null;
    
    /** Separador de linha */
    protected static String lineEnd = System.getProperty("line.separator");

    /** The ActionMapping defining where we will be submitting this form */
    protected ActionMapping mapping = null;

    /**
     * The ActionServlet instance we are associated with (so that we can
     * initialize the <code>servlet</code> property on any form bean that
     * we create).
     */
    protected ActionServlet servlet = null;

    /**
     * The name of the form bean to (create and) use. This is either the same
     * as the 'name' attribute, if that was specified, or is obtained from the
     * associated <code>ActionMapping</code> otherwise.
     */
    protected String beanName = null;

    /**
     * The scope of the form bean to (create and) use. This is either the same
     * as the 'scope' attribute, if that was specified, or is obtained from the
     * associated <code>ActionMapping</code> otherwise.
     */
    protected String beanScope = null;

    /**
     * JavaScript para paginação
     */
    private static final String script =
        "<script type=\"text/javaScript\"> "
        + "function paginacao(pagina) { "
        	+ "var pageNum = $('pageNum').value = pagina;\n"
        	+ "$('submitPaginacao').click();\n"
        	+ "} "
        + "function valida(campo) {" 
        	+ "try {"
        	+ "var pagina = parseInt(campo.value);\n"
        	+ "if (pagina <= 0 || pagina == undefined)\n"
        		+ "throw new Error(\"\");\n"
        	+ "else if (pagina > $F('pageMax'))\n"
        		+ "campo.value = $F('pageMax');\n"
        	+ "}"
        	+ "catch (erro) {\n"
        		+ "campo.value = \"1\";\n"
        	+ "}\n"
        	+ "$('pageNum').value = campo.value;\n"
        + "}\n" +
        "</script>";
    
    
    public String getDenominacao() {
        return denominacao;
    }

    public void setDenominacao(String denominacao) {
        this.denominacao = denominacao;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getPageMax() {
        return pageMax;
    }

    public void setPageMax(Integer pageMax) {
        this.pageMax = pageMax;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getQtdItens() {
        return qtdItens;
    }

    public void setQtdItens(Integer qtdItens) {
        this.qtdItens = qtdItens;
    }
    
    /**
     * Return the name of the form bean corresponding to this tag. There is
     * no corresponding setter method; this method exists so that the nested
     * tag classes can obtain the actual bean name derived from other
     * attributes of the tag.
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * Return the action URL to which this form should be submitted.
     */
    public String getAction() {
        return (this.action);
    }

    /**
     * Set the action URL to which this form should be submitted.
     *
     * @param action The new action URL
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Return the request method used when submitting this form.
     */
    public String getMethod() {
        return (this.method);
    }

    /**
     * Set the request method used when submitting this form.
     *
     * @param method The new request method
     */
    public void setMethod(String method) {
        this.method = method;
    }
    
    public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
    

    // --------------------------------------------------------- Public Methods

	/**
     * Render the beginning of this form.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Se a paginação não estiver setada, ignora tag
        TagUtils tagUtils = TagUtils.getInstance();
        if (tagUtils.lookup(pageContext, "paginable", null) == null)
            return (SKIP_BODY);
            
        
        // Busca nome do form bean name, scopo, e atributos de paginação
        this.lookup();

        StringBuffer results = new StringBuffer();

        // Cria a tag de abertura do form de busca
        results.append(this.renderFormStartElement());

        // Gera links da paginação
        if (pageMax.intValue() > 1)
            results.append(this.renderLinksPaginacao());
        
        results.append(this.renderSumario());
        
        tagUtils.write(pageContext, results.toString());

        // Store this tag itself as a page attribute
        pageContext.setAttribute(Constants.FORM_KEY, this,
                PageContext.REQUEST_SCOPE);

        // Localiza ou cria o bean associado ao form
        this.initFormBean();
        
        return (EVAL_BODY_INCLUDE);
    }

    
    /**
     * Gera sumário da paginação
     * 
     * @return
     */
    protected String renderSumario() {
        StringBuffer saida = new StringBuffer(100);
        
        denominacao = (denominacao == null)? "itens": denominacao;
        if ((genero == null) || (!genero.toUpperCase().startsWith("F")))
            genero = " encontrados";
        else
            genero = " encontradas";
        
        saida.append("<center> Página " + pageNum + " de " + pageMax + "<br>");
        saida.append("Total de " +  denominacao + genero + ": " + qtdItens);
        saida.append("</center>");
        
        return saida.toString();
    }
    
    /**
     * Gera os a tabela com os botões da paginação
     * @return
     * @throws JspException
     */
    public String renderLinksPaginacao() throws JspException {
        StringBuffer results = new StringBuffer();
        
        String imgPrim = (pageNum.intValue() > 1)? "primeira": "primeira_des";
        String imgAnte = (pageNum.intValue() > 1)? "voltar": "voltar_des";
        
        String imgUltm = (pageNum.intValue() < pageMax.intValue())? "ultima": "ultima_des";
        String imgProx = (pageNum.intValue() < pageMax.intValue())? "avancar": "avancar_des";
        
        results.append("<table width=\"80\" align=\"center\">");
        results.append(lineEnd);
        results.append("<tr valign=\"middle\">");
        results.append(lineEnd);
        
        results.append("<input type=\"submit\" value=\"submit\" id=\"submitPaginacao\" style=\"display: none\">");
        results.append(lineEnd);
        results.append("<input type=\"hidden\" name=\"pageNum\" id=\"pageNum\">");
        results.append(lineEnd);
        results.append("<input type=\"hidden\" id=\"pageMax\" value=\"" + pageMax + "\">");
        results.append(lineEnd);
        if (pageSize > 0) {
        	results.append("<input type=\"hidden\" id=\"pageSize\" value=\"" + pageSize + "\">");
        	results.append(lineEnd);
        }
        
        results.append("<td align=\"left\">");
        results.append(" <img src=\"img_css/" + imgPrim + ".gif\" border=\"0\"");
        if (pageNum.intValue() > 1)
            results.append("style=\"cursor: pointer;\" onclick=\"javascript: paginacao(1);\"");
        results.append("></td>");
        results.append(lineEnd);
        
        results.append("<td align=\"right\">");
        results.append("  <img src=\"img_css/" + imgAnte + ".gif\" border=\"0\"");
        if (pageNum.intValue() > 1)
            results.append("style=\"cursor: pointer;\" onclick=\"javascript: paginacao(" + (pageNum.intValue()-1) +");\"");
        results.append("></td>");
        results.append(lineEnd);
        
        results.append("<td align=\"center\">");
        results.append(lineEnd);
        results.append(script);
        results.append("  <input name=\"pagina\" id=\"pagina\" type=\"text\" size=\"3\" onkeypress=\"valida(this);\">");
        results.append(lineEnd);
        results.append("</td>");
        results.append(lineEnd);
        
        results.append("<td align=\"left\">");
        results.append("  <img src=\"img_css/" + imgProx + ".gif\" border=\"0\"");
        if (pageMax.intValue() != pageNum.intValue())
            results.append("style=\"cursor: pointer;\" onclick=\"javascript: paginacao(" + (pageNum.intValue()+1) +");\"");
        results.append("></td>");
        results.append(lineEnd);
        
        results.append("<td align=\"left\">");
        results.append("  <img src=\"img_css/" + imgUltm + ".gif\" border=\"0\"");
        if (pageMax.intValue() != pageNum.intValue())
            results.append("style=\"cursor: pointer;\" onclick=\"javascript: paginacao(" + pageMax.intValue() +");\"");
        results.append("></td>");
        results.append(lineEnd);
        
        results.append("</tr></table>");
        results.append(lineEnd);

        return results.toString();
    }
    
    
    /**
     * Locate or create the bean associated with our form.
     * @throws JspException
     * @since Struts 1.1
     */
    protected void initFormBean() throws JspException {
        int scope = PageContext.SESSION_SCOPE;
        if ("request".equalsIgnoreCase(beanScope)) {
            scope = PageContext.REQUEST_SCOPE;
        }

        Object bean = pageContext.getAttribute(beanName, scope);
        if (bean == null) {
            // New and improved - use the values from the action mapping
            bean = RequestUtils.createActionForm(
                    (HttpServletRequest) pageContext.getRequest(), mapping,
                    moduleConfig, servlet);
            if (bean instanceof ActionForm) {
                ((ActionForm) bean).reset(mapping,
                        (HttpServletRequest) pageContext.getRequest());
            }
            
            pageContext.setAttribute(beanName, bean, scope);
        }
        pageContext.setAttribute(Constants.BEAN_KEY, bean,
                PageContext.REQUEST_SCOPE);
    }

    /**
     * Generates the opening <code>&lt;form&gt;</code> element with appropriate
     * attributes.
     * @since Struts 1.1
     */
    protected String renderFormStartElement() {

        StringBuffer results = new StringBuffer("<form");

        // render attributes
        results.append(" name=\"");
        results.append(beanName);
        results.append("\"");
        
        renderAttribute(results, "method", getMethod() == null ? "post"
                : getMethod());
        renderAction(results);

        results.append(">");
        return results.toString();
    }

    /**
     * Renders the action attribute
     */
    protected void renderAction(StringBuffer results) {

        HttpServletResponse response = (HttpServletResponse) this.pageContext
                .getResponse();

        results.append(" action=\"");
        results.append(response.encodeURL(TagUtils.getInstance()
                .getActionMappingURL(this.action, this.pageContext)));

        results.append("\"");
    }

    /**
     * Renders attribute="value" if not null
     */
    protected void renderAttribute(StringBuffer results, String attribute,
            String value) {
        if (value != null) {
            results.append(" ");
            results.append(attribute);
            results.append("=\"");
            results.append(value);
            results.append("\"");
        }
    }

    /**
     * Render the end of this form.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doEndTag() throws JspException {

        // Se a paginação não estiver setada, ignora tag
        TagUtils tU = TagUtils.getInstance();
        if (tU.lookup(pageContext, "paginable", null) == null)
            return (EVAL_PAGE);
        
        
        // Remove the page scope attributes we created
        pageContext.removeAttribute(Constants.BEAN_KEY,
                PageContext.REQUEST_SCOPE);
        pageContext.removeAttribute(Constants.FORM_KEY,
                PageContext.REQUEST_SCOPE);

        // Render a tag representing the end of our current form
        StringBuffer results = new StringBuffer("</form>");

        // Print this value to our output writer
        JspWriter writer = pageContext.getOut();
        try {
            writer.print(results.toString());
        } catch (IOException e) {
            throw new JspException("Erro de IO em sipac:paginacao:\n"
                    + e.getMessage());
        }

        // Continue processing this page
        return (EVAL_PAGE);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Look up values for the <code>name</code>, <code>scope</code>, and
     * <code>type</code> properties if necessary.
     *
     * @exception JspException if a required value cannot be looked up
     */
    protected void lookup() throws JspException {
        TagUtils tagUtils = TagUtils.getInstance(); 
        
        // Look up the module configuration information we need
        moduleConfig = tagUtils.getModuleConfig(pageContext);
        
        if (moduleConfig == null) {
            JspException e = new JspException("Erro em sipac:paginacao");
            pageContext.setAttribute(Globals.EXCEPTION_KEY, e, PageContext.REQUEST_SCOPE);
            throw e;
        }
        servlet = (ActionServlet) pageContext.getServletContext().getAttribute(
                Globals.ACTION_SERVLET_KEY);

        // Look up the action mapping we will be submitting to
        String mappingName = tagUtils.getActionMappingName(action);
        mapping = (ActionMapping) moduleConfig.findActionConfig(mappingName);
        if (mapping == null) {
            JspException e = new JspException(
                    "sipac:pagincao: Erro ao obter mapping para" + mappingName);
            pageContext.setAttribute(Globals.EXCEPTION_KEY, e,
                    PageContext.REQUEST_SCOPE);
            throw e;
        }

        // Look up the form bean definition
        FormBeanConfig formBeanConfig = moduleConfig.findFormBeanConfig(mapping.getName());
        if (formBeanConfig == null) {
            JspException e = new JspException("sipac:paginacao: bean não encontrado");
            pageContext.setAttribute(Globals.EXCEPTION_KEY, e,
                    PageContext.REQUEST_SCOPE);
            throw e;
        }

        // Calculate the required values
        beanName = mapping.getAttribute();
        beanScope = mapping.getScope();
        
        
        // Valores da paginação
        pageNum = (Integer) tagUtils.lookup(pageContext, beanName, "pageNum", null);
        pageMax = (Integer) pageContext.getRequest().getAttribute("fim");
        qtdItens = (Integer) pageContext.getRequest().getAttribute("total");
    }
    
    /**
     * Libera recursos da tag
     */ 
    public void release() {
        super.release();
        action = null;
        moduleConfig = null;
        mapping = null;
        method = null;
        servlet = null;

        this.genero = null;
        this.genero = null;
        this.pageMax = null;
        this.pageNum = null;
        this.qtdItens = null;
        this.pageSize = 0;
    }
}