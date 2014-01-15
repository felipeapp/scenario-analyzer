/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '23/08/2010'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Interface que deverá ser implementada para cálcular o prazo máximo dos discentes
 * 
 * @author Henrique André
 *
 * @param <T>
 */
public interface CalculoPrazoMaximo<T> {
	
	/**
	 * Nível de ensino dos alunos que serão afetados pelo cálculo
	 * @return
	 */
	public char getNivel();
	
	/**
	 * Método que calculará o prazo máximo
	 * 
	 * @param discente
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	public T calcular(DiscenteAdapter discente, Movimento mov) throws ArqException;
}
