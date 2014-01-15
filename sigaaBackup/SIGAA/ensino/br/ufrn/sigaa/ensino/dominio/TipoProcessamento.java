/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 13/09/2006
*/
package br.ufrn.sigaa.ensino.dominio;

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
 * Entidade responsável pela definição do tipo de processamento;
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_processamento", schema = "ensino", uniqueConstraints = {})
public class TipoProcessamento implements PersistDB {

	// Fields    

	/** Chave primária */
	private int id;

	/** Desrição do tipo de processamento */
	private String descricao;

	// Constructors

	/** default constructor */
	public TipoProcessamento() {
	}

	/** minimal constructor */
	public TipoProcessamento(int idTipoProcessamento) {
		this.id = idTipoProcessamento;
	}

	/** full constructor */
	public TipoProcessamento(int idTipoProcessamento, String descricao) {
		this.id = idTipoProcessamento;
		this.descricao = descricao;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_processamento", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoProcessamento) {
		this.id = idTipoProcessamento;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 40)
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

}
