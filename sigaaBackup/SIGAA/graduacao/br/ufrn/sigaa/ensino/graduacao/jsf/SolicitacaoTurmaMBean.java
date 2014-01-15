/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Controller responsável por operações de solicitação de turma.
 * @author Leonardo
 * @author Victor Hugo
 */
@Component("solicitacaoTurma") @Scope("session")
public class SolicitacaoTurmaMBean extends SigaaAbstractController<SolicitacaoTurma> implements SeletorComponenteCurricular, OperadorHorarioTurma {

	// Definições das Views
	/** Define o link para o formulário de solicitações de turmas de ensino individual. */
	public static final String JSP_SOLICITACAO_ENSINO_INDIVIDUAL = "/graduacao/solicitacao_turma/solicitacoes_ensino_individual.jsp";
	/** Define o link para o formulário de solicitação de dados da turma. */
	public static final String JSP_SOLICITACAO_DADOS = "/graduacao/solicitacao_turma/dados_solicitacao.jsp";
	/** Define o link para o formulário de resumo dos dados da turma. */
	public static final String JSP_SOLICITACAO_RESUMO = "/graduacao/solicitacao_turma/resumo.jsp";
	/** Define o link para o formulário de listagem de solicitações de turmas. */
	public static final String JSP_LIST_PAGE = "/graduacao/solicitacao_turma/lista.jsp";

	/** Coleção de subunidades da turma. */
	private Collection<ComponenteCurricular> subunidades = new HashSet<ComponenteCurricular>();
	/** Coleção de componentes curriculares retornados pela busca de solicitações de turmas. */
	private Collection<ComponenteCurricular> componentes = new HashSet<ComponenteCurricular>();
	/** Coleção de matrizes curriculares para o qual a turma terá reserva de vagas. */
	private Collection<MatrizCurricular> matrizes = new ArrayList<MatrizCurricular>();
	/** Coleção de solicitações de turmas dos alunos para o coordenador. */
	private Collection<SolicitacaoTurma> solicitacoes = new ArrayList<SolicitacaoTurma>();
	/** Coleção de solicitações de turmas de ensino individual dos alunos para o coordenador. */
	private List<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividual = new ArrayList<SolicitacaoEnsinoIndividual>();
	/** Coleção de solicitações de turmas dos alunos para o coordenador que foram atendidas. */
	private Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividualAtendidas = new ArrayList<SolicitacaoEnsinoIndividual>();
	/** Discente a ser adicionado à turma. */
	private DiscenteGraduacao discente = new DiscenteGraduacao();
	/** Coleção de horários da turma. */
	private List<Horario> horariosGrade;
	/** Coleção de horários marcados para a turma.*/
	private String[] horariosMarcados;
	/** variavel informa se o procedimento em execução é uma alteração de solicitação. */
	private boolean alterarSolicitacao = false;
			
	/** Data de fim do horário quando um componente permite flexibilidade de horário */
	private Date periodoFim;
	/** Data de início do horário quando um componente permite flexibilidade de horário */
	private Date periodoInicio;
	
	/** Ano da solicitação de turma, utilizado para filtrar as solicitações */
	private Integer ano;
	/** Período da solicitação de turma, utilizado para filtrar as solicitações */
	private Integer periodo;

	// campos para auxilio na visualização
	/** Indica se o usuário tem permissão para alterar o horário da turma. */
	private boolean alterarHorario = false;

	/** DataModel usado na exibição da lista de {@link GrupoHorarios} */
	private DataModel modelGrupoHorarios = new ListDataModel();	
	/** Lista com os Horários agrupados por período */
	public List<GrupoHorarios> grupoHorarios = new ArrayList<GrupoHorarios>();
	
	/**
	 * Mapa de solicitações de turmas pendentes para o componente
	 * <idComponente, Boolean>
	 */
	public Map<Integer, Boolean> mapaAtendimentos;
	
	/** Pseudo turma a ser criada. A turma é utilizada para captação do horário a ser definido na solicitação de turma. */
	public Turma turma;

