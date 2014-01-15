/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que define a forma como o diploma � emitido para um curso ou matriz curricular
 */
@Entity
@Table(name = "situacao_diploma", schema = "ensino", uniqueConstraints = {})
public class SituacaoDiploma implements Validatable {

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public SituacaoDiploma() {
	}

	/** default minimal constructor */
	public SituacaoDiploma(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public SituacaoDiploma(int idSituacaoDiploma, String descricao) {
		this.id = idSituacaoDiploma;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_situacao_diploma", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idSituacaoDiploma) {
		this.id = idSituacaoDiploma;
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
	
	/** Utilizado para verifica se os campos obrigat�rio foram informados.  */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descri��o", lista);
		return lista;
	}

}
