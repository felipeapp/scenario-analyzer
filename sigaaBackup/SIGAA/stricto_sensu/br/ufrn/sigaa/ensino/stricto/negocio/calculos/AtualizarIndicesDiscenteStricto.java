/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio.calculos;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.sigaa.ensino.negocio.calculos.AtualizarIndicesDiscente;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * Classe para calcular os �ndices acad�micos dos discentes
 * de p�s-gradua��o stricto sensu.
 * 
 * @author David Pereira
 *
 */
public class AtualizarIndicesDiscenteStricto extends AtualizarIndicesDiscente<DiscenteStricto> {

	@Override
	public char getNivelEnsino() {
		return NivelEnsino.STRICTO;
	}

}
