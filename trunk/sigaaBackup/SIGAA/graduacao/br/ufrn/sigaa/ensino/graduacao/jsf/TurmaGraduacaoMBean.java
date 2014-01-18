/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 02/02/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.ReservaCursoDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoTurmaDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dao.PlanoMatriculaIngressantesDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dao.TurmaGraduacaoDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.dominio.TurmaSolicitacaoTurma;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.TurmaMBean;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.jsf.CursoMBean;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * MBean respons�vel por opera��es com Turma de Gradua��o.
 * @author Leonardo
 * @author Victor Hugo
 * @author �dipo Elder F. Melo (refactoring)
 * 
 */
@Component("turmaGraduacaoBean") @Scope("session")
public class TurmaGraduacaoMBean extends TurmaMBean {
	
	/** Define o link para o formul�rio de dados gerais. */
	public static final String JSP_DADOS_GERAIS = "/graduacao/turma/dados_gerais.jsp";
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_RESUMO = "/graduacao/turma/resumo.jsp";
	/** Define o link para para o relat�rio de componentes sem solicita��o de turmas. */
	public static final String JSP_COMPONENTES_SEM_SOLICITACAO = "/graduacao/turma/componentes_sem_solicitacao.jsp";
	/** Define o link para para o formul�rio de confirma��o de remo��o de turma. */
	public static final String JSP_CONFIRMA_REMOCAO  = "/graduacao/turma/confirma_remocao.jsp";
	/** Define o link para para o formul�rio de confirma��o de remo��o de turma. */
	public static final String JSP_CONFIRMA_MATRICULA_ENSINO_INDIVIDUAL  = "/graduacao/turma/resumo_matricula_ensino_individual.jsp";
	/** constante que indica atendimento a solicitacao de turma. */
	public static final int ATENDER_SOLICITACAO_TURMA  = 1;
	
	/** Sele��o de p�lo EAD referente � turma. */
	private Collection<SelectItem> polos;
	/** Lista de escolhas de p�los pelo usu�rio. */
	private Integer[] polosEscolhidos;
	/** id do curso para adicionar reserva. */
	private int idCursoReserva = 0;	
	/** indica qual operacao esta sendo executada sobre determinada turma. */
	private int operacaoTurma = 0;	
	
	/** Lista de componentes curriculares para o qual o chefe de departamento pode criar turmas sem solicita��o. */ 
	private Collection<ComponenteCurricular> componentesSemSolicitacao;
	
	/** Lista de selectItens de matrizes curriculares que podem ter reservas na turma. */
	private List<SelectItem> possiveisMatrizes = new ArrayList<SelectItem>(0);
	/** Outras solicita��es de turmas que foram atendidas pela cria��o desta turma.*/
	private List<SolicitacaoTurma> outrasSolicitacoes;
	/** Cole��o de outras turmas criadas do componente mesmo hor�rio. */
	private Collection<Turma> outrasTurmas = new ArrayList<Turma>();
	/** Lista de outras solicita��es de turmas. No caso de solicita��o de turma de f�rias, o chefe tamb�m pode juntar solicita��es. */
	private List<SolicitacaoTurma> outrasSolicitacoesFeriasAtendidas = new ArrayList<SolicitacaoTurma>();
	/** Cole��o de discentes que solicitaram a turma. */
	private List<DiscentesSolicitacao> discentesSolicitacao;
	/** Indica que a opera��o atual � de remo��o de turma. */
	private boolean remover;
	/** Quantidade de subturmas a ser criadas no atendimento da solcita��o de turma. */
	private int quantidadeSubturmas;
	
	/** Indica se ser� criada uma �nica turma para curso, ou turmas separadas por p�lo. */
	private boolean turmaUnicaCursoEad = false;
	
	/** Indica que a turma a ser criada � de conv�nio PROB�SICA. */
	private boolean turmaProbasica;
	
	/** Construtor padr�o. */
	public TurmaGraduacaoMBean() throws DAOException {
		super();
	}
	
	/**
	 * Indica se a opera��o corrente � de edi��o do c�digo da turma. O c�digo da
	 * turma ser� aterado somente quando a turma est� sendo editada e quando n�o
	 * permite subturmas.
	 * 
	 * @return
	 */
	public boolean isEditarCodigoTurma() {
		return obj.getId() != 0 && !obj.getDisciplina().isAceitaSubturma() && !isTurmaEad();
	}
	
	/**
	 * Inicia a opera��o do DAE de adicionar reserva a uma turma sem precisar de
	 * solicita��o de turma. S� PODE SER REALIZADO POR ADMINISTRADOR DAE.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarAdicionarReservaSemSolicitacao() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO);

		int id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id, Turma.class);
		obj.getReservas().iterator();

		setReserva(new ReservaCurso());
		getReserva().setTurma( obj );

		return forward(JSP_RESERVAS);
	}
	
	/**
	 * Busca de turmas de acordo com os par�metros.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/relatorios/turma/seleciona_turmas_ano_periodo.jsp</li>
	 * <li>sigaa.war/graduacao/relatorios/turmas_oferecidas.jsp</li>
	 * <li>sigaa.war/graduacao/turma/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws DAOException {

		if (obj.getAno() <= 0 || obj.getPeriodo() <= 0) {
			addMensagemErro("Informe Ano e Per�odo v�lidos.");
			return null;
		}
		if (getAcessoMenu().isChefeDepartamento() || getAcessoMenu().isSecretarioDepartamento()) {
			obj.getDisciplina().getUnidade().setId(getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
		}
		if (isUserInRole(SigaaPapeis.SECRETARIA_CENTRO)) {
			Unidade departamento = getGenericDAO().findByPrimaryKey(obj.getDisciplina().getUnidade().getId(), Unidade.class);
			if (departamento.getGestora().getId() != getUsuarioLogado().getVinculoAtivo().getUnidade().getId()) {
				addMensagemErro("S� permitido buscar por turmas de departamento do seu centro.");
				return null;
			}
		}
		TurmaDao dao = getDAO(TurmaDao.class);
		Collection<Turma> resultado;
		if (obj.getSituacaoTurma().getId() != 0) {
			resultado = dao.findByAnoPeriodo(obj.getAno(), obj.getPeriodo(), obj.getSituacaoTurma()
					.getId(), obj.getDisciplina().getUnidade().getId(), NivelEnsino.GRADUACAO, null);
		} else {
			resultado = dao.findByAnoPeriodo(obj.getAno(), obj.getPeriodo(), null, 
					obj.getDisciplina().getUnidade().getId(), NivelEnsino.GRADUACAO, null);
		}

		setResultadosBusca(resultado);
		
		if(resultado == null || resultado.isEmpty()) {
			addMensagemWarning("N�o foram encontradas turmas com os par�metros informados");
			return null;
		}
		
		if (obj.getDisciplina().getUnidade().getId() != 0) {
			UnidadeDao daoU = getDAO(UnidadeDao.class);
			obj.getDisciplina().setUnidade(daoU.findByPrimaryKey(obj.getDisciplina().getUnidade().getId(), Unidade.class));
		} else {
			obj.getDisciplina().getUnidade().setNome("TODOS");
		}

		return null;
	}
	
	/** Retorna o n�mero total de solicita��es atendidas com a cria��o da turma.
	 * @return
	 * @throws DAOException
	 */
	public Integer getTotalSolicitacoes() throws DAOException {
		if (obj.getId() > 0) {
			TurmaDao dao = getDAO(TurmaDao.class);
			return dao.findTotalSolicitacoesMatricula(obj.getId());
		} else {
			return null;
		}
	}
	
