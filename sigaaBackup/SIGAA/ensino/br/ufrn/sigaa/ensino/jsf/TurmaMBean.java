/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/05/2010
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.PreRemove;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoTurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.AlteracaoTurma;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.jsf.EstruturaCurricularMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.SeletorComponenteCurricular;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.negocio.PeriodoAcademicoHelper;

/** Controller abstrato responsável por operações com turmas.
 * 
 * @author Édipo Elder F. Melo
 *
 */
public abstract class TurmaMBean extends SigaaAbstractController<Turma> implements SeletorComponenteCurricular, OperadorHorarioTurma, OperadorDocenteTurma {
	
	// Definições das views 
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_RESUMO = "/ensino/turma/view.jsp";
	/** Define o link para adição de reservas à outras turmas*/
	public static final String JSP_OUTRAS_TURMAS = "/graduacao/turma/outras_turmas.jsf";
	/** Define o link para adição de reservas à turma. */
	public static final String JSP_RESERVAS = "/graduacao/turma/form_reservas.jsf";
	
	/** Lista de componentes curriculares para o docente escolher para o qual será criada a turma. */ 
	private Collection<ComponenteCurricular> componentes;
	/** Coleção de turmas criadas para cada subunidade quando a disciplina é de bloco */
	private Collection<Turma> turmasSubunidades = new ArrayList<Turma>();
	/** Solicitação de turma atendida pela criação desta turma. */
	private SolicitacaoTurma solicitacao;
	/** Dados iniciais da turma sendo alterada */
	private AlteracaoTurma alteracaoTurma;	
	/**
	 * Coleção de selectItens com os tipos de turma que podem ser criados para
	 * popular o select no formulário quando a turma estiver sendo criada sem
	 * ser a partir de uma solicitação
	 */
	private Collection<SelectItem> tiposTurmaCombo = new ArrayList<SelectItem>();
	/**
	 * Indica, na atualização de turma, se esta operação deve permitir adicionar
	 * outras reservas de solicitações de turma ainda não atendidas a esta
	 * turma. As outras reservas devem ser no mesmo horário desta turma e é
	 * necessário que esteja no período de cadastro de turma regular ou de ferias.
	 */
	private boolean adicionarOutrasReservas;
	/** Indica que é apenas visualização, para configurar os botões da JSP de resumo */
	private boolean apenasVisualizacao;
	/** Combo de cursos (quando o cadastro é de turma de um curso de convênio) */
	private Collection<SelectItem> cursosCombo;
	/** Conjunto de turmas agrupadas. */
	private Set<Turma> turmasAgrupadoras = new LinkedHashSet<Turma>();
	/** Curso a ser inserido na reserva de vagas.*/
	private int idCurso;
	/** ID da solicitação de turma atendida pela criação desta turma. */
	private int idSolicitacao;
	/** ID da sub-unidade selecionada na seleção dos docentes. */
	private int idSubunidade;
	/** Reserva de vagas desta turma para um curso. */
	private ReservaCurso reserva = new ReservaCurso();
	/** Indica se o cadastro de turmas é de ensino a distância. */
	private boolean turmaEad;
	/** Guarda o cógido original da turma, antes da alteração de dados. */
	protected String codigoTurmaBeforeUpdate;

