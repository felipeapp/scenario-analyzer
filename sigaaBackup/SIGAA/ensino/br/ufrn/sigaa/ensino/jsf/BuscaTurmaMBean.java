/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/03/2008
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.MovimentoCadastro;
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
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dao.FecharTurmaDao;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.TurmaGraduacaoMBean;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoUtil;
import br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator;
import br.ufrn.sigaa.parametros.dominio.ParametrosMedio;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * MBean de operações de buscas a qualquer nível de ensino de uma turma.
 *
 * @author Andre M Dantas
 *
 */
@Component("buscaTurmaBean")
@Scope("session")
public class BuscaTurmaMBean extends SigaaAbstractController<Turma> {
	
	// Opções de ordenação de turmas
	/** Indica se a ordenação dos dados deve ser feita por componente curricular. */
	public static final int ORDENAR_POR_COMPONENTE_CURRICULAR = 1;
	
	/** Indica se a ordenação dos dados deve ser feita de acordo com os horários da turma. */
	public static final int ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS = 2;
	
	/** Indica se a ordenação dos dados deve ser feita pelo nome do docente, da disciplina e descrição dos horários, nessa ordem de preferência. */
	public static final int ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS = 3;
	
	/** Indica se a ordenação dos dados deve ser feita pelo local. */
	public static final int ORDENAR_POR_LOCAL = 4;
	
	
	/** Lista de Matriculados */
	private Collection<MatriculaComponente> matriculados;

	/** Lista de Solicitações de matrículas */
	private Collection<SolicitacaoMatricula> solicitantes;

	/** Turma Escolhida pelo usuário. */
	private Turma turmaEscolhida;

	/** Indica se busca de lato sensu com docentes externos. */
	private boolean docenteExterno = false;

	// Campos usados na busca 
	/** Nível de ensino da turma utilizado para restringir o resultado da busca. */
	private Character nivelTurma;
	/** Código da turma utilizado para restringir o resultado da busca. */
	private String codigoTurma;
	/** Código da disciplina utilizado para restringir o resultado da busca. */
	private String codigoDisciplina;
	/** Nome da disciplina utilizado para restringir o resultado da busca. */
	private String nomeDisciplina;
	/** Nível de ensino da turma utilizado para restringir o resultado da busca. */
	private String nomeDocente;
	/** Situação da turma utilizada para restringir o resultado da busca. */
	private SituacaoTurma situacaoTurma;
	/** Ano da turma utilizada para restringir o resultado da busca. */
	private Integer anoTurma;
	/** Período da turma para restringir o resultado da busca. */
	private Integer periodoTurma;
	/** Curso utilizado para restringir o resultado da busca. */
	private Curso curso;
	/** Unidade utilizado para restringir o resultado da busca. */
	private Unidade unidade;
	/** Local de aulas utilizado para restringir o resultado da busca. */
	private String local;
	/** Tipo da turma utilizado para restringir o resultado da busca. */
	private Integer tipoTurma;
	/** Horário da turma utilizado para restringir o resultado da busca. */
	private String turmaHorario;
	/** Opção de ordenação escolhido */
	private Integer ordenarPor;

	// Filtros usados na busca 
	/** Indica se filtra a busca por nível de ensino. */
	private boolean filtroNivel;
	/** Indica se filtra a busca por unidade. */
	private boolean filtroUnidade;
	/** Indica se filtra a busca por código da turma. */
	private boolean filtroCodigoTurma;
	/** Indica se filtra a busca por código da disciplina. */
	private boolean filtroCodigo;
	/** Indica se filtra a busca por nome da disciplina*/
	private boolean filtroDisciplina;
	/** Indica se filtra a busca por nome do docente. */
	private boolean filtroDocente;
	/** Indica se filtra a busca por situação da turma.  */
	private boolean filtroSituacao;
	/** Indica se filtra a busca por pólo de ensino. */
	private boolean filtroPolo;
	/** Indica se filtra a busca por ano e período da turma. */
	private boolean filtroAnoPeriodo;
	/** Indica se filtra a busca por tipo de convênio. */
	private boolean filtroConvenio;
	/** Indica se filtra a busca por curso. */
	private boolean filtroCurso;
	/** Indica que o resultado da busca será gerado no formato de relatório. */
	private boolean filtroRelatorio;
	/** Indica se filtra a busca por local de aulas. */
	private boolean filtroLocal;
	/** Indica se filtra a busca por tipo tipo de turma. */
	private boolean filtroTipo;
	/** Indica se filtra a busca por horário. */
	private boolean filtroHorario;
	/** Indica se é gerado o diário da turma. */
	private boolean filtroDiarioTurma;		
	/** Indica se a busca é realizada pela SEDIS */
	private Polo polo;
	/** Indica se a busca vai ser ordenada por algum critério informado. */
	private boolean ordenarBusca;
	
	/** Nível de ensino do gestor técnico, do DAE, ou PPG. */
	private char nivelGestor;
	
	/** Unidade do gestor de curso técnico. */
	private int unidadeGestor;
	
	/**
	 * Coleção de selectItens com os tipos de turma que podem ser criados para
	 * popular o select no formulário quando a turma estiver sendo criada sem
	 * ser a partir de uma solicitação
	 */
	private Collection<SelectItem> tiposTurmaCombo = new ArrayList<SelectItem>();

	/** Calendário acadêmico utilizado na análise das solicitações. */
	private CalendarioAcademico calendario;

	/** Construtor padrão. */
	public BuscaTurmaMBean() {
		obj = new Turma();
		polo = new Polo();
		curso = new Curso();
		unidade = new Unidade();
		situacaoTurma = new SituacaoTurma();
		
		tiposTurmaCombo = new ArrayList<SelectItem>();
		
		SelectItem regular = new SelectItem();
		regular.setLabel("REGULAR");
		regular.setValue(Turma.REGULAR);
		tiposTurmaCombo.add(regular);
	
		SelectItem ferias = new SelectItem();
		ferias.setLabel("FÉRIAS");
		ferias.setValue(Turma.FERIAS);
		tiposTurmaCombo.add(ferias);
	
		SelectItem ensinoIndividual = new SelectItem();
		ensinoIndividual.setLabel("ENSINO INDIVIDUAL");
		ensinoIndividual.setValue(Turma.ENSINO_INDIVIDUAL);
		tiposTurmaCombo.add(ensinoIndividual);
	}
	
