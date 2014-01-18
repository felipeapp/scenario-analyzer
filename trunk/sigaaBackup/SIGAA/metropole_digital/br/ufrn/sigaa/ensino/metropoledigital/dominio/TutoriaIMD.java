package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import java.util.Date;

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
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Entidade que relaciona um vínculo de um tutor com uma Turma.
 * 
 * @author Gleydson, Rafael Silva, Rafael Barros
 * 
 */
@Entity
@Table(name = "tutoria_imd", schema = "metropole_digital")
public class TutoriaIMD implements PersistDB, Comparable<TutoriaIMD>{
	
	
	/**
     * Chave primária da tabela tutoria_imd
     */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.tutoria_imd_id_tutoria_imd_seq") })
	@Column(name = "id_tutoria_imd", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
     * Turma de entrada na qual o tutor exerce a tutoria
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma_entrada")
	private TurmaEntradaTecnico turmaEntrada;

	/**
     * Referencia o objeto do tutor que está exercendo tutoria
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tutor_imd")
	private TutorIMD tutor;

	/**
     * Corresponde a data na qual o tutor encerrou a tutoria 
     */
	@Column(name = "data_fim_tutoria")
	private Date dataFimTutoria;

	public TutoriaIMD() {

	}

	public TurmaEntradaTecnico getTurmaEntrada() {
		return turmaEntrada;
	}

	public void setTurmaEntrada(TurmaEntradaTecnico turmaEntrada) {
		this.turmaEntrada = turmaEntrada;
	}

	public TutorIMD getTutor() {
		return tutor;
	}

	public void setTutor(TutorIMD tutor) {
		this.tutor = tutor;
	}
	
	public boolean isAtivo() {
		
		return ( dataFimTutoria == null || dataFimTutoria.after(new Date())); 
		
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((turmaEntrada == null) ? 0 : turmaEntrada.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TutoriaIMD other = (TutoriaIMD) obj;
		if (turmaEntrada == null) {
			if (other.turmaEntrada != null)
				return false;
		} else if (!turmaEntrada.equals(other.turmaEntrada))
			return false;
		return true;
	}

	public Date getDataFimTutoria() {
		return dataFimTutoria;
	}

	public void setDataFimTutoria(Date dataFimTutoria) {
		this.dataFimTutoria = dataFimTutoria;
	}

	@Override
	public int compareTo(TutoriaIMD o) {
		return this.getTurmaEntrada().getEspecializacao().getDescricao().compareTo(o.getTurmaEntrada().getEspecializacao().getDescricao());
	}


	
	

}
