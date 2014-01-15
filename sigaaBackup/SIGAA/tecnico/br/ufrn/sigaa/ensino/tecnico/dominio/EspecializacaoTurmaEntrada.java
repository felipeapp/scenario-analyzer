/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/06/2007
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Especialização que diferencia os alunos de uma turma de entrada específica.
 * Uma turma de entrada é um agrupador para um conjunto de alunos que entram na escola no mesmo período 
 * e devem cursar as mesmas turmas regulares durante todo curso. 
 * A especialização determina um aspecto específico de tais turmas que serão cursadas pelo aluno,
 * impondo bloqueios no momento da matrícula em turmas regulares para alunos cujas turmas de entrada
 * possuam especializações diferentes.
 * Por exemplo, ao criar-se uma turma regular no ensino técnico, pode-se definir que esta turma será oferecida somente
 * a alunos de uma determinada especialização, evitando que alunos de outras especializações possam se matricular nesta turma.
 *
 * @author Andre M Dantas
 *
 */
@Entity
@Table(name = "especializacao_turma_entrada", schema = "tecnico")
public class EspecializacaoTurmaEntrada implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_especializacao_turma_entrada", nullable = false)
	private int id;

	/** Descrição da Especialização da Turma de Entrada. */
	private String descricao;

	/** Indica a Unidade na qual a Especialização é utilizada. */
	@ManyToOne
	@JoinColumn(name="id_unidade")
	private Unidade unidade;

	@Column(name = "somente_turma")
	private boolean somenteTurma = false;

	@ManyToMany
	@JoinTable(name = "especializacoes", schema="tecnico",
		    joinColumns = @JoinColumn (name="id_especializacao_turma_entrada_a"),
		    inverseJoinColumns = @JoinColumn(name="id_especializacao_turma_entrada_b"))	
	private Collection<EspecializacaoTurmaEntrada> especializacoes;
	
	public EspecializacaoTurmaEntrada(int id) {
		this.id = id;
	}

	public EspecializacaoTurmaEntrada() {
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
	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EspecializacaoTurmaEntrada other = (EspecializacaoTurmaEntrada) obj;
		if (id != other.id)
			return false;
		return true;
	}


	public boolean isSomenteTurma() {
		return somenteTurma;
	}


	public void setSomenteTurma(boolean somenteTurma) {
		this.somenteTurma = somenteTurma;
	}

	public Collection<EspecializacaoTurmaEntrada> getEspecializacoes() {
		return especializacoes;
	}

	public void setEspecializacoes(
			Collection<EspecializacaoTurmaEntrada> especializacoes) {
		this.especializacoes = especializacoes;
	}

}
