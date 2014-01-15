/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Managed Bean para pr�-matr�cula e matr�cula de discentes de gradua��o em
 * turmas.
 *
 * @author ricardo
 * @author Andre Dantas
 *
 */
@Component("matriculaGraduacao") @Scope("session")
public class MatriculaGraduacaoMBean extends SigaaAbstractController<MatriculaComponente> implements OperadorDiscente {
	
	/** Modelo de agenda utlizado para exibir a agenda das turmas que o discente est� se matriculando/matriculado */ 
	private AgendaDataModel turmaAgendaModel;

	/** Discente cuja matr�cula ser� efetuada */
	private DiscenteAdapter discente;

	/** Turmas selecionadas para a matricula */
	private Collection<Turma> turmas = new ArrayList<Turma>();

	/** Turmas que o discente j� est� matriculado */
	private Collection<Turma> turmasJaMatriculadas = new ArrayList<Turma>(0);

	/** Turmas que o discente j� est� matriculado */
	private Collection<RegistroAtividade> atividadesJaMatriculadas = new ArrayList<RegistroAtividade>(0);	
	
	/** Turmas que foi negada solicitacao */
	private Collection<Turma> turmasSolicitadasNegadas = new ArrayList<Turma>(0);	

	/** Informa a opera��o que est� sendo realizada no momento no cabe�alho da p�gina  */
	private String operacaoAtual;

	/** Restri��es a serem ignoradas em caso de matr�cula compuls�ria */
	private RestricoesMatricula restricoes = new RestricoesMatricula();

	/** Situa��o da matr�cula, se em espera ou matriculado */
	private SituacaoMatricula situacao = new SituacaoMatricula();

	/** Hor�rios da unidade para ajudar a criar as tabelas de hor�rios das turmas escolhidas */
	private List<Horario> horarios;

	/** Hor�rios de disponibilidade do tutor com seus alunos */
	private List<HorarioTutoria> horariosTutoria;

	/** Dados da turma para filtrar busca de turmas abertas */
	private Turma dadosBuscaTurma;

	/** Atributo que Auxilia na busca */
	private Boolean[] boolDadosBusca = {false, false, false, false, false, false, false};

	/** N�mero da solicita��o de matr�cula gerada ap�s a submiss�o do mesmo */
	private Integer numeroSolicitacao;

	/** Lista de alunos a serem carregados quando a sele��o de aluno n�o � por busca e sim numa lista j� carregada */
	private Collection<Discente> discentesCurso;

	/** Componentes que o aluno escolhido j� concluiu, essa lista ajuda em valida��es na sele��o de turmas */
	private Collection<ComponenteCurricular> componentesMatriculadosConcluidos;

	/** Calend�rio Acad�mico para fazer verifica��es ao realizar matr�cula */
	private CalendarioAcademico calendarioParaMatricula;

	/** Turmas para sugest�o de matr�cula */
	private Collection<SugestaoMatricula> turmasCurriculo;
	/** Turmas equivalentes para sugest�o de matr�cula */
	private Collection<SugestaoMatriculaEquivalentes> turmasEquivalentesCurriculo;

	/** md5 gerado pelas turmas escolhidas numa matr�cula */
	private String md5;

	/** Identifica o tipo de matr�cula */
	private int tipoMatricula;

	/** Quantidade de matr�culas que foram orientadas (exibido na matr�cula online)*/
	private int qtdMatriculasOrientadas;

	/** Solicita��es de matr�cula realizadas */
	private Collection<SolicitacaoMatricula> solicitacoesMatricula;
	/** Solicita��es de Matr�culas de atividades realizadas */
	private Collection<SolicitacaoMatricula> solicitacoesAtividade;

	/** Tipos de matr�culas */
	public static final int SOLICITACAO_MATRICULA = 1, FORA_PRAZO = 2, COMPULSORIA = 3, ALUNO_ESPECIAL = 4, ALUNO_RECEM_CADASTRADO = 5,
		ANALISE_MATRICULA = 6, MATRICULA_FERIAS = 7, MATRICULA_EAD = 8, MATRICULA_CONVENIO = 9, TURMAS_NAO_ONLINE = 10, MATRICULA_REGULAR = 11;

	/** Orienta��o geral de matr�cula cadastrada pela coordena��o ou pelo orientador acad�mico */
	private OrientacaoMatricula orientacao;

	/** Utilizado na matr�cula compuls�ria para pegar o ano.periodo que a matricula ser� realizada */
	private int anoPeriodo;

	/** Turmas das solicita��es indeferidas */
	private HashSet<Turma> turmasIndeferidas = new HashSet<Turma>();

	/** Lista de tutorias do alunos */
	private Collection<TutoriaAluno> tutorias;

	/** Este atributo indica se o caso � matr�cula de discente de outro programa
	 * utilizado apenas para stricto */
	private boolean matricularDiscenteOutroPrograma = false;

	/** Indica se � re-matr�cula */
	private boolean rematricula = false;

	/** Indica os limites de cr�ditos (m�ximos ou m�nimos) de turmas matriculadas */
	private ExtrapolarCredito extrapolarCredito;
	/** Indica se � concordancia ou n�o */
	private boolean concordancia = false;
	/** Indica se � para exibir concordancia */
	private boolean exibirConcordancia = true;
	
	/** este atributo indica se a opera��o que est� sendo executada � de defini��o de 
	 * hor�rio de tutoria presencial que � executada pelos alunos EAD antes de poderem  
	 * realizar a matr�cula on-line.*/
	private boolean alunoEadDefinindoHorarioTutoria = false;
	
	/** Turmas de entrada disponiveis para o discente*/
	private List <SelectItem> turmasEntradaCombo;
	private TurmaEntradaTecnico turmaEntradaSelecionada;
	
	/** Texto a ser exibido no bot�o confirmar. */
	private String confirmButtonHorarioTutoriaPresencial = "Confirmar";

	/** Turmas que foram solicitadas exclus�o de matr�cula. */
	private Collection<Turma> turmasSolicitadasExclusao;
	
	/** Se a solicita��o de matr�cula foi confirmada. */
	private boolean solicitacaoConfirmada; 
	
	/** Matr�culas que o aluno solicita a remo��o da sua lista por desist�ncia. */
	private Set<MatriculaComponente> matriculasDesistencia;
	
	public void setTutorias(Collection<TutoriaAluno> tutorias) {
		this.tutorias = tutorias;
	}

	/** Construtor padr�o. */
	public MatriculaGraduacaoMBean() {
	}

	/**
	 * Limpa os dados do MBean para sua utiliza��o em uma nova opera��o de matr�cula <br>
	 * JSP: N�o invocado por JSP
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
	 * Verifica se a matr�cula corresponde ao n�vel de gradua��o. <br>
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Redireciona para a p�gina com a lista das turmas abertas para os
	 * componentes pertencentes ao curr�culo do discente.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul>
	 * JSP: 
	 * @return
	 */
	public String listarSugestoesMatricula() {
		setOperacaoAtual("Turmas Abertas do Curr�culo do Aluno");
		return redirect("/graduacao/matricula/turmas_curriculo.jsf");
	}
	
	/**
	 * Redireciona para a p�gina com a lista das turmas equivalentes abertas para os
	 * componentes pertencentes ao curr�culo do discente.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String listarSugestoesMatriculaEquivalentes() throws ArqException {
		setOperacaoAtual("Turmas Abertas do Curr�culo do Aluno");
		carregarTurmasEquivalentesCurriculo();
		return redirect("/graduacao/matricula/turmas_equivalentes_curriculo.jsf");
	}

	/** 
	 * Verifica se o ator da opera��o � um tutor de EAD. <br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/resumo_solicitacoes.jsp</li>
	 * </ul>
	 */
	public boolean isTutorEad() {
		return getAcessoMenu().isTutorEad()
			&& SigaaSubsistemas.PORTAL_TUTOR.equals(getSubSistema());
	}

