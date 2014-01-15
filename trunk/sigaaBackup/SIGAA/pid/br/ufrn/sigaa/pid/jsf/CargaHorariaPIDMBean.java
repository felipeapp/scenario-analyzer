/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/10/2009
 *
 */
package br.ufrn.sigaa.pid.jsf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.OrientacaoAtividadeDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.prodocente.CHDedicadaResidenciaMedicaDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dao.CargaHorariaEadDAO;
import br.ufrn.sigaa.ead.dominio.CargaHorariaEad;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.OrientacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalDocente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pid.dao.PlanoIndividualDocenteDao;
import br.ufrn.sigaa.pid.dominio.AtividadeComplementarPID;
import br.ufrn.sigaa.pid.dominio.AtividadesEspecificasDocente;
import br.ufrn.sigaa.pid.dominio.CargaHorariaAdministracao;
import br.ufrn.sigaa.pid.dominio.CargaHorariaAtividadesComplementares;
import br.ufrn.sigaa.pid.dominio.CargaHorariaOrientacao;
import br.ufrn.sigaa.pid.dominio.CargaHorariaProjeto;
import br.ufrn.sigaa.pid.dominio.ChEnsinoPIDocenteTurma;
import br.ufrn.sigaa.pid.dominio.ChProjetoPIDMembroProjeto;
import br.ufrn.sigaa.pid.dominio.ChResidenciaMedicaPID;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;
import br.ufrn.sigaa.pid.dominio.TipoAtividadeComplementarPID;
import br.ufrn.sigaa.pid.dominio.TipoOrientacaoPID;
import br.ufrn.sigaa.pid.negocio.PIDUtils;
import br.ufrn.sigaa.prodocente.atividades.dominio.CHDedicadaResidenciaMedica;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * MBean responsável pelo cadastro de CH do Plano Individual do Docente
 * Existe apenas um PID por semestre para cada docente.
 * 
 * @author agostinho campos
 *
 */

@Component("cargaHorariaPIDMBean") 
@Scope("request")
public class CargaHorariaPIDMBean extends SigaaAbstractController<PlanoIndividualDocente> {

	/** Valor da carga horária referente a dedicação exclusiva do docente. */
	public static final int CARGA_HORARIA_DEDICACAO_EXCLUSIVA =	Integer.parseInt(ParametroHelper
			.getInstance().getParametro(ParametrosGerais.CARGA_HORARIA_DEDICACAO_EXCLUSIVA));
	
	/** Indica que são selecionados todos os PIDS na listagem de PIDs. */
	public static final int TODOS_PIDS = 0;
	/** Indica que são selecionados os PIDS não cadastrados, na listagem de PIDs. */
	public static final int PIDS_NAO_CADASTRADOS = -1;
	
	/** Ano do cadastro do PID. */
	private int ano;
	
	/** Período do cadastro do PID. */
	private int periodo;
	
	/**
	 * Variável que armazena a CH restante. Essa CH pode ser distribuída
	 * em outras áreas do PID.
	 */
	private double chRestanteADistribuir;
	
	/**
	 * Listagem de grupos para listagem de Checkboxs para o grupo Ensino.
	 */
	private ArrayList<AtividadeComplementarPID> listaGrupoEnsino;
	
	/**
	 * Listagem de grupos para listagem de Checkboxs para o grupo Atividade Administrativa.
	 */
	private ArrayList<AtividadeComplementarPID> listaGrupoAtivAdmin;
	
	/**
	 * Listagem de grupos para listagem de Checkboxs para o grupo Atividade de Extensão.
	 */
	private ArrayList<AtividadeComplementarPID> listaGrupoAtivExtensao;
	
	/**
	 * Listagem de grupos para listagem de Checkboxs para o grupo Atividade 
	 * de Pesquisa e Produção Técnica Científica.
	 */
	private ArrayList<AtividadeComplementarPID> listaGrupoAtivPesqProducaTec;
	
	/**
	 * Lista de orientações que o docente pode estar vinculado (Graduação). 
	 */
	private List<CargaHorariaOrientacao> listaCHOrientacaoPosGraduacao;
	
	/**
	 * Lista de orientações que o docente pode estar vinculado (Pós-Graduação).
	 */
	private List<CargaHorariaOrientacao> listaCHOrientacaoGraduacao;

	/**
	 * Lista dos projetos de Extensão que o docente pode estar vinculado. 
	 */
	private List<CargaHorariaProjeto> listaCargaHorariaProjetoExtensao;
	
	/**
	 * Lista dos projetos de Pesquisa que o docente pode estar vinculado. 
	 */
	private List<CargaHorariaProjeto> listaCargaHorariaProjetoPesquisa;

	/**
	 * Lista dos projetos de Ensino que o docente pode estar vinculado. 
	 */
	private List<CargaHorariaProjeto> listaCargaHorariaProjetoEnsino;
	/**
	 * Lista das designações que um docente pode assumir
	 * Ex: Coordenador de Curso
	 */
	private List<Designacao> listaChAdmin;
	
	/**
	 * Lista de PIDs do docente 
	 */
	private List<PlanoIndividualDocente> allPIDDocente;
	
	/**
	 * Lista de PIDs do docente homologados.
	 */
	private List<PlanoIndividualDocente> pidsHomologados;
	
	/**
	 * Lista de PIDs do docente a serem cadastrados.
	 */
	private List<PlanoIndividualDocente> pidsCadastrados;
	
	/**
	 * Lista de atividades que são adicionadas manualmente pelo docente. 
	 */
	private List<AtividadesEspecificasDocente> ativEspecificasAdicionasDocente;
	
	/**
	 * Lista de atividades que são adicionadas/removidas manualmente pelo docente. 
	 */
	private List<AtividadesEspecificasDocente> ativEspecificasAdicionasDocenteRemover;
	
	/**
	 * Os itens dessa lista são passados para o ProcessadorCargaHorariaPID que faz remoção 
	 * das atividades que foram desmarcadas no checkbox pelos usuários.
	 */
	private List<CargaHorariaAtividadesComplementares> chAtividadeComplementarRemover;
	
	/**
	 * Exibe as turmas vinculadas a esse Docente
	 */
	private ArrayList<DocenteTurma> listaDocenteTurma;
	
	/**
	 * Após o PID ser cadastrado podem ocorrer mudanças nas turmas que estão vinculadas a esse PID.
	 * Por exemplo: as turmas podem ser removidas ou alteradas para outro professor.
	 * Dessa forma é necessário remover essa entidades que não estão mais vinculados ao PID do docente. 
	 */
	
	/** Lista de turmas transferidas ou removidas */
	private ArrayList<ChEnsinoPIDocenteTurma> turmasTransferidasOuRemovidas;
	
	/** Lista de designações a serem removidas */
	private ArrayList<CargaHorariaAdministracao> listaDesignacoesRemover;
	
	/** Lista de orientações a serem removidas */
	private ArrayList<CargaHorariaOrientacao> listaCargaHorariaOrientacaoRemover;
	
	/** Lista de turmas de Residências Médica a serem removidas */
	private ArrayList<ChResidenciaMedicaPID> turmasResidenciaMedicaRemover;
	
	/** Lista de projetos a serem removidas */
	private ArrayList<ChProjetoPIDMembroProjeto> projetosRemover;

	/** Indica se o usuário pode alterar um PID homologado. */
	private boolean permiteAlterarPIDHomologado = ParametroHelper.getInstance().getParametroBoolean(ParametrosPortalDocente.PERMITE_ALTERAR_PID_HOMOLOGADO);

	/** Lista de CH de orientações de graduação sem atividades associadas. */
	private List<CargaHorariaOrientacao> listaCHOrientacaoGraduacaoSemAtividades;
	
	/** FILTROS DO CHEFE DO DEPARTAMENTO */
	
	/** Combo com datas para o filtro de PIDs pelo chefe do departamento. */
	private List <SelectItem> datasCombo;
	/** Combo com situações do PID */
	private List <SelectItem> situacaoCombo;
	/** Lista de docentes não externos da unidade. */	
	private List <Servidor> docentesUnidades;
	/** Data selecionada pelo chefe do departamento */	
	private String dataFiltroChefia;
	/** Situação selecionada pelo chefe do departamento */	
	private Integer situacaoFiltroChefia;
	/** Caso o filtro deva ser feito em busca de todos os docentes do depatarmento */	
	private Boolean todosDocentes;
	/** Servidor selecionado pelo chefe do departamento */	
	private Servidor servidorFiltroChefia;
	/** Ano que será filtrado pelo chefe do departamento */	
	private Integer anoFiltro;
	/** Período que será filtrado pelo chefe do departamento */	
	private Integer periodoFiltro;
	
	public CargaHorariaPIDMBean() {
		obj = new PlanoIndividualDocente();
		obj.setServidor(new Servidor());
		
		clear();
	}

	/**
	 * Inicializa as variáveis de instância
	 */
	private void clear() {
		
		listaDocenteTurma = new ArrayList<DocenteTurma>();
		
		listaCHOrientacaoPosGraduacao = new ArrayList<CargaHorariaOrientacao>();
		listaCHOrientacaoGraduacao = new ArrayList<CargaHorariaOrientacao>();
		listaCHOrientacaoGraduacaoSemAtividades = new ArrayList<CargaHorariaOrientacao>();
				
		listaCargaHorariaProjetoPesquisa = new ArrayList<CargaHorariaProjeto>();
		listaCargaHorariaProjetoExtensao = new ArrayList<CargaHorariaProjeto>();
		listaCargaHorariaProjetoEnsino = new ArrayList<CargaHorariaProjeto>();
		
		listaChAdmin = new ArrayList<Designacao>();
		
		ativEspecificasAdicionasDocente = new ArrayList<AtividadesEspecificasDocente>();
		
		ativEspecificasAdicionasDocenteRemover = new ArrayList<AtividadesEspecificasDocente>();
		chAtividadeComplementarRemover = new ArrayList<CargaHorariaAtividadesComplementares>();

		turmasTransferidasOuRemovidas = new ArrayList<ChEnsinoPIDocenteTurma>();
		turmasResidenciaMedicaRemover = new ArrayList<ChResidenciaMedicaPID>();
		projetosRemover = new ArrayList<ChProjetoPIDMembroProjeto>();
		listaCargaHorariaOrientacaoRemover = new ArrayList<CargaHorariaOrientacao>();
		listaDesignacoesRemover = new ArrayList<CargaHorariaAdministracao>();
		
		listaGrupoEnsino = new ArrayList<AtividadeComplementarPID>();
		listaGrupoAtivExtensao = new ArrayList<AtividadeComplementarPID>();
		listaGrupoAtivPesqProducaTec = new ArrayList<AtividadeComplementarPID>();
		listaGrupoAtivAdmin = new ArrayList<AtividadeComplementarPID>();
	}
	
