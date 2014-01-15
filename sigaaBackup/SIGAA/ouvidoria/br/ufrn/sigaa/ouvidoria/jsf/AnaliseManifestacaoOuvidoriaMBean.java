package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.ResponsavelUnidadeDAO;
import br.ufrn.comum.dao.ServidorDAO;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.rh.dominio.Servidor;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ouvidoria.dao.AcompanhamentoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.AssuntoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.DelegacaoUsuarioRespostaDao;
import br.ufrn.sigaa.ouvidoria.dao.HistoricoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.ManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.AssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Controller erspons�vel pelas opera��es de an�lise de manifesta��es pelos usu�rios da ouvidoria.
 * 
 * @author Bernardo
 *
 */
@Component(value="analiseManifestacaoOuvidoria") @Scope(value="request")
public class AnaliseManifestacaoOuvidoriaMBean extends AnaliseManifestacaoAbstractController {
	
	/** Prazo de resposta definido para o hist�rico carregado. */
	private Date prazoResposta;
	
	/** Unidade selecionada no formul�rio de c�pia de manifesta��o. */
	private Unidade unidadeSelecionadaCopia;
	
	/** Respons�vel pela unidade selecionada no formul�rio de c�pia de manifesta��o. */
	private Responsavel responsavelUnidadeCopia;
	
	/** Armazena a lista de {@link Responsavel} e {@link Pessoa} adicionadas para receberem uma c�pia da manifesta��o. */
	private Collection<Responsavel> responsaveisUnidadesCopia;
	
	/** Indica se o usu�rio deseja adicionar uma pessoa para receber a c�pia da manifesta��o. */
	private boolean copiaPessoa;
	
	/** Indica se, no momento do encaminhamento da manifesta��o, o sigilo do manifestante ser� mantido. */
	private boolean manterSigilo;
	
	/** Indica se � para voltar a tela de acompanhamento ap�s finalizar uma manifesta��o */
	private boolean acompanhar;
    
    public AnaliseManifestacaoOuvidoriaMBean() {
    	init();
    }
    
    /**
     * Inicia os dados necess�rios para adicionar pessoas ou unidades para receberem c�pias da manifesta��o.
     * 
     */
	private void initDadosResponsavelCopia() {
		responsavelUnidadeCopia = new Responsavel();
    	responsavelUnidadeCopia.setServidor(new Servidor());
		responsavelUnidadeCopia.getServidor().setPessoa(new PessoaGeral());
	}

	/**
	 * Lista as manifesta��es pendentes de an�lise para a ouvidoria e 
	 * direciona o usu�rio para a listagem de manifesta��es pendentes.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
    public String listarPendentes() throws HibernateException, DAOException {
		popularManifestacoesPendentes();
		
		if(isEmpty(manifestacoes)) {
		    addMensagemWarning("N�o existem manifesta��es pendentes cadastradas no sistema.");
		    return cancelar();
		}
		
		removeOperacaoAtiva();
		
		return forward(getDirBase() + "/listar_pendentes.jsp");
    }
    
    // Opera��es do caso de uso de Listar Manifesta��es Encaminhadas
    
    /**
     * Lista as manifesta��es j� encaminhadas pela ouvidoria, mas que
     * ainda n�o foram respondidas ao usu�rio e direciona o fluxo para
     * a tela de listagem das manifesta�es encaminhadas.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    public String listarEncaminhadas() throws HibernateException, DAOException {
    	categoriaAssuntoManifestacao = new CategoriaAssuntoManifestacao();
    	manifestacoes = new ArrayList<Manifestacao>();
    	getPaginacao().setPaginaAtual(0);
    	popularManifestacoesEncaminhadas();
		
		if(isEmpty(manifestacoes)) {
		    addMensagemWarning("N�o existem manifesta��es encaminhadas ou designadas cadastradas no sistema.");
		    return cancelar();
		}
		
		removeOperacaoAtiva();
		
		return forward(getDirBase() + "/listar_encaminhadas.jsp");
    }
    
    /**
     * Popula os dados e retorna todas as manifesta��es encaminhadas pela ouvidoria. 
     * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
	 * 
     * @return
     * @throws DAOException
     */
    public Collection<Manifestacao> getManifestacoesEncaminhadas() throws DAOException {
		popularManifestacoesEncaminhadas();
		
		return manifestacoes;
    }
    
