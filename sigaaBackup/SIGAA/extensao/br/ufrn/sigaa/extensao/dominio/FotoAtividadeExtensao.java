/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/06/2008
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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Fotos das Ações de Extensão. O coordenador da ação pode adicionar fotos que
 * podem ser utilizadas tanto na analise da proposta quanto na divulgação da
 * ação para a comunidade externa.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "foto_atividade", schema = "extensao", uniqueConstraints = {})
public class FotoAtividadeExtensao implements Validatable {

	@Id
	@Column(name = "id_foto_atividade")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	//Descrição da foto.
	@Column(name = "descricao")
	private String descricao;

	//Id da foto Original
	@Column(name = "id_foto_original")
	private Integer idFotoOriginal;

	//Id da mini foto
	@Column(name = "id_foto_mini")
	private Integer idFotoMini;

	//Atividade na qual a ação participa.
	@ManyToOne
	@JoinColumn(name = "id_atividade")
	private AtividadeExtensao atividade;

	//Indica se a foto esta ativa no sistema.
	@Column(name = "ativo")
	private boolean ativo;

	/** default constructor */
	public FotoAtividadeExtensao() {
	}

	/**
	 * Legenda da foto
	 * 
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * id da foto em tamanho Original
	 * 
	 * @return
	 */
	public Integer getIdFotoOriginal() {
		return idFotoOriginal;
	}

	public void setIdFotoOriginal(Integer idFotoOriginal) {
		this.idFotoOriginal = idFotoOriginal;
	}

	/**
	 * id da foto em tamanho reduzido utilizado para exibir varias fotos em uma
	 * mesma pagina web
	 * 
	 */
	public Integer getIdFotoMini() {
		return idFotoMini;
	}

	public void setIdFotoMini(Integer idFotoMini) {
		this.idFotoMini = idFotoMini;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

	/**
	 * Ação de extensão das fotos
	 * 
	 * @return
	 */
	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "idFotoNormal");
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
