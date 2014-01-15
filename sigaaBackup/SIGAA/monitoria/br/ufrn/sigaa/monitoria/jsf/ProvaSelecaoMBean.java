/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/07/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.monitoria.InscricaoSelecaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProvaSelecaoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ComponenteCurricularMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecaoComponenteCurricular;
import br.ufrn.sigaa.monitoria.dominio.TipoSituacaoProvaSelecao;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaMov;
import br.ufrn.sigaa.monitoria.negocio.ProvaSelecaoValidator;

/** Controller responsável pela operação de cadastro de provas de seleção (usado em bolsas de monitoria).
 * @author Édipo Elder F. Melo
 *
 */
@Component("provaSelecao") @Scope("session")
public class ProvaSelecaoMBean extends SigaaAbstractController<ProvaSelecao> {

	/** Coleção de provas de seleção. */
	private Collection<ProvaSelecao> provasSelecao = new ArrayList<ProvaSelecao>();

	/** Coleção de Componentes Curriculares a serem removidos. */
	private Collection<ProvaSelecaoComponenteCurricular> provaComponentesRemovidos = new ArrayList<ProvaSelecaoComponenteCurricular>();

	/** Projeto de ensino associado. */
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	/** Mensagem a ser exibida ao usuário na operação de adição de componente curricular ao processo seletivo. */
	private String mensagemAdicionarComponente;
	
	/** Lista de projetos coordenados pelo usuário atual. Coordenador no cadastro da prova. */
	private Collection<ProjetoEnsino> projetos = new ArrayList<ProjetoEnsino>();

	/** Construtor padrão. */
	public ProvaSelecaoMBean() {
		obj = new ProvaSelecao();
	}

	/** 
	 * Verifica os papéis.
	 * 
	 * JSP: Não invocado por JSP.
	 * 
	 * @throws SegurancaException  só docentes podem realizar esta operação.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	public void checkChangeRole() throws SegurancaException {
		if (!getAcessoMenu().isMonitoria()) {
			checkDocenteRole();
		}
	}

	/**
	 * Lista todos os componentes curriculares do projeto de ensino.
	 * 
	 * @return lista de {@link ComponenteCurricularMonitoria}
	 * @throws DAOException por busca dos componentes curriculares
	 */
	public Collection<ComponenteCurricularMonitoria> getAllComponentesCurriculares() throws DAOException {

		Collection<ComponenteCurricularMonitoria> componentes = new ArrayList<ComponenteCurricularMonitoria>();
		TurmaDao turmaDao = getDAO(TurmaDao.class);

		// marcando componentes...
		int situacoes[] = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_SITUACOES_TURMAS);
		for (ComponenteCurricularMonitoria ccm : obj.getProjetoEnsino().getComponentesCurriculares()) {
			ProvaSelecaoComponenteCurricular pscc = new ProvaSelecaoComponenteCurricular();
			pscc.setComponenteCurricularMonitoria(ccm);
			pscc.setProvaSelecao(obj);
			if (turmaDao.countTurmasByComponente(ccm.getDisciplina(), situacoes) <= 0) {
				ccm.setComponenteNovo(true);
			}
			if (!obj.getComponentesObrigatorios().contains(pscc)) {
				componentes.add(ccm);
			}
		}

