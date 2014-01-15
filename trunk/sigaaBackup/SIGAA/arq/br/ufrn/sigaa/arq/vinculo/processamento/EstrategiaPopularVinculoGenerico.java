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
 * Contém implementação para popular o vínculo genérico
 * 
 * @author Henrique André
 *
 */
public class EstrategiaPopularVinculoGenerico implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		return vinculo;
	}

}