	/** Construtor padrão. */
	public TurmaMBean() {
		clear();
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

	/** Limpa os atributos deste controller. */
	protected void clear() {
		setConfirmButton("Cadastrar");
		initObj();
	}

	/** Inicializa os atributos do controller. */
	protected void initObj(){
		apenasVisualizacao = false;
		obj = new Turma();
		obj.setSituacaoTurma(new SituacaoTurma());
		obj.setDisciplina(new ComponenteCurricular());
		obj.setCurso(new Curso());
		obj.setCampus( new CampusIes() );
		reserva = new ReservaCurso();
		adicionarOutrasReservas = false;
		turmasAgrupadoras = null;
		solicitacao = null;
		turmaEad = false;
		setReadOnly(false);
	}
	
	// forms padrões a todos os níveis de ensino
	
	/** Redireciona o usuário para o formulário de horário da turma.<br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String formHorarios() {
		HorarioTurmaMBean horarioBean = getMBean("horarioTurmaBean");
		return horarioBean.populaHorarioTurma(this, "Cadastro de Turma", obj);
	}
	
	/** Redireciona o usuário para o formulário de docentes da turma.<br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String formDocentes(){
		try {
			beforeDefinirDocentesTurma();
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
		DocenteTurmaMBean docenteBean = getMBean("docenteTurmaBean");
		return docenteBean.populaDocentesTurma(this, "Cadastro de Turma", obj, isTurmaEad());
	}
	
	// forms a definir por cada nível de ensino
	/** Redireciona o usuário para o formulário de seleção de componente curricular para o qual a turma será criada. */
	public abstract String formSelecaoComponente();
	/** Redireciona o usuário para o formulário de dados gerais da turma. */
	public abstract String formDadosGerais();
	/** Redireciona o usuário para o formulário de confirmação do cadastro da turma. */
	public abstract String formConfirmacao();
	/** Redireciona o usuário para o formulário de confirmação da remoção da turma. */
	public abstract String formConfirmacaoRemover();
	
	// métodos a executar antes de cada passo
	/** Método executado antes de exibir o formulário para seleção de componente curricular. */
	public abstract void beforeSelecionarComponente() throws ArqException;
	/** Método executado antes de exibir o formulário para definição dos docentes da turma. */
	public abstract void beforeDefinirDocentesTurma() throws ArqException;
	/** Método executado antes de exibir o formulário de dados gerais da turma. */
	public abstract void beforeDadosGerais() throws ArqException;
	/** Método executado antes de exibir o formulário para confirmação do cadastro. */
	public abstract void beforeConfirmacao() throws ArqException;
	/** Método executado antes de exibir o formulário alteração de dados de uma turma. */
	public abstract void beforeAtualizarTurma() throws ArqException;
	
	// métodos para validações
	/** Valida os dados gerais da turma informados pelo usuário. */
	public abstract void validaDadosGerais(ListaMensagens erros) throws DAOException;
	/** Valida os docentes da turma informados pelo usuário. */
	public abstract void validaDocentesTurma(ListaMensagens erros) throws DAOException;
	/** Valida os horários da turma informados pelo usuário. */
	public abstract void validaHorariosTurma(ListaMensagens erros) throws DAOException;
	
	// métodos para verificação de permissões
	/** Verifica se as permissões do usuário para atualizar uma turma. */
	public abstract void checkRoleAtualizarTurma() throws ArqException;
	/** Verifica se as permissões do usuário para remover uma turma. */
	public abstract void checkRolePreRemover() throws ArqException;
	/** Verifica se as permissões do usuário para cadastrar uma turma. */
	public abstract void checkRoleCadastroTurmaSemSolicitacao(ListaMensagens erros) throws SegurancaException;
	
	// métodos de cadastro
	/** CAdastrar, altera ou remove a turma. */
	public abstract String cadastrar() throws ArqException;
	
	// métodos de controle de fluxo 
	/** Indica se o usuário tem permissão para alterar os horários da turma. */
	public abstract boolean isPodeAlterarHorarios() ;
	/** Indica se a turma tem horários. */
	public abstract boolean isDefineHorario();
	/** Indica se a turma terá docentes. */
	public abstract boolean isDefineDocentes();
	/** Retorna o nível de ensino em uso no subsistema atual. */
	@Override
	public abstract char getNivelEnsino();
	
	// fluxo de cadastro de turma
	
	/** Inicia o cadastro de uma turma sem solicitação.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public final String preCadastrar() throws ArqException, NegocioException {
		return iniciarTurmaSemSolicitacao();
	}
	
	/**
	 * Inicia a criação de turma sem solicitação, verificando se o usuário tem a permissão para tal operação.
	 * Caso possua, redireciona o usuário para o formulário de seleção de componente curricular.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public final String iniciarTurmaSemSolicitacao() throws ArqException, NegocioException {
		checkRoleCadastroTurmaSemSolicitacao(erros);
		clear();
		initObj();
		beforeSelecionarComponente();
		if (hasErrors())
			return null;
		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA);
		return formSelecaoComponente();
	}
	
	/** Define o componente curricular da disciplina e retorna ao usuário o formulário para definição dos dados gerais da turma.<br/>Método não invocado por JSP´s.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.SeletorComponenteCurricular#selecionaComponenteCurricular(br.ufrn.sigaa.ensino.dominio.ComponenteCurricular)
	 */
	@Override
	public final String selecionaComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		if (!componente.isPermiteCriarTurma()){
			addMensagemErro("Somente Componentes Curriculares do tipo Disciplina, Módulo ou Atividades Coletivas podem ser utilizados para criar turma.");
			return null;
		}
		if (obj == null)
			obj = new Turma();
		
		GenericDAO dao = getGenericDAO();
		componente = dao.findAndFetch(componente.getId(), ComponenteCurricular.class, "unidade");
		
		if(!componente.getUnidade().isUnidadeAcademica()) {
			addMensagemErro("Este componente curricular não pode ser selecionado, pois está associado a uma unidade não acadêmica: " + componente.getUnidade().getCodigoNome());
			return null;
		}
		
		if (obj.isDistancia() != componente.isDistancia()){
			addMensagemErro("O componente curricular (" + (componente.isDistancia() ? "EAD" :"presencial")+") não é compatível com a modalidade de educação da turma (" + (obj.isDistancia() ? "EAD" :"presencial")+")");
			return null;
		}
		
		componente.getUnidade().getId();
		componente.getUnidade().getGestoraAcademica().getId();
		this.turmasAgrupadoras = null;
		obj.setDisciplina(componente);
		beforeDadosGerais();
		return formDadosGerais();
	}
	
	/**
	 * Submete a tela de dados gerais de criação de turmas para validação.
	 * Uma vez validados os dados, o usuário será redirecionado para definição de horário da turma ou para a definição de docentes.
	 * Caso nenhuma destas definições sejam necessárias para a turma, o usuário será direcionado para a tela de confirmação do cadastro da turma.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public final String submeterDadosGerais() throws ArqException {
		validaDadosGerais(erros);
		if (hasErrors())
			return null;
		if (isDefineHorario()) {
			return formHorarios();
		} else if (isDefineDocentes()) {
			return formDocentes();
		} else if (isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			setReserva(new ReservaCurso());
			getReserva().setTurma( obj );
			return forward(JSP_RESERVAS);
		} else {
			return formConfirmacao();
		}
	}
	
	/** Define o horário da turma e faz outras validações específicas ao nível de ensino da turma.
	 * Caso validado, o usuário será direcionado para o formulário de definição de docentes ou 
	 * para a confirmação da criação da turma quando é definido que a turma não terá docentes.<br/>Método não invocado por JSP´s.
	 * @see br.ufrn.sigaa.ensino.jsf.OperadorDocenteTurma#defineHorariosDocenteTurma(java.util.List)
	 */
	public final String defineHorariosTurma(Collection<HorarioTurma> horarios) throws ArqException{
		validaHorariosTurma(erros);
		if (hasOnlyErrors())
			return null;
		for (HorarioTurma horario : horarios)
			horario.setTurma(obj);
		obj.setHorarios((List<HorarioTurma>) horarios);
		obj.setDescricaoHorario(HorarioTurmaUtil.formatarCodigoHorarios(obj));
		if (isDefineDocentes()) {
			verificaAlteracaoHorarioDocente();
			hasErrors();
			beforeDefinirDocentesTurma();
			return formDocentes();
		} else if (isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			setReserva(new ReservaCurso());
			getReserva().setTurma( obj );
			return forward(JSP_RESERVAS);
		} else {
			beforeConfirmacao();
			return formConfirmacao();
		}
	}
	

