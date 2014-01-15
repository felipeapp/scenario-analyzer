package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.DiscenteProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.InscricaoSelecaoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.PlanoTrabalhoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.negocio.CadastroExtensaoMov;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.CalendarioProjeto;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.InscricaoSelecaoProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;
import br.ufrn.sigaa.projetos.negocio.PlanoTrabalhoProjetoValidator;

/**
 * MBean responsável pelo controle de planos de trabalho de ações associadas.
 * @author geyson
 */
@Scope("session")
@Component("planoTrabalhoProjeto")
public class PlanoTrabalhoProjetoMBean extends SigaaAbstractController<PlanoTrabalhoProjeto> {
	
	/** Usado para armazenar informações de consulta ao banco. */
    private Collection<PlanoTrabalhoProjeto> planosCoordenadorLogado = new ArrayList<PlanoTrabalhoProjeto>();
    
    /** Usado para armazenar informações de consulta ao banco. */
    private Collection<Projeto> projetos = new ArrayList<Projeto>();
    
    /** Usado para armazenar id de projeto selecionado */
    private Integer idProjeto;
    
    /** Controle de plano de trabalho voluntário  */
    private boolean voluntario;
    
    /** tela de cronograma para plano de trabalho */
    private TelaCronograma telaCronograma = new TelaCronograma();

    /** Lista de discentes do projeto. */
	private Collection<DiscenteProjeto> discentesProjeto = new ArrayList<DiscenteProjeto>();
    
    /** Construtor padrão. */
	public PlanoTrabalhoProjetoMBean() {
	 this.obj = new PlanoTrabalhoProjeto();
	 setIdProjeto(new Integer(0));
	 setVoluntario(false);
	}
	
	/**
     * Verifica se usuário atual é coordenador de ação associadas.
     * <br>
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     *  <li>Não é chamado por JSPs.</li>
     * </ul>
     * 
     */
	@Override
    public void checkChangeRole() throws SegurancaException {
	if (!getAcessoMenu().isDocente()) {
	    throw new SegurancaException("Usuário não autorizado a realizar esta operação.");
	}
	super.checkChangeRole();
    }
	
