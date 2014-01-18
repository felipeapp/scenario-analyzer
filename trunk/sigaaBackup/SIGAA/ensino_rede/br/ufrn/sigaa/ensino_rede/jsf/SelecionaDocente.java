package br.ufrn.sigaa.ensino_rede.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;

/**
 * Interface que define as opera��es comuns para os managed beans que utilizam a consulta
 * de Docentes de Rede de Ensino.
 */
public interface SelecionaDocente {
	/**
	 * Define o docente selecionado para o MBean da opera��o
	 *
	 * @param discente
	 */
	public void setDocenteRede(DocenteRede docenteRede) throws ArqException;

	/**
	 * M�todo a ser chamado ap�s a sele��o do docente
	 *
	 * @return
	 */
	public String selecionaDocenteRede() throws ArqException;
	
}
