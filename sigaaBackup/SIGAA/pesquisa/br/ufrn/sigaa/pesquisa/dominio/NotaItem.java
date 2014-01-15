/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;



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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que registra a nota dada por uma avaliador a um determinado 
 * item examinado na avaliação de projetos
 * 
 * @author ricardo
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "nota_item", schema = "pesquisa", uniqueConstraints = {})
public class NotaItem implements PersistDB, Comparable<NotaItem>{

	// Fields

	private int id;

	private ItemAvaliacao itemAvaliacao;

	private AvaliacaoProjeto avaliacaoProjeto;
	
	private AvaliacaoApresentacaoResumo avaliacaoApresentacaoResumo;
	/** Nota do membro da comissão, limitada a nota máxima do item avaliado */
	private Double nota;

	// Constructors

	/** default constructor */
	public NotaItem() {
	}

	/** minimal constructor */
	public NotaItem(int idNotaItem) {
		this.id = idNotaItem;
	}

	/** full constructor */
	public NotaItem(int idNotaItem, ItemAvaliacao itemAvaliacao,
			AvaliacaoProjeto avaliacaoProjeto, Double nota) {
		this.id = idNotaItem;
		this.itemAvaliacao = itemAvaliacao;
		this.avaliacaoProjeto = avaliacaoProjeto;
		this.nota = nota;
	}

	// Property accessors
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_nota_item", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idNotaItem) {
		this.id = idNotaItem;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_item_avaliacao", unique = false, nullable = true, insertable = true, updatable = true)
	public ItemAvaliacao getItemAvaliacao() {
		return this.itemAvaliacao;
	}

	public void setItemAvaliacao(ItemAvaliacao itemAvaliacao) {
		this.itemAvaliacao = itemAvaliacao;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_avaliacao_projeto", unique = false, nullable = true, insertable = true, updatable = true)
	public AvaliacaoProjeto getAvaliacaoProjeto() {
		return this.avaliacaoProjeto;
	}

	public void setAvaliacaoProjeto(AvaliacaoProjeto avaliacaoProjeto) {
		this.avaliacaoProjeto = avaliacaoProjeto;
	}

	@Column(name = "nota", unique = false, nullable = true, insertable = true, updatable = true, precision = 4, scale = 0)
	public Double getNota() {
		return this.nota;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}

	@Override
	public boolean equals(Object obj) {
		if (getId() == 0) {
			return false;
		} else {
			return EqualsUtil.testEquals(this, obj, "id");
		}
	}

	public int compareTo(NotaItem o) {
		return ((Integer) this.getItemAvaliacao().getOrdem()).compareTo(o.getItemAvaliacao().getOrdem());
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_avaliacao_apresentacao_resumo", unique = false, nullable = true, insertable = true, updatable = true)
	public AvaliacaoApresentacaoResumo getAvaliacaoApresentacaoResumo() {
		return avaliacaoApresentacaoResumo;
	}

	public void setAvaliacaoApresentacaoResumo(
			AvaliacaoApresentacaoResumo avaliacaoApresentacaoResumo) {
		this.avaliacaoApresentacaoResumo = avaliacaoApresentacaoResumo;
	}

}