    /**
     * Retorna a lista p�ginada de manifesta��es que est�o sendo encaminhas.
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_encaminhas.jsp</li>
	 * </ul>
     * @return
     */
	public Collection<Manifestacao> getManifestacoesEncaminhadasPaginadas() {
		setTamanhoPagina(50);
		ManifestacaoDao dao = null;
		
		if(isEmpty(super.getManifestacoes())) {
			try {
				dao = getDAO(ManifestacaoDao.class);
				manifestacoes = dao.findManifestacoesEncaminhadasOuvidoria(ano, numero, dataInicial, dataFinal, categoriaAssuntoManifestacao, nome, matriculaSiape, cpf, getPaginacao());
			} catch (DAOException ex) {
				tratamentoErroPadrao(ex);
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return super.getManifestacoes();
	}
    
    /**
     * Busca manifesta��es encaminhadas de acordo com os filtros indicados.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_encaminhas.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws DAOException
     */
	public String buscarManifestacaoEncaminhadas() throws DAOException {
		validarDadosBusca();
		
		if(hasErrors())
		    return null;
		
		anularDadosNaoUsadosBusca();
		
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoes = dao.findManifestacoesEncaminhadasOuvidoria(ano, numero, dataInicial, dataFinal, categoriaAssuntoManifestacao, nome, matriculaSiape, cpf, getPaginacao());
		
		if(isEmpty(manifestacoes))
		    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return forward(getDirBase() + "/listar_encaminhadas.jsp");
    }
    
    // Opera��es do caso de uso de Listar Manifesta��es Respondidas
	
    /**
     * Lista as manifesta��es j� encaminhadas pela ouvidoria que
     * j� foram respondidas e direciona o fluxo para
     * a tela de listagem das manifesta�es respondidas.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    public String listarRespondidas() throws HibernateException, DAOException {
		popularManifestacoesRespondidas();
		
		if(isEmpty(manifestacoes)) {
		    addMensagemWarning("N�o existem manifesta��es respondidas cadastradas no sistema.");
		    return cancelar();
		}
		
		removeOperacaoAtiva();
		
		return forward(getDirBase() + "/listar_respondidas.jsp");
    }

    /**
     * Popula a listagem de manifesta��es pendentes de an�lise.
     * 
     * @throws DAOException
     */
    private void popularManifestacoesPendentes() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		manifestacoes = new ArrayList<Manifestacao>();
		
		try {
		    manifestacoes = dao.getAllManifestacoesPendentes();
		} finally {
		    dao.close();
		}
    }
    
    /**
     * Popula a listagem de manifesta��es encaminhadas pela ouvidoria.
     * 
     * @throws DAOException
     */
    private void popularManifestacoesEncaminhadas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		manifestacoes = new ArrayList<Manifestacao>();
		
		try {
		    manifestacoes = dao.getAllManifestacoesEncaminhadas();
		} finally {
		    dao.close();
		}
    }
    
    /**
     * Popula a listagem de manifesta��es j� respondidas.
     * 
     * @throws DAOException
     */
    private void popularManifestacoesRespondidas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		manifestacoes = new ArrayList<Manifestacao>();
		
