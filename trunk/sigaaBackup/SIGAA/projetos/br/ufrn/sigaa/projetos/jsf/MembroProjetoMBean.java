/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/05/2008
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.projetos.FuncaoMembroDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoAssociadoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.ParticipanteExterno;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.mensagens.MensagensExtensao;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.pesquisa.jsf.FuncaoMembroEquipeMBean;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoPesquisaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.negocio.MembroProjetoHelper;
import br.ufrn.sigaa.projetos.negocio.MembroProjetoValidator;

/*******************************************************************************
 * MBean respons�vel pelo controle geral do CRUD de Membros de um Projeto.	
 * 
 * @author Ilueny Santos
 * @author Thalisson Muriel
 * 
 ******************************************************************************/
@Scope("session")
@Component("membroProjeto")
public class MembroProjetoMBean extends SigaaAbstractController<MembroProjeto> {

	/** Membro do Projeto utilizado no formul�rio de defini��o dos Membros. */
	private MembroProjeto membroEquipe = new MembroProjeto();
	/** Membro do projeto utilizado para alterar o coordenador de uma a��o  */
	private MembroProjeto membroEquipeAux = new MembroProjeto();
	
	/** Projeto ao qual o Membro far� parte. */
	private Projeto projeto = new Projeto();

	/** Utilizado no cadastro de Membros da Equipe. */
	private Servidor servidor = new Servidor();
	/** Utilizado no cadastro de Membros da Equipe. */
	private Servidor docente = new Servidor();
	/** Utilizado no cadastro de Membros da Equipe. */
	private Discente discente = new Discente();
	/** Utilizado no cadastro de Membros da Equipe. */
	private ParticipanteExterno participanteExterno = new ParticipanteExterno();

	/** Utilizado no cadastro dos Participantes Externos. */
	private String cpf = new String("");
	/** Lista de mensagens utilizada para informar os erros do Ajax	 */
	private List<MensagemAviso> avisosAjax = new ArrayList<MensagemAviso>();
	
	/** Fun��o de Membro para Docente */
	private FuncaoMembro funcaoDocente = new FuncaoMembro();
	/** Fun��o de Membro para Discente */
	private FuncaoMembro funcaoDiscente = new FuncaoMembro();
	/** Fun��o de Membro para Servidor */
	private FuncaoMembro funcaoServidor = new FuncaoMembro();
	/** Fun��o de Membro para Externo */
	private FuncaoMembro funcaoExterno = new FuncaoMembro();
	
	/** Indica se o membro faz parte de um Projeto de Pesquisa. */
	private boolean pesquisa = false;
	
	/** Indica se o membro faz parte de um Projeto de Pesquisa. */
	private boolean associados = false;
	
	/** Lista de Membros do Projeto. */
	private Collection<MembroProjeto> membrosProjetos;

	public MembroProjetoMBean() {
	}

