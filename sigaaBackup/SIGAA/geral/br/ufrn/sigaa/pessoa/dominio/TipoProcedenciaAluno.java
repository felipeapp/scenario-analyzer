/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Set;

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
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;

/**
 * Entidade Respons�vel pelo gerenciamento dos Tipo Proced�ncia dos Alunos, se � da pr�pria
 * Institui��o de Ensino, se � de outra Institui��o de Ensino ou se n�o foi informado.
 */
@Entity
@Table(name = "tipo_procedencia_aluno", schema = "lato_sensu", uniqueConstraints = {})
public class TipoProcedenciaAluno implements Validatable {

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoProcedenciaAluno() {
	}

	/** default minimal constructor */
	public TipoProcedenciaAluno(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoProcedenciaAluno(int idTipoProcedenciaAluno, String descricao) {
		this.id = idTipoProcedenciaAluno;
		this.descricao = descricao;
	}

	/** full constructor */
	public TipoProcedenciaAluno(int idTipoProcedenciaAluno, String descricao,
			Set<DiscenteLato> discenteLatos) {
		this.id = idTipoProcedenciaAluno;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_procedencia_aluno", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoProcedenciaAluno) {
		this.id = idTipoProcedenciaAluno;
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
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		return lista;
	}

}
