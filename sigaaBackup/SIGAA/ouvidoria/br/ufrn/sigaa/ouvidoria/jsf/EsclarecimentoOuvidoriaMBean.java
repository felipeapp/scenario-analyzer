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
 * Controller respons�vel pelas opera��es de solicita��o e resposta de esclarecimentos por parte da ouvidoria.
 * 
 * @author Diego J�come
 *
 */
@Component(value="esclarecimentoOuvidoria") @Scope(value="request")
public class EsclarecimentoOuvidoriaMBean extends AnaliseManifestacaoAbstractController {

	/** Armazena as manifesta��es copiadas para a pessoa logada. */
	private Collection<Manifestacao> manifestacoesCopiadas;
	
	/** E-mail usado na autentica��o do usu�rio no portal p�blico. */
	private String email;
	
	/** C�digo de acesso usada na autentica��o do usu�rio no portal p�blico. */
	private String codigoAcesso;
	
	/** 
	 * Id do interessado n�o autenticado que est� tentando verificar suas manifesta��es
	 * atrav�s do portal p�blico. 
	 **/
	private int idInteressadoNaoAutenticado; 
	
	/** Se a solicita��o deve redirecionar para a tela de manifesta��es pendentes. */
	private boolean forwardPendentes = false;
	
    public EsclarecimentoOuvidoriaMBean() {
    	init();
    }
           
    // M�todos referentes a opera��o de responder a manifesta��o no Portal Docente e Portal Discente. 

    /**
     * Direciona o usu�rio para a p�gina de acompanhamento de suas manifesta��es cadastradas e copiadas.<br />
     * M�todo n�o invocado por JSPs.
     * 
     * @return
     */
    public String acompanharManifestacao() {
    	return forward(getDirFlexivel() + "/acompanhar.jsp");
    }
    
