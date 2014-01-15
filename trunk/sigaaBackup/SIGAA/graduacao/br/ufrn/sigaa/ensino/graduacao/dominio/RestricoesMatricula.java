/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/03/2007
 *
 */

package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Representa as possíveis restrições de matrícula que podem ser ignoradas em
 * uma matrícula compulsória.
 *
 * Valor TRUE indica se a restrição deve ser verificada
 *
 * @author Andre Dantas
 *
 */
@Entity
@Table(name="restricoes_matricula", schema="graduacao")
public class RestricoesMatricula implements PersistDB{

	/** Chave primária. */
    @Id
    @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_resticoes_matricula", nullable = false)
	private int id;

	/**
	 * Não verificar se o discente possui pré-requisitos para matricular-se nas
	 * turmas
	 */
	private boolean preRequisitos;

	/**
	 * Não verificar se o discente possui co-requisitos para matricular-se nas
	 * turmas
	 */
	private boolean coRequisitos;

	/** Não verificar choque de horários entre turmas a serem matriculadas */
	private boolean choqueHorarios;

	/**
	 * Não verificar se o discente possui aprovação ou aproveitamento nos
	 * componentes (e equivalentes) das turmas a serem matriculadas
	 */
	private boolean mesmoComponente;

	/** Não verificar limite de créditos extra-curriculares */
	private boolean limiteCreditosExtra;
	
	/**
	 * Não verificar limite máximo de créditos por semestre do currículo do
	 * aluno
	 */
	private boolean limiteMaxCreditosSemestre;
	
	/** Define um valor máximo de créditos por semestre para a matrícula. */
	private Integer valorMaximoCreditos;

	/**
	 * Não verificar limite mínimo de créditos por semestre do currículo do
	 * aluno
	 */
	private Boolean limiteMinCreditosSemestre;

	/** Define um valor mínimo de créditos por semestre para a matrícula. */
	private Integer valorMinimoCreditos;

	/** Não verificar o limite máximo de matrículas de alunos especiais */
	private boolean alunoEspecial;

	/** Não verificar restrições aplicadas à matrículas de alunos de outros campus (mobilidade acadêmica) */
	private boolean alunoOutroCampus;

	/** Não verificar restrições aplicadas a matriculas em turmas especiais de férias */
	private boolean turmaFerias;

	/** Não verificar capacidade da turma */
	private boolean capacidadeTurma;

	/** Alertar sobre a capacidade da turma, mas permitir a matrícula  */ 
	@Transient
	private boolean alertaCapacidadeTurma;
	
	/** Para alguns coordenadores ou gestores de ensino é permitida
	 *  a duplicidade de matrícula caso o componente curricular tenha conteúdo variável,
	 *  ou seja, caso o atributo conteudoVariavel == true  */ 
	@Transient
	private boolean permiteDuplicidadeCasoConteudoVariavel = false;
	
	/** Não verificar se o aluno possui trancamento de programa no ano-período da matrícula */
	private boolean trancamentoPrograma;
	
	public boolean isAlunoEspecial() {
		return alunoEspecial;
	}

	public void setAlunoEspecial(boolean alunoEspecial) {
		this.alunoEspecial = alunoEspecial;
	}

	public boolean isChoqueHorarios() {
		return choqueHorarios;
	}

	public void setChoqueHorarios(boolean coincidenciaHorarios) {
		choqueHorarios = coincidenciaHorarios;
	}

	public boolean isCoRequisitos() {
		return coRequisitos;
	}

	public void setCoRequisitos(boolean coRequisitos) {
		this.coRequisitos = coRequisitos;
	}

	public boolean isMesmoComponente() {
		return mesmoComponente;
	}

	public void setMesmoComponente(boolean creditosDisciplina) {
		mesmoComponente = creditosDisciplina;
	}

	public boolean isLimiteCreditosExtra() {
		return limiteCreditosExtra;
	}

	public void setLimiteCreditosExtra(boolean limiteCreditosExtra) {
		this.limiteCreditosExtra = limiteCreditosExtra;
	}

	public boolean isLimiteMaxCreditosSemestre() {
		return limiteMaxCreditosSemestre;
	}

	public void setLimiteMaxCreditosSemestre(boolean limiteCreditosSemestre) {
		limiteMaxCreditosSemestre = limiteCreditosSemestre;
	}

	public boolean isPreRequisitos() {
		return preRequisitos;
	}

	public void setPreRequisitos(boolean preRequisitos) {
		this.preRequisitos = preRequisitos;
	}

