/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/11/2011
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Representa um item que pode ser utilizado para compor um Formulário
 * de Evolução da Criança
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="item_infantil", schema="infantil", uniqueConstraints={})
public class ItemInfantil implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_item_infantil", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Descrição textual do item. */
	private String descricao;
	
	/** Indica se deve ser informada uma observação textual para o item
	 * durante o preenchimento do Formulário de Evolução da Criança. */
	@Column(name = "tem_observacao")
	private boolean temObservacao;
	
	/** Indica a forma de avaliação do item. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="forma_avaliacao")
	private TipoFormaAvaliacao formaAvaliacao = new TipoFormaAvaliacao();

	/** Serve pra indicar se o Item Infantil está ativo para o preenchimento */
	@CampoAtivo
	private boolean ativo;
	
	/** Construtor padrão. */
	public ItemInfantil() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isTemObservacao() {
		return temObservacao;
	}

	public void setTemObservacao(boolean temObservacao) {
		this.temObservacao = temObservacao;
	}

	public TipoFormaAvaliacao getFormaAvaliacao() {
		return formaAvaliacao;
	}

	public void setFormaAvaliacao(TipoFormaAvaliacao formaAvaliacao) {
		this.formaAvaliacao = formaAvaliacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

}