	/** Define se o coordenador do curso pode solicitar turma de férias sem que os discentes tenham realizado a solicitação.
	 * Caso FALSE o coordenador só poderá solicitar turmas se algum discente tiver solicitado. */
	private Boolean permiteSolicitarTurmaSemSolicitacao = ParametroHelper.getInstance().getParametroBoolean( ParametrosGraduacao.DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO );
	
	/** Indica se está solicitando turma direto sem considerar as solicitações de discentes. */
	private Boolean modoDireto = null;
	
	/** Construtor padrão. */
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
	
	/** Retorna o link para o formulário de listagem de solicitações de turmas. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return JSP_LIST_PAGE;
	}

	/** Retorna o link para o formulário de solicitação de dados da turma. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return JSP_SOLICITACAO_DADOS;
	}
	
	/**
	 * 
	 * Redireciona para a página de solicitações.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/solicitacao_turma/view.jsp
	 * @return
	 */
	public String listaSolicitacoes() {
		return forward(getListPage());
	}

	/**
	 * Atualiza a solicitação de turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
					
					if( !isEmpty( s.getMatriculaGerada() ) && !s.getMatriculaGerada().isExcluida() ) // removendo as solicitações deste componente que já foram atendidas
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

			setConfirmButton("Alterar Solicitação de Turma");
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
	 * Inicia caso de uso de solicitação de turma REGULAR.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Não é permitido realizar solicitação de abertura de turma fora do período " +
					"determinado no calendário universitário" );
			return null;
		}

		if( getSubSistema().equals(SigaaSubsistemas.GRADUACAO) &&  getCursoAtualCoordenacao() == null ){
			addMensagemErroPadrao();
			return null;
		} else if( getSubSistema().equals(SigaaSubsistemas.STRICTO_SENSU) &&  getProgramaStricto() == null ){

		}

		// solicitação de turma regular
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
			throw new NegocioException("O calendário do próximo período regular ainda não se encontra definido.");
		periodoInicio = prox.getInicioPeriodoLetivo();
		obj.setCalendario(prox);
		
		setConfirmButton("Cadastrar Solicitação de Turma");
		ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
		return mBean.buscarComponente(this, "Solicitação de Turma", null,false, false, null);
	}

	/** Inicia caso de uso de solicitação de turma de Férias.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		
		/** Validar se está dentro do período da coordenação solicitar turma de férias ao departamento. */
		if ( !getCalendarioVigente().isPeriodoSolicitacaoTurmaFerias() ) {
			addMensagemErro( CalendarioAcademico.getDescricaoPeriodo("solicitação de turma de férias", 
					getCalendarioVigente().getInicioSolicitacaoTurmaFerias(), 
					getCalendarioVigente().getFimSolicitacaoTurmaFerias()) );
		}
		
		if( hasErrors() )
			return null;
		
		// solicitação de turma de Férias
		obj.setTipo( Turma.FERIAS );
		obj.setPermiteSolicitarTurmaSemSolicitacao(permiteSolicitarTurmaSemSolicitacao);
		
		//Limpa a lista para a criação de uma nova solicitação de Turma.
		solicitacoesEnsinoIndividual.clear();

		if (getCursoAtualCoordenacao() != null) {
			this.obj.setCurso( getCursoAtualCoordenacao() );
		}

		if ( getCalendarioVigente() != null ) {
			this.obj.setAno( getCalendarioVigente().getAnoFeriasVigente().shortValue() );
			this.obj.setPeriodo( getCalendarioVigente().getPeriodoFeriasVigente().byteValue() );
		}

