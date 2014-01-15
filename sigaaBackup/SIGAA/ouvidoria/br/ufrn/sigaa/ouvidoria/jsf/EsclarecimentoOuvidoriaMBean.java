package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dao.AcompanhamentoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.DelegacaoUsuarioRespostaDao;
import br.ufrn.sigaa.ouvidoria.dao.HistoricoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.InteressadoNaoAutenticadoDao;
import br.ufrn.sigaa.ouvidoria.dao.ManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;

/**
 * Controller responsável pelas operações de solicitação e resposta de esclarecimentos por parte da ouvidoria.
 * 
 * @author Diego Jácome
 *
 */
@Component(value="esclarecimentoOuvidoria") @Scope(value="request")
public class EsclarecimentoOuvidoriaMBean extends AnaliseManifestacaoAbstractController {

	/** Armazena as manifestações copiadas para a pessoa logada. */
	private Collection<Manifestacao> manifestacoesCopiadas;
	
	/** E-mail usado na autenticação do usuário no portal público. */
	private String email;
	
	/** Código de acesso usada na autenticação do usuário no portal público. */
	private String codigoAcesso;
	
	/** 
	 * Id do interessado não autenticado que está tentando verificar suas manifestações
	 * através do portal público. 
	 **/
	private int idInteressadoNaoAutenticado; 
	
	/** Se a solicitação deve redirecionar para a tela de manifestaçães pendentes. */
	private boolean forwardPendentes = false;
	
    public EsclarecimentoOuvidoriaMBean() {
    	init();
    }
           
    // Métodos referentes a operação de responder a manifestação no Portal Docente e Portal Discente. 

    /**
     * Direciona o usuário para a página de acompanhamento de suas manifestações cadastradas e copiadas.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     */
    public String acompanharManifestacao() {
    	return forward(getDirFlexivel() + "/acompanhar.jsp");
    }
    
