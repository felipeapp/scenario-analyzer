/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;


/**
 * Situações que as turmas podem assumir.
 *
 */
@Entity
@Table(name = "situacao_turma", schema = "ensino", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class SituacaoTurma implements PersistDB {

	// Fields

	private int id;

	private String descricao;

	public static final int CONSOLIDADA = 3;
	public static final int ABERTA = 1;
	public static final int A_DEFINIR_DOCENTE = 2;
	public static final int EXCLUIDA = 4;
	//public static final int CANCELADA = 5;
	public static final int INTERROMPIDA = 6;

	// Constructors

	/** default constructor */
	public SituacaoTurma() {
	}

	/** minimal constructor */
	public SituacaoTurma(int idSituacaoTurma) {
		this.id = idSituacaoTurma;
	}
	
	public SituacaoTurma(int idSituacaoTurma, String descricao) {
		this.id = idSituacaoTurma;
		this.descricao = descricao;
	}

	/** full constructor */
	public SituacaoTurma(int idSituacaoTurma, String descricao,
			Set<Turma> turmas) {
		this.id = idSituacaoTurma;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_situacao_turma", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idSituacaoTurma) {
		this.id = idSituacaoTurma;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 40)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		return descricao;
	}
	
	public static Collection<SituacaoTurma> getSituacoesTodas(){
		ArrayList<SituacaoTurma> situacoes = new ArrayList<SituacaoTurma>(0);
		situacoes.add(new SituacaoTurma(ABERTA, "ABERTA"));
		situacoes.add(new SituacaoTurma(A_DEFINIR_DOCENTE, "A DEFINIR DOCENTE"));
		situacoes.add(new SituacaoTurma(CONSOLIDADA, "CONSOLIDADA"));
		situacoes.add(new SituacaoTurma(EXCLUIDA, "EXCLUÍDA"));
		return situacoes;
	}
	
	public static Collection<SituacaoTurma> getSituacoesValidas(){
		ArrayList<SituacaoTurma> situacoes = new ArrayList<SituacaoTurma>(0);
		situacoes.add(new SituacaoTurma(ABERTA, "ABERTA"));
		situacoes.add(new SituacaoTurma(A_DEFINIR_DOCENTE, "A DEFINIR DOCENTE"));
		situacoes.add(new SituacaoTurma(CONSOLIDADA, "CONSOLIDADA"));
		return situacoes;
	}
	
	public static Collection<SituacaoTurma> getSituacoesInvalidas(){
		ArrayList<SituacaoTurma> situacoes = new ArrayList<SituacaoTurma>(0);
		situacoes.add(new SituacaoTurma(EXCLUIDA, "EXCLUÍDA"));
		return situacoes;
	}
	
	public static Collection<SituacaoTurma> getSituacoesAbertas(){
		ArrayList<SituacaoTurma> situacoes = new ArrayList<SituacaoTurma>(0);
		situacoes.add(new SituacaoTurma(ABERTA, "ABERTA"));
		situacoes.add(new SituacaoTurma(A_DEFINIR_DOCENTE, "A DEFINIR DOCENTE"));
		return situacoes;
	}
	
	
}
