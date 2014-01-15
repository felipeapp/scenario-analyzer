/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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

/** Registra as observa��es de um bolsista de Doc�ncia Assistida.
 * em uma turma de doc�ncia assistida.
 * @author �dipo Elder F. Melo
 *
 */
@Entity 
@Table(name="observacoes_docencia_assistida", schema="avaliacao")
public class ObservacoesDocenciaAssistida implements PersistDB, ObservacaoAvaliacaoInstitucional {

	/** Chave prim�ria. */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="obs_docente_turma_seq")
	@SequenceGenerator(name = "obs_docente_turma_seq", sequenceName = "avaliacao.obs_docente_turma_seq", allocationSize=1)
	private int id;
	
	/** Turma de doc�ncia assistida ao qual esta observa��o se refere. */
	@ManyToOne @JoinColumn(name="id_turma_docencia_assistida")
	private TurmaDocenciaAssistida turmaDocenciaAssistida;
	
	/** Avalia��o Institucional ao qual esta observa��o est� associada. */
	@ManyToOne @JoinColumn(name="id_avaliacao")
	private AvaliacaoInstitucional avaliacao;
	
	/** Texto da observa��o dado pelo usu�rio que preencheu a Avalia��o Institucional. */
	private String observacoes;
	
	/** Texto da observa��o moderado pela Comiss�o Pr�pria de Avalia��o. */
	@Column(name="observacoes_moderadas")
	private String observacoesModeradas;

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a Avalia��o Institucional ao qual esta observa��o est� associada.
	 * @return
	 */
	public AvaliacaoInstitucional getAvaliacao() {
		return avaliacao;
	}

	/** Seta a Avalia��o Institucional ao qual esta observa��o est� associada.
	 * @param avaliacao
	 */
	public void setAvaliacao(AvaliacaoInstitucional avaliacao) {
		this.avaliacao = avaliacao;
	}

	/** Retorna a turma de doc�ncia assistida ao qual esta observa��o se refere.
	 * @return
	 */
	public TurmaDocenciaAssistida getTurmaDocenciaAssistida() {
		return turmaDocenciaAssistida;
	}

	/** Seta a turma de doc�ncia assistida ao qual esta observa��o se refere.
	 * @param turma
	 */
	public void setTurmaDocenciaAssistida(TurmaDocenciaAssistida turmaDocenciaAssistida) {
		this.turmaDocenciaAssistida = turmaDocenciaAssistida;
	}

	/** Retorna o texto da observa��o dado pelo usu�rio que preencheu a Avalia��o Institucional.
	 * @return
	 */
	public String getObservacoes() {
		return observacoes;
	}

	/** Seta o texto da observa��o dado pelo usu�rio que preencheu a Avalia��o Institucional.
	 * @param observacoes
	 */
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	/** Retorna o texto da observa��o moderado pela Comiss�o Pr�pria de Avalia��o.
	 * @return
	 */
	public String getObservacoesModeradas() {
		return observacoesModeradas;
	}

	/** Seta o texto da observa��o moderado pela Comiss�o Pr�pria de Avalia��o.
	 * @param observacoesModeradas
	 */
	public void setObservacoesModeradas(String observacoesModeradas) {
		this.observacoesModeradas = observacoesModeradas;
	}
	
	/** Retorna o texto da observa��o dada.
	 * @see #getObservacoes()
	 */
	@Override
	public String toString() {
		return getObservacoes();
	}
}
