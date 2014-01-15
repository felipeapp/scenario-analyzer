/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;

/**
 * Objeto movimento que carrega todas as frequências lançadas
 * na visualização
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