	/**
	 * Inicia o cadastro de turmas sem ser a partir de solicita��o de turma,
	 * apenas SEDIS e DAE podem realizar. Departamentos s� podem criar turmas a
	 * partir de solicita��es.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>sigaa.war/stricto/menus/matricula.jsp</li>
	 * <li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @see TurmaGraduacaoMBean#iniciarTurmaSemSolicitacao() 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		clear();
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.SEDIS, SigaaPapeis.PPG,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.COORDENADOR_CURSO});

		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) && isPortalCoordenadorGraduacao()) {
			if (getCursoAtualCoordenacao().isProbasica()) {
				obj.setCurso(getCursoAtualCoordenacao());
				obj.setTipo( Turma.REGULAR );
			} else {
				throw new SegurancaException("Apenas coordenadores de cursos PROBASICA tem acesso a essa opera��o");
			}
		} else if (isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG)) {
			obj.setTipo( Turma.REGULAR );
		}

		obj.setSituacaoTurma( new SituacaoTurma( SituacaoTurma.A_DEFINIR_DOCENTE ) );
		if( isUserInRole( SigaaPapeis.PPG ) ){
			obj.setAno(CalendarUtils.getAnoAtual());
			obj.setPeriodo(getPeriodoAtual());
		}else{
			obj.setAno(getCalendario().getAno());
			obj.setPeriodo(getCalendario().getPeriodo());
		}

		if( obj.isTurmaFerias() ){
			obj.setDataInicio(getCalendario().getInicioFerias());
			obj.setDataFim( getCalendario().getFimFerias() );
		} else {
			obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
			obj.setDataFim( getCalendario().getFimPeriodoLetivo() );
		}

		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA);
		return formDadosGerais();
	}

	/**
	 * Inicia cadastro para turmas de cursos de pro-b�sica.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarProbasica() throws ArqException {
		clear();
		turmaProbasica = true;
		checkRole(new int[] { SigaaPapeis.GESTOR_PROBASICA, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE});
		if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) && !getCursoAtualCoordenacao().isProbasica() ){
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o.");
		}

		CursoDao dao = getDAO(CursoDao.class);
		setCursosCombo(toSelectItems(dao.findByConvenioAcademico(ConvenioAcademico.PROBASICA, NivelEnsino.GRADUACAO), "id", "descricao"));
		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA);

		obj.setAno(getCalendario().getAno());
		obj.setPeriodo(getCalendario().getPeriodo());
		obj.setSituacaoTurma( new SituacaoTurma( SituacaoTurma.A_DEFINIR_DOCENTE ) );
		obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
		obj.setDataFim( getCalendario().getFimPeriodoLetivo() );

		return formSelecaoComponente();
	}

	/**
	 * Adiciona reservas de vagas para matriz curriculares, pelo DAE, que n�o
	 * foram solicitadas pelas coordena��es de curso.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/form_reservas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String adicionarReservaSemSolicitacao() throws ArqException{

		ReservaCursoDao dao = getDAO(ReservaCursoDao.class);

		validateRequiredId(idCursoReserva, "Curso", erros);
		validateRequired(getReserva().getMatrizCurricular(), "Matriz Curricular", erros);
		ValidatorUtil.validateMinValue( new Integer( getReserva().getVagasReservadas() ) , 1, "Vagas reservadas", erros);

		
		int vagasReservadasTurma = 0;
//		dao.lock(obj);
		if( !isEmpty( obj.getReservas() ) ){
			for( ReservaCurso rc : obj.getReservas() )
				vagasReservadasTurma += rc.getVagasReservadas();
		}
		if( !obj.isEad() && getReserva().getVagasReservadas() + vagasReservadasTurma > obj.getCapacidadeAluno() ){
			erros.addErro("N�o � poss�vel adicionar esta reserva pois o total de vagas reservadas n�o pode ser superior a capacidade da turma.");
		}
		
		if( hasErrors() ){
			return null;
		}

		// recuperando dados para visualiza��o
		getReserva().setMatrizCurricular(dao.refresh(getReserva().getMatrizCurricular()));
		getReserva().setSolicitacao(null);
		getReserva().setTurma(obj);
		getReserva().setPossuiVagaIngressantes(getReserva().getVagasReservadasIngressantes() > 0);
		if (obj.getReservas().contains(getReserva()))
			addMensagemErro("J� existe uma reserva para esta matriz nessa turma.");
		else
			obj.addReservaCurso(getReserva());
		setReserva(new ReservaCurso());
		return forward( JSP_RESERVAS );
	}

	/**
	 * Remove reservas de turma que n�o possuem solicita��o. Apenas o DAE pode
	 * realizar.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/form_reservas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removerReservaSemSolicitacao() throws ArqException{

		int id = getParameterInt("idMatriz", 0);
		Iterator<ReservaCurso> iterator = obj.getReservas().iterator();
		while (iterator.hasNext()) {
			ReservaCurso reserva = iterator.next();
			if (reserva.getMatrizCurricular().getId() == id) {
				if (reserva.getVagasReservadasIngressantes() > 0)
					addMensagemErro("N�o � poss�vel remover pois h� Planos de Matr�culas de Ingressantes cadastrados para esta reserva.");
				else
					iterator.remove();
				break;
			}
		}

		return forward( JSP_RESERVAS );
	}	

	
	/** Indica se o usu�rio pode alterar o hor�rio da turma ou n�o.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#isPodeAlterarHorarios()
	 */
	@Override
	public boolean isPodeAlterarHorarios() {
		if (isAtendimentoSolicitacao() && obj.getId() == 0)
			return false;
		else if (obj.getId() == 0)
			return true;
		boolean isPeriodo;
		if (obj.isTurmaFerias())
			isPeriodo =  getCalendarioVigente().isPeriodoCadastroTurmasFerias();
		else if (obj.isTurmaEnsinoIndividual())
			isPeriodo = getCalendarioVigente().isPeriodoCadastroTurmaEnsinoIndiv();
		else 
			isPeriodo = getCalendarioVigente().isPeriodoCadastroTurmas() || getCalendarioVigente().isPeriodoAjustesTurmas();
		return obj.isAberta()
				&& (
					 isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) ||
					 isUserInRole(SigaaPapeis.PPG, SigaaPapeis.DAE) && !isMatriculada() || 
					(
						(obj.getId() == 0 && !isAtendimentoSolicitacao() || !isMatriculada() && !isAtendimentoSolicitacao()) && !isAdicionarOutrasReservas()
						&& isPeriodo && !isMatriculada() 
					) || 
					(
						obj.getSolicitacao() == null && isPeriodo && !isMatriculada() 
					) ||
					(obj.isTurmaRegularOrigemEnsinoIndiv() && isPeriodo)
				);
	}

	@Override
	public void checkRoleCadastroTurmaSemSolicitacao(ListaMensagens erros) throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE, new int[] {SigaaPapeis.DAE, SigaaPapeis.SEDIS});
		// verifica, para o chefe/scret�rio de departamento, se est� no per�odo de cria��o de turma
		boolean isPeriodo = getCalendarioVigente().isPeriodoCadastroTurmas() || getCalendarioVigente().isPeriodoCadastroTurmaEnsinoIndiv() || getCalendarioVigente().isPeriodoCadastroTurmasFerias();
		if (!isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) &&
				isUserInRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE) && !isPeriodo){
			erros.addErro("N�o est� no per�odo de cadastro de turmas.");
		}
	}	
	
	@Override
	public String formDadosGerais() {
		return forward(JSP_DADOS_GERAIS);
	}

	@Override
	public void validaDadosGerais(ListaMensagens erros) throws DAOException {
		if( obj.getDisciplina() == null || obj.getDisciplina().getId() <= 0 ){
			addMensagemErro("Selecione um componente curricular.");
		} else 	if (!obj.getDisciplina().isAtivo()) {
			addMensagemErro("O componente curricular " + obj.getDisciplina().getDescricaoResumida() + " n�o est� ativo.");
		}
		
		if (isDefineQuantidadeSubturmas()){
			validateMinValue(quantidadeSubturmas, 1, "Quantidade de Subturmas a Criar", erros);
		}
		
		if(erros.isErrorPresent()) return;

		TurmaDao dao = getDAO(TurmaDao.class);
		obj.setPolo( dao.refresh( obj.getPolo() ) );
		if( obj.getPolo() != null && obj.getPolo().getCidade() != null )
			obj.getPolo().getCidade().getNomeUF();

		if (turmaProbasica) {
			validateRequiredId(obj.getCurso().getId(), "Curso", erros);
		}

		TurmaValidator.validaDadosBasicos(obj, getUsuarioLogado(), erros);
		if ( obj.isTurmaEnsinoIndividual() || obj.isTurmaFerias() ) {
			TurmaValidator.validaCapacidadeTurma(obj, null, erros);
		}

		if ( obj.isDistancia() ) {
			if (!isTurmaUnicaCursoEad()) {
				if (((polosEscolhidos == null || polosEscolhidos.length == 0) && obj.getId() == 0) || (obj.getId() != 0 && (obj.getPolo() == null || obj.getPolo().getId() == 0))) {
					addMensagemErro("Selecione um P�lo");
				}
			} else {
				validateRequiredId(obj.getCurso().getId(), "Curso", erros);
				obj.setPolo(new Polo());
			}
		}
		
		if (obj.getCurso() != null && obj.getCurso().getId() > 0)
			obj.setCurso(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));

		// tratando sub-turmas 
		if( obj.getDisciplina().isAceitaSubturma() ){
			int idTurmaAgrupadora = getParameterInt("turmaAgrupadoraSelecionada", 0);
			if( idTurmaAgrupadora > 0 ){
				obj.setTurmaAgrupadora( dao.findByPrimaryKey(idTurmaAgrupadora, Turma.class) );
			} else {
				obj.setTurmaAgrupadora(null);
			}
		} else {
			// verificando o c�digo da turma, quando se trata de altera��o de dados
			if (obj.getId() != 0 && !isTurmaEad()) {
				
				// se n�o for turma de ensino individual (Turmas de ensino individual possuem codigo 'IND') 
				// e caso possua letras no c�digo da turma, remove
				if (!obj.isTurmaEnsinoIndividual() && obj.getTurmaAgrupadora() == null )
					obj.setCodigo(removeLetras(obj.getCodigo()));
				
				// verifica se o c�digo informado � v�lido.
				if (obj.getTurmaAgrupadora() != null && obj.getCodigo().equals(obj.getTurmaAgrupadora().getCodigo())) {
					addMensagemErro("Informe um c�digo diferente para a turma.");
					return;
				}
				// verifica se existe turma com o c�digo informado.
				Collection<Turma> turmas = dao.findByDisciplinaAnoPeriodo(obj.getDisciplina(), obj.getAno(), obj.getPeriodo(), 0, (char) 0);
				for (Turma turma : turmas){
					if (obj.getId() != turma.getId() && turma.getCodigo().equals(obj.getCodigo())) {
						addMensagemErro("Existe uma turma com o c�digo informado. Por favor, informe outro c�digo para a turma.");
						return;
					}
				}
			}
		}

		if (obj.getTurmaAgrupadora() != null){
			if (obj.getAno() != obj.getTurmaAgrupadora().getAno())
				addMensagemErro("O ano da subturma deve ser igual ao ano da turma agrupadora.");
			if (obj.getPeriodo() != obj.getTurmaAgrupadora().getPeriodo())
				addMensagemErro("O per�odo da subturma deve ser igual ao per�odo da turma agrupadora.");
		}
		
		
		// Caso seja turma de gradua��o a valida��o das datas ser� feita no HorarioTurmaMBean
		if (!obj.isGraduacao()){		
			// valida se a data informada / alterada pelo chefe de departamento est� dentro do per�odo de f�rias
			if (obj.isTurmaFerias() && getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE)){
				CalendarioAcademico calTurma = CalendarioAcademicoHelper.getCalendario(obj);
				validateMinValue(obj.getDataInicio(), calTurma.getInicioFerias(), "In�cio", erros);
				validateMaxValue(obj.getDataFim(), calTurma.getFimFerias(), "Fim", erros);
			}
			
			if (erros.isErrorPresent())
				return;
	
			if (!isUserInRole(SigaaPapeis.DAE) && obj.getDataInicio() == null ) {
				if( obj.isTurmaRegular() )
					obj.setDataInicio(getCalendario().getInicioPeriodoLetivo());
				else if( obj.isTurmaFerias() )
					obj.setDataInicio(getCalendario().getInicioFerias());
			}
			if (!isUserInRole(SigaaPapeis.DAE) && obj.getDataFim() == null ) {
				if( obj.isTurmaRegular() )
					obj.setDataFim(getCalendario().getFimPeriodoLetivo());
				else if( obj.isTurmaFerias() )
					obj.setDataFim(getCalendario().getFimFerias());
			}
		}	
	}
	
	/**
	 * Chama o processador e realiza o cadastro da turma. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/resumo.jsp</li>
	 * </ul>
	 */
	@Override
	public final String cadastrar() throws ArqException {

		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {
			Comando cmd = SigaaListaComando.CADASTRAR_TURMA;
			TurmaMov mov = new TurmaMov();			
			if (getConfirmButton().equalsIgnoreCase("alterar")) {
				cmd = SigaaListaComando.ALTERAR_TURMA;
				mov.setAlteracaoTurma(getAlteracaoTurma());
			}
			if (obj.getCurso() != null && obj.getCurso().getId() == 0) 
				obj.setCurso(null);
			
			if( obj.isEad() || isTurmaEad() ){
				if (isTurmaUnicaCursoEad()) {
					obj.setPolo(null);
				} else {
					mov.setPolos(polosEscolhidos);
					obj.setCurso(null);
				}
			}
			
			mov.setCodMovimento(cmd);
			mov.setTurma(obj);
			mov.setQuantidadeSubturmas(quantidadeSubturmas);
			
			if( getSolicitacao() != null && (getSolicitacao().isTurmaEnsinoIndividual() || getSolicitacao().isTurmaFerias() )) {
				mov.setSolicitacaoEnsinoIndividualOuFerias( getSolicitacao() );
				mov.setSolicitacoes(outrasSolicitacoesFeriasAtendidas);
			}else{
				mov.setSolicitacaoEnsinoIndividualOuFerias(null);
				mov.setSolicitacoes( new HashSet<SolicitacaoTurma>() );
				if (obj.getReservas() != null)
					for( ReservaCurso r : obj.getReservas() ){
						mov.getSolicitacoes().add( r.getSolicitacao() );
					}
			}

			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}

			super.afterCadastrar();
			if (cmd.equals(SigaaListaComando.CADASTRAR_TURMA)) {
				if( obj.getDisciplina().isSubUnidade() )
					addMessage("ATEN��O! A turma criada foi de uma subunidade de um bloco e s� estar� dispon�vel para matr�cula ap�s todas as subunidades do bloco terem sido criadas.", TipoMensagemUFRN.INFORMATION);
				if(isTurmaEad()) {
					addMensagemInformation("Turma(s) Criada(s) com sucesso!");
				}
				else{
					addMessage("Turma " + obj.getDescricaoSemDocente() + " cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
				}
				
			} else {
				addMessage("Turma " + obj.getDescricaoSemDocente() + " alterada com sucesso!", TipoMensagemUFRN.INFORMATION);
			}
			verificaAlteracaoCodigoTurma();
			if( isAtendimentoSolicitacao() ){
				AnaliseSolicitacaoTurmaMBean analiseMBean = (AnaliseSolicitacaoTurmaMBean) getMBean("analiseSolicitacaoTurma");
				return analiseMBean.retornaAtencimendoSolicitacao();
			}
	
			if (cmd.equals(SigaaListaComando.ALTERAR_TURMA)) {
				BuscaTurmaMBean mBean = getMBean("buscaTurmaBean");
				return mBean.buscarGeral();
			} else {
				return cancelar();
			}
		}
	}
	
	/**
	 * Remove o bean da sess�o e redireciona o usu�rio 
	 * para a p�gina de solicita��es.
	 </b>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/form_reservas.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cancelar() {
		try {
			if (operacaoTurma == ATENDER_SOLICITACAO_TURMA) {
				operacaoTurma = 0;
				AnaliseSolicitacaoTurmaMBean analiseMBean = (AnaliseSolicitacaoTurmaMBean) getMBean("analiseSolicitacaoTurma");
				return analiseMBean.gerenciarSolicitacoes();
			}
		} catch (ArqException e) {			
			return super.cancelar();
		}
		operacaoTurma = 0;
		return super.cancelar();
		
			
	}
	
	/**
	 * Chama o processador e inativa a turma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/resumo.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		
		TurmaMov mov = new TurmaMov();
		mov.setCodMovimento(SigaaListaComando.REMOVER_TURMA);
		mov.setTurma(obj);

		try {
			Turma removida = (Turma) executeWithoutClosingSession(mov, getCurrentRequest());
			if( removida.getPolo() != null )
				removida.setPolo( getGenericDAO().findByPrimaryKey( removida.getPolo().getId() , Polo.class) );
			addMessage("Turma " + removida.getDescricaoSemDocente() + " removida com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return getFormPage();
		} catch (SegurancaException e) {
			throw e;
		}

		clear();
		return cancelar();
	}

	@Override
	public void validarPreRemover() throws ArqException {
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class);
		
		List<PlanoMatriculaIngressantes> planos = dao.findByTurma(obj);
		
		if (isNotEmpty(planos)) {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("N�o � poss�vel remover a turma porque ela esta vinculado a um plano de matr�cula para aluno ingressante. <br>Planos:");
			
			for (PlanoMatriculaIngressantes p : planos) {
				sb.append("<br>");
				sb.append(p);
			}
			
			addMensagemErro(sb.toString());
			
		}
	}

	/**
	 * Inicia o caso de uso de atualiza��o de turmas.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 */
	@Override
	public void checkRoleAtualizarTurma() throws ArqException {
		if ( !OperacaoTurmaValidator.isPermiteAlterar(obj) ) {
			addMensagemErro("Caro usu�rio, com seu n�vel de permiss�o n�o � poss�vel alterar esta turma.");
			return;
		}
	}
	
	/**
	 * Verifica se � poss�vel remover a turma selecionada.
	 * <br />
	 * M�todo n�o chamado por JSPs.
	 */
	public void checkRolePreRemover() throws DAOException{
		// Validar n�vel do componente selecionado
		if( !isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE) && !obj.isPassivelRemocaoPeloChefe( getCalendario() ) ){
			// a mensagem para o chefe de departamento � mais detalhada:
			if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)) {
				if (!getCalendarioVigente().isPeriodoAjustesTurmas() && !getCalendarioVigente().isPeriodoCadastroTurmas() && !getCalendarioVigente().isPeriodoSugestaoTurmaChefe())
					addMensagemErro("N�o � poss�vel remover a turma selecionada pois n�o est� no per�odo de cadastro turmas.");
				else
					addMensagemErro("N�o � poss�vel remover a turma selecionada pois a turma possui mais de "+
							ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_PERMITE_REMOCAO_TURMA_PELO_CHEFE)
							+" discentes matriculados ou em espera de processamento da matr�cula.");
			} else
				addMensagemErro("N�o � poss�vel remover a turma selecionada pois existem matr�culas ativas " +
				"ou em espera de processamento associadas a ela.");
			return;
		} else if( isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE) && !obj.isPassivelRemocaoPeloChefe( getCalendario() ) ){
			addMensagemWarning("Existem matr�culas ativas ou em espera de processamento associadas � turma selecionada.");
		}
		this.remover = true;
	}
	
	/**
	 * Retorna todos os componentes que pode ser criado turma sem que haja solicita��o de turma
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> getComponentesSemSolicitacao() throws DAOException{
		Unidade unidade = getServidorUsuario().getUnidade();
		
		if (getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isResponsavel())
			unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
		else if(getCursoAtualCoordenacao() != null)				
			unidade = getCursoAtualCoordenacao().getUnidadeCoordenacao();				
		
		if (componentesSemSolicitacao == null) {
			ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
			componentesSemSolicitacao = dao.findComponentesSemSolicitacao( unidade.getId() );
		}
		return componentesSemSolicitacao;
	}
	
	@Override
	public boolean isDefineHorario() {
		if (isTurmaEad()){
			boolean defineHorarioEad = ParametroHelper.getInstance().getParametroBoolean( ParametrosGraduacao.DEFINE_HORARIO_TURMA_EAD );
			return defineHorarioEad;
		} else
			return obj.getDisciplina().isExigeHorarioEmTurmas();
	}

	/** Retorna uma cole��o de selecItem de p�los para o cadastro de turma para o EAD.
	 * @return the polos
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPolos() throws DAOException {
		if (polos == null) {
			PoloDao dao = getDAO(PoloDao.class);
			polos = toSelectItems(dao.findPolosByNivel(getNivelEnsino()), "id", "descricao");
		}
		return polos;
	}

	/** Seta a sele��o de p�lo EAD referente � turma. 
	 * @param 
	 */
	public void setPolos(Collection<SelectItem> polos) {
		this.polos = polos;
	}
	
	/** Retorna a lista de escolhas de p�los pelo usu�rio.  
	 * @return
	 */
	public Integer[] getPolosEscolhidos() {
		return polosEscolhidos;
	}

	/** Seta a lista de escolhas de p�los pelo usu�rio.
	 * @param polosEscolhidos
	 */
	public void setPolosEscolhidos(Integer[] polosEscolhidos) {
		this.polosEscolhidos = polosEscolhidos;
	}
	
	/**
	 * Carrega o calend�rio correto para realizar an�lise das solicita��es
	 * @throws DAOException
	 */
	protected CalendarioAcademico getCalendario() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();
		if (getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().isProbasica()) {
			cal = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao());
		}
		if (cal == null) {
			addMensagemErro("Erro ao carregar calend�rio acad�mico no ano-per�odo informado: " + obj.getAnoPeriodo());
			return null;
		} else {
			return cal;
		}
	}

	@Override
	public void validaDocentesTurma(ListaMensagens erros) throws DAOException {
	}

	@Override
	public String formSelecaoComponente() {
		if ((isPortalDocente()|| isPortalCoordenadorGraduacao()) 
				&& obj.getId() == 0 
				&& (getAcessoMenu().isChefeDepartamento() || isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO))) {
			return forward(JSP_COMPONENTES_SEM_SOLICITACAO);
		}
		try {
			ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
			
			Unidade unidade = null;
			if (isUserInRole(SigaaPapeis.SEDIS, SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP)) {
				unidade = null;
			} else  if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) && 
					(isPortalDocente() || isPortalCoordenadorGraduacao())) {
				if(getCursoAtualCoordenacao()!=null)				
					unidade = getCursoAtualCoordenacao().getUnidadeCoordenacao();				
			} else  if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO)) 
				unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			
			return mBean.buscarComponente(this, "Cadastro de Turmas", unidade, false,false, null);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}
	
	/** Retorna o n�vel de ensino em uso no subsistema atual.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getNivelEnsino()
	 */
	@Override
	public char getNivelEnsino() {
		return NivelEnsino.GRADUACAO;
	}

	@Override
	public String formConfirmacao() {
		return forward(JSP_RESUMO);
	}

	@Override
	public boolean isDefineDocentes() {
		// se a turma for de m�dulo, n�o define docente
		if (obj.getDisciplina().isBloco())
			return false;
		else
			return true;
	}

	/**
	 * Inicia o cadastro de turmas EAD.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastroTurmaEad() throws ArqException {
		clear();
		setTurmaEad(true);
		obj.setDistancia(true);
		obj.setSituacaoTurma( new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE) );
		obj.setPolo(new Polo());
		obj.setAno(getCalendario().getAno());
		obj.setPeriodo(getCalendario().getPeriodo());

		if( obj.isTurmaFerias() ){
			obj.setDataInicio(getCalendario().getInicioFerias());
			obj.setDataFim( getCalendario().getFimFerias() );
		} else {
			obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
			obj.setDataFim( getCalendario().getFimPeriodoLetivo() );
		}

		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA);
		prepareMovimento(SigaaListaComando.ALTERAR_TURMA);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_TURMA.getId());
		
		return formSelecaoComponente();
	}
	
	/**
	 * Seleciona um componente da lista de componentes sem solicita��o e seta na
	 * turma a ser criada e redireciona para dados gerais.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/componentes_sem_solicitacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String selecionarComponenteSemSolicitacao() throws ArqException{
		int id = getParameterInt("id");
		ComponenteCurricular componente = getGenericDAO().findByPrimaryKey(id, ComponenteCurricular.class);
		componente.getDetalhes();
		return selecionaComponenteCurricular(componente);
	}
	
	/**
	 * Inicia o atendimento de uma solicita��o de turma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws RemoteException 
	 * @throws NegocioException 
	 */
	public String iniciarAtendimentoSolicitacao() throws ArqException, NegocioException, RemoteException {
		clear();
		setSolicitacao(null);
		outrasSolicitacoesFeriasAtendidas = new ArrayList<SolicitacaoTurma>();
		discentesSolicitacao = null;
		
		checkRole( SigaaPapeis.GESTOR_TURMAS_UNIDADE );
		ReservaCursoDao dao = getDAO(ReservaCursoDao.class);
		TurmaGraduacaoDao tgDao = getDAO(TurmaGraduacaoDao.class);

		int id = getParameterInt("id");
		setSolicitacao(dao.findByPrimaryKey(id, SolicitacaoTurma.class));
		if (getSolicitacao() == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		
		// seta o calendario vigente para fazer a valida��o da solicita��o
		getSolicitacao().setCalendario(getCalendario());
		getSolicitacao().getComponenteCurricular().getDescricao();		
		
		obj = getSolicitacao().toTurma();
		

		if (getSolicitacao() != null && getSolicitacao().isGeraTurmaRegular()){
			obj.setTipo(Turma.REGULAR);
		}

		
		ListaMensagens lista = validarSelecaoComponenteCurricular(obj.getDisciplina());
		if (lista.isErrorPresent()) {
			addMensagens(lista);
			return null;
		}
		
		// Pega o calend�rio do mesmo ano e per�odo da turma que est� sendo criado.
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(obj);

		if( cal == null ){
			addMensagemErro("N�o � poss�vel realizar o cadastro de turmas pois o calend�rio acad�mico de " + obj.getAnoPeriodo() 
					+ " n�o est� definido, por favor, entre em contato com o "+RepositorioDadosInstitucionais.get("siglaUnidadeGestoraGraduacao")+".");
			return null;
		}		
		
		ListaMensagens listaMsg = getSolicitacao().validarAtendimentoSolicitacao();
		if( getSolicitacao().isTurmaRegular() && isEmpty(getSolicitacao().getComponenteCurricular().getPrograma()) )
			listaMsg.addErro("N�o � poss�vel criar a turma deste componente pois n�o est� cadastrado o programa deste componente para este semestre.");
		
		if (listaMsg.isErrorPresent()) {
			addMensagens(listaMsg);
			return null;
		}


		if( obj.isTurmaFerias() ){
			obj.setDataInicio(cal.getInicioFerias());
			obj.setDataFim(cal.getFimFerias() );
		} else {
			obj.setDataInicio(cal.getInicioPeriodoLetivo() );
			obj.setDataFim(cal.getFimPeriodoLetivo() );
		}

		obj.setReservas( getDAO(ReservaCursoDao.class).findBySolicitacao(getSolicitacao().getId()));
		Collections.sort((List<ReservaCurso>) obj.getReservas()) ;
		for (Iterator<ReservaCurso> it = obj.getReservas().iterator(); it.hasNext();) {
			ReservaCurso rc = it.next();
			rc.getSolicitacao().getRegistroEntrada().toString();
			if( rc.getVagasReservadas() > 0 ) // Reserva j� foi atendida
				it.remove();
			rc.setPodeRemover(true);
			rc.setVagasReservadas( rc.getVagasSolicitadas() != null ? rc.getVagasSolicitadas() : 0 );
			rc.setVagasReservadasIngressantes( rc.getVagasSolicitadasIngressantes() != null ? rc.getVagasSolicitadasIngressantes() : 0 );
			rc.setTurma(obj);
		}

		// Setando vagas da turma
		if( obj.isTurmaEnsinoIndividual()  ){

			if( isEmpty( getSolicitacao().getDiscentes() ) || getSolicitacao().getDiscentes().size() == 0 ){
				addMensagemErro("N�o h� nenhum discente nesta solicita��o de turma de " + obj.getTipoString());
			}
			
			List<Turma> turmasEnsinoIndividual = new ArrayList<Turma>();  
			turmasEnsinoIndividual = tgDao.findByComponenteTipoAnoPeriodo(obj.getDisciplina().getId(), Turma.ENSINO_INDIVIDUAL, obj.getAno(), obj.getPeriodo());
			
			if (!turmasEnsinoIndividual.isEmpty()){
				Turma turmaIndividual = turmasEnsinoIndividual.iterator().next();
				TurmaDao tDao = getDAO(TurmaDao.class);
				int qtdAlunoMatriculado = tDao.findTotalMatriculados(turmaIndividual.getId());
				int qtdMaxAlunoTurmaIndividual = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL);
				if ( (qtdAlunoMatriculado + getSolicitacao().getDiscentes().size()) <= qtdMaxAlunoTurmaIndividual ){
					turmaIndividual.setCapacidadeAluno( qtdAlunoMatriculado + getSolicitacao().getDiscentes().size() );
					obj = turmaIndividual;
					return forward(JSP_CONFIRMA_MATRICULA_ENSINO_INDIVIDUAL);
				} else {
					addMensagemErro("N�o foi poss�vel realizar a opera��o.<br/>" +
						"� permitido a cria��o de uma �nica turma de ensino individualizado por componente e semestre.");
				}	
			}
			
			obj.setCapacidadeAluno( getSolicitacao().getDiscentes().size() );
			
			if (hasErrors()) return null;
			
		}else if( obj.isTurmaFerias() ){
			
			boolean podeCadastrarSemSolcitacao = 
					ParametroHelper.getInstance().getParametroBoolean( ParametrosGraduacao.DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO );

			if (!podeCadastrarSemSolcitacao) {
				if( isEmpty( getSolicitacao().getDiscentes() ) || getSolicitacao().getDiscentes().size() == 0 ){
					addMensagemErro("N�o h� nenhum discente nesta solicita��o de turma de " + obj.getTipoString());
					return null;
				}
			}

		}else{
			int contVagas = 0;
			for( ReservaCurso rc : obj.getReservas() ){
				contVagas += rc.getVagasReservadas() + rc.getVagasReservadasIngressantes();
			}
			obj.setCapacidadeAluno( contVagas );
		}

		// Popular a turma a ser criada com uma reserva
		if( obj.isTurmaFerias() ){

			if( hasErrors() )
				return null;

			ReservaCurso reserva = new ReservaCurso();
			reserva.setVagasSolicitadas( (short) ( getSolicitacao().getVagas() > 0 ? getSolicitacao().getVagas() : getSolicitacao().getDiscentes().size() ) );
			reserva.setVagasReservadas( reserva.getVagasSolicitadas() );
			reserva.setIdSolicitacao( getSolicitacao().getId() );
			reserva.setSolicitacao(getSolicitacao());
			reserva.getSolicitacao().setRegistroEntrada(dao.refresh(getSolicitacao().getRegistroEntrada()));
			reserva.setPodeRemover(false);
			
			if ( isEmpty( getSolicitacao().getDiscentes() ) ) {
				Curso c = getSolicitacao().getCurso(); 
				reserva.setCurso(c);
			} else {
				MatrizCurricular matriz = getSolicitacao().getDiscentes().iterator().next().getDiscenteGraduacao().getMatrizCurricular();
				reserva.setMatrizCurricular(matriz);
			}

			obj.setReservas( new ArrayList<ReservaCurso>() );
			obj.addReservaCurso(reserva);

		}		
		// Seta os hor�rios da solicita��o
		HorarioDao daoH = getDAO(HorarioDao.class);
		List<HorarioTurma> horarios = HorarioTurmaUtil.parseCodigoHorarios(getSolicitacao().getHorario(),
				getSolicitacao().getUnidade().getId(), NivelEnsino.GRADUACAO, daoH);
		obj.setSolicitacao(getSolicitacao());
		obj.setHorarios(horarios);
		obj.setDescricaoHorario( HorarioTurmaUtil.formatarCodigoHorarios(obj) );

		// Carregando sub-turmas
		carregarSubturmas();

		setAdicionarOutrasReservas(getCalendario().isPeriodoCadastroTurmas() || getCalendario().isPeriodoAjustesTurmas());
		
		// Carregando outras solicita��es pendentes para o mesmo componente
		carregarOutrasSolicitacoes();

		// Retirando as reservas que j� foram adicionadas.
		removerReservasDoSelect();
		operacaoTurma = ATENDER_SOLICITACAO_TURMA;
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_TURMA.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA);
		setConfirmButton("Atender Solicita��o");
		return formDadosGerais();
	}
	
	/**
	 * Carrega a lista outrasSolicitacoes com todas as solicita��es que podem
	 * ser adicionadas nesta mesma turma ou seja, que possuem a mesma componente
	 * e mesmo hor�rio.
	 * 
	 * @throws DAOException
	 */
	private void carregarOutrasSolicitacoes() throws DAOException{

		if( obj.getDescricaoHorario() == null )
			return; // A turma que esta sendo atendida est� sem hor�rio, logo n�o d� pra carregar outras solicita��es compat�veis

		// Carregando outras solicita��es pendentes para o mesmo componente 
		SolicitacaoTurmaDao daoSolicitacao = getDAO(SolicitacaoTurmaDao.class);
		List<Integer> idsComponentes = new ArrayList<Integer>();
		idsComponentes.add(obj.getDisciplina().getId());
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(obj.getDisciplina().getEquivalencia());
		if( arvore != null ){
			Integer[] componentes = arvore.componentesIsolados();
			idsComponentes.addAll( Arrays.asList( componentes ) );
		}
		outrasSolicitacoes = daoSolicitacao.findPendentesByComponente( idsComponentes, obj.getDescricaoHorario().trim(), obj.getAno(), obj.getPeriodo() );

		for (Iterator<SolicitacaoTurma> it = outrasSolicitacoes.iterator(); it.hasNext();) {
			SolicitacaoTurma s = it.next();
			s.getRegistroEntrada().toString();
			for (Iterator<ReservaCurso> itReserva = s.getReservas().iterator(); itReserva.hasNext();) {
				ReservaCurso rc = itReserva.next();
				if( rc.getDataAtendimento() == null ) {// Se reserva n�o tiver sido atendida seta vagas reservadas como vagas solicitadas por padr�o
					rc.setVagasReservadas( rc.getVagasSolicitadas() != null ?  rc.getVagasSolicitadas() : 0 );
					rc.setVagasReservadasIngressantes( rc.getVagasSolicitadasIngressantes() != null ?  rc.getVagasSolicitadasIngressantes() : 0 );
				} else // Sen�o remove a reserva das poss�veis reservas a ser adicionadas
					itReserva.remove();
			}

		}
	}
	
	/**
	 * Inicia o atendimento de solicita��o de turma buscando outras turmas j�
	 * criadas do mesmo componente/hor�rio para adicionar a nova reserva.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String atenderReservaTurmaExistente() throws SegurancaException, DAOException{
		checkRole( SigaaPapeis.GESTOR_TURMAS_UNIDADE );
		clear();
		ReservaCursoDao dao = getDAO(ReservaCursoDao.class);

		int id = getParameterInt("id");
		setSolicitacao(dao.findByPrimaryKey(id, SolicitacaoTurma.class));
		
		// Verificando se esta no per�odo de cadastramento de turma regular 
		if( getSolicitacao().isTurmaRegular() && !getCalendario().isPeriodoCadastroTurmas() ){
			addMensagemErro("N�o est� no per�odo de cadastro de turmas");
			return null;
		}
		
		// Verificando se esta no per�odo de cadastramento de turma de f�rias
		if(  getSolicitacao().isTurmaFerias() && !getCalendario().isPeriodoCadastroTurmasFerias() ){
			addMensagemErro("N�o est� no per�odo de cadastro de turmas de f�rias");
			return null;
		}

		//Verificando se o componente desta solicita��o possui programa cadastrado para o semestre da solicita��o
		ComponenteCurricularPrograma programa = getSolicitacao().getComponenteCurricular().getPrograma();
		if( getSolicitacao().isTurmaRegular() && ( isEmpty(programa) || programa.getAno() != getSolicitacao().getAno() || programa.getPeriodo() != getSolicitacao().getPeriodo() ) ){
			addMensagemErro("N�o � poss�vel criar a turma deste componente pois n�o esta cadastrado o programa deste componente para este semestre");
			return null;
		}

		if (isEmpty(getSolicitacao().getReservas())) {
			addMensagemErro("Esta solicita��o n�o possui reservas");
			return null;
		}
		
		if (getSolicitacao().isTurmaEnsinoIndividual()) {
			addMensagemErro("Opera��o n�o permitida para turmas de ensino individual");
			return null;
		}
		
		if( getSolicitacao().getHorario() != null ){
			TurmaDao daoTurma = getDAO(TurmaDao.class);
			outrasTurmas = daoTurma.findByComponenteHorarioAnoPeriodo( getSolicitacao().getComponenteCurricular().getId(), getSolicitacao().getHorario().trim(), getSolicitacao().getAno(), getSolicitacao().getPeriodo() );
		}

		if( isEmpty(outrasTurmas) ){
			addMensagemErro("Neste semestre n�o h� nenhuma turma criada deste componente neste mesmo hor�rio");
			return null;
		}

		return forward(JSP_OUTRAS_TURMAS);
	}

	/**
	 * Remove as solicita��es que j� foram atendidas na reserva da lista de
	 * solicita��es dispon�veis para serem adicionadas.
	 * 
	 */
	private void removerReservasDoSelect(){

		for( ReservaCurso r : obj.getReservas() ){
			outrasSolicitacoes.remove(r.getSolicitacao());
		}

	}
	
	/** Indica se o usu�rio pode selecionar o curso para a reserva de vagas.
	 * @return
	 */
	public boolean isSelecionarCurso() {
		return (isParaConvenio() && getSolicitacao() == null) || (getCursosCombo() != null && !getCursosCombo().isEmpty());
	}
	/**
	 * Adiciona outra reserva na cria��o da turma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String adicionarReserva() throws DAOException {

		// Validando capacidade da turma
		if (obj.getCapacidadeAluno() == null) {
			addMensagemErro("Por favor, primeiro informe a capacidade de alunos dessa turma. "
					+ "As reservas n�o podem ultrapassar esse valor");
			return redirectMesmaPagina();
		}

		GenericDAO dao = getGenericDAO();

		int qtdVagas = getParameterInt( "qtdVagas" );
		int idReserva = getParameterInt( "idReservaCurso" );

		if( !obj.isTurmaFerias() && qtdVagas <= 0 ){
			addMensagemErro("A quantidade de vagas a ser reservada deve ser superior a 0 (zero).");
			return redirectMesmaPagina();
		}

		ReservaCurso reserva = dao.findByPrimaryKey(idReserva, ReservaCurso.class);
		reserva.getSolicitante();
		
		for (ReservaCurso r : obj.getReservas()) {
			if( r.getId() == idReserva ){
				addMensagemErro("Esta reserva j� foi adicionada a turma.");
				return redirectMesmaPagina();
			}
		}

		reserva.setVagasReservadas((short) qtdVagas);
		reserva.setPodeRemover(true);

		if (obj.addReservaCurso(reserva))
			addMessage("Reserva adicionada com sucesso.", TipoMensagemUFRN.INFORMATION);
		else
			addMessage("Reserva n�o pode ser adicionada.", TipoMensagemUFRN.ERROR);


		// Setando reserva como atendida para ocultar o bot�o de adicionar reserva da JSP
		for( SolicitacaoTurma sol : getOutrasSolicitacoes() ){
			for( ReservaCurso r : sol.getReservas() )
				if( r.getId() == reserva.getId() )
					r.setTurma(obj);
		}

		// Ajusta a quantidade de vagas de acordo com as vagas da reserva
		if (obj.getCapacidadeAluno() < obj.getTotalVagasReservadas())
			obj.setCapacidadeAluno(obj.getTotalVagasReservadas());
		
		return redirectMesmaPagina();
	}
	
	/** Popula a solicita��o com os discentes que a solicitaram. 
	 * @return
	 * @throws DAOException 
	 */
	private void popularDiscentesSolicitacao() throws DAOException  {
		SolicitacaoTurma solicitacao = getGenericDAO().refresh(getSolicitacao());
		discentesSolicitacao = new ArrayList<DiscentesSolicitacao>(); 
		discentesSolicitacao.addAll( solicitacao.getDiscentes() );

		if( obj.isTurmaFerias() && !isEmpty( outrasSolicitacoesFeriasAtendidas ) ){
			for( SolicitacaoTurma sol : outrasSolicitacoesFeriasAtendidas ) {
				discentesSolicitacao.addAll( sol.getDiscentes() );
			}
		}
	}
	
	/**
	 * Remove uma reserva de curso da lista de reservas realizadas.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void removerReservaCurso(ActionEvent evt) throws DAOException {
		int id = getParameterInt("idMatriz");
		ReservaCurso reserva = new ReservaCurso(id);

		int idSolicitacao = 0;
		if( id == 0 ){
			idSolicitacao = getParameterInt("idSolicitacao");
		}

		boolean removeu = false;
		if( obj.isTurmaFerias() ){
			// Caso de turma de f�rias
			for( ReservaCurso r : obj.getReservas() ){
				if( r.getIdSolicitacao() == idSolicitacao ){
					r.setVagasReservadas( (short)0 );
					r.setTurma(null);
					removeu = obj.getReservas().remove(r);
					break;
				}
			}

			for (Iterator<SolicitacaoTurma> it = outrasSolicitacoesFeriasAtendidas.iterator(); it.hasNext();) {
				SolicitacaoTurma sol = it.next();
				if( sol.getId() == idSolicitacao )
					it.remove();
				popularDiscentesSolicitacao();
			}

		}else{
			// Caso turma regular 
			for( ReservaCurso r : obj.getReservas() ){
				if( r.getId() == reserva.getId() ){
					r.setVagasReservadas( (short)0 );
					r.setTurma(null);
					r.setPodeRemover(true);
					removeu = obj.getReservas().remove(r);
					break;
				}
			}

			for( SolicitacaoTurma st : outrasSolicitacoes ){
				for( ReservaCurso rc: st.getReservas() ){
					if( rc.getId() == reserva.getId() ) {
						rc.setTurma(null);
						rc.setPodeRemover(true);
					}
				}
			}

		}
		
		// Setando reserva como atendida para ocultar o bot�o de adicionar reserva da JSP
		for( SolicitacaoTurma sol : getOutrasSolicitacoes() ){
			for( ReservaCurso r : sol.getReservas() )
				if( r.getId() == reserva.getId() )
					r.setTurma(null);
		}

		if (removeu){
			addMensagemWarning("Reserva removida com sucesso.");
		}
		else
			addMensagemErro("Reserva n�o pode ser removida.");
	}
	
	/** Indica se a cria��o da turma � em atendimento � uma solicita��o.
	 * @return
	 */
	public boolean isAtendimentoSolicitacao() {
		return (getSolicitacao() != null);
	}
	
	/** Retorna a cole��o de outras solicita��es de turmas que foram atendidas pela cria��o desta turma.
	 * @return
	 */
	public Collection<SolicitacaoTurma> getOutrasSolicitacoes() {
		return outrasSolicitacoes;
	}

	/** Seta a cole��o de outras solicita��es de turmas que foram atendidas pela cria��o desta turma.
	 * @param outrasSolicitacoes
	 */
	public void setOutrasSolicitacoes(
			List<SolicitacaoTurma> outrasSolicitacoes) {
		this.outrasSolicitacoes = outrasSolicitacoes;
	}

	/** Retorna uma cole��o de selectItem de solicita��es de turmas que foram atendidas pela cria��o desta turma.
	 * @return
	 */
	public Collection<SelectItem> getOutrasSolicitacoesCombo() {
		return toSelectItems(outrasSolicitacoes, "id", "descricao");
	}

	/**
	 * Caso seja solicita��o de ensino individual, retorna os discentes da solicita��o de ensino individual.
	 * Caso seja solicita��o de turma de f�rias, retorna todos os discentes das solicita��es que est�o adicionadas nesta solicita��o de turma. 
	 * @return
	 * @throws DAOException 
	 */
	public List<DiscentesSolicitacao> getDiscentes() throws DAOException {
		if (discentesSolicitacao == null && getSolicitacao() != null) {
			popularDiscentesSolicitacao();
			Collections.sort(discentesSolicitacao);
		}
		return discentesSolicitacao;
	}
	
	/** Retorna a cole��o de outras turmas criadas do componente mesmo hor�rio
	 * @return
	 */
	public Collection<Turma> getOutrasTurmas() {
		return outrasTurmas;
	}

	/** Seta a cole��o de outras turmas criadas do componente mesmo hor�rio
	 * @param outrasTurmas
	 */
	public void setOutrasTurmas(Collection<Turma> outrasTurmas) {
		this.outrasTurmas = outrasTurmas;
	}
	
	public List<SolicitacaoTurma> getOutrasSolicitacoesFeriasAtendidas() {
		return outrasSolicitacoesFeriasAtendidas;
	}

	/**
	 * Seta a lista de outras solicita��es de turmas. No caso de solicita��o de
	 * turma de f�rias, o chefe tamb�m pode juntar solicita��es. deseja juntar
	 * 
	 * @param outrasSolicitacoesFeriasAtendidas
	 */
	public void setOutrasSolicitacoesFeriasAtendidas(
			List<SolicitacaoTurma> outrasSolicitacoesFeriasAtendidas) {
		this.outrasSolicitacoesFeriasAtendidas = outrasSolicitacoesFeriasAtendidas;
	}

	public List<DiscentesSolicitacao> getDiscentesSolicitacao() {
		return discentesSolicitacao;
	}

	public void setDiscentesSolicitacao(
			List<DiscentesSolicitacao> discentesSolicitacao) {
		this.discentesSolicitacao = discentesSolicitacao;
	}

	@Override
	protected void initObj() {
		super.initObj();
		componentesSemSolicitacao = null;
		discentesSolicitacao = null;
		outrasSolicitacoes = null;
		outrasSolicitacoesFeriasAtendidas = null;
		outrasTurmas = null;
		polos = null;
		polosEscolhidos = null;
		possiveisMatrizes = null;
		quantidadeSubturmas = 0;
		turmaProbasica = false;
	}
	
	@Override
	public void beforeSelecionarComponente() throws DAOException {
		obj.setAno(getCalendario().getAno());
		obj.setPeriodo(getCalendario().getPeriodo());
		obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
		obj.setDataFim( getCalendario().getFimPeriodoLetivo() );
		obj.setSituacaoTurma( new SituacaoTurma( SituacaoTurma.A_DEFINIR_DOCENTE ) );
		if(isPortalCoordenadorGraduacao() && getCursoAtualCoordenacao()!= null && getCursoAtualCoordenacao().isADistancia() ) {
			setTurmaEad(true);
			obj.setDistancia(true);
		}
		// verifica se existe componente que pode ter turma criada sem solicita��o, caso o usu�rio seja chefe de departamento
		try {
			if (isPortalDocente() && getAcessoMenu().isChefeDepartamento()
					|| isPortalCoordenadorGraduacao() && getAcessoMenu().isCoordenadorCursoGrad()) {
				if (ValidatorUtil.isEmpty(getComponentesSemSolicitacao())){
					addMensagemErro("N�o h� componentes curriculares que possam ser criados sem solicita��o de turmas.");
				}
			}
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	/**
	 * Carrega as matrizes curriculares do curso selecionado na p�gina de adi��o
	 * de reserva em turmas.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/form_reservas.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaMatrizesCurriculares(ValueChangeEvent evt) throws DAOException {
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		idCursoReserva = (Integer) evt.getNewValue();
		possiveisMatrizes = toSelectItems( dao.findAtivasByCurso(idCursoReserva), "id", "descricao");
	}
	
	/** Retorna a lista de selectItens de matrizes curriculares que podem ter reservas na turma.
	 * @return
	 */
	public List<SelectItem> getPossiveisMatrizes() {
		// evita NullPointerException na view:
		if (possiveisMatrizes == null)
			possiveisMatrizes = new ArrayList<SelectItem>();
		return possiveisMatrizes;
	}

	/** Seta a lista de selectItens de matrizes curriculares que podem ter reservas na turma.
	 * @param possiveisMatrizes
	 */
	public void setPossiveisMatrizes(List<SelectItem> possiveisMatrizes) {
		this.possiveisMatrizes = possiveisMatrizes;
	}

	@Override
	public void beforeConfirmacao() throws ArqException {
	}

	@Override
	public void beforeDadosGerais() throws ArqException {
		
			obj.setCapacidadeAluno(null);
			obj.setCampus(new CampusIes());
			obj.setHorarios(new ArrayList<HorarioTurma>());
			obj.setTipo(null);
			obj.setLocal(null);
			
			if( isUserInRole( SigaaPapeis.PPG ) ){
				obj.setAno(CalendarUtils.getAnoAtual());
				obj.setPeriodo(getPeriodoAtual());
			}else{
				obj.setAno(getCalendario().getAno());
				obj.setPeriodo(getCalendario().getPeriodo());
			}
			
			obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
			obj.setDataFim( getCalendario().getFimPeriodoLetivo() );

	}

	@Override
	public void beforeDefinirDocentesTurma() throws ArqException {
	}

	@Override
	public void validaHorariosTurma(ListaMensagens erros) throws DAOException {
	}

	@Override
	public void beforeAtualizarTurma() throws DAOException {
		
		if (isTurmaEad()) {
			if (obj.getPolo() == null)
				obj.setPolo(new Polo());
		}
		
		setReserva(new ReservaCurso());
		if( obj.getDisciplina().isGraduacao() && obj.getCampus() == null )
			obj.setCampus(new CampusIes());
		
		
		discentesSolicitacao = null;
		if( !obj.isAberta() && !isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG ) ){
			addMensagemErro("N�o � poss�vel alterar turmas que n�o est�o abertas.");
			obj = new Turma();
			return;
		} else if( !obj.isAberta() && isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG ) ){
			addMensagemWarning("Aten��o! Esta turma j� foi consolidada.");
		}

		if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO)) {
			setAdicionarOutrasReservas(getCalendario().isPeriodoCadastroTurmas() || getCalendario().isPeriodoAjustesTurmas());
		} else if(isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.CDP)) {
			setAdicionarOutrasReservas(true);
		}
		
		if(isAdicionarOutrasReservas()){
			// Arrumando as reservas que j� foram atendidas para a exibi��o na JSP
			// as reservas atendidas n�o podem ser removidas da turma. 
			for (ReservaCurso rc : obj.getReservas()) {
				rc.setPodeRemover(false);
			}
			carregarOutrasSolicitacoes();
		}
		this.remover = false;
		polosEscolhidos = null;
		componentesSemSolicitacao = null;
		this.remover = false;
	}

	/** Indica se exibe os discentes solicitantes da solicita��o de matr�cula.
	 * @return
	 */
	public boolean isExibeDiscentesSolicitantes() {
		if ( getSolicitacao() != null && getSolicitacao().isTurmaEnsinoIndividual() )
			return true;
		else if ( getSolicitacao() != null && getSolicitacao().isTurmaFerias() && !isEmpty(getSolicitacao().getDiscentes()) )
			return true;
		return false;
	}
	
	@Override
	public String formConfirmacaoRemover() {
		this.remover = true;
		return forward(JSP_CONFIRMA_REMOCAO);
	}

	public boolean isRemover() {
		return remover;
	}
	
	/** Set a turma agrupadora da subturma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/dados_gerais.jsp</li>
	 * </ul>
	 * @param evt
	 */
	public  void setTurmaAgrupadora(ValueChangeEvent evt) {
		obj.getTurmaAgrupadora().setId((Integer) evt.getNewValue());
	}

	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException {
				
		ListaMensagens lista = new ListaMensagens();
		
		if ( !componente.isPermiteCriarTurma()) {
			lista.addErro("N�o � poss�vel criar uma turma do componente " + componente.getCodigo()+". Somente Componentes Curriculares do tipo Disciplina, M�dulo ou Atividades Coletivas podem ser utilizados para criar turma.");
		}	
		
		return lista;
	}

	public int getQuantidadeSubturmas() {
		return quantidadeSubturmas;
	}

	public void setQuantidadeSubturmas(int quantidadeSubturmas) {
		this.quantidadeSubturmas = quantidadeSubturmas;
	}

	public boolean isDefineQuantidadeSubturmas() {
		return obj.getDisciplina().isAceitaSubturma() && getSolicitacao()!= null && getSolicitacao().isTurmaFerias();
	}

	/** Indica se a turma ser� �nica para o curso EAD, ou ser� por p�lo.
	 * @return
	 */
	public boolean isTurmaUnicaCursoEad() {
		if (obj.getId() != 0) {
			if (!isEmpty(obj.getPolo())) 
				return false;
			else
				return true;
		} else {
			return turmaUnicaCursoEad;
		}
	}

	public void setTurmaUnicaCursoEad(boolean turmaUnicaCursoEad) {
		this.turmaUnicaCursoEad = turmaUnicaCursoEad;
	}

	@Override
	public String retornarSelecaoComponente() {
		// TODO redirecionar o usu�rio para o formul�rio que invocou a sele��o de componentes.
		return cancelar();
	}

	/** Duplica uma turma.<br />
	 * M�todo n�o chamado por JSPs.
	 * @return
	 * @throws ArqException
	 */
	public String duplicarTurma() throws ArqException {
		if (isEmpty(obj))
			populateObj(true);
		if (obj == null || obj.getSituacaoTurma().getId() == SituacaoTurma.EXCLUIDA) {
			obj = new Turma();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		
		// prepara os dados como se fosse alterar a turma
		String retorno = atualizar();
		
		// clona a turma
		Turma clone = null;
		try {
			clone = (Turma) BeanUtils.cloneBean(obj);
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
			return null;
		}
		// zera os IDs dos objetos que dever�o ser persistidos, e n�o atualizados.
		// turma
		clone.setId(0);
		// hor�rios da turma
		if (clone.getHorarios() != null)
			for (HorarioTurma ht : clone.getHorarios())
				ht.setId(0);
		// docentes
		if (clone.getDocentesTurmas() != null)
			clone.getDocentesTurmas().clear();
		// reserva de vagas
		if (clone.getReservas() != null)
			for (ReservaCurso reserva : clone.getReservas())
				reserva.setId(0);
		clone.setObservacao("Turma clonada a partir da turma " + obj.getAnoPeriodo() + " - " + obj.getDisciplina().getCodigo() + " T" + obj.getCodigo());
		
		Collection<TurmaSolicitacaoTurma> turmasSolicitacaoTurmas = clone.getTurmasSolicitacaoTurmas();
		
		if (isNotEmpty(turmasSolicitacaoTurmas)) 
			setSolicitacao(turmasSolicitacaoTurmas.iterator().next().getSolicitacao());
				
		
		
		obj = clone;
		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA);
		setConfirmButton("Cadastrar");
		return retorno;
	}
	
	/**
	 * Retorna todos os cursos de gradua��o, podendo ser de ensino a dist�ncia caso a turma seja de EAD, em formato de combo
	 * <br/><br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul> 
	 *  <li>/sigaa.war/graduacao/turma/form_reservas.jsp</li>
	 * </ul> 
	 * </ul> 
	 **/
	public Collection<SelectItem> getCursosGraduacaoCombo() throws DAOException {
		CursoMBean mbean = getMBean("curso");
		if (obj.isEad())
			return mbean.getAllCursosGraduacaoEADCombo();
		else
			return mbean.getAllCursosGraduacaoPresenciaisCombo();
	}

	@Override
	public String definePeriodosTurma(Date inicio, Date fim)
			throws ArqException {
		
		obj.setDataInicio(inicio);
		obj.setDataFim(fim);
		
		return null;
	}
	

	/**
	 * M�todo utilizado para realizar o cadastro das matriculas dos discentes solicitantes de ensino individualizado.
	 *  * M�todo Chamado pelas seguintes JSPs:
	 * <ul> 
	 *  <li>/sigaa.war/graduacao/turma/resumo_matricula_ensino_individual.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	public String cadastrarMatriculasEnsinoIndividual() throws ArqException, NegocioException, RemoteException{
		
		prepareMovimento(SigaaListaComando.MATRICULAR_ALUNO_TURMA_ENSINO_INDIVIDUAL);
		
		Comando cmd = SigaaListaComando.MATRICULAR_ALUNO_TURMA_ENSINO_INDIVIDUAL;
		TurmaMov mov = new TurmaMov();			
		
		mov.setCodMovimento(cmd);
		mov.setTurma(obj);
		mov.setQuantidadeSubturmas(quantidadeSubturmas);
		mov.setSolicitacaoEnsinoIndividualOuFerias( getSolicitacao() );
	
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}

		super.afterCadastrar();
		addMessage("Turma " + obj.getDescricaoSemDocente() + " alterada com sucesso!", TipoMensagemUFRN.INFORMATION);
		
		verificaAlteracaoCodigoTurma();
		if( isAtendimentoSolicitacao() ){
			AnaliseSolicitacaoTurmaMBean analiseMBean = (AnaliseSolicitacaoTurmaMBean) getMBean("analiseSolicitacaoTurma");
			return analiseMBean.retornaAtencimendoSolicitacao();
		}
			
		return cancelar();
	}

	public int getIdCursoReserva() {
		return idCursoReserva;
	}

	public void setIdCursoReserva(int idCursoReserva) {
		this.idCursoReserva = idCursoReserva;
	}

	public int getOperacaoTurma() {
		return operacaoTurma;
	}

	public void setOperacaoTurma(int operacaoTurma) {
		this.operacaoTurma = operacaoTurma;
	}
}
