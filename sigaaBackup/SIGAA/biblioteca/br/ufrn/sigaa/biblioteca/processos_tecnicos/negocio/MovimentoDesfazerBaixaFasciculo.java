/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 19/05/2010
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 * O ato de desfazer uma baixa. S�o informados o fasc�culo que vai ter a baixa desfeita e o
 * novo status dele. 
 *
 * @author Br�ulio
 */
public class MovimentoDesfazerBaixaFasciculo extends AbstractMovimentoAdapter {
	
	/** O fasc�culo que ter� a baixa desfeita. */
	private Fasciculo fasciculo;
	
	/** A nova situa��o na qual o fasc�culo passar� a estar
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
