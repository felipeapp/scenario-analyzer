package br.ufrn.sigaa.monitoria.dominio;

import java.util.Comparator;

/**
 * Classe utilizada na compara��o entre discentes de monitoria.
 * Utilizada na lista de discentes inscritos para o cadastro do 
 * resultado da prova seletiva.
 * 
 * @author ilueny
 *
 */
public class DiscenteMonitoriaPorPrioridadeComparator implements Comparator<DiscenteMonitoria> {
    
    /**
     * Compara discentes de monitoria por ordem de prioridade e alfab�tica.
     */
    public int compare(DiscenteMonitoria dm1, DiscenteMonitoria dm2) {
	// Primeiro crit�rio: se � priorit�rio.
    	int result = 0;
    	if (dm2.getPrioritario() != null) {
	   result = dm2.getPrioritario().compareTo(dm1.getPrioritario());
    	}
	if (result == 0) {
	    // Segundo crit�rio: Ordem alfab�tica
	    result = dm1.getDiscente().getPessoa().compareTo(dm2.getDiscente().getPessoa());
	}
	return result;

    }

}
