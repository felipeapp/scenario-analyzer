package br.ufrn.sigaa.ensino_rede.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.dominio.CampusIes;

public interface SelecionaCampus {
	
	/**
	 * Define o campus selecionado para o MBean da operação
	 *
	 * @param discente
	 */
	public void setCampus(CampusIes campus) throws ArqException;

	/**
	 * Método a ser chamado após a seleção do discente
	 *
	 * @return
	 */
	public String selecionaCampus() throws ArqException;

}
