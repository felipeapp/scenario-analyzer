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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade responsável pelo gerenciamento dos Tipos de atividade acadêmica 
 * específica complementar, que podem ser um estágio Supervisionado por exemplo.
 * 
 * @author 
 */
@Entity
@Table(name = "tipo_atividade_complementar", schema = "ensino", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class TipoAtividadeComplementar implements Validatable {

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoAtividadeComplementar() {
	}

	/** default minimal constructor */
	public TipoAtividadeComplementar(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoAtividadeComplementar(int idTipoAtividadeComplementar,
			String descricao) {
		this.id = idTipoAtividadeComplementar;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_atividade_complementar", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoAtividadeComplementar) {
		this.id = idTipoAtividadeComplementar;
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

	/** Utilizado para verifica se os campos obrigatório foram informados.  */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);
		return lista;
	}

}
