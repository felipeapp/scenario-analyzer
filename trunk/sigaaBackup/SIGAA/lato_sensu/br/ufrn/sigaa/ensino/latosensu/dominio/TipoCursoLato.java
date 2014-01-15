/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe que registra os tipos de cursos de pós-graduação lato sensu existentes
 * (especialização, aperfeiçoamento, etc.)
 */
@Entity
@Table(name = "tipo_curso_lato", schema = "lato_sensu", uniqueConstraints = {})
public class TipoCursoLato implements Validatable {

	// Fields

	private int id;

	private String descricao;
	
	private Integer chMinima;
	
	private Integer chMaxima;
	
	@CampoAtivo
	private boolean ativo;

	// Constructors

	/** default constructor */
	public TipoCursoLato() {
	}

	/** default minimal constructor */
	public TipoCursoLato(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoCursoLato(int idTipoCursoLato, String descricao) {
		this.id = idTipoCursoLato;
		this.descricao = descricao;
	}

	/** full constructor */
	public TipoCursoLato(int idTipoCursoLato, String descricao,
			Set<CursoLato> cursoLatos, Integer chMinima) {
		this.id = idTipoCursoLato;
		this.descricao = descricao;
		this.chMinima = chMinima;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_curso_lato", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoCursoLato) {
		this.id = idTipoCursoLato;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ch_minima", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getChMinima() {
		return chMinima;
	}

	public void setChMinima(Integer chMinima) {
		this.chMinima = chMinima;
	}
	
	@Column(name = "ch_maxima", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getChMaxima() {
		return chMaxima;
	}

	public void setChMaxima(Integer chMaxima) {
		this.chMaxima = chMaxima;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(chMinima, "Ch Mínima", lista);
		if (chMinima != null && chMaxima != null 
				&& chMinima > chMaxima) {
			lista.addMensagem(MensagensArquitetura.VALOR_MENOR_IGUAL_A, "Ch Mínimo", chMaxima);
		}
		return lista;
	}

}