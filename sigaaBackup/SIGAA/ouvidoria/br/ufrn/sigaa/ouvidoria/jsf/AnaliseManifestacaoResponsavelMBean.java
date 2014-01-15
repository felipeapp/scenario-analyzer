package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dao.AcompanhamentoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.DelegacaoUsuarioRespostaDao;
import br.ufrn.sigaa.ouvidoria.dao.HistoricoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.ManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.DelegacaoUsuarioResposta;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoHistoricoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Controller responsável por operações de análise de manifestações feitas por um responsável de unidade.
 * 
 * @author Bernardo
 *
 */
@Component(value="analiseManifestacaoResponsavel") @Scope(value="request")
public class AnaliseManifestacaoResponsavelMBean extends AnaliseManifestacaoAbstractController {
    
	/** Delegação trabalhada ao encaminhar uma manifestação. */
	private DelegacaoUsuarioResposta delegacao;
	
	/** Delegação atualmente ativa utilizada ao reencaminhar uma manifestação. */
	private DelegacaoUsuarioResposta delegacaoAntiga;

	/**
	 * Contrutor padrão
	 */
	public AnaliseManifestacaoResponsavelMBean() {
    	init();
    }

	/**
	 * Lista as manifetações pendentes de análise e direciona o usuário para a tela de listagem.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/menu.jsp</li>
     * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
    public String listarPendentes() throws HibernateException, DAOException {
		popularManifestacoesPendentes();
		
		if(isEmpty(manifestacoes)) {
		    addMensagemWarning("Não existem manifestações pendentes cadastradas no sistema.");
		    return cancelar();
		}
		
		removeOperacaoAtiva();
		
		return forward(getDirBase() + "/listar_pendentes.jsp");
    }

    /**
     * Popula as manfiestações atualmente pendentes para a unidade.
     * 
     * @throws DAOException
     */
    private void popularManifestacoesPendentes() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		manifestacoes = new ArrayList<Manifestacao>();
		
