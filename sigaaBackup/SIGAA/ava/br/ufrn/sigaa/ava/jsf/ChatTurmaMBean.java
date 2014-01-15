/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Managed bean para cadastro de not�cias na turma
 * virtual.
 * 
 * @author Ilueny Santos
 */
@Component("chatTurmaBean") 
@Scope("request")
public class ChatTurmaMBean extends CadastroTurmaVirtual<ChatTurma> {

	/**
	 * Construtor Padr�o, que inicializa o object.
	 */
	public ChatTurmaMBean () {
		object = new ChatTurma();
	}
	
	/**
	 * Retorna a lista de chats da turma.<br/>
	 * Chamado pelo m�todo "getListagem()" do bean CadastroTurmaVirtual.<br/>
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
	 * Retorna uma especifica��o para a turma.
	 * N�o � usado em jsps.
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
					notification.addError("T�tulo: Campo obrigat�rio n�o informado.");
				}
				if (isEmpty(chat.getTurma())) {
					notification.addError("Turma: Campo obrigat�rio n�o informado.");
				}
				if (isEmpty(chat.getDataInicio())) {
					notification.addError("Data In�cio: Campo obrigat�rio n�o informado.");
				}
				if (isEmpty(chat.getHoraInicio())) {
					notification.addError("Hora In�cio: Campo obrigat�rio n�o informado.");
				}
				if (isEmpty(chat.getDataFim())) {
					notification.addError("Data Fim: Campo obrigat�rio n�o informado.");
				}
				if (isEmpty(chat.getHoraFim())) {
					notification.addError("Hora Fim: Campo obrigat�rio n�o informado.");
				}

				//Verificando se o agendamento do chat est� com data e hora futuras
				if (chat.getDataInicio() != null && chat.getDataFim() != null && chat.getHoraInicio() != null && chat.getHoraFim() != null) {
					if (chat.getDataHoraInicio().after(chat.getDataHoraFim())) {
						notification.addError("Per�odo Inv�lido: In�cio do agendamento deve ser antes Fim.");
					}					
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					
					if (ValidatorUtil.isEmpty(chat.getConteudo()) && chat.getDataHoraInicio().compareTo(cal.getTime()) < 0) {
						notification.addError("Per�odo Inicial Inv�lido: Ajuste o agendamento para um per�odo atual ou futuro. (Data e hora atual: " + CalendarUtils.format(new Date(), "dd/MM/yyyy HH:mm") + ")");
					}
				}
				
				return !notification.hasMessages();				
			}
		};
	}
	
	/**
	 * Indica ao ControllerTurmaVirtual que � para cadastrar em todas as turmas selecionadas.
	 */
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}

	/**
	 * Verifica se o usu�rio logado pode cadastrar um chat para a turma trabalhada.
	 * <br />
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
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
	 * Inicia o cadastro de um Chat para o t�pico de aula informado.
	 * N�o � chamado por jsp.
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
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Cria um chat para uma turma virtual passando o id da turma como par�metro de request
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna a senha para acesso ao chat de uma turma passada como par�metro.
	 * M�todo n�o invocado por JSP�s
	 * @return
	 */
	public String getChatPassKeyParam() {
		return ChatEngine.generatePassKey(getUsuarioGeralLogado(), getParameterInt("id"));
	}

	/**
	 * Retorna uma inst�ncia de UsuarioGeral com os dados do usu�rio logado.
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
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Grava o conte�do do arquivo do chat agendado no banco de dados.
	 * Este m�todo n�o � chamado por JSPs.
	 * 
	 * @return
	 */
	private void gravarConteudoChatNoBanco(ChatTurma chat) {
		try {
			if (ValidatorUtil.isNotEmpty(chat)) {
				
				if (!chat.isChatTerminou())
					chat.setConteudo(ChatEngine.getChatContent(chat.getId(), chat.getDataHoraInicio(), chat.getDataHoraFim()));
				
				//Persiste o conte�do do arquivo do chat agendado no banco.
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
