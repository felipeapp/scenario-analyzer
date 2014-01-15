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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade responsável pela definição do tipo de documento legal.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_documento_legal", schema = "ensino", uniqueConstraints = {})
public class TipoDocumentoLegal implements Validatable {

	// Fields
	
	/** Chave primária */
	private int id;

	/** Descrição do Documento Legal */
	private String descricao;

	// Constructors

	/** default constructor */
	public TipoDocumentoLegal() {
	}

	/** default minimal constructor */
	public TipoDocumentoLegal(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoDocumentoLegal(int idTipoDocumentoLegal, String descricao) {
		this.id = idTipoDocumentoLegal;
		this.descricao = descricao;
	}


	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_documento_legal", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoDocumentoLegal) {
		this.id = idTipoDocumentoLegal;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
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
		
		return null;
	}

}
