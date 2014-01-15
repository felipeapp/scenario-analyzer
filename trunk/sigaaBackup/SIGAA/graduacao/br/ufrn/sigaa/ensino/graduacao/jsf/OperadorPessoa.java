/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Interface implementada pelos managed beans que farão buscas por pessoas
 * @author Andre M Dantas
 *
 */
public interface OperadorPessoa {

	/**
	 * Define a pessoa selecionada para o MBean da operação
	 *
	 * @param discente
	 */
	public void setPessoa(Pessoa pessoa);

	/**
	 * Método a ser chamado após a seleção da pessoa
	 *
	 * @return
	 */
	public String selecionaPessoa();

}
