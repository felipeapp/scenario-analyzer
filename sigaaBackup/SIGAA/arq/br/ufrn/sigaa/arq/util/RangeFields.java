/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 * 
 * Created on 01/07/2005
 *
 */
package br.ufrn.sigaa.arq.util;

import javax.servlet.jsp.JspException;

import br.ufrn.arq.erros.ArqException;

/**
 * Classe que implementa o Function Range para limitação de campos
 * 
 * @author Gleydson Lima
 *  
 */
public class RangeFields  {

    private PropertyReader reader;
    
    private static RangeFields singleton;
    

    public RangeFields() {

        reader = PropertyReader
                .getInstance("br.ufrn.sipac.arq.util.RangeFields");

    }

    public int getRange(String key) throws JspException {

        try {
            String range = reader.getErrorMsg(key);
            if (range == null) {
                throw new JspException("Range para o campo " + key
                        + " não encontrado");
            } else {
                return Integer.parseInt(range);
            }
        } catch (ArqException e) {
            throw new JspException("erro ao abrir arquivo de ranges");
        }

    }
    
    public static RangeFields getInstance() {
        if ( singleton == null ) {
            singleton = new RangeFields();
        }
        return singleton;
    }

  
}