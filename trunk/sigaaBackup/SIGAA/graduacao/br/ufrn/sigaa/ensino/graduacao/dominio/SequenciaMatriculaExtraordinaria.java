package br.ufrn.sigaa.ensino.graduacao.dominio;

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
 * Sequência utilizada para controlar/contabilizar as matrículas extraordinárias por ano-período.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "sequencia_matricula_extraordinaria", schema = "graduacao")
public class SequenciaMatriculaExtraordinaria implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_seq_matricula_extraordinaria", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Turma na qual a matrícula extraordinária é realizada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;
	
	/** Número de sequência da matrícula extraordinária. */
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
	 * Incrementa a sequência atual em uma unidade.
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
