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
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade responsável pela definição do tipo de regime do aluno;
 * como por exemplo:
 * 		Externo, Interno ou Semi-Interno.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_regime_aluno", schema = "ensino", uniqueConstraints = {})
public class TipoRegimeAluno implements Validatable {

	/** Constante do tipo de regime do aluno */
	public static final int EXTERNO = 1;

	/** Chave primária */
	private int id;

	/** Descrição do tipo de regime do aluno. */
	private String descricao;

	// Constructors

	/** default constructor */
	public TipoRegimeAluno() {
	}

	/** default minimal constructor */
	public TipoRegimeAluno(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoRegimeAluno(int idTipoRegimeAluno, String descricao) {
		this.id = idTipoRegimeAluno;
		this.descricao = descricao;
	}


	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_regime_aluno", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoRegimeAluno) {
		this.id = idTipoRegimeAluno;
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
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}
