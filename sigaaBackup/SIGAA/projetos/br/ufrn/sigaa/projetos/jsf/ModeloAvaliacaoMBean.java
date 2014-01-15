/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/10/2010
 *
 */
package br.ufrn.sigaa.projetos.jsf;



import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.ModeloAvaliacaoDao;
import br.ufrn.sigaa.arq.dao.projetos.QuestionarioAvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.ModeloAvaliacao;
import br.ufrn.sigaa.projetos.dominio.QuestionarioAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliacao;

/**
 * Mbean respons�vel pelo gerenciamento de modelos de avalia��es.
 * @author geyson
 * @author  ilueny
 *
 */
@Component("modeloAvaliacao")
@Scope("request")
public class ModeloAvaliacaoMBean extends SigaaAbstractController<ModeloAvaliacao> {

	/** Cole��o de modelos de avalia��es */
    public Collection<ModeloAvaliacao> modelos;
    /** Atributo respons�vel por informar se deve-se ou n�o voltar para p�gina anterior */
    public boolean volta;
    
    
    /** Construtor */
    public ModeloAvaliacaoMBean() {
    	obj = new ModeloAvaliacao();
    	modelos = new ArrayList<ModeloAvaliacao>();
    	setLabelCombo("descricao");
    	setIdCombo("id");
    }
    
    /**
	 * Redireciona para tela de cadastro de modelo
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li><li>sigaa.war/projetos/Avaliacoes/ModeloAvaliacao/lista.jsp</li></li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
    public String iniciarCadastroModeloAvaliacao() throws ArqException{
    	checkListRole();
    	prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		obj = new ModeloAvaliacao();
		setConfirmButton("Cadastrar");
		if(getCurrentURL().contains("/sigaa/portais/docente/docente"))
			volta = false;
    	else
    		volta = true;
		return forward("/projetos/Avaliacoes/ModeloAvaliacao/form.jsp");
    }
    
    /**
     * M�todo utilizado para iniciar a busca dos modelos de avalia��o
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/projetos/menu.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     * @throws SegurancaException
     */
    public String iniciarBuscaModelos() throws DAOException, SegurancaException{
    	checkListRole();
    	setModelos(getDAO(ModeloAvaliacaoDao.class).findByTipoEdital(getTipoEdital()));
		return forward("/projetos/Avaliacoes/ModeloAvaliacao/lista.jsp");
    }
    
    /**
     * Combo para ModeloAvaliacao de avalia��es
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/projetos/Avaliacoes/Distribuicao/form.jsp</li>
     * </ul>
     * @throws DAOException 
     */
    public Collection<SelectItem> getAllCombo() throws DAOException {
    	return toSelectItems(getDAO(ModeloAvaliacaoDao.class).findByTipoEdital(getTipoEdital()), "id", "descricao");
    }
    
