/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 22/02/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dao.TransferenciaTurmaDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.TurmaOrigemTurmaDestinos;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoTransferenciaTurmas;
import br.ufrn.sigaa.ensino.graduacao.negocio.TransferenciaTurmasValidator;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.jsf.UnidadeMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed Bean para tratar da transfer�ncia de alunos entre turmas
 *
 * @author Leonardo Campos
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
@Component("transferenciaTurma")
@Scope("session")
public class TransferenciaTurmaMBean extends SigaaAbstractController<Object> implements OperadorDiscente {

	//Defini��es das Views
	/** JSP de escolha da turma de origem da transfer�ncia. */
	public static final String JSP_TURMA_ORIGEM = "/graduacao/transferencia_turmas/turma_origem.jsp";
	/** JSP de escolha da turma de destino da transfer�ncia. */
	public static final String JSP_TURMA_DESTINO = "/graduacao/transferencia_turmas/turma_destino.jsp";
	/** JSP de escolha da turma de destino da transfer�ncia. */
	public static final String JSP_TURMA_DESTINO_OUTRO_COMPONENTE = "/graduacao/transferencia_turmas/turma_destino_outro_componente.jsp";
	/** JSP de escolha de alunos que ser�o transferidos. */
	public static final String JSP_ALUNOS = "/graduacao/transferencia_turmas/alunos.jsp";
	/** Comprovante da transfer�ncia entre turmas. */
	public static final String JSP_COMPROVANTE = "/graduacao/transferencia_turmas/comprovante.jsp";
	/** Comprovante de transfer�ncia das turmas do discente. */
	public static final String JSP_COMPROVANTE_DISCENTE = "/graduacao/transferencia_turmas/comprovante_discente.jsp";
	/** JSP com informa��es do discente e poss�veis turmas para transfer�ncia. */
	public static final String JSP_TRANSFERENCIA_TURMAS = "/graduacao/transferencia_turmas/transferencia_turmas_discente.jsp";
	/**Indica se a estrat�gia de transfer�ncia autom�tica de discente se seguir� a ordem de prioridade da matr�cula. */
	public static final String PRIORIDADE_MATRICULA = "PRIO";
	/**Indica se a estrat�gia de transfer�ncia autom�tica de discente ser� de forma aleat�ria. */ 
	public static final String RANDOMICA = "RAND";

	
	/** Turma de origem da transfer�ncia. */
	private Turma turmaOrigem;
	
	/** Turma destino da transfer�ncia. */
	private Turma turmaDestino;

	/** Resultado da busca das turmas de origem. */
	private Collection<Turma> turmasOrigem;
	
	/** Resultado da busca das turmas de destino. */
	private Collection<Turma> turmasDestino;

	/** MBean de unidades para busca de componentes. */
	private UnidadeMBean unidadeBean;
	
	/** Componentes listados para a sele��o. */
	private Collection<SelectItem> componentes;

	/** Marca se se a transfer�ncia � autom�tica ou manual. */
	private boolean automatica;
	
	// Campos utilizados para a transfer�ncia autom�tica
	/** Quantidade de alunos matriculados a serem transferidos. */
	private Integer qtdMatriculas;
	
	/** N�mero de solicita��es a transferir. */
	private Integer qtdSolicitacoes;

	// Cole��es utilizadas para a transfer�ncia manual
	/** Conjunto de matr�culas da turma de origem selecionada. */
	private Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
	
	/** Conjunto de solicita��es de matr�culas n�o analisadas para a turma de origem selecionada. */
	private Collection<SolicitacaoMatricula> solicitacoes = new ArrayList<SolicitacaoMatricula>();
	
	/** Ano e per�odo da turma de origem. */
	private Integer ano, periodo;
	
	/** Discente alvo da transfer�ncia. */
	private DiscenteAdapter discente;
	
	/**
	 * Utilizado pra quando quer fazer transfer�ncia entre turmas de unidade acad�mica especializada.
	 * Precisa persistir a unidade acad�mica especializada selecionada no primeiro combo (centro)
	 */
	private Unidade unidade = new Unidade();

	/** Armazena a lista de poss�veis turmas de destino para cada turma de origem selecionada. */
	private List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos = new ArrayList<TurmaOrigemTurmaDestinos>();
	
	/** Lista em dataModel das turmas de Origem e Destino para a transfer�ncia individual de turmas. */
	private ListDataModel turmasDataModel;
	
	/** Verificar se a turma destino pode ser de componente curricular diferente da turma de origem. */
	private boolean componenteDiferente;
	
	public TransferenciaTurmaMBean() {
		clear();
	}

