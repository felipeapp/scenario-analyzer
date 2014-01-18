package br.ufrn.sigaa.ensino_rede.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;

/**
 * Interface que define as operações comuns para os managed beans que utilizam a consulta
 * de Docentes de Rede de Ensino.
 */
public interface SelecionaDocente {
	/**
	 * Define o docente selecionado para o MBean da operação
	 *
	 * @param discente
	 */
	public void setDocenteRede(DocenteRede docenteRede) throws ArqException;

	/**
	 * Método a ser chamado após a seleção do docente
	 *
	 * @return
	 */
	public String selecionaDocenteRede() throws ArqException;
	
}
