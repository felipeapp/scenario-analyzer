/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/01/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;

/**
 * Classe utilizada para encapsular as informações de negócio necessárias para a convocação de candidatos
 * para as vaga remanescentes do vestibular.
 * 
 * @author Leonardo Campos
 *
 */
public class MovimentoConvocacaoVestibular extends MovimentoCadastro {

	/**	Coleção de novas convocações. */
	private Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes;
	
	/**	Coleção de cancelamentos de convocações a serem registrados. */
	private Collection<CancelamentoConvocacao> cancelamentos;
	
	/**	Construtor padrão. */
	public MovimentoConvocacaoVestibular() {
	}

	public Collection<ConvocacaoProcessoSeletivoDiscente> getConvocacoes() {
		return convocacoes;
	}

	public void setConvocacoes(
			Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes) {
		this.convocacoes = convocacoes;
	}

	public Collection<CancelamentoConvocacao> getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(Collection<CancelamentoConvocacao> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}
}
