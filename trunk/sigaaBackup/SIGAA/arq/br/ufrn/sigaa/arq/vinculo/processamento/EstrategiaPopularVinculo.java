/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 11/08/2011
 */


package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.VinculoUsuario;

/**
 * Interface que será implementada para definir a forma como o vinculo deverá ser populado
 * 
 * @author Henrique André
 *
 */
public interface EstrategiaPopularVinculo {

	/**
	 * Implementa a lógica
	 * 
	 * @param vinculo
	 * @return
	 * @throws DAOException
	 */
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException;
	
}
