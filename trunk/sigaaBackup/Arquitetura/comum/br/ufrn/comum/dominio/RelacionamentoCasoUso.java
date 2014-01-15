/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.comum.dominio;

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
 * Representa o relacionamento entre dois casos de uso e o tipo
 * de relacionamento entre eles.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="relacionamento_caso_uso", schema="comum")
public class RelacionamentoCasoUso implements PersistDB {

	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="comum.relacionamento_caso_uso_seq")})
	private int id;
	
	/** Caso de uso origem do relacionamento, ou seja, de onde parte o relacionamento */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_origem")
	private CasoUso origem;
	
	/** Caso de uso destino do relacionamento, ou seja, para onde vai o relacionamento */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_destino")
	private CasoUso destino;
	
	/** Tipo do relacionamento entre os dois casos de uso */
	@ManyToOne @JoinColumn(name="id_tipo_relacionamento_caso_uso")
	private TipoRelacionamentoCasoUso tipo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CasoUso getOrigem() {
		return origem;
	}

	public void setOrigem(CasoUso origem) {
		this.origem = origem;
	}

	public CasoUso getDestino() {
		return destino;
	}

	public void setDestino(CasoUso destino) {
		this.destino = destino;
	}

	public TipoRelacionamentoCasoUso getTipo() {
		return tipo;
	}

	public void setTipo(TipoRelacionamentoCasoUso tipo) {
		this.tipo = tipo;
	}

}
