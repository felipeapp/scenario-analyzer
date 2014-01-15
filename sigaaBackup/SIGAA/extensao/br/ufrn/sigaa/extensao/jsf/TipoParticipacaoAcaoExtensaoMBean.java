/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/08/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.extensao.TipoParticipacaoAcaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParticipacaoAcaoExtensao;

/**
 * MBean de tipo de participa��o
 * @autor geyson karlos 
 */
@Component("tipoParticipacaoAcaoExtensao")
@Scope("request")
public class TipoParticipacaoAcaoExtensaoMBean extends
		SigaaAbstractController<TipoParticipacaoAcaoExtensao> {

	/**
	 * Construtor Padr�o.
	 */
	public TipoParticipacaoAcaoExtensaoMBean() {
		initObj();
	}

	/**
	 * Inicia MBean
	 */
	private void initObj() {
		obj = new TipoParticipacaoAcaoExtensao();
		obj.setTipoAcaoExtensao(new TipoAtividadeExtensao());
	}
	
	/**
	 * Inicia o MBean e prepara o movimento de cadastro.
	 * JSP: /sigaa.war/extensao/TipoParticipacaoAcaoExtensao/lista.jsp
	 * JSP: /sigaa.war/extensao/menu.jsp
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		return super.preCadastrar();
	}
	
	/**
	 *  Atributo para auxiliar no controle de redirecionamento de p�ginas.
	 */
	private boolean listagem;
	
	/**
	 * @see SigaaAbstractController#getFormPage()
	 * JSP: N�o invocado por JSP.
	 */
	@Override
	public String getFormPage() {
		return "/extensao/TipoParticipacaoAcaoExtensao/form.jsf";
	}

	/**
	 * @see SigaaAbstractController#getListPage()
	 * JSP: N�o invocado por JSP.
	 */
	public String getListPage() {
		setListagem(true);
		return "/extensao/TipoParticipacaoAcaoExtensao/lista.jsf";
	}
	
	/**
	 * Atualiza entidade do tipo TipoParticipacaoAcaoExtensao.
	 * JSP: /sigaa.war/extensao/TipoParticipacaoAcaoExtensao/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {
		setId();
		TipoParticipacaoAcaoExtensao temp = getGenericDAO().findByPrimaryKey(obj.getId(), TipoParticipacaoAcaoExtensao.class);
		
		if(temp == null){
			addMensagemErro("Registro j� exclu�do!");
			return null;
		}
		
		if(temp.isTipoParticipacaoAcaoExtensaoFixaNoSistema()){
			addMensagemErro("Esse tipo de participa��o n�o pode ser alterado.");
			return null;
		}
		
		if(!temp.isAtivo()){
			addMensagemErro("Registro j� exclu�do!");
			return null;
		}
			
		return super.atualizar();
	}
	
	/**
	 * Redireciona para a p�gina de listagem ap�s atualiza��o.
	 * JSP: N�o invocado por JSP.
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		setListagem(true);
		forward("/extensao/TipoParticipacaoAcaoExtensao/lista.jsf");
	}

	/**
	 * Sobreescreve comportamento padr�o para n�o realizar nenhuma a��o. 
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
	
	}
	
	/**
	 * Sobreescreve comportamento padr�o para evitar uma remo��o ou altera��o de um registro j� removido. 
	 * JSP: N�o invocado por JSP.
	 */
	@Override
	public void beforeInativar() {
		setId();
		TipoParticipacaoAcaoExtensao temp = null;
		try {	
			temp = getGenericDAO().findByPrimaryKey(obj.getId(), TipoParticipacaoAcaoExtensao.class);
			
			if(!temp.isAtivo()){
				addMensagemErro("Registro j� exclu�do!");
				setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
				prepareMovimento(ArqListaComando.DESATIVAR);
			}
			else{
				setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
				prepareMovimento(ArqListaComando.DESATIVAR);
			}
		
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
		
		
	}
	
	/**
	 * Inativa entidade do tipo TipoParticipacaoAcaoExtensao.
	 * JSP:/sigaa.war/extensao/TipoParticipacaoAcaoExtensao/lista.jsp
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {

		beforeInativar();

		if(hasErrors())
			return null;
		
		PersistDB obj = this.obj;
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
		setId();

		if (obj.getId() == 0 ) {
			addMensagemErro(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			
			if(this.obj.isTipoParticipacaoAcaoExtensaoFixaNoSistema()){
				addMensagemErro("Esse tipo de participa��o n�o pode ser alterado.");
				return null;
			}
			
			
			if( !checkOperacaoAtiva(ArqListaComando.DESATIVAR.getId()) )
				return cancelar();
			
			try {
					execute(mov);
					addMensagem(OPERACAO_SUCESSO);

			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return forward(getFormPage());
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return forward(getFormPage());
			}

			setResultadosBusca(null);
			afterInativar();
			removeOperacaoAtiva();

			String forward = forwardInativar();
			if (forward == null) {
				return forward(getListPage());
			} else {
				return forward(forward);
			}

		}
	}
	
	/**
	 * Persisti um novo registro do tipo TipoParticipacaoAcaoExtensao.
	 * JSP: N�o invocado por JSP. 
	 */
	@Override
	public String cadastrar() throws ArqException {
		erros = obj.validate();
		if(hasErrors())
			return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		
		if (obj.getId() == 0) 
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
		else 
			 mov.setCodMovimento(ArqListaComando.ALTERAR);
		
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
		
		return forward(forwardCadastrar());
	}
	
	/**
	 * Redireciona para o menu do modulo Extens�o ou redireciona para listagem 
	 * de Tipo de Participa��o de Extens�o ap�s cadastrar um novo tipo de participa��o. 
	 * JSP: N�o invocado por JSP.
	 */
	public String forwardCadastrar() {
		if(isListagem())
			return getListPage();
		else
			return "/extensao/menu.jsp";
	}
	
	
	
	/**
	 * Retorna lista de TipoParticipacaoAcaoExtensao ordenado pelo campo "tipo_acao_extensao".
	 * JSP:/sigaa.war/extensao/TipoParticipacaoAcaoExtensao/lista.jsp
	 * @return
	 * @throws DAOException 
	 */
	public Collection<TipoParticipacaoAcaoExtensao> getAllObj() throws DAOException {
		TipoParticipacaoAcaoExtensaoDao dao = getDAO(TipoParticipacaoAcaoExtensaoDao.class);
		return dao.findAllTipoParticipacaoAtivos();
		
	}
	
	/**
	 * Seta Atributo para "true" ap�s inativa��o.
	 */
	@Override
	protected void afterInativar() {
		setListagem(true);
	}


	/**
	 * Retorna todos os tipos de Ativadade para preencher o combobox na p�gina form.jsp. 
	 * JSP:/sigaa.war/extensao/TipoParticipacaoAcaoExtensao/form.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTiposAtividades() throws DAOException{
		return toSelectItems(getGenericDAO().findAllAtivos(TipoAtividadeExtensao.class, "descricao"), "id", "descricao");
	}

	public void setListagem(boolean listagem) {
		this.listagem = listagem;
	}

	public boolean isListagem() {
		return listagem;
	}
	


}
