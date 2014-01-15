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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;

/*******************************************************************************
 * <p>
 * Representa um grupo de pesquisa ao qual uma ação de extensão está vinculada.
 * Propostas de ações de tipo projeto normalmente tem vínculo com algum grupo de
 * pesquisa já existente.
 * </p>
 * 
 * @author Victor Hugo
 * @author Gleydson
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "atividade_grupo_pesquisa")
public class AtividadeGrupoPesquisa implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_atividade_grupo_pesquisa", nullable = false)
	private int id;

	//Representa um grupo de pesquisa
	@JoinColumn(name = "id_grupo_pesquisa", referencedColumnName = "id_grupo_pesquisa")
	@ManyToOne
	private GrupoPesquisa grupoPesquisa;

	//Atividade associada a este grupo de pesquisa.
	@JoinColumn(name = "id_atividade", referencedColumnName = "id_atividade")
	@ManyToOne
	private AtividadeExtensao atividade;

	/** Creates a new instance of AtividadeGrupoPesquisa */
	public AtividadeGrupoPesquisa() {
	}

	public AtividadeGrupoPesquisa(int id) {
		this.id = id;
	}

	/**
	 * @return Returns the grupoPesquisa.
	 */
	public GrupoPesquisa getGrupoPesquisa() {
		return grupoPesquisa;
	}

	/**
	 * @param grupoPesquisa
	 *            The grupoPesquisa to set.
	 */
	public void setGrupoPesquisa(GrupoPesquisa grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
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
		return "br.ufrn.sigaa.extensao.dominio.AtividadeGrupoPesquisa[id=" + id
		+ "]";
	}

	/**
	 * @see br.ufrn.arq.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		return null;
	}

}