	/**
	 * Inicia o cadastramento do PID
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/portais/docente/menu_docente.jsp</li>
 	 * </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {

		if ( getVerificarExibicaoLinkPID() ) {
			obj.setServidor(getUsuarioLogado().getServidor());
			ano = getCalendarioVigente().getAno();
			periodo = getCalendarioVigente().getPeriodo();
			
			PlanoIndividualDocenteDao pidDAO = getDAO(PlanoIndividualDocenteDao.class);
			allPIDDocente = pidDAO.findAllPIDDocente(obj.getServidor());
			// inclui os PID ainda não preenchidos
			incluiPseudoPlanos();
			return forward("/pid/listagem.jsp");
		}
		else { 
			addMessage("O Plano Individual do Docente não está disponível para docentes externos", TipoMensagemUFRN.WARNING);
			return redirectJSF("/portais/docente/docente.jsf");
		}
	}

	/** Inclui na lista de planos encontrados, pseudoplanos, isto é, planos que serão cadastrados. */
	private void incluiPseudoPlanos() {
		if (allPIDDocente == null) allPIDDocente = new ArrayList<PlanoIndividualDocente>();
		int ano = PlanoIndividualDocente.ANO_INICIO, periodo = PlanoIndividualDocente.PERIODO_INICIO;
		while (ano * 10 + periodo <= getCalendarioVigente().getAno() * 10 + getCalendarioVigente().getPeriodo()) {
			boolean  incluir = true;
			for (PlanoIndividualDocente pidExistente : allPIDDocente)
				if (pidExistente.getAno() * 10 + pidExistente.getPeriodo() == ano * 10 + periodo) {
					incluir = false;
					break;
				}
			if (incluir) {
				PlanoIndividualDocente pseudoPID = new PlanoIndividualDocente();
				pseudoPID.setAno(ano);
				pseudoPID.setPeriodo(periodo);
				allPIDDocente.add(pseudoPID);
			}
			if (++periodo > 2) { periodo = 1; ano++; }
		}
		// ordena por ano-período, do maior para o menor
		Collections.sort(allPIDDocente, new Comparator<PlanoIndividualDocente>() {
			@Override
			public int compare(PlanoIndividualDocente o1, PlanoIndividualDocente o2) {
				return o2.getAno() * 10 + o2.getPeriodo() - o1.getAno() * 10 + o1.getPeriodo();
			}
		});
	}
	
	/**
	 * Verifica se é um docente válido para exibir o link no menu do Portal do Docente.
	 * 
	 * Docente válido é aquele que não é docente externo e nem é docente do Ensino Médio ou Infantil.
	 * 
	 * Método não invocado por JSP's.
	 * 
	 * @return
	 */
	public boolean getVerificarExibicaoLinkPID() {
		if ( !getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno() && (
				getUsuarioLogado().getVinculoAtivo().getServidor().getCargo().getId() == Cargo.PROFESSOR_DO_MAGISTERIO_SUPERIOR ||
				getUsuarioLogado().getVinculoAtivo().getServidor().getCargo().getId() == Cargo.DOCENTE_SUPERIOR_EFETIVO ||
				getUsuarioLogado().getVinculoAtivo().getServidor().getCargo().getId() == Cargo.DOCENTE_SUPERIOR_SUBSTITUTO ))
				
			return true;
		else 
			return false;
	}

	/**
	 * Acessa o PID selecionado
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>sigaa.war/pid/listagem.jsp</li>
 	 * </ul>
	 * @throws NegocioException 
	 * 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String acessarPID() throws ArqException, NegocioException {
		clear();
		Integer idPID = getParameterInt("id", 0);
		
		prepareMovimento(SigaaListaComando.ALTERAR_PID);
		
		PlanoIndividualDocenteDao pidDAO = getDAO(PlanoIndividualDocenteDao.class);		
		obj = pidDAO.findByPrimaryKey(idPID, PlanoIndividualDocente.class);
		
		ano = obj.getAno();
		periodo = obj.getPeriodo();
		
		if ( obj != null) {
			
			// mesmo que o usuário seja chefe departamento, está exibindo o PID como docente
			carregarDadosPID(pidDAO, null, false);
			
			setConfirmButton("Alterar");
			return forward("/pid/form.jsp");
		}
		
		return null;
		
	}

	/**
	 * Popula uma série de cargas horárias do PID e realiza os cálculos de CH necessários 
	 * 
	 * Método não invocado por JSP's.
	 * 
	 * @param pidDAO
	 * @param b 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public void carregarDadosPID(PlanoIndividualDocenteDao pidDAO, PlanoIndividualDocente objBusca, boolean chefeDepartamento) throws ArqException, NegocioException {
		
		if (objBusca != null)
			obj = objBusca;
		
		// docentes com dedicação exclusiva tem registrado no banco 99h, mas na verdade são 40h
		if ( obj.getServidor().isDedicacaoExclusiva() )
			obj.getServidor().setRegimeTrabalho(CARGA_HORARIA_DEDICACAO_EXCLUSIVA);
		
		ativEspecificasAdicionasDocente = pidDAO.findAtividadesEspecificasDocenteByPID(obj);
		
		popularCHEnsino(chefeDepartamento); // disciplinas do docente
		popularCHProjeto(); // popula Pesquisa e Extensão
		
		popularCHOrientacao(chefeDepartamento); // graduação e pós-graduação
		popularCHAdministracao(chefeDepartamento); // designações que possa ter
		
		calcularTotalCargaHorariaEnsino(null);
		
		calcularHorarioPorPercentualPesquisa(null);
		calcularHorarioPorPercentualExtensao(null);			
		calcularHorarioPorPercentualOutrasAtividades(null);
		calcularHorarioPorPercentualOutrasAtividadesEnsino(null);
		calcularHorarioPorPercentualAdministracao(null);
		
		carregarTodasAtividadesComplementares();
		carregarAtividadesComplementaresSelecionadas();
	}
	
	/**
	 * Cadastra um novo PID para o Ano/Semestre atual se não existir nenhum
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>sigaa.war/pid/listagem.jsp</li>
 	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String iniciarCadastroNovoPID() throws ArqException, NegocioException {
		clear();
		prepareMovimento(SigaaListaComando.CADASTRAR_PID);
		
		ano = getParameterInt("ano", getCalendarioVigente().getAno());
		periodo = getParameterInt("periodo", getCalendarioVigente().getPeriodo());

		PlanoIndividualDocenteDao pidDAO = getDAO(PlanoIndividualDocenteDao.class);
		obj = pidDAO.findPIDByServidorAnoPeriodo(getUsuarioLogado().getServidor(), ano, periodo);
		
		if (obj == null) {
			
			obj = new PlanoIndividualDocente();
			obj.setServidor(getUsuarioLogado().getServidor());
			obj.setAno(ano);
			obj.setPeriodo(periodo);
			
			carregarDadosPID(pidDAO, null, false);
			chRestanteADistribuir = (obj.getServidor().getRegimeTrabalho()-obj.getTotalGrupoEnsino());
			
			setConfirmButton("Cadastrar");
			return forward("/pid/form.jsp");
		}
		else {
			addMensagemErro("Já existe um PID cadastrado para o período atual. Caso necessário, é possível atualizar os dados nele informados através do ícone <em> Alterar Plano Individual do Docente </em>.");
			return iniciar();
		}
	}

	/**
	 * Carrega os elementos que foram selecionados no checkbox ao cadastrar o PID  
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private void carregarAtividadesComplementaresSelecionadas() throws HibernateException, DAOException {
		List<CargaHorariaAtividadesComplementares> itensSelecionadosPID = obj.getChAtividadesComplementares();
		
		for (CargaHorariaAtividadesComplementares chHorariaOutrasAtiv : itensSelecionadosPID) {
			AtividadeComplementarPID ativSelecionada = chHorariaOutrasAtiv.getAtividadeComplementarPID();
			
			// verifica quais atividades tem observações e seta true para exibir na página
			if (!ValidatorUtil.isEmpty(chHorariaOutrasAtiv.getObservacao())) {
				ativSelecionada.setObservacao(chHorariaOutrasAtiv.getObservacao());
			}
			
			if (ativSelecionada.getTipoAtividadeComplementar().isEnsino()) {
				for (AtividadeComplementarPID itemEnsino : listaGrupoEnsino)
					if (itemEnsino.getId() == ativSelecionada.getId()){
						itemEnsino.setObservacao(ativSelecionada.getObservacao());
						itemEnsino.setSelecionada(true);
					}	
			}
			if (ativSelecionada.getTipoAtividadeComplementar().isAdministracao()) {
				for (AtividadeComplementarPID itemAdm : listaGrupoAtivAdmin)
					if (itemAdm.getId() == ativSelecionada.getId()){
						itemAdm.setObservacao(ativSelecionada.getObservacao());
						itemAdm.setSelecionada(true);
					}	
			}
			if (ativSelecionada.getTipoAtividadeComplementar().isExtensaoOutrasAtividade()) {
				for (AtividadeComplementarPID itemAtivExten : listaGrupoAtivExtensao)
					if (itemAtivExten.getId() == ativSelecionada.getId()){
						itemAtivExten.setObservacao(ativSelecionada.getObservacao());
						itemAtivExten.setSelecionada(true);
					}	
			}
			if (ativSelecionada.getTipoAtividadeComplementar().isPesquisaProducaoTecnicaCient()) {
				for (AtividadeComplementarPID itemPesqProdu : listaGrupoAtivPesqProducaTec) {
					if (itemPesqProdu.getId() == ativSelecionada.getId()){
						itemPesqProdu.setObservacao(ativSelecionada.getObservacao());
						itemPesqProdu.setSelecionada(true);
					}	
				}
			}
		}
	}
	
	/**
	 * Verifica os itens selecionados nos CheckBoxs. Chamado quando se vai cadastrar o PID.
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private List<CargaHorariaAtividadesComplementares> vericarAtividaesComplementaresSelecionadas() throws HibernateException, DAOException {
		
		List<AtividadeComplementarPID> itensSelecionados = new ArrayList<AtividadeComplementarPID>();
		List<CargaHorariaAtividadesComplementares> listaAtividadesComplementares = new ArrayList<CargaHorariaAtividadesComplementares>();
		PlanoIndividualDocenteDao pidDAO = getDAO(PlanoIndividualDocenteDao.class);
		
		for (AtividadeComplementarPID ativEnsino : listaGrupoEnsino) {
			if (ativEnsino.isSelecionada())
				itensSelecionados.add(ativEnsino);
		}
		for (AtividadeComplementarPID ativExtensao : listaGrupoAtivExtensao) {
			if (ativExtensao.isSelecionada())
				itensSelecionados.add(ativExtensao);
		}
		for (AtividadeComplementarPID ativPesqui : listaGrupoAtivPesqProducaTec) {
			if (ativPesqui.isSelecionada())
				itensSelecionados.add(ativPesqui);
		}
		for (AtividadeComplementarPID ativAdmin : listaGrupoAtivAdmin) {
			if (ativAdmin.isSelecionada())
				itensSelecionados.add(ativAdmin);
		}
		
		if (obj.getId() == 0) {
			for (AtividadeComplementarPID atividade : itensSelecionados) {
				CargaHorariaAtividadesComplementares cargaHoraria = new CargaHorariaAtividadesComplementares();
				cargaHoraria.setAtividadeComplementarPID(atividade);
				cargaHoraria.setPlanoIndividualDocente(obj);
				
				cargaHoraria.setObservacao(atividade.getObservacao());
			
				listaAtividadesComplementares.add(cargaHoraria);
			}
		}
		
		if (obj.getId() != 0) {
		
			List<CargaHorariaAtividadesComplementares> listaCHAtivComplementarBanco = pidDAO.findCargaHorariaAtividadesComplementaresByPID(obj);
			List<AtividadeComplementarPID> itensExistentesBanco = new ArrayList<AtividadeComplementarPID>();

			for (CargaHorariaAtividadesComplementares chBanco : listaCHAtivComplementarBanco) {
				chBanco.getAtividadeComplementarPID().setSelecionada(true);
				itensExistentesBanco.add(chBanco.getAtividadeComplementarPID());
			}
				
			for (AtividadeComplementarPID ativiSelecionada : itensSelecionados) {
				if (!itensExistentesBanco.contains(ativiSelecionada)) { // se o usuário selecionou novos itens no checkbox, adiciona-os.  
					CargaHorariaAtividadesComplementares cargaHoraria = new CargaHorariaAtividadesComplementares();
					cargaHoraria.setAtividadeComplementarPID(ativiSelecionada);
					cargaHoraria.setPlanoIndividualDocente(obj);
					
					cargaHoraria.setObservacao(ativiSelecionada.getObservacao());
					
					listaAtividadesComplementares.add(cargaHoraria);
				}
			}
			
				for (CargaHorariaAtividadesComplementares ativBanco : listaCHAtivComplementarBanco) {
					// verifica se o usuário desmarcou alguma atividade em relação ao que existe no banco dados
					if (!itensSelecionados.contains(ativBanco.getAtividadeComplementarPID())) { 
						chAtividadeComplementarRemover.add(ativBanco);
					}
				}
				
				// adiciona/atualiza os observações das atividades selecionadas
				for (CargaHorariaAtividadesComplementares ativSelecionada : listaCHAtivComplementarBanco) {
					AtividadeComplementarPID item = ativSelecionada.getAtividadeComplementarPID();
					
					if (itensSelecionados.contains(item)) {
						ativSelecionada.setObservacao(itensSelecionados.get(itensSelecionados.indexOf(item)).getObservacao());

						listaAtividadesComplementares.add(ativSelecionada);
					}
				}
			
		} // fim modo alteração
		
		return listaAtividadesComplementares;
	}

	/**
	 * O docente pode querer adicionar atividades específicas manualmente. São atividades 
	 * que NÃO estão na lista de atividades disponíveis para serem selecionadas por checkbox.
	 *
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
	 * 
	 * @param arg0
	 * @throws DAOException 
	 */
	public void adicionarAtividadeComplementar() throws DAOException {
		
		ListaMensagens lista = obj.validateOutrasAtividades();
		
		for (MensagemAviso msg : lista.getMensagens())
			addMensagemErroAjax(msg.getMensagem());
			
		if (obj.getAtividadeAutorizadaCONSEPE() && lista.isEmpty()) {
			AtividadesEspecificasDocente ativ = new AtividadesEspecificasDocente();
			ativ.setDenominacao(obj.getOutrasAtividades()); // docente adiciona manualmente alguma atividade
			ativ.setTipoAtividadeComplementar(getDAO(GenericDAOImpl.class).findByPrimaryKey(TipoAtividadeComplementarPID.OUTRAS_ATIVIDADES, TipoAtividadeComplementarPID.class));
			ativ.setPlanoIndividualDocente(obj);
 			ativEspecificasAdicionasDocente.add(ativ);
			obj.setOutrasAtividades("");
		}
		else if (lista.isEmpty()) {
			MensagemAviso msg = UFRNUtils.getMensagem(MensagensGerais.NAO_AUTORIZADO_PELO_CONSEPE);
			addMensagemErroAjax(msg.getMensagem()); 
			obj.setOutrasAtividades("");
		}
	}
	
