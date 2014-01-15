/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/07/2010
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

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

/**
 * Classe que armazena estatísticas da Avaliação Institucional, tais como a
 * quantidade de Alunos que avaliaram, disciplinas avaliadas, etc.<br/>
 * Esta classe é utilizada pelo processador
 * ProcessadorEstatisticaAvaliacaoInstitucional, que é chamado pelo
 * timer EstatisticasAvaliacaoInstitucionalTimer.<br/>
 * <b>Não é recomendável atualizar os dados estatísticos que não sejam pelo
 * ProcessadorEstatisticaAvaliacaoInstitucional, pois o mesmo remove os
 * dados estatísticos do ano-período da Avaliação Institucional atual.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity 
@Table(schema="avaliacao", name="dados_estatisticos")
public class EstatisticaAvaliacaoInstitucional implements PersistDB{

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="avaliacao.avaliacao_seq") })
	private int id;
	/** Ano ao qual o dado estatístico se refere. */
	private int ano;
	/** Período ao qual o dado estatístico se refere. */
	private int periodo;
	/** Descrição do dado estatístico. */
	private String descricao;
	/** Valor do dado estatístico. */
	private Integer valor;
	/** Valor percentual do dado estatístico. */
	private Double percentual;
	/** Formulário de Avaliação Institucional ao qual esta estatística está vinculada. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formulario_avaliacao")
	private FormularioAvaliacaoInstitucional formulario;
	
	/** Construtor padrão. */
	public EstatisticaAvaliacaoInstitucional() {
	}
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public EstatisticaAvaliacaoInstitucional(int id) {
		super();
		this.id = id;
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param ano
	 * @param periodo
	 * @param descricao
	 * @param valor
	 * @param percentual 
	 */
	public EstatisticaAvaliacaoInstitucional(int id, int ano, int periodo, int idFormulario, String descricao, Integer valor, Double percentual) {
		super();
		this.id = id;
		this.ano = ano;
		this.periodo = periodo;
		this.descricao = descricao;
		this.valor = valor;
		this.percentual = percentual;
		this.formulario = new FormularioAvaliacaoInstitucional(idFormulario);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public Double getPercentual() {
		return percentual;
	}

	public void setPercentual(Double percentual) {
		this.percentual = percentual;
	}

	public FormularioAvaliacaoInstitucional getFormulario() {
		return formulario;
	}

	public void setFormulario(FormularioAvaliacaoInstitucional formulario) {
		this.formulario = formulario;
	}
}
