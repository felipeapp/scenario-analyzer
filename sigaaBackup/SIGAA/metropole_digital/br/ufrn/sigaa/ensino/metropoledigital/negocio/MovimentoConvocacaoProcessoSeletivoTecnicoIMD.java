package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.tecnico.dominio.CancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;

/**
 * Classe utilizada para encapsular as informa��es de neg�cio necess�rias para a convoca��o de candidatos
 * para as vaga remanescentes do vestibular do IMD.
 * 
 * @author Rafael Barros
 *
 */
public class MovimentoConvocacaoProcessoSeletivoTecnicoIMD extends MovimentoCadastro {

	/**	Cole��o de novas convoca��es. */
	private Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes;
	
	/**	Cole��o de cancelamentos de convoca��es a serem registrados. */
	private Collection<CancelamentoConvocacaoTecnico> cancelamentos;
	
	/**	Construtor padr�o. */
	public MovimentoConvocacaoProcessoSeletivoTecnicoIMD () {
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

