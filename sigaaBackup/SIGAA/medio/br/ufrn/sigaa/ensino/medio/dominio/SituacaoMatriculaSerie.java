/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 24/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Situações que a série do ensino médio pode assumir.
 * 
 * @author Rafael Gomes
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "situacao_matricula_serie", schema = "medio", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class SituacaoMatriculaSerie implements PersistDB {
	
	/** Matrícula confirmada no série */
	public static final SituacaoMatriculaSerie MATRICULADO = new SituacaoMatriculaSerie(1, "MATRICULADO");
	/** Aprovado na série */
	public static final SituacaoMatriculaSerie APROVADO = new SituacaoMatriculaSerie(2, "APROVADO");
	/** Reprovado na Série */
	public static final SituacaoMatriculaSerie REPROVADO = new SituacaoMatriculaSerie(3, "REPROVADO");
	/** Trancado na Série*/
	public static final SituacaoMatriculaSerie TRANCADO = new SituacaoMatriculaSerie(4, "TRANCADO");
	/** Cancelado na Série */
	public static final SituacaoMatriculaSerie CANCELADO = new SituacaoMatriculaSerie(5, "CANCELADO");	
	/** Aluno aprovado na série mas pagando dependência da série anterior. */
	public static final SituacaoMatriculaSerie APROVADO_DEPENDENCIA = new SituacaoMatriculaSerie(6, "APROVADO EM DEPENDÊNCIA");	
	
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="medio.hibernate_sequence") }) 
	@Column(name = "id_situacao_matricula_serie", unique = true, nullable = false, insertable = true, updatable = true)	
	private int id;

	/** Descrição do status. */
	private String descricao;
	
	/** Situação matrícula referente ao status da série */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_situacao_matricula")
	private SituacaoMatricula situacaoMatricula;

	/** Construtor padrão */
	public SituacaoMatriculaSerie() {
	}
	
	/** Construtor passando id e descrição */
	public SituacaoMatriculaSerie(int id, String descricao){
		this.id = id;
		this.descricao = descricao;
	}
	/** Construtor passando comente o id */
	public SituacaoMatriculaSerie(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public SituacaoMatricula getSituacaoMatricula() {
		return situacaoMatricula;
	}

	public void setSituacaoMatricula(SituacaoMatricula situacaoMatricula) {
		this.situacaoMatricula = situacaoMatricula;
	}
	
	/**
	 * Implementação do método equals comparando-se os ids das {@link SituacaoMatriculaSerie}.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Definição do hashcode da classe.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}	
	
	/**
	 * retorna todas as situações de matrícula em série
	 * @return
	 */
	public static Collection<SituacaoMatriculaSerie> getSituacoesTodas() {
		ArrayList<SituacaoMatriculaSerie> situacoes = new ArrayList<SituacaoMatriculaSerie>(0);
		situacoes.add(MATRICULADO);
		situacoes.add(APROVADO);
		situacoes.add(REPROVADO);
		situacoes.add(CANCELADO);
		situacoes.add(TRANCADO);
		situacoes.add(APROVADO_DEPENDENCIA);
		return situacoes;
	}	

	/**
	 * retorna todas as situações positivas de matrícula em série
	 * @return
	 */
	public static Collection<SituacaoMatriculaSerie> getSituacoesPositivas() {
		ArrayList<SituacaoMatriculaSerie> situacoes = new ArrayList<SituacaoMatriculaSerie>(0);
		situacoes.add(MATRICULADO);
		situacoes.add(APROVADO);
		situacoes.add(APROVADO_DEPENDENCIA);
		return situacoes;
	}
	
	/**
	 * Situações matriculadas ou concluídas.
	 * @return
	 */
	public static Collection<SituacaoMatriculaSerie> getSituacoesMatriculadoOuConcluido() {
		ArrayList<SituacaoMatriculaSerie> situacoes = new ArrayList<SituacaoMatriculaSerie>(0);
		situacoes.add(APROVADO);
		situacoes.add(REPROVADO);
		situacoes.add(MATRICULADO);
		situacoes.add(APROVADO_DEPENDENCIA);
		return situacoes;
	}
	
	/**
	 * Situações concluídas.
	 * @return
	 */
	public static Collection<SituacaoMatriculaSerie> getSituacoesConcluido() {
		ArrayList<SituacaoMatriculaSerie> situacoes = new ArrayList<SituacaoMatriculaSerie>(0);
		situacoes.add(APROVADO);
		situacoes.add(REPROVADO);
		situacoes.add(APROVADO_DEPENDENCIA);
		return situacoes;
	}
	
	/**
	 * Situações Ativas.
	 * @return
	 */
	public static Collection<SituacaoMatriculaSerie> getSituacoesAtivas() {
		ArrayList<SituacaoMatriculaSerie> situacoes = new ArrayList<SituacaoMatriculaSerie>(0);
		situacoes.add(APROVADO);
		situacoes.add(REPROVADO);
		situacoes.add(MATRICULADO);
		situacoes.add(TRANCADO);
		situacoes.add(APROVADO_DEPENDENCIA);
		return situacoes;
	}
	
	/**
	 * retorna todas as situações aprovadas de matrícula em série
	 * @return
	 */
	public static Collection<SituacaoMatriculaSerie> getSituacoesAprovadas() {
		ArrayList<SituacaoMatriculaSerie> situacoes = new ArrayList<SituacaoMatriculaSerie>(0);
		situacoes.add(APROVADO);
		situacoes.add(APROVADO_DEPENDENCIA);
		return situacoes;
	}
	
	/**
	 * Retorna a situação referente ao id passado.
	 * @param id
	 * @return
	 */
	public static SituacaoMatriculaSerie getSituacao(int id) {
		List<SituacaoMatriculaSerie> situacoes = (List<SituacaoMatriculaSerie>) getSituacoesTodas();
		return situacoes.get(situacoes.indexOf(new SituacaoMatriculaSerie(id)));
	}	
}
