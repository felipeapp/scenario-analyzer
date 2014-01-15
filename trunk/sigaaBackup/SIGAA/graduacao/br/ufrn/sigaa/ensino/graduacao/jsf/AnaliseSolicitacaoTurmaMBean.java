/*
 * Superintend�ncia de Inform�tica - UFRN
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
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
 * Managed Bean para an�lise de solicita��es de abertura de turma
 *
 * @author Leonardo
 *
 */
@Component("analiseSolicitacaoTurma") @Scope("session")
public class AnaliseSolicitacaoTurmaMBean extends SigaaAbstractController<SolicitacaoTurma> {

	// Defini��es das Views
	/** Link da view de listagem de solicita��es. */
	public static final String JSP_LISTA_SOLICITACOES = "/graduacao/turma/solicitacoes.jsp";
	/** Link da view de impress�o de solicita��es. */
	public static final String JSP_IMPRESSAO_SOLICITACOES = "/graduacao/turma/impressao_solicitacoes.jsp";
	/** Link da view de solicita��o de altera��o de hor�rio. */
	public static final String JSP_SOLICITACAO_RENOVACAO_HORARIO = "/graduacao/turma/solicitar_alteracao_horario.jsp";

	/** Observa��o indicada na solicita��o de altera��o de hor�rio. */
	private String observacao;

	/** Lista de solicita��es de turma. */
	private List<SolicitacaoTurma> solicitacoes;

	//Atributos utilizados para filtrar as solicita��es
	/** Ano em que foi feita a solicita��o. */
	private Integer ano;

	/** Per�odo em que foi feita a solicita��o. */
	private Integer periodo;

	/** Tipo de turma a ser buscada. */
	private Integer tipoTurma;

	//Atributos utilizados para realizar busca nas solicita��es
	/** Id do curso escolhido. */
	private int idCurso;
	
	/** Id do componente escolhido. */
	private int idComponente;
	
	/** Hor�rio indicado. */
	private String horario;
	
	/** indica se o caso de uso � um atendimento a solicita��o de turma. */
	private boolean atendimentoSolicitacao = false;
	
	/** Docente que ministra a disciplina. */
	private Servidor docente = new Servidor();

	/** Filtros de busca. */
	private boolean filtroCurso, filtroComponente, filtroHorario, filtroDocente;

	/** calend�rio acad�mico vigente. */
	private CalendarioAcademico cal;
	
	/** 
	 * Este atributo guarda o c�digo da opera��o que esta sendo realizada
	 * 1 - Solicitar Altera��o de Hor�rio
	 * 2 - Negar Solicita��o de Turma
	 */
	private int operacao;
	
	/** Indica que o usu�rio realizou uma busca para filtrar as solicita��es. */
	private boolean realizouBusca;

	/** Define a opera��o de solicita��o de altera��o de hor�rio. */
	public static final int SOLICITAR_ALTERACAO_HORARIO = 1;
	/** Define a opera��o de nega��o de uma solicita��o. */
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
	 * Carrega o calend�rio correto para realizar an�lise das solicita��es
	 * @throws DAOException
	 */
	private void carregarCalendario() throws DAOException {
		cal = getCalendarioVigente();
		if( cal.getNivel() == NivelEnsino.TECNICO ){
			cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		}
	}
	
	/**
	 * Lista as solicita��es de turma regulares do per�odo correto.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Lista as solicita��es de turma de ensino individual do per�odo correto.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Lista as solicita��es de turma de ferias do per�odo correto.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Lista as solicita��es de todos os tipos do per�odo correto.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Retorna o usu�rio � lista de solicita��es, ap�s atender a cria��o da turma.
	 * <br/> M�todo n�o invocado por JSP�s
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
	 * M�todo respons�vel por carregar as solicita��es de acordo com os dados indicados.
	 * <br/> M�todo n�o invocado por JSP�s
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
	 * Verifica se alguma solicita��o n�o ser� buscada
	 */
	private void validarBuscaSolicitacoes() {
		if( (tipoTurma == null || tipoTurma == Turma.FERIAS) && (cal.getAnoFeriasVigente() == null || cal.getPeriodoFeriasVigente() == null) ){
			addMensagemWarning("O ano.per�odo de f�rias corrente n�o est� definido no calend�rio vigente, portanto n�o ser� poss�vel visualizar as solicita��es de turmas de f�rias.");
		}
	}

