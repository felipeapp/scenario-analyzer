/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 31/01/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscentesSolicitacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoTurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.GrupoHorarios;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.jsf.HorarioTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.OperadorHorarioTurma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Controller respons�vel por opera��es de solicita��o de turma.
 * @author Leonardo
 * @author Victor Hugo
 */
@Component("solicitacaoTurma") @Scope("session")
public class SolicitacaoTurmaMBean extends SigaaAbstractController<SolicitacaoTurma> implements SeletorComponenteCurricular, OperadorHorarioTurma {

	// Defini��es das Views
	/** Define o link para o formul�rio de solicita��es de turmas de ensino individual. */
	public static final String JSP_SOLICITACAO_ENSINO_INDIVIDUAL = "/graduacao/solicitacao_turma/solicitacoes_ensino_individual.jsp";
	/** Define o link para o formul�rio de solicita��o de dados da turma. */
	public static final String JSP_SOLICITACAO_DADOS = "/graduacao/solicitacao_turma/dados_solicitacao.jsp";
	/** Define o link para o formul�rio de resumo dos dados da turma. */
	public static final String JSP_SOLICITACAO_RESUMO = "/graduacao/solicitacao_turma/resumo.jsp";
	/** Define o link para o formul�rio de listagem de solicita��es de turmas. */
	public static final String JSP_LIST_PAGE = "/graduacao/solicitacao_turma/lista.jsp";

	/** Cole��o de subunidades da turma. */
	private Collection<ComponenteCurricular> subunidades = new HashSet<ComponenteCurricular>();
	/** Cole��o de componentes curriculares retornados pela busca de solicita��es de turmas. */
	private Collection<ComponenteCurricular> componentes = new HashSet<ComponenteCurricular>();
	/** Cole��o de matrizes curriculares para o qual a turma ter� reserva de vagas. */
	private Collection<MatrizCurricular> matrizes = new ArrayList<MatrizCurricular>();
	/** Cole��o de solicita��es de turmas dos alunos para o coordenador. */
	private Collection<SolicitacaoTurma> solicitacoes = new ArrayList<SolicitacaoTurma>();
	/** Cole��o de solicita��es de turmas de ensino individual dos alunos para o coordenador. */
	private List<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividual = new ArrayList<SolicitacaoEnsinoIndividual>();
	/** Cole��o de solicita��es de turmas dos alunos para o coordenador que foram atendidas. */
	private Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividualAtendidas = new ArrayList<SolicitacaoEnsinoIndividual>();
	/** Discente a ser adicionado � turma. */
	private DiscenteGraduacao discente = new DiscenteGraduacao();
	/** Cole��o de hor�rios da turma. */
	private List<Horario> horariosGrade;
	/** Cole��o de hor�rios marcados para a turma.*/
	private String[] horariosMarcados;
	/** variavel informa se o procedimento em execu��o � uma altera��o de solicita��o. */
	private boolean alterarSolicitacao = false;
			
	/** Data de fim do hor�rio quando um componente permite flexibilidade de hor�rio */
	private Date periodoFim;
	/** Data de in�cio do hor�rio quando um componente permite flexibilidade de hor�rio */
	private Date periodoInicio;
	
	/** Ano da solicita��o de turma, utilizado para filtrar as solicita��es */
	private Integer ano;
	/** Per�odo da solicita��o de turma, utilizado para filtrar as solicita��es */
	private Integer periodo;

	// campos para auxilio na visualiza��o
	/** Indica se o usu�rio tem permiss�o para alterar o hor�rio da turma. */
	private boolean alterarHorario = false;

	/** DataModel usado na exibi��o da lista de {@link GrupoHorarios} */
	private DataModel modelGrupoHorarios = new ListDataModel();	
	/** Lista com os Hor�rios agrupados por per�odo */
	public List<GrupoHorarios> grupoHorarios = new ArrayList<GrupoHorarios>();
	
	/**
	 * Mapa de solicita��es de turmas pendentes para o componente
	 * <idComponente, Boolean>
	 */
	public Map<Integer, Boolean> mapaAtendimentos;
	
	/** Pseudo turma a ser criada. A turma � utilizada para capta��o do hor�rio a ser definido na solicita��o de turma. */
	public Turma turma;

	/** Define se o coordenador do curso pode solicitar turma de f�rias sem que os discentes tenham realizado a solicita��o.
	 * Caso FALSE o coordenador s� poder� solicitar turmas se algum discente tiver solicitado. */
	private Boolean permiteSolicitarTurmaSemSolicitacao = ParametroHelper.getInstance().getParametroBoolean( ParametrosGraduacao.DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO );
	
	/** Indica se est� solicitando turma direto sem considerar as solicita��es de discentes. */
	private Boolean modoDireto = null;
	
	/** Construtor padr�o. */
	public SolicitacaoTurmaMBean(){
		initObj();
	}

