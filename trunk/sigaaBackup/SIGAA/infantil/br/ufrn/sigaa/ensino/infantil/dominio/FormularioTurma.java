/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/11/2011
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

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
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Clase que associa um Formul�rio de Evolu��o da Crian�a
 * do Ensino Infantil a uma Turma.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="formulario_turma", schema="infantil", uniqueConstraints={})
public class FormularioTurma implements PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_formulario_turma", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Formul�rio de Evolu��o da Crian�a ao qual a Turma est� associada. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_formulario_evolucao_crianca")
	private FormularioEvolucaoCrianca formulario;
	
	/** Turma � qual o Formul�rio de Evolu��o da Crian�a est� associado. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_turma")
	private Turma turma;
	
	/** Construtor padr�o. */
	public FormularioTurma() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FormularioEvolucaoCrianca getFormulario() {
		return formulario;
	}

	public void setFormulario(FormularioEvolucaoCrianca formulario) {
		this.formulario = formulario;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	
}