	/**
	 * Retorna um objeto com todas as restrições de matrícula existentes
	 * marcadas para serem verificadas.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesTodas() {
		RestricoesMatricula r = new RestricoesMatricula();
		r.setCapacidadeTurma(true);
		r.setAlunoEspecial(true);
		r.setChoqueHorarios(true);
		r.setCoRequisitos(true);
		r.setMesmoComponente(true);
		r.setLimiteCreditosExtra(true);
		r.setLimiteMaxCreditosSemestre(true);
		r.setLimiteMinCreditosSemestre(true);
		r.setPreRequisitos(true);
		r.setAlunoOutroCampus(true);
		r.setTurmaFerias(true);
		r.setTrancamentoPrograma(true);
		return r;
	}

	/**
	 * Retorna um objeto com nenhuma as restrições de matrícula existentes
	 * marcadas para serem verificadas.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesNenhuma() {
		RestricoesMatricula r = new RestricoesMatricula();
		r.setAlunoEspecial(false);
		r.setChoqueHorarios(false);
		r.setCoRequisitos(false);
		r.setMesmoComponente(false);
		r.setLimiteCreditosExtra(false);
		r.setLimiteMaxCreditosSemestre(false);
		r.setPreRequisitos(false);
		r.setAlunoOutroCampus(false);
		r.setTurmaFerias(false);
		r.setTrancamentoPrograma(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na matrícula de alunos regulares em turmas regulares. 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesRegular() {
		RestricoesMatricula r = getRestricoesTodas();
		r.setAlunoEspecial(false);
		r.setTurmaFerias(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na matrícula de alunos regulares em turmas regulares de alunos de convênios. 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesConvenio() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setAlunoOutroCampus(false);
		return r;
	}
	
	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na re-matrícula de alunos regulares em turmas regulares. 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesReMatricula() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setCapacidadeTurma(false);
		r.setAlertaCapacidadeTurma(true);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na matrícula em turmas de férias. 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesTurmaFerias() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setLimiteMinCreditosSemestre(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na matrícula realizada por alunos especiais.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesAlunoEspecial() {
		RestricoesMatricula r = getRestricoesNenhuma();
		r.setCapacidadeTurma(true);
		r.setAlunoEspecial(true);
		r.setChoqueHorarios(true);
		r.setCoRequisitos(false);
		r.setMesmoComponente(true);
		r.setLimiteCreditosExtra(false);
		r.setLimiteMaxCreditosSemestre(false);
		r.setPreRequisitos(false);
		r.setAlunoOutroCampus(false);
		r.setTurmaFerias(false);
		r.setTrancamentoPrograma(true);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na matrícula em atividades.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesRegistroAtividade() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setChoqueHorarios(false);
		r.setCapacidadeTurma(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na matrícula em atividades a partir de um
	 * {@link RegistroAtividade registro de atividade} específico.
	 * 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesRegistroAtividade(RegistroAtividade registro) {
		RestricoesMatricula r = getRestricoesRegular();
		r.setChoqueHorarios(false);
		r.setCapacidadeTurma(false);

		if ( registro != null && (registro.isMatriculaCompulsoria() || registro.isDispensa()) ) {
			r.setPreRequisitos(false);
			r.setCoRequisitos(false);
		}
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na matrícula de alunos do Ensino a Distância em turmas
	 * regulares.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesEAD() {
		RestricoesMatricula r = getRestricoesRegular();
		boolean defineHorarioEad = ParametroHelper.getInstance().getParametroBoolean( ParametrosGraduacao.DEFINE_HORARIO_TURMA_EAD );
		if( defineHorarioEad )
			r.setChoqueHorarios(true);
		else
			r.setChoqueHorarios(false);
		r.setCapacidadeTurma(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na matrícula de alunos do Ensino a Distância em turmas de
	 * férias.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesEADFerias() {
		RestricoesMatricula r = getRestricoesEAD();
		r.setLimiteMinCreditosSemestre(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restrições de matrícula a serem
	 * verificadas na solicitação de matrícula em turmas de férias.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesSolicitacaoTurmaFerias() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setChoqueHorarios(false);
		r.setCapacidadeTurma(false);
		return r;
	}

	public boolean isAlunoOutroCampus() {
		return alunoOutroCampus;
	}

	public void setAlunoOutroCampus(boolean alunoOutroCampus) {
		this.alunoOutroCampus = alunoOutroCampus;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isTurmaFerias() {
		return turmaFerias;
	}

	public void setTurmaFerias(boolean turmaFerias) {
		this.turmaFerias = turmaFerias;
	}

	public boolean isCapacidadeTurma() {
		return capacidadeTurma;
	}

	public void setCapacidadeTurma(boolean capacidadeTurma) {
		this.capacidadeTurma = capacidadeTurma;
	}

	public Boolean getLimiteMinCreditosSemestre() {
		return limiteMinCreditosSemestre;
	}

	public void setLimiteMinCreditosSemestre(Boolean limiteMinCreditosSemestre) {
		this.limiteMinCreditosSemestre = limiteMinCreditosSemestre;
	}

	public boolean isAlertaCapacidadeTurma() {
		return alertaCapacidadeTurma;
	}

	public void setAlertaCapacidadeTurma(boolean alertaCapacidadeTurma) {
		this.alertaCapacidadeTurma = alertaCapacidadeTurma;
	}

	public boolean isTrancamentoPrograma() {
		return trancamentoPrograma;
	}

	public void setTrancamentoPrograma(boolean trancamentoPrograma) {
		this.trancamentoPrograma = trancamentoPrograma;
	}

	public Integer getValorMaximoCreditos() {
		return valorMaximoCreditos;
	}

	public void setValorMaximoCreditos(Integer valorMaximoCreditos) {
		this.valorMaximoCreditos = valorMaximoCreditos;
	}

	public Integer getValorMinimoCreditos() {
		return valorMinimoCreditos;
	}

	public void setValorMinimoCreditos(Integer valorMinimoCreditos) {
		this.valorMinimoCreditos = valorMinimoCreditos;
	}

	public boolean isPermiteDuplicidadeCasoConteudoVariavel() {
		return permiteDuplicidadeCasoConteudoVariavel;
	}

	public void setPermiteDuplicidadeCasoConteudoVariavel(
			boolean permiteDuplicidadeCasoConteudoVariavel) {
		this.permiteDuplicidadeCasoConteudoVariavel = permiteDuplicidadeCasoConteudoVariavel;
	}
	
	
	
}