	/**
	 * Prepara o ambiente para altera��o do membro da equipe. Realiza a
	 * validac�o do per�odo permitido para alterac�o da equipe. As altera��es
	 * nos membros da equipe de extens�o s�o permitidas somente ate quando forem
	 * decorridos 1/3 do tempo total de realiza��o da a��o.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista_unica_acao.jsp</li>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarMembroEquipe() throws ArqException {
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		try {
			getCurrentRequest().setAttribute("listaOrigem",	getParameter("lista_origem"));
			int idMembro = getParameterInt("idMembro", 0);
			
			if (idMembro > 0) {
				membroEquipe = dao.findByPrimaryKey(idMembro, MembroProjeto.class);
				membroEquipe.setProjeto( dao.findProjetoMembroLeve(idMembro) );
				
				// evitar erro de lazy na valida��o
				membroEquipe.getProjeto().getEquipe().iterator();
				if (membroEquipe.getCategoriaMembro().isServidor()) {
					getGenericDAO().refresh(membroEquipe.getServidor().getEscolaridade());
				}
				
				prepareMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
				setReadOnly(false);
				setConfirmButton("Confirmar Altera��o");
				return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_ALTERA_FORM);
			} else {
				return null;
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Inativa um membro da equipe caso ele n�o seja coordenador.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preInativar() throws ArqException, NegocioException	{	
		getCurrentRequest().setAttribute("listaOrigem", getParameter("lista_origem"));
		Integer idMembro = getParameterInt("id", 0);
		if (!ValidatorUtil.isEmpty(idMembro)) {
			obj = getGenericDAO().findByPrimaryKey(idMembro, MembroProjeto.class);
			ListaMensagens mensagens = new ListaMensagens();
			MembroProjetoValidator.validaRemoverMembroProjeto(obj, mensagens);
			if (mensagens.isEmpty()) {
				prepareMovimento(SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE);
				setOperacaoAtiva(SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE.getId());
				MovimentoCadastro mov = new MovimentoCadastro(obj, SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE);
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
				recarregarMembrosEquipe();	    
			}else {
				addMensagens(mensagens);
			}
		}else {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		}
		return forward(getParameter("lista_origem"));
	}
	
	/**
	 * Recarrega do banco a lista de membros da equipe dependendo da 
	 * origem da solicita��o.
	 * 
	 * @param listaOrigem
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	private void recarregarMembrosEquipe() throws DAOException, SegurancaException {
		String listaOrigem = getParameter("lista_origem");
		if (listaOrigem.equalsIgnoreCase(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA)){
			if (associados) 
				listarMembrosProjetosCoordenadorAssociados();
			else
				listarMembrosProjetosCoordenador();
		}
		if (listaOrigem.equalsIgnoreCase(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_PESQUISA))
			listarMembrosProjetosPesquisaCoordenador();
		
		if (listaOrigem.equalsIgnoreCase(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_UNICA_ACAO)) 
			listarMembrosProjeto();
		
		if (listaOrigem.equalsIgnoreCase(ConstantesNavegacaoProjetos.GERENCIAR_MEMBROS))
			listarMembrosProjeto();
	}
	
	/**
	 * Prepara para remo��o do membro da equipe.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista_unica_acao.jsp</li>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preRemoverMembroEquipe() throws ArqException {
		getCurrentRequest().setAttribute("listaOrigem", getParameter("lista_origem"));
		int idMembro = getParameterInt("idMembro", 0);
		if (idMembro > 0) {
			membroEquipe = getGenericDAO().findByPrimaryKey(idMembro, MembroProjeto.class);
			membroEquipe.getProjeto().getEquipe().iterator();
		}else {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		}

		prepareMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
		setReadOnly(true);
		setConfirmButton("Confirmar Finaliza��o");

		return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_ALTERA_FORM);
	}

	/**
	 * Altera/Finaliza membros da equipe do projeto. Nos casos de finaliza��o o
	 * membro da equipe � inativado. Altera��es no coordenador s�
	 * podem ser realizadas por gestores.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/MembroProjeto/altera_form.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String alterarMembroEquipe() throws ArqException {
		getCurrentRequest().setAttribute("listaOrigem", getParameter("lista_origem"));
		ListaMensagens mensagens = new ListaMensagens();

		//quem fez a �ltima altera��o!
		membroEquipe.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());

		// tamb�m n�o permite remo��o do coordenador da atividade pelo pr�prio coordenador.
		if (getConfirmButton().equalsIgnoreCase("CONFIRMAR FINALIZA��O")) {
			validateRequired(membroEquipe.getDataInicio(), "Data de In�cio", mensagens);
			validateRequired(membroEquipe.getDataFim(), "Data de Fim", mensagens);

			if (membroEquipe.isValido()) {
				ValidatorUtil.validaOrdemTemporalDatas(membroEquipe.getDataInicio(), membroEquipe.getDataFim(), true, "Per�odo", mensagens);
			}

			// somente gestores de extens�o ou pesquisa podem remover coordenadores
			if ((!isUserInRole(SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.GESTOR_PESQUISA)) && (membroEquipe.isCoordenador())) {
				mensagens.addErro("Coordenador(a) n�o pode ser removido.");
				mensagens.addErro("Toda a��o de acad�mica deve conter pelo menos um(a) coordenador(a).");
			}

		} else {
			MembroProjetoValidator.validaAlteracaoMembroExtensao(membroEquipe, getDAO(MembroProjetoDao.class), mensagens);
		}

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		try {

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(membroEquipe);
			mov.setCodMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);		
			recarregarMembrosEquipe();	
			return forward(getParameter("lista_origem"));

		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			addMensagemErroPadrao();
			return null;
		}
	}

	/**
	 * Prepara o ambiente e realiza valida��es relativas ao cadastro do novo membro da equipe.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista_unica_acao.jsp</li>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAdicionarMembroEquipe() throws ArqException {
		getCurrentRequest().setAttribute("listaOrigem", getParameter("lista_origem"));		
		int idProjeto = getParameterInt("id", 0);
		if (idProjeto > 0) {
			projeto = getGenericDAO().findByPrimaryKey(idProjeto, Projeto.class);
		}		
		// carrega todos os membros da equipe
		projeto.getEquipe().iterator();
		// prepara campos do mbean para cadastro de novo membro da a��o
		popularDadosMembroProjeto();
		prepareMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
		setConfirmButton("Confirmar Cadastro");
		setReadOnly(false);
		return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_CADASTRO_FORM);
	}

	/**
	 * Adiciona um novo membro � lista de membros da equipe.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/MembroProjeto/cadastro_form.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public String adicionarMembroEquipe() {
		getCurrentRequest().setAttribute("listaOrigem",	getParameter("lista_origem"));
		try {
			ListaMensagens mensagens = new ListaMensagens();
			int categoriaMembro = getParameterInt("categoriaMembro",0);
			if(categoriaMembro == 0) categoriaMembro = 1; // Docente � a categoria padr�o!
			getCurrentSession().setAttribute("categoriaAtual", categoriaMembro);
			
			membroEquipe.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			membroEquipe.setAtivo(true);
			if ( membroEquipe.getChDedicada() == null ) membroEquipe.setChDedicada(0); 
			validateRequired(membroEquipe.getDataInicio(), "Data de In�cio", mensagens);

			if (CategoriaMembro.DISCENTE == categoriaMembro)
				adicionaDiscente(mensagens);

			else if (CategoriaMembro.DOCENTE == categoriaMembro)
				adicionaDocente(mensagens);

			else if (CategoriaMembro.SERVIDOR == categoriaMembro)
				adicionaServidor(mensagens);

			else if (CategoriaMembro.EXTERNO == categoriaMembro)
				adicionaParticipanteExterno(mensagens);

			MembroProjetoValidator.validaChTotalMembroProjeto(projeto.getEquipe(), membroEquipe, mensagens);

			if(hasErrors()){
				addMensagens(mensagens);
				return null;
			}
			
			// Salva no banco o membro
			salvarMembroEquipe(membroEquipe);
			recarregarMembrosEquipe();				
			return forward(getParameter("lista_origem"));
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	}

	/**
	 * Adiciona um discente � lista de Membros do Projeto.
	 * 
	 * @return
	 */
	private void adicionaDiscente(ListaMensagens mensagens) {
		getCurrentSession().setAttribute("aba", "membro-discente");
		try {
			if ((discente == null) || (discente.getId() == 0)) {
				discente = new Discente();
				mensagens.addErro("Selecione o Discente.");
			}

			membroEquipe.setDiscente(getGenericDAO().findByPrimaryKey(discente.getId(), Discente.class));
			membroEquipe.setFuncaoMembro(getGenericDAO().findByPrimaryKey(funcaoDiscente.getId(), FuncaoMembro.class));
			
			MembroProjetoHelper.adicionarDiscente(getGenericDAO(), membroEquipe, projeto, mensagens); 
			
			if(membroParticipaEquipe())
				mensagens.addErro("Membro j� pertence a equipe.");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
	}
	
	/**
	 * Adiciona um docente � lista de Membros do Projeto.
	 * 
	 * @return
	 */
	private void adicionaDocente(ListaMensagens mensagens) {
		adicionaDocenteSemValidacao(mensagens);
		try {
			membroEquipe.setFuncaoMembro(getGenericDAO().findByPrimaryKey(funcaoDocente.getId(), FuncaoMembro.class));

			MembroProjetoHelper.adicionarDocente(getGenericDAO(), membroEquipe, projeto, mensagens);
			
			if(membroParticipaEquipe())
				mensagens.addErro("Membro j� pertence a equipe.");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
	}
	
	/**
	 * Adiciona um servidor ao membroEquipe
	 * M�todo utilizado pelos m�todos adicionaDocente e alterarCoordenador
	 * 
	 * @param mensagens
	 */
	private void adicionaDocenteSemValidacao(ListaMensagens mensagens){
		getCurrentSession().setAttribute("aba", "membro-docente");
		try {
			if ((docente == null) || (docente.getId() == 0)) {
				docente = new Servidor();
				mensagens.addErro("Selecione o Docente.");
			}

			membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(docente.getId(), Servidor.class));
		}catch (Exception e){
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
	}

	/**
	 * Adiciona um servidor � lista de Membros do Projeto.
	 * 
	 * @return
	 */
	private void adicionaServidor(ListaMensagens mensagens) {
		adicionaServidorSemValidacao(mensagens);
		try {
			membroEquipe.setFuncaoMembro(getGenericDAO().findByPrimaryKey(funcaoServidor.getId(), FuncaoMembro.class));

			MembroProjetoHelper.adicionarServidor(getGenericDAO(), membroEquipe, projeto, mensagens);
			
			if(membroParticipaEquipe())
				mensagens.addErro("Membro j� pertence a equipe.");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
	}
	
	/**
	 * Adiciona um servidor ao membroEquipe
	 * M�todo utilizado pelos m�todos adicionaServidor e alterarCoordenador
	 * 
	 * @param mensagens
	 */
	private void adicionaServidorSemValidacao(ListaMensagens mensagens){
		getCurrentSession().setAttribute("aba", "membro-servidor");
		try {
			if ((servidor == null) || (servidor.getId() == 0)) {
				servidor = new Servidor();
				mensagens.addErro("Selecione o Sevidor.");
			}

			membroEquipe.setServidor(getDAO(MembroProjetoDao.class).findByPrimaryKey(servidor.getId(), Servidor.class));
		}catch(Exception e){
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
	}

	/**
	 * Adiciona um participante externo � lista de Membros do Projeto.
	 * 
	 * @return
	 * @param mensagens
	 */
	private void adicionaParticipanteExterno(ListaMensagens mensagens) {
		getCurrentSession().setAttribute("aba", "membro-externo");
		try {
			if ((participanteExterno == null) || (participanteExterno.getPessoa() == null)
					|| (participanteExterno.getPessoa().getNome() == null)) {
				participanteExterno = new ParticipanteExterno();
				cpf = "";
				mensagens.addErro("Selecione o Participante Externo.");
			}
			  
			participanteExterno.setPessoa( membroEquipe.getParticipanteExterno().getPessoa() );
			participanteExterno.getPessoa().setNome( participanteExterno.getPessoa().getNome() != null ? participanteExterno.getPessoa().getNome().trim().toUpperCase() : "" );
			membroEquipe.setFuncaoMembro(getGenericDAO().findByPrimaryKey(funcaoExterno.getId(), FuncaoMembro.class));
			membroEquipe.setParticipanteExterno(participanteExterno);
			membroEquipe.getParticipanteExterno().getPessoa().setCpf_cnpj( Long.parseLong(Formatador.getInstance().parseStringCPFCNPJ(cpf)) );

			Pessoa p = verificaPessoaExternaCadastrada(membroEquipe.getParticipanteExterno().getPessoa());
			if ( p != null ) {
				membroEquipe.getParticipanteExterno().setPessoa(p);
				MembroProjetoHelper.adicionarParticipanteExterno(getGenericDAO(), membroEquipe, projeto, mensagens);
			}
			
			if(membroParticipaEquipe())
				mensagens.addErro("Membro j� pertence a equipe.");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
	}
	
	
	/**
	 * Verifica se um poss�vel Membro j� participa de uma Equipe.
	 * 
	 * @param mensagens
	 * @return
	 */
	private boolean membroParticipaEquipe(){
		for(MembroProjeto mp : projeto.getEquipe()){
			if(pesquisa && mp.getPessoa().equals(membroEquipe.getPessoa()))
				return true;
			else if(mp.getPessoa().equals(membroEquipe.getPessoa()) &&
					mp.getFuncaoMembro().equals(membroEquipe.getFuncaoMembro()))
				return true;
		}
		return false;
	}

    /**
     * M�todo utilizado para salvar um membro da equipe do projeto no banco.
     * 
     * @param membro
     * @throws ArqException
     * @throws NegocioException
     */
    private MembroProjeto salvarMembroEquipe(MembroProjeto membro) throws ArqException, NegocioException {
    	try {
    		
    		MovimentoCadastro mov = new MovimentoCadastro();
    		mov.setObjMovimentado(membro);
    		mov.setCodMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
    		membro = execute(mov, getCurrentRequest());
    		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
    		projeto.addMembroEquipe(membro);
    		return membro;
    	} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
    	} catch (Exception e) {
    		addMensagemErroPadrao();
    		return null;
    	}
    }
	
	/**
	 * Prepara os objetos do mbean para o cadatro de novo membro da equipe.
	 */
	private void popularDadosMembroProjeto() {
		cpf = "";
		docente = new Servidor();
		servidor = new Servidor();
		discente = new Discente();
		participanteExterno = new ParticipanteExterno();
		membroEquipe = new MembroProjeto();
		membroEquipe.setProjeto(projeto);
		membroEquipe.setDataFim(projeto.getDataFim());
		membroEquipe.setFuncaoMembro(new FuncaoMembro());
		membroEquipe.setDocenteExterno(new DocenteExterno());
		membroEquipe.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		membroEquipe.setAtivo(true);
	}

	/**
	 * Verifica se o j� existe alguem na base dos sistema com o CPF informado.
	 * Auxilia no cadastro de um novo participante externo.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/MembroProjeto/cadastro_form.jsp</li>
	 *	</ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void buscarParticipanteExternoByCPF() throws DAOException {

		PessoaDao pessoaDao = getDAO(PessoaDao.class);

		try {

			// limpa os dados
			docente = new Servidor();
			servidor = new Servidor();
			membroEquipe = new MembroProjeto();
			membroEquipe.setProjeto(projeto);
			membroEquipe.setFuncaoMembro(new FuncaoMembro());
			membroEquipe.setParticipanteExterno(new ParticipanteExterno());
			if (ValidatorUtil.isEmpty(membroEquipe.getDataFim())) {
				membroEquipe.setDataFim(projeto.getDataFim());
			}				    

			if (participanteExterno == null) {
				participanteExterno = new ParticipanteExterno();
			}

			// pessoa internacional
			if (participanteExterno.getPessoa().isInternacional()) {
				participanteExterno.setPessoa(new Pessoa());
				membroEquipe.setParticipanteExterno(participanteExterno);
				// permite a edi��o do nome da pessoa pelo usu�rio
				membroEquipe.setSelecionado(true);
				cpf = "";

			} else {
				// permite a edi��o do nome da pessoa pelo usu�rio
				membroEquipe.setSelecionado(false);
			}

			if ((cpf != null) && (!cpf.trim().equals(""))) {
				avisosAjax.clear();

				if (!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(UFRNUtils.parseCpfCnpj(cpf))) {
					getAvisosAjax().add(new MensagemAviso("CPF inv�lido!", TipoMensagemUFRN.ERROR));
				} else {

					Pessoa p = pessoaDao.findByCpf(UFRNUtils.parseCpfCnpj(cpf));
					participanteExterno.setPessoa(p);

					if (participanteExterno.getPessoa() == null) {
						p = new Pessoa();
						p.setCpf_cnpjString(cpf);
						participanteExterno.setPessoa(p);
						membroEquipe.setParticipanteExterno(participanteExterno);
						// permite a edi��o do nome da pessoa pelo usu�rio
						membroEquipe.setSelecionado(true);
					} else {
						// n�o permite inclusao do nome da pessoa
						membroEquipe.setSelecionado(false);
					}

				}

			}

		} catch (Exception e) {
			notifyError(e);
		} finally {
			pessoaDao.close();
		}

		getCurrentSession().setAttribute("aba", "membro-externo");
		getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.EXTERNO);

	}

	/**
	 * Verifica, pelo cpf_cnpj, se a pessoa externa j� est� no banco. 
	 * Caso esteja, retorna a pessoa encontrada.
	 * Caso n�o esteja, persiste a pessoa externa.
	 * 
	 * @param pessoa
	 *            Objeto do tipo pessoa
	 * @return pessoa Objeto do tipo pessoa se cadastrou com sucesso, Retorna
	 *         Null se falhar.
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws DAOException
	 */
	private Pessoa verificaPessoaExternaCadastrada(Pessoa pessoa) throws ArqException, RemoteException {

		PessoaDao pessoaDao = getDAO(PessoaDao.class);

		if ((pessoa.isInternacional())	|| ((pessoaDao.findByCpf(pessoa.getCpf_cnpj()) == null))) {

			pessoa.setTipo('F');

			try {

				ProjetoPesquisaValidator.limparDadosPessoa(pessoa);

				// Cadastrar a pessoa
				PessoaMov pessoaMov = new PessoaMov();
				pessoaMov.setPessoa(pessoa);
				pessoaMov.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
				pessoaMov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);

				prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA);
				pessoa = (Pessoa) execute(pessoaMov, getCurrentRequest());

			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			} finally {
				// Repreparar o comando anterior
				prepareMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO);
				pessoaDao.close();
			}

			// pessoa = pessoaDao.findByCpf(pessoa.getCpf_cnpj());

		} else { // a pessoa j� est� no banco...

			// Buscar pessoa pelo CPF
			pessoa = pessoaDao.findByCpfCnpj(pessoa.getCpf_cnpj()).iterator().next();
			pessoa = pessoaDao.findByPrimaryKey(pessoa.getId(), Pessoa.class);

		}
		return pessoa;

	}
	
	/**
	 * M�todo utilizado para preparar a altera��o do coordenador de uma a��o de extens�o
	 * Esta opera��o s� pode ser realizada pelo coordenador atual da a��o de extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista.jsp</li>
	 *	</ul>
	 *
	 * @author julio
	 * @return
	 * @throws ArqException 
	 */
	public String preAlterarCoordenador() throws ArqException{
		getCurrentRequest().setAttribute("listaOrigem",	getParameter("lista_origem"));
		getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.DOCENTE);
		getCurrentSession().setAttribute("aba", "membro-docente");
		int idMembro = getParameterInt("idMembro",0);
		int idProjeto = getParameterInt("idProjeto",0);
		if ( (idMembro > 0) && (idProjeto > 0) ){
			membroEquipeAux = getGenericDAO().findByPrimaryKey(idMembro, MembroProjeto.class); // Salva o coordenador atual
			projeto = getGenericDAO().findByPrimaryKey(idProjeto, Projeto.class); // Recupera o projeto atual
			projeto.getEquipe().iterator(); // carrega todos os membros da equipe
			popularDadosMembroProjeto(); // prepara campos do mbean para cadastro de novo membro da a��o
			membroEquipe.setFuncaoMembro(membroEquipeAux.getFuncaoMembro()); // Seta a fun��o do novo coordenador para "Coordenador"
			prepareMovimento(SigaaListaComando.ALTERAR_COORDENADOR_PROJETO_BASE);
		}else{
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		}
		return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_ALTERA_COORD);
	}
	
	/**
	 * M�todo utilizado para alterar o coordenador de uma a��o de extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/projetos/MembroProjeto/altera_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @author julio
	 * @return
	 * @throws ArqException 
	 */
	public String alterarCoordenador() throws ArqException{
		membroEquipe.setFuncaoMembro(membroEquipeAux.getFuncaoMembro());
		try{
			ListaMensagens mensagens = new ListaMensagens();
			int categoriaMembro = getParameterInt("categoriaMembro");
			getCurrentSession().setAttribute("categoriaAtual", categoriaMembro);
			getCurrentRequest().setAttribute("listaOrigem", getParameter("lista_origem"));
			
			// Valida atributos espec�ficos do membro a ser inserido
			if (categoriaMembro == CategoriaMembro.DOCENTE){
				getCurrentSession().setAttribute("aba", "membro-docente");
				validateRequired(docente.getPessoa().getNome(), "Nome", mensagens);
			}else if(categoriaMembro == CategoriaMembro.SERVIDOR){
				getCurrentSession().setAttribute("aba", "membro-servidor");
				validateRequired(servidor.getPessoa().getNome(), "Nome", mensagens);
			}
			
			// Valida a Carga Hor�ria e a data de in�cio do novo coordenador
			validateRequired(membroEquipe.getChDedicada(), "CH Semanal", mensagens);
			validateRequired(membroEquipe.getDataInicio(), "Data de In�cio", mensagens);
			if(membroEquipe.getChDedicada() != null && membroEquipe.getChDedicada() > MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA){
				mensagens.addMensagem(MensagensExtensao.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA, MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA);
			}
			
			// Verifica se da data de in�cio do novo coordenador(= data final do coordenador atual) est� dentro do per�odo permitido
			if(!CalendarUtils.isDentroPeriodo(membroEquipeAux.getDataInicio(), membroEquipeAux.getProjeto().getDataFim(), membroEquipe.getDataInicio())){
				mensagens.addErro("A Data In�cio do novo coordenador est� vazia ou n�o est� no per�odo permitido. Insira uma data entre: " +
						Formatador.getInstance().formatarData(membroEquipeAux.getDataInicio()) + " a "+ Formatador.getInstance().formatarData(membroEquipeAux.getProjeto().getDataFim()));
			}
			
			// Verifica se houve algum erro de valida��o
			if(!mensagens.isEmpty()){
				addMensagens(mensagens); 
				return null; 
			}
			
			// Seta a data final do coordenador atual com a data de in�cio do novo coordenador
			membroEquipeAux.setDataFim(CalendarUtils.subtraiDias(membroEquipe.getDataInicio(),1));
			
			// Completa os dados do novo coordenador de acordo com sua categoria ap�s valida��o dos dados b�sicos
			CategoriaMembro cat = new CategoriaMembro(categoriaMembro);
			membroEquipe.setCategoriaMembro(cat);
			if (CategoriaMembro.DOCENTE == categoriaMembro){
				adicionaDocenteSemValidacao(mensagens);
			} else if (CategoriaMembro.SERVIDOR == categoriaMembro){
				adicionaServidorSemValidacao(mensagens);
			} 
			MembroProjetoValidator.validaCoordenacaoSimultaneaExtensao(null, membroEquipe, mensagens);
			MembroProjetoValidator.validaCoordenacao(membroEquipe, mensagens);
			if(membroParticipaEquipe())	mensagens.addErro("Membro j� pertence a equipe.");	
			
			// Verifica se houve algum erro de valida��o
			if(!mensagens.isEmpty()){
				addMensagens(mensagens); 
				return null;
			}
			
			// Envia dados para o processador
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ALTERAR_COORDENADOR_PROJETO_BASE);
			mov.setObjMovimentado(membroEquipe);
			mov.setObjAuxiliar(membroEquipeAux);
			execute(mov);
			
		}catch(Exception e){
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
		recarregarMembrosEquipe();
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return gerenciarMembrosProjeto();
	}

	
	@Override
	public String getDirBase() {
		return "/projetos/MembroProjeto";
	}
	
	/**
	 * M�todo que retorna o usu�rio para a p�gina de listagem
	 * <br />
	 * M�todo usado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s) </li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarLista() {
		String listPage = super.getListPage();
		return forward(listPage);
	}
	
	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public ParticipanteExterno getParticipanteExterno() {
		return participanteExterno;
	}

	public void setParticipanteExterno(ParticipanteExterno participanteExterno) {
		this.participanteExterno = participanteExterno;
	}

	public List<MensagemAviso> getAvisosAjax() {
		return avisosAjax;
	}

	public void setAvisosAjax(List<MensagemAviso> avisosAjax) {
		this.avisosAjax = avisosAjax;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	/**
	 * Lista todos os membros de equipes de extens�o coordenadas pelo usu�rio atual.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	private void listarMembrosProjetosCoordenador() throws DAOException, SegurancaException {
		if (!DesignacaoFuncaoProjetoHelper.isCoordenadorOrDesignacaoCoordenador(getUsuarioLogado().getPessoa().getId())) {
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o.");
		}
		pesquisa = false;
		associados = false;
		membrosProjetos = getDAO(MembroProjetoDao.class).findByCoordenador( getUsuarioLogado().getServidorAtivo());
	}
	/**
	 * Lista todos os membros de projetos de a��es associadas onde s�o coordenados pelo usu�rio atual.
	 */
	private void listarMembrosProjetosCoordenadorAssociados() throws DAOException, SegurancaException {
		associados = true;
		Pessoa pessoa = (getDocenteExternoUsuario() != null ? getDocenteExternoUsuario().getPessoa() : getUsuarioLogado().getServidor().getPessoa());
		membrosProjetos = getDAO(ProjetoAssociadoDao.class).findByCoordenadorAssociados(pessoa);
	}
	
	/**
	 * Lista todos os membros de projetos de pesquisa onde s�o coordenados pelo usu�rio atual.
	 * 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	private void listarMembrosProjetosPesquisaCoordenador() throws SegurancaException, DAOException{
		if (!getAcessoMenu().isCoordPesquisa())
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o.");
		pesquisa = true;
		associados = false;
		membrosProjetos = getDAO(MembroProjetoDao.class).findByCoordenadorPesquisa(getUsuarioLogado().getServidorAtivo());
	}
	
	/**
	 * Lista todos os membros de projetos de pesquisa onde s�o coordenados pelo usu�rio atual.
	 * 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	private void listarMembrosProjetosPesquisaSelecionado() throws SegurancaException, DAOException{
		getGenericDAO().refresh(projeto);   
		pesquisa = true;
		membrosProjetos = getGenericDAO().findAtivosByExactField(MembroProjeto.class, "projeto.id", projeto.getId(), "pessoa", "asc");
	}

	/**
	 * Lista todos os membros de equipes de extens�o da a��o selecionada.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	private void listarMembrosProjeto() throws DAOException, SegurancaException {
		getGenericDAO().refresh(projeto);   
		pesquisa = false;
		membrosProjetos = getGenericDAO().findAtivosByExactField(MembroProjeto.class, "projeto.id", projeto.getId(), "pessoa", "asc");
	}

	/**
	 * M�todo utilizado para preparar a altera��o compuls�ria do coordenador de uma proposta
	 * de a��o acad�mica associada.
	 * Esta opera��o s� pode ser realizada por um membro do comit� integrado de ensino, pesquisa e extens�o.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/projetos/AlteracaoProjeto/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAlterarCoordenador() throws ArqException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		prepareMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
		int id = getParameterInt("id",0);

		projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);    
		membrosProjetos = getGenericDAO().findAtivosByExactField(MembroProjeto.class, "projeto.id", projeto.getId(), "pessoa", "asc");
		getCurrentRequest().setAttribute("listaOrigem",	ConstantesNavegacaoProjetos.GERENCIAR_MEMBROS);	    
		return forward(ConstantesNavegacaoProjetos.GERENCIAR_MEMBROS);	    

	}
	
	/**
	 * Inicia o cadastro/atualizacao/remo��o de membros da equipe da a��o para bolsistas.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/extensao/AlterarAtividade/lista.jsp</li>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarMembrosProjeto() throws ArqException {
		if ( !DesignacaoFuncaoProjetoHelper.isCoordenadorOrDesignacaoCoordenador(getUsuarioLogado().getPessoa().getId())) {
			throw new SegurancaException(
					"Usu�rio n�o autorizado a realizar esta opera��o.");
		}
		if(getCurrentRequest().getAttribute("listaOrigem") != null){
			if(getCurrentRequest().getAttribute("listaOrigem").toString().equals(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA)){
				getCurrentRequest().setAttribute("listaOrigem",	ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA);		
				listarMembrosProjetosCoordenador();
				return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA);
			}else {
				getCurrentRequest().setAttribute("listaOrigem", ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_UNICA_ACAO);
				return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_UNICA_ACAO);
			}
		}else {
			getCurrentRequest().setAttribute("listaOrigem",	ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA);		
			listarMembrosProjetosCoordenador();
			return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA);
		}

	}
	
	/**
	 * Gerenciamento dos membros das a��es academicas associadas.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarMembrosProjetoAssociados() throws ArqException {
		getCurrentRequest().setAttribute("listaOrigem",	ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA);		
		getCurrentRequest().getSession().setAttribute("tipoEdital", Edital.ASSOCIADO);
		listarMembrosProjetosCoordenadorAssociados();
		if (isCoordenadorAssociado(membrosProjetos)) 
			return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA);
		else{
			addMensagemErro("S� pode acessar essa funcionalidade os coordenadores de A��es Integradas em Execu��o ou Conclu�das.");
			return null;
		}
	}

	/**
	 * Verifica se o usu�rio logado e coordenador de alguma a��o academica associada.
	 */
	public boolean isCoordenadorAssociado(Collection<MembroProjeto> membros){
	    for (MembroProjeto membro : membros) {
			if (membro.isCoordenadorAtivo() && membro.getPessoa().getId() == getUsuarioLogado().getPessoa().getId()) 
			    return true;
	    }
	    return false;
	}
	
	/**
	 * Inicia o cadastro/atualizacao/remo��o de membros da equipe de Projetos de Pesquisa.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarMembrosProjetoPesquisa() throws ArqException {
		getCurrentRequest().getSession().setAttribute("tipoEdital", Edital.PESQUISA);

		if (!getAcessoMenu().isCoordPesquisa() && !isUserInRole(SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.ADMINISTRADOR_PESQUISA)) {
			throw new SegurancaException(
					"Usu�rio n�o autorizado a realizar esta opera��o.");
		}

		getCurrentRequest().setAttribute("listaOrigem",	ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_PESQUISA);
		if(projeto != null && projeto.getId() > 0 ){
			listarMembrosProjetosPesquisaSelecionado();
			return redirectJSF(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_PESQUISA);
		} else {
			listarMembrosProjetosPesquisaCoordenador();
			return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_PESQUISA);
		}
	}

	/**
	 * Inicia o cadastro/atualizacao/remocao de membros da equipe da a��o por
	 * membros da proex membros da proex podem alterar coordenador da a��o.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/extensao/AlterarAtividade/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarMembrosProjetoAcaoSelecionada()	throws ArqException {
	    getCurrentRequest().setAttribute("listaOrigem", ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_UNICA_ACAO);
	    getCurrentRequest().getSession().setAttribute("tipoEdital", Edital.EXTENSAO);
	    
	    checkRole(SigaaPapeis.GESTOR_EXTENSAO,
	    		SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
	    		SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
	    		SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO);

	    int idProjeto = getParameterInt("id", 0);
	    if (idProjeto > 0) {
	    	projeto = getGenericDAO().findByPrimaryKey(idProjeto, Projeto.class);
	    	listarMembrosProjeto();		
	    	prepareMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
	    	return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_LISTA_UNICA_ACAO);
	    }else {
	    	addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
	    	return null;
	    }
	}

	/**
	 * Carrega membro da equipe do projeto MBean para visualiza��o.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
	 *		<li>sigaa.war/extensao/DocumentosAutenticados/membros.jsp</li>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista_unica_acao.jsp</li>
	 *		<li>sigaa.war/projetos/MembroProjeto/lista.jsp</li>
	 *		<li>sigaa.war/monitoria/DocumentosAutenticados/lista_docentes.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public String view() {
		Integer id = getParameterInt("idMembro",0);
		try {
			obj = getGenericDAO().findByPrimaryKey(id, MembroProjeto.class);
			return forward(ConstantesNavegacaoProjetos.MEMBROPROJETO_VIEW);
		} catch (Exception e) {
			notifyError(e);
			return null;
		}
	}
	
	/**
	 * M�todo que responde �s requisi��es de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de docentes
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/MembroProjeto/cadastro_form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Servidor> autoCompleteNomeDocente(Object event) throws DAOException{
		String nome = event.toString();
		return (List<Servidor>) getDAO(ServidorDao.class).findByDocente(nome, 0, true, false);
	}
	
	/**
	 * Carrega o docente do autocomplete
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/MembroProjeto/cadastro_form.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 */
	public void carregaDocente(ActionEvent e){
		docente = (Servidor) e.getComponent().getAttributes().get("docenteAutoComplete");
	}
	
	/**
	 * M�todo que responde �s requisi��es de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Tecnicos Administrativos
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/MembroProjeto/cadastro_form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException 
	 */
	public List<Servidor> autoCompleteNomeServidorTecnico(Object event) throws DAOException{
		String nome = event.toString();
 		return (List<Servidor>) getDAO(ServidorDao.class).findByNome(nome, 0, true, false, Categoria.TECNICO_ADMINISTRATIVO, false);
	}
	
	/**
	 * Carrega o Servidor T�cnico administrativo do autocomplete
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/MembroProjeto/cadastro_form.jsp</li>
	 * </ul>
	 * @param e
	 */
	public void carregaServidorTecnico(ActionEvent e){
		servidor = (Servidor) e.getComponent().getAttributes().get("servidorAutoComplete");
	}
	
	/**
	 * M�todo que responde �s requisi��es de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Discentes
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/MembroProjeto/cadastro_form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Discente> autoCompleteNomeDiscente(Object event) throws DAOException{
		String nome = event.toString();
		if ( projeto.getTipoProjeto().getId() == TipoProjeto.PESQUISA && projeto.isInterno() )
				return (List<Discente>) getDAO(DiscenteDao.class).findByNome(nome, 0, new char[]{ NivelEnsino.MESTRADO, NivelEnsino.DOUTORADO }, null, false, true, new PagingInformation());		 
		return (List<Discente>) getDAO(DiscenteDao.class).findByNome(nome, 0, ' ', new PagingInformation());		 
	}
	
	/**
	 * Carrega o Discente do autocomplete
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		</li>sigaa/projetos/MembroProjeto/cadastro_form.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 */
	public void carregaDiscente(ActionEvent e){
		discente = (Discente) e.getComponent().getAttributes().get("discenteAutoComplete");
	}
	
	/**
	 * Retorna as fun��es que os Docentes podem desempenhar no projeto 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllFuncaoDocente() throws DAOException {
		if ( projeto.getTipoProjeto().getId() == TipoProjeto.PESQUISA ) {
			FuncaoMembroDao dao = getDAO(FuncaoMembroDao.class);
			try {
				return toSelectItems(dao.findByFuncoesPesquisa(FuncaoMembro.ESCOPO_DOCENTE), "id", "descricao");
			} finally {
				dao.close();
			}
		
		} else {
			FuncaoMembroEquipeMBean mBean = getMBean("funcaoMembroEquipe");
			return mBean.getAllServidoresCombo(); 
		}
	}

	/**
	 * Retorna as fun��es que os Servidores podem desempenhar no projeto  
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllFuncaoServidor() throws DAOException {
		if ( projeto.getTipoProjeto().getId() == TipoProjeto.PESQUISA ) {
			FuncaoMembroDao dao = getDAO(FuncaoMembroDao.class);
			try {
				return toSelectItems(dao.findByFuncoesPesquisa(FuncaoMembro.ESCOPO_SERVIDOR), "id", "descricao");
			} finally {
				dao.close();
			}
		
		} else {
			FuncaoMembroEquipeMBean mBean = getMBean("funcaoMembroEquipe");
			return mBean.getAllServidoresCombo(); 
		}
	}

	/**
	 * Retorna as fun��es que os Discentes podem desempenhar no projeto
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllFuncaoDiscente() throws DAOException {
		if ( projeto.getTipoProjeto().getId() == TipoProjeto.PESQUISA ) {
			FuncaoMembroDao dao = getDAO(FuncaoMembroDao.class);
			try {
				return toSelectItems(dao.findByFuncoesPesquisa(FuncaoMembro.ESCOPO_DISCENTE), "id", "descricao");
			} finally {
				dao.close();
			}
		
		} else {
			FuncaoMembroEquipeMBean mBean = getMBean("funcaoMembroEquipe");
			return mBean.getAllDiscentesCombo(); 
		}
	}

	/**
	 * Retorna as fun��es que os Servidores podem desempenhar no projeto
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllFuncaoExterno() throws DAOException {
		if ( projeto.getTipoProjeto().getId() == TipoProjeto.PESQUISA ) {
			FuncaoMembroDao dao = getDAO(FuncaoMembroDao.class);
			try {
				return toSelectItems(dao.findByFuncoesPesquisa(FuncaoMembro.ESCOPO_EXTERNO), "id", "descricao");
			} finally {
				dao.close();
			}
		
		} else {
			FuncaoMembroEquipeMBean mBean = getMBean("funcaoMembroEquipe");
			return mBean.getAllServidoresCombo(); 
		}
	}

	public boolean ischPassivoAlteracao() {
		if ( membroEquipe.getProjeto().getTipoProjeto().isExtensao() && 
				membroEquipe.getProjeto().getDataCadastro().after( 
					ParametroHelper.getInstance().getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA) ) ) {
			return false;
		}
		return true;
	}
	
	public MembroProjeto getMembroEquipe() {
		return membroEquipe;
	}

	public void setMembroEquipe(MembroProjeto membroEquipe) {
		this.membroEquipe = membroEquipe;
	}

	public Collection<MembroProjeto> getMembrosProjetos() {
	    return membrosProjetos;
	}

	public void setMembrosProjetosCoordenador(Collection<MembroProjeto> membrosProjetos) {
	    this.membrosProjetos = membrosProjetos;
	}

	public Projeto getProjeto() {
	    return projeto;
	}

	public void setProjeto(Projeto projeto) {
	    this.projeto = projeto;
	}

	public Discente getDiscente() {
	    return discente;
	}

	public void setDiscente(Discente discente) {
	    this.discente = discente;
	}

	public FuncaoMembro getFuncaoDocente() {
	    return funcaoDocente;
	}

	public void setFuncaoDocente(FuncaoMembro funcaoDocente) {
	    this.funcaoDocente = funcaoDocente;
	}

	public FuncaoMembro getFuncaoDiscente() {
	    return funcaoDiscente;
	}

	public void setFuncaoDiscente(FuncaoMembro funcaoDiscente) {
	    this.funcaoDiscente = funcaoDiscente;
	}

	public FuncaoMembro getFuncaoServidor() {
	    return funcaoServidor;
	}

	public void setFuncaoServidor(FuncaoMembro funcaoServidor) {
	    this.funcaoServidor = funcaoServidor;
	}

	public FuncaoMembro getFuncaoExterno() {
	    return funcaoExterno;
	}

	public void setFuncaoExterno(FuncaoMembro funcaoExterno) {
	    this.funcaoExterno = funcaoExterno;
	}

	public boolean isPesquisa() {
		return pesquisa;
	}

	public void setPesquisa(boolean pesquisa) {
		this.pesquisa = pesquisa;
	}

	public MembroProjeto getMembroEquipeAux() {
		return membroEquipeAux;
	}

	public void setMembroEquipeAux(MembroProjeto membroEquipeAux) {
		this.membroEquipeAux = membroEquipeAux;
	}

	public boolean isAssociados() {
		return associados;
	}

	public void setAssociados(boolean associados) {
		this.associados = associados;
	}

}