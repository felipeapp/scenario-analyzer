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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 * O ato de desfazer uma baixa. São informados o exemplar que vai ter a baixa desfeita e o
 * novo status dele. 
 *
 * @author Bráulio
 */
public class MovimentoDesfazerBaixaExemplar extends AbstractMovimentoAdapter {
	
	/** O exemplar que terá a baixa desfeita. */
	private Exemplar exemplar;
	
	/** A nova situação na qual o exemplar passará a estar depois da baixa ter
	 * sido desfeita. */
	private SituacaoMaterialInformacional novaSituacao;
	
	public MovimentoDesfazerBaixaExemplar (
			Exemplar exemplar, SituacaoMaterialInformacional novaSituacao ) {
		this.exemplar = exemplar;
		this.novaSituacao = novaSituacao;
	}

	public Exemplar getExemplar() {
		return exemplar;
	}

	public SituacaoMaterialInformacional getNovaSituacao() {
		return novaSituacao;
	}
	
}
