/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '21/02/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.forum.dao.ForumGeralDao;
import br.ufrn.sigaa.ava.forum.dao.ForumGeralMensagemDao;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralMensagem;

/**
 * Managed-Bean para gerenciamento de Geral de Fóruns.
 * 
 * @author Ilueny Santos
 * 
 */
@Component("forumBean")
@Scope("session")
public class ForumMBean extends AbstractControllerCadastro<ForumGeral> {
	
	/** Lista de tópicos do fórum selecionado. */
	private List<ForumGeralMensagem> topicos = new ArrayList<ForumGeralMensagem>();
	
	/** Lista de fóruns cadastrados pelo usuário atual. */
	private List<ForumGeral> foruns = new ArrayList<ForumGeral>();
	
	/** Construtor padrão. */
	public ForumMBean() {
		obj = new ForumGeral();
	}
	
    /** Diretório Base do Fórum.   */
	@Override
	public String getDirBase() {
		return "/ava/Foruns";
	}
	
    /**
     * Redireciona para a lista de fóruns cadastrados.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/lista.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String listar() throws ArqException {
		atualizarForuns();
		return super.listar();
	}
	
	/** Prepara para o cadastro de um novo fórum. */
    /**
     * Redireciona para a lista de fóruns cadastrados.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/lista.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {		
		//Configurando valores default para o novo fórum.
		obj = ForumGeral.getNewInstanceForumDefault();		
		prepareMovimento(SigaaListaComando.CADASTRAR_FORUM_GERAL);
		return super.preCadastrar();
	}
	
	
	/** 
	 * Cadastra um novo fórum e exibe lista atualizada de fóruns cadastrados.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/form.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();

		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return inativar();
		} else {

			ListaMensagens lista = obj.validate();
			if (lista != null && !lista.isEmpty()) {
				addMensagens(lista);
				return null;
			}			
			try {
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				
				if (obj.getId() == 0) {					
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_FORUM_GERAL);
				} else {
					if( !checkOperacaoAtiva(SigaaListaComando.ALTERAR_FORUM_GERAL.getId()) ){
						return cancelar();
					}
					mov.setCodMovimento(SigaaListaComando.ALTERAR_FORUM_GERAL);
				}
				
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
				removeOperacaoAtiva();
				return view();
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			}
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_FORM);
		}
	}

	/** 
	 * Prepara o ambiente para alteração do fórum selecionado. 
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/lista.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String atualizar() throws ArqException {
		if (ValidatorUtil.isEmpty(obj)) {
			addMensagemErro("Fórum selecionado não é um fórum válido.");
			return null;
		}

		try {
			setOperacaoAtiva(SigaaListaComando.ALTERAR_FORUM_GERAL.getId());
			prepareMovimento(SigaaListaComando.ALTERAR_FORUM_GERAL);

			setReadOnly(false);
			this.obj =  getGenericDAO().findByPrimaryKey(obj.getId(), ForumGeral.class);
			setConfirmButton("Alterar");
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_FORM);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}
	}

		
	/**
	 * Ver detalhes do fórum selecionado. 
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/lista.jsp</li>
     * </ul>
     * 
     */
	public String view() throws DAOException {
		if (ValidatorUtil.isEmpty(obj)) {
			addMensagemErro("Fórum selecionado não é um fórum válido.");
			return null;
		}

		obj = getGenericDAO().findByPrimaryKey(obj.getId(), ForumGeral.class);
		filtrarTopicosForum();
		return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_VIEW);		
	}
	
	
    /**
     * Visualizar o arquivo anexo.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/view.jsp</li>
     * </ul>
     * 
     */
    public void viewArquivo() {
    	try {
    		int idArquivo = getParameterInt("idArquivo");
    		EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
    	} catch (Exception e) {
    		notifyError(e);
    		addMensagemErro("Arquivo não encontrado!");
    		return;
    	}
    	FacesContext.getCurrentInstance().responseComplete();
    }

	
	/** 
	 * Lista os tópicos do fórum selecionado. 
     * <br />
     * 
     * Método não chamado por JSP(s):
     * 
     */
	public void filtrarTopicosForum() {
		topicos = getDAO(ForumGeralMensagemDao.class).findTopicosByForum(obj.getId());
	}
	
	/** 
	 * Lista os fóruns do usuário atual.
     * <br />
     * 
     * Método não chamado por JSP(s):
     * 
     */
	public void atualizarForuns() {
		foruns = getDAO(ForumGeralDao.class).findByUsuario(getUsuarioLogado());
	}
	
	/**
     * Redireciona para a confirmação da remoção do fórum.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/view.jsp</li>
     * </ul>
     * 
     */
	@Override
	public String preRemover() {
		if (ValidatorUtil.isEmpty(obj)) {
			addMensagemErro("Fórum selecionado não é um fórum válido.");
			return null;
		}
		
		try {
			prepareMovimento(SigaaListaComando.REMOVER_FORUM_GERAL);
			obj = getDAO(ForumGeralDao.class).findByPrimaryKey(obj.getId(), ForumGeral.class);
			
			//Evitar erro de lazy
			obj.getTopicos().iterator();
			filtrarTopicosForum();
			
			setConfirmButton("Remover fórum");
			return forward(ConstantesNavegacaoForum.JSP_FORUM_GERAL_TOPICO_REMOVER);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}
	
	/** 
	 * Remove um fórum selecionado.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Forums/remover.jsp</li>
     * </ul>
	 * @throws ArqException 
     * 
     */
	@Override
	public String remover() throws ArqException {
		checkChangeRole();

		try {

			if (ValidatorUtil.isEmpty(obj)) {
				addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			}

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.REMOVER_FORUM_GERAL);
			mov.setObjMovimentado(obj);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);

			return listar();

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return forward(ConstantesNavegacaoForum.JSP_FORUM_TURMA_FORM);
	}

	/**
	 * Limita o cadastro de tópicos por usuário para o fórum atual.
	 * Depende do tipo de fórum.
	 * 
	 * @throws DAOException 
	 */
	public boolean isPermiteNovoTopico() {
		boolean result = true;
		try {
			if (ValidatorUtil.isNotEmpty(obj) && obj.isCadaUsuarioApenasUmTopico()) {
				int qtdTopicos = getDAO(ForumGeralMensagemDao.class).findCountTopicosByForum(obj.getId(), getUsuarioLogado().getId());
				result = (qtdTopicos == 0);			
			}
		} catch (DAOException e) {
			notifyError(e);
		}
		return result;
	}
	
	
	public List<ForumGeralMensagem> getTopicos() {
		return topicos;
	}

	public void setTopicos(List<ForumGeralMensagem> topicos) {
		this.topicos = topicos;
	}
	
	public List<ForumGeral> getForuns() {
		return foruns;
	}

	public void setForuns(List<ForumGeral> foruns) {
		this.foruns = foruns;
	}
}
