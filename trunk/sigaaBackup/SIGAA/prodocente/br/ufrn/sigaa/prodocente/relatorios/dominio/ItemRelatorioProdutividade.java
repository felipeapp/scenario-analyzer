/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Entidade que representa um item pontuável em um relatório de produtividade. 
 * Estes itens podem ser reutilizados para vários relatórios diferentes.
 *
 * @author eric
 */
@Entity
@Table(name = "item_relatorio_produtividade", schema = "prodocente")
public class ItemRelatorioProdutividade implements Validatable {

	/**
	 * id do item do relatório de produtividade
	 */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_item_relatorio_produtividade", nullable = false)
	private int id;

	/**
	 * descrição do item
	 */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	/**
	 * pontuação que o item vale
	 */
	@Column(name = "pontuacao")
	private Double pontuacao;

	/**
	 * mínima pontuação que o docente pode ter com produções deste item
	 */
	@Column(name = "pontuacao_minima")
	private Double pontuacaoMinima;

	/**
	 * máxima pontuação que o docente pode ter com produções deste item
	 */
	@Column(name = "pontuacao_maxima")
	private Double pontuacaoMaxima;

	/**
	 * tópico deste item
	 */
	@Column(name = "topico")
	private String topico;

	/**
	 * número tópico deste item
	 */
	@Column(name = "numero_topico")
	private int numeroTopico;

	/**
	 * Indica se este item do relatório é registrado via produção intelectual 
	 * ou em alguma outra atividade acadêmica como por exemplo horas de aula ou orientação de pós-graduação. 
	 * Nestes casos o docente ganha ponto do relatório de produtividade porém estas atividades não são registradas 
	 * no módulo de produção intelectual.
	 * 
	 */
	@Column(name = "producao_intelectual")
	private boolean producaoIntelectual = false;

	@OneToMany(mappedBy = "id")
	private Collection<GrupoItem> grupoItemCollection;

	/**
	 * Caminho completo da classe que implementa o mapeamento deste item 
	 * do relatório de produtividade para as produções dos docentes. 
	 */
	@Column(name = "producao_mapper")
	private String producaoMapper;
	
	/**
	 * atributo para controle na jsp
	 */
	@Transient
	private Boolean selecionado = new Boolean(false);

	/**
	 * coleção transiente de produções
	 */
	@Transient
	private Collection<ProducaoIntelectual> producoes;

	/** 
	 * Quantidade setada na busca para fazer a multiplicada
	 */
	@Transient
	private int qtd;

	/**
	 * Retorna o total de pontos deste item que o docente possui
	 * @return
	 */
	public double totalPontos() {
		if (producoes == null) {
			return 0;
		} else {
			double totalPontos = producoes.size() * pontuacao;
			if (totalPontos > pontuacaoMaxima) {
				return pontuacaoMaxima;
			} else {
				return pontuacao;
			}
		}
	}

	/** Creates a new instance of ItemRelatorioProdutividade */
	public ItemRelatorioProdutividade() {
	}

	/**
	 * Creates a new instance of ItemRelatorioProdutividade with the specified
	 * values.
	 *
	 * @param id
	 *            the id of the ItemRelatorioProdutividade
	 */
	public ItemRelatorioProdutividade(int idItemRelatorioProdutividade) {
		this.id = idItemRelatorioProdutividade;
	}

	/**
	 * Creates a new instance of ItemRelatorioProdutividade with the specified
	 * values.
	 *
	 * @param id
	 *            the id of the ItemRelatorioProdutividade
	 * @param descricao
	 *            the descricao of the ItemRelatorioProdutividade
	 */
	public ItemRelatorioProdutividade(int idItemRelatorioProdutividade,
			String descricao) {
		this.id = idItemRelatorioProdutividade;
		this.descricao = descricao;
	}

	/**
	 * Gets the id of this ItemRelatorioProdutividade.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id of this ItemRelatorioProdutividade to the specified value.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(int idItemRelatorioProdutividade) {
		this.id = idItemRelatorioProdutividade;
	}

	/**
	 * Gets the descricao of this ItemRelatorioProdutividade.
	 *
	 * @return the descricao
	 */
	public String getDescricao() {
		return this.descricao;
	}

	/**
	 * Sets the descricao of this ItemRelatorioProdutividade to the specified
	 * value.
	 *
	 * @param descricao
	 *            the new descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * Gets the pontuacao of this ItemRelatorioProdutividade.
	 *
	 * @return the pontuacao
	 */
	public Double getPontuacao() {
		return this.pontuacao;
	}

