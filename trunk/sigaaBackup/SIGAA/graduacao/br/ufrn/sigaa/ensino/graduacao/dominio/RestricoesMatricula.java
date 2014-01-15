/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Representa as poss�veis restri��es de matr�cula que podem ser ignoradas em
 * uma matr�cula compuls�ria.
 *
 * Valor TRUE indica se a restri��o deve ser verificada
 *
 * @author Andre Dantas
 *
 */
@Entity
@Table(name="restricoes_matricula", schema="graduacao")
public class RestricoesMatricula implements PersistDB{

	/** Chave prim�ria. */
    @Id
    @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_resticoes_matricula", nullable = false)
	private int id;

	/**
	 * N�o verificar se o discente possui pr�-requisitos para matricular-se nas
	 * turmas
	 */
	private boolean preRequisitos;

	/**
	 * N�o verificar se o discente possui co-requisitos para matricular-se nas
	 * turmas
	 */
	private boolean coRequisitos;

	/** N�o verificar choque de hor�rios entre turmas a serem matriculadas */
	private boolean choqueHorarios;

	/**
	 * N�o verificar se o discente possui aprova��o ou aproveitamento nos
	 * componentes (e equivalentes) das turmas a serem matriculadas
	 */
	private boolean mesmoComponente;

	/** N�o verificar limite de cr�ditos extra-curriculares */
	private boolean limiteCreditosExtra;
	
	/**
	 * N�o verificar limite m�ximo de cr�ditos por semestre do curr�culo do
	 * aluno
	 */
	private boolean limiteMaxCreditosSemestre;
	
	/** Define um valor m�ximo de cr�ditos por semestre para a matr�cula. */
	private Integer valorMaximoCreditos;

	/**
	 * N�o verificar limite m�nimo de cr�ditos por semestre do curr�culo do
	 * aluno
	 */
	private Boolean limiteMinCreditosSemestre;

	/** Define um valor m�nimo de cr�ditos por semestre para a matr�cula. */
	private Integer valorMinimoCreditos;

	/** N�o verificar o limite m�ximo de matr�culas de alunos especiais */
	private boolean alunoEspecial;

	/** N�o verificar restri��es aplicadas � matr�culas de alunos de outros campus (mobilidade acad�mica) */
	private boolean alunoOutroCampus;

	/** N�o verificar restri��es aplicadas a matriculas em turmas especiais de f�rias */
	private boolean turmaFerias;

	/** N�o verificar capacidade da turma */
	private boolean capacidadeTurma;

	/** Alertar sobre a capacidade da turma, mas permitir a matr�cula  */ 
	@Transient
	private boolean alertaCapacidadeTurma;
	
	/** Para alguns coordenadores ou gestores de ensino � permitida
	 *  a duplicidade de matr�cula caso o componente curricular tenha conte�do vari�vel,
	 *  ou seja, caso o atributo conteudoVariavel == true  */ 
	@Transient
	private boolean permiteDuplicidadeCasoConteudoVariavel = false;
	
	/** N�o verificar se o aluno possui trancamento de programa no ano-per�odo da matr�cula */
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
	 * Retorna um objeto com todas as restri��es de matr�cula existentes
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
	 * Retorna um objeto com nenhuma as restri��es de matr�cula existentes
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
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na matr�cula de alunos regulares em turmas regulares. 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesRegular() {
		RestricoesMatricula r = getRestricoesTodas();
		r.setAlunoEspecial(false);
		r.setTurmaFerias(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na matr�cula de alunos regulares em turmas regulares de alunos de conv�nios. 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesConvenio() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setAlunoOutroCampus(false);
		return r;
	}
	
	/**
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na re-matr�cula de alunos regulares em turmas regulares. 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesReMatricula() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setCapacidadeTurma(false);
		r.setAlertaCapacidadeTurma(true);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na matr�cula em turmas de f�rias. 
	 * @return
	 */
	public static RestricoesMatricula getRestricoesTurmaFerias() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setLimiteMinCreditosSemestre(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na matr�cula realizada por alunos especiais.
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
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na matr�cula em atividades.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesRegistroAtividade() {
		RestricoesMatricula r = getRestricoesRegular();
		r.setChoqueHorarios(false);
		r.setCapacidadeTurma(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na matr�cula em atividades a partir de um
	 * {@link RegistroAtividade registro de atividade} espec�fico.
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
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na matr�cula de alunos do Ensino a Dist�ncia em turmas
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
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na matr�cula de alunos do Ensino a Dist�ncia em turmas de
	 * f�rias.
	 * @return
	 */
	public static RestricoesMatricula getRestricoesEADFerias() {
		RestricoesMatricula r = getRestricoesEAD();
		r.setLimiteMinCreditosSemestre(false);
		return r;
	}

	/**
	 * Retorna um objeto com o conjunto de restri��es de matr�cula a serem
	 * verificadas na solicita��o de matr�cula em turmas de f�rias.
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
