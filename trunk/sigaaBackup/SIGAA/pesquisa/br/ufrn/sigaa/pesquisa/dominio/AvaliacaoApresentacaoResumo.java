/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/10/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Collection;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Representa uma avaliação de apresentação de resumo (painel) feita no congresso de
 * iniciação científica por um avaliador de apresentação
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "avaliacao_apresentacao_resumo", schema = "pesquisa", uniqueConstraints = {})
public class AvaliacaoApresentacaoResumo implements Validatable {

	@Id@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_avaliacao_apresentacao_resumo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_resumo")
	private ResumoCongresso resumo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_avaliador")
	private AvaliadorCIC avaliador;
	
	private String observacoes;
	
	private Double media;
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "avaliacaoApresentacaoResumo" )
	private Collection<NotaItem> notasItens = new TreeSet<NotaItem>();
	
	/** Atributo transiente usado na distribuição dos resumos */
	@Transient
	private boolean selecionado;
	
	/** Atributo transiente usado na distribuição dos resumos */
	@Column(name = "selecionado_apresentacao_oral")
	private boolean selecionadoApresentacaoOral;

	public AvaliacaoApresentacaoResumo() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ResumoCongresso getResumo() {
		return resumo;
	}

	public void setResumo(ResumoCongresso resumo) {
		this.resumo = resumo;
	}

	public AvaliadorCIC getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(AvaliadorCIC avaliador) {
		this.avaliador = avaliador;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(avaliador, "Avaliador", erros);
		ValidatorUtil.validateRequired(resumo, "Resumo", erros);
		return erros;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Double getMedia() {
		return media;
	}

	public void setMedia(Double media) {
		this.media = media;
	}

	public Collection<NotaItem> getNotasItens() {
		return notasItens;
	}

	public void setNotasItens(Collection<NotaItem> notasItens) {
		this.notasItens = notasItens;
	}

	public boolean addNotaItem(NotaItem obj) {
		obj.setAvaliacaoApresentacaoResumo(this);
		return notasItens.add(obj);
	}

	public boolean removeNotaItem(NotaItem obj) {
		obj.setAvaliacaoApresentacaoResumo(null);
		return notasItens.remove(obj);
	}
	
	/**
	 * Calcular a media das notas da avaliação
	 *
	 * @return
	 */
	public void calcularMedia() {
		double media = 0;
		if ( notasItens != null && !notasItens.isEmpty() ) {
			int somaPesos = 0;
			for (NotaItem nota : notasItens){
				media += nota.getNota() * nota.getItemAvaliacao().getPeso();
				somaPesos += nota.getItemAvaliacao().getPeso();
			}
			if (somaPesos != 0) {
				media /= somaPesos;
			}
		}
		setMedia(media);
	}
	
	public boolean isAvaliado(){
		return media != null && media > 0;
	}

	public boolean isSelecionadoApresentacaoOral() {
		return selecionadoApresentacaoOral;
	}

	public void setSelecionadoApresentacaoOral(boolean selecionadoApresentacaoOral) {
		this.selecionadoApresentacaoOral = selecionadoApresentacaoOral;
	}

}