	/** Método invocado quando o usuário deseja retornar do formulário de reserva de espaço físico.
	 * @see br.ufrn.sigaa.infraestrutura.jsf.OperadorReservaRecursoFisico#reservaRecursoFisicoVoltar()
	 */
	public String reservaRecursoFisicoVoltar(){
		if (isDefineHorario()) {
			HorarioTurmaMBean horarioBean = getMBean("horarioTurmaBean");
			return horarioBean.populaHorarioTurma(this, "Cadastro de Turma", obj);
		} else {
			return formDadosGerais();
		}
	}
	
	/** Método invocado quando o usuário deseja retornar do formulário de reserva vagas para o curso.
	 * @see br.ufrn.sigaa.infraestrutura.jsf.OperadorReservaRecursoFisico#reservaRecursoFisicoVoltar()
	 */
	public String reservaCursoVoltar(){
		if (isDefineDocentes()) {
			return formDocentes();
		} else if (isDefineHorario()) {
			HorarioTurmaMBean horarioBean = getMBean("horarioTurmaBean");
			return horarioBean.populaHorarioTurma(this, "Cadastro de Turma", obj);
		} else {
			return formDadosGerais();
		}
	}
	
	/**
	 * Verifica se os horários dos docentes da turma correspondem ao horário da
	 * turma. Caso o horário da turma tenha sido alterado, os horários dos
	 * docentes deverão ser alterados também.
	 * @throws DAOException 
	 */
	protected void verificaAlteracaoHorarioDocente() throws DAOException {
		HashMap<Integer, ParametrosGestoraAcademica> cacheParam = new HashMap<Integer, ParametrosGestoraAcademica>();
		
		for (DocenteTurma dt : obj.getDocentesTurmas()) {
			TurmaValidator.validaHorariosDocenteTurma(obj, dt, erros, getUsuarioLogado(), cacheParam);
		}
	}