	/**
     * Lista as manifesta��es cadastradas e copiadas, e direciona o usu�rio para a tela de acompanhamento.<br />
     * M�todo invocado pela(s) seguinte(s) JSP(s):
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
     * Popula a lista de manifesta��es cadastradas pelo usu�rio.
     * 
     * @throws DAOException
     */
	private void popularManifestacoesCadastradas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoes = dao.findManifestacoesByAnoNumeroPessoa(ano, numero, getUsuarioLogado().getPessoa().getId());
	}
	
	/**
	 * Popula a lista de manifesta��es copiadas para o usu�rio logado.
	 * 
	 * @throws DAOException
	 */
	private void popularManifestacoesCopiadas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoesCopiadas = dao.findManifestacoesCopiadas(ano, numero, getUsuarioLogado().getPessoa().getId(), getAllUnidadesResponsabilidadeUsuario().toArray(new UnidadeGeral[0]));
	}
    
    /**
     * Detalha a manifesta��o de acordo com o id passado.
     * Direciona o usu�rio para a tela de detalhamento da manifesta��o de acordo com o par�metro #forward passado.
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
		    	if(manifestacoesCopiadas != null && manifestacoesCopiadas.contains(obj)) { //Se a manifesta��o est� copiada para a pessoa, permite visualiza��o completa do hist�rico
		    		historicos = dao.getAllHistoricosByManifestacao(obj.getId());
		    		
		    		copias = acompanhamentoDao.findAllAcompanhamentosByManifestacao(obj.getId());
		    		
		    		delegacoes = delegacaoDao.findAllDelegacoesByManifestacao(obj.getId());
		    	}
		    	else //Caso contr�rio, a manifesta��o foi cadastrada pela pessoa, e ela s� deve ter acesso ao hist�rico de resposta da ouvidoria
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
     * Direciona o fluxo para a tela de resposta da manifesta��o <br />
 	 * M�todo invocado pela(s) seguinte(s) JSP(s):
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
     * Envia e-mail para o ouvidor na manifesta��o com a resposta do pedido de esclarecimento.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
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
		
	//  M�todos refentes a opera��o de responder o esclarecimento no Portal P�blico
	
    /**
     * Lista as manifesta��es de acordo com o e-mail e a senha do usu�rio do portal p�blico
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
			addMensagemErro("Digite o c�digo de acesso cadastrado");
			return null;
		}
		    	
		String senha = UFRNUtils.toMD5(codigoAcesso);
    	ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		manifestacoes = dao.findManifestacoesByEmailCodigoAcesso(email, senha);
		
		if (manifestacoes == null || manifestacoes.isEmpty()){
			addMensagemErro("Cadastro inv�lido");
			return null;
		}
		
		return forward("/public/ouvidoria/Manifestacao/lista_manifestacoes.jsp");
    }
	
    /**
     * Envia o usu�rio para a p�gina de resposta de esclarecimento no portal p�blico.
     * M�todo invocado pela(s) seguinte(s) JSP(s):
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
     * Envia e-mail para o ouvidor na manifesta��o com a resposta do pedido de esclarecimento.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
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

    // M�todos referentes a opera��o de confirma��o de c�digo de acesso. 
    
    /**
	 *  <p>Caso o usu�rio confirme o link enviado para o email, vai ser gerado o hash da senha gerada 
	 * e salvo no banco no banco "senha".</p>
	 * 
	 *   <p>Assim apenas se ele for o dono do email de fato vai ser alterado a senha usada para se logar no sistema. 
	 *   Garantindo o m�nimo de seguran�a.</p>
	 *
	 *  <p>Esse m�todo � chamado a partir do pretty-config.xml quando o usu�rio acessa o endere�o: 
	 *      link /link/public/ouvidoria/confirmarCOdigoAcesso/"codigo"/"id"</p>
	 *
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
				addMensagemInformation("C�digo de Acesso confirmado com sucesso!");
				
			} catch (NegocioException e) {
				addMensagemErro("Endere�o para confirma��o do c�digo de acesso inv�lido!");
			} 
		}else{
			addMensagemErro("Endere�o para confirma��o do c�digo de acesso inv�lido!");
		}
		
		return redirect("/public/ouvidoria/Manifestacao/confirmaCodigoAcesso.jsf");
	}
	
    /**
     * Direciona para a tela de "logon", para o usu�rio externo ter acesso as suas manifesta��es. 
     * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/ouvidoria/Manifestacao/confirmarCodigoAcesso.jsp</li>
	 * </ul>
     */
	public String telaAcessoManifestacoes () {
		
		return forward("/public/ouvidoria/Manifestacao/form_senha.jsp");
		
	}
	
	// M�todos comuns ao Portal P�blico e aos portais Discente e Docente
	
	/**
	 * Recupera o id da manifesta��o selecionada e inicia o processo de detalhamento da mesma.<br />
     * M�todo n�o invocado por JSPs.
	 */
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = getParameterInt("idManifestacao",0);
		
		detalharManifestacao(id, true);
    }
    	
    /**
     * Retorna o texto da �ltima solicita��o de esclarecimento. 
     * M�todo invocado pela(s) seguinte(s) JSP(s):
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
     * Envia e-mail para o ouvidor na manifesta��o com a resposta do pedido de esclarecimento.<br />
	 * M�todo n�o invocado por JSPs
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

		    addMensagemInformation("Resposta do esclarecimento para a manifesta��o de protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> enviada com sucesso.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return;
		}
	}
	
	// M�todos referentes a opera��o de solicitar esclarecimento por parte do ouvidor.
    
    /**
     * Carrega o id da manifesta��o selecionada e chama o fluxo de pedido de esclareciemento.
     * <br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
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
     * Envia e-mail para o interessado na manifesta��o.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
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

		    addMensagemInformation("Solicita��o de esclarecimento para a manifesta��o de protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> enviada com sucesso.");
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
     * Retorna para a tela de acompanhar manifesta��es
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
     * Retorna para a tela de listar manifesta��es pendentes
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
    
    // M�todos arquiteturais do MBean
    
    @Override
    public String getDirBase() {
    	String dirBase = "/ouvidoria/Esclarecimento/";
		return dirBase;
    }

    /**
     * Retorna o diret�rio dependendo do portal onde o usu�rio se encontra.
	 * N�o invocada por JSP(s) 
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
     * Indica se o interessado � da comunidade interna.
     * 
     * @return
     */
    public boolean isComunidadeInterna() {
    	return !verificarCategoria(CategoriaSolicitante.COMUNIDADE_EXTERNA);
    }
    
    /**
     * Verifica se a categoria passada � igual � categoria do solicitante da manifesta��o trabalhada.
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
