/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 13/10/2004
 */
package br.ufrn.arq.web.tags;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 
 * Tag Library para inserir botão para actions sem form na view
 * 
 * @author Gleydson Lima
 *  
 */
public class BuscaCodigoTag extends TagSupport {

    /** Name do Bean * */
    private String name;

    /** Propriedade do Bean que servirá como argumento da Busca * */
    private String queryProperty;

    /** Nome da função JavaScript a ser gerada * */
    private String jsFunction;

    private String beanDesc;
    
    /** Não á msgPane. Caso codigo não for encontrado, seleciona primeira opção da lista */
    private boolean noMsgPane;

    public BuscaCodigoTag() {
        super();
    }

    private String formatMethod(String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public int doStartTag() throws JspException {

        try {

            /**
             * <script language="JavaScript">
             * 
             * var arr = new Array(); arr[0] = 100; arr[1] = 200;
             * 
             * 
             * function buscaCodigo(combo, codigo, nextFocus) { var x = 0; var
             * achou = false; for (x=0; x < combo.length; x++) { if ( arr[x] ==
             * codigo.value ) { achou = true; combo.selectedIndex = x;
             * nextFocus.focus(); } } if ( !achou ) { alert('Alguma coisa não
             * encontrada'); codigo.focus(); } }
             * 
             * </script>
             *  
             */

            StringBuffer js = new StringBuffer();

            js.append("<SCRIPT LANGUAGE=\"JAVASCRIPT\">");

            Object obj = pageContext.findAttribute(name);

            if (!(obj instanceof Collection<?>)) {
                throw new JspException("O atributo name não é uma coleção");
            }

            js.append("function " + jsFunction + "(combo, codigo, msgPane) { ");

            if (!noMsgPane)
            	js.append("if (codigo.value != 0){");
            
            
            js.append("var arr = new Array();");

            Collection<?> col = (Collection<?>) obj;
            Iterator<?> it = col.iterator();
            int count = 0;

            if (noMsgPane)
            	js.append("arr[" + count++ + "] = " + 0 + ";");
            
            while (it.hasNext()) {

                Object atual = it.next();

                StringTokenizer st = new StringTokenizer(queryProperty, ".");
                String propertyInsideToken;

                while (st.hasMoreTokens()) {
                    propertyInsideToken = st.nextToken();
                    Class<?> c = atual.getClass();
                    atual = c.getMethod(formatMethod(propertyInsideToken),
							(Class[]) null).invoke(atual, (Object[]) null);
                    if (atual == null) {
                        atual = new Integer(0);
                        //return 0;
                    }

                    c = atual.getClass();

                }

                //invoke method
                js.append("arr[" + count++ + "] = " + String.valueOf(atual)
                        + ";");
            }

            js.append("var x = 0;");
            js.append("var achou = false;");

            js.append("for (x=0; x < combo.length; x++) {");
            js.append("if ( arr[x] == codigo.value ) { ");
            js.append("achou = true;");
            js.append("combo.selectedIndex = x;");
            
            js.append("if (msgPane != null){");
            js.append("msgPane.value =  combo[x].label;");
            js.append("}");
            js.append("}");
            js.append("}");

            js.append("if ( !achou ) { ");
            if (!noMsgPane) {
            	js.append("if (msgPane != null){");
            	js.append("msgPane.value = 'Código não encontrado!!';");
            	js.append("}");
            	js.append("combo.selectedIndex = -1;");
            } else {
            	// Caso codigo não encontrada seleciona o primeiro do select
            	js.append("combo.selectedIndex = 0;");
            }
            
            //js.append("codigo.focus();");
            if (!noMsgPane)
            	js.append("}");
            
            js.append("}");

            js.append("} </SCRIPT>");

            pageContext.getOut().println(js);
            
            release();

        } catch (Exception e) {
            throw new JspException(e);
        }

        return super.doStartTag();

    }

    @Override
    public void release() {
    	super.release();
    	
    	this.beanDesc = null;
    	this.jsFunction = null;
    	this.name = null;
    	this.noMsgPane = false;
    	this.queryProperty = null;
    }
    
    /**
     * @return Returns the jsFunction.
     */
    public String getJsFunction() {
        return jsFunction;
    }

    /**
     * @param jsFunction
     *            The jsFunction to set.
     */
    public void setJsFunction(String jsFunction) {
        this.jsFunction = jsFunction;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the queryProperty.
     */
    public String getQueryProperty() {
        return queryProperty;
    }

    /**
     * @param queryProperty
     *            The queryProperty to set.
     */
    public void setQueryProperty(String queryProperty) {
        this.queryProperty = queryProperty;
    }

    /**
     * @return Returns the beanDesc.
     */
    public String getBeanDesc() {
        return beanDesc;
    }

    /**
     * @param beanDesc
     *            The beanDesc to set.
     */
    public void setBeanDesc(String beanDesc) {
        this.beanDesc = beanDesc;
    }

	public boolean isNoMsgPane() {
		return noMsgPane;
	}

	public void setNoMsgPane(boolean noMsgPane) {
		this.noMsgPane = noMsgPane;
	}
}