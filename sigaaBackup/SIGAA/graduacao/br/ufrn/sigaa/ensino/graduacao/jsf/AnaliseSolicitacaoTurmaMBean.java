/*
 * Superintendência de Informática - UFRN
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 07/02/2007
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isAllNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoTurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed Bean para análise de solicitações de abertura de turma
 *
 * @author Leonardo
 *
 */
@Component("analiseSolicitacaoTurma") @Scope("session")
public class AnaliseSolicitacaoTurmaMBean extends SigaaAbstractController<SolicitacaoTurma> {

	// Definições das Views
	/** Link da view de listagem de solicitações. */
	public static final String JSP_LISTA_SOLICITACOES = "/graduacao/turma/solicitacoes.jsp";
	/** Link da view de impressão de solicitações. */
	public static final String JSP_IMPRESSAO_SOLICITACOES = "/graduacao/turma/impressao_solicitacoes.jsp";
	/** Link da view de solicitação de alteração de horário. */
	public static final String JSP_SOLICITACAO_RENOVACAO_HORARIO = "/graduacao/turma/solicitar_alteracao_horario.jsp";

	/** Observação indicada na solicitação de alteração de horário. */
	private String observacao;

	/** Lista de solicitações de turma. */
	private List<SolicitacaoTurma> solicitacoes;

	//Atributos utilizados para filtrar as solicitações
	/** Ano em que foi feita a solicitação. */
	private Integer ano;

	/** Período em que foi feita a solicitação. */
	private Integer periodo;

	/** Tipo de turma a ser buscada. */
	private Integer tipoTurma;

	//Atributos utilizados para realizar busca nas solicitações
	/** Id do curso escolhido. */
	private int idCurso;
	
	/** Id do componente escolhido. */
	private int idComponente;
	
	/** Horário indicado. */
	private String horario;
	
	/** indica se o caso de uso é um atendimento a solicitação de turma. */
	private boolean atendimentoSolicitacao = false;
	
	/** Docente que ministra a disciplina. */
	private Servidor docente = new Servidor();

	/** Filtros de busca. */
	private boolean filtroCurso, filtroComponente, filtroHorario, filtroDocente;

	/** calendário acadêmico vigente. */
	private CalendarioAcademico cal;
	
	/** 
	 * Este atributo guarda o código da operação que esta sendo realizada
	 * 1 - Solicitar Alteração de Horário
	 * 2 - Negar Solicitação de Turma
	 */
	private int operacao;
	
	/** Indica que o usuário realizou uma busca para filtrar as solicitações. */
	private boolean realizouBusca;

	/** Define a operação de solicitação de alteração de horário. */
	public static final int SOLICITAR_ALTERACAO_HORARIO = 1;
	/** Define a operação de negação de uma solicitação. */
	public static final int NEGAR_SOLICITACAO_TURMA = 2;

	public AnaliseSolicitacaoTurmaMBean() throws DAOException{
		obj = new SolicitacaoTurma();
		carregarCalendario();
	}

	/**
	 * Limpa os dados do MBean.
	 */
	private void clear(){
		filtroCurso = false;
		filtroComponente = false;
		filtroHorario = false;
		filtroDocente = false;
		idCurso = 0;
		idComponente = 0;
		horario = "";
		docente = new Servidor();
		solicitacoes = new ArrayList<SolicitacaoTurma>();
	}

	/**
	 * Carrega o calendário correto para realizar análise das solicitações
	 * @throws DAOException
	 */
	private void carregarCalendario() throws DAOException {
		cal = getCalendarioVigente();
		if( cal.getNivel() == NivelEnsino.TECNICO ){
			cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		}
	}
	
	/**
	 * Lista as solicitações de turma regulares do período correto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarSolicitacoesRegulares() throws ArqException{
		carregarCalendario();
		tipoTurma = Turma.REGULAR;
		return gerenciarSolicitacoes();
	}


	/**
	 * Lista as solicitações de turma de ensino individual do período correto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/graduacao/departamento.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarSolicitacoesEnsinoIndividual() throws ArqException{
		carregarCalendario();
		tipoTurma = Turma.ENSINO_INDIVIDUAL;
		return gerenciarSolicitacoes();
	}

	/**
	 * Lista as solicitações de turma de ferias do período correto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarSolicitacoesFerias() throws ArqException{
		carregarCalendario();
		tipoTurma = Turma.FERIAS;
		return gerenciarSolicitacoes();
	}

	/**
	 * Lista as solicitações de todos os tipos do período correto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/outras_turmas.jsp</li>
	 * <li>/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * <li>/sigaa.war/graduacao/departamento.jsp</li>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarSolicitacoesTodas() throws ArqException {
		carregarCalendario();

		periodo = null;
		ano = null;
		tipoTurma = null;
		return gerenciarSolicitacoes();
	}
	
	/** Retorna o usuário à lista de solicitações, após atender a criação da turma.
	 * <br/> Método não invocado por JSP´s
	 * @return
	 * @throws ArqException
	 */
	public String retornaAtencimendoSolicitacao() throws ArqException {
		if (realizouBusca)
			return buscar();
		else
			return gerenciarSolicitacoes();
	}

