/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/11/2008
 *
 */

package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.chat.ChatEngine;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.GrupoDestinatarios;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.cv.dao.MembroComunidadeDao;
import br.ufrn.sigaa.ava.dominio.RegistroAtividadeTurma;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.EnqueteComunidade;
import br.ufrn.sigaa.cv.dominio.ForumComunidade;
import br.ufrn.sigaa.cv.dominio.ForumMensagemComunidade;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;
import br.ufrn.sigaa.cv.dominio.NoticiaComunidade;
import br.ufrn.sigaa.cv.dominio.SolicitacaoParticipacaoComunidade;
import br.ufrn.sigaa.cv.dominio.TipoComunidadeVirtual;
import br.ufrn.sigaa.cv.negocio.MovimentoCadastroCv;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean respons�vel por efetuar opera��es na Comunidade Virtual
 * 
 * @author David Pereira
 * @author Agostinho Campos
 * 
 */
@Component
@Scope("session")
public class ComunidadeVirtualMBean extends SigaaAbstractController<ComunidadeVirtual> {

	/** Vari�vel representativa de uma comunidade virtual. */
	private ComunidadeVirtual comunidade;
	
	/** Link referente a documenta��o da comunidade virtual no WIKI */
	private static final String WIKI_COMUNIDADE_VIRTUAL = "http://www.info.ufrn.br/wikisistemas/doku.php?id=suporte:sigaa:comunidades_virtuais"; 

	/** Verificadores dos tipos de usu�rio da comunidade virtual*/
	/** Se o usu�rio for o dono da comunidade virtual */
	private boolean dono;
	/** Se o usu�rio pode administrar o conte�do da comunidade virtual */
	private boolean adminConteudo;
	/** Se o usu�rio for convidado da comunidade virtual */
	private boolean convidado;

	/** Vari�vel representativa de um membro de uma comunidade virtual. */
	private MembroComunidade membro;

	/** Lista dos participantes da comunidade virtual. */
	private Set<MembroComunidade> participantes = new LinkedHashSet<MembroComunidade>();

	/** Lista das solicita��es de participa��o da comunidade virtual. */
	private List<SolicitacaoParticipacaoComunidade> listaSolicitacoes = new ArrayList<SolicitacaoParticipacaoComunidade>();

	/** Lista dos t�picos de mensagens da comunidade virtual. */
	private List<ForumMensagemComunidade> topicosComunidadeAtual = new ArrayList<ForumMensagemComunidade>();

	/** Lista das enquentes da comunidade virtual. */
	private List<EnqueteComunidade> enquentesAtuais = new ArrayList<EnqueteComunidade>();

	/**
	 * Inst�ncia o dom�nio ComunidadeVirtual
	 * 
	 * @throws DAOException
	 */
	public ComunidadeVirtualMBean() throws DAOException {
		obj = new ComunidadeVirtual();
		comunidade  = new ComunidadeVirtual();
	}

