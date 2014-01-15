/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.calculos.AtualizarIndicesDiscente;

/**
 * Classe para calcular os índices acadêmicos dos discentes de graduação.
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
