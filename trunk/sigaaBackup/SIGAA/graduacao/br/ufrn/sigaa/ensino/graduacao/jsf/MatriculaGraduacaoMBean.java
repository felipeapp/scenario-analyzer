/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 26/01/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.UFRNUtils.completaZeros;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarTurmaIndividualmente;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.agenda.dominio.Agenda;
import br.ufrn.sigaa.agenda.dominio.Evento;
import br.ufrn.sigaa.agenda.jsf.AgendaDataModel;
import br.ufrn.sigaa.agenda.negocio.AgendaFactory;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.ead.HorarioTutoriaDao;
import br.ufrn.sigaa.arq.dao.ead.TutoriaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MobilidadeEstudantilDao;
import br.ufrn.sigaa.arq.dao.ensino.OrientacaoMatriculaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.ExtrapolarCreditoDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatriculaGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.RegistroAtividadeDao;
import br.ufrn.sigaa.avaliacao.dao.CalendarioAvaliacaoDao;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.TipoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalHelper;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.HorarioTutoria;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.TutoriaAluno;
import br.ufrn.sigaa.ead.negocio.EADHelper;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.OrientacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ExtrapolarCredito;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatriculaEquivalentes;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMatriculaGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoSolicitacaoMatricula;
import br.ufrn.sigaa.ensino.metropoledigital.jsf.MatriculaModuloAvancadoMBean;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.jsf.MatriculaDiscenteOutroProgramaMBean;
import br.ufrn.sigaa.ensino.stricto.negocio.MatriculaStrictoValidator;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.ensino.tecnico.negocio.MatriculaTecnicoValidator;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosEAD;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.jsf.PortalDiscenteMBean;
/**
 * Managed Bean para pré-matrícula e matrícula de discentes de graduação em
 * turmas.
 *
 * @author ricardo
 * @author Andre Dantas
 *
 */
@Component("matriculaGraduacao") @Scope("session")
public class MatriculaGraduacaoMBean extends SigaaAbstractController<MatriculaComponente> implements OperadorDiscente {
	
	/** Modelo de agenda utlizado para exibir a agenda das turmas que o discente está se matriculando/matriculado */ 
	private AgendaDataModel turmaAgendaModel;

	/** Discente cuja matrícula será efetuada */
	private DiscenteAdapter discente;

	/** Turmas selecionadas para a matricula */
	private Collection<Turma> turmas = new ArrayList<Turma>();

	/** Turmas que o discente já está matriculado */
	private Collection<Turma> turmasJaMatriculadas = new ArrayList<Turma>(0);

	/** Turmas que o discente já está matriculado */
	private Collection<RegistroAtividade> atividadesJaMatriculadas = new ArrayList<RegistroAtividade>(0);	
	
	/** Turmas que foi negada solicitacao */
	private Collection<Turma> turmasSolicitadasNegadas = new ArrayList<Turma>(0);	

	/** Informa a operação que está sendo realizada no momento no cabeçalho da página  */
	private String operacaoAtual;

	/** Restrições a serem ignoradas em caso de matrícula compulsória */
	private RestricoesMatricula restricoes = new RestricoesMatricula();

	/** Situação da matrícula, se em espera ou matriculado */
	private SituacaoMatricula situacao = new SituacaoMatricula();

	/** Horários da unidade para ajudar a criar as tabelas de horários das turmas escolhidas */
	private List<Horario> horarios;

	/** Horários de disponibilidade do tutor com seus alunos */
	private List<HorarioTutoria> horariosTutoria;

	/** Dados da turma para filtrar busca de turmas abertas */
	private Turma dadosBuscaTurma;

	/** Atributo que Auxilia na busca */
	private Boolean[] boolDadosBusca = {false, false, false, false, false, false, false};

	/** Número da solicitação de matrícula gerada após a submissão do mesmo */
	private Integer numeroSolicitacao;

	/** Lista de alunos a serem carregados quando a seleção de aluno não é por busca e sim numa lista já carregada */
	private Collection<Discente> discentesCurso;

	/** Componentes que o aluno escolhido já concluiu, essa lista ajuda em validações na seleção de turmas */
	private Collection<ComponenteCurricular> componentesMatriculadosConcluidos;

	/** Calendário Acadêmico para fazer verificações ao realizar matrícula */
	private CalendarioAcademico calendarioParaMatricula;

	/** Turmas para sugestão de matrícula */
	private Collection<SugestaoMatricula> turmasCurriculo;
	/** Turmas equivalentes para sugestão de matrícula */
	private Collection<SugestaoMatriculaEquivalentes> turmasEquivalentesCurriculo;

	/** md5 gerado pelas turmas escolhidas numa matrícula */
	private String md5;

	/** Identifica o tipo de matrícula */
	private int tipoMatricula;

	/** Quantidade de matrículas que foram orientadas (exibido na matrícula online)*/
	private int qtdMatriculasOrientadas;

	/** Solicitações de matrícula realizadas */
	private Collection<SolicitacaoMatricula> solicitacoesMatricula;
	/** Solicitações de Matrículas de atividades realizadas */
	private Collection<SolicitacaoMatricula> solicitacoesAtividade;

	/** Tipos de matrículas */
	public static final int SOLICITACAO_MATRICULA = 1, FORA_PRAZO = 2, COMPULSORIA = 3, ALUNO_ESPECIAL = 4, ALUNO_RECEM_CADASTRADO = 5,
		ANALISE_MATRICULA = 6, MATRICULA_FERIAS = 7, MATRICULA_EAD = 8, MATRICULA_CONVENIO = 9, TURMAS_NAO_ONLINE = 10, MATRICULA_REGULAR = 11;

	/** Orientação geral de matrícula cadastrada pela coordenação ou pelo orientador acadêmico */
	private OrientacaoMatricula orientacao;

	/** Utilizado na matrícula compulsória para pegar o ano.periodo que a matricula será realizada */
	private int anoPeriodo;

	/** Turmas das solicitações indeferidas */
	private HashSet<Turma> turmasIndeferidas = new HashSet<Turma>();

	/** Lista de tutorias do alunos */
	private Collection<TutoriaAluno> tutorias;

	/** Este atributo indica se o caso é matrícula de discente de outro programa
	 * utilizado apenas para stricto */
	private boolean matricularDiscenteOutroPrograma = false;

	/** Indica se é re-matrícula */
	private boolean rematricula = false;

	/** Indica os limites de créditos (máximos ou mínimos) de turmas matriculadas */
	private ExtrapolarCredito extrapolarCredito;
	/** Indica se é concordancia ou não */
	private boolean concordancia = false;
	/** Indica se é para exibir concordancia */
	private boolean exibirConcordancia = true;
	
	/** este atributo indica se a operação que está sendo executada é de definição de 
	 * horário de tutoria presencial que é executada pelos alunos EAD antes de poderem  
	 * realizar a matrícula on-line.*/
	private boolean alunoEadDefinindoHorarioTutoria = false;
	
	/** Turmas de entrada disponiveis para o discente*/
	private List <SelectItem> turmasEntradaCombo;
	private TurmaEntradaTecnico turmaEntradaSelecionada;
	
	/** Texto a ser exibido no botão confirmar. */
	private String confirmButtonHorarioTutoriaPresencial = "Confirmar";

	/** Turmas que foram solicitadas exclusão de matrícula. */
	private Collection<Turma> turmasSolicitadasExclusao;
	
	/** Se a solicitação de matrícula foi confirmada. */
	private boolean solicitacaoConfirmada; 
	
	/** Matrículas que o aluno solicita a remoção da sua lista por desistência. */
	private Set<MatriculaComponente> matriculasDesistencia;
	
	public void setTutorias(Collection<TutoriaAluno> tutorias) {
		this.tutorias = tutorias;
	}

	/** Construtor padrão. */
	public MatriculaGraduacaoMBean() {
	}

