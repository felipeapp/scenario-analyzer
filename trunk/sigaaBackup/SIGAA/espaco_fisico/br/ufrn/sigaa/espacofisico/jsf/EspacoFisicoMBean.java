/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.espacofisico.EspacoFisicoDao;
import br.ufrn.sigaa.arq.dao.espacofisico.TipoEspacoDAO;
import br.ufrn.sigaa.arq.dao.espacofisico.TipoRecursoDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.RecursoEspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.TipoEspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.TipoRecursoEspacoFisico;
import br.ufrn.sigaa.espacofisico.negocio.EspacoFisicoValidator;
import br.ufrn.sigaa.espacofisico.negocio.MovimentoEspacoFisico;

@Component("espacoFisicoBean") @Scope("request")
public class EspacoFisicoMBean extends SigaaAbstractController<EspacoFisico> implements RealizarBusca {
	
	/**
	 * Contem as mensagem que ser�o renderizados depois de alguma a��o ajax
	 */
	private Collection<MensagemAviso> avisosAjax;
	
	private DataModel modelRecursos;
	
	private DataModel modelResultadoBusca;
	
	private RecursoEspacoFisico recurso;
	
	/**
	 * Contem os recursos devem ser enviados ao processador para remo��o
	 */
	private List<RecursoEspacoFisico> recursoRemover;
	
	/**
	 * Construtor Padr�o
	 * 
	 * @throws DAOException
	 */
	public EspacoFisicoMBean() throws DAOException {
		initEspacoFisico();
		initRecursoEspaco();
	}
	
	/**
	 * Redireciona para a p�gina onde informa os dados gerais do espa�o f�sico
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/recursos/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaDadosDoEspacoFisico() {
		return forward("/infra_fisica/espaco_fisico/form.jsp");
	}
	
	/**
	 * Redireciona para a p�gina onde informa os recursos que o espa�o f�sico possui
	 * JSP: N�o invocado por JSP
	 * 
	 * @return
	 */
	public String telaRecursosDoEspacoFisico() {
		return forward("/infra_fisica/recursos/form.jsp");
	}
	
	/**
	 * Inicializa objetos usados na primeira etapa do cadastro
	 */
	private void initEspacoFisico() {
		
		obj = new EspacoFisico();
		obj.setUnidadeResponsavel( new Unidade() );
		obj.setUnidadePreferenciaReserva( new Unidade() );
		obj.setTipo( new TipoEspacoFisico() );
		obj.setRecursos( new ArrayList<RecursoEspacoFisico>() );
	}
	
	/**
	 * Inicializa objetos usados na segunda etapa do cadastro
	 */
	private void initRecursoEspaco() {
		recursoRemover = new ArrayList<RecursoEspacoFisico>();
		modelRecursos = new ListDataModel( obj.getRecursos() );
		
		recurso = new RecursoEspacoFisico();
		recurso.setTipo( new TipoRecursoEspacoFisico() );
	}
	
	/**
	 * Inicia o processo de cadastro do espa�o f�sico 
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroEspacoFisico() throws ArqException {
		
		initEspacoFisico();
		initRecursoEspaco();
		
		prepareMovimento(SigaaListaComando.ADICIONAR_ESPACO_FISICO);
		return telaDadosDoEspacoFisico();
	}
	
	/**
	 * Inicia o processo de cadastro do espa�o f�sico sem limpar o obj
	 * para aproveitar alguns campos j� digitados
	 * JSP: N�o invocado por jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroOutroEspacoFisico() throws ArqException {
		TipoEspacoFisico tipo = obj.getTipo();
		Unidade unidadeResponsavel = obj.getUnidadeResponsavel();
		
		initEspacoFisico();
		initRecursoEspaco();
		
		obj.setTipo(tipo);
		obj.setUnidadeResponsavel(unidadeResponsavel);
		
		prepareMovimento(SigaaListaComando.ADICIONAR_ESPACO_FISICO);
		return telaDadosDoEspacoFisico();
	}	
	

	/**
	 * Inicia o processo de alterar um espa�o f�sico
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/busca_geral/operacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlterarEspacoFisico() throws ArqException {
		
		getBuscaEspacoFisicoMBean().injetarEspacoFisico();
		
		prepareMovimento(SigaaListaComando.ALTERAR_ESPACO_FISICO);

		EspacoFisicoDao dao = getDAO(EspacoFisicoDao.class);
		
		obj = dao.findByPrimaryKey(obj.getId(), EspacoFisico.class);
			
		if (obj.getUnidadePreferenciaReserva() == null) 
			obj.setUnidadePreferenciaReserva(new Unidade());
			
		modelRecursos = new ListDataModel( obj.getRecursos() );
		
		return forward("/infra_fisica/espaco_fisico/form.jsp");
	}

	/**
	 * Pega o mbean que realizou a busca
	 * 
	 * @see BuscaEspacoFisicoMBean
	 * @return
	 */
	private BuscaEspacoFisicoMBean getBuscaEspacoFisicoMBean() {
		return getMBean("buscaEspacoFisico");
	}
	
