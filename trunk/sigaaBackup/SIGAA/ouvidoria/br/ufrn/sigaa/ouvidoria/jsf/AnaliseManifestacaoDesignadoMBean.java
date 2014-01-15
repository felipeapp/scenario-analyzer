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
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dao.AcompanhamentoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.DelegacaoUsuarioRespostaDao;
import br.ufrn.sigaa.ouvidoria.dao.HistoricoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.ManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoHistoricoManifestacao;

/**
 * Controller responsável por receptar as operações de análise de manifestações por um designado.
 * 
 * @author Bernardo
 *
 */
@Component(value="analiseManifestacaoDesignado") @Scope(value="request")
public class AnaliseManifestacaoDesignadoMBean extends AnaliseManifestacaoAbstractController {
    
    public AnaliseManifestacaoDesignadoMBean() {
    	init();
    }

    /**
     * Lista as manifestações pendentes e direciona o usuário para a listagem.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/detalhes_manifestacao.jsp</li>
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
		    addMensagemWarning("Não existem manifestações pendentes designadas para sua pessoa.");
		    return cancelar();
		}
		
		removeOperacaoAtiva();
		
		return forward(getDirBase() + "/listar_pendentes.jsp");
    }

    /**
     * Popula as manifestações pendentes para a pessoa logada.
     * 
     * @throws DAOException
     */
    private void popularManifestacoesPendentes() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		manifestacoes = new ArrayList<Manifestacao>();
		
		try {
		    manifestacoes = dao.getAllManifestacoesPendentesByDesignado(getUsuarioLogado().getPessoa().getId());
		} finally {
		    dao.close();
		}
    }
    
    /**
     * Retorna uma coleção contendo todas as manifestações pendentes designadas para a pessoa logada.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/menu.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public Collection<Manifestacao> getManifestacoesPendentes() throws DAOException {
		popularManifestacoesPendentes();
		
		return getManifestacoes();
    }
    
    /**
     * Inicia a operação de detalhar uma manifestação pendente e inicia seu detalhamento.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/listar_pendentes.jsp</li>
     * </ul>
     */
    public void detalharManifestacaoPendente(ActionEvent evt) throws DAOException {
		setOperacaoAtiva(VISUALIZAR_PENDENTE);
		
		detalharManifestacao(evt);
    }
    
    /**
     * Recupera o id da manifestação selecionada para detalhar seus dados.<br />
     * Método não invocado por JSPs.
     */
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		
		detalharManifestacao(id, true);
    }

    /**
     * Detalha a manifestação com id passado, recuperando seus dados e históricos.
     * Direciona o usuário para a página de detalhes da manifestação de acordo com o parâmetro passado. 
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
		    if(historicoPendenteResposta != null && !historicoPendenteResposta.isLido())
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
     * Inicia o processo de resposta de uma manifestação, carregando seus dados.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/listar_pendentes.jsp</li>
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
     * Recupera o histórico que está pendente de resposta e direciona o usuário para a página de resposta.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/detalhes_manifestacao.jsp</li>
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
		
		prepareMovimento(SigaaListaComando.ENVIAR_RESPOSTA_UNIDADE);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
    /**
     * Envia a resposta digitada para a ouvidoria.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/detalhes_manifestacao.jsp</li>
     * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String enviarResposta() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(historico);
		carregarResponsavelUnidade(historico.getUnidadeResponsavel().getId());
		mov.setObjAuxiliar(responsavelUnidade);
		mov.setCodMovimento(SigaaListaComando.ENVIAR_RESPOSTA_UNIDADE);
		
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
     * Recupera o histórico que está pendente de resposta de acordo com os históricos listados.
     * 
     * @return
     */
    private HistoricoManifestacao getHistoricoPendenteResposta() {
		for (HistoricoManifestacao h : historicos) {
		    if(h.getTipoHistoricoManifestacao().getId() == TipoHistoricoManifestacao.OUVIDORIA_RESPONSAVEL && isEmpty(h.getResposta())) {
			return h;
		    }
		}
		
		return null;
    }
    
    /**
     * Direciona o usuário para a página de acompanhamento de manifestações designadas a ele.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     */
    public String acompanharManifestacao() {
    	return forward(getDirBase() + "/acompanhar.jsp");
    }
    
    /**
     * Lista as manifestações que o usuário pode acompanhar e direciona o fluxo para a página de acompanhamento.<br />
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
     * Realiza a busca de manifestações de acordo com os dados informados.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/acompanhar.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/detalhes_manifestacao.jsp</li>
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
     * Popula as manifestações que o usuário pode acompanhar.
     * 
     * @throws DAOException
     */
    private void popularManifestacoes() throws DAOException {
    	ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
    	try {
    		manifestacoes = dao.findManifestacoesAcompanhamentoDesignado(ano, numero, dataInicial, dataFinal, categoriaAssuntoManifestacao, getUsuarioLogado().getPessoa().getId(), getPaginacao());
    	} finally {
    		dao.close();
    	}
	}
    
    /**
	 * Limpa as manifestações e chama o método de paginação para avançar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/acompanhar.jsp</li>
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
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/acompanhar.jsp</li>
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
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/acompanhar.jsp</li>
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
     * Verifica se a operação ativa é a de resposta de uma manifestação pendente.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/detalhes_manifestacao.jsp</li>
     * </ul>
     */
	public boolean isOperacaoResponder() {
    	return isOperacaoAtiva(RESPONDER_PENDENTE);
    }
    
	/**
	 * Verifica se a categoria do solicitante da manifestação trabalhada é igual à passada como parâmetro.
	 * 
	 * @param categoria
	 * @return
	 */
    private boolean verificarCategoria(int categoria) {
    	return obj.getInteressadoManifestacao().getCategoriaSolicitante().getId() == categoria;
    }
    
    /**
     * Verifica se a categoria do solicitante da manifestação trabalhada é DISCENTE.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     */
    public boolean isCategoriaDiscente() {
    	return isComunidadeInterna() && verificarCategoria(CategoriaSolicitante.DISCENTE);
    }
    
    /**
     * Verifica se o solicitante da manifestação trabalhada faz parte da comunidade interna.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/designado/detalhes_manifestacao.jsp</li>
     * </ul>
     * 
     * @return
     */
    public boolean isComunidadeInterna() {
    	return obj.getInteressadoManifestacao() != null && obj.getInteressadoManifestacao().getCategoriaSolicitante().getId() != CategoriaSolicitante.COMUNIDADE_EXTERNA;
    }
    
    @Override
    public String getDirBase() {
    	return "/ouvidoria/Manifestacao/designado";
    }
    
    /**
     * Retorna as manifestações que o usuário pode acompanhar, paginadas.
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

}