	/**
	 * Prepara o movimento e exibe a p�gina para criar uma nova Comunidade Virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/criar.jsp
	 * 
	 * @param e
	 * @throws ArqException
	 */
	public void criar(ActionEvent e) throws ArqException {
		
		if ( getUsuarioLogado().getVinculoAtivo().getServidor() != null || getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno() || getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_AMBIENTES_VIRTUAIS)) {
			obj = new ComunidadeVirtual();
			obj.setTipoComunidadeVirtual(new TipoComunidadeVirtual(TipoComunidadeVirtual.PUBLICA));
	
			prepareMovimento(SigaaListaComando.CADASTRAR_COMUNIDADE_VIRTUAL);
			forward("/cv/criar.jsp");
		}	
	}

	/**
	 * Lista todos os grupos que s�o do tipo 'Comunidade Virtual'.
	 * 
	 * @return
	 */
	private List<GrupoDestinatarios> getGrupos() throws DAOException {
		MembroComunidadeDao comunidadeVirtDao = getDAO(MembroComunidadeDao.class);
		return comunidadeVirtDao.findAllGrupoDestinatarios();
	}

	/**
	 * Transforma a lista de grupos em List<SelectItem> para exibir em um combobox.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/criar.jsp
	 */
	public List<SelectItem> getGruposCombo() throws DAOException {
		return toSelectItems(getGrupos(), "id", "descricaoCompleta");
	}

	/**
	 * Abre a p�gina de configura��o da comunidade virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 * @throws HibernateException
	 */
	public String configuracoesComunidade() throws DAOException {
		listarSolicitacoesPendentes();
		return forward("/cv/Configuracoes/conf.jsp");
	}

	/**
	 * Altera dados da Comunidade (Nome e Descri��o).<br/><br/>
	 * 
	 * Chamado por:
	 * sigaa.war/cv/Configuracoes/conf.jsp
	 * 
	 * 
	 * @throws ArqException 
	 */
	public void alterarDadosComunidade() throws ArqException {
		prepareMovimento(SigaaListaComando.ATUALIZAR_CV);

		try {
			
			MovimentoCadastroCv mov = new MovimentoCadastroCv();
			mov.setCodMovimento(SigaaListaComando.ATUALIZAR_CV);
			mov.setObjMovimentado(comunidade);
			mov.setSpecification(new NullSpecification());
			execute(mov);
			
			addMensagemInformation("Dados da Comunidade Virtual alterados com sucesso!");
			
		} catch (NegocioException e) {
			notifyError(e);
		}
	}
	
	
	/**
	 * Desativa uma Comunidade Virtual (deixa seu status ativo = false).<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/Configuracoes/conf.jsp
	 */
	public String desativarComunidadeVirtual() throws ArqException {
		ComunidadeVirtual cv = getComunidade();
		cv.setAtiva(false);

		prepareMovimento(ArqListaComando.ALTERAR);

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(cv);
		mov.setCodMovimento(ArqListaComando.ALTERAR);
		try {
			execute(mov);
		} catch (NegocioException e) {
			notifyError(e);
		}
		
		BuscarComunidadeVirtualMBean buscarBean = getMBean("buscarComunidadeVirtualMBean");
		buscarBean.setComunidadesPorPessoa(null);
		addMessage("Comunidade Virtual desativada com sucesso!",TipoMensagemUFRN.INFORMATION);
		return redirecionarDeAcordoUsuarioLogado();
	}

	/**
	 * Ap�s uma Comunidade Virtual ser desativada, o usu�rio logado
	 * � redirecionado para seu portal correspondente.
	 * @return
	 */
	protected String redirecionarDeAcordoUsuarioLogado() {
		Servidor s = getUsuarioLogado().getServidor();
		Discente d = getUsuarioLogado().getDiscente();

		if (d != null) {
			return redirectJSF("/sigaa/portais/discente/discente.jsf");
		}else if (s != null && s.isDocente()) {
			return redirectJSF("/sigaa/portais/docente/docente.jsf");
		}
		return redirectJSF("/sigaa/cv/visualizar_comunidades.jsf");
	}

	/**
	 * Permite um usu�rio visualizar uma comunidade do tipo Moderada 
	 * com permiss�o setada apenas como visualiza��o.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/buscar_comunidade.jsp
	 *  
	 * @param evt
	 * @return
	 * @throws ArqException 
	 */
	public String visualizarComunidadeVirtualModerada(ActionEvent evt) throws ArqException {
		comunidade = getComunidadeVirtualByID(getIDComunidadeVirtualFromView(evt));
		membro = new MembroComunidade();
		membro.setPermissao(MembroComunidade.VISITANTE);

		try {
			findTopicosForumComunidadeAtual();
			findEnquetesComunidadeAtual();
			atualizarListaParticipantesComunidade();
	
			setSubSistemaAtual(SigaaSubsistemas.COMUNIDADE_VIRTUAL);
			return redirect("/cv/index.jsf");
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}
	}

	/**
	 * Recupera o ID da comunidade atrav�s do ActvionEvent.
	 *  
	 * @param evt
	 * @return
	 */
	private Integer getIDComunidadeVirtualFromView(ActionEvent evt) {
		Integer idComunidade = (Integer) evt.getComponent().getAttributes().get("idComunidade");
		return idComunidade;
	}

	/**
	 * Retorna uma Comunidade Virtual de acordo com seu ID.
	 * 
	 * @param idComunidade
	 * @return
	 * @throws DAOException
	 */
	private ComunidadeVirtual getComunidadeVirtualByID(Integer idComunidade)
			throws DAOException {
		ComunidadeVirtual cv = getDAO(ComunidadeVirtualDao.class)
				.findByPrimaryKey(idComunidade, ComunidadeVirtual.class);
		return cv;
	}

	/**
	 * Exibe as solicita��es de usu�rios que est�o pendentes para participarem 
	 * de uma comunidade do tipo Moderada.
	 * 
	 * @throws DAOException
	 */
	private void listarSolicitacoesPendentes() throws DAOException {
		listaSolicitacoes = getDAO(ComunidadeVirtualDao.class).findAllSolicitacoesPendentes(getComunidade().getId());
	}

	/**
	 * Autoriza a participa��o da pessoa na comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/Configuracoes/conf.jsp
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String liberarParticipacaoUsuarioComunidade() throws NegocioException, ArqException {
		Integer idPessoa = getParameterInt("idPessoa");
		Integer idSolicit = getParameterInt("id");

		prepareMovimento(ArqListaComando.CADASTRAR);
		
		MembroComunidade membroComunidade = new MembroComunidade();
		membroComunidade.setAtivo(true);
		membroComunidade.setComunidade(comunidade);
		membroComunidade.setPermissao(MembroComunidade.MEMBRO);
		membroComunidade.setPessoa(getDAO(GenericDAOImpl.class).findByPrimaryKey(idPessoa, Pessoa.class));

		atualizarListaParticipantesComunidade();
		if (!comunidade.getParticipantesComunidade().contains(membroComunidade) ) {
			// cadastra o usu�rio na comunidade
			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setObjMovimentado(membroComunidade);
			movCad.setCodMovimento(ArqListaComando.CADASTRAR);
	
			execute(movCad);
	
			prepareMovimento(ArqListaComando.ALTERAR);
			
			SolicitacaoParticipacaoComunidade particComunidadeVirtual = getDAO(ComunidadeVirtualDao.class).findByPrimaryKey(idSolicit, SolicitacaoParticipacaoComunidade.class);
			particComunidadeVirtual.setAceitoComunidade(true);
			particComunidadeVirtual.setPendenteDecisao(false);
	
			// seta a solicita��o para j� realizada
			MovimentoCadastro movCad2 = new MovimentoCadastro();
			movCad2.setObjMovimentado(particComunidadeVirtual);
			movCad2.setCodMovimento(ArqListaComando.ALTERAR);
	
			execute(movCad2);
	
			addMessage("Usu�rio autorizado com sucesso!", TipoMensagemUFRN.INFORMATION);
	
			atualizarListaParticipantesComunidade();
			listarSolicitacoesPendentes();
		}
		else
			addMessage("Aten��o: usu�rio j� cadastrado na comunidade!", TipoMensagemUFRN.ERROR);
		
		return forward("/cv/Configuracoes/conf.jsp");
	}

	/**
	 * Atualiza a lista de participantes da comunidade.
	 * 
	 * @throws DAOException
	 */
	public void atualizarListaParticipantesComunidade() throws DAOException {
		comunidade = getDAO(ComunidadeVirtualDao.class).findByPrimaryKey(comunidade.getId(), ComunidadeVirtual.class);
		participantes = getDAO(ComunidadeVirtualDao.class).findParticipantes(getComunidade());
		comunidade.setParticipantesComunidade(participantes);
	}

	/**
	 * Recusa a participa��o de um usu�rio na comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/Configuracoes/conf.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String recusarParticipacaoUsuarioComunidade() throws ArqException,
			NegocioException {
		Integer idSolicit = getParameterInt("id");

		SolicitacaoParticipacaoComunidade particComunidadeVirtual = getDAO(
				ComunidadeVirtualDao.class).findByPrimaryKey(idSolicit,
				SolicitacaoParticipacaoComunidade.class);
		particComunidadeVirtual.setAceitoComunidade(false);
		particComunidadeVirtual.setPendenteDecisao(false);

		prepareMovimento(ArqListaComando.ALTERAR);

		MovimentoCadastro movCad2 = new MovimentoCadastro();
		movCad2.setObjMovimentado(particComunidadeVirtual);
		movCad2.setCodMovimento(ArqListaComando.ALTERAR);

		execute(movCad2);
		atualizarListaParticipantesComunidade();
		listarSolicitacoesPendentes();
		return forward("/cv/Configuracoes/conf.jsp");
	}

	/**
	 * Remove um participante da Comunidade Virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * @return
	 */
	public String removerPartipanteComunidade() {
		MembroComunidadeMBean mbean = getMBean("membroComunidadeMBean");
		mbean.remover();
		return redirecionarDeAcordoUsuarioLogado();
	}
	
	/**
	 * Cria uma sala de chat e registra o usu�rio logado e o ID da comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String createChat() throws ArqException {

		ChatEngine.createChat(getComunidade().getId());
		try {
			ChatEngine.registerUser(getUsuarioGeralLogado(), getComunidade()
					.getId());
		} catch (IOException e) {
			throw new ArqException(e);
		}

		return null;
	}
	
	/**
	 * Vincula um Grupo pr�-definido de usu�rios a determinada comunidade. <br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/Configuracoes/conf.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String vincularGrupoComunidade() throws ArqException, NegocioException {

		if (!ValidatorUtil.isEmpty(comunidade.getIdGrupoAssociado())) {
		
			prepareMovimento(SigaaListaComando.VINCULAR_GRUPO_COMUNIDADE);
			MovimentoCadastroCv mov = new MovimentoCadastroCv();
			mov.setCodMovimento(SigaaListaComando.VINCULAR_GRUPO_COMUNIDADE);
			mov.setComunidade(comunidade);
			
			execute(mov);
			addMensagemInformation("Grupo vinculado a comunidade com sucesso!");
		}
		else {
			addMensagemWarning("Por favor, selecione um dos Grupos dispon�veis na lista.");
		}
		
		return null;
	}

	/**
	 * Cadastra uma nova comunidade virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/criar.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		// Popular tipo da comunidade virtual
		TipoComunidadeVirtual tipoComunidadeVirtual = obj.getTipoComunidadeVirtual();
		if (!isEmpty(tipoComunidadeVirtual)) {
			getGenericDAO().initialize(tipoComunidadeVirtual);
			if (tipoComunidadeVirtual.isCriacaoRestritaGestores()) {
				checkRole(SigaaPapeis.GESTOR_COMUNIDADADES_VIRTUAIS);
			}
		}

		ListaMensagens lista = obj.validate();
		if (!isEmpty(lista.getMensagens())) {
			addMensagens(lista);
			return null;
		}

		obj.setUsuario(getUsuarioLogado());

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_COMUNIDADE_VIRTUAL);
		execute(mov);

		BuscarComunidadeVirtualMBean buscarBean = getMBean("buscarComunidadeVirtualMBean");
		buscarBean.setComunidadesPorPessoa(null);
		addMensagemInformation("Comunidade cadastrada com sucesso!");
		return cancelar();
	}

	/**
	 * Exibe a p�gina de entrada de uma comunidade virtual, carregando 
	 * informa��es b�sicas relacionadas a comunidade que est� sendo aberta.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/portais/discente/discente.jsp</li>
	 *  <li>/sigaa.war/portais/docente/docente.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException 
	 */
	public String entrar() throws ArqException {
		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class);

		Integer idComunidade = getParameterInt("idComunidade");
		if (idComunidade == null)
			idComunidade = comunidade.getId();
		
		comunidade = dao.findByPrimaryKey(idComunidade, ComunidadeVirtual.class);

		if (comunidade.isAtiva()) {
			try {
				// Garante que a turma ter� um mural.
				getMural();
			
				membro = dao.findMembroByPessoa(comunidade, getUsuarioLogado().getPessoa());
				
				if(isEmpty(membro)) {
					addMensagemErro("N�o foi encontrado um membro na comunidade associado a sua pessoa.");
					return cancelar();
				}
				
				listarSolicitacoesPendentes();
				
				//Caso seja administrador, verifica se ha solicitacoes pendentes.
				if (membro.isAdministrador() && !listaSolicitacoes.isEmpty()) {
					addMensagemWarning("Existem novas solicita��es de participa��o na comunidade! Acesse a op��o 'Configura��es' para gerenciar as solicita��es.");
				}
	
				findTopicosForumComunidadeAtual();
				findEnquetesComunidadeAtual();
				atualizarListaParticipantesComunidade();
	
				setSubSistemaAtual(SigaaSubsistemas.AMBIENTES_VIRTUAIS);
				return redirect("/cv/index.jsf");
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
				return null;
			}
		} else {
			return redirecionarDeAcordoUsuarioLogado();
		}
	}
	
	
	/**
	 * Exibe a p�gina de entrada da comunidade virtual quando acessado a partir da �rea p�blica.
	 * No caso de acesso externo o id da comunidade � passado atrav�s do pattern definido 
	 * no pretty-faces. 
	 * 
	 * M�todo chamado pelo seguinte XML:
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/pretty-config.xml</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String entrarPublico() throws ArqException{
		
		if(!isEmpty(getUsuarioLogado()))
			return entrar();
		else{
			return redirect("/verTelaLogin.do?urlRedirect=/sigaa/public/comunidadevirtual/" + comunidade.getId());
		}
		
	}

	/**
	 * Abre a p�gina principal da comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String paginaPrincipal() throws ArqException {
		findEnquetesComunidadeAtual();
		
		try {
			getGenericDAO().initialize(comunidade);
			findTopicosForumComunidadeAtual();
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}
		return redirect("/cv/index.jsf");
	}

	/**
	 * Localiza os t�picos dos f�runs atuais da comunidade virtual
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void findTopicosForumComunidadeAtual() throws ArqException, NegocioException {
		ForumMensagemComunidadeMBean comunidadeMBean = (ForumMensagemComunidadeMBean) getMBean("forumMensagemComunidadeMBean");
		comunidadeMBean.setForumSelecionado(getMural().getId());
		topicosComunidadeAtual = comunidadeMBean.lista();
	}

	/**
	 * Localiza as enquetes atuais da comunidade virtual.
	 */
	private void findEnquetesComunidadeAtual() {
		enquentesAtuais = getDAO(ComunidadeVirtualDao.class)
				.findEnquetesByComunidade(comunidade);
	}

	/**
	 * Retorna um f�rum da Comunidade Virtual.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/principal.jsp</li> 
	 * 	<li>/sigaa.war/cv/include/_menu_comunidade.jsp</li>
	 * 	<li>/sigaa.war/cv/ForumComunidade/_form.jsp</li>
	 * 	<li>/sigaa.war/cv/ForumComunidade/mensagens.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public ForumComunidade getMural() throws ArqException, NegocioException {
		ForumComunidade mural = getDAO(ComunidadeVirtualDao.class).findForumByComunidade(comunidade);
		
		if (mural == null) {
			mural = new ForumComunidade();
			mural.setData(new Date());
			mural.setTitulo(comunidade.getNome());
			mural.setDescricao(comunidade.getDescricao());
			mural.setUsuario(getUsuarioLogado());
			mural.setComunidade(comunidade);

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(mural);
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			prepareMovimento(ArqListaComando.CADASTRAR);
			executeWithoutClosingSession(mov, getCurrentRequest());
			
			return getDAO(ComunidadeVirtualDao.class).findForumByComunidade(comunidade);
		}
		
		return mural;
		
	}

	/**
	 * Gera um PassKey para ser usado no chat com a comunidade que estiver aberta.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * 
	 * @return
	 */
	public String getChatPassKey() {
		return ChatEngine.generatePassKey(getUsuarioGeralLogado(), comunidade.getId());
	}

	/**
	 * Gera um PassKey para ser usado no chat usando getParameterInt<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * 
	 * @return
	 */
	public String getChatPassKeyParam() {
		return ChatEngine.generatePassKey(getUsuarioGeralLogado(),
				getParameterInt("id"));
	}

	/**
	 * Retorna UsuarioGeral que estiver logado.
	 * 
	 * @return
	 */
	private UsuarioGeral getUsuarioGeralLogado() {
		UsuarioGeral usr = new UsuarioGeral(getUsuarioLogado().getId(),
				getUsuarioLogado().getNome(), 0, getUsuarioLogado().getLogin(),
				null);
		usr.setIdFoto(getUsuarioLogado().getIdFoto());
		return usr;
	}

	/**
	 * De acordo com a permiss�o do usu�rio logado, exibe os tipos de comunidade dispon�veis para serem criadas.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/criar.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTiposComunidades() throws DAOException {
		if (isGestorComunidades() ) {
			Collection<SelectItem> list = getAll(TipoComunidadeVirtual.class, "id", "descricao");
			return list;
		} else {
			return toSelectItems(getGenericDAO().findByExactField(TipoComunidadeVirtual.class, "criacaoRestritaGestores", false), "id", "descricao");
		}
	}

	/**
	 * Retorna uma lista de moderadores<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_info_comunidade.jsp
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroComunidade> getModeradores() {
		return CollectionUtils.select(getParticipantes(), new Predicate() {
			public boolean evaluate(Object obj) {
				return ((MembroComunidade) obj).isModerador();
			}
			
		});
	}

	/**
	 * Retorna uma lista de Administradores<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/include/_info_comunidade.jsp
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroComunidade> getAdministradores() {
		return CollectionUtils.select(getParticipantes(), new Predicate() {
			public boolean evaluate(Object obj) {
				return ((MembroComunidade) obj).isAdministrador();
			}
			
		});
	}
	/**
	 * Retorna uma lista de not�cias de acordo com a comunidade<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/include/_menu_comunidade.jsp</li>
	 * 	<li>/sigaa.war/cv/principal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<NoticiaComunidade> getNoticias() throws DAOException {
		return getDAO(ComunidadeVirtualDao.class).findNoticiasByComunidade(comunidade);
	}

	/**
	 * Retorna uma lista com as �ltimas atividades realizadas na comunidade.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegistroAtividadeTurma> getUltimasAtividades() throws DAOException {
		return getDAO(ComunidadeVirtualDao.class).findUltimasAtividades(comunidade);
	}

	/**
	 * Verifica se o usu�rio logado possui permiss�o GESTOR_COMUNIDADADES_VIRTUAIS<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/criar.jsp
	 * 
	 * @return
	 */
	public boolean isGestorComunidades() {
		if ( isUserInRole(SigaaPapeis.GESTOR_COMUNIDADADES_VIRTUAIS) )
			return true;
		else
			return false;
	}
	
	/**
	 * Verifica se o usu�rio logado possui v�nculo de servidor.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *        <li>sigaa.war\cv\criar.jsp</li>
	 *  </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public boolean isServidor() {
		if ( getUsuarioLogado().getVinculoAtivo().isVinculoServidor() )
			return true;
		else
			return false;
	}
	
		
	/**
	 * Redireciona para da documenta��o no Wiki.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\portais\docente\menu_docente.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void oQueEIsso(ActionEvent e) {
		redirect(WIKI_COMUNIDADE_VIRTUAL);
	}
	
	// Gets e sets
	
	public boolean isDono() {
		return dono;
	}

	public void setDono(boolean dono) {
		this.dono = dono;
	}

	public boolean isAdminConteudo() {
		return adminConteudo;
	}

	public void setAdminConteudo(boolean adminConteudo) {
		this.adminConteudo = adminConteudo;
	}

	public boolean isConvidado() {
		return convidado;
	}

	public void setConvidado(boolean convidado) {
		this.convidado = convidado;
	}

	public MembroComunidade getMembro() {
		return membro;
	}

	public void setMembro(MembroComunidade membro) {
		this.membro = membro;
	}

	public List<SolicitacaoParticipacaoComunidade> getListaSolicitacoes() {
		return listaSolicitacoes;
	}

	public void setListaSolicitacoes(
			List<SolicitacaoParticipacaoComunidade> listaSolicitacoes) {
		this.listaSolicitacoes = listaSolicitacoes;
	}

	public Set<MembroComunidade> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(Set<MembroComunidade> participantes) {
		this.participantes = participantes;
	}

	public List<ForumMensagemComunidade> getTopicosComunidadeAtual() {
		return topicosComunidadeAtual;
	}

	public void setTopicosComunidadeAtual(
			List<ForumMensagemComunidade> topicosComunidadeAtual) {
		this.topicosComunidadeAtual = topicosComunidadeAtual;
	}

	public List<EnqueteComunidade> getEnquentesAtuais() {
		return enquentesAtuais;
	}

	public void setEnquentesAtuais(List<EnqueteComunidade> enquentesAtuais) {
		this.enquentesAtuais = enquentesAtuais;
	}
	
	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}
}
