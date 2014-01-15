/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 11/08/2011
 */


package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.VinculoUsuario;

/**
 * Interface que ser� implementada para definir a forma como o vinculo dever� ser populado
 * 
 * @author Henrique Andr�
 *
 */
public interface EstrategiaPopularVinculo {

	/**
	 * Implementa a l�gica
	 * 
	 * @param vinculo
	 * @return
	 * @throws DAOException
	 */
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException;
	
}
