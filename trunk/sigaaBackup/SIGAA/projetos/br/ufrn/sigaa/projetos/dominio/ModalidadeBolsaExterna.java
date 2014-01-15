/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;


/**
 * Entidade que representa a Modalidade de Bolsas. (REUNI, CNPq...)
 * @author Arlindo Rodrigues
 */
@Entity
@Table(name = "modalidade_bolsa_externa", schema = "projetos")
public class ModalidadeBolsaExterna implements PersistDB {
	/** Modalidade da bolsa referente ao REUNI */
	public static final int REUNI = 3;
	
	/** Chave primária */
	private int id;
	/** Descrição da modalidade da bolsa */
	private String descricao;
	
	/** Carga Horária Mínima */	
	private int chMin;
	
	/** Carga Horária Máxima */	
	private int chMax;

	
	// Constructors
	/** default constructor */
	public ModalidadeBolsaExterna() {
	}

	/** minimal constructor */
	public ModalidadeBolsaExterna(int idModalidadeBolsaExterna) {
		this.id = idModalidadeBolsaExterna;
	}

	/** full constructor */
	public ModalidadeBolsaExterna(int idModalidadeBolsaExterna,
			String descricao){
		this.id = idModalidadeBolsaExterna;
		this.descricao = descricao;
	}

	// Property accessors
	/**
	 * Chave primária da indicação.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_modalidade_bolsa_externa")
	public int getId() {
		return this.id;
	}

	public void setId(int idModalidadeBolsaExterna) {
		this.id = idModalidadeBolsaExterna;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Column(name = "ch_min")
	public int getChMin() {
		return chMin;
	}

	public void setChMin(int chMin) {
		this.chMin = chMin;
	}

	@Column(name = "ch_max")
	public int getChMax() {
		return chMax;
	}

	public void setChMax(int chMax) {
		this.chMax = chMax;
	}
}
