/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/01/2011
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.tecnico.dominio.CancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;

/**
 * Classe utilizada para encapsular as informações de negócio necessárias para a convocação de candidatos
 * para as vaga remanescentes do vestibular.
 * 
 * @author Leonardo Campos
 *
 */
public class MovimentoConvocacaoProcessoSeletivoTecnico extends MovimentoCadastro {

	/**	Coleção de novas convocações. */
	private Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes;
	
	/**	Coleção de cancelamentos de convocações a serem registrados. */
	private Collection<CancelamentoConvocacaoTecnico> cancelamentos;
	
	/**	Construtor padrão. */
	public MovimentoConvocacaoProcessoSeletivoTecnico () {
	}

	public Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> getConvocacoes() {
		return convocacoes;
	}

	public void setConvocacoes(
			Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes) {
		this.convocacoes = convocacoes;
	}

	public Collection<CancelamentoConvocacaoTecnico> getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(Collection<CancelamentoConvocacaoTecnico> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}
}
