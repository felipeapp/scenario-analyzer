package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Representa os Possíveis turnos de trabalho para uma determinada unidade de trabalho.
 * @author Rafael Moreira
 *
 */
@Entity
@Table(name = "tipo_turno", schema="comum")
public class TipoTurno implements PersistDB {

	public static final int MANHA             = 1;
	public static final int TARDE             = 2;
	public static final int NOITE             = 3;
	public static final int MANHA_TARDE       = 4;
	public static final int TARDE_NOITE       = 5;
	public static final int MANHA_TARDE_NOITE = 6;
	
	/** Identificador */
	@Id
    @GeneratedValue(generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    				  parameters = {@Parameter(name = "sequence_name", value = "comum.tipo_turno_seq")})
	@Column(name = "id_tipo_turno", nullable = false)
	private int id;
	
	/** Denominação do tipo de turno */
	private String denominacao;
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	
	
}
