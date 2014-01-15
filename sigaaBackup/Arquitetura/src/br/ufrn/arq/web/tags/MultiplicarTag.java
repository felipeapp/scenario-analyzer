/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 09/12/2004
 */
package br.ufrn.arq.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ReflectionUtils;

/**
 * Tag para multiplicação de dois atributos de beans
 *
 * @author Gleydson Lima
 *
 */
public class MultiplicarTag extends TagSupport {

    private String bean1;

    private String property1;

    private String bean2;

    private String property2;

    private String decimal;

    /** data ou valor * */

    public MultiplicarTag() {
        super();
    }

    /**
     * Metodo manipulador da taglibrary
     */
    public int doStartTag() throws JspException {

        Object obj = pageContext.findAttribute(bean1);
        Object obj2 = pageContext.findAttribute(bean2);



        try {

           /* // Valor do Objeto 1
            StringTokenizer st = new StringTokenizer(property1, ".");
            Object atual = obj;
            String propertyInsideToken;

            while (st.hasMoreTokens()) {
                propertyInsideToken = st.nextToken();
                atual = c.getMethod(formatMethod(propertyInsideToken), (Class<?>) null).invoke(atual, (Object[]) null);
                if (atual == null) {
                    return 0;
                }

                c = atual.getClass();

            }
            // Valor do Objeto 2
            st = new StringTokenizer(property2, ".");
            Object atual2 = obj2;

            while (st.hasMoreTokens()) {
                propertyInsideToken = st.nextToken();
                atual2 = c2.getMethod(formatMethod(propertyInsideToken), (Class<?>) null).invoke(atual2, (Object[]) null);
                if (atual == null) {
                    return 0;
                }

                c2 = atual2.getClass();

            }*/

            double v1 = Double.parseDouble(ReflectionUtils.evalProperty(obj, property1));
            double v2 = Double.parseDouble(ReflectionUtils.evalProperty(obj2, property2));

            if (decimal != null && decimal.toLowerCase().equals("3")) {
                pageContext.getOut().print(
                        Formatador.getInstance().formatarMoeda3(v1 * v2));
            }else{
                pageContext.getOut().println(
                    Formatador.getInstance().formatarMoeda(v1 * v2));
            }

        } catch (Exception e) {
            throw new JspException(e);
        }

        return super.doStartTag();
    }

    public String getBean1() {
        return bean1;
    }

    public void setBean1(String bean1) {
        this.bean1 = bean1;
    }

    public String getBean2() {
        return bean2;
    }

    public void setBean2(String bean2) {
        this.bean2 = bean2;
    }

    public String getProperty1() {
        return property1;
    }

    public void setProperty1(String property) {
        this.property1 = property;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }
    /**
     * @return Retorna decimal.
     */
    public String getDecimal() {
        return decimal;
    }
    /**
     * @param decimal The decimal to set.
     */
    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }
}