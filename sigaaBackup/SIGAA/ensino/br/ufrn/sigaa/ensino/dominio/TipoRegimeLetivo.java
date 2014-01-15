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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Tipo de regime letivo (divisões de períodos) com o qual um curso ou matriz 
 * curricular podem trabalhar, regime letivo no entanto é a forma na qual se irá
 * trabalhar no ano, podendo ser no entanto Anual, Semestral, Bimestral, dentre outros.
 */
@Entity
@Table(name = "tipo_regime_letivo", schema = "ensino", uniqueConstraints = {})
public class TipoRegimeLetivo implements Validatable {

	public static final int SEMESTRAL = 2;

	private int id;

	private String descricao;

	/** default constructor */
	public TipoRegimeLetivo() {
	}

	/** default minimal constructor */
	public TipoRegimeLetivo(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoRegimeLetivo(int idTipoRegimeLetivo, String descricao) {
		this.id = idTipoRegimeLetivo;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_regime_letivo", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoRegimeLetivo) {
		this.id = idTipoRegimeLetivo;
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

	/** Utilizado para verifica se os campos obrigatório foram informados. */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);
		return lista;
	}
}