/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.seguranca.log.CriadoEm;

/**
 * Entidade que agrupa os discente convocados.
 * 
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(schema="ensino_rede", name = "convocacao_discente_associado")
public class ConvocacaoDiscenteAssociado {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_convocacao_discente_associado", nullable = false)
	private int id;
	
	/** Descricao da Convocacao. */ 
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	/** Data da convocação. */
	@Column(name = "data", nullable = false)
	@CriadoEm
	private Date data;

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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return descricao;
	}
}
