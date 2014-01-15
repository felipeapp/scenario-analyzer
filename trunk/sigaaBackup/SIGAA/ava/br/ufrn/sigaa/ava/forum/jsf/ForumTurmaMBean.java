/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/04/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.forum.dao.ForumTurmaDao;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.jsf.MenuTurmaMBean;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Managed-Bean para gerenciamento de Fóruns na turma.
 * 
 * @author Ilueny Santos
 * 
 */
@Component("forumTurmaBean")
@Scope("request")
public class ForumTurmaMBean extends AbstractControllerCadastro<ForumTurma> {
	
	/** Bean com funcionalidades gerais de fórum. */
	private ForumMBean forumBean = getMBean("forumBean");
	
	/** Turma selecionada para criação do fórum. */
	private Turma turma = new Turma();
	
	/** Lista de fóruns da turma selecionada. */
	private List<ForumTurma> forunsTurma;
	
	/** 
	 * Construtor padrão. 
     * <br />
     * 
     * Método não chamado por JSP(s):
     * 
     */
	public ForumTurmaMBean() {
		obj = new ForumTurma();
		forumBean = getMBean("forumBean");
		forumBean.setObj(new ForumGeral());
	}
	
	/** 
	 * Atualiza e exibe a lista de fóruns da turma.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/Foruns/lista.jsp</li>
     * </ul>
     * 
     */
	public String listar() throws ArqException {
		if (ValidatorUtil.isNotEmpty(turma) ) {
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			forunsTurma = getDAO(ForumTurmaDao.class).findByTurma(turma,tBean.isPermissaoDocente());
		}
		return forward(ConstantesNavegacaoForum.JSP_FORUM_TURMA_LISTA);
	}

	/** 
	 * Prepara para o cadastro de um novo fórum de turma.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/ForumTurma/lista.jsp</li>
     * </ul>
     * 
     */
	public String preCadastrar() throws ArqException {
		try {
			forumBean.setObj(ForumGeral.getNewInstanceForumDefault());
			prepareMovimento(SigaaListaComando.CADASTRAR_FORUM_TURMA);
			setConfirmButton("Cadastrar");
			return forward(ConstantesNavegacaoForum.JSP_FORUM_TURMA_FORM);
		}catch (Exception e) {
			tratamentoErroPadrao(e);			
		}
		return null;
	}


	/**
	 * Inicia o cadastro de um fórum.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/ForumTurma/listar.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String novoForumTurma() throws ArqException {
		obj = new ForumTurma();
		obj.setForum(new ForumGeral());
		MenuTurmaMBean menuTurma = getMBean("menuTurma");
		obj.setTurma(menuTurma.turma());
		return preCadastrar();
	}
	
	/** 
	 * Cadastra ou atualiza um fórum para o tópico de aula da turma selecionado.
     * <br />
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/ava/ForumTurma/form.jsp</li>
     * </ul>
     * 
     */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		
		obj.setForum(forumBean.getObj());				
		ListaMensagens lista = obj.validate();
		if (lista != null && !lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}			
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);

			boolean alterar = false;
			TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
			
			if (obj.getId() == 0) {	
				tvBean.registrarAcao(obj.getNome(), EntidadeRegistroAva.FORUM, AcaoAva.INICIAR_INSERCAO);
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_FORUM_TURMA);
			} else {
				alterar = true;
				tvBean.registrarAcao(obj.getNome(), EntidadeRegistroAva.FORUM, AcaoAva.INICIAR_ALTERACAO, obj.getId());
				
				if( !checkOperacaoAtiva(SigaaListaComando.ALTERAR_FORUM_TURMA.getId()) ){
					return cancelar();
				}
				mov.setCodMovimento(SigaaListaComando.ALTERAR_FORUM_TURMA);
			}

			if ( !alterar )
				tvBean.registrarAcao(obj.getNome(), EntidadeRegistroAva.FORUM, AcaoAva.INSERIR, obj.getId());
			else
				tvBean.registrarAcao(obj.getNome(), EntidadeRegistroAva.FORUM, AcaoAva.ALTERAR, obj.getId());


			
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			forumBean.setObj(obj.getForum());
			return forumBean.view();

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} catch (Exception ex){
			notifyError(ex);
			addMensagemErroPadrao();
			ex.printStackTrace();			
			return null;
		}
		return forward(ConstantesNavegacaoForum.JSP_FORUM_TURMA_FORM);
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
			prepareMovimento(SigaaListaComando.REMOVER_FORUM_TURMA);
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), ForumTurma.class);
			
			//Evitar erro de lazy
			obj.getForum().getId();
			obj.getForum().getTopicos().iterator();
			
			forumBean.setObj(obj.getForum());
			forumBean.filtrarTopicosForum();
			
			setConfirmButton("Remover fórum");
			return forward(ConstantesNavegacaoForum.JSP_FORUM_TURMA_REMOVER);
			
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
     * 	<li>sigaa.war/ava/ForumTurma/form.jsp</li>
     * </ul>
	 * @throws ArqException 
     * 
     */
	@Override
	public String remover() throws ArqException {
		checkChangeRole();
		if (ValidatorUtil.isEmpty(obj)) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
		}
		
		TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
		tvBean.registrarAcao(obj.getNome(), EntidadeRegistroAva.FORUM, AcaoAva.INICIAR_REMOCAO, obj.getId());

		try {

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.REMOVER_FORUM_TURMA);

			obj = getGenericDAO().findByPrimaryKey(obj.getId(), ForumTurma.class);
			//Evitar erro de lazy
			obj.getForum().getId();
			obj.getForum().getTopicos().iterator();

			mov.setObjMovimentado(obj);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);

			tvBean.registrarAcao(obj.getNome(), EntidadeRegistroAva.FORUM, AcaoAva.REMOVER, obj.getId());
			
			return listar();

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return forward(ConstantesNavegacaoForum.JSP_FORUM_TURMA_FORM);
	}

	
	public Turma getTurma() {
		return turma;
	}


	public void setTurma(Turma turma) {
		this.turma = turma;
	}


	public List<ForumTurma> getForunsTurma() {
		return forunsTurma;
	}


	public void setForunsTurma(List<ForumTurma> forunsTurma) {
		this.forunsTurma = forunsTurma;
	}
	

}