	/**
	 * Redireciona para o formulário de busca de turmas por
	 * departamento/situação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarBuscaPorDepartamento() {
		obj.setAno(CalendarUtils.getAnoAtual());
		obj.setPeriodo(getPeriodoAtual());
		setResultadosBusca(null);
		return forward("/graduacao/relatorios/turma/seleciona_turmas_ano_periodo.jsp");
	}

	/**
	 * Busca as turmas oferecidas no ano/período/curso informado.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/relatorios/turmas_oferecidas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscarTurmasOferecidas() throws DAOException {
		TurmaDao dao = getDAO(TurmaDao.class);
		ValidatorUtil.validateRequired(curso, "Curso", erros);
		ValidatorUtil.validateMinValue(obj.getAno(), 1900, "Ano", erros);
		ValidatorUtil.validateRange(obj.getPeriodo(), 1, 4, "Período", erros);
		if (hasErrors()) {
			return null;
		}
		getCurrentRequest().setAttribute("resultadoBusca", dao.findTurmasOferecidasAoCurso(obj.getAno(), obj.getPeriodo(), curso.getId()));
		return null;
	}
	
	/**
	 * Inicializa os filtros utilizados na consulta de turmas.
	 */
	private void initFiltros() {
		filtroAnoPeriodo = true;
		filtroCodigo = false;
		filtroDisciplina = false;
		filtroDocente = false;
		filtroPolo = false;
	}

	/** 
	 * Seleciona a turma de uma consulta
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 *  <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 *  <li>sigaa.war/lato/coordenador.jsp</li>
	 * </ul>
	 * @return sigaa.war/ensino/turma/discentes.jsp
	 * @throws DAOException
	 */
	public String selecionaTurma() throws DAOException {
		TurmaDao dao = getDAO(TurmaDao.class);
		SolicitacaoMatriculaDao sdao = getDAO(SolicitacaoMatriculaDao.class);

		turmaEscolhida = dao.findByPrimaryKey(getParameterInt("id"), Turma.class);

		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		situacoes.add(SituacaoMatricula.CANCELADO );
		situacoes.add(SituacaoMatricula.NAO_CONCLUIDO );
		matriculados = dao.findMatriculasByTurma(getParameterInt("id"), situacoes);

		solicitantes = sdao.findByTurma(turmaEscolhida.getId(), false,
				SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO,	SolicitacaoMatricula.VISTA);

		// Organizar as solicitações de acordo com o resultado do processamento
		Map<String, Collection<SolicitacaoMatricula>> gruposSolicitacoes = new HashMap<String, Collection<SolicitacaoMatricula>>();
		for (SolicitacaoMatricula sol : solicitantes) {
			String resultadoProcessamento = sol.getResultadoProcessamento();
			Collection<SolicitacaoMatricula> solicitacoesResultado = gruposSolicitacoes.get(resultadoProcessamento);

			if (solicitacoesResultado == null) {
				solicitacoesResultado = new ArrayList<SolicitacaoMatricula>();
				gruposSolicitacoes.put(resultadoProcessamento, solicitacoesResultado);
			}
			solicitacoesResultado.add(sol);
		}
		getCurrentRequest().setAttribute("gruposSolicitacoes", gruposSolicitacoes);

		return forward("/ensino/turma/discentes.jsp");
	}

	/** 
	 * Lista alunos para impressão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return /ensino/turma/discentes_relatorio.jsp
	 * @throws DAOException
	 */
	public String listarAlunosImpressao() throws DAOException {
		TurmaDao dao = getDAO(TurmaDao.class);

		turmaEscolhida = dao.findByPrimaryKey(getParameterInt("id"), Turma.class);

		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		situacoes.add(SituacaoMatricula.CANCELADO );
		situacoes.add(SituacaoMatricula.NAO_CONCLUIDO );
		matriculados = dao.findMatriculasDadosPessoaisByTurma(getParameterInt("id"), situacoes);

		return forward("/ensino/turma/discentes_relatorio.jsp");
	}

	/** 
	 * Retorna o caminho para o formulário de busca de turma.
	 * 
	 * Método não é invocado por jsp
	 * 
	 * @return /ensino/turma/busca_turma.jsp
	 */
	public String telaSelecaoTurma() {
		return forward("/ensino/turma/busca_turma.jsp");
	}

	/** 
	 * Busca as turmas abertas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @param ae
	 */
	public void buscarTurmasAbertas(ActionEvent ae) {
		TurmaDao dao = getDAO(TurmaDao.class);
		String codigo = null;
		String nome = null;
		String nomeDocente = null;
		Integer ano = null;
		Integer periodo = null;
		Integer situacao = 0;
		erros = new ListaMensagens();
		if (Boolean.valueOf(getParameter("buscaCodigo"))){
			codigo = getParameter("codigoDisciplina");
			ValidatorUtil.validateRequired(codigo, "Código", erros);
		}
		if (Boolean.valueOf(getParameter("buscaNome"))){
			nome = getParameter("nomeDisciplina");
			ValidatorUtil.validateRequired(nome, "Nome da Disciplina", erros);
		}
		if (Boolean.valueOf(getParameter("buscaNomeDocente"))){
			nomeDocente = getParameter("nomeDocente");
			ValidatorUtil.validateRequired(nomeDocente, "Nome do Docente", erros);
		}
		if (Boolean.valueOf(getParameter("buscaSituacaoTurma")))
			situacao = getParameterInt("situacaoTurma");
		if (Boolean.valueOf(getParameter("buscaAno"))){
			try {
				ano = getParameterInt("anoTurma");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				addMensagemErro("Ano inválido.");
				setResultadosBusca(null);
				return ;
			}
			try {
				periodo = getParameterInt("periodoTurma");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				addMensagemErro("Período inválido.");
				setResultadosBusca(null);
				return ;
			}
		}
		if(hasErrors()){
			addMensagens(erros);
			return;
		}
		if (codigo == null && nome == null && nomeDocente == null && ano == null && periodo == null) {
			addMensagemErro("Por favor, escolha algum critério de busca");
		} else {
			try {
				if(getNivelEnsino() != NivelEnsino.LATO &&  !docenteExterno)
					setResultadosBusca(dao.gerarRelatorio(situacao, codigo, nome, nomeDocente,
							ano, periodo, getUnidadeGestora(), getNivelEnsino()));
				else
					setResultadosBusca(dao.gerarRelatorioDocenteExterno(situacao, codigo, nome, nomeDocente,
							ano, periodo, getUnidadeGestora(), getNivelEnsino()));
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
			}
		}
	}

