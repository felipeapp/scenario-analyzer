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
 * Controller ersponsável pelas operações de análise de manifestações pelos usuários da ouvidoria.
 * 
 * @author Bernardo
 *
 */
@Component(value="analiseManifestacaoOuvidoria") @Scope(value="request")
public class AnaliseManifestacaoOuvidoriaMBean extends AnaliseManifestacaoAbstractController {
	
	/** Prazo de resposta definido para o histórico carregado. */
	private Date prazoResposta;
	
	/** Unidade selecionada no formulário de cópia de manifestação. */
	private Unidade unidadeSelecionadaCopia;
	
	/** Responsável pela unidade selecionada no formulário de cópia de manifestação. */
	private Responsavel responsavelUnidadeCopia;
	
	/** Armazena a lista de {@link Responsavel} e {@link Pessoa} adicionadas para receberem uma cópia da manifestação. */
	private Collection<Responsavel> responsaveisUnidadesCopia;
	
	/** Indica se o usuário deseja adicionar uma pessoa para receber a cópia da manifestação. */
	private boolean copiaPessoa;
	
	/** Indica se, no momento do encaminhamento da manifestação, o sigilo do manifestante será mantido. */
	private boolean manterSigilo;
	
	/** Indica se é para voltar a tela de acompanhamento após finalizar uma manifestação */
	private boolean acompanhar;
    
    public AnaliseManifestacaoOuvidoriaMBean() {
    	init();
    }
    
    /**
     * Inicia os dados necessários para adicionar pessoas ou unidades para receberem cópias da manifestação.
     * 
     */
	private void initDadosResponsavelCopia() {
		responsavelUnidadeCopia = new Responsavel();
    	responsavelUnidadeCopia.setServidor(new Servidor());
		responsavelUnidadeCopia.getServidor().setPessoa(new PessoaGeral());
	}

	/**
	 * Lista as manifestações pendentes de análise para a ouvidoria e 
	 * direciona o usuário para a listagem de manifestações pendentes.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
		    addMensagemWarning("Não existem manifestações pendentes cadastradas no sistema.");
		    return cancelar();
		}
		
		removeOperacaoAtiva();
		
		return forward(getDirBase() + "/listar_pendentes.jsp");
    }
    
    // Operações do caso de uso de Listar Manifestações Encaminhadas
    
    /**
     * Lista as manifestações já encaminhadas pela ouvidoria, mas que
     * ainda não foram respondidas ao usuário e direciona o fluxo para
     * a tela de listagem das manifestaões encaminhadas.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
		    addMensagemWarning("Não existem manifestações encaminhadas ou designadas cadastradas no sistema.");
		    return cancelar();
		}
		
		removeOperacaoAtiva();
		
		return forward(getDirBase() + "/listar_encaminhadas.jsp");
    }
    
    /**
     * Popula os dados e retorna todas as manifestações encaminhadas pela ouvidoria. 
     * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Retorna a lista páginada de manifestações que estão sendo encaminhas.
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Busca manifestações encaminhadas de acordo com os filtros indicados.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
    
    // Operações do caso de uso de Listar Manifestações Respondidas
	
    /**
     * Lista as manifestações já encaminhadas pela ouvidoria que
     * já foram respondidas e direciona o fluxo para
     * a tela de listagem das manifestaões respondidas.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
		    addMensagemWarning("Não existem manifestações respondidas cadastradas no sistema.");
		    return cancelar();
		}
		
		removeOperacaoAtiva();
		
		return forward(getDirBase() + "/listar_respondidas.jsp");
    }

    /**
     * Popula a listagem de manifestações pendentes de análise.
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
     * Popula a listagem de manifestações encaminhadas pela ouvidoria.
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
     * Popula a listagem de manifestações já respondidas.
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
     * Popula os dados e retorna as manifestações pendentes de análise pela ouvidoria. 
     * 
     * @return
     * @throws DAOException
     */
    public Collection<Manifestacao> getManifestacoesPendentes() throws DAOException {
		popularManifestacoesPendentes();
		
		return manifestacoes;
    }
    
    /**
     * Popula os dados e retorna as manifestações encaminhadas pela ouvidoria. 
     * 
     * @return
     * @throws DAOException
     */
    public Collection<Manifestacao> getManifestacoesRespondidas() throws DAOException {
		popularManifestacoesRespondidas();
		
		return manifestacoes;
    }
    
