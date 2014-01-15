/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Managed Bean para tratar da transferência de alunos entre turmas
 *
 * @author Leonardo Campos
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
@Component("transferenciaTurma")
@Scope("session")
public class TransferenciaTurmaMBean extends SigaaAbstractController<Object> implements OperadorDiscente {

	//Definições das Views
	/** JSP de escolha da turma de origem da transferência. */
	public static final String JSP_TURMA_ORIGEM = "/graduacao/transferencia_turmas/turma_origem.jsp";
	/** JSP de escolha da turma de destino da transferência. */
	public static final String JSP_TURMA_DESTINO = "/graduacao/transferencia_turmas/turma_destino.jsp";
	/** JSP de escolha da turma de destino da transferência. */
	public static final String JSP_TURMA_DESTINO_OUTRO_COMPONENTE = "/graduacao/transferencia_turmas/turma_destino_outro_componente.jsp";
	/** JSP de escolha de alunos que serão transferidos. */
	public static final String JSP_ALUNOS = "/graduacao/transferencia_turmas/alunos.jsp";
	/** Comprovante da transferência entre turmas. */
	public static final String JSP_COMPROVANTE = "/graduacao/transferencia_turmas/comprovante.jsp";
	/** Comprovante de transferência das turmas do discente. */
	public static final String JSP_COMPROVANTE_DISCENTE = "/graduacao/transferencia_turmas/comprovante_discente.jsp";
	/** JSP com informações do discente e possíveis turmas para transferência. */
	public static final String JSP_TRANSFERENCIA_TURMAS = "/graduacao/transferencia_turmas/transferencia_turmas_discente.jsp";
	/**Indica se a estratégia de transferência automática de discente se seguirá a ordem de prioridade da matrícula. */
	public static final String PRIORIDADE_MATRICULA = "PRIO";
	/**Indica se a estratégia de transferência automática de discente será de forma aleatória. */ 
	public static final String RANDOMICA = "RAND";

	
	/** Turma de origem da transferência. */
	private Turma turmaOrigem;
	
	/** Turma destino da transferência. */
	private Turma turmaDestino;

	/** Resultado da busca das turmas de origem. */
	private Collection<Turma> turmasOrigem;
	
	/** Resultado da busca das turmas de destino. */
	private Collection<Turma> turmasDestino;

	/** MBean de unidades para busca de componentes. */
	private UnidadeMBean unidadeBean;
	
	/** Componentes listados para a seleção. */
	private Collection<SelectItem> componentes;

	/** Marca se se a transferência é automática ou manual. */
	private boolean automatica;
	
	// Campos utilizados para a transferência automática
	/** Quantidade de alunos matriculados a serem transferidos. */
	private Integer qtdMatriculas;
	
	/** Número de solicitações a transferir. */
	private Integer qtdSolicitacoes;

	// Coleções utilizadas para a transferência manual
	/** Conjunto de matrículas da turma de origem selecionada. */
	private Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
	
	/** Conjunto de solicitações de matrículas não analisadas para a turma de origem selecionada. */
	private Collection<SolicitacaoMatricula> solicitacoes = new ArrayList<SolicitacaoMatricula>();
	
	/** Ano e período da turma de origem. */
	private Integer ano, periodo;
	
	/** Discente alvo da transferência. */
	private DiscenteAdapter discente;
	
	/**
	 * Utilizado pra quando quer fazer transferência entre turmas de unidade acadêmica especializada.
	 * Precisa persistir a unidade acadêmica especializada selecionada no primeiro combo (centro)
	 */
	private Unidade unidade = new Unidade();

	/** Armazena a lista de possíveis turmas de destino para cada turma de origem selecionada. */
	private List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos = new ArrayList<TurmaOrigemTurmaDestinos>();
	
	/** Lista em dataModel das turmas de Origem e Destino para a transferência individual de turmas. */
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
	 * Método responsável por limpar o objeto da turma de origem.
	 * */
	private void clearTurmaOrigem() {
		turmaOrigem = new Turma();
		turmaOrigem.setDisciplina(new ComponenteCurricular());
	}

	/**
	 * Método responsável por setar os atributos iniciais e iniciar a operação de transferência de turmas. 
	 * @throws DAOException
	 */
	private void iniciar() throws DAOException {
		clear();
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
	}

