/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 13/09/2006
*/
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade responsável pela definição das Areas SESU (Secretaria de Ensino Superior)  
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "area_sesu", schema = "ensino", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AreaSesu implements Validatable {

	private int id;
	
	/**
	 * Código de identificação da Área SESU (Secretaria de Ensino Superior)
	 */
	private String codigo;
	
	/**
	 * Nome da área de conhecimento SESU (Secretaria de Ensino Superior)
	 */
	private String nome;
	
	/**
	 * Duração padrão em anos dos cursos da área
	 */
	private Integer duracaoPadrao;
	
	/**
	 * Fator de Retenção dos cursos da área. 
	 */
	private Double fatorRetencao;
	
	/**
	 * Peso do grupo dos cursos da área. 
	 */
	private Double pesoGrupo;
	
	/** default constructor */
	public AreaSesu() {
	}

	/** default minimal constructor */
	public AreaSesu(int id) {
		this.id = id;
	}

	/** full constructor */
	public AreaSesu(int idAreaSesu, Integer codGrupoSesu, String sigla, Double fatorRetencao, Integer duracaoPadrao ) {
		this.id = idAreaSesu;
		this.codigo = sigla;
		this.fatorRetencao = fatorRetencao;
		this.duracaoPadrao = duracaoPadrao;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_area_sesu", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idAreaSesu) {
		this.id = idAreaSesu;
	}

	@Column(name = "codigo")
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String sigla) {
		this.codigo = sigla;
	}
	
	@Column(name = "nome")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "fator_retencao")
	public Double getFatorRetencao() {
		return this.fatorRetencao;
	}

	public void setFatorRetencao(Double fatorRetencao) {
		this.fatorRetencao = fatorRetencao;
	}

	@Column(name = "duracao_padrao")
	public Integer getDuracaoPadrao() {
		return this.duracaoPadrao;
	}

	public void setDuracaoPadrao(Integer duracaoPadrao) {
		this.duracaoPadrao = duracaoPadrao;
	}
	
	@Column(name = "peso_grupo")
	public Double getPesoGrupo() {
		return pesoGrupo;
	}

	public void setPesoGrupo(Double pesoGrupo) {
		this.pesoGrupo = pesoGrupo;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	@Override
	public String toString() {
		return this.getCodigo() + " - " + this.getNome();
	}

	@Transient
	public String getDescricao() {
		return this.toString();
	}
	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getCodigo(), "Código", lista);
		ValidatorUtil.validateRequired(getNome(), "Nome da área", lista);
		ValidatorUtil.validateRequired(getDuracaoPadrao(), "Duração Padrão", lista);
		ValidatorUtil.validateRequired(getFatorRetencao(), "Fator de Retenção", lista);
		ValidatorUtil.validateRequired(getPesoGrupo(), "Peso do Grupo", lista);
		return lista;
	}
}