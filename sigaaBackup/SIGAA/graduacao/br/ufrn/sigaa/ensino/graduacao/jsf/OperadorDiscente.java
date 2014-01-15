/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Interface implementada pelos managed beans que far�o buscas por discentes
 *
 * @author David Pereira
 *
 */
public interface OperadorDiscente {

	/**
	 * Define o discente selecionado para o MBean da opera��o
	 *
	 * @param discente
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException;

	/**
	 * M�todo a ser chamado ap�s a sele��o do discente
	 * Chamado por: graduacao/busca_discente.jsp
	 *
	 * @return
	 */
	public String selecionaDiscente() throws ArqException;

}
