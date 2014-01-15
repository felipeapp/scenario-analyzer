/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 16/11/2006
*
*/
package br.ufrn.sigaa.monitoria.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.monitoria.AtividadeMonitorDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.monitoria.dominio.AtividadeMonitor;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EnvioFrequencia;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.negocio.RelatorioMonitoriaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * Managed bean para cadastrar as atividades de um monitor.
 * 
 * Prograd(Pró-Reitoria de Graduação)
 * 
 * @author David Ricardo
 * @author ilueny santos
 * @author Victor Hugo
 * 
 */
@Component("atividadeMonitor") 
@Scope("session")
public class AtividadeMonitorMBean extends SigaaAbstractController<AtividadeMonitor> {

	/** Atributo utilizado para o nome do botão de confirmação do sistema */
    private static final String CONFIRM_ORIENTADOR = "Confirmar Avaliação";

    // +1 porque pega de GregorianCalendar
    /** Atributo utilizado para representar o ano da Atividade */
    private int anoAtividade = CalendarUtils.getAnoAtual();
    /** Atributo utilizado para informar o ano do projeto */
    private int anoProjeto = CalendarUtils.getAnoAtual();
    /** Atributo utilizado para informar o mês */
    private int mes = CalendarUtils.getMesAtual() + 1;
    /** Atributo utilizado para representar o iD do centro */
    private int idCentro = 0;

    /** usado no gerenciamento de atividades de um monitor */ 
    private Collection<AtividadeMonitor> atividades = new HashSet<AtividadeMonitor>();
    /** Atributo utilizado para representar o centro */
    private Unidade centro = new Unidade();
    /** Atributo utilizado para representar o discente */
    private Discente discente = new Discente();
    /** Atributo utilizado para representar o Projeto de Ensino */
    private ProjetoEnsino projetoEnsino = new ProjetoEnsino();
    /** Atributo utilizado para representar a situação do monitor */
    private SituacaoDiscenteMonitoria situacaoMonitor = new SituacaoDiscenteMonitoria();
    /** Atributo utilizado para representar o tipo do vínculo do discente */
    private TipoVinculoDiscenteMonitoria tipoVinculo = new TipoVinculoDiscenteMonitoria();
    /** Atributo utilizado para representar a frequencia */
    private Integer frequencia;
    /** Atributo utilizado para representar o título do projeto */
    private String tituloProjeto = null;
    /** Atributo utilizado para representar a Data de início da validação da frequencia */
    private Date inicioValidacaoFrequencia = null;
    /** Atributo utilizado para representar a Data de fim da validação da frequencia */
    private Date fimValidacaoFrequencia = null; 
    /** Atributo utilizado para representar a Data de iníco do cadastro */
    private Date inicioDataCadastro = null;
    /** Atributo utilizado para representar a Data de fim do cadastro */
    private Date fimDataCadastro = null;
    /** Atributo utilizado para representar a coleção de docentes */
    private Collection<EquipeDocente> docentes;
    /** Atributo utilizado para representar a coleção das atividades do Discente Monitor */
    private Collection<AtividadeMonitor> atividadesDiscente = new HashSet<AtividadeMonitor>();
    /** Atributo utilizado para representar a Atividade do Monitor */
    private AtividadeMonitor atividade = new AtividadeMonitor();

    /** filtros utilizados na busca  */ 
    private boolean checkBuscaValidacaoFrequencia;
    /** Atributo utilizado para representar o filtro de Frequencia na página de busca */
    private boolean checkBuscaFrequencia;
    /** Atributo utilizado para representar o filtro de Ano Atividade na página de busca */
    private boolean checkBuscaAnoAtividade;
    /** Atributo utilizado para representar o filtro de Mes na página de busca */
    private boolean checkBuscaMes;
    /** Atributo utilizado para representar o filtro de Discente na página de busca */
    private boolean checkBuscaDiscente;
    /** Atributo utilizado para representar o filtro de Projeto na página de busca */
    private boolean checkBuscaProjeto;
    /** Atributo utilizado para representar o filtro de Situação na página de busca */
    private boolean checkBuscaSituacao;
    /** Atributo utilizado para representar o filtro de Tipo de Vínculo na página de busca */
    private boolean checkBuscaTipoVinculo;
    /** Atributo utilizado para representar o filtro de Ano do Projeto */
    private boolean checkBuscaAnoProjeto;
    /** Atributo utilizado para representar o filtro do Centro */
    private boolean checkBuscaIdCentro;
    /** Atributo utilizado para representar o filtro da Data de cadastro */
    private boolean checkBuscaDataCadastro;

    

