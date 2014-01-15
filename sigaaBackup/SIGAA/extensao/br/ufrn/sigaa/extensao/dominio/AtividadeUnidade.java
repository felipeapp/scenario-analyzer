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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Unidade;

/*******************************************************************************
 * Relaciona uma ação de extensão à diversas unidades da instituição. Uma ação de
 * extensão pode estar vinculada com várias unidades além da unidade proponente.
 * 
 * @author Victor Hugo
 * @author Gleydson
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "atividade_unidade")
public class AtividadeUnidade implements Validatable {

	//Chave primária
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_atividade_unidade", nullable = false)
	private int id;

	
	//Unidade associada a atividade 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade unidade;

	
	@ManyToOne
	@JoinColumn(name = "id_atividade")
	private br.ufrn.sigaa.extensao.dominio.AtividadeExtensao atividade;

	/** Construtor padrão */
	public AtividadeUnidade() {
	}

	public AtividadeUnidade(Integer id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return Returns the unidade.
	 */
	public Unidade getUnidade() {
		return unidade;
	}

	/**
	 * @param unidade
	 *            The unidade to set.
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public br.ufrn.sigaa.extensao.dominio.AtividadeExtensao getAtividade() {
		return this.atividade;
	}

	public void setAtividade(
			br.ufrn.sigaa.extensao.dominio.AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public String toString() {
		return "br.ufrn.sigaa.extensao.dominio.AtividadeUnidade[id=" + id + "]";
	}

	public ListaMensagens validate() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "unidade.id", "atividade.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, unidade.getId(), atividade.getId());
	}

}