    /**
	 * Combo para TipoAvaliacao de avalia��es
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/projetos/Avaliacoes/ModeloAvaliacao/form.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllComboTipo() {
		return getAll(TipoAvaliacao.class, "id", "descricao");
	}
	
	/**
	 * Combo para Question�rio de avalia��es
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/projetos/Avaliacoes/ModeloAvaliacao/form.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllComboQuestionario() {
		return getAllAtivo(QuestionarioAvaliacao.class, "id", "descricao");
	}
	
	/**
	 * Cadastar modelo de avalia��o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/projetos/Avaliacoes/ModeloAvaliacao/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
	NegocioException {
		if(obj != null){
			//valida campos
			ValidatorUtil.validateRequired(obj.getDescricao(), "T�tulo", erros);
			ValidatorUtil.validateRequired(obj.getTipoAvaliacao(), "Tipo", erros);
			ValidatorUtil.validateRequired(obj.getQuestionario(), "Question�rio", erros);
			
			if(getGenericDAO().findAtivosByExactField(ModeloAvaliacao.class, "descricao", this.obj.getDescricao()).size() > 0)
				if(!getConfirmButton().equalsIgnoreCase("Alterar") )
					addMensagemErro("J� existe um modelo de avalia��o com este t�tulo.");
			
			ModeloAvaliacaoDao daoModelo = getDAO(ModeloAvaliacaoDao.class);
			if(!hasErrors()){
				daoModelo = getDAO(ModeloAvaliacaoDao.class);
				if(obj.getId() > 0 &&  getConfirmButton().equalsIgnoreCase("Alterar")){
					if(daoModelo.count(" from projetos.modelo_avaliacao where id_edital = "+obj.getEdital().getId()+" and" +
							" id_tipo_avaliacao = "+obj.getTipoAvaliacao().getId()+" and id_modelo_avaliacao <> "+ obj.getId() +"") > 0)
						erros.addErro(" J� existe um modelo de avalia��o para o edital e tipo de avalia��o selecionados.");
				}

			}
			if(obj.getId() == 0){
				if(daoModelo.count(" from projetos.modelo_avaliacao where id_edital = "+obj.getEdital().getId()+" and" +
						" id_tipo_avaliacao = "+obj.getTipoAvaliacao().getId()+" and ativo = "+true+" ") > 0)
					erros.addErro(" J� existe um modelo de avalia��o para o edital e tipo de avalia��o selecionados.");
			}
		}
		if (hasErrors()) {
			addMensagens(erros);
			return null;
		}
		else{
			if ( obj.getEdital().getId() > 0 )
				getGenericDAO().initialize(obj.getEdital());
			else
				obj.setEdital(null);
			obj.setTipo( getTipoEdital() );
			getGenericDAO().initialize(obj.getTipoAvaliacao());
			getGenericDAO().findAndFetch(obj.getQuestionario().getId(), QuestionarioAvaliacao.class, "itensAvaliacao");
			
			if(getConfirmButton().equalsIgnoreCase("Alterar")){
				prepareMovimento(ArqListaComando.ALTERAR);
				setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
				this.obj.setAtivo(true);
			}
			PersistDB obj = this.obj;
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			if (obj.getId() == 0) {
				
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				try {
					execute(mov);
					addMensagem(OPERACAO_SUCESSO);
				} catch (Exception e) {
					notifyError(e);
					addMensagemErroPadrao();
					e.printStackTrace();
					return null;
				}
					
					return iniciarBuscaModelos();

			} else {
				if( !checkOperacaoAtiva(ArqListaComando.ALTERAR.getId()) )
					return cancelar();
				
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				try {
					execute(mov);
					addMensagem(OPERACAO_SUCESSO);
				} catch (Exception e) {
					notifyError(e);
					addMensagemErroPadrao();
					e.printStackTrace();
					return null;
				}

				removeOperacaoAtiva();

				return iniciarBuscaModelos();
		}
		}
	}
	
	/**
	 * M�todo utilizado para carregar o MBean com o obj selecionado e redirecionar para a p�gina de formul�rio
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/projetos/Avaliacoes/ModeloAvaliacao/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String atualizar() throws ArqException {
		ModeloAvaliacaoDao dao = getDAO(ModeloAvaliacaoDao.class);
		setId();
		PersistDB obj = this.obj;
		this.obj = dao.findByPrimaryKey(obj.getId(), ModeloAvaliacao.class);
		
		// Verifica se foi utilizado o bot�o voltar do navegador ap�s o modelo ter sido removido
		if(!this.obj.isAtivo()){
			addMensagemErro("Selecione um modelo v�lido."); 
			return null;
		}
		
		if(dao.findDistribuicaoModeloExistente(this.obj) != null){
			addMensagemErro("O Modelo de Avalia��o n�o pode ser (alterado), pois j� existem avaliadores utilizando este modelo.");
			removeOperacaoAtiva();
			return iniciarBuscaModelos();
		}
		setConfirmButton("Alterar");
		return forward("/projetos/Avaliacoes/ModeloAvaliacao/form.jsf");
	}
	
	/**
	 * Inativa modelo selecionado
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/ModeloAvaliacao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		prepareMovimento(ArqListaComando.DESATIVAR);
		
		ModeloAvaliacaoDao dao = getDAO(ModeloAvaliacaoDao.class);
		this.obj.setId(getParameterInt("id"));
		setReadOnly(false);

		this.obj =  dao.findByPrimaryKey(this.obj.getId(), ModeloAvaliacao.class);
		
		// Verifica se foi utilizado o bot�o voltar do navegador ap�s o modelo ter sido removido
		if(!this.obj.isAtivo()){
			addMensagemErro("Selecione um modelo v�lido."); 
			return null;
		}
		
		if(dao.findDistribuicaoModeloExistente(this.obj) != null){
			addMensagemErro("O Modelo de Avalia��o n�o pode ser alterada, pois j� exsitem avaliadores utilizando este modelo.");
			removeOperacaoAtiva();
			return iniciarBuscaModelos();
		}
		
		PersistDB obj = this.obj;
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
		setId();

		if (obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			try {
				execute(mov);
				Edital ed = this.obj.getEdital();
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return forward(getFormPage());
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return forward(getFormPage());
			}

		
			removeOperacaoAtiva();
			
			return iniciarBuscaModelos();

		}

	}
	
	/**
	 * M�todo utilizado para cancelar a opera��o atual, redirecionando para a p�gina de busca de modelos ou para o menu do subsistema atual.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/projetos/Avaliacoes/ModeloAvaliacao/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		if(volta){
			try {
				volta = false;
				return iniciarBuscaModelos();
			} catch (DAOException e) {
				e.printStackTrace();
			} catch (SegurancaException e) {
				e.printStackTrace();
			}
		}
		else
			return super.cancelar();
		
		return null;
	}
	
	/**
	 * M�todo utilizado para redirecionar para tela de visualiza��o de modelo de question�rio
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/projetos/Avaliacoes/ModeloAvaliacao/lista.jsp</li>
	 * </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String viewModelo() throws DAOException{
		ModeloAvaliacaoDao dao = getDAO(ModeloAvaliacaoDao.class);
		setId();
		PersistDB obj = this.obj;
		this.obj = dao.findByPrimaryKey(obj.getId(), ModeloAvaliacao.class);
		return forward("/projetos/Avaliacoes/ModeloAvaliacao/view.jsf");
	}
	
	/**
	 * M�todo utilizado para carregar o question�rio na tela de cadastro ou altera��o de modelo de question�rio
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li></li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void carregaQuestionario(ValueChangeEvent e) throws DAOException {
		Integer id =  (Integer) e.getNewValue();
		if(id > 0){
			QuestionarioAvaliacaoDao dao = getDAO(QuestionarioAvaliacaoDao.class);
			QuestionarioAvaliacao q = dao.findByPrimaryKey(id, QuestionarioAvaliacao.class);
				this.obj.setQuestionario(q);
		}
	}
	
	/**
	 * M�todo utilizado para redirecionar o usu�rio para a lista de modelos de question�rios
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li></li>
	 * </ul>
	 * 
	 */
	@Override
	public String getListPage() {
		return forward("/projetos/Avaliacoes/ModeloAvaliacao/lista.jsf");
	}
    
	public Collection<ModeloAvaliacao> getModelos() {
		return modelos;
	}

	public void setModelos(Collection<ModeloAvaliacao> modelos) {
		this.modelos = modelos;
	}

	public boolean isVolta() {
		return volta;
	}

	public void setVolta(boolean volta) {
		this.volta = volta;
	}
    
    
    
}
