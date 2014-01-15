/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 17/01/2012
 */
package br.ufrn.sigaa.ensino.dominio;

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
 * Sequ�ncia utilizada para controlar/contabilizar as matr�culas por turma.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "sequencia_matricula_turma", schema = "ensino")
public class SequenciaMatriculaTurma implements PersistDB {
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_seq_matricula_turma", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Turma na qual a matr�cula foi realizada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;
	
	/** N�mero de sequ�ncia da matr�cula. */
	private int sequencia;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSequencia() {
		return sequencia;
	}

	public void setSequencia(int sequencia) {
		this.sequencia = sequencia;
	}

	/** 
	 * Incrementa a sequ�ncia atual em uma unidade.
	 */
	public void incrementarSequencia() {
		sequencia += 1;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}
}
