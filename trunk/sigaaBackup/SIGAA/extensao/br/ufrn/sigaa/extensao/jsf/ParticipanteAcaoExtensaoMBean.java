/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * MBean responsável por controlar o cadastro de participantes de ações de
 * extensão. <br/>
 * 
 * O cadastro destes participantes só pode ser realizado pelo coordenador da ação
 * através do menu no portal docente.
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
//	/** Usado como auxiliar de tela de busca para gerar relatório*/
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
//	/** Atributo utilizado para representar a Atividade de Extensão */
//	private AtividadeExtensao acao = new AtividadeExtensao();
//	
//	/** Atributo utilizado para representar a Sub Atividade de Extensão */
//	private SubAtividadeExtensao subAcao = new SubAtividadeExtensao();
//
//	/** Atributo utilizado para armazenar informaçoes obtidas através de uma consulta ao banco */
//	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
//	
//	/** Usado como auxiliar para a continuação do cadastro de relatório. */
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
//	/** Usado para armazenar informações de consulta ao banco */
//	private Collection<AtividadeExtensao> atividadesCoordenador;
//	
//	/** Usado para armazenar informações de consulta ao banco */
//	private Collection<AtividadeExtensao> subAtividadesCoordenador;
//	
//	/** Usado para armazenar informações de consulta ao banco */
//	private Collection<AtividadeExtensao> atividadesGenrenciador;
//	
//	/** Usada para o envio de mensagen para participantes */
//	private String msgParticipantes = new String();
//	
//
//
//	/** Atributo utilizado para verificar se continua-se ou não o gerenciamento */
//	private boolean continuarGerenciamento = false;

//	/**
//	 * Construtor padrão.
//	 */
//	public ParticipanteAcaoExtensaoMBean() {
//		obj = new ParticipanteAcaoExtensao();
//	}

//	/**
//	 * Direciona o usuário para uma tela onde serão listadas todas as ações de
//	 * extensão onde ele é coordenador.
//	 * 
//	 * 
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/menu_ta.jsp
//	 * sigaa.war/extensao/menu.jsp
//	 * sigaa.war/portais/docente/menu_docente.jsp
//	 * 
//	 * @return Lista de ações coordenadas pelo usuário logado.
//	 * @throws ArqException 
//	 */
//	public String listarAcoesExtensao() throws ArqException {
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
//		}
//		if(isContinuarGerenciamento()){
//			carregarParticipantes(obj.getAcaoExtensao().getId());
//			setContinuarGerenciamento(false);
//			return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_LISTA);
//		}
//		
//		//retira o botão de retornar ao relatório(utilizado no cadastro de relatórios parciais e finais)
//		//a partir do cadastro de relatórios finais e parciais o coordenador pode atualizar a lista de participantes.
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
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/portais/docente/menu_docente.jsp
//	 * 
//	 * @return Lista de ações coordenadas pelo usuário logado.
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
//				addMensagemWarning("Não foi encontrada nenhuma ação ou mini ação na qual seja possível o usuário logado gerenciar participantes.");
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
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSPs</li>
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
//	 * Método utilizado para realizar ações depois de atulizar.
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>Não é chamado por JSPs</li>
//	 * </ul>
//	 * 
//	 */
//	@Override
//	public void afterAtualizar() throws ArqException {
//		// carregando para evitar erro de lazy na validação
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
//			carregarMunicipios(); // carrega os municípios da UF já salva para a participante.
//		}
//		
//	}

