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
 * Cont�m implementa��o para popular o v�nculo gen�rico
 * 
 * @author Henrique Andr�
 *
 */
public class EstrategiaPopularVinculoGenerico implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		return vinculo;
	}

}
