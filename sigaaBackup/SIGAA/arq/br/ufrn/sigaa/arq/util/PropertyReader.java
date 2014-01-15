/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 * 
 * Created on 30/11/2004
 *
 */
package br.ufrn.sigaa.arq.util;

import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import br.ufrn.arq.erros.ArqException;

/**
 * Classe Utilitária para buscar os erros que encontram-se na classe de
 * propriedades SIPACMessages.properties
 * 
 * @author Gleydson Lima
 *  
 */
public class PropertyReader {

    private ResourceBundle bundle;

    private static PropertyReader singleton = new PropertyReader();

    private PropertyReader() {
		this("br.ufrn.sipac.arq.util.SIPACMensagens");
	}

	private PropertyReader(String bundleName) {
		bundle = PropertyResourceBundle.getBundle(bundleName);
	}

    public String getErrorMsg(String key) throws ArqException {

        String error = (String) bundle.getObject(key);
		if (error == null) {
			Enumeration e = bundle.getKeys();
			String msg = "Erro não encontrado: " + key + "\n" + "KEYS: ";
			while (e.hasMoreElements())
				msg += "[" + e.nextElement() + "]";

			throw new ArqException(msg);
		}
        return error;

    }

    public static PropertyReader getInstance() {
        return singleton;
    }

    public static PropertyReader getInstance(String bundle) {
        singleton = new PropertyReader(bundle);
        return singleton;
    }

    public boolean isCreated() {
        return bundle != null;
    }
}