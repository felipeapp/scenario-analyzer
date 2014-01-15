/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Representa uma avaliação de um resumo para o congresso de iniciação científica
 * realizada por um avaliador de resumos
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "avaliacao_resumo", schema = "pesquisa")
public class AvaliacaoResumo implements Validatable {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_avaliacao_resumo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_avaliador")
	private AvaliadorCIC avaliador;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_resumo")
	private ResumoCongresso resumo;
	
	@Column(name = "erro_conteudo")
	private boolean erroConteudo;
	
	@Column(name = "erro_portugues")
	private boolean erroPortugues;
	
	private String parecer;
	
	/** Atributo transiente usado na distribuição dos resumos */
	@Transient
	private boolean selecionado;
	
	public AvaliacaoResumo() {
		
	}
	
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(avaliador, "Avaliador", erros);
		ValidatorUtil.validateRequired(resumo, "Resumo", erros);
		return erros;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AvaliadorCIC getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(AvaliadorCIC avaliador) {
		this.avaliador = avaliador;
	}

	public ResumoCongresso getResumo() {
		return resumo;
	}

	public void setResumo(ResumoCongresso resumo) {
		this.resumo = resumo;
	}

	public boolean isErroConteudo() {
		return erroConteudo;
	}

	public void setErroConteudo(boolean erroConteudo) {
		this.erroConteudo = erroConteudo;
	}

	public boolean isErroPortugues() {
		return erroPortugues;
	}

	public void setErroPortugues(boolean erroPortugues) {
		this.erroPortugues = erroPortugues;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
}
