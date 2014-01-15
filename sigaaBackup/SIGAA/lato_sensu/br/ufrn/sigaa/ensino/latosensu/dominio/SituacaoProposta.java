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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Classe que armazena os possíveis status das propostas de curso lato sensu
 */
@Entity
@Table(name = "situacao_proposta", schema = "lato_sensu", uniqueConstraints = {})
public class SituacaoProposta implements Validatable {

	/** Proposta preenchida pelo usuário, porém ainda não submetida. */
	public static final int INCOMPLETA = 1;
	
	/** Proposta submetida. */
	public static final int SUBMETIDA = 2;
	
	/** Proposta em tramitação. Não utilizado pelo Sigaa. */
	public static final int EM_DILIGENCIA = 4;
	
	/** Proposta aprovada para entrar em funcionamento. */
	public static final int ACEITA = 5;
	
	/** Proposta não aprovada para entrar em funcionamento. */
	public static final int NAO_ACEITA = 6;
	
	/** Proposta excluída. */
	public static final int EXCLUIDA = 7;
	
	/** Chave primária. */
	private int id;

	/** Descrição da Situação da Proposta. */
	private String descricao;

	private Set<PropostaCursoLato> propostasCurso = new HashSet<PropostaCursoLato>(0);

	@CampoAtivo
	private boolean ativo;

	private boolean valida;
	
	// Constructors

	/** default constructor */
	public SituacaoProposta() {
	}

	/** minimal constructor */
	public SituacaoProposta(int idSituacaoProposta) {
		this.id = idSituacaoProposta;
	}

	public SituacaoProposta(int idSituacaoProposta, String descricaoSituacaoProposta) {
		this.id = idSituacaoProposta;
		this.descricao = descricaoSituacaoProposta;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_situacao_proposta", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idSituacaoProposta) {
		this.id = idSituacaoProposta;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 40)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "situacaoProposta")
	public Set<PropostaCursoLato> getPropostasCurso() {
		return this.propostasCurso;
	}

	public void setPropostasCurso(Set<PropostaCursoLato> propostaCursos) {
		this.propostasCurso = propostaCursos;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isValida() {
		return valida;
	}

	public void setValida(boolean valida) {
		this.valida = valida;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}
}