	/**
	 * Remove uma atividade complementar adicionada manualmente pelo docente
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
 	 * 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public void removerAtividadeComplementar() throws NegocioException, ArqException {
		
		int id = getParameterInt("id", -1);
		if ( id >= 0 ) {
			ativEspecificasAdicionasDocenteRemover.add( ativEspecificasAdicionasDocente.get(id) );
			ativEspecificasAdicionasDocente.remove(id);
		}
	}

	/**
	 * Carrega e popula a CH de administração
	 * 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private void popularCHAdministracao(boolean chefeDepartamento) throws HibernateException, DAOException {
			br.ufrn.sigaa.arq.dao.rh.DesignacaoDAO designacaoDao = getDAO(br.ufrn.sigaa.arq.dao.rh.DesignacaoDAO.class);
			listaChAdmin = designacaoDao.findAllDesignacoesByServidorAnoPeriodo(obj.getServidor().getId(),ano,periodo );
			
			if (obj.getId() == 0) {
				for (Designacao designacao : listaChAdmin) {
					CargaHorariaAdministracao chAdmin = new CargaHorariaAdministracao();
					chAdmin.setDesignacao(designacao);
					chAdmin.setPlanoIndividualDocente(obj);
					
					obj.getDesignacoesDocente().add(chAdmin);
				}
			}
			if (obj.getId() != 0) {
				// chefe departamento sempre visualiza as designações que o docente possuía quando submeteu o PID
				// mesmo que sejam adicionadas/removidas designações posteriormente 
				if (chefeDepartamento) {
					listaChAdmin.clear();
					for (CargaHorariaAdministracao it : obj.getDesignacoesDocente())
						listaChAdmin.add(it.getDesignacao());
				}
				else
					verificarMudancaCHDesignacoes();
			}
	}

	/**
	 * Remove os registros de CargaHorariaAdministracao e adiciona ao objeto as designações que foram carregadas do banco.
	 *   
	 */  
	private void verificarMudancaCHDesignacoes() {
		
		ArrayList<Designacao> listaDesignacoesACadastrar = new ArrayList<Designacao>();
		listaDesignacoesRemover = new ArrayList<CargaHorariaAdministracao>();
		
		for (CargaHorariaAdministracao designacao : obj.getDesignacoesDocente())
			listaDesignacoesRemover.add(designacao);
		
		for (Designacao itemBD : listaChAdmin)
			listaDesignacoesACadastrar.add(itemBD);  
		
		obj.setDesignacoesDocente(new ArrayList<CargaHorariaAdministracao>());
		for (Designacao designacao : listaDesignacoesACadastrar) {
			CargaHorariaAdministracao chAdminstracao = new CargaHorariaAdministracao();
			chAdminstracao.setDesignacao(designacao);
			chAdminstracao.setPlanoIndividualDocente(obj);
			obj.getDesignacoesDocente().add(chAdminstracao);		
		}	
	}

	/**
	 * Carrega e popula os projetos de Pesquisa e Extensão na qual o Docente logado seja coordenador
	 * 
	 * @throws DAOException
	 */
	private void popularCHProjeto() throws DAOException {

		listaCargaHorariaProjetoPesquisa.clear();
		listaCargaHorariaProjetoExtensao.clear();
		listaCargaHorariaProjetoEnsino.clear();
		
		Date dataReferencia = obj.getId() == 0 ? new Date() : obj.getDataCadastro();
		
		Date dataReferenciaPesquisaInicio = new Date();
		Date dataReferenciaPesquisaFim = new Date();
		carregarDataReferencia(dataReferenciaPesquisaInicio,dataReferenciaPesquisaFim);
		
		MembroProjetoDao membroProjetoDao = getDAO(MembroProjetoDao.class);
		Collection<MembroProjeto> listaProjetoPesquisa = membroProjetoDao.findProjetosPesquisaAnoPeriodoAtual( obj.getServidor().getId());
		Collection<MembroProjeto> listaProjetoAssociados = membroProjetoDao.findProjetosAssociadosAnoPeriodoAtual(obj.getServidor().getId());
		
		for (MembroProjeto membroProjeto : listaProjetoPesquisa) {
			CargaHorariaProjeto cargaHorariaProjeto = new CargaHorariaProjeto();
			cargaHorariaProjeto.setMembroProjeto(membroProjeto);
			obj.getChProjeto().setMembroProjeto(membroProjeto);

			// se projeto de pesquisa possuir dataFim null exibe na listagem. se possuir dataFim, verifica validade.
			if (membroProjeto.getDataFim() == null) {
				listaCargaHorariaProjetoPesquisa.add(cargaHorariaProjeto);
			} else if (membroProjeto.getDataInicio().before(dataReferenciaPesquisaFim) && membroProjeto.getDataFim().after(dataReferenciaPesquisaInicio)) {
				listaCargaHorariaProjetoPesquisa.add(cargaHorariaProjeto);
			}
		}		
		// projetos associados de pesquisa
		for (MembroProjeto membroProjeto : listaProjetoAssociados) {
			CargaHorariaProjeto cargaHorariaProjeto = new CargaHorariaProjeto();
			cargaHorariaProjeto.setMembroProjeto(membroProjeto);
			obj.getChProjeto().setMembroProjeto(membroProjeto);
			if (membroProjeto.getProjeto().isPesquisa()) {
				listaCargaHorariaProjetoPesquisa.add(cargaHorariaProjeto);
			}
		}
		Collection<MembroProjeto> listaAtividadeExtensao = membroProjetoDao.findProjetosExtensaoAnoPeriodoAtual( obj.getServidor().getId());
		
		for (MembroProjeto membroProjetoExtensao : listaAtividadeExtensao) {
			CargaHorariaProjeto cargaHorariaProjeto = new CargaHorariaProjeto();
			cargaHorariaProjeto.setMembroProjeto(membroProjetoExtensao);
			obj.getChProjeto().setMembroProjeto(membroProjetoExtensao);

			// se projeto de extensão possuir dataFim null exibe na listagem. se possuir dataFim, verifica validade.
			if (membroProjetoExtensao.getDataFim() != null 
					&& membroProjetoExtensao.getDataFim().after(dataReferencia) ) {
				listaCargaHorariaProjetoExtensao.add(cargaHorariaProjeto);
			}
			else // projetos com dataFim null adiciona a lista sem considerar a dataFim
				listaCargaHorariaProjetoExtensao.add(cargaHorariaProjeto);
		}
		// projetos associados de extensão
		for (MembroProjeto membroProjeto : listaProjetoAssociados) {
			CargaHorariaProjeto cargaHorariaProjeto = new CargaHorariaProjeto();
			cargaHorariaProjeto.setMembroProjeto(membroProjeto);
			obj.getChProjeto().setMembroProjeto(membroProjeto);
			if (membroProjeto.getProjeto().isExtensao()) {
				listaCargaHorariaProjetoExtensao.add(cargaHorariaProjeto);
			}
		}
		
		Collection<MembroProjeto> listaProjetoEnsino = membroProjetoDao.findProjetosEnsinoAnoPeriodoAtual(obj.getServidor().getId());
		
		for (MembroProjeto membroProjetoEnsino : listaProjetoEnsino) {
			CargaHorariaProjeto cargaHorariaProjeto = new CargaHorariaProjeto();
			cargaHorariaProjeto.setMembroProjeto(membroProjetoEnsino);
			obj.getChProjeto().setMembroProjeto(membroProjetoEnsino);

			// se projeto de ensino possuir dataFim null exibe na listagem. se possuir dataFim, verifica validade.
			if (membroProjetoEnsino.getDataFim() != null 
					 ) {
				listaCargaHorariaProjetoEnsino.add(cargaHorariaProjeto);
			}
			else // projetos com dataFim null adiciona a lista sem considerar a dataFim
				listaCargaHorariaProjetoEnsino.add(cargaHorariaProjeto);
		}
		// projetos associados de ensino
		for (MembroProjeto membroProjeto : listaProjetoAssociados) {
			CargaHorariaProjeto cargaHorariaProjeto = new CargaHorariaProjeto();
			cargaHorariaProjeto.setMembroProjeto(membroProjeto);
			obj.getChProjeto().setMembroProjeto(membroProjeto);
			if (membroProjeto.getProjeto().isEnsino()) {
				listaCargaHorariaProjetoEnsino.add(cargaHorariaProjeto);
			}
		}
		if (obj.getId() == 0) {
			for (CargaHorariaProjeto projetoPesquisa : listaCargaHorariaProjetoPesquisa)
				obj.getChProjeto().addCHProjetoPIDMembroProjeto(new ChProjetoPIDMembroProjeto(), projetoPesquisa.getMembroProjeto());
			
			for (CargaHorariaProjeto projetoExtensao : listaCargaHorariaProjetoExtensao)
				obj.getChProjeto().addCHProjetoPIDMembroProjeto(new ChProjetoPIDMembroProjeto(), projetoExtensao.getMembroProjeto());
			
			for (CargaHorariaProjeto projetoEnsino : listaCargaHorariaProjetoEnsino)
				obj.getChProjeto().addCHProjetoPIDMembroProjeto(new ChProjetoPIDMembroProjeto(), projetoEnsino.getMembroProjeto());
		}
		if (obj.getId() != 0) {
			verificarMudancasProjetoPID();
		}
	}