	/**
	 * Limpa dados do MBean
	 */
	private void clear() {
		clearTurmaOrigem();
		turmaDestino = new Turma();
		turmaDestino.setDisciplina(new ComponenteCurricular());
		
		turmasOrigem = new ArrayList<Turma>();
		turmasDestino = new ArrayList<Turma>();
		
		componentes = new ArrayList<SelectItem>();
		qtdMatriculas = 0;
		qtdSolicitacoes = 0;
		
		matriculas = new ArrayList<MatriculaComponente>();
		solicitacoes = new ArrayList<SolicitacaoMatricula>();
		
		unidade = new Unidade();
		unidadeBean = getMBean("unidade");
		unidadeBean.setObj(new Unidade());
		unidadeBean.setUnidades(new ArrayList<SelectItem>());
		
		listTurmaOrigemDestinos = new ArrayList<TurmaOrigemTurmaDestinos>();
		
		componenteDiferente = false;
	}
	
	/**
	 * M�todo respons�vel por limpar o objeto da turma de origem.
	 * */
	private void clearTurmaOrigem() {
		turmaOrigem = new Turma();
		turmaOrigem.setDisciplina(new ComponenteCurricular());
	}

	/**
	 * M�todo respons�vel por setar os atributos iniciais e iniciar a opera��o de transfer�ncia de turmas. 
	 * @throws DAOException
	 */
	private void iniciar() throws DAOException {
		clear();
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
	}

	/**
	 * Inicia caso de uso de transfer�ncia autom�tica
	 * <br>
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/aluno.jsp</li> 
	 *  <li>/sigaa.war/graduacao/departamento.jsp</li> 
	 *  <li>/sigaa.war/portais/docente/menu_docente.jsp</li> 
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAutomatica() throws ArqException {
		checkRole(new int[] { SigaaPapeis.DAE }, SigaaPapeis.GESTOR_TURMAS_UNIDADE);

		setAutomatica(true);
		iniciar();

		// Verificar se a unidade dos componentes � restrita pelas permiss�es do usu�rio
		if ( isUnidadeRestrita() ) {
			// Validar per�odo de ajuste de turmas pelos departamentos
			if (!getCalendarioVigente().isPeriodoAjustesTurmas()){
				CalendarioAcademico cal = getCalendarioVigente();
				Formatador fmt = Formatador.getInstance();
				addMensagemErro(" O per�odo oficial para ajuste de turmas estende-se de " + 
						fmt.formatarData(cal.getInicioAjustesMatricula()) + " a " + fmt.formatarData(cal.getFimAjustesMatricula()) +
						" e de " + fmt.formatarData(cal.getInicioAjustesReMatricula()) + " a " + fmt.formatarData(cal.getFimAjustesReMatricula()) + ".");
				return null;
			}
			// Buscar os componentes oferecidos pela unidade do usu�rio
			ComponenteCurricularDao daoComponente = getDAO(ComponenteCurricularDao.class);

			if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) 
					&& getCursoAtualCoordenacao() != null )
				turmaOrigem.getDisciplina().setUnidade( getCursoAtualCoordenacao().getUnidadeCoordenacao() );
			else
				turmaOrigem.getDisciplina().setUnidade( getUsuarioLogado().getVinculoAtivo().getUnidade() );
			
			componentes = toSelectItems(daoComponente.findByUnidadeTurmaAnoSemestre(turmaOrigem.getDisciplina().getUnidade().getId(), getNivelEnsino(), ano, periodo), "id", "descricaoResumida");

		}

		prepareMovimento(SigaaListaComando.TRANSFERENCIA_AUTOMATICA);
		setOperacaoAtiva(SigaaListaComando.TRANSFERENCIA_AUTOMATICA.getId());
		return forward(JSP_TURMA_ORIGEM);
	}

	/**
	 * Verifica se a unidade dos componentes � restrita pelas permiss�es do usu�rio
	 * <br />
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/transferencia_turma/turma_origem.jsp</li> 
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isUnidadeRestrita() {
		return isUserInRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE);
	}
	
	/**
	 * Verifica se o usu�rio tem permiss�o de administrador
	 * <br />
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/transferencia_turma/turma_origem.jsp</li> 
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isAdministrador() {
		return isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE);
	}

	/**
	 * Inicia a transfer�ncia manual
	 * <br />
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/aluno.jsp</li> 
	 *  <li>/sigaa.war/graduacao/departamento.jsp</li> 
	 *  <li>/sigaa.war/portais/docente/menu_docente.jsp</li> 
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarManual() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		setAutomatica(false);
		iniciar();
		
		prepareMovimento(SigaaListaComando.TRANSFERENCIA_MANUAL);
		setOperacaoAtiva(SigaaListaComando.TRANSFERENCIA_MANUAL.getId());
		return forward(JSP_TURMA_ORIGEM);
	}
	
	/**
	 * Inicia caso de uso de transfer�ncia para os cursos de p�s-gradua��o lato sensu.
	 * <br>
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/lato/menu_coordenador.jsp</li> 
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarLatoSensu() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO);
		
		setAutomatica(false);
		
		prepararMovimento(false);
		
		iniciar();
		ano = null;
		periodo = null;
		
		ComponenteCurricularDao daoComponente = getDAO(ComponenteCurricularDao.class);
		componentes = toSelectItems(daoComponente.findByCursoLato(getCursoAtualCoordenacao().getId()), "id", "descricaoResumida");
		
		return forward(JSP_TURMA_ORIGEM);
	}
	
	/**
	 * Inicia caso de uso de transfer�ncia autom�tica para o m�dulo t�cnico.
	 * <br>
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li> 
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarTecnicoAutomatica() throws ArqException {
		return iniciarTecnico( true );
	}
	
	/**
	 * Inicia caso de uso de transfer�ncia manual para o m�dulo t�cnico.
	 * <br>
	 * M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li> 
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarTecnicoManual() throws ArqException {
		return iniciarTecnico( false );
	}
	
	/**
	 * Inicia caso de uso de transfer�ncia de turma para o m�dulo t�cnico.
	 * @param automatica
	 * @return
	 * @throws ArqException
	 */
	private String iniciarTecnico(boolean automatica) throws ArqException {
		checkRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		
		setAutomatica(automatica);
		
		prepararMovimento(automatica);
		
		iniciar();
		
		ComponenteCurricularDao daoComponente = getDAO(ComponenteCurricularDao.class);
		componentes = toSelectItems(daoComponente.findByUnidadeTurmaAnoSemestre(getUnidadeGestora(), getNivelEnsino(), ano, periodo), "id", "descricaoResumida");
		
		return forward(JSP_TURMA_ORIGEM);		
	}