	/**
	 * Inicia caso de uso de transferência automática
	 * <br>
	 * Método chamado pela(s) JSP(s):
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

		// Verificar se a unidade dos componentes é restrita pelas permissões do usuário
		if ( isUnidadeRestrita() ) {
			// Validar período de ajuste de turmas pelos departamentos
			if (!getCalendarioVigente().isPeriodoAjustesTurmas()){
				CalendarioAcademico cal = getCalendarioVigente();
				Formatador fmt = Formatador.getInstance();
				addMensagemErro(" O período oficial para ajuste de turmas estende-se de " + 
						fmt.formatarData(cal.getInicioAjustesMatricula()) + " a " + fmt.formatarData(cal.getFimAjustesMatricula()) +
						" e de " + fmt.formatarData(cal.getInicioAjustesReMatricula()) + " a " + fmt.formatarData(cal.getFimAjustesReMatricula()) + ".");
				return null;
			}
			// Buscar os componentes oferecidos pela unidade do usuário
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
	 * Verifica se a unidade dos componentes é restrita pelas permissões do usuário
	 * <br />
	 * Método chamado pela(s) JSP(s):
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
	 * Verifica se o usuário tem permissão de administrador
	 * <br />
	 * Método chamado pela(s) JSP(s):
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
	 * Inicia a transferência manual
	 * <br />
	 * Método chamado pela(s) JSP(s):
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
	 * Inicia caso de uso de transferência para os cursos de pós-graduação lato sensu.
	 * <br>
	 * Método chamado pela(s) JSP(s):
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
	 * Inicia caso de uso de transferência automática para o módulo técnico.
	 * <br>
	 * Método chamado pela(s) JSP(s):
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
	 * Inicia caso de uso de transferência manual para o módulo técnico.
	 * <br>
	 * Método chamado pela(s) JSP(s):
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
	 * Inicia caso de uso de transferência de turma para o módulo técnico.
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
	 * Método responsável por iniciar e preparar os movimentos da transferência de turmas para o processador.
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
	 * no ano-período definido.
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
				addMensagemWarning("Não foram encontradas turmas do componente selecionado para o período corrente.");
			}
			
		} else {
			addMensagemErro("É necessário selecionar um componente curricular para realizar a busca de turmas");
		}

	}
	
	/**
	 * Buscar todas as turmas de destino abertas para o componente curricular selecionado
	 * no ano-período definido.
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
				addMensagemWarning("Não foram encontradas turmas do componente selecionado para o período corrente.");
			}
			
		} else {
			addMensagemErro("É necessário selecionar um componente curricular para realizar a busca de turmas");
		}

	}
	
	
	/**
	 * Seleciona a turma de origem, realiza as validações necessárias
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

			// Realizar validações
			erros = new ListaMensagens();
			TransferenciaTurmasValidator.validaTurmaOrigem(turmaOrigem, erros, false, isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE));
			componenteDiferente = false;
			if (hasErrors()){
				addMensagens(erros);
				return null;
			}
			
			// Buscar turmas de destino candidatas
			// Turmas de Lato Sensu podem sofrer transferência, mesmo sendo a distância, pois não possuem polo atrelado às turmas deste nível.
			turmasDestino = turmaDao.findDestinosTransferencia(turmaOrigem, isUserInRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_LATO) && !isUnidadeRestrita(), 
																turmaOrigem.isLato() ? null : new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL) );
			if (turmasDestino == null || turmasDestino.isEmpty()) {
				addMensagemErro("Não há turmas de destino disponíveis para efetuar a transferência.");
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
	 * Seleciona a turma de origem, realiza as validações necessárias
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

			// Realizar validações
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
	 * Seleciona a turma de destino, realiza as validações necessárias 
	 * e redireciona para a tela de definição dos alunos a serem transferidos
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
	 * String que armazena a página de destino da operação voltar turma de origem.
	 * <br>
	 * Chamado pela JSP:
	 * <ul><li>/sigaa.war/graduacao/transferencia_turmas/turma_destino.jsp</li></ul>
	 * @return
	 */
	public String voltarTurmaOrigem() {
		return forward(JSP_TURMA_ORIGEM);
	}

