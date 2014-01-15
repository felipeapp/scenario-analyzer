/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/*******************************************************************************
 * Relaciona uma Ação de Extensão com todas as suas entidades financiadoras.
 * 
 * @author Victor Hugo
 * @author Gleydson
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "atividade_entidade_financiadora")
public class AtividadeEntidadeFinanciadora implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_atividade_entidade_financiadora", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entidade_financiadora", nullable = false)
	private EntidadeFinanciadora entidadeFinanciadora;

	@ManyToOne
	@JoinColumn(name = "id_atividade", referencedColumnName = "id_atividade")
	private AtividadeExtensao atividade;

	/** Creates a new instance of AtividadeEntidadeFinanciadora */
	public AtividadeEntidadeFinanciadora() {
	}

	public AtividadeEntidadeFinanciadora(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AtividadeExtensao getAtividade() {
		return this.atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public String toString() {
		if (entidadeFinanciadora != null)
			return entidadeFinanciadora.getNome();

		return null;
	}

	public ListaMensagens validate() {
		return null;
	}

	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return entidadeFinanciadora;
	}

	public void setEntidadeFinanciadora(
			EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

}
