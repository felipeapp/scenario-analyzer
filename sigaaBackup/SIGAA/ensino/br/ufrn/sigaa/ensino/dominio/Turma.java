/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '02/10/2008'
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Where;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.chat.ChatEngine;
import br.ufrn.arq.chat.UsuarioChat;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.usuarios.UserOnlineMonitor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.dominio.TurmaSolicitacaoTurma;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que registra as ofertas de vagas para matr�culas em um componente curricular para um
 * determinado ano e per�odo letivo.
 *
 * Gleydson Lima
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "turma", schema = "ensino", uniqueConstraints = {})
public class Turma implements PersistDB {

	// constantes
	/** Constante que indica o tipo de turma regular. */
	public static final int REGULAR = 1;
	/** Constante que indica o tipo de turma de f�rias. */
	public static final int FERIAS = 2;
	/** Constante que indica o tipo de turma de ensino individual. */
	public static final int ENSINO_INDIVIDUAL = 3;

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="ensino.turma_seq") }) 	
	@Column(name = "id_turma", nullable = false)
	private int id;

	/** Indica a situa��o da turma. Pode est� aberta, consolidada e outras. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_situacao_turma", unique = false, nullable = true, insertable = true, updatable = true)
	private SituacaoTurma situacaoTurma;

	/** Disciplina associada a turma. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_disciplina", unique = false, nullable = true, insertable = true, updatable = true)
	private ComponenteCurricular disciplina;

	/** Caso seja EAD, indica o p�lo da turma. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_polo")
	private Polo polo;

	/** Ano letivo da turma. */
	@Column(name = "ano", unique = false, nullable = false, insertable = true, updatable = true)
	private int ano;

	/** Per�odo letivo da turma. */
	@Column(name = "periodo", unique = false, nullable = false, insertable = true, updatable = true)
	private int periodo;

	/** Quantidade m�xima de alunos. */
	@Column(name = "capacidade_aluno", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer capacidadeAluno;

	/** Local das aulas. */
	@Column(name = "local", unique = false, nullable = true, insertable = true, updatable = true, length = 40)
	private String local;
	
	/** C�digo gerado pelo sistema para identificar a turma. */
	@Column(name = "codigo", unique = false, nullable = true, insertable = true, updatable = true)
	private String codigo;

	/** Data de in�cio do per�odo letivo da turma. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataInicio;

	/** Data de fim do per�odo letivo da turma */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataFim;

	/** Descri��o textual do hor�rio da turma. */
	@Column(name = "descricao_horario")
	private String descricaoHorario;

	/** Cole��o de docentes da turma. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "turma")
	private Set<DocenteTurma> docentesTurmas = new HashSet<DocenteTurma>(0);

	/** Lista de hor�rios da turma. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "turma")
	@OrderBy("dataInicio ASC, dia ASC, horaInicio ASC")
	private List<HorarioTurma> horarios = new ArrayList<HorarioTurma>(0);

	/** Lista de reservas de vagas para curso da turma. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "turma")
	private Collection<ReservaCurso> reservas = new HashSet<ReservaCurso>(0);

	/** Especialidades de turmas do ensino t�cnico */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_especializacao_turma_entrada")
	private EspecializacaoTurmaEntrada especializacao;

	/**
	 * Observa��o cadastrada para turma. Geralmente utilizada por turmas vinculadas a componentes de T�picos Especiais,
	 * onde o tema espec�fico � definido a cada per�odo letivo 
	 */
	private String observacao;

	/** Quantidade de solicita��es da turma. */
	@Column(name="total_solicitacoes")
	private Integer totalSolicitacoes;

	/**
	 * Informa se a turma � especificamente para alunos de um curso (geralmente
	 * usado para cursos de conv�nio)
	 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	private Curso curso;

	/** Solicita��es de turmas atendidas com esta turma. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "turma")
	private Collection<TurmaSolicitacaoTurma> turmasSolicitacaoTurmas = new HashSet<TurmaSolicitacaoTurma>();

	/** Indica que a turma � de ensino a dist�ncia. */
	private boolean distancia;

	/** Tipo da turma: regular, de f�rias ou de ensino individual. */
	private Integer tipo;

	/** Indica se a turma foi processada no processamento de matr�cula */
	private Boolean processada;
	
	/** Indica se a turma foi processada no processamento da rematr�cula */
	@Column(name="processada_rematricula")
	private Boolean processadaRematricula;

	/** C�digo utilizado na migra��o da turma. */
	private String codmergpa;

	/** ID do p�lo do ensino a dist�ncia da turma. */
	@Column(updatable=false, insertable=false, name="id_polo")
	private Integer idPolo;

	/** Campus ao qual a turma est� ligada. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_campus_ies")
	private CampusIes campus;

	/** Usu�rio que consolidou a turma. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario_consolidacao")
	private Usuario usuarioConsolidacao;
	
	/** Data de consolida��o da turma. */
	@Column(name="data_consolidacao")
	private Date dataConsolidacao;
	
	/**
	 * Turma agrupadora, utilizada para unificar para o docente a vis�o das subturmas na turma virtual, consolida��o, etc.. 
	 */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_turma_agrupadora")
	//@Transient
	private Turma turmaAgrupadora;
	
	/** Subturmas desta turma, caso ela seja uma turma agrupadora. */
	@OneToMany(mappedBy = "turmaAgrupadora", fetch=FetchType.LAZY)
	@Where(clause="id_situacao_turma  != " + SituacaoTurma.EXCLUIDA)
	//@Transient
	private List<Turma> subturmas;
	
	/** Indica se � uma turma agrupadora de subturma ou n�o. */
	//@Transient
	private boolean agrupadora = false;
	
	/** Registro de entrada do usu�rio que cadastrou a turma. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data de cadastro da turma. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro de entrada do usu�rio que modificou a turma. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data de altera��o da turma. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;

	// Transients
	/** Quantidade de alunos matriculados com status EM ESPERA. */
	@Transient
	private long qtdEspera;
	/** Quantidade de alunos matriculados com status MATRICULADO. */
	@Transient
	private long qtdMatriculados;
	/** Quantidade de alunos matriculados com status APROVADO. */
	@Transient
	private long qtdAprovados;
	/** Quantidade de alunos matriculados com status REPROVADO. */
	@Transient
	private long qtdReprovados;
	/** Quantidade de alunos matriculados com status TRANCADO. */
	@Transient
	private long qtdTrancados;
	/** Quantidade de alunos matriculados com status REPROVADO POR FALTA. */
	@Transient
	private long qtdReprovadosFalta;
	/** Quantidade de alunos matriculados com status REPROVADO POR M�DIA E FALTA. */
	@Transient
	private long qtdReprovadosMediaFalta;
	/** Quantidade de Vagas dispon�veis para matr�cula, com exce��o das vagas para alunos ingressantes. */
	@Transient
	private long qtdVagasDisponiveis;

	/** Quantidade de alunos que desistiram da matr�cula. */
	@Transient
	private int totalDesistencias;
	/** Indica se o aluno deseja matricular nesta turma. */
	@Transient
	private boolean matricular;
	
	/** Atributo transiente para ser usado em par�metros de busca */
	@Transient
	private String nomesDocentes;
	
	/** Cole��o de matr�culas da turma. */
	@Transient
	private Collection<MatriculaComponente> matriculasDisciplina = new HashSet<MatriculaComponente>(0);
	
	/** Solicita��o de turma referente a esta turma. */
	@Transient
	private SolicitacaoTurma solicitacao;
	
	/** TurmaSerie de ensino m�dio, no qual a turma de disciplina pertence.*/
	@Transient 
	private TurmaSerie turmaSerie;
	
	/** Atributo utilizado para indicar se a turma est� sendo selecionada numa determinada opera��o de caso de uso.*/
	@Transient
	private boolean selecionada;
	
	/** Atributo utilizado para indicar se discente est� apto a matricular-se.*/
	@Transient
	private boolean podeMatricular = true;
	
	/** Porcentagem de aulas definidos pelos hor�rios da turma em rela��o a ch.*/
	@Transient
	private float porcentagemAulas;
		
	public boolean isPodeMatricular() {
		return podeMatricular;
	}

	public void setPodeMatricular(boolean podeMatricular) {
		this.podeMatricular = podeMatricular;
	}
	
	/**
	 * Par�metros da gestora acad�mica para esta turma
	 */
	@Transient
	private ParametrosGestoraAcademica parametros;
	
	// Constructors
	/** default constructor */
	public Turma() {
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public Turma(int id) {
		setId(id);
	}

	/** Indica se a turma � regular.
	 * @return
	 */
	@Transient
	public boolean isTurmaRegular(){
		return  ( tipo != null && tipo == REGULAR);
	}

	/** Indica se a turma � de f�rias
	 * @return
	 */
	@Transient
	public boolean isTurmaFerias(){
		return ( tipo != null && tipo == FERIAS);
	}

	/** Indica se a turma � de ensino individual.
	 * @return
	 */
	@Transient
	public boolean isTurmaEnsinoIndividual(){
		return  ( tipo != null && tipo == ENSINO_INDIVIDUAL);
	}

	/**
	 * Indica se essa turma teve sua origem em uma solicita��o de Ensino Individual
	 * 
	 * @return
	 */
	public boolean isTurmaRegularOrigemEnsinoIndiv() {
		return getSolicitacao() != null && getSolicitacao().isTurmaEnsinoIndividual() && isTurmaRegular();
	}
	
	/**
	 * Indica se esta turma pode ser removida pelo chefe. Para que o chefe possa
	 * remover a turma, ela n�o pode ter alunos matriculados se n�o for de
	 * ensino individualizado. Se for de ensino individualizado e for remover em
	 * uma data anterior ao in�cio do processamento da matr�cula, o chefe tamb�m
	 * pode remover a turma, mesmo que tenha alunos matriculados.
	 * 
	 * @param cal
	 * @return
	 */
	public boolean isPassivelRemocaoPeloChefe(CalendarioAcademico cal){
		int qtdMax = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_PERMITE_REMOCAO_TURMA_PELO_CHEFE);
		return ( !this.isTurmaEnsinoIndividual() && !isMatriculada() )
				|| ( this.isTurmaEnsinoIndividual() && cal != null && cal.getInicioProcessamentoMatricula() != null 
						&& cal.getInicioProcessamentoMatricula().before(new Date()) 
						&& this.getAno() + this.getPeriodo() <= cal.getAno() + cal.getPeriodo() )
						// caso a turma tenha menos de 5 (parametrizado) discentes, o chefe poder� remover a turma no per�odo de ajustes ou per�odo de sugest�o de turma pelo chefe do depto.
						|| (matriculasDisciplina != null && matriculasDisciplina.size() <= qtdMax && (cal.isPeriodoAjustesTurmas() || cal.isPeriodoSugestaoTurmaChefe()));
	}

	/** Retorna a lista de hor�rios da turma. 
	 * @return
	 */
	public List<HorarioTurma> getHorarios() {
		return horarios;
	}

	/** Set a lista de hor�rios da turma.
	 * @param horarios
	 */
	public void setHorarios(List<HorarioTurma> horarios) {
		this.horarios = horarios;
	}


	/** Retorna a lista de reservas de vagas para curso da turma. 
	 * @return
	 */
	public Collection<ReservaCurso> getReservas() {
		return reservas;
	}

	/** Seta a lista de reservas de vagas para curso da turma.
	 * @param reservas
	 */
	public void setReservas(Collection<ReservaCurso> reservas) {
		this.reservas = reservas;
	}

	/** Indica a situa��o da turma. Pode est� aberta, consolidada e outras. 
	 * @return
	 */
	public SituacaoTurma getSituacaoTurma() {
		return this.situacaoTurma;
	}

	/** Seta a situa��o da turma. Pode est� aberta, consolidada e outras. 
	 * @param situacaoTurma
	 */
	public void setSituacaoTurma(SituacaoTurma situacaoTurma) {
		this.situacaoTurma = situacaoTurma;
	}

	/** Retorna a disciplina associada a turma. 
	 * @return
	 */
	public ComponenteCurricular getDisciplina() {
		return this.disciplina;
	}

	/** Seta a disciplina associada a turma.
	 * @param disciplina
	 */
	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	/** Retorna o ano letivo da turma. 
	 * @return
	 */
	public int getAno() {
		return this.ano;
	}

	/** Seta o ano letivo da turma.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo letivo da turma. 
	 * @return
	 */
	public int getPeriodo() {
		return this.periodo;
	}

	/** Seta o per�odo letivo da turma.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna a quantidade m�xima de alunos. 
	 * @return
	 */
	public Integer getCapacidadeAluno() {
		return this.capacidadeAluno;
	}

	/** Seta a quantidade m�xima de alunos.
	 * @param capacidadeAluno
	 */
	public void setCapacidadeAluno(Integer capacidadeAluno) {
		this.capacidadeAluno = capacidadeAluno;
	}

	/** Retorna o total de alunos matriculados na turma (somat�rio de alunos com status EM ESPERA, MATRICULADO, APROVADO, REPROVADO, TRANCADO ou REPROVADO POR FALTA).
	 * @return
	 */
	public Integer getTotalMatriculados() {
		if (isAgrupadora()) {
			int total = 0;
			if (getSubturmas() != null)
				for (Turma subTurma : getSubturmas())
					total += subTurma.getTotalMatriculados();
			return total;
		} else {
			return (int) (qtdMatriculados + qtdAprovados
					+ qtdReprovados + qtdTrancados + qtdReprovadosFalta + qtdReprovadosMediaFalta);
		}
	}

	/** Retorna o local onde ser�o lecionadas as aulas.
	 * @return
	 */
	public String getLocal() {
		return this.local;
	}
	
	/** Retorna uma descri��o textual do recurso f�sico reservado.
	 * @return
	 */

	/** Seta o local onde ser�o lecionadas as aulas.
	 * @param local
	 */
	public void setLocal(String local) {
		this.local = local;
	}

	/** Retorna o c�digo gerado pelo sistema para identificar a turma.  
	 * @return
	 */
	public String getCodigo() {
		return this.codigo;
	}

	/** Seta o c�digo gerado pelo sistema para identificar a turma.
	 * @param codigo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/** Retorna a data de in�cio do per�odo letivo da turma. 
	 * @return
	 */
	public Date getDataInicio() {
		return this.dataInicio;
	}

	/** Seta a data de in�cio do per�odo letivo da turma.
	 * @param dataInicio
	 */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/** Retorna a data de fim do per�odo letivo da turma 
	 * @return
	 */
	public Date getDataFim() {
		return this.dataFim;
	}

	/** Seta a data de fim do per�odo letivo da turma
	 * @param dataFim
	 */
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/** Retorna a cole��o de docentes da turma. 
	 * @return
	 */
	public Set<DocenteTurma> getDocentesTurmas() {
		return this.docentesTurmas;
	}

	/** Seta a cole��o de docentes da turma.
	 * @param docenteTurmas
	 */
	public void setDocentesTurmas(Set<DocenteTurma> docenteTurmas) {
		this.docentesTurmas = docenteTurmas;
	}


	/** Retorna a cole��o de matr�culas da turma. 
	 * @return
	 */
	public Collection<MatriculaComponente> getMatriculasDisciplina() {
		return this.matriculasDisciplina;
	}

	/** Seta a cole��o de matr�culas da turma.
	 * @param matriculaDisciplinas
	 */
	public void setMatriculasDisciplina(
			Collection<MatriculaComponente> matriculaDisciplinas) {
		this.matriculasDisciplina = matriculaDisciplinas;
	}	

	/** Retorna a solicita��o de turma referente a esta turma. 
	 * @return
	 */
	public SolicitacaoTurma getSolicitacao() {
		return solicitacao;
	}

	/** Seta a solicita��o de turma referente a esta turma.
	 * @param solicitacao
	 */
	public void setSolicitacao(SolicitacaoTurma solicitacao) {
		this.solicitacao = solicitacao;
	}

	/**
	 * Adiciona uma matr�cula � lista de matr�culas da turma.
	 * @param obj
	 * @return
	 */
	public boolean addMatriculaDisciplina(MatriculaComponente obj) {
		obj.setTurma(this);
		return matriculasDisciplina.add(obj);
	}

	/**
	 * Remove uma matr�cula da lista de matr�culas da turma.
	 * @param obj
	 * @return
	 */
	public boolean removeMatriculaDisciplina(MatriculaComponente obj) {
		obj.setTurma(null);
		return matriculasDisciplina.remove(obj);
	}

	/**
	 * Adiciona um docente � lista de docentes da turma. 
	 * @param obj
	 * @return
	 */
	public boolean addDocenteTurma(DocenteTurma obj) {
		obj.setTurma(this);
		return docentesTurmas.add(obj);
	}

	/**
	 * Remove um docente da lista de docentes da turma.
	 * @param obj
	 * @return
	 */
	public boolean removeDocenteTurma(DocenteTurma obj) {
		obj.setTurma(null);
		return docentesTurmas.remove(obj);
	}

	/**
	 * Adiciona um hor�rio � turma.
	 * @param obj
	 * @return
	 */
	public boolean addHorarioTurma(HorarioTurma obj) {
		obj.setTurma(this);
		return horarios.add(obj);
	}

	/**
	 * Remove um hor�rio da turma.
	 * @param obj
	 * @return
	 */
	public boolean removeHorarioTurma(HorarioTurma obj) {
		obj.setTurma(null);
		return horarios.remove(obj);
	}

	/**
	 * Adiciona uma reserva � lista de reservas de vagas da turma.
	 * @param obj
	 * @return
	 */
	public boolean addReservaCurso(ReservaCurso obj) {
		obj.setTurma(this);
		return reservas.add(obj);
	}
	
	/** Adiciona uma reserva � lista de reservas de espa�o f�sico.
	 * @param reserva
	 * @return
	 */

	/**
	 * Remove uma reserva da lista de reservas de vagas da turma.
	 * @param obj
	 * @return
	 */
	public boolean removeReservaCurso(ReservaCurso obj) {
		obj.setTurma(null);
		return reservas.remove(obj);
	}

	/** Indica se a turma possui reserva para o curso.
	 * @param curso
	 * @return
	 */
	public boolean contemReservaCurso (Curso curso) {
		if (reservas == null) reservas = new HashSet<ReservaCurso>(0);
		if (curso != null) 
			for (ReservaCurso reserva : reservas) {
				Curso cursoReserva =  reserva.getMatrizCurricular() != null ? reserva.getMatrizCurricular().getCurso() : reserva.getCurso(); 
				if (cursoReserva != null && cursoReserva.getId() == curso.getId())
					return true;
			}
		return false;
	}
	
	/** Compara este objeto � outro, comparando os atributos: (id) ou (disciplina.id, codigo, ano, periodo).
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Turma outro = (Turma) obj;
		if (outro != null) {
			if (outro.getId() == getId())
				return true;
			else
				return  outro.getDisciplina() != null && getDisciplina() != null && outro.getDisciplina().getId() == getDisciplina().getId() &&
						codigo != null && codigo.equals(outro.getCodigo()) &&
						ano == outro.getAno() &&
						periodo == outro.getPeriodo()
				;
		}

		return false;
	}

	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId(), disciplina, codigo);
	}

	/**
	 * Retorna a carga hor�ria total do componente curricular
	 * da turma.
	 * @return
	 */
	@Transient
	public int getChTotalTurma() {
		if (getDisciplina() != null)
			return getDisciplina().getChTotal();
		else
			return 0;
	}

	/** Retorna o nome da discipina e o c�digo da turma.
	 * @return
	 */
	@Transient
	public String getNome() {
		return getAno() + "." + getPeriodo() + " - "
				+ getDisciplina().getNome() + " - Turma " + getCodigo();
	}

	/** Retorna o nome da discipina e o c�digo da turma.
	 * @return
	 */
	@Transient
	public String getNomeResumido() {
		return getDisciplina().getNome() + " - Turma " + getCodigo();
	}

	/**
	 * Descri��o completa da Turma, para a melhor identifica��o.
	 */
	@Transient
	public String getNomeCompleto() {
		return getAno() + "." + getPeriodo() + " - "
				+ getDisciplina().getNome() + " - Turma " + getCodigo() + " - Hor�rio " + getDescricaoHorario();
	}
	
	/**
	 * Consolida a turma, setando a sua situa��o para CONSOLIDADA.
	 *
	 */
	public void consolidar() {
		this.situacaoTurma = new SituacaoTurma();
		this.situacaoTurma.setId(SituacaoTurma.CONSOLIDADA);
	}

	/**
	 * Retorna true se a turma estiver consolidada e false caso contr�rio
	 *
	 * @return
	 */
	@Transient
	public boolean isConsolidada() {
		if (this.situacaoTurma != null)
			return this.situacaoTurma.getId() == SituacaoTurma.CONSOLIDADA;
		return false;
	}

	/** Retorna o ano-per�odo da turma.
	 * @return
	 */
	@Transient
	public String getAnoPeriodo() {
		return ano + ((periodo == 0) ? "" : "." + periodo);
	}

	/**
	 * Retorna uma cole��o com os docentes da turma.
	 * 
	 * @return
	 */
	@Transient
	public Set<Docente> getDocentes() {
		Set<Docente> docentes = new HashSet<Docente>();
		if (getDocentesTurmas() != null) {
			for (DocenteTurma dtt : getDocentesTurmas()) {
				if (isEmpty(dtt.getDocenteExterno()))
					docentes.add(dtt.getDocente());
				else if (isEmpty(dtt.getDocente())
						|| dtt.getDocente().getId() == 0)
					docentes.add(dtt.getDocenteExterno());
			}
		}
		return docentes;
	}

	/** Retorna a descri��o textual do hor�rio da turma. 
	 * @return
	 */
	public String getDescricaoHorario() {
		return descricaoHorario;
	}


	/** Seta a descri��o textual do hor�rio da turma.
	 * @param descricaoHorario
	 */
	public void setDescricaoHorario(String descricaoHorario) {
		this.descricaoHorario = descricaoHorario;
	}
	
	/**
	 * Retorna uma String representando o hor�rio da semana atual caso a disciplina 
	 * permita hor�rio flex�vel, ou a {@link #descricaoHorario} caso contr�rio.
	 * 
	 * @return
	 */
	public String getDescricaoHorarioSemanaAtual(){
		if (getDisciplina().isPermiteHorarioFlexivel()){
			List<HorarioTurma> horariosSemana = new ArrayList<HorarioTurma>();
			for (HorarioTurma ht : horarios) {
				if (CalendarUtils.isDentroPeriodo(ht.getDataInicio(), ht.getDataFim()))  {
					if (!horariosSemana.contains(ht))
						horariosSemana.add(ht);
				}
			}
			List<HorarioTurma> horariosBackup = getHorarios();
			setHorarios(horariosSemana);
			String retorno = HorarioTurmaUtil.formatarCodigoHorarios(this);
			setHorarios(horariosBackup);
			if (retorno.indexOf("(") > 0)
			return retorno.substring(0, retorno.indexOf("("));
			else return retorno;
		}
		return descricaoHorario;
	}

	/**
	 * Retorna uma String de descri��o completa da turma.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		String descricao = getDisciplina().getNome()
				+ " - "
				+ getCodigo()
				+ " ("
				+ getAnoPeriodo()
				+ ")"
				+ ((especializacao != null && !ValidatorUtil.isEmpty(especializacao.getDescricao())) ? " ["
						+ especializacao.getDescricao() + "]" : "")
				+ ((observacao != null && !ValidatorUtil.isEmpty(observacao)) ? " [" + observacao + "]" : "")
				+ "<br>Docentes: " + getDocentesNomes();
		return descricao;
	}
	
	/** Retorna uma descri��o resumida da turma. 
	 * @return
	 */
	@Transient
	public String getDescricaoResumida() {
		return getDisciplina().getCodigo() + " - T" + getCodigo(); 
	}
	
	/**
	 * Retorna a descri��o da turma para os casos de turma infantil.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoTurmaInfantil() {
		String descricao = getDisciplina().getNome()
		+ " - "
		+ (getDescricaoHorario().equals("M") ? "Matutino" : "Vespertino")
		+ " - "
		+ getCodigo()
		+ " ("
		+ getAnoPeriodo()
		+ ")"
		+ "<br>Docentes: " + getDocentesNomes();
		return descricao;
	}
	
	/**
	 * Retorna a descri��o da turma para os casos de turma infantil.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoTurmaInfantilResumida() {
		String descricao = getDisciplina().getNome()
		+ " - "
		+ (getDescricaoHorario().equals("M") ? "Matutino" : "Vespertino")
		+ " - "
		+ getCodigo()
		+ " ("
		+ getAnoPeriodo()
		+ ")";
		return descricao;
	}
	
	/**
	 * Retorna a descri��o da turma para os casos de turma m�dio.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoTurmaMedio() {
		String descricao = (getTurmaSerie() != null && getTurmaSerie().getSerie() != null ? getTurmaSerie().getSerie().getDescricaoCompleta() : "")
		+ ": "
		+ (getDisciplina() != null && getDisciplina().getDetalhes() != null ? getDisciplina().getNome() : "")
		+ (getCodigo() != null ? " - Turma: " + getCodigo() : "")
		+ " ("
		+ getAnoPeriodo()
		+ ")"
		+ ((!isEmpty(polo)) ? " - P�lo: " + (!isEmpty(polo.getDescricao()) ? polo.getDescricao() : getLocal()) : "")
		+ ((especializacao != null) ? " ["
				+ especializacao.getDescricao() + "]" : "");
		return descricao;
	}
	
	/**
	 * Retorna o nome do componente referente � turma.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoDisciplina() {
		String descricao = getDisciplina().getNome();
		return descricao;
	}

	/**
	 * Retorna o nome do centro associado � disciplina da turma.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoCentro() {
		if ( getDisciplina().getUnidade().getUnidadeGestora() != null) {
			return getDisciplina().getUnidade().getUnidadeGestora().getNome();
		}

		return "";
	}

	/**
	 * Retorna o nome do departamento associado a disciplina da turma.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoDepartamento() {
		String descricao = getDisciplina().getUnidade().getNome();
		return descricao;
	}

	/**
	 * Retorna a descri��o da turma com a informa��o da carga hor�ria da
	 * turma.
	 * @return
	 */
	@Transient
	public String getDescricaoComCh() {
		String descricao = getDisciplina().getNome()
				+ " - "
				+ getCodigo()
				+ " ("
				+ getAnoPeriodo()
				+ ")"
				+ ((especializacao != null && !ValidatorUtil.isEmpty(especializacao.getDescricao())) ? " ["
						+ especializacao.getDescricao() + "]" : "")
				+ ((observacao != null && !ValidatorUtil.isEmpty(observacao)) ? " [" + observacao + "]" : "") 
				+ " - "	+ getChTotalTurma() + "h" 
				+ "<br>Docentes: " + getDocentesNomes();
		return descricao;
	}

	/**
	 * Retorna uma descri��o completa da turma, apenas sem os nomes dos docentes.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoSemDocente() {
		String descricao = (getDisciplina() != null ? getDisciplina().getDescricaoResumida() : "")
				+ (getCodigo() != null ? " - Turma: " + getCodigo() : "")
				+ " ("
				+ getAnoPeriodo()
				+ ")"
				+ ((!isEmpty(polo)) ? " - P�lo: " + (!isEmpty(polo.getDescricao()) ? polo.getDescricao() : getLocal()) : "")
				+ ((especializacao != null) ? " ["
						+ especializacao.getDescricao() + "]" : "");
		return descricao;
	}
	
	/**
	 * Retorna uma String contendo o c�digo e o nome da disciplina seguidos do c�digo da turma.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoCodigo() {
		String descricao = (getDisciplina() != null ? getDisciplina().getDescricaoResumida() : "")
				+ (getCodigo() != null ? " - Turma: " + getCodigo() : "");
				
		return descricao;
	}
	
	/** Retorna uma descri��o resumida da turma com docentes. 
	 * @return
	 */
	@Transient
	public String getDescricaoComDocentes() {
		return (getDisciplina() != null ? getDisciplina().getDescricaoResumida() : "") + " - T" + getCodigo() + " - Docente(s): " + getDocentesNomes(); 
	}

	/**
	 * Retorna uma String contendo o nome da disciplina seguido dos docentes associados a turma.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoSemCodigo() {

		String descricao = getDisciplina().getNome() + "<br>Docentes: "
				+ getDocentesNomes();
		return descricao;
	}

	/**
	 * Retorna a lista dos docentes da turma que s�o servidores.
	 * @return
	 */
	@Transient
	public ArrayList<Servidor> getDocentesMatriculaComNome() {

		ArrayList<Servidor> listaMatriculas = new ArrayList<Servidor>();
		Iterator<DocenteTurma> it = getDocentesTurmas().iterator();

		if (it != null) {
			int tamanho = getDocentesTurmas().size();
			for (int i = 1; i <= tamanho; i++) {
				DocenteTurma dt = it.next();
				if (dt != null) {
					if (dt.getDocenteExterno() == null) {
						listaMatriculas.add(dt.getDocente());
					}
				}
			}
		}
		return listaMatriculas;
	}

	/** Retorna o nome da disciplina da turma, indicando se � especializa��o.
	 * @return
	 */
	@Transient
	public String getNomeDisciplinaEspecializacao() {
		return getDisciplina().getNome()
				+ (especializacao != null ? " - "
						+ especializacao.getDescricao() : "");
	}

	/** Retorna a chave prim�ria. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Indica se o aluno deseja matricular nesta turma. 
	 * @return
	 */
	public boolean isMatricular() {
		return matricular;
	}

	/**
	 * Indica se tem alunos matriculados na turma
	 * @return
	 */
	@Transient
	public boolean isMatriculada() {
		return this.getQtdMatriculados() > 0 || ( this.getTotalSolicitacoes() != null && this.getTotalSolicitacoes() > 0);
	}

	/** Seta se o aluno deseja matricular nesta turma.
	 * @param matricular
	 */
	public void setMatricular(boolean matricular) {
		this.matricular = matricular;
	}

	/**
	 * Retorna uma cole��o contendo os discentes da turma.
	 * 
	 * @return
	 */
	@Transient
	public Collection<Discente> getDiscentes() {
		Collection<Discente> discentes = new HashSet<Discente>();
		for (MatriculaComponente matricula : getMatriculasDisciplina()) {
			discentes.add(matricula.getDiscente().getDiscente());
		}
		return discentes;
	}


	/** Retorna a quantidade de alunos matriculados com status EM ESPERA. 
	 * @return
	 */
	public long getQtdEspera() {
		return qtdEspera;
	}

	/** REtorna a quantidade de alunos matriculados com status EM ESPERA. 
	 * @param qtdEspera
	 */
	public void setQtdEspera(long qtdEspera) {
		this.qtdEspera = qtdEspera;
	}

	/** Retorna a quantidade de alunos matriculados com status MATRICULADO. 
	 * @return
	 */
	public long getQtdMatriculados() {
		return qtdMatriculados;
	}

	/** Seta a quantidade de alunos matriculados com status MATRICULADO.  
	 * @param qtdMatriculados
	 */
	public void setQtdMatriculados(long qtdMatriculados) {
		this.qtdMatriculados = qtdMatriculados;
	}

	/** Retorna a quantidade de alunos matriculados com status APROVADO. 
	 * @return
	 */
	public long getQtdAprovados() {
		return qtdAprovados;
	}

	/** Seta a quantidade de alunos matriculados com status APROVADO.
	 * @param qtdAprovados
	 */
	public void setQtdAprovados(long qtdAprovados) {
		this.qtdAprovados = qtdAprovados;
	}

	/** Retorna a quantidade de alunos matriculados com status REPROVADO. 
	 * @return
	 */
	public long getQtdReprovados() {
		return qtdReprovados;
	}

	/** Retorna a quantidade de alunos matriculados com status REPROVADO POR FALTA. 
	 * @return
	 */
	public long getQtdReprovadosFalta() {
		return qtdReprovadosFalta;
	}
	
	/** Retorna a quantidade de alunos matriculados com status REPROVADO POR M�DIA E FALTA. 
	 * @return
	 */
	public long getQtdReprovadosMediaFalta() {
		return qtdReprovadosMediaFalta;
	}

	/** Seta a quantidade de alunos matriculados com status REPROVADO POR FALTA. 
	 * @param qtdReprovadosFalta
	 */
	public void setQtdReprovadosFalta(long qtdReprovadosFalta) {
		this.qtdReprovadosFalta = qtdReprovadosFalta;
	}
	
	/** Seta a quantidade de alunos matriculados com status REPROVADO POR M�DIA E FALTA. 
	 * @param qtdReprovadosMediaFalta
	 */
	public void setQtdReprovadosMediaFalta(long qtdReprovadosMediaFalta) {
		this.qtdReprovadosMediaFalta = qtdReprovadosMediaFalta;
	}

	/** Seta a quantidade de alunos matriculados com status REPROVADO. 
	 * @param qtdReprovados
	 */
	public void setQtdReprovados(long qtdReprovados) {
		this.qtdReprovados = qtdReprovados;
	}

	/** Retorna a quantidade de alunos matriculados com status TRANCADO. 
	 * @return
	 */
	public long getQtdTrancados() {
		return qtdTrancados;
	}

	/** Seta a quantidade de alunos matriculados com status TRANCADO. 
	 * @param qtdTrancados
	 */
	public void setQtdTrancados(long qtdTrancados) {
		this.qtdTrancados = qtdTrancados;
	}

	/**
	 * Retorna uma String com os nomes dos docentes de uma turma.
	 * @return
	 */
	@Transient
	public String getDocentesNomes() {
		if (getDocentes() == null)
			return null;

		if (situacaoTurma != null && situacaoTurma.getId() == SituacaoTurma.A_DEFINIR_DOCENTE) {
			return "A DEFINIR DOCENTE";
		}

		StringBuffer nomes = new StringBuffer();
		Iterator<Docente> it = getDocentes().iterator();
		if (it != null) {
			int tamanho = getDocentes().size();
			for (int i = 1; i <= tamanho; i++) {
				Docente d = it.next();
				if (d != null) {
					nomes.append(d.getNome());
					if (i + 1 == tamanho)
						nomes.append(" e ");
					else if (i < tamanho)
						nomes.append(", ");
				}
			}
			if (!nomes.toString().equals("null"))
				return nomes.toString();
		}
		return "";
	}
	
	/**
	 * Retorna uma Cole��o de String com os nomes dos docentes de uma turma e respectivo hor�rio na turma.
	 * @return
	 */
	@Transient
	public Collection<String> getDocentesNomesHorarios() {
		if (getDocentes() == null)
			return null;

		Collection<String> listaNomes = new ArrayList<String>();
		if (situacaoTurma != null && situacaoTurma.getId() == SituacaoTurma.A_DEFINIR_DOCENTE) {
			listaNomes.add("A DEFINIR DOCENTE");
		} else {
			for (DocenteTurma docente : getDocentesTurmas()) {
				StringBuilder nome = new StringBuilder(docente.getDocenteNome());
				if (!isEmpty(docente.getDescricaoHorario())) {
					nome.append(": ");
					nome.append(docente.getDescricaoHorario());
				}
				listaNomes.add(nome.toString());
			}
		}
		return listaNomes;
	}

	/**
	 * Retorna uma String com os nomes dos docentes
	 * e a carga hor�ria que cada um deles possui na turma.
	 * @return
	 */
	@Transient
	public String getDocentesNomesCh() {
		if (getDocentesTurmas() == null)
			return null;

		StringBuffer nomes = new StringBuffer();
		Iterator<DocenteTurma> it = getDocentesTurmas().iterator();
		if (it != null) {
			int tamanho = getDocentesTurmas().size();
			for (int i = 1; i <= tamanho; i++) {
				DocenteTurma dt = it.next();
				if (dt != null) {
					
					if (dt.getDocente() != null && !isEmpty( dt.getDocente().getNome() ) ) {
						nomes.append(dt.getDocente().getNome());
						nomes.append(" ("+dt.getChDedicadaPeriodo()+"h)");
						if (i + 1 == tamanho)
							nomes.append(" e ");
						else if (i < tamanho)
							nomes.append(", ");
					}
					else if (dt.getDocenteExterno() != null &&  !isEmpty( dt.getDocenteExterno().getNome() ) ) {
						nomes.append(dt.getDocenteExterno().getNome());
						nomes.append(" ("+dt.getChDedicadaPeriodo()+"h)");
						if (i + 1 == tamanho)
							nomes.append(" e ");
						else if (i < tamanho)
							nomes.append(", ");
					}

				}
			}
			if (!nomes.toString().equals("null"))
				return nomes.toString();
		}
		return "";
	}

	/**
	 * Identifica se o hor�rio passado como par�metro
	 * est� presente na lista de hor�rios da turma.
	 * @param horario
	 * @return
	 */
	public boolean temHorario(HorarioTurma horario) {
		for (HorarioTurma ht : horarios) {
			if (horario.getDia() == ht.getDia()	&& 
					// se for do mesmo n�vel de ensino a verifica��o do id do hor�rio ser� suficiente
					// mas na verifica��o entre turmas de diferentes n�veis � necess�rio verificar o conflito pelo in�cio e fim do Hor�rio
					( horario.getHorario().checarConflitoHorario(ht.getHorario()) ) && 
					ht.checarConflitoPerido(horario)) {
				
				horario.setConflitoHorario(true);
				ht.setConflitoHorario(true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Retorna uma descri��o dos hor�rios da turma.
	 * @return
	 */
	public String getHorarioComConflito() {
		StringBuilder sb = new StringBuilder();
		for (HorarioTurma ht : horarios) {
			sb.append(ht.getDescricaoHorario());
			sb.append(" / ");
		}
		sb.delete(sb.lastIndexOf(" / "), sb.length());
		return sb.toString();
	}

	/**
	 * Identifica se o hor�rio passado como par�metro choca 
	 * com algum hor�rio da turma.
	 * @param horario
	 * @return
	 */
	public boolean chocaHorario(HorarioTurma horario) {
		for (HorarioTurma ht : horarios) {
			if (!horario.getHoraFim().before(ht.getHoraInicio())
					& !horario.getHoraInicio().after(ht.getHoraFim()))
				return true;
		}
		return false;
	}

	/** Informa se a turma � especificamente para alunos de um curso (geralmente usado para cursos de conv�nio)
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta se a turma � especificamente para alunos de um curso (geralmente usado para cursos de conv�nio)
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna, caso seja EAD, o p�lo da turma. 
	 * @return
	 */
	public Polo getPolo() {
		return polo;
	}

	/** Seta, caso seja EAD, o p�lo da turma.
	 * @param polo
	 */
	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	/** Retorna as especialidades de turmas do ensino t�cnico 
	 * @return
	 */
	public EspecializacaoTurmaEntrada getEspecializacao() {
		return especializacao;
	}

	/** Seta as especialidades de turmas do ensino t�cnico
	 * @param especializacao
	 */
	public void setEspecializacao(EspecializacaoTurmaEntrada especializacao) {
		this.especializacao = especializacao;
	}

	/**
	 * Retorna uma descri��o da turma para casos de n�vel T�cnico.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoNivelTecnico() {
		StringBuffer descricao = new StringBuffer();
		descricao.append(getAnoPeriodo() + " - "
				+ getDisciplina() != null ? getDisciplina().getDescricaoResumida() : "");
		
		if (getEspecializacao() != null && !getEspecializacao().getDescricao().equals(""))
			descricao.append(" (" + getEspecializacao().getDescricao() + ")");
		
		if (getObservacao() != null && !getObservacao().equals(""))
			descricao.append(" (" + getObservacao() + ")");
		
		if (getCodigo() != null && !getCodigo().equals(""))
			descricao.append(" - Turma: " + getCodigo());

		return descricao.toString();
	}

	/**
	 * Retorna uma descri��o da turma para casos de n�vel Lato Sensu.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoNivelLato() {
		return getDisciplina().getCodigo() + " - "
				+ getDisciplina().getDetalhes().getNome();
	}

	/** Retorna as solicita��es de turmas atendidas com esta turma. 
	 * @return
	 */
	public Collection<TurmaSolicitacaoTurma> getTurmasSolicitacaoTurmas() {
		return turmasSolicitacaoTurmas;
	}

	/** Seta as solicita��es de turmas atendidas com esta turma.
	 * @param turmasSolicitacaoTurmas
	 */
	public void setTurmasSolicitacaoTurmas(
			Collection<TurmaSolicitacaoTurma> turmasSolicitacaoTurmas) {
		this.turmasSolicitacaoTurmas = turmasSolicitacaoTurmas;
	}

	/** Retorna o total de cr�ditos do componente curricular.
	 * @return
	 */
	@Transient
	public float getQtdBase() {
		return getDisciplina().getCrTotal();
	}

	/**
	 * Retorna a observa��o cadastrada para turma. Geralmente utilizada por
	 * turmas vinculadas a componentes de T�picos Especiais, onde o tema
	 * espec�fico � definido a cada per�odo letivo
	 * 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * Seta a observa��o cadastrada para turma. Geralmente utilizada por
	 * turmas vinculadas a componentes de T�picos Especiais, onde o tema
	 * espec�fico � definido a cada per�odo letivo
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * Retorna o total de alunos matriculados na turma (somat�rio de alunos com
	 * status EM ESPERA, MATRICULADO, APROVADO, REPROVADO, TRANCADO ou REPROVADO
	 * POR FALTA) e a quantidade m�xima de alunos. 
	 * 
	 * @return
	 */
	@Transient
	public String getMatriculadosCapacidade() {
		return getTotalMatriculados() + "/" + capacidadeAluno;
	}

	/** Indica que a turma � de ensino a dist�ncia. 
	 * @return
	 */
	public boolean isDistancia() {
		return distancia;
	}

	/** Seta que a turma � de ensino a dist�ncia. 
	 * @param distancia
	 */
	public void setDistancia(boolean distancia) {
		this.distancia = distancia;
	}

	/** Retorna o ipo da turma: regular, de f�rias ou de ensino individual. 
	 * @return
	 */
	public Integer getTipo() {
		return tipo;
	}

	/** Seta o ipo da turma: regular, de f�rias ou de ensino individual.
	 * @param tipo
	 */
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	/**
	 * Retorna o tipo da solicita��o de turma em String
	 *
	 * @return
	 */
	@Transient
	public String getTipoString() {
		if( tipo == null )
			return "Indefinido";
		switch (tipo) {
		case REGULAR:
			return "REGULAR";
		case FERIAS:
			return "FERIAS";
		case ENSINO_INDIVIDUAL:
			return "ENSINO INDIVIDUAL";
		default:
			return "-";
		}
	}

	/** Indica se a turma foi processada no processamento de matr�cula 
	 * @return
	 */
	public Boolean getProcessada() {
		return processada;
	}

	/** Seta se a turma foi processada no processamento de matr�cula 
	 * @param processada
	 */
	public void setProcessada(Boolean processada) {
		this.processada = processada;
	}

	/** Retorna o c�digo utilizado na migra��o da turma. 
	 * @return
	 */
	public String getCodmergpa() {
		return codmergpa;
	}

	/** Seta o c�digo utilizado na migra��o da turma.
	 * @param codmergpa
	 */
	public void setCodmergpa(String codmergpa) {
		this.codmergpa = codmergpa;
	}

	/** Retorna a quantidade de solicita��es da turma. 
	 * @return
	 */
	public Integer getTotalSolicitacoes() {
		return totalSolicitacoes;
	}

	/** Seta a quantidade de solicita��es da turma.
	 * @param totalSolicitacoes
	 */
	public void setTotalSolicitacoes(Integer totalSolicitacoes) {
		this.totalSolicitacoes = totalSolicitacoes;
	}

	/** Retorna os nomes dos docentes da turma.
	 * @return
	 */
	public String getNomesDocentes() {
		return nomesDocentes;
	}

	/** Retorna os nomes dos docentes da turma.
	 * @param nomesDocentes
	 */
	public void setNomesDocentes(String nomesDocentes) {
		this.nomesDocentes = nomesDocentes;
	}

	/** Indica se a turma � de componente curricular de gradua��o.
	 * @return
	 */
	public boolean isGraduacao() {
		return !ValidatorUtil.isEmpty(disciplina) && disciplina.getNivel() == NivelEnsino.GRADUACAO;
	}

	/** Indica se a turma � de componente curricular de lato sensu.
	 * @return
	 */
	public boolean isLato() {
		return !ValidatorUtil.isEmpty(disciplina) && disciplina.getNivel() == NivelEnsino.LATO;
	}

	/** Indica se a turma � de componente curricular de stricto sensu
	 * @return
	 */
	public boolean isStricto() {
		return !ValidatorUtil.isEmpty(disciplina) &&  disciplina.getNivel() == NivelEnsino.STRICTO;
	}

	/** Indica se a turma � de componente curricular de ensino t�cnico.
	 * @return
	 */
	public boolean isTecnico() {
		return !ValidatorUtil.isEmpty(disciplina) &&  disciplina.getNivel() == NivelEnsino.TECNICO;
	}
	
	/** Indica se a turma � de componente curricular do n�vel forma��o complementar.
	 * @return
	 */
	public boolean isFormacaoComplementar() {
		return !ValidatorUtil.isEmpty(disciplina) &&  disciplina.getNivel() == NivelEnsino.FORMACAO_COMPLEMENTAR;
	}
	
	/** Indica se a turma � de componente curricular de ensino infantil.
	 * @return
	 */
	public boolean isInfantil() {
		return !ValidatorUtil.isEmpty(disciplina) &&  disciplina.getNivel() == NivelEnsino.INFANTIL;
	}
	
	/** Indica se a turma � de componente curricular de ensino m�dio.
	 * @return
	 */
	public boolean isMedio() {
		return !ValidatorUtil.isEmpty(disciplina) &&  disciplina.getNivel() == NivelEnsino.MEDIO;
	}

	/** Indica se a turma � de um componente curricular de resid�ncia m�dica.
	 * @return
	 */
	public boolean isResidenciaMedica() {
		return !ValidatorUtil.isEmpty(disciplina) &&  disciplina.getNivel() == NivelEnsino.RESIDENCIA;
	}

	/** Indica se a turma est� aberta, ou seja, se a situa��o dela � ABERTA ou A DEFINIR DOCENTE.
	 * @return
	 */
	public boolean isAberta() {
		return this.getSituacaoTurma().getId() == SituacaoTurma.ABERTA || this.getSituacaoTurma().getId() == SituacaoTurma.A_DEFINIR_DOCENTE;
	}

	/** Retorna o ID do p�lo do ensino a dist�ncia da turma.  
	 * @return 
	 */
	public Integer getIdPolo() {
		return idPolo;
	}

	/** Seta o ID do p�lo do ensino a dist�ncia da turma.
	 * @param idPolo
	 */
	public void setIdPolo(Integer idPolo) {
		this.idPolo = idPolo;
	}

	/** Retorna o registro de entrada do usu�rio que cadastrou a turma. 
	 * @return
	 */
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	/** Seta o registro de entrada do usu�rio que cadastrou a turma.
	 * @param registroCadastro
	 */
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	/** Retorna a data de cadastro da turma. 
	 * @return
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data de cadastro da turma.
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna o registro de entrada do usu�rio que modificou a turma. 
	 * @return
	 */
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o registro de entrada do usu�rio que modificou a turma.
	 * @param registroAtualizacao
	 */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Retorna a data de altera��o da turma. 
	 * @return
	 */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/** Seta a data de altera��o da turma.
	 * @param dataAlteracao
	 */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/**
	 * Retorna verdadeiro se, e somente se, o ano e o per�odo passados como par�metro forem 
	 * iguais ao ano e per�odo da turma.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public boolean isAnoPeriodo(int ano, int periodo) {
		return this.ano == ano && this.periodo == periodo;
	}

	/** Indica se a turma � de Ensino � Dist�ncia.
	 * @return
	 */
	public boolean isEad() {
	     return (isGraduacao() && ((polo != null && polo.getId() != 0) ||distancia) ) 
	    		 || (!isGraduacao() && distancia);
	}
	
	/** Na cria��o de turma se for de gradua��o o campus � obrigat�rio.
	 * S� deve ter campus para turmas de gradua��o que N�O s�o EAD e que N�O s�o de conv�nio.<br/>
	 * 
	 * S� valida para turmas abertas pois apenas turmas abertas podem ser alteradas, com exce��o de ADMINISTRADOR_DAE e PPG 
	 * que pode alterar turmas j� consolidadas, porem s� � poss�vel alterar os docentes da turma, ent�o n�o deve validar se a turma tem campus.
	 * */
	public boolean isCampusObrigatorio(){
		return this.isGraduacao() && !this.isDistancia() && isEmpty(this.getCurso());
	}

	/** Retorna uma descri��o completa da turma, apenas sem os nomes dos docentes. 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricaoSemDocente();
	}

	/** Retorna o Campus ao qual a turma est� ligada. 
	 * @return
	 */
	public CampusIes getCampus() {
		return campus;
	}

	/** Seta o Campus ao qual a turma est� ligada.
	 * @param campus
	 */
	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}
	
	/** Retorna o total de usu�rios on-line no chat
	 * @return
	 */
	@Transient
	public int getUsersOnlineChat() {
		return ChatEngine.getTotalUserOnLine(id);
	}
	
	/** Indica se h� docentes online no momento. 
	 * @return
	 */
	@Transient
	public boolean isDocenteOnline() {
		return !getDocentesOnline().isEmpty(); 
	}
	
	/**
	 * Retorna uma lista com todos os docentes online no momento.
	 * 
	 * @return
	 */
	private List<Integer> getDocentesOnline() {
		List<Integer> docentesOnline = new ArrayList<Integer>();
		for (DocenteTurma dt : docentesTurmas) {
			if (!isEmpty(dt.getUsuario()) && UserOnlineMonitor.isUserOnline(dt.getUsuario().getLogin(), Sistema.SIGAA)) {
				docentesOnline.add(dt.getUsuario().getId());
			}
		}
		return docentesOnline;
	}
	
	/**
	 * Retorna verdadeiro caso exista algum docente online no chat da turma.
	 * 
	 * @return
	 */
	@Transient
	public boolean isDocenteNoChat() {
		boolean encontrou = false;
		
		try {
			List<Integer> docentesOnline = getDocentesOnline();
			List<UsuarioChat> listMembers = ChatEngine.listMembers(id);
			for (UsuarioChat usuario : listMembers) {
				if (docentesOnline.contains(usuario.getId())) {
					encontrou = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return encontrou;
	}

	/**
	 * Retorna a carga hor�ria do docente na turma. Utilizado no portal docente quando s�o listadas as turmas
	 * e apenas um DocenteTurma � carregado na cole��o de docentesTurmas.
	 * 
	 */
	@Transient
	public Integer getChDedicadaDocente(){
		if(getDocentesTurmas() != null){
			Iterator<DocenteTurma> it = getDocentesTurmas().iterator();
			if(it != null){
				return it.next().getChDedicadaPeriodo();
			}
		}
		return 0; 
	}
	
	/**
	 * Retorna a carga hor�ria dedicada do servidor  ou docente externo informado. Um dos par�metros dever� necess�riamente ser nulo.
	 * @param servidor
	 * @param docente
	 * @return
	 */
	public int getChDedicadaDocente( Servidor servidor, DocenteExterno docente ){
		if(getDocentesTurmas() != null){
			for(DocenteTurma dt : docentesTurmas){
				if( servidor != null && dt.getDocente() != null && dt.getDocente().equals(servidor) )
					return dt.getChDedicadaPeriodo();
				if( docente != null && dt.getDocenteExterno() != null && dt.getDocenteExterno().equals(docente) )
					return dt.getChDedicadaPeriodo();
			}
		}
		return 0;
	}
	
	/** Indica se a turma foi processada no processamento da rematr�cula 
	 * @return
	 */
	public Boolean getProcessadaRematricula() {
		return processadaRematricula;
	}

	/** Seta se a turma foi processada no processamento da rematr�cula 
	 * @param processadaRematricula
	 */
	public void setProcessadaRematricula(Boolean processadaRematricula) {
		this.processadaRematricula = processadaRematricula;
	}

	/** Retorna o usu�rio que consolidou a turma. 
	 * @return
	 */
	public Usuario getUsuarioConsolidacao() {
		return usuarioConsolidacao;
	}

	/** Seta o usu�rio que consolidou a turma.
	 * @param usuarioConsolidacao
	 */
	public void setUsuarioConsolidacao(Usuario usuarioConsolidacao) {
		this.usuarioConsolidacao = usuarioConsolidacao;
	}

	/** Retorna a data de consolida��o da turma. 
	 * @return
	 */
	public Date getDataConsolidacao() {
		return dataConsolidacao;
	}

	/** Seta a data de consolida��o da turma.
	 * @param dataConsolidacao
	 */
	public void setDataConsolidacao(Date dataConsolidacao) {
		this.dataConsolidacao = dataConsolidacao;
	}

	/** Retorna a quantidade de alunos que desistiram da matr�cula. 
	 * @return
	 */
	public int getTotalDesistencias() {
		return totalDesistencias;
	}

	/** Seta a quantidade de alunos que desistiram da matr�cula.
	 * @param totalDesistencias
	 */
	public void setTotalDesistencias(int totalDesistencias) {
		this.totalDesistencias = totalDesistencias;
	}

	/** Retorna a turma agrupadora, utilizada para unificar para o docente a vis�o das subturmas na turma virtual, consolida��o, etc.. 
	 * @return
	 */
	public Turma getTurmaAgrupadora() {
		return turmaAgrupadora;
	}

	/** Seta a turma agrupadora, utilizada para unificar para o docente a vis�o das subturmas na turma virtual, consolida��o, etc..
	 * @param turmaAgrupadora
	 */
	public void setTurmaAgrupadora(Turma turmaAgrupadora) {
		this.turmaAgrupadora = turmaAgrupadora;
	}

	/** Indica se � uma turma agrupadora de subturma ou n�o. 
	 * @return
	 */
	public boolean isAgrupadora() {
		return agrupadora;
	}

	/** Seta se � uma turma agrupadora de subturma ou n�o. 
	 * @param agrupadora
	 */
	public void setAgrupadora(boolean agrupadora) {
		this.agrupadora = agrupadora;
	}

	/** Retorna as subturmas desta turma, caso ela seja uma turma agrupadora. 
	 * @return
	 */
	public List<Turma> getSubturmas() {
		return subturmas;
	}

	/** Seta as subturmas desta turma, caso ela seja uma turma agrupadora.
	 * @param subturmas
	 */
	public void setSubturmas(List<Turma> subturmas) {
		this.subturmas = subturmas;
	}

	/** Indica se a turma � uma subturma.
	 * @return
	 */
	public boolean isSubTurma() {
		return turmaAgrupadora != null;
	}
	
	/**
	 * Retorna o total das vagas que s�o reservadas para os cursos
	 * 
	 * @return
	 */
	@Transient
	public Integer getTotalVagasReservadas(){
		Integer totalVagas = 0;
		for (ReservaCurso reserva : reservas) {
			totalVagas += reserva.getVagasReservadas() + reserva.getVagasReservadasIngressantes();
		}
		return totalVagas;
	}

	/**
	 * Indica se a turma foi migrada do Sistema PontoA
	 * 
	 * @return
	 */
	@Transient
	public boolean isMigradoGraduacao() {
		return codmergpa != null;
	}


	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}

	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}

	public ParametrosGestoraAcademica getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosGestoraAcademica parametros) {
		this.parametros = parametros;
	}

	/** Retorna o n�mero de aulas que a turma regular teria. Isto � usado para o c�lculo do percentual de faltas do discente.
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public int getNumAulas() throws NegocioException, DAOException {
		// CH da turma em horas, multiplicado por 60 minutos, dividido pela quantidades de minutos de uma aula.
		// Ex.: 60 (4 cr)* 60 / 50 = 72 aulas. 60 (4 cr) * 60 / 60 = 60 aulas.
		if (parametros == null) {
			parametros = ParametrosGestoraAcademicaHelper.getParametros(this);
			if (parametros.getMinutosAulaRegular() == null) 
				throw new NegocioException("O par�metro da quantidade de minutos de uma aula regular n�o est� definido.");
		}
		int nAulas = getChTotalTurma() * 60 / parametros.getMinutosAulaRegular();
		return nAulas;
	}
	
	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public boolean isSelecionada() {
		return selecionada;
	}	
	
	/** 
	 * Retorna a descri��o da turma as informa��es do Polo. 
	 * @return
	 */
	@Transient
	public String getDescricaoPolo() {
		String turmaDesc = codigo;
		if (polo != null) {
			turmaDesc += " - " + polo.getDescricao();
		}
		turmaDesc += " (" + matriculasDisciplina.size() + " alunos)";
		return turmaDesc; 
	}	
	
	public long getQtdVagasDisponiveis() {
		return qtdVagasDisponiveis;
	}

	public void setQtdVagasDisponiveis(long qtdVagasDisponiveis) {
		this.qtdVagasDisponiveis = qtdVagasDisponiveis;
	}

	/** 
	 * Retorna o m�s/ano de fim.
	 */
	public String getMesAnoFim() {
		if (dataFim == null)
			return null;
		else
			return (CalendarUtils.getMesByData(dataFim) +1) + "/" + CalendarUtils.getAno(dataFim);
	}

	/** 
	 * Retorna o m�s/ano de in�cio.
	 */
	public String getMesAnoInicio() {
		if (dataInicio == null)
			return null;
		else
			return (CalendarUtils.getMesByData(dataInicio)+1) + "/" + CalendarUtils.getAno(dataInicio);	
	}

	public boolean isEstagioEad() {
		if (disciplina != null )
			return( disciplina.isEstagio() && isEad());
		else
			return false;
	}

	public void setPorcentagemAulas(float porcentagemAulas) {
		this.porcentagemAulas = porcentagemAulas;
	}

	public float getPorcentagemAulas() {
		return porcentagemAulas;
	}
}
