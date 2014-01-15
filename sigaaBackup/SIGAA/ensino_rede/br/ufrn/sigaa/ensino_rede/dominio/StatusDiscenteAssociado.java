/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/08/2013
 * 
 */
package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Descreve a situação do curso associado na rede de ensino. Ex.: ativo, cancelado, trancado, etc.
 * @author Henrique André
 *
 */
@Entity
@Table(schema="ensino_rede", name = "status_discente_associado")
public class StatusDiscenteAssociado implements PersistDB  {
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_status_discente_associado", nullable = false)
	private int id;
	
	/** Descrição do status do discente. */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	/**
	 * Construtor parametrizado. 
	 */
	public StatusDiscenteAssociado() {
	}
	
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
}