	/**
     * Lista as manifestações cadastradas e copiadas, e direciona o usuário para a tela de acompanhamento.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Esclarecimento/responder_esclarecimento.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String listarManifestacoes() throws DAOException {
		popularManifestacoesCadastradas();
		popularManifestacoesCopiadas();
		
		return acompanharManifestacao();
    }

    /**
     * Popula a lista de manifestações cadastradas pelo usuário.
     * 
     * @throws DAOException
     */
	private void popularManifestacoesCadastradas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoes = dao.findManifestacoesByAnoNumeroPessoa(ano, numero, getUsuarioLogado().getPessoa().getId());
	}
	
	/**
	 * Popula a lista de manifestações copiadas para o usuário logado.
	 * 
	 * @throws DAOException
	 */
	private void popularManifestacoesCopiadas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoesCopiadas = dao.findManifestacoesCopiadas(ano, numero, getUsuarioLogado().getPessoa().getId(), getAllUnidadesResponsabilidadeUsuario().toArray(new UnidadeGeral[0]));
	}
    
    /**
     * Detalha a manifestação de acordo com o id passado.
     * Direciona o usuário para a tela de detalhamento da manifestação de acordo com o parâmetro #forward passado.
     * 
     * @param id
     * @param forward
     * @throws DAOException
     */
    private void detalharManifestacao(Integer id, boolean forward) throws DAOException {
		HistoricoManifestacaoDao dao = getDAO(HistoricoManifestacaoDao.class);
		AcompanhamentoManifestacaoDao acompanhamentoDao = getDAO(AcompanhamentoManifestacaoDao.class);
		DelegacaoUsuarioRespostaDao delegacaoDao = getDAO(DelegacaoUsuarioRespostaDao.class);
		
		try {
		    obj = dao.findByPrimaryKey(id, Manifestacao.class);
		    
		    if(!isEmpty(obj)) {
		    	if(manifestacoesCopiadas != null && manifestacoesCopiadas.contains(obj)) { //Se a manifestação está copiada para a pessoa, permite visualização completa do histórico
		    		historicos = dao.getAllHistoricosByManifestacao(obj.getId());
		    		
		    		copias = acompanhamentoDao.findAllAcompanhamentosByManifestacao(obj.getId());
		    		
		    		delegacoes = delegacaoDao.findAllDelegacoesByManifestacao(obj.getId());
		    	}
		    	else //Caso contrário, a manifestação foi cadastrada pela pessoa, e ela só deve ter acesso ao histórico de resposta da ouvidoria
		    		historicos = dao.getAllHistoricosVisiveisInteressadoByManifestacao(obj.getId());
		    }
		    
			if(forward)
			    forward("/public/ouvidoria/Manifestacao/detalhes_manifestacao.jsp");
			
		} finally {
		    dao.close();
		    acompanhamentoDao.close();
		    delegacaoDao.close();
		}
    }
        
    /**
     * Direciona o fluxo para a tela de resposta da manifestação <br />
 	 * Método invocado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * <li>/sigaa.war/ouvidoria/Manifestacao/discente/acompanhar.jsp</li>
 	 * </ul>
 	 *  
     * @param id
     * @return
     * @throws ArqException 
     */
     public void iniciarResponderEsclarecimento() throws ArqException {
    	Integer id = getParameterInt("idManifestacao");
		detalharManifestacao(id, false);
		setOperacaoAtiva(RESPONDER_ESCLARECIMENTO);
		prepareMovimento(SigaaListaComando.RESPONDER_ESCLARECIMENTO);
		historico = new HistoricoManifestacao();
		forward("/ouvidoria/Esclarecimento/responder_esclarecimento.jsp");
    }
	
    /**
     * Envia e-mail para o ouvidor na manifestação com a resposta do pedido de esclarecimento.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/discente/responder_esclarecimento.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
	public String responderEsclarecimento() throws ArqException {
		
		processarRespostaEsclarecimento();
		
    	if(hasErrors())
    		return null;
    	
		return listarManifestacoes();
	}
		
	//  Métodos refentes a operação de responder o esclarecimento no Portal Público
	
    /**
     * Lista as manifestações de acordo com o e-mail e a senha do usuário do portal público
     * <ul>
     * <li>/sigaa.war/public/ouvidoria/Manifestacao/form_senha.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String listarManifestacoesPortalPublico() throws DAOException {

		if( StringUtils.isEmpty(email) ){
			addMensagemErro("Digite o e-mail cadastrado");
			return null;
		}
		
		if( StringUtils.isEmpty(codigoAcesso) ){
			addMensagemErro("Digite o código de acesso cadastrado");
			return null;
		}
		    	
		String senha = UFRNUtils.toMD5(codigoAcesso);
    	ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		manifestacoes = dao.findManifestacoesByEmailCodigoAcesso(email, senha);
		
		if (manifestacoes == null || manifestacoes.isEmpty()){
			addMensagemErro("Cadastro inválido");
			return null;
		}
		
		return forward("/public/ouvidoria/Manifestacao/lista_manifestacoes.jsp");
    }
	
    /**
     * Envia o usuário para a página de resposta de esclarecimento no portal público.
     * Método invocado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 * <li>/sigaa.war/ouvidoria/Manifestacao/discente/acompanhar.jsp</li>
 	 * </ul> 
     * @param id
     * @return
     * @throws ArqException 
     */
     public void iniciarResponderEsclarecimentoPublico(ActionEvent evt) throws ArqException {
    	Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		detalharManifestacao(id, false);
		setOperacaoAtiva(RESPONDER_ESCLARECIMENTO);
		prepareMovimento(SigaaListaComando.RESPONDER_ESCLARECIMENTO);
		historico = new HistoricoManifestacao();
		forward("/public/ouvidoria/Manifestacao/responder_esclarecimento.jsp");
     }
    
    /**
     * Envia e-mail para o ouvidor na manifestação com a resposta do pedido de esclarecimento.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/ouvidoria/Manifestacao/responder_esclarecimento.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
	public String responderEsclarecimentoPublico() throws ArqException {
		
		processarRespostaEsclarecimento();
		
    	if(hasErrors())
    		return null;
    	
		return listarManifestacoesPortalPublico();
	}

    // Métodos referentes a operação de confirmação de código de acesso. 
    
    /**
	 *  <p>Caso o usuário confirme o link enviado para o email, vai ser gerado o hash da senha gerada 
	 * e salvo no banco no banco "senha".</p>
	 * 
	 *   <p>Assim apenas se ele for o dono do email de fato vai ser alterado a senha usada para se logar no sistema. 
	 *   Garantindo o mínimo de segurança.</p>
	 *
	 *  <p>Esse método é chamado a partir do pretty-config.xml quando o usuário acessa o endereço: 
	 *      link /link/public/ouvidoria/confirmarCOdigoAcesso/"codigo"/"id"</p>
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String confirmarCodigoAcesso() throws ArqException{
		
		InteressadoNaoAutenticadoDao dao = null;
		
		InteressadoNaoAutenticado interessado = null;
		
		try{
			dao = getDAO(InteressadoNaoAutenticadoDao.class);
			interessado = dao.findInteressadoByIdCodigoAcesso(codigoAcesso, idInteressadoNaoAutenticado);	
		}finally{
			if(dao != null) dao.close();
		}
		
		if (interessado != null) {
			
			prepareMovimento(SigaaListaComando.CONFIRMAR_CODIGO_ACESSO_OUVIDORIA);
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(interessado);
			movimento.setCodMovimento(SigaaListaComando.CONFIRMAR_CODIGO_ACESSO_OUVIDORIA);
			
			try {
				execute(movimento);
				addMensagemInformation("Código de Acesso confirmado com sucesso!");
				
			} catch (NegocioException e) {
				addMensagemErro("Endereço para confirmação do código de acesso inválido!");
			} 
		}else{
			addMensagemErro("Endereço para confirmação do código de acesso inválido!");
		}
		
		return redirect("/public/ouvidoria/Manifestacao/confirmaCodigoAcesso.jsf");
	}
	
    /**
     * Direciona para a tela de "logon", para o usuário externo ter acesso as suas manifestações. 
     * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/ouvidoria/Manifestacao/confirmarCodigoAcesso.jsp</li>
	 * </ul>
     */
	public String telaAcessoManifestacoes () {
		
		return forward("/public/ouvidoria/Manifestacao/form_senha.jsp");
		
	}
	
	// Métodos comuns ao Portal Público e aos portais Discente e Docente
	
	/**
	 * Recupera o id da manifestação selecionada e inicia o processo de detalhamento da mesma.<br />
     * Método não invocado por JSPs.
	 */
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = getParameterInt("idManifestacao",0);
		
		detalharManifestacao(id, true);
    }
    	
    /**
     * Retorna o texto da última solicitação de esclarecimento. 
     * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Esclarecimento/responder_esclarecimento.jsp</li>
	 * </ul>
	 * 
     * @param id
     * @param forward
     * @throws DAOException
     */
    public String getUltimaSolicitacaoEsclarecimento () throws DAOException {
    	
    	HistoricoManifestacaoDao hDao = null;
    	
    	try {
    		
    		hDao = getDAO(HistoricoManifestacaoDao.class);
    		HistoricoManifestacao ultimoHistorico = hDao.getUltimaSolicitacaoEsclarecimento(obj.getId());
    		return ultimoHistorico.getResposta();
    		
    	} finally {
    		if (hDao != null)
    			hDao.close();
    	}
    	
    }
	
    /**
     * Envia e-mail para o ouvidor na manifestação com a resposta do pedido de esclarecimento.<br />
	 * Método não invocado por JSPs
     * 
     * @return
     * @throws ArqException
     */
	private void processarRespostaEsclarecimento() throws ArqException {
		if (historico.getResposta() == null || historico.getResposta().isEmpty())
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta");
			
    	if(hasErrors())
    		return;
    	
		MovimentoCadastro mov = new MovimentoCadastro();
		historico.setManifestacao(obj);
		mov.setObjMovimentado(historico);
		mov.setCodMovimento(SigaaListaComando.RESPONDER_ESCLARECIMENTO);
				
		try {
		    execute(mov);

		    addMensagemInformation("Resposta do esclarecimento para a manifestação de protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> enviada com sucesso.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return;
		}
	}
	
	// Métodos referentes a operação de solicitar esclarecimento por parte do ouvidor.
    
    /**
     * Carrega o id da manifestação selecionada e chama o fluxo de pedido de esclareciemento.
     * <br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/acompanhar.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public String solicitarEsclarecimentos() throws ArqException {
    	Integer id = getParameterInt("idManifestacao");
    	forwardPendentes = getParameterBoolean("pendentes");
		detalharManifestacao(id, false);
		setOperacaoAtiva(SOLICITAR);
		prepareMovimento(SigaaListaComando.SOLICITAR_ESCLARECIMENTO);
		historico = new HistoricoManifestacao();
		return forward(getDirBase() + "solicitar_esclarecimento.jsp");
    }
    
    /**
     * Envia e-mail para o interessado na manifestação.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String enviarEmailEsclarecimento () throws ArqException {
    	
    	if (StringUtils.isEmpty(historico.getResposta()))
    		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Esclarecimento");
    	
    	if(hasErrors())
    		return null;
    	
		MovimentoCadastro mov = new MovimentoCadastro();
		historico.setManifestacao(obj);
		mov.setObjMovimentado(historico);
		mov.setCodMovimento(SigaaListaComando.SOLICITAR_ESCLARECIMENTO);
				
		try {
		    execute(mov);

		    addMensagemInformation("Solicitação de esclarecimento para a manifestação de protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> enviada com sucesso.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		AnaliseManifestacaoOuvidoriaMBean aMOMBean = getMBean("analiseManifestacaoOuvidoria"); 
		
		if (!forwardPendentes)
			return aMOMBean.listarManifestacoes();
		else
			return aMOMBean.listarPendentes();
    }
	
    /**
     * Retorna para a tela de acompanhar manifestações
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Esclarecimento/solicitar_esclarecimento.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String acompanhar() throws DAOException {    	
		AnaliseManifestacaoOuvidoriaMBean aMOMBean = getMBean("analiseManifestacaoOuvidoria"); 
		return aMOMBean.listarManifestacoes();
	}
    
    /**
     * Retorna para a tela de listar manifestações pendentes
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Esclarecimento/solicitar_esclarecimento.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String retornarPendentes() throws DAOException {    	
		AnaliseManifestacaoOuvidoriaMBean aMOMBean = getMBean("analiseManifestacaoOuvidoria"); 
		return aMOMBean.listarPendentes();
	}
    
    // Métodos arquiteturais do MBean
    
    @Override
    public String getDirBase() {
    	String dirBase = "/ouvidoria/Esclarecimento/";
		return dirBase;
    }

    /**
     * Retorna o diretório dependendo do portal onde o usuário se encontra.
	 * Não invocada por JSP(s) 
     * 
     * @return
     * @throws ArqException
     */
    public String getDirFlexivel() {
    	String dirBase = "";
    	
    	if(getSubSistema().getId() == SigaaSubsistemas.OUVIDORIA.getId()) {
    		dirBase += "/ouvidoria/Manifestacao";
    	} else if (getSubSistema().getId() == SigaaSubsistemas.PORTAL_DISCENTE.getId()) {
    		dirBase += "/ouvidoria/Manifestacao/discente";
    	} else if (getSubSistema().getId() == SigaaSubsistemas.PORTAL_DOCENTE.getId()) {
    		dirBase += "/ouvidoria/Manifestacao/docente";
    	}
    	
		return dirBase;
    } 
    
    /**
     * Indica se o interessado é da comunidade interna.
     * 
     * @return
     */
    public boolean isComunidadeInterna() {
    	return !verificarCategoria(CategoriaSolicitante.COMUNIDADE_EXTERNA);
    }
    
    /**
     * Verifica se a categoria passada é igual à categoria do solicitante da manifestação trabalhada.
     * 
     * @param categoria
     * @return
     */
    private boolean verificarCategoria(int categoria) {
    	return obj.getInteressadoManifestacao().getCategoriaSolicitante().getId() == categoria;
    }
    
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public String getCodigoAcesso() {
		return codigoAcesso;
	}

	public void setCodigoAcesso(String codigoAcesso) {
		this.codigoAcesso = codigoAcesso;
	}

	public int getIdInteressadoNaoAutenticado() {
		return idInteressadoNaoAutenticado;
	}

	public void setIdInteressadoNaoAutenticado(int idInteressadoNaoAutenticado) {
		this.idInteressadoNaoAutenticado = idInteressadoNaoAutenticado;
	}

	public void setForwardPendentes(boolean forwardPendentes) {
		this.forwardPendentes = forwardPendentes;
	}

	public boolean isForwardPendentes() {
		return forwardPendentes;
	}

}
