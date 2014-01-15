/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 13/09/2006
*/
package br.ufrn.sigaa.ensino.dominio;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Entidade responsável pela definição do tipo de veiculação EAD (Ensino a Distância).
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_veiculacao_ead", schema = "ensino", uniqueConstraints = {})
public class TipoVeiculacaoEad implements Validatable {

	// Fields

	/** Chave primária */
	private int id;

	/** Descrição do tipo de veiculação ead */
	private String descricao;

	// Constructors

	/** default constructor */
	public TipoVeiculacaoEad() {
	}

	/** default minimal constructor */
	public TipoVeiculacaoEad(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoVeiculacaoEad(int idTipoVeiculacaoEad, String descricao) {
		this.id = idTipoVeiculacaoEad;
		this.descricao = descricao;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_veiculacao_ead", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoVeiculacaoEad) {
		this.id = idTipoVeiculacaoEad;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true)
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

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}