	 /**
     * Verifica as permissões e lista todos os planos de trabalho do coordenador atual.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     * 	<li> /sigaa.war/portais/docente/menu_docente.jsp </li> 
     * </ul>
     * 
     * @return Lista todos os planos de trabalho onde o usuario é coordenador ativo de ações associadas em execução
     * @throws DAOException Gerado na busca das ações associadas possíveis de cadastro de plano.
     */
    public String planosCoordenador() throws SegurancaException, DAOException {
	checkChangeRole();
	PlanoTrabalhoProjetoDao dao = getDAO(PlanoTrabalhoProjetoDao.class);
	planosCoordenadorLogado = dao.findByServidorCoordenadorAtivo(getUsuarioLogado().getServidor(),
		new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO});

	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_PLANOS_COORDENADOR);
    }
    
	/**
	 * Carrega todos os planos de trabalho onde o usuário logado é o
	 * discente das respectivas ações.
	 * <br/>
	 * Método chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/projetos/PlanoTrabalho/planos_discente.jsp </li>
	 * </ul>
	 * 
	 * @throws DAOException Gerado pela busca dos planos
	 */
	public String planosDiscente() throws DAOException {
		discentesProjeto = getDAO(DiscenteProjetoDao.class).findByDiscenteComPlanoTrabalho(getDiscenteUsuario().getId(), null);	
		return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_PLANOS_DISCENTE);
	}
    
    /**
     * Inicia o caso de uso de cadastro de planos de trabalho.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     * 	<li> /sigaa.war/portais/docente/menu_docente.jsp </li>
     * </ul>
     * 
     * @return retorna página com todas os projetos coordenadas pelo usuário atual que podem receber cadastros de planos de trabalho
     * @throws DAOException Gerado na busca dos projetos possíveis de cadastro de plano.
     */
    public String iniciarCadastroPlano() throws SegurancaException, DAOException {
	checkChangeRole();
	ProjetoDao dao = getDAO(ProjetoDao.class);
	projetos = dao.findByServidor(getUsuarioLogado().getServidor(),
			new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO});
	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_LISTA);
    }
    
    /**
     * Inicia o caso de uso de cadastro de planos de trabalho.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     * 	<li> /sigaa.war/portais/docente/menu_docente.jsp </li>
     * </ul>
     * 
     * @return retorna página com todas os projetos coordenadas pelo usuário atual que podem receber cadastros de planos de trabalho
     * @throws DAOException Gerado na busca dos projetos possíveis de cadastro de plano.
     */
    public String iniciarCadastroPlanoVoluntario() throws SegurancaException, DAOException {
	checkChangeRole();
	ProjetoDao dao = getDAO(ProjetoDao.class);
	projetos = dao.findByServidor(getUsuarioLogado().getServidor(),
			new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO});
	setVoluntario(true);
	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_LISTA);
    }
    
    /**
     * Inicia o cadastro de novo plano de trabalho
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul><li> /sigaa.war/projetos/PlanoTrabalho/projetos_lista.jsp </li></ul> 
     * 
     * @return Página com formulário para cadastro de dados gerais do plano de trabalho.
     * @throws ArqException Somente coordenadores podem realizar esta ação.
     */
    public String novoPlanoTrabalho() throws ArqException {

    	checkChangeRole();

    	prepareMovimento(SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO);
    	setOperacaoAtiva(SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO.getId());
    	Projeto projeto = getGenericDAO().findByPrimaryKey(idProjeto, Projeto.class);
    	ListaMensagens mensagens = new ListaMensagens();
    	PlanoTrabalhoProjetoValidator.validaNovoPlano(projeto, mensagens);
    	
    	if(!isVoluntario())
    		PlanoTrabalhoProjetoValidator.validaLimiteBolsistas(projeto, false, mensagens);

    	if (!mensagens.isEmpty()) {
    		addMensagens(mensagens);
    		return null;
    	}

    	obj = new PlanoTrabalhoProjeto();
    	obj.setProjeto(projeto);
    	obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
    	obj.setDiscenteProjeto(novoDiscenteProjeto());
    	obj.setInscricaoSelecaoProjeto(new InscricaoSelecaoProjeto());
    	getInscricoesAcaoAssociadaSelecionada();

    	if(isVoluntario()){
    		obj.setTipoVinculo(new TipoVinculoDiscente(TipoVinculoDiscente.ACAO_ASSOCIADA_VOLUNTARIO));
    	}else{
    		obj.setTipoVinculo(new TipoVinculoDiscente());
    	}

    	setReadOnly(false);
    	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_BOLSISTA_FORM);

    }

    /**
     * Cria um novo discente de extensão devidamente configurado para ser
     * incluído no plano de trabalho.
     * <br>
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     * 	<li> Não invocado por JSP </li>
     * </ul>
     * @return Discente pronto para ser adicionado ao plano de trabalho;
     * @throws DAOException Carrega TipoSituacaoDiscente para legenda.
     */
	private DiscenteProjeto novoDiscenteProjeto() throws DAOException {
		DiscenteProjeto discenteProjeto = new DiscenteProjeto();
		discenteProjeto.setAtivo(true);
		discenteProjeto.setPlanoTrabalhoProjeto(null);
		discenteProjeto.setTipoVinculo(new TipoVinculoDiscente());
		discenteProjeto.setBanco(new Banco());
		discenteProjeto.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		discenteProjeto.setProjeto(obj.getProjeto());
		discenteProjeto.setSituacaoDiscenteProjeto(new TipoSituacaoDiscenteProjeto(TipoSituacaoDiscenteProjeto.SELECIONADO));
		return discenteProjeto;
	}
	
    
    /**
     * Submete a primeira página de cadastro dos planos de trabalho para validação
     * dos dados e prepara a tela do cronograma. (Próximo passo do cadastro).
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul><li> /sigaa.war/projetos/PlanoTrabalho/form.jsp </li></ul> 
     * 
     * @return Em caso de erro retorna para mesma tela, em caso de sucesso na validação vai para tela de cronograma.
     */
    public String submeterDadosGerais() throws ArqException {

    	checkChangeRole();
    	
    	if (obj.getDiscenteProjeto() == null) {
			obj.setDiscenteProjeto(novoDiscenteProjeto());
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
			return null;
		}
		
		if (ValidatorUtil.isEmpty(obj.getDiscenteProjeto().getDiscente())) {
			obj.getDiscenteProjeto().setDiscente(new Discente());
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
			return null;
		}
    	
    	int id = obj.getDiscenteProjeto().getDiscente().getId();
    	obj.getDiscenteProjeto().setDiscente(getGenericDAO().findByPrimaryKey(id, Discente.class));
    	
    	// Verificando se discente selecionado faz parte da lista de interessados.
    	obj.getProjeto().setInscricoesSelecao(getGenericDAO().findByExactField(InscricaoSelecaoProjeto.class, "projeto.id", obj.getProjeto().getId()));    	
		for (InscricaoSelecaoProjeto insc : obj.getProjeto().getInscricoesSelecao()) {
			if (insc.isAtivo() && insc.getDiscente().getId() == obj.getDiscenteProjeto().getDiscente().getId()) {
				obj.setInscricaoSelecaoProjeto(insc); 
				break;
			}
		}
		
		getGenericDAO().initialize(obj.getDiscenteProjeto().getTipoVinculo());
		obj.getDiscenteProjeto().setDataFim(obj.getDataFim());
		
		ListaMensagens mensagens = new ListaMensagens();
		//Valida
    	PlanoTrabalhoProjetoValidator.validaDadosGerais(obj, mensagens, getCalendarioVigente());
    	
    	if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			if(!obj.isAlterando())
				obj.getDiscenteProjeto().setDiscente(new Discente());
			return null;
		}

    	if (!obj.isAlterando()) {

    		obj.setTipoVinculo(obj.getDiscenteProjeto().getTipoVinculo());
    		obj.setAtivo(true);
    		getGenericDAO().initialize(obj.getDiscenteProjeto().getTipoVinculo());
    		getGenericDAO().initialize(obj.getDiscenteProjeto().getSituacaoDiscenteProjeto());
    		
    		Date dataInicio = obj.getDiscenteProjeto().getDataInicio();
        	CalendarioProjeto calAtual = getGenericDAO().findByExactField(CalendarioProjeto.class, "anoReferencia", obj.getProjeto().getAno(), true);
        	PlanoTrabalhoProjetoValidator.estaDentroPeriodoBolsa(dataInicio, calAtual, mensagens);

    		if (!mensagens.isEmpty()) {
    			addMensagens(mensagens);
    			return null;
    		}
    	}
    	// Inicializar tela do cronograma
    	TelaCronograma cronograma = new TelaCronograma(obj.getDataInicio(), obj.getDataFim(), obj.getCronogramas());
    	setTelaCronograma(cronograma);

    	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_CRONOGRAMA);
	}

    /**
     * Diretório base do caso de uso. 
     * 
     * @return Retorna diretório base do caso de uso.
     */
    @Override
    public String getDirBase() {
    	return "/extensao/PlanoTrabalho";
    }
    
    /**
     * Redireciona o usuário para página de cadastro de dados gerais do plano de trabalho.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul><li> /sigaa.war/projetos/PlanoTrabalho/cronograma.jsp </li></ul> 
     * 
     * @return Página de dados gerais do plano.
     * @throws DAOException 
     */
    public String irDadosGerais() throws DAOException {
    	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_BOLSISTA_FORM);
    }
    
    /**
     * Redireciona o usuário para página do cronograma do plano de trabalho.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul><li> /sigaa.war/projetos/PlanoTrabalho/resumo.jsp </li></ul> 
     * 
     * @return Página de cronograma do plano.
     */
    public String irCronograma() {
    	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_CRONOGRAMA);
    }
    
    /**
     * Submete dados do cronograma e redireciona o usuário para tela de resumo.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul><li> /sigaa.war/projetos/PlanoTrabalho/cronograma.jsp </li></ul> 
     * 
     * @return Página de resumo. 
     * @throws DAOException Gerado pela busca dos dados para validação.
     */
    public String submeterCronograma() throws SegurancaException, DAOException, Exception {

	checkChangeRole();

	// Obter objetos cronogramas a partir dos dados do formulário
	String[] calendario = getCurrentRequest().getParameterValues("telaCronograma.calendario");
	getTelaCronograma().setCalendario(calendario);

	String[] atividadesDesenvolvidas = getCurrentRequest().getParameterValues("telaCronograma.atividade");
	getTelaCronograma().setAtividade(atividadesDesenvolvidas);

	// Obtendo atividades desenvolvidas do cronograma a partir da view.
	getTelaCronograma().definirCronograma(getCurrentRequest());
	List<CronogramaProjeto> cronogramas = getTelaCronograma().getCronogramas();
	for (CronogramaProjeto cronograma : cronogramas) {
	    cronograma.setPlanoTrabalhoProjeto(getObj());
	}
	getObj().setCronogramas(cronogramas);

	// Validar dados gerais do plano de trabalho
	if (ValidatorUtil.isEmpty(calendario)) {
	    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Cronograma");
	    return null;
	}

	if (ValidatorUtil.isEmpty(atividadesDesenvolvidas)) {
	    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Atividade");
	    return null;
	}
	
	ListaMensagens mensagens = new ListaMensagens();
	PlanoTrabalhoProjetoValidator.validarCronogramaProjeto(getObj(), mensagens);
	if (!mensagens.isEmpty()) {
	    addMensagens(mensagens);
	    return null;
	}

	obj.setProjeto(getGenericDAO().findByPrimaryKey(obj.getProjeto().getId(), Projeto.class));
	
	if(!obj.isAlterando()){
		setConfirmButton("Cadastrar");
	}
	else{
		setConfirmButton("Alterar");
	}
	
	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_RESUMO);
    }
    
    /**
     * Cadastra um plano de trabalho de um projeto associado.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul><li> /sigaa.war/projetos/PlanoTrabalho/resumo.jsp </li></ul> 
     * 
     * @return Página de sucesso do cadastro ou remoção do plano.
     * @throws ArqException Gerado pela chamada ao processador
     * 
     */
    @Override
    public String cadastrar() throws NegocioException, ArqException {
    	checkChangeRole();
    	Integer [] operacoes = {SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO.getId(), SigaaListaComando.REMOVER_PLANO_TRABALHO_PROJETO.getId()};
    	MovimentoCadastro mov = new MovimentoCadastro();	
    	if(isOperacaoAtiva(operacoes)){
    		if(isOperacaoAtiva(SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO.getId()) 
    				&& (getConfirmButton().equalsIgnoreCase("Cadastrar") || getConfirmButton().equalsIgnoreCase("Alterar"))){

    			ListaMensagens mensagens = new ListaMensagens();
    			mensagens = obj.validate();
    			if (!mensagens.isEmpty()) {
    				addMensagens(mensagens);
    				return null;
    			}
    			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO);

    		}
    		if (isOperacaoAtiva(SigaaListaComando.REMOVER_PLANO_TRABALHO_PROJETO.getId()) && getConfirmButton().equalsIgnoreCase("Remover")) {

    			ListaMensagens mensagens = new ListaMensagens();
    			mensagens = obj.validate();
    			if (!mensagens.isEmpty()) {
    				addMensagens(mensagens);
    				return null;
    			}
    			mov.setCodMovimento(SigaaListaComando.REMOVER_PLANO_TRABALHO_PROJETO);
    		}
    		mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			obj = new PlanoTrabalhoProjeto();
			if(!getConfirmButton().equalsIgnoreCase("Cadastrar")){
				return planosCoordenador();
			}
			else{
				return cancelar();
			}
    	}
    	else{
    		addMensagemErro("Ação já executada.");
    		return cancelar();
    	}

    }
    
    /**
     * Inicia remoção de uma plano de trbalho. Carrega o plano de trabalho selecionado 
     * e exibe tela de confirmação.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul><li> /sigaa.war/projetos/PlanoTrabalho/planos_coordenador.jsp </li></ul> 
     *
     * @return
     * @throws ArqException
     */
    public String preRemoverPlanoTrabalho() throws ArqException {

    	checkChangeRole();
    	prepareMovimento(SigaaListaComando.REMOVER_PLANO_TRABALHO_PROJETO);
    	setOperacaoAtiva(SigaaListaComando.REMOVER_PLANO_TRABALHO_PROJETO.getId());
    	int id = getParameterInt("id", 0);
    	if(obj == null) {
    		obj = new PlanoTrabalhoProjeto();
    		((PersistDB) obj).setId(id);
    	}else {
    		((PersistDB) obj).setId(id);
    	}

    	obj = getGenericDAO().findByPrimaryKey(obj.getId(), PlanoTrabalhoProjeto.class);

    	// evitar nullpointer na exibição do coordenador do projeto associado...
    	obj.getProjeto().setEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", obj.getProjeto().getId()));

    	// Inicializar tela do cronograma
    	TelaCronograma cronograma = new TelaCronograma(obj.getDataInicio(), obj.getDataFim(), obj.getCronogramas());
    	setTelaCronograma(cronograma);
    	setReadOnly(true);
    	setConfirmButton("Remover");
    	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_RESUMO);

    }
    
    /**
     * Inicia a atualização do plano de trabalho do discente.
     * Esta operação permite que somente o plano de trabalho seja alterado
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul><li> /sigaa.war/projetos/PlanoTrabalho/planos_coordenador.jsp </li></ul>
     * 
     * @return Página para alteração dos dados do plano.
     * @throws ArqException Somente coordenadores de ações podem atualizar os planos.  
     */
    public String atualizar() throws ArqException {
	checkChangeRole();
	prepareMovimento(SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO);
	setOperacaoAtiva(SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO.getId());
	setId();
	obj = getGenericDAO().findByPrimaryKey(obj.getId(), PlanoTrabalhoProjeto.class);
	obj.getCronogramas().iterator();
	
	if (!ValidatorUtil.isEmpty(obj.getDiscenteProjeto())) { 
	    int id = obj.getDiscenteProjeto().getDiscente().getId();
	    getGenericDAO().refresh(obj.getDiscenteProjeto());
	    obj.getDiscenteProjeto().setDiscente(getGenericDAO().findByPrimaryKey(id, Discente.class));
	}
	
	setReadOnly(false);

	// Informa que plano de trabalho será alterado
	this.obj.setAlterando(true);
	return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_BOLSISTA_FORM);
    }
    
    /**
     * Carrega plano e prepara MBeans para visualização.
     * <br/>
     * Método chamado pela(s) seguintes JSPs:
     * <ul>
     * 	<li> /sigaa.war/projetos/PlanoTrabalho/listaPlanos.jsp </li>
     * </ul>
     * 
     * @return Página de visualização do plano.
     */
    public String view() {

    	try {
    		Integer id = getParameterInt("id", 0);
    		if (id != 0) {
    			DiscenteProjeto discente = getGenericDAO().findByPrimaryKey(id, DiscenteProjeto.class);		
    			obj = getGenericDAO().findByPrimaryKey(discente.getPlanoTrabalhoProjeto().getId(), PlanoTrabalhoProjeto.class);
    		}else {
    			int idPlano = getParameterInt("idPlano", 0);
    			obj = getGenericDAO().findByPrimaryKey(idPlano, PlanoTrabalhoProjeto.class);
    		}

    		// evitar nullpointer na exibição do coordenador...
    		obj.getProjeto().setEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", obj.getProjeto().getId()));                
    		// Inicializar tela do cronograma
    		TelaCronograma cronograma = new TelaCronograma(obj.getDataInicio(), obj.getDataFim(), obj.getCronogramas());
    		setTelaCronograma(cronograma);
    		return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_VIEW);

    	} catch (Exception e) {
    		notifyError(e);
    		addMensagemErro(e.getMessage());
    		return null;
    	}

    }
    
    /**
	 * redireciona para tela de finalizar discente projeto.
	 * <br/>
	 * Método chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/projetos/PlanoTrabalho/planos_coordenador.jsp </li></ul>
	 * 
	 */
	public String iniciarFinalizarDiscente() throws ArqException {
		checkChangeRole();
		setId();	
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), PlanoTrabalhoProjeto.class);
		prepareMovimento(SigaaListaComando.FINALIZAR_DISCENTE_PROJETO_PLANO);
		setOperacaoAtiva(SigaaListaComando.FINALIZAR_DISCENTE_PROJETO_PLANO.getId());
		return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_FINALIZAR_DISCENTE);
	}
	
	/**
	 * Realiza operação de finalização do discente no plano de trabalho.
	 * <br/>
	 * Método chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/projetos/PlanoTrabalho/finalizar_discente.jsp </li> 
	 * </ul>
	 */
	public String finalizarDiscente() throws ArqException {

		checkChangeRole();
		if(isOperacaoAtiva(SigaaListaComando.FINALIZAR_DISCENTE_PROJETO_PLANO.getId())){
			ListaMensagens mensagens = new ListaMensagens();
			PlanoTrabalhoProjetoValidator.validaFinalizarDiscente(obj, mensagens);
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}
			try {
				CadastroExtensaoMov mov = new CadastroExtensaoMov();
				mov.setObjMovimentado(obj);
				mov.setCodMovimento(SigaaListaComando.FINALIZAR_DISCENTE_PROJETO_PLANO);
				execute(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				notifyError(e);
			}
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			return planosCoordenador();// TODO: indicar novo dicente? iniciarAlterarPlano();
		}
		else
			addMensagemErro("Discente já finalizado.");
		return cancelar();

	}
	
	/**
	 * Retorna todos os planos do coordenador para indicar/substituir bolsista.
	 * <br/>
	 * Método chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/projetos/PlanoTrabalho/menu_docente.jsp </li> 
	 * </ul>
	 * @throws ArqException 
	 * 
	 */
	public String iniciarIndicarDiscentePlanoLista() throws ArqException {
		checkChangeRole();
		PlanoTrabalhoProjetoDao dao = getDAO(PlanoTrabalhoProjetoDao.class);
		planosCoordenadorLogado = dao.findByServidorCoordenadorAtivo(getUsuarioLogado().getServidor(),
				new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO});
		return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_ALTERAR_DISCENTE_PLANO_LISTA);
	}
	
	/**
	 * Carrega do banco o plano de trabalho selecionado e 
	 * redireciona o usuário para tela de indicação de novo
	 * discente para o plano.
	 *	<br/>
	 * Método chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/menu_ta.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/menu_docente.jsp </li> 
	 * </ul>
	 * 
	 * @return Página com formulário para inidicação de novo discente no plano.
	 * @throws ArqException Somente coordenadores podem realizar esta operação.
	 */
	public String iniciarIndicarDiscente() throws ArqException {
		checkChangeRole();
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), PlanoTrabalhoProjeto.class);
		obj.setDiscenteProjetoNovo(new DiscenteProjeto());
		obj.getDiscenteProjetoNovo().setProjeto(new Projeto(obj.getProjeto().getId()));
		obj.getDiscenteProjetoNovo().setBanco(new Banco());	
		obj.getDiscenteProjetoNovo().setTipoVinculo(new TipoVinculoDiscente());
		getInscricoesAcaoAssociadaSelecionada();
		prepareMovimento(SigaaListaComando.INDICAR_DISCENTE_PROJETO_PLANO);
		setOperacaoAtiva(SigaaListaComando.INDICAR_DISCENTE_PROJETO_PLANO.getId());
		return forward(ConstantesNavegacaoProjetos.PLANO_TRABALHO_PROJETO_INDICAR_DISCENTE);
	}
	
	/**
	 * Indica novo discente para o plano de trabalho.
	 * <br/>
	 * Método chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/alterar_discente_plano_lista.jsp </li>
	 * </ul>
	 */
	public String indicarDiscente() throws ArqException {
		checkChangeRole();
		if(isOperacaoAtiva(SigaaListaComando.INDICAR_DISCENTE_PROJETO_PLANO.getId())){
			try {	    
				int idDiscente = obj.getDiscenteProjetoNovo().getDiscente().getId();
				Discente discente = getGenericDAO().findByPrimaryKey(idDiscente, Discente.class);
				if (discente == null) {
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Novo Discente");
					return null;
				}
				//Validação
				obj.getDiscenteProjetoNovo().setDiscente(discente);
				ListaMensagens mensagens = new ListaMensagens();
				
				if(obj.getDiscenteProjetoNovo().getTipoVinculo().getId() > 0 ){
					getGenericDAO().initialize(obj.getDiscenteProjetoNovo().getTipoVinculo());
				}
				PlanoTrabalhoProjetoValidator.validaIndicarDiscente(obj, mensagens);
				if (!mensagens.isEmpty()) {
					addMensagens(mensagens);
					return null;
				}
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				mov.setCodMovimento(SigaaListaComando.INDICAR_DISCENTE_PROJETO_PLANO);
				if (!obj.getDiscenteProjetoNovo().getTipoVinculo().isRemunerado()) {
					obj.getDiscenteProjetoNovo().setBanco(null);
				}
				execute(mov, getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
			}
		}else{
			addMensagemErro("Opereação já executada.");
			return cancelar();
		}
		return iniciarIndicarDiscentePlanoLista();	
	}
	
	/**
	 * Lista todos os discentes que demonstraram interesse em participar do
	 * processo seletivo da ação associada.
	 * <br/>
	 * Método chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/projetos/PlanoTrabalho/form.jsp </li></ul>
	 * <ul><li> /sigaa.war/projetos/PlanoTrabalho/indicar_discente.jsp </li></ul>
	 * 
	 * @throws DAOException se ocorrer erro relacionado com acesso aos dados
	 */
	public void getInscricoesAcaoAssociadaSelecionada() throws DAOException {

		InscricaoSelecaoProjetoDao dao = getDAO(InscricaoSelecaoProjetoDao.class);
		Collection<InscricaoSelecaoProjeto> inscritos = dao.findInscritosProcessoSeletivoByProjeto(obj.getProjeto().getId());
		for (InscricaoSelecaoProjeto inscricao : inscritos) {
			inscricao.setPrioritario(isPrioritario(inscricao.getDiscente()));
		}

		obj.getProjeto().setInscricoesSelecao(inscritos);
	}

	public Collection<PlanoTrabalhoProjeto> getPlanosCoordenadorLogado() {
		return planosCoordenadorLogado;
	}

	public void setPlanosCoordenadorLogado(
			Collection<PlanoTrabalhoProjeto> planosCoordenadorLogado) {
		this.planosCoordenadorLogado = planosCoordenadorLogado;
	}

	public Collection<Projeto> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<Projeto> projetos) {
		this.projetos = projetos;
	}

	public Integer getIdProjeto() {
		return idProjeto;
	}

	public void setIdProjeto(Integer idProjeto) {
		this.idProjeto = idProjeto;
	}

	public TelaCronograma getTelaCronograma() {
		return telaCronograma;
	}

	public void setTelaCronograma(TelaCronograma telaCronograma) {
		this.telaCronograma = telaCronograma;
	}

	public boolean isVoluntario() {
		return voluntario;
	}

	public void setVoluntario(boolean voluntario) {
		this.voluntario = voluntario;
	}

	public Collection<DiscenteProjeto> getDiscentesProjeto() {
		return discentesProjeto;
	}

	public void setDiscentesProjeto(Collection<DiscenteProjeto> discentesProjeto) {
		this.discentesProjeto = discentesProjeto;
	}
    
	
}
