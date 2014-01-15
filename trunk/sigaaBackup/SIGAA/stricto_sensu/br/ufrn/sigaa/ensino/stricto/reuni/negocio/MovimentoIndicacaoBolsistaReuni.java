/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/04/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.IndicacaoBolsistaReuni;

/**
 * Movimento para realizar as operações sobre Indicação Bolsista Reuni
 * @author Arlindo Rodrigues
 */
public class MovimentoIndicacaoBolsistaReuni extends AbstractMovimentoAdapter {
	
	private IndicacaoBolsistaReuni indicacao;
	
	private DiscenteStricto discenteAtual;

	public IndicacaoBolsistaReuni getIndicacao() {
		return indicacao;
	}

	public void setIndicacao(IndicacaoBolsistaReuni indicacao) {
		this.indicacao = indicacao;
	}

	public DiscenteStricto getDiscenteAtual() {
		return discenteAtual;
	}

	public void setDiscenteAtual(DiscenteStricto discenteAtual) {
		this.discenteAtual = discenteAtual;
	}
	
}
