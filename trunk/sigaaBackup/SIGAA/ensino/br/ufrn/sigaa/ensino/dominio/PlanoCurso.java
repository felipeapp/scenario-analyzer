/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/02/2010
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe que representa um plano de curso para uma turma virtual.
 * 
 * @author Daniel Augusto
 *
 */
@Entity
@Table(name = "plano_curso", schema = "ensino")
public class PlanoCurso implements Validatable {
	
	/**
	 * Chave primaria
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_plano_curso", nullable = false)
	private int id;
	
	/**
	 * Aplicação do método de ensino.
	 */
	private String metodologia;
	
	/** 
	 * Procedimentos que serão aplicados para se avaliar as aprendizagens dos discentes da turma regida por este plano de curso.
	 */
	@Column(name="procedimento_avaliacao_aprendizagem")
	private String procedimentoAvalicao;
	
	/**
	 * Horário disponibilizado pelo professor para que os alunos tirem dúvidas sobre o conteúdo da disciplina.
	 */
	@Column(name="horario_atendimento")
	private String horarioAtendimento;
	
	/**
	 * Turma da qual o plano faz parte.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_turma")
	private Turma turma = new Turma();
	
	/**
	 * Indica se o docente concluiu o plano de curso.
	 */
	private boolean finalizado;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	public String getProcedimentoAvalicao() {
		return procedimentoAvalicao;
	}

	public void setProcedimentoAvalicao(String procedimentoAvalicaoAprendizagem) {
		this.procedimentoAvalicao = procedimentoAvalicaoAprendizagem;
	}

	public String getHorarioAtendimento() {
		return horarioAtendimento;
	}

	public void setHorarioAtendimento(String horarioAtendimento) {
		this.horarioAtendimento = horarioAtendimento;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Turma getTurma() {
		return turma;
	}
	
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(metodologia, "Metodologia", lista);
		ValidatorUtil.validateRequired(procedimentoAvalicao, "Procedimentos de Avaliação da Aprendizagem", lista);
		
		int tamanhoMaximo = 1000, tamanhoMinimo = 50, minimoCaracteres = 10, minimoPalavras = 10;
		
		ValidatorUtil.validateMaxLength(metodologia, tamanhoMaximo, "Metodologia", lista);
		ValidatorUtil.validateMaxLength(procedimentoAvalicao, tamanhoMaximo, "Procedimentos de Avaliação da Aprendizagem", lista);

		ValidatorUtil.validateLeroLero(metodologia, "Metodologia", tamanhoMinimo, minimoCaracteres, minimoPalavras, lista);
		ValidatorUtil.validateLeroLero(procedimentoAvalicao, "Procedimentos de Avaliação da Aprendizagemo", tamanhoMinimo, minimoCaracteres, minimoPalavras, lista);
		
		return lista;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}
}