	/**
	 * M�todo respons�vel por iniciar e preparar os movimentos da transfer�ncia de turmas para o processador.
	 * @param automatica
	 * @throws ArqException
	 */
	private void prepararMovimento(boolean automatica) throws ArqException {
		if (automatica) {
			prepareMovimento(SigaaListaComando.TRANSFERENCIA_AUTOMATICA);
			setOperacaoAtiva(SigaaListaComando.TRANSFERENCIA_AUTOMATICA.getId());
		} else {
			prepareMovimento(SigaaListaComando.TRANSFERENCIA_MANUAL);
			setOperacaoAtiva(SigaaListaComando.TRANSFERENCIA_MANUAL.getId());
		}
	}
	
	/**
	 * Carrega todos componentes curriculares da unidade selecionada
	 * <br />
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/transferencia_turmas/turma_origem.jsp</li>
	 * </ul>
	 * @param evt
	 */
	public void buscarComponentes(ValueChangeEvent evt) throws DAOException {
		ComponenteCurricularDao daoComponente = getDAO(ComponenteCurricularDao.class);

		Integer idUnidade = null;
		
		if ( !isUnidadeRestrita() ){
			ano = getParameterInt("buscaTurma:paramAno") != null ? getParameterInt("buscaTurma:paramAno") : ano;
			periodo = getParameterInt("buscaTurma:paramPeriodo") != null ? getParameterInt("buscaTurma:paramPeriodo") : periodo;
		}	
		
		if( evt != null ){
			idUnidade = (Integer) evt.getNewValue();
		}else{
			idUnidade = unidade.getId();
		}
		componentes = toSelectItems(daoComponente.findByUnidadeTurmaAnoSemestre(idUnidade, getNivelEnsino(), ano, periodo), "id", "descricaoResumida");
		
	}
	
	/**
	 * Buscar todas as turmas abertas para o componente curricular selecionado
	 * no ano-per�odo definido.
	 * <br />
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/transferencia_turmas/turma_origem.jsp</li>
	 * </ul>
	 *
	 * @param evt
	 */
	public void buscarTurmas(ActionEvent evt) throws DAOException, LimiteResultadosException {
		int idComponente = turmaOrigem.getDisciplina().getId();
		if (idComponente != 0) {
			TurmaDao turmaDao = getDAO(TurmaDao.class);
			turmasOrigem = turmaDao.findOrigemTransferencia(ano, periodo, idComponente);
			
			if (ValidatorUtil.isEmpty(turmasOrigem)) {
				addMensagemWarning("N�o foram encontradas turmas do componente selecionado para o per�odo corrente.");
			}
			
		} else {
			addMensagemErro("� necess�rio selecionar um componente curricular para realizar a busca de turmas");
		}

	}
	