	/**
	 * Retorna a data do último dia do período. Tem como objetivo ser utilizado para verificar se o docente participou de algum 
	 * projeto de pesquisa no período determinado.
	 * Não invocado por JSPs.  
	 */ 
	private void carregarDataReferencia(Date dataReferenciaInicio, Date dataReferenciaFim) {
		
		Integer anoReferencia;
		Integer periodoReferencia;
		
		if (obj.getId() == 0){
			int mes = br.ufrn.arq.util.CalendarUtils.getMesAtual();
			anoReferencia = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
			periodoReferencia = (( mes < 6 ) ? 1 :  2);			
		} else {
			anoReferencia = obj.getAno();
			periodoReferencia = obj.getPeriodo();
		}
		
		Integer mesInicio = periodoReferencia == 1 ? 0 : 5;
		Integer mesFim = periodoReferencia == 1 ? 6 : 0;
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.MONTH, mesInicio);
		cal.set(Calendar.YEAR, anoReferencia);
		dataReferenciaInicio.setTime(cal.getTime().getTime());

		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.MONTH, mesFim);
		cal.add(Calendar.DAY_OF_MONTH,-1);
		cal.set(Calendar.YEAR, anoReferencia);		
		dataReferenciaFim.setTime(cal.getTime().getTime());
	}

	/**
	 * Remove os registros de ChProjetoPIDMembroProjeto e adiciona ao objeto os projeto que foram carregadas do banco.
	 */
	private void verificarMudancasProjetoPID() {
		
		ArrayList<CargaHorariaProjeto> listaProjetosACadastrar = new ArrayList<CargaHorariaProjeto>();
		projetosRemover = new ArrayList<ChProjetoPIDMembroProjeto>();
		
		for (ChProjetoPIDMembroProjeto projetos : obj.getChProjeto().getChProjetoPIDMembroProjeto())
			projetosRemover.add(projetos);
		
		for (CargaHorariaProjeto itemBD : listaCargaHorariaProjetoPesquisa)
			listaProjetosACadastrar.add(itemBD);  
		for (CargaHorariaProjeto itemBD : listaCargaHorariaProjetoExtensao)
			listaProjetosACadastrar.add(itemBD);  
		for (CargaHorariaProjeto itemBD : listaCargaHorariaProjetoEnsino)
			listaProjetosACadastrar.add(itemBD);  
		
		obj.getChProjeto().setChProjetoPIDMembroProjeto(new ArrayList<ChProjetoPIDMembroProjeto>());
		for (CargaHorariaProjeto cargaHorariaProjeto : listaProjetosACadastrar)
			obj.getChProjeto().addCHProjetoPIDMembroProjeto(new ChProjetoPIDMembroProjeto(), cargaHorariaProjeto.getMembroProjeto());
	}
	
	/**
	 * Popular CH Orientação de Graduação e Pós-Graduação
	 * @throws DAOException
	 */
	private void popularCHOrientacao(boolean chefeDepartamento) throws DAOException {

			PlanoIndividualDocenteDao planoOriDocenteDao = getDAO(PlanoIndividualDocenteDao.class);
			
			ArrayList<Integer> situacoesMatricula = new ArrayList<Integer>();
			situacoesMatricula.add(SituacaoMatricula.MATRICULADO.getId());
			situacoesMatricula.add(SituacaoMatricula.APROVADO.getId());
			
			// orientações de graduação
			Collection<OrientacaoAtividade> orientacoesAtividade = getDAO(OrientacaoAtividadeDao.class).
				findByOrientadorDiscenteComponente(obj.getServidor().getId(), null, null, obj.getAno(),
				obj.getPeriodo(), situacoesMatricula, null, false, NivelEnsino.GRADUACAO,null);

			TipoOrientacaoPID tipoOrientacao = planoOriDocenteDao.findTipoOrientacaoPIDByTipo(TipoOrientacaoPID.GRADUACAO);
			for (OrientacaoAtividade orientacaoAtiv : orientacoesAtividade) {
				int chDedicada = orientacaoAtiv.getRegistroAtividade().getMatricula().getComponente().getDetalhes().getChDedicadaDocente();
				CargaHorariaOrientacao chOrientacao = new CargaHorariaOrientacao();
				chOrientacao.setMatriculaComponente(orientacaoAtiv.getRegistroAtividade().getMatricula());
				chOrientacao.setDiscente(orientacaoAtiv.getRegistroAtividade().getMatricula().getDiscente().getDiscente());
				chOrientacao.setPlanoIndividualDocente(obj);
				chOrientacao.setTipoOrientacao(tipoOrientacao);
				chOrientacao.setChDedicada(chDedicada);
				if (chDedicada > 0 ){
					listaCHOrientacaoGraduacao.add(chOrientacao);
				} else {
					listaCHOrientacaoGraduacaoSemAtividades.add(chOrientacao);
				}
			}
			
			// orientações de pós-graduação
			Collection<OrientacaoAcademica> orientacoesAcademica = getDAO(OrientacaoAcademicaDao.class).findAllStrictoByServidor(obj.getServidor());
			tipoOrientacao = planoOriDocenteDao.findTipoOrientacaoPIDByTipo(TipoOrientacaoPID.POS_GRADUACAO);
			for (OrientacaoAcademica orientacaoAcademica : orientacoesAcademica) {
				CargaHorariaOrientacao chOrientacao = new CargaHorariaOrientacao();
				chOrientacao.setDiscente(orientacaoAcademica.getDiscente());
				chOrientacao.setPlanoIndividualDocente(obj);
				chOrientacao.setTipoOrientacao(tipoOrientacao);
				
				listaCHOrientacaoPosGraduacao.add(chOrientacao);
			}
			
			if (obj.getId() == 0) {
				for (CargaHorariaOrientacao chOrientacaoGraduacao : listaCHOrientacaoGraduacao)
					obj.getChOrientacao().add(chOrientacaoGraduacao);
				
				for (CargaHorariaOrientacao chOrientacaoPos : listaCHOrientacaoPosGraduacao)
					obj.getChOrientacao().add(chOrientacaoPos);
			}
			if (obj.getId() != 0) {
				// chefe departamento sempre visualiza os orientações que o docente possuía quando submeteu o PID
				// mesmo que sejam adicionadas/removidas orientações após a submissão 
				if (chefeDepartamento) {   
					listaCHOrientacaoGraduacao.clear();
					listaCHOrientacaoPosGraduacao.clear();
					for (CargaHorariaOrientacao it : obj.getChOrientacao()) {
						if (it.getTipoOrientacao().isGraduacao())
							listaCHOrientacaoGraduacao.add(it);
						else
							listaCHOrientacaoPosGraduacao.add(it);
					}
				}
				else
					verificarMudancasOrientacoesPID();
			}
	}

	/**
	 * Prepara a remoção de todas as orientações e adiciona ao objeto as orientações que foram carregadas do banco.  
	 */
	private void verificarMudancasOrientacoesPID() {
		
		ArrayList<CargaHorariaOrientacao> listaOrientacoesACadastrar = new ArrayList<CargaHorariaOrientacao>();
		listaCargaHorariaOrientacaoRemover = new ArrayList<CargaHorariaOrientacao>();
		
		for (CargaHorariaOrientacao ch : obj.getChOrientacao())
			listaCargaHorariaOrientacaoRemover.add(ch); //remove todas orientações para cadastrar as orientações que foram carregadas
		
		for (CargaHorariaOrientacao itemBD : listaCHOrientacaoGraduacao)
			listaOrientacoesACadastrar.add(itemBD);  
		for (CargaHorariaOrientacao itemBD : listaCHOrientacaoPosGraduacao)
			listaOrientacoesACadastrar.add(itemBD);  
		
		obj.setChOrientacao(new ArrayList<CargaHorariaOrientacao>());
		obj.setChOrientacao(listaOrientacoesACadastrar);
	}
	
	/**
	 * Popular CH Ensino
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void popularCHEnsino(boolean chefeDepartamento) throws ArqException, NegocioException {
		DocenteTurmaDao docenteTurmaDao = getDAO(DocenteTurmaDao.class);
		listaDocenteTurma = docenteTurmaDao.findByDocenteTurma(obj.getServidor().getId(), obj.getAno(), obj.getPeriodo());
		
		ParametrosGestoraAcademica param = getParametrosAcademicos();
		for (DocenteTurma dt : listaDocenteTurma)
			PIDUtils.calcularChDedicadaSemana(dt, param.getHorasCreditosAula(), param.getHorasCreditosEstagio());
		
		for (DocenteTurma docenteTurma : buscarTurmasFerias()) {
			listaDocenteTurma.add(docenteTurma);
		}
				
		if(obj.getId() == 0) {
			for (DocenteTurma docenteTurma : listaDocenteTurma)
				obj.getChEnsino().addCHEnsinoDocenteTurma(new ChEnsinoPIDocenteTurma(), docenteTurma);
		}
		if (obj.getId() != 0) {
			
			// chefe departamento sempre visualiza as turmas que o docente possuía quando submeteu o PID
			// mesmo que sejam adicionadas/removidas turmas após a submissão 
			if (chefeDepartamento) {   
				listaDocenteTurma.clear();
				for (ChEnsinoPIDocenteTurma it : obj.getChEnsino().getChEnsinoDocenteTurma()){
					DocenteTurma dt = it.getDocenteTurma();
					PIDUtils.calcularChDedicadaSemana(dt, param.getHorasCreditosAula(), param.getHorasCreditosEstagio());
					listaDocenteTurma.add(dt);
				}	
			}
			else 
				verificarMudancasTurmasPID();
		}
		
		boolean turmaRemovida = false;
		
		// remove as turmas de lato sensu
		Iterator<DocenteTurma> iterator = listaDocenteTurma.iterator(); 
		while (iterator.hasNext()){
			DocenteTurma dt = iterator.next();
			if (dt == null) {
				turmaRemovida = true;
				iterator.remove();
			} else if (dt.getTurma().isDistancia()) {
				iterator.remove();
			} else if (dt.getTurma().isLato()) {
				iterator.remove();
			}
		}
		if (turmaRemovida)
			addMensagemWarning("Houveram alterações nas turmas do docente desde que seu PID foi cadastrado. É preciso que o docente refaça o PID para submissão e homologação da chefia para refletir a nova situação de Carga Horária.");
		// verifica se o docente possui CH dedicada de Residência Médica. Caso tenha, popula um objeto ChEnsinoPIDocenteTurma para exibir
		// na mesma listagem das turmas que o Docente possui para fins de visualização.
		popularTurmasResidenciaMedica();
		popularEnsinoEAD();
	}

	/**
	 * Prepara a remoção de todas as turmas de residência médica e adiciona ao objeto as turmas que foram carregadas do banco. 
	 * É necessário pois podem existir alterações nas turmas do docente após o mesmo ter submetido o PID (como remoção de turma,
	 * adição de turma ou transferência de turma para outros professores).
	 * 
	 * 
	 * @throws ArqException
	 */
	private void verificarMudancasTurmasPID() throws ArqException {
		
		ArrayList<DocenteTurma> listaTurmasACadastrar = new ArrayList<DocenteTurma>();
		turmasTransferidasOuRemovidas = new ArrayList<ChEnsinoPIDocenteTurma>();
		
		for (ChEnsinoPIDocenteTurma chEnsino : obj.getChEnsino().getChEnsinoDocenteTurma())
			turmasTransferidasOuRemovidas.add(chEnsino);
		
		for (DocenteTurma itemBD : listaDocenteTurma)
			listaTurmasACadastrar.add(itemBD);  
		
		obj.getChEnsino().setChEnsinoDocenteTurma(new ArrayList<ChEnsinoPIDocenteTurma>());
		for (DocenteTurma docenteTurma : listaTurmasACadastrar)
			obj.getChEnsino().addCHEnsinoDocenteTurma(new ChEnsinoPIDocenteTurma(), docenteTurma);		
	}
	
	/**
	 * Prepara a remoção de todas as turmas de residência médica e adiciona ao objeto as turmas que foram carregadas do banco.  
	 * 
	 * @param listaTurmasResidenciaMedica
	 * @throws ArqException
	 */
	private void verificarMudancasTurmasResidenciaMedica(Collection<CHDedicadaResidenciaMedica> listaTurmasResidenciaMedica) throws ArqException {
		
		ArrayList<CHDedicadaResidenciaMedica> listaTurmasResidenciaMedicaCadastrar = new ArrayList<CHDedicadaResidenciaMedica>();
		turmasResidenciaMedicaRemover = new ArrayList<ChResidenciaMedicaPID>();
		
		for (ChResidenciaMedicaPID chResidenciaMed : obj.getChResidenciaMedicaPID())
			turmasResidenciaMedicaRemover.add(chResidenciaMed);
		
		for (CHDedicadaResidenciaMedica itemBD : listaTurmasResidenciaMedica)
			listaTurmasResidenciaMedicaCadastrar.add(itemBD);  
		
		obj.setChResidenciaMedicaPID(new ArrayList<ChResidenciaMedicaPID>());
		for (CHDedicadaResidenciaMedica residencia : listaTurmasResidenciaMedicaCadastrar)
			obj.addCHResidenciaMedicaPID(residencia, new ChResidenciaMedicaPID());	
	}

	/**
	 * Popular as turmas de residência médica caso existam para o docente 
	 * @throws ArqException 
	 */
	private void popularTurmasResidenciaMedica() throws ArqException {
		
		CHDedicadaResidenciaMedicaDao dao = getDAO(CHDedicadaResidenciaMedicaDao.class);
		List<CHDedicadaResidenciaMedica> listaTurmasResidenciaMedica = dao.findByServidorAnoPeriodo(obj.getServidor().getId(), obj.getAno(), obj.getPeriodo()); 
		
		for (CHDedicadaResidenciaMedica chDedicadaResidenciaMedica : listaTurmasResidenciaMedica) {
			
			ChEnsinoPIDocenteTurma chEnsinoPIDocenteTurma = new ChEnsinoPIDocenteTurma();
			DocenteTurma docenteTurma = new DocenteTurma();
			Turma turma = new Turma(); ComponenteCurricular compC = new ComponenteCurricular(); 
			compC.setNome(chDedicadaResidenciaMedica.getProgramaResidenciaMedica().getNome()); compC.setNivel(NivelEnsino.RESIDENCIA);
			turma.setDisciplina(compC);	
			docenteTurma.setTurma(turma);
			chEnsinoPIDocenteTurma.setDocenteTurma(docenteTurma);
			
			chEnsinoPIDocenteTurma.getDocenteTurma().setChResidenciaSemTurma(true);
			chEnsinoPIDocenteTurma.getDocenteTurma().setChDedicadaPeriodo(0);
			chEnsinoPIDocenteTurma.getDocenteTurma().getTurma().setAno(chDedicadaResidenciaMedica.getAno());
			chEnsinoPIDocenteTurma.getDocenteTurma().getTurma().setPeriodo(chDedicadaResidenciaMedica.getSemestre());
			chEnsinoPIDocenteTurma.getDocenteTurma().setChDedicadaPeriodo(chDedicadaResidenciaMedica.getChSemanal());
			
			// para fins de visualização e contagem de CH de Residência Médica, adiciona essas CHs nessa listagem.
			listaDocenteTurma.add(chEnsinoPIDocenteTurma.getDocenteTurma());
			// adiciona na listagem dessa entidade apenas para visualização e cálculo do valor da CH na view. removido antes de cadastrar.
			obj.getChEnsino().getChEnsinoDocenteTurma().add(chEnsinoPIDocenteTurma);
			
			if (obj.getId() == 0) {
				obj.addCHResidenciaMedicaPID(chDedicadaResidenciaMedica, new ChResidenciaMedicaPID());
			}
		}
		if (obj.getId() != 0)
			verificarMudancasTurmasResidenciaMedica(listaTurmasResidenciaMedica);
	}
	
	/**
	 * Popular a carga horária de ensino à distância 
	 * @throws ArqException 
	 */
	private void popularEnsinoEAD() throws ArqException {
		CargaHorariaEadDAO dao = getDAO(CargaHorariaEadDAO.class);
		List<CargaHorariaEad> lista = dao.findByServidorAnoPeriodo(obj.getServidor().getId(), obj.getAno(), obj.getPeriodo()); 
		obj.setChEnsinoEad(lista);
	}

	/**
	 * Traz as turmas do docente de acordo com o ano/período das férias 
	 * Os PIDs dos semestres .2 devem trazer as disciplinas ministradas nas férias .3
	 * Os PIDs do semestre .1 devem trazer as turmas ministradas nas férias .4 do ano anterior.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private Collection<DocenteTurma> buscarTurmasFerias() throws DAOException {
		// necessário caso esteja acessando do portal público, não existe usuário logado
		int periodoFerias = obj.getPeriodo();
		int anoFerias = obj.getAno();
		
		if (periodoFerias == 1)
			anoFerias = obj.getAno()-1;
		else
			anoFerias = obj.getAno();
			
		if (obj.getPeriodo() == 1)
			periodoFerias = 4; // férias de fim de ano
		if (obj.getPeriodo() == 2)
			periodoFerias = 3; // férias do meio do ano
			
		return getDAO(DocenteTurmaDao.class).findByDocenteTurma(obj.getServidor().getId(), anoFerias, periodoFerias);
	}

	/**
	 * Valida a CH do PID antes de permitir o docente enviar o PID para homologação
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
	 * 
	 */
	public String paginaConfirmacaoCadastro() throws NegocioException, SegurancaException, ArqException {
		if ( !obj.isCargaHorariaDocenteExcedida() ) { 
			double total = UFRNUtils.truncateDouble(obj.getTotalGrupoEnsino() + obj.getTotalGrupoOutrasAtividades(), 1);
			if ( obj.getServidor().getRegimeTrabalho() != total ) {
				addMensagemErro("Caro docente, para enviar o Plano Individual do Docente é necessário que você distribua sua carga horária para que a mesma tenha um total igual a " + obj.getServidor().getRegimeTrabalho() + " horas semanais.");
				return forward("/pid/form.jsf");
			}
		}
		
		prepareMovimento(ArqListaComando.ALTERAR);
		return forward("/pid/confirma_cadastro.jsf");
	}
	
	/**
	 * Cadastra o Plano Individual do Docente - PID
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws NegocioException, SegurancaException, ArqException {

		ListaMensagens listaMsgs = obj.validate();
		if (listaMsgs != null && !listaMsgs.isEmpty()) {
			addMensagens(listaMsgs);
			return forward("/pid/form.jsf");
		}
		
		Integer statusAnterior = obj.getStatus();
		
		obj.setAno(ano);
		obj.setPeriodo(periodo);
		obj.setStatus(PlanoIndividualDocente.CADASTRADO);
		obj.setAtividadesEspecificasDocente(ativEspecificasAdicionasDocente);
		obj.setChAtividadesComplementares( vericarAtividaesComplementaresSelecionadas() );

		// remove a CH de residência médica da listagem de CH de ensino
		// necessário pois a turma de Residência Médica não é cadastrada no PID através dessa entidade
		removerCHResidenciaMedicaFromCHEnsino();
		
		try {
		
			MovimentoCadastro movCad = new MovimentoCadastro();
			if (obj.getId() == 0) {
				movCad.setObjMovimentado(obj);
				movCad.setCodMovimento(SigaaListaComando.CADASTRAR_PID);
			}
			if (obj.getId() != 0) {
				movCad.setObjAuxiliar(statusAnterior);
				movCad.setObjMovimentado(obj);
				movCad.setColObjMovimentado(chAtividadeComplementarRemover);
				movCad.setCodMovimento(SigaaListaComando.ALTERAR_PID);
				prepareMovimento(SigaaListaComando.ALTERAR_PID);
			}
			
			//cadastra/atualiza o PID
			execute(movCad);
			
			removerAtividadesEspecDocente();
			
			// remove os registros possivelmente antigos que existam no banco
			// pois o PID já foi cadastrado com os dados atualizados  
			removerTurmasPID();	
			removerCHDesignacoes(); 
			removerCHOrientacoes();
			removerCHResidenciaMedica();
			removeProjetosPID();
			
			PlanoIndividualDocenteDao pidDAO = getDAO(PlanoIndividualDocenteDao.class);
			clear();
			carregarDadosPID(pidDAO, null, false);
				
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		ativEspecificasAdicionasDocenteRemover = new ArrayList<AtividadesEspecificasDocente>();
		chAtividadeComplementarRemover = new ArrayList<CargaHorariaAtividadesComplementares>();
		
		addMensagemInformation("Plano Individual do Docente cadastrado com sucesso.");
		
		carregarTodasAtividadesComplementares();
		carregarAtividadesComplementaresSelecionadas();
		
		return forward("/pid/form.jsf");
	}
	
	/** Retorna ao formulário, para alterações.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/confirma_cadastro.jsp</li>
 	 * </ul>
	 * 
	 * @return
	 */
	public String voltarConfirmacao() {
		return forward("/pid/form.jsf");
	}
	
	/**
	 * Delega para o processador remover os projetos do PID
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void removeProjetosPID() throws ArqException, NegocioException {
		if (!projetosRemover.isEmpty()) {
			prepareMovimento(SigaaListaComando.REMOVER_PROJETOS_PID);
			MovimentoCadastro movRemoverTurmas = new MovimentoCadastro();
			movRemoverTurmas.setColObjMovimentado(projetosRemover);
			movRemoverTurmas.setCodMovimento(SigaaListaComando.REMOVER_PROJETOS_PID);
			execute(movRemoverTurmas);
		}	
	}
	
	/**
	 * Delega para o processador remover as turmas de residência médica 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void removerCHResidenciaMedica() throws ArqException, NegocioException {
		if (!turmasResidenciaMedicaRemover.isEmpty()) {
			prepareMovimento(SigaaListaComando.REMOVER_TURMAS_RESIDENCIA_MEDICA);
			MovimentoCadastro movRemoverTurmas = new MovimentoCadastro();
			movRemoverTurmas.setColObjMovimentado(turmasResidenciaMedicaRemover);
			movRemoverTurmas.setCodMovimento(SigaaListaComando.REMOVER_TURMAS_RESIDENCIA_MEDICA);
			execute(movRemoverTurmas);
		}	
	}

	/**
	 * Delega para o processador as atividades que o docente removeu do seu PID 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void removerAtividadesEspecDocente() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.REMOVER_ATIV_ESPECIFICAS_DOCENTE);
		MovimentoCadastro movRemoverAtivEspecDocente = new MovimentoCadastro();
		movRemoverAtivEspecDocente.setColObjMovimentado(ativEspecificasAdicionasDocenteRemover);
		movRemoverAtivEspecDocente.setCodMovimento(SigaaListaComando.REMOVER_ATIV_ESPECIFICAS_DOCENTE);
		
		execute(movRemoverAtivEspecDocente);
	}

	/**
	 * Após o PID ser cadastrado podem ocorrer mudanças nas turmas que estão vinculadas a esse PID
	 * Por exemplo: as turmas podem ser removidas ou alteradas para outro professor.
	 * Dessa forma é necessário remover essa entidades que não estão mais vinculados ao PID do docente. 
	 * 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void removerTurmasPID() throws ArqException, NegocioException {
		if (!turmasTransferidasOuRemovidas.isEmpty()) {
			prepareMovimento(SigaaListaComando.REMOVER_TURMAS_DOCENTE_PID);
			MovimentoCadastro movRemoverTurmas = new MovimentoCadastro();
			movRemoverTurmas.setColObjMovimentado(turmasTransferidasOuRemovidas);
			movRemoverTurmas.setCodMovimento(SigaaListaComando.REMOVER_TURMAS_DOCENTE_PID);
			execute(movRemoverTurmas);
		}
	}
	
	/**
	 * Remove as designações que o docente não possui mais 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void removerCHDesignacoes() throws ArqException, NegocioException {
		if (!listaDesignacoesRemover.isEmpty()) {
			prepareMovimento(SigaaListaComando.REMOVER_DESIGNACOES_DOCENTE_PID);
			MovimentoCadastro movRemoverTurmas = new MovimentoCadastro();
			movRemoverTurmas.setColObjMovimentado(listaDesignacoesRemover);
			movRemoverTurmas.setCodMovimento(SigaaListaComando.REMOVER_DESIGNACOES_DOCENTE_PID);
			execute(movRemoverTurmas);
		}
	}
	
	/**
	 * Remover as orientações do docente
	 * 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void removerCHOrientacoes() throws ArqException, NegocioException {
		if (!listaCargaHorariaOrientacaoRemover.isEmpty()) {
			prepareMovimento(SigaaListaComando.REMOVER_ORIENTACOES_DOCENTE_PID);
			MovimentoCadastro movRemoverOrientacoes = new MovimentoCadastro();
			movRemoverOrientacoes.setColObjMovimentado(listaCargaHorariaOrientacaoRemover);
			movRemoverOrientacoes.setCodMovimento(SigaaListaComando.REMOVER_ORIENTACOES_DOCENTE_PID);
			execute(movRemoverOrientacoes);
		}
	}
	
	/**
	 * Altera o status do PID para PlanoIndividualDocente.ENVIADO_HOMOLOGACAO.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/confirma_cadastro.jsp</li>
 	 * </ul>
	 *  
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String enviarPIDParaChefiaDepartamento() throws ArqException, NegocioException {
		
		if ( !obj.isConcordanciaTermoEnvioPID() ) {
			addMensagemErroAjax("Você precisa ler e concordar com o termo de veracidade especificado abaixo.");
			return null;
		}

		if( !confirmaSenha())
			return null;
		
		obj.setStatus(PlanoIndividualDocente.ENVIADO_HOMOLOGACAO);
		obj.setAtividadesEspecificasDocente(ativEspecificasAdicionasDocente);
		obj.setChAtividadesComplementares( vericarAtividaesComplementaresSelecionadas() );
		
		// remove a CH de residência médica da listagem de CH de ensino
		// necessário pois a turma de Residência Médica não será salva 
		// através da entidade CHEnsino. Turmas de Residência Médica são 
		// relacionadas através da entidade CHDedicadaResidenciaMedica
		removerCHResidenciaMedicaFromCHEnsino();
				
		prepareMovimento(ArqListaComando.ALTERAR);
		MovimentoCadastro movCad = new MovimentoCadastro();
		movCad.setObjMovimentado(obj);
		movCad.setCodMovimento(ArqListaComando.ALTERAR);
		
		try {
			execute(movCad);
		} catch (NegocioException e) {
			notifyError(e);
		}
		
		addMensagemInformation("Plano Individual do Docente foi enviado para a Chefia do Departamento com sucesso.");
		
		removerTurmasPID();	
		removerCHDesignacoes(); 
		removerCHOrientacoes();
		removerCHResidenciaMedica();
		removeProjetosPID();
		
		setReadOnly(true); // indica que o botão será "Voltar" na tela de visualização do PID
		setConfirmButton("<< Voltar");
		return forward("/pid/view.jsf");
	}
	
	/**
	 * Chefe do departamento homologa o PID enviado pelo docente
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/view.jsp</li>
 	 * </ul>
	 * 
	 * @throws ArqException 
	 * 
	 */
	public String homologarPID() throws ArqException {
		alterarStatusPID(PlanoIndividualDocente.HOMOLOGADO);
		addMensagemInformation("O PID desse docente foi alterado com sucesso");
		return gerarListagemPIDChefeDepartamento();
	}
	
	/**
	 * Altera o status do PID de acordo com o valor informado 
	 * 
	 * @param statusPID
	 * @throws ArqException
	 */
	private void alterarStatusPID(int statusPID) throws ArqException {
		
		obj.setStatus(statusPID);
		obj.setDataHomologacao(null);
		obj.setRegistroEntrada(null);
		
		if (statusPID == PlanoIndividualDocente.HOMOLOGADO) {
			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			obj.setDataHomologacao(new Date());
		}
		
		// remove a CH de residência médica da listagem de CH de ensino
		// necessário pois a turma de Residência Médica não é cadastrada no PID através dessa entidade
		removerCHResidenciaMedicaFromCHEnsino();
		
		MovimentoCadastro movCad = new MovimentoCadastro();
		movCad.setObjMovimentado(obj);
		movCad.setCodMovimento(ArqListaComando.ALTERAR);
		
		try {
			execute(movCad);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
			notifyError(e);
		}
		
		// envia o e-mail.
		if (obj.isHomologado()) {
			// envia e-mail para o docente informando que o PID foi homologado
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.TEXT_PLAN);
			mail.setAssunto("Plano Individual do Docente Atualizado.");
			mail.setMensagem("Prezado(a) " + obj.getServidor().getNome() + ", \n\n" + 
					"Informamos que o Plano Individual do Docente - PID - de "+
					obj.getAno()+"."+obj.getPeriodo()
					+" foi homologado pelo Chefe do Departamento.");
			mail.setEmail(obj.getServidor().getPessoa().getEmail());
			mail.setNome(obj.getServidor().getNome());
			Mail.send(mail);
		}
		
	}

	/**
	 * Recusa o PID enviado pelo docente
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/view.jsp</li>
 	 * </ul>
 	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String recusarPID() throws ArqException {
		alterarStatusPID(PlanoIndividualDocente.CADASTRADO);
		addMensagemInformation("O PID desse docente foi alterado com sucesso");
		return gerarListagemPIDChefeDepartamento();
	}
	
	/**
	 * A CH de residência médica que é exibida na mesma listagem de CHEnsino serve apenas 
	 * para facilitar/agrupar a visualização do usuário. Quando o docente possui CH de 
	 * residência médica essa CH é persistida na classe associativa ChResidenciaMedicaPID.
	 * 
	 * É necessário remover os objetos da lista de Ensino que representam a CH residência médica antes de cadastrar o PID.  
	 */
	private void removerCHResidenciaMedicaFromCHEnsino() {
		ArrayList<ChEnsinoPIDocenteTurma> chResidenciaMedicaRemover = new ArrayList<ChEnsinoPIDocenteTurma>();
		for (ChEnsinoPIDocenteTurma chResidenciaPID : obj.getChEnsino().getChEnsinoDocenteTurma()) {
			if (chResidenciaPID.getCargaHorariaEnsino()==null) // objeto que representa CH de residência médica
				chResidenciaMedicaRemover.add(chResidenciaPID);
		}
		
		for (ChEnsinoPIDocenteTurma chEnsinoPIDocenteTurma : chResidenciaMedicaRemover) {
			obj.getChEnsino().getChEnsinoDocenteTurma().remove(chEnsinoPIDocenteTurma);
		}
	}

	/**
	 * Calcula o total da CH de Ensino para exibir o total na tela
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
	 * 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void calcularTotalCargaHorariaEnsino(ActionEvent arg0) throws HibernateException, DAOException {
		Double chTotalDisciplinas = 0.0;
		ParametrosGestoraAcademica param = getParametrosAcademicos();
		
		for (DocenteTurma chDocenteTurma : listaDocenteTurma) {
			PIDUtils.calcularChDedicadaSemana(chDocenteTurma, param.getHorasCreditosAula(), param.getHorasCreditosEstagio());
			if (chDocenteTurma != null) {
				if (chDocenteTurma.getTurma() != null) {
					if (!chDocenteTurma.isChResidenciaSemTurma())
						chTotalDisciplinas += chDocenteTurma.getChDedicadaSemana();
					else
						chTotalDisciplinas += chDocenteTurma.getChDedicadaPeriodo();
				}	
			}
		}
		
		// atividades orientadas
		for (CargaHorariaOrientacao chOrientacao : listaCHOrientacaoGraduacao) {
			chTotalDisciplinas += chOrientacao.getChDedicadaSemanal();
		}
		
		// ensino à distância
		for (CargaHorariaEad chEad : obj.getChEnsinoEad()) {
			chTotalDisciplinas += chEad.getChDedicada();
		}
		
		chTotalDisciplinas = Math.round(chTotalDisciplinas * 10d) / 10d;
		
 		BigDecimal totalChTurmas = new BigDecimal(chTotalDisciplinas);
		BigDecimal totalChEnsino = new BigDecimal(obj.getChEnsino().calcularCHEnsino());
				
		BigDecimal totalFinal = totalChTurmas.add(totalChEnsino);
		
		obj.setTotalGrupoEnsino(totalFinal.doubleValue());
		
		chRestanteADistribuir = (obj.getServidor().getRegimeTrabalho() - totalFinal.doubleValue());
		
		calcularHorarioPorPercentualPesquisa(null);
		calcularHorarioPorPercentualExtensao(null);
		calcularHorarioPorPercentualAdministracao(null);
		calcularHorarioPorPercentualOutrasAtividadesEnsino(null);
		calcularHorarioPorPercentualOutrasAtividades(null);
	}
	
	/**
	 * Carrega todos os elementos para formar uma listagem de CheckBox de atividades complementares
	 * 
	 * @throws DAOException
	 */
	private void carregarTodasAtividadesComplementares() throws DAOException {
		
		if (obj.getChOutrasAtividades().getPercentualOutrasAtividadesEnsino() > 0 && listaGrupoEnsino.isEmpty())
			listaGrupoEnsino = (ArrayList<AtividadeComplementarPID>) exibirListagemCheckBox(TipoAtividadeComplementarPID.ENSINO);
		
		if (obj.getChProjeto().getPercentualExtensao() > 0 && listaGrupoAtivExtensao.isEmpty())
			listaGrupoAtivExtensao = (ArrayList<AtividadeComplementarPID>) exibirListagemCheckBox(TipoAtividadeComplementarPID.EXTENSAO_OUTRAS_ATIVIDADES);
		
		if (obj.getChProjeto().getPercentualPesquisa() > 0 && listaGrupoAtivPesqProducaTec.isEmpty())
			listaGrupoAtivPesqProducaTec = (ArrayList<AtividadeComplementarPID>) exibirListagemCheckBox(TipoAtividadeComplementarPID.PESQUISA_PROD_TEC_CIENT); 
		
		if (obj.getPercentualAdministracao() > 0 && listaGrupoAtivAdmin.isEmpty())
			listaGrupoAtivAdmin = (ArrayList<AtividadeComplementarPID>) exibirListagemCheckBox(TipoAtividadeComplementarPID.ADMINISTRACAO); 
	}
	
	/**
	 * Evento Ajax disparado ao mudar de campo. Calcula a CH de Pesquisa de acordo com o percentual informado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
 	 * 
	 * @param arg
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void calcularHorarioPorPercentualPesquisa(ActionEvent arg) throws HibernateException, DAOException  {
		if ( obj.getChProjeto().getPercentualPesquisa() == 0)
			listaGrupoAtivPesqProducaTec = new ArrayList<AtividadeComplementarPID>();
			
			obj.getChProjeto().setChPesquisa( calcularCargaHorariaPorPercentual(obj.getChProjeto().getPercentualPesquisa()) );
			somarCHPesquisaExtensaoOutrasAtiv();
			
			carregarTodasAtividadesComplementares();
			carregarAtividadesComplementaresSelecionadas();
	}

	/**
	 * Evento Ajax disparado ao mudar de campo. Calcula a CH de Extensão de acordo com o percentual informado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
 	 * 
	 * @param arg
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void calcularHorarioPorPercentualExtensao(ActionEvent arg) throws HibernateException, DAOException {
		if ( obj.getChProjeto().getPercentualExtensao() == 0)
			listaGrupoAtivExtensao = new ArrayList<AtividadeComplementarPID>();
			
			obj.getChProjeto().setChExtensao( calcularCargaHorariaPorPercentual(obj.getChProjeto().getPercentualExtensao()) );
			somarCHPesquisaExtensaoOutrasAtiv();
			
			carregarTodasAtividadesComplementares();
			carregarAtividadesComplementaresSelecionadas();
	}
	
	/**
	 * Carrega os elementos que forma a listagem de checkbox de acordo com o tipo da atividade informado 
	 * 
	 * @param idTipoAtividade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private List<AtividadeComplementarPID> exibirListagemCheckBox(int idTipoAtividade) throws HibernateException, DAOException {
		return getDAO(PlanoIndividualDocenteDao.class).findAtividadeComplementarPIDByTipo(idTipoAtividade);
	}
	
	/**
	 * Evento Ajax disparado ao mudar de campo. Calcula a CH de Administração acordo com o percentual informado.
 	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul> 
	 * 
	 * @param arg
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void calcularHorarioPorPercentualAdministracao(ActionEvent arg) throws HibernateException, DAOException {
		
		if (obj.getPercentualAdministracao() == 0)
			listaGrupoAtivAdmin = new ArrayList<AtividadeComplementarPID>();
			
			obj.setChTotalAdministracao( calcularCargaHorariaPorPercentual(obj.getPercentualAdministracao()) );
			somarCHPesquisaExtensaoOutrasAtiv();
			
			carregarTodasAtividadesComplementares();
			carregarAtividadesComplementaresSelecionadas();
	}
	
	/**
	 * Evento Ajax disparado ao mudar de campo. Calcula a CH de Outras Atividades de Ensino de acordo com o percentual informado.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
 	 *  
	 * @param arg
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void calcularHorarioPorPercentualOutrasAtividadesEnsino(ActionEvent arg) throws HibernateException, DAOException {
		if (obj.getChOutrasAtividades().getPercentualOutrasAtividadesEnsino() == 0)
			listaGrupoEnsino = new ArrayList<AtividadeComplementarPID>();

		obj.getChOutrasAtividades().setChSemanalOutrasAtividadesEnsino( calcularCargaHorariaPorPercentual(obj.getChOutrasAtividades().getPercentualOutrasAtividadesEnsino()) );
		somarCHPesquisaExtensaoOutrasAtiv();
		
		carregarTodasAtividadesComplementares();
		carregarAtividadesComplementaresSelecionadas();
	}
	
	/**
	 * Evento Ajax disparado ao mudar de campo. Calcula a CH de Outras Atividades de acordo com o percentual informado.
 	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/form.jsp</li>
 	 * </ul>
	 * 
	 * @param arg
	 */
	public void calcularHorarioPorPercentualOutrasAtividades(ActionEvent arg) {
		obj.getChOutrasAtividades().setChSemanalOutrasAtividades( calcularCargaHorariaPorPercentual(obj.getChOutrasAtividades().getPercentualOutrasAtividades()) );
		somarCHPesquisaExtensaoOutrasAtiv();
	}
	
	/**
	 * Soma todas as CH dos campos de Pesquisa, Extensão, Administração, Outras atividades, etc.
	 */
	private void somarCHPesquisaExtensaoOutrasAtiv() {
		double todosCampos =
			obj.getChProjeto().getChPesquisa() + obj.getChProjeto().getChExtensao() + 
			obj.getChTotalAdministracao() + 
			obj.getChOutrasAtividades().getChSemanalOutrasAtividades() + 
			obj.getChOutrasAtividades().getChSemanalOutrasAtividadesEnsino();
		
		obj.setTotalGrupoOutrasAtividades(todosCampos);
	}
	
	/**
	 * Transforma o percentual de dedicação informado pelo docente em CH. 
	 * Para realizar o cálculo, utiliza-se a CH que resta a ser distribuída. 
	 *   
	 * @param percentualInformado
	 * @return
	 */
	private double calcularCargaHorariaPorPercentual(double percentualInformado) {
		double percentual = (percentualInformado / 100);
		double x = (percentual * chRestanteADistribuir);
		
		if (chRestanteADistribuir < 0) {
			obj.setCHDocenteExcedida(true);
			chRestanteADistribuir = 0;
			return 0;
		}
		
		return x;
	}
	
	/**
	 * Gera listagem de PIDs enviados para o chefe do departamento
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/portais/docente/menu_docente.jsp</li>
 	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String gerarListagemPIDChefeDepartamento() throws DAOException {
		
		PlanoIndividualDocenteDao pidDao = null;
		ServidorDao sDao = null;
		
		inicializarFiltragem();
		
		try {
			pidDao = getDAO(PlanoIndividualDocenteDao.class);
			sDao = getDAO(ServidorDao.class);
			
			Unidade unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			
			Servidor docente = servidorFiltroChefia;
			if ( servidorFiltroChefia.getPessoa().getNome() == null || servidorFiltroChefia.getPessoa().getNome().isEmpty() ){
				todosDocentes = true;
				if (docentesUnidades == null)
					docentesUnidades = sDao.findDocenteNaoExternosByUnidade(unidade);		
				docente = null;
			} else
				todosDocentes= false;
			
			if ( situacaoFiltroChefia == TODOS_PIDS || situacaoFiltroChefia == PIDS_NAO_CADASTRADOS )
				allPIDDocente = pidDao.findPIDByAnoPeriodoServidorUnidadeStatus(anoFiltro, periodoFiltro, docente, unidade ,true);
			else
				allPIDDocente = pidDao.findPIDByAnoPeriodoServidorUnidadeStatus(anoFiltro, periodoFiltro, docente, unidade ,false,situacaoFiltroChefia);
			
			montarDatasCombo();
			montarSituacaoCombo();
			popularPIDSCadastrados();
			popularPIDSNaoCadastrados();
			
			return forward("/pid/homologacao/lista_pids_chefia.jsp");
		} finally {
			if ( pidDao != null )
				pidDao.close();
			if ( sDao != null )
				sDao.close();
		}
	}
		
	/**
	 * Exibe a listagem de PIDs do docente no decorrer do tempo
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/portais/docente/menu_docente.jsp</li>
 	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarListagemPIDDocente() throws DAOException {
		allPIDDocente = getDAO(PlanoIndividualDocenteDao.class).findAllPIDDocente(getUsuarioLogado().getServidor());
		incluiPseudoPlanos();
		return forward("/pid/listagem.jsp");
	}
	
	
	/**
	 * Exibe o PID em modo de visualização para que o chefe do departamento possa
	 * analisa-lo, homologar ou recusar.
	 * 
	 * <br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * 		<li>/sigaa.war/pid/homologacao/lista_pids_chefia.jsp</li>
 	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String visualizarPIDEnviadoChefiaDepartamento() throws ArqException, NegocioException {
		prepareMovimento(ArqListaComando.ALTERAR);
		setReadOnly(false);
		
		// seta que o chefe do departamento está visualizado o PID
		configurarVisualizacaoPID(getAcessoMenu().isChefeDepartamento());
		return forward("/pid/view.jsf");
	}
	
	/**
	 * Exibe o PID em modo de visualização 
	 * <br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
 	 * 		<li>/sigaa.war/pid/listagem.jsp</li>
 	 * </ul>
 	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String visualizarPID() throws ArqException, NegocioException {
		setReadOnly(true);
		// mesmo que o usuário seja chefe departamento e também docente, está exibindo o PID como docente
		configurarVisualizacaoPID(false); 
		return forward("/pid/view.jsf");
	}
	
	/**
	 * Método invocado para pré carregar os dados antes de exibir a tela de view do PID
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void configurarVisualizacaoPID(boolean chefeDepartamento) throws ArqException, NegocioException {
		setConfirmButton("<< Voltar");
		
		Integer idPID = getParameterInt("id", 0);
		
		PlanoIndividualDocenteDao pidDAO = getDAO(PlanoIndividualDocenteDao.class);
		obj = pidDAO.findByPrimaryKey(idPID, PlanoIndividualDocente.class);
		
		ano = obj.getAno();
		periodo = obj.getPeriodo();
		clear();
		if ( obj != null)
			carregarDadosPID(pidDAO, null, chefeDepartamento);
	}
	
	/**
	 * Libera ou bloqueia o acesso ao botão de submissão do PID para a chefia
	 * <br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
 	 * 		<li>/sigaa.war/pid/confirma_cadastro.jsp</li>
 	 * </ul>
	 *  
	 * @param arg
	 */
	public void liberarBotaoConfirmacao(ActionEvent arg) {
		if ( obj.isConcordanciaTermoEnvioPID() )
			obj.setConcordanciaTermoEnvioPID(true);
		else
			obj.setConcordanciaTermoEnvioPID(false);
			
	}
		
	/**
	 * Monta o combo com os períodos que possuem PIDs.
	 * 
	 * Não invocado por JSPs
	 * 
	 * @return
	 */
	private void montarDatasCombo () {		
		datasCombo = new ArrayList<SelectItem>();
		
		int mes = br.ufrn.arq.util.CalendarUtils.getMesAtual();
		int anoAtual = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
		int periodoAtual = (( mes < 6 ) ? 1 :  2);
		int anoInicial = PlanoIndividualDocente.ANO_INICIO;
		int periodoInicial = PlanoIndividualDocente.PERIODO_INICIO;
		
		int primeiroPeriodo = 1;
		int segundoPeriodo = 2;
		
		datasCombo.add(new SelectItem(anoAtual+"."+periodoAtual));		
		if ( periodoAtual == 2 ) 
			datasCombo.add(new SelectItem(anoAtual+"."+primeiroPeriodo));
		
		anoAtual--;

		for ( int ano = anoAtual ; ano > anoInicial ; ano-- ){
			datasCombo.add(new SelectItem(ano+"."+segundoPeriodo));
			datasCombo.add(new SelectItem(ano+"."+primeiroPeriodo));
		}
		
		datasCombo.add(new SelectItem(anoInicial+"."+segundoPeriodo));		
		if ( periodoInicial != 2 )
			datasCombo.add(new SelectItem(anoInicial+"."+primeiroPeriodo));

	}
	
	/**
	 * Monta o combo com as situações dos PIDs.
	 * 
	 * Não invocado por JSPs
	 * 
	 * @return
	 */
	private void montarSituacaoCombo () {	
		situacaoCombo = new ArrayList<SelectItem>();
		situacaoCombo.add(new SelectItem(TODOS_PIDS,"-- TODOS OS PIDS --"));
		situacaoCombo.add(new SelectItem(PlanoIndividualDocente.HOMOLOGADO," HOMOLOGADO "));
		situacaoCombo.add(new SelectItem(PlanoIndividualDocente.ENVIADO_HOMOLOGACAO," PENDENTE HOMOLOGAÇÃO "));
		situacaoCombo.add(new SelectItem(PlanoIndividualDocente.CADASTRADO," CADASTRANDO "));
		situacaoCombo.add(new SelectItem(PIDS_NAO_CADASTRADOS," NÃO CADASTRADO "));
	}
	
	/**
	 * Popula os PIDs que ainda não foram cadastrados, para mostrar ao chefe do departamento, 
	 * os docentes que ainda não cadatraram PIDs. 
	 * 
	 * Não invocado por JSPs
	 * 
	 * @return
	 */
	private void popularPIDSNaoCadastrados () {		
		
		if ( situacaoFiltroChefia == TODOS_PIDS || situacaoFiltroChefia == PIDS_NAO_CADASTRADOS ){
			
			if ( !todosDocentes ){

				if ( ValidatorUtil.isEmpty(allPIDDocente) ){
					PlanoIndividualDocente pidNaoCadastrado = new PlanoIndividualDocente();
					pidNaoCadastrado.setAno(anoFiltro);
					pidNaoCadastrado.setPeriodo(periodoFiltro);
					pidNaoCadastrado.setServidor(servidorFiltroChefia);
					pidsCadastrados.add(pidNaoCadastrado);
				}	
			}	
			if ( allPIDDocente != null){
								
				if ( todosDocentes && docentesUnidades != null) {
					for ( Servidor docente : docentesUnidades ){
						
						boolean cadastrar = true;								
						
						for ( PlanoIndividualDocente pid : allPIDDocente ) 					
							if ( pid.getServidor().getId() == docente.getId() ) {
								cadastrar = false;
								break;
							}	
						
						if ( cadastrar ) {
							PlanoIndividualDocente pidNaoCadastrado = new PlanoIndividualDocente();
							pidNaoCadastrado.setAno(anoFiltro);
							pidNaoCadastrado.setPeriodo(periodoFiltro);
							pidNaoCadastrado.setServidor(docente);
							pidsCadastrados.add(pidNaoCadastrado);
						}	
					}
				} 
			}	
		}
	}
	
	/**
	 * Popula os PIDs cadastrados, para mostrar ao chefe do departamento, 
	 * 
	 * Não invocado por JSPs
	 * 
	 * @return
	 */
	private void popularPIDSCadastrados() {
		pidsHomologados = new ArrayList<PlanoIndividualDocente>();
		pidsCadastrados = new ArrayList<PlanoIndividualDocente>();
			
		if ( situacaoFiltroChefia != PIDS_NAO_CADASTRADOS )
			if ( allPIDDocente != null && !allPIDDocente.isEmpty() ) {
				for ( PlanoIndividualDocente pid : allPIDDocente ){
					if ( pid.isHomologado() || pid.isEnviadoHomologacao() )
						pidsHomologados.add(pid);
					else 
						pidsCadastrados.add(pid);
				}
			}
	}
	
	/**
	 * Inicializa os dados da filtragem.
	 * 
	 * Nao Invocado por JSPs
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private void inicializarFiltragem () {
		
		if ( servidorFiltroChefia == null )
			servidorFiltroChefia = new Servidor();

		if ( todosDocentes == null )
			todosDocentes = true;
		
		if ( situacaoFiltroChefia == null )
			situacaoFiltroChefia = TODOS_PIDS;
		
		int mes = br.ufrn.arq.util.CalendarUtils.getMesAtual();
		int anoAtual = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
		int periodoAtual = (( mes < 6 ) ? 1 :  2);
		
		if (dataFiltroChefia == null) 
			dataFiltroChefia = anoAtual +"."+ periodoAtual;
			
		if (dataFiltroChefia != null) {
			String data = dataFiltroChefia.replace(".","-");
			String [] anoPeriodo = data.split("-");
			if (anoPeriodo.length > 1){
				anoFiltro = Integer.valueOf(anoPeriodo[0]);
				periodoFiltro = Integer.valueOf(anoPeriodo[1]);
			} else {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano-Período");
			} 					
		}
	}	
	
	// GETS/SETS
	
	public boolean isPermiteAlterarPIDHomologado() {
		return permiteAlterarPIDHomologado ;
	}
	
	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public List<Designacao> getListaChAdmin() {
		return listaChAdmin;
	}

	public void setListaChAdmin(List<Designacao> listaChAdmin) {
		this.listaChAdmin = listaChAdmin;
	}

	public List<PlanoIndividualDocente> getAllPIDDocente() {
		return allPIDDocente;
	}

	public void setAllPIDDocente(List<PlanoIndividualDocente> allPIDDocente) {
		this.allPIDDocente = allPIDDocente;
	}

	public List<CargaHorariaProjeto> getListaCargaHorariaProjetoExtensao() {
		return listaCargaHorariaProjetoExtensao;
	}

	public void setListaCargaHorariaProjetoExtensao(
			List<CargaHorariaProjeto> listaCargaHorariaProjetoExtensao) {
		this.listaCargaHorariaProjetoExtensao = listaCargaHorariaProjetoExtensao;
	}

	public List<CargaHorariaProjeto> getListaCargaHorariaProjetoPesquisa() {
		return listaCargaHorariaProjetoPesquisa;
	}

	public void setListaCargaHorariaProjetoPesquisa(
			List<CargaHorariaProjeto> listaCargaHorariaProjetoPesquisa) {
		this.listaCargaHorariaProjetoPesquisa = listaCargaHorariaProjetoPesquisa;
	}

	public double getChRestanteADistribuir() {
		return chRestanteADistribuir;
	}

	public void setChRestanteADistribuir(double chRestanteADistribuir) {
		this.chRestanteADistribuir = chRestanteADistribuir;
	}

	public List<CargaHorariaOrientacao> getListaCHOrientacaoPosGraduacao() {
		return listaCHOrientacaoPosGraduacao;
	}

	public ArrayList<AtividadeComplementarPID> getListaGrupoEnsino() {
		return listaGrupoEnsino;
	}

	public void setListaGrupoEnsino(
			ArrayList<AtividadeComplementarPID> listaGrupoEnsino) {
		this.listaGrupoEnsino = listaGrupoEnsino;
	}

	public ArrayList<AtividadeComplementarPID> getListaGrupoAtivAdmin() {
		return listaGrupoAtivAdmin;
	}

	public void setListaGrupoAtivAdmin(
			ArrayList<AtividadeComplementarPID> listaGrupoAtivAdmin) {
		this.listaGrupoAtivAdmin = listaGrupoAtivAdmin;
	}

	public ArrayList<AtividadeComplementarPID> getListaGrupoAtivExtensao() {
		return listaGrupoAtivExtensao;
	}

	public void setListaGrupoAtivExtensao(
			ArrayList<AtividadeComplementarPID> listaGrupoAtivExtensao) {
		this.listaGrupoAtivExtensao = listaGrupoAtivExtensao;
	}

	public ArrayList<AtividadeComplementarPID> getListaGrupoAtivPesqProducaTec() {
		return listaGrupoAtivPesqProducaTec;
	}

	public void setListaGrupoAtivPesqProducaTec(
			ArrayList<AtividadeComplementarPID> listaGrupoAtivPesqProducaTec) {
		this.listaGrupoAtivPesqProducaTec = listaGrupoAtivPesqProducaTec;
	}

	public List<AtividadesEspecificasDocente> getAtivEspecificasAdicionasDocente() {
		return ativEspecificasAdicionasDocente;
	}

	public List<AtividadesEspecificasDocente> getAtivEspecificasAdicionasDocenteRemover() {
		return ativEspecificasAdicionasDocenteRemover;
	}

	public ArrayList<DocenteTurma> getListaDocenteTurma() {
		return listaDocenteTurma;
	}

	public List<CargaHorariaOrientacao> getListaCHOrientacaoGraduacao() {
		return listaCHOrientacaoGraduacao;
	}
	
	public List<CargaHorariaOrientacao> getListaCHOrientacaoGraduacaoSemAtividades(){
		return listaCHOrientacaoGraduacaoSemAtividades;
	}

	public void setListaCHOrientacaoGraduacao(
			List<CargaHorariaOrientacao> listaCHOrientacaoGraduacao) {
		this.listaCHOrientacaoGraduacao = listaCHOrientacaoGraduacao;
	}

	public void setListaDocenteTurma(ArrayList<DocenteTurma> listaDocenteTurma) {
		this.listaDocenteTurma = listaDocenteTurma;
	}
	
	/**
	 * Retorna a carga horária total dedicada ao ensino presencial
	 * <br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
 	 * 		<li>/sigaa.war/pid/_painel_ensino.jsp</li>
 	 * </ul>
	 *  
	 */
	public double getChTotalEnsinoPresencial() throws DAOException {
		double res = 0;
		
		ParametrosGestoraAcademica param = getParametrosAcademicos();
		
		if ( !ValidatorUtil.isEmpty(listaDocenteTurma) ){
			for ( DocenteTurma d : listaDocenteTurma ){
				PIDUtils.calcularChDedicadaSemana(d, param.getHorasCreditosAula(), param.getHorasCreditosEstagio());
				if ( d != null )
					if ( d.getTurma() != null ){
						// Turmas de Residência Médica possuem apenas CH Semanal que é setada na CH dedicada ao período do docente
						if (!d.isChResidenciaSemTurma()){
							res += d.getChDedicadaSemana();
						} else { 
							// Turmas de Residência Médica possuem apenas CH Semanal que é setada na CH dedicada ao período do docente
							res += d.getChDedicadaPeriodo();
						}
					}	
			}
		}
		
		return res;
	}
	
	/**
	 * Retorna a carga horária total dedicada ao ensino à distância
	 * <br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
 	 * 		<li>/sigaa.war/pid/_painel_ensino.jsp</li>
 	 * </ul>
	 *  
	 */
	public double getChTotalEnsinoDistancia() {
		double res = 0;
		
		if ( obj != null && !ValidatorUtil.isEmpty(obj.getChEnsinoEad()) ){
			for ( CargaHorariaEad chEad : obj.getChEnsinoEad() ){
				res += chEad.getChDedicada();
			}
		}
		
		return res;
	}
	
	/**
	 * Retorna a carga horária total dedicada a orientações de atividades
	 * <br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
 	 * 		<li>/sigaa.war/pid/_painel_ensino.jsp</li>
 	 * </ul>
	 *  
	 */
	public double getChTotalOrientacoes() {
		double res = 0;

		if ( !ValidatorUtil.isEmpty(listaCHOrientacaoGraduacao) ){
			for ( CargaHorariaOrientacao chO : listaCHOrientacaoGraduacao ){
				res += chO.getChDedicadaSemanal();
			}
		}
		
		return res;
	}

	public void setPidsHomologados(List<PlanoIndividualDocente> pidsHomologados) {
		this.pidsHomologados = pidsHomologados;
	}

	public List<PlanoIndividualDocente> getPidsHomologados() {
		return pidsHomologados;
	}

	public List<CargaHorariaProjeto> getListaCargaHorariaProjetoEnsino() {
		return listaCargaHorariaProjetoEnsino;
	}

	public void setListaCargaHorariaProjetoEnsino(
			List<CargaHorariaProjeto> listaCargaHorariaProjetoEnsino) {
		this.listaCargaHorariaProjetoEnsino = listaCargaHorariaProjetoEnsino;
	}
	
	public void setPidsCadastrados(List<PlanoIndividualDocente> pidsCadastrados) {
		this.pidsCadastrados = pidsCadastrados;
	}

	public List<PlanoIndividualDocente> getPidsCadastrados() {
		return pidsCadastrados;
	}

	public void setDatasCombo(List <SelectItem> datasCombo) {
		this.datasCombo = datasCombo;
	}

	public List <SelectItem> getDatasCombo() {
		return datasCombo;
	}

	public void setServidorFiltroChefia(Servidor servidorFiltroChefia) {
		this.servidorFiltroChefia = servidorFiltroChefia;
	}

	public Servidor getServidorFiltroChefia() {
		return servidorFiltroChefia;
	}

	public void setDataFiltroChefia(String dataFiltroChefia) {
		this.dataFiltroChefia = dataFiltroChefia;
	}

	public String getDataFiltroChefia() {
		return dataFiltroChefia;
	}

	public void setDocentesUnidades(List <Servidor> docentesUnidades) {
		this.docentesUnidades = docentesUnidades;
	}

	public List <Servidor> getDocentesUnidades() {
		return docentesUnidades;
	}

	public Boolean getTodosDocentes() {
		return todosDocentes;
	}

	public void setTodosDocentes(Boolean todosDocentes) {
		this.todosDocentes = todosDocentes;
	}

	public void setSituacaoCombo(List <SelectItem> situacaoCombo) {
		this.situacaoCombo = situacaoCombo;
	}

	public List <SelectItem> getSituacaoCombo() {
		return situacaoCombo;
	}

	public void setSituacaoFiltroChefia(Integer situacaoFiltroChefia) {
		this.situacaoFiltroChefia = situacaoFiltroChefia;
	}

	public Integer getSituacaoFiltroChefia() {
		return situacaoFiltroChefia;
	}

	public void setPeriodoFiltro(Integer periodoFiltro) {
		this.periodoFiltro = periodoFiltro;
	}

	public Integer getPeriodoFiltro() {
		return periodoFiltro;
	}

	public void setAnoFiltro(Integer anoFiltro) {
		this.anoFiltro = anoFiltro;
	}

	public Integer getAnoFiltro() {
		return anoFiltro;
	}
}