		setConfirmButton("Cadastrar Solicitação de Turma");
		ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
		return mBean.buscarComponente(this, "Solicitação de Turma", null,false, false, null);
	}
	
	/**
	 * Esse método serve para verificar se o docente pode informar a quantidade de vagas para uma determinada 
	 * turma de Férias. 
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
	 * Inicia a listagem de solicitação de turmas do curso do discente.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Esta opção está disponível apenas para discentes regulares.");
			return null;
		}
	}
	
	/**
	 * Inicia o caso de uso de solicitação de turma de FÉRIAS.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		
		/** Validar se está dentro do período da coordenação solicitar turma de férias ao departamento. */
		if ( !getCalendarioVigente().isPeriodoSolicitacaoTurmaFerias() ) {
			addMensagemErro( CalendarioAcademico.getDescricaoPeriodo("solicitação de turma de férias", 
					getCalendarioVigente().getInicioSolicitacaoTurmaFerias(), 
					getCalendarioVigente().getFimSolicitacaoTurmaFerias()) );
		}
		
		if( hasErrors() )
			return null;

		
		return iniciarSolicitacaoFeriasOuEnsinoIndividual();
	}
	
	/**
	 * Inicia o caso de uso de visualização de turma de FÉRIAS.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Verifica se o período de férias foi definido.
	 * Método não invocado por JSP's.
	 */
	private void verificaPeriodoFeriasDefinido(){
		
		if (getCalendarioVigente().getInicioFerias() == null || getCalendarioVigente().getFimFerias() == null) {
			addMensagemErro( ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO) + 
					" não definiu as datas do Período Letivo de Férias.");
		}
		
		if( getCursoAtualCoordenacao() == null ){
			addMensagemErroPadrao();
		}
		
	}

	/**
	 * Inicia o caso de uso de solicitação de turma para solicitar criação de
	 * turmas a partir de solicitações de ensino individual. Este método carrega
	 * as solicitações de ensino individual realizadas para o coordenador
	 * selecionar o componente e os alunos da turma de ensino individual.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Não foi possível carregar o calendário vigente, contacte a administração.");
			return null;
		}

		if( !getCalendarioVigente().isPeriodoSolicitacaoTurmaEnsinoIndiv() ){
			Formatador fmt = Formatador.getInstance();
			addMensagemErro("Não é permitido realizar solicitação de turma de ensino individualizado fora do período "
					+ "determinado no calendário universitário. O período oficial para solicitação de turma de ensino individualizado estende-se de "
					+ fmt.formatarData(getCalendarioVigente().getInicioSolicitacaoTurmaEnsinoIndiv()) + " a " + fmt.formatarData(getCalendarioVigente().getFimSolicitacaoTurmaEnsinoIndiv()) + ".");
			//return null;
		}

		if( getCursoAtualCoordenacao() == null ){
			addMensagemErro("Esta operação só está disponível para coordenações de curso.");
			return null;
		}

		this.obj.setTipo( Turma.ENSINO_INDIVIDUAL );

		return iniciarSolicitacaoFeriasOuEnsinoIndividual();
	}

	/**
	 * Executa processamento comum a inicialização da solicitação de turma de
	 * ferias e ensino individual e redireciona para a pagina. onde o
	 * coordenador irá selecionar o componente que possui solicitação dos alunos
	 * para solicitar a turma ao departamento.<br/>Método não invocado por JSP´s.
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
			addMensagemInformation("Não foi encontrado nenhuma solicitação de turma para o Curso.");
			return iniciarSolicitacaoFeriasSemSolicitacao();
		}
		
		if( isEmpty( solicitacoesEnsinoIndividual ) ){
			String msgErro = "Não há solicitações de " + obj.getTipoString() + " de alunos do seu curso";
			if( obj.isTurmaFerias() )
				msgErro+= " para o período de férias vigente (" + cal.getAnoFeriasVigente() + "." + cal.getPeriodoFeriasVigente() + ")";
			addMensagemErro(msgErro);
			return null;
		}

		mapaAtendimentos = new HashMap<Integer, Boolean>();
		for( SolicitacaoEnsinoIndividual sol : solicitacoesEnsinoIndividual ){
			if( !mapaAtendimentos.containsKey(sol.getComponente().getId()) || !mapaAtendimentos.get(sol.getComponente().getId()) )
				mapaAtendimentos.put(sol.getComponente().getId(), sol.isSolicitada());
		}
		
		setConfirmButton("Cadastrar Solicitação de Turma");
		return forward(JSP_SOLICITACAO_ENSINO_INDIVIDUAL);
	}

	/**
	 * Busca por componentes curriculares para o qual se deseja criar uma
	 * solicitação de turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
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
					addMensagemErro("Entre com o código da disciplina que deseja buscar.");
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
	 * Carrega as informações do componente selecionado e apresenta a tela dos
	 * dados gerais.<br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	@Override
	public String selecionaComponenteCurricular(ComponenteCurricular componente) throws ArqException {		
		if( (componente.isAtividade() && (componente.getFormaParticipacao() != null && !componente.getFormaParticipacao().isEspecialColetiva()) ) ){
			addMensagemErro("Não é possível solicitar turmas de componentes do tipo atividade.");
			return null;
		}
		if (obj.getCurso().isADistancia() != componente.isDistancia()){
			addMensagemErro("O componente curricular (" + (componente.isDistancia() ? "EAD" :"presencial")+") não é compatível com a modalidade de educação da turma (" + (obj.getCurso().isADistancia() ? "EAD" :"presencial")+")");
			return null;
		}
		return prepareSolicitacao(componente);
	}

	/**
	 * Inicia a solicitação de turma para subunidades de componentes de bloco.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Após a seleção do componente que será solicitado a turma, prepara a solicitação e inicia o caso e uso,
	 * levando a tela inicial da solicitação da turma.<br/>Método não invocado por JSP´s.
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
	 * Seleciona uma solicitação de ensino individual ou de ferias para que seja
	 * criada a solicitação de turma para aquele componente com os discentes
	 * interessado.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagem(MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO, "Solicitação");
			return null;
		}
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);

		SolicitacaoEnsinoIndividual solicitacaoEnsinoIndividual = dao.findByPrimaryKey(id, SolicitacaoEnsinoIndividual.class);
		if (solicitacaoEnsinoIndividual == null) {
			addMensagem(MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO, "Solicitação");
			return null;
		}
		obj.setComponenteCurricular( solicitacaoEnsinoIndividual.getComponente() );
		obj.setAno( (short) solicitacaoEnsinoIndividual.getAno() );
		obj.setPeriodo( (byte) solicitacaoEnsinoIndividual.getPeriodo() );
		obj.setTipo( solicitacaoEnsinoIndividual.getTipo() );

		// carregando outros discentes que também realizaram solicitação de ensino individual da mesma disciplina
		if (getCursoAtualCoordenacao() == null) {
			addMensagemErro("O curso atual coordenado não está definido. Reinicie o procedimento utilizando os links do sistema.");
			return cancelar();
		}
		solicitacoesEnsinoIndividual = dao.findByComponenteAnoPeriodoSituacao(getCursoAtualCoordenacao().getId(), solicitacaoEnsinoIndividual.getComponente().getId(), solicitacaoEnsinoIndividual.getAno(), solicitacaoEnsinoIndividual.getPeriodo(), SolicitacaoEnsinoIndividual.SOLICITADA);
		Collections.sort(solicitacoesEnsinoIndividual);

		prepareMovimento(SigaaListaComando.SOLICITAR_ABERTURA_TURMA);
		setOperacaoAtiva( SigaaListaComando.SOLICITAR_ABERTURA_TURMA.getId() );
		if( (obj.getComponenteCurricular().isAtividade() && (obj.getComponenteCurricular().getFormaParticipacao() != null && !obj.getComponenteCurricular().getFormaParticipacao().isEspecialColetiva()) )|| obj.getComponenteCurricular().isSubUnidade() ){
			addMensagemErro("Não é possível solicitar turmas de componentes do tipo atividade ou subunidade de componentes de bloco.");
			return iniciarSolicitacaoFeriasOuEnsinoIndividual();
		}
		return selecionaComponenteCurricular(obj.getComponenteCurricular());
	}

	/**
	 * Submete os dados gerais da solicitação.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("O ano informado não é válido.");

		if( obj.getPeriodo() <= 0  || obj.getPeriodo() >= 5 )
			addMensagemErro("O período informado não é válido.");

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
							+ " discentes, será convertida para turma regular automaticamente.");
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
					// setando total de vagas da solicitação 
					obj.setVagas( (short) obj.getDiscentes().size() );
				}
			}
		} else if( obj.isTurmaEnsinoIndividual() && obj.getId() != 0 ){
			getGenericDAO().lock( obj );
		}

		if (hasOnlyErrors())
			return null;

		if( obj.isTurmaEnsinoIndividual() ){
			// realizado validações especificas de turma de ensino individual
			//  deve verificar se ha choque de horário da turma que esta sendo solicitada com as solicitações de turma dos alunos
			TurmaValidator.validarSolicitacaoTurmaEnsinoIndividual(obj, getCalendarioVigente(), erros);
		} else if( obj.isTurmaFerias() && !isEmpty( obj.getDiscentes() ) ){
			// verificando se já não foi criado turma de férias com algum dos discentes da solicitação.
			// Esta validação é necessária pois, por falha do sistema, alguns discentes conseguiram solicitar turma de férias em mais de um componente curricular */
			TurmaValidator.validarDiscentesTurmaFerias(obj, erros);
		}
		
		obj.setHorarios(HorarioTurmaUtil.parseCodigoHorarios(obj.getHorario(), obj.getUnidade().getId(), getNivelEnsino(), getDAO(HorarioDao.class)));

		obj.setComponenteCurricular(getGenericDAO().findByPrimaryKey(obj.getComponenteCurricular().getId(), ComponenteCurricular.class));
		obj.getComponenteCurricular().setSubUnidades((List<ComponenteCurricular>) getGenericDAO().findByExactField(ComponenteCurricular.class, "blocoSubUnidade", obj.getComponenteCurricular().getId()));
		if( hasOnlyErrors() )
			return null;

		// independente do componente, turma de ensino individual nao registra horário
		if( obj.isTurmaEnsinoIndividual() && !obj.isGeraTurmaRegular() )
			return telaResumo();	
		else if (obj.getComponenteCurricular().isExigeHorarioEmTurmas())
			return formHorarios();
		else
			return telaResumo();

	}

	/**
	 * Remove da coleção (e da view) o grupo de horário escolhido na view.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/horarios.jsp</li>
	 * </ul>
	 */
	public void removerPeriodoHorarioFlexivel(ActionEvent evt) {
		
		if (!modelGrupoHorarios.isRowAvailable()) {
			addMensagemErro("Horário já foi removido");
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
	 * Cadastra a solicitação de turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
				
				// caso seja remoção 
				obj.setCodMovimento( SigaaListaComando.REMOVER_SOLICITACAO_TURMA );
				msgSucesso = "Solicitação de turma removida com sucesso!";
			}else if( alterarHorario ){
				
				if( !checkOperacaoAtiva( SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA.getId() ) ){
					return cancelar();
				}
				
				// caso seja alteração de horário da solicitação  
				obj.setCodMovimento( SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA );
				obj.setSituacao( SolicitacaoTurma.ATENDIDA_ALTERACAO );
				
				// removendo matrizes com 0 (ZERO) vagas solicitadas 
				for (Iterator<ReservaCurso> it = obj.getReservas().iterator(); it.hasNext();) {
					ReservaCurso rc = it.next();
					if( rc.getVagasSolicitadas() == null || (rc.getVagasSolicitadas() != null && rc.getVagasSolicitadas() <= 0) )
						it.remove();
				}
				msgSucesso = "Horário da solicitação de turma alterado com sucesso!";
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
				msgSucesso = "Solicitação de turma alterada com sucesso!";
			}else{
				// caso seja cadastro de nova solicitação
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
				msgSucesso = "Solicitação de turma cadastrada com sucesso!";
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
	 * Remove o bean da sessão e redireciona o usuário 
	 * Redireciona o usuário para a página de solicitações.
	 * </b>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Define uma pseudoturma, utilizada para definir o horário.
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

	/** Indica se a operação atual é de alteração da solicitação de turma.
	 * @return
	 */
	public boolean isAlteracaoSolicitada() {
		return obj.getSituacao() == SolicitacaoTurma.SOLICITADA_ALTERACAO;
	}

	/**
	 * Lista as solicitações do usuário logado (que deve ser um coordenador).<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/lista.jsp</li>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class);
		// caso discente, lista apenas as solicitações para o curso
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
	 * Retorna a soma de vagas de todas as reservas da solicitação
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
	 * e seta nas reservas adicionando estas à coleção de reservas
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
	 * Método responsável por analisar se a matriz curricular possui algum currículo, 
	 * cujo o componente seja oferecido no primeiro período.
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
	 * sendo solicitada. a ser utilizado apenas nas solicitações de turma
	 * especial de ferias. Art. 246. Cada aluno poderá obter matrícula em apenas
	 * um componente curricular por período letivo especial de férias.
	 * Regulamento dos Cursos Regulares de Graduação, Resolução N°
	 * 227/2009-CONSEPE, de 3 de dezembro de 2009.<br/>
	 * Método não invocado por JSP´s.
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
	 * Método não invocado por JSP´s.
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
	 * Inicia o caso de uso de remoção de turma solicitação de turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Carrega as informações da solicitação e exibe na JSP de visualização.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna os horários agrupados por período
	 * 
	 * @return
	 */
	private List<GrupoHorarios> getPeriodosHorariosEscolhidos() {
		grupoHorarios = new ArrayList<GrupoHorarios>();
		grupoHorarios = HorarioTurmaUtil.agruparHorarioPorPeriodo(obj.getHorarios());
		
		return grupoHorarios;
	}	
	
	/**
	 * Verifica se já foi adicionado uma horário na data indicada
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void validarPeriodoEscolhido() {
		
		validateRequired(periodoInicio, "Data do início do período", erros);
		validateRequired(periodoFim, "Data do fim do período", erros);
		ValidatorUtil.validaInicioFim(obj.getCalendario().getInicioPeriodoLetivo(), periodoInicio, "Data início", erros);
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
	
	/** Adiciona um horário à solicitação de turma.<br/>Método não invocado por JSP´s.
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
		
		// validar horários da turma
		TurmaValidator.validaHorarios(t, erros, getUsuarioLogado());
		
		if (hasErrors())
			return;		
		
		obj.getHorarios().addAll(t.getHorarios());
		
		modelGrupoHorarios = new ListDataModel(getPeriodosHorariosEscolhidos());
		periodoInicio = CalendarUtils.adicionaUmDia(periodoFim);
		periodoFim = null;
		addMensagemInformation("Período Adicionado com Sucesso.");		
	}
	
	/**
	 * Indica se no formulário de lista deve ser exibido apenas o botão de visualização
	 * 
	 * @return <code>true</code> SE o ano-período da listagem for anterior ao ano-período atual
	 *  			OU o ano-período é o atual mas não está no período de ajustes de turmas ou solicitação de ensino individualizado
	 * <p /> <code>false</code> SE o ano-período for posterior ao atual 
	 *  			 OU o ano-período é o atual mas está no período de ajustes de turmas ou solicitação de ensino individualizado 
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
	
	/** Invoca o controller de horário de turma para definir o horário desta solicitação.
	 * <br />
	 * Método não invocado por JSPs.
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
	
	/** Retorna a coleção de componentes curriculares retornados pela busca de solicitações de turmas. 
	 * @return
	 */
	public Collection<ComponenteCurricular> getComponentes() {
		return componentes;
	}

	/** Seta a coleção de componentes curriculares retornados pela busca de solicitações de turmas.
	 * @param componentes
	 */
	public void setComponentes(Collection<ComponenteCurricular> componentes) {
		this.componentes = componentes;
	}

	/** Retorna a coleção de horários da turma. 
	 * @return
	 */
	public List<Horario> getHorariosGrade() {
		return horariosGrade;
	}

	/** Seta a coleção de horários da turma.
	 * @param horariosGrade
	 */
	public void setHorariosGrade(List<Horario> horariosGrade) {
		this.horariosGrade = horariosGrade;
	}

	/** Retorna a coleção de horários marcados para a turma.
	 * @return
	 */
	public String[] getHorariosMarcados() {
		return horariosMarcados;
	}

	/** Seta a coleção de horários marcados para a turma.
	 * @param horariosMarcados
	 */
	public void setHorariosMarcados(String[] horariosMarcados) {
		this.horariosMarcados = horariosMarcados;
	}

	/** Retorna a coleção de matrizes curriculares para o qual a turma terá reserva de vagas. 
	 * @return
	 */
	public Collection<MatrizCurricular> getMatrizes() {
		return matrizes;
	}

	/** Retorna uma coleção de SelectItem de matrizes curriculares para o qual a turma terá reserva de vagas.
	 * @return
	 */
	public Collection<SelectItem> getMatrizesCombo() {
		return toSelectItems(matrizes, "id", "descricaoMin");
	}

	/** Seta a coleção de matrizes curriculares para o qual a turma terá reserva de vagas.
	 * @param matrizes
	 */
	public void setMatrizes(Collection<MatrizCurricular> matrizes) {
		this.matrizes = matrizes;
	}

	/** Retorna a coleção de solicitações de turmas dos alunos para o coordenador. 
	 * @return
	 */
	public Collection<SolicitacaoTurma> getSolicitacoes() {
		return solicitacoes;
	}

	/** Seta a coleção de solicitações de turmas dos alunos para o coordenador.
	 * @param solicitacoes
	 */
	public void setSolicitacoes(Collection<SolicitacaoTurma> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	/** Indica se o usuário tem permissão para alterar o horário da turma. 
	 * @return
	 */
	public boolean isAlterarHorario() {
		return alterarHorario;
	}

	/** Seta se o usuário tem permissão para alterar o horário da turma. 
	 * @param alterarHorario
	 */
	public void setAlterarHorario(boolean alterarHorario) {
		this.alterarHorario = alterarHorario;
	}

	/** Retorna o discente a ser adicionado à turma. 
	 * @return
	 */
	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	/** seta o discente a ser adicionado à turma.
	 * @param discente
	 */
	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	/** Retorna a coleção de solicitações de turmas de ensino individual dos alunos para o coordenador. 
	 * @return
	 */
	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnsinoIndividual() {
		return solicitacoesEnsinoIndividual;
	}

	/** Retorna a coleção de solicitações de turmas de ensino individual pendentes dos alunos para o coordenador.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnsinoIndividualPendentes() throws DAOException{
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);
		return dao.findByCurso(getCursoAtualCoordenacao().getId(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), SolicitacaoEnsinoIndividual.SOLICITADA);
	}

	/** Seta a coleção de solicitações de turmas de ensino individual dos alunos para o coordenador.
	 * @param solicitacoesEnsinoIndividual
	 */
	public void setSolicitacoesEnsinoIndividual(
			List<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividual) {
		this.solicitacoesEnsinoIndividual = solicitacoesEnsinoIndividual;
	}

	/** Retorna a coleção de solicitações de turmas dos alunos para o coordenador que foram atendidas. 
	 * @return
	 */
	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnsinoIndividualAtendidas() {
		return solicitacoesEnsinoIndividualAtendidas;
	}

	/** Seta a coleção de solicitações de turmas dos alunos para o coordenador que foram atendidas.
	 * @param solicitacoesEnsinoIndividualAtendidas
	 */
	public void setSolicitacoesEnsinoIndividualAtendidas(
			Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividualAtendidas) {
		this.solicitacoesEnsinoIndividualAtendidas = solicitacoesEnsinoIndividualAtendidas;
	}

	/** Retorna o link para o formulário de solicitação de dados da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
	public String telaDadosGerais(){
		return forward( JSP_SOLICITACAO_DADOS );
	}

	/** Retorna o link para o formulário de resumo dos dados da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
	public String telaResumo(){
		return forward( JSP_SOLICITACAO_RESUMO );
	}

	/** Retorna o link para o formulário de listagem de solicitações de turmas.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
	public String telaLista(){
		return forward( JSP_LIST_PAGE );
	}

	/** Retorna o link para o formulário de busca de turmas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	

	/** Retorna o ano da solitação de turma, utilizado para filtrar as solicitações 
	 * @return
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano da solitação de turma, utilizado para filtrar as solicitações
	 * @param ano
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/** Retorna o período da solitação de turma, utilizado para filtrar as solicitações 
	 * @return
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/** Seta o período da solitação de turma, utilizado para filtrar as solicitações
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	/** Retorna o mapa de solicitações de turmas pendentes para o componente 
	 * @return
	 */
	public Map<Integer, Boolean> getMapaAtendimentos() {
		return mapaAtendimentos;
	}

	/** Seta o mapa de solicitações de turmas pendentes para o componente
	 * @param mapaAtendimentos
	 */
	public void setMapaAtendimentos(Map<Integer, Boolean> mapaAtendimentos) {
		this.mapaAtendimentos = mapaAtendimentos;
	}

	/** Retorna a data de fim do horário quando um componente permite flexibilidade de horário  
	 * @return
	 */
	public Date getPeriodoFim() {
		return periodoFim;
	}

	/** Seta a data de fim do horário quando um componente permite flexibilidade de horário
	 * @param periodoFim
	 */
	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	/** Retorna a data de início do horário quando um componente permite flexibilidade de horário 
	 * @return
	 */
	public Date getPeriodoInicio() {
		return periodoInicio;
	}

	/** Seta a data de início do horário quando um componente permite flexibilidade de horário
	 * @param periodoInicio
	 */
	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	/** Retorna o DataModel usado na exibição da lista de GrupoHorarios 
	 * @return
	 */
	public DataModel getModelGrupoHorarios() {
		return modelGrupoHorarios;
	}

	/** Seta o DataModel usado na exibição da lista de GrupoHorarios
	 * @param modelGrupoHorarios
	 */
	public void setModelGrupoHorarios(DataModel modelGrupoHorarios) {
		this.modelGrupoHorarios = modelGrupoHorarios;
	}

	/** Invoca o controler responsável por definir o horário da turma.<br/>Método não invocado por JSP´s.
	 * @see br.ufrn.sigaa.ensino.jsf.OperadorHorarioTurma#defineHorariosTurma(java.util.Collection)
	 */
	@Override
	public String defineHorariosTurma(Collection<HorarioTurma> horarios) throws ArqException {
		if( !checkOperacaoAtiva( SigaaListaComando.REMOVER_SOLICITACAO_TURMA.getId(), 
				SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA.getId() ,
				SigaaListaComando.SOLICITAR_ABERTURA_TURMA.getId() ) ){
			return cancelar();
		}
		
		// valida o horário escolhido
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
			// não pode haver outra turma de ensino individual do mesmo componente no mesmo horário
			SolicitacaoTurmaDao dao = getDAO( SolicitacaoTurmaDao.class );
			Collection<SolicitacaoTurma> outrasTurmas = dao.findByComponenteHorarioAnoPeriodo(obj.getComponenteCurricular(), horarioEscolhido, obj.getAno(), obj.getPeriodo());
			if (!isEmpty(outrasTurmas)) {
				for (SolicitacaoTurma outraTurma : outrasTurmas) {
					if(outraTurma.getId() != obj.getId()){
						addMensagemErro("Já existe uma solicitação de turma de ensino individual para este componente neste mesmo horário.");
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

	/** Retorna do controler responsável por definir o horário da turma.<br/>Método não invocado por JSP´s.
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

	/** Indica se o usuário tem permissão para alterar o horário da solicitação da turma.
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
			lista.addErro("Não é possível criar uma turma do componente " + componente.getCodigo() + " porque esta atividade não aceita criação de turma. Entre em contato com " + ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO) + " para mudar as características do componente.");
		}
		return lista;
	}

	/** Retorna do formulário de seleção de componente curricular.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Retorna uma coleção de reservas de curso com vagas
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
	 * Redireciona o usuário para a tela de voltar conforme o tipo da turma solicitada.
	 * Método chamado pela(s) seguinte(s) JSP(s):
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