	/**
	 * Inicia uma busca por espa�o f�sico
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaEspacoFisico() throws ArqException {
		
		prepareMovimento(SigaaListaComando.ALTERAR_ESPACO_FISICO);
		BuscaEspacoFisicoMBean mBean = getMBean("buscaEspacoFisico");
		mBean.setRequisitor(DadosRequisitor.GERENCIAR_ESPACO_FISICO);
		return mBean.iniciarBusca();		
	}
	
	/**
	 * Vai para a segunda etapa do cadastro, gerenciar os recursos do espa�o f�sico
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/espaco_fisico/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String gerenciarRecursos() throws DAOException {
		
		Collection<MensagemAviso> lista = new ArrayList<MensagemAviso>();
		validarEspacoFisico(lista);
		
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		
		return telaRecursosDoEspacoFisico();
	}
	
	/**
	 * Valida a primeira etapa do cadastro
	 * 
	 * @param lista
	 * @throws DAOException 
	 */
	private void validarEspacoFisico(Collection<MensagemAviso> lista) throws DAOException {
		
		if (getUltimoComando().equals(SigaaListaComando.ADICIONAR_ESPACO_FISICO)) {
			try {
				EspacoFisicoValidator.isCodigoDisponivel(obj.getCodigo());
			} catch (NegocioException e) {
				ValidatorUtil.addMensagemErro(e.getMessage(), lista);
			}
		} else {
			ValidatorUtil.validateRequired(obj.getCodigo(), "C�digo", lista);
		}
		

		ValidatorUtil.validateRequiredId(obj.getUnidadeResponsavel().getId(), "Localiza��o do espa�o", lista );
		ValidatorUtil.validateRequiredId(obj.getTipo().getId(), "Tipo", lista );
	}
	
	/**
	 * Adiciona um novo recurso no espa�o f�sico
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/recursos/form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @throws DAOException
	 */
	public void adicionarRecurso(ActionEvent event) throws DAOException {
		
		avisosAjax = new ArrayList<MensagemAviso>();	
		
		ValidatorUtil.validateRequiredId(recurso.getTipo().getId(), "Tipo do Recurso", avisosAjax);
		ValidatorUtil.validaInt(recurso.getQuantidade(), "Quantidade", avisosAjax);
		
		if (!avisosAjax.isEmpty()) {
			return ;
		}
		
		TipoRecursoDAO dao = getDAO(TipoRecursoDAO.class);
		TipoRecursoEspacoFisico tipoRecurso = dao.findByPrimaryKey(recurso.getTipo().getId(), TipoRecursoEspacoFisico.class);
		recurso.setTipo(tipoRecurso);
		recurso.setEspacoFisico(obj);
		
		if (obj.getRecursos().contains(recurso)) {
			avisosAjax.add(new MensagemAviso("Recurso j� foi inserido.", TipoMensagemUFRN.WARNING));
			return ;
		}
		
		obj.adicionarRecurso(recurso);
		
		recurso = new RecursoEspacoFisico();
		recurso.setTipo(new TipoRecursoEspacoFisico());
				
	}

	/**
	 * Remove um recurso do espa�o f�sico
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/recursos/form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 */
	public void removerRecurso(ActionEvent event) {
		int indice = modelRecursos.getRowIndex();
		RecursoEspacoFisico recursoRemovido = obj.getRecursos().remove(indice);
		if( recursoRemovido.getId() > 0 ){
			if( recursoRemover == null )
				recursoRemover = new ArrayList<RecursoEspacoFisico>();
			recursoRemover.add( recursoRemovido );
		}
	}
	
	/**
	 * Atualiza o recurso
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/recursos/form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 */
	public void atualizarRecurso(ActionEvent event) {
		RecursoEspacoFisico r = (RecursoEspacoFisico) modelRecursos.getRowData();
		
		avisosAjax = new ArrayList<MensagemAviso>();
		ValidatorUtil.validaInt(r.getQuantidade(), "Quantidade", avisosAjax);

		if (!avisosAjax.isEmpty()) {
			return ;
		}
		
		modelRecursos = new ListDataModel( obj.getRecursos() );
	}	

