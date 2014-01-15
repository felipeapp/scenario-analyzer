/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/11/2006
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

/*******************************************************************************
 * <p>
 * Uma ação de extensão do tipo produto. <br/>
 * 
 * Produtos são publicações e outros tipos de produção acadêmica que
 * instrumentalizam ou que resultam de atividades de ensino, pesquisa e
 * extensão, tais como: livros, revistas, vídeos, filmes, cartilhas, softwares e
 * CDs. <br/>
 * 
 * Os registros de produção e publicação poderão ter uma classificação
 * detalhada, a critério de cada universidade.
 * </p>
 * 
 * @author Gleydson
 * @author Victor Hugo
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "produto")
public class ProdutoExtensao implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_produto", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_produto")
	private TipoProduto tipoProduto = new TipoProduto();

	@Column(name = "tiragem")
	private Integer tiragem;

	@Column(name = "produto_gerado")
	private String produtoGerado;

	public ProdutoExtensao() {
		this.setTiragem(0);
	}

	public ProdutoExtensao(int id) {
		this.setId(id);
		this.setTiragem(0);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TipoProduto getTipoProduto() {
		return this.tipoProduto;
	}

	public void setTipoProduto(TipoProduto tipoProduto) {
		this.tipoProduto = tipoProduto;
	}

	public String toString() {
		return tipoProduto != null ? tipoProduto.getDescricao() : "";
	}

	/**
	 * Informa a tiragem (total de exemplares produzidos), campo solicitado no
	 * relatório anual submetido ao ministério da educação.
	 * 
	 * @return
	 */
	public Integer getTiragem() {
		return tiragem;
	}

	public void setTiragem(Integer tiragem) {
		this.tiragem = tiragem;
	}

	public ListaMensagens validate() {
		return null;
	}

	public String getProdutoGerado() {
		return produtoGerado;
	}

	public void setProdutoGerado(String produtoGerado) {
		this.produtoGerado = produtoGerado;
	}

}