 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/11/2010
 *
 */
package br.ufrn.sigaa.ava.questionarios.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe que guarda os dados dos feedbacks a serem exibidos para o discente após a resolução de um questionário
 * 
 * @author Fred_Castro
 *
 */

@Entity @Table(name="feedback_questionario_turma", schema="ava")
public class FeedbackQuestionarioTurma implements PersistDB {

	/** Identificador do objeto */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_feedback_questionario_turma")
	private int id;

	/** Porcentagem máxima. Para este feedback ser exibido, a nota do aluno deve estar entre a porcentagem mínima e máxima. */
	@Column(name="porcentagem_maxima")
	private int porcentagemMaxima;
	
	/** Porcentagem mínima. Para este feedback ser exibido, a nota do aluno deve estar entre a porcentagem mínima e máxima. */
	@Column(name="porcentagem_minima")
	private int porcentagemMinima;
	
	/** A mensagem, em HTML,  a ser exibida */
	private String texto;
	
	/** O questionário ao qual pertence este feedback */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_questionario_turma", unique = false, nullable = true, insertable = true, updatable = true)
	private QuestionarioTurma questionario;
	
	/** Construtor padrão */
	public FeedbackQuestionarioTurma () {
		
	}

	/**
	 * Construtor que já monta o objeto com o texto, porcentagemMaxima e questionário
	 * 
	 * @param texto
	 * @param porcentagemMaxima
	 * @param questionario
	 */
	public FeedbackQuestionarioTurma (String texto, int porcentagemMaxima, QuestionarioTurma questionario) {
		this.texto = texto;
		this.porcentagemMaxima = porcentagemMaxima;
		this.questionario = questionario;
	}
	
	public int getPorcentagemMaxima() {
		return porcentagemMaxima;
	}
	
	public void setPorcentagemMaxima(int porcentagemMaxima) {
		this.porcentagemMaxima = porcentagemMaxima;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public QuestionarioTurma getQuestionario() {
		return questionario;
	}

	public void setQuestionario(QuestionarioTurma questionario) {
		this.questionario = questionario;
	}

	public int getPorcentagemMinima() {
		return porcentagemMinima;
	}

	public void setPorcentagemMinima(int porcentagemMinima) {
		this.porcentagemMinima = porcentagemMinima;
	}
}