    /**
     * Construtor padrão.
     * 
     */
    public AtividadeMonitorMBean() {

	try {
	    EnvioFrequencia envioFrequenciaAtivo =  getGenericDAO().
	    findByExactField(EnvioFrequencia.class, "ativo", Boolean.TRUE).iterator().next();

	    obj = new AtividadeMonitor();
	    obj.setAno(envioFrequenciaAtivo.getAno());
	    obj.setMes(envioFrequenciaAtivo.getMes());	    
	} catch (DAOException e) {
	    notifyError(e);
	}
    }

    
    /**
     * Lista todos os projetos do discente.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>sigaa.war/portais/discente/menu_discente.jsp</li>
     * </ul>
     * 
     * @return Formulário de cadastro do relatório de atividades.
     */
    public String listarMeusProjetos(){
    	resetBean();
    	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_MEUS_PROJETOS); 
    }
    
    /**
     * Método que inicia o caso de uso de cadastrar atividades do monitor. Nele
     * é verificado a permissão do usuário e carregado as informações
     * necessárias ao cadastro.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>sigaa.war/portais/discente/menu_discente.jsp</li>
     * </ul>
     * 
     * @return Formulário de cadastro do relatório de atividades.
     */
    public String iniciarCadastroAtividade() {

	try {
	    
	    	if (getDiscenteUsuario() == null) {
			throw new SegurancaException();
	    	}

	    	int idDiscenteMonitoria = getParameterInt("idDiscente", 0);
	    	if (idDiscenteMonitoria > 0) {
		
	    	    DiscenteMonitoriaDao daoDM =  getDAO(DiscenteMonitoriaDao.class);
	    	    DiscenteMonitoria discenteMonitoriaUsuarioLogado = daoDM.findByPrimaryKey(idDiscenteMonitoria, DiscenteMonitoria.class);
	    	    
	    	    if (discenteMonitoriaUsuarioLogado == null) {
	    		addMensagemErro("O Usuário atual não faz parte de Projetos de Monitoria ativos.");
	    		return null;
	    	    }
	    	    
	    	    discente = discenteMonitoriaUsuarioLogado.getDiscente().getDiscente();

	    	    ListaMensagens mensagens = new ListaMensagens();
	    	    RelatorioMonitoriaValidator.validaEnvioRelatorioAtividadeMonitor(discenteMonitoriaUsuarioLogado, mensagens);
	    	    
	    	    if (!mensagens.isEmpty()) {
	    		addMensagens(mensagens);
	    		obj = new AtividadeMonitor();
	    		return listarAtividades();
	    	    } else {
	    		EnvioFrequencia regraEnvioFrequencia =  getGenericDAO().findByExactField(EnvioFrequencia.class, "ativo", Boolean.TRUE).iterator().next();
	    		
	    		obj = new AtividadeMonitor();
	    		obj.setDiscenteMonitoria(discenteMonitoriaUsuarioLogado);
	    		obj.setAno(regraEnvioFrequencia.getAno());
	    		obj.setMes(regraEnvioFrequencia.getMes());
	    		obj.setAtivo(true);
	    		setConfirmButton("Cadastrar");
	    		prepareMovimento(SigaaListaComando.CADASTRAR_ATIVIDADE_MONITOR);
	    		setReadOnly(false);
	    		return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_FORM);
	    	    }
	    	}
	    
	} catch (SegurancaException e) {
	    addMensagemErro(e.getMessage());
	} catch (ArqException e) {
	    notifyError(e);
	}
	
	return null;
    }
    
    
    
    /**
     * Método que inicia o caso de uso de cadastro de atividades do monitor (frequência) por um
     * Gestor de monitoria. Nele é verificado a permissão do usuário e carregado as informações
     * necessárias ao cadastro. 
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li>sigaa.war/monitoria/AtividadeMonitor/lista.jsp</li></ul>
     * 
     * @return Formulário de cadastro do relatório de atividades.
     * @throws ArqException 
     * @throws SegurancaException 
     * @throws ArqException possível erro na busca de discente e validações
     */
    public String iniciarCadastroAtividadeGestor() throws ArqException {
    	checkRole(SigaaPapeis.GESTOR_MONITORIA);
	    	int id = getParameterInt("id", 0);
	    	if (id > 0) {		
	    	    DiscenteMonitoria dm = getDAO(DiscenteMonitoriaDao.class).findByPrimaryKey(id, DiscenteMonitoria.class);
	    	    discente = dm.getDiscente().getDiscente();
	    	    obj = new AtividadeMonitor();
	    	    obj.setDiscenteMonitoria(dm);
	    	    obj.setMes(CalendarUtils.getMesAtual());
	    	    obj.setAno(CalendarUtils.getAnoAtual());
	    	    obj.setRegistroEntradaPrograd(getUsuarioLogado().getRegistroEntrada());
	    	    setConfirmButton("Cadastrar");
	    	    prepareMovimento(SigaaListaComando.CADASTRAR_ATIVIDADE_MONITOR);
	    	    setReadOnly(false);
	    	    return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_FORM);
	    	}else {
	    		return null;
	    	}
    }

    
    /**
     * Reseta o MBean e inicializa o campo 'mes' para que o combobox apareça com '-- SELECIONE --' na view.
     *<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li>sigaa.war/monitoria/index.jsp</ul></li>
     * 
     * @return
     */
    public String relatorioAtividades() {
    	resetBean();
    	mes = 0;
    	return forward("/monitoria/ValidacaoProgradAtividadeMonitor/lista.jsp");
    }


    /**
     * Retorna todos os relatórios de atividades do discente logado.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li>sigaa.war/portais/discente/menu_discente.jsp</li></ul>
     * <ul><li>sigaa.war/monitoria/AlterarDiscenteMonitoria/lista.jsp</li></ul>
     * 
     * @return Retorna a lista relatórios de atividades do discente logado.
     * @throws DAOException Busca dos relatórios de atividades do discente.
     * @throws SegurancaException 
     * 
     */
    public String listarAtividades() throws DAOException {
    	AtividadeMonitorDao dao =  getDAO(AtividadeMonitorDao.class);    	
    	if (getAcessoMenu().isMonitoria()) {
    	    int id = getParameterInt("idDiscenteMonitoria", 0);
    	    DiscenteMonitoria dm = getDAO(DiscenteMonitoriaDao.class).findByPrimaryKey(id, DiscenteMonitoria.class);
    	    discente = dm.getDiscente().getDiscente();
    	}else {    	
    	    discente = getDiscenteUsuario().getDiscente();
    	}
    	
    	if (ValidatorUtil.isEmpty(discente)) {
    	    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
    	    return null;
    	}
    	atividadesDiscente = dao.findByDiscente(discente);
    	
    	return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_LISTA);
    }

    /**
     * Realiza busca por atividades de monitores de acordo com os parâmetros
     * passados usado na tela de busca...
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li>sigaa.war/monitoria/ValidacaoProgradAtividadeMonitor/lista.jsp</li></ul>
     * 
     * @return Retorna mesma página com resultado da busca.
     * @throws DAOException Busca dos relatórios de atividade do discente.
     */
    public String buscar() throws DAOException {

    	ListaMensagens listaErros = new ListaMensagens();    	
    	
    	if (atividades != null) {
    		atividades.clear();
    	}

    	Integer anoAtividadeTemp = null;
    	Integer mesTemp = null;
    	Integer idDiscenteTemp = null;
    	String  tituloTemp = null;
    	Integer idSituacaoTemp = null;
    	Integer idTipoVinculoTemp = null;
    	Integer anoProjetoTemp = null;
    	Integer idCentroTemp = null;
    	Integer frequencia = null;
    	Date dataInicioValidacaoFreq = null;
    	Date dataFimValidacaoFreq = null;
    	Date dataInicioCadastro = null;
    	Date dataFimCadastro = null;

    	if(checkBuscaFrequencia) { 
    		frequencia = this.frequencia;
    		if(frequencia == -1) {
    			listaErros.addErro("Selecione 'SIM' ou 'NÃO' para o campo 'Frequências Validadas'.");
    		}
    	}
	
    	if(checkBuscaValidacaoFrequencia) {
    		dataInicioValidacaoFreq = this.inicioValidacaoFrequencia;
    		dataFimValidacaoFreq = this.fimValidacaoFrequencia;
    		ValidatorUtil.validateRequired(dataInicioValidacaoFreq, "Data Início de Validação da Frequência", listaErros);
    		ValidatorUtil.validateRequired(dataFimValidacaoFreq, "Data Fim de Validação da Frequência", listaErros);
    	}
    	if(checkBuscaDataCadastro) {
    		dataInicioCadastro = this.inicioDataCadastro;
    		dataFimCadastro = this.fimDataCadastro;
    		ValidatorUtil.validateRequired(dataInicioCadastro, "Data Início de Cadastro", listaErros);
    		ValidatorUtil.validateRequired(dataFimCadastro, "Data Fim de Cadastro", listaErros);
    	}
	
    	if (checkBuscaAnoAtividade) {
    		anoAtividadeTemp = this.anoAtividade;
    		ValidatorUtil.validateRequired(anoAtividadeTemp, "Ano da atividade", listaErros);
    	}
    	if (checkBuscaMes) {
    		mesTemp = this.mes;
    		if(mesTemp == 0) {
    			listaErros.addErro("Selecione o mês.");
    		}
    	}
    	if (checkBuscaDiscente) {
    		idDiscenteTemp = discente.getId();
    		ValidatorUtil.validateRequiredId(idDiscenteTemp, "Discente", listaErros);
    	}
    	if (checkBuscaProjeto) {
    		tituloTemp = getTituloProjeto();
    		ValidatorUtil.validateRequired(tituloTemp, "Projeto", listaErros);
    	}
    	if (checkBuscaSituacao) {
    		idSituacaoTemp = situacaoMonitor.getId();
    		if(idSituacaoTemp == 0) {
    			listaErros.addErro("Selecione a situação do monitor.");
    		}
    	}
    	if (checkBuscaTipoVinculo) {
    		idTipoVinculoTemp = tipoVinculo.getId();
    		if(idTipoVinculoTemp == 0)
    			listaErros.addErro("Selecione um tipo de vínculo.");
    	}
    	if (checkBuscaAnoProjeto) {
    		anoProjetoTemp = this.anoProjeto;
    		ValidatorUtil.validateRequired(anoProjetoTemp, "Ano do Projeto", listaErros);
    	}
    	if (checkBuscaIdCentro) {
    		idCentroTemp = centro.getId();
    		if(idCentroTemp == 0) {
    			listaErros.addErro("Selecione o centro do projeto.");
    		}
    	}

    	/**
    	 * Se não tiver critério força a consulta pelo ano atual
    	 */
    	if (!checkBuscaDiscente && !checkBuscaProjeto && !checkBuscaMes && !checkBuscaSituacao
    			&& !checkBuscaAnoAtividade && !checkBuscaAnoProjeto && !checkBuscaTipoVinculo
    			&& !checkBuscaIdCentro && !checkBuscaFrequencia && !checkBuscaValidacaoFrequencia && !checkBuscaDataCadastro)  {

    		listaErros.addWarning(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
    	}
    	
    	if(!listaErros.isEmpty()) {
    		addMensagens(listaErros);
    		return null;
    	}

    	AtividadeMonitorDao dao = null;
    	dao =  getDAO(AtividadeMonitorDao.class);
    	atividades = dao.filter(anoAtividadeTemp, mesTemp, idDiscenteTemp, tituloTemp,
    			idTipoVinculoTemp, idSituacaoTemp, anoProjetoTemp, idCentroTemp, frequencia,
    			dataInicioValidacaoFreq, dataFimValidacaoFreq, dataInicioCadastro, dataFimCadastro);
    	
    	if(atividades.isEmpty()) {
    		addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
    	}

    	return null;

    }    


    /**
     * Carrega a atividade selecionada para a exibição.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/monitoria/AtividadeMonitor/lista_avaliar_atividade.jsp</li>
     * 	<li>sigaa.war/monitoria/AtividadeMonitor/lista.jsp</li>
     * 	<li>sigaa.war/monitoria/CancelarBolsas/lista_atividades.jsp</li>
     * 	<li>sigaa.war/monitoria/ConsultarMonitor/view.jsp</li>
     * 	<li>sigaa.war/monitoria/ValidacaoProgradAtividadeMonitor/lista.jsp</li>
     * </ul>
     * 
     * @return Página com relatório de atividades selecionado do discente
     * @throws DAOException Busca do relatório
     */
    public String visualizarRelatorioMonitor() throws DAOException {

	int idAtividade = getParameterInt("id", 0);
	if (idAtividade > 0) {
	    GenericDAO dao = getGenericDAO();
	    atividade = dao.findByPrimaryKey(idAtividade, AtividadeMonitor.class);
	    
	    getGenericDAO().initialize(centro);
	    getGenericDAO().initialize(situacaoMonitor);
	    return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_VIEW);
	} else {
	    return null;
	}
    }

    /**
     * Este método inicia o caso de uso onde o orientador valida as atividades
     * cadastradas pelos seus orientandos. Nele é verificado a permissão e
     * carregadas as informações necessárias para realização do caso de uso.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li>sigaa.war/portais/docente/menu_docente.jsp</li></ul>
     * 
     * @return Lista de todos os relatórios dos projetos do servidor atual
     * @throws DAOException Busca dos relatórios
     * @throws SegurancaException Somente docente podem realizar esta operação.
     */
    public String iniciarAvaliacaoAtividadeOrientador() throws DAOException, SegurancaException {

    	checkDocenteRole();
    	atividades.clear();

    	/* O usuário deve ser servidor para realizar esta operação */
    	if (getServidorUsuario() == null) {
    		throw new SegurancaException();	
    	}

    	DadosAcesso acesso = getAcessoMenu();
    	Boolean coordenador = false;

    	if (acesso.isCoordenadorMonitoria()) {
    		coordenador = true;
    	}

    	atividades = getDAO(AtividadeMonitorDao.class).avaliarRelatorioAtividadeMensal(getServidorUsuario(), coordenador);

    	return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_LISTAAVALIACAO);
	
    }

    /**
     * Este método leva para a tela onde o orientador irá realizar a avaliação
     * sobre o relatório de atividades selecionado que foi submetido por seus
     * orientandos.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li>sigaa.war/monitoria/AtividadeMonitor/lista_avaliar_atividade.jsp</li></ul>
     * 
     * @return Formulário de validação.
     * @throws ArqException Preparação do processador
     */
    public String avaliarRelatorioAtividade() throws ArqException {

    	checkDocenteRole();
    	prepareMovimento(SigaaListaComando.ORIENTADOR_VALIDAR_ATIVIDADE_MONITOR);
    	GenericDAO dao = getGenericDAO();
    	Integer id = getParameterInt("id");
    	this.obj = dao.findByPrimaryKey(id, AtividadeMonitor.class);
    	this.obj.getDiscenteMonitoria().getDiscente().getNome();

    	// propondo 100% para freqüências ainda não validadas.
    	if (obj.getDataValidacaoOrientador() == null) {
    		obj.setFrequencia(100);
    		obj.setValidadoOrientador(true);
    	}

    	// recupera do banco o a autorização para envio e validação da freqüência
    	AtividadeMonitorDao daoAtividade =  getDAO(AtividadeMonitorDao.class);
    	EnvioFrequencia envioFrequenciaAtivo =  daoAtividade.findByExactField(
    			EnvioFrequencia.class, "ativo", Boolean.TRUE).iterator().next();

    	ProjetoEnsino projeto = obj.getDiscenteMonitoria().getProjetoEnsino();
    	// verifica se o projeto está na faixa de anos liberados para envio.
    	if (projeto.isPermitidoCadastrarRelatorioAtividadesMonitores(envioFrequenciaAtivo)) {
    		// verifica se tá no período do recebimento de freqüência
    		if (obj.isAtividadePodeSerCadastradaValidada(envioFrequenciaAtivo)) {
    			setConfirmButton(CONFIRM_ORIENTADOR);
    			return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_FORMAVALIACAO);
    		} else {
    			addMensagemErro("Prazo para validação do relatório da atividades selecionado terminou.");
    			return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_LISTAAVALIACAO);
    		}
    	} else {
    		addMensagemErro("Cadastro de freqüência para projetos de " + anoProjeto + " ainda não foi liberado!");
    		return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_LISTAAVALIACAO);
    	}

    }

    /**
     * Orientador avalia a atividade do monitor e registra a freqüência.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/AtividadeMonitor/form_avaliar_atividade.jsp</li>
     * </ul>
     * 
     * @return
     * @throws SegurancaException
     * @throws ArqException
     * @throws NegocioException
     */
    public String orientadorValidarAtividade() {
	try {
	    checkDocenteRole();
	    
    		obj.setRegistroEntradaOrientador(getUsuarioLogado().getRegistroEntrada());
    		obj.setDataValidacaoOrientador(new Date());
    		//obj.setValidadoOrientador(true);    		
    		ListaMensagens lista = obj.validate();
    		
    		if (!lista.isEmpty()) {
    		    addMensagens(lista);
    		    return null;
    		} else {    		    
    		    if (!obj.isValidadoOrientador()) {
    		    	obj.setFrequencia(0);
    		    }
    		    MovimentoCadastro mov = new MovimentoCadastro();
    		    mov.setObjMovimentado(obj);
    		    mov.setCodMovimento(SigaaListaComando.ORIENTADOR_VALIDAR_ATIVIDADE_MONITOR);
    		    execute(mov, getCurrentRequest());
    		    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
    		    return iniciarAvaliacaoAtividadeOrientador();
    		}
    		
	} catch (NegocioException e) {
	    addMensagens(e.getListaMensagens());
	    return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_FORMAVALIACAO);
	} catch (SegurancaException e) {
	    addMensagemErro(e.getMessage());
	    return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_FORMAVALIACAO);
	} catch (Exception e) {
	    tratamentoErroPadrao(e);
	    return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_FORMAVALIACAO);
	}
    }

    /**
     * Discente cadastra a atividade do mês pra que o orientador possa validá-la
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/AtividadeMonitor/form.jsp</li>
     * </ul>
     * @return
     * @throws SegurancaException
     * @throws ArqException
     * @throws NegocioException
     */
    public String cadastrarAtividade() throws SegurancaException, ArqException, NegocioException {
    	try {
	    	ListaMensagens lista = new ListaMensagens();
	    	if (getConfirmButton().equalsIgnoreCase("remover")) {
	    		remover();
	    		return listarAtividades();
	    	} else {
	    		lista = obj.validate();
	    		if(!getConfirmButton().equalsIgnoreCase("alterar")){
	    			RelatorioMonitoriaValidator.validaFrequenciaMesmoMes(obj, lista);
	    		}
	    		
				if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MONITORIA)) {
					ValidatorUtil.validateRequired(obj.getObservacaoPrograd(), "Observações (Motivo do Cadastro)", erros);
				}
	    		
	    		if (!lista.isEmpty()) {
	    			addMensagens(lista);
	    			return null;
	    		}else {
	    			//Cadastro Realizado pelo Gestor de Monitoria.
	    			if (getAcessoMenu().isMonitoria()) {
	    				obj.setRegistroEntradaPrograd(getUsuarioLogado().getRegistroEntrada());
	    				obj.setValidadoPrograd(true);
	    				obj.setDataValidacaoPrograd(new Date());
	    			}        		
	    			MovimentoCadastro mov = new MovimentoCadastro();
	    			mov.setObjMovimentado(obj);
	    			mov.setCodMovimento(SigaaListaComando.CADASTRAR_ATIVIDADE_MONITOR);
	    			if(getConfirmButton().equalsIgnoreCase("alterar")){
	    				mov.setAcao(ArqListaComando.ALTERAR.getId());
	    			}else{
	    				mov.setAcao(ArqListaComando.CADASTRAR.getId());
	    			}
	    			lista = execute(mov, getCurrentRequest());
	    			if(lista != null){
	    				addMensagens(lista);
	    				return null;
	    			}
	    			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	    			return listarAtividades();
	    		}
	    	}
    	}catch (NegocioException e) {
    		addMensagemErro(e.getMessage());
    		return null;
		}
    }
    
    /**
     * Ação executada antes de remover a atividade do monitor.
     * 
     * Método Não chamado pela(s) seguinte(s) JSP(s): 
     * 
     */
    public void beforeRemover() throws DAOException {
    	if(getGenericDAO().findByPrimaryKey(this.obj.getId(), AtividadeMonitor.class) == null){
    		this.obj = null;
    	}
    }

    /**
     * Método chamado para remover a atividade de um monitor.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/AtividadeMonitor/form.jsp></li>
     * </ul>
     * @throws ArqException 
     * 
     */
    @Override
    public String remover() throws ArqException {
    	beforeRemover();

		PersistDB obj = this.obj;
		prepareMovimento(ArqListaComando.DESATIVAR);
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);

		if (obj == null || obj.getId() == 0) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return listarAtividades();
		} else {

			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return redirect(getListPage());
			} 

			setResultadosBusca(null);
			return  listarAtividades();
		}
    }
    
    
    /**
     * Método chamado para atualizar a atividade de um monitor.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/AtividadeMonitor/lista.jsp></li>
     * </ul>
     * @throws ArqException 
     * 
     */
    public String atualizar() {

	String result = null;

	try {

	    prepareMovimento(SigaaListaComando.CADASTRAR_ATIVIDADE_MONITOR);
	    if(getGenericDAO().findByPrimaryKey(getParameterInt("id", 0), AtividadeMonitor.class) == null){
	    	addMensagemErro("Atividade não selecionada.");
			return redirectMesmaPagina();
	    }
	    setId();
	    setReadOnly(false);

	    this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), AtividadeMonitor.class);

	    if (obj != null) {

		// recupera do banco o a autorização para envio e validação da freqüência
		AtividadeMonitorDao daoAtividade =  getDAO(AtividadeMonitorDao.class);
		EnvioFrequencia envioFrequenciaAtivo =  daoAtividade.findByExactField(EnvioFrequencia.class, "ativo",	Boolean.TRUE).iterator().next();

		ProjetoEnsino projeto = obj.getDiscenteMonitoria().getProjetoEnsino();
		//Verifica se o projeto está na faixa de anos liberados para envio da frequência.
		if ((getAcessoMenu().isMonitoria()) || projeto.isPermitidoCadastrarRelatorioAtividadesMonitores(envioFrequenciaAtivo)) {

		    //Verifica se está no período do recebimento de frequência
		    if ((getAcessoMenu().isMonitoria()) || obj.isAtividadePodeSerCadastradaValidada(envioFrequenciaAtivo)) {
			setConfirmButton("Alterar");
			result = forward(getFormPage());
		    } else {
			addMensagemErro("Alteração do Relatório de Atividades está fora do prazo permitido pela Pró-Reitoria de Graduação.");
			result = listarAtividades();
		    }

		} else {
		    addMensagemErro("Cadastro de freqüência para projetos de " + projeto.getAno() + " ainda não foi liberado pela Pró-Reitoria de Graduação.");
		    result = listarAtividades();
		}

	    } else {
		addMensagemErro("Atividade não selecionada.");
		return listarAtividades();
	    }

	} catch (ArqException e) {
	    addMensagemErro(e.getMessage());
	    notifyError(e);
	}

	return result;

    }

    /**
     * Carrega a atividade mensal do monitor e envio o usuário para tela de
     * invalidação da atividade pela prograd(Pró-Reitoria de Graduação)
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/ValidacaoProgradAtividadeMonitor/lista.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String iniciarDesvalidarAtividadeMonitor() {

	try {

	    checkRole(SigaaPapeis.GESTOR_MONITORIA);
	    prepareMovimento(SigaaListaComando.DESVALIDAR_ATIVIDADE_MONITOR);

	    setId();
	    PersistDB obj =  this.obj;

	    GenericDAO dao = getGenericDAO();
	    this.obj = (AtividadeMonitor) dao.findByPrimaryKey(obj.getId(), obj.getClass());
	    this.obj.getDiscenteMonitoria().getId();

	} catch (ArqException e) {
	    notifyError(e);
	    addMensagemErro(e.getMessage());
	}

	return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_FORM_VALIDACAO);

    }

    /**
     * Prograd(Pró-Reitoria de Graduação) invalida frequência validada pelo orientador. Geralmente
     * utilizada quando um orientador/coordenador valida a atividade com 0%
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/ValidacaoProgradAtividadeMonitor/lista.jsp</li>
     * </ul>
     * 
     * @return
     * @throws SegurancaException
     * @throws ArqException
     * @throws NegocioException
     */
    public String desvalidarAtividadeMonitor() throws SegurancaException, ArqException, NegocioException {
	checkRole(SigaaPapeis.GESTOR_MONITORIA);
	
	ListaMensagens lista = obj.validate();

	if (!lista.isEmpty()) {
	    addMensagens(lista);
	    return null;
	}else {

	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setObjMovimentado(obj);
	    prepareMovimento(SigaaListaComando.DESVALIDAR_ATIVIDADE_MONITOR);
	    mov.setCodMovimento(SigaaListaComando.DESVALIDAR_ATIVIDADE_MONITOR);

	    try {
		execute(mov);
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		addMensagemInformation("Informe ao orientador do discente ou ao coordenador do projeto de monitoria para revalidar a atividade.");

	    } catch (Exception e) {
	    	addMensagemErro(e.getMessage());
	    	notifyError(e);
	    	return null;
	    }
	    buscar();
	    return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_LISTA_VALIDACAO);
	}
    }

    public String getDirBase() {
	return "/monitoria/AtividadeMonitor";
    }
    
    @Override
    public String getListPage() {
    	return forward(ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_LISTA);
    }

    public Collection<AtividadeMonitor> getAtividades() {
	return atividades;
    }

    public void setAtividades(Collection<AtividadeMonitor> atividades) {
	this.atividades = atividades;
    }

    public int getAnoAtividade() {
	return anoAtividade;
    }

    public void setAnoAtividade(int anoAtividade) {
	this.anoAtividade = anoAtividade;
    }

    public int getMes() {
	return mes;
    }

    public void setMes(int mes) {
	this.mes = mes;
    }

    public Discente getDiscente() {
	return discente;
    }

    public void setDiscente(Discente discente) {
	this.discente = discente;
    }

    public ProjetoEnsino getProjetoEnsino() {
	return projetoEnsino;
    }

    public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
	this.projetoEnsino = projetoEnsino;
    }

    public boolean isCheckBuscaDiscente() {
	return checkBuscaDiscente;
    }

    public void setCheckBuscaDiscente(boolean checkBuscaDiscente) {
	this.checkBuscaDiscente = checkBuscaDiscente;
    }

    public boolean isCheckBuscaMes() {
	return checkBuscaMes;
    }

    public void setCheckBuscaMes(boolean checkBuscaMes) {
	this.checkBuscaMes = checkBuscaMes;
    }

    public boolean isCheckBuscaProjeto() {
	return checkBuscaProjeto;
    }

    public void setCheckBuscaProjeto(boolean checkBuscaProjeto) {
	this.checkBuscaProjeto = checkBuscaProjeto;
    }

    public Collection<EquipeDocente> getDocentes() {
	return docentes;
    }

    public void setDocentes(Collection<EquipeDocente> docentes) {
	this.docentes = docentes;
    }

    /**
     * Retorna o nome do mês atual
     */
    public String getMesAtualString() {
	return getMes(obj.getMes() - 1);
    }

    @Override
    public int getAnoAtual() {
	return obj.getAno();
    }

    public boolean isCheckBuscaSituacao() {
	return checkBuscaSituacao;
    }

    public void setCheckBuscaSituacao(boolean checkBuscaSituacao) {
	this.checkBuscaSituacao = checkBuscaSituacao;
    }

    public boolean isCheckBuscaTipoVinculo() {
	return checkBuscaTipoVinculo;
    }

    public void setCheckBuscaTipoVinculo(boolean checkBuscaTipoVinculo) {
	this.checkBuscaTipoVinculo = checkBuscaTipoVinculo;
    }

    public SituacaoDiscenteMonitoria getSituacaoMonitor() {
	return situacaoMonitor;
    }

    public void setSituacaoMonitor(SituacaoDiscenteMonitoria situacaoMonitor) {
	this.situacaoMonitor = situacaoMonitor;
    }
    
    public TipoVinculoDiscenteMonitoria getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscenteMonitoria tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public int getAnoProjeto() {
	return anoProjeto;
    }

    public void setAnoProjeto(int anoProjeto) {
	this.anoProjeto = anoProjeto;
    }

    public int getIdCentro() {
	return idCentro;
    }

    public void setIdCentro(int idCentro) {
	this.idCentro = idCentro;
    }

    public boolean isCheckBuscaAnoAtividade() {
	return checkBuscaAnoAtividade;
    }

    public void setCheckBuscaAnoAtividade(boolean checkBuscaAnoAtividade) {
	this.checkBuscaAnoAtividade = checkBuscaAnoAtividade;
    }

    public boolean isCheckBuscaAnoProjeto() {
	return checkBuscaAnoProjeto;
    }

    public void setCheckBuscaAnoProjeto(boolean checkBuscaAnoProjeto) {
	this.checkBuscaAnoProjeto = checkBuscaAnoProjeto;
    }

    public boolean isCheckBuscaIdCentro() {
	return checkBuscaIdCentro;
    }

    public void setCheckBuscaIdCentro(boolean checkBuscaIdCentro) {
	this.checkBuscaIdCentro = checkBuscaIdCentro;
    }

    public Collection<AtividadeMonitor> getAtividadesDiscente() {
	return atividadesDiscente;
    }

    public void setAtividadesDiscente(
	    Collection<AtividadeMonitor> atividadesDiscente) {
	this.atividadesDiscente = atividadesDiscente;
    }

    public AtividadeMonitor getAtividade() {
	return atividade;
    }

    public void setAtividade(AtividadeMonitor atividade) {
	this.atividade = atividade;
    }
    public String getTituloProjeto() {
	return tituloProjeto;
    }

    public void setTituloProjeto(String tituloProjeto) {
	this.tituloProjeto = tituloProjeto;
    }

	public Integer getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	public boolean isCheckBuscaFrequencia() {
		return checkBuscaFrequencia;
	}

	public void setCheckBuscaFrequencia(boolean checkBuscaFrequencia) {
		this.checkBuscaFrequencia = checkBuscaFrequencia;
	}
    
	public Date getInicioValidacaoFrequencia() {
		return inicioValidacaoFrequencia;
	}

	public void setInicioValidacaoFrequencia(Date inicioValidacaoFrequencia) {
		this.inicioValidacaoFrequencia = inicioValidacaoFrequencia;
	}

	public Date getFimValidacaoFrequencia() {
		return fimValidacaoFrequencia;
	}

	public void setFimValidacaoFrequencia(Date fimValidacaoFrequencia) {
		this.fimValidacaoFrequencia = fimValidacaoFrequencia;
	}

	public boolean isCheckBuscaValidacaoFrequencia() {
		return checkBuscaValidacaoFrequencia;
	}

	public void setCheckBuscaValidacaoFrequencia(
			boolean checkBuscaValidacaoFrequencia) {
		this.checkBuscaValidacaoFrequencia = checkBuscaValidacaoFrequencia;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public Date getInicioDataCadastro() {
		return inicioDataCadastro;
	}

	public void setInicioDataCadastro(Date inicioDataCadastro) {
		this.inicioDataCadastro = inicioDataCadastro;
	}

	public Date getFimDataCadastro() {
		return fimDataCadastro;
	}

	public void setFimDataCadastro(Date fimDataCadastro) {
		this.fimDataCadastro = fimDataCadastro;
	}

	public boolean isCheckBuscaDataCadastro() {
		return checkBuscaDataCadastro;
	}

	public void setCheckBuscaDataCadastro(boolean checkBuscaDataCadastro) {
		this.checkBuscaDataCadastro = checkBuscaDataCadastro;
	}
	
}