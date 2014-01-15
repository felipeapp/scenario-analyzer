/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.ConsolidacaoIndividual;

/**
 * Classe que representa um movimento sobre um objeto da classe ConsolidacaoIndividual
 *
 * @author Gleydson
 *
 */
public class ConsolidacaoIndividualMov extends AbstractMovimentoAdapter {

	private ConsolidacaoIndividual consolicacao;

	private float mediaMinima;

	private float faltasMinima;
	
	private Integer metodoAvaliacao;

	public ConsolidacaoIndividual getConsolicacao() {
		return consolicacao;
	}

	public void setConsolicacao(ConsolidacaoIndividual consolicacao) {
		this.consolicacao = consolicacao;
	}

	public float getMediaMinima() {
		return mediaMinima;
	}

	public void setMediaMinima(float mediaMinima) {
		this.mediaMinima = mediaMinima;
	}

	public float getFaltasMinima() {
		return faltasMinima;
	}

	public void setFaltasMinima(float faltasMinima) {
		this.faltasMinima = faltasMinima;
	}

	public Integer getMetodoAvaliacao() {
		return metodoAvaliacao;
	}
	
	public void setMetodoAvaliacao(Integer metodoAvaliacao) {
		this.metodoAvaliacao = metodoAvaliacao;
	}

}