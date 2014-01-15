package br.ufrn.sigaa.ensino_rede.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.dominio.CampusIes;

public interface SelecionaCampus {
	
	/**
	 * Define o campus selecionado para o MBean da opera��o
	 *
	 * @param discente
	 */
	public void setCampus(CampusIes campus) throws ArqException;

	/**
	 * M�todo a ser chamado ap�s a sele��o do discente
	 *
	 * @return
	 */
	public String selecionaCampus() throws ArqException;

}
