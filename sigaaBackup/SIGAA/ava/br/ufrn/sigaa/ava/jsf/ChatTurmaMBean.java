/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/06/2011'
 *
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.chat.ChatEngine;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.ChatTurmaDao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ChatTurma;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator;

/**
 * Managed bean para cadastro de notícias na turma
 * virtual.
 * 
 * @author Ilueny Santos
 */
@Component("chatTurmaBean") 
@Scope("request")
public class ChatTurmaMBean extends CadastroTurmaVirtual<ChatTurma> {

	/**
	 * Construtor Padrão, que inicializa o object.
	 */
	public ChatTurmaMBean () {
		object = new ChatTurma();
	}
	
	/**
	 * Retorna a lista de chats da turma.<br/>
	 * Chamado pelo método "getListagem()" do bean CadastroTurmaVirtual.<br/>
	 * 
	 */
	@Override
	public List<ChatTurma> lista() {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		try {
			registrarAcao(null, EntidadeRegistroAva.CHAT, AcaoAva.LISTAR);
			return	getDAO(ChatTurmaDao.class).findChatsByTurma(turma(),tBean.isPermissaoDocente());
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	/**
	 * Retorna uma especificação para a turma.
	 * Não é usado em jsps.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {				
				ChatTurma chat = (ChatTurma) objeto;
				
				if (isEmpty(chat.getTitulo())) {
					notification.addError("Título: Campo obrigatório não informado.");
				}
				if (isEmpty(chat.getTurma())) {
					notification.addError("Turma: Campo obrigatório não informado.");
				}
				if (isEmpty(chat.getDataInicio())) {
					notification.addError("Data Início: Campo obrigatório não informado.");
				}
				if (isEmpty(chat.getHoraInicio())) {
					notification.addError("Hora Início: Campo obrigatório não informado.");
				}
				if (isEmpty(chat.getDataFim())) {
					notification.addError("Data Fim: Campo obrigatório não informado.");
				}
				if (isEmpty(chat.getHoraFim())) {
					notification.addError("Hora Fim: Campo obrigatório não informado.");
				}

				//Verificando se o agendamento do chat está com data e hora futuras
				if (chat.getDataInicio() != null && chat.getDataFim() != null && chat.getHoraInicio() != null && chat.getHoraFim() != null) {
					if (chat.getDataHoraInicio().after(chat.getDataHoraFim())) {
						notification.addError("Período Inválido: Início do agendamento deve ser antes Fim.");
					}					
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					
					if (ValidatorUtil.isEmpty(chat.getConteudo()) && chat.getDataHoraInicio().compareTo(cal.getTime()) < 0) {
						notification.addError("Período Inicial Inválido: Ajuste o agendamento para um período atual ou futuro. (Data e hora atual: " + CalendarUtils.format(new Date(), "dd/MM/yyyy HH:mm") + ")");
					}
				}
				
				return !notification.hasMessages();				
			}
		};
	}
	
	/**
	 * Indica ao ControllerTurmaVirtual que é para cadastrar em todas as turmas selecionadas.
	 */
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}

	/**
	 * Verifica se o usuário logado pode cadastrar um chat para a turma trabalhada.
	 * <br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/ChatTurma/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isPermiteCadastrarChat() {
		TurmaVirtualMBean tv = getMBean("turmaVirtual");
		PermissaoAvaMBean perm = getMBean("permissaoAva");
		
		return tv.isDocente() 
				|| perm.getPermissaoUsuario() != null && perm.getPermissaoUsuario().isDocente()
				|| OperacaoTurmaValidator.verificarNivelTecnico(turma());
	}

	
	/** 
	 * Inicia o cadastro de um Chat para o tópico de aula informado.
	 * Não é chamado por jsp.
	 *  
	 */
	public String preCadastrar(TopicoAula topicoAula) throws ArqException {
		try {
			topicoAula = getGenericDAO().findByPrimaryKey(topicoAula.getId(), TopicoAula.class, "id", "descricao", "turma.id");			
			object = new ChatTurma();
			object.setAula(topicoAula);
			object.setTurma(turma());
			object.setUsuario(getUsuarioLogado());
			
			List <String> cadastrarEm = new ArrayList <String> ();
			cadastrarEm.add(""+turma().getId());
			setCadastrarEm(cadastrarEm);

			prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
			return forward("/ava/ChatTurma/novo.jsp");

		}catch (Exception e) {
			tratamentoErroPadrao(e);			
			return null;
		}
	}

	/** 
	 * Inicia o cadastro de um Chat para a turma atual.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 * 		<li>/sigaa.war/ava/ChatTurma/listar.jsp</li>
	 *  </ul>
	 *  
	 */
	public String agendarNovoChatTurma() throws ArqException {
		try {
			object = new ChatTurma();
			object.setAula(new TopicoAula());
			object.setTurma(turma());
			object.setUsuario(getUsuarioLogado());
			
			List <String> cadastrarEm = new ArrayList <String> ();
			cadastrarEm.add(""+turma().getId());
			setCadastrarEm(cadastrarEm);

			prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
			return forward("/ava/ChatTurma/novo.jsp");

		}catch (Exception e) {
			tratamentoErroPadrao(e);			
			return null;
		}
	}

	

	/**
	 * Cria um chat para uma turma virtual
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 * 		<li>/sigaa.war/ava/ChatTurma/listar.jsp</li>
	 * 		<li>sigaa.war/portais/discente/discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws ArqException
	 */
	public String createChat() throws ArqException {		
		ChatEngine.createChat(object.getId());
		try {
			ChatEngine.registerUser(getUsuarioGeralLogado(), object.getId());
		} catch (IOException e) {
			throw new ArqException(e);
		}
		return null;
	}
	
	/**
	 * Cria um chat para uma turma virtual passando o id da turma como parâmetro de request
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/portais/discente/discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws ArqException
	 */
	public String createChatParam() throws ArqException {
		registrarAcao(null, EntidadeRegistroAva.CHAT, AcaoAva.ACESSAR, turma().getId() );
		object = new ChatTurma(getParameterInt("id"));
		return createChat();
	}
	
	/**
	 * Retorna a senha para acesso ao chat da turma atual
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/ChatTurma/lista.jsp</li>
	 *  </ul>
	 * @return
	 */
	public String getChatPassKey() {
		AbstractMaterialTurma chat = (AbstractMaterialTurma) getCurrentRequest().getAttribute("material");
		if ( chat != null ) {
			return ChatEngine.generatePassKey(getUsuarioGeralLogado(), chat.getId());
		} else {
			return "";
		}
	}
	
	/**
	 * Retorna a senha para acesso ao chat de uma turma passada como parâmetro.
	 * Método não invocado por JSP´s
	 * @return
	 */
	public String getChatPassKeyParam() {
		return ChatEngine.generatePassKey(getUsuarioGeralLogado(), getParameterInt("id"));
	}

	/**
	 * Retorna uma instância de UsuarioGeral com os dados do usuário logado.
	 * @return
	 */
	private UsuarioGeral getUsuarioGeralLogado() {
		UsuarioGeral usr = new UsuarioGeral(getUsuarioLogado().getId(), getUsuarioLogado().getNome(), 0, getUsuarioLogado().getLogin(), null);
		usr.setIdFoto(getUsuarioLogado().getIdFoto());
		return usr;
	}

	
	@Override
	public void antesPersistir() {
		if (ValidatorUtil.isEmpty(object.getAula())) {
			object.setAula(null);
		}
		super.antesPersistir();
	}
	
	/**
	 * Exibe dados do chat agendado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/ChatTurma/listar.jsp</li>
	 *       <li>sigaa.war/ava/ChatTurma/editar.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public String mostrar() throws DAOException {		
		registrarAcao(AcaoAva.ACESSAR);		
		object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
		
		if (object == null){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
			return tBean.retornarParaTurma();			
		}else {
			gravarConteudoChatNoBanco(object);
		}
		
		return forward("/ava/ChatTurma/mostrar.jsp");
	}
	
	/**
	 * Grava o conteúdo do arquivo do chat agendado no banco de dados.
	 * Este método não é chamado por JSPs.
	 * 
	 * @return
	 */
	private void gravarConteudoChatNoBanco(ChatTurma chat) {
		try {
			if (ValidatorUtil.isNotEmpty(chat)) {
				
				if (!chat.isChatTerminou())
					chat.setConteudo(ChatEngine.getChatContent(chat.getId(), chat.getDataHoraInicio(), chat.getDataHoraFim()));
				
				//Persiste o conteúdo do arquivo do chat agendado no banco.
				if (ValidatorUtil.isEmpty(chat.getConteudo()) && chat.isChatTerminou()) {
					chat.setConteudo(ChatEngine.getChatContent(chat.getId(), chat.getDataHoraInicio(), chat.getDataHoraFim()));
					prepareMovimento(ArqListaComando.CADASTRAR);
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setCodMovimento(ArqListaComando.CADASTRAR);
					mov.setAcao(MovimentoCadastro.ACAO_ALTERAR);
					mov.setObjMovimentado(chat);
					execute(mov);
				}			
			}
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} catch (Exception e) {
			notifyError(e);
		}
	}


}