	/**
	 * String que armazena a página de destino da operação voltar turma de destino.
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
	 * Chama o processador para efetivar a transferência entre turmas
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
		
		// Validação dos dados do formulário
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

		// Criação do movimento
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
			addMensagemErro("Atenção! Não foi possível transferir nenhum discente.");
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
		addMessage("Transferência realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		addMensagemWarning(" Atenção! Somente os discentes que não possuíam choque de horário com a turma de destino foram transferidos! ");
		
		
		removeOperacaoAtiva();
		return forward(JSP_COMPROVANTE);
	}

	/**
	 * Método responsável por carregar e setar numa coleção de matriculas, as que foram selecionadas pelo usuário.
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
	 * Método responsável por carregar e setar numa coleção de solicitações de matricula, as que foram selecionadas pelo usuário.
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
	 * Método responsável pelo controle da seleção de item da comboBox dos Centros da Instituição, 
	 * onde a alteração deste acarreta o carregamento e atualização da listagem de departamentos
	 * exibidos na comboBox de departamento da JSP.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método responsável pelo controle pela alteração de ano e período, 
	 * onde a alteração deste acarreta o carregamento e atualização da listagem de departamentos e componentes
	 * exibidos nas comboBox de departamento e componente da JSP.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Inicia a operação de alteração de status do discente.<br />
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método responsável por listar as informações do discente selecionado na busca de discente, assim como, 
	 * listar as turmas matriculadas pelo mesmo junto das turmas sugestivas para transferência no semestre atual. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método responsável por realizar a consolidação da transferência das turmas do discente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			
		/* Preenchimento da lista de turmas de Origem com suas respectivas turmas destinos da transferência. */
		for (int i = 0; i < listTurmaOrigemDestinos.size(); i++) {
		
			idTurmaOrigem = getParameterInt(localParam + i + idParamOrigem) != null ? getParameterInt(localParam + i + idParamOrigem) : 0;
			idTurmaDestino = getParameterInt(localParam + i + idParamDestino) != null ? getParameterInt(localParam + i + idParamDestino) : 0;
			
			
			if(idTurmaOrigem != idTurmaDestino && idTurmaDestino != 0){
				existeDestino = true;
				idsTurmasOrigem.add(idTurmaOrigem);
				idsTurmasDestino.add(idTurmaDestino);
			}
		}
		
		//Lista para armazenar apenas as turmas que serão transferidas.
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
				
				/* Validar ser existe choque de horário do discente com a turma destino*/
				if(!discente.getCurso().getModalidadeEducacao().isSemiPresencial() &&
						HorarioTurmaUtil.hasChoqueHorarios(turmaDestino, discente, turmaOrigem)){
					
					erros.addErro("Atenção! Não foi possível transferir o discente para a turma '"+ turmaDestino.getDescricaoSemDocente() +"' devido a choque de horários com outras turmas matriculadas.");
					
				}
				/* Valida a situação das turmas de Origem e Destino*/
				TransferenciaTurmasValidator.validaTurmaOrigem(turmaOrigem, erros, true, isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE));
				TransferenciaTurmasValidator.validaTurmaDestino(turmaOrigem, turmaDestino, erros, true, isAdministrador());
				
				if (hasErrors()) {
					addMensagens(erros);
					return null;
				}
				
				turmasTransferencia.add(transferir);
			}
					
			if(!hasErrors()){
				// Criação do movimento
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
					
				addMessage("Transferência(s) de turma(s) do aluno "+discente.getNome()+" realizada(s) com sucesso!", TipoMensagemUFRN.INFORMATION);
				
				removeOperacaoAtiva();
				return forward(JSP_COMPROVANTE_DISCENTE);
			}
		}
		return null;
	}
	
	/**
	 * Retorna a posição do objeto passado dentro da listagem de {@link TurmaOrigemTurmaDestinos}.
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
		return isAutomatica() ? "(Automática) " : "(Manual) ";
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
	 * Seta o discente que terá as turmas transferidas. Invocado pelo MBean de busca de discente.
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}
	
	/** 
	 * Retorna o dataModel da lista das matrículas em componente curricular que será implantada no histórico.
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