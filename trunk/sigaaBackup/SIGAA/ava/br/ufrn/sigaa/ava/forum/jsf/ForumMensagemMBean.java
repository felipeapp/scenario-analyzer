/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '28/03/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.ava.forum.dao.ForumGeralDao;
import br.ufrn.sigaa.ava.forum.dao.ForumGeralMensagemDao;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralMensagem;
import br.ufrn.sigaa.ava.forum.dominio.OrdemMensagensForum;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed-Bean para gerenciamento de Geral de Mensagens e t�picos dos F�runs.
 * 
 * @author Ilueny Santos
 * 
 */
@Component("forumMensagemBean")
@Scope("session")
public class ForumMensagemMBean extends AbstractControllerCadastro<ForumGeralMensagem> {

	/** F�rum atual selecionado. */
	private ForumGeral forum = new ForumGeral();

	/** Mensagem atual selecionada. */
	private ForumGeralMensagem mensagem = new ForumGeralMensagem();
	
	/** Lista de respostas ao t�pico selecionado. */
	private List<ForumGeralMensagem> mensagens = new ArrayList<ForumGeralMensagem>();

	/** Mensagem/T�pico que est� sendo respondido. */
	private ForumGeralMensagem mensagemRespondida = new ForumGeralMensagem();

    /** Ordem de exibi��o das mensagens do t�pico. */
	private OrdemMensagensForum ordem = new OrdemMensagensForum();

    /** Se est� cadastrando novo t�pico. */
	private boolean cadastrarNovoTopico = false;

	/** Construtor padr�o. */
	public ForumMensagemMBean() {
		obj = new ForumGeralMensagem();
	}
	
	/** Diret�rio Base. */
	@Override
	public String getDirBase() {
		return "/ava/Foruns/Mensagem";
	}
	