	/**
	 * Carrega as unidades que ser�o usadas na busca de solicita��es
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
	 * Carregas as solicita��es de turmas para o pr�ximo semestre
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
	 * Carrega as solicita��es de f�rias para o per�odo atual
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
	 * Carrega as solicita��es de ensino individual para o per�odo atual
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
				addMensagemErro("Informe o hor�rio.");
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
			addMensagemErro("Por favor, escolha algum crit�rio de busca.");
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

	/** Definem o Ano-Per�odo trabalhado nas solicita��es de turmas regulares.
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

	/** Leva o usu�rio � lista de solicita��es de turmas. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formListaSolicitacoes() {
		return forward(JSP_LISTA_SOLICITACOES);
	}

	/**
	 * Solicita ao coordenador que fez a solicita��o que altere o hor�rio da solicita��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
		 * valida��o: s� pode haver solicita��o de altera��o de hor�rio se a solicita��o estiver ABERTA
		 */
		if( obj.getSituacao() != SolicitacaoTurma.ABERTA && obj.getSituacao() != SolicitacaoTurma.SOLICITADA_ALTERACAO && obj.getSituacao() != SolicitacaoTurma.ATENDIDA_ALTERACAO && obj.getSituacao() != SolicitacaoTurma.SUGESTAO_DEPARTAMENTO)
			addMensagemErro("S� � poss�vel solicitar altera��o de hor�rio para solicita��es de turmas com a situa��o ABERTA.");
	
		/** verificando se est� no per�odo de cadastramento de turma regular, exceto quando a solicita��o de altera��o de hor�rio for 
		 * por parte do coordenador, podendo ser realizada independente do prazo cadastrado do calend�rio acad�mico.*/
		if(  obj.getPeriodo() <= 2 && obj.isTurmaRegular() && !cal.isPeriodoCadastroTurmas() && !cal.isPeriodoAjustesTurmas() && !isPortalCoordenadorGraduacao() && !cal.isPeriodoSugestaoTurmaChefe() ){
			addMensagemErro("N�o est� no per�odo de cadastro de turmas.");
		}
		/** verificando se est� no per�odo de cadastramento de turma de f�rias, exceto quando a solicita��o de altera��o de hor�rio for 
		 * por parte do coordenador, podendo ser realizada independente do prazo cadastrado do calend�rio acad�mico.*/
		if(  obj.getPeriodo() > 2 && !cal.isPeriodoCadastroTurmasFerias() && !isPortalCoordenadorGraduacao()){
			addMensagemErro("N�o est� no per�odo de cadastro de turmas de f�rias.");
		}

		if( obj.isTurmaEnsinoIndividual() && !cal.isPeriodoCadastroTurmaEnsinoIndiv() && !isPortalCoordenadorGraduacao()){
			Formatador fmt = Formatador.getInstance();
			addMensagemErro("N�o � permitido solicitar altera��o de hor�rio de turma de ensino individualizado fora do per�odo "
					+ "determinado no calend�rio universit�rio. O per�odo oficial para cadastro de turma de ensino individualizado estende-se de "
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
	 * Grava a opera��o que est� sendo realizada: Solicita��o de altera��o de hor�rio
	 * ou Nega��o de Solicita��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/solicitar_alteracao_horario.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String gravarOperacao() throws ArqException{

		if( isEmpty( obj.getObservacoes() ) ){
			if( isSolicitarAlteracaoHorario() )
				addMensagemErro("� obrigat�rio informar o hor�rio para o qual deseja que seja alterado e a justificativa.");
			else
				addMensagemErro("� obrigat�rio informar o motivo da nega��o da cria��o da turma.");
			return null;
		}

		try {
			if( isSolicitarAlteracaoHorario() )
				obj.setCodMovimento(SigaaListaComando.SOLICITAR_ALTERACAO_HORARIO);
			else if( isNegarSolicitacaoTurma() )
				obj.setCodMovimento(SigaaListaComando.NEGAR_SOLICITACAO_TURMA);
			else
				throw new NegocioException( "Opera��o n�o identificada. Contacte a administra��o do sistema." );

			executeWithoutClosingSession(obj, getCurrentRequest());
			addMessage( "Opera��o " + getDescricaoOperacao() + " realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
		}

		return gerenciarSolicitacoes();
	}

	/**
	 * Este m�todo vai para o form para o chefe de departamento entrar com a justificativa para negar solicita��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Persiste a nega��o de cria��o de turma.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMessage("Cria��o de turma negada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
		}
		return gerenciarSolicitacoes();
	}

	/**
	 * Retorna uma lsita com todos os cursos que solicitam disciplinas de acordo com o departamento do usu�rio logado.
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
	 * Retorna uma cole��o contendo todos os componentes que possuem solicita��o de turma realizadas.
	 * @return
	 */
	public Collection<ComponenteCurricular> getAllComponentesSolicitantes() {
		SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class);
		try {
			if (getUnidadeResponsabilidade() == null) {
				addMensagemErro("N�o foi poss�vel determinar a Unidade ao qual est� vinculado.");
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
	 * Redireciona o usu�rio para a p�gina de impress�o de solicita��es.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna a descri��o textual da opera��o atual.
	 * @return
	 */
	public String getDescricaoOperacao(){
		switch(operacao){
			case SOLICITAR_ALTERACAO_HORARIO:
				return "Solicitar Altera��o de Hor�rio";
			case NEGAR_SOLICITACAO_TURMA:
				return "Negar Solicita��o de Turma";
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
	 * Redireciona o usu�rio para a p�gina de solicita��es.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