	/**
	 * Buscar todas as turmas de destino abertas para o componente curricular selecionado
	 * no ano-per�odo definido.
	 * <br />
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/transferencia_turmas/turma_destino_outro_componente.jsp</li>
	 * </ul>
	 *
	 * @param evt
	 */
	public void buscarTurmasDestino(ActionEvent evt) throws DAOException, LimiteResultadosException {
		int idComponente = turmaDestino.getDisciplina().getId();
		if (idComponente != 0) {
			TurmaDao turmaDao = getDAO(TurmaDao.class);
			turmasDestino = new ArrayList<Turma>();
			turmasDestino = turmaDao.findOrigemTransferencia(ano, periodo, idComponente);
			
			if (ValidatorUtil.isEmpty(turmasDestino)) {
				addMensagemWarning("N�o foram encontradas turmas do componente selecionado para o per�odo corrente.");
			}
			
		} else {
			addMensagemErro("� necess�rio selecionar um componente curricular para realizar a busca de turmas");
		}

	}
	
	
	/**
	 * Seleciona a turma de origem, realiza as valida��es necess�rias
	 * e busca as turmas de destino candidatas.
	 * <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/transferencia_turmas/turma_origem.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String selecionarTurmaOrigem() {
		if (!checkOperacaoAtiva(getUltimoComando().getId())) {
			return cancelar();
		}
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		
		try {
			// Buscar dados da turma de origem
			turmaOrigem = turmaDao.findAndFetch(getParameterInt("id"), Turma.class, "disciplina");
			
			if (!automatica) {
				matriculas = turmaDao.findMatriculasByTurma(turmaOrigem.getId(), SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA);
				solicitacoes = solicitacaoDao.findByTurma(turmaOrigem.getId(), true, 
						SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA);
			}

			// Realizar valida��es
			erros = new ListaMensagens();
			TransferenciaTurmasValidator.validaTurmaOrigem(turmaOrigem, erros, false, isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE));
			componenteDiferente = false;
			if (hasErrors()){
				addMensagens(erros);
				return null;
			}
			
			// Buscar turmas de destino candidatas
			// Turmas de Lato Sensu podem sofrer transfer�ncia, mesmo sendo a dist�ncia, pois n�o possuem polo atrelado �s turmas deste n�vel.
			turmasDestino = turmaDao.findDestinosTransferencia(turmaOrigem, isUserInRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_LATO) && !isUnidadeRestrita(), 
																turmaOrigem.isLato() ? null : new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL) );
			if (turmasDestino == null || turmasDestino.isEmpty()) {
				addMensagemErro("N�o h� turmas de destino dispon�veis para efetuar a transfer�ncia.");
				return null;
			}			
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		return forward(JSP_TURMA_DESTINO);
	}

	/**
	 * Seleciona a turma de origem, realiza as valida��es necess�rias
	 * e busca as turmas de destino candidatas.
	 * <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/transferencia_turmas/turma_origem.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String selecionarTurmaOrigemParaOutroComponente() {
		if (!checkOperacaoAtiva(getUltimoComando().getId())) {
			return cancelar();
		}
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		
		try {
			// Buscar dados da turma de origem
			turmaOrigem = turmaDao.findAndFetch(getParameterInt("id"), Turma.class, "disciplina");
			
			if (!automatica) {
				matriculas = turmaDao.findMatriculasByTurma(turmaOrigem.getId(), SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA);
				solicitacoes = solicitacaoDao.findByTurma(turmaOrigem.getId(), true, 
						SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA);
			}

			// Realizar valida��es
			erros = new ListaMensagens();
			TransferenciaTurmasValidator.validaTurmaOrigem(turmaOrigem, erros, false, isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE));
			turmasDestino.clear();
			turmaDestino = new Turma();
			turmaDestino.setDisciplina(new ComponenteCurricular());
			componenteDiferente = true;
			if (hasErrors()){
				addMensagens(erros);
				return null;
			}
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		return forward(JSP_TURMA_DESTINO_OUTRO_COMPONENTE);
	}
	
	/**
	 * Seleciona a turma de destino, realiza as valida��es necess�rias 
	 * e redireciona para a tela de defini��o dos alunos a serem transferidos
	 * <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/transferencia_turmas/turma_destino.jsp</li>
	 * </ul>
	 * @return
	 */
	public String selecionarTurmaDestino() {
		if (!checkOperacaoAtiva(getUltimoComando().getId())) {
			return cancelar();
		}
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		
		try {
			// Buscar dados da turma de origem
			turmaDestino = turmaDao.findByPrimaryKey(getParameterInt("id"), Turma.class);
			
			erros = new ListaMensagens();
			TransferenciaTurmasValidator.validaTurmaDestino(turmaOrigem, turmaDestino, erros, false, (isAdministrador() && componenteDiferente));
			if (hasErrors()){
				addMensagens(erros);
				return null;
			}

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		return forward(JSP_ALUNOS);
	}
	
	/**
	 * String que armazena a p�gina de destino da opera��o voltar turma de origem.
	 * <br>
	 * Chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/transferencia_turmas/turma_destino.jsp</li></ul>
	 * @return
	 */
	public String voltarTurmaOrigem() {
		return forward(JSP_TURMA_ORIGEM);
	}

	/**
	 * String que armazena a p�gina de destino da opera��o voltar turma de destino.
	 * <br>
	 * Chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/transferencia_turmas/alunos.jsp</li></ul>
	 * @return
	 */
	public String voltarTurmaDestino() {
		if (isAdministrador() && componenteDiferente) 
			return forward(JSP_TURMA_DESTINO_OUTRO_COMPONENTE);
		else 
			return forward(JSP_TURMA_DESTINO);
	}

	/**
	 * Chama o processador para efetivar a transfer�ncia entre turmas
	 * <br>
	 * Chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/transferencia_turmas/alunos.jsp</li></ul>
	 * @return
	 * @throws ArqException 
	 */
	public String efetuarTransferencia() throws ArqException {
		if (!checkOperacaoAtiva(getUltimoComando().getId())) {
			return cancelar();
		}

		Collection<MatriculaComponente> matriculas = carregaMatriculasSelecionadas();
		Collection<SolicitacaoMatricula> solicitacoes = carregaSolicitacoesSelecionadas();
		
		// Valida��o dos dados do formul�rio
		if (isAutomatica()) {
			if (qtdMatriculas == null) {
				qtdMatriculas = 0;
			}
			if (qtdSolicitacoes == null) {
				qtdSolicitacoes = 0;
			}
		}
		
		erros = new ListaMensagens();
		TransferenciaTurmasValidator.validaAlunos(isAutomatica(), 
				qtdMatriculas, qtdSolicitacoes,
				turmaOrigem, turmaDestino, matriculas, solicitacoes, erros);
		if (hasErrors())
			return null;

		// Cria��o do movimento
		MovimentoTransferenciaTurmas mov = new MovimentoTransferenciaTurmas();

		if (isAutomatica()) {
			mov.setCodMovimento(SigaaListaComando.TRANSFERENCIA_AUTOMATICA);
			mov.setQtdMatriculas(qtdMatriculas);
			mov.setQtdSolicitacoes(qtdSolicitacoes);
		} else {
			mov.setCodMovimento(SigaaListaComando.TRANSFERENCIA_MANUAL);
			mov.setMatriculas( matriculas);
			mov.setSolicitacoes(solicitacoes);
		}
		mov.setTurmaOrigem(turmaOrigem);
		mov.setTurmaDestino(turmaDestino);
		mov.setAdministrador(isAdministrador() && componenteDiferente);

		// Chamar processador
		List<Discente> discentes = null;
		try {
			discentes  = execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		if (discentes == null || discentes.isEmpty()) {
			addMensagemErro("Aten��o! N�o foi poss�vel transferir nenhum discente.");
			prepararMovimento(isAutomatica());
			return null;
		}
		
		// Preparar dados para o comprovante
		TurmaDao turmaDao = getDAO( TurmaDao.class );
		SolicitacaoMatriculaDao solicitacaoDao =  getDAO( SolicitacaoMatriculaDao.class );
		
		turmaOrigem.setQtdEspera( solicitacaoDao.countByTurma(turmaOrigem, SolicitacaoMatricula.getStatusSolicitacoesPendentes(), true ));
		turmaOrigem.setQtdMatriculados(turmaDao.findQtdAlunosPorTurma( turmaOrigem.getId(), SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA, 
				SituacaoMatricula.TRANCADO, SituacaoMatricula.APROVADO, SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_FALTA));
		turmaDestino.setQtdEspera( solicitacaoDao.countByTurma(turmaDestino, SolicitacaoMatricula.getStatusSolicitacoesPendentes(), true ));
		turmaDestino.setQtdMatriculados(turmaDao.findQtdAlunosPorTurma( turmaDestino.getId(), SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA,
				SituacaoMatricula.TRANCADO, SituacaoMatricula.APROVADO, SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_FALTA));
		
		getCurrentRequest().setAttribute("turmaOrigem", turmaOrigem);
		getCurrentRequest().setAttribute("turmaDestino", turmaDestino);
		getCurrentRequest().setAttribute("discentes", discentes);
		addMessage("Transfer�ncia realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		addMensagemWarning(" Aten��o! Somente os discentes que n�o possu�am choque de hor�rio com a turma de destino foram transferidos! ");
		
		
		removeOperacaoAtiva();
		return forward(JSP_COMPROVANTE);
	}

	/**
	 * M�todo respons�vel por carregar e setar numa cole��o de matriculas, as que foram selecionadas pelo usu�rio.
	 * @return
	 */
	private Collection<MatriculaComponente> carregaMatriculasSelecionadas() {
		Collection<MatriculaComponente> resultados = new HashSet<MatriculaComponente>();
		if (matriculas == null) return resultados;
		
		for (MatriculaComponente matricula : matriculas) {
			if (matricula.isSelected())
				resultados.add(matricula);
		}
		return resultados;
	}
	
	/**
	 * M�todo respons�vel por carregar e setar numa cole��o de solicita��es de matricula, as que foram selecionadas pelo usu�rio.
	 * @return
	 */
	
	private Collection<SolicitacaoMatricula> carregaSolicitacoesSelecionadas() {
		Collection<SolicitacaoMatricula> resultados = new HashSet<SolicitacaoMatricula>();
		if (solicitacoes == null) return resultados;
		
		for (SolicitacaoMatricula solicitacao : solicitacoes) {
			if (solicitacao.isSelected())
				resultados.add(solicitacao);
		}
		return resultados;
	}

	/**
	 * M�todo respons�vel pelo controle da sele��o de item da comboBox dos Centros da Institui��o, 
	 * onde a altera��o deste acarreta o carregamento e atualiza��o da listagem de departamentos
	 * exibidos na comboBox de departamento da JSP.
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul><li>
	 * 	/sigaa.war/graduacao/transferencia_turmas/turma_origem.jsp
	 * </li></ul>
	 * @param e
	 * @throws DAOException
	 */
	public void changeCentro(ValueChangeEvent e) throws DAOException{
		Integer id = (Integer) e.getNewValue();
		unidade = getGenericDAO().findByPrimaryKey(id, Unidade.class);
		if( unidade.isCentro() ){
			unidadeBean.changeCentro(e);
			componentes = new ArrayList<SelectItem>();
			turmaOrigem.getDisciplina().getUnidade().setId(0);
		}else{
			buscarComponentes(null);
		}
	}
	
	/**
	 * M�todo respons�vel pelo controle pela altera��o de ano e per�odo, 
	 * onde a altera��o deste acarreta o carregamento e atualiza��o da listagem de departamentos e componentes
	 * exibidos nas comboBox de departamento e componente da JSP.
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul><li>
	 * 	/sigaa.war/graduacao/transferencia_turmas/turma_origem.jsp
	 * </li></ul>
	 * @param e
	 * @throws DAOException
	 */
	public void changeAnoPeriodo(ValueChangeEvent e) throws DAOException{
		
			unidadeBean.setId();
			componentes = new ArrayList<SelectItem>();
			turmaOrigem.getDisciplina().getUnidade().setId(0);
		
	}
	
	/** Inicia a opera��o de altera��o de status do discente.<br />
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *  <li>sigaa.war/ensino/tecnico/menu/discente.jsp</li>
	 *  </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String buscarDiscente() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);                 
		prepareMovimento(SigaaListaComando.TRANSFERENCIA_TURMAS_ALUNO);
		setOperacaoAtiva(SigaaListaComando.TRANSFERENCIA_TURMAS_ALUNO.getId());

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao( OperacaoDiscente.TRANSFERENCIA_TURMAS_ALUNO );
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * M�todo respons�vel por listar as informa��es do discente selecionado na busca de discente, assim como, 
	 * listar as turmas matriculadas pelo mesmo junto das turmas sugestivas para transfer�ncia no semestre atual. 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul>
	 */
	public String selecionaDiscente() throws DAOException{
		if (!checkOperacaoAtiva(getUltimoComando().getId())) {
			return cancelar();
		}
		clear();
		
		TurmaDao dao = getDAO(TurmaDao.class);
		
		turmasOrigem = dao.findAllByDiscente( discente.getDiscente(), SituacaoMatricula.getSituacoesMatriculadas().toArray(new SituacaoMatricula[0]), new SituacaoTurma[] { new SituacaoTurma(SituacaoTurma.CONSOLIDADA), new SituacaoTurma(SituacaoTurma.ABERTA), new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE) });
		
		TurmaOrigemTurmaDestinos tOrigemDestinos = new TurmaOrigemTurmaDestinos();
		
		for (Turma t : turmasOrigem) {
			t = dao.findAndFetch(t.getId(), Turma.class, "disciplina");
			tOrigemDestinos.setTurma(t);
			
			tOrigemDestinos.setListTurmasDestino((List<Turma>) dao.findDestinosTransferencia(t, false, new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL)));
			
			tOrigemDestinos.setSelectTurmasDestino(toSelectItems(tOrigemDestinos.getListTurmasDestino(), "id", "DescricaoNivelTecnico"));
			
			listTurmaOrigemDestinos.add(tOrigemDestinos);
			
			tOrigemDestinos = new TurmaOrigemTurmaDestinos();
		}
		
		return forward(JSP_TRANSFERENCIA_TURMAS);
	}
	
	/**
	 * M�todo respons�vel por realizar a consolida��o da transfer�ncia das turmas do discente.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>/sigaa.war/graduacao/transferencia_turmas/transferencia_turmas_discente.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String transferirTurmasDiscente() throws ArqException{
		
		if (!checkOperacaoAtiva(getUltimoComando().getId())) {
			return cancelar();
		}
		
		String localParam = "table_turma:tableTurmas:";
		String idParamOrigem = ":idTurmaOrigem";
		String idParamDestino = ":idTurmaDestino";
		int idTurmaOrigem;
		int idTurmaDestino;
		MatriculaComponente matricula;
		boolean existeDestino = false;
		List<Integer> idsTurmasOrigem = new ArrayList<Integer>();
		List<Integer> idsTurmasDestino = new ArrayList<Integer>();
		
		List<Turma> turmasOrigem;
		List<Turma> turmasDestino;
		List<MatriculaComponente> matriculas;

		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		TransferenciaTurmaDao transferenciaDao = getDAO(TransferenciaTurmaDao.class);
			
		/* Preenchimento da lista de turmas de Origem com suas respectivas turmas destinos da transfer�ncia. */
		for (int i = 0; i < listTurmaOrigemDestinos.size(); i++) {
		
			idTurmaOrigem = getParameterInt(localParam + i + idParamOrigem) != null ? getParameterInt(localParam + i + idParamOrigem) : 0;
			idTurmaDestino = getParameterInt(localParam + i + idParamDestino) != null ? getParameterInt(localParam + i + idParamDestino) : 0;
			
			
			if(idTurmaOrigem != idTurmaDestino && idTurmaDestino != 0){
				existeDestino = true;
				idsTurmasOrigem.add(idTurmaOrigem);
				idsTurmasDestino.add(idTurmaDestino);
			}
		}
		
		//Lista para armazenar apenas as turmas que ser�o transferidas.
		List<TurmaOrigemTurmaDestinos> turmasTransferencia = new ArrayList<TurmaOrigemTurmaDestinos>();
		
		if(existeDestino) {
			turmasOrigem = transferenciaDao.findTurmasTransferenciaByIds(idsTurmasOrigem.toArray());
			turmasDestino = transferenciaDao.findTurmasTransferenciaByIds(idsTurmasDestino.toArray());
			matriculas = matriculaDao.findMatriculadosByDiscenteTurmas(discente, idsTurmasOrigem.toArray());
			
			for(int i = 0; i < turmasOrigem.size();i++) {
				turmaOrigem = turmasOrigem.get(i);
				turmaDestino = turmasDestino.get(i);
				matricula = matriculas.get(i);
				matricula.setDiscente(discente);
				
				TurmaOrigemTurmaDestinos transferir = new TurmaOrigemTurmaDestinos();
				
				transferir.setTurma(turmaOrigem);
				transferir.setTurmaDestino(turmaDestino);
				transferir.setMatricula(matricula);
				
				listTurmaOrigemDestinos.get(getPosicaoListTurmaOrigemDestinos(transferir)).setTurma(turmaOrigem);
				listTurmaOrigemDestinos.get(getPosicaoListTurmaOrigemDestinos(transferir)).setTurmaDestino(turmaDestino);
				listTurmaOrigemDestinos.get(getPosicaoListTurmaOrigemDestinos(transferir)).setMatricula(matricula);
				
				erros = new ListaMensagens();	
				
				/* Validar ser existe choque de hor�rio do discente com a turma destino*/
				if(!discente.getCurso().getModalidadeEducacao().isSemiPresencial() &&
						HorarioTurmaUtil.hasChoqueHorarios(turmaDestino, discente, turmaOrigem)){
					
					erros.addErro("Aten��o! N�o foi poss�vel transferir o discente para a turma '"+ turmaDestino.getDescricaoSemDocente() +"' devido a choque de hor�rios com outras turmas matriculadas.");
					
				}
				/* Valida a situa��o das turmas de Origem e Destino*/
				TransferenciaTurmasValidator.validaTurmaOrigem(turmaOrigem, erros, true, isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE));
				TransferenciaTurmasValidator.validaTurmaDestino(turmaOrigem, turmaDestino, erros, true, isAdministrador());
				
				if (hasErrors()) {
					addMensagens(erros);
					return null;
				}
				
				turmasTransferencia.add(transferir);
			}
					
			if(!hasErrors()){
				// Cria��o do movimento
				prepareMovimento(SigaaListaComando.TRANSFERENCIA_TURMAS_ALUNO);
				setOperacaoAtiva(SigaaListaComando.TRANSFERENCIA_TURMAS_ALUNO.getId());
				
				MovimentoTransferenciaTurmas mov = new MovimentoTransferenciaTurmas();
	
				mov.setCodMovimento(SigaaListaComando.TRANSFERENCIA_TURMAS_ALUNO);
				mov.setListTurmaOrigemDestinos(turmasTransferencia);
				mov.setTransfTurmasByDiscente(true);
				mov.setAdministrador(isAdministrador());
				
				try {
					execute(mov);
				} catch (NegocioException e) {
					e.printStackTrace();
					addMensagens(e.getListaMensagens());
					return null;
				}
					
				addMessage("Transfer�ncia(s) de turma(s) do aluno "+discente.getNome()+" realizada(s) com sucesso!", TipoMensagemUFRN.INFORMATION);
				
				removeOperacaoAtiva();
				return forward(JSP_COMPROVANTE_DISCENTE);
			}
		}
		return null;
	}
	
	/**
	 * Retorna a posi��o do objeto passado dentro da listagem de {@link TurmaOrigemTurmaDestinos}.
	 * 
	 * @param transferir
	 * @return
	 */
	private int getPosicaoListTurmaOrigemDestinos(TurmaOrigemTurmaDestinos transferir) {
		for(int i = 0;i < listTurmaOrigemDestinos.size();i++) {
			if(listTurmaOrigemDestinos.get(i).getTurma().equals(transferir.getTurma()) &&
					listTurmaOrigemDestinos.get(i).getTurmaDestino().equals(transferir.getTurmaDestino()))
				return i;
		}
		return -1;
	}

	public String getDescricaoTipo() {
		return isAutomatica() ? "(Autom�tica) " : "(Manual) ";
	}
	


	
	// Getters e Setters

	public Turma getTurmaDestino() {
		return turmaDestino;
	}

	public void setTurmaDestino(Turma turmaDestino) {
		this.turmaDestino = turmaDestino;
	}

	public Turma getTurmaOrigem() {
		return turmaOrigem;
	}

	public void setTurmaOrigem(Turma turmaOrigem) {
		this.turmaOrigem = turmaOrigem;
	}

	public boolean isAutomatica() {
		return automatica;
	}

	public void setAutomatica(boolean automatica) {
		this.automatica = automatica;
	}

	public Collection<SelectItem> getComponentes() {
		return componentes;
	}

	public void setComponentes(Collection<SelectItem> componentes) {
		this.componentes = componentes;
	}

	public Integer getQtdMatriculas() {
		return qtdMatriculas;
	}

	public void setQtdMatriculas(Integer qtdMatriculas) {
		this.qtdMatriculas = qtdMatriculas;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Collection<Turma> getTurmasOrigem() {
		return this.turmasOrigem;
	}

	public void setTurmasOrigem(Collection<Turma> turmasOrigem) {
		this.turmasOrigem = turmasOrigem;
	}

	public Collection<Turma> getTurmasDestino() {
		return this.turmasDestino;
	}

	public void setTurmasDestino(Collection<Turma> turmasDestino) {
		this.turmasDestino = turmasDestino;
	}

	public Integer getQtdSolicitacoes() {
		return this.qtdSolicitacoes;
	}

	public void setQtdSolicitacoes(Integer qtdSolicitacoes) {
		this.qtdSolicitacoes = qtdSolicitacoes;
	}

	public Collection<MatriculaComponente> getMatriculas() {
		return this.matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoes() {
		return this.solicitacoes;
	}

	public void setSolicitacoes(Collection<SolicitacaoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}
	
	/** 
	 * Seta o discente que ter� as turmas transferidas. Invocado pelo MBean de busca de discente.
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}
	
	/** 
	 * Retorna o dataModel da lista das matr�culas em componente curricular que ser� implantada no hist�rico.
	 */
	public ListDataModel getTurmasDataModel() {
		this.turmasDataModel = new ListDataModel(listTurmaOrigemDestinos);
		return turmasDataModel;
	}

	public void setTurmasDataModel(ListDataModel turmasDataModel) {
		this.turmasDataModel = turmasDataModel;
	}

	public boolean isComponenteDiferente() {
		return componenteDiferente;
	}

	public void setComponenteDiferente(boolean componenteDiferente) {
		this.componenteDiferente = componenteDiferente;
	}

	public List<TurmaOrigemTurmaDestinos> getListTurmaOrigemDestinos() {
		return listTurmaOrigemDestinos;
	}

	public void setListTurmaOrigemDestinos(
			List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos) {
		this.listTurmaOrigemDestinos = listTurmaOrigemDestinos;
	}

}