    /**
     * Inicia o processo de detalhamento de uma manifestação encaminhada.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Inicia o processo de detalhamento de uma manifestação respondida.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Recupera o id da manifestação selecionada para detalhar seus dados.<br />
     * Método não invocado por JSPs.
     */
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		
		detalharManifestacao(id, true);
    }

    /**
     * Detalha a manifestação e, de acordo com o parâmetro passado, direciona o usuário
     * para a página de detalhamento da manifestação.
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
     * Inicia o proceso de responder uma manifestação pendente.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Inicia o processo de responder uma manifestação encaminhada.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Carrega o id da manifestação que o usuário selecionou para responder, detalha seus dados
     * e chama o direcionamento do fluxo.<br />
     * Método não invocado por JSPs.
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
     * Chama o direcionamento de fluxo para uma manifestação encaminhada que já foi carregada previamente.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Chama o direcionamento de fluxo para uma manifestação pendente que já foi carregada previamente.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Chama o direcionamento de fluxo para uma manifestação que já foi carregada previamente.<br />
     * Método não invocado por JSPs.
     * 
     * @throws ArqException
     */
	public void responderManifestacaoCarregada() throws ArqException {
		historico = new HistoricoManifestacao();
		
		prepareMovimento(SigaaListaComando.ENVIAR_RESPOSTA_USUARIO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
	}
    
	/**
	 * Trata de enviar a resposta da manifestação ao manifestante.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
		    
		    addMensagemInformation("Resposta enviada com sucesso para a manifestação sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b>.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		if(isOperacaoPendente())
			return listarPendentes();
		
		return listarEncaminhadas();
    }
    
    /**
     * Recupera o id d amanifestação selecionada e chama o detalhamento da manifestação e o encaminhamento
     * para a devida página.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Inicia os dados necessários e direciona o fluxo para a tela de encaminhamento da manifestação carregada.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
		historico.setSolicitacao("Para sua análise e encaminhamento do assunto.\nPeço informar-nos das providências adotadas para a ciência do interessado.");
		
		setOperacaoAtiva(ENCAMINHAR);
		
		prepareMovimento(SigaaListaComando.ENCAMINHAR_MANIFESTACAO_UNIDADE);
		prepareMovimento(SigaaListaComando.ENVIAR_COPIA_MANIFESTACAO);
		
		forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
    /**
     * Listener responsável por selecionar a unidade destino da manifestação escolhida pelo usuário.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Listener responsável por selecionar a unidde escolhida para receber uma cópia da manifestação
     * escolhida pelo usuário.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Carrega o responsável pela unidade que receberá uma cópia da manifestação.
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
     * Adiciona uma unidade na lista de recebimento de cópias da manifestação.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @param e
     */
    public void adicionarUnidadeCopia(ActionEvent e) {
    	if(isEmpty(unidadeSelecionadaCopia)) {
    		addMensagemErroAjax("Nenhuma Unidade selecionada para cópia.");
    		return;
    	}
    	
    	if(responsavelUnidadeCopia == null || isEmpty(responsavelUnidadeCopia.getServidor())) {
    		addMensagemErroAjax("Nenhum Servidor responsável encontrado para a unidade selecionada.");
    		return;
    	}
    	
    	if(unidadeSelecionadaCopia.equals(historico.getUnidadeResponsavel())) {
    		addMensagemErroAjax("Não é possível enviar cópia para a unidade que irá receber o encaminhamento da manifestação.");
    		return;
    	}
    	
    	if(isNotEmpty(responsaveisUnidadesCopia)) {
    		for (Responsavel r : responsaveisUnidadesCopia) {
    			if(unidadeSelecionadaCopia.equals(r.getUnidade())) {
    	    		addMensagemErroAjax("Unidade já adicionada para receber cópia.");
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
     * Adiciona a pessoa escolhida na lista de recebimento de cópias da manifestação.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
    		addMensagemErroAjax("Não é possível adicionar essa pessoa para receber cópia pois ela não tem CPF cadastrado no sistema.");
    		return;
    	}
    	
    	responsavelUnidadeCopia.setUnidade(null);
		responsavelUnidadeCopia.getServidor().setPessoa(p);
    	
    	responsaveisUnidadesCopia.add(responsavelUnidadeCopia);
    	initDadosResponsavelCopia();
    }
    
    /**
     * Remove a pessoa escolhida da listagem de recebimento das cópias da manifestação.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Recupera o id d amanifestação selecionada e chama o o método que cancela a manifestação.
     * para a devida página.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
		
		// Verifica se o obj já foi cancelado.
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
	 * Remove a manifestação selecionada.
	 * Método chamado pela seguinte JSP:
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
     * Cadastra o encaminhamento da manifestação, cadastrando, se for o caso, as cópias adicionadas.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
		    
		    addMensagemInformation("Manifestação sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> encaminhada com sucesso para a Unidade responsável.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarPendentes();
    }
    
    /**
     * Carrega o id da manifestação selecionada e chama o detalhamento da manifestação e o encaminhamento
     * do fluxo.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Direciona o fluxo para a tela de encaminhamento da manifestação carregada.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Inicia a edição de uma manifestação.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
		    
		    addMensagemInformation("Manifestação sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> editada com sucesso.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarPendentes();
    }
    
    /**
     * Popula a lista de manifestações e direciona o usuário para a tela de acompanhamento.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Popula as manifestações para acompanhamento da ouvidoria.
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
     * Direciona o fluxo para a página de acompanhamento de manifestações.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     */
    public String acompanharManifestacao() {
    	return forward(getDirBase() + "/acompanhar.jsp");
    }
    
    /**
     * Carrega o id da manifestação escolhida para detalhá-la e encaminhar o usuário para a tela de
     * alteração do prazo da manifestação.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Chama o direcionamento de fluxo para alteração de prazo de uma manifestação 
     * que já foi carregada previamente.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Registra a alteração de prazo de resposta da manifestação.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/detalhes_manifestacao.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String alterarPrazoRespostaManifestacao() throws ArqException {
    	if(prazoResposta.equals(historico.getPrazoResposta()))
    		addMensagemErro("O novo prazo é igual ao prazo já cadastrado. Favor modificar o prazo para prosseguir.");
    	
    	if(hasErrors())
    		return null;
    	
		MovimentoCadastro mov = new MovimentoCadastro();
		historico.setManifestacao(obj);
		mov.setObjMovimentado(historico);
		mov.setObjAuxiliar(responsavelUnidade);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_PRAZO_RESPOSTA_MANIFESTACAO);
		
		try {
		    execute(mov);
		    
		    addMensagemInformation("Prazo de resposta da manifestação sob o protocolo/ano <b>" + obj.getNumeroAno() + "</b> alterado com sucesso.");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return listarEncaminhadas();
    }
    
    /**
     * Carrega o id da manifestação selecionada e chama o detalhamento da manifestação e o encaminhamento
     * do fluxo.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Carrega o id da manifestação selecionada e chama o detalhamento da manifestação e o encaminhamento
     * do fluxo.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Direciona o fluxo para a tela de encaminhamento da manifestação carregada.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
     * Registra a finalização da manifestação.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
    			historico.setResposta("Manifestação finalizada sem resposta virtual. Para mais informação procure a ouvidoria.");
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
		    	addMensagemInformation("Resposta enviada com sucesso para a Manifestação sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b>.");
		    
		    addMensagemInformation("Manifestação sob o protocolo/ano <b>" + obj.getNumero() + "/" + obj.getAno() + "</b> finalizada com sucesso.");
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
     * Busca manifestações de acordo com os filtros indicados.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
	 * Anula os dados que não foram marcados para uso na busca.
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
	 * Valida os dados informados para busca da manifestação, adicionando os erros relacionados.
	 * 
	 */
	private void validarDadosBusca() {
		if(buscaNumeroAno && isEmpty(ano))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if(buscaNumeroAno && isEmpty(numero))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número");
		if(buscaPeriodo && isEmpty(dataInicial))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Inicial");
		if(buscaPeriodo && isEmpty(dataFinal))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Final");
		if(buscaCategoriaAssunto && isEmpty(categoriaAssuntoManifestacao))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria do Assunto");
		if(buscaNome && isEmpty(nome))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome do Manifestante");
		if(buscaMatriculaSiape && isEmpty(matriculaSiape))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Matrícula/SIAPE do Manifestante");
		if(buscaCpf && isEmpty(cpf))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF do Manifestante");
 		if (dataInicial != null && dataFinal != null && dataInicial.getTime() > dataFinal.getTime())
			addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM,"Período de Cadastro");
	}
	
	/**
	 * Limpa as manifestações e chama o método de paginação para avançar a página.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
	 * Limpa as manifestações e chama o método de paginação para voltar a página.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
	 * Limpa as manifestações e chama o método de paginação para trocar a página.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
	 * Retorna todos os assuntos associados à categoria de assunto selecionada.
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
     * Retorna um combo contendo os assuntos associados à categoria de assunto selecionada.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getAllAssuntosByCategoriaCombo() throws DAOException {
        return toSelectItems(getAllAssuntosByCategoria(), "id", "descricao");
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
    
    /**
     * Indica se o interessado é um discente.
     * 
     * @return
     */
    public boolean isCategoriaDiscente() {
    	return isComunidadeInterna() && verificarCategoria(CategoriaSolicitante.DISCENTE);
    }
    
    /**
     * Indica se o interessado é da comunidade interna.
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
     * Retorna a lista páginada de manifestações para companhamento.
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
