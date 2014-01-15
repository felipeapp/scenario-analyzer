/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Interface que dever� ser implementada para c�lcular o prazo m�ximo dos discentes
 * 
 * @author Henrique Andr�
 *
 * @param <T>
 */
public interface CalculoPrazoMaximo<T> {
	
	/**
	 * N�vel de ensino dos alunos que ser�o afetados pelo c�lculo
	 * @return
	 */
	public char getNivel();
	
	/**
	 * M�todo que calcular� o prazo m�ximo
	 * 
	 * @param discente
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	public T calcular(DiscenteAdapter discente, Movimento mov) throws ArqException;
}