	/**
	 * Limpa os dados do MBean para sua utilização em uma nova operação de matrícula <br>
	 * JSP: Não invocado por JSP
	 */
	public void clear() {
		if (!isGraduacao())
			discente = new DiscenteStricto();
		else
			discente = new DiscenteGraduacao();
		discentesCurso = new ArrayList<Discente>(0);
		turmas = new HashSet<Turma>();
		situacao = new SituacaoMatricula();
		restricoes = RestricoesMatricula.getRestricoesRegular();
		if( SigaaSubsistemas.PORTAL_COORDENADOR.equals( getSubSistema() ) && isNotEmpty(getCursoAtualCoordenacao()) ){
			try {
				calendarioParaMatricula = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao() );
			} catch (DAOException e) {
				notifyError(e);
				e.printStackTrace();
				calendarioParaMatricula = getCalendarioVigente();
			}
		} else{
			calendarioParaMatricula = getCalendarioVigente();
		}
		dadosBuscaTurma = new Turma();
		dadosBuscaTurma.setDisciplina(new ComponenteCurricular());
		matricularDiscenteOutroPrograma = false;
		horarios = null;
		alunoEadDefinindoHorarioTutoria = false;
		confirmButtonHorarioTutoriaPresencial = "Confirmar";
		solicitacaoConfirmada = false;
	}

	/** 
	 * Verifica se a matrícula corresponde ao nível de graduação. <br>
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/turmas_curriculo.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/turmas_equivalentes_curriculo.jsp</li>
	 *   <li>/sigaa.war/stricto/matricula/turmas_programa.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isGraduacao() {
		return (discente != null && discente.getId() > 0 &&  discente.isGraduacao()) ||
			(!SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema())
			&& !SigaaSubsistemas.TECNICO.equals(getSubSistema())
			&& !SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema()));
	}

	/**
	 * Redireciona para a página com a lista das turmas abertas para os
	 * componentes pertencentes ao currículo do discente.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul>
	 * JSP: 
	 * @return
	 */
	public String listarSugestoesMatricula() {
		setOperacaoAtual("Turmas Abertas do Currículo do Aluno");
		return redirect("/graduacao/matricula/turmas_curriculo.jsf");
	}
	
	/**
	 * Redireciona para a página com a lista das turmas equivalentes abertas para os
	 * componentes pertencentes ao currículo do discente.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String listarSugestoesMatriculaEquivalentes() throws ArqException {
		setOperacaoAtual("Turmas Abertas do Currículo do Aluno");
		carregarTurmasEquivalentesCurriculo();
		return redirect("/graduacao/matricula/turmas_equivalentes_curriculo.jsf");
	}

	/** 
	 * Verifica se o ator da operação é um tutor de EAD. <br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/resumo_solicitacoes.jsp</li>
	 * </ul>
	 */
	public boolean isTutorEad() {
		return getAcessoMenu().isTutorEad()
			&& SigaaSubsistemas.PORTAL_TUTOR.equals(getSubSistema());
	}

	/** 
	 * Verifica se o ator da operação é um discente, que não tenha papel de secretário.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/_info_discente.jsp</li>
	 * </ul>
	 */
	public boolean isDiscenteLogado() {
		return (getUsuarioLogado().getDiscenteAtivo() != null) 
			&& (getCursoAtualCoordenacao() == null && tipoMatricula != ALUNO_RECEM_CADASTRADO);
	}
	
	/**
	 * Verifica se a operação está sendo realizada em modo otimizado.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/_info_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isModoOtimizado() {
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.PORTAL_DISCENTE_MODO_REDUZIDO);
	}

	/** 
	 * Verifica se o ator da operação é um chefe de departamento
	 */
	protected boolean isChefeDepartamento() {
		return getAcessoMenu().isChefeDepartamento();
	}

	/**
	 * Inicia o caso de uso de matrícula e vai para o caso correto de acordo com as configurações do MBean.<br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/confirmacao.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/resumo_solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{

		if( isMatricularDiscenteOutroPrograma() ){
			MatriculaDiscenteOutroProgramaMBean outroMBean =  (MatriculaDiscenteOutroProgramaMBean) getMBean("matriculaDiscenteOutroProgramaBean");
			return outroMBean.iniciar();
		}
		if( isSolicitacaoMatricula() )
			return iniciarSolicitacaoMatricula();
		else if( isMatriculaEAD() )
			return iniciarMatriculaEAD();
		else if( isMatriculaFerias() )
			return iniciarMatriculaFerias();
		else if( isForaPrazo() )
			return iniciarForaPrazo();
		/* A matrícula de aluno especial do stricto sensu é feita pelas coordenações dos programas de pós
		 * através do método iniciarMatriculasRegulares() */
		else if( isAlunoEspecial() && !(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema()) 
				&& isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS)) )
			return iniciarEspecial();
		else if( isCompulsoria() )
			return iniciarCompulsoria();
		else if( isMatriculaConvenio() )
			return iniciarMatriculaConvenio();
		else if( isAlunoRecemCadastrado() )
			return iniciarMatriculaRecemCadastrado();
		else if ( isMatriculaTurmasNaoOnline())
			return iniciarMatriculaTurmasNaoMatriculaveis();
		else
			return iniciarMatriculasRegulares();

	}

	/**
	 * Inicia a matrícula de aluno probásica.<br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * JSP: 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public String iniciarMatriculaConvenio() throws SegurancaException, ArqException {
		checkRole(SigaaPapeis.GESTOR_PROBASICA, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
		if (isUserInRole( SigaaPapeis.COORDENADOR_CURSO) && !getAcessoMenu().isCoordenadorCursoProbasica())
			throw new SegurancaException("Operação exclusiva de coordenadores de curso de PROBASICA");

		clear();
		situacao = SituacaoMatricula.MATRICULADO;

		tipoMatricula = MATRICULA_CONVENIO;

		restricoes = RestricoesMatricula.getRestricoesConvenio();
		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_GRADUACAO.getId());
		setOperacaoAtual("Matrícula PROBASICA");

		return buscarDiscente();
	}

	/**
	 * Inicia a matrícula de aluno da modalidade ensino a distância.<br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>SIGAA/app/sigaa.ear/sigaa.war/ead/menu.jsp</li>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public String iniciarMatriculaEAD() throws SegurancaException, ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD);

		if (!isUserInRole(SigaaPapeis.COORDENADOR_GERAL_EAD) && isUserInRole(SigaaPapeis.COORDENADOR_CURSO) && !getAcessoMenu().isCursoEad())
			throw new SegurancaException("Operação exclusiva de coordenadores de ensino a distância");
		
		if (!isPeriodoRegular() && !getCalendarioParaMatricula().isPeriodoMatriculaAlunoCadastrado() && !isUserInRole(SigaaPapeis.COORDENADOR_GERAL_EAD)){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Matrícula de Alunos EAD",
					getCalendarioParaMatricula().getInicioMatriculaAlunoCadastrado(), getCalendarioParaMatricula().getFimMatriculaAlunoCadastrado()));
			return null;
		}

		clear();
		tipoMatricula = MATRICULA_EAD;

		restricoes = RestricoesMatricula.getRestricoesEAD();
		situacao = SituacaoMatricula.MATRICULADO;
		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_GRADUACAO.getId());
		setOperacaoAtual("Matrícula EAD");
		return buscarDiscente();
	}

	/**
	 * Inicia o caso de uso para as matrículas de discentes especiais que são feitas pelo coordenador de cursos stricto sensu.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculasEspecial() throws ArqException {
		clear();
		tipoMatricula = ALUNO_ESPECIAL;
		return iniciarMatriculaRegularEspecial();
	}
	
	/**
	 * Inicia o caso de uso para as matrículas que são feitas pelo DAE e Coordenações.
	 * Nesse caso de uso, as matrículas nas turmas submetidas vão direto para o status EM ESPERA.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 *   <li>/sigaa.war/graduacao/secretaria.jsp</li>
	 *   <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculasRegulares() throws ArqException {
		clear();
		if ((getCalendarioParaMatricula().isPeriodoMatriculaRegular()
				|| getCalendarioParaMatricula().isPeriodoMatriculaTurmaFerias()) || isPortalPpg() || isPortalComplexoHospitalar()) {
			tipoMatricula = MATRICULA_REGULAR;
			return iniciarMatriculaRegularEspecial();
		} else {
			addMensagemErro("Não está no período de matrículas regulares ou de férias. Veja o calendário do programa.");
			return null;
		}
	}
	
	/** Iniciar a matrícula de alunos regulares ou especiais.
	 * @return
	 * @throws ArqException 
	 */
	private String iniciarMatriculaRegularEspecial() throws ArqException{
		if( !isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) 
				&&  !isPeriodoRegular() && ( getCursoAtualCoordenacao() != null && !getCursoAtualCoordenacao().isPodeMatricular() )){
			addMensagemErro("Não é permitido matrículas fora do período oficial de matrículas regulares");
			return null;
		}
		if( !isUserInRole(SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) && isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma operação de matrícula é permitida durante o período de processamento de matrículas");
			return null;
		}
		
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_CENTRO
				, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, 
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA);
		
		if (( SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema()) || SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema()) )&&
				getNivelEnsino() == NivelEnsino.GRADUACAO  && !getCursoAtualCoordenacao().isPodeMatricular()){
			addMensagemErro("O curso " + getCursoAtualCoordenacao() + " não está configurado para permitir que " +
					"matrículas de alunos ATIVOS sejam feitas pela coordenação");
			return null;
		}
		setOperacaoAtual("Matrícula On-Line");
		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_GRADUACAO.getId());
		if (!isUserInRole(SigaaPapeis.PPG) && getCalendarioParaMatricula().isPeriodoReMatricula()) {
			restricoes = RestricoesMatricula.getRestricoesReMatricula();
			rematricula = true;
		} else {
			rematricula = false;
			restricoes = RestricoesMatricula.getRestricoesRegular();
		}
		return buscarDiscente();
	}

	/**
	 * Inicia a matrícula de alunos cadastrados.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaRecemCadastrado() throws ArqException {
		clear();
		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma operação de matrícula é permitida durante o período de processamento de matrículas");
			return null;
		}
		checkRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO
				, SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG,
				SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_GRADUACAO);
		if (!getCalendarioParaMatricula().isPeriodoMatriculaAlunoCadastrado() &&
				!getCalendarioParaMatricula().isPeriodoReMatricula()){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Matrícula de Aluno Ingressante",
					getCalendarioParaMatricula().getInicioMatriculaAlunoCadastrado(), getCalendarioParaMatricula().getFimMatriculaAlunoCadastrado()));
			return null;
		}
		concordancia = true;
		setOperacaoAtual("Matrícula On-Line");
		prepareMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
		setOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA.getId());
		if (getCalendarioParaMatricula().isPeriodoReMatricula()) {
			restricoes = RestricoesMatricula.getRestricoesReMatricula();
			rematricula = true;
		} else {
			rematricula = false;
			restricoes = RestricoesMatricula.getRestricoesRegular();
		}
		tipoMatricula = ALUNO_RECEM_CADASTRADO;
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)) {
			tipoMatricula = SOLICITACAO_MATRICULA;
			DiscenteDao dao = getDAO(DiscenteDao.class);
			Collection<Curso> cursos = new ArrayList<Curso>(0);
			cursos.add(getCursoAtualCoordenacao());
			discentesCurso = dao.findByCursoAnoPeriodo(calendarioParaMatricula.getAno(), calendarioParaMatricula.getPeriodo(),
					cursos, true);
			if (isEmpty(discentesCurso)) {
				addMensagemErro("Não existe alunos recém-cadastrados no curso " + getCursoAtualCoordenacao());
				return null;
			}

			return telaSelecaoDiscente();
		} else {
			return buscarDiscente();
		}
	}

	/**
	 * Matrícula de turmas que não estão disponíveis na matrícula online.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaTurmasNaoMatriculaveis() throws ArqException {
		clear();
		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma operação de matrícula é permitida durante o período de processamento de matrículas");
			return null;
		}
		checkRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO
				, SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG,
				SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_GRADUACAO);
		if (!isPeriodoRegular() && !getCalendarioParaMatricula().isPeriodoMatriculaAlunoCadastrado()){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Matrícula de Turmas Não OnLine",
					getCalendarioParaMatricula().getInicioMatriculaAlunoCadastrado(), getCalendarioParaMatricula().getFimMatriculaAlunoCadastrado()));
			return null;
		}
		setOperacaoAtual("Matrícula de Turmas Não On-Line");
		prepareMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
		setOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA.getId());
		if (getCalendarioParaMatricula().isPeriodoReMatricula()) {
			restricoes = RestricoesMatricula.getRestricoesReMatricula();
			rematricula = true;
		} else {
			restricoes = RestricoesMatricula.getRestricoesRegular();
			rematricula = false;
		}
		tipoMatricula = TURMAS_NAO_ONLINE;
		return buscarDiscente();
	}

	/**
	 * Inicia o caso de uso para as matrículas de turmas de férias que são feitas pelas Coordenações de curso.
	 * Nesse caso de uso, as matrículas nas turmas submetidas vão direto para o status MATRICULADO.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 *   <li>/sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaFerias() throws ArqException {
		clear();
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.COORDENADOR_GERAL_EAD);
		if ( !isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE) && !isPeriodoMatriculaFerias() ){
			addMensagemErro("Não está no período de matrículas em turmas de férias.");
			return null;
		}
		tipoMatricula = MATRICULA_FERIAS;

		setOperacaoAtual("Matrícula em Curso Especial de Férias");

		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_GRADUACAO.getId());
		restricoes = RestricoesMatricula.getRestricoesTurmaFerias();

		return buscarDiscente();
	}

	/**
	 * Retorna um texto informando a situação do aluno.<br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/selecao_discentes.jsp</li>
	 * </ul>  
	 * @return
	 */
	public String getTituloSelecaoDiscente() {
		if (isMatriculaEAD())
			return "Matrícula de Ensino a Distância";
		else
			return "Matrícula de Discentes Recém-Cadastrados";
	}

	/**
	 * Redireciona para tela onde escolhe as restrições que serão aplicadas na matrícula do aluno escolhido.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String escolherRestricoes() {
		return forward("/graduacao/matricula/escolha_restricoes.jsp");
	}

	/**
	 * Redireciona para tela onde mostra um resumo com solicitações de turmas.
	 * <br/>
	 * Método não chamado por JSP.
	 * @return
	 */
	public String telaResumoSolicitacoes() {
		return redirect("/graduacao/matricula/resumo_solicitacoes.jsf");
	}

	/**
	 * Gera uma MD5 com base nas solicitações  de matrícula.
	 */
	private void generateMD5() {
		StringBuilder ids = new StringBuilder();
		ids.append(discente.getId());
		for (Turma t : getTurmas()) {
			ids.append(t.getId());
		}
		if (!isEmpty(solicitacoesAtividade)) {
			for (SolicitacaoMatricula sol : solicitacoesAtividade) {
				ids.append(sol.getComponente().getId());
			}
		}

		md5 = UFRNUtils.toMD5(ids.toString()).toUpperCase();
	}

	/**
	 * Redireciona para tela onde mostra a comprovação de matrícula.
	 * <br/>
	 * Método não chamado por JSP.
	 * @return
	 */
	public String gerarComprovanteSolicitacoes() {
		generateMD5();
		return telaComprovanteSolicitacoes();
	}

	/**
	 * Carrega as solicitações de matrícula e exibe.<br>
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/resumo_solicitacoes.jsp</li>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *   <li>/sigaa.war/stricto/matricula/opcoes.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String verComprovanteSolicitacoes() throws ArqException {
		SolicitacaoMatriculaDao sdao = getDAO(SolicitacaoMatriculaDao.class);
		CalendarioAcademico cal = getCalendarioParaMatricula();
		if (isDiscenteLogado())
			discente = getDiscenteUsuario();

		if ( isEmpty(discente) ) {
			addMensagemErro("Não foi possível identificar o discente para a emissão do comprovante de solicitação de matrícula.");
			return null;
		}
		
		if (MetropoleDigitalHelper.isMetropoleDigital(discente)){
			MatriculaModuloAvancadoMBean matriculaHorarioBean = getMBean("matriculaModuloAvancadoMBean");
			return matriculaHorarioBean.verComprovante();
		}
		
		int solicitacoes = sdao.countByDiscenteAnoPeriodo(discente.getDiscente(), cal.getAno(), cal.getPeriodo(),
				SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO,SolicitacaoMatricula.VISTA,
				SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA, SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);
		if (solicitacoes  == 0) {
			addMensagemWarning("Você ainda não realizou matrícula on-line no semestre " + getCalendarioParaMatricula().getAnoPeriodo());
			return null;
		}
		carregarSolicitacoesMatriculas();
		return gerarComprovanteSolicitacoes();
	}

	/**
	 * Direciona o usuário para a tela de comprovante das solicitações.
	 * <br/>
	 * Método não invocado por JSP´s
	 * @return
	 */
	public String telaComprovanteSolicitacoes() {
		return redirect("/graduacao/matricula/comprovante_solicitacoes.jsf");
	}

	/**
	 * Direciona o usuário para a tela de confirmação da matricula de graduação.<br>
	 * 
	 * JSP: Não invocado por JSP
	 * @return
	 * @throws Exception
	 */
	private String visualizarConfirmacao() throws Exception {
		return redirect("/graduacao/matricula/confirmacao.jsf");
	}

	/**
	 * Redireciona para a tela de informações da tutoria.
	 * <br/>
	 * Método não chamado por JSP.
	 * @return
	 */
	public String telaInfoTutoria() {
		return forward("/graduacao/matricula/info_tutoria.jsp");
	}

	/**
	 * Redireciona pra tela de buscar outras turmas (que não estão na grade curricular do aluno).<br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li> /sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul> 
	 * JSP:
	 * @return
	 * @throws DAOException 
	 */
	public String telaOutrasTurmas() throws DAOException {
		if (isAlunoEspecial()){
			if(discente.isGraduacao())
				dadosBuscaTurma.getDisciplina().setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
			else if(discente.isStricto())
				dadosBuscaTurma.getDisciplina().setUnidade(getGenericDAO().findByPrimaryKey(getProgramaStricto().getId(), Unidade.class));
		}
		setOperacaoAtual("Outras Turmas Abertas");
		return forward("/graduacao/matricula/turmas_extra_curriculo.jsp");
	}

	/**
	 * Redireciona para tela onde mostra as orientações de matrícula dadas pelo orientador acadêmico.<br>
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li> /sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 *   <li>  sigaa.war/graduacao/matricula/turmas_selecionadas.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String telaSolicitacoes() throws DAOException {
		setOperacaoAtual("Matrículas Submetidas");
		popularOrientacaoMatricula();
		return forward("/graduacao/matricula/turmas_analisadas.jsp");
	}

	/**
	 * Carrega a orientação de matrícula dada pelo orientador acadêmico
	 * @throws DAOException 
	 */
	private void popularOrientacaoMatricula() throws DAOException {
		CalendarioAcademico calendario = getCalendarioVigente();
		OrientacaoMatriculaDao orientacaoDao = getDAO(OrientacaoMatriculaDao.class);
		orientacao = orientacaoDao.findByDiscente(discente.getDiscente(), calendario.getAno(), calendario.getPeriodo());
	}

	/**
	 * Operação para que o discente possa acompanhar a análise de suas solicitações
	 * de matrícula.<br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul> 
	 * JSP: 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acompanharSolicitacoes() throws DAOException {
		// Popular discente
		DiscenteAdapter discenteUsuario = getUsuarioLogado().getDiscenteAtivo();
		if (discenteUsuario == null)  {
			addMensagemWarning("Somente discentes podem acompanhar a análise de suas orientações de matrícula");
			return null;
		} else {
			discente = discenteUsuario;
		}

		// Popular solicitações de matrícula
		popularSolicitacoesMatricula();
		if (isEmpty(solicitacoesMatricula) ) {
			addMensagemWarning("Você não possui solicitações de matrícula cadastradas para o período atual");
			return null;
		}

		getCurrentRequest().setAttribute("consulta", true);
		return telaSolicitacoes();
	}

	/**
	 * Redireciona para tela onde seleciona os alunos que o tutor tem responsabilidade.<br>
	 * 
	 * JSP: Não invocado por JSP
	 * @return
	 */
	public String telaSelecaoTutoria() {
		return forward("/graduacao/matricula/selecao_tutoria.jsp");
	}

	/**
	 * Tela onde seleciona um discente para fazer matrícula.<br>
	 * 
	 * JSP: Não invocado por JSP
	 * @return
	 */
	public String telaSelecaoDiscente() {
		return forward("/graduacao/matricula/selecao_discentes.jsp");
	}

	/**
	 * Tela onde mostra as turmas que o aluno selecionou.<br>
	 * JSP: Não invocado por jsp
	 * @param redirect
	 * @return
	 */
	public String telaSelecaoTurmas(boolean redirect) {
		setOperacaoAtual("Turmas Selecionadas");
		if (redirect)
			return redirect("/graduacao/matricula/turmas_selecionadas.jsf");
		return forward("/graduacao/matricula/turmas_selecionadas.jsf");
	}
	
	/** Retorna o Modelo de agenda utlizado para exibir a agenda das turmas que o discente está se matriculando/matriculado.
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public AgendaDataModel getTurmasAgendaModel() throws DAOException, SegurancaException {		
		turmaAgendaModel = new AgendaDataModel();
		Agenda agendaGeral = new Agenda();
		Collection<Turma> todas = new ArrayList<Turma>();
		todas.addAll(turmasJaMatriculadas);
		todas.addAll(turmas);
		for (Agenda agenda : AgendaFactory.getInstance().createFrom(todas)) {
			for (Evento evento : agenda.getEventos()) {
				agendaGeral.addEvento(evento);
			}
		}
		turmaAgendaModel = new AgendaDataModel(agendaGeral);
		
		return turmaAgendaModel;
	}

	/**
	 * Tela onde mostra as turmas que o aluno selecionou.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/resumo.jsp </li>
	 * </ul> 
 	 * @return
	 */
	public String telaSelecaoTurmas() {
		return telaSelecaoTurmas(true);
	}

	/**
	 * Direciona o usuário para a tela onde exibirá as turmas de férias.<br>
	 * 
	 * JSP: Não invocado por jsp. 
	 * 
	 * @return
	 */
	public String telaAlunosTurmaFerias() {
		return forward("/graduacao/matricula/discentes_ferias.jsp");
	}

	/**
	 * Vai para a tela de resumo, para exibir as turmas referentes ou
	 * às solicitações de matrícula do aluno.
	 * Nessa tela é solicitada a confirmação  da matrícula.<br>
	 * <br/><br/>
	 * Método chamado pelas seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String telaResumo() throws DAOException {
		// validação
		if (discente == null || discente.getId() == 0) {
			addMensagemErro("Escolha um discente");
			return null;
		}

		if ((isCompulsoria() || isForaPrazo()) && (situacao == null || situacao.getId() == 0)) {
			addMensagemErro("O status da turma deve ser selecionado");
			return null;
		}
		setOperacaoAtual("Confirmação");
		discente.setGestoraAcademica( getGenericDAO().refresh(discente.getGestoraAcademica()) );
		discente.getGestoraAcademica().getNomeMunicipio();

		return redirect("/graduacao/matricula/resumo.jsf");
	}

	/**
	 * Verifica se esta no prazo de realizar matrícula regular
	 * @return
	 */
	private boolean isPeriodoRegular() {
		return getCalendarioParaMatricula().isPeriodoMatriculaRegular();
	}

	/**
	 * Verifica se está no período de processamento de matrícula.
	 * @return
	 */
	protected boolean isPeriodoProcessamento() {
		return getCalendarioParaMatricula().isPeriodoProcessamento();
	}

	/**
	 * Testa se está no período de matricula de turma de ferias, ou seja,
	 * os dois primeiros dias úteis de semestres de ferias (3 e 4)
	 * @return
	 */
	private boolean isPeriodoMatriculaFerias(){
		return getCalendarioParaMatricula().isPeriodoMatriculaTurmaFerias();
	}

	/**
	 * Faz uma série de validações antes de dar inicio a matrícula do aluno
	 * @throws DAOException 
	 */
	private void validarInicioSolicitacaoMatricula() throws DAOException {

		if (getCalendarioParaMatricula() == null) clear();

		if (SigaaSubsistemas.PORTAL_DISCENTE.equals(getSubSistema()) || SigaaSubsistemas.PORTAL_TURMA.equals(getSubSistema()) ){
			setSubSistemaAtual(SigaaSubsistemas.PORTAL_DISCENTE);
			discente = getUsuarioLogado().getDiscenteAtivo();
			getCurrentSession().setAttribute("nivel", discente.getNivel());
		}

		if (( (isTutorEad() && getDiscente().isDiscenteEad()) || isDiscenteLogado()) && !isPeriodoRegular()){
			addMensagemErro("Não está no período oficial de matrículas on-line");
			return;
		}

		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma operação de matrícula é permitida durante o período de processamento de matrículas");
			return;
		}
		
		boolean alunoEadFazMatricula = ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.ALUNO_EAD_FAZ_MATRICULA_ONLINE);
		boolean alunoEspecialFazMatricula = ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.ALUNO_ESPECIAL_FAZ_MATRICULA_ONLINE);

		if (discente != null) {
			// alunos fazendo solicitação de matricula
			// Se o ator for o aluno, buscar seus dados em DiscenteGraduacao
			if ((!getDiscente().isRegular() && !alunoEspecialFazMatricula )
					|| (getDiscente().getCurso() != null && getDiscente().getCurso().isProbasica())
					|| (!isTutorEad() && getDiscente().isDiscenteEad() && !alunoEadFazMatricula )
					|| (!getDiscente().isGraduacao() &&  !getDiscente().isTecnico())) {
				addMensagemErro("Essa operação está disponível apenas para discentes de graduação regulares sem convênio acadêmico");
				return;
			}
			
			boolean alunoEspecialFazRematricula = (!getDiscente().isRegular() && getCalendarioParaMatricula().isPeriodoReMatricula() &&
					( getDiscente().getFormaIngresso().getCategoriaDiscenteEspecial() != null && 
					  getDiscente().getFormaIngresso().getCategoriaDiscenteEspecial().isPermiteRematricula() ));
			
			if((!getDiscente().isRegular() && alunoEspecialFazMatricula && !getCalendarioParaMatricula().isPeriodoMatriculaAlunoEspecial()) && !alunoEspecialFazRematricula ){
				addMensagemErro("Não está no período oficial de matrículas on-line");
				return;
			}

			/* Regras de obrigatoriedade: <br/>
			 * RESOLUÇÃO No 028/2010-CONSAD, de 16 de setembro de 2010
			 * Art. 3o
			 * I - tratando-se de discente
			 * a) imposibilidade de se efetuar matricula em disciplina.
			 */
			try {
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiInrregularidadeAdministrativa(discente.getPessoa().getId());
			} catch (NegocioException ne) {
				addMensagemErro(ne.getMessage());
				return;
			}
			
			if ( !getDiscente().isMetropoleDigital() && !StatusDiscente.getStatusMatriculavelGraduacao().contains(discente.getStatus()) ) {
				addMensagemErro("Apenas alunos ativos podem realizar matrículas.");
				return;
			}

			if (discente.isGraduacao() &&  ((DiscenteGraduacao)discente).isEAD() && !alunoEadFazMatricula && !isTutorEad()) {
				addMensagemErro("Discentes de ensino a distância não realizam solicitação de matrícula on-line. Quem realiza é o seu tutor.");
				return;
			}
			
			if (discente.isGraduacao() 
					&& ((DiscenteGraduacao)discente).isEAD() 
					&& ParametroHelper.getInstance().getParametroBoolean(ParametrosEAD.OBRIGATORIEDADE_EM_DEFINIR_HORARIO_PARA_TUTORIA) ) {
				
				//TutoriaAluno ta = getGenericDAO().findByPrimaryKey(id, TutoriaAluno.class);
				//discente = getGenericDAO().findByPrimaryKey(ta.getAluno().getId(), DiscenteGraduacao.class);
				CalendarioAcademico cal = getCalendarioParaMatricula();
				
				HorarioTutoriaDao hdao = getDAO(HorarioTutoriaDao.class);
				horariosTutoria = hdao.findByDiscente(getDiscente().getId(), cal.getAno(), cal.getPeriodo());
				if ( !isAlunoEadDefinindoHorarioTutoria() && isEmpty(horariosTutoria)) {
					addMensagemErro("Antes de iniciar a matrícula é necessário configurar os horários para tutoria presencial através do menu "+RepositorioDadosInstitucionais.get("siglaSigaa")+" -> Portal Discente -> Ensino -> Definir horário de tutoria presencial.");
					return;
				}
				
			}
			
			if (discente.isGraduacao()){
				/* Verifica se o Discente Possui mobilidade estudantil no período vigente. */
				MobilidadeEstudantilDao mobDao = getDAO(MobilidadeEstudantilDao.class);
				try {					
					if (mobDao.possuiMobilidadeAtivaNoPeriodo(discente, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo())){
						addMensagemErro("Não é possível realizar matrícula, pois existe uma Mobilidade Estudantil Ativa.");
						return;
					}
				} finally {
					if (mobDao != null)
						mobDao.close();
				}
			}

			if (discente.isTecnico()){
				DiscenteDao discenteDao = getDAO(DiscenteDao.class);
				try{
					setDiscente( discenteDao.findByPK(getDiscente().getId()) );
					
					if (((DiscenteTecnico)discente).getTurmaEntradaTecnico() == null){
						
						// Se o discente é do metópole digital e não tem turma de entrada, deve selecionar uma.
						if (!getDiscente().isMetropoleDigital()) {
							addMensagemErro("Não é possível realizar matrículas de alunos sem turma de entrada cadastrada");
						}
						
						return;
					} else {
						if (getDiscente().isMetropoleDigital()) {
							addMensagemErro("Você já realizou sua matrícula");
							return;
						}
					}
					
					if(((DiscenteTecnico) discente).getTurmaEntradaTecnico().getEspecializacao() != null){
						((DiscenteTecnico) discente).getTurmaEntradaTecnico().getEspecializacao().getId();
						((DiscenteTecnico) discente).getTurmaEntradaTecnico().getEspecializacao().getDescricao();
					}
					
					/* Verifica se existe disciplinas pendentes, caso contrário o aluno está concluído, não podendo se matricular */
					List<MatriculaComponente> disciplinas = discenteDao.findDisciplinasConcluidasMatriculadas(discente.getId(), true);
					Collection<ComponenteCurricular> componentesPendentes = discenteDao.findComponentesPendentesTecnico(discente.getId(), disciplinas);
					if (componentesPendentes.isEmpty()){
						addMensagemErro("Não é possível realizar matrícula, pois todos os Componentes Curriculares obrigatórios já foram pagos.");
						return;
					}
				} finally {
					if (discenteDao != null)
						discenteDao.close();
				}								
			}
			
		} else {
			addMensagemErro("Essa operação é permitida apenas para alunos de graduação");
			return;
		}
	}

	/**
	 * Inicia o caso de uso feito para solicitar matrículas em turmas. Os atores são os alunos de graduação e
	 * os tutores de EAD que solicitam matrículas dos seus alunos.
	 * Esse solicitação será analisada pela coordenação do curso antes de serem efetivadas (passarem ao status EM ESPERA).
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/instrucoes_tecnico.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/instrucoes.jsp</li>
	 *   <li>/sigaa.war/portais/discente/menu_discente_testes.jsp</li>
	 *   <li>/sigaa.war/portais/tutor/menu_tutor.jsp</li>
	 * </ul> 
	 * @return++
	 * @throws ArqException
	 */
	public String iniciarSolicitacaoMatricula() throws ArqException {
		if (ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.CONCORDANCIA_REGULAMENTO)
				&& discente != null && discente.isGraduacao() && !discente.isDiscenteEad()) {
			if(!concordancia){
				addMensagemErro("Atenção: você deve baixar uma cópia eletrônica do novo regulamento dos cursos de graduação e declarar que está ciente das " +
						"alterações, marcando na opção correspondente antes de poder iniciar sua matrícula.");
				return null;
			} else if(exibirConcordancia) {
				prepareMovimento(SigaaListaComando.CONCORDAR_REGULAMENTO);
				setOperacaoAtiva(SigaaListaComando.CONCORDAR_REGULAMENTO.getId());
				MovimentoSolicitacaoMatricula mov = new MovimentoSolicitacaoMatricula();
				mov.setDiscente(discente);
				mov.setCodMovimento(SigaaListaComando.CONCORDAR_REGULAMENTO);
				try {
					execute(mov);
				} catch (NegocioException e) {
					addMensagemErro(e.getMessage());
					return null;
				}
			}
		}
		
		rematricula = getCalendarioParaMatricula().isPeriodoReMatricula();
		carregarRestricoesMatricula();

		tipoMatricula = SOLICITACAO_MATRICULA;

		if (!isDiscenteLogado() && !isTutorEad()) {
			checkRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.CDP
					, SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG,
					SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_GRADUACAO);
			setOperacaoAtual("Matrícula OnLine");
			prepareMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
			setOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA.getId());
			return buscarDiscente();
		}
		
		if (isTutorEad()) {
			setOperacaoAtual("Matrícula OnLine");
			prepareMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
			setOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA.getId());
			setConfirmButtonHorarioTutoriaPresencial("Próximo passo >>");
			return telaSelecaoTutoria();
		} else {
			if (isEmpty(discente)) {
				addMensagemErro("Atenção: A operação de solicitação de matrícula deve ser iniciada novamente para que os dados sejam populados corretamente.");
				return cancelar();
			}

			// Validar início para discentes de graduação
			if (discente.isGraduacao() && discente.isRegular()) {
				// Só deixar o discente fazer a matrícula se ele tiver preenchido a avaliação institucional
				int ano = calendarioParaMatricula.getAnoAnterior();
				int periodo = calendarioParaMatricula.getPeriodoAnterior();

				AvaliacaoInstitucionalDao avaliacaoDao = getDAO(AvaliacaoInstitucionalDao.class);
				if (discenteDeveriaFazerAvaliacaoInstitucional(ano, periodo)) {
					CalendarioAvaliacao calAvaliacao = AvaliacaoInstitucionalHelper.getCalendarioAvaliacaoAtivo(discente);
					if (calAvaliacao != null && !avaliacaoDao
							.isAvaliacaoFinalizada(discente.getDiscente(), ano, periodo, calAvaliacao.getFormulario().getId())) {
						addMensagemErro("Não é possível realizar a matrícula ainda porque você não preencheu a Avaliação Institucional " +
							"referente ao período " + ano + "." + periodo);
						return null;
					}
					// turmas com docência assistida
					// está no período?
					CalendarioAvaliacaoDao calAvalDao = getDAO(CalendarioAvaliacaoDao.class);
					if (calAvalDao.hasCalendarioAtivo(TipoAvaliacaoInstitucional.AVALIACAO_DOCENCIA_ASSISTIDA, discente.isDiscenteEad())) { 
						if (!avaliacaoDao.isDocenciaAssistidaFinalizada(discente.getDiscente(), ano, periodo)) {
							addMensagemErro("Não é possível realizar a matrícula ainda porque você não preencheu a Avaliação Institucional da Docência Assistida " +
									"referente ao período " + ano + "." + periodo + ".");
								return null;
						}
					}
				}
			}

			if (discente.isTecnico() && !isPassivelSolicitacaoTecnico()) {
				return null;
			}

			if ( isNecessariaAtualizacaoDadosDiscente() ) {
				addMensagemWarning("ATENÇÃO: antes de realizar a matrícula é necessário atualizar seus dados pessoais.");
				AlteracaoDadosDiscenteMBean bean = getMBean("alteracaoDadosDiscente");
				return bean.iniciarAcessoDiscente();
			}
			setOperacaoAtual("Matrícula OnLine");
			prepareMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
			setOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA.getId()); 
			return selecionaDiscente();
		}
	}
	
	public String iniciarSolicitacaoMatriculaIMD () throws Exception {
		
		if (!confirmaSenha())
			return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(getDiscente());
		mov.setObjAuxiliar(turmaEntradaSelecionada);
		mov.setCodMovimento(SigaaListaComando.SELECIONAR_TURMA_ENTADA_IMD);
		execute(mov);
		
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			dao.initialize(turmaEntradaSelecionada);
			turmas = dao.findByExactField(Turma.class, "especializacao.id", turmaEntradaSelecionada.getEspecializacao().getId());
			setOperacaoAtiva(SigaaListaComando.SELECIONAR_TURMA_ENTADA_IMD.getId());
			tipoMatricula = MATRICULA_REGULAR;
			restricoes = RestricoesMatricula.getRestricoesNenhuma();
			
			calendarioParaMatricula = CalendarioAcademicoHelper.getCalendario(discente);
			
			return confirmarSubmissaoSolicitacao();
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Carrega as restrições a serem verificadas durante a operação de matrícula online
	 */
	private void carregarRestricoesMatricula() {
		if( isTutorEad() ) {
			restricoes = RestricoesMatricula.getRestricoesEAD();
		} else if (discente.isRegular()) {
			if (rematricula) {
				restricoes = RestricoesMatricula.getRestricoesReMatricula();
			} else {
				restricoes = RestricoesMatricula.getRestricoesRegular();
			}
		} else if (discente.isGraduacao() && !discente.isRegular()) {
			restricoes = RestricoesMatricula.getRestricoesAlunoEspecial();
		}
	}

	/**
	 * Valida a obrigatoriedade e necessidade de atualização dos dados pessoais dos discentes
	 * @throws DAOException 
	 */
	private boolean isNecessariaAtualizacaoDadosDiscente() throws DAOException {
		Boolean atualizacaoSolicitada = getParametrosAcademicos().getSolicitarAtualizacaoDadosMatricula();
		Date ultimaAtualizacao = discente.getPessoa().getUltimaAtualizacao();
		
		return atualizacaoSolicitada != null && atualizacaoSolicitada 
				&& (ultimaAtualizacao == null || ultimaAtualizacao.before(getCalendarioParaMatricula().getInicioMatriculaOnline()));
	}

	/**
	 * Verifica se o discente de nível técnico pode realizar solicitações de matrícula
	 * online
	 *
	 * @return
	 * @throws DAOException
	 */
	private boolean isPassivelSolicitacaoTecnico() throws DAOException {
		if (discente == null || !discente.isTecnico()) {
			return false;
		}

		if (discente.getGestoraAcademica().getId() == Unidade.ESCOLA_MUSICA) {
			// Contar o total de matrículas pendentes de consolidação para o período anterior
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
			int totalMatriculados = matriculaDao.countMatriculasByDiscente(discente.getDiscente(),
					calendarioParaMatricula.getAnoAnterior(), calendarioParaMatricula.getPeriodoAnterior(),
					SituacaoMatricula.MATRICULADO);


			if (totalMatriculados > 0) {
				addMensagemWarning("Por determinação da " + discente.getGestoraAcademica().getNome() +
						" , sua matrícula só poderá ser " +
						" iniciada depois que todas as suas turmas do semestre passado " +
						" (" + calendarioParaMatricula.getAnoPeriodoAnterior() +")" +
						" sejam consolidadas pelos respectivos professores.");
				return false;
			}
			
			ListaMensagens erros = new ListaMensagens();
			MatriculaTecnicoValidator.validarReprovacoes(discente.getDiscente(), erros);
			if (!erros.isEmpty()) {
				addMensagens(erros);
				return false;
			}
		}
		return true;
	}

	/**
	 * Identifica se o discente estava matriculado em turmas em um determinado período.
	 * Utilizado para identificar se o discente deveria realizar a avaliação institucional.
	 */
	protected boolean discenteDeveriaFazerAvaliacaoInstitucional(int ano, int periodo) throws DAOException {
		return getDAO(TurmaDao.class).discenteEstavaMatriculadoNoPeriodo(getUsuarioLogado().getDiscenteAtivo().getDiscente(), ano, periodo);
	}

	/** 
	 * Lista de status possíveis para a matrícula após a execução do caso de uso.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/escolha_restricoes.jsp</li>
	 * </ul> 
	 * JSP: 
	 */
	public List<SelectItem> getStatus() {
		List<SelectItem> status = new ArrayList<SelectItem>();
		if (isCompulsoria() || isForaPrazo()) {
			status.add(new SelectItem(SituacaoMatricula.EM_ESPERA.getId(), SituacaoMatricula.EM_ESPERA.getDescricao()));
			status.add(new SelectItem(SituacaoMatricula.MATRICULADO.getId(), SituacaoMatricula.MATRICULADO.getDescricao()));
		}
		return status;
	}

	/**
	 * Inicia caso de uso em que podem-se realizar matrículas em qualquer período,
	 *  o status da matrícula é escolhido pelo usuário e as restrições a serem verificadas também
	 *  são escolhidas.<br>
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarCompulsoria() throws ArqException {
		return iniciarCompulsoriaForaPrazo(SigaaListaComando.MATRICULA_COMPULSORIA);
	}

	/**
	 * Inicia caso de uso no qual realiza-se matrículas em qualquer período. E pode-se escolher o status
	 * da matrícula EM ESPERA ou direto em MATRICULADO.
	 * Nesse caso de uso todas as restrições normais são analisadas.<br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarForaPrazo() throws ArqException {
		clear();
		if (isPeriodoRegular()){
			addMensagemErro("Ainda está no período regular de matrículas");
			return null;
		}
		restricoes = RestricoesMatricula.getRestricoesRegular();
		return iniciarCompulsoriaForaPrazo(SigaaListaComando.MATRICULA_FORA_PRAZO);
	}

	/**
	 * Prepara o movimento para matrícula fora de prazo ou compulsória.
	 * 
	 * @param comando
	 * @return
	 * @throws ArqException 
	 */
	private String iniciarCompulsoriaForaPrazo(Comando comando) throws ArqException {
		clear();
		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma operação de matrícula é permitida durante o período de processamento de matrículas");
			return null;
		}
		// Apenas DAE e CDP podem executar essa operação
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.CDP });
		prepareMovimento(comando);
		setOperacaoAtiva(comando.getId());

		// Redirecionar para a busca de discentes
		if (comando.equals(SigaaListaComando.MATRICULA_COMPULSORIA)) {
			tipoMatricula = COMPULSORIA;
			return buscarDiscenteCompulsoria();
		} else {
			tipoMatricula = FORA_PRAZO;
			return buscarDiscenteForaPrazo();
		}

	}

	/**
	 * Inicia caso de uso que realiza matrículas para alunos especiais
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 *   <li>/sigaa.war/graduacao/departamento.jsp</li>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarEspecial() throws ArqException {
		clear();
		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma operação de matrícula é permitida durante o período de processamento de matrículas");
			return null;
		}
		checkRole(new int[] { SigaaPapeis.CHEFE_DEPARTAMENTO,SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.DAE });
		if( getCalendarioParaMatricula() == null ){
			addMensagemErro("Não foi possível carregar o calendário vigente, contacte a administração.");
			return null;
		}
		if (!getCalendarioParaMatricula().isPeriodoMatriculaAlunoEspecial()) {
			addMensagemErro("Não é permitido realizar matrículas de alunos especiais fora do " +
					"período determinado no calendário universitário");
			return null;
		}

		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_GRADUACAO.getId());
		
		restricoes = RestricoesMatricula.getRestricoesAlunoEspecial();
		restricoes.setCapacidadeTurma(false);
		tipoMatricula = ALUNO_ESPECIAL;
		return buscarDiscenteEspecial();
	}

	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/discentes_ferias.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/info_tutoria.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String buscarDiscente() throws ArqException {
		setOperacaoAtual("Busca de Discente");
		if( isMatricularDiscenteOutroPrograma() ){
			MatriculaDiscenteOutroProgramaMBean outroMBean =  (MatriculaDiscenteOutroProgramaMBean) getMBean("matriculaDiscenteOutroProgramaBean");
			return outroMBean.iniciar();
		}
		if (isAlunoRecemCadastrado() && !SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema()))
			return  buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_ALUNO_CADASTRADO);
		else if (isAlunoRecemCadastrado() && SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema()))
			return  telaSelecaoDiscente();
		else if (isMatriculaTurmasNaoOnline())
			return  buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_TURMAS_NAO_ONLINE);
		else if (isSolicitacaoMatricula() && isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP))
			return  buscarDiscenteMatricula(OperacaoDiscente.SOLICITACAO_MATRICULA_DAE);
		else if (isSolicitacaoMatricula() && isTutorEad())
			return  telaSelecaoTutoria();
		else if (isSolicitacaoMatricula())
			return  buscarDiscenteMatricula(OperacaoDiscente.SOLICITACAO_MATRICULA);
		else if(!isSolicitacaoMatricula() && SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema()) 
				&& isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS)){
			if (tipoMatricula == ALUNO_ESPECIAL) {
				restricoes = RestricoesMatricula.getRestricoesAlunoEspecial();
				return buscarDiscenteEspecial();
			} else {
				restricoes = RestricoesMatricula.getRestricoesRegular();
				return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA);
			}
		}else
			return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA);
	}

	/**
	 * Busca alunos para a realização da matrícula compulsória.<br>
	 * JSP: Não invocado por JSP
	 * @return
	 */
	public String buscarDiscenteCompulsoria() {
		return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_COMPULSORIA);
	}

	/**
	 * Busca alunos para realizar matrícula fora do prazo.<br>
	 * JSP: Não invocado por JSP
	 * @return
	 */
	public String buscarDiscenteForaPrazo() {
		return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_FORA_PRAZO);
	}

	/**
	 * Busca alunos especiais para realizar matrícula.<br>
	 * JSP: Não invocado por JSP
	 * @return
	 */
	public String buscarDiscenteEspecial() {
		return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_ALUNO_ESPECIAL);
	}

	/**
	 * Busca alunos para realizar matrícula de férias.<br>
	 * JSP: Não invocado por JSP
	 * @return
	 */
	public String buscarDiscenteFerias() {
		return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_ALUNO_FERIAS);
	}

	/**
	 * Busca alunos de acordo com a operação
	 * @param operacao
	 * @return
	 */
	private String buscarDiscenteMatricula(int operacao) {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(operacao);
		if (isMatriculaEAD()){
			buscaDiscenteMBean.setEad(true);
			if(isMatriculaFerias())
				this.setRestricoes(RestricoesMatricula.getRestricoesEADFerias());
		}

		String forward = buscaDiscenteMBean.popular();

		if( SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) && isUserInRole( SigaaPapeis.PPG ) )
			buscaDiscenteMBean.getOperacao().setTiposValidos(new int[] { Discente.REGULAR, Discente.ESPECIAL });

		return forward;
	}

	/**
	 * Inicia a etapa de seleção de turmas.<br>
	 * 
	 * JSP: Não invocado por JSP
	 * @return
	 */
	public String iniciarSelecaoTurmas() {
		if (isEmpty(getTurmas()))
			return listarSugestoesMatricula();
		else
			return telaSelecaoTurmas(false);
	}

	/**
	 * Direciona o usuário para a tela de instruções de matrícula.<br>
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul> 
	 * JSP: 
	 * @return
	 * @throws ArqException 
	 */
	public String telaInstrucoes() throws ArqException {
		
		
		clear();
		validarInicioSolicitacaoMatricula();
		

		// Verifica se o coordenador do curso liberou alguma restrição para o aluno.
		// por enquanto as únicas restrições sendo liberadas são limite máximo e mínimo de créditos por semestre.
		permissaoExtrapolarCredito();

		if (hasOnlyErrors())
			return null;
		if (discente.isGraduacao()){
			if(ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.CONCORDANCIA_REGULAMENTO)) {
				exibirConcordancia = getDAO(DiscenteDao.class).verificaConcordanciaRegulamento(discente.getId());
				concordancia = !exibirConcordancia;
			}
			
			if(ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.ATUALIZAR_DADOS_PESSOAIS_MATRICULA_ONLINE)) {
				PessoaDao dao = getDAO(PessoaDao.class);
				if(dao.findUltimaAtualizacao(discente.getPessoa().getId()).getTime() < getCalendarioParaMatricula().getInicioMatriculaOnline().getTime()){
					AlteracaoDadosDiscenteMBean alteracaoDadosBean = getMBean("alteracaoDadosDiscente");
					alteracaoDadosBean.setAcessoMatricula(true);
					alteracaoDadosBean.setConfirmButton("Confirmar alteração e retornar para matrícula on-line");
					return alteracaoDadosBean.iniciarAcessoDiscente();
				}
			}
			
			String paginaInstrucoes = discente.isRegular() ? "regular.jsp" : "especial.jsp";
			return forward("/graduacao/matricula/instrucoes/instrucoes_" + paginaInstrucoes);
		} else if (discente.isTecnico()){		
			if(MetropoleDigitalHelper.isMetropoleDigital(discente)){
				MatriculaModuloAvancadoMBean matriculaModuloAvancadoMBean = getMBean("matriculaModuloAvancadoMBean");
				return matriculaModuloAvancadoMBean.iniciarDiscente();
			}
			GenericDAO dao = getGenericDAO();
			DiscenteTecnico dt = (DiscenteTecnico) discente;
			
			if (dt.isMetropoleDigital() && dt.getTurmaEntradaTecnico() == null){
				
				turmaEntradaSelecionada = new TurmaEntradaTecnico ();
				
				if (getDiscente().getStatus() != StatusDiscente.CADASTRADO){
					addMensagemErro("Somente discentes com status \"Cadastrado\" podem efetuar matrícula.");
					return null;
				}
				
				TurmaEntradaTecnicoDao teDao = null;
				
				try {
				teDao = getDAO(TurmaEntradaTecnicoDao.class);
				
				turmasEntradaCombo = teDao.findTurmasEntradaDisponiveisParaDiscenteIMD (getDiscente().getId());
				
				prepareMovimento(SigaaListaComando.SELECIONAR_TURMA_ENTADA_IMD);
				
				return forward ("/graduacao/matricula/instrucoes_imd.jsp");
				} finally {
					if (dao != null)
						dao.close();
				}
			}
			
			dt.setEstruturaCurricularTecnica(dao.refresh(dt.getEstruturaCurricularTecnica()));
			return forward("/graduacao/matricula/instrucoes_tecnico.jsp");
		}
		addMensagemErro("Essa operação é permitida apenas para alunos de Graduação e Técnico");
		return null;
	}
	
	/**
	 * Verifica se o discente tem permissão para extrapolar o número de créditos para o semestre atual.
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("deprecation") // por mudança no modelo de limite de créditos extrapolados na matrícula
	protected void permissaoExtrapolarCredito() throws HibernateException, DAOException {
		ExtrapolarCreditoDao ecDao = getDAO(ExtrapolarCreditoDao.class);
		extrapolarCredito = ecDao.findPermissaoAtivo(discente.getDiscente(), getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo() );

		if (extrapolarCredito != null) {
			// caso a permissão seja no modelo antigo, sem valor máximo:
			if (extrapolarCredito.getCrMaximoExtrapolado() == null) {
				if (extrapolarCredito.isExtrapolarMaximo())
					restricoes.setLimiteMaxCreditosSemestre(false);
				else
					restricoes.setLimiteMinCreditosSemestre(false);
			} else {
				// modelo novo, com valor máximo e mínimo
				restricoes.setValorMaximoCreditos(extrapolarCredito.getCrMaximoExtrapolado());
				restricoes.setValorMinimoCreditos(extrapolarCredito.getCrMinimoExtrapolado());
			}
		}
	}

	/** Combo de calendários para ser escolhido na matrícula compulsória
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/escolha_restricoes.jsp</li>
	 * </ul>
	 * @throws DAOException */
	public Collection<SelectItem> getCalendariosPossiveis() throws DAOException {
		ArrayList<SelectItem> combo = new ArrayList<SelectItem>(0);
		CalendarioAcademicoDao dao =getDAO(CalendarioAcademicoDao.class);
		if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE)) {
			Collection<CalendarioAcademico> cals = dao.findByUnidade(Unidade.UNIDADE_DIREITO_GLOBAL, NivelEnsino.GRADUACAO);
			for (CalendarioAcademico c : cals) {
				if (c.getAnoFeriasVigente() != null && c.getPeriodoFeriasVigente() != null)	
					combo.add( new SelectItem( c.getAnoFeriasVigente() + "" + c.getPeriodoFeriasVigente(), c.getAnoPeriodoFeriasVigente() ) );
				combo.add(new SelectItem(c.getAno() + "" + c.getPeriodo(), c.getAnoPeriodo()));
			}
		} else {
			combo.add(new SelectItem(getCalendarioParaMatricula().getAno() + "" + getCalendarioParaMatricula().getPeriodo(), getCalendarioParaMatricula().getAnoPeriodo()));
		}
		return combo;
	}

	/**
	 * Carrega solicitações previamente cadastradas e ainda não submetidas
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/selecao_discentes.jsp</li>
	 * </ul>
	 */
	public String selecionaDiscente() throws ArqException {

		// Se o discente ainda não foi carregado
 		DiscenteDao ddao = getDAO(DiscenteDao.class);
		if (!isDiscenteLogado()) {
			limparSugestoes();
			
			Integer id = getParameterInt("idDiscente");
			if (id != null) {
				if (discente.isGraduacao())
					discente = ddao.findByPrimaryKey(id, DiscenteGraduacao.class);
				else
					discente = ddao.findByPK(id);
			}
			if( calendarioParaMatricula == null )
				calendarioParaMatricula = CalendarioAcademicoHelper.getCalendario(discente);
			CalendarioAcademico cal  = getCalendarioParaMatricula();
			if (isAlunoRecemCadastrado() && !discente.isCadastrado(cal.getAno(), cal.getPeriodo()) ) {
				addMensagemErro("Essa operação só permite selecionar discente cadastrados com entrada no ano-período atual");
				return null;
			}
			if (discente.isTecnico()) {
				((DiscenteTecnico) discente).getTurmaEntradaTecnico().getEspecializacao().getDescricao();
			}
			
		}

		popularComponentesTurmas();

		// validações e decisão de navegação
		if (isSolicitacaoMatricula() || isAlunoRecemCadastrado()) {
			if (!isDiscenteLogado() && !isAlunoRecemCadastrado() && !isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP)) {
				validarInicioSolicitacaoMatricula();
			}
			escolhaAlunoParaSolicitacao();
			if (isMatriculaTurmasNaoOnline() 
					|| (discente.isGraduacao() && !discente.isRegular()))
				return telaOutrasTurmas();
			else
				return iniciarSelecaoTurmas();
		} else 	if (isCompulsoria()) {
			setOperacaoAtual("Escolha de Restrições a Ignorar e Status da Matrícula");
			return escolherRestricoes();
		} else 	if (isForaPrazo()) {
			setOperacaoAtual("Escolha do Status das Matrículas");
			return escolherRestricoes();
		} else if (isMatriculaEAD() && !isMatriculaFerias()) {
			escolhaAlunoParaSolicitacao();
			DiscenteGraduacao dg = (DiscenteGraduacao) discente;
			dg.getPolo().getDescricao();
			return iniciarSelecaoTurmas();
		} else if (isMatriculaConvenio()) {
			return escolhaAlunoDeConvenio();
		} else if (NivelEnsino.isAlgumNivelStricto(getNivelEnsino())) {
			if( !isUserInRole(SigaaPapeis.PPG) ){
				dadosBuscaTurma.getDisciplina().setUnidade( getGenericDAO().findByPrimaryKey(getProgramaStricto().getId(), Unidade.class) );
				boolDadosBusca[4] = true;
			}
			dadosBuscaTurma.setAno( calendarioParaMatricula.getAno() );
			dadosBuscaTurma.setPeriodo( calendarioParaMatricula.getPeriodo() );
			return telaOutrasTurmas();
		} else {
			return telaOutrasTurmas();
		}

	}

	/**
	 * Método que limpa as coleções de sugestões de matrícula
	 */
	private void limparSugestoes() {
		turmasCurriculo = null;
		turmasEquivalentesCurriculo = null;
	}

	/**
	 * Popula os Componentes Curriculares das Turmas.
	 * Método não chamado por JSP.
	 * @param ddao
	 * @throws DAOException
	 */
	public void popularComponentesTurmas() throws DAOException {
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		DiscenteDao ddao = getDAO(DiscenteDao.class);
		RegistroAtividadeDao rDao = getDAO(RegistroAtividadeDao.class);

		// Carregar turmas já matriculadas por esse discente
		turmasJaMatriculadas = ddao.findTurmasMatriculadas(discente.getId());

		Collection<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>();
		situacoes.add(SituacaoMatricula.MATRICULADO);
		situacoes.add(SituacaoMatricula.APROVADO);
		
		atividadesJaMatriculadas = rDao.findByDiscente(discente.getId(), getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo(), situacoes);
		
		// Se não for solicitação, buscar as solicitações pendentes realizadas
		if (!isSolicitacaoMatricula() && !isAlunoRecemCadastrado() && !isMatriculaFerias()) {
			Collection<Turma> turmasSolicitadas = solicitacaoDao.findTurmasSolicitadasEmEspera(discente.getDiscente(),
					calendarioParaMatricula.getAno(), calendarioParaMatricula.getPeriodo());
			for (Turma t : turmasSolicitadas) {
				if (!turmasJaMatriculadas.contains(t)) {
					turmasJaMatriculadas.add(t);
				}
			}
		}
		
		//se for discente stricto o aluno não pode solicitar a matrícula em uma turma
		//que ele já tenha solicitado anteriormente e o coordenador negou ou então está aguardando análise do outro programa
		if( discente.isStricto() ){
			
			turmasSolicitadasNegadas = solicitacaoDao.findTurmasSolicitacoesByDiscente(discente.getDiscente().getId(),
					calendarioParaMatricula.getAno(), calendarioParaMatricula.getPeriodo(), 
					SolicitacaoMatricula.NEGADA, SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA, SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);
			
			turmasSolicitadasExclusao = solicitacaoDao.findTurmasSolicitacoesByDiscente(discente.getDiscente().getId(),
					calendarioParaMatricula.getAno(), calendarioParaMatricula.getPeriodo(), SolicitacaoMatricula.EXCLUSAO_SOLICITADA);
		
			SolicitacaoTurma solNegada = new SolicitacaoTurma();
			solNegada.setSituacao(SolicitacaoTurma.NEGADA);
			for (Turma t : turmasSolicitadasNegadas) {
				t.setSolicitacao(solNegada);
				if (!turmasJaMatriculadas.contains(t)) {
					turmasJaMatriculadas.add(t);
				}
			}
			for (Turma t : turmasSolicitadasExclusao) {
				t.setSolicitacao(solNegada);
				if (!turmasJaMatriculadas.contains(t)) {
					turmasJaMatriculadas.add(t);
				}
			}
			
		}

		// carrega esses componentes e turmas para validações futuras
		componentesMatriculadosConcluidos = ddao.findComponentesMatriculadosConcluidos(discente.getDiscente());
		if (!isMatriculaFerias() && !discente.isStricto())
			carregarTurmasCurriculo();
		turmas = new ArrayList<Turma>(0);

		if (turmasJaMatriculadas != null) {
			for (Turma t : turmasJaMatriculadas) {
				if (t.getHorarios() != null)
					t.getHorarios().iterator();
			}
		}
	}

	/**
	 * Escolhe um aluno de Convênio.
	 * Método não chamado por JSP.
	 * @return
	 * @throws DAOException 
	 */
	private String escolhaAlunoDeConvenio() throws DAOException {
		return telaOutrasTurmas();
	}

	/**
	 * Escolhe o aluno para realizar a solicitação.
	 * Método não chamado por JSP.
	 * @throws ArqException
	 */
	public void escolhaAlunoParaSolicitacao() throws ArqException {

		if (getCalendarioParaMatricula() == null)
			clear();
		int ano  = getCalendarioParaMatricula().getAno();
		int periodo = getCalendarioParaMatricula().getPeriodo();

		// busca as solicitações do semestre
		// carregando solicitações de matriculas que esse discente já possa possuir
		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);

		/// busca apenas as cadastradas
		if (isNecessariaValidacaoDaSolicitacao()) {
			turmas = dao.findTurmasSolicitacoesByDiscente(discente.getId(), ano, periodo, SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.VISTA,
					SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.ATENDIDA,SolicitacaoMatricula.NEGADA, SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);
		} else {
			turmas = dao.findTurmasSolicitacoesByDiscente(discente.getId(), ano, periodo,
					SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA);
		}

		// Dependendo da modalidade de ensino, a orientação pelo coordenador é feita com status diferentes
		Integer[] statusOrientadas = isNecessariaValidacaoDaSolicitacao()
			? new Integer[] {SolicitacaoMatricula.ATENDIDA,SolicitacaoMatricula.NEGADA, SolicitacaoMatricula.SOLICITADA_COORDENADOR, SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA}
			: new Integer[] {SolicitacaoMatricula.ORIENTADO,SolicitacaoMatricula.VISTA};

		qtdMatriculasOrientadas = dao.countByDiscenteAnoPeriodo(
				discente.getDiscente(), ano, periodo, statusOrientadas);

		popularSolicitacoesMatricula();

		turmasIndeferidas = new HashSet<Turma>();
		for (SolicitacaoMatricula sol : solicitacoesMatricula) {
			if (sol.isProcessada()) {
				sol.getMatriculaGerada().getSituacaoMatricula().getDescricao();
				if (sol.getTurma() != null && sol.getTurma().getDocentesTurmas() != null) {
					sol.getTurma().getDocentesNomes();
				}

				if ( sol.isInDeferida() ) {
					if(!hasOutraSolicitacaoMesmaTurma(sol))
						turmas.remove(sol.getTurma());
					turmasIndeferidas.add(sol.getTurma());
				} else if ( sol.isProcessada() && !sol.getMatriculaGerada().getSituacaoMatricula().equals(SituacaoMatricula.MATRICULADO) ) {
					turmas.remove(sol.getTurma());
				} else {
					if (isSolicitacaoMatricula()) {
						// para não dar erro na validação com os já matriculados
						componentesMatriculadosConcluidos.remove(sol.getComponente());
						removerTurmaJaMatriculada(sol);
					}
				}
			}

			if (sol.isNegada()) {
				turmas.remove(sol.getTurma());
				turmasIndeferidas.add(sol.getTurma());
			}

			if (sol.isAtendida() || sol.isNegada()) {
				sol.getRegistroAlteracao().getUsuario().getNome();
			}
			if (sol.getNumeroSolicitacao() != null && sol.getNumeroSolicitacao() > 0)
				numeroSolicitacao = sol.getNumeroSolicitacao();
		}

		verificarComponentesSugestao();
		if (isEmpty(turmas))
			turmas = new ArrayList<Turma>(0);
		else {
			for (Turma t : turmas) {
				if (t.getHorarios() != null)
					t.getHorarios().iterator();
				if (t.getDocentesTurmas() != null)
					t.getDocentesTurmas().iterator() ;
				if(t.getEspecializacao() != null)
					t.getEspecializacao().getEspecializacoes().iterator();
			}
		}
	}

	private boolean isNecessariaValidacaoDaSolicitacao() {
		return discente.isDiscenteEad() || !discente.isRegular() || discente.isStricto();
	}

	

	/**
	 * Verifica se há uma outra solicitação não-indeferida para a mesma turma da solicitação passada como argumento.
	 * @param sol
	 * @return
	 */
	private boolean hasOutraSolicitacaoMesmaTurma(SolicitacaoMatricula sol) {
		for(SolicitacaoMatricula solicitacao: solicitacoesMatricula){
			if(solicitacao.getId() != sol.getId() 
					&& solicitacao.getTurma() != null 
					&& solicitacao.getTurma().getId() == sol.getTurma().getId()
					&& !solicitacao.isInDeferida())
				return true;
		}
		return false;
	}

	/**
	 * Remove a turma já matriculada.
	 * @param sol
	 */
	private void removerTurmaJaMatriculada(SolicitacaoMatricula sol) {
		if (sol.getTurma() != null) {
			Collection<Turma> turmasARemover = new ArrayList<Turma>();
			
			for (Turma t : turmasJaMatriculadas) {
				if (t.equals(sol.getTurma()) || (sol.isProcessada() && t.getDisciplina().getId() == sol.getTurma().getDisciplina().getId()) ) {
					turmasARemover.add(t);
				}
			}
			turmasJaMatriculadas.removeAll(turmasARemover);
		}
	}

	/**
	 * Popula as solicitações de matrícula feitas pelo discente
	 * no ano/período corrente
	 *
	 * @throws DAOException
	 */
	private void popularSolicitacoesMatricula() throws DAOException {
		CalendarioAcademico cal = getCalendarioParaMatricula();
		SolicitacaoMatriculaDao sdao = getDAO(SolicitacaoMatriculaDao.class);
		solicitacoesMatricula = sdao.findByDiscenteAnoPeriodo(discente.getDiscente(), cal.getAno(), cal.getPeriodo(), null,
				SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA,
				SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA, SolicitacaoMatricula.EXCLUSAO_SOLICITADA,
				SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA, SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);

		if (discente.isStricto()) {
			solicitacoesAtividade = sdao.findSolicitacoesAtividadeByDiscente((DiscenteStricto) discente, cal.getAno(), cal.getPeriodo());
		}
	}

	/**
	 * Carrega solicitações previamente cadastradas e ainda não submetidas de turmas de ferias
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/discentes_ferias.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String selecionaDiscenteFerias() throws ArqException {

		Integer id = getParameterInt("id");

		if( id == null || id <= 0 ){
			addMensagemErro("Selecione um discente");
			return null;
		}

		TurmaDao dao = getDAO( TurmaDao.class );
		DiscentesSolicitacao discSolicitacao = dao.findByPrimaryKey(id, DiscentesSolicitacao.class);
		Collection<Turma> turmasDaSolicitao = dao.findBySolicitacao( discSolicitacao.getSolicitacaoTurma().getId() );

		discente = discSolicitacao.getDiscenteGraduacao();

		if( turmasDaSolicitao == null || turmasDaSolicitao.isEmpty() ){
			addMensagemErro("A turma solicitada por este discente ainda não foi criada pelo chefe de departamento, portanto, não é possível matricular este discente na turma de férias solicitada até que ela seja criada.");
			return null;
		}

		if( turmasDaSolicitao != null && turmasDaSolicitao.size() == 1 ){

			Turma t = null;
			Iterator<Turma> iter = turmasDaSolicitao.iterator();
			if( iter.hasNext() )
				t = iter.next();
			
			if(t != null){
				Turma turma = getGenericDAO().findByPrimaryKey(t.getId(), Turma.class);
				if (turma.getDocentesTurmas() != null)
					turma.getDocentesTurmas().iterator();
	
				if (turma != null) {
					turma.setMatricular(true);
					turma.getHorarios().iterator();
					// validações a cada turma que está querendo se matricular
	
					try {
						// Verifica se o coordenador do curso liberou para o aluno
						// poder extrapolar o limite mínimo ou máximo.
						permissaoExtrapolarCredito();
	
						ListaMensagens msgsErros = validarTurmaIndividualmente(discente.getDiscente(), turma,
								turmasDaSolicitao, getTodosComponentes(), restricoes);
	
						if (!msgsErros.isEmpty()) {
							addMensagens(msgsErros);
							return null;
						}
	
						if (!turmas.contains(turma))
							turmas.add(turma);
					} catch (NegocioException e) {
						addMensagemErro(e.getMessage());
						return null;
					}
				}
			}
		

		}

		if( turmas != null && turmas.size() > 0 )
			return telaSelecaoTurmas();
		else
			return telaSolicitacoes();
	}

	/**
	 * Escolhe o status da matrícula.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul>
	 * @return
	 */
	public String escolherStatus() {
		setOperacaoAtual("Escolha do Status da Matrícula");
		return escolherRestricoes();
	}

	/**
	 * Operação acionada quando o usuário está apenas solicitando matrícula em turmas.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterSolicitacoes() throws ArqException {
		
		String validacao = validarSubmissaoSolicitacao();
		if (validacao != null || hasOnlyErrors()) 
			return null;
		
		if (isDiscenteLogado()){
			solicitacaoConfirmada = false;
			addMensagemWarning("Por favor, confirme a senha para fazer a solicitação de matrículas.");
			if (discente.isGraduacao())
				verificaSolicitacaoEnsinoIndividualAtivo(discente);
			return telaResumoSolicitacoes();
		} else {
			solicitacaoConfirmada = true;
			return confirmarSubmissaoSolicitacao();
		}
		
	}

	/**
	 * Operação acionada para confirmar a solicitação de matrícula em turmas.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/resumo_solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmarSubmissaoSolicitacao() throws ArqException {
		
		if (isDiscenteLogado()){
		
			if (!confirmaSenha())
				return null;
			
			String validacao = validarSubmissaoSolicitacao();
			if (validacao != null || hasOnlyErrors()) 
				return null;
		}
		
		MovimentoSolicitacaoMatricula mov = new MovimentoSolicitacaoMatricula();
		mov.setCalendarioAcademicoAtual(getCalendarioParaMatricula());
		mov.setDiscente(discente);
		mov.setTurmas(turmas);
		mov.setCodMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
		mov.setRematricula(rematricula);
		mov.setCalendarioParaMatricula(calendarioParaMatricula);
		mov.setMatriculas(matriculasDesistencia);

		if (isDiscenteLogado()) {
			mov.setAcao(MovimentoSolicitacaoMatricula.SUBMETER_NOVAS);
		} else { 
			mov.setAcao(MovimentoSolicitacaoMatricula.SUBMETER_JA_ORIENTADAS);
		}

		mov.setHorariosTutoria(horariosTutoria);
		
		// Chamar processador
		try {
			mov = executeWithoutClosingSession(mov, getCurrentRequest());
			numeroSolicitacao = mov.getNumSolicitacao();
			addMensagens(mov.getMensagens());
			addMessage("Matrículas submetidas com sucesso!", TipoMensagemUFRN.INFORMATION);

			// No caso de rematrícula, re-popular turmas no portal discente 
			// para tratar casos de desistência de matrículas
			if ( rematricula ) {
				PortalDiscenteMBean portalDiscente = getMBean("portalDiscente");
				portalDiscente.resetTurmasAbertas();
			}
			
			solicitacaoConfirmada = true;
			return telaResumoSolicitacoes();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			hasErrors();
			return null;
		}
	}

	/**
	 * Faz as validações da matrícula da turma.
	 * <br/>
	 * Método não invocado por JSPs:
	 * @return
	 * @throws ArqException
	 */
	private String validarSubmissaoSolicitacao() throws DAOException,
			ArqException {
		// Verificar seleção do discente
		if (discente == null
				|| discente.getId() == 0
				|| !isOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA
						.getId(), SigaaListaComando.MATRICULAR_GRADUACAO
						.getId(), SigaaListaComando.MATRICULA_COMPULSORIA
						.getId(), SigaaListaComando.MATRICULA_FORA_PRAZO
						.getId(), SigaaListaComando.SELECIONAR_TURMA_ENTADA_IMD
						.getId())) {
			addMensagemErro("É necessário reiniciar a operação desde a tela inicial.");
			return cancelar();
		}

		// Verificar se a operação foi concluída anteriormente
		if (isEmpty(tipoMatricula) && isEmpty(operacaoAtual)) {
			clear();
			validarInicioSolicitacaoMatricula();
			return iniciarSolicitacaoMatricula();
		}

		if (discente.isGraduacao()) {
			try {
				// Verifica se o coordenador do curso liberou para o aluno
				// poder extrapolar o limite mínimo ou máximo.
				permissaoExtrapolarCredito();
				// modelo antigo
				if (restricoes.getValorMinimoCreditos() == null) {
					if ( restricoes.getLimiteMinCreditosSemestre() != null &&
							restricoes.getLimiteMinCreditosSemestre() ) {
						MatriculaGraduacaoValidator.validarLimiteMinimoCreditosSemestre(getDiscente().getDiscente(), getTurmasSemestre(), atividadesJaMatriculadas, getCalendarioParaMatricula());
					}
					if ( restricoes.isLimiteMaxCreditosSemestre() ) {
						MatriculaGraduacaoValidator.validarLimiteMaximoCreditosSemestre(getDiscente().getDiscente(), getTurmasSemestre(), getCalendarioParaMatricula());
					}
				} else {
					if ( restricoes.isLimiteMaxCreditosSemestre() ) { 
						MatriculaGraduacaoValidator.validarLimiteMaximoCreditosSemestre(getDiscente().getDiscente(), getTurmasSemestre(), getCalendarioParaMatricula(), restricoes.getValorMaximoCreditos());
					}
					if ( restricoes.getLimiteMinCreditosSemestre() != null && restricoes.getLimiteMinCreditosSemestre() ) { 
						MatriculaGraduacaoValidator.validarLimiteMinimoCreditosSemestre(getDiscente().getDiscente(), getTurmasSemestre(), atividadesJaMatriculadas, getCalendarioParaMatricula(), restricoes.getValorMinimoCreditos());
					}	
				}
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				notifyError(e);
				return null;
			}
		}

		erros = validarSolicitacoesTurma();
		if (!erros.getErrorMessages().isEmpty()) {
			addMensagens(erros);
			return null;
		}
		
		return null;
	}

	/**
	 * Realizar a matrícula do discente
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/resumo.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String matricular() throws Exception {
		
		MovimentoMatriculaGraduacao movimento = new MovimentoMatriculaGraduacao();
		movimento.setDiscente(discente);
		movimento.setTurmas(turmas);
		movimento.setCalendarioAcademicoAtual(getCalendarioParaMatricula());
		movimento.setMatriculaEAD(isMatriculaEAD());
		movimento.setMatriculaConvenio(isMatriculaConvenio());
		movimento.setMatriculaFerias(isMatriculaFerias());
		movimento.setRestricoes(restricoes);

		if (isCompulsoria()) {
			movimento.setRestricoes(restricoes);
			movimento.setSituacao(situacao);
			movimento.setCodMovimento(SigaaListaComando.MATRICULA_COMPULSORIA);
		} else if (isForaPrazo()) {
			movimento.setSituacao(situacao);
			movimento.setCodMovimento(SigaaListaComando.MATRICULA_FORA_PRAZO);
		} else {
			movimento.setCodMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		}
		
		
		if(isPermiteMatriculaRegularEmOutrosProgramas()) {
			 
			for(Turma t : turmas) {
				ComponenteCurricular disciplina = getGenericDAO().findAndFetch(t.getDisciplina().getId(), ComponenteCurricular.class, "unidade");
				if ( disciplina.getUnidade().getId() != getProgramaStricto().getId() ) {
					movimento.setMatriculandoTurmasOutrosProgramas(true);
					break;
				}
			}
		}
		
		if(isPermiteDuplicidadeCasoConteudoVariavel()) {
			movimento.setPermiteDuplicidadeCasoConteudoVariavel(true);
		}		

		// Chamar processador
		try {
			executeWithoutClosingSession(movimento, getCurrentRequest());
			return visualizarConfirmacao();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	
	/**
	 * Utilizado em jsp para habilitar ou não o combo para escolher turmas de outros programas.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/matricula/turmas_extra_curriculo.jsp  
	 * 
	 * @return
	 */
	public boolean isPermiteMatriculaRegularEmOutrosProgramas() {
		return (isPortalCoordenadorStricto() && isMatriculaRegular() && isPermiteCoordenadorMatricularDiscenteStrictoRegular());
	}

	/**
	 * Exibe o atestado de matrícula.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/confirmacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String exibirAtestadoMatricula() throws SegurancaException, DAOException {
		getCurrentSession().setAttribute("atestadoLiberado", discente.getId());
		AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
		atestado.setDiscente(discente);
		return atestado.selecionaDiscente();
	}

	/** Indica se deve exibir o painel de turmas selecionadas na view.
	 * @return
	 */
	public boolean isExibirTurmasSelecionadas() {
		if (isSolicitacaoMatricula())
			if ( isAlunoIngressante() )
				return !isEmpty(turmas) || !isEmpty(turmasJaMatriculadas);
			else
				return !isEmpty(turmas);
		return !isEmpty(turmas) || !isEmpty(turmasJaMatriculadas);
	}
	
	/** Indica se deve exibir a lista com as turmas já matriculadas do aluno.
	 * @return
	 */
	public boolean isExibirTurmasJaMatriculadas() {
		return !getMatriculadas().isEmpty() && (!isSolicitacaoMatricula() || (isSolicitacaoMatricula() && isAlunoIngressante()) );
	}

	/**
	 * Buscar as turmas selecionadas e adicioná-las ao conjunto de turmas a
	 * matricular
	 * <br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/turmas_curriculo.jsp</li>
	 *  <li>/sigaa.war/graduacao/matricula/turmas_equivalentes_curriculo.jsp</li>
	 *  <li>/sigaa.war/graduacao/matricula/turmas_extra_curriculo.jsp</li>
	 *  <li>/sigaa.war/stricto/matricula/turmas_outros_programas.jsp</li>
	 *  <li>/sigaa.war/stricto/matricula/turmas_programa.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws ArqException
	 */
	public String selecionarTurmas() throws ArqException{
		adicionarTurma();
		
		if (hasErrors())
			return null;
		
		return telaSelecaoTurmas();
	}

	/**
	 * Valida a turma selecionada e adicionada ao conjunto de turmas solicitadas
	 * Método não invocado por JSPs:
	 * 
	 * @throws DAOException
	 * @throws ArqException
	 */
	public void adicionarTurma() throws DAOException, ArqException {
		if (isEmpty(discente)
				|| !isOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA
						.getId(), SigaaListaComando.MATRICULAR_GRADUACAO
						.getId(), SigaaListaComando.MATRICULA_COMPULSORIA
						.getId(), SigaaListaComando.MATRICULA_FORA_PRAZO
						.getId())) {
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return ;
		}

		HttpServletRequest req = getCurrentRequest();
		String[] selecaoTurmas = req.getParameterValues("selecaoTurmas");

		MatriculaComponenteDao matdao = getDAO(MatriculaComponenteDao.class);
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		CalendarioAcademicoDao calDao = null;
		Collection<Turma> turmasAdicionadas = new ArrayList<Turma>();
		try {
			if (selecaoTurmas == null) {
			    addMensagemErro("É necessário selecionar no mínimo uma turma");
			    return ;
			}
			
			int tamanhoSelecao = selecaoTurmas.length;			

			// Verificando se já tem alguma turma de férias adicionada, se tiver não poderá adicionar outra
			// se não tiver poderá adicionar APENAS mais uma.
			if( isMatriculaFerias() ) {
				
				ArrayList<Turma> turmasFeriasJaMatriculadas = new ArrayList<Turma>();
				if (!ValidatorUtil.isEmpty(turmasJaMatriculadas))
					for ( Turma turma : turmasJaMatriculadas )
						if (turma.isTurmaFerias())
							turmasFeriasJaMatriculadas.add(turma);
				
				if (turmas.size() > 0 || tamanhoSelecao > 1 || (tamanhoSelecao == 1 && !ValidatorUtil.isEmpty(turmasFeriasJaMatriculadas))) {
				    addMensagem(MensagensGraduacao.NUMERO_MAXIMO_MATRICULA_TURMA_FERIAS);
				    return ;
				}    
			}
			
			ParametrosGestoraAcademica parametrosDiscente = ParametrosGestoraAcademicaHelper.getParametros(discente);
			boolean metodologiasDiferentes = false;
			
			ArrayList<Turma> turmasSubmetidas = new ArrayList<Turma>(0);
			for (int i = 0; i < tamanhoSelecao; i++) {
				Turma turma = matdao.findByPrimaryKey(Integer.valueOf(selecaoTurmas[i]), Turma.class);
				turma.getDisciplina().getUnidade().getId();
				turma.getDisciplina().getUnidade().getGestoraAcademica().getId();
				// Verificando se a turma selecionada é de férias, não estando no período de matrícula de férias
				if (!isMatriculaFerias() && !isCompulsoria() && turma.isTurmaFerias()) {
				    addMensagemErro("A turma "+turma.getDescricao()+" não pode ser selecionada uma vez que não é o período para matrícula de turmas de férias.");
				    return ;
				} 
				if (discente.isDiscenteEad() != turma.isEad()) {
					addMensagemErro("A turma "+turma.getDescricao()+" não é da mesma modalidade de educação do discente.");
				}
				turma.setMatricular(true);
				turmasSubmetidas.add(turma);
				
				turma.getHorarios().iterator();
				turma.getLocal();
				if(isNotEmpty(turma.getDisciplina().getPrograma()))
					turma.getDisciplina().getPrograma().getNumUnidades();
				if (turma.getDocentesTurmas() != null)
					turma.getDocentesTurmas().iterator();
				
				ParametrosGestoraAcademica parametrosTurma = ParametrosGestoraAcademicaHelper.getParametros(turma);				
				
				if( !parametrosDiscente.getMetodoAvaliacao().equals( parametrosTurma.getMetodoAvaliacao() )  ) {
					addMensagemErro("Não é possível matricular-se na turma " + turma.getDescricaoResumida() + ", pois esta turma possui um método de avaliação diferente do método de avaliação do programa do discente.");
					metodologiasDiferentes = true;
				}
			}

			if(metodologiasDiferentes || hasErrors()) 
				return ;
			
			// Verifica se o coordenador do curso liberou para o aluno
			// poder extrapolar o limite mínimo ou máximo.
			permissaoExtrapolarCredito();

			ListaMensagens todosErros = new ListaMensagens();
			Collection<ComponenteCurricular> todosComponentes = getTodosComponentes(turmasSubmetidas);
			Collection<ComponenteCurricular> componentesPagos = discenteDao.findComponentesCurricularesConcluidos(discente.getDiscente());
			

			calDao = getDAO(CalendarioAcademicoDao.class);
			for (int i = 0; i < tamanhoSelecao; i++) {
				Turma turma = turmasSubmetidas.get(i);

				if (isMatriculaTurmasNaoOnline() && turma.getDisciplina().isMatriculavel()) {
					addMensagemErro("Não é possível adicionar turmas de disciplinas matriculáveis on-line através dessa operação");
					return ;
				}
				
				if (discente.isStricto() && !(isUserInRole(SigaaPapeis.PPG) && isPortalPpg())){
					CalendarioAcademico cal = calDao.findByUnidadeNivel(turma.getDisciplina().getUnidade().getId(), NivelEnsino.STRICTO);
					if (cal != null && !cal.isPeriodoMatriculaRegular()){
						addMensagemErro("Não é possível matricular em turma do componente " + turma.getDisciplina().getCodigoNome() 
								+ ", pois o Programa responsável está fora do prazo de matrícula. ("+
								Formatador.getInstance().formatarData(cal.getInicioMatriculaOnline())+" - "+
								Formatador.getInstance().formatarData(cal.getFimMatriculaOnline())+")");
						return ;
					}
					if (turmasSolicitadasNegadas.contains(turma)) {
						addMensagemErro("Não é possível matricular em turma do componente " + turma.getDisciplina().getCodigoNome()+" pois seu orientador negou a solicitação de matrícula em turmas do componente.");
						return ;
					}
					if (turmasSolicitadasExclusao.contains(turma)) {
						addMensagemErro("Não é possível matricular em turma do componente " + turma.getDisciplina().getCodigoNome()+" pois você já solicitou a exclusão da mesma e o orientador ainda não analisou a solicitação.");
						return ;
					}
				}

				// coleção de turmas já matriculadas e selecionadas pra se matricular
				ArrayList<Turma> turmasSemestreESubmetidas = new ArrayList<Turma>(getTurmasSemestre());
				turmasSemestreESubmetidas.addAll(turmasSubmetidas);
				turmasSemestreESubmetidas.removeAll(turmasIndeferidas);
				
				RestricoesMatricula restricoesSemCoRequisito = new RestricoesMatricula();
				restricoesSemCoRequisito = UFRNUtils.deepCopy(restricoes);
				restricoesSemCoRequisito.setCoRequisitos(false);
				
				ListaMensagens msgsErros = validarSolicitacaoTurma(turma, turmasSemestreESubmetidas, todosComponentes, componentesPagos, restricoesSemCoRequisito);

				if (!msgsErros.isEmpty()) {
					todosErros.addAll(msgsErros.getMensagens());
					if ( msgsErros.isErrorPresent() ) {
						continue;
					}
				}

				if (!discente.isStricto() && !discente.isRegular() && getAcessoMenu().isChefeDepartamento()) {
					if (!turma.getDisciplina().getUnidade().equals(getUsuarioLogado().getVinculoAtivo().getUnidade())) {
						todosErros.addErro("Não é possível matricular em turma do componente " + turma.getDisciplina().getCodigoNome() 
								+ " pois o componente não é do seu departamento.");
						continue;
					}
				}

				if (!turmas.contains(turma)) {
					// adiciona na coleção temporária
					turmasAdicionadas.add(turma);
				}
			}

			// se TODAS as turmas selecionadas deram erro, volta pra mesma tela
			if (!todosErros.isEmpty()) {
				addMensagens(todosErros);
			}

			if (turmasAdicionadas.isEmpty()) {
				if (!todosErros.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for(Turma t : turmasSubmetidas) {
						sb.append(t.getId()+",");
					}
					getCurrentRequest().setAttribute("turmasSubmetidas", sb.toString());
				}
				return ;
			} else {
				turmas.addAll(turmasAdicionadas);
				verificarComponentesSugestao();
				StringBuilder msg = new StringBuilder("As seguintes turmas foram selecionadas com sucesso: ");
				for (Turma t : turmasAdicionadas) {
					msg.append(t.getDisciplina().getCodigo() +" - Turma " + t.getCodigo() + ", ");
				}
				msg.replace(msg.lastIndexOf(", "), msg.length(), ".");
				addMensagemInformation(msg.toString());
			}
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return ;
		}
		horarios = null;
	}

	/**
	 * Valida uma turma de acordo com as regras de matrícula definidas
	 * 
	 * @param turma
	 * @param turmasSubmetidas
	 * @param todosComponentes
	 * @param componentesPagos
	 * @param restricoes
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private ListaMensagens validarSolicitacaoTurma(Turma turma, Collection<Turma> turmasSubmetidas, Collection<ComponenteCurricular> todosComponentes, 
			Collection<ComponenteCurricular> componentesPagos, RestricoesMatricula restricoes) throws ArqException, NegocioException, DAOException {
		
		
		if( isPermiteDuplicidadeCasoConteudoVariavel() && turma.getDisciplina().isConteudoVariavel() ) {
			restricoes.setPermiteDuplicidadeCasoConteudoVariavel( true );
		}
		
		// validações a cada turma que está querendo se matricular
		ListaMensagens msgsErros = validarTurmaIndividualmente(discente, turma, turmasSubmetidas, todosComponentes, restricoes, componentesPagos, getCalendarioParaMatricula());

		// Validar discentes especiais de graduação
		if(discente.isGraduacao() && !discente.isRegular()){
			DiscenteDao ddao = getDAO(DiscenteDao.class);
			long totalPeriodoCursados = ddao.findQtdPeriodosCursados(discente.getId(), getCalendarioParaMatricula());
			MatriculaGraduacaoValidator.validarAlunoEspecial(discente.getDiscente(), turmasSubmetidas.size(), (int)totalPeriodoCursados, msgsErros);
		}
		
		// Validar discentes especiais de pós-graduação
		if (!discente.isRegular() && discente.isStricto()) {
			MatriculaStrictoValidator.validarMatriculaDiscenteEspecial(discente.getDiscente(), turmasSubmetidas.size(), msgsErros.getMensagens());
		}
		return msgsErros;
	}

	
	private boolean isPermiteDuplicidadeCasoConteudoVariavel() {
		return ((isPortalCoordenadorStricto() && getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO)) || (isPortalPpg() && getUsuarioLogado().isUserInRole(SigaaPapeis.PPG)) );
	}

	/**
	 * Valida todas as turmas selecionadas, de acordo com as regras de matrícula definidas
	 * 
	 * @return
	 * @throws ArqException
	 */
	private ListaMensagens validarSolicitacoesTurma() throws ArqException {
		ListaMensagens listaMensagens = new ListaMensagens();
		
		Collection<Turma> turmasToValidate = new ArrayList<Turma>();
		turmasToValidate.addAll(turmas); turmasToValidate.addAll(turmasJaMatriculadas);
	
		ValidatorUtil.validateEmptyCollection("É necessário selecionar pelo menos uma turma para confirmar a solicitação.", turmasToValidate, listaMensagens);

		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		try {
			Collection<ComponenteCurricular> todosComponentes = getTodosComponentes(turmas);
			Collection<ComponenteCurricular> componentesPagos = discenteDao.findComponentesCurricularesConcluidos(discente.getDiscente());
			Collection<Turma> turmasJaMatriculadas = discenteDao.findTurmasMatriculadas(discente.getId());
			for (Turma turma : turmas) {
				if(!turmasJaMatriculadas.contains(turma))
					listaMensagens.addAll( validarSolicitacaoTurma(turma, turmas, todosComponentes, componentesPagos, restricoes) );
			}
		} catch (NegocioException e) {
			listaMensagens.addAll(e.getListaMensagens());
		} finally {
			discenteDao.close();
		}
		return listaMensagens;
	}
	
	/**
	 * Verifica a sugestão de componentes curriculares.
	 * <br/>
	 * Método não chamado por JSP.
	 * @throws ArqException
	 */
	private void verificarComponentesSugestao() throws ArqException {
		TreeSet<Integer> componentesSelecionados = new TreeSet<Integer>();
		for (Turma t : turmas) {
			componentesSelecionados.add(t.getDisciplina().getId());
		}
		if (turmasCurriculo != null) {
			for(SugestaoMatricula sugestao : turmasCurriculo) {
				ComponenteCurricular comp = sugestao.getTurma().getDisciplina();
				if ((componentesSelecionados.contains(comp.getId()) && comp.getQtdMaximaMatriculas() == 1) ||
						(!isEmpty(comp.getEquivalencia()) && ExpressaoUtil.eval(comp.getEquivalencia(), componentesSelecionados))) {
					sugestao.setTipoInvalido(SugestaoMatricula.JA_SELECIONADO);
				} else if (sugestao.getTipoInvalido() != null && sugestao.getTipoInvalido() == SugestaoMatricula.JA_SELECIONADO) {
					sugestao.setTipoInvalido(null);
				}
			}
		}
		if (isGraduacao() && discente.isDiscenteEad()){
			/*Solicita a verificação de turmas com reserva para o curso do discente de EAD.
			 *Caso tenha turmas diversas para o mesmo componente, mas uma dessas possua reserva para o curso, 
			 *apenas esta será sugerida para a matrícula.*/ 
			MatriculaGraduacaoValidator.verificarReservasCurso(turmasCurriculo, discente);
		}
	}

	
	
	public void setTurmasCurriculo(Collection<SugestaoMatricula> turmasCurriculo) {
		this.turmasCurriculo = turmasCurriculo;
	}

	/**
	 * Retorna as turmas do semestre.
	 * @return
	 */
	public Collection<Turma> getTurmasSemestre() {
		Collection<Turma> turmasSemestre = new ArrayList<Turma>(0);
		// ... e já matriculadas
		if (turmasJaMatriculadas != null)
			turmasSemestre.addAll(turmasJaMatriculadas);
		// as já selecionadas ...
		if (turmas != null) {
			for (Turma t  :turmas) {
				if (!turmasSemestre.contains(t)) {
					turmasSemestre.add(t);
				}
			}
		}
		return turmasSemestre;
	}

	/**
	 * Retorna os todos componentes pagos pelo discente escolhido.
	 * E ainda os componentes das turmas escolhidas para matrícula.
	 * Essa coleção retornada é usada na validação das seleções das turmas, especificamente na validação
	 * de requisitos dos componentes
	 * <br/>
	 * Método não chamado por JSP.
	 * @return
	 */
	public Collection<ComponenteCurricular> getTodosComponentes() {
		// Busca todos os componentes de turmas integralizadas
		// criando uma coleção de todos os componentes (concluídos e os das turmas para matrícula)
		componentesMatriculadosConcluidos = componentesMatriculadosConcluidos != null ? componentesMatriculadosConcluidos : new  ArrayList<ComponenteCurricular>();
		ArrayList<ComponenteCurricular> todosComponentes = new ArrayList<ComponenteCurricular>(componentesMatriculadosConcluidos);
		Collection<Turma> turmasSemestre = getTurmasSemestre();
		if (turmasSemestre != null) {
			for (Turma t : turmasSemestre) {
				if (!todosComponentes.contains(t.getDisciplina())) {
					todosComponentes.add(t.getDisciplina());
				}
			}
		}
		return todosComponentes;
	}

	/**
	 * Retornas todos os componentes referente as turmas passadas como parâmetro.
	 * @param turmas
	 * @return
	 */
	protected Collection<ComponenteCurricular> getTodosComponentes(Collection<Turma> turmas) {
		Collection<ComponenteCurricular> todosComponentes = getTodosComponentes();
		if (turmas != null) {
			for(Turma t : turmas) {
				if (!todosComponentes.contains(t.getDisciplina()))
					todosComponentes.add(t.getDisciplina());
			}
		}
		return todosComponentes;
	}


	/**
	 * Retorna todos os horários cadastrados para graduação
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/comprovante_solicitacoes.jsp</li>
	 *  <li>/sigaa.war/graduacao/matricula/comprovante.jsp</li>
	 *  <li>/sigaa.war/graduacao/matricula/turmas_selecionadas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<Horario> getHorarios() throws ArqException {
		if (isEmpty(horarios)) {
			HorarioDao dao = getDAO(HorarioDao.class);
			Unidade unidade = null;
			char nivel = getNivelEnsino();
			if (nivel == ' ') {
				nivel = discente.getNivel();
			}
			if (NivelEnsino.isEnsinoBasico(nivel)) {
				unidade = new Unidade(getUnidadeGestora());
			}
			
			Collection<Integer> idUnidades = new ArrayList<Integer>();			
			List<Horario> horariosUnidades = new ArrayList<Horario>();
			
			Collection<Turma> todas = new ArrayList<Turma>();
			todas.addAll(turmasJaMatriculadas);
			todas.addAll(turmas);
			
			for( Turma t : todas ) {
				if( !isEmpty(t.getHorarios()) && 
						!idUnidades.contains( t.getHorarios().iterator().next().getHorario().getUnidade().getId() ) ) {
					idUnidades.add(t.getHorarios().iterator().next().getHorario().getUnidade().getId());					
					horariosUnidades.addAll(dao.findAtivoByUnidade(t.getHorarios().iterator().next().getHorario().getUnidade(), nivel));
				}				
			}
			
			
			horarios = !horariosUnidades.isEmpty() ? horariosUnidades : (List<Horario>) dao.findAtivoByUnidade(unidade, nivel);
			// inclui os horários inativos, caso exista
			
			
			Collections.sort(horarios, new Comparator<Horario>() {				
				public int compare(Horario o1, Horario o2) {
					return o1.getInicio().compareTo(o2.getInicio());
				}
			});
			
		}
		return horarios;
	}

	/**
	 * Retorna só os horários das turmas já selecionadas para o discente ser matriculado
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/comprovante_solicitacoes.jsp</li>
	 *  <li>/sigaa.war/graduacao/matricula/comprovante.jsp</li>
	 *  <li>/sigaa.war/graduacao/matricula/turmas_selecionadas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTurma> getHorariosTurma() throws DAOException {
		List<HorarioTurma> horariosTurma = new ArrayList<HorarioTurma>();
		if (!getTurmasSemestre().isEmpty() || !getTurmas().isEmpty()){
			TurmaDao dao = getDAO(TurmaDao.class);
			if (isSolicitacaoMatricula()) {
				horariosTurma = dao.findHorariosByTurmas(getTurmas());
			} else
				horariosTurma = dao.findHorariosByTurmas(getTurmasSemestre());
		}
		
		if (horariosTurma == null) {
			horariosTurma = new ArrayList<HorarioTurma>();
		}
		
		return horariosTurma;
	}

	/**
	 * Remove uma turma do conjunto de turmas selecionadas
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/turmas_selecionadas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String removerTurma() throws ArqException {
		// Recuperar turma selecionada
		Integer idTurma = getParameterInt("idTurma");
		if (idTurma == null) {
			addMensagemErro("É necessário selecionar uma turma para remoção");
			return telaSelecaoTurmas();
		}

		// Validar remoção e em seguida remover a turma selecionada
		Turma turma = null;
		for (Turma t : turmas) {
			if (t.getId() == idTurma) {
				if (isMatriculaTurmasNaoOnline() && t.getDisciplina().isMatriculavel()) {
					addMensagemErro("Não é possível remover turmas de disciplinas matriculáveis on-line através dessa operação");
					return null;
				}
				if (isDiscenteLogado() && !t.getDisciplina().isMatriculavel()) {
					addMensagemErro("Não é possível remover turmas de disciplinas não matriculáveis on-line através dessa operação");
					return null;
				}
				if (isDiscenteLogado() && discente.isStricto() && !MatriculaStrictoValidator.isPassivelCancelamentoSolicitacao(discente.getDiscente(), t, calendarioParaMatricula)) {
					addMensagemErro("Não é possível remover turmas de disciplinas cuja solicitação já foi atendida pelo orientador ou coordenador");
					return null;
				}

				turma = t;
				turmas.remove(t);
				break;
			}
		}

		// Testar se a turma selecionada foi encontrada na lista
		if (turma == null) {
			addMensagemWarning("A turma selecionada já havia sido removido da lista de turmas");
			return telaSelecaoTurmas();
		}

		// Validar co-requisitos
		boolean removeuCoRequisitos = false;
		if (restricoes.isCoRequisitos()) {

			// Verificar se é necessário remover os co-requisitos
			Collection<Turma> coRequisitos = new ArrayList<Turma>(0);
			for(Turma t : turmas) {
				if ( !isEmpty(t.getDisciplina().getCoRequisito()) ) {
					boolean atendeCoRequisito = ExpressaoUtil.eval(t.getDisciplina().getCoRequisito(), turma.getDisciplina());
					if (atendeCoRequisito) {
						coRequisitos.add(t);
					}
				}
			}
			removeuCoRequisitos = turmas.removeAll(coRequisitos);
		}

		// Atualizar status de sugestões
		verificarComponentesSugestao();
		addMensagemInformation("Turma removida com sucesso!");
		if(removeuCoRequisitos)
			addMensagemWarning("(Obs.: As turmas dos co-requisitos desse componente também foram removidas)");

		horarios = null;
		
		return telaSelecaoTurmas();
	}

	/**
	 * Remove uma turma com registro de matricula componente do conjunto de turmas matriculadas. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/turmas_escolhidas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String removerTurmaMatriculada() throws ArqException {
		MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class);
		
		// Recuperar turma selecionada
		Integer idTurma = getParameterInt("idTurma");
		if (idTurma == null) {
			addMensagemErro("É necessário selecionar uma turma para remoção");
			return telaSelecaoTurmas();
		}

		// Validar remoção e em seguida remover a turma selecionada
		Turma turma = null;
		for (Turma t : turmasJaMatriculadas) {
			if (t.getId() == idTurma) {
				if (isMatriculaTurmasNaoOnline() && t.getDisciplina().isMatriculavel()) {
					addMensagemErro("Não é possível remover turmas de disciplinas matriculáveis on-line através dessa operação");
					return null;
				}
				if (isDiscenteLogado() && !t.getDisciplina().isMatriculavel()) {
					addMensagemErro("Não é possível remover turmas de disciplinas não matriculáveis on-line através dessa operação");
					return null;
				}
				if (isDiscenteLogado() && discente.isStricto() && !MatriculaStrictoValidator.isPassivelCancelamentoSolicitacao(discente.getDiscente(), t, calendarioParaMatricula)) {
					addMensagemErro("Não é possível remover turmas de disciplinas cuja solicitação já foi atendida pelo orientador ou coordenador");
					return null;
				}

				turma = t;
				turmasJaMatriculadas.remove(t);
				break;
			}
		}

		// Testar se a turma selecionada foi encontrada na lista
		if (turma == null) {
			addMensagemWarning("A turma selecionada já havia sido removido da lista de turmas");
			return telaSelecaoTurmas();
		}

		// Validar co-requisitos
		boolean removeuCoRequisitos = false;
		if (restricoes.isCoRequisitos()) {

			// Verificar se é necessário remover os co-requisitos
			Collection<Turma> coRequisitos = new ArrayList<Turma>(0);
			for(Turma t : turmasJaMatriculadas) {
				if ( !isEmpty(t.getDisciplina().getCoRequisito()) ) {
					boolean atendeCoRequisito = ExpressaoUtil.eval(t.getDisciplina().getCoRequisito(), turma.getDisciplina());
					if (atendeCoRequisito) {
						coRequisitos.add(t);
					}
				}
			}
			removeuCoRequisitos = turmasJaMatriculadas.removeAll(coRequisitos);
		}

		// Atualizar status de sugestões
		verificarComponentesSugestao();
		if (matriculasDesistencia == null)
			matriculasDesistencia = new HashSet<MatriculaComponente>();
		matriculasDesistencia.addAll(matDao.findMatriculadosByDiscenteTurmas(discente, turma.getId()));
		addMensagemInformation("Turma removida com sucesso!");
		if(removeuCoRequisitos)
			addMensagemWarning("(Obs.: As turmas dos co-requisitos desse componente também foram removidas)");

		horarios = null;
		
		return telaSelecaoTurmas();
	}
	
	/**
	 * Cancelar operação de matricula/pré-matricula
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/confirmacao.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/discentes_ferias.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/info_tutoria.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/resumo.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/selecao_discentes.jsp</li>
	 *   <li>/sigaa.war/graduacao/matricula/selecao_tutoria.jsp</li>
	 * </ul>
	 * @return
	 */
	public String cancelarMatricula() {
		removeOperacaoAtiva();
		return cancelar();
	}

	/**
	 * Verifica se é para exibir o quadro de horários.
	 * @return
	 */
	public boolean isExibirQuadroHorarios() {
		return !isMatriculaEAD() && !isTutorEad() && (getDiscente() != null && !getDiscente().isDiscenteEad());
	}

	/**
	 * Buscar as turmas abertas para as disciplinas do currículo do discente
	 *
	 * @return
	 * @throws ArqException
	 */
	public Collection<SugestaoMatricula> getTurmasCurriculo() {
		return turmasCurriculo;
	}

	/**
	 * Método responsável por carregar as turmas do currículo do discente. 
	 * @throws DAOException 
	 */
	private void carregarTurmasCurriculo() throws DAOException {
		MatriculaGraduacaoDao matdao = getDAO(MatriculaGraduacaoDao.class);
		turmasCurriculo = matdao.findSugestoesMatricula(discente, getTurmasJaMatriculadas(),
				getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo(), isDiscenteLogado());
	}
	
	/**
	 * Método responsável por carregar as turmas equivalentes do currículo do discente. 
	 * @throws ArqException 
	 */
	private void carregarTurmasEquivalentesCurriculo() throws ArqException {
		if (isEmpty(discente)) {
			return;
		}
		
		if (turmasEquivalentesCurriculo == null) {
			MatriculaGraduacaoDao matdao = getDAO(MatriculaGraduacaoDao.class);
			turmasEquivalentesCurriculo = matdao.findSugestoesEquivalentesMatricula(discente, getTurmasJaMatriculadas(),
				getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo(), isDiscenteLogado());
		}
		
		if (isGraduacao() && discente.isDiscenteEad()){
			/*Verificação de turmas com reserva para o curso do discente de EAD.
			 *Caso tenha turmas diversas para o mesmo componente, mas uma dessas possua reserva para o curso, 
			 *apenas esta será sugerida para a matrícula.*/ 
			MatriculaGraduacaoValidator.verificarEquivalenciasReservasCurso(turmasEquivalentesCurriculo, discente);
		}
	}

	/** Retorna as turmas abertas para componentes equivalentes aos componentes curriculares do currículo do discente.
	 * @return
	 * @throws ArqException 
	 */
	public Collection<SugestaoMatriculaEquivalentes> getTurmasEquivalentesCurriculo() throws ArqException {
		return turmasEquivalentesCurriculo;
	}
	
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	/** Define o discente que fará matrícula em componente curriculares.
	 * @throws DAOException 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.ensino.dominio.DiscenteAdapter)
	 */
	public void setDiscente(DiscenteAdapter discente) throws DAOException {
		this.discente = getDAO(DiscenteDao.class).findByPK(discente.getId());
	}

	/**
	 * Retorna lista de discentes orientados pelo tutor logado.
	 * <br/><br/>
	 * Método não chamado por JSPs:
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> getDiscentesTutoria() throws DAOException {
		return EADHelper.findDiscentesByTutor(getUsuarioLogado().getTutor().getId(), getUsuarioLogado().getPessoa().getId(), null, null);
	}

	/**
	 * Seleciona aluno orientado do tutor logado e redireciona para o form de
	 * dados da tutoria
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/selecao_tutoria.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarTutoriaAluno() throws ArqException {
		TutoriaAluno ta = null;
		if( SigaaSubsistemas.PORTAL_TUTOR.equals(getSubSistema()) ){
			Integer id = getParameterInt("id");
			ta = getGenericDAO().findByPrimaryKey(id, TutoriaAluno.class);
			discente = getGenericDAO().findByPrimaryKey(ta.getAluno().getId(), DiscenteGraduacao.class);
			setConfirmButtonHorarioTutoriaPresencial("Próximo passo >>");
			alunoEadDefinindoHorarioTutoria = false;
		}

		if( SigaaSubsistemas.PORTAL_DISCENTE.equals(getSubSistema()) || (SigaaSubsistemas.PORTAL_TURMA.equals(getSubSistema()) && getDiscenteUsuario() != null) ){
			TutoriaAlunoDao tutoriaDao = getDAO(TutoriaAlunoDao.class);
			discente = getDiscenteUsuario();
			ta = tutoriaDao.findUltimoByAluno(discente.getId());
			alunoEadDefinindoHorarioTutoria = true;
			setConfirmButtonHorarioTutoriaPresencial("Confirmar");
			prepareMovimento(SigaaListaComando.ALUNO_EAD_DEFININDO_HORARIO_TUTORIA);
		}

		if( ta == null ){
			addMensagemErro("Nenhuma tutoria foi encontrada para o discente: " + discente.getMatriculaNome());
			return null;
		}
		
		validarInicioSolicitacaoMatricula();
		if (hasErrors()) {
			return null;
		}

		CalendarioAcademico cal = getCalendarioParaMatricula();
		HorarioTutoriaDao hdao = getDAO(HorarioTutoriaDao.class);
		horariosTutoria = hdao.findByTutoria(ta.getId(), cal.getAno(), cal.getPeriodo());
		
		popularHorariosTutoria(horariosTutoria, ta);
		
		TurmaDao dao = getDAO(TurmaDao.class);
		turmas = dao.findByDiscenteMatSolicitadas(discente.getDiscente(), SolicitacaoMatricula.VISTA);
		if (turmas == null) {
			turmas = new ArrayList<Turma>(0);
		}
		return telaInfoTutoria();
	}

	/** Popula os horários da tutoria. 
	 * @param horarios
	 * @param ta
	 */
	private void popularHorariosTutoria( List<HorarioTutoria> horarios, TutoriaAluno ta ){
				
		  for( HorarioTutoria ht : horarios ){
			ht.setSelecionado(true);
		  }
		
		if( !possuiDia(horarios, Calendar.MONDAY) )
			horarios.add( new HorarioTutoria( ta, Calendar.MONDAY ) );

		if( !possuiDia(horarios, Calendar.TUESDAY) )
			horarios.add( new HorarioTutoria( ta, Calendar.TUESDAY ) );
		
		if( !possuiDia(horarios, Calendar.WEDNESDAY) )
			horarios.add( new HorarioTutoria( ta, Calendar.WEDNESDAY ) );
		
		if( !possuiDia(horarios, Calendar.THURSDAY) )
			horarios.add( new HorarioTutoria( ta, Calendar.THURSDAY ) );
		
		if( !possuiDia(horarios, Calendar.FRIDAY) )
			horarios.add( new HorarioTutoria( ta, Calendar.FRIDAY ) );
		
		if( !possuiDia(horarios, Calendar.SATURDAY) )
			horarios.add( new HorarioTutoria( ta, Calendar.SATURDAY ) );
		
		if( !possuiDia(horarios, Calendar.SUNDAY) )
			horarios.add( new HorarioTutoria( ta, Calendar.SUNDAY ) );
		
		
		Collections.sort( horarios, new Comparator<HorarioTutoria>(){
			public int compare(HorarioTutoria ht1,	HorarioTutoria ht2) {
				return new CompareToBuilder()
				.append(ht1.getDiaSemana(), ht2.getDiaSemana())
				.toComparison();  
			}
		});
		
	}
	
	/** Indica se an lista de horários de tutoria possui algum horário para o dia especificado. 
	 * @param horarios
	 * @param dia
	 * @return
	 */
	private boolean possuiDia(List<HorarioTutoria> horarios, int dia){
		for( HorarioTutoria horario : horarios ){
			if( horario.getDiaSemana() == dia)  
				return true;
		}
		return false;
	}
	
	/**
	 * Submissão do formulário com dados da tutoria, onde o tutor informa os
	 * períodos em que o aluno poderá tirar dúvidas.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/info_tutoria.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String submeterInfoTutoria() throws ArqException {
		erros = new ListaMensagens();
		List<HorarioTutoria> horariostutoriabackup = new ArrayList<HorarioTutoria>(horariosTutoria) ;
		
		for (Iterator<HorarioTutoria> it = horariosTutoria.iterator(); it.hasNext();) {
			HorarioTutoria  horario = it.next();
			if( horario.isSelecionado() ){
				erros.addAll(horario.validate().getMensagens());							
				
			}else {
				it.remove();
			}
		}
		
		if( isEmpty(horariosTutoria) ){
			addMensagemErro("É necessário definir ao menos um horário da tutoria presencial.");
			
		}

		if (hasErrors()){			
			horariosTutoria = horariostutoriabackup;			
			return null;
		}

		
		
		if( isUserInRole(SigaaPapeis.TUTOR_EAD) && SigaaSubsistemas.PORTAL_TUTOR.equals(getSubSistema()) ){
			return selecionaDiscente();
		} else{
			
			MovimentoSolicitacaoMatricula mov = new MovimentoSolicitacaoMatricula();
			mov.setCalendarioAcademicoAtual(getCalendarioParaMatricula());
			mov.setCodMovimento(SigaaListaComando.ALUNO_EAD_DEFININDO_HORARIO_TUTORIA);
			mov.setHorariosTutoria(horariosTutoria);
			mov.setDiscente( discente );
			
			try {
				execute(mov);
				addMensagemInformation("Horário de tutoria presencial definido com sucesso!");
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			}
			
			
			return cancelar();
		}
	}

	/**
	 * Submete e valida a submissão das restrições a serem ignoradas (no caso da compulsória) e
	 * o status da matricula escolhido pelo usuário (também no caso de fora do prazo)
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/escolha_restricoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String submeterRestricoes() throws DAOException {
		if ((isCompulsoria() || isForaPrazo()) && (situacao == null || situacao.getId() == 0)) {
			addMensagemErro("O status da matrícula deve ser selecionado");
			return null;
		}
		situacao = getGenericDAO().findByPrimaryKey(situacao.getId(), SituacaoMatricula.class);
		//if( calendarioParaMatricula.getId() != 0 )
			//calendarioParaMatricula = getGenericDAO().findByPrimaryKey(calendarioParaMatricula.getId(),CalendarioAcademico.class);
		//else

		//Unidade.UNIDADE_DIREITO_GLOBAL, NivelEnsino.GRADUACAO
		int ano = anoPeriodo / 10;
		int periodo = anoPeriodo - ano * 10;

		calendarioParaMatricula = new CalendarioAcademico();
		calendarioParaMatricula.setAno(ano);
		calendarioParaMatricula.setPeriodo(periodo);

		popularComponentesTurmas();
		
		return telaOutrasTurmas();
	}

	/**
	 * Carrega turmas por expressão.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/painel_turmas_abertas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String getCarregarTurmasPorExpressao() throws ArqException {
		if (discente == null) {
			return null;
		}

		TurmaDao dao = getDAO(TurmaDao.class);

		Curso curso = null;
		if ( discente.isGraduacao() &&  discente.getCurso() != null && discente.getCurso().isProbasica()) {
			curso = discente.getCurso();
		}
		Polo polo = null;
		if (discente.isGraduacao() && ((DiscenteGraduacao)discente).getPolo() != null) {
			polo = ((DiscenteGraduacao)discente).getPolo();
		}
		Boolean buscarMatriculaveis = null;
		if (isSolicitacaoMatricula())
			buscarMatriculaveis = true;

		try {

			String expressao = getParameter("expressao");
			if (isEmpty(expressao)) {
				getCurrentRequest().setAttribute("erroExpressao", "Não foi possível carregar expressão");
				return null;
			}
			Collection<ComponenteCurricular> componentes = ExpressaoUtil.expressaoToComponentes(expressao, discente.getId());
			if (isEmpty(componentes)) {
				getCurrentRequest().setAttribute("erroExpressao", "Não foi possível carregar expressão");
				return null;
			}
			Collection<Integer> tiposTurma = new ArrayList<Integer>(3);
			tiposTurma.add(Turma.REGULAR);

			Collection<Turma> resultadoTurmasBuscadas;
			resultadoTurmasBuscadas = dao.findAbertasByExpressao(componentes, curso, polo,
					getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo(), buscarMatriculaveis, getNivelEnsino(), discente.isDiscenteEad(), tiposTurma.toArray(new Integer[]{}));
			getCurrentRequest().setAttribute("resultadoTurmasBuscadas", resultadoTurmasBuscadas);

			ComponenteCurricularDao ccdao = getDAO(ComponenteCurricularDao.class);
			String expressaoFormatada = ExpressaoUtil.buildExpressaoFromDB(expressao, ccdao, false);
			getCurrentRequest().setAttribute("expressaoFormatada", expressaoFormatada);
			return "";
		} catch (LimiteResultadosException lre) {
			getCurrentRequest().setAttribute("erroExpressao", "O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
			return null;
		}
	}

	/**
	 * Busca por qualquer turma aberta de qualquer componente curricular
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/turmas_extra_curriculo.jsp</li>
	 *   <li>/sigaa.war/stricto/matricula/turmas_outros_programas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String buscarOutrasTurmas() throws ArqException {
		TurmaDao dao = getDAO(TurmaDao.class);
		ComponenteCurricular cc = dadosBuscaTurma.getDisciplina();
		String codigo = null;
		String nomeComponente = null;
		String nomeDocente = null;
		String horario = null;
		TipoComponenteCurricular tipo = new TipoComponenteCurricular();
		Unidade unidade = new Unidade();
		Integer ano = null;
		Integer periodo = null;
		Boolean buscarReservasCurso = null;
		if (boolDadosBusca[0])
			codigo = cc.getCodigo().trim();
		if (boolDadosBusca[1])
			nomeComponente = cc.getNome().trim();
		if (boolDadosBusca[2])
			horario = dadosBuscaTurma.getDescricaoHorario().trim();
		if (boolDadosBusca[3])
			nomeDocente = dadosBuscaTurma.getNomesDocentes().trim();
		if (boolDadosBusca[4])
			unidade = cc.getUnidade();
		if (boolDadosBusca[5]){
			ano = dadosBuscaTurma.getAno();
			periodo = dadosBuscaTurma.getPeriodo();
		}
		if (boolDadosBusca[6]){
			buscarReservasCurso = true;
		}
		
		if (tipoMatricula == ALUNO_ESPECIAL && isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			
			if(!isEmpty(discente) && discente.isStricto() && isPortalCoordenadorStricto()) {
				unidade = getProgramaStricto();
			} else {
				unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			}
		}

		Curso curso = null;
		Polo polo = null;
		if (discente.isGraduacao()) {
			DiscenteGraduacao dg = (DiscenteGraduacao)discente;
			if ( discente.getCurso() != null && (discente.getCurso().isProbasica() || discente.getCurso().isParfor() ))
				curso = discente.getCurso();
			if (dg.getPolo() != null)
				polo = dg.getPolo();
		}


		Boolean buscarMatriculaveis = isDiscenteLogado() ? true : null;
		if (isMatriculaTurmasNaoOnline())
			buscarMatriculaveis = false;

		if (codigo == null && nomeComponente == null && unidade == null && (ano == null || periodo == null)) {
			addMensagemErro("Por favor, escolha algum critério de busca");
		} else {
			try {
				if( !boolDadosBusca[5] ){
					ano = getCalendarioParaMatricula().getAno();
					periodo = getCalendarioParaMatricula().getPeriodo();
				}

				// Definir tipos de turma a serem buscados, dependendo do tipo de matrícula
				Collection<Integer> tiposTurma = new ArrayList<Integer>(3);
				tiposTurma.add(Turma.REGULAR);
				if( isCompulsoria() ){
					tiposTurma.add(Turma.ENSINO_INDIVIDUAL);
					tiposTurma.add(Turma.FERIAS);
				}

				// Verificar se é matrícula em turma de férias
				if (isMatriculaFerias()) {
					ano = getCalendarioParaMatricula().getAnoFeriasVigente();
					periodo = getCalendarioParaMatricula().getPeriodoFeriasVigente();
					tiposTurma = new ArrayList<Integer>(3);
					tiposTurma.add(Turma.FERIAS);
				}
				if (unidade == null) unidade = new Unidade();
				tipo = new TipoComponenteCurricular();
				
				Collection<Turma> resultadoTurmasBuscadas = new ArrayList<Turma>();
				if (discente.isGraduacao() && discente.isRegular()) {
					DiscenteGraduacao dg = (DiscenteGraduacao)discente;
					resultadoTurmasBuscadas = dao.findAbertasByComponenteCurricular(dg, null, nomeComponente,
							codigo, unidade.getId(), tipo.getId(), nomeDocente,  horario, curso, polo,
							ano , periodo, buscarMatriculaveis, getNivelEnsino(), discente.isDiscenteEad(), buscarReservasCurso, tiposTurma.toArray(new Integer[]{}) );
				} else {
					resultadoTurmasBuscadas = dao.findAbertasByComponenteCurricular(null, nomeComponente,
							codigo, unidade.getId(), tipo.getId(), nomeDocente,  horario, curso, polo,
							ano , periodo, buscarMatriculaveis, getNivelEnsino(), discente.isDiscenteEad(), tiposTurma.toArray(new Integer[]{}) );
				}
				if (isEmpty(resultadoTurmasBuscadas)) {
					addMensagemWarning("Não foram encontradas turmas abertas para os parâmetros de busca especificados.");
				}else {
					if(isPortalCoordenadorStricto()){
					filtrarTurmasCadastradas(resultadoTurmasBuscadas);
					}
					if (isGraduacao() && discente.isDiscenteEad()){
						ListaMensagens msgErros =  new ListaMensagens();
						MatriculaGraduacaoValidator.verificarExtrasReservasCurso(resultadoTurmasBuscadas, discente,msgErros);
						addMensagens(msgErros);
					}
				}
				getCurrentRequest().setAttribute("resultadoTurmasBuscadas", resultadoTurmasBuscadas);
			} catch (LimiteResultadosException lre) {
				addMensagemErro("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
			}
		}
		return null;
	}

	
	/**
	 * Realiza o filtro das turmas já solicitadas/matriculadas para o aluno. Para que na consulta sejam disponibilizadas para 
	 * a realização da matrícula apenas os componentes para os quais o discente não tenha solicitação ou matrícula ativa.
	 * @param resultadoTurmasBuscadas
	 * @throws DAOException
	 */
	private void filtrarTurmasCadastradas(Collection<Turma> resultadoTurmasBuscadas) throws DAOException {
		popularComponentesTurmas();
		for (Turma ts : getTurmasSemestre()) {
			for(Turma turma:resultadoTurmasBuscadas ){
				if(ts.getId() == turma.getId())
					turma.setSelecionada(true);
				if(!turma.getDisciplina().isConteudoVariavel() && ts.getDisciplina().getId() == turma.getDisciplina().getId()){
					turma.setPodeMatricular(false);
				}
			}
		}
	}	
	
	/**
	 * Retorna lista de selects dos tipos de componentes existentes
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoComponenteCurricular> getTiposComponentes() throws DAOException {
		return getGenericDAO().findAll(TipoComponenteCurricular.class);
	}

	/**
	 * Retorna todos os alunos que tem como tutor o usuário logado.
	 * Essa consulta é usada na operação de matrícula feita por um tutor EaD
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/selecao_tutoria.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<TutoriaAluno> getTutorias() throws DAOException {

		if (tutorias == null) {
			TutoriaAlunoDao dao = getDAO(TutoriaAlunoDao.class);
			tutorias = dao.findByTutor(getUsuarioLogado().getPessoa());
		}
		return tutorias;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoesMatriculas() {
		return solicitacoesMatricula;
	}

	public void setSolicitacoesMatriculas(Collection<SolicitacaoMatricula> sol) {
		solicitacoesMatricula = sol;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoesAtividade() {
		return this.solicitacoesAtividade;
	}

	public void setSolicitacoesAtividade(Collection<SolicitacaoMatricula> solicitacoesAtividade) {
		this.solicitacoesAtividade = solicitacoesAtividade;
	}

	/**
	 * Retorna todos os alunos que solicitaram turma de férias de um curso.
	 * Esse método é usado quando um coordenador de curso está fazendo matricula de alunos em
	 * turmas de férias do seu curso
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/discentes_ferias.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscentesSolicitacao> getAlunosFerias() throws DAOException {
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		return dao.findBySolicitouTurmaFerias(getCursoAtualCoordenacao().getId(), getCalendarioParaMatricula().getAnoFeriasVigente(), getCalendarioParaMatricula().getPeriodoFeriasVigente(), Turma.FERIAS );
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public String getOperacaoAtual() {
		return operacaoAtual;
	}


	public void setOperacaoAtual(String operacaoAtual) {
		this.operacaoAtual = operacaoAtual;
	}


	public RestricoesMatricula getRestricoes() {
		return restricoes;
	}


	public void setRestricoes(RestricoesMatricula restricoes) {
		this.restricoes = restricoes;
	}


	public SituacaoMatricula getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoMatricula situacao) {
		this.situacao = situacao;
	}

	public void setHorarios(List<Horario> horarios) {
		this.horarios = horarios;
	}

	public Turma getDadosBuscaTurma() {
		return dadosBuscaTurma;
	}

	public void setDadosBuscaTurma(Turma dadosBuscaTurma) {
		this.dadosBuscaTurma = dadosBuscaTurma;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public Collection<Turma> getTurmasJaMatriculadas() {
		return turmasJaMatriculadas;
	}

	public void setTurmasJaMatriculadas(Collection<Turma> turmasJaMatriculadas) {
		this.turmasJaMatriculadas = turmasJaMatriculadas;
	}

	public Collection<Discente> getDiscentesCurso() {
		return discentesCurso;
	}

	public void setDiscentesCurso(Collection<Discente> discentesCurso) {
		this.discentesCurso = discentesCurso;
	}

	public boolean isSolicitacaoMatricula() {
		return tipoMatricula == SOLICITACAO_MATRICULA || tipoMatricula == TURMAS_NAO_ONLINE;
	}

	/**
	 * Esse método serve pra definir o tipo de Matrícula. 
	 * 
	 * JSP: Método não invocado por jsp.
	 */
	public void definirTipoSolicitacaoMatricula() {
		tipoMatricula = SOLICITACAO_MATRICULA;
	}

	public boolean isMatriculaTurmasNaoOnline() {
		return tipoMatricula == TURMAS_NAO_ONLINE;
	}

	public boolean isForaPrazo() {
		return tipoMatricula == FORA_PRAZO;
	}

	public boolean isCompulsoria() {
		return tipoMatricula == COMPULSORIA;
	}

	public boolean isAlunoEspecial() {
		return tipoMatricula == ALUNO_ESPECIAL;
	}

	public boolean isAlunoRecemCadastrado() {
		return tipoMatricula == SOLICITACAO_MATRICULA && SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema())
			|| tipoMatricula == ALUNO_RECEM_CADASTRADO;
	}

	public boolean isAnaliseMatricula() {
		return tipoMatricula == ANALISE_MATRICULA;
	}

	public boolean isMatriculaFerias() {
		return tipoMatricula == MATRICULA_FERIAS;
	}
	
	public boolean isMatriculaRegular() {
		return tipoMatricula == MATRICULA_REGULAR;
	}

	public boolean isMatriculaEAD() {
		return tipoMatricula == MATRICULA_EAD || getSubSistema().equals(SigaaSubsistemas.SEDIS);
	}
	
	/** Verifica se o aluno da solicitação de matrícula é ingressante e não reingresso.*/
	public boolean isAlunoIngressante(){
		return getCalendarioParaMatricula().getAno() == discente.getAnoIngresso() 
				&& getCalendarioParaMatricula().getPeriodo() == discente.getPeriodoIngresso()
				&& !FormaIngresso.REINGRESSO.contains(discente.getFormaIngresso().getId());
	}

	public boolean isMatriculaConvenio() {
		return tipoMatricula == MATRICULA_CONVENIO;
	}

	public CalendarioAcademico getCalendarioParaMatricula() {
		return calendarioParaMatricula == null ? getCalendarioVigente() : calendarioParaMatricula;
	}

	public void setCalendarioParaMatricula(CalendarioAcademico calendarioParaMatricula) {
		this.calendarioParaMatricula = calendarioParaMatricula;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	/**
	 * Tem como finalidade carregar as solicitações de matrícula do discente logado.
	 * 
	 * @throws ArqException
	 */
	private void carregarSolicitacoesMatriculas() throws ArqException {
		if (isEmpty(turmas))
			clear();
		if (isDiscenteLogado())
			discente = getDiscenteUsuario();
		escolhaAlunoParaSolicitacao();
		generateMD5();
	}

	/**
	 * Retorna o número de solicitações formatadas.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/comprovante_solicitacoes.jsp</li>
	 *   <li>/sigaa.war/stricto/matricula/resumo_solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String getNumeroSolicitacaoFormatado() throws ArqException {
 		if (numeroSolicitacao == null || numeroSolicitacao <= 0)
			carregarSolicitacoesMatriculas();
		if (numeroSolicitacao != null)
			return completaZeros(numeroSolicitacao, 5);
		return iniciarSolicitacaoMatricula();
	}

	public int getQtdMatriculasOrientadas() {
		return qtdMatriculasOrientadas;
	}

	public void setQtdMatriculasOrientadas(int qtdMatriculasOrientadas) {
		this.qtdMatriculasOrientadas = qtdMatriculasOrientadas;
	}

	public boolean isExibirOrientacoes() {
		return qtdMatriculasOrientadas > 0;
	}

	public Boolean[] getBoolDadosBusca() {
		return boolDadosBusca;
	}

	public void setBoolDadosBusca(Boolean[] boolDadosBusca) {
		this.boolDadosBusca = boolDadosBusca;
	}

	public OrientacaoMatricula getOrientacao() {
		return orientacao;
	}

	public void setOrientacao(OrientacaoMatricula orientacao) {
		this.orientacao = orientacao;
	}

	public int getAnoPeriodo() {
		return anoPeriodo;
	}

	public void setAnoPeriodo(int anoPeriodo) {
		this.anoPeriodo = anoPeriodo;
	}

	public HashSet<Turma> getTurmasIndeferidas() {
		return turmasIndeferidas;
	}

	public void setTurmasIndeferidas(HashSet<Turma> turmasIndeferidas) {
		this.turmasIndeferidas = turmasIndeferidas;
	}

	public boolean isMatricularDiscenteOutroPrograma() {
		return matricularDiscenteOutroPrograma;
	}

	public void setMatricularDiscenteOutroPrograma(
			boolean matricularDiscenteOutroPrograma) {
		this.matricularDiscenteOutroPrograma = matricularDiscenteOutroPrograma;
	}

	/**
	 * Este método retorna a primeira turma da coleção de turmas encontradas na busca
	 * é utilizado para imprimir o ano/período das turmas que foram buscadas no <caption> da tabela com o resultado.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/turmas_extra_curriculo.jsp</li>
	 *  <li>/sigaa.war/stricto/matricula/turmas_outros_programas.jsp</li>
	 * </ul>
	 * @return
	 */
	public Turma getPrimeiraTurmaBusca(){
		@SuppressWarnings("unchecked")
		Collection<Turma> resultado = (Collection<Turma>) getCurrentRequest().getAttribute("resultadoTurmasBuscadas");
		if( !isEmpty( resultado ) )
			return resultado.iterator().next();
		return null;
	}


	public boolean isBuscarTurmasAbertas() {
		return !isMatriculaEAD()
			&& !isTutorEad()
			&& discente != null
			&&(discente.isGraduacao() || (discente.isStricto() && tipoMatricula != SOLICITACAO_MATRICULA) || discente.isResidencia());
	}

	public boolean isRematricula() {
		return rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}

	public boolean isConcordancia() {
		return concordancia;
	}

	public void setConcordancia(boolean concordancia) {
		this.concordancia = concordancia;
	}

	public boolean isExibirConcordancia() {
		return exibirConcordancia;
	}

	public void setExibirConcordancia(boolean exibirConcordancia) {
		this.exibirConcordancia = exibirConcordancia;
	}

	public boolean getParametroConcordanciaRegulamento() {
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.CONCORDANCIA_REGULAMENTO);
	}
	
	/**
	 * Retorna a data em que a solicitação de matrícula foi gravada pela última vez.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/comprovante_solicitacoes.jsp</li>
	 * </ul>
	 * @return Data em que a solicitação de matrícula foi gravada pela última vez.
	 */
	public Date getGravadoEm(){
		Collection<Date> datas = new ArrayList<Date>();
		if(!isEmpty(solicitacoesMatricula))
			for(SolicitacaoMatricula s: solicitacoesMatricula)
				datas.add(s.getDataSolicitacao());
		if(!isEmpty(solicitacoesAtividade))
			for(SolicitacaoMatricula s: solicitacoesAtividade)
				datas.add(s.getDataSolicitacao());
		if(!ValidatorUtil.isEmpty(datas))		
			return Collections.max(datas);
		return null;
	}
	
	/**
	 * Retorna a data do primeiro dia de aula de todas as turmas selecionadas.
	 * Utilizada para inicializar a agenda de horários na data especificada.
	 * Se não houver nenhuma turma selecionada, retorna a data atual.
	 * <br/><br/>
	 * Método chamado pela seguintes JSP:
	 * <ul>
	 *   <li>  sigaa.war/graduacao/matricula/turmas_selecionadas.jsp</li>
	 * </ul>  
	 * @return
	 * @throws DAOException
	 */
	public Date getPrimeiroDiaAula() throws DAOException {
		List<HorarioTurma> horarios = getHorariosTurma();
		if(!isEmpty(horarios)){
			Collections.sort(horarios);
			return horarios.iterator().next().getDataInicio();
		}
		return new Date();
	}

	public boolean isAlunoEadDefinindoHorarioTutoria() {
		return alunoEadDefinindoHorarioTutoria;
	}

	public void setAlunoEadDefinindoHorarioTutoria(
			boolean alunoEadDefinindoHorarioTutoria) {
		this.alunoEadDefinindoHorarioTutoria = alunoEadDefinindoHorarioTutoria;
	}

	public List<HorarioTutoria> getHorariosTutoria() {
		return horariosTutoria;
	}

	public void setHorariosTutoria(List<HorarioTutoria> horariosTutoria) {
		this.horariosTutoria = horariosTutoria;
	}

	public String getConfirmButtonHorarioTutoriaPresencial() {
		return confirmButtonHorarioTutoriaPresencial;
	}

	public void setConfirmButtonHorarioTutoriaPresencial(
			String confirmButtonHorarioTutoriaPresencial) {
		this.confirmButtonHorarioTutoriaPresencial = confirmButtonHorarioTutoriaPresencial;
	}

	/**
	 * Indica se o coodenador do programa de pós pode matricular discente regular.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPermiteCoordenadorMatricularDiscenteStrictoRegular(){
		String param = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.PERMITE_COORDENACAO_MATRICULAR_ALUNO);
		return (param == null || param.equals("R") || param.equals("T"));
	}
	
	/**
	 * Indica se o coodenador do programa de pós pode matricular discente especial.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPermiteCoordenadorMatricularDiscenteStrictoEspecial(){
		String param = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.PERMITE_COORDENACAO_MATRICULAR_ALUNO);
		return (param == null || param.equals("E") || param.equals("T"));
	}

	public Collection<Turma> getTurmasSolicitadasNegadas() {
		return turmasSolicitadasNegadas;
	}

	public void setTurmasSolicitadasNegadas(
			Collection<Turma> turmasSolicitadasNegadas) {
		this.turmasSolicitadasNegadas = turmasSolicitadasNegadas;
	}
	
	
	/**
	 * Retorna as turmas ja matriculadas do discente.
	 * Exclui turmas cuja solicitacao de turma foi negada.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/matricula/turmas_selecionadas.jsp
	 * @return
	 */
	public Collection<Turma> getMatriculadas() {		
		if(ValidatorUtil.isEmpty(turmasSolicitadasNegadas)) {
			return turmasJaMatriculadas;
		} else {
			Collection<Turma> matriculadas = new ArrayList<Turma>();			
			for( Turma t : turmasJaMatriculadas ) {
				if(!turmasSolicitadasNegadas.contains(t)) {
					matriculadas.add(t);
				}
			}			
			return matriculadas;			
		}		
	}

	public void setSolicitacaoConfirmada(boolean solicitacaoConfirmada) {
		this.solicitacaoConfirmada = solicitacaoConfirmada;
	}

	public boolean isSolicitacaoConfirmada() {
		return solicitacaoConfirmada;
	}

	public List<SelectItem> getTurmasEntradaCombo() {
		return turmasEntradaCombo;
	}

	public void setTurmasEntradaCombo(List<SelectItem> turmasEntradaCombo) {
		this.turmasEntradaCombo = turmasEntradaCombo;
	}

	public TurmaEntradaTecnico getTurmaEntradaSelecionada() {
		return turmaEntradaSelecionada;
	}

	public void setTurmaEntradaSelecionada(
			TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}

	public Set<MatriculaComponente> getMatriculasDesistencia() {
		return matriculasDesistencia;
	}

	public void setMatriculasDesistencia(
			Set<MatriculaComponente> matriculasDesistencia) {
		this.matriculasDesistencia = matriculasDesistencia;
	}

	/**
	 * Método utilizado para verificar as solicitações de ensino individualizado do aluno para o ano e período da matricula.
	 * @param discente
	 * @throws DAOException
	 */
	private void verificaSolicitacaoEnsinoIndividualAtivo(DiscenteAdapter discente) throws DAOException{
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);
		
		Collection<SolicitacaoEnsinoIndividual> solicitacoesCancelar = new ArrayList<SolicitacaoEnsinoIndividual>();
		
		Collection<Integer> situacoes = new ArrayList<Integer>();
		situacoes.add(SolicitacaoEnsinoIndividual.ATENDIDA);
		situacoes.add(SolicitacaoEnsinoIndividual.SOLICITADA);
		
		solicitacoesCancelar.addAll(dao.findByDiscenteAnoPeriodo(discente.getId(), Turma.ENSINO_INDIVIDUAL, getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo(), situacoes));
	
		if (!solicitacoesCancelar.isEmpty()){
			addMensagemWarning("Prezado aluno suas solicitações de ensino individualizado serão canceladas, caso seja feita alguma alteração do plano de matrícula.");
		}	
	}
}
