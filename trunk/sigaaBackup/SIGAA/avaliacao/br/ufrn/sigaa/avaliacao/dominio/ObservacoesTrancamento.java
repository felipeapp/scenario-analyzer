/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 29/04/2008
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
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Observa��es digitadas pelo discente durante a avalia��o
 * para justificar o trancamento de uma determinada turma.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="observacoes_trancamento", schema="avaliacao")
public class ObservacoesTrancamento implements PersistDB, ObservacaoAvaliacaoInstitucional {
	
	/** Chave prim�ria. */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="obs_docente_turma_seq")
	@SequenceGenerator(name = "obs_docente_turma_seq", sequenceName = "avaliacao.obs_docente_turma_seq", allocationSize=1) 
	private int id;
	
	/** Avalia��o Institucional ao qual esta observa��o est� associada. */
	@ManyToOne (fetch=FetchType.LAZY) 
	@JoinColumn(name="id_avaliacao")
	private AvaliacaoInstitucional avaliacao;
	
	/** Turma ao qual esta observa��o se refere. */
	@ManyToOne (fetch=FetchType.LAZY) 
	@JoinColumn(name="id_turma")
	private Turma turma;
	
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

	/** Retorna a turma ao qual esta observa��o se refere.
	 * @return
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma ao qual esta observa��o se refere.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
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