		try {
		    manifestacoes = dao.getAllManifestacoesRespondidas();
		} finally {
		    dao.close();
		}
    }
    
    /**
     * Popula os dados e retorna as manifesta��es pendentes de an�lise pela ouvidoria. 
     * 
     * @return
     * @throws DAOException
     */
    public Collection<Manifestacao> getManifestacoesPendentes() throws DAOException {
		popularManifestacoesPendentes();
		
		return manifestacoes;
    }
    
    /**
     * Popula os dados e retorna as manifesta��es encaminhadas pela ouvidoria. 
     * 
     * @return
     * @throws DAOException
     */
    public Collection<Manifestacao> getManifestacoesRespondidas() throws DAOException {
		popularManifestacoesRespondidas();
		
		return manifestacoes;
    }
    
    /**
     * Inicia o processo de detalhamento de uma manifesta��o encaminhada.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_encaminhadas.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws DAOException
     */
    public void detalharManifestacaoEncaminhada(ActionEvent evt) throws DAOException {
		setOperacaoAtiva(VISUALIZAR_ENCAMINHADA);
		
		detalharManifestacao(evt);
    }
    
    /**
     * Inicia o processo de detalhamento de uma manifesta��o respondida.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_encaminhadas.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws DAOException
     */
    public void detalharManifestacaoRespondida(ActionEvent evt) throws DAOException {
		setOperacaoAtiva(VISUALIZAR_RESPONDIDA);
		
		detalharManifestacao(evt);
    }
    
    /**
     * Recupera o id da manifesta��o selecionada para detalhar seus dados.<br />
     * M�todo n�o invocado por JSPs.
     */
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		
		detalharManifestacao(id, true);
    }

    /**
     * Detalha a manifesta��o e, de acordo com o par�metro passado, direciona o usu�rio
     * para a p�gina de detalhamento da manifesta��o.
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
		    
		    manterSigilo = obj.isAnonima();
		    
		    historicos = dao.getAllHistoricosByManifestacao(obj.getId());
		    
		    copias = acompanhamentoDao.findAllAcompanhamentosByManifestacao(obj.getId());
		    
		    delegacoes = delegacaoDao.findAllDelegacoesByManifestacao(obj.getId());
		} finally {
		    dao.close();
		    acompanhamentoDao.close();
		    delegacaoDao.close();
		}
		
		if(forward)
		    forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
    /**
     * Inicia o proceso de responder uma manifesta��o pendente.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_pendentes.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void responderManifestacaoPendente(ActionEvent evt) throws ArqException {
		setOperacaoAtiva(RESPONDER_PENDENTE);
		
		responderManifestacao(evt);
    }
    
    /**
     * Inicia o processo de responder uma manifesta��o encaminhada.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_pendentes.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void responderManifestacaoEncaminhada(ActionEvent evt) throws ArqException {
		setOperacaoAtiva(RESPONDER_ENCAMINHADA);
		
		responderManifestacao(evt);
    }
    
    /**
     * Carrega o id da manifesta��o que o usu�rio selecionou para responder, detalha seus dados
     * e chama o direcionamento do fluxo.<br />
     * M�todo n�o invocado por JSPs.
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
     * Chama o direcionamento de fluxo para uma manifesta��o encaminhada que j� foi carregada previamente.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @throws ArqException
     */
    public void responderManifestacaoEncaminhadaCarregada() throws ArqException {
    	setOperacaoAtiva(RESPONDER_ENCAMINHADA);
    	
		responderManifestacaoCarregada();
    }
    
    /**
     * Chama o direcionamento de fluxo para uma manifesta��o pendente que j� foi carregada previamente.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @throws ArqException
     */
    public void responderManifestacaoPendenteCarregada() throws ArqException {
    	setOperacaoAtiva(RESPONDER_PENDENTE);
    	
		responderManifestacaoCarregada();
    }

    /**
     * Chama o direcionamento de fluxo para uma manifesta��o que j� foi carregada previamente.<br />
     * M�todo n�o invocado por JSPs.
     * 
     * @throws ArqException
     */
	public void responderManifestacaoCarregada() throws ArqException {
		historico = new HistoricoManifestacao();
		
		prepareMovimento(SigaaListaComando.ENVIAR_RESPOSTA_USUARIO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
	}
    
	/**
	 * Trata de enviar a resposta da manifesta��o ao manifestante.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
    public String enviarResposta() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		historico.setManifestacao(obj);
		mov.setObjMovimentado(historico);
		mov.setCodMovimento(SigaaListaComando.ENVIAR_RESPOSTA_USUARIO);
		
		try {
		    execute(mov);
		    
		    addMensagemInformation("Resposta enviada com sucesso para a manifesta��o sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b>.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		if(isOperacaoPendente())
			return listarPendentes();
		
		return listarEncaminhadas();
    }
    
    /**
     * Recupera o id d amanifesta��o selecionada e chama o detalhamento da manifesta��o e o encaminhamento
     * para a devida p�gina.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_pendentes.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void encaminharManifestacao(ActionEvent evt) throws ArqException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		detalharManifestacao(id, false);
		
		encaminharManifestacaoCarregada();
    }
    
    /**
     * Inicia os dados necess�rios e direciona o fluxo para a tela de encaminhamento da manifesta��o carregada.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @throws ArqException
     */
    public void encaminharManifestacaoCarregada() throws ArqException {
		historico = new HistoricoManifestacao();
		historico.setUnidadeResponsavel(new Unidade());
		responsavelUnidade = new Responsavel();
		unidadeSelecionadaCopia = new Unidade();
		initDadosResponsavelCopia();
		responsaveisUnidadesCopia = new ArrayList<Responsavel>();
		copiaPessoa = false;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 7);
		
		historico.setPrazoResposta(cal.getTime());
		historico.setSolicitacao("Para sua an�lise e encaminhamento do assunto.\nPe�o informar-nos das provid�ncias adotadas para a ci�ncia do interessado.");
		
		setOperacaoAtiva(ENCAMINHAR);
		
		prepareMovimento(SigaaListaComando.ENCAMINHAR_MANIFESTACAO_UNIDADE);
		prepareMovimento(SigaaListaComando.ENVIAR_COPIA_MANIFESTACAO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
    /**
     * Listener respons�vel por selecionar a unidade destino da manifesta��o escolhida pelo usu�rio.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @param e
     * @throws Exception
     */
    public void selecionarUnidade(ActionEvent e) throws Exception {
		UnidadeDAOImpl unidadeDao = getDAO(UnidadeDAOImpl.class);
		
		try {
		    Unidade unidade = null;
		    
		    Integer idUnidadeDestino = (Integer) e.getComponent().getAttributes().get("idUnidadeDestino");
		    
		    if(isEmpty(idUnidadeDestino) && isNotEmpty(historico.getUnidadeResponsavel().getCodigo())) {
				UnidadeGeral unidadeGeral = unidadeDao.findByCodigo(historico.getUnidadeResponsavel().getCodigo());
				idUnidadeDestino = 0;
				
				if(isNotEmpty(unidadeGeral)) {
				    unidade = new Unidade(unidadeGeral);
				    unidade.setNome(unidadeGeral.getNome());
				    unidade.setCodigo(unidadeGeral.getCodigo());
				    idUnidadeDestino = unidade.getId();
				}
		    }
		    if(isNotEmpty(idUnidadeDestino) && isEmpty(unidade)) {
		    	unidade = unidadeDao.findByPrimaryKey(idUnidadeDestino, Unidade.class, "id", "nome");
		    }
		    
		    historico.setUnidadeResponsavel(unidade);
		    
		    if(ValidatorUtil.isNotEmpty(idUnidadeDestino)) {
				carregarResponsavelUnidade(idUnidadeDestino);
		    }
		    if(ValidatorUtil.isEmpty(idUnidadeDestino) || responsavelUnidade == null) {
		    	responsavelUnidade = new Responsavel();
		    }
		} finally {
		    unidadeDao.close();
		}
    }
    
    /**
     * Listener respons�vel por selecionar a unidde escolhida para receber uma c�pia da manifesta��o
     * escolhida pelo usu�rio.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @param e
     * @throws Exception
     */
    public void selecionarUnidadeCopia(ActionEvent e) throws Exception {
		UnidadeDAOImpl unidadeDao = getDAO(UnidadeDAOImpl.class);
		
		try {
		    Unidade unidade = null;
		    
		    Integer idUnidadeDestino = (Integer) e.getComponent().getAttributes().get("idUnidadeDestino");
		    
		    if(isEmpty(idUnidadeDestino) && isNotEmpty(unidadeSelecionadaCopia.getCodigo())) {
				UnidadeGeral unidadeGeral = unidadeDao.findByCodigo(unidadeSelecionadaCopia.getCodigo());
				idUnidadeDestino = 0;
				
				if(isNotEmpty(unidadeGeral)) {
				    unidade = new Unidade(unidadeGeral);
				    unidade.setNome(unidadeGeral.getNome());
				    unidade.setCodigo(unidadeGeral.getCodigo());
				    idUnidadeDestino = unidade.getId();
				}
		    }
		    if(isNotEmpty(idUnidadeDestino) && isEmpty(unidade)) {
		    	unidade = unidadeDao.findByPrimaryKey(idUnidadeDestino, Unidade.class, "id", "nome");
		    }
		    
		    unidadeSelecionadaCopia = unidade;
		    
		    if(ValidatorUtil.isNotEmpty(idUnidadeDestino)) {
				carregarResponsavelUnidadeCopia(idUnidadeDestino);
		    }
		    if(ValidatorUtil.isEmpty(idUnidadeDestino) || responsavelUnidadeCopia == null) {
		    	responsavelUnidadeCopia = new Responsavel();
		    }
		} finally {
		    unidadeDao.close();
		}
    }

    /**
     * Carrega o respons�vel pela unidade que receber� uma c�pia da manifesta��o.
     * 
     * @param idUnidadeDestino
     */
    private void carregarResponsavelUnidadeCopia(Integer idUnidadeDestino) {
		ResponsavelUnidadeDAO responsavelDao = getDAO(ResponsavelUnidadeDAO.class);
		ServidorDAO servidorDao = getDAO(ServidorDAO.class, Sistema.COMUM);
		
		try {
			responsavelUnidadeCopia = responsavelDao.findResponsavelAtualByUnidade(idUnidadeDestino, NivelResponsabilidade.CHEFE);
			
			// Tratando erro de NullPointer (Lazy)
			if(isNotEmpty(responsavelUnidadeCopia) && isNotEmpty(responsavelUnidadeCopia.getServidor())) {
				responsavelUnidadeCopia.setServidor(servidorDao.findByPrimaryKey(responsavelUnidadeCopia.getServidor().getId()));
			}
		} finally {
			responsavelDao.close();
		    servidorDao.close();
		}
	}
    
    /**
     * Adiciona uma unidade na lista de recebimento de c�pias da manifesta��o.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @param e
     */
    public void adicionarUnidadeCopia(ActionEvent e) {
    	if(isEmpty(unidadeSelecionadaCopia)) {
    		addMensagemErroAjax("Nenhuma Unidade selecionada para c�pia.");
    		return;
    	}
    	
    	if(responsavelUnidadeCopia == null || isEmpty(responsavelUnidadeCopia.getServidor())) {
    		addMensagemErroAjax("Nenhum Servidor respons�vel encontrado para a unidade selecionada.");
    		return;
    	}
    	
    	if(unidadeSelecionadaCopia.equals(historico.getUnidadeResponsavel())) {
    		addMensagemErroAjax("N�o � poss�vel enviar c�pia para a unidade que ir� receber o encaminhamento da manifesta��o.");
    		return;
    	}
    	
    	if(isNotEmpty(responsaveisUnidadesCopia)) {
    		for (Responsavel r : responsaveisUnidadesCopia) {
    			if(unidadeSelecionadaCopia.equals(r.getUnidade())) {
    	    		addMensagemErroAjax("Unidade j� adicionada para receber c�pia.");
    	    		responsavelUnidadeCopia = new Responsavel();
    	    		unidadeSelecionadaCopia = new Unidade();
    	    		return;
    	    	}
			}
    	}
    	else {
    		responsaveisUnidadesCopia = new ArrayList<Responsavel>();
    	}
    	
    	responsavelUnidadeCopia.setUnidade(unidadeSelecionadaCopia);
    	
    	responsaveisUnidadesCopia.add(responsavelUnidadeCopia);
		unidadeSelecionadaCopia = new Unidade();
		initDadosResponsavelCopia();
    }
    
    /**
     * Adiciona a pessoa escolhida na lista de recebimento de c�pias da manifesta��o.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @param e
     * @throws DAOException
     */
    public void adicionarPessoaCopia(ActionEvent e) throws DAOException {
    	if(isEmpty(responsavelUnidadeCopia.getServidor().getPessoa())) {
    		addMensagemErroAjax("Nenhuma Pessoa selecionada.");
    		return;
    	}
    	
    	Pessoa p = getGenericDAO().findByPrimaryKey(responsavelUnidadeCopia.getServidor().getPessoa().getId(), Pessoa.class, "id", "nome", "email", "cpf_cnpj");
    	
    	if(p.getCpf_cnpj() == null) {
    		addMensagemErroAjax("N�o � poss�vel adicionar essa pessoa para receber c�pia pois ela n�o tem CPF cadastrado no sistema.");
    		return;
    	}
    	
    	responsavelUnidadeCopia.setUnidade(null);
		responsavelUnidadeCopia.getServidor().setPessoa(p);
    	
    	responsaveisUnidadesCopia.add(responsavelUnidadeCopia);
    	initDadosResponsavelCopia();
    }
    
    /**
     * Remove a pessoa escolhida da listagem de recebimento das c�pias da manifesta��o.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws DAOException
     */
    public void removerPessoaCopia(ActionEvent evt) throws DAOException {
    	Integer idPessoa = (Integer) evt.getComponent().getAttributes().get("idPessoaRemocao");
    	Responsavel r = new Responsavel();
    	
    	for (Responsavel responsavel : responsaveisUnidadesCopia) {
			if(responsavel.getServidor().getPessoa().getId() == idPessoa) {
				r = responsavel;
				break;
			}
		}
    	
    	responsaveisUnidadesCopia.remove(r);
    }
    
    
    
    /**
     * Recupera o id d amanifesta��o selecionada e chama o o m�todo que cancela a manifesta��o.
     * para a devida p�gina.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_pendentes.jsp</li>
	 * </ul>
     * 97052241
     * @param evt
     * @throws ArqException
     */
    public void removerManifestacao() throws ArqException {
		Integer id = getParameterInt("idManifestacao");
		
		if (id == null || id.equals(0)) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return;
		}
		
		// Verifica se o obj j� foi cancelado.
		obj = getGenericDAO().findByPrimaryKey(id, Manifestacao.class);
		if (obj != null && obj.getStatusManifestacao().getId() == StatusManifestacao.CANCELADA){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return;
		}
		
		this.obj = new Manifestacao();
		this.obj.setId(id);
		populateObj();
		this.obj.setStatusManifestacao(new StatusManifestacao(StatusManifestacao.CANCELADA, "Cancelada"));
		
				
		prepareMovimento(ArqListaComando.ALTERAR);
		
		removerManifestacaoCarregada();
    }
    
    /**
	 * Remove a manifesta��o selecionada.
	 * M�todo chamado pela seguinte JSP:
     * <ul>
	 * <li>sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_pendentes.jsp</li>
	 * </ul>
	 */
	private String removerManifestacaoCarregada() throws ArqException {
						
		MovimentoCadastro mov = new MovimentoCadastro();

		mov.setObjMovimentado(obj);
				
		mov.setCodMovimento(ArqListaComando.ALTERAR);

		if (isEmpty(obj)) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return redirect(getListPage());
			} 
			
			return listarPendentes();
		}
				
	}

    
    /**
     * Cadastra o encaminhamento da manifesta��o, cadastrando, se for o caso, as c�pias adicionadas.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String cadastrarEncaminhamento() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		obj.setAnonimatoSolicitado(obj.isAnonima());
		obj.setAnonima(manterSigilo);
		historico.setManifestacao(obj);
		mov.setObjMovimentado(historico);
		mov.setObjAuxiliar(responsavelUnidade);
		mov.setCodMovimento(SigaaListaComando.ENCAMINHAR_MANIFESTACAO_UNIDADE);
		
		MovimentoCadastro movCopia = null;
		if(isNotEmpty(responsaveisUnidadesCopia)) {
			movCopia = new MovimentoCadastro();
			movCopia.setObjAuxiliar(responsaveisUnidadesCopia);
			movCopia.setObjMovimentado(historico);
			movCopia.setCodMovimento(SigaaListaComando.ENVIAR_COPIA_MANIFESTACAO);
		}
		
		try {
		    execute(mov);
		    if(movCopia != null)
		    	execute(movCopia);
		    
		    addMensagemInformation("Manifesta��o sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> encaminhada com sucesso para a Unidade respons�vel.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarPendentes();
    }
    
    /**
     * Carrega o id da manifesta��o selecionada e chama o detalhamento da manifesta��o e o encaminhamento
     * do fluxo.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_pendentes.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void editarManifestacao(ActionEvent evt) throws ArqException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		detalharManifestacao(id, false);
		
		editarManifestacaoCarregada();
    }
    
    /**
     * Direciona o fluxo para a tela de encaminhamento da manifesta��o carregada.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_pendentes.jsp</li>
	 * </ul>
     * 
     * @throws ArqException
     */
    public void editarManifestacaoCarregada() throws ArqException {
		setOperacaoAtiva(EDITAR);
		
		prepareMovimento(SigaaListaComando.EDITAR_MANIFESTACAO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
    /**
     * Inicia a edi��o de uma manifesta��o.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String alterarDadosManifestacao() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.EDITAR_MANIFESTACAO);
		
		try {
		    execute(mov);
		    
		    addMensagemInformation("Manifesta��o sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> editada com sucesso.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarPendentes();
    }
    
    /**
     * Popula a lista de manifesta��es e direciona o usu�rio para a tela de acompanhamento.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String listarManifestacoes() throws DAOException {
    	categoriaAssuntoManifestacao = new CategoriaAssuntoManifestacao();
    	getPaginacao().setPaginaAtual(0);
    	manifestacoes = new ArrayList<Manifestacao>();
    	popularManifestacoes();
		return acompanharManifestacao();
    }
    
    /**
     * Popula as manifesta��es para acompanhamento da ouvidoria.
     * 
     * @throws DAOException
     */
    private void popularManifestacoes() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		try {
			manifestacoes = dao.findManifestacoesAcompanhamentoOuvidoria(ano, numero, dataInicial, dataFinal, categoriaAssuntoManifestacao, nome, matriculaSiape, cpf, getPaginacao());
		} finally {
			dao.close();
		}
	}
    
    /**
     * Direciona o fluxo para a p�gina de acompanhamento de manifesta��es.<br />
     * M�todo n�o invocado por JSPs.
     * 
     * @return
     */
    public String acompanharManifestacao() {
    	return forward(getDirBase() + "/acompanhar.jsp");
    }
    
    /**
     * Carrega o id da manifesta��o escolhida para detalh�-la e encaminhar o usu�rio para a tela de
     * altera��o do prazo da manifesta��o.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_pendentes.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void alterarPrazoManifestacao(ActionEvent evt) throws ArqException {
    	Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		detalharManifestacao(id, false);
		
		alterarPrazoManifestacaoCarregada();
    }
    
    /**
     * Chama o direcionamento de fluxo para altera��o de prazo de uma manifesta��o 
     * que j� foi carregada previamente.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @throws ArqException
     */
    public void alterarPrazoManifestacaoCarregada() throws ArqException {
    	setOperacaoAtiva(ALTERAR_PRAZO);
    	
    	for (HistoricoManifestacao h : historicos) {
			if(h.getResposta() == null) {
				historico = h;
				carregarResponsavelUnidade(historico.getUnidadeResponsavel().getId());
				prazoResposta = historico.getPrazoResposta();
				break;
			}
		}
		
		prepareMovimento(SigaaListaComando.ALTERAR_PRAZO_RESPOSTA_MANIFESTACAO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
	}
    
    /**
     * Registra a altera��o de prazo de resposta da manifesta��o.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String alterarPrazoRespostaManifestacao() throws ArqException {
    	if(prazoResposta.equals(historico.getPrazoResposta()))
    		addMensagemErro("O novo prazo � igual ao prazo j� cadastrado. Favor modificar o prazo para prosseguir.");
    	
    	if(hasErrors())
    		return null;
    	
		MovimentoCadastro mov = new MovimentoCadastro();
		historico.setManifestacao(obj);
		mov.setObjMovimentado(historico);
		mov.setObjAuxiliar(responsavelUnidade);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_PRAZO_RESPOSTA_MANIFESTACAO);
		
		try {
		    execute(mov);
		    
		    addMensagemInformation("Prazo de resposta da manifesta��o sob o protocolo/ano <b>" + obj.getNumeroAno() + "</b> alterado com sucesso.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarEncaminhadas();
    }
    
    /**
     * Carrega o id da manifesta��o selecionada e chama o detalhamento da manifesta��o e o encaminhamento
     * do fluxo.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_respondidas.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void finalizarManifestacaoRespondida(ActionEvent evt) throws ArqException {
		acompanhar = false;
    	Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		detalharManifestacao(id, false);
		finalizarManifestacaoCarregada();
    }
    
    /**
     * Carrega o id da manifesta��o selecionada e chama o detalhamento da manifesta��o e o encaminhamento
     * do fluxo.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/acompanhar.jsp</li>
	 * </ul>
     * 
     * @param evt
     * @throws ArqException
     */
    public void finalizarManifestacaoRespondidaAcompanhar(ActionEvent evt) throws ArqException {
		acompanhar = true;
    	Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		detalharManifestacao(id, false);
		finalizarManifestacaoCarregada();
    }
       
    /**
     * Direciona o fluxo para a tela de encaminhamento da manifesta��o carregada.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/listar_respondidas.jsp</li>
	 * </ul>
     * 
     * @throws ArqException
     */
    public void finalizarManifestacaoCarregada() throws ArqException {
		setOperacaoAtiva(FINALIZAR);
		historico = new HistoricoManifestacao();
		
		prepareMovimento(SigaaListaComando.FINALIZAR_MANIFESTACAO);
		
		for (HistoricoManifestacao h : historicos) {
			if(h.isRespostaUsuario() || isNotEmpty(h.getResposta()) && isNotEmpty(h.getDataResposta())) {
				historico.setResposta(h.getResposta());
				break;
			}
		}
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
        
    /**
     * Registra a finaliza��o da manifesta��o.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String registrarFinalizacaoManifestacao() throws ArqException {
    	
    	if(!getUsuarioLogado().isUserInRole( new int[]{SigaaPapeis.OUVIDOR} ) ){
    		if ( !obj.isRespondida() && isEmpty(historico.getResposta())) 
    			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta");
    	} else {
    		if ( !obj.isRespondida() && isEmpty(historico.getResposta())) 
    			historico.setResposta("Manifesta��o finalizada sem resposta virtual. Para mais informa��o procure a ouvidoria.");
    	}
    		
    	
    	if(hasErrors())
    		return null;
    	
		MovimentoCadastro mov = new MovimentoCadastro();
		historico.setManifestacao(obj);
		mov.setObjMovimentado(historico);
		mov.setCodMovimento(SigaaListaComando.FINALIZAR_MANIFESTACAO);
		
		boolean somenteFinalizar = obj.isRespondida();
		
		try {
		    execute(mov);
		    
		    if(!somenteFinalizar)
		    	addMensagemInformation("Resposta enviada com sucesso para a Manifesta��o sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b>.");
		    
		    addMensagemInformation("Manifesta��o sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> finalizada com sucesso.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		if (!acompanhar)
			return listarRespondidas();
		else
			return listarManifestacoes();
    }
          
    /**
     * Busca manifesta��es de acordo com os filtros indicados.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/acompanhar.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws DAOException
     */
	public String buscarManifestacao() throws DAOException {
		validarDadosBusca();
		
		if(hasErrors())
		    return null;
		
		anularDadosNaoUsadosBusca();
		
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoes = dao.findManifestacoesAcompanhamentoOuvidoria(ano, numero, dataInicial, dataFinal, categoriaAssuntoManifestacao, nome, matriculaSiape, cpf, getPaginacao());
		
		if(isEmpty(manifestacoes))
		    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return acompanharManifestacao();
    }

	/**
	 * Anula os dados que n�o foram marcados para uso na busca.
	 * 
	 */
	private void anularDadosNaoUsadosBusca() {
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
		if(!buscaNome) {
			nome = null;
		}
		if(!buscaMatriculaSiape) {
			matriculaSiape = null;
		}
		if(!buscaCpf) {
			cpf = null;
		}
	}

	/**
	 * Valida os dados informados para busca da manifesta��o, adicionando os erros relacionados.
	 * 
	 */
	private void validarDadosBusca() {
		if(buscaNumeroAno && isEmpty(ano))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if(buscaNumeroAno && isEmpty(numero))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "N�mero");
		if(buscaPeriodo && isEmpty(dataInicial))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Inicial");
		if(buscaPeriodo && isEmpty(dataFinal))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Final");
		if(buscaCategoriaAssunto && isEmpty(categoriaAssuntoManifestacao))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria do Assunto");
		if(buscaNome && isEmpty(nome))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome do Manifestante");
		if(buscaMatriculaSiape && isEmpty(matriculaSiape))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Matr�cula/SIAPE do Manifestante");
		if(buscaCpf && isEmpty(cpf))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF do Manifestante");
 		if (dataInicial != null && dataFinal != null && dataInicial.getTime() > dataFinal.getTime())
			addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM,"Per�odo de Cadastro");
	}
	
	/**
	 * Limpa as manifesta��es e chama o m�todo de pagina��o para avan�ar a p�gina.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/acompanhar.jsp</li>
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
	 * Limpa as manifesta��es e chama o m�todo de pagina��o para voltar a p�gina.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/acompanhar.jsp</li>
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
	 * Limpa as manifesta��es e chama o m�todo de pagina��o para trocar a p�gina.<br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/acompanhar.jsp</li>
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
	 * Retorna todos os assuntos associados � categoria de assunto selecionada.
	 * 
	 * @return
	 * @throws DAOException
	 */
    public Collection<AssuntoManifestacao> getAllAssuntosByCategoria() throws DAOException {
        AssuntoManifestacaoDao dao = getDAO(AssuntoManifestacaoDao.class);
        Collection<AssuntoManifestacao> assuntos = new ArrayList<AssuntoManifestacao>();
        
        try {
            if(obj != null && obj.getAssuntoManifestacao() != null
        	    && isNotEmpty(obj.getAssuntoManifestacao().getCategoriaAssuntoManifestacao()))
        	assuntos = dao.getAllAssuntosAtivosByCategoria(obj.getAssuntoManifestacao().getCategoriaAssuntoManifestacao());
        } finally {
            dao.close();
        }
        
        return assuntos;
    }

    /**
     * Retorna um combo contendo os assuntos associados � categoria de assunto selecionada.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getAllAssuntosByCategoriaCombo() throws DAOException {
        return toSelectItems(getAllAssuntosByCategoria(), "id", "descricao");
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
    
    /**
     * Indica se o interessado � um discente.
     * 
     * @return
     */
    public boolean isCategoriaDiscente() {
    	return isComunidadeInterna() && verificarCategoria(CategoriaSolicitante.DISCENTE);
    }
    
    /**
     * Indica se o interessado � da comunidade interna.
     * 
     * @return
     */
    public boolean isComunidadeInterna() {
    	return !verificarCategoria(CategoriaSolicitante.COMUNIDADE_EXTERNA);
    }
    
    @Override
    public String getDirBase() {
    	return super.getDirBase() + "/ouvidor";
    }

    /**
     * Retorna a lista p�ginada de manifesta��es para companhamento.
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
	
	public Unidade getUnidadeSelecionadaCopia() {
		return unidadeSelecionadaCopia;
	}

	public void setUnidadeSelecionadaCopia(Unidade unidadeSelecionadaCopia) {
		this.unidadeSelecionadaCopia = unidadeSelecionadaCopia;
	}

	public Responsavel getResponsavelUnidadeCopia() {
		return responsavelUnidadeCopia;
	}

	public void setResponsavelUnidadeCopia(Responsavel responsavelUnidadeCopia) {
		this.responsavelUnidadeCopia = responsavelUnidadeCopia;
	}

	public Collection<Responsavel> getResponsaveisUnidadesCopia() {
		return responsaveisUnidadesCopia;
	}

	public void setResponsaveisUnidadesCopia(
			Collection<Responsavel> responsaveisUnidadesCopia) {
		this.responsaveisUnidadesCopia = responsaveisUnidadesCopia;
	}

	public boolean isCopiaPessoa() {
		return copiaPessoa;
	}

	public void setCopiaPessoa(boolean copiaPessoa) {
		this.copiaPessoa = copiaPessoa;
	}

	public Date getPrazoResposta() {
		return prazoResposta;
	}

	public void setPrazoResposta(Date prazoResposta) {
		this.prazoResposta = prazoResposta;
	}

	public boolean isManterSigilo() {
		return manterSigilo;
	}

	public void setManterSigilo(boolean manterSigilo) {
		this.manterSigilo = manterSigilo;
	}
	
	public boolean isUsuarioOuvidor() {
		return getUsuarioLogado().isUserInRole( new int[]{SigaaPapeis.OUVIDOR} );
	}

	public void setAcompanhar(boolean acompanhar) {
		this.acompanhar = acompanhar;
	}

	public boolean isAcompanhar() {
		return acompanhar;
	}
	
}
