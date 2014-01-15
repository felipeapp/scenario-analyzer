/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/09/2009
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;

/**
 * Entidade que armazena informações de unidades federativas.
 * 
 * @author Jean Guerethes
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "unidade_federativa", schema="comum")
public class UnidadeFederativa implements PersistDB {

	public static final int RN = 24;

	/**
	 * Id da UF padrão que deve ser utilizado pelos sistemas. 
	 */
	public static final int ID_UF_PADRAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.UF_PADRAO);
	
	// Fields

	private int id;

	private Pais pais = new Pais();

	private String descricao;

	private String sigla;

	private Municipio capital;

	/** default constructor */
	public UnidadeFederativa() {
	}

	/** default minimal constructor */
	public UnidadeFederativa(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public UnidadeFederativa(int idUnidadeFederativa, String sigla) {
		this.id = idUnidadeFederativa;
		this.sigla = sigla;
	}

	// Property accessors
	@Id
	@Column(name = "id_unidade_federativa", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	public int getId() {
		return this.id;
	}

	public void setId(int idUnidadeFederativa) {
		this.id = idUnidadeFederativa;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais", unique = false, nullable = true, insertable = true, updatable = true)
	public Pais getPais() {
		return this.pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 128)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "sigla", unique = false, nullable = false, insertable = true, updatable = true, length = 2)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@ManyToOne
	@JoinColumn(name="id_capital")
	public Municipio getCapital() {
		return capital;
	}

	public void setCapital(Municipio capital) {
		this.capital = capital;
	}

}