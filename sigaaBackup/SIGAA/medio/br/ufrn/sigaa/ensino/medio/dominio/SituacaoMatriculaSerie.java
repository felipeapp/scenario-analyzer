/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Situa��es que a s�rie do ensino m�dio pode assumir.
 * 
 * @author Rafael Gomes
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "situacao_matricula_serie", schema = "medio", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class SituacaoMatriculaSerie implements PersistDB {
	
	/** Matr�cula confirmada no s�rie */
	public static final SituacaoMatriculaSerie MATRICULADO = new SituacaoMatriculaSerie(1, "MATRICULADO");
	/** Aprovado na s�rie */
	public static final SituacaoMatriculaSerie APROVADO = new SituacaoMatriculaSerie(2, "APROVADO");
	/** Reprovado na S�rie */
	public static final SituacaoMatriculaSerie REPROVADO = new SituacaoMatriculaSerie(3, "REPROVADO");
	/** Trancado na S�rie*/
	public static final SituacaoMatriculaSerie TRANCADO = new SituacaoMatriculaSerie(4, "TRANCADO");
	/** Cancelado na S�rie */
	public static final SituacaoMatriculaSerie CANCELADO = new SituacaoMatriculaSerie(5, "CANCELADO");	
	/** Aluno aprovado na s�rie mas pagando depend�ncia da s�rie anterior. */
	public static final SituacaoMatriculaSerie APROVADO_DEPENDENCIA = new SituacaoMatriculaSerie(6, "APROVADO EM DEPEND�NCIA");	
	
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="medio.hibernate_sequence") }) 
	@Column(name = "id_situacao_matricula_serie", unique = true, nullable = false, insertable = true, updatable = true)	
	private int id;

	/** Descri��o do status. */
	private String descricao;
	
	/** Situa��o matr�cula referente ao status da s�rie */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_situacao_matricula")
	private SituacaoMatricula situacaoMatricula;

	/** Construtor padr�o */
	public SituacaoMatriculaSerie() {
	}
	
	/** Construtor passando id e descri��o */
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
	 * Implementa��o do m�todo equals comparando-se os ids das {@link SituacaoMatriculaSerie}.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Defini��o do hashcode da classe.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}	
	
	/**
	 * retorna todas as situa��es de matr�cula em s�rie
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
	 * retorna todas as situa��es positivas de matr�cula em s�rie
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
	 * Situa��es matriculadas ou conclu�das.
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
	 * Situa��es conclu�das.
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
	 * Situa��es Ativas.
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
	 * retorna todas as situa��es aprovadas de matr�cula em s�rie
	 * @return
	 */
	public static Collection<SituacaoMatriculaSerie> getSituacoesAprovadas() {
		ArrayList<SituacaoMatriculaSerie> situacoes = new ArrayList<SituacaoMatriculaSerie>(0);
		situacoes.add(APROVADO);
		situacoes.add(APROVADO_DEPENDENCIA);
		return situacoes;
	}
	
	/**
	 * Retorna a situa��o referente ao id passado.
	 * @param id
	 * @return
	 */
	public static SituacaoMatriculaSerie getSituacao(int id) {
		List<SituacaoMatriculaSerie> situacoes = (List<SituacaoMatriculaSerie>) getSituacoesTodas();
		return situacoes.get(situacoes.indexOf(new SituacaoMatriculaSerie(id)));
	}	
}