//	/**
//	 * Realiza algumas validações antes de passar o controle do cadastro para o
//	 * cadastrar de {@link SigaaAbstractController#cadastrar()}.
//	 * 
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
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
//					addMensagemErro("Discente: Campo obrigatório não informado.");
//					if (obj.getTipoParticipacao().getId() == 0) {
//						addMensagemErro("Categoria: Campo obrigatório não informado.");
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
//					addMensagemErro("Servidor: Campo obrigatório não informado.");
//					if (obj.getTipoParticipacao().getId() == 0) {
//						addMensagemErro("Categoria: Campo obrigatório não informado.");
//					}
//					return null;
//				}
//			}
//			if (obj.getTipoParticipante() == ParticipanteAcaoExtensao.DISCENTES){
//				return cadastroColetivoDiscentes();
//			}
//		}
//		ListaMensagens mensagens = new ListaMensagens();
//		// validações
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
//		// Tratamento do campo de Observação no Certificado antes de cadastrar
//		// Tratamento para que ????? Qual o motivo deixar a primeira letra em minúsculo ?????
//		// Esse tipo de comentário não serve para nada !!!
//		
//		/* Converte a primeira letra da observação no certificado para minúsculo antes de cadastrar 
//		 * e retira os espaços em branco antes e depois da observação.  Isso provavelmente porque 
//		 * a observação vai ser concatenada com o texto do certificado e não pode começar com letra maiúscula.
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
//	 * Carrega as informações do participante da ação a partir do discente informado no formulário de cadastro
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
//	 * Antes de cadastrar e depois da validação, faz mais alguns ajustes no novo
//	 * participante da ação.
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>Não é chamado por JSPs</li>
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
//	 * Após o cadastramento, configura o Mbean para permitir um novo cadastro
//	 * logo em seguida, facilitando o cadastro em sequência de novos
//	 * participantes.
//	 * 
//	 * Não é chamado por JSPs.
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
//	 * Método utilizado para iniciar o gerenciamento dos participantes
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String iniciaGerenciarParticipantes() throws ArqException{
//	    if (!getAcessoMenu().isCoordenadorExtensao()) {
//		throw new SegurancaException("Usuário não autorizado a realizar esta operação");
//	    }
//	    clearMensagens();
//	    obj = new ParticipanteAcaoExtensao();
//	    int idAcao = getParameterInt("id", 0);
//	    carregarParticipantes(idAcao);
//	    
//	    //se participantes é nulo, nao redireciona para pagina de listagem
//	    if(acao.getParticipantesNaoOrdenados() == null || acao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("Não há participantes cadastrados para esta ação de extensão.");
//	    	return null;
//	    }
//	    
//	 // Verifica se é nulo para carregar municipios
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
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String iniciarCadastroParticipante() throws ArqException{
//	    if (!getAcessoMenu().isCoordenadorExtensao()) {
//		throw new SegurancaException("Usuário não autorizado a realizar esta operação");
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
//	    	addMensagemErro("Não é possível cadastrar novos participantes, pois a ação possui situação concluída");
//			return null;
//	    }
//	    	
//	}
//
//	/**
//	 * Prepara o MBean e o processador que irá realizar o cadastro do
//	 * participante. Valida para somente coordenadores de ações realizarem esta
//	 * operação. Redireciona o usuário para o form de cadastro.
//	 * <br>
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
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
//			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
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
//			addMensagemErro("Não é possível cadastrar novos participantes, pois a ação possui situação concluída");
//			return null;
//		}
//	}
//
//	/**
//	 * Método utilizado para redicionar para a página do formulário
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Seleciona a ação de extensão e lista todos os participantes para
//	 * impressão de lista de frequência.
//	 * <br>
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
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
//			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
//		}
//		clearMensagens();
//		
//		carregarParticipantes(getParameterInt("id", 0));
//		
//		 //se participantes é nulo, nao redireciona para pagina de listagem
//	    if(acao.getParticipantesNaoOrdenados() == null || acao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("Não há participantes cadastrados para esta ação de extensão.");
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
//	 * Filtra os participantes da ação de extensão por campos como Nome, Instituição ou Município e gera o relatório
//	 * <br>
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
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
//			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
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
//			ValidatorUtil.validateRequired(obj.getMunicipio(), "Município", lista);
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
//		 //se participantes é nulo, nao redireciona para pagina de listagem
//	    if(acao.getParticipantesNaoOrdenados() == null || acao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("Não há participantes cadastrados para esta ação de extensão.");
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
//			 // Verifica se algum dos objetos é nulo para carregar municipios
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
//	 * da ação com id correspondente ao parâmetro passado.
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
//	 * da subAção com id correspondente ao parâmetro passado.
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
//	 * da ação com id correspondente ao parâmetro passado, filtrados pela busca.
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
//	 * da SubAção com id correspondente ao parâmetro passado, filtrados pela busca.
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
//	 * Método utilizado para informar os resultados da busca
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	      // avança
//	      while (k++ < getPaginacao().getPaginaAtual() * getTamanhoPagina() && iterator.hasNext()) 
//	       iterator.next();
//	      // monta a página
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
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
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
//	 * Lista todos os participantes da ação selecionada possibilitando alteração
//	 * ou remoção de qualquer participante.
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>Não é chamado por JSPs.</li>
//	 * </ul>
//	 * 
//	 * @return Lista de participantes da ação selecionada.
//	 * @throws ArqException se o usuário não for um coordenador de ação.
//	 * @throws NegocioException se o usuário não for um coordenador de ação.
//	 */
//	public String alterarParticipantes() throws ArqException, NegocioException {
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException(
//					"Usuário não autorizado a realizar esta operação");
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
//	 * Lista os tipos de participação compatíveis com a ação do participante.
//	 * <br>
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
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
//	 * Método usado para preparar um movimento de alteração de entidade.
//	 * <br>
//	 * Método chamado pala(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>Não é chamado por JSPs.</li>
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
//	 * Método chamado para remover uma entidade. Não remove do banco, apenas
//	 * inativa o participante. Somente coordenadores podem realizar esta
//	 * operação.
//	 * 
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String remover() throws ArqException {
//
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
//		}
//		obj.setAtivo(false);
//		obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setObjMovimentado(obj);
//
//		if (obj.getId() == 0) {
//			addMensagemErro("Não há objeto informado para remoção");
//			return null;
//		} else {
//
//			mov.setCodMovimento(ArqListaComando.ALTERAR);
//			try {
//				execute(mov, getCurrentRequest());
//				addMessage("Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
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
//	 * Retorna todas as atividades coordenadas pelo usuário atual que podem
//	 * receber cadastros de participantes.
//	 * <br>
//	 * Método chamado pela(s) seuginte(s) JSP(s):
//	 * <ul>
//	 * 		<li>Não é chamado por JSPs.</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws DAOException
//	 * @throws SegurancaException
//	 */
//	public Collection<AtividadeExtensao> carregarAtividadeCoordenador() throws DAOException, SegurancaException {
//
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
//		}
//		Integer[] situacoes = (Integer[]) ArrayUtils.addAll(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO, TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
//		return getDAO(AtividadeExtensaoDao.class).findByCoordenadorAtivo(getUsuarioLogado().getServidor(), situacoes);		
//	}
//
//	/** 
//	 * Realiza o cadastro de discentes em lote pelo número das matrículas informadas.
//	 *
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//		//Se for CURSO ou EVENTO(Limitar número de participantes de acordo com o número de vagas)
//		if (obj.getAcaoExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO 
//				|| obj.getAcaoExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO) {
//			
//			Integer numeroVagasCursoEvento = obj.getAcaoExtensao().getCursoEventoExtensao().getNumeroVagas();
//			Integer numeroParticipantesTentandoCadastrar = numerosMatricula.size();
//			Integer numeroParticipantesCadastrados = obj.getAcaoExtensao().getParticipantesNaoOrdenados().size();
//			
//			if ((numeroParticipantesTentandoCadastrar + numeroParticipantesCadastrados) > numeroVagasCursoEvento) 
//				mensagens.addErro("Esta ação de extensão pode ter no máximo " + numeroVagasCursoEvento + " participantes.");
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
//				addMensagemErro("O campo Matrículas possui matrícula(s) inválida");
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
//			// Verificar duplicidade do participante na ação
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
//		addMessage("Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
//		removeOperacaoAtiva();
//		afterCadastrar();
//		matriculas = "";
//		
//		return forward(ConstantesNavegacao.PARTICIPANTESEXTENSAO_FORM);
//	}
//	
//	/**
//     * Redireciona para tela de notificação para participantes
//     * <br />
//     * Método chamado pela(s) seguinte(s) JSP(s):
//     * <ul>
//     *  <li>igaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//     * </ul>
//	 * @return
//	 * @throws ArqException
//	 */
//	public String iniciarNotificacaoParticipantes() throws ArqException{
//		if (!getAcessoMenu().isCoordenadorExtensao()) {
//			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
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
//     * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Permite ao coordenador autorizar/negar a emissão de certificados para os participantes.
//	 * <br />
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
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
//	 * Validação das matrículas informadas no cadastro coletivo de discentes. 
//	 */
//	private ListaMensagens validarFiltros() {
//		
//		ListaMensagens erros = new ListaMensagens();
//		
//		valida: {
//			ValidatorUtil.validateRequired(matriculas, "Matrículas", erros);
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
//					addMensagemErro("Erro no processamento das matrículas informadas.");
//				}
//			}
//			if (numerosMatricula.isEmpty()) 
//				erros.addErro("É necessário informar alguma matrícula válida");
//			
//			// verifica a ocorrência de matrículas duplicadas
//			duplicada: {
//				for (Long mat : numerosMatricula) {
//					int cont = 0;
//					for (Long nums : numerosMatricula)
//						if (nums.equals(mat) && ++cont == 2) {
//							erros.addErro("Não pode existir matrícula duplicada");
//							break duplicada;
//						}
//				}
//			}
//		}
//		return erros;
//	}
//	
//	/**
//	 * Utilizado no cadastro de participantes de ações de extensão.
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
//		    addMensagemErro("Selecione algum participante da ação para realizar as alterações.");
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
//	 * Utilizado no cadastro de participantes de ações de extensão.
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
//		    addMensagemErro("Selecione algum participante da ação para realizar as alterações.");
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
//	 * Permite o reenvio do código e senha para o participante que perdeu.
//	 * Somento o coordenador da ação pode gerar o código e senha de acesso do participante da ação.
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
//        		// IMPORTANTE: O email do participante pode ser diferente do email que ele fez a inscrição que é o email para qual a senha será enviada.
//        		// é muito importante avisar isso para o usuário !!!!!!!!!!!!!!!!!!!!!
//        		//
//        		// OBSERVAÇÂO 2: As mensagem são mostrada na ordem inversa, então a de atenção vem primeiro !!!!
//        		
//        		addMensagemInformation("ATENÇÃO: Para alterar esse email, use a opção de \"Alterar inscriações <i>on-line</i>\" .");
//        		addMensagemInformation("Senha reenviar para o email com o qual o participantes realizou a inscrição: "+iap.getEmail());
//        		
//        		
//	    	}else {
//	    	    addMensagemErro("Este participante não realizou sua inscrição através do portal público do "+RepositorioDadosInstitucionais.get("siglaSigaa")+".");
//	    	}
//		return null;
//	}
//
//	/**
//	 * Método que redireciona para tela do relatório com os dados dos participantes
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/extensao/ParticipanteAcaoExtensao/lista.jsp</li>
//	 * </ul>
//	 * @return
//	 */
//	public String gerarRelatorioDadosParticipanteExtensao(){
//		if(resultadosBusca.size() > 0){
//			return redirect("/sigaa/extensao/ParticipanteAcaoExtensao/lista_participantes_acao_rel.jsf");
//		}else{
//			addMensagemErro("Não há participantes nesta Ação.");
//			return null;
//		}
//		
//	}
//	
//	/**
//	 * Exporta as notas em formato de planilha xls
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//			 //se participantes é nulo, nao redireciona para pagina de listagem
//		    if(acao.getParticipantesNaoOrdenados() == null || acao.getParticipantesNaoOrdenados().size() <= 0){
//		    	addMensagemErro("Não há participantes cadastrados para esta ação de extensão.");
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
//	 * Método utilizado para iniciar o gerenciamento dos participantes em subAtividades
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String iniciaGerenciarParticipantesSubAtividades() throws ArqException{
//	    if (!getAcessoMenu().isCoordenadorExtensao()) {
//		throw new SegurancaException("Usuário não autorizado a realizar esta operação");
//	    }
//	    clearMensagens();
//	    obj = new ParticipanteAcaoExtensao();
//	    int idSubAcao = getParameterInt("id", 0);
//	    carregarParticipantesSubAcao(idSubAcao);
//	    
//	    //se participantes é nulo, nao redireciona para pagina de listagem
//	    if(subAcao.getParticipantesNaoOrdenados() == null || subAcao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("Não há participantes cadastrados para esta ação de extensão.");
//	    	return null;
//	    }
//	    
//	 // Verifica se é nulo para carregar municipios
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
//	 * Método utilizado para setar as atividades do coordenador
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>Não é chamado por JSP(s)</li>
//	 * </ul>
//	 * 
//	 * @param atividadesCoordanador the atividadesCoordanador to set
//	 */
//	public void setAtividadesCoordanador(Collection<AtividadeExtensao> atividadesCoordenador) {
//		this.atividadesCoordenador = atividadesCoordenador;
//	}
//
//	/**
//	 * Método utilizado para informar as atividades do coordenador
//	 * <br>
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Carrega os municípios de uma unidade federativa.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
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
//	 * Filtra os participantes da ação de extensão por campos como Nome, Instituição ou Município e gera o relatório
//	 * <br>
//	 * Método chamadado pela(s) seguinte(s) JSP(s):
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
//			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
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
//			ValidatorUtil.validateRequired(obj.getMunicipio(), "Município", lista);
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
//		 //se participantes é nulo, nao redireciona para pagina de listagem
//	    if(subAcao.getParticipantesNaoOrdenados() == null || subAcao.getParticipantesNaoOrdenados().size() <= 0){
//	    	addMensagemErro("Não há participantes cadastrados para esta ação de extensão.");
//	    	return null;
//	    }
//
//		if ( hasErrors() ) {
//			resultadosBusca = new ArrayList<ParticipanteAcaoExtensao>();
//			return null;
//		}
//	
//		
//			 // Verifica se algum dos objetos é nulo para carregar municipios
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
