/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/10/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/*******************************************************************************
 * MBean respons�vel por controlar o cadastro de participantes de a��es de
 * extens�o. <br/>
 * 
 * O cadastro destes participantes s� pode ser realizado pelo coordenador da a��o
 * atrav�s do menu no portal docente.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope("session")
@Component("participanteAcaoExtensao")
@Deprecated
public class ParticipanteAcaoExtensaoMBean extends SigaaAbstractController<ParticipanteAcaoExtensao> {	
	

//	/** Usado como auxiliar de tela de busca de pessoas externas*/
//	private boolean checkBuscaNome;
//	
//	/** Usado como auxiliar de tela de busca de pessoas da instituicao*/
//	private boolean checkBuscaInstituicao;
//	
//	/** Usado como auxiliar de tela de busca de pessoas por municipio*/
//	private boolean checkBuscaMunicipio;
//	
//	/** Usado como auxiliar de tela de busca para gerar relat�rio*/
//	private boolean checkGerarRelatorio;
//	
//	/** Usado como auxiliar de busca para trazer dados do participante procurado*/
//	private ParticipanteAcaoExtensao buscaParticipante;
//
//
//	/** Usado como auxiliar de tela de busca de pessoas externas*/
//	private String buscaNome;
//	
//	/** Usado como auxiliar de tela de busca de pessoas da instituicao*/
//	private String buscaInstituicao;
//	
//	
//	
//	
//	/** Atributo utilizado para representar a Atividade de Extens�o */
//	private AtividadeExtensao acao = new AtividadeExtensao();
//	
//	/** Atributo utilizado para representar a Sub Atividade de Extens�o */
//	private SubAtividadeExtensao subAcao = new SubAtividadeExtensao();
//
//	/** Atributo utilizado para armazenar informa�oes obtidas atrav�s de uma consulta ao banco */
//	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
//	
//	/** Usado como auxiliar para a continua��o do cadastro de relat�rio. */
//	private boolean continuarCadastroRelatorio = false;
//	
//	/** Usado como auxiliar no cadastro de participantes */
//	private String matriculas;
//
//	/** Categoria do Participante(DISCENTE) */
//	public static final int DISCENTES = 4;
//
//	/** Usado como auxiliar no cadastro de participante */
//	private Collection<Long> numerosMatricula = new ArrayList<Long>();
//	
//	/** Usado para armazenar informa��es de consulta ao banco */
//	private Collection<AtividadeExtensao> atividadesCoordenador;
//	
//	/** Usado para armazenar informa��es de consulta ao banco */
//	private Collection<AtividadeExtensao> subAtividadesCoordenador;
//	
//	/** Usado para armazenar informa��es de consulta ao banco */
//	private Collection<AtividadeExtensao> atividadesGenrenciador;
//	
//	/** Usada para o envio de mensagen para participantes */
//	private String msgParticipantes = new String();
//	
//
//
//	/** Atributo utilizado para verificar se continua-se ou n�o o gerenciamento */
//	private boolean continuarGerenciamento = false;

//	/**
//	 * Construtor padr�o.
//	 */
//	public ParticipanteAcaoExtensaoMBean() {
//		obj = new ParticipanteAcaoExtensao();
//	}

