/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 02/03/2011
 * 
 */
package br.ufrn.sigaa.assistencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe que representa o status do cartao benefício do discente
 *
 * @author geyson
 */
@Entity
@Table( name="status_cartao_beneficio", schema="sae")
public class StatusCartaoBeneficio implements PersistDB {

	/** ativo */
	public static final int ATIVO 		= 1;
	/** encerrado */
	public static final int ENCERRADO 	= 2;
	/** perdido */
	public static final int PERDIDO 	= 3;
	
	
	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_status_cartao_beneficio", unique = true, nullable = false)
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Denominação do status */
	@Column( name ="descricao")
	private String descricao;

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
