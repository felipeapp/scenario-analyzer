/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Interface implementada pelos managed beans que far�o buscas por pessoas
 * @author Andre M Dantas
 *
 */
public interface OperadorPessoa {

	/**
	 * Define a pessoa selecionada para o MBean da opera��o
	 *
	 * @param discente
	 */
	public void setPessoa(Pessoa pessoa);

	/**
	 * M�todo a ser chamado ap�s a sele��o da pessoa
	 *
	 * @return
	 */
	public String selecionaPessoa();

}