	/**
	 * Sets the pontuacao of this ItemRelatorioProdutividade to the specified
	 * value.
	 *
	 * @param pontuacao
	 *            the new pontuacao
	 */
	public void setPontuacao(Double pontuacao) {
		this.pontuacao = pontuacao;
	}

	/**
	 * Gets the pontuacaoMinima of this ItemRelatorioProdutividade.
	 *
	 * @return the pontuacaoMinima
	 */
	public Double getPontuacaoMinima() {
		return this.pontuacaoMinima;
	}

	/**
	 * Sets the pontuacaoMinima of this ItemRelatorioProdutividade to the
	 * specified value.
	 *
	 * @param pontuacaoMinima
	 *            the new pontuacaoMinima
	 */
	public void setPontuacaoMinima(Double pontuacaoMinima) {
		this.pontuacaoMinima = pontuacaoMinima;
	}

	/**
	 * Gets the pontuacaoMaxima of this ItemRelatorioProdutividade.
	 *
	 * @return the pontuacaoMaxima
	 */
	public Double getPontuacaoMaxima() {
		return this.pontuacaoMaxima;
	}

	/**
	 * Sets the pontuacaoMaxima of this ItemRelatorioProdutividade to the
	 * specified value.
	 *
	 * @param pontuacaoMaxima
	 *            the new pontuacaoMaxima
	 */
	public void setPontuacaoMaxima(Double pontuacaoMaxima) {
		this.pontuacaoMaxima = pontuacaoMaxima;
	}

	/**
	 * Gets the topico of this ItemRelatorioProdutividade.
	 *
	 * @return the topico
	 */
	public String getTopico() {
		return this.topico;
	}

	/**
	 * Sets the topico of this ItemRelatorioProdutividade to the specified
	 * value.
	 *
	 * @param topico
	 *            the new topico
	 */
	public void setTopico(String topico) {
		this.topico = topico;
	}

	/**
	 * Gets the numeroTopico of this ItemRelatorioProdutividade.
	 *
	 * @return the numeroTopico
	 */
	public int getNumeroTopico() {
		return this.numeroTopico;
	}

	/**
	 * Sets the numeroTopico of this ItemRelatorioProdutividade to the specified
	 * value.
	 *
	 * @param numeroTopico
	 *            the new numeroTopico
	 */
	public void setNumeroTopico(int numeroTopico) {
		this.numeroTopico = numeroTopico;
	}

	/**
	 * Gets the grupoItemCollection of this ItemRelatorioProdutividade.
	 *
	 * @return the grupoItemCollection
	 */
	public Collection<GrupoItem> getGrupoItemCollection() {
		return this.grupoItemCollection;
	}

	/**
	 * Sets the grupoItemCollection of this ItemRelatorioProdutividade to the
	 * specified value.
	 *
	 * @param grupoItemCollection
	 *            the new grupoItemCollection
	 */
	public void setGrupoItemCollection(Collection<GrupoItem> grupoItemCollection) {
		this.grupoItemCollection = grupoItemCollection;
	}

	/**
	 * Determines whether another object is equal to this
	 * ItemRelatorioProdutividade. The result is <code>true</code> if and only
	 * if the argument is not null and is a ItemRelatorioProdutividade object
	 * that has the same id field values as this object.
	 *
	 * @param object
	 *            the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the argument;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		// not set
		if (!(object instanceof ItemRelatorioProdutividade)) {
			return false;
		}
		ItemRelatorioProdutividade other = (ItemRelatorioProdutividade) object;
		if (this.id != other.id)
			return false;
		return true;
	}

	/**
	 * Returns a string representation of the object. This implementation
	 * constructs that representation based on the id fields.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return "br.ufrn.sigaa.prodocente.relatorio.dominio.ItemRelatorioProdutividade[id="
				+ id + "]";
	}

	public ListaMensagens validate() {
		return null;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isProducaoIntelectual() {
		return producaoIntelectual;
	}

	public void setProducaoIntelectual(boolean producaoIntelectual) {
		this.producaoIntelectual = producaoIntelectual;
	}

	public int getQtd() {
		return qtd;
	}

	public void setQtd(int qtd) {
		this.qtd = qtd;
	}

	public String getProducaoMapper() {
		return producaoMapper;
	}

	public void setProducaoMapper(String producaoMapper) {
		this.producaoMapper = producaoMapper;
	}

}