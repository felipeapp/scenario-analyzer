/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
	 * Contem as mensagem que serão renderizados depois de alguma ação ajax
	 */
	private Collection<MensagemAviso> avisosAjax;
	
	private DataModel modelRecursos;
	
	private DataModel modelResultadoBusca;
	
	private RecursoEspacoFisico recurso;
	
	/**
	 * Contem os recursos devem ser enviados ao processador para remoção
	 */
	private List<RecursoEspacoFisico> recursoRemover;
	
	/**
	 * Construtor Padrão
	 * 
	 * @throws DAOException
	 */
	public EspacoFisicoMBean() throws DAOException {
		initEspacoFisico();
		initRecursoEspaco();
	}
	
	/**
	 * Redireciona para a página onde informa os dados gerais do espaço físico
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
	 * Redireciona para a página onde informa os recursos que o espaço físico possui
	 * JSP: Não invocado por JSP
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
	 * Inicia o processo de cadastro do espaço físico 
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
	 * Inicia o processo de cadastro do espaço físico sem limpar o obj
	 * para aproveitar alguns campos já digitados
	 * JSP: Não invocado por jsp
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
	 * Inicia o processo de alterar um espaço físico
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
	 * Inicia uma busca por espaço físico
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
	 * Vai para a segunda etapa do cadastro, gerenciar os recursos do espaço físico
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
			ValidatorUtil.validateRequired(obj.getCodigo(), "Código", lista);
		}
		

		ValidatorUtil.validateRequiredId(obj.getUnidadeResponsavel().getId(), "Localização do espaço", lista );
		ValidatorUtil.validateRequiredId(obj.getTipo().getId(), "Tipo", lista );
	}
	
	/**
	 * Adiciona um novo recurso no espaço físico
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
			avisosAjax.add(new MensagemAviso("Recurso já foi inserido.", TipoMensagemUFRN.WARNING));
			return ;
		}
		
		obj.adicionarRecurso(recurso);
		
		recurso = new RecursoEspacoFisico();
		recurso.setTipo(new TipoRecursoEspacoFisico());
				
	}

	/**
	 * Remove um recurso do espaço físico
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
		
		// caso não existe nenhuma unidade com prioridade, seta null
		if (isEmpty(obj.getUnidadePreferenciaReserva()))
			obj.setUnidadePreferenciaReserva(null);
		
		if (getUltimoComando().equals(SigaaListaComando.ADICIONAR_ESPACO_FISICO))
			cadastrarEspacoFisico();
		else
			atualizarEspacoFisico();
	}
	
	/**
	 * Insere um novo espaço físico no banco
	 * JSP: Não invocado por jsp
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
			addMensagemInformation("Cadastro do espaço físico realizada com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e, e.getMessage());
		}
		
		return cancelar();
	}
	
	/**
	 * Altera o espaço físico no banco
	 * JSP: Não invocado por jsp
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
			addMensagemInformation("Atualização do espaço fisico realizada com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		return cancelar();
	}		
	
	/**
	 * Remove o espaço físico (ativo recebe false)
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
			addMensagemInformation("Remoção do espaço fisico realizada com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}		
	}
	
	/**
	 * Monta combo com os tipo de espaço físicos
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
	 * Monta combo com os tipos de recursos que um espaço físico pode ter
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
	 * Não implementado
	 */
	public String selecionaEspacoFisico() throws ArqException {
		return null;
	}

	public void setEspacoFisico(EspacoFisico espacoFisico) throws ArqException {
		obj = espacoFisico;
	}

}
