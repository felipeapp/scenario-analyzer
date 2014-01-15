/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/03/2010
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.annotations.Length;
import br.ufrn.arq.negocio.validacao.annotations.Required;

/**
 * Entidade para armazenar os possíveis índices acadêmicos
 * utilizados no regulamento dos cursos de graduação.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="indice_academico", schema="ensino")
public class IndiceAcademico implements PersistDB {
	
	public static final int IECH = 4;
	public static final int IEPL = 5;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	/** Identificador do índice acadêmico */	
	private int id;
	
	/** Nome do índice */
	@Required
	private String nome;
	
	/** Sigla do índice */
	@Required @Length(max="5")
	private String sigla;
	
	/** Indica se o índice irá ou não aparecer no histórico */
	@Column(name="exibido_historico")
	private boolean exibidoHistorico;

	/** Indica se o índice está sendo utilizado ou não */
	private boolean ativo;
	
	/** Indica o nome completo da classe responsável por realizar o cálculo do índice */
	@Required
	private String classe;
	
	/** Descrição do índice, texto do regulamento que fala do índice */
	private String descricao;
	
	/** Ordem na qual o índice será calculado */
	private int ordem;
	
	/** Nível de ensino associado ao índice acadêmico */
	private char nivel;
	
	/** Faixa de valores que o índice pode assumir (utilizado na legenda) */
	private String faixa;
	
	/** Indica se a legenda do índice irá ou não aparecer no histórico */
	@Column(name="exibido_legenda")
	private boolean exibidoLegenda;
		
	/** Frequência de faixas de divisão do gráfico histograma usado nos relatórios */
	@Column(name="frequencia_divisao_histograma")
	private String frequenciaDivisaoHistograma;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public boolean isExibidoHistorico() {
		return exibidoHistorico;
	}

	public void setExibidoHistorico(boolean exibidoHistorico) {
		this.exibidoHistorico = exibidoHistorico;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public boolean isExibidoLegenda() {
		return exibidoLegenda;
	}

	public void setExibidoLegenda(boolean exibidoLegenda) {
		this.exibidoLegenda = exibidoLegenda;
	}

	public String getFaixa() {
		return faixa;
	}

	public void setFaixa(String faixa) {
		this.faixa = faixa;
	}

	public String getLegenda(){
		return "("+ getNome() + ": " + getFaixa() + ")";
	}

	public String getFrequenciaDivisaoHistograma() {
		return frequenciaDivisaoHistograma;
	}

	public void setFrequenciaDivisaoHistograma(String frequenciaDivisaoHistograma) {
		this.frequenciaDivisaoHistograma = frequenciaDivisaoHistograma;
	}
}
