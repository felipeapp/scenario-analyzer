/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/26
 */
package br.ufrn.sigaa.ead.dominio;

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
import br.ufrn.arq.web.tags.UFRNFunctions;

/**
 * Classe que guarda informações sobre os horários de atendimento
 * de um tutor no Pólo de ensino a distancia.
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(name="horario_tutor", schema="ead")
public class HorarioTutor implements PersistDB {

	/**Chave Primária*/
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Tutor associado aos horários */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_tutor")
	private TutorOrientador tutor;
	
	/** Dia da semana */
	private byte dia;
	
	/** início da disponibilidade */
	@Column(name="hora_inicio")
	private byte horaInicio;
	
	/** fim da disponibilidade */
	@Column(name="hora_fim")
	private byte horaFim;

	public HorarioTutor() { }
	public HorarioTutor(byte dia) { this.dia = dia; }
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TutorOrientador getTutor() {
		return tutor;
	}

	public void setTutor(TutorOrientador tutor) {
		this.tutor = tutor;
	}

	public byte getDia() {
		return dia;
	}

	public void setDia(byte dia) {
		this.dia = dia;
	}

	public byte getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(byte horaInicio) {
		this.horaInicio = horaInicio;
	}

	public byte getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(byte horaFim) {
		this.horaFim = horaFim;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tutor == null) ? 0 : tutor.hashCode());
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
		final HorarioTutor other = (HorarioTutor) obj;
		if (dia != other.dia)
			return false;
		if (horaFim != other.horaFim)
			return false;
		if (horaInicio != other.horaInicio)
			return false;
		if (tutor == null) {
			if (other.tutor != null)
				return false;
		} else if (!tutor.equals(other.tutor))
			return false;
		else if (id != other.getId())
			return false;
		
		return true;
	}

	public String getDiaDesc() {
		return UFRNFunctions.descDiaSemana(dia);
	}
	
}