		try {
		    manifestacoes = dao.getAllManifestacoesPendentesByUnidade(getAllUnidadesResponsabilidadeUsuario().toArray(new UnidadeGeral[0]));
		} finally {
		    dao.close();
		}
    }
    
    /**
     * Recupera as manifestações pendentes de análise.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<Manifestacao> getManifestacoesPendentes() throws DAOException {
		popularManifestacoesPendentes();
		
		return getManifestacoes();
    }
    
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		
		detalharManifestacao(id, true);
    }

    /**
     * Detalha a manifestação de acordo com seu id e direciona o fluxo para a tela
     * de detalhamento da manifestação, se assim for indicado pelo parâmetro.
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
		    obj = getManifestacaoById(id);
		    
		    obj = dao.refresh(obj);
		    
		    historicos = dao.getAllHistoricosByManifestacao(obj.getId());
		    
		    copias = acompanhamentoDao.findAllAcompanhamentosByManifestacao(obj.getId());
		    
		    delegacoes = delegacaoDao.findAllDelegacoesByManifestacao(obj.getId());
		    
		    HistoricoManifestacao historicoPendenteResposta = getHistoricoPendenteResposta();
		    if(historicoPendenteResposta != null && !historicoPendenteResposta.isLido() 
		    		&& historicoPendenteResposta.getManifestacao().getStatusManifestacao().getId() == StatusManifestacao.ENCAMINHADA_UNIDADE)
		    	marcarHistoricoLido(historicoPendenteResposta);
		} finally {
		    dao.close();
		    acompanhamentoDao.close();
		    delegacaoDao.close();
		}
		
		if(forward)
		    forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
    /**
     * Recupera o id da manifestação escolhida para iniciar o processo de resposta da mesma.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/listar_pendentes.jsp</li>
     * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void responderManifestacao(ActionEvent evt) throws ArqException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		detalharManifestacao(id, false);
		
		responderManifestacaoCarregada();
    }

    /**
     * Inicia o processo de resposta de uma manfiestação previamente carregada.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp</li>
     * </ul>
     * 
     * @throws ArqException
     */
    public void responderManifestacaoCarregada() throws ArqException {
		historico = getHistoricoPendenteResposta();
		
		if(isEmpty(historico)) {
		    addMensagemErro("Erro ao recuperar os dados do histórico da manifestação. Favor reiniciar a operação.");
		    cancelar();
		}
		
		setOperacaoAtiva(RESPONDER_PENDENTE);
		
		prepareMovimento(SigaaListaComando.ENVIAR_RESPOSTA_OUVIDORIA);
		
		prepareMovimento(SigaaListaComando.ENVIAR_RESPOSTA_USUARIO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
    /**
     * Recupera, dentro dos históricos encontados para a manifestação, o que se encontra pendente de resposta.
     * 
     * @return
     */
    public HistoricoManifestacao getHistoricoPendenteResposta() {
		for (HistoricoManifestacao h : historicos) {
		    if(h.getTipoHistoricoManifestacao().getId() == TipoHistoricoManifestacao.OUVIDORIA_RESPONSAVEL && isEmpty(h.getResposta())) {
		    	return h;
		    }
		}
		
		return null;
    }

    /**
     * Envia a resposta dada à manifestação para a ouvidoria.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp</li>
     * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String enviarRespostaOuvidoria() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(historico);
		mov.setCodMovimento(SigaaListaComando.ENVIAR_RESPOSTA_OUVIDORIA);
		
		try {
		    execute(mov);
		    
		    addMensagemInformation("Resposta encaminhada com sucesso para a manifestação sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b>.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarPendentes();
    }
    
    
    
    /**
     * Envia a resposta dada à manifestação para a o interessado.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp</li>
     * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String enviarRespostaInteressado() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(historico);
		mov.setCodMovimento(SigaaListaComando.ENVIAR_RESPOSTA_USUARIO);
		
		try {
		    execute(mov);
		    
		    addMensagemInformation("Resposta enviada com sucesso para a manifestação sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b>.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarPendentes();
    }
    
    
    
    /**
     * Recupera o id da manifestação escolhida para iniciar o encaminhamento/reencaminhamento de uma manifestação.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/listar_pendentes.jsp</li>
     * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void encaminharManifestacao(ActionEvent evt) throws ArqException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		detalharManifestacao(id, false);
		
		if(obj.isDesignada())
			reencaminharManifestacaoCarregada();
		else
			encaminharManifestacaoCarregada();
    }
    
    /**
     * Inicia o processo de reencaminhamento de uma manfiestação já carregada.<br />
     * Método não invocado por JSPs.
     * 
     * @throws ArqException
     */
    private void reencaminharManifestacaoCarregada() throws ArqException {
    	setOperacaoAtiva(REENCAMINHAR);
		
		delegacao = new DelegacaoUsuarioResposta();
		delegacao.setPessoa(new Pessoa());
		historico = getHistoricoPendenteResposta();
		delegacaoAntiga = getDelegacaoAtiva();
		
		prepareMovimento(SigaaListaComando.DESIGNAR_OUTRA_PESSOA_MANIFESTACAO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
	}
    
    /**
     * Retorna a delegação ativa dentro do histórico carregado.
     * 
     * @return
     */
    private DelegacaoUsuarioResposta getDelegacaoAtiva() {
    	for (DelegacaoUsuarioResposta d : historico.getDelegacoesUsuarioResposta()) {
    		if(d.isAtivo()) {
    			return d;
    		}
    	}
    	
    	return new DelegacaoUsuarioResposta();
    }

    /**
     * Inicia o processo de encaminhamento de uma manifestação já carregada.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp</li>
     * </ul>
     * 
     * @throws ArqException
     */
	public void encaminharManifestacaoCarregada() throws ArqException {
		setOperacaoAtiva(ENCAMINHAR);
		
		delegacao = new DelegacaoUsuarioResposta();
		delegacao.setPessoa(new Pessoa());
		historico = getHistoricoPendenteResposta();
		
		prepareMovimento(SigaaListaComando.DESIGNAR_PESSOA_MANIFESTACAO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
	/**
	 * Chama o cadastro de encaminhamento.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp</li>
     * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarEncaminhamento() throws ArqException {
		return cadastrarEncaminhamento(SigaaListaComando.DESIGNAR_PESSOA_MANIFESTACAO);
    }
	
	/**
	 * Chama o cadastro de um reencaminhamento.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp</li>
     * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarReencaminhamento() throws ArqException {
		return cadastrarEncaminhamento(SigaaListaComando.DESIGNAR_OUTRA_PESSOA_MANIFESTACAO);
    }
	
	/**
	 * Cadastra o encaminhamento/reencaminhamento de acordo com o comando passado.<br />
	 * Método não invocado por JSPs.
	 * 
	 * @param comando
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarEncaminhamento(Comando comando) throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(delegacao);
		mov.setObjAuxiliar(historico);
		mov.setCodMovimento(comando);
		
		try {
		    execute(mov);
		    
		    addMensagemInformation("Manifestação sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> encaminhada com sucesso para a pessoa designada.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarPendentes();
    }
    
	/**
	 * Lista as manifestações de acompanhamento do usuário.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/menu.jsp</li>
     * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String listarManifestacoes() throws DAOException {
    	categoriaAssuntoManifestacao = new CategoriaAssuntoManifestacao();
    	popularManifestacoes();
    	
		return acompanharManifestacao();
    }

    /**
     * Direciona o fluxo para a página de acompanhamento de manifestações.
     * 
     * @return
     */
	private String acompanharManifestacao() {
		return forward(getDirBase() + "/acompanhar.jsp");
	}
    
	/**
	 * Filtra as manifestações encontradas de acordo com o informado.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/acompanhar.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp</li>
     * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String buscarManifestacao() throws DAOException {
		if(buscaNumeroAno && isEmpty(ano))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if(buscaNumeroAno && isEmpty(numero))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Numero");
		if(buscaPeriodo && isEmpty(dataInicial))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Inicial");
		if(buscaPeriodo && isEmpty(dataFinal))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Final");
		if(buscaCategoriaAssunto && isEmpty(categoriaAssuntoManifestacao))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria do Assunto");
		
		if(hasErrors())
		    return null;
		
		if(!buscaNumeroAno) {
		    ano = null;
		    numero = null;
		}
		if(!buscaPeriodo) {
			dataInicial = null;
			dataFinal = null;
		}
		if(!buscaCategoriaAssunto) {
			categoriaAssuntoManifestacao = new CategoriaAssuntoManifestacao();
		}
		
		popularManifestacoes();
		
		if(isEmpty(manifestacoes))
		    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return acompanharManifestacao();
    }

    /**
     * Popula as manifestações de acompanhamento do usuário.
     * 
     * @throws DAOException
     */
	private void popularManifestacoes() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		try {
			manifestacoes = dao.findManifestacoesAcompanhamentoUnidade(ano, numero, dataInicial, dataFinal, categoriaAssuntoManifestacao, getPaginacao(), getAllUnidadesResponsabilidadeUsuario().toArray(new UnidadeGeral[0]));
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Limpa as manifestações e chama o método de paginação para avançar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/acompanhar.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void nextPage(ActionEvent e) {
		PagingInformation paging = getMBean("paginacao");
		
		setManifestacoes(new ArrayList<Manifestacao>());
		
		paging.nextPage(e);
	}

	/**
	 * Limpa as manifestações e chama o método de paginação para voltar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/acompanhar.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void previousPage(ActionEvent e) {
		PagingInformation paging = getMBean("paginacao");
		
		setManifestacoes(new ArrayList<Manifestacao>());
		
		paging.previousPage(e);
	}
	
	/**
	 * Limpa as manifestações e chama o método de paginação para trocar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/responsavel/acompanhar.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void changePage(ValueChangeEvent e) {
		PagingInformation paging = getMBean("paginacao");
		
		setManifestacoes(new ArrayList<Manifestacao>());
		
		paging.changePage(e);
	}
    
	/**
	 * Verifica se a categoria passada corresponde à categoria do interessado.
	 * 
	 * @param categoria
	 * @return
	 */
    private boolean verificarCategoria(int categoria) {
    	return obj.getInteressadoManifestacao().getCategoriaSolicitante().getId() == categoria;
    }
    
    public boolean isCategoriaDiscente() throws DAOException {
    	return isComunidadeInterna() && verificarCategoria(CategoriaSolicitante.DISCENTE);
    }
    
    public boolean isComunidadeInterna() throws DAOException {
    	if (obj.getInteressadoManifestacao() == null || obj.getInteressadoManifestacao().getCategoriaSolicitante() == null)
    		 obj = getGenericDAO().refresh(obj);    		
    	return obj.getInteressadoManifestacao().getCategoriaSolicitante().getId() != CategoriaSolicitante.COMUNIDADE_EXTERNA;
    }
    
    @Override
    public String getDirBase() {
    	return "/ouvidoria/Manifestacao/responsavel";
    }
    
    /**
     * Retorna as manifestações para acompanhamento, de forma paginada.
     * 
     * @return
     */
	public Collection<Manifestacao> getManifestacoesAcompanhamento() {
		setTamanhoPagina(50);
		
		if(isEmpty(super.getManifestacoes())) {
			try {
				popularManifestacoes();
			} catch (DAOException ex) {
				tratamentoErroPadrao(ex);
			}
		}
		
		return super.getManifestacoes();
	}

    public DelegacaoUsuarioResposta getDelegacao() {
        return delegacao;
    }

    public void setDelegacao(DelegacaoUsuarioResposta delegacao) {
        this.delegacao = delegacao;
    }

	public DelegacaoUsuarioResposta getDelegacaoAntiga() {
		return delegacaoAntiga;
	}

	public void setDelegacaoAntiga(DelegacaoUsuarioResposta delegacaoAntiga) {
		this.delegacaoAntiga = delegacaoAntiga;
	}

}
