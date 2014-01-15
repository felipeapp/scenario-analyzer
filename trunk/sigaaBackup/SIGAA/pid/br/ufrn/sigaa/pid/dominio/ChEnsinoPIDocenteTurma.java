/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 03/03/2010
 */
package br.ufrn.sigaa.pid.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * Entidade que relaciona a CHEnsino do PID com DocenteTurma. O PID de
 * um docente geralmente tem várias turmas (e suas respectivas cargas horárias)
 * que são representadas por DocenteTurma.
 * 
 * @author agostinho campos
 *
 */
@Entity
@Table(name = "ch_ensino_pid_docente_turma", schema = "pid")
public class ChEnsinoPIDocenteTurma implements PersistDB, Validatable {
	
	/** Define a unicidade na base de dados. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_ch_ensino_pid_docente_turma")
	private int id;

	/**
	 * evita propagar erro de ObjectNotFoundException
	 * esse erro é tratado na aplicação e o registro que 
	 * faz referencia ao registro inexistente é removido do 
	 * banco.
	 * 
	 * Por exemplo quando uma turma é transferida para outro
	 * professor, é necessário remover essa turma do PID
	 * @NotFound(action=NotFoundAction.IGNORE)
	 */
	@NotFound(action=NotFoundAction.IGNORE)  
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_turma")
	private DocenteTurma docenteTurma;

	/**
	 * Define a CH de ensino cadastrada pelo Docente.
	 * @see br.ufrn.sigaa.pid.dominio.CargaHorariaEnsino
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_carga_horaria_ensino")
	private CargaHorariaEnsino cargaHorariaEnsino;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		return null;
	}

	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	public CargaHorariaEnsino getCargaHorariaEnsino() {
		return cargaHorariaEnsino;
	}

	public void setCargaHorariaEnsino(CargaHorariaEnsino cargaHorariaEnsino) {
		this.cargaHorariaEnsino = cargaHorariaEnsino;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((cargaHorariaEnsino == null) ? 0 : cargaHorariaEnsino
						.hashCode());
		result = prime * result
				+ ((docenteTurma == null) ? 0 : docenteTurma.hashCode());
		result = prime * result + id;
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
		ChEnsinoPIDocenteTurma other = (ChEnsinoPIDocenteTurma) obj;
		if (cargaHorariaEnsino == null) {
			if (other.cargaHorariaEnsino != null)
				return false;
		} else if (!cargaHorariaEnsino.equals(other.cargaHorariaEnsino))
			return false;
		if (docenteTurma == null) {
			if (other.docenteTurma != null)
				return false;
		} else if (!docenteTurma.equals(other.docenteTurma))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
}
