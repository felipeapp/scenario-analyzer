/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/03/2010
 */
package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Interface a ser implementada por todas as classes
 * que realizam cálculos de índices acadêmicos.
 * 
 * @author David Pereira
 *
 */
public interface CalculoIndiceAcademico {

	/**
	 * Calcula o índice acadêmico para o discente passado como parâmetro e o retorna.
	 * @param discente
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public double calcular(Discente discente, Movimento mov) throws DAOException;
	
}
