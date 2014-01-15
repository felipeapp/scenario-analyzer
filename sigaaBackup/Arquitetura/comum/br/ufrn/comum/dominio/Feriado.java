/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/08/2009
 */
package br.ufrn.comum.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra as informações referente aos feriados.
 * 
 * @author Jean Guerethes
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "feriado", schema="comum")
public class Feriado implements Validatable{

	public static final char FERIADO = 'F';
	
	public static final char SUSPENSAO_ATIVIDADES = 'S';
	
	@Id
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_feriado")
	private Date dataFeriado;
	
	@Column(name = "descricao")
	private String descricao;

	/** Categoria do feriado, ou seja, se é um feriado, suspensão de atividades, ponto facultativo, etc. */
	@Column(name="categoria")
	private char categoria;
	
	/** Município onde está a unidade, conforme base de dados dos correios */
	@ManyToOne
	@JoinColumn(name = "id_municipio")
	private Municipio municipio;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "id_unidade_federativa")
	private UnidadeFederativa unidade = new UnidadeFederativa();

	public Municipio getMunicipio() {
		return municipio;
	}
	
	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}
	
	public Date getDataFeriado() {
		return dataFeriado;
	}

	public void setDataFeriado(Date dataFeriado) {
		this.dataFeriado = dataFeriado;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UnidadeFederativa getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeFederativa unidade) {
		this.unidade = unidade;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDataFeriado(), "Data do Feriado", lista);
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);
		if ((getMunicipio() != null) && (getUnidade() == null)) {
			ValidatorUtil.validateRequired(getMunicipio().getId(), "Município", lista);	
		}
		return lista;
	}

	public char getCategoria() {
		return categoria;
	}

	public void setCategoria(char categoria) {
		this.categoria = categoria;
	}

}