	/** Define os docentes da turma, fazendo validações específicas ao nível de ensino da turma e redirecionando
	 * o usuário para a confirmação dos dados da criação de turma.<br/>Método não invocado por JSP´s.
	 * @see br.ufrn.sigaa.ensino.jsf.OperadorDocenteTurma#defineDocentesTurma(java.util.Collection)
	 */
	public final String defineDocentesTurma(Collection<DocenteTurma> docentesTurma) throws ArqException{
		validaDocentesTurma(erros);
		if (hasOnlyErrors())
			return null;
		obj.setDocentesTurmas((Set<DocenteTurma>) docentesTurma);
		// turma de férias não tem reservas
		if ( obj.isGraduacao() &&
				(isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO)  || (isUserInRole(SigaaPapeis.SEDIS) && obj.isEad()))) {
			return formReservaCurso();
		} else {
			return formConfirmacao();
		}
	}
	
	/** Direciona o usuário para o formulário de reserva de curso.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 * @throws DAOException 
	 */
	public final String formReservaCurso() throws DAOException {
		setReserva(new ReservaCurso());
		getReserva().setTurma( obj );
		EstruturaCurricularMBean mBean = getMBean("curriculo");
		mBean.initObj();
		return forward(JSP_RESERVAS);
	}
	
	// --- Fim do Fluxo de Cadastro de Turma ---
	
	// Fluxo de dados para alteração da turma
	
	/**
	 * Inicia o caso de uso de atualização de turmas.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	@Override
	public final String atualizar() throws ArqException {
		
		setReadOnly(false);
		setApenasVisualizacao(false);
		
		populateObj(true);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			obj = new Turma();
			return null;
		}
		
		// atualiza as matrículas na turma
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		obj.setMatriculasDisciplina(matriculaDao.findByTurma(obj.getId()));
		// atualiza o total de alunos matriculados
		SituacaoMatricula[] situacoes = SituacaoMatricula.getSituacoesAtivas().toArray(new SituacaoMatricula[SituacaoMatricula.getSituacoesAtivas().size()]);
		obj.setQtdMatriculados( matriculaDao.countMatriculasByTurma(obj, situacoes) );
		
		// carrega a solicitação da turma
		SolicitacaoTurmaDao solTurmaDao = getDAO(SolicitacaoTurmaDao.class);
		SolicitacaoTurma solTurma = solTurmaDao.findByTurma(obj.getId());
		obj.setSolicitacao(solTurma);
		
		// evita lazy exception ao tentar acessar a unidade do componente
		ComponenteCurricular componente = matriculaDao.findAndFetch(obj.getDisciplina().getId(), ComponenteCurricular.class, "unidade");
		obj.setDisciplina(componente);
				
		checkRoleAtualizarTurma();
		
		if (hasOnlyErrors()) {
			obj = new Turma();
			return null;
		}
		
		// Se a turma não tiver data de início e fim e o calendário for carregado então o sistema seta automaticamente a data de início e fim
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(obj);
		if( cal != null ){
			if( obj.getDataInicio() == null )
				obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
			if( obj.getDataFim() == null )
				obj.setDataFim( getCalendario().getFimPeriodoLetivo() );
		}
			
		if (obj.isDistancia()) {
			turmaEad = true;
			if (obj.getPolo() == null) obj.setPolo(new Polo());
		}
		
		if ( !isEmpty(obj.getPolo()) ) {
			obj.getPolo().getCidade().getNomeUF();
			obj.setDistancia(true);
			turmaEad = true;
		}
		// Inicializar dados
		if (obj.getTurmaAgrupadora() != null)
			obj.getTurmaAgrupadora().getId();
		
		if (obj.getReservas() != null) {
			for (ReservaCurso reserva : obj.getReservas()) {
				reserva.getSolicitante();
				if (reserva.getSolicitacao() != null)
					reserva.getSolicitacao().getRegistroEntrada().toString();
			}
		}
		
		obj.getDocentesTurmas().iterator();
		for (DocenteTurma dt : obj.getDocentesTurmas()){
			if (dt.getHorarios() != null)
				dt.getHorarios().iterator();
		}
		obj.getHorarios().iterator();
		
		if (obj.getCurso() == null)
			obj.setCurso(new Curso());

		if(obj.getCampus() == null)
			obj.setCampus(new CampusIes());
		
		if (obj.getEspecializacao() != null)
			obj.getEspecializacao().getDescricao();
		else
			obj.setEspecializacao(new EspecializacaoTurmaEntrada());
		
		beforeAtualizarTurma();
		
		if(hasOnlyErrors()) return null;
		
		prepareMovimento(SigaaListaComando.ALTERAR_TURMA);
		setConfirmButton("Alterar");
		codigoTurmaBeforeUpdate = obj.getCodigo();
		preencherAlteracaoTurma();
		// não é permitido alterar o componente curricular da turma, portando já redireciona para os dados gerais.
		return formDadosGerais();
	}
	
	
	/**
	 * Método utilizado para preencher os dados iniciais da turma com o objetivo de salvar um registro de AlteracaoTurma.
	 */
	private void preencherAlteracaoTurma() {
		
		alteracaoTurma = new AlteracaoTurma();
		
		alteracaoTurma.setTurmaAlterada(new Turma(obj.getId()));
		alteracaoTurma.setFimAnterior(obj.getDataFim());
		alteracaoTurma.setInicioAnterior(obj.getDataInicio());
		alteracaoTurma.setTipoTurmaAnterior(obj.getTipo());
		alteracaoTurma.setCodigoTurmaAnterior(obj.getCodigo());
		
		//Nem todas as turmas definem Campus
		if(!ValidatorUtil.isEmpty(obj.getCampus()))		
			alteracaoTurma.setCampusAnterior(new CampusIes(obj.getCampus().getId()));
		else
			alteracaoTurma.setCampusAnterior(null);
		
		//Turma EAD não tem capacidade definida
		if(!ValidatorUtil.isEmpty(obj.getCapacidadeAluno()))		
			alteracaoTurma.setCapacidadeAlunoAnterior(obj.getCapacidadeAluno());
		else
			alteracaoTurma.setCapacidadeAlunoAnterior(null);
		
		//Nem todas as turmas estão com docentes definidos
		if(isDefineDocentes()){
			alteracaoTurma.setDocentesAnteriores(obj.getDocentesNomes());
		} else {
			alteracaoTurma.setDocentesAnteriores(null);
		}
		
		//Nem todas as turmas definem horários
		if(isDefineHorario()){		
			alteracaoTurma.setHorarioAnterior(obj.getDescricaoHorario());
		} else {
			alteracaoTurma.setHorarioAnterior(null);
		}
		
		if(obj.isSubTurma()) {
			alteracaoTurma.setTurmaAgrupadoraAnterior(new Turma(obj.getTurmaAgrupadora().getId()));
		} else {
			alteracaoTurma.setTurmaAgrupadoraAnterior(null);
		}
		
	}
	
	/** Verifica se o código da turma foi alterado na atualização de dados da turma e, caso sim, inclui uma mensagem de aviso para o usuário.*/
	protected void verificaAlteracaoCodigoTurma() {
		if (codigoTurmaBeforeUpdate != null && !codigoTurmaBeforeUpdate.equals(obj.getCodigo()))
			addMensagemWarning("O código da turma foi alterado de " + codigoTurmaBeforeUpdate + " para " + obj.getCodigo());
	}
	
	/**
	 * Usado para realizar validações em {@link PreRemove}
	 * @throws ArqException
	 */
	public void validarPreRemover() throws ArqException {
		
	}
	
	/**
	 * Inicia o caso de uso de remover turmas.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	@Override
	public final String preRemover() {
		
		
		try {
			populateObj(true);
			
			validarPreRemover();
			
			if (hasErrors())
				return null;
			
			if (obj == null) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				obj = new Turma();
				return null;
			}

			obj.getReservas().iterator();
			obj.getDocentesTurmas().iterator();
			obj.getHorarios().iterator();

			SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);

			// Buscar se já existem alunos matriculados para a turma selecionada
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
			obj.setQtdMatriculados( matriculaDao.findTotalMatriculasByTurmaSituacao(obj, 
					SituacaoMatricula.MATRICULADO.getId(), 
					SituacaoMatricula.APROVADO.getId(), 
					SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), 
					SituacaoMatricula.APROVEITADO_DISPENSADO.getId(), 
					SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(), 
					SituacaoMatricula.REPROVADO.getId(), 
					SituacaoMatricula.REPROVADO_FALTA.getId(), 
					SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId()) );
			obj.setTotalSolicitacoes( solicitacaoDao.countByTurma(obj, SolicitacaoMatricula.getStatusSolicitacoesPendentes(), true ).intValue() );

			List<MatriculaComponente> matriculas = matriculaDao.findAtivasByTurma(obj);
			
			if (matriculas != null ) {
				Collections.sort(matriculas);
				obj.setMatriculasDisciplina(matriculas);
			}
			checkRolePreRemover();
			
			prepareMovimento(SigaaListaComando.REMOVER_TURMA);
			setReadOnly(true);
			setConfirmButton("Remover");
			if (hasErrors())
				return null;
			return formConfirmacaoRemover();
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
	}
	
	/** Remove a turma.<br/>Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#remover()
	 */
	@Override
	public String remover() throws ArqException {
		
		validarPreRemover();
		
		if (hasErrors())
			return null;
		
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
	
	// --- Fim do fluxo de remoção de turma.
	
	// Outros métodos
	
	/**
	 * Carrega o calendário correto para realizar análise das solicitações
	 * @throws DAOException
	 */
	protected CalendarioAcademico getCalendario() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();
		return cal;
	}

	/** Indica se a turma é para cursos de convênio.
	 * @return
	 */
	public boolean isParaConvenio() {
		return cursosCombo != null;
	}

	/**
	 * Redireciona para o formulário de definição de docentes da turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/turma/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String definicaoDocenteTurmaVoltar() {
		if (isDefineHorario()) {
			HorarioTurmaMBean horarioBean = getMBean("horarioTurmaBean");
			return horarioBean.populaHorarioTurma(this, "Cadastro de Turma", obj);
		} else {
			return formDadosGerais();
		}
	}

	/**
	 * Cancela a operação corrente e limpa os dados da seção.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/dados_gerais.jsp</li>
	 * <li>sigaa.war/graduacao/turma/docentes.jsp</li>
	 * <li>sigaa.war/graduacao/turma/form_reservas.jsp</li>
	 * <li>sigaa.war/graduacao/turma/horarios.jsp</li>
	 * <li>sigaa.war/graduacao/turma/resumo.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		resetBean();
		initObj();

		if (isTurmaEad()) {
			return redirectMenuEad();
		}

		return redirect( getSubSistema().getLink() );
	}

	/** Retorna o curso a ser inserido na reserva de vagas.
	 * @return
	 */
	public int getIdCurso() {
		return idCurso;
	}

	/** Seta o curso a ser inserido na reserva de vagas.
	 * @param idCurso
	 */
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	/** Retorna a reserva de vagas desta turma para um curso.
	 * @return
	 */
	public ReservaCurso getReserva() {
		return reserva;
	}

	/** Seta a reserva de vagas desta turma para um curso. 
	 * @param reserva
	 */
	public void setReserva(ReservaCurso reserva) {
		this.reserva = reserva;
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

	/** Método invocado pela arquitetura após o cadastro da turma.
	 * <br/>Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return forward( getSubSistema().getForward() );
	}

	/** Retorna o ID da solicitação de turma atendida pela criação desta turma. 
	 * @return
	 */
	public int getIdSolicitacao() {
		return idSolicitacao;
	}

	/** Seta o ID da solicitação de turma atendida pela criação desta turma. 
	 * @param idSolicitacao
	 */
	public void setIdSolicitacao(int idSolicitacao) {
		this.idSolicitacao = idSolicitacao;
	}

	/** Retorna a coleção de turmas criadas para cada subunidade quando a disciplina é de bloco
	 * @return
	 */
	public Collection<Turma> getTurmasSubunidades() {
		return turmasSubunidades;
	}

	/** Seta a coleção de turmas criadas para cada subunidade quando a disciplina é de bloco 
	 * @param turmasSubunidades
	 */
	public void setTurmasSubunidades(Collection<Turma> turmasSubunidades) {
		this.turmasSubunidades = turmasSubunidades;
	}

	/** Retorna o ID da sub-unidade selecionada na seleção dos docentes. 
	 * @return
	 */
	public int getIdSubunidade() {
		return idSubunidade;
	}

	/** Seta o ID da sub-unidade selecionada na seleção dos docentes. 
	 * @param idSubunidade
	 */
	public void setIdSubunidade(int idSubunidade) {
		this.idSubunidade = idSubunidade;
	}

	/** Indica se a turma tem discentes matriculados.
	 * @return
	 */
	public boolean isMatriculada() {
		return obj.isMatriculada();
	}

	/** Retorna o próximo calendário acadêmico.
	 * Método não invocado por JSP
	 * @return
	 * @throws DAOException
	 */
	public String getProximoCalendario() throws DAOException {
		Integer proximo =  DiscenteHelper.somaSemestres(getCalendario().getAno(), getCalendario().getPeriodo(), 1);
		StringBuilder sb = new StringBuilder(proximo + "");
		sb.insert(4, ".");
		return sb.toString();
	}

	/** Indica se a turma é de curso de pós-graduação stricto sensu.
	 * @return
	 */
	public boolean isTurmaStricto() {
		return NivelEnsino.isAlgumNivelStricto(getNivelEnsino()) ||
		SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema());

	}

	/** Indica se a turma é de curso de graduação.
	 * @return
	 */
	public boolean isTurmaGraduacao() {
		return getNivelEnsino() == NivelEnsino.GRADUACAO ||
		SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema()) ||
		SigaaSubsistemas.GRADUACAO.equals(getSubSistema());

	}

	/**
	 * Indica se a turma setada pode ser alterada pelo usuário logado.
	 * @return
	 * @throws DAOException 
	 */
	public boolean isPassivelEdicao() throws DAOException {
		if ( isReadOnly() ) {
			return false;
		}
		return obj.isAberta() && (obj.getId() == 0
				|| getAcessoMenu().isDae()
				|| getAcessoMenu().isAlgumUsuarioStricto()
				|| getAcessoMenu().isAlgumUsuarioLato()
				|| ( (getAcessoMenu().isChefeDepartamento() || getAcessoMenu().isSecretarioDepartamento()
				|| getAcessoMenu().isGestorResidenciaMedica() || getAcessoMenu().isCoordenadorResidenciaMedica())
						&& !(obj.getAno()*10 + obj.getPeriodo() < getCalendario().getAno()*10 + getCalendario().getPeriodo())
						&& obj.isResidenciaMedica() ? true : ( getCalendario().isPeriodoAjustesTurmas() || getCalendario().isPeriodoCadastroTurmas() )
						&& obj.isTurmaRegular() ) );
	}

	/**
	 * Indica se está no período de cadastro de turma.
	 * @return
	 * @throws DAOException 
	 */
	public boolean isPeriodoCadastroTurma() throws DAOException{
		if( getCalendario() != null && 
				( getCalendario().isPeriodoCadastroTurmas() || getCalendario().isPeriodoCadastroTurmasFerias() 
						||  getCalendario().isPeriodoCadastroTurmaEnsinoIndiv() 
						||  getCalendario().isPeriodoSugestaoTurmaChefe()) )
			return true;
		else
			return false;
	}

	/** Redireciona o usuário para o menu do módulo EAD.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	private String redirectMenuEad() {
		return	redirect("/ead/menu.jsf");
	}

	/** Retorna o combo de cursos (quando o cadastro é de turma de um curso de convênio) 
	 * @return
	 */
	public Collection<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	/** Seta o combo de cursos (quando o cadastro é de turma de um curso de convênio)
	 * @param cursosConvenio
	 */
	public void setCursosCombo(Collection<SelectItem> cursosConvenio) {
		this.cursosCombo = cursosConvenio;
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

	/**
	 * Carrega as informações da turma e redireciona para o formulário de
	 * visualização.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException{
		int id = getParameterInt("id");
		obj = getGenericDAO().findAndFetch(id, Turma.class,
				"reservas", "docentesTurmas");
		return forward(JSP_RESUMO);
	}

	/** Indica que é apenas visualização, para configurar os botões da jsp de resumo .
	 * @return
	 */
	public boolean isApenasVisualizacao() {
		return apenasVisualizacao;
	}

	/** Seta que é apenas visualização, para configurar os botões da jsp de resumo.
	 * @param apenasVisualizacao
	 */
	public void setApenasVisualizacao(boolean apenasVisualizacao) {
		this.apenasVisualizacao = apenasVisualizacao;
	}
	
	/**
	 * Atualiza, por ajax, a data de início e fim da turma de acordo com a
	 * mudança do período da turma no formulário.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void atualizarDataInicioFim(ActionEvent evt) throws DAOException {
		atualizarDataInicioFim();
	}

	/**
	 * Atualiza a data de início e fim da turma de acordo com a
	 * mudança do período da turma no formulário.<br>
	 * Método não chamado por JSP(s):
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	private void atualizarDataInicioFim() throws DAOException {
		if (!isPassivelEdicao())
			return ;
	
		int periodo = obj.getPeriodo();
		
		boolean periodoFerias = false;
		if( PeriodoAcademicoHelper.getInstance().isPeriodoIntervalar(periodo) ){
			//periodo = periodo - 2;
			periodoFerias = true;
		}		
		
		CalendarioAcademico proximoCalendario = null;
		
		if(isPortalCoordenadorStricto()) {
			if( periodoFerias ){
				if( getCursoAtualCoordenadroStricto() == null){
					proximoCalendario = CalendarioAcademicoHelper.getCalendarioExato(obj.getAno(), periodo,new Unidade(getProgramaStricto().getId()), getNivelEnsino(), null, null, null, periodo);
				}else{
					proximoCalendario = CalendarioAcademicoHelper.getCalendarioExato(obj.getAno(), periodo,new Unidade(getProgramaStricto().getId()), getNivelEnsino(), null, null, getCursoAtualCoordenadroStricto(), periodo);	
				}
			}else {
				if( getCursoAtualCoordenadroStricto() == null){
					proximoCalendario = CalendarioAcademicoHelper.getCalendarioExato(obj.getAno(), periodo,new Unidade(getProgramaStricto().getId()), getNivelEnsino(), null, null, null, null);
				}else{
					proximoCalendario = CalendarioAcademicoHelper.getCalendarioExato(obj.getAno(), periodo,new Unidade(getProgramaStricto().getId()), getNivelEnsino(), null, null, getCursoAtualCoordenadroStricto(), null);
				}
			}
		}		
		
		if(proximoCalendario == null){		
			if( periodoFerias ){
				if( getCursoAtualCoordenadroStricto() == null){
					proximoCalendario = CalendarioAcademicoHelper.getCalendarioExato(obj.getAno(), periodo, new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL), getNivelEnsino(), null, null, null, periodo);
				}else{
					proximoCalendario = CalendarioAcademicoHelper.getCalendarioExato(obj.getAno(), periodo, new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL), getNivelEnsino(), null, null, getCursoAtualCoordenadroStricto(), periodo);
				}
			}else{
				if( getCursoAtualCoordenadroStricto() == null){
					proximoCalendario = CalendarioAcademicoHelper.getCalendarioExato(obj.getAno(), periodo, new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL), getNivelEnsino(), null, null, null, null);
				}else{
					proximoCalendario = CalendarioAcademicoHelper.getCalendarioExato(obj.getAno(), periodo, new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL), getNivelEnsino(), null, null, getCursoAtualCoordenadroStricto(), null);
				}
			}
		}
	
		if ( proximoCalendario == null )
			return;
		
		if( periodoFerias ){
			obj.setDataInicio(proximoCalendario.getInicioFerias());
			obj.setDataFim(proximoCalendario.getFimFerias());
		}else{
			obj.setDataInicio(proximoCalendario.getInicioPeriodoLetivo());
			obj.setDataFim(proximoCalendario.getFimPeriodoLetivo());
		}
		
		carregarSubturmas();
	}

	/**
	 * Atualiza, por ajax, o ano e o período turma de acordo com a
	 * mudança do tipo de turma no formulário.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/dados_gerais.jsp</li>
	 * </ul>
	 * 	 
	 * @param evt
	 * @throws DAOException 
	 */
	public void atualizarAnoPeriodo (ValueChangeEvent evt) throws DAOException{
		
		Integer novoTipo = (Integer) evt.getNewValue();
		
		if (novoTipo != null){
		
			if (novoTipo.intValue() == Turma.FERIAS){
				obj.setAno(getCalendario().getAnoFeriasVigente());
				obj.setPeriodo(getCalendario().getPeriodoFeriasVigente());
			} else {
				obj.setAno(getCalendario().getAno());
				obj.setPeriodo(getCalendario().getPeriodo());
			}
				
		}
		
		atualizarDataInicioFim();
	}
	
	/** Indica se o subsistema do usuário é o portal do coordenador.
	 * @return
	 */
	public boolean isSubsistemaPortalCoordenadorGrad(){
		return SigaaSubsistemas.PORTAL_COORDENADOR.equals( getSubSistema() );
	}

	/**
	 * Indica, na atualização de turma, se esta operação deve permitir adicionar
	 * outras reservas de solicitações de turma ainda não atendidas a esta
	 * turma. 
	 * 
	 * @return
	 */
	public boolean isAdicionarOutrasReservas() {
		return adicionarOutrasReservas;
	}

	/**
	 * Seta, na atualização de turma, se esta operação deve permitir adicionar
	 * outras reservas de solicitações de turma ainda não atendidas a esta
	 * turma. As outras reservas devem ser no mesmo horário desta turma é
	 * necessário que este no período de cadastro de turma, regular ou de férias
	 * 
	 * @param adicionarOutrasReservas
	 */
	public void setAdicionarOutrasReservas(boolean adicionarOutrasReservas) {
		this.adicionarOutrasReservas = adicionarOutrasReservas;
	}

	/**
	 * Retorna uma coleção de selectItens com os tipos de turma que podem ser
	 * criados para popular o select no formulário quando a turma estiver sendo
	 * criada sem ser a partir de uma solicitação
	 * 
	 * @param tiposTurmaCombo
	 */
	public void setTiposTurmaCombo(Collection<SelectItem> tiposTurmaCombo) {
		this.tiposTurmaCombo = tiposTurmaCombo;
	}

	/** Retorna a lista de componentes curriculares que o usuário pode escolher para criar uma turma.
	 * @return
	 */
	public Collection<ComponenteCurricular> getComponentes() {
		return componentes;
	}

	/** Seta a lista de componentes curriculares que o usuário pode escolher para criar uma turma.
	 * @param componentes
	 */
	public void setComponentes(Collection<ComponenteCurricular> componentes) {
		this.componentes = componentes;
	}

	/** Retorna o conjunto de turmas agrupadas. 
	 * @return
	 * @throws DAOException
	 */
	public Set<Turma> getTurmasAgrupadoras() throws DAOException {
		if (turmasAgrupadoras == null) {
			carregarSubturmas();
		}
		return turmasAgrupadoras;
	}
	
	/** Retorna o conjunto de selectItem de turmas agrupadas. 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTurmasAgrupadorasCombo() throws DAOException {
		if (getTurmasAgrupadoras() != null) {
			return toSelectItems(getTurmasAgrupadoras(), "id", "codigo");
		}
		return new ArrayList<SelectItem>();
	}
	
	/**
	 * Retorna uma coleção de select item com as turmas das subunidades e seus respectivos
	 * horários para popular o select na tela de seleção dos docentes das subunidades.
	 * Utilizado caso a turma criada seja do tipo bloco
	 * @return
	 */
	public Collection<SelectItem> getTurmasHorariosSubunidades(){
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		for(  Turma turma : turmasSubunidades ){
			SelectItem si = new SelectItem();
			if( turma.getDisciplina().isDisciplina() )
				si.setLabel( turma.getDisciplina().toString() + " - " + turma.getDescricaoHorario() + " - "  + turma.getDisciplina().getChTotal() + "h" );
			else
				si.setLabel( turma.getDisciplina().toString() + " - " + turma.getDisciplina().getChTotal() + "h" );
			si.setValue( turma.getDisciplina().getId() );
			itens.add(si);
		}

		return itens;
	}

	/**
	 * Carrega as sub-turmas existentes caso o componente permita a criação de sub-turmas.
	 * @throws DAOException
	 */
	protected void carregarSubturmas() throws DAOException {
		TurmaDao daoTurma = getDAO( TurmaDao.class );
		daoTurma.initialize(obj.getDisciplina());
		
		if( obj.getDisciplina().isAceitaSubturma() ){
			turmasAgrupadoras = new LinkedHashSet<Turma>();
			// busca subturmas do mesmo componente e agrupa
			Collection<Turma> outrasTurmasComponente = daoTurma.findByDisciplinaAnoPeriodoSituacao(obj.getDisciplina(), obj.getAno(), obj.getPeriodo(), 
					new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE), new SituacaoTurma(SituacaoTurma.ABERTA));
			Collection<Turma> subturmas = null;
			
			// intera para remover turmas que são ead das que não são
			if (outrasTurmasComponente != null) {
				Iterator<Turma> iterator = outrasTurmasComponente.iterator();
				while (iterator.hasNext()) {
					if (iterator.next().isEad() != isTurmaEad())
						iterator.remove();
				}
			}

			// Se for criação de turma sem ser a partir de uma solicitação então ainda não possui horários portanto deve exibir todas as sub-turmas do componente.
			// Na tela seguinte, a tela de seleção do horário, será feita a validação para garantir que haverá o choque de horário com as sub-turmas selecionadas
			if( solicitacao != null ) 
				subturmas = HorarioTurmaUtil.verificarChoqueHorario(obj, outrasTurmasComponente, false);
			else
				subturmas = outrasTurmasComponente;
			
			if (subturmas != null) {
				for( Turma t : subturmas ){
					if( t.getTurmaAgrupadora() != null )
						turmasAgrupadoras.add(t.getTurmaAgrupadora() );
				}
			}
			for( Turma t : turmasAgrupadoras ){
				if( t != null )
					t.setSubturmas( br.ufrn.arq.util.CollectionUtils.toList( daoTurma.findSubturmasByTurmaFetchDocentes(t) ) );
			}
			
			for (Iterator<Turma> it = turmasAgrupadoras.iterator(); it.hasNext();) {
				Turma turma = it.next();
				if( turma == null){
					it.remove();
					continue;
				}
				
			}
			// reordena por código de turma
			ArrayList<Turma> listaTurmas = CollectionUtils.toList(turmasAgrupadoras);
			Collections.sort(listaTurmas, new Comparator<Turma>() {
				@Override
				public int compare(Turma o1, Turma o2) {
					return o1.getCodigo().compareTo(o2.getCodigo());
				}
			});
			turmasAgrupadoras.clear();
			turmasAgrupadoras.addAll(listaTurmas);
		}
	}

	/**
	 * Redireciona o usuário para o formulário de dados gerais. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/WEB-INF/jsp/ensino/turma/horarios.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.ensino.jsf.OperadorHorarioTurma#definicaoHorarioTurmaVoltar()
	 */
	public String definicaoHorarioTurmaVoltar(){
		return formDadosGerais();
	}
	
	/** Retorna a solicitação de turma atendida pela criação desta turma.
	 * @return
	 */
	public SolicitacaoTurma getSolicitacao() {
		return solicitacao;
	}

	/** Seta a solicitação de turma atendida pela criação desta turma. 
	 * @param getSolicitacao()
	 */
	public void setSolicitacao(SolicitacaoTurma solicitacao) {
		this.solicitacao = solicitacao;
	}
	
	/** Remove letras de um texto. Utilizado para remover letras que indicam a subturma de um código de turma.
	 * @param texto
	 * @return
	 */
	protected String removeLetras(String texto) {
		if (texto == null) 
			return null;
		StringBuilder codigo = new StringBuilder();
		for (char c : texto.toCharArray()) {
			switch (c) {
				case '0': case '1': case '2': case '3': case '4': 
				case '5': case '6': case '7': case '8': case '9': codigo.append(c); 
			}
		}
		return codigo.toString();
	}

	/** Seta se o cadastro é de turmas de ensino a distância.
	 * @param turmaEad
	 */
	public void setTurmaEad(boolean turmaEad) {
		this.turmaEad = turmaEad;
	}
	
	/** Indica se o cadastro de turmas é de ensino a distância. 
	 * @return 
	 */
	public boolean isTurmaEad() {
		return turmaEad;
	}
	
	/**
	 * Retorna um texto contendo a descrição da modalidade com base na informação fornecida por {@link #isTurmaEad()}
	 * @return
	 */
	public String getModalidadeTurma() {
		if (isTurmaEad())
			return "A Distância";
		else
			return "Presencial";
	}

	public AlteracaoTurma getAlteracaoTurma() {
		return alteracaoTurma;
	}

	public void setAlteracaoTurma(AlteracaoTurma alteracaoTurma) {
		this.alteracaoTurma = alteracaoTurma;
	}
}
