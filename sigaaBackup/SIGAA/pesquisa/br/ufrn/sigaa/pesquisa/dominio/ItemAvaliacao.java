/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que representa os itens da avaliação de projetos de pesquisa
 * 
 * @author ricardo
 */
@SuppressWarnings("serial")
@Entity()
@Table(name = "item_avaliacao", schema = "pesquisa", uniqueConstraints = {})
public class ItemAvaliacao implements PersistDB, Validatable {

	/** Constantes dos tipos de itens de avaliação */
	public static final int AVALIACAO_PROJETO = 1;
	public static final int AVALIACAO_RESUMO = 2;
	
	// Fields

	/** chave primária */
	private int id;

	/** Descrição para o item de avaliação */
	private String descricao;

	/** Data de criação do Item de Avaliação */
	private Date dataCriacao;

	/** Data de desativação do Item de Avaliação */
	private Date dataDesativacao;

	/** Peso para um determinado Item de Avaliação */
	private int peso = 1;

	/** Ordem do Item de Avaliação */
	private int ordem;
	
	/** Tipo do Item de Avaliação */
	private int tipo = 1;

	// Constructors

	/** default constructor */
	public ItemAvaliacao() {
	}

	/** minimal constructor */
	public ItemAvaliacao(int idItemAvaliacao) {
		this.id = idItemAvaliacao;
	}

	/** full constructor */
	public ItemAvaliacao(int idItemAvaliacao, String descricao,
			Date dataCriacao, Date dataDesativacao) {
		this.id = idItemAvaliacao;
		this.descricao = descricao;
		this.dataCriacao = dataCriacao;
		this.dataDesativacao = dataDesativacao;
	}

	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if (getDescricao() == null || "".equals(getDescricao().trim())) {
			mensagens.addErro("Informe uma descrição para o item de avaliação");
		}
		if (getPeso() <= 0) {
			mensagens.addErro("O peso não deve ser inferior a 1");
		}
		return mensagens;
	}

	/** Geração da chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_item_avaliacao", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idItemAvaliacao) {
		this.id = idItemAvaliacao;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 300)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_criacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_desativacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataDesativacao() {
		return this.dataDesativacao;
	}

	public void setDataDesativacao(Date dataDesativacao) {
		this.dataDesativacao = dataDesativacao;
	}

	@Column(name = "peso", unique = false, nullable = false, insertable = true, updatable = true)
	public int getPeso() {
		return this.peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	@Override
	public boolean equals(Object obj) {
		if (getId() == 0) {
			return false;
		} else {
			return EqualsUtil.testEquals(this, obj, "id");
		}
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Column(name = "tipo", unique = false, nullable = true, insertable = true, updatable = true)
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

}