//	/**
//	 * Direciona o usu�rio para uma tela onde ser�o listadas todas as a��es de
//	 * extens�o onde ele � coordenador.
//	 * 
//	 * 
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/menu_ta.jsp
//	 * sigaa.war/extensao/menu.jsp
//	 * sigaa.war/portais/docente/menu_docente.jsp
//	 * 
//	 * @return Lista de a��es coordenadas pelo usu�rio logado.
//	 * @throws ArqException 
//	 */
//	public String listarAcoesExtensao() throws ArqException {
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		if(isContinuarGerenciamento()){
//			carregarParticipantes(obj.getAcaoExtensao().getId());
//			setContinuarGerenciamento(false);
//			return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA);
//		}
//		
//		//retira o bot�o de retornar ao relat�rio(utilizado no cadastro de relat�rios parciais e finais)
//		//a partir do cadastro de relat�rios finais e parciais o coordenador pode atualizar a lista de participantes.
//		continuarCadastroRelatorio = false;
//		matriculas = "";
//		try {
//		    atividadesCoordenador = carregarAtividadeCoordenador();
//		    Integer[] situacoes = (Integer[]) ArrayUtils.addAll(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO, TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
//			subAtividadesCoordenador = getDAO(AtividadeExtensaoDao.class).findSubAtividadesByCoordenadorAtivo(getUsuarioLogado().getServidor(), situacoes);
//		} catch (DAOException e) {
//		    notifyError(e);
//		}
//		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_ACOES_LISTA);
//	}
	
	
	
	
	
	
	
	
//	/**
//	 * 
//	 * Gerenciar Participantes (COORDENADOR) 
//	 * 
//	 * 
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/portais/docente/menu_docente.jsp
//	 * 
//	 * @return Lista de a��es coordenadas pelo usu�rio logado.
//	 * @throws ArqException 
//	 */
//	public String listarAcoesExtensaoCoordenador() throws ArqException {		
//		try {
//			AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
//			Integer[] situacoes = (Integer[]) ArrayUtils.addAll(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO, TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
//			atividadesCoordenador = dao.findByCoordenadorAtivo(getUsuarioLogado().getServidor(), situacoes);
//			atividadesGenrenciador = dao.findByGerenciadorAtivo(getUsuarioLogado().getServidor(), situacoes);
//			subAtividadesCoordenador = dao.findSubAtividadesByCoordenadorAtivo(getUsuarioLogado().getServidor(), situacoes);
//			
//			
//			if(ValidatorUtil.isEmpty(atividadesCoordenador) && 
//					ValidatorUtil.isEmpty(atividadesGenrenciador) && 
//					ValidatorUtil.isEmpty(subAtividadesCoordenador)  ) {
//				addMensagemWarning("N�o foi encontrada nenhuma a��o ou mini a��o na qual seja poss�vel o usu�rio logado gerenciar participantes.");
//				return null;
//			}
//			
//		} catch (DAOException e) {
//		    notifyError(e);
//		}
//		
//		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_ACOES_LISTA);
//	}
//	
//	
	
	

	/**
	 * Antes de cadastrar efetivamente
	 * {@link SigaaAbstractController#cadastrar()} e de validar o cadastro no
	 * SigaaAbstractController, prepara o participante preenchendo data de cadastro,
	 * registro de entrada, etc.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSPs</li>
	 * </ul>
	 * 
	 */
	@Override
	public void beforeCadastrarAndValidate() {
		obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		obj.setDataCadastro(new Date());
		obj.setAtivo(true);
	}

//	/**
//	 * M�todo utilizado para realizar a��es depois de atulizar.
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>N�o � chamado por JSPs</li>
//	 * </ul>
//	 * 
//	 */
//	@Override
//	public void afterAtualizar() throws ArqException {
//		// carregando para evitar erro de lazy na valida��o
//		obj.getAcaoExtensao().setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findByAcao(acao.getId()));
//		if(obj.getCpf() == null)
//		    obj.setInternacional(true);
//		
//		if(obj.getUnidadeFederativa() == null)
//			obj.setUnidadeFederativa(new UnidadeFederativa(-1));
//		
//		if(obj.getMunicipio() == null){
//			obj.setMunicipio(new Municipio(-1));
//		}else{
//			carregarMunicipios(); // carrega os munic�pios da UF j� salva para a participante.
//		}
//		
//	}

//	/**
//	 * Realiza algumas valida��es antes de passar o controle do cadastro para o
//	 * cadastrar de {@link SigaaAbstractController#cadastrar()}.
//	 * 
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp
//	 * 
//	 * @return
//	 * @throws SegurancaException, ArqException, NegocioException
//	 */
//	@Override
//	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
//			
//		// evitar nullpointer no equals dos objetos comparados...
//		if (obj.getTipoParticipante() != null) {
//			
//			if (obj.getTipoParticipante() == ParticipanteAcaoExtensao.DISCENTE_UFRN) {
//				obj.setDiscente(getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), Discente.class));
//				if (obj.getDiscente() == null) {
//					obj.setDiscente(new Discente());
//					addMensagemErro("Discente: Campo obrigat�rio n�o informado.");
//					if (obj.getTipoParticipacao().getId() == 0) {
//						addMensagemErro("Categoria: Campo obrigat�rio n�o informado.");
//					}
//					return null;
//				}
//				carregaParticipanteAcaoExtensao();
//			}
//			
//			if (obj.getTipoParticipante() == ParticipanteAcaoExtensao.SERVIDOR_UFRN) {
//				obj.setServidor(getGenericDAO().findByPrimaryKey(obj.getServidor().getId(), Servidor.class));
//				if (obj.getServidor() == null){
//					obj.setServidor(new Servidor());
//					addMensagemErro("Servidor: Campo obrigat�rio n�o informado.");
//					if (obj.getTipoParticipacao().getId() == 0) {
//						addMensagemErro("Categoria: Campo obrigat�rio n�o informado.");
//					}
//					return null;
//				}
//			}
//			if (obj.getTipoParticipante() == ParticipanteAcaoExtensao.DISCENTES){
//				return cadastroColetivoDiscentes();
//			}
//		}
//		ListaMensagens mensagens = new ListaMensagens();
//		// valida��es
//		obj.getAcaoExtensao().setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findByAcao(acao.getId()));
//		ParticipanteAcaoExtensaoValidator.validaNovoParticipante(obj, mensagens);
//		
//		if (!mensagens.isEmpty()) {
//			addMensagens(mensagens);
//			return null;
//		}
//		
//		
//		if(ValidatorUtil.isEmpty(obj.getSubAtividade())){
//			obj.setSubAtividade(null);
//		}
//		
//		// Tratamento do campo de Observa��o no Certificado antes de cadastrar
//		// Tratamento para que ????? Qual o motivo deixar a primeira letra em min�sculo ?????
//		// Esse tipo de coment�rio n�o serve para nada !!!
//		
//		/* Converte a primeira letra da observa��o no certificado para min�sculo antes de cadastrar 
//		 * e retira os espa�os em branco antes e depois da observa��o.  Isso provavelmente porque 
//		 * a observa��o vai ser concatenada com o texto do certificado e n�o pode come�ar com letra mai�scula.
//		 */
//		if(  StringUtils.notEmpty(obj.getObservacaoCertificado())  ){
//			String observacao = obj.getObservacaoCertificado().trim();
//			observacao = observacao.substring(0, 1).toLowerCase().concat(observacao.substring(1));
//			obj.setObservacaoCertificado(observacao);
//		}
//		
//		String forward = super.cadastrar();
//		if (forward == null) {
//			return redirectMesmaPagina();
//		} else {
//			return redirectJSF(forward);
//		}
//
//	}
	
//	/**
//	 * Carrega as informa��es do participante da a��o a partir do discente informado no formul�rio de cadastro
//	 */
//	private void carregaParticipanteAcaoExtensao(){
//		obj.setCpf(obj.getDiscente().getPessoa().getCpf_cnpj());
//		obj.setNome(obj.getDiscente().getPessoa().getNome());
//		obj.setDataNascimento(obj.getDiscente().getPessoa().getDataNascimento());
//		obj.setEndereco(obj.getDiscente().getPessoa().getEndereco());
//		obj.setCep(obj.getDiscente().getPessoa().getCEP());
//		obj.setEmail(obj.getDiscente().getPessoa().getEmail());
//	}

//	/**
//	 * Antes de cadastrar e depois da valida��o, faz mais alguns ajustes no novo
//	 * participante da a��o.
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>N�o � chamado por JSPs</li>
//	 * </ul>
//	 * 
//	 */
//	@Override
//	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
//
//		// evitar erro de unsaved transient exception
//		if ((obj.getTipoParticipante() != null)	&& (obj.getTipoParticipante() == ParticipanteAcaoExtensao.OUTROS)) {
//			obj.setServidor(null);
//			obj.setDiscente(null);
//
//			// padronizando uppercase no nome
//			if (obj.getNome() != null)
//				obj.setNome(obj.getNome().toUpperCase());
//		}
//		if ((obj.getTipoParticipante() != null) && (obj.getTipoParticipante() == ParticipanteAcaoExtensao.DISCENTE_UFRN)) {
//			obj.setServidor(null);
//		}
//		if ((obj.getTipoParticipante() != null) && (obj.getTipoParticipante() == ParticipanteAcaoExtensao.SERVIDOR_UFRN)) {
//			obj.setDiscente(null);
//		}
//		if ((obj.getTipoParticipante() != null)	&& (obj.getTipoParticipante() == ParticipanteAcaoExtensao.DISCENTES)) {
//			obj.setServidor(null);
//		}
//	}

//	/**
//	 * Ap�s o cadastramento, configura o Mbean para permitir um novo cadastro
//	 * logo em seguida, facilitando o cadastro em sequ�ncia de novos
//	 * participantes.
//	 * 
//	 * N�o � chamado por JSPs.
//	 * 
//	 */
//	@Override
//	protected void afterCadastrar() throws ArqException {
//
//			
//		getGenericDAO().clearSession();
//		acao = getGenericDAO().findByPrimaryKey(acao.getId(), AtividadeExtensao.class);
//		acao.setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findByAcao(acao.getId()));
//		
//		for(ParticipanteAcaoExtensao p : acao.getParticipantesNaoOrdenados()) {
//			if(p.getDiscente()!=null)
//				p.getDiscente().getPessoa();
//		}
//		obj = new ParticipanteAcaoExtensao();
//		obj.setAcaoExtensao(acao);
//		prepareMovimento(ArqListaComando.CADASTRAR);
//
//		setReadOnly(false);
//		setConfirmButton("Cadastrar");
//
//	}
	
//	/**
//	 * M�todo utilizado para iniciar o gerenciamento dos participantes
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String iniciaGerenciarParticipantes() throws ArqException{
//	    if (!getAcessoMenu().isCoordenadorExtensao()) {
//		throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//	    }
//	    clearMensagens();
//	    obj = new ParticipanteAcaoExtensao();
//	    int idAcao = getParameterInt("id", 0);
//	    carregarParticipantes(idAcao);
//	    
//	    //se participantes � nulo, nao redireciona para pagina de listagem
//	    if(acao.getParticipantesNaoOrdenados() == null || acao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("N�o h� participantes cadastrados para esta a��o de extens�o.");
//	    	return null;
//	    }
//	    
//	 // Verifica se � nulo para carregar municipios
//	    obj.setAcaoExtensao(acao);
//    	if (obj.getUnidadeFederativa() == null){ 
//    		obj.setUnidadeFederativa(buscaParticipante.getUnidadeFederativa());
//    	}
//	    carregarMunicipios();
//	    
//	    
//	    obj.getAcaoExtensao().setSituacaoProjeto(acao.getSituacaoProjeto());
//	    setConfirmButton("Cadastrar");
//	    setReadOnly(false);
//	    prepareMovimento(ArqListaComando.ALTERAR);
//	    return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA);
//	    
//	}
//	
//	/**
//	 * Redireciona para tela de cadastro de participante.
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String iniciarCadastroParticipante() throws ArqException{
//	    if (!getAcessoMenu().isCoordenadorExtensao()) {
//		throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//	    }
//		    obj = new ParticipanteAcaoExtensao();
//		    int idAcao = getParameterInt("id", 0);
//		    carregarMunicipios();
//		    acao = getGenericDAO().findByPrimaryKey(idAcao, AtividadeExtensao.class);
//	    if(acao.getSituacaoProjeto().getId() != TipoSituacaoProjeto.EXTENSAO_CONCLUIDO){
//		    carregarParticipantes(idAcao);
//		    obj.setAcaoExtensao(acao);
//		    obj.getAcaoExtensao().setSituacaoProjeto(acao.getSituacaoProjeto());
//		    setConfirmButton("Cadastrar");
//		    setReadOnly(false);
//		    prepareMovimento(ArqListaComando.CADASTRAR);
//		    return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_FORM);
//	    }else{
//	    	addMensagemErro("N�o � poss�vel cadastrar novos participantes, pois a a��o possui situa��o conclu�da");
//			return null;
//	    }
//	    	
//	}
//
//	/**
//	 * Prepara o MBean e o processador que ir� realizar o cadastro do
//	 * participante. Valida para somente coordenadores de a��es realizarem esta
//	 * opera��o. Redireciona o usu�rio para o form de cadastro.
//	 * <br>
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException, NegocioException 
//	 */
//	@Override
//	public String preCadastrar() throws ArqException, NegocioException {
//
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		if(acao.getSituacaoProjeto().getId() != TipoSituacaoProjeto.EXTENSAO_CONCLUIDO){
//			obj = new ParticipanteAcaoExtensao();
//			int idAcao = getParameterInt("id", 0);
//			carregarParticipantes(idAcao);
//			obj.setAcaoExtensao(acao);
//			obj.getAcaoExtensao().setSituacaoProjeto(acao.getSituacaoProjeto());
//			if(obj.getMunicipio() == null)
//				obj.setMunicipio(obj.getUnidadeFederativa().getCapital());
//			setConfirmButton("Cadastrar");
//			
//			setReadOnly(false);
//			prepareMovimento(ArqListaComando.CADASTRAR);
//			prepareMovimento(ArqListaComando.ALTERAR);
//			if(getCurrentURL().contains("ParticipanteAcaoExtensao/lista"))
//			    	setContinuarGerenciamento(true);
//			return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_FORM);
//		}
//		else{
//			addMensagemErro("N�o � poss�vel cadastrar novos participantes, pois a a��o possui situa��o conclu�da");
//			return null;
//		}
//	}
//
//	/**
//	 * M�todo utilizado para redicionar para a p�gina do formul�rio
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 */
//	@Override
//	public String forwardCadastrar() {
//		return getDirBase() + "/form.jsf";
//	}
//
//	/**
//	 * Seleciona a a��o de extens�o e lista todos os participantes para
//	 * impress�o de lista de frequ�ncia.
//	 * <br>
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException
//	 * @throws NegocioException
//	 */
//	public String listarParticipantes() throws ArqException, NegocioException {
//
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		clearMensagens();
//		
//		carregarParticipantes(getParameterInt("id", 0));
//		
//		 //se participantes � nulo, nao redireciona para pagina de listagem
//	    if(acao.getParticipantesNaoOrdenados() == null || acao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("N�o h� participantes cadastrados para esta a��o de extens�o.");
//	    	return null;
//	    }
//	
//	    
//	    if(getParameterBoolean("presenca")){
//	    	return forward(ConstantesNavegacao.PARTICIPANTES_EXTENSAO_LISTA_PRESENCA_PRINT);
//	    }else if(getParameterBoolean("contato")){
//	    	return forward(ConstantesNavegacao.PARTICIPANTEEXTENSAO_CONTATO_PRINT);
//	    }else{
//	    	return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA_PRINT);
//	    }
//	}
//	
//	/**
//	 * Filtra os participantes da a��o de extens�o por campos como Nome, Institui��o ou Munic�pio e gera o relat�rio
//	 * <br>
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException
//	 * @throws NegocioException
//	 */
//	public String buscarParticipantes() throws ArqException, NegocioException {
//
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		clearMensagens();
//		
//		ListaMensagens lista = new ListaMensagens();
//		
//		buscaParticipante = new ParticipanteAcaoExtensao();
//		buscaParticipante.setDiscente(null);
//		buscaParticipante.setServidor(null);
//		buscaParticipante.setAcaoExtensao(acao);
//		
//		if ( checkBuscaNome ){
//			if ( isEmpty(buscaNome) )
//				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
//			else
//				buscaParticipante.setNome(buscaNome);
//		} else
//			setBuscaNome(null);
//			
//		if(checkBuscaMunicipio){
//			ValidatorUtil.validateRequired(obj.getUnidadeFederativa(), "UF", lista);
//			ValidatorUtil.validateRequired(obj.getMunicipio(), "Munic�pio", lista);
//			buscaParticipante.setUnidadeFederativa(obj.getUnidadeFederativa());
//			buscaParticipante.setMunicipio(obj.getMunicipio());
//		} else
//		{
//			buscaParticipante.setUnidadeFederativa(new UnidadeFederativa(0));
//			buscaParticipante.setMunicipio(new Municipio(0));			
//		}
//		
//		if ( hasErrors() ) {
//			resultadosBusca = new ArrayList<ParticipanteAcaoExtensao>();
//			return null;
//		}
//
//		carregarParticipantesFiltro(acao.getId());
//		//carregarParticipantesFiltro(getParameterInt("id", 0));
//		
//		 //se participantes � nulo, nao redireciona para pagina de listagem
//	    if(acao.getParticipantesNaoOrdenados() == null || acao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("N�o h� participantes cadastrados para esta a��o de extens�o.");
//	    	return null;
//	    }
//
//		if ( hasErrors() ) {
//			resultadosBusca = new ArrayList<ParticipanteAcaoExtensao>();
//			return null;
//		}
//	
//		if(checkGerarRelatorio){
//			MunicipioDao dao = getDAO(MunicipioDao.class);
//			buscaParticipante.getUnidadeFederativa().setSigla(dao.findByPrimaryKey(obj.getUnidadeFederativa().getId(), UnidadeFederativa.class).getSigla());
//			buscaParticipante.getMunicipio().setNome(dao.findByPrimaryKey(obj.getMunicipio().getId(), Municipio.class).getNome());
//	    	return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA_PRINT);
//		}else{
//			 // Verifica se algum dos objetos � nulo para carregar municipios
//		    if(obj == null) obj = buscaParticipante;
//		    else
//		    	if (obj.getUnidadeFederativa() == null){ 
//		    		obj.setUnidadeFederativa(buscaParticipante.getUnidadeFederativa());
//		    		obj.setMunicipio(buscaParticipante.getMunicipio());
//		    	}
//			carregarMunicipios();
//			return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA);
//		}
//		
//		
//	    
//	}
//	
//	
//	
//	/**
//	 * Carrega, na propriedade 'acao' do MBean, todos os participantes
//	 * da a��o com id correspondente ao par�metro passado.
//	 * 
//	 * @param idAcao
//	 * @throws ArqException 
//	 */
//	private void carregarParticipantes(int idAcao) throws ArqException {
//		acao = getGenericDAO().findByPrimaryKey(idAcao, AtividadeExtensao.class);
//		acao.setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findByAcao(idAcao));
//		setResultadosBusca(acao.getParticipantesOrdenados());
//		getPaginacao().setTotalRegistros(resultadosBusca.size());
//		setTamanhoPagina(50);
//		getPaginacao().setPaginaAtual(0);
//	}
//	
//	
//	/**
//	 * Carrega, na propriedade 'subAcao' do MBean, todos os participantes
//	 * da subA��o com id correspondente ao par�metro passado.
//	 * 
//	 * @param idAcao
//	 * @throws ArqException 
//	 */
//	private void carregarParticipantesSubAcao(int idSubAcao) throws ArqException {
//		subAcao = getGenericDAO().findByPrimaryKey(idSubAcao, SubAtividadeExtensao.class);
//		subAcao.setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findBySubAcao(idSubAcao));
//		setResultadosBusca(subAcao.getParticipantesOrdenados());
//		getPaginacao().setTotalRegistros(resultadosBusca.size());
//		setTamanhoPagina(50);
//		getPaginacao().setPaginaAtual(0);
//	}
//	
//	/**
//	 * Carrega, na propriedade 'resultadosBusca' do MBean, os participantes
//	 * da a��o com id correspondente ao par�metro passado, filtrados pela busca.
//	 * 
//	 * @param idAcao
//	 * @throws ArqException 
//	 */
//	private void carregarParticipantesFiltro(int idAcao) throws ArqException {
//		acao = getGenericDAO().findByPrimaryKey(idAcao, AtividadeExtensao.class);
//		acao.setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findByAcaoFiltrada(idAcao, buscaParticipante));
//		setResultadosBusca(acao.getParticipantesOrdenados());
//		getPaginacao().setTotalRegistros(resultadosBusca.size());
//		setTamanhoPagina(50);
//		getPaginacao().setPaginaAtual(0);
//	}
//	
//	
//	/**
//	 * Carrega, na propriedade 'resultadosBusca' do MBean, os participantes
//	 * da SubA��o com id correspondente ao par�metro passado, filtrados pela busca.
//	 * 
//	 * @param idAcao
//	 * @throws ArqException 
//	 */
//	private void carregarParticipantesFiltroSubAcao(int idSubAcao) throws ArqException {
//		subAcao = getGenericDAO().findByPrimaryKey(idSubAcao, SubAtividadeExtensao.class);
//		subAcao.setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findBySubAcaoFiltrada(idSubAcao, buscaParticipante));
//		setResultadosBusca(subAcao.getParticipantesOrdenados());
//		getPaginacao().setTotalRegistros(resultadosBusca.size());
//		setTamanhoPagina(50);
//		getPaginacao().setPaginaAtual(0);
//	}
//	
//	
//	/**
//	 * M�todo utilizado para informar os resultados da busca
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/lista.jsp</li>
//	 * </ul> 
//	 */
//	 public Collection<ParticipanteAcaoExtensao> getResultadosBusca() {
//	     Collection<ParticipanteAcaoExtensao> pagina = new ArrayList<ParticipanteAcaoExtensao>();
//	     
//	     if (!ValidatorUtil.isEmpty(resultadosBusca)) {
//	      Iterator<ParticipanteAcaoExtensao> iterator = resultadosBusca.iterator();
//	      int k = 0;
//	      // avan�a
//	      while (k++ < getPaginacao().getPaginaAtual() * getTamanhoPagina() && iterator.hasNext()) 
//	       iterator.next();
//	      // monta a p�gina
//	      k = 0;
//	      while (k++ < getTamanhoPagina() && iterator.hasNext()) {
//		  ParticipanteAcaoExtensao participante = iterator.next();
//		      pagina.add(participante);
//	      }
//	     }
//	     return pagina;
//	 }
//	 
//	/**
//	 * Visualizar dados de um participante.
//	 * <br>
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/lista.jsp</li>
//	 * 		<li>sigaa.war/extensao/RelatorioCursoEvento/form.jsp</li>
//	 * 		<li>sigaa.war/extensao/RelatorioProjeto/form.jsp</li>
//	 * </ul>
//	 * 
//	 * @return tela com detalhes do participante selecionado.
//	 * @throws DAOException se ocorrer erro na busca do participante no banco. 
//	 */
//	public String view() throws DAOException {
//
//		int id = getParameterInt("id", 0);
//		obj = getGenericDAO().findByPrimaryKey(id, ParticipanteAcaoExtensao.class);
//
//		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_VIEW);
//	}
//
//	/**
//	 * Lista todos os participantes da a��o selecionada possibilitando altera��o
//	 * ou remo��o de qualquer participante.
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>N�o � chamado por JSPs.</li>
//	 * </ul>
//	 * 
//	 * @return Lista de participantes da a��o selecionada.
//	 * @throws ArqException se o usu�rio n�o for um coordenador de a��o.
//	 * @throws NegocioException se o usu�rio n�o for um coordenador de a��o.
//	 */
//	public String alterarParticipantes() throws ArqException, NegocioException {
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException(
//					"Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		carregarParticipantes(getParameterInt("id", 0));
//		setReadOnly(false);
//		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA);
//	}
//
//	@Override
//	public String getDirBase() {
//		return "/extensao/ParticipanteAcaoExtensao";
//	}
//
//	/**
//	 * Lista os tipos de participa��o compat�veis com a a��o do participante.
//	 * <br>
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 */
//	public Collection<SelectItem> getTiposParticipacaoCombo() {
//
//		try {
//
//			if ((obj != null) && (obj.getAcaoExtensao() != null)) {
//
//				List<TipoParticipacaoAcaoExtensao> lista = new ArrayList<TipoParticipacaoAcaoExtensao>();
//				
//				TipoParticipacaoAcaoExtensaoDao extDao = getDAO(TipoParticipacaoAcaoExtensaoDao.class);
//				
//				lista.addAll(extDao.findAllTipoParticipacaoByTipoAcaoExtensao(obj.getAcaoExtensao().getTipoAtividadeExtensao()));
//				lista.addAll(extDao.findAllTipoParticipacaoByTipoAcaoExtensao(null));
//				
//				return toSelectItems(lista, "id", "descricao");
//			}
//			return new ArrayList<SelectItem>();
//			
//		} catch (DAOException e) {
//			notifyError(e);
//			return null;
//		}
//	}
//	
//	/**
//	 * M�todo usado para preparar um movimento de altera��o de entidade.
//	 * <br>
//	 * M�todo chamado pala(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>N�o � chamado por JSPs.</li>
//	 * </ul>
//	 * 
//	 */
//	@Override
//	public void afterPreRemover() {
//		try {
//			prepareMovimento(ArqListaComando.ALTERAR);
//		} catch (ArqException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * M�todo chamado para remover uma entidade. N�o remove do banco, apenas
//	 * inativa o participante. Somente coordenadores podem realizar esta
//	 * opera��o.
//	 * 
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String remover() throws ArqException {
//
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		obj.setAtivo(false);
//		obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setObjMovimentado(obj);
//
//		if (obj.getId() == 0) {
//			addMensagemErro("N�o h� objeto informado para remo��o");
//			return null;
//		} else {
//
//			mov.setCodMovimento(ArqListaComando.ALTERAR);
//			try {
//				execute(mov, getCurrentRequest());
//				addMessage("Opera��o realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
//				afterCadastrar();
//				
//			} catch (NegocioException e) {
//				addMensagemErro(e.getMessage());
//				return forward(getFormPage());
//			}
//			
//			carregarParticipantes(obj.getAcaoExtensao().getId());
//			return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA);
//
//		}
//	}
//
//	/**
//	 * Retorna todas as atividades coordenadas pelo usu�rio atual que podem
//	 * receber cadastros de participantes.
//	 * <br>
//	 * M�todo chamado pela(s) seuginte(s) JSP(s):
//	 * <ul>
//	 * 		<li>N�o � chamado por JSPs.</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws DAOException
//	 * @throws SegurancaException
//	 */
//	public Collection<AtividadeExtensao> carregarAtividadeCoordenador() throws DAOException, SegurancaException {
//
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		Integer[] situacoes = (Integer[]) ArrayUtils.addAll(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO, TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
//		return getDAO(AtividadeExtensaoDao.class).findByCoordenadorAtivo(getUsuarioLogado().getServidor(), situacoes);		
//	}
//
//	/** 
//	 * Realiza o cadastro de discentes em lote pelo n�mero das matr�culas informadas.
//	 *
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp	  
//	 * 
//	 * @return
//	 * @throws SegurancaException
//	 * @throws ArqException
//	 * @throws NegocioException
//	 * 
//	 */	
//	public String cadastroColetivoDiscentes() throws SegurancaException, ArqException, NegocioException {
//		
//		ListaMensagens mensagens = new ListaMensagens();		
//		
//		// Validar campos selecionados
//		mensagens.addAll(validarFiltros().getErrorMessages());
//		
//		//Se for CURSO ou EVENTO(Limitar n�mero de participantes de acordo com o n�mero de vagas)
//		if (obj.getAcaoExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO 
//				|| obj.getAcaoExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO) {
//			
//			Integer numeroVagasCursoEvento = obj.getAcaoExtensao().getCursoEventoExtensao().getNumeroVagas();
//			Integer numeroParticipantesTentandoCadastrar = numerosMatricula.size();
//			Integer numeroParticipantesCadastrados = obj.getAcaoExtensao().getParticipantesNaoOrdenados().size();
//			
//			if ((numeroParticipantesTentandoCadastrar + numeroParticipantesCadastrados) > numeroVagasCursoEvento) 
//				mensagens.addErro("Esta a��o de extens�o pode ter no m�ximo " + numeroVagasCursoEvento + " participantes.");
//		}
//		if (!mensagens.isEmpty()) {
//			addMensagens(mensagens);
//			return null;
//		}
//		final boolean isCertificado = obj.isAutorizacaoCertificado();
//		final Integer frequencia = obj.getFrequencia();
//		final TipoParticipacaoAcaoExtensao categoria = obj.getTipoParticipacao();
//		
//		// buscar discentes por matricula
//		all = new ArrayList<ParticipanteAcaoExtensao>(numerosMatricula.size());
//		DiscenteDao dao = getDAO(DiscenteDao.class);
//		ParticipanteAcaoExtensao participante;
//		
//		for (Long mat : numerosMatricula) {
//			
//			participante = new ParticipanteAcaoExtensao();
//			Discente discente = dao.findByMatricula(mat);
//			
//			// verificar matricula inexistente
//			if (discente == null) {
//				addMensagemErro("O campo Matr�culas possui matr�cula(s) inv�lida");
//				return null;
//			}
//			participante.setDiscente(discente);
//			participante.setTipoParticipante(ParticipanteAcaoExtensao.DISCENTE_UFRN);
//			participante.setAutorizacaoCertificado(isCertificado);
//			participante.setFrequencia(frequencia);
//			participante.setTipoParticipacao(categoria);
//			participante.setAcaoExtensao(obj.getAcaoExtensao());
//			participante.setServidor(null);
//			participante.setEmail(discente.getPessoa().getEmail());
//			participante.setObservacaoCertificado(obj.getObservacaoCertificado());
//			
//			Validatable objValidavel = null;
//			objValidavel = participante;
//			
//			participante.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
//			participante.setDataCadastro(new Date());
//			participante.setAtivo(true);
//			
//			if (objValidavel != null) {
//				ListaMensagens lista = objValidavel.validate();
//				
//				if (lista != null && !lista.isEmpty()) 
//					mensagens.addAll(lista.getMensagens());
//			}
//			// Verificar duplicidade do participante na a��o
//			ParticipanteAcaoExtensaoValidator.validaNovoParticipante(participante, mensagens);
//			
//			if (!mensagens.isEmpty()) {
//				addMensagens(mensagens);
//				return null;
//			}
//			all.add(participante);
//		}
//		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PARTICIPANTES_EXTENSAO.getId());	
//		
//		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_PARTICIPANTES_EXTENSAO.getId())) 
//			return cancelar();
//
//		prepareMovimento(SigaaListaComando.CADASTRAR_PARTICIPANTES_EXTENSAO);
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setCodMovimento(SigaaListaComando.CADASTRAR_PARTICIPANTES_EXTENSAO);
//		mov.setColObjMovimentado(all);
//		
//		execute(mov);
//		addMessage("Opera��o realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
//		removeOperacaoAtiva();
//		afterCadastrar();
//		matriculas = "";
//		
//		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_FORM);
//	}
//	
//	/**
//     * Redireciona para tela de notifica��o para participantes
//     * <br />
//     * M�todo chamado pela(s) seguinte(s) JSP(s):
//     * <ul>
//     *  <li>igaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//     * </ul>
//	 * @return
//	 * @throws ArqException
//	 */
//	public String iniciarNotificacaoParticipantes() throws ArqException{
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		int idAcao = getParameterInt("id", 0);
//		acao = getGenericDAO().findByPrimaryKey(idAcao, AtividadeExtensao.class);
//
//		carregarParticipantes(idAcao);
//		obj.setAcaoExtensao(acao);
//		obj.getAcaoExtensao().setSituacaoProjeto(acao.getSituacaoProjeto());
//		setReadOnly(false);
//		prepareMovimento(ArqListaComando.CADASTRAR);
//		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_NOTIFICA);
//
//	}
//	
//	/**
//     * Usado para enviar e-mails para participantes 
//     * <br />
//     * M�todo chamado pela(s) seguinte(s) JSP(s):
//     * <ul>
//     *  <li>igaa.war/extensao/ParticipanteAcaoExtensao/notificacao_participante.jsp</li>
//     * </ul>
//     * @throws NegocioException 
//     * @throws ArqException 
//     * @throws SegurancaException 
//	 * @throws NegocioRemotoException 
//     * 
//     */
//    public String notificacaoParticipantes() throws SegurancaException, ArqException, NegocioException, NegocioRemotoException {
//    	
//    		Mensagem mensagem = new Mensagem();
//    		mensagem.setTitulo(acao.getTitulo());
//    		setMsgParticipantes(StringUtils.removerComentarios(getMsgParticipantes()));
//    		mensagem.setMensagem(getMsgParticipantes());
//    		
//    		List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
//    		
//    		for (ParticipanteAcaoExtensao part : acao.getParticipantesNaoOrdenados()) {			
//    			DestinatarioDTO dest = new DestinatarioDTO();
//    			dest.setEmail(part.getEmail());
//    			destinatarios.add(dest);
//    		}
//    		
//    		NotificacaoDTO not = new NotificacaoDTO();
//    		not.setEnviarEmail(true);
//    		not.setEnviarMensagem(false);
//    		not.setDestinatarios(destinatarios);
//    		not.setTitulo(mensagem.getTitulo());
//    		not.setMensagem(mensagem.getMensagem());
//    		not.setAutorizado(true);
//    		not.setContentType(MailBody.HTML);
//    		not.setNomeRemetente(getUsuarioLogado().getNome());
//    		
//    		
//    		// Preparar cadastro
//    		EnvioNotificacoesRemoteService enviador = getMBean("envioNotificacoesInvoker");
//    		enviador.enviar(DtoUtils.deUsuarioParaDTO(getUsuarioLogado()), not);
//    		
//    		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
//    		setMsgParticipantes("");
//    		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_ACOES_LISTA); 		
//    	
//    }
//    
//    
//	/**
//	 * Permite ao coordenador autorizar/negar a emiss�o de certificados para os participantes.
//	 * <br />
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp
//	 * 
//	 * @param event
//	 * @throws SegurancaException
//	 * @throws ArqException
//	 * @throws NegocioException
//	 */
//	public void autorizarCertificado(ActionEvent event) throws SegurancaException, ArqException, NegocioException {
//		
//		obj.setId(getParameterInt("id"));
//		populateObj(true);
//		
//		if (isEmpty(obj)) {
//			addMensagemAjax(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
//		}else {
//        		obj.setAutorizacaoCertificado(!obj.isAutorizacaoCertificado());
//        		
//        		// Persistir novo status
//        		prepareMovimento(ArqListaComando.ALTERAR);		
//        		MovimentoCadastro mov = new MovimentoCadastro();
//        		mov.setObjMovimentado(obj);
//        		mov.setCodMovimento(ArqListaComando.ALTERAR);
//        		execute(mov);
//        		
//        		int idx = acao.getParticipantesNaoOrdenados().indexOf(obj);
//        		if (idx >= 0) {
//        		    acao.getParticipantesNaoOrdenados().get(idx).setAutorizacaoCertificado(obj.isAutorizacaoCertificado());
//        		}        		
//        		obj = new ParticipanteAcaoExtensao();
//		}
//	}
//	
//	/**
//	 * Valida��o das matr�culas informadas no cadastro coletivo de discentes. 
//	 */
//	private ListaMensagens validarFiltros() {
//		
//		ListaMensagens erros = new ListaMensagens();
//		
//		valida: {
//			ValidatorUtil.validateRequired(matriculas, "Matr�culas", erros);
//			if (!erros.isEmpty()) 
//				break valida;
//			
//			String[] arrayMatriculas = matriculas.split("[\\D]+");
//
//			numerosMatricula = new ArrayList<Long>();
//			if (arrayMatriculas.length != 0) {
//				try {
//					for (String mat : arrayMatriculas) 
//						numerosMatricula.add(Long.valueOf(mat));
//					
//				} catch (NumberFormatException e) {
//					addMensagemErro("Erro no processamento das matr�culas informadas.");
//				}
//			}
//			if (numerosMatricula.isEmpty()) 
//				erros.addErro("� necess�rio informar alguma matr�cula v�lida");
//			
//			// verifica a ocorr�ncia de matr�culas duplicadas
//			duplicada: {
//				for (Long mat : numerosMatricula) {
//					int cont = 0;
//					for (Long nums : numerosMatricula)
//						if (nums.equals(mat) && ++cont == 2) {
//							erros.addErro("N�o pode existir matr�cula duplicada");
//							break duplicada;
//						}
//				}
//			}
//		}
//		return erros;
//	}
//	
//	/**
//	 * Utilizado no cadastro de participantes de a��es de extens�o.
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp
//	 * 
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 * @throws NegocioException 
//	 */
//	public String atualizarParticipacoes() throws ArqException, NegocioException{
//		
//	    	final List<ParticipanteAcaoExtensao> lista = new ArrayList<ParticipanteAcaoExtensao>(); 
//		for (ParticipanteAcaoExtensao part : acao.getParticipantesNaoOrdenados()) {
//			if (part.isSelecionado()) {
//			    if (part.getFrequencia() == null) {
//				addMensagemErro("Nenhuma frequencia foi informada para o participante: " + part.getNome());
//				obj = new ParticipanteAcaoExtensao();
//				obj.setAcaoExtensao(acao);
//				obj.getAcaoExtensao().setSituacaoProjeto(acao.getSituacaoProjeto());
//				setConfirmButton("Cadastrar");
//				return null;
//			    }
//			    lista.add(part);
//			}			
//		}
//		
//		if (ValidatorUtil.isEmpty(lista)) {
//		    addMensagemErro("Selecione algum participante da a��o para realizar as altera��es.");
//		    return null;
//		}
//		
//		prepareMovimento(SigaaListaComando.ALTERAR_PARTICIPACOES_EXTENSAO);
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setCodMovimento(SigaaListaComando.ALTERAR_PARTICIPACOES_EXTENSAO);
//		mov.setColObjMovimentado(lista);		
//		execute(mov);
//		
//		addMensagem(OPERACAO_SUCESSO);
//		carregarParticipantes(acao.getId());
//		return redirectMesmaPagina();
//	}
//	
//	
//	/**
//	 * Utilizado no cadastro de participantes de a��es de extens�o.
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp
//	 * 
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 * @throws NegocioException 
//	 */
//	public String atualizarParticipacoesSubAtividade() throws ArqException, NegocioException{
//		
//	    	final List<ParticipanteAcaoExtensao> lista = new ArrayList<ParticipanteAcaoExtensao>(); 
//		for (ParticipanteAcaoExtensao part : subAcao.getParticipantesNaoOrdenados()) {
//			if (part.isSelecionado()) {
//			    if (part.getFrequencia() == null) {
//				addMensagemErro("Nenhuma frequencia foi informada para o participante: " + part.getNome());
//				obj = new ParticipanteAcaoExtensao();
//				obj.setSubAtividade(subAcao);
//				obj.getSubAtividade().getAtividade().setSituacaoProjeto(subAcao.getAtividade().getSituacaoProjeto());
//				setConfirmButton("Cadastrar");
//				return null;
//			    }
//			    lista.add(part);
//			}			
//		}
//		
//		if (ValidatorUtil.isEmpty(lista)) {
//		    addMensagemErro("Selecione algum participante da a��o para realizar as altera��es.");
//		    return null;
//		}
//		
//		prepareMovimento(SigaaListaComando.ALTERAR_PARTICIPACOES_EXTENSAO);
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setCodMovimento(SigaaListaComando.ALTERAR_PARTICIPACOES_EXTENSAO);
//		mov.setColObjMovimentado(lista);		
//		execute(mov);
//		
//		addMensagem(OPERACAO_SUCESSO);
//		carregarParticipantesSubAcao(subAcao.getId());
//		return redirectMesmaPagina();
//	}
//	
//	
//	/**
//	 * Permite o reenvio do c�digo e senha para o participante que perdeu.
//	 * Somento o coordenador da a��o pode gerar o c�digo e senha de acesso do participante da a��o.
//	 * 
//	 * JSP: /sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp
//	 *   
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	public String reenviarCodigoSenhaAcesso() throws NegocioException, ArqException {
//	    	int id = getParameterInt("id", 0);
//	    	InscricaoAtividadeParticipante iap = getDAO(InscricaoAtividadeParticipanteDao.class).findByParticipanteAcaoExtensao(id);
//	    	if (iap != null) {
//        		MovimentoCadastro mov = new MovimentoCadastro();
//        		prepareMovimento(SigaaListaComando.REENVIAR_SENHA_PARTICIPANTE_EXTENSAO);
//        		mov.setCodMovimento(SigaaListaComando.REENVIAR_SENHA_PARTICIPANTE_EXTENSAO);
//        		mov.setObjMovimentado(iap);
//        		execute(mov);
//        		
//        		
//        		// IMPORTANTE: O email do participante pode ser diferente do email que ele fez a inscri��o que � o email para qual a senha ser� enviada.
//        		// � muito importante avisar isso para o usu�rio !!!!!!!!!!!!!!!!!!!!!
//        		//
//        		// OBSERVA��O 2: As mensagem s�o mostrada na ordem inversa, ent�o a de aten��o vem primeiro !!!!
//        		
//        		addMensagemInformation("ATEN��O: Para alterar esse email, use a op��o de \"Alterar inscria��es <i>on-line</i>\" .");
//        		addMensagemInformation("Senha reenviar para o email com o qual o participantes realizou a inscri��o: "+iap.getEmail());
//        		
//        		
//	    	}else {
//	    	    addMensagemErro("Este participante n�o realizou sua inscri��o atrav�s do portal p�blico do "+RepositorioDadosInstitucionais.get("siglaSigaa")+".");
//	    	}
//		return null;
//	}
//
//	/**
//	 * M�todo que redireciona para tela do relat�rio com os dados dos participantes
//	 * <br />
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/extensao/ParticipanteAcaoExtensao/lista.jsp</li>
//	 * </ul>
//	 * @return
//	 */
//	public String gerarRelatorioDadosParticipanteExtensao(){
//		if(resultadosBusca.size() > 0){
//			return redirect("/sigaa/extensao/ParticipanteAcaoExtensao/lista_participantes_acao_rel.jsf");
//		}else{
//			addMensagemErro("N�o h� participantes nesta A��o.");
//			return null;
//		}
//		
//	}
//	
//	/**
//	 * Exporta as notas em formato de planilha xls
//	 * <br />
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String exportarPlanilhaContatos() throws ArqException{
//		try{
//			
//			clearMensagens();
//			carregarParticipantes(getParameterInt("id", 0));
//			
//			 //se participantes � nulo, nao redireciona para pagina de listagem
//		    if(acao.getParticipantesNaoOrdenados() == null || acao.getParticipantesNaoOrdenados().size() <= 0){
//		    	addMensagemErro("N�o h� participantes cadastrados para esta a��o de extens�o.");
//		    	return null;
//		    }
//		    
//			ContatosExcelParticipanteAcaoExtensaoMBean contatosSheet = new ContatosExcelParticipanteAcaoExtensaoMBean(acao);
//			contatosSheet.buildSheet();
//		}catch (NegocioException e){
//			addMensagens(e.getListaMensagens());
//		}catch (Exception e) {
//			throw new ArqException(e);
//		}
//		return null;
//	}
//	
//	
//	/**
//	 * M�todo utilizado para iniciar o gerenciamento dos participantes em subAtividades
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String iniciaGerenciarParticipantesSubAtividades() throws ArqException{
//	    if (!getAcessoMenu().isCoordenadorExtensao()) {
//		throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//	    }
//	    clearMensagens();
//	    obj = new ParticipanteAcaoExtensao();
//	    int idSubAcao = getParameterInt("id", 0);
//	    carregarParticipantesSubAcao(idSubAcao);
//	    
//	    //se participantes � nulo, nao redireciona para pagina de listagem
//	    if(subAcao.getParticipantesNaoOrdenados() == null || subAcao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("N�o h� participantes cadastrados para esta a��o de extens�o.");
//	    	return null;
//	    }
//	    
//	 // Verifica se � nulo para carregar municipios
//	    obj.setSubAtividade(subAcao);
//    	if (obj.getUnidadeFederativa() == null){ 
//    		obj.setUnidadeFederativa(buscaParticipante.getUnidadeFederativa());
//    	}
//	    carregarMunicipios();
//	    
//	    
//	    obj.getSubAtividade().getAtividade().setSituacaoProjeto(subAcao.getAtividade().getSituacaoProjeto());
//	    setConfirmButton("Cadastrar");
//	    setReadOnly(false);
//	    prepareMovimento(ArqListaComando.ALTERAR);
//	    return forward(ConstantesNavegacao.PARTICIPANTES_SUB_EXTENSAO_LISTA);
//	    
//	}
//	
//	/**
//	 * M�todo utilizado para setar as atividades do coordenador
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>N�o � chamado por JSP(s)</li>
//	 * </ul>
//	 * 
//	 * @param atividadesCoordanador the atividadesCoordanador to set
//	 */
//	public void setAtividadesCoordanador(Collection<AtividadeExtensao> atividadesCoordenador) {
//		this.atividadesCoordenador = atividadesCoordenador;
//	}
//
//	/**
//	 * M�todo utilizado para informar as atividades do coordenador
//	 * <br>
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return the atividadesCoordanador
//	 */
//	public Collection<AtividadeExtensao> getAtividadesCoordenador() {
//		return atividadesCoordenador;
//	}
//	
//	public AtividadeExtensao getAcao() {
//		return acao;
//	}
//	
//	public void setAcao(AtividadeExtensao acao) {
//		this.acao = acao;
//	}
//	
//	public boolean isContinuarCadastroRelatorio() {
//		return continuarCadastroRelatorio;
//	}
//	
//	public void setContinuarCadastroRelatorio(boolean continuarCadastroRelatorio) {
//		this.continuarCadastroRelatorio = continuarCadastroRelatorio;
//	}
//	
//	public String getMatriculas() {
//		return matriculas;
//	}
//	
//	public void setMatriculas(String matriculas) {
//		this.matriculas = matriculas;
//	}
//
//	public String getMsgParticipantes() {
//		return msgParticipantes;
//	}
//
//	public void setMsgParticipantes(String msgParticipantes) {
//		this.msgParticipantes = msgParticipantes;
//	}
//
//	public boolean isContinuarGerenciamento() {
//		return continuarGerenciamento;
//	}
//
//	public void setContinuarGerenciamento(boolean continuarGerenciamento) {
//		this.continuarGerenciamento = continuarGerenciamento;
//	}
//	
//	
//	/**
//	 * Carrega os munic�pios de uma unidade federativa.
//	 * <br />
//	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/form_inscricao.jsp</li>
//	 * </ul>
//	 * 
//	 * @throws DAOException
//	 */
//	public void carregarMunicipios() throws DAOException {		
//		MunicipioDao dao = getDAO(MunicipioDao.class);
//		UnidadeFederativa uf = dao.findByPrimaryKey(obj.getUnidadeFederativa().getId(), UnidadeFederativa.class);
//		Collection<Municipio> municipios = dao.findByUF(uf.getId());
//		municipiosEndereco = new ArrayList<SelectItem>();
//		if (uf.getCapital() != null) {
//			municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));		
//		}
//		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
//	}
//	
//	public Collection<SelectItem> getMunicipiosEndereco() {
//		return municipiosEndereco;
//	}
//
//	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
//		this.municipiosEndereco = municipiosEndereco;
//	}
//	
//	
//	/**
//	 * Filtra os participantes da a��o de extens�o por campos como Nome, Institui��o ou Munic�pio e gera o relat�rio
//	 * <br>
//	 * M�todo chamadado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException
//	 * @throws NegocioException
//	 */
//	public String buscarParticipantesSubAcao() throws ArqException, NegocioException {
//
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");
//		}
//		clearMensagens();
//		
//		ListaMensagens lista = new ListaMensagens();
//		
//		buscaParticipante = new ParticipanteAcaoExtensao();
//		buscaParticipante.setDiscente(null);
//		buscaParticipante.setServidor(null);
//		buscaParticipante.setSubAtividade(subAcao);
//		
//		if ( checkBuscaNome ){
//			if ( isEmpty(buscaNome) )
//				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
//			else
//				buscaParticipante.setNome(buscaNome);
//		} else
//			setBuscaNome(null);
//			
//		if(checkBuscaMunicipio){
//			ValidatorUtil.validateRequired(obj.getUnidadeFederativa(), "UF", lista);
//			ValidatorUtil.validateRequired(obj.getMunicipio(), "Munic�pio", lista);
//			buscaParticipante.setUnidadeFederativa(obj.getUnidadeFederativa());
//			buscaParticipante.setMunicipio(obj.getMunicipio());
//		} else
//		{
//			buscaParticipante.setUnidadeFederativa(new UnidadeFederativa(0));
//			buscaParticipante.setMunicipio(new Municipio(0));			
//		}
//		
//		if ( hasErrors() ) {
//			resultadosBusca = new ArrayList<ParticipanteAcaoExtensao>();
//			return null;
//		}
//
//		carregarParticipantesFiltroSubAcao(subAcao.getId());
//		//carregarParticipantesFiltro(getParameterInt("id", 0));
//		
//		 //se participantes � nulo, nao redireciona para pagina de listagem
//	    if(subAcao.getParticipantesNaoOrdenados() == null || subAcao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("N�o h� participantes cadastrados para esta a��o de extens�o.");
//	    	return null;
//	    }
//
//		if ( hasErrors() ) {
//			resultadosBusca = new ArrayList<ParticipanteAcaoExtensao>();
//			return null;
//		}
//	
//		
//			 // Verifica se algum dos objetos � nulo para carregar municipios
//		if(obj == null) 
//		   	obj = buscaParticipante;		    
//		else {		    	
//			if (obj.getUnidadeFederativa() == null){		    
//				obj.setUnidadeFederativa(buscaParticipante.getUnidadeFederativa());		    	
//				obj.setMunicipio(buscaParticipante.getMunicipio());		    	
//			}		    
//		}
//		    
//		carregarMunicipios();		
//		return forward(ConstantesNavegacao.PARTICIPANTES_SUB_EXTENSAO_LISTA);
//		
//		
//		
//	    
//	}
//	
//
//
//	public boolean isCheckBuscaNome() {
//		return checkBuscaNome;
//	}
//
//	public void setCheckBuscaNome(boolean checkBuscaNome) {
//		this.checkBuscaNome = checkBuscaNome;
//	}
//
//	public boolean isCheckBuscaInstituicao() {
//		return checkBuscaInstituicao;
//	}
//
//	public void setCheckBuscaInstituicao(boolean checkBuscaInstituicao) {
//		this.checkBuscaInstituicao = checkBuscaInstituicao;
//	}
//
//	public boolean isCheckBuscaMunicipio() {
//		return checkBuscaMunicipio;
//	}
//
//	public void setCheckBuscaMunicipio(boolean checkBuscaMunicipio) {
//		this.checkBuscaMunicipio = checkBuscaMunicipio;
//	}
//
//	public boolean isCheckGerarRelatorio() {
//		return checkGerarRelatorio;
//	}
//
//	public void setCheckGerarRelatorio(boolean checkGerarRelatorio) {
//		this.checkGerarRelatorio = checkGerarRelatorio;
//	}
//
//	public ParticipanteAcaoExtensao getBuscaParticipante() {
//		return buscaParticipante;
//	}
//
//	public void setBuscaParticipante(ParticipanteAcaoExtensao buscaParticipante) {
//		this.buscaParticipante = buscaParticipante;
//	}
//	
//	public String getBuscaNome() {
//		return buscaNome;
//	}
//
//	public void setBuscaNome(String buscaNome) {
//		this.buscaNome = buscaNome;
//	}
//
//	public String getBuscaInstituicao() {
//		return buscaInstituicao;
//	}
//
//	public void setBuscaInstituicao(String buscaInstituicao) {
//		this.buscaInstituicao = buscaInstituicao;
//	}
//
//	public Collection<AtividadeExtensao> getSubAtividadesCoordenador() {
//		return subAtividadesCoordenador;
//	}
//
//	public void setSubAtividadesCoordenador(
//			Collection<AtividadeExtensao> subAtividadesCoordenador) {
//		this.subAtividadesCoordenador = subAtividadesCoordenador;
//	}
//
//	public SubAtividadeExtensao getSubAcao() {
//		return subAcao;
//	}
//
//	public void setSubAcao(SubAtividadeExtensao subAcao) {
//		this.subAcao = subAcao;
//	}
//
//	public Collection<AtividadeExtensao> getAtividadesGenrenciador() {
//		return atividadesGenrenciador;
//	}
//
//	public void setAtividadesGenrenciador(
//			Collection<AtividadeExtensao> atividadesGenrenciador) {
//		this.atividadesGenrenciador = atividadesGenrenciador;
//	}
	
	

	

}
