/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Movimentação de cancelamento automático
 *
 * @author Gleydson
 *
 */
public class MovimentoCancelamentoAutomatico extends MovimentoCadastro {

	/** Discentes que serão cancelados */
	private Collection<Discente> discentes;
	/** Ano de referência */
	private int ano;
	/** Período de referência */
	private int periodo;
	
	/** Mensagem de Observação no histórico dos discentes */
	private String observacoes;
	
	/** este atributo diz ao processador se ele deve ignorar as pendencias que o discente possui na biblioteca ao cancelar o vinculo.
	 * Caso true o sistema irá ignorar qualquer pendencia e executar o cancelamento do aluno*/
	private boolean ignorarPendencias = false;
	
	/** Este atributo diz ao processador TipoMovimentacaoAluno a ser setado no MovimentacaoAluno. Usado ao jubilar aluno,
	 *  que pode ser por Abandono ou por Prazo Máximo por exemplo. */
	private TipoMovimentacaoAluno tipoMovimentacao;

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public boolean isIgnorarPendencias() {
		return ignorarPendencias;
	}

	public void setIgnorarPendencias(boolean ignorarPendencias) {
		this.ignorarPendencias = ignorarPendencias;
	}

	public TipoMovimentacaoAluno getTipoMovimentacao() {
		return tipoMovimentacao;
	}

	public void setTipoMovimentacao(TipoMovimentacaoAluno tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public String getObservacoes() {
		return observacoes;
	}

}