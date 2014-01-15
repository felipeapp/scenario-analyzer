/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/02/2012
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;

/**
 *
 *    Movimento para cadastrar uma classificação bibliográfica.
 *
 * @author felipe
 * @since 15/02/2012
 * @version 1.0 criacao da classe
 *
 */

public class MovimentoCadastraClassificacaoBibliografica extends AbstractMovimentoAdapter {

	/**A classificação que vai ser criada */
	private ClassificacaoBibliografica classificacaoBibliografica;
	
	public MovimentoCadastraClassificacaoBibliografica(ClassificacaoBibliografica classificacaoBibliografica) {
		this.classificacaoBibliografica = classificacaoBibliografica;
	}

	public ClassificacaoBibliografica getClassificacaoBibliografica() {
		return classificacaoBibliografica;
	}
	
}
