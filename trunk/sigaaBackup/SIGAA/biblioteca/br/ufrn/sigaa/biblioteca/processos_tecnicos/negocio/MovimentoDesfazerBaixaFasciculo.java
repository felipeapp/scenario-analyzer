/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 19/05/2010
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 * O ato de desfazer uma baixa. São informados o fascículo que vai ter a baixa desfeita e o
 * novo status dele. 
 *
 * @author Bráulio
 */
public class MovimentoDesfazerBaixaFasciculo extends AbstractMovimentoAdapter {
	
	/** O fascículo que terá a baixa desfeita. */
	private Fasciculo fasciculo;
	
	/** A nova situação na qual o fascículo passará a estar
	 * depois de ter a baixa desfeita. */
	private SituacaoMaterialInformacional novaSituacao;
	
	public MovimentoDesfazerBaixaFasciculo(Fasciculo fasciculo,
			SituacaoMaterialInformacional novaSituacao ) {
		this.fasciculo = fasciculo;
		this.novaSituacao = novaSituacao;
	}

	public Fasciculo getFasciculo() {
		return fasciculo;
	}

	public SituacaoMaterialInformacional getNovaSituacao() {
		return novaSituacao;
	}
	
}