    /**
     * Redireciona para a tela de confirma��o da remo��o.
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String preRemover() {
		if (ValidatorUtil.isEmpty(obj)) {
			addMensagemErro("Mensagem selecionada n�o � uma mensagem v�lida.");
			return null;
		}
		
		try {
			prepareMovimento(SigaaListaComando.REMOVER_FORUM_GERAL_MENSAGEM);
			setOperacaoAtiva(SigaaListaComando.REMOVER_FORUM_GERAL_MENSAGEM.getId());
			obj = getDAO(ForumGeralMensagemDao.class).findByPrimaryKey(obj.getId(), ForumGeralMensagem.class);
			
			//Evitar erro de lazy
			obj.getTopico().getId();
			obj.getForum().getId();
			ordem = obj.getForum().getOrdenacaoPadrao();
			
			mensagens = getDAO(ForumGeralMensagemDao.class).findMensagensByMensagem(obj.getId());
			setConfirmButton("Remover " + obj.getForum().getTipo().getLabelMensagem() + " do o f�rum");
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_REMOVER);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}
	
	/**
	 * Ver detalhes do t�pico ou mensagem pai selecionada. 
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li>
     * </ul>
     * 
     */
	public String view() throws DAOException {
		if (ValidatorUtil.isEmpty(obj)) {
			addMensagemErro("Mensagem selecionada n�o � uma mensagem v�lida.");
			return null;
		}
		
		obj = getDAO(ForumGeralDao.class).findAndFetch(obj.getId(), ForumGeralMensagem.class, "usuario");
		if (ValidatorUtil.isEmpty(obj) || !obj.isAtivo()) {
			addMensagemErro("Mensagem selecionada n�o � uma mensagem v�lida.");
			return null;
		}

		getPaginacao().setPaginaAtual(0);
		setTamanhoPagina(20);
		filtrarMensagens(obj.getForum().getOrdenacaoPadrao());

		return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_VIEW);		
	}
	
	/**
	 * Ver detalhes do t�pico ou mensagem pai selecionada. 
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li>
     * </ul>
     * 
     */
	public void mensagemShowModal() throws DAOException {
		int id = getParameterInt("id", 0);
		mensagem = getGenericDAO().findByPrimaryKey(id, ForumGeralMensagem.class);
		if (ValidatorUtil.isEmpty(mensagem) || !mensagem.isAtivo()) {
			addMensagemErroAjax("Mensagem selecionada n�o � uma mensagem v�lida.");
		}
	}
	
	
	/** 
	 * Atualiza mensagens do t�pico. 
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li>
     * </ul>
     * 
     */
	public void filtrarMensagens(final OrdemMensagensForum ordem) throws DAOException {
		this.ordem = ordem; 
		if (ValidatorUtil.isNotEmpty(this.ordem)) {
			switch (this.ordem.getId()) {
			
				case OrdemMensagensForum.MAIS_ANTIGAS_PRIMEIRO:
					mensagens = getDAO(ForumGeralMensagemDao.class).findMensagensByMensagem(obj.getId(), "data", true, getPaginacao());
					break;

				case OrdemMensagensForum.MAIS_RECENTES_PRIMEIRO:
					mensagens = getDAO(ForumGeralMensagemDao.class).findMensagensByMensagem(obj.getId(), "data", false, getPaginacao());
					break;

				case OrdemMensagensForum.LISTAR_RESPOSTAS:
					mensagens = getDAO(ForumGeralMensagemDao.class).findMensagensByMensagem(obj.getId(), "hierarquia", true, getPaginacao());
					break;

				case OrdemMensagensForum.RESPOSTAS_ANINHADAS:
					mensagens = getDAO(ForumGeralMensagemDao.class).findMensagensByMensagem(obj.getId(), "hierarquia", true, getPaginacao());
					break;

				default:
					mensagens = getDAO(ForumGeralMensagemDao.class).findMensagensByMensagem(obj.getId(), "data", true, getPaginacao());
					break;					
			}				
		}
	}
	
	

	 /**
     * Altera a ordem de exibi��o das mensagens do t�pico.
     * 
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li> 
     * </ul>
     * @param evt Valor recebido da jsp na gera��o do evento.
     */
	public void changeOrdenarMensagens(ValueChangeEvent evt) {
		try {			
			ordem.setId(new Integer(evt.getNewValue().toString()));
			filtrarMensagens(ordem);
		} catch (Exception e) {
			notifyError(e);
		}
	}
	
	
	/** 
	 * Novo T�pico para o f�rum selecionado. 
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/view.jsp</li>
     * </ul>
     * 
     */
	public String novoTopico() throws ArqException {
		setCadastrarNovoTopico(true);
		
		if (forum == null) {
			forum = new ForumGeral();
			addMensagemErro("F�rum selecionado n�o � um f�rum v�lido.");
			return null;
		}
		
		forum = getGenericDAO().findByPrimaryKey(forum.getId(), ForumGeral.class);
		if (ValidatorUtil.isEmpty(forum) || !forum.isAtivo()) {
			addMensagemErro("F�rum selecionado n�o � um f�rum v�lido.");
			return null;
		}
		
		//Criando novo t�pico
		obj = new ForumGeralMensagem();
		obj.setForum(forum);
		obj.setTopico(obj);
		obj.setMensagemPai(null);
		prepareMovimento(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM.getId());
		setConfirmButton("Enviar " + obj.getForum().getTipo().getLabelMensagem() + " para o f�rum");
		return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_FORM);		
	}
	
	
	/** 
	 * Responder t�pico ou comentar mensagem. 
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li>
     * </ul>
     * 
     */
	public String responder() throws ArqException {
		
		cadastrarNovoTopico = false;
				
		if(ValidatorUtil.isEmpty(obj.getId())){
			addMensagemErro("Caro usu�rio, voc� utilizou a op��o 'Voltar' do navegador.");
			return cancelar();
		}
		
		if (mensagemRespondida == null) {
			mensagemRespondida = new ForumGeralMensagem();
			addMensagemErro("Mensagem para resposta n�o � uma mensagem v�lida.");
			return null;
		}
		
		mensagemRespondida = getGenericDAO().findByPrimaryKey(mensagemRespondida.getId(), ForumGeralMensagem.class);
		if (ValidatorUtil.isEmpty(mensagemRespondida) || !mensagemRespondida.isAtivo()) {
			addMensagemErro("Mensagem para resposta n�o � uma mensagem v�lida.");
			return null;
		}
		
		//Evitar erro de lazy
		mensagemRespondida.getForum().getId();
		mensagemRespondida.getTopico().getId();
		
		//Criando resposta
		obj = new ForumGeralMensagem();
		obj.setForum(mensagemRespondida.getForum());
		obj.setTopico(mensagemRespondida.getTopico());
		obj.setMensagemPai(mensagemRespondida);
		obj.setTitulo("Re: " + mensagemRespondida.getTitulo());
		prepareMovimento(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM.getId());
		setConfirmButton("Responder " + obj.getForum().getTipo().getLabelMensagem());
		return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_FORM);		
	}

	/**
	 * Realiza valida��o e cadastro e remo��o de mansagens do f�rum.
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/form.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		
		if(getConfirmButton().equalsIgnoreCase("Responder T�pico de Discuss�o") && !obj.isTipoResposta()){
			addMensagemErro("Caro usu�rio, por favor n�o utilize o bot�o 'Voltar' do navegador.");
			return cancelar();
		}
		
		ListaMensagens lista = obj.validate();
		if (lista != null && !lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}			
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);

			boolean cadastrar = obj.getId() == 0;
			
			if (obj.getId() == 0) {	
				if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM.getId())){
					return cancelar();
				}
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_FORUM_GERAL_MENSAGEM);
				TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
				mov.setObjAuxiliar(tvBean.turma());
			} else {
				if( !checkOperacaoAtiva(SigaaListaComando.ALTERAR_FORUM_GERAL_MENSAGEM.getId()) ){
					return cancelar();
				}
				mov.setCodMovimento(SigaaListaComando.ALTERAR_FORUM_GERAL_MENSAGEM);
			}

			obj = execute(mov);
			addMensagem(OPERACAO_SUCESSO);
			removeOperacaoAtiva();

			if (isFromForumTurma() ){
				TurmaVirtualMBean tBean = getMBean("turmaVirtual");
				tBean.setUltimasMensagens(null);
			}
			
			if ( obj.getForum().getMonitorarLeitura() && cadastrar)
				notificar();
			else if ( obj.getForum().getMonitorarLeitura() && !cadastrar)
				notificarAssinantes();

			if (obj.isTipoTopico()) {
				obj = getGenericDAO().findAndFetch(obj.getId(), ForumGeralMensagem.class, "usuario");
			}else {
				obj = getGenericDAO().findAndFetch(obj.getTopico().getId(), ForumGeralMensagem.class, "usuario");
			}
							
			filtrarMensagens(obj.getForum().getOrdenacaoPadrao());	
			removeOperacaoAtiva();
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_VIEW);

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	/** 
	 * Prepara o ambiente para altera��o da mensagem selecionada. 
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String atualizar() throws ArqException {
		if (ValidatorUtil.isEmpty(obj)) {
			addMensagemErro("Mensagem selecionada n�o � uma mensagem v�lida.");
			return null;
		}

		try {
			setOperacaoAtiva(SigaaListaComando.ALTERAR_FORUM_GERAL_MENSAGEM.getId());
			prepareMovimento(SigaaListaComando.ALTERAR_FORUM_GERAL_MENSAGEM);

			setReadOnly(false);
			this.obj =  getGenericDAO().findByPrimaryKey(obj.getId(), ForumGeralMensagem.class);
			setConfirmButton("Alterar");
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_FORM);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}
	}



	/**
	 * Removendo mensagens do f�rum.
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/remover.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String remover() throws ArqException {
		checkChangeRole();

		if (ValidatorUtil.isEmpty(obj)) {
			addMensagemErro("N�o h� objeto informado para remo��o");
			return cancelar();
		}

		try {
			
			if(!checkOperacaoAtiva(SigaaListaComando.REMOVER_FORUM_GERAL_MENSAGEM.getId())){
				return cancelar();
			}
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.REMOVER_FORUM_GERAL_MENSAGEM);
			mov.setObjMovimentado(obj);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);

			if (obj.isTipoTopico()) {
				ForumMBean fb = getMBean("forumBean");
				fb.setObj(obj.getForum());
				removeOperacaoAtiva();
				return fb.view();
			}else {
				obj = getDAO(ForumGeralDao.class).findByPrimaryKey(obj.getTopico().getId(), ForumGeralMensagem.class);
			}
			
			if (isFromForumTurma() ){
				TurmaVirtualMBean tBean = getMBean("turmaVirtual");
				tBean.setUltimasMensagens(null);
			}
			
			filtrarMensagens(obj.getForum().getOrdenacaoPadrao());
			removeOperacaoAtiva();
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_VIEW);

		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_FORM);
		}
	}
	
	/** 
	 * Prepara o sistema para interrup��o de discuss�o. 
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li>
     * </ul>
     * 
     */
	public String preInterromper() {
		if (ValidatorUtil.isEmpty(obj) || !obj.isAtivo()) {
			addMensagemErro("Mensagem selecionada n�o � uma mensagem v�lida.");
			return null;
		}

		try {
			prepareMovimento(SigaaListaComando.INTERROMPER_FORUM_GERAL_MENSAGEM);
			obj = getDAO(ForumGeralDao.class).findByPrimaryKey(obj.getId(), ForumGeralMensagem.class);
			
			//Evitar erro de lazy
			obj.getTopico().getId();
			obj.getForum().getId();
			
			mensagens = getDAO(ForumGeralMensagemDao.class).findMensagensByMensagem(obj.getId());
			setConfirmButton("Interromper discuss�o");
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_INTERROMPER);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}
	
	/** 
	 * Realiza a interrup��o de mensagem no t�pico convertendo-a em um novo t�pico. 
     * <br />
     * 
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/interromper.jsp</li>
     * </ul>
     * 
     */
	public String interromper() throws SegurancaException {
		checkChangeRole();
		
		if (ValidatorUtil.isEmpty(obj) || !obj.isAtivo()) {
			addMensagemErro("Mensagem selecionada n�o � uma mensagem v�lida.");
			return null;
		}
		
		try {

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.INTERROMPER_FORUM_GERAL_MENSAGEM);
			mov.setObjMovimentado(obj);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);

			if (obj.isTipoTopico()) {
				ForumMBean fb = getMBean("forumBean");
				fb.setObj(obj.getForum());
				return fb.view();
			}else {
				obj = getDAO(ForumGeralDao.class).findByPrimaryKey(obj.getTopico().getId(), ForumGeralMensagem.class);
			}
			filtrarMensagens(obj.getForum().getOrdenacaoPadrao());
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_VIEW);

		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_FORM);
		}
		
	}
	
    /**
     * Visualizar o arquivo anexo.
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/Mensagem/view.jsp</li>
     * </ul>
     * 
     */
    public void viewArquivo() {
    	try {
    		int idArquivo = getParameterInt("idArquivo");
    		EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
    	} catch (Exception e) {
    		notifyError(e);
    		addMensagemErro("Arquivo n�o encontrado!");
    		return;
    	}
    	FacesContext.getCurrentInstance().responseComplete();
    }

	
	/**
	 * Verifica se � permitido ao usu�rio ver a discuss�o
	 * antes de postar mensagens.
	 * 
	 * @throws DAOException 
	 */
	public boolean isPermiteVerDiscussaoTopico() {
		boolean result = true;
		try {
			if (ValidatorUtil.isNotEmpty(obj) && obj.isTipoTopico() 
					&& !obj.getForum().getTipo().isPermiteVerDiscussaoAntesPostar() 
					&& !isModerador()) {
				int qtdMensagens = getDAO(ForumGeralMensagemDao.class).findCountMensagensByTopico(obj.getId(), getUsuarioLogado().getId());
				result = (qtdMensagens > 0);			
			}
		} catch (DAOException e) {
			notifyError(e);
		}
		return result;
	}

    @Override
    public String cancelar() {
    	removeOperacaoAtiva();
    	return super.cancelar();
    }
	
	public ForumGeral getForum() {
		return forum;
	}

	public void setForum(ForumGeral forum) {
		this.forum = forum;
	}

	public List<ForumGeralMensagem> getMensagens() {
		return mensagens;
	}

	public void setMensagens(List<ForumGeralMensagem> mensagens) {
		this.mensagens = mensagens;
	}

	public ForumGeralMensagem getMensagemRespondida() {
		return mensagemRespondida;
	}

	public void setMensagemRespondida(ForumGeralMensagem mensagemRespondida) {
		this.mensagemRespondida = mensagemRespondida;
	}

	public OrdemMensagensForum getOrdem() {
		return ordem;
	}

	public void setOrdem(OrdemMensagensForum ordem) {
		this.ordem = ordem;
	}

	public ForumGeralMensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(ForumGeralMensagem mensagem) {
		this.mensagem = mensagem;
	}
	
		
	/** M�todo utilizado na pagina��o das mensagens. Seleciona uma p�gina espec�fica. 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa/ava/Foruns/Mensagem/_mensagens.jsp</li>
     * </ul>
     */
	public void changePage (ValueChangeEvent e) throws DAOException{
		getPaginacao().changePage(e);
		filtrarMensagens(ordem);
	}
	
	/** M�todo utilizado na pagina��o das mensagens. Navega para p�gina anterior. 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa/ava/Foruns/Mensagem/_mensagens.jsp</li>
     * </ul>
     */
	public void previousPage (ActionEvent e) throws DAOException{
		getPaginacao().previousPage(e);
		filtrarMensagens(ordem);
	}
	
	/** M�todo utilizado na pagina��o das mensagens. Navega para pr�xima p�gina. 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa/ava/Foruns/Mensagem/_mensagens.jsp</li>
     * </ul>
     */
	public void nextPage (ActionEvent e) throws DAOException{
		getPaginacao().nextPage(e);
		filtrarMensagens(ordem);
	}
	
	/** M�todo utilizado na pagina��o das mensagens. Navega para primeira p�gina. 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa/ava/Foruns/Mensagem/_mensagens.jsp</li>
     * </ul>
     */
	public void primeiraPagina (ActionEvent e) throws DAOException{
		getPaginacao().primeiraPagina(e);
		filtrarMensagens(ordem);
	}
	
	/** M�todo utilizado na pagina��o das mensagens. Navega para �ltima p�gina p�gina. 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa/ava/Foruns/Mensagem/_mensagens.jsp</li>
     * </ul>
     */
	public void ultimaPagina (ActionEvent e) throws DAOException{
		getPaginacao().ultimaPagina(e);
		filtrarMensagens(ordem);
	}

	/** M�todo que verifica se o usu�rio possui permiss�o de docente para visualizar as perguntas do f�rum. 
	 * <br />
     * M�todo n�o invocado por JSP(s):
      */
	public boolean isModerador() throws DAOException {
		
		TurmaVirtualDao tDao = null;
		
		try {
		
			if (  getUsuarioLogado().getId() == obj.getForum().getUsuario().getId() )
				return true;
			
			tDao = getDAO(TurmaVirtualDao.class);
			ForumTurma forumTurma = tDao.findByExactField(ForumTurma.class, "forum.id", obj.getForum().getId(), true); 
			if ( forumTurma != null ){

				Usuario usuario = getUsuarioLogado();
				Turma turma = forumTurma.getTurma();
				boolean docente = false;
				
				// Verifica se o usu�rio � docente desta turma
				if (usuario.getVinculoAtivo().isVinculoServidor()) {
					if (!isEmpty(turma.getSubturmas())) {
						docente = tDao.isDocenteSubTurma(usuario.getServidor(), turma);
					} else {
						docente = tDao.isDocenteTurma(usuario.getServidorAtivo(), turma);
					}
				}
				
				// Verifica se o usu�rio � docente externo desta turma.
				if (usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
					if (!isEmpty(turma.getSubturmas())) {
						docente = tDao.isDocenteExternoSubTurma(usuario.getDocenteExterno(), turma);
					} else {
						docente = tDao.isDocenteExternoTurma(usuario.getDocenteExterno(), turma);
					}
				}
				
				if (docente)
					return true;
				
				// Verifica se o usu�rio possui permissao.
				PermissaoAva  permissao = tDao.findPermissaoByPessoaTurma((Pessoa) getUsuarioLogado().getPessoa(), turma);
				if (permissao != null && (permissao.isDocente() || permissao.isForum()) )
					return true;
			}
		}finally {
			if (tDao != null)
				tDao.close();
		}
		return false;
	}
	
	/** M�todo que verifica se o t�pico � de um f�rum da turma virtual. 
	 * <br />
     * M�todo n�o invocado por JSP(s)
      */
	private boolean isFromForumTurma() throws NegocioException, ArqException{
		TurmaVirtualDao tDao = null;
		
		try {
					
			tDao = getDAO(TurmaVirtualDao.class);
			ForumTurma forumTurma = tDao.findByExactField(ForumTurma.class, "forum.id", obj.getForum().getId(), true); 
					
			if ( forumTurma != null )
				return true;
			else 
				return false;
			
		}finally {
			if (tDao != null)
				tDao.close();
		}
	}
	
	/** M�todo respons�vel pelo envio de e-mails. 
	 * <br />
     * M�todo n�o invocado por JSP(s)
      */
	public void notificar() throws NegocioException, ArqException{
		TurmaVirtualDao tDao = null;
		
		try {
					
			tDao = getDAO(TurmaVirtualDao.class);
			ForumTurma forumTurma = tDao.findByExactField(ForumTurma.class, "forum.id", obj.getForum().getId(), true); 
					
			if ( forumTurma != null ){
				Turma turma = forumTurma.getTurma();
				notificarTurma(turma);			
			} else 
				notificarAssinantes();
			
		}finally {
			if (tDao != null)
				tDao.close();
		}
	}
	
	/** Envia e-mails para os participantes da turma virtual. 
	 * <br />
     * M�todo n�o invocado por JSP(s)
      */
	private void notificarTurma(Turma turma) throws DAOException{

			String assunto = "";
			String msg = "";
			
			if ( !obj.isTipoTopico() ){
				assunto = "Resposta ao T�pico: " + obj.getMensagemPai().getTitulo() + " - " +turma.getDescricaoSemDocente();	
    			msg += "<b>T�tulo da Resposta:</b> ";
    			msg += obj.getTitulo();
    			msg += "<br/>"+(char)13; // Enter(pula uma linha)
    			msg += "<br/>"+(char)13; 
    			msg += "<b>Autor(a):</b> ";
    			msg += getUsuarioLogado().getNome();
    			msg += "<br/>"+(char)13; 
    			msg += "<br/>"+(char)13;
    			msg += "<b>Descri��o:</b> ";
    			// Removendo coment�rios inseridos quando se copia e cola conte�dos de arquivos do Word.
    			msg += StringUtils.removerComentarios(obj.getConteudo());
    						
			} else {
				assunto = "Novo T�pico de F�rum Cadastrado : " + obj.getTitulo() + " - " +turma.getDescricaoSemDocente();	
				msg = StringUtils.removerComentarios(obj.getConteudo());	
			}
			
			TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
			tvBean.notificarTurma(turma,assunto,msg,  ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE , ControllerTurmaVirtual.AUTORIZADO , ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);
			
	}
	
    /**
     * Envia e-mail para os participantes do t�pico.
     * 
     */
	private void notificarAssinantes() throws NegocioException, ArqException {
    	ForumGeralMensagemDao dao = getDAO(ForumGeralMensagemDao.class);

    	if (obj != null) {
	    	List<String> emails = null;
	    	try {
	    		emails = dao.findEmailsByTopico(obj.getTopico());
	    		
	    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
	    		String data = sdf.format(obj.getData());
	
	    		for (String email : emails) {
	    			MailBody mail = new MailBody();
	    			mail.setContentType(MailBody.HTML);
	    			mail.setAssunto("Movimenta��es no F�rum: " + obj.getForum().getTitulo() + " - T�pico: " + obj.getTitulo() + " - " + data );
	    			mail.setFromName("F�rum SIGAA: " + getUsuarioLogado().getNome());
	    			mail.setEmail(email);
	
	    			StringBuffer msg = new StringBuffer();
	    			msg.append("<b>T�tulo do T�pico:</b> ");
	    			msg.append(obj.getTitulo());
	    			msg.append("<br/>"+(char)13); // Enter(pula uma linha)
	    			msg.append("<b>Autor(a):</b> ");
	    			msg.append(getUsuarioLogado().getNome());
	    			msg.append("<br/>"+(char)13);
	    			msg.append("<b>Descri��o:</b> ");
	    			// Removendo coment�rios inseridos quando se copia e cola conte�dos de arquivos do Word.
	    			msg.append(StringUtils.removerComentarios(obj.getConteudo()));
	    			
	    			mail.setMensagem(msg.toString());
	    			Mail.send(mail);
	    		}
	    	}finally{
	    		dao.close();
	    	}
    	}
    }

	public void setCadastrarNovoTopico(boolean cadastrarNovoTopico) {
		this.cadastrarNovoTopico = cadastrarNovoTopico;
	}

	public boolean isCadastrarNovoTopico() {
		return cadastrarNovoTopico;
	}
}