	/** 
	 * Configura os dados para busca de turmas do nível Técnico.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 * </ul>
	 * @return
	 */
	public String popularBuscaTecnico() {
		filtroNivel = true;
		filtroUnidade = true;
		nivelTurma = getNivelEnsino();
		return popularBuscaGeral();
	}
	
	/**
	 * Carrega dados de uma turma para serem exibidos em uma tela de resumo.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/resumo.jsp</li>
	 * <li>sigaa.war/graduacao/turma/view_painel.jsp</li>
	 * <li>sigaa.war/graduacao/turma/view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCarregarTurma() throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id", 0), Turma.class);
		if (!isEmpty(obj)) {
			if (!isEmpty(obj.getReservas())) obj.getReservas().iterator();
			if (!isEmpty(obj.getDocentesTurmas())) obj.getDocentesTurmas().iterator();
		}
		return null;
	}
	
	/** Retorna o total de discentes matriculados na turma.
	 * @return
	 * @throws DAOException
	 */
	public Integer getAlunosMatriculados() throws DAOException {
		if (obj.getId() > 0 && "true".equals(getParameter("contarMatriculados"))) {
			TurmaDao dao = getDAO(TurmaDao.class);
			return dao.findTotalMatriculados(obj.getId());
		} else {
			return null;
		}
	}
	
	/** Retorna o total de solicitações de matrícula na turma.
	 * @return
	 * @throws DAOException
	 */
	public Integer getSolicitacoesRematricula() throws DAOException {
		if (obj.getId() > 0 && "true".equals(getParameter("contarMatriculados"))) {
			TurmaDao dao = getDAO(TurmaDao.class);
			return dao.findTotalSolicitacoesReMatricula(obj.getId());
		} else {
			return null;
		}
	}

	/**
	 * Inicia a busca de turma com a opção de relatório já marcada por padrão
	 * este método deve ser chamado para iniciar relatórios de turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>sigaa.war/stricto/menus/relatorios.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarRelatorio() {
		filtroRelatorio = true;
		if( getAcessoMenu().isProgramaStricto() )
			filtroUnidade = true;

		return popularBuscaGeral();
	}

	/** 
	 * Inicializa os dados para a busca de turmas
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/departamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarTurmasSituacao() {
		filtroRelatorio = true;
		filtroSituacao = true;
		if( getAcessoMenu().isSecretarioDepartamento() )
			filtroUnidade = true;

		return popularBuscaGeral();
	}
	
	/**
	 * Inicia a operação do DAE de adicionar reserva a uma turma sem precisar de
	 * solicitação de turma. SÓ PODE SER REALIZADO POR ADMINISTRADOR DAE.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarAdicionarReservaSemSolicitacao() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO);
		TurmaGraduacaoMBean mBean = getMBean("turmaGraduacaoBean");
		return mBean.iniciarAdicionarReservaSemSolicitacao();
	}
	
	/** 
	 * Popula dos dados gerais para a realização da busca de turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 * <li>sigaa.war/ead/menu.jsp</li>
	 * <li>sigaa.war/graduacao/departamento.jsp</li>
	 * <li>sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>sigaa.war/graduacao/menus/consultas.jsp</li>
	 * <li>sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 * <li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>sigaa.war/graduacao/turma/resumo.jsp</li>
	 * <li>sigaa.war/lato/menu_coordenador.jsp</li>
	 * <li>sigaa.war/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * <li>sigaa.war/portais/cpolo/menu_cpolo.jsp</li>
	 * <li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * <li>sigaa.war/portais/rh_plan/abas/graduacao.jsp</li>
	 * <li>sigaa.war/portais/tutor/menu_tutor.jsp</li>
	 * <li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>sigaa.war/stricto/menus/matricula.jsp</li>
	 * <li>sigaa.war/avaliacao/menu_avaliacao.jsp</li>
	 * </ul>
	 * 
	 * @return /ensino/turma/busca_turma.jsf
	 */
	public String popularBuscaGeral() {
		CoordenacaoPolo cp = getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo();
		if (cp != null && cp.getPolo() != null) {
			polo = cp.getPolo();
			filtroPolo = true;
		}
		else {
			polo = new Polo();
		}

		initFiltros();
		situacaoTurma = new SituacaoTurma(SituacaoTurma.ABERTA);
		CalendarioAcademico cal = getCalendarioVigente();
		anoTurma = cal != null ? cal.getAno() : CalendarUtils.getAnoAtual();
		periodoTurma = cal != null ? cal.getPeriodo() : getPeriodoAtual();

		if(nivelTurma == null){
			if( NivelEnsino.isValido( getNivelEnsino() ) ){
				nivelTurma = getNivelEnsino();
				filtroNivel = true;
			}else
				nivelTurma = NivelEnsino.GRADUACAO;

			if( NivelEnsino.isAlgumNivelStricto(nivelTurma) ){
				nivelTurma = NivelEnsino.STRICTO;
				filtroNivel = true;
			}
		}

		// Popular unidade
		if(isNotEmpty(getCursoAtualCoordenacao()) && isNotEmpty(getCursoAtualCoordenacao().getUnidadeCoordenacao()))
			unidade = new Unidade( getCursoAtualCoordenacao().getUnidadeCoordenacao().getId());
		else
			unidade = new Unidade( getUsuarioLogado().getVinculoAtivo().getUnidade().getId() );
		if( getProgramaStricto() != null ) {
			unidade = new Unidade( getProgramaStricto().getId());
		}
		if (unidade != null && unidade.getId() != 0 
				&& (getAcessoMenu().isChefeDepartamento()
						|| getAcessoMenu().isChefeUnidade())) {
			filtroUnidade = true;
		}

		codigoDisciplina = "";
		nomeDisciplina = "";
		nomeDocente = "";

		setResultadosBusca(new ArrayList<Turma>(0));
		return forward("/ensino/turma/busca_turma.jsf");
	}
	
