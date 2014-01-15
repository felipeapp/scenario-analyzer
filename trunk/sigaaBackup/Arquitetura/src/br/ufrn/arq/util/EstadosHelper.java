/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/02/2005
 */
package br.ufrn.arq.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.comum.dominio.Estado;
import br.ufrn.comum.dominio.Sistema;

/**
 * Classe com métodos para a gereção e verificação referente aos estados brasilieros.
 *
 * @author Rafael Borja
 * @author Gleydson Lima
 *
 */
public class EstadosHelper {
	
	private static Collection<Estado> estados;
	private static Collection<String> siglas;
	private static Collection<String> nomes;

    /**
     * Retorna colleção com as siglas dos 27 estados brasilieros mais distrito
     * federal.
     *
     * @return colleção com as siglas dos 27 estados brasilieros mais distrito
     *         federal
     */
    public static Collection<String> siglas() {
    	if (siglas == null) {
    		siglas = new ArrayList<String>(getEstados().size());
    		for (Iterator<Estado> it = getEstados().iterator(); it.hasNext(); ) {
    			Estado atual = it.next();
    			siglas.add(atual.getDenominacao());
    		}
    	}
    	
        return siglas;
    }

    /**
     * Retorna colleção com os nomes dos 27 estados brasilieros mais distrito
     * federal.
     *
     * @return colleção com os nomes dos 27 estados brasilieros mais distrito
     *         federal
     */
    public static Collection<String> nomes() {
    	if (nomes == null) {
    		nomes = new ArrayList<String>(getEstados().size());
    		for (Iterator<Estado> it = getEstados().iterator(); it.hasNext(); ) {
    			Estado atual = it.next();
    			nomes.add(atual.getDescricao());
    		}
    	}
    	
        return nomes;
    }

    /**
     * Testa se a sigla da u.f. passada como parâmetro correspondo a algum
     * estado brasiliero.
     *
     * @param estado sigla do estado.
     * @return true se estado for uma sigla válida.
     */
    public static boolean isValidEstado(String estado) {
        for (Iterator<String> it = siglas().iterator(); it.hasNext(); ) {
        	String atual = it.next();
            if (atual.equalsIgnoreCase(estado))
                return true;
        }
        return false;
    }

	public static Collection<Estado> getEstados() {
		if (estados == null) {
			GenericDAO genDAO = null;
	
			try {
				genDAO = DAOFactory.getGeneric(Sistema.SIPAC);
				estados = genDAO.findAll(Estado.class);
			} catch (Exception e) {
				estados = new ArrayList<Estado>();
			} finally {
				if (genDAO != null) genDAO.close();
			}
		}

		return estados;
	}

    public static int sigla2Id(String sigla) {
    	for (Estado e: getEstados())
    		if (sigla.equalsIgnoreCase(e.getDenominacao()))
    			return e.getId();

    	return -1;

    }
}