	/**
	 * Método responsável por carregar as solicitações de acordo com os dados indicados.
	 * <br/> Método não invocado por JSP´s
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarSolicitacoes() throws ArqException {
		realizouBusca = false;
		clear();
		
		validarBuscaSolicitacoes();
		
		List<Unidade> unidades = new ArrayList<Unidade>();
		carregarUnidadesParaBusca(unidades);
		
		SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class);
			
		if (tipoTurma == null) {
			carregarSolicitacoesTurmaProximoSemestre(unidades, dao);
			carregarSolicitacoesEnsinoIndividual(unidades, dao);
			carregarSolicitacoesFerias(unidades, dao);
		} else {
			carregarSolicitacoesByTipo(unidades, dao);
		}
		
		for (SolicitacaoTurma sol : solicitacoes) {
			sol.setCalendario(cal);
		}
		
		prepareMovimento(SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA);
		return formListaSolicitacoes();
	}

	/**
	 * Carregas todas as solicitacoes de acordo com um tipo de turma
	 * 
	 * @param unidades
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarSolicitacoesByTipo(List<Unidade> unidades, SolicitacaoTurmaDao dao) throws DAOException {
		
		switch (tipoTurma) {
		case Turma.REGULAR:
			carregaAnoPeriodoRegular();
			break;

		case Turma.FERIAS:
			ano = cal.getAnoFeriasVigente();
			periodo = cal.getPeriodoFeriasVigente();
			break;

		case Turma.ENSINO_INDIVIDUAL:
			ano = cal.getAno();
			periodo = cal.getPeriodo();
			break;
		default:
			break;
		}

		solicitacoes = dao.filter(getUsuarioLogado(), unidades, ano, periodo, tipoTurma, null, null, null, null);
	}

	/**
	 * Verifica se alguma solicitação não será buscada
	 */
	private void validarBuscaSolicitacoes() {
		if( (tipoTurma == null || tipoTurma == Turma.FERIAS) && (cal.getAnoFeriasVigente() == null || cal.getPeriodoFeriasVigente() == null) ){
			addMensagemWarning("O ano.período de férias corrente não está definido no calendário vigente, portanto não será possível visualizar as solicitações de turmas de férias.");
		}
	}

