/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 15/12/2010
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.tecnico.dominio.CancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe utilizada para encapsular as informa��es de neg�cio necess�rias para o
 * processamento do cadastramento de discentes.
 * 
 * @author Leonardo Campos
 * 
 */
public class MovimentoCadastramentoDiscenteTecnico extends AbstractMovimentoAdapter {

	/**	Cole��o de discentes cujo cadastramento deve ser confirmado. */
	private Collection<Discente> cadastrados;
	
	/**	Cole��o de discentes cujo cadastramento deve ser cancelado. */
	private Collection<Discente> cancelados;
	
	/**	Cole��o de cancelamentos de convoca��es a serem registrados. */
	private Collection<CancelamentoConvocacaoTecnico> cancelamentos;
	
	/** Indica que o cancelamento/cadastramento � referente aos discentes importados de outros concursos. */
	private boolean discentesImportados;
	
	/**	Construtor padr�o. */
	public MovimentoCadastramentoDiscenteTecnico() {
	}

	public Collection<Discente> getCadastrados() {
		return cadastrados;
	}

	public void setCadastrados(Collection<Discente> cadastrados) {
		this.cadastrados = cadastrados;
	}

	public Collection<CancelamentoConvocacaoTecnico> getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(Collection<CancelamentoConvocacaoTecnico> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public Collection<Discente> getCancelados() {
		return cancelados;
	}

	public void setCancelados(Collection<Discente> cancelados) {
		this.cancelados = cancelados;
	}

	public boolean isDiscentesImportados() {
		return discentesImportados;
	}

	public void setDiscentesImportados(boolean discentesImportados) {
		this.discentesImportados = discentesImportados;
	}
}