		return componentes;
	}

    /**
     * Seleciona o componente curricular e o insere na lista de componentes
     * obrigatórios para prova seletiva.
     * 
     * Método chamado pelas seguintes JSPs: 
     * <ul>
     * 	<li>/monitoria/CadastrarProvaSelecao/form.jsp</li>
     * </ul>
     * 
     * @throws SegurancaException somente docentes podem realizar esta operação.
     * @throws DAOException por busca dos componentes curriculares.
     * 
     */
    public void adicionarComponenteCurricular() throws DAOException,  SegurancaException {
    	mensagemAdicionarComponente = "";
    	checkChangeRole();
    	Integer idComponente = getParameterInt("idComponente", 0);
    	if (idComponente > 0) {
    		// verifica se o componente é novo, isto é, tem pelo menos uma turma aberta ou consolidada.
    		TurmaDao turmaDao = getDAO(TurmaDao.class);
    		ComponenteCurricularMonitoria ccm = getGenericDAO().findByPrimaryKey(idComponente, ComponenteCurricularMonitoria.class);
    		ProvaSelecaoComponenteCurricular pscc = new ProvaSelecaoComponenteCurricular();
    		pscc.setComponenteCurricularMonitoria(getGenericDAO().findByPrimaryKey(idComponente, ComponenteCurricularMonitoria.class));
    		int situacoes[] = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_SITUACOES_TURMAS);
    		if (turmaDao.countTurmasByComponente(ccm.getDisciplina(), situacoes) <= 0) {
    			mensagemAdicionarComponente = UFRNUtils.getMensagem(MensagensMonitoria.COMPONENTE_CURRICULAR_NOVO, ccm.getDisciplina().getDescricaoResumida()).getMensagem();    				
    			pscc.getComponenteCurricularMonitoria().setComponenteNovo(true);
    		}
    		pscc.setProvaSelecao(obj);
    		pscc.setRegistro(getUsuarioLogado().getRegistroEntrada());
    		obj.getComponentesObrigatorios().add(pscc);
    	}
    }

    /**
     * Remove componente curricular da lista de componentes obrigatórios para
     * prova seletiva.
     * <br/>
     * 
     * Método chamado pelas seguintes JSPs:
     * <ul>
     * 	<li>sigaa.war/monitoria/CadastrarProvaSelecao/form.jsp</li>
     * </ul>
     * 
     * @return retorna mesma tela permitindo remoção de novo componente curricular.
     * @throws DAOException por busca do componente curricular.
     * @throws SegurancaException somente docentes podem realizar esta operação.
     */
    public String removeComponenteCurricular() throws DAOException, SegurancaException {
    	checkChangeRole();
    	Integer idComponente = getParameterInt("idComponente", 0);
    	Integer idProvaComponente = getParameterInt("idProvaComponente", 0);
    	if (idComponente > 0) {
    		ProvaSelecaoComponenteCurricular pscc = new ProvaSelecaoComponenteCurricular();
    		pscc.setId(idProvaComponente);
    		pscc.setComponenteCurricularMonitoria(getGenericDAO().findByPrimaryKey(idComponente, ComponenteCurricularMonitoria.class));
    		pscc.setProvaSelecao(obj);
    		obj.getComponentesObrigatorios().remove(pscc);
    		// adiciona na lista de componentes para remover do banco de dados
    		if (pscc.getId() > 0) {
    			provaComponentesRemovidos.add(pscc);
    		}
    	}
    	return null;
    }

    /** 
     * Realiza o cadastro da prova seletiva.
     * <br/>
     * 
     * Método chamado pelas seguintes JSPs:
     * <ul><li>sigaa.war/monitoria/CadastrarProvaSelecao/form.jsp</li></ul>
     * 
     * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
     * @throws ArqException somente docentes podem realizar esta operação.
     * @return retorna para página de projeto coordenados pelo usuário atual.
     * 
     */
    @Override
    public String cadastrar() throws ArqException {
    	checkChangeRole();

    	// a remoção só é permitida se não existirem alunos cadastrados na prova
    	// seletiva...
    	if ("Remover".equalsIgnoreCase(getConfirmButton())) {
    		obj.setAtivo(false);
    	} else {
    		obj.setRegistro(getUsuarioLogado().getRegistroEntrada());
    	}

    	ListaMensagens mensagens = new ListaMensagens();
    	// preparando para validar, evita o erro de lazy
    	InscricaoSelecaoMonitoriaDao dao = getDAO(InscricaoSelecaoMonitoriaDao.class);
    	obj.setDiscentesInscritos(dao.findByProvaSeletiva(obj.getId()));

    	ListaMensagens lista = obj.validate();
    	if (lista != null && !lista.isEmpty()) {
    		addMensagens(lista);
    		return forward(getFormPage());
    	}

    	// verifica limites de bolsas reservadas na prova seletiva...
    	ProvaSelecaoValidator.validaReservaVagaProva(obj, mensagens);

    	// a prova não pode conter atividades novas e antigas. Deve ser somente novas, ou somente antigas.
    	TurmaDao turmaDao = getDAO(TurmaDao.class);
    	boolean componenteNovo = false;
    	int situacoes[] = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_SITUACOES_TURMAS);
    	for (ProvaSelecaoComponenteCurricular ccm : obj.getComponentesObrigatorios()) {
    		if (ccm.isObrigatorio()) {
    			if (turmaDao.countTurmasByComponente(ccm.getComponenteCurricularMonitoria().getDisciplina(), situacoes) <= 0) {
    				componenteNovo = true;
    			}
    		}
    	}
    	if (componenteNovo) {
    		addMensagemErro("Selecione como obrigatórios para a prova, apenas componentes antigos.");
    	}

    	if (hasErrors()) {
    		addMensagens(mensagens);
    		return forward(getFormPage());
    	} else {

    		ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
    		mov.setObjMovimentado(obj);
    		mov.setProvaComponentesRemovidos(provaComponentesRemovidos);
    		mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROVA_SELECAO_MONITORIA);
    		mov.setAcao(ProjetoMonitoriaMov.ACAO_CADASTRAR_PROVA_SELECAO);
    		prepareMovimento(SigaaListaComando.CADASTRAR_PROVA_SELECAO_MONITORIA);
    		try {

    			execute(mov, getCurrentRequest());
    			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

    			obj = new ProvaSelecao();
    			provaComponentesRemovidos.clear();

    			// membros da prograd podem realizar alterações nas provas seletivas
    			// nestes casos, o redirecionamento após o cadastro deve ser
    			// para a página da prograd
    			if (getAcessoMenu().isMonitoria()) {
    				return forward(ConstantesNavegacaoMonitoria.VALIDASELECAO_LISTA);
    			} else {
    				// tela de projetos coordenados pelo usuário logado
    				return iniciarProcessoSeletivo();
    			}

    		} catch (Exception e) {
    			addMensagemErro(e.getMessage());
    			return forward(getFormPage());
    		}
    	}

    }

    /**
     * Lista todas as seleções do projeto de ensino escolhido.
     * <br/>
     * 
     * Não é chamado por JSP.
     * 
     * @return Página com lista das seleções cadastradas.
     */
    public String listarSelecoes() {

    	try {
    		Integer idProjeto = getParameterInt("id");
    		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
    		projetoEnsino = dao.findByPrimaryKey(idProjeto, ProjetoEnsino.class);
    		provasSelecao = dao.findSelecoesByProjeto(idProjeto, null);
    		return forward(getListPage());
    	} catch (Exception e) {
    		notifyError(e);
    		addMensagemErro(e.getMessage());
    		return null;
    	}
    }

    /**
     * Prepara o movimento e envia o usuário para tela de de cadastro da prova
     * seletiva.
     * <br/><br/>
     * Método chamado pelas seguintes JSPs: 
     * <ul>
     * 	<li> sigaa.war/monitoria/CadastrarProvaSelecao/lista.jsp</li>
     * 	<li>sigaa.war/monitoria/CadastrarProvaSelecao/projetos.jsp</li>
     * </ul>
     * 
     * @return Página de cadastro da prova seletiva.
     * @throws SegurancaException Somente coordenadores (docentes) podem realizar esta operação.
     */
    public String novaProvaSeletiva() throws SegurancaException {
    	checkChangeRole();

    	try {

    		Integer idProjeto = getParameterInt("idProjeto");
    		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
    		ProjetoEnsino projeto = dao.findByPrimaryKey(idProjeto, ProjetoEnsino.class);

    		// verifica se o projeto pode cadastrar prova seletiva...
    		ListaMensagens mensagens = new ListaMensagens();
    		ProvaSelecaoValidator.validaPermissaoProjetoAtivo(projeto, mensagens);

    		if (!mensagens.isEmpty()) {
    			addMensagens(mensagens);
    			return null;
    		}

    		obj = new ProvaSelecao();
    		obj.setProjetoEnsino(projeto);
    		obj.setAtivo(true);
    		obj.setSituacaoProva(getGenericDAO().findByPrimaryKey(TipoSituacaoProvaSelecao.AGUARDANDO_INSCRICAO, TipoSituacaoProvaSelecao.class));

    		//atualiza a prova com as bolsas ainda disponíveis para reservas
    		atualizarBolsasDisponiveis();

    		prepareMovimento(SigaaListaComando.CADASTRAR_PROVA_SELECAO_MONITORIA);
    		setConfirmButton("Cadastrar");

    		return forward(getFormPage());

    	} catch (Exception e) {
    		notifyError(e);
    		addMensagemErro(e.getMessage());
    		return null;
    	}
    }

    /**
     * Atualiza bolsas disponíveis levando em cosideração bolsas reservadas para outras provas.
     * @throws DAOException
     */
    private void atualizarBolsasDisponiveis()
    throws DAOException {

    	ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
    	//exibindo o total de bolsas do projeto que foram reservadas para outras provas	    
    	int totalRemuneradasNasProvas = dao.findTotalBolsasRemuneradasProvas(obj);
    	int totalNaoRemuneradasNasProvas = dao.findTotalBolsasNaoRemuneradasProvas(obj);

    	obj.setTotalBolsasRemuneradasDisponiveisReserva(obj.getProjetoEnsino().getBolsasConcedidas() - totalRemuneradasNasProvas);
    	obj.setTotalBolsasNaoRemuneradasDisponiveisReserva(obj.getProjetoEnsino().getBolsasNaoRemuneradas() - totalNaoRemuneradasNasProvas);
    }

    /**
     * Lista os candidatos cadastrados na prova seletiva informada.
     * <br/><br/>
     * Método chamado pelas seguintes JSPs:
     * <ul>
     * 	<li> sigaa.war/extensao/DetalhesSelecaoExtensao/lista_atividades.jsp
     *  <li> sigaa.war/monitoria/CadastrarProvaSelecao/lista.jsp
     *  <li> sigaa.war/monitoria/CadastrarProvaSelecao/projetos.jsp
     *  <li> sigaa.war/monitoria/DetalhesSelecaoMonitoria/lista_projetos.jsp
     *  <li> sigaa.war/monitoria/ValidaSelecaoMonitor/provas.jsp
     * </ul>
     * 
     * @return Página com os detalhes da prova seletiva. Lista dos candidatos à vaga de bolsista.
     * @throws DAOException Por erro ao buscar discentes inscritos.
     *  
     */
    public String visualizarCandidatos() throws DAOException {

    	Integer idProva = getParameterInt("id");

    	if ((idProva != null) && (idProva > 0)) {

    		loadObj(idProva);

    		InscricaoSelecaoMonitoriaDao dao = getDAO(InscricaoSelecaoMonitoriaDao.class);
    		obj.setDiscentesInscritos(dao.findByProvaSeletiva(idProva));

    		return forward(ConstantesNavegacaoMonitoria.DETALHESSELECAOMONITORIA_VISUALIZAR_CANDIDATOS);

    	} else {
    		addMensagemErro("Prova de seleção não informada.");
    		return null;
    	}

    }

    /**
     * Carrega a lista dos DiscenteMonitoria de um projeto para
     * exibir a página dos resultados da seleção.
     * <br/><br/>
     * Método chamado pelas seguintes JSPs:
     * <ul>
     *  <li> sigaa.war/monitoria/CadastrarProvaSelecao/lista.jsp
     *  <li> sigaa.war/monitoria/CadastrarProvaSelecao/projetos.jsp
     *  <li> sigaa.war/monitoria/DetalhesSelecaoMonitoria/lista_projetos.jsp
     *  <li> sigaa.war/monitoria/DiscenteMonitoria/projetos_visualizar_resultado.jsp
     * 	<li> sigaa.war/monitoria/ValidaSelecaoMonitor/provas.jsp
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String visualizarResultados() throws ArqException {

    	Integer idProva = getParameterInt("id");

    	if ((idProva != null) && (idProva > 0)) {
    		loadObj(idProva);
    	}

    	// todos os discentes que se inscreveram nessa seleção
    	GenericDAO dao = getGenericDAO();
    	obj.setResultadoSelecao(dao.findByExactField(DiscenteMonitoria.class, "provaSelecao.id", idProva, "asc", "classificacao"));

    	if (obj.getResultadoSelecao() == null
    			|| obj.getResultadoSelecao().size() == 0) {
    		addMensagemErro("O resultado da seleção desta prova ainda não foi publicado.");
    		return null;
    	}

    	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_VISUALIZAR_RESULTADO_SELECAO);

    }

    /**
     * Visualizar dados da prova seletiva.
     * <br/><br/>
     * Método chamado pelas seguintes JSPs:
     * <ul> 
     * 	<li> sigaa.war/monitoria/DiscenteMonitoria/projetos_visualizar_resultado.jsp</li>
     * </ul>
     * 
     * @return Página com o resultado da prova seletiva.
     * @throws DAOException Erro ao carregar prova seletiva.
     */
    public String view() throws DAOException {

    	Integer idProva = getParameterInt("id");

    	if ((idProva != null) && (idProva > 0)) {
    		loadObj(idProva);
    	}

    	return forward(ConstantesNavegacaoMonitoria.CADASTROPROVASELECAO_VIEW);

    }

    /** Recarrega o obj do banco com o id passado.
     *  
     * @param id id do objeto passado.
     */
    private void loadObj(int id) {

    	try {
    		obj = getGenericDAO().findByPrimaryKey(id, ProvaSelecao.class);
    	} catch (DAOException e) {
    		notifyError(e);
    	}

    }

    /** 
     * Alterar prova seletiva.
     * <br/><br/>
     * Método chamado pelas seguintes JSPs:
     * <ul>
     * 	<li> sigaa.war/monitoria/CadastrarProvaSelecao/lista.jsp
     *  <li> sigaa.war/monitoria/CadastrarProvaSelecao/projetos.jsp
     *  <li> sigaa.war/monitoria/ValidaSelecaoMonitor/provas.jsp
     * </ul> 
     * 
     * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
     * @throws ArqException somente coordenadores pode realizar esta operação.
     * @return Retorna para página de alterar dados da prova.
     */
    public String atualizar() throws ArqException {
	checkChangeRole();
	try {
	    prepareMovimento(SigaaListaComando.CADASTRAR_PROVA_SELECAO_MONITORIA);
	    setOperacaoAtiva(SigaaListaComando.CADASTRAR_PROVA_SELECAO_MONITORIA.getId());
	    setId();
	    loadObj(obj.getId());
	    atualizarBolsasDisponiveis();
	} catch (Exception e) {
	    addMensagemErroPadrao();
	    notifyError(e);
	}

	setConfirmButton("Alterar Prova");
	return forward(getFormPage());
    }

    /**
     * Diretório base do caso de uso.
     * <br/>
     * 
     * Não é chamado por JSP.
     * 
     * @return diretório do cadastro da prova.
     * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase()
     */
    public String getDirBase() {
	return "/monitoria/CadastrarProvaSelecao";
    }

    /** 
     * Retorna uma coleção de SelectItem com o número de Bolsas Concedidas ainda disponíveis (de n a 0).
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/CadastrarProvaSelecao/form.jsp</li>
     * </ul>
     * @return coleção de {@link SelectItem} para ser exibido no combo.
     */
    public Collection<SelectItem> getBolsasRemuneradasDisponiveisReservaCombo() {
    	ArrayList<SelectItem> lista = new ArrayList<SelectItem>();
    	for (int i = 0; i <= this.obj.getTotalBolsasRemuneradasDisponiveisReserva(); i++) {
    		Integer aux = new Integer(i);
    		SelectItem item = new SelectItem(aux.toString(), aux.toString());
    		lista.add(item);
    	}
    	return lista;
    }    

    /** 
     * Retorna uma coleção de SelectItem com o número de Bolsas Não Remuneradas (de n a 0).
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/CadastrarProvaSelecao/form.jsp</li>
     * </ul>
     * @return coleção de {@link SelectItem} para ser exibido no combo.
     */
    public Collection<SelectItem> getBolsasNaoRemuneradasDisponiveisReservaCombo() {
    	ArrayList<SelectItem> lista = new ArrayList<SelectItem>();
    	for (int i = 0; i <= this.obj.getTotalBolsasNaoRemuneradasDisponiveisReserva(); i++) {
    		Integer aux = new Integer(i);
    		SelectItem item = new SelectItem(aux.toString(), aux.toString());
    		lista.add(item);
    	}
    	return lista;
    }

    
    
	/**
	 * Lista todos os projetos ativos coordenados pelo usuário logado 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/CadastrarProvaSelecao/projetos.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * 
	 */
    public String iniciarProcessoSeletivo() throws DAOException, SegurancaException {
		checkDocenteRole();
		//somente servidores ativos tem projetos de monitoria
		if(!isEmpty(getServidorUsuario())){
			projetos = getDAO(ProvaSelecaoDao.class).findProjetosCoordenadosByServidor(getServidorUsuario().getId());
		}else {
			projetos = null;	
		}
		return forward(ConstantesNavegacaoMonitoria.CADASTROPROVASELECAO_PROJETOS);
    }

    
    /** Retorna a coleção de provas de seleção. 
     * @return
     */
    public Collection<ProvaSelecao> getProvasSelecao() {
    	return provasSelecao;
    }

    /** Seta a coleção de provas de seleção.
     * @param provasSelecao
     */
    public void setProvasSelecao(Collection<ProvaSelecao> provasSelecao) {
    	this.provasSelecao = provasSelecao;
    }

    /** Retorna o projeto de ensino associado. 
     * @return
     */
    public ProjetoEnsino getProjetoEnsino() {
    	return projetoEnsino;
    }

    /** Seta o projeto de ensino associado.
     * @param projetoEnsino
     */
    public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
    	this.projetoEnsino = projetoEnsino;
    }

    /** Retorna a coleção de Componentes Curriculares a serem removidos. 
     * @return
     */
    public Collection<ProvaSelecaoComponenteCurricular> getProvaComponentesRemovidos() {
    	return provaComponentesRemovidos;
    }

    /** Seta a coleção de Componentes Curriculares a serem removidos.
     * @param provaComponentesRemovidos
     */
    public void setProvaComponentesRemovidos(
    		Collection<ProvaSelecaoComponenteCurricular> provaComponentesRemovidos) {
    	this.provaComponentesRemovidos = provaComponentesRemovidos;
    }

    /** Retorna a mensagem a ser exibida ao usuário na operação de adição de componente curricular ao processo seletivo. 
     * @return
     */
    public String getMensagemAdicionarComponente() {
    	return mensagemAdicionarComponente;
    }

    /** Seta a mensagem a ser exibida ao usuário na operação de adição de componente curricular ao processo seletivo.
     * @param mensagemAdicionarComponente
     */
    public void setMensagemAdicionarComponente(String mensagemAdicionarComponente) {
    	this.mensagemAdicionarComponente = mensagemAdicionarComponente;
    }

    /**
     * Lista de situações porssíveis para uma prova seletiva. 
     */
    public Collection<SelectItem> getAllSituacaoProvaCombo() throws ArqException {
    	return toSelectItems(getGenericDAO().findAll(TipoSituacaoProvaSelecao.class), "id", "descricao");
    }

	public Collection<ProjetoEnsino> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}

    
}
