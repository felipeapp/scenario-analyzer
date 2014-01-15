/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe utilizada para encapsular as informa��es de neg�cio necess�rias para a convoca��o de candidatos
 * para as vaga remanescentes do vestibular.
 * 
 * @author Leonardo Campos
 *
 */
public class MovimentoConvocacaoProcessoSeletivoTecnico extends MovimentoCadastro {

	/**	Cole��o de novas convoca��es. */
	private Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes;
	
	/**	Cole��o de cancelamentos de convoca��es a serem registrados. */
	private Collection<CancelamentoConvocacaoTecnico> cancelamentos;
	
	/**	Construtor padr�o. */
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