	/**
	 * Carrega as unidades que serão usadas na busca de solicitações
	 * 
	 * @param unidades
	 */
	private void carregarUnidadesParaBusca(List<Unidade> unidades) {
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)) {
			Curso cursoCoord = getCursoAtualCoordenacao();
			if(isEmpty(cursoCoord)) {
				unidades.add(getUnidadeResponsabilidade());
			}
			else {
				unidades.add(cursoCoord.getUnidadeCoordenacao());
			}
			unidades.add(getUsuarioLogado().getVinculoAtivo().getUnidade());
		} 
		
		if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO)) {
			unidades.add(getUsuarioLogado().getVinculoAtivo().getUnidade());
		}
	}

	/**
	 * Carregas as solicitações de turmas para o próximo semestre
	 * 
	 * @param unidades
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarSolicitacoesTurmaProximoSemestre(List<Unidade> unidades, SolicitacaoTurmaDao dao) throws DAOException {
		
		int anoRegular = 0; 
		int periodoRegular = 0;
		
		if( isAllNotEmpty(cal.getPeriodoNovasTurmas(), cal.getAnoNovasTurmas() != null )) {
			anoRegular = cal.getAnoNovasTurmas();
			periodoRegular = cal.getPeriodoNovasTurmas();
		} else {
			anoRegular = cal.getProximoAnoPeriodoRegular().getAno();
			periodoRegular = cal.getProximoAnoPeriodoRegular().getPeriodo();
		}
		
		solicitacoes = dao.filter(getUsuarioLogado(), unidades, anoRegular, periodoRegular, Turma.REGULAR, null, null, null, null);
	}

	/**
	 * Carrega as solicitações de férias para o período atual
	 * 
	 * @param unidades
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarSolicitacoesFerias(List<Unidade> unidades, SolicitacaoTurmaDao dao) throws DAOException {
		
		if (isAllNotEmpty( cal.getAnoFeriasVigente(), cal.getPeriodoFeriasVigente() )) {
			solicitacoes.addAll( dao.filter(getUsuarioLogado(), unidades, cal.getAnoFeriasVigente(), cal.getPeriodoFeriasVigente(), Turma.FERIAS, null, null, null, null) );
		}
		
	}

	/**
	 * Carrega as solicitações de ensino individual para o período atual
	 * 
	 * @param unidades
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarSolicitacoesEnsinoIndividual(List<Unidade> unidades, SolicitacaoTurmaDao dao) throws DAOException {
		solicitacoes.addAll( dao.filter(getUsuarioLogado(), unidades, cal.getAno(), cal.getPeriodo(), Turma.ENSINO_INDIVIDUAL, null, null, null, null) );
	}

	@Override
	public String buscar() throws DAOException {
		realizouBusca = true;
		Integer curso = null;
		Integer componente = null;
		String horario = null;
		Servidor servidor = null;

		if( filtroCurso && idCurso > 0 ){
			curso = idCurso;
		}
		if( filtroComponente && idComponente > 0){
			componente = idComponente;
		}
		if( filtroHorario ){
			if( isEmpty(this.horario) ){
				addMensagemErro("Informe o horário.");
			}
			horario = this.horario.trim().toUpperCase();
		}
		if( filtroDocente ){
			if( isEmpty(this.docente) ){
				addMensagemErro("Informe o docente.");
			}
			servidor = this.docente;
		}

		if( curso == null && componente == null && horario == null && servidor == null ){
			addMensagemErro("Por favor, escolha algum critério de busca.");
			return null;
		}else{
			
			List<Unidade> unidades = new ArrayList<Unidade>();
			unidades.add(getUnidadeResponsabilidade());
			carregarCalendario();
			carregaAnoPeriodoRegular();
			SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class);
			solicitacoes = dao.filter(getUsuarioLogado(), unidades, ano, periodo, tipoTurma, curso, componente, horario, servidor);
		}
		
		for (SolicitacaoTurma sol : solicitacoes) {
			sol.setCalendario(cal);
		}

		return formListaSolicitacoes();
	}

	/** Definem o Ano-Período trabalhado nas solicitações de turmas regulares.
	 * @throws DAOException
	 */
	private void carregaAnoPeriodoRegular() throws DAOException {
		if( isAllNotEmpty(cal.getPeriodoNovasTurmas(), cal.getAnoNovasTurmas())) {
			ano = cal.getAnoNovasTurmas();
			periodo = cal.getPeriodoNovasTurmas();
		} else {
			ano = cal.getProximoAnoPeriodoRegular().getAno();
			periodo = cal.getProximoAnoPeriodoRegular().getPeriodo();
		}
	}

	/** Leva o usuário à lista de solicitações de turmas. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formListaSolicitacoes() {
		return forward(JSP_LISTA_SOLICITACOES);
	}

	/**
	 * Solicita ao coordenador que fez a solicitação que altere o horário da solicitação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String solicitarAlteracaoHorario() throws ArqException{
		checkRole( SigaaPapeis.GESTOR_TURMAS_UNIDADE );
		int id = getParameterInt("id");
		GenericDAO dao = getGenericDAO();
		if ( id != 0 )
			obj = dao.findByPrimaryKey(id, SolicitacaoTurma.class);

		carregarCalendario();
		
		/**
		 * validação: só pode haver solicitação de alteração de horário se a solicitação estiver ABERTA
		 */
		if( obj.getSituacao() != SolicitacaoTurma.ABERTA && obj.getSituacao() != SolicitacaoTurma.SOLICITADA_ALTERACAO && obj.getSituacao() != SolicitacaoTurma.ATENDIDA_ALTERACAO && obj.getSituacao() != SolicitacaoTurma.SUGESTAO_DEPARTAMENTO)
			addMensagemErro("Só é possível solicitar alteração de horário para solicitações de turmas com a situação ABERTA.");
	
		/** verificando se está no período de cadastramento de turma regular, exceto quando a solicitação de alteração de horário for 
		 * por parte do coordenador, podendo ser realizada independente do prazo cadastrado do calendário acadêmico.*/
		if(  obj.getPeriodo() <= 2 && obj.isTurmaRegular() && !cal.isPeriodoCadastroTurmas() && !cal.isPeriodoAjustesTurmas() && !isPortalCoordenadorGraduacao() && !cal.isPeriodoSugestaoTurmaChefe() ){
			addMensagemErro("Não está no período de cadastro de turmas.");
		}
		/** verificando se está no período de cadastramento de turma de férias, exceto quando a solicitação de alteração de horário for 
		 * por parte do coordenador, podendo ser realizada independente do prazo cadastrado do calendário acadêmico.*/
		if(  obj.getPeriodo() > 2 && !cal.isPeriodoCadastroTurmasFerias() && !isPortalCoordenadorGraduacao()){
			addMensagemErro("Não está no período de cadastro de turmas de férias.");
		}

		if( obj.isTurmaEnsinoIndividual() && !cal.isPeriodoCadastroTurmaEnsinoIndiv() && !isPortalCoordenadorGraduacao()){
			Formatador fmt = Formatador.getInstance();
			addMensagemErro("Não é permitido solicitar alteração de horário de turma de ensino individualizado fora do período "
					+ "determinado no calendário universitário. O período oficial para cadastro de turma de ensino individualizado estende-se de "
					+ fmt.formatarData(cal.getInicioCadastroTurmaEnsinoIndiv()) + " a " + fmt.formatarData(cal.getFimCadastroTurmaEnsinoIndiv()) + ".");
			return null;
		}
		
		if( hasErrors() )
			return null;

		operacao = SOLICITAR_ALTERACAO_HORARIO;
		prepareMovimento( SigaaListaComando.SOLICITAR_ALTERACAO_HORARIO );
		return forward(JSP_SOLICITACAO_RENOVACAO_HORARIO);
	}

	/**
	 * Grava a operação que está sendo realizada: Solicitação de alteração de horário
	 * ou Negação de Solicitação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitar_alteracao_horario.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gravarOperacao() throws ArqException{

		if( isEmpty( obj.getObservacoes() ) ){
			if( isSolicitarAlteracaoHorario() )
				addMensagemErro("É obrigatório informar o horário para o qual deseja que seja alterado e a justificativa.");
			else
				addMensagemErro("É obrigatório informar o motivo da negação da criação da turma.");
			return null;
		}

		try {
			if( isSolicitarAlteracaoHorario() )
				obj.setCodMovimento(SigaaListaComando.SOLICITAR_ALTERACAO_HORARIO);
			else if( isNegarSolicitacaoTurma() )
				obj.setCodMovimento(SigaaListaComando.NEGAR_SOLICITACAO_TURMA);
			else
				throw new NegocioException( "Operação não identificada. Contacte a administração do sistema." );

			executeWithoutClosingSession(obj, getCurrentRequest());
			addMessage( "Operação " + getDescricaoOperacao() + " realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
		}

		return gerenciarSolicitacoes();
	}

	/**
	 * Este método vai para o form para o chefe de departamento entrar com a justificativa para negar solicitação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarNegarSolicitacao() throws ArqException {
		checkRole( SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO );

		populateObj(true);
		
		carregarCalendario();

		obj.setCalendario(cal);
		
		ListaMensagens listaErros = obj.validarNegacaoSolicitacao();
		
		if (listaErros.isErrorPresent())
			addMensagens(listaErros);

		if( hasErrors() )
			return null;

		operacao = NEGAR_SOLICITACAO_TURMA;
		prepareMovimento( SigaaListaComando.NEGAR_SOLICITACAO_TURMA );
		return forward(JSP_SOLICITACAO_RENOVACAO_HORARIO);

	}

	/**
	 * Persiste a negação de criação de turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String negarSolicitacao() throws ArqException{
		try {
			obj.setCodMovimento(SigaaListaComando.NEGAR_SOLICITACAO_TURMA);
			executeWithoutClosingSession(obj, getCurrentRequest());
			addMessage("Criação de turma negada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
		}
		return gerenciarSolicitacoes();
	}

	/**
	 * Retorna uma lsita com todos os cursos que solicitam disciplinas de acordo com o departamento do usuário logado.
	 * @return
	 */
	public Collection<Curso> getAllCursosSolicitantes() {
		SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class);
		try {
			return dao.findCursosSolicitantesDepartamento(getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), ano, periodo, tipoTurma);
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Curso>();
		}
	}

	/**
	 * Retorna os cursos solicitantes em forma de combo.
	 * @see #getAllCursosSolicitantes()
	 * @return
	 */
	public Collection<SelectItem> getAllCursosSolicitantesCombo(){
		return toSelectItems(getAllCursosSolicitantes(), "id", "descricaoCompleta");
	}

	/**
	 * Retorna uma coleção contendo todos os componentes que possuem solicitação de turma realizadas.
	 * @return
	 */
	public Collection<ComponenteCurricular> getAllComponentesSolicitantes() {
		SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class);
		try {
			if (getUnidadeResponsabilidade() == null) {
				addMensagemErro("Não foi possível determinar a Unidade ao qual está vinculado.");
				return null;
			}
			return dao.findComponentesSolicitantesDepartamento(getUnidadeResponsabilidade().getId(), ano, periodo, tipoTurma);
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<ComponenteCurricular>();
		}
	}

	/**
	 * Retorna os componentes solicitantes em forma de combo.
	 * @see #getAllComponentesSolicitantes()
	 * @return
	 */
	public Collection<SelectItem> getAllComponentesSolicitantesCombo(){
		return toSelectItems(getAllComponentesSolicitantes(), "id", "descricaoResumida");
	}

	/**
	 * Redireciona o usuário para a página de impressão de solicitações.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String imprimir(){
		return forward(JSP_IMPRESSAO_SOLICITACOES);
	}

	public int getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	public List<SolicitacaoTurma> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(List<SolicitacaoTurma> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
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

	public Integer getTipoTurma() {
		return tipoTurma;
	}

	public void setTipoTurma(Integer tipoTurma) {
		this.tipoTurma = tipoTurma;
	}

	public int getIdComponente() {
		return idComponente;
	}

	public void setIdComponente(int idComponente) {
		this.idComponente = idComponente;
	}

	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public boolean isFiltroComponente() {
		return filtroComponente;
	}

	public void setFiltroComponente(boolean filtroComponente) {
		this.filtroComponente = filtroComponente;
	}

	public boolean isFiltroHorario() {
		return filtroHorario;
	}

	public void setFiltroHorario(boolean filtroHorario) {
		this.filtroHorario = filtroHorario;
	}

	public boolean isFiltroDocente() {
		return filtroDocente;
	}

	public void setFiltroDocente(boolean filtroDocente) {
		this.filtroDocente = filtroDocente;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	/**
	 * Retorna a descrição textual da operação atual.
	 * @return
	 */
	public String getDescricaoOperacao(){
		switch(operacao){
			case SOLICITAR_ALTERACAO_HORARIO:
				return "Solicitar Alteração de Horário";
			case NEGAR_SOLICITACAO_TURMA:
				return "Negar Solicitação de Turma";
		}
		return null;
	}

	public boolean isSolicitarAlteracaoHorario(){
		return operacao == SOLICITAR_ALTERACAO_HORARIO;
	}

	public boolean isNegarSolicitacaoTurma(){
		return operacao == NEGAR_SOLICITACAO_TURMA;
	}
	
	/** Retorna o curso selecionado na busca.
	 * @return
	 * @throws DAOException 
	 */
	public Curso getCurso() throws DAOException {
		return getGenericDAO().findByPrimaryKey(idCurso, Curso.class);
	}
	
	/** Retorna o componente curricular selecionado na busca.
	 * @return
	 * @throws DAOException 
	 */
	public ComponenteCurricular getComponenteCurricular() throws DAOException{
		return getGenericDAO().findByPrimaryKey(idComponente, ComponenteCurricular.class);
	}
	
	/**
	 * Redireciona o usuário para a página de solicitações.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * @throws ArqException 
	 */
	public String cancelar() {
		try {
			
			if(atendimentoSolicitacao){
				atendimentoSolicitacao = false;
				return gerenciarSolicitacoes();
			}
		} catch (ArqException e) {
			e.printStackTrace();
			return super.cancelar();
		}	
		return super.cancelar();
	}

	public boolean isAtendimentoSolicitacao() {
		return atendimentoSolicitacao;
	}

	public void setAtendimentoSolicitacao(boolean atendimentoSolicitacao) {
		this.atendimentoSolicitacao = atendimentoSolicitacao;
	}
}
