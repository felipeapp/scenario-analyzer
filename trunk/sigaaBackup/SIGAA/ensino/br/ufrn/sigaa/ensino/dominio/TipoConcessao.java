/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;


/**
 * Tipo de concessão feita para o aproveitamento de um componente curricular
 * no histórico escolar de um discente
 */
@Entity
@Table(name = "tipo_concessao", schema = "ensino", uniqueConstraints = {})
public class TipoConcessao implements PersistDB {

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoConcessao() {
	}

	/** minimal constructor */
	public TipoConcessao(int idTipoConcessao) {
		this.id = idTipoConcessao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_concessao", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoConcessao) {
		this.id = idTipoConcessao;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 0)
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
