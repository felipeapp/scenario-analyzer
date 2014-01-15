/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.calculos.AtualizarIndicesDiscente;

/**
 * Classe para calcular os �ndices acad�micos dos discentes de gradua��o.
 * 
 * @author David Pereira
 *
 */
public class AtualizarIndicesDiscenteGraduacao extends AtualizarIndicesDiscente<DiscenteGraduacao> {

	@Override
	public char getNivelEnsino() {
		return NivelEnsino.GRADUACAO;
	}

}