	/** 
	 * Verifica se o ator da opera��o � um discente, que n�o tenha papel de secret�rio.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Verifica se a opera��o est� sendo realizada em modo otimizado.
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/_info_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isModoOtimizado() {
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.PORTAL_DISCENTE_MODO_REDUZIDO);
	}

	/** 
	 * Verifica se o ator da opera��o � um chefe de departamento
	 */
	protected boolean isChefeDepartamento() {
		return getAcessoMenu().isChefeDepartamento();
	}

	/**
	 * Inicia o caso de uso de matr�cula e vai para o caso correto de acordo com as configura��es do MBean.<br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
		/* A matr�cula de aluno especial do stricto sensu � feita pelas coordena��es dos programas de p�s
		 * atrav�s do m�todo iniciarMatriculasRegulares() */
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
	 * Inicia a matr�cula de aluno prob�sica.<br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
			throw new SegurancaException("Opera��o exclusiva de coordenadores de curso de PROBASICA");

		clear();
		situacao = SituacaoMatricula.MATRICULADO;

		tipoMatricula = MATRICULA_CONVENIO;

		restricoes = RestricoesMatricula.getRestricoesConvenio();
		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_GRADUACAO.getId());
		setOperacaoAtual("Matr�cula PROBASICA");

		return buscarDiscente();
	}

	/**
	 * Inicia a matr�cula de aluno da modalidade ensino a dist�ncia.<br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
			throw new SegurancaException("Opera��o exclusiva de coordenadores de ensino a dist�ncia");
		
		if (!isPeriodoRegular() && !getCalendarioParaMatricula().isPeriodoMatriculaAlunoCadastrado() && !isUserInRole(SigaaPapeis.COORDENADOR_GERAL_EAD)){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Matr�cula de Alunos EAD",
					getCalendarioParaMatricula().getInicioMatriculaAlunoCadastrado(), getCalendarioParaMatricula().getFimMatriculaAlunoCadastrado()));
			return null;
		}

		clear();
		tipoMatricula = MATRICULA_EAD;

		restricoes = RestricoesMatricula.getRestricoesEAD();
		situacao = SituacaoMatricula.MATRICULADO;
		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_GRADUACAO.getId());
		setOperacaoAtual("Matr�cula EAD");
		return buscarDiscente();
	}

	/**
	 * Inicia o caso de uso para as matr�culas de discentes especiais que s�o feitas pelo coordenador de cursos stricto sensu.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Inicia o caso de uso para as matr�culas que s�o feitas pelo DAE e Coordena��es.
	 * Nesse caso de uso, as matr�culas nas turmas submetidas v�o direto para o status EM ESPERA.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
			addMensagemErro("N�o est� no per�odo de matr�culas regulares ou de f�rias. Veja o calend�rio do programa.");
			return null;
		}
	}
	
	/** Iniciar a matr�cula de alunos regulares ou especiais.
	 * @return
	 * @throws ArqException 
	 */
	private String iniciarMatriculaRegularEspecial() throws ArqException{
		if( !isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) 
				&&  !isPeriodoRegular() && ( getCursoAtualCoordenacao() != null && !getCursoAtualCoordenacao().isPodeMatricular() )){
			addMensagemErro("N�o � permitido matr�culas fora do per�odo oficial de matr�culas regulares");
			return null;
		}
		if( !isUserInRole(SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) && isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma opera��o de matr�cula � permitida durante o per�odo de processamento de matr�culas");
			return null;
		}
		
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_CENTRO
				, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, 
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA);
		
		if (( SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema()) || SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema()) )&&
				getNivelEnsino() == NivelEnsino.GRADUACAO  && !getCursoAtualCoordenacao().isPodeMatricular()){
			addMensagemErro("O curso " + getCursoAtualCoordenacao() + " n�o est� configurado para permitir que " +
					"matr�culas de alunos ATIVOS sejam feitas pela coordena��o");
			return null;
		}
		setOperacaoAtual("Matr�cula On-Line");
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
	 * Inicia a matr�cula de alunos cadastrados.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
			addMensagemErro("Nenhuma opera��o de matr�cula � permitida durante o per�odo de processamento de matr�culas");
			return null;
		}
		checkRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO
				, SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG,
				SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_GRADUACAO);
		if (!getCalendarioParaMatricula().isPeriodoMatriculaAlunoCadastrado() &&
				!getCalendarioParaMatricula().isPeriodoReMatricula()){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Matr�cula de Aluno Ingressante",
					getCalendarioParaMatricula().getInicioMatriculaAlunoCadastrado(), getCalendarioParaMatricula().getFimMatriculaAlunoCadastrado()));
			return null;
		}
		concordancia = true;
		setOperacaoAtual("Matr�cula On-Line");
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
				addMensagemErro("N�o existe alunos rec�m-cadastrados no curso " + getCursoAtualCoordenacao());
				return null;
			}

			return telaSelecaoDiscente();
		} else {
			return buscarDiscente();
		}
	}

	/**
	 * Matr�cula de turmas que n�o est�o dispon�veis na matr�cula online.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
			addMensagemErro("Nenhuma opera��o de matr�cula � permitida durante o per�odo de processamento de matr�culas");
			return null;
		}
		checkRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO
				, SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG,
				SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_GRADUACAO);
		if (!isPeriodoRegular() && !getCalendarioParaMatricula().isPeriodoMatriculaAlunoCadastrado()){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Matr�cula de Turmas N�o OnLine",
					getCalendarioParaMatricula().getInicioMatriculaAlunoCadastrado(), getCalendarioParaMatricula().getFimMatriculaAlunoCadastrado()));
			return null;
		}
		setOperacaoAtual("Matr�cula de Turmas N�o On-Line");
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
	 * Inicia o caso de uso para as matr�culas de turmas de f�rias que s�o feitas pelas Coordena��es de curso.
	 * Nesse caso de uso, as matr�culas nas turmas submetidas v�o direto para o status MATRICULADO.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
			addMensagemErro("N�o est� no per�odo de matr�culas em turmas de f�rias.");
			return null;
		}
		tipoMatricula = MATRICULA_FERIAS;

		setOperacaoAtual("Matr�cula em Curso Especial de F�rias");

		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_GRADUACAO.getId());
		restricoes = RestricoesMatricula.getRestricoesTurmaFerias();

		return buscarDiscente();
	}

	/**
	 * Retorna um texto informando a situa��o do aluno.<br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/selecao_discentes.jsp</li>
	 * </ul>  
	 * @return
	 */
	public String getTituloSelecaoDiscente() {
		if (isMatriculaEAD())
			return "Matr�cula de Ensino a Dist�ncia";
		else
			return "Matr�cula de Discentes Rec�m-Cadastrados";
	}

	/**
	 * Redireciona para tela onde escolhe as restri��es que ser�o aplicadas na matr�cula do aluno escolhido.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String escolherRestricoes() {
		return forward("/graduacao/matricula/escolha_restricoes.jsp");
	}

	/**
	 * Redireciona para tela onde mostra um resumo com solicita��es de turmas.
	 * <br/>
	 * M�todo n�o chamado por JSP.
	 * @return
	 */
	public String telaResumoSolicitacoes() {
		return redirect("/graduacao/matricula/resumo_solicitacoes.jsf");
	}

	/**
	 * Gera uma MD5 com base nas solicita��es  de matr�cula.
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
	 * Redireciona para tela onde mostra a comprova��o de matr�cula.
	 * <br/>
	 * M�todo n�o chamado por JSP.
	 * @return
	 */
	public String gerarComprovanteSolicitacoes() {
		generateMD5();
		return telaComprovanteSolicitacoes();
	}

	/**
	 * Carrega as solicita��es de matr�cula e exibe.<br>
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
			addMensagemErro("N�o foi poss�vel identificar o discente para a emiss�o do comprovante de solicita��o de matr�cula.");
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
			addMensagemWarning("Voc� ainda n�o realizou matr�cula on-line no semestre " + getCalendarioParaMatricula().getAnoPeriodo());
			return null;
		}
		carregarSolicitacoesMatriculas();
		return gerarComprovanteSolicitacoes();
	}

	/**
	 * Direciona o usu�rio para a tela de comprovante das solicita��es.
	 * <br/>
	 * M�todo n�o invocado por JSP�s
	 * @return
	 */
	public String telaComprovanteSolicitacoes() {
		return redirect("/graduacao/matricula/comprovante_solicitacoes.jsf");
	}

	/**
	 * Direciona o usu�rio para a tela de confirma��o da matricula de gradua��o.<br>
	 * 
	 * JSP: N�o invocado por JSP
	 * @return
	 * @throws Exception
	 */
	private String visualizarConfirmacao() throws Exception {
		return redirect("/graduacao/matricula/confirmacao.jsf");
	}

	/**
	 * Redireciona para a tela de informa��es da tutoria.
	 * <br/>
	 * M�todo n�o chamado por JSP.
	 * @return
	 */
	public String telaInfoTutoria() {
		return forward("/graduacao/matricula/info_tutoria.jsp");
	}

	/**
	 * Redireciona pra tela de buscar outras turmas (que n�o est�o na grade curricular do aluno).<br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Redireciona para tela onde mostra as orienta��es de matr�cula dadas pelo orientador acad�mico.<br>
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li> /sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 *   <li>  sigaa.war/graduacao/matricula/turmas_selecionadas.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String telaSolicitacoes() throws DAOException {
		setOperacaoAtual("Matr�culas Submetidas");
		popularOrientacaoMatricula();
		return forward("/graduacao/matricula/turmas_analisadas.jsp");
	}

	/**
	 * Carrega a orienta��o de matr�cula dada pelo orientador acad�mico
	 * @throws DAOException 
	 */
	private void popularOrientacaoMatricula() throws DAOException {
		CalendarioAcademico calendario = getCalendarioVigente();
		OrientacaoMatriculaDao orientacaoDao = getDAO(OrientacaoMatriculaDao.class);
		orientacao = orientacaoDao.findByDiscente(discente.getDiscente(), calendario.getAno(), calendario.getPeriodo());
	}

	/**
	 * Opera��o para que o discente possa acompanhar a an�lise de suas solicita��es
	 * de matr�cula.<br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
			addMensagemWarning("Somente discentes podem acompanhar a an�lise de suas orienta��es de matr�cula");
			return null;
		} else {
			discente = discenteUsuario;
		}

		// Popular solicita��es de matr�cula
		popularSolicitacoesMatricula();
		if (isEmpty(solicitacoesMatricula) ) {
			addMensagemWarning("Voc� n�o possui solicita��es de matr�cula cadastradas para o per�odo atual");
			return null;
		}

		getCurrentRequest().setAttribute("consulta", true);
		return telaSolicitacoes();
	}

	/**
	 * Redireciona para tela onde seleciona os alunos que o tutor tem responsabilidade.<br>
	 * 
	 * JSP: N�o invocado por JSP
	 * @return
	 */
	public String telaSelecaoTutoria() {
		return forward("/graduacao/matricula/selecao_tutoria.jsp");
	}

	/**
	 * Tela onde seleciona um discente para fazer matr�cula.<br>
	 * 
	 * JSP: N�o invocado por JSP
	 * @return
	 */
	public String telaSelecaoDiscente() {
		return forward("/graduacao/matricula/selecao_discentes.jsp");
	}

	/**
	 * Tela onde mostra as turmas que o aluno selecionou.<br>
	 * JSP: N�o invocado por jsp
	 * @param redirect
	 * @return
	 */
	public String telaSelecaoTurmas(boolean redirect) {
		setOperacaoAtual("Turmas Selecionadas");
		if (redirect)
			return redirect("/graduacao/matricula/turmas_selecionadas.jsf");
		return forward("/graduacao/matricula/turmas_selecionadas.jsf");
	}
	
	/** Retorna o Modelo de agenda utlizado para exibir a agenda das turmas que o discente est� se matriculando/matriculado.
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
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Direciona o usu�rio para a tela onde exibir� as turmas de f�rias.<br>
	 * 
	 * JSP: N�o invocado por jsp. 
	 * 
	 * @return
	 */
	public String telaAlunosTurmaFerias() {
		return forward("/graduacao/matricula/discentes_ferias.jsp");
	}

	/**
	 * Vai para a tela de resumo, para exibir as turmas referentes ou
	 * �s solicita��es de matr�cula do aluno.
	 * Nessa tela � solicitada a confirma��o  da matr�cula.<br>
	 * <br/><br/>
	 * M�todo chamado pelas seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String telaResumo() throws DAOException {
		// valida��o
		if (discente == null || discente.getId() == 0) {
			addMensagemErro("Escolha um discente");
			return null;
		}

		if ((isCompulsoria() || isForaPrazo()) && (situacao == null || situacao.getId() == 0)) {
			addMensagemErro("O status da turma deve ser selecionado");
			return null;
		}
		setOperacaoAtual("Confirma��o");
		discente.setGestoraAcademica( getGenericDAO().refresh(discente.getGestoraAcademica()) );
		discente.getGestoraAcademica().getNomeMunicipio();

		return redirect("/graduacao/matricula/resumo.jsf");
	}

	/**
	 * Verifica se esta no prazo de realizar matr�cula regular
	 * @return
	 */
	private boolean isPeriodoRegular() {
		return getCalendarioParaMatricula().isPeriodoMatriculaRegular();
	}

	/**
	 * Verifica se est� no per�odo de processamento de matr�cula.
	 * @return
	 */
	protected boolean isPeriodoProcessamento() {
		return getCalendarioParaMatricula().isPeriodoProcessamento();
	}

	/**
	 * Testa se est� no per�odo de matricula de turma de ferias, ou seja,
	 * os dois primeiros dias �teis de semestres de ferias (3 e 4)
	 * @return
	 */
	private boolean isPeriodoMatriculaFerias(){
		return getCalendarioParaMatricula().isPeriodoMatriculaTurmaFerias();
	}

	/**
	 * Faz uma s�rie de valida��es antes de dar inicio a matr�cula do aluno
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
			addMensagemErro("N�o est� no per�odo oficial de matr�culas on-line");
			return;
		}

		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma opera��o de matr�cula � permitida durante o per�odo de processamento de matr�culas");
			return;
		}
		
		boolean alunoEadFazMatricula = ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.ALUNO_EAD_FAZ_MATRICULA_ONLINE);
		boolean alunoEspecialFazMatricula = ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.ALUNO_ESPECIAL_FAZ_MATRICULA_ONLINE);

		if (discente != null) {
			// alunos fazendo solicita��o de matricula
			// Se o ator for o aluno, buscar seus dados em DiscenteGraduacao
			if ((!getDiscente().isRegular() && !alunoEspecialFazMatricula )
					|| (getDiscente().getCurso() != null && getDiscente().getCurso().isProbasica())
					|| (!isTutorEad() && getDiscente().isDiscenteEad() && !alunoEadFazMatricula )
					|| (!getDiscente().isGraduacao() &&  !getDiscente().isTecnico())) {
				addMensagemErro("Essa opera��o est� dispon�vel apenas para discentes de gradua��o regulares sem conv�nio acad�mico");
				return;
			}
			
			boolean alunoEspecialFazRematricula = (!getDiscente().isRegular() && getCalendarioParaMatricula().isPeriodoReMatricula() &&
					( getDiscente().getFormaIngresso().getCategoriaDiscenteEspecial() != null && 
					  getDiscente().getFormaIngresso().getCategoriaDiscenteEspecial().isPermiteRematricula() ));
			
			if((!getDiscente().isRegular() && alunoEspecialFazMatricula && !getCalendarioParaMatricula().isPeriodoMatriculaAlunoEspecial()) && !alunoEspecialFazRematricula ){
				addMensagemErro("N�o est� no per�odo oficial de matr�culas on-line");
				return;
			}

			/* Regras de obrigatoriedade: <br/>
			 * RESOLU��O No 028/2010-CONSAD, de 16 de setembro de 2010
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
				addMensagemErro("Apenas alunos ativos podem realizar matr�culas.");
				return;
			}

			if (discente.isGraduacao() &&  ((DiscenteGraduacao)discente).isEAD() && !alunoEadFazMatricula && !isTutorEad()) {
				addMensagemErro("Discentes de ensino a dist�ncia n�o realizam solicita��o de matr�cula on-line. Quem realiza � o seu tutor.");
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
					addMensagemErro("Antes de iniciar a matr�cula � necess�rio configurar os hor�rios para tutoria presencial atrav�s do menu "+RepositorioDadosInstitucionais.get("siglaSigaa")+" -> Portal Discente -> Ensino -> Definir hor�rio de tutoria presencial.");
					return;
				}
				
			}
			
			if (discente.isGraduacao()){
				/* Verifica se o Discente Possui mobilidade estudantil no per�odo vigente. */
				MobilidadeEstudantilDao mobDao = getDAO(MobilidadeEstudantilDao.class);
				try {					
					if (mobDao.possuiMobilidadeAtivaNoPeriodo(discente, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo())){
						addMensagemErro("N�o � poss�vel realizar matr�cula, pois existe uma Mobilidade Estudantil Ativa.");
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
						
						// Se o discente � do met�pole digital e n�o tem turma de entrada, deve selecionar uma.
						if (!getDiscente().isMetropoleDigital()) {
							addMensagemErro("N�o � poss�vel realizar matr�culas de alunos sem turma de entrada cadastrada");
						}
						
						return;
					} else {
						if (getDiscente().isMetropoleDigital()) {
							addMensagemErro("Voc� j� realizou sua matr�cula");
							return;
						}
					}
					
					if(((DiscenteTecnico) discente).getTurmaEntradaTecnico().getEspecializacao() != null){
						((DiscenteTecnico) discente).getTurmaEntradaTecnico().getEspecializacao().getId();
						((DiscenteTecnico) discente).getTurmaEntradaTecnico().getEspecializacao().getDescricao();
					}
					
					/* Verifica se existe disciplinas pendentes, caso contr�rio o aluno est� conclu�do, n�o podendo se matricular */
					List<MatriculaComponente> disciplinas = discenteDao.findDisciplinasConcluidasMatriculadas(discente.getId(), true);
					Collection<ComponenteCurricular> componentesPendentes = discenteDao.findComponentesPendentesTecnico(discente.getId(), disciplinas);
					if (componentesPendentes.isEmpty()){
						addMensagemErro("N�o � poss�vel realizar matr�cula, pois todos os Componentes Curriculares obrigat�rios j� foram pagos.");
						return;
					}
				} finally {
					if (discenteDao != null)
						discenteDao.close();
				}								
			}
			
		} else {
			addMensagemErro("Essa opera��o � permitida apenas para alunos de gradua��o");
			return;
		}
	}

	/**
	 * Inicia o caso de uso feito para solicitar matr�culas em turmas. Os atores s�o os alunos de gradua��o e
	 * os tutores de EAD que solicitam matr�culas dos seus alunos.
	 * Esse solicita��o ser� analisada pela coordena��o do curso antes de serem efetivadas (passarem ao status EM ESPERA).
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
				addMensagemErro("Aten��o: voc� deve baixar uma c�pia eletr�nica do novo regulamento dos cursos de gradua��o e declarar que est� ciente das " +
						"altera��es, marcando na op��o correspondente antes de poder iniciar sua matr�cula.");
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
			setOperacaoAtual("Matr�cula OnLine");
			prepareMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
			setOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA.getId());
			return buscarDiscente();
		}
		
		if (isTutorEad()) {
			setOperacaoAtual("Matr�cula OnLine");
			prepareMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
			setOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA.getId());
			setConfirmButtonHorarioTutoriaPresencial("Pr�ximo passo >>");
			return telaSelecaoTutoria();
		} else {
			if (isEmpty(discente)) {
				addMensagemErro("Aten��o: A opera��o de solicita��o de matr�cula deve ser iniciada novamente para que os dados sejam populados corretamente.");
				return cancelar();
			}

			// Validar in�cio para discentes de gradua��o
			if (discente.isGraduacao() && discente.isRegular()) {
				// S� deixar o discente fazer a matr�cula se ele tiver preenchido a avalia��o institucional
				int ano = calendarioParaMatricula.getAnoAnterior();
				int periodo = calendarioParaMatricula.getPeriodoAnterior();

				AvaliacaoInstitucionalDao avaliacaoDao = getDAO(AvaliacaoInstitucionalDao.class);
				if (discenteDeveriaFazerAvaliacaoInstitucional(ano, periodo)) {
					CalendarioAvaliacao calAvaliacao = AvaliacaoInstitucionalHelper.getCalendarioAvaliacaoAtivo(discente);
					if (calAvaliacao != null && !avaliacaoDao
							.isAvaliacaoFinalizada(discente.getDiscente(), ano, periodo, calAvaliacao.getFormulario().getId())) {
						addMensagemErro("N�o � poss�vel realizar a matr�cula ainda porque voc� n�o preencheu a Avalia��o Institucional " +
							"referente ao per�odo " + ano + "." + periodo);
						return null;
					}
					// turmas com doc�ncia assistida
					// est� no per�odo?
					CalendarioAvaliacaoDao calAvalDao = getDAO(CalendarioAvaliacaoDao.class);
					if (calAvalDao.hasCalendarioAtivo(TipoAvaliacaoInstitucional.AVALIACAO_DOCENCIA_ASSISTIDA, discente.isDiscenteEad())) { 
						if (!avaliacaoDao.isDocenciaAssistidaFinalizada(discente.getDiscente(), ano, periodo)) {
							addMensagemErro("N�o � poss�vel realizar a matr�cula ainda porque voc� n�o preencheu a Avalia��o Institucional da Doc�ncia Assistida " +
									"referente ao per�odo " + ano + "." + periodo + ".");
								return null;
						}
					}
				}
			}

			if (discente.isTecnico() && !isPassivelSolicitacaoTecnico()) {
				return null;
			}

			if ( isNecessariaAtualizacaoDadosDiscente() ) {
				addMensagemWarning("ATEN��O: antes de realizar a matr�cula � necess�rio atualizar seus dados pessoais.");
				AlteracaoDadosDiscenteMBean bean = getMBean("alteracaoDadosDiscente");
				return bean.iniciarAcessoDiscente();
			}
			setOperacaoAtual("Matr�cula OnLine");
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
	 * Carrega as restri��es a serem verificadas durante a opera��o de matr�cula online
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
	 * Valida a obrigatoriedade e necessidade de atualiza��o dos dados pessoais dos discentes
	 * @throws DAOException 
	 */
	private boolean isNecessariaAtualizacaoDadosDiscente() throws DAOException {
		Boolean atualizacaoSolicitada = getParametrosAcademicos().getSolicitarAtualizacaoDadosMatricula();
		Date ultimaAtualizacao = discente.getPessoa().getUltimaAtualizacao();
		
		return atualizacaoSolicitada != null && atualizacaoSolicitada 
				&& (ultimaAtualizacao == null || ultimaAtualizacao.before(getCalendarioParaMatricula().getInicioMatriculaOnline()));
	}

	/**
	 * Verifica se o discente de n�vel t�cnico pode realizar solicita��es de matr�cula
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
			// Contar o total de matr�culas pendentes de consolida��o para o per�odo anterior
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
			int totalMatriculados = matriculaDao.countMatriculasByDiscente(discente.getDiscente(),
					calendarioParaMatricula.getAnoAnterior(), calendarioParaMatricula.getPeriodoAnterior(),
					SituacaoMatricula.MATRICULADO);


			if (totalMatriculados > 0) {
				addMensagemWarning("Por determina��o da " + discente.getGestoraAcademica().getNome() +
						" , sua matr�cula s� poder� ser " +
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
	 * Identifica se o discente estava matriculado em turmas em um determinado per�odo.
	 * Utilizado para identificar se o discente deveria realizar a avalia��o institucional.
	 */
	protected boolean discenteDeveriaFazerAvaliacaoInstitucional(int ano, int periodo) throws DAOException {
		return getDAO(TurmaDao.class).discenteEstavaMatriculadoNoPeriodo(getUsuarioLogado().getDiscenteAtivo().getDiscente(), ano, periodo);
	}

	/** 
	 * Lista de status poss�veis para a matr�cula ap�s a execu��o do caso de uso.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Inicia caso de uso em que podem-se realizar matr�culas em qualquer per�odo,
	 *  o status da matr�cula � escolhido pelo usu�rio e as restri��es a serem verificadas tamb�m
	 *  s�o escolhidas.<br>
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Inicia caso de uso no qual realiza-se matr�culas em qualquer per�odo. E pode-se escolher o status
	 * da matr�cula EM ESPERA ou direto em MATRICULADO.
	 * Nesse caso de uso todas as restri��es normais s�o analisadas.<br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarForaPrazo() throws ArqException {
		clear();
		if (isPeriodoRegular()){
			addMensagemErro("Ainda est� no per�odo regular de matr�culas");
			return null;
		}
		restricoes = RestricoesMatricula.getRestricoesRegular();
		return iniciarCompulsoriaForaPrazo(SigaaListaComando.MATRICULA_FORA_PRAZO);
	}

	/**
	 * Prepara o movimento para matr�cula fora de prazo ou compuls�ria.
	 * 
	 * @param comando
	 * @return
	 * @throws ArqException 
	 */
	private String iniciarCompulsoriaForaPrazo(Comando comando) throws ArqException {
		clear();
		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma opera��o de matr�cula � permitida durante o per�odo de processamento de matr�culas");
			return null;
		}
		// Apenas DAE e CDP podem executar essa opera��o
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
	 * Inicia caso de uso que realiza matr�culas para alunos especiais
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
			addMensagemErro("Nenhuma opera��o de matr�cula � permitida durante o per�odo de processamento de matr�culas");
			return null;
		}
		checkRole(new int[] { SigaaPapeis.CHEFE_DEPARTAMENTO,SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.DAE });
		if( getCalendarioParaMatricula() == null ){
			addMensagemErro("N�o foi poss�vel carregar o calend�rio vigente, contacte a administra��o.");
			return null;
		}
		if (!getCalendarioParaMatricula().isPeriodoMatriculaAlunoEspecial()) {
			addMensagemErro("N�o � permitido realizar matr�culas de alunos especiais fora do " +
					"per�odo determinado no calend�rio universit�rio");
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
	 * Redirecionar para o Managed Bean para a busca de discentes de gradua��o.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Busca alunos para a realiza��o da matr�cula compuls�ria.<br>
	 * JSP: N�o invocado por JSP
	 * @return
	 */
	public String buscarDiscenteCompulsoria() {
		return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_COMPULSORIA);
	}

	/**
	 * Busca alunos para realizar matr�cula fora do prazo.<br>
	 * JSP: N�o invocado por JSP
	 * @return
	 */
	public String buscarDiscenteForaPrazo() {
		return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_FORA_PRAZO);
	}

	/**
	 * Busca alunos especiais para realizar matr�cula.<br>
	 * JSP: N�o invocado por JSP
	 * @return
	 */
	public String buscarDiscenteEspecial() {
		return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_ALUNO_ESPECIAL);
	}

	/**
	 * Busca alunos para realizar matr�cula de f�rias.<br>
	 * JSP: N�o invocado por JSP
	 * @return
	 */
	public String buscarDiscenteFerias() {
		return buscarDiscenteMatricula(OperacaoDiscente.MATRICULA_ALUNO_FERIAS);
	}

	/**
	 * Busca alunos de acordo com a opera��o
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
	 * Inicia a etapa de sele��o de turmas.<br>
	 * 
	 * JSP: N�o invocado por JSP
	 * @return
	 */
	public String iniciarSelecaoTurmas() {
		if (isEmpty(getTurmas()))
			return listarSugestoesMatricula();
		else
			return telaSelecaoTurmas(false);
	}

	/**
	 * Direciona o usu�rio para a tela de instru��es de matr�cula.<br>
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
		

		// Verifica se o coordenador do curso liberou alguma restri��o para o aluno.
		// por enquanto as �nicas restri��es sendo liberadas s�o limite m�ximo e m�nimo de cr�ditos por semestre.
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
					alteracaoDadosBean.setConfirmButton("Confirmar altera��o e retornar para matr�cula on-line");
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
					addMensagemErro("Somente discentes com status \"Cadastrado\" podem efetuar matr�cula.");
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
		addMensagemErro("Essa opera��o � permitida apenas para alunos de Gradua��o e T�cnico");
		return null;
	}
	
	/**
	 * Verifica se o discente tem permiss�o para extrapolar o n�mero de cr�ditos para o semestre atual.
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("deprecation") // por mudan�a no modelo de limite de cr�ditos extrapolados na matr�cula
	protected void permissaoExtrapolarCredito() throws HibernateException, DAOException {
		ExtrapolarCreditoDao ecDao = getDAO(ExtrapolarCreditoDao.class);
		extrapolarCredito = ecDao.findPermissaoAtivo(discente.getDiscente(), getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo() );

		if (extrapolarCredito != null) {
			// caso a permiss�o seja no modelo antigo, sem valor m�ximo:
			if (extrapolarCredito.getCrMaximoExtrapolado() == null) {
				if (extrapolarCredito.isExtrapolarMaximo())
					restricoes.setLimiteMaxCreditosSemestre(false);
				else
					restricoes.setLimiteMinCreditosSemestre(false);
			} else {
				// modelo novo, com valor m�ximo e m�nimo
				restricoes.setValorMaximoCreditos(extrapolarCredito.getCrMaximoExtrapolado());
				restricoes.setValorMinimoCreditos(extrapolarCredito.getCrMinimoExtrapolado());
			}
		}
	}

	/** Combo de calend�rios para ser escolhido na matr�cula compuls�ria
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Carrega solicita��es previamente cadastradas e ainda n�o submetidas
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/selecao_discentes.jsp</li>
	 * </ul>
	 */
	public String selecionaDiscente() throws ArqException {

		// Se o discente ainda n�o foi carregado
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
				addMensagemErro("Essa opera��o s� permite selecionar discente cadastrados com entrada no ano-per�odo atual");
				return null;
			}
			if (discente.isTecnico()) {
				((DiscenteTecnico) discente).getTurmaEntradaTecnico().getEspecializacao().getDescricao();
			}
			
		}

		popularComponentesTurmas();

		// valida��es e decis�o de navega��o
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
			setOperacaoAtual("Escolha de Restri��es a Ignorar e Status da Matr�cula");
			return escolherRestricoes();
		} else 	if (isForaPrazo()) {
			setOperacaoAtual("Escolha do Status das Matr�culas");
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
	 * M�todo que limpa as cole��es de sugest�es de matr�cula
	 */
	private void limparSugestoes() {
		turmasCurriculo = null;
		turmasEquivalentesCurriculo = null;
	}

	/**
	 * Popula os Componentes Curriculares das Turmas.
	 * M�todo n�o chamado por JSP.
	 * @param ddao
	 * @throws DAOException
	 */
	public void popularComponentesTurmas() throws DAOException {
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		DiscenteDao ddao = getDAO(DiscenteDao.class);
		RegistroAtividadeDao rDao = getDAO(RegistroAtividadeDao.class);

		// Carregar turmas j� matriculadas por esse discente
		turmasJaMatriculadas = ddao.findTurmasMatriculadas(discente.getId());

		Collection<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>();
		situacoes.add(SituacaoMatricula.MATRICULADO);
		situacoes.add(SituacaoMatricula.APROVADO);
		
		atividadesJaMatriculadas = rDao.findByDiscente(discente.getId(), getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo(), situacoes);
		
		// Se n�o for solicita��o, buscar as solicita��es pendentes realizadas
		if (!isSolicitacaoMatricula() && !isAlunoRecemCadastrado() && !isMatriculaFerias()) {
			Collection<Turma> turmasSolicitadas = solicitacaoDao.findTurmasSolicitadasEmEspera(discente.getDiscente(),
					calendarioParaMatricula.getAno(), calendarioParaMatricula.getPeriodo());
			for (Turma t : turmasSolicitadas) {
				if (!turmasJaMatriculadas.contains(t)) {
					turmasJaMatriculadas.add(t);
				}
			}
		}
		
		//se for discente stricto o aluno n�o pode solicitar a matr�cula em uma turma
		//que ele j� tenha solicitado anteriormente e o coordenador negou ou ent�o est� aguardando an�lise do outro programa
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

		// carrega esses componentes e turmas para valida��es futuras
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
	 * Escolhe um aluno de Conv�nio.
	 * M�todo n�o chamado por JSP.
	 * @return
	 * @throws DAOException 
	 */
	private String escolhaAlunoDeConvenio() throws DAOException {
		return telaOutrasTurmas();
	}

	/**
	 * Escolhe o aluno para realizar a solicita��o.
	 * M�todo n�o chamado por JSP.
	 * @throws ArqException
	 */
	public void escolhaAlunoParaSolicitacao() throws ArqException {

		if (getCalendarioParaMatricula() == null)
			clear();
		int ano  = getCalendarioParaMatricula().getAno();
		int periodo = getCalendarioParaMatricula().getPeriodo();

		// busca as solicita��es do semestre
		// carregando solicita��es de matriculas que esse discente j� possa possuir
		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);

		/// busca apenas as cadastradas
		if (isNecessariaValidacaoDaSolicitacao()) {
			turmas = dao.findTurmasSolicitacoesByDiscente(discente.getId(), ano, periodo, SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.VISTA,
					SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.ATENDIDA,SolicitacaoMatricula.NEGADA, SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);
		} else {
			turmas = dao.findTurmasSolicitacoesByDiscente(discente.getId(), ano, periodo,
					SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA);
		}

		// Dependendo da modalidade de ensino, a orienta��o pelo coordenador � feita com status diferentes
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
						// para n�o dar erro na valida��o com os j� matriculados
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
	 * Verifica se h� uma outra solicita��o n�o-indeferida para a mesma turma da solicita��o passada como argumento.
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
	 * Remove a turma j� matriculada.
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
	 * Popula as solicita��es de matr�cula feitas pelo discente
	 * no ano/per�odo corrente
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
	 * Carrega solicita��es previamente cadastradas e ainda n�o submetidas de turmas de ferias
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
			addMensagemErro("A turma solicitada por este discente ainda n�o foi criada pelo chefe de departamento, portanto, n�o � poss�vel matricular este discente na turma de f�rias solicitada at� que ela seja criada.");
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
					// valida��es a cada turma que est� querendo se matricular
	
					try {
						// Verifica se o coordenador do curso liberou para o aluno
						// poder extrapolar o limite m�nimo ou m�ximo.
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
	 * Escolhe o status da matr�cula.
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp</li>
	 * </ul>
	 * @return
	 */
	public String escolherStatus() {
		setOperacaoAtual("Escolha do Status da Matr�cula");
		return escolherRestricoes();
	}

	/**
	 * Opera��o acionada quando o usu�rio est� apenas solicitando matr�cula em turmas.
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
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
			addMensagemWarning("Por favor, confirme a senha para fazer a solicita��o de matr�culas.");
			if (discente.isGraduacao())
				verificaSolicitacaoEnsinoIndividualAtivo(discente);
			return telaResumoSolicitacoes();
		} else {
			solicitacaoConfirmada = true;
			return confirmarSubmissaoSolicitacao();
		}
		
	}

	/**
	 * Opera��o acionada para confirmar a solicita��o de matr�cula em turmas.
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
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
			addMessage("Matr�culas submetidas com sucesso!", TipoMensagemUFRN.INFORMATION);

			// No caso de rematr�cula, re-popular turmas no portal discente 
			// para tratar casos de desist�ncia de matr�culas
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
	 * Faz as valida��es da matr�cula da turma.
	 * <br/>
	 * M�todo n�o invocado por JSPs:
	 * @return
	 * @throws ArqException
	 */
	private String validarSubmissaoSolicitacao() throws DAOException,
			ArqException {
		// Verificar sele��o do discente
		if (discente == null
				|| discente.getId() == 0
				|| !isOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA
						.getId(), SigaaListaComando.MATRICULAR_GRADUACAO
						.getId(), SigaaListaComando.MATRICULA_COMPULSORIA
						.getId(), SigaaListaComando.MATRICULA_FORA_PRAZO
						.getId(), SigaaListaComando.SELECIONAR_TURMA_ENTADA_IMD
						.getId())) {
			addMensagemErro("� necess�rio reiniciar a opera��o desde a tela inicial.");
			return cancelar();
		}

		// Verificar se a opera��o foi conclu�da anteriormente
		if (isEmpty(tipoMatricula) && isEmpty(operacaoAtual)) {
			clear();
			validarInicioSolicitacaoMatricula();
			return iniciarSolicitacaoMatricula();
		}

		if (discente.isGraduacao()) {
			try {
				// Verifica se o coordenador do curso liberou para o aluno
				// poder extrapolar o limite m�nimo ou m�ximo.
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
	 * Realizar a matr�cula do discente
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Utilizado em jsp para habilitar ou n�o o combo para escolher turmas de outros programas.
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
	 * Exibe o atestado de matr�cula.
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
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
	
	/** Indica se deve exibir a lista com as turmas j� matriculadas do aluno.
	 * @return
	 */
	public boolean isExibirTurmasJaMatriculadas() {
		return !getMatriculadas().isEmpty() && (!isSolicitacaoMatricula() || (isSolicitacaoMatricula() && isAlunoIngressante()) );
	}

	/**
	 * Buscar as turmas selecionadas e adicion�-las ao conjunto de turmas a
	 * matricular
	 * <br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * M�todo n�o invocado por JSPs:
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
			addMensagemErro("Aten��o! Esta opera��o foi conclu�da anteriormente. Por favor, reinicie o processo.");
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
			    addMensagemErro("� necess�rio selecionar no m�nimo uma turma");
			    return ;
			}
			
			int tamanhoSelecao = selecaoTurmas.length;			

			// Verificando se j� tem alguma turma de f�rias adicionada, se tiver n�o poder� adicionar outra
			// se n�o tiver poder� adicionar APENAS mais uma.
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
				// Verificando se a turma selecionada � de f�rias, n�o estando no per�odo de matr�cula de f�rias
				if (!isMatriculaFerias() && !isCompulsoria() && turma.isTurmaFerias()) {
				    addMensagemErro("A turma "+turma.getDescricao()+" n�o pode ser selecionada uma vez que n�o � o per�odo para matr�cula de turmas de f�rias.");
				    return ;
				} 
				if (discente.isDiscenteEad() != turma.isEad()) {
					addMensagemErro("A turma "+turma.getDescricao()+" n�o � da mesma modalidade de educa��o do discente.");
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
					addMensagemErro("N�o � poss�vel matricular-se na turma " + turma.getDescricaoResumida() + ", pois esta turma possui um m�todo de avalia��o diferente do m�todo de avalia��o do programa do discente.");
					metodologiasDiferentes = true;
				}
			}

			if(metodologiasDiferentes || hasErrors()) 
				return ;
			
			// Verifica se o coordenador do curso liberou para o aluno
			// poder extrapolar o limite m�nimo ou m�ximo.
			permissaoExtrapolarCredito();

			ListaMensagens todosErros = new ListaMensagens();
			Collection<ComponenteCurricular> todosComponentes = getTodosComponentes(turmasSubmetidas);
			Collection<ComponenteCurricular> componentesPagos = discenteDao.findComponentesCurricularesConcluidos(discente.getDiscente());
			

			calDao = getDAO(CalendarioAcademicoDao.class);
			for (int i = 0; i < tamanhoSelecao; i++) {
				Turma turma = turmasSubmetidas.get(i);

				if (isMatriculaTurmasNaoOnline() && turma.getDisciplina().isMatriculavel()) {
					addMensagemErro("N�o � poss�vel adicionar turmas de disciplinas matricul�veis on-line atrav�s dessa opera��o");
					return ;
				}
				
				if (discente.isStricto() && !(isUserInRole(SigaaPapeis.PPG) && isPortalPpg())){
					CalendarioAcademico cal = calDao.findByUnidadeNivel(turma.getDisciplina().getUnidade().getId(), NivelEnsino.STRICTO);
					if (cal != null && !cal.isPeriodoMatriculaRegular()){
						addMensagemErro("N�o � poss�vel matricular em turma do componente " + turma.getDisciplina().getCodigoNome() 
								+ ", pois o Programa respons�vel est� fora do prazo de matr�cula. ("+
								Formatador.getInstance().formatarData(cal.getInicioMatriculaOnline())+" - "+
								Formatador.getInstance().formatarData(cal.getFimMatriculaOnline())+")");
						return ;
					}
					if (turmasSolicitadasNegadas.contains(turma)) {
						addMensagemErro("N�o � poss�vel matricular em turma do componente " + turma.getDisciplina().getCodigoNome()+" pois seu orientador negou a solicita��o de matr�cula em turmas do componente.");
						return ;
					}
					if (turmasSolicitadasExclusao.contains(turma)) {
						addMensagemErro("N�o � poss�vel matricular em turma do componente " + turma.getDisciplina().getCodigoNome()+" pois voc� j� solicitou a exclus�o da mesma e o orientador ainda n�o analisou a solicita��o.");
						return ;
					}
				}

				// cole��o de turmas j� matriculadas e selecionadas pra se matricular
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
						todosErros.addErro("N�o � poss�vel matricular em turma do componente " + turma.getDisciplina().getCodigoNome() 
								+ " pois o componente n�o � do seu departamento.");
						continue;
					}
				}

				if (!turmas.contains(turma)) {
					// adiciona na cole��o tempor�ria
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
	 * Valida uma turma de acordo com as regras de matr�cula definidas
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
		
		// valida��es a cada turma que est� querendo se matricular
		ListaMensagens msgsErros = validarTurmaIndividualmente(discente, turma, turmasSubmetidas, todosComponentes, restricoes, componentesPagos, getCalendarioParaMatricula());

		// Validar discentes especiais de gradua��o
		if(discente.isGraduacao() && !discente.isRegular()){
			DiscenteDao ddao = getDAO(DiscenteDao.class);
			long totalPeriodoCursados = ddao.findQtdPeriodosCursados(discente.getId(), getCalendarioParaMatricula());
			MatriculaGraduacaoValidator.validarAlunoEspecial(discente.getDiscente(), turmasSubmetidas.size(), (int)totalPeriodoCursados, msgsErros);
		}
		
		// Validar discentes especiais de p�s-gradua��o
		if (!discente.isRegular() && discente.isStricto()) {
			MatriculaStrictoValidator.validarMatriculaDiscenteEspecial(discente.getDiscente(), turmasSubmetidas.size(), msgsErros.getMensagens());
		}
		return msgsErros;
	}

	
	private boolean isPermiteDuplicidadeCasoConteudoVariavel() {
		return ((isPortalCoordenadorStricto() && getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO)) || (isPortalPpg() && getUsuarioLogado().isUserInRole(SigaaPapeis.PPG)) );
	}

	/**
	 * Valida todas as turmas selecionadas, de acordo com as regras de matr�cula definidas
	 * 
	 * @return
	 * @throws ArqException
	 */
	private ListaMensagens validarSolicitacoesTurma() throws ArqException {
		ListaMensagens listaMensagens = new ListaMensagens();
		
		Collection<Turma> turmasToValidate = new ArrayList<Turma>();
		turmasToValidate.addAll(turmas); turmasToValidate.addAll(turmasJaMatriculadas);
	
		ValidatorUtil.validateEmptyCollection("� necess�rio selecionar pelo menos uma turma para confirmar a solicita��o.", turmasToValidate, listaMensagens);

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
	 * Verifica a sugest�o de componentes curriculares.
	 * <br/>
	 * M�todo n�o chamado por JSP.
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
			/*Solicita a verifica��o de turmas com reserva para o curso do discente de EAD.
			 *Caso tenha turmas diversas para o mesmo componente, mas uma dessas possua reserva para o curso, 
			 *apenas esta ser� sugerida para a matr�cula.*/ 
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
		// ... e j� matriculadas
		if (turmasJaMatriculadas != null)
			turmasSemestre.addAll(turmasJaMatriculadas);
		// as j� selecionadas ...
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
	 * E ainda os componentes das turmas escolhidas para matr�cula.
	 * Essa cole��o retornada � usada na valida��o das sele��es das turmas, especificamente na valida��o
	 * de requisitos dos componentes
	 * <br/>
	 * M�todo n�o chamado por JSP.
	 * @return
	 */
	public Collection<ComponenteCurricular> getTodosComponentes() {
		// Busca todos os componentes de turmas integralizadas
		// criando uma cole��o de todos os componentes (conclu�dos e os das turmas para matr�cula)
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
	 * Retornas todos os componentes referente as turmas passadas como par�metro.
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
	 * Retorna todos os hor�rios cadastrados para gradua��o
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
			// inclui os hor�rios inativos, caso exista
			
			
			Collections.sort(horarios, new Comparator<Horario>() {				
				public int compare(Horario o1, Horario o2) {
					return o1.getInicio().compareTo(o2.getInicio());
				}
			});
			
		}
		return horarios;
	}

	/**
	 * Retorna s� os hor�rios das turmas j� selecionadas para o discente ser matriculado
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * M�todo chamado pela seguinte JSP:
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
			addMensagemErro("� necess�rio selecionar uma turma para remo��o");
			return telaSelecaoTurmas();
		}

		// Validar remo��o e em seguida remover a turma selecionada
		Turma turma = null;
		for (Turma t : turmas) {
			if (t.getId() == idTurma) {
				if (isMatriculaTurmasNaoOnline() && t.getDisciplina().isMatriculavel()) {
					addMensagemErro("N�o � poss�vel remover turmas de disciplinas matricul�veis on-line atrav�s dessa opera��o");
					return null;
				}
				if (isDiscenteLogado() && !t.getDisciplina().isMatriculavel()) {
					addMensagemErro("N�o � poss�vel remover turmas de disciplinas n�o matricul�veis on-line atrav�s dessa opera��o");
					return null;
				}
				if (isDiscenteLogado() && discente.isStricto() && !MatriculaStrictoValidator.isPassivelCancelamentoSolicitacao(discente.getDiscente(), t, calendarioParaMatricula)) {
					addMensagemErro("N�o � poss�vel remover turmas de disciplinas cuja solicita��o j� foi atendida pelo orientador ou coordenador");
					return null;
				}

				turma = t;
				turmas.remove(t);
				break;
			}
		}

		// Testar se a turma selecionada foi encontrada na lista
		if (turma == null) {
			addMensagemWarning("A turma selecionada j� havia sido removido da lista de turmas");
			return telaSelecaoTurmas();
		}

		// Validar co-requisitos
		boolean removeuCoRequisitos = false;
		if (restricoes.isCoRequisitos()) {

			// Verificar se � necess�rio remover os co-requisitos
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

		// Atualizar status de sugest�es
		verificarComponentesSugestao();
		addMensagemInformation("Turma removida com sucesso!");
		if(removeuCoRequisitos)
			addMensagemWarning("(Obs.: As turmas dos co-requisitos desse componente tamb�m foram removidas)");

		horarios = null;
		
		return telaSelecaoTurmas();
	}

	/**
	 * Remove uma turma com registro de matricula componente do conjunto de turmas matriculadas. 
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
			addMensagemErro("� necess�rio selecionar uma turma para remo��o");
			return telaSelecaoTurmas();
		}

		// Validar remo��o e em seguida remover a turma selecionada
		Turma turma = null;
		for (Turma t : turmasJaMatriculadas) {
			if (t.getId() == idTurma) {
				if (isMatriculaTurmasNaoOnline() && t.getDisciplina().isMatriculavel()) {
					addMensagemErro("N�o � poss�vel remover turmas de disciplinas matricul�veis on-line atrav�s dessa opera��o");
					return null;
				}
				if (isDiscenteLogado() && !t.getDisciplina().isMatriculavel()) {
					addMensagemErro("N�o � poss�vel remover turmas de disciplinas n�o matricul�veis on-line atrav�s dessa opera��o");
					return null;
				}
				if (isDiscenteLogado() && discente.isStricto() && !MatriculaStrictoValidator.isPassivelCancelamentoSolicitacao(discente.getDiscente(), t, calendarioParaMatricula)) {
					addMensagemErro("N�o � poss�vel remover turmas de disciplinas cuja solicita��o j� foi atendida pelo orientador ou coordenador");
					return null;
				}

				turma = t;
				turmasJaMatriculadas.remove(t);
				break;
			}
		}

		// Testar se a turma selecionada foi encontrada na lista
		if (turma == null) {
			addMensagemWarning("A turma selecionada j� havia sido removido da lista de turmas");
			return telaSelecaoTurmas();
		}

		// Validar co-requisitos
		boolean removeuCoRequisitos = false;
		if (restricoes.isCoRequisitos()) {

			// Verificar se � necess�rio remover os co-requisitos
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

		// Atualizar status de sugest�es
		verificarComponentesSugestao();
		if (matriculasDesistencia == null)
			matriculasDesistencia = new HashSet<MatriculaComponente>();
		matriculasDesistencia.addAll(matDao.findMatriculadosByDiscenteTurmas(discente, turma.getId()));
		addMensagemInformation("Turma removida com sucesso!");
		if(removeuCoRequisitos)
			addMensagemWarning("(Obs.: As turmas dos co-requisitos desse componente tamb�m foram removidas)");

		horarios = null;
		
		return telaSelecaoTurmas();
	}
	
	/**
	 * Cancelar opera��o de matricula/pr�-matricula
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Verifica se � para exibir o quadro de hor�rios.
	 * @return
	 */
	public boolean isExibirQuadroHorarios() {
		return !isMatriculaEAD() && !isTutorEad() && (getDiscente() != null && !getDiscente().isDiscenteEad());
	}

	/**
	 * Buscar as turmas abertas para as disciplinas do curr�culo do discente
	 *
	 * @return
	 * @throws ArqException
	 */
	public Collection<SugestaoMatricula> getTurmasCurriculo() {
		return turmasCurriculo;
	}

	/**
	 * M�todo respons�vel por carregar as turmas do curr�culo do discente. 
	 * @throws DAOException 
	 */
	private void carregarTurmasCurriculo() throws DAOException {
		MatriculaGraduacaoDao matdao = getDAO(MatriculaGraduacaoDao.class);
		turmasCurriculo = matdao.findSugestoesMatricula(discente, getTurmasJaMatriculadas(),
				getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo(), isDiscenteLogado());
	}
	
	/**
	 * M�todo respons�vel por carregar as turmas equivalentes do curr�culo do discente. 
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
			/*Verifica��o de turmas com reserva para o curso do discente de EAD.
			 *Caso tenha turmas diversas para o mesmo componente, mas uma dessas possua reserva para o curso, 
			 *apenas esta ser� sugerida para a matr�cula.*/ 
			MatriculaGraduacaoValidator.verificarEquivalenciasReservasCurso(turmasEquivalentesCurriculo, discente);
		}
	}

	/** Retorna as turmas abertas para componentes equivalentes aos componentes curriculares do curr�culo do discente.
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

	/** Define o discente que far� matr�cula em componente curriculares.
	 * @throws DAOException 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.ensino.dominio.DiscenteAdapter)
	 */
	public void setDiscente(DiscenteAdapter discente) throws DAOException {
		this.discente = getDAO(DiscenteDao.class).findByPK(discente.getId());
	}

	/**
	 * Retorna lista de discentes orientados pelo tutor logado.
	 * <br/><br/>
	 * M�todo n�o chamado por JSPs:
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
	 * M�todo chamado pela seguinte JSP:
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
			setConfirmButtonHorarioTutoriaPresencial("Pr�ximo passo >>");
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

	/** Popula os hor�rios da tutoria. 
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
	
	/** Indica se an lista de hor�rios de tutoria possui algum hor�rio para o dia especificado. 
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
	 * Submiss�o do formul�rio com dados da tutoria, onde o tutor informa os
	 * per�odos em que o aluno poder� tirar d�vidas.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
			addMensagemErro("� necess�rio definir ao menos um hor�rio da tutoria presencial.");
			
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
				addMensagemInformation("Hor�rio de tutoria presencial definido com sucesso!");
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			}
			
			
			return cancelar();
		}
	}

	/**
	 * Submete e valida a submiss�o das restri��es a serem ignoradas (no caso da compuls�ria) e
	 * o status da matricula escolhido pelo usu�rio (tamb�m no caso de fora do prazo)
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/escolha_restricoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String submeterRestricoes() throws DAOException {
		if ((isCompulsoria() || isForaPrazo()) && (situacao == null || situacao.getId() == 0)) {
			addMensagemErro("O status da matr�cula deve ser selecionado");
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
	 * Carrega turmas por express�o.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
				getCurrentRequest().setAttribute("erroExpressao", "N�o foi poss�vel carregar express�o");
				return null;
			}
			Collection<ComponenteCurricular> componentes = ExpressaoUtil.expressaoToComponentes(expressao, discente.getId());
			if (isEmpty(componentes)) {
				getCurrentRequest().setAttribute("erroExpressao", "N�o foi poss�vel carregar express�o");
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
	 * M�todo chamado pelas seguintes JSPs:
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
			addMensagemErro("Por favor, escolha algum crit�rio de busca");
		} else {
			try {
				if( !boolDadosBusca[5] ){
					ano = getCalendarioParaMatricula().getAno();
					periodo = getCalendarioParaMatricula().getPeriodo();
				}

				// Definir tipos de turma a serem buscados, dependendo do tipo de matr�cula
				Collection<Integer> tiposTurma = new ArrayList<Integer>(3);
				tiposTurma.add(Turma.REGULAR);
				if( isCompulsoria() ){
					tiposTurma.add(Turma.ENSINO_INDIVIDUAL);
					tiposTurma.add(Turma.FERIAS);
				}

				// Verificar se � matr�cula em turma de f�rias
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
					addMensagemWarning("N�o foram encontradas turmas abertas para os par�metros de busca especificados.");
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
	 * Realiza o filtro das turmas j� solicitadas/matriculadas para o aluno. Para que na consulta sejam disponibilizadas para 
	 * a realiza��o da matr�cula apenas os componentes para os quais o discente n�o tenha solicita��o ou matr�cula ativa.
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
	 * Retorna todos os alunos que tem como tutor o usu�rio logado.
	 * Essa consulta � usada na opera��o de matr�cula feita por um tutor EaD
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Retorna todos os alunos que solicitaram turma de f�rias de um curso.
	 * Esse m�todo � usado quando um coordenador de curso est� fazendo matricula de alunos em
	 * turmas de f�rias do seu curso
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Esse m�todo serve pra definir o tipo de Matr�cula. 
	 * 
	 * JSP: M�todo n�o invocado por jsp.
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
	
	/** Verifica se o aluno da solicita��o de matr�cula � ingressante e n�o reingresso.*/
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
	 * Tem como finalidade carregar as solicita��es de matr�cula do discente logado.
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
	 * Retorna o n�mero de solicita��es formatadas.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Este m�todo retorna a primeira turma da cole��o de turmas encontradas na busca
	 * � utilizado para imprimir o ano/per�odo das turmas que foram buscadas no <caption> da tabela com o resultado.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
	 * Retorna a data em que a solicita��o de matr�cula foi gravada pela �ltima vez.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/comprovante_solicitacoes.jsp</li>
	 * </ul>
	 * @return Data em que a solicita��o de matr�cula foi gravada pela �ltima vez.
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
	 * Utilizada para inicializar a agenda de hor�rios na data especificada.
	 * Se n�o houver nenhuma turma selecionada, retorna a data atual.
	 * <br/><br/>
	 * M�todo chamado pela seguintes JSP:
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
	 * Indica se o coodenador do programa de p�s pode matricular discente regular.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Indica se o coodenador do programa de p�s pode matricular discente especial.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * M�todo utilizado para verificar as solicita��es de ensino individualizado do aluno para o ano e per�odo da matricula.
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
			addMensagemWarning("Prezado aluno suas solicita��es de ensino individualizado ser�o canceladas, caso seja feita alguma altera��o do plano de matr�cula.");
		}	
	}
}
