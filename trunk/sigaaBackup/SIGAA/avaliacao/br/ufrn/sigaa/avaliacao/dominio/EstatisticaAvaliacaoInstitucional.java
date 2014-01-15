/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe que armazena estat�sticas da Avalia��o Institucional, tais como a
 * quantidade de Alunos que avaliaram, disciplinas avaliadas, etc.<br/>
 * Esta classe � utilizada pelo processador
 * ProcessadorEstatisticaAvaliacaoInstitucional, que � chamado pelo
 * timer EstatisticasAvaliacaoInstitucionalTimer.<br/>
 * <b>N�o � recomend�vel atualizar os dados estat�sticos que n�o sejam pelo
 * ProcessadorEstatisticaAvaliacaoInstitucional, pois o mesmo remove os
 * dados estat�sticos do ano-per�odo da Avalia��o Institucional atual.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity 
@Table(schema="avaliacao", name="dados_estatisticos")
public class EstatisticaAvaliacaoInstitucional implements PersistDB{

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="avaliacao.avaliacao_seq") })
	private int id;
	/** Ano ao qual o dado estat�stico se refere. */
	private int ano;
	/** Per�odo ao qual o dado estat�stico se refere. */
	private int periodo;
	/** Descri��o do dado estat�stico. */
	private String descricao;
	/** Valor do dado estat�stico. */
	private Integer valor;
	/** Valor percentual do dado estat�stico. */
	private Double percentual;
	/** Formul�rio de Avalia��o Institucional ao qual esta estat�stica est� vinculada. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formulario_avaliacao")
	private FormularioAvaliacaoInstitucional formulario;
	
	/** Construtor padr�o. */
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