	/**
	 * Carrega as informações da turma e redireciona para o formulário de visualização.
	 * Método chamado pela(s) seguinte(s) JSP(s): /ensino/turma/busca_turma.jsp
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException{
		populateObj(true);
		if (obj == null || obj.getSituacaoTurma().getId() == SituacaoTurma.EXCLUIDA) {
			obj = new Turma();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		TurmaMBean mBean = null;
		if (obj.isMedio())
			mBean = getMBean("turmaMedio");
		else if (obj.isTecnico() || obj.isFormacaoComplementar())
			mBean = getMBean("turmaTecnicoBean");
		else if (obj.isInfantil()) {
			// as turmas de ensino infantil não seguem o padrão
			addMensagemWarning("Para visualizar a turma de ensino Infantil, utilize o módulo de Ensino Infantil");
			return null;
		} else if (obj.isGraduacao())
			mBean = getMBean("turmaGraduacaoBean");
		else if (obj.isLato())
			mBean = getMBean("turmaLatoSensuBean");
		else if (obj.isStricto())
			mBean = getMBean("turmaStrictoSensuBean");
		else if (obj.isResidenciaMedica())
			mBean = getMBean("turmaResidenciaMedica");
		else {
			addMensagemErro("Não foi possível determinar o nível de ensino da turma.");
			return null;
		}
		mBean.setApenasVisualizacao(true);
		return mBean.view();
	}

	/** Redireciona para o MBean responsável pelas operações da turma do nível de ensino da turma.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar() 
	 */
	@Override
	public String atualizar() throws ArqException {
		populateObj(true);
		if (obj == null || obj.getSituacaoTurma().getId() == SituacaoTurma.EXCLUIDA) {
			obj = new Turma();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		TurmaMBean mBean = null;
		if (obj.isGraduacao())
			mBean = getMBean("turmaGraduacaoBean");
		else if (obj.isLato())
			mBean = getMBean("turmaLatoSensuBean");
		else if (obj.isStricto())
			mBean = getMBean("turmaStrictoSensuBean");
		else if (obj.isTecnico() || obj.isFormacaoComplementar())
			mBean = getMBean("turmaTecnicoBean");
		else if (obj.isResidenciaMedica())
			mBean = getMBean("turmaResidenciaMedica");
		else {
			addMensagemErro("Não foi possível determinar o nível de ensino da turma.");
			return null;
		}
		return mBean.atualizar();
	}
	
	/** Redireciona para o MBean responsável pelas operações de duplicação de turma.
	 * No momento, somente turmas de graduação poderão ser duplicadas.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar() 
	 */
	public String duplicarTurma() throws ArqException {
		populateObj(true);
		if (obj == null || obj.getSituacaoTurma().getId() == SituacaoTurma.EXCLUIDA) {
			obj = new Turma();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		TurmaGraduacaoMBean mBean = null;
		if (obj.isGraduacao())
			mBean = getMBean("turmaGraduacaoBean");
		else {
			addMensagemErro("Não foi possível determinar o nível de ensino da turma.");
			return null;
		}
		mBean.setObj(obj);
		return mBean.duplicarTurma();
	}
	
	/**
	 * Inicia o caso de uso de remover turmas.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 */
	@Override
	public String preRemover() {
		try {
			populateObj(true);
			if (obj == null || obj.getSituacaoTurma().getId() == SituacaoTurma.EXCLUIDA) {
				obj = new Turma();
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			TurmaMBean mBean = null;
			if (obj.isGraduacao())
				mBean = getMBean("turmaGraduacaoBean");
			else if (obj.isLato())
				mBean = getMBean("turmaLatoSensuBean");
			else if (obj.isStricto())
				mBean = getMBean("turmaStrictoSensuBean");
			else if (obj.isTecnico() || obj.isFormacaoComplementar())
				mBean = getMBean("turmaTecnicoBean");
			else if (obj.isResidenciaMedica())
				mBean = getMBean("turmaResidenciaMedica");
			else {
				addMensagemErro("Não foi possível determinar o nível de ensino da turma.");
				return null;
			}
			return mBean.preRemover();
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
	}
	
	/** 
	 * Realiza a busca geral por turmas. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscarGeral() throws DAOException {
		Character nivel = null;
		String codigo = null;
		String codigoTurma = null;
		String nome = null;
		String docente = null;
		Integer ano = null;
		Integer periodo = null;
		Integer situacao = null;
		Polo poloEscolhido = null;
		Curso c = null;
		Unidade u = null;
		String local = null;
		Integer tipo = null;
		String horario = null;
		Integer ordenarPor = null;
		ModalidadeEducacao modalidade = new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL);
		
		if(getTurmasEAD())
			modalidade = new ModalidadeEducacao(ModalidadeEducacao.A_DISTANCIA);
		else if (isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_GERAL_EAD))
			modalidade = null;
		else if ( getAcessoMenu().isEad() || getAcessoMenu().isCursoEad() || getAcessoMenu().isTutorEad() )
			modalidade = null;
		
		// Quando o nível na consulta de turma é mestrado ou doutorado deve converter para stricto e
		// voltar para o nível correto ao retornar para a jsp. Está variável armazena o nível 
		// selecionado no formulário para voltar ao original no final da consulta.
		char nivelAntigo = 'E';
		
		if(ordenarBusca) {
			ordenarPor = this.ordenarPor;
		}

		if(filtroNivel){
			if (nivelTurma == '0')
				addMensagemErro("Selecione um nível de ensino válido.");
			if( NivelEnsino.isAlgumNivelStricto(nivelTurma) ){
				nivelAntigo = nivelTurma;
				nivelTurma = NivelEnsino.STRICTO;
			}
			nivel = nivelTurma;
		}
		if(filtroUnidade) {
			if (unidade.getId() == 0)
				addMensagemErro("Selecione uma unidade válida.");
			u = unidade;
		}
		if (filtroCodigoTurma)
			codigoTurma = this.codigoTurma.trim();
		if (filtroCodigo)
			codigo = codigoDisciplina.trim();
		if (filtroDisciplina)
			nome = nomeDisciplina.trim();
		if (filtroDocente)
			docente = nomeDocente.trim();
		if (filtroSituacao)
			situacao = situacaoTurma.getId() > 0 ? situacaoTurma.getId() : null;
		if (filtroAnoPeriodo){
			ano = anoTurma;
			periodo = periodoTurma;
		}
		if(filtroPolo) {
			if (polo.getId() == 0)
				addMensagemErro("Selecione um pólo válido.");
			poloEscolhido = polo;
		}
		if(filtroCurso) {
			if (curso.getId() == 0)
				addMensagemErro("Selecione um curso válido.");
			
			c = getGenericDAO().refresh(curso);
		}
		if(filtroLocal)
			local = this.local;
		if(filtroTipo && !isEmpty(this.tipoTurma) )
			tipo = this.tipoTurma;
		if (filtroHorario) {
			horario = this.turmaHorario;
		}
		
		if (filtroDiarioTurma) {
			if (situacao == null || situacao != SituacaoTurma.CONSOLIDADA) {
				addMensagemErro("Situação: Só é possível emitir Diário de Classe das turmas Consolidadas. Por favor altere o valor do filtro 'Situação' para 'CONSOLIDADA' caso precise emitir o Diário de Classe.");
			}
			if (unidade.getId() != getUsuarioLogado().getVinculoAtivo().getUnidade().getId()) {
				addMensagemErro("Unidade: Só é possível emitir Diário de Classe do seu Departamento. Por favor altere o valor do filtro 'Unidade' para o seu Departamento caso precise emitir o Diário de Classe.");
			}
		}
		
		if (hasOnlyErrors())
			return null;
		
		if ((getTurmasEAD() || nivel == null) && u == null && codigo == null && codigoTurma == null
				&& nome == null && docente == null && situacao == null && ano == null && periodo == null && poloEscolhido == null 
				&& !filtroConvenio && c == null && tipo == null && local == null && horario == null) {
			addMensagemErro("Por favor, escolha algum critério de busca.");
			return null;
		} else {
			TurmaDao dao = getDAO(TurmaDao.class);
			unidade = dao.refresh(unidade);
			curso = dao.refresh(curso);
			situacaoTurma = dao.refresh(situacaoTurma);
			polo = dao.refresh(polo);
			try {
				Collection<Curso> cursos = null;
				if (filtroConvenio || isUserInRole(SigaaPapeis.COORDENADOR_CURSO) 
						&& getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().isProbasica()) {
					CursoDao cdao = getDAO(CursoDao.class);
					cursos = cdao.findByConvenioAcademico(ConvenioAcademico.PROBASICA, NivelEnsino.GRADUACAO);
				}
				
				if(filtroCurso) {
					if (c != null && c.isProbasica()) {
						cursos = new ArrayList<Curso>();
						cursos.add(c);
						c = null;
					}
				}
				
				setResultadosBusca(dao.findGeral(nivel, u, codigo, codigoTurma , nome, docente,
						(situacao != null ? new Integer[] { situacao } : null ) , ano, periodo, poloEscolhido, cursos, 
						modalidade, c, null, local, tipo, horario,ordenarPor,ConsolidacaoUtil.getSituacaoesAConsolidar()));
				if(!resultadosBusca.isEmpty()){
					List<Integer> ids= new ArrayList<Integer>();				
					for(Turma turma:resultadosBusca){
						if(turma.getPolo() != null)
							ids.add(turma.getPolo().getId());		
					}
					if (!isEmpty(ids)) {
						PoloDao poloDao = getDAO(PoloDao.class);
						Map<Integer, Polo> p = poloDao.findByPrimaryKeyOtimizado(ids);									
					    for(Turma turma: resultadosBusca){
					    	if(turma.getPolo() != null)
					    		turma.setPolo(p.get(turma.getPolo().getId()));			    	
					    }
					}
				}
				if( NivelEnsino.isAlgumNivelStricto(nivelTurma) )
					nivelTurma = nivelAntigo;
			} catch (LimiteResultadosException e) {
				addMensagemErro("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
				setResultadosBusca(new ArrayList<Turma>(0));
				if( NivelEnsino.isAlgumNivelStricto(nivelTurma) )
					nivelTurma = nivelAntigo;
				return null;
			} catch (Exception e) {
				setResultadosBusca(new ArrayList<Turma>(0));
				if( NivelEnsino.isAlgumNivelStricto(nivelTurma) )
					nivelTurma = nivelAntigo;
				return tratamentoErroPadrao(e);
			}
		}
		
		if( ValidatorUtil.isEmpty(getResultadosBusca()) )
			addMessage("Nenhuma turma encontrada de acordo com os critérios de busca informados.", TipoMensagemUFRN.WARNING);
		if(filtroRelatorio)
			return relatorio();

		if (isUserInRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG)) 
			nivelGestor = getNivelEnsino();

		if (isUserInRole(SigaaPapeis.GESTOR_TECNICO))
			unidadeGestor = getUsuarioLogado().getVinculoAtivo().getUnidade().getId();
		return telaBuscaGeral();
	}

	/** Redireciona o usuário para o formulário de busca geral de turmas.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String telaBuscaGeral() {
		return forward("/ensino/turma/busca_turma.jsf");
	}

	/** 
	 * Gera o relatório de turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 *  
	 * @return /ensino/turma/relatorio.jsp
	 */
	public String gerarRelatorio() {
		return forward("/ensino/turma/relatorio.jsp");
	}

	/** 
	 * Retorna a página de relatório de turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/relatorios/ocupacao_turma/relatorio.jsp</li>
	 * </ul>
	 * @return /geral/turma/relatorio.jsp
	 */
	public String relatorio() {
		return forward("/ensino/turma/relatorio.jsp");
	}

	/**
	 * Reabre uma turma já consolidada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return menuGraduacao
	 * @throws ArqException
	 */
	public String reabrirTurma() throws ArqException{
		Integer id = getParameterInt("id");
		if(id == null){
			addMensagemErro("Informe a turma.");
			return null;
		}
		
		Turma turma = new Turma(id);
		turma = getGenericDAO().findByPrimaryKey(turma.getId(), Turma.class);
		
		if (!OperacaoTurmaValidator.isPermiteReabrirTurma(turma)) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação.");
		}		
		
		prepareMovimento(SigaaListaComando.REABRIR_TURMA);

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REABRIR_TURMA);
		mov.setObjMovimentado(turma);

		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		addMessage("Turma reaberta com sucesso!", TipoMensagemUFRN.INFORMATION);
		return buscarGeral();
	}

	/**
	 * Fechar uma turma diretamente (somente se todas as matrículas estiverem consolidadas, ou seja, ninguém matriculado).
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return menuGraduacao
	 * @throws ArqException
	 */
	public String fecharTurma() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO);
		Integer id = getParameterInt("id");
		if(id == null){
			addMensagemErro("Informe a turma.");
			return null;
		}
		
		// caso o usuário seja chefe de departamento, verificar se todos alunos estão trancado/cancelado
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			FecharTurmaDao dao = getDAO(FecharTurmaDao.class);
			
			try {
				if(dao.containsAlunosAtivos(id)) {
					addMensagemErro("A turma não poderá ser fechada pois existem matrículas ativas.");
					return null;
				}
			} finally {
				dao.close();
			}
		}
		
