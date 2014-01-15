/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 14/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;

/** Registra as observações de um bolsista de Docência Assistida.
 * em uma turma de docência assistida.
 * @author Édipo Elder F. Melo
 *
 */
@Entity 
@Table(name="observacoes_docencia_assistida", schema="avaliacao")
public class ObservacoesDocenciaAssistida implements PersistDB, ObservacaoAvaliacaoInstitucional {

	/** Chave primária. */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="obs_docente_turma_seq")
	@SequenceGenerator(name = "obs_docente_turma_seq", sequenceName = "avaliacao.obs_docente_turma_seq", allocationSize=1)
	private int id;
	
	/** Turma de docência assistida ao qual esta observação se refere. */
	@ManyToOne @JoinColumn(name="id_turma_docencia_assistida")
	private TurmaDocenciaAssistida turmaDocenciaAssistida;
	
	/** Avaliação Institucional ao qual esta observação está associada. */
	@ManyToOne @JoinColumn(name="id_avaliacao")
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

	/** Retorna a turma de docência assistida ao qual esta observação se refere.
	 * @return
	 */
	public TurmaDocenciaAssistida getTurmaDocenciaAssistida() {
		return turmaDocenciaAssistida;
	}

	/** Seta a turma de docência assistida ao qual esta observação se refere.
	 * @param turma
	 */
	public void setTurmaDocenciaAssistida(TurmaDocenciaAssistida turmaDocenciaAssistida) {
		this.turmaDocenciaAssistida = turmaDocenciaAssistida;
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
