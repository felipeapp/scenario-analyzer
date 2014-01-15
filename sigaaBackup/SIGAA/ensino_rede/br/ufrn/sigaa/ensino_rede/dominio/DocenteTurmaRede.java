package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

@Entity
@Table(schema="ensino_rede", name = "docente_turma_rede")
public class DocenteTurmaRede implements PersistDB {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_docente_turma_rede", nullable = false)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_docente_rede")
	private DocenteRede docente;
	
	@ManyToOne
	@JoinColumn(name = "id_turma_rede")
	private TurmaRede turma;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DocenteRede getDocente() {
		return docente;
	}

	public void setDocente(DocenteRede docente) {
		this.docente = docente;
	}

	public TurmaRede getTurma() {
		return turma;
	}

	public void setTurma(TurmaRede turma) {
		this.turma = turma;
	}

}