	/** Inicializa os atributos do controller. */
	private void initObj() {
		obj = new SolicitacaoTurma();
		obj.setUnidade(new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL));
		obj.setComponenteCurricular( new ComponenteCurricular() );
		obj.setDiscentes( new ArrayList<DiscentesSolicitacao>() );
		gerarGradeHorarios();
		setConfirmButton("Cadastrar");
		if (ano == null) ano = getCalendarioVigente().getAno();
		if (periodo == null) periodo = getCalendarioVigente().getPeriodo();
		modelGrupoHorarios = new ListDataModel();
		grupoHorarios = new ArrayList<GrupoHorarios>();
		turma = null;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
	}
	
	/** Retorna o link para o formul�rio de listagem de solicita��es de turmas. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return JSP_LIST_PAGE;
	}

	/** Retorna o link para o formul�rio de solicita��o de dados da turma. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return JSP_SOLICITACAO_DADOS;
	}
	
	/**
	 * 
	 * Redireciona para a p�gina de solicita��es.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/solicitacao_turma/view.jsp
	 * @return
	 */
	public String listaSolicitacoes() {
		return forward(getListPage());
	}

	/**
	 * Atualiza a solicita��o de turma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() {
		try {
			setOperacaoAtiva(SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA.getId());
			prepareMovimento(SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA);
			initObj();
			setId();
			setAlterarSolicitacao(true);
			MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
			this.obj = dao.findByPrimaryKey(obj.getId(), SolicitacaoTurma.class);
			this.obj.getDiscentes().iterator();

			matrizes = dao.findAtivasByCurso( this.obj.getCurso().getId() );

			HorarioDao daoH = getDAO(HorarioDao.class);
			List<HorarioTurma> horarios = HorarioTurmaUtil.parseCodigoHorarios(obj.getHorario(), getUnidadeGestora(), NivelEnsino.GRADUACAO, daoH);

			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(obj.toTurma());
			obj.setCalendario(cal);
			
			if (!obj.getComponenteCurricular().isPermiteHorarioFlexivel()) {
				horariosMarcados = HorarioTurmaUtil.parseHorarios( horarios, horariosGrade );
			} else {
				for (HorarioTurma ht : horarios) {
					if (ht.getDataInicio() == null) ht.setDataInicio(obj.getCalendario().getInicioPeriodoLetivo());
					if (ht.getDataFim() == null) ht.setDataFim(obj.getCalendario().getFimPeriodoLetivo());
				}
				grupoHorarios = HorarioTurmaUtil.agruparHorarioPorPeriodo(horarios);
				modelGrupoHorarios = new ListDataModel(grupoHorarios);
				obj.setHorarios(horarios);
			}
			if( obj.isTurmaEnsinoIndividual() || obj.isTurmaFerias() ){
				SolicitacaoEnsinoIndividualDao daoSol = getDAO(SolicitacaoEnsinoIndividualDao.class);
				solicitacoesEnsinoIndividual = daoSol.findByComponenteAnoPeriodoSituacao(getCursoAtualCoordenacao().getId(), obj.getComponenteCurricular().getId(), Integer.valueOf(obj.getAno()) , Integer.valueOf(obj.getPeriodo()), SolicitacaoEnsinoIndividual.SOLICITADA, SolicitacaoEnsinoIndividual.ATENDIDA);
				for (Iterator<SolicitacaoEnsinoIndividual> it = solicitacoesEnsinoIndividual.iterator(); it
						.hasNext();) {
					SolicitacaoEnsinoIndividual s = it.next();
					
					if( !isEmpty( s.getMatriculaGerada() ) && !s.getMatriculaGerada().isExcluida() ) // removendo as solicita��es deste componente que j� foram atendidas
						it.remove();
				}
			}

			if( obj.getSituacao() == SolicitacaoTurma.SOLICITADA_ALTERACAO)
				alterarHorario = true;
			
			if( obj.getReservas().isEmpty() ) {
				carregarReservas();
			}

			obj.setPermiteSolicitarTurmaSemSolicitacao( 
					ParametroHelper.getInstance().getParametroBoolean( ParametrosGraduacao.DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO ) );

			setConfirmButton("Alterar Solicita��o de Turma");
		} catch (ArqException e) {
			return tratamentoErroPadrao(e);
		}
		return forward(getFormPage());

	}

	/**
	 * Carrega os horarios com base na unidade gravada nos parametros da gestora da academica 
	 */
	private void gerarGradeHorarios(){
		try {
			HorarioDao dao = getDAO(HorarioDao.class);
			if (horariosGrade == null)
				horariosGrade = (List<Horario>) dao.findAtivoByUnidade(new Unidade(getUnidadeGestora()), NivelEnsino.GRADUACAO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
	}

	/**
	 * Inicia caso de uso de solicita��o de turma REGULAR.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarRegular() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG);
		initObj();
		if( getCalendarioVigente() == null ){
			addMensagemErroPadrao();
			return null;
		}
		if( !getCalendarioVigente().isPeriodoSolicitacaoTurma() ){
			addMensagemErro("N�o � permitido realizar solicita��o de abertura de turma fora do per�odo " +
					"determinado no calend�rio universit�rio" );
			return null;
		}

		if( getSubSistema().equals(SigaaSubsistemas.GRADUACAO) &&  getCursoAtualCoordenacao() == null ){
			addMensagemErroPadrao();
			return null;
		} else if( getSubSistema().equals(SigaaSubsistemas.STRICTO_SENSU) &&  getProgramaStricto() == null ){

		}

		// solicita��o de turma regular
		this.obj.setTipo( Turma.REGULAR );

		if( getCalendarioVigente().getAnoNovasTurmas() != null ){
			this.obj.setAno( getCalendarioVigente().getAnoNovasTurmas().shortValue() ) ;
		}else{
			this.obj.setAno( (short) getCalendarioVigente().getProximoAnoPeriodoRegular().getAno() );
		}

		if( getCalendarioVigente().getPeriodoNovasTurmas() != null ){
			this.obj.setPeriodo( getCalendarioVigente().getPeriodoNovasTurmas().byteValue() );
		} else{
			this.obj.setPeriodo( (byte) getCalendarioVigente().getProximoAnoPeriodoRegular().getPeriodo() );
		}

		if (getCursoAtualCoordenacao() != null) {
			this.obj.setCurso( getCursoAtualCoordenacao() );
		} else if (getProgramaStricto() != null) {

		}

		CalendarioAcademico prox = CalendarioAcademicoHelper.getProximoCalendario(getUnidadeGestora(), NivelEnsino.GRADUACAO, getCursoAtualCoordenacao().getConvenio());
		if(prox == null)
			throw new NegocioException("O calend�rio do pr�ximo per�odo regular ainda n�o se encontra definido.");
		periodoInicio = prox.getInicioPeriodoLetivo();
		obj.setCalendario(prox);
		
		setConfirmButton("Cadastrar Solicita��o de Turma");
		ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
		return mBean.buscarComponente(this, "Solicita��o de Turma", null,false, false, null);
	}

	/** Inicia caso de uso de solicita��o de turma de F�rias.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarSolicitacaoFeriasSemSolicitacao() throws ArqException{

		checkChangeRole();
		initObj();
		obj.setTipo( Turma.FERIAS );
		setModoDireto(Boolean.TRUE);
		
		verificaPeriodoFeriasDefinido();
		
		/** Validar se est� dentro do per�odo da coordena��o solicitar turma de f�rias ao departamento. */
		if ( !getCalendarioVigente().isPeriodoSolicitacaoTurmaFerias() ) {
			addMensagemErro( CalendarioAcademico.getDescricaoPeriodo("solicita��o de turma de f�rias", 
					getCalendarioVigente().getInicioSolicitacaoTurmaFerias(), 
					getCalendarioVigente().getFimSolicitacaoTurmaFerias()) );
		}
		
		if( hasErrors() )
			return null;
		
		// solicita��o de turma de F�rias
		obj.setTipo( Turma.FERIAS );
		obj.setPermiteSolicitarTurmaSemSolicitacao(permiteSolicitarTurmaSemSolicitacao);
		
		//Limpa a lista para a cria��o de uma nova solicita��o de Turma.
		solicitacoesEnsinoIndividual.clear();

		if (getCursoAtualCoordenacao() != null) {
			this.obj.setCurso( getCursoAtualCoordenacao() );
		}

		if ( getCalendarioVigente() != null ) {
			this.obj.setAno( getCalendarioVigente().getAnoFeriasVigente().shortValue() );
			this.obj.setPeriodo( getCalendarioVigente().getPeriodoFeriasVigente().byteValue() );
		}

		setConfirmButton("Cadastrar Solicita��o de Turma");
		ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
		return mBean.buscarComponente(this, "Solicita��o de Turma", null,false, false, null);
	}
	
	/**
	 * Esse m�todo serve para verificar se o docente pode informar a quantidade de vagas para uma determinada 
	 * turma de F�rias. 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isPossivelInformarQntVagasTurmaFerias() throws DAOException {
		if ( this.obj.isTurmaEnsinoIndividual() ) {
			return false;
		} else if ( this.obj.isTurmaFerias() ) {
			return this.obj.isPermiteSolicitarTurmaSemSolicitacao() && this.obj.isTurmaFerias();  
		} else {
			return true;
		}
	}
	
	/**
	 * Inicia a listagem de solicita��o de turmas do curso do discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarListaSolicitacoesCursoDiscente() throws ArqException{
		if (getUsuarioLogado().getVinculoAtivo().isVinculoDiscente()
				&& getUsuarioLogado().getVinculoAtivo().getDiscente().isRegular()) {
			solicitacoes = null;
			SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class);
			Curso curso = getUsuarioLogado().getVinculoAtivo().getDiscente().getCurso();
			ano = getCalendarioVigente().getAnoNovasTurmas();
			periodo = getCalendarioVigente().getPeriodoNovasTurmas();
			solicitacoes = dao.findByCurso(curso, ano, periodo);
			if (isEmpty(solicitacoes)) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			return forward("/graduacao/solicitacao_turma/lista_discente.jsp");
		} else {
			addMensagemErro("Esta op��o est� dispon�vel apenas para discentes regulares.");
			return null;
		}
	}
	
	/**
	 * Inicia o caso de uso de solicita��o de turma de F�RIAS.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarSolicitacaoFerias() throws ArqException{

		checkChangeRole();
		initObj();
		obj.setTipo( Turma.FERIAS );
		setModoDireto(Boolean.FALSE);
		
		verificaPeriodoFeriasDefinido();
		
		/** Validar se est� dentro do per�odo da coordena��o solicitar turma de f�rias ao departamento. */
		if ( !getCalendarioVigente().isPeriodoSolicitacaoTurmaFerias() ) {
			addMensagemErro( CalendarioAcademico.getDescricaoPeriodo("solicita��o de turma de f�rias", 
					getCalendarioVigente().getInicioSolicitacaoTurmaFerias(), 
					getCalendarioVigente().getFimSolicitacaoTurmaFerias()) );
		}
		
		if( hasErrors() )
			return null;

		
		return iniciarSolicitacaoFeriasOuEnsinoIndividual();
	}
	
	/**
	 * Inicia o caso de uso de visualiza��o de turma de F�RIAS.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String visualizarSolicitacaoFerias() throws ArqException{

		checkChangeRole();
		initObj();
		obj.setTipo( Turma.FERIAS );
		setModoDireto(Boolean.FALSE);

		verificaPeriodoFeriasDefinido();
		
		if( hasErrors() )
			return null;
		
		return iniciarSolicitacaoFeriasOuEnsinoIndividual();
	}


	/**
	 * Verifica se o per�odo de f�rias foi definido.
	 * M�todo n�o invocado por JSP's.
	 */
	private void verificaPeriodoFeriasDefinido(){
		
		if (getCalendarioVigente().getInicioFerias() == null || getCalendarioVigente().getFimFerias() == null) {
			addMensagemErro( ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO) + 
					" n�o definiu as datas do Per�odo Letivo de F�rias.");
		}
		
		if( getCursoAtualCoordenacao() == null ){
			addMensagemErroPadrao();
		}
		
	}

	/**
	 * Inicia o caso de uso de solicita��o de turma para solicitar cria��o de
	 * turmas a partir de solicita��es de ensino individual. Este m�todo carrega
	 * as solicita��es de ensino individual realizadas para o coordenador
	 * selecionar o componente e os alunos da turma de ensino individual.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarSolicitacaoEnsinoIndividual() throws ArqException{

		checkChangeRole();
		initObj();
		setModoDireto(Boolean.FALSE);

		if( getCalendarioVigente() == null ){
			addMensagemErro("N�o foi poss�vel carregar o calend�rio vigente, contacte a administra��o.");
			return null;
		}

		if( !getCalendarioVigente().isPeriodoSolicitacaoTurmaEnsinoIndiv() ){
			Formatador fmt = Formatador.getInstance();
			addMensagemErro("N�o � permitido realizar solicita��o de turma de ensino individualizado fora do per�odo "
					+ "determinado no calend�rio universit�rio. O per�odo oficial para solicita��o de turma de ensino individualizado estende-se de "
					+ fmt.formatarData(getCalendarioVigente().getInicioSolicitacaoTurmaEnsinoIndiv()) + " a " + fmt.formatarData(getCalendarioVigente().getFimSolicitacaoTurmaEnsinoIndiv()) + ".");
			//return null;
		}

		if( getCursoAtualCoordenacao() == null ){
			addMensagemErro("Esta opera��o s� est� dispon�vel para coordena��es de curso.");
			return null;
		}

		this.obj.setTipo( Turma.ENSINO_INDIVIDUAL );

		return iniciarSolicitacaoFeriasOuEnsinoIndividual();
	}

	/**
	 * Executa processamento comum a inicializa��o da solicita��o de turma de
	 * ferias e ensino individual e redireciona para a pagina. onde o
	 * coordenador ir� selecionar o componente que possui solicita��o dos alunos
	 * para solicitar a turma ao departamento.<br/>M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarSolicitacaoFeriasOuEnsinoIndividual() throws ArqException{
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);

		CalendarioAcademico cal = getCalendarioVigente();
		this.obj.setCurso( getCursoAtualCoordenacao() );
		if( obj.isTurmaFerias() ){
			solicitacoesEnsinoIndividual = dao.findByCurso(getCursoAtualCoordenacao().getId(), obj.getTipo(), cal.getAnoFeriasVigente(), cal.getPeriodoFeriasVigente());
		}else{
			solicitacoesEnsinoIndividual = dao.findByCurso(getCursoAtualCoordenacao().getId(), obj.getTipo(), cal.getAno(), cal.getPeriodo());
		}

		this.obj.setPermiteSolicitarTurmaSemSolicitacao( 
				ParametroHelper.getInstance().getParametroBoolean( ParametrosGraduacao.DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO ) );
		
		if ( obj.isTurmaFerias() && isEmpty( solicitacoesEnsinoIndividual ) && this.obj.isPermiteSolicitarTurmaSemSolicitacao() ) {
			addMensagemInformation("N�o foi encontrado nenhuma solicita��o de turma para o Curso.");
			return iniciarSolicitacaoFeriasSemSolicitacao();
		}
		
		if( isEmpty( solicitacoesEnsinoIndividual ) ){
			String msgErro = "N�o h� solicita��es de " + obj.getTipoString() + " de alunos do seu curso";
			if( obj.isTurmaFerias() )
				msgErro+= " para o per�odo de f�rias vigente (" + cal.getAnoFeriasVigente() + "." + cal.getPeriodoFeriasVigente() + ")";
			addMensagemErro(msgErro);
			return null;
		}

		mapaAtendimentos = new HashMap<Integer, Boolean>();
		for( SolicitacaoEnsinoIndividual sol : solicitacoesEnsinoIndividual ){
			if( !mapaAtendimentos.containsKey(sol.getComponente().getId()) || !mapaAtendimentos.get(sol.getComponente().getId()) )
				mapaAtendimentos.put(sol.getComponente().getId(), sol.isSolicitada());
		}
		
		setConfirmButton("Cadastrar Solicita��o de Turma");
		return forward(JSP_SOLICITACAO_ENSINO_INDIVIDUAL);
	}

	/**
	 * Busca por componentes curriculares para o qual se deseja criar uma
	 * solicita��o de turma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/busca_componentes.jsp</li>
	 * <li></li>
	 * </ul>
	 * 
	 * @return
	 */
	public String buscarComponente() {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o par�metro de busca");
			return null;
		}

		try {
			DisciplinaDao dao = getDAO(DisciplinaDao.class);
			if ("nome".equalsIgnoreCase(param)){
				if( obj.getComponenteCurricular().getDetalhes().getNome() == null || obj.getComponenteCurricular().getDetalhes().getNome().trim().length() ==0  ){
					addMensagemErro("Entre com o nome da disciplina que deseja buscar.");
					return null;
				}
				setComponentes(dao.findByNome(obj.getComponenteCurricular().getDetalhes().getNome(), 0, NivelEnsino.GRADUACAO, null, false, true, TipoComponenteCurricular.BLOCO, TipoComponenteCurricular.DISCIPLINA, TipoComponenteCurricular.MODULO, TipoComponenteCurricular.ATIVIDADE));
			}else if ("codigo".equalsIgnoreCase(param)){
				if( obj.getComponenteCurricular().getDetalhes().getCodigo() == null || obj.getComponenteCurricular().getDetalhes().getCodigo().trim().length() ==0  ){
					addMensagemErro("Entre com o c�digo da disciplina que deseja buscar.");
					return null;
				}
				setComponentes(dao.findByCodigoLike(obj.getComponenteCurricular().getDetalhes().getCodigo(), 0, NivelEnsino.GRADUACAO, false, true,TipoComponenteCurricular.BLOCO, TipoComponenteCurricular.DISCIPLINA, TipoComponenteCurricular.MODULO, TipoComponenteCurricular.ATIVIDADE));
			} else
				setComponentes(null);
		} catch (LimiteResultadosException e) {
			addMensagemErro( "O limite de resultados foi ultrapassado. Por favor, refine a consulta." );
			e.printStackTrace();
		}catch (DAOException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			notifyError(e);
		}

		return null;
	}

	/**
	 * Carrega as informa��es do componente selecionado e apresenta a tela dos
	 * dados gerais.<br/>
	 * M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	@Override
	public String selecionaComponenteCurricular(ComponenteCurricular componente) throws ArqException {		
		if( (componente.isAtividade() && (componente.getFormaParticipacao() != null && !componente.getFormaParticipacao().isEspecialColetiva()) ) ){
			addMensagemErro("N�o � poss�vel solicitar turmas de componentes do tipo atividade.");
			return null;
		}
		if (obj.getCurso().isADistancia() != componente.isDistancia()){
			addMensagemErro("O componente curricular (" + (componente.isDistancia() ? "EAD" :"presencial")+") n�o � compat�vel com a modalidade de educa��o da turma (" + (obj.getCurso().isADistancia() ? "EAD" :"presencial")+")");
			return null;
		}
		return prepareSolicitacao(componente);
	}

	/**
	 * Inicia a solicita��o de turma para subunidades de componentes de bloco.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/subunidades.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionarSubunidade(ComponenteCurricular comp) throws ArqException {
		return prepareSolicitacao(comp);
	}

	/**
	 * Ap�s a sele��o do componente que ser� solicitado a turma, prepara a solicita��o e inicia o caso e uso,
	 * levando a tela inicial da solicita��o da turma.<br/>M�todo n�o invocado por JSP�s.
	 * @param comp
	 * @return
	 * @throws ArqException
	 */
	public String prepareSolicitacao( ComponenteCurricular comp ) throws ArqException{
		obj.setComponenteCurricular( comp );
		carregarReservas();
		if( obj.getReservas() == null || obj.getReservas().isEmpty() ){
			addMensagem(MensagensGraduacao.ERRO_AO_CARREGAR_MATRIZES_CURRICULARES);
			return null;
		}
		
		setOperacaoAtiva( SigaaListaComando.SOLICITAR_ABERTURA_TURMA.getId() );
		prepareMovimento(SigaaListaComando.SOLICITAR_ABERTURA_TURMA);
		return telaDadosGerais();
	}

	/**
	 * Seleciona uma solicita��o de ensino individual ou de ferias para que seja
	 * criada a solicita��o de turma para aquele componente com os discentes
	 * interessado.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /sigaa.war/graduacao/solicitacao_turma/solicitacoes_ensino_individual.jsp
	 * </li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionarSolicitacaoEnsinoIndividual() throws ArqException {
		Integer id = getParameterInt("id_solicitacao");
		if( id == null ){
			addMensagem(MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO, "Solicita��o");
			return null;
		}
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);

		SolicitacaoEnsinoIndividual solicitacaoEnsinoIndividual = dao.findByPrimaryKey(id, SolicitacaoEnsinoIndividual.class);
		if (solicitacaoEnsinoIndividual == null) {
			addMensagem(MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO, "Solicita��o");
			return null;
		}
		obj.setComponenteCurricular( solicitacaoEnsinoIndividual.getComponente() );
		obj.setAno( (short) solicitacaoEnsinoIndividual.getAno() );
		obj.setPeriodo( (byte) solicitacaoEnsinoIndividual.getPeriodo() );
		obj.setTipo( solicitacaoEnsinoIndividual.getTipo() );

		// carregando outros discentes que tamb�m realizaram solicita��o de ensino individual da mesma disciplina
		if (getCursoAtualCoordenacao() == null) {
			addMensagemErro("O curso atual coordenado n�o est� definido. Reinicie o procedimento utilizando os links do sistema.");
			return cancelar();
		}
		solicitacoesEnsinoIndividual = dao.findByComponenteAnoPeriodoSituacao(getCursoAtualCoordenacao().getId(), solicitacaoEnsinoIndividual.getComponente().getId(), solicitacaoEnsinoIndividual.getAno(), solicitacaoEnsinoIndividual.getPeriodo(), SolicitacaoEnsinoIndividual.SOLICITADA);
		Collections.sort(solicitacoesEnsinoIndividual);

		prepareMovimento(SigaaListaComando.SOLICITAR_ABERTURA_TURMA);
		setOperacaoAtiva( SigaaListaComando.SOLICITAR_ABERTURA_TURMA.getId() );
		if( (obj.getComponenteCurricular().isAtividade() && (obj.getComponenteCurricular().getFormaParticipacao() != null && !obj.getComponenteCurricular().getFormaParticipacao().isEspecialColetiva()) )|| obj.getComponenteCurricular().isSubUnidade() ){
			addMensagemErro("N�o � poss�vel solicitar turmas de componentes do tipo atividade ou subunidade de componentes de bloco.");
			return iniciarSolicitacaoFeriasOuEnsinoIndividual();
		}
		return selecionaComponenteCurricular(obj.getComponenteCurricular());
	}

	/**
	 * Submete os dados gerais da solicita��o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/dados_solicitacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String submeterDados() throws ArqException, NegocioException{

		if( obj.getTipo() < 0 )
			addMensagemErro("Tipo da turma indefinido.");

		obj.setVagas( (short) somarTotalVagasReservadas() );
		//validando campos
		if( !(obj.isTurmaEnsinoIndividual() || obj.isTurmaFerias() ) && obj.getVagas() <= 0 )
			addMensagemErro("A quantidade de vagas solicitadas deve ser superior a zero.");

		if( obj.getAno() <= 0)
			addMensagemErro("O ano informado n�o � v�lido.");

		if( obj.getPeriodo() <= 0  || obj.getPeriodo() >= 5 )
			addMensagemErro("O per�odo informado n�o � v�lido.");

		if( obj.isTurmaFerias() || obj.isTurmaEnsinoIndividual() ){
			obj.setDiscentes( new ArrayList<DiscentesSolicitacao>() );
			String[] linhas = getCurrentRequest().getParameterValues("discentesSelecionados");
			if( obj.isTurmaEnsinoIndividual() ){
				if( linhas == null  ){
					addMensagemErro("Selecione pelo menos um discente para cursar a turma de ensino individualizado.");
					return null;
				}else if( linhas.length > ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL) ){
					addMensagemWarning("A turma, por ter mais de "
							+ ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL)
							+ " discentes, ser� convertida para turma regular automaticamente.");
				}
			}
			else if( obj.isTurmaFerias() && !obj.isPermiteSolicitarTurmaSemSolicitacao() ){
				if( isEmpty(linhas) ){
					addMensagemErro("Pelo menos um discente deve ser selecionado.");
					return null;
				} 
			}
			
			if ( !isEmpty(linhas) ) {
				DiscentesSolicitacaoDao dao = getDAO( DiscentesSolicitacaoDao.class );
				obj.setDiscentes( new ArrayList<DiscentesSolicitacao>() );
				solicitacoesEnsinoIndividualAtendidas = new ArrayList<SolicitacaoEnsinoIndividual>();
				for( String idString : linhas ){
					int id = Integer.parseInt(idString);
	
					SolicitacaoEnsinoIndividual solicitacao = dao.findByPrimaryKey(id, SolicitacaoEnsinoIndividual.class);
	
					solicitacao.setSolicitacaoTurma(obj);
					solicitacoesEnsinoIndividualAtendidas.add(solicitacao);
	
					DiscentesSolicitacao ds = null;
					if( solicitacao.getSolicitacaoTurma() != null ){
						ds = dao.findByDiscenteSolicitacao( solicitacao.getDiscente().getId() , solicitacao.getSolicitacaoTurma().getId());
					}
					if( ds == null )
						ds = new DiscentesSolicitacao();
					
					ds.setDiscenteGraduacao( solicitacao.getDiscente() );
					ds.setSolicitacaoTurma(obj);
					obj.getDiscentes().add(ds);
				}

				if (obj.getVagas() == 0) {
					// setando total de vagas da solicita��o 
					obj.setVagas( (short) obj.getDiscentes().size() );
				}
			}
		} else if( obj.isTurmaEnsinoIndividual() && obj.getId() != 0 ){
			getGenericDAO().lock( obj );
		}

		if (hasOnlyErrors())
			return null;

		if( obj.isTurmaEnsinoIndividual() ){
			// realizado valida��es especificas de turma de ensino individual
			//  deve verificar se ha choque de hor�rio da turma que esta sendo solicitada com as solicita��es de turma dos alunos
			TurmaValidator.validarSolicitacaoTurmaEnsinoIndividual(obj, getCalendarioVigente(), erros);
		} else if( obj.isTurmaFerias() && !isEmpty( obj.getDiscentes() ) ){
			// verificando se j� n�o foi criado turma de f�rias com algum dos discentes da solicita��o.
			// Esta valida��o � necess�ria pois, por falha do sistema, alguns discentes conseguiram solicitar turma de f�rias em mais de um componente curricular */
			TurmaValidator.validarDiscentesTurmaFerias(obj, erros);
		}
		
		obj.setHorarios(HorarioTurmaUtil.parseCodigoHorarios(obj.getHorario(), obj.getUnidade().getId(), getNivelEnsino(), getDAO(HorarioDao.class)));

		obj.setComponenteCurricular(getGenericDAO().findByPrimaryKey(obj.getComponenteCurricular().getId(), ComponenteCurricular.class));
		obj.getComponenteCurricular().setSubUnidades((List<ComponenteCurricular>) getGenericDAO().findByExactField(ComponenteCurricular.class, "blocoSubUnidade", obj.getComponenteCurricular().getId()));
		if( hasOnlyErrors() )
			return null;

		// independente do componente, turma de ensino individual nao registra hor�rio
		if( obj.isTurmaEnsinoIndividual() && !obj.isGeraTurmaRegular() )
			return telaResumo();	
		else if (obj.getComponenteCurricular().isExigeHorarioEmTurmas())
			return formHorarios();
		else
			return telaResumo();

	}

	/**
	 * Remove da cole��o (e da view) o grupo de hor�rio escolhido na view.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/horarios.jsp</li>
	 * </ul>
	 */
	public void removerPeriodoHorarioFlexivel(ActionEvent evt) {
		
		if (!modelGrupoHorarios.isRowAvailable()) {
			addMensagemErro("Hor�rio j� foi removido");
			return;
		}
		
		final GrupoHorarios grupo = (GrupoHorarios) modelGrupoHorarios.getRowData();
		
		@SuppressWarnings("unchecked")
		Collection<HorarioTurma> horariosSelecionados = CollectionUtils.select(obj.getHorarios(),new Predicate(){
			public boolean evaluate(Object arg) {
				HorarioTurma ht = (HorarioTurma) arg;
				if (ht.getDataInicio().equals(grupo.getPeriodo().getInicio()) && ht.getDataFim().equals( grupo.getPeriodo().getFim() ))
					return true;
				return false;
			}
		});
		
		obj.getHorarios().removeAll(horariosSelecionados);
		grupoHorarios.remove(grupo);
		modelGrupoHorarios = new ListDataModel(grupoHorarios);
		horariosMarcados = null;
	}

	/**
	 * Cadastra a solicita��o de turma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/resumo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		String msgSucesso = null;
		try {

			if (getConfirmButton().equalsIgnoreCase("remover")) {
				
				if( !checkOperacaoAtiva( SigaaListaComando.REMOVER_SOLICITACAO_TURMA.getId() ) ){
					return cancelar();
				}
				
				// caso seja remo��o 
				obj.setCodMovimento( SigaaListaComando.REMOVER_SOLICITACAO_TURMA );
				msgSucesso = "Solicita��o de turma removida com sucesso!";
			}else if( alterarHorario ){
				
				if( !checkOperacaoAtiva( SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA.getId() ) ){
					return cancelar();
				}
				
				// caso seja altera��o de hor�rio da solicita��o  
				obj.setCodMovimento( SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA );
				obj.setSituacao( SolicitacaoTurma.ATENDIDA_ALTERACAO );
				
				// removendo matrizes com 0 (ZERO) vagas solicitadas 
				for (Iterator<ReservaCurso> it = obj.getReservas().iterator(); it.hasNext();) {
					ReservaCurso rc = it.next();
					if( rc.getVagasSolicitadas() == null || (rc.getVagasSolicitadas() != null && rc.getVagasSolicitadas() <= 0) )
						it.remove();
				}
				msgSucesso = "Hor�rio da solicita��o de turma alterado com sucesso!";
			} else if( obj.getId() > 0 ){
				
				if( !checkOperacaoAtiva( SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA.getId() ) ){
					return cancelar();
				}
				
				// ALTERACAO DA TURMA
				obj.setCodMovimento( SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA );
				if( obj.isTurmaEnsinoIndividual() || obj.isTurmaFerias() )
					obj.setSolicitacoesEnsinoIndividualAtendidas(getSolicitacoesEnsinoIndividualAtendidas());
				
				// removendo matrizes com 0 (ZERO) vagas solicitadas 
				for (Iterator<ReservaCurso> it = obj.getReservas().iterator(); it.hasNext();) {
					ReservaCurso rc = it.next();
					if(rc.getId() <= 0 &&  (rc.getVagasSolicitadas() == null || (rc.getVagasSolicitadas() != null &&
					   rc.getVagasSolicitadas() <= 0) ) )
						it.remove();
				}
				msgSucesso = "Solicita��o de turma alterada com sucesso!";
			}else{
				// caso seja cadastro de nova solicita��o
				if( !checkOperacaoAtiva( SigaaListaComando.SOLICITAR_ABERTURA_TURMA.getId() ) ){
					return cancelar();
				}
				
				obj.setCodMovimento(SigaaListaComando.SOLICITAR_ABERTURA_TURMA);
				obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
				obj.setDataCadastro( new Date() );
				if( obj.isTurmaEnsinoIndividual() || obj.isTurmaFerias() )
					obj.setSolicitacoesEnsinoIndividualAtendidas(getSolicitacoesEnsinoIndividualAtendidas());

				// removendo matrizes com 0 (ZERO) vagas solicitadas 
				for (Iterator<ReservaCurso> it = obj.getReservas().iterator(); it.hasNext();) {
					ReservaCurso rc = it.next();
					if( rc.getVagasSolicitadas() == null || (rc.getVagasSolicitadas() != null && rc.getVagasSolicitadas() <= 0) )
						it.remove();
				}
				msgSucesso = "Solicita��o de turma cadastrada com sucesso!";
			}
			
			
			if(obj.getUnidade() == null || obj.getUnidade().getId() == 0) {
				obj.setUnidade(getGenericDAO().findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class));
			} else {
				obj.setUnidade(getGenericDAO().findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
			}

			executeWithoutClosingSession(obj, getCurrentRequest());
			
		} catch(NegocioException e) {
			e.printStackTrace();
			addMensagemErro(e.getMessage());
			return null;
		}
		removeOperacaoAtiva();
		addMessage(msgSucesso, TipoMensagemUFRN.INFORMATION);
		return cancelar();

	}
	
	/**
	 * Remove o bean da sess�o e redireciona o usu�rio 
	 * Redireciona o usu�rio para a p�gina de solicita��es.
	 * </b>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/resumo.jsp</li>
	 */
	@Override
	public String cancelar() {
		if(isAlterarSolicitacao()){	
			alterarSolicitacao = false;
			try {
				return listar();
			} catch (ArqException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return super.cancelar();
		}
			
	}
	
	/** Define uma pseudoturma, utilizada para definir o hor�rio.
	 * @throws ArqException
	 * @throws NegocioException
	 */
	protected void definePseudoTurma() throws ArqException, NegocioException {
		turma = new Turma();
		turma.setDisciplina(obj.getComponenteCurricular());
		turma.setAno(obj.getAno());
		turma.setPeriodo(obj.getPeriodo());
		turma.setTipo(obj.getTipo());
		turma.setLocal("A DEFINIR");
		turma.setSolicitacao(obj);
		getGenericDAO().refresh(turma.getDisciplina().getUnidade());
		if ( isPossivelInformarQntVagasTurmaFerias() ) {
			if (obj.getDiscentes() != null)
				turma.setCapacidadeAluno(obj.getDiscentes().size());
			else
				turma.setCapacidadeAluno(0);
		}
		if (obj.getHorarios() != null)
			turma.setHorarios(obj.getHorarios());
		if (obj.isTurmaRegular()) {
			
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
			turma.setDataInicio(cal.getInicioPeriodoLetivo());
			turma.setDataFim(cal.getFimPeriodoLetivo());
		} else if (obj.isTurmaEnsinoIndividual()){
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
			turma.setDataInicio(cal.getInicioPeriodoLetivo());
			turma.setDataFim(cal.getFimPeriodoLetivo());
		} else if (obj.isTurmaFerias()) {
			CalendarioAcademico cal = getCalendarioVigente();
			turma.setDataInicio(cal.getInicioFerias());
			turma.setDataFim(cal.getFimFerias());
		}
	}

	/** Indica se a opera��o atual � de altera��o da solicita��o de turma.
	 * @return
	 */
	public boolean isAlteracaoSolicitada() {
		return obj.getSituacao() == SolicitacaoTurma.SOLICITADA_ALTERACAO;
	}

	/**
	 * Lista as solicita��es do usu�rio logado (que deve ser um coordenador).<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/lista.jsp</li>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class);
		// caso discente, lista apenas as solicita��es para o curso
		if (getUsuarioLogado().getVinculoAtivo().isVinculoDiscente()) {
			Curso curso = getUsuarioLogado().getVinculoAtivo().getDiscente().getCurso();
			solicitacoes = dao.findByCurso(curso, ano, periodo);
			if (isEmpty(solicitacoes)) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			return forward("/graduacao/solicitacao_turma/lista_discente.jsp");
		}
		
		checkRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO );

		CalendarioAcademico cal = null;
		if( ano == null || periodo == null ){
			cal = getCalendarioVigente();
			ano = cal.getAno();
			periodo = cal.getPeriodo();
		} else if ( periodo == 3 || periodo == 4 ){
			cal = getCalendarioVigente();
		}else {
			cal = CalendarioAcademicoHelper.getCalendario(ano, periodo, new Unidade( Unidade.UNIDADE_DIREITO_GLOBAL ), NivelEnsino.GRADUACAO, null, null, null);
			ano = cal.getAno();
			periodo = cal.getPeriodo();
		}
		

		solicitacoes = dao.findByCurso(getCursoAtualCoordenacao(), cal.getAno(), cal.getPeriodo());
		solicitacoes.addAll( dao.findByCursoTipo(getCursoAtualCoordenacao(), cal.getAno(), cal.getPeriodo(), Turma.ENSINO_INDIVIDUAL) );
		if (cal.getAnoFeriasVigente() != null && cal.getPeriodoFeriasVigente() != null)
			solicitacoes.addAll( dao.findByCursoTipo(getCursoAtualCoordenacao(), cal.getAnoFeriasVigente(), cal.getPeriodoFeriasVigente(), Turma.FERIAS) );

		for (SolicitacaoTurma sol : solicitacoes) {
			sol.setCalendario(cal);
		}
		
		return forward(JSP_LIST_PAGE);
	}

	/**
	 * Retorna a soma de vagas de todas as reservas da solicita��o
	 * @return
	 * @throws DAOException 
	 */
	private int somarTotalVagasReservadas() throws DAOException{
		int totalVagas = 0;
		if( isPossivelInformarQntVagasTurmaFerias() ){
			for(ReservaCurso rc: obj.getReservas()){
				totalVagas += rc.getVagasSolicitadas() != null ? rc.getVagasSolicitadas() : 0 ;
				if (rc.getVagasSolicitadasIngressantes() != null) {
					rc.setPossuiVagaIngressantes(true); 
					totalVagas += rc.getVagasSolicitadasIngressantes();
				}
			}
		}
		return totalVagas;
	}

	/**
	 * Busca as matrizes curriculares ativas do curso do coordenador
	 * e seta nas reservas adicionando estas � cole��o de reservas
	 * @throws DAOException
	 *
	 */
	private void carregarReservas() throws DAOException{
		obj.setReservas( new ArrayList<ReservaCurso>() );

		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		try {
			matrizes = dao.findAtivasByCurso( this.obj.getCurso().getId() );
			
			if( matrizes != null && !matrizes.isEmpty() ){
				for( MatrizCurricular mc: matrizes ){
					ReservaCurso rc = new ReservaCurso();
					rc.setSolicitacao( obj );
					rc.setMatrizCurricular( mc );
					rc.setVagasOcupadas( (short)0 );
					rc.setVagasOcupadasIngressantes( (short)0 );
					rc.setPossuiVagaIngressantes(reservaToIngressantes(mc));
					obj.getReservas().add( rc );
				}
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * M�todo respons�vel por analisar se a matriz curricular possui algum curr�culo, 
	 * cujo o componente seja oferecido no primeiro per�odo.
	 * @return
	 * @throws DAOException 
	 */
	private boolean reservaToIngressantes(MatrizCurricular matrizCurricular) throws DAOException{
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		Collection<Curriculo> listCurriculos = dao.findByMatriz(matrizCurricular.getId(), true);
		for (Curriculo curriculo : listCurriculos) {
			for (CurriculoComponente cc : curriculo.getCurriculoComponentes()) {
				if (cc.getComponente().getId() == obj.getComponenteCurricular().getId()
						&& cc.getSemestreOferta() == 1)
				return true;
			}
		}
		return false;
	}

	/**
	 * Adiciona um discente a lista de discente interessados na turma que esta
	 * sendo solicitada. a ser utilizado apenas nas solicita��es de turma
	 * especial de ferias. Art. 246. Cada aluno poder� obter matr�cula em apenas
	 * um componente curricular por per�odo letivo especial de f�rias.
	 * Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N�
	 * 227/2009-CONSEPE, de 3 de dezembro de 2009.<br/>
	 * M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String adicionarDiscente() throws ArqException{

		try {
			TurmaValidator.validarAdicaoDiscenteTurmaFerias(obj, discente, erros);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		if( hasErrors() )
			return null;

		DiscentesSolicitacao ds = new DiscentesSolicitacao();
		ds.setSolicitacaoTurma( obj );
		ds.setDiscenteGraduacao( discente );

		obj.getDiscentes().add( ds );

		discente = new DiscenteGraduacao();

		return null;

	}

	/**
	 * Remove um discente da lista de discentes interessados na turma de ferias.<br/>
	 * M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 */
	public String removerDiscente(){

		Integer id = getParameterInt("idDiscenteGrad");

		if( id == null || id <= 0 ){
			addMensagemErro("Por favor selecione um discente.");
			return null;
		}

		for (Iterator<DiscentesSolicitacao> iter = obj.getDiscentes().iterator(); iter.hasNext();) {
			DiscentesSolicitacao ds = iter.next();

			if( ds.getDiscenteGraduacao().getId() == id ){
				iter.remove();
				return null;
			}

		}

		return null;
	}

	/**
	 * Inicia o caso de uso de remo��o de turma solicita��o de turma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String preRemover() {
		try {
			setOperacaoAtiva(SigaaListaComando.REMOVER_SOLICITACAO_TURMA.getId());
			prepareMovimento(SigaaListaComando.REMOVER_SOLICITACAO_TURMA);

			GenericDAO dao = getGenericDAO();
			
			dao.setSistema(getSistema());
			dao.setSession(getSessionRequest());
			
			setId();

			this.obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
			this.obj.getReservas().iterator();
			setConfirmButton( "Remover" );
			
			this.obj.setPermiteSolicitarTurmaSemSolicitacao( 
					ParametroHelper.getInstance().getParametroBoolean( ParametrosGraduacao.DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO ) );
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}

		setReadOnly(true);

		return telaResumo();
	}

	public boolean isOperacaoAtivaRemover() {
		return isOperacaoAtiva(SigaaListaComando.REMOVER_SOLICITACAO_TURMA.getId());
	}
	
	/**
	 * Carrega as informa��es da solicita��o e exibe na JSP de visualiza��o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.ear/sigaa.war/graduacao/solicitacao_turma/lista.jsp</li>
	 * <li>/sigaa.ear/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String view() throws DAOException {

		Integer id = getParameterInt("id");
		boolean isListaSolicitacao = getParameterBoolean("isListaSolicitacao");
		alterarSolicitacao = isListaSolicitacao;
		GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(id, SolicitacaoTurma.class);
		Collections.sort((List<ReservaCurso>) obj.getReservas());
		return forward( "/graduacao/solicitacao_turma/view.jsp" );
	}

	/**
	 * Retorna os hor�rios agrupados por per�odo
	 * 
	 * @return
	 */
	private List<GrupoHorarios> getPeriodosHorariosEscolhidos() {
		grupoHorarios = new ArrayList<GrupoHorarios>();
		grupoHorarios = HorarioTurmaUtil.agruparHorarioPorPeriodo(obj.getHorarios());
		
		return grupoHorarios;
	}	
	
	/**
	 * Verifica se j� foi adicionado uma hor�rio na data indicada
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void validarPeriodoEscolhido() {
		
		validateRequired(periodoInicio, "Data do in�cio do per�odo", erros);
		validateRequired(periodoFim, "Data do fim do per�odo", erros);
		ValidatorUtil.validaInicioFim(obj.getCalendario().getInicioPeriodoLetivo(), periodoInicio, "Data in�cio", erros);
		ValidatorUtil.validaInicioFim(periodoFim, obj.getCalendario().getFimPeriodoLetivo(), "Data Fim", erros);
		ValidatorUtil.validaInicioFim(periodoInicio, periodoFim, "Data Inicio", erros);
		
		if (hasErrors())
			return;
		
		Collection<HorarioTurma> horariosAgrupadosPorData = CollectionUtils.select(obj.getHorarios(),new Predicate(){
			public boolean evaluate(Object arg) {
				HorarioTurma ht = (HorarioTurma) arg;
				return CalendarUtils.isIntervalosDeDatasConflitantes(ht.getDataInicio(), ht.getDataFim(), periodoInicio, periodoFim);
			}
		});
		
		if (!horariosAgrupadosPorData.isEmpty())
			addMensagemErro("Choque de datas.");
	}	
	
	/** Adiciona um hor�rio � solicita��o de turma.<br/>M�todo n�o invocado por JSP�s.
	 * @param evt
	 * @throws ArqException
	 */
	public void adicionarHorario(ActionEvent evt) throws ArqException {

		if (obj.getHorarios() == null)
			obj.setHorarios(new ArrayList<HorarioTurma>());
		
		validarPeriodoEscolhido();
		
		if (hasErrors())
			return;
		
		Turma t = new Turma();
		t.setDisciplina(obj.getComponenteCurricular());
		t.setDataInicio(obj.getCalendario().getInicioPeriodoLetivo());
		t.setDataFim(obj.getCalendario().getFimPeriodoLetivo());
		
		horariosMarcados =  getCurrentRequest().getParameterValues("horEscolhidos");
		
		List<HorarioTurma> listaHorariosEscolhidos = null;
		if (horariosMarcados != null) {
			listaHorariosEscolhidos = HorarioTurmaUtil.extrairHorariosEscolhidos(horariosMarcados, t, horariosGrade, periodoInicio, periodoFim);
			t.setHorarios(listaHorariosEscolhidos);
		}		
		
		// validar hor�rios da turma
		TurmaValidator.validaHorarios(t, erros, getUsuarioLogado());
		
		if (hasErrors())
			return;		
		
		obj.getHorarios().addAll(t.getHorarios());
		
		modelGrupoHorarios = new ListDataModel(getPeriodosHorariosEscolhidos());
		periodoInicio = CalendarUtils.adicionaUmDia(periodoFim);
		periodoFim = null;
		addMensagemInformation("Per�odo Adicionado com Sucesso.");		
	}
	
	/**
	 * Indica se no formul�rio de lista deve ser exibido apenas o bot�o de visualiza��o
	 * 
	 * @return <code>true</code> SE o ano-per�odo da listagem for anterior ao ano-per�odo atual
	 *  			OU o ano-per�odo � o atual mas n�o est� no per�odo de ajustes de turmas ou solicita��o de ensino individualizado
	 * <p /> <code>false</code> SE o ano-per�odo for posterior ao atual 
	 *  			 OU o ano-per�odo � o atual mas est� no per�odo de ajustes de turmas ou solicita��o de ensino individualizado 
	 */
	public boolean isExibirApenasView(){
		CalendarioAcademico cal = getCalendarioVigente();
		if (   (ano * 10 + periodo < cal.getAno() * 10 + cal.getPeriodo())
			|| (ano * 10 + periodo == cal.getAno() * 10 + cal.getPeriodo()
				&& !( cal.isPeriodoAjustesTurmas() || cal.isPeriodoSolicitacaoTurmaEnsinoIndiv() )))
			return true;
		else 
			return false;
	}
	
	/** Invoca o controller de hor�rio de turma para definir o hor�rio desta solicita��o.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String formHorarios() throws ArqException, NegocioException {
		definePseudoTurma();
		HorarioTurmaMBean horarioBean = getMBean("horarioTurmaBean");
		return horarioBean.populaHorarioTurma(this, "Cadastro de Turma", turma);
	}
	
	/** Retorna a cole��o de componentes curriculares retornados pela busca de solicita��es de turmas. 
	 * @return
	 */
	public Collection<ComponenteCurricular> getComponentes() {
		return componentes;
	}

	/** Seta a cole��o de componentes curriculares retornados pela busca de solicita��es de turmas.
	 * @param componentes
	 */
	public void setComponentes(Collection<ComponenteCurricular> componentes) {
		this.componentes = componentes;
	}

	/** Retorna a cole��o de hor�rios da turma. 
	 * @return
	 */
	public List<Horario> getHorariosGrade() {
		return horariosGrade;
	}

	/** Seta a cole��o de hor�rios da turma.
	 * @param horariosGrade
	 */
	public void setHorariosGrade(List<Horario> horariosGrade) {
		this.horariosGrade = horariosGrade;
	}

	/** Retorna a cole��o de hor�rios marcados para a turma.
	 * @return
	 */
	public String[] getHorariosMarcados() {
		return horariosMarcados;
	}

	/** Seta a cole��o de hor�rios marcados para a turma.
	 * @param horariosMarcados
	 */
	public void setHorariosMarcados(String[] horariosMarcados) {
		this.horariosMarcados = horariosMarcados;
	}

	/** Retorna a cole��o de matrizes curriculares para o qual a turma ter� reserva de vagas. 
	 * @return
	 */
	public Collection<MatrizCurricular> getMatrizes() {
		return matrizes;
	}

	/** Retorna uma cole��o de SelectItem de matrizes curriculares para o qual a turma ter� reserva de vagas.
	 * @return
	 */
	public Collection<SelectItem> getMatrizesCombo() {
		return toSelectItems(matrizes, "id", "descricaoMin");
	}

	/** Seta a cole��o de matrizes curriculares para o qual a turma ter� reserva de vagas.
	 * @param matrizes
	 */
	public void setMatrizes(Collection<MatrizCurricular> matrizes) {
		this.matrizes = matrizes;
	}

	/** Retorna a cole��o de solicita��es de turmas dos alunos para o coordenador. 
	 * @return
	 */
	public Collection<SolicitacaoTurma> getSolicitacoes() {
		return solicitacoes;
	}

	/** Seta a cole��o de solicita��es de turmas dos alunos para o coordenador.
	 * @param solicitacoes
	 */
	public void setSolicitacoes(Collection<SolicitacaoTurma> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	/** Indica se o usu�rio tem permiss�o para alterar o hor�rio da turma. 
	 * @return
	 */
	public boolean isAlterarHorario() {
		return alterarHorario;
	}

	/** Seta se o usu�rio tem permiss�o para alterar o hor�rio da turma. 
	 * @param alterarHorario
	 */
	public void setAlterarHorario(boolean alterarHorario) {
		this.alterarHorario = alterarHorario;
	}

	/** Retorna o discente a ser adicionado � turma. 
	 * @return
	 */
	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	/** seta o discente a ser adicionado � turma.
	 * @param discente
	 */
	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	/** Retorna a cole��o de solicita��es de turmas de ensino individual dos alunos para o coordenador. 
	 * @return
	 */
	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnsinoIndividual() {
		return solicitacoesEnsinoIndividual;
	}

	/** Retorna a cole��o de solicita��es de turmas de ensino individual pendentes dos alunos para o coordenador.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnsinoIndividualPendentes() throws DAOException{
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);
		return dao.findByCurso(getCursoAtualCoordenacao().getId(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), SolicitacaoEnsinoIndividual.SOLICITADA);
	}

	/** Seta a cole��o de solicita��es de turmas de ensino individual dos alunos para o coordenador.
	 * @param solicitacoesEnsinoIndividual
	 */
	public void setSolicitacoesEnsinoIndividual(
			List<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividual) {
		this.solicitacoesEnsinoIndividual = solicitacoesEnsinoIndividual;
	}

	/** Retorna a cole��o de solicita��es de turmas dos alunos para o coordenador que foram atendidas. 
	 * @return
	 */
	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnsinoIndividualAtendidas() {
		return solicitacoesEnsinoIndividualAtendidas;
	}

	/** Seta a cole��o de solicita��es de turmas dos alunos para o coordenador que foram atendidas.
	 * @param solicitacoesEnsinoIndividualAtendidas
	 */
	public void setSolicitacoesEnsinoIndividualAtendidas(
			Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividualAtendidas) {
		this.solicitacoesEnsinoIndividualAtendidas = solicitacoesEnsinoIndividualAtendidas;
	}

	/** Retorna o link para o formul�rio de solicita��o de dados da turma.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @return
	 */
	public String telaDadosGerais(){
		return forward( JSP_SOLICITACAO_DADOS );
	}

	/** Retorna o link para o formul�rio de resumo dos dados da turma.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @return
	 */
	public String telaResumo(){
		return forward( JSP_SOLICITACAO_RESUMO );
	}

	/** Retorna o link para o formul�rio de listagem de solicita��es de turmas.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @return
	 */
	public String telaLista(){
		return forward( JSP_LIST_PAGE );
	}

	/** Retorna o link para o formul�rio de busca de turmas.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/dados_solicitacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String telaBusca() throws ArqException{
		return iniciarSolicitacaoFeriasOuEnsinoIndividual();
	}
	

	/** Retorna o ano da solita��o de turma, utilizado para filtrar as solicita��es 
	 * @return
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano da solita��o de turma, utilizado para filtrar as solicita��es
	 * @param ano
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo da solita��o de turma, utilizado para filtrar as solicita��es 
	 * @return
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo da solita��o de turma, utilizado para filtrar as solicita��es
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	/** Retorna o mapa de solicita��es de turmas pendentes para o componente 
	 * @return
	 */
	public Map<Integer, Boolean> getMapaAtendimentos() {
		return mapaAtendimentos;
	}

	/** Seta o mapa de solicita��es de turmas pendentes para o componente
	 * @param mapaAtendimentos
	 */
	public void setMapaAtendimentos(Map<Integer, Boolean> mapaAtendimentos) {
		this.mapaAtendimentos = mapaAtendimentos;
	}

	/** Retorna a data de fim do hor�rio quando um componente permite flexibilidade de hor�rio  
	 * @return
	 */
	public Date getPeriodoFim() {
		return periodoFim;
	}

	/** Seta a data de fim do hor�rio quando um componente permite flexibilidade de hor�rio
	 * @param periodoFim
	 */
	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	/** Retorna a data de in�cio do hor�rio quando um componente permite flexibilidade de hor�rio 
	 * @return
	 */
	public Date getPeriodoInicio() {
		return periodoInicio;
	}

	/** Seta a data de in�cio do hor�rio quando um componente permite flexibilidade de hor�rio
	 * @param periodoInicio
	 */
	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	/** Retorna o DataModel usado na exibi��o da lista de GrupoHorarios 
	 * @return
	 */
	public DataModel getModelGrupoHorarios() {
		return modelGrupoHorarios;
	}

	/** Seta o DataModel usado na exibi��o da lista de GrupoHorarios
	 * @param modelGrupoHorarios
	 */
	public void setModelGrupoHorarios(DataModel modelGrupoHorarios) {
		this.modelGrupoHorarios = modelGrupoHorarios;
	}

	/** Invoca o controler respons�vel por definir o hor�rio da turma.<br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.sigaa.ensino.jsf.OperadorHorarioTurma#defineHorariosTurma(java.util.Collection)
	 */
	@Override
	public String defineHorariosTurma(Collection<HorarioTurma> horarios) throws ArqException {
		if( !checkOperacaoAtiva( SigaaListaComando.REMOVER_SOLICITACAO_TURMA.getId(), 
				SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA.getId() ,
				SigaaListaComando.SOLICITAR_ABERTURA_TURMA.getId() ) ){
			return cancelar();
		}
		
		// valida o hor�rio escolhido
		Turma turmaTemp;
		try {
			turmaTemp = (Turma) BeanUtils.cloneBean(turma);
		} catch (Exception e) {
			throw new ArqException(e);
		}
		turmaTemp.setHorarios((List<HorarioTurma>) horarios);
		obj.setHorario(HorarioTurmaUtil.formatarCodigoHorarios(turmaTemp));
		String horarioEscolhido = HorarioTurmaUtil.formatarCodigoHorarios(turmaTemp);
		if( obj.isTurmaEnsinoIndividual() ){
			// n�o pode haver outra turma de ensino individual do mesmo componente no mesmo hor�rio
			SolicitacaoTurmaDao dao = getDAO( SolicitacaoTurmaDao.class );
			Collection<SolicitacaoTurma> outrasTurmas = dao.findByComponenteHorarioAnoPeriodo(obj.getComponenteCurricular(), horarioEscolhido, obj.getAno(), obj.getPeriodo());
			if (!isEmpty(outrasTurmas)) {
				for (SolicitacaoTurma outraTurma : outrasTurmas) {
					if(outraTurma.getId() != obj.getId()){
						addMensagemErro("J� existe uma solicita��o de turma de ensino individual para este componente neste mesmo hor�rio.");
						return null;
					}
				}
			}
		}
		
		turma.setHorarios((List<HorarioTurma>) horarios);
		obj.setHorario(HorarioTurmaUtil.formatarCodigoHorarios(turma));
		
		if (!obj.getComponenteCurricular().isPermiteHorarioFlexivel() && !isEmpty(horarios))
			obj.setUnidade(horarios.iterator().next().getHorario().getUnidade());
		
		obj.setHorarios(turma.getHorarios());
		return telaResumo();
	}

	/** Retorna do controler respons�vel por definir o hor�rio da turma.<br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.sigaa.ensino.jsf.OperadorHorarioTurma#definicaoHorarioTurmaVoltar()
	 */
	@Override
	public String definicaoHorarioTurmaVoltar() {
//		if(obj.getComponenteCurricular().isBloco() ){
//			return telaSubunidades();
//		} else {
			return telaDadosGerais();
//		}
	}

	/** Indica se o usu�rio tem permiss�o para alterar o hor�rio da solicita��o da turma.
	 * @see br.ufrn.sigaa.ensino.jsf.OperadorHorarioTurma#isPodeAlterarHorarios()
	 */
	@Override
	public boolean isPodeAlterarHorarios() {
		return true;
	}

	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente)
			throws ArqException {
		ListaMensagens lista = new ListaMensagens();
		
		if ( componente.isAtividade() && !componente.isAtividadeColetiva() ){
			lista.addErro("N�o � poss�vel criar uma turma do componente " + componente.getCodigo() + " porque esta atividade n�o aceita cria��o de turma. Entre em contato com " + ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO) + " para mudar as caracter�sticas do componente.");
		}
		return lista;
	}

	/** Retorna do formul�rio de sele��o de componente curricular.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war\graduacao\componente_programa\busca_componente.jsp</li>
	 * </ul>
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.SeletorComponenteCurricular#retornarSelecaoComponente()
	 */
	public String retornarSelecaoComponente() {
		try {
			return getModoDireto() != null && !getModoDireto() ? iniciarSolicitacaoFeriasOuEnsinoIndividual() : cancelar() ;
		} catch (ArqException e) {
			return tratamentoErroPadrao(e);
		}
	}

	public Boolean getPermiteSolicitarTurmaSemSolicitacao() {
		return permiteSolicitarTurmaSemSolicitacao;
	}

	public void setPermiteSolicitarTurmaSemSolicitacao(
			Boolean permiteSolicitarTurmaSemSolicitacao) {
		this.permiteSolicitarTurmaSemSolicitacao = permiteSolicitarTurmaSemSolicitacao;
	}

	public Boolean getModoDireto() {
		return modoDireto;
	}

	public void setModoDireto(Boolean modoDireto) {
		this.modoDireto = modoDireto;
	}
	
	/** Retorna uma cole��o de reservas de curso com vagas
	 * @return
	 */
	public Collection<ReservaCurso> getReservasValidas() {
		Collection<ReservaCurso> lista = new ArrayList<ReservaCurso>();
		for (ReservaCurso reserva : obj.getReservas())
			if (reserva.getTotalVagasSolicitadas() > 0)
				lista.add(reserva);
		return lista;
	}

	@Override
	public String definePeriodosTurma(Date inicio, Date fim)
			throws ArqException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Redireciona o usu�rio para a tela de voltar conforme o tipo da turma solicitada.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/resumo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String voltarResumo() throws ArqException, NegocioException{
		
		if( obj.isTurmaEnsinoIndividual() && !obj.isGeraTurmaRegular() )
			return telaDadosGerais();	
		else if (obj.getComponenteCurricular().isExigeHorarioEmTurmas())
			return formHorarios();
		else
			return telaDadosGerais();
	}


	public boolean isAlterarSolicitacao() {
		return alterarSolicitacao;
	}

	public void setAlterarSolicitacao(boolean listaSolicitacao) {
		this.alterarSolicitacao = listaSolicitacao;
	}
	
}

