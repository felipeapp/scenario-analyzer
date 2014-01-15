/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '22/07/2010'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Define a implementa��o que ser� usada para calcular o perfil incial do discente.
 * 
 * @author Henrique Andr�
 *
 */
public class PerfilInicialFactory {

	/**
	 * Retorna a implementa��o do PerfilInicial que ser� usada para calcular o perfil inicial do discente
	 * 
	 * @param discente
	 * @return
	 * @throws NegocioException
	 */
	public static PerfilInicial getPerfilInicial(DiscenteGraduacao discente) throws NegocioException {
		return new PerfilInicialNovoRegulamento();
	}
	
}
