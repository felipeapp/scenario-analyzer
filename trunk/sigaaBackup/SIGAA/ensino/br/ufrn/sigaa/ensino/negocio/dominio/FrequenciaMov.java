/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;

/**
 * Objeto movimento que carrega todas as frequ�ncias lan�adas
 * na visualiza��o
 *
 * @author Gleydson
 *
 */
public class FrequenciaMov extends AbstractMovimentoAdapter {

	private List<FrequenciaAluno> frequencias;

	public FrequenciaMov(List<FrequenciaAluno> frequencias) {
		setCodMovimento(SigaaListaComando.LANCAR_FREQUENCIA);
		this.frequencias = frequencias;
	}

	public List<FrequenciaAluno> getFrequencias() {
		return frequencias;
	}

	public void setFrequencias(List<FrequenciaAluno> frequencias) {
		this.frequencias = frequencias;
	}

}
