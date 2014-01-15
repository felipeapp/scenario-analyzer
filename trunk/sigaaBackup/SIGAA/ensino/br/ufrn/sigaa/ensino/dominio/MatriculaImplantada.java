package br.ufrn.sigaa.ensino.dominio;

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

/**
 * Esta entidade representa as matrículas que foram implantadas, é necessário para permitir uma posterior alteração
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "matricula_implantada",schema="ensino")
public class MatriculaImplantada implements PersistDB{

	/** Chave primária. */
	@Id @Column(name = "id_matricula_implantada")
	@GeneratedValue(generator="SEQ_MATRICULA")
	@GenericGenerator(name="SEQ_MATRICULA", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.matricula_implantada_seq")})
	private int id;
	
	/**
	 * Registro da matrícula que foi implantada
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matriculaComponente;
	
	public MatriculaImplantada( MatriculaComponente matriculaComponente ) {
		this.matriculaComponente = matriculaComponente;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}

	
}