		Turma turma = new Turma(id);
		prepareMovimento(SigaaListaComando.FECHAR_TURMA);

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.FECHAR_TURMA);
		mov.setObjMovimentado(turma);

		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		addMessage("Turma fechada com sucesso!", TipoMensagemUFRN.INFORMATION);
		return buscarGeral();
	}
	
	/**
	 * Consolidação realizada pelos gestores do ensino técnico.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return @see ConsolidarTurmaMBean#escolherTurma()
	 * @throws ArqException 
	 */
	public String consolidarTurma() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG,SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		
		Integer id = getParameterInt("id");
		Turma t = dao.findByPrimaryKey(id, Turma.class);
		t.setQtdMatriculados( dao.findTotalMatriculasByTurmaAtivos(t) );
		
		if (!OperacaoTurmaValidator.isPermiteConsolidar(t)) {
			addMensagemErro("Somente turmas abertas do mesmo nível de ensino"+ 
					( getNivelEnsino() == NivelEnsino.TECNICO ? " e mesma unidade " : " " ) 
					+ "do usuário podem ser consolidadas.");
			return null;
		}
		
		ConsolidarTurmaMBean consolidarBean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
		consolidarBean.setTurma(t);
		return consolidarBean.escolherTurma();
	}

	/**
	 * Retorna todos os níveis de ensino.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public SelectItem[] getNiveisCombo(){
		return NivelEnsino.getNiveisCombo();
	}
	
	/**
	 * Retorna uma lista de {@link SelectItem} com todas as possíveis opções de ordenação dos dados.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<SelectItem> getAllOpcoesOrdenacaoCombo() {
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(ORDENAR_POR_COMPONENTE_CURRICULAR, "Ordenar por Componente Curricular"));
		itens.add(new SelectItem(ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS, "Ordenar por dias da semana e respectivos horários"));
		itens.add(new SelectItem(ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS, "Ordenar por docente, disciplinas e respectivos horários"));
		itens.add(new SelectItem(ORDENAR_POR_LOCAL, "Ordenar por Local"));		
		return itens;
	}
	
	
	/**
	 * Configura campos pré-determinados para a busca de turmas do nível Lato.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getReabrirTurmaGestorLato() {
		this.nivelTurma = NivelEnsino.LATO;
		this.filtroNivel = true;
		this.situacaoTurma = new SituacaoTurma(SituacaoTurma.CONSOLIDADA);
		this.filtroSituacao = true;
		return "";
	}
	
	/**
	 * Verifica as permissões para ter acesso a reabertura de turma
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPermitirReabrirTurma() {
		return ((isUserInRole(SigaaPapeis.GESTOR_LATO) && getSubSistema().equals(SigaaSubsistemas.LATO_SENSU))
				|| (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && getSubSistema().equals(SigaaSubsistemas.GRADUACAO)) 
				|| (isUserInRole(SigaaPapeis.PPG) && getSubSistema().equals(SigaaSubsistemas.STRICTO_SENSU)));
	}

	/**
	 * Indica se esta no período de cadastro de turma.
	 * @return
	 * @throws DAOException 
	 */
	public boolean isPeriodoCadastroTurma() throws DAOException{
		if( getCalendario() != null && 
				( getCalendario().isPeriodoCadastroTurmas() || getCalendario().isPeriodoCadastroTurmasFerias() 
						||  getCalendario().isPeriodoCadastroTurmaEnsinoIndiv() ) )
			return true;
		else
			return false;
	}
	
	/**
	 * Carrega o calendário correto para realizar análise das solicitações
	 * @throws DAOException
	 */
	protected CalendarioAcademico getCalendario() throws DAOException {
		if (calendario == null) {
			calendario = getCalendarioVigente();
			if (getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().isProbasica()) {
				calendario = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao());
			} else if( calendario.getNivel() == NivelEnsino.TECNICO ){
				calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
			} else if( isUserInRole( SigaaPapeis.PPG ) ) {
				calendario = CalendarioAcademicoHelper.getCalendario(obj.getDisciplina());
			}
		}
		if (calendario == null) {
			addMensagemErro("Erro ao carregar calendário acadêmico no ano-período informado: " + obj.getAnoPeriodo());
			return null;
		} else
			return calendario;
	}
	
	/**
	 * Retorna todos os curso com o nível informado.
	 * Método não chamado por JSP.
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public boolean isBuscaTurmasEAJ() throws DAOException{
		return getUsuarioLogado().getVinculoAtivo().getUnidade().getId() == 
				ParametroHelper.getInstance().getParametroInt(ParametrosMedio.ID_UNIDADE_GERAL_MEDIO);
	}
	
	/**
	 * Retorna todos os cursos do nível e unidade gestora acadêmica atuais
	 * definidos na sessão em formato para combo <br/>
	 * <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 * <li>/sigaa.war/ensino/tecnico/modulo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllCursoNivelAtualCombo() throws ArqException{
		CursoDao dao = getDAO(CursoDao.class);
		Collection<Curso> allCurso = isTecnico() ? dao.findByNivel(getNivelEnsino(), true, null, new Unidade(getUnidadeGestora())) : dao.findByNivel(getNivelEnsino());
		return toSelectItems(allCurso, "id", "descricao");
	}
	
	public boolean isDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(boolean docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	/** Retorna a situação da turma utilizada para restringir o resultado da busca. 
	 * @return
	 */
	public Integer getSituacaoTurma() {
		return situacaoTurma == null ? 0 : situacaoTurma.getId();
	}
	
	/** Retorna a descrição da situação da turma
	 * @return
	 */
	public String getDescricaoSituacaoTurma() {
		return situacaoTurma == null ? "" : situacaoTurma.getDescricao();
	}

	/** Seta a situação da turma utilizada para restringir o resultado da busca.
	 * @param situacaoTurma
	 */
	public void setSituacaoTurma(Integer situacaoTurma) {
		this.situacaoTurma = new SituacaoTurma(situacaoTurma);
	}

	/** Retorna o ano da turma utilizada para restringir o resultado da busca. 
	 * @return
	 */
	public Integer getAnoTurma() {
		return anoTurma;
	}

	/** Seta o ano da turma utilizada para restringir o resultado da busca.
	 * @param anoTurma
	 */
	public void setAnoTurma(Integer anoTurma) {
		this.anoTurma = anoTurma;
	}

	/** Retorna o período da turma para restringir o resultado da busca. 
	 * @return
	 */
	public Integer getPeriodoTurma() {
		return periodoTurma;
	}

	/** Seta o período da turma para restringir o resultado da busca.
	 * @param periodoTurma
	 */
	public void setPeriodoTurma(Integer periodoTurma) {
		this.periodoTurma = periodoTurma;
	}

	/** Retorna o nível de ensino da turma utilizado para restringir o resultado da busca. 
	 * @return
	 */
	public Character getNivelTurma() {
		return nivelTurma;
	}

	/** Seta o nível de ensino da turma utilizado para restringir o resultado da busca.
	 * @param nivelTurma
	 */
	public void setNivelTurma(Character nivelTurma) {
		this.nivelTurma = nivelTurma;
	}

	/** Retorna o código da disciplina utilizado para restringir o resultado da busca. 
	 * @return
	 */
	public String getCodigoDisciplina() {
		return codigoDisciplina;
	}

	/** Seta o código da disciplina utilizado para restringir o resultado da busca.
	 * @param codigoDisciplina
	 */
	public void setCodigoDisciplina(String codigoDisciplina) {
		this.codigoDisciplina = codigoDisciplina;
	}

	/** Retorna o nome da disciplina utilizado para restringir o resultado da busca. 
	 * @return
	 */
	public String getNomeDisciplina() {
		return nomeDisciplina;
	}

	/** Seta o nome da disciplina utilizado para restringir o resultado da busca.
	 * @param nomeDisciplina
	 */
	public void setNomeDisciplina(String nomeDisciplina) {
		this.nomeDisciplina = nomeDisciplina;
	}

	/** Retorna o nível de ensino da turma utilizado para restringir o resultado da busca. 
	 * @return
	 */
	public String getNomeDocente() {
		return nomeDocente;
	}

	/** Seta o nível de ensino da turma utilizado para restringir o resultado da busca.
	 * @param nomeDocente
	 */
	public void setNomeDocente(String nomeDocente) {
		this.nomeDocente = nomeDocente;
	}

	/** Indica se filtra a busca por ano e período da turma.  
	 * @return
	 */
	public boolean isFiltroAnoPeriodo() {
		return filtroAnoPeriodo;
	}

	/** Seta se filtra a busca por ano e período da turma. 
	 * @param filtroAnoPeriodo
	 */
	public void setFiltroAnoPeriodo(boolean filtroAnoPeriodo) {
		this.filtroAnoPeriodo = filtroAnoPeriodo;
	}

	/** Indica se filtra a busca por código da disciplina. 
	 * @return
	 */
	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	/** Seta se filtra a busca por código da disciplina. 
	 * @param filtroCodigo
	 */
	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	/** Indica se filtra a busca por nome da disciplina
	 * @return
	 */
	public boolean isFiltroDisciplina() {
		return filtroDisciplina;
	}

	/** Seta se filtra a busca por nome da disciplina
	 * @param filtroDisciplina
	 */
	public void setFiltroDisciplina(boolean filtroDisciplina) {
		this.filtroDisciplina = filtroDisciplina;
	}

	/** Indica se filtra a busca por nome do docente. 
	 * @return
	 */
	public boolean isFiltroDocente() {
		return filtroDocente;
	}

	/** Seta se filtra a busca por nome do docente. 
	 * @param filtroDocente
	 */
	public void setFiltroDocente(boolean filtroDocente) {
		this.filtroDocente = filtroDocente;
	}

	/** Indica se filtra a busca por nível de ensino. 
	 * @return
	 */
	public boolean isFiltroNivel() {
		return filtroNivel;
	}

	/** Seta se filtra a busca por nível de ensino. 
	 * @param filtroNivel
	 */
	public void setFiltroNivel(boolean filtroNivel) {
		this.filtroNivel = filtroNivel;
	}

	/** Indica se filtra a busca por situação da turma. 
	 * @return
	 */
	public boolean isFiltroSituacao() {
		return filtroSituacao;
	}

	/** Seta se filtra a busca por situação da turma. 
	 * @param filtroSituacao
	 */
	public void setFiltroSituacao(boolean filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	/** Retorna a unidade utilizado para restringir o resultado da busca.
	 * @return
	 */
	public Unidade getUnidade() {
		return unidade;
	}

	/** Seta a unidade utilizado para restringir o resultado da busca. 
	 * @param unidade
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/** Indica se filtra a busca por unidade. 
	 * @return
	 */
	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	/** Seta se filtra a busca por unidade. 
	 * @param filtroUnidade
	 */
	public void setFiltroUnidade(boolean filtroUnidade) {
		this.filtroUnidade = filtroUnidade;
	}

	/** Retorna o código da turma utilizado para restringir o resultado da busca. 
	 * @return
	 */
	public String getCodigoTurma() {
		return codigoTurma;
	}

	/** Seta o código da turma utilizado para restringir o resultado da busca. 
	 * @param codigoturma
	 */
	public void setCodigoTurma(String codigoturma) {
		this.codigoTurma = codigoturma;
	}

	/** Indica se filtra a busca por código da turma. 
	 * @return
	 */
	public boolean isFiltroCodigoTurma() {
		return filtroCodigoTurma;
	}

	/** Seta se filtra a busca por código da turma. 
	 * @param filtroCodigoTurma
	 */
	public void setFiltroCodigoTurma(boolean filtroCodigoTurma) {
		this.filtroCodigoTurma = filtroCodigoTurma;
	}

	/** Indica se a busca realizada é de turmas de EAD.
	 * @return
	 */
	public Boolean getTurmasEAD() {
		if( getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_POLO) 
				|| getSubSistema().equals(SigaaSubsistemas.SEDIS))
			return true;
		if (getCursoAtualCoordenacao() != null)
			return getCursoAtualCoordenacao().isADistancia();
		else
			return getParameterBoolean("turmasEAD");
	}

	/** Indica se filtra a busca por pólo de ensino. 
	 * @return
	 */
	public boolean isFiltroPolo() {
		return filtroPolo;
	}

	/** Seta se filtra a busca por pólo de ensino. 
	 * @param filtroPolo
	 */
	public void setFiltroPolo(boolean filtroPolo) {
		this.filtroPolo = filtroPolo;
	}

	/** Indica se a busca é realizada pela SEDIS 
	 * @return
	 */
	public Polo getPolo() {
		return polo;
	}

	/** Seta se a busca é realizada pela SEDIS  
	 * @param polo
	 */
	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	/** Indica se filtra a busca por tipo de convênio. 
	 * @return
	 */
	public boolean isFiltroConvenio() {
		return filtroConvenio;
	}

	/** Seta se filtra a busca por tipo de convênio. 
	 * @param filtroConvenio
	 */
	public void setFiltroConvenio(boolean filtroConvenio) {
		this.filtroConvenio = filtroConvenio;
	}

	/** Indica se filtra a busca por curso. 
	 * @return
	 */
	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	/** Seta se filtra a busca por curso. 
	 * @param filtroCurso
	 */
	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	/** Retorna o curso utilizado para restringir o resultado da busca.
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso utilizado para restringir o resultado da busca. 
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Indica que o resultado da busca será gerado no formato de relatório. 
	 * @return
	 */
	public boolean isFiltroRelatorio() {
		return filtroRelatorio;
	}

	/** Seta que o resultado da busca será gerado no formato de relatório. 
	 * @param filtroRelatorio
	 */
	public void setFiltroRelatorio(boolean filtroRelatorio) {
		this.filtroRelatorio = filtroRelatorio;
	}

	/** Retorna a lista de Solicitações de matrículas
	 * @return
	 */
	public Collection<SolicitacaoMatricula> getSolicitantes() {
		return solicitantes;
	}

	/** Seta a lista de Solicitações de matrículas 
	 * @param solicitantes
	 */
	public void setSolicitantes(Collection<SolicitacaoMatricula> solicitantes) {
		this.solicitantes = solicitantes;
	}

	/** Indica se o nível de ensino é de stricto sensu.
	 * @return
	 */
	public boolean isStricto(){
		return NivelEnsino.isAlgumNivelStricto( nivelTurma );
	}

	/** Indica se filtra a busca por local de aulas. 
	 * @return
	 */
	public boolean isFiltroLocal() {
		return filtroLocal;
	}

	/** Seta se filtra a busca por local de aulas. 
	 * @param filtroLocal
	 */
	public void setFiltroLocal(boolean filtroLocal) {
		this.filtroLocal = filtroLocal;
	}

	/** Retorna o local de aulas utilizado para restringir o resultado da busca. 
	 * @return
	 */
	public String getLocal() {
		return local;
	}

	/** Seta o local de aulas utilizado para restringir o resultado da busca. 
	 * @param local
	 */
	public void setLocal(String local) {
		this.local = local;
	}

	/** Retorna o tipo da turma utilizado para restringir o resultado da busca.
	 * @return
	 */
	public Integer getTipoTurma() {
		return tipoTurma;
	}

	/** Seta o tipo da turma utilizado para restringir o resultado da busca. 
	 * @param tipoTurma
	 */
	public void setTipoTurma(Integer tipoTurma) {
		this.tipoTurma = tipoTurma;
	}

	/** Indica se filtra a busca por tipo tipo de turma. 
	 * @return
	 */
	public boolean isFiltroTipo() {
		return filtroTipo;
	}

	/** Seta se filtra a busca por tipo tipo de turma. 
	 * @param filtroTipo
	 */
	public void setFiltroTipo(boolean filtroTipo) {
		this.filtroTipo = filtroTipo;
	}

	/** Seta o nível de ensino do gestor técnico, do DAE, ou PPG. 
	 * @param nivelGestor
	 */
	public void setNivelGestor(char nivelGestor) {
		this.nivelGestor = nivelGestor;
	}

	/** Retorna o nível de ensino do gestor técnico, do DAE, ou PPG.
	 * @return
	 */
	public char getNivelGestor() {
		return nivelGestor;
	}
	
	/** Retorna a turma Escolhida pelo usuário.
	 * @return
	 */
	public Turma getTurmaEscolhida() {
		return turmaEscolhida;
	}
	
	/** Seta a turma Escolhida pelo usuário. 
	 * @param turmaEscolhida
	 */
	public void setTurmaEscolhida(Turma turmaEscolhida) {
		this.turmaEscolhida = turmaEscolhida;
	}

	/** Retorna a lista de Matriculados.
	 * @return
	 */
	public Collection<MatriculaComponente> getMatriculados() {
		return matriculados;
	}

	/** Seta a lista de Matriculados. 
	 * @param matriculados
	 */
	public void setMatriculados(Collection<MatriculaComponente> matriculados) {
		this.matriculados = matriculados;
	}

	/** Seta a unidade do gestor de curso técnico. 
	 * @param unidadeGestor
	 */
	public void setUnidadeGestor(int unidadeGestor) {
		this.unidadeGestor = unidadeGestor;
	}

	/** Retorna a unidade do gestor de curso técnico. 
	 * @return
	 */
	public int getUnidadeGestor() {
		return unidadeGestor;
	}

	/** Retorna o horário da turma utilizado para restringir o resultado da busca.
	 * @return
	 */
	public String getTurmaHorario() {
		return turmaHorario;
	}

	/** Seta o horário da turma utilizado para restringir o resultado da busca. 
	 * @param turmaHorario
	 */
	public void setTurmaHorario(String turmaHorario) {
		this.turmaHorario = turmaHorario;
	}

	/** Indica se filtra a busca por horário. 
	 * @return
	 */
	public boolean isFiltroHorario() {
		return filtroHorario;
	}

	/** Seta se filtra a busca por horário.
	 * @param filtroHorario
	 */
	public void setFiltroHorario(boolean filtroHorario) {
		this.filtroHorario = filtroHorario;
	}

	/** Indica se é gerado o diário da turma. 
	 * @return
	 */
	public boolean isFiltroDiarioTurma() {
		return filtroDiarioTurma;
	}

	/** Seta se é gerado o diário da turma. 
	 * @param filtroDiarioTurma
	 */
	public void setFiltroDiarioTurma(boolean filtroDiarioTurma) {
		this.filtroDiarioTurma = filtroDiarioTurma;
	}
	
	/**
	 * Retorna uma coleção de selectItens com os tipos de turma que podem ser
	 * criados para popular o select no formulário quando a turma estiver sendo
	 * criada sem ser a partir de uma solicitação
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTiposTurmaCombo(){
		return tiposTurmaCombo;
	}
	
	/** Retorna uma descrição textual do nível de ensino da turma.
	 * @return
	 */
	public String getNivelDescricao(){
		return NivelEnsino.getDescricao(this.nivelTurma);
	}

	public Integer getOrdenarPor() {
		return ordenarPor;
	}

	public void setOrdenarPor(Integer ordenarPor) {
		this.ordenarPor = ordenarPor;
	}

	public boolean isOrdenarBusca() {
		return ordenarBusca;
	}

	public void setOrdenarBusca(boolean ordenarBusca) {
		this.ordenarBusca = ordenarBusca;
	}
	
}