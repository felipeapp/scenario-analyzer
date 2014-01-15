/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/05/2011
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio;

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
 * Entidade que representa as frequências das atividades de docência assistida.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="frequencia_atividade_docencia_assistida", schema="stricto_sensu")
public class FrequenciaAtividadeDocenciaAssistida implements PersistDB {
	
	/**
	 * Chave primária
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_frequencia_atividade_docencia_assistida")
	private int id;		
	
	/** Descrição da frequência */
	private String descricao;
	
	/** Número de semanas que contém na frequência da atividade. 
	 * Utilizado para realizar o cálculo de CH máx. e mín. */
	@Column(name = "quantidade_semanas")
	private int quantSemanas;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
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

	public int getQuantSemanas() {
		return quantSemanas;
	}

	public void setQuantSemanas(int quantSemanas) {
		this.quantSemanas = quantSemanas;
	}
}
