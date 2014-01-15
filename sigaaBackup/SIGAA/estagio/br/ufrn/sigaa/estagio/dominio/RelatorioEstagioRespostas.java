/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 17/11/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;

/**
 * Entidade que cont�m as respostas referente ao relat�rio de est�gio associado.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "relatorio_estagio_respostas", schema = "estagio")
public class RelatorioEstagioRespostas implements PersistDB {
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_relatorio_estagio_respostas")
	private int id;		
	
	/**
	 * Relat�rio de est�gio associado a resposta 
	 **/
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_relatorio_estagio")	
	private RelatorioEstagio relatorioEstagio;
	
	/** Respostas do question�rio */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_questionario_respostas")	
	private QuestionarioRespostas questionarioRespostas;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RelatorioEstagio getRelatorioEstagio() {
		return relatorioEstagio;
	}

	public void setRelatorioEstagio(RelatorioEstagio relatorioEstagio) {
		this.relatorioEstagio = relatorioEstagio;
	}

	public QuestionarioRespostas getQuestionarioRespostas() {
		return questionarioRespostas;
	}

	public void setQuestionarioRespostas(QuestionarioRespostas questionarioRespostas) {
		this.questionarioRespostas = questionarioRespostas;
	}
}