	/**
	 * Insere ou atualiza no banco
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/recursos/form.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public void persistirEspacoFisico() throws ArqException {
		
		// caso n�o existe nenhuma unidade com prioridade, seta null
		if (isEmpty(obj.getUnidadePreferenciaReserva()))
			obj.setUnidadePreferenciaReserva(null);
		
		if (getUltimoComando().equals(SigaaListaComando.ADICIONAR_ESPACO_FISICO))
			cadastrarEspacoFisico();
		else
			atualizarEspacoFisico();
	}
	
	/**
	 * Insere um novo espa�o f�sico no banco
	 * JSP: N�o invocado por jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarEspacoFisico() throws ArqException {
		
		MovimentoEspacoFisico mov = new MovimentoEspacoFisico();
		
		mov.setEspacoFisico(obj);
		mov.setCodMovimento(SigaaListaComando.ADICIONAR_ESPACO_FISICO);
		
		try {
			executeWithoutClosingSession(mov , getCurrentRequest());
			addMensagemInformation("Cadastro do espa�o f�sico realizada com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e, e.getMessage());
		}
		
		return cancelar();
	}
	
	/**
	 * Altera o espa�o f�sico no banco
	 * JSP: N�o invocado por jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String atualizarEspacoFisico() throws ArqException {
		MovimentoEspacoFisico mov = new MovimentoEspacoFisico();
		
		mov.setEspacoFisico(obj);
		mov.setRecursosRemovidos(recursoRemover);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_ESPACO_FISICO);
		
		try {
			executeWithoutClosingSession(mov , getCurrentRequest());
			addMensagemInformation("Atualiza��o do espa�o fisico realizada com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		return cancelar();
	}		
	
	/**
	 * Remove o espa�o f�sico (ativo recebe false)
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/busca_geral/operacao.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @throws ArqException
	 */
	public void removerEspacoFisico(ActionEvent event) throws ArqException {
		
		prepareMovimento(SigaaListaComando.REMOVER_ESPACO_FISICO);
		
		getBuscaEspacoFisicoMBean().injetarEspacoFisico();
		
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), EspacoFisico.class);
		
		MovimentoEspacoFisico mov = new MovimentoEspacoFisico();
		mov.setCodMovimento(SigaaListaComando.REMOVER_ESPACO_FISICO);
		mov.setEspacoFisico(obj);
		try {
			execute(mov , getCurrentRequest());
			BuscaEspacoFisicoMBean buscaMbean = getMBean("buscaEspacoFisico");
			buscaMbean.removerAndAtualizarBusca(obj);			
			addMensagemInformation("Remo��o do espa�o fisico realizada com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}		
	}
	
	/**
	 * Monta combo com os tipo de espa�o f�sicos
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/espaco_fisico/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAllTipoEspacoCombo() throws DAOException {
		
		TipoEspacoDAO dao = getDAO(TipoEspacoDAO.class);
		Collection<TipoEspacoFisico> col = null;
		
		col = dao.findAll(TipoEspacoFisico.class);
		
		return toSelectItems(col, "id", "denominacao");
	}
	
	/**
	 * Monta combo com os tipos de recursos que um espa�o f�sico pode ter
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/busca_geral/busca.jsp</li>
	 * 	<li>sigaa.war/infra_fisica/recursos/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getTipoRecursosCombo() throws DAOException {
		TipoRecursoDAO dao = getDAO(TipoRecursoDAO.class);
		Collection<TipoRecursoEspacoFisico> col = null;
		col = dao.findAll(TipoRecursoEspacoFisico.class);
		return toSelectItems(col, "id", "denominacao");
	}

	public RecursoEspacoFisico getRecurso() {
		return recurso;
	}

	public void setRecurso(RecursoEspacoFisico recurso) {
		this.recurso = recurso;
	}
	
	public DataModel getModelRecursos() {
		return modelRecursos;
	}

	public void setModelRecursos(DataModel modelRecursos) {
		this.modelRecursos = modelRecursos;
	}

	public List<RecursoEspacoFisico> getRecursoRemover() {
		return recursoRemover;
	}

	public void setRecursoRemover(List<RecursoEspacoFisico> recursoRemover) {
		this.recursoRemover = recursoRemover;
	}
	
	public Collection<MensagemAviso> getAvisosAjax() {
		return avisosAjax;
	}

	public void setAvisosAjax(Collection<MensagemAviso> avisosAjax) {
		this.avisosAjax = avisosAjax;
	}

	public DataModel getModelResultadoBusca() {
		return modelResultadoBusca;
	}

	public void setModelResultadoBusca(DataModel modelResultadoBusca) {
		this.modelResultadoBusca = modelResultadoBusca;
	}

	/**
	 * N�o implementado
	 */
	public String selecionaEspacoFisico() throws ArqException {
		return null;
	}

	public void setEspacoFisico(EspacoFisico espacoFisico) throws ArqException {
		obj = espacoFisico;
	}

}
