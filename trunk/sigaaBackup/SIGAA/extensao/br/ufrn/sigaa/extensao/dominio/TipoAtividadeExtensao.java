/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2007
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;

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
 * Representa um tipo de ação de extensão. <br>
 * Pode ser: Programa, Projeto, Curso, Evento, Prestação de Serviços ou Produto.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_atividade_extensao")
public class TipoAtividadeExtensao implements Validatable {

	// constantes tipos de atividade
	public static final int PROGRAMA = 1;
	public static final int PROJETO = 2;
	public static final int CURSO = 3;
	public static final int EVENTO = 4;
	public static final int PRESTACAO_SERVICO = 5;
	public static final int PRODUTO = 6;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_atividade_extensao", nullable = false)
	private int id;

	//Descrição textual do tipo de atividade.
	@Column(name = "descricao", nullable = false)
	private String descricao;

	/**
	 * Ao remover as produções e ações, as mesmas não serão removidas da base de
	 * dados, apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

	public TipoAtividadeExtensao() {
	}

	public TipoAtividadeExtensao(int id) {
		this.id = id;
	}

	public TipoAtividadeExtensao(int id, String descricao) {
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

	/**
	 * Ao remover as ações, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE.
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo() {
		return this.ativo;
	}

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da
	 * base de dados, apenas o campo ativo será marcado como FALSE
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String toString() {
		if (descricao == null)
			return null;
		return descricao.toUpperCase();
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

	public static Collection<Integer> getAllTiposAtividadesExtensao() {
		Collection<Integer> tipos = new ArrayList<Integer>();
		tipos.add(PROGRAMA);
		tipos.add(PROJETO);
		tipos.add(CURSO);
		tipos.add(EVENTO);
		tipos.add(PRESTACAO_SERVICO);
		tipos.add(PRODUTO);
		return tipos;
	}
	
	/**
	 * Determines whether another object is equal to this TipoAtividadeExtensao.
	 * The result is <code>true</code> if and only if the argument is not null
	 * and is a TipoAtividadeExtensao object that has the same id field values
	 * as this object.
	 * 
	 * @param object
	 *            the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the argument;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TipoAtividadeExtensao)) {
			return false;
		}
		TipoAtividadeExtensao other = (TipoAtividadeExtensao) object;
		if (this.id != other.id || this.id == 0)
			return false;
		return true;
	}

	public boolean isProjeto() {
		return this.id == PROJETO;
	}
	
	public boolean isProduto() {
		return this.id == PRODUTO;
	}
	
	public boolean isPrograma() {
		return this.id == PROGRAMA;
	}
	
	public boolean isCurso() {
		return this.id == CURSO;
	}
	
	public boolean isEvento() {
		return this.id == EVENTO;
	}
	
}
