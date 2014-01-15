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
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.prodocente.atividades.dominio.CHDedicadaResidenciaMedica;

/**
 * Entidade que relaciona a CHDedicadaResidenciaMedica com PlanoIndividualDocente. Caso o
 * docente possua CH de Residência Médica, essa CH será exibida juntamente com as turmas
 * que ele possui.
 * 
 * @author agostinho campos
 *
 */
@Entity
@Table(name = "ch_residencia_medica_pid", schema = "pid")
public class ChResidenciaMedicaPID implements PersistDB, Validatable {

	/**
	 * Define a unicidade na base de dados.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_ch_residencia_medica_pid")
	private int id;

	/**
	 * Define o plano individual do docente
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_plano_individual_docente")
	private PlanoIndividualDocente planoIndividualDocente;

	/**
	 * Define a carga horária da residência médica
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ch_dedicada_residencia_medica")
	private CHDedicadaResidenciaMedica chDedicadaResidenciaMedica;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PlanoIndividualDocente getPlanoIndividualDocente() {
		return planoIndividualDocente;
	}

	public void setPlanoIndividualDocente(
			PlanoIndividualDocente planoIndividualDocente) {
		this.planoIndividualDocente = planoIndividualDocente;
	}

	public CHDedicadaResidenciaMedica getChDedicadaResidenciaMedica() {
		return chDedicadaResidenciaMedica;
	}

	public void setChDedicadaResidenciaMedica(
			CHDedicadaResidenciaMedica chDedicadaResidenciaMedica) {
		this.chDedicadaResidenciaMedica = chDedicadaResidenciaMedica;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((chDedicadaResidenciaMedica == null) ? 0
						: chDedicadaResidenciaMedica.hashCode());
		result = prime * result + id;
		result = prime
				* result
				+ ((planoIndividualDocente == null) ? 0
						: planoIndividualDocente.hashCode());
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
		ChResidenciaMedicaPID other = (ChResidenciaMedicaPID) obj;
		if (chDedicadaResidenciaMedica == null) {
			if (other.chDedicadaResidenciaMedica != null)
				return false;
		} else if (!chDedicadaResidenciaMedica
				.equals(other.chDedicadaResidenciaMedica))
			return false;
		if (id != other.id)
			return false;
		if (planoIndividualDocente == null) {
			if (other.planoIndividualDocente != null)
				return false;
		} else if (!planoIndividualDocente.equals(other.planoIndividualDocente))
			return false;
		return true;
	}

	public ListaMensagens validate() {
		return null;
	}
}
