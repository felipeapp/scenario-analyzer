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
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Representa os tipos de produtos possíveis. Utilizado na composição da classe
 * ProdutoExtesao. <br>
 * Exemplo: Jornal, Livro, Manual, Revista, etc.
 * </p>
 * 
 * @author Victor Hugo
 * @author Gleydson
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_produto")
public class TipoProduto implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_produto", nullable = false)
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	private boolean ativo = true;

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Creates a new instance of TipoProduto */
	public TipoProduto() {
	}

	public TipoProduto(int id) {
		this.id = id;
	}

	public TipoProduto(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String toString() {
		return "br.ufrn.sigaa.extensao.dominio.TipoProduto[id=" + id + "]";
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}
