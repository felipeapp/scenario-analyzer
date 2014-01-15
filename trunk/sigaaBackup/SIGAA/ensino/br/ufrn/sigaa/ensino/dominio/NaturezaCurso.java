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
 * Essa Entidade é responsável pelo gerenciamento de Natureza de um curso, sendo responsável 
 * pela informação se o Curso é de Graduação, Sequencial de formação específica ou 
 * Sequencial de complementação de estudos, Outros.
 *   
 */
@Entity
@Table(name = "natureza_curso", schema = "ensino", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NaturezaCurso implements Validatable {

	public static final int GRADUACAO = 3;

	public static final int OUTRAS = 4;

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public NaturezaCurso() {
	}

	/** default minimal constructor */
	public NaturezaCurso(int id) {
		this.id = id;
	}

	/** full constructor */
	public NaturezaCurso(int idNaturezaCurso, String descricao) {
		this.id = idNaturezaCurso;
		this.descricao = descricao;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_natureza_curso", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idNaturezaCurso) {
		this.id = idNaturezaCurso;
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
