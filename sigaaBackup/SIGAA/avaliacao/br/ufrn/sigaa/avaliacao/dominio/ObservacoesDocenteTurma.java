/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 30/12/2008 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Registra as observações de um aluno para um docente
 * em uma turma.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="observacoes_docente_turma", schema="avaliacao")
public class ObservacoesDocenteTurma implements PersistDB, ObservacaoAvaliacaoInstitucional {

	/** Chave primária. */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="obs_docente_turma_seq")
	@SequenceGenerator(name = "obs_docente_turma_seq", sequenceName = "avaliacao.obs_docente_turma_seq", allocationSize=1)
	private int id;
	
	/** Turma ao qual esta observação se refere. */
	@ManyToOne (fetch=FetchType.LAZY) 
	@JoinColumn(name="id_turma")
	private Turma turma;
	
	/** DocenteTurma ao qual esta observação se refere. */
	@ManyToOne (fetch=FetchType.LAZY) 
	@JoinColumn(name="id_docente_turma")
	private DocenteTurma docenteTurma;
	
	/** Avaliação Institucional ao qual esta observação está associada. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_avaliacao")
	private AvaliacaoInstitucional avaliacao;
	
	/** Texto da observação dado pelo usuário que preencheu a Avaliação Institucional. */
	private String observacoes;
	
	/** Texto da observação moderado pela Comissão Própria de Avaliação. */
	@Column(name="observacoes_moderadas")
	private String observacoesModeradas;

	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o docenteTurma ao qual esta observação se refere.
	 * @return
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	/** Seta o docenteTurma ao qual esta observação se refere.
	 * @param docenteTurma
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	/** Retorna a Avaliação Institucional ao qual esta observação está associada.
	 * @return
	 */
	public AvaliacaoInstitucional getAvaliacao() {
		return avaliacao;
	}

	/** Seta a Avaliação Institucional ao qual esta observação está associada.
	 * @param avaliacao
	 */
	public void setAvaliacao(AvaliacaoInstitucional avaliacao) {
		this.avaliacao = avaliacao;
	}

	/** Retorna a turma ao qual esta observação se refere.
	 * @return
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma ao qual esta observação se refere.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Retorna o texto da observação dado pelo usuário que preencheu a Avaliação Institucional.
	 * @return
	 */
	public String getObservacoes() {
		return observacoes;
	}

	/** Seta o texto da observação dado pelo usuário que preencheu a Avaliação Institucional.
	 * @param observacoes
	 */
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	/** Retorna o texto da observação moderado pela Comissão Própria de Avaliação.
	 * @return
	 */
	public String getObservacoesModeradas() {
		return observacoesModeradas;
	}

	/** Seta o texto da observação moderado pela Comissão Própria de Avaliação.
	 * @param observacoesModeradas
	 */
	public void setObservacoesModeradas(String observacoesModeradas) {
		this.observacoesModeradas = observacoesModeradas;
	}
	
	/** Retorna o texto da observação dada.
	 * @see #getObservacoes()
	 */
	@Override
	public String toString() {
		return getObservacoes();
	}
	
}
