/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * que realizam c�lculos de �ndices acad�micos.
 * 
 * @author David Pereira
 *
 */
public interface CalculoIndiceAcademico {

	/**
	 * Calcula o �ndice acad�mico para o discente passado como par�metro e o retorna.
	 * @param discente
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public double calcular(Discente discente, Movimento mov) throws DAOException;
	
}
