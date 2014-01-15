/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/12/2008
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

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.espacofisico.EspacoFisicoDao;
import br.ufrn.sigaa.arq.dao.espacofisico.ParametrosBusca;
import br.ufrn.sigaa.arq.dao.espacofisico.RestricoesBusca;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;

/**
 * MBean para realizar busca por espaço físicos.
 * 
 * @see RealizarBusca
 * 
 * @author Henrique André
 * 
 */
@Component("buscaEspacoFisico") @Scope("request")
public class BuscaEspacoFisicoMBean extends SigaaAbstractController<EspacoFisico> {

	/**
	 * Contém as mensagens que vão ser renderizados depois de alguma ação ajax
	 */
	private Collection<MensagemAviso> avisosAjax;
	
	/**
	 * Informações sobre o mbean que esta requisitando informação
	 */
	private DadosRequisitor requisitor;
	
	/**
	 * Mapeia os checkbox's do formulário de busca
	 */
	private RestricoesBusca restricoes = new RestricoesBusca();
	
	/**
	 * Recebe os valores que vem do formulário de busca
	 */
	private ParametrosBusca parametros = new ParametrosBusca();
	
	/**
	 * Resultado da busca, seguindo os critérios de busca
	 */
	public List<EspacoFisico> resultadoBusca;
	
	/**
	 * Model pra usar no datatable de resultado da busca
	 */
	private DataModel modelResultadoBusca;
	
	/**
	 * Inicio do processo de busca. Redireciona pra pagina de busca
	 * JSP: Não invocado por jsp
	 * 
	 * @return
	 */
	public String iniciarBusca() {
		
		resultadoBusca = null;
		modelResultadoBusca = null;
		parametros = new ParametrosBusca();
		restricoes = new RestricoesBusca();
		
		return forward("/infra_fisica/busca_geral/busca.jsp");
	}
	
	/**
	 * Realiza a busca de acordo com os critérios de busca
	 * JSP:
	 * /sigaa.war/infra_fisica/busca_geral/busca.jsp
	 * 
	 * @param event
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void buscar(ActionEvent event) throws HibernateException, DAOException {

		validarParametrosBusca();
		if (!avisosAjax.isEmpty())
			return ;
		
		EspacoFisicoDao dao = getDAO(EspacoFisicoDao.class);
		
		resultadoBusca = dao.buscaRefinada(restricoes, parametros);	
		modelResultadoBusca = new ListDataModel(resultadoBusca);
		
		if (modelResultadoBusca.getRowCount() == 0)
			addMensagemWarning("Nenhum registro encontrado. Por favor refine a busca.");
	}	

	public void removerAndAtualizarBusca(EspacoFisico espaco) {
		resultadoBusca.remove(espaco);
		modelResultadoBusca = new ListDataModel(resultadoBusca);
	}
	
	
	/**
	 * URL da página que tem as operações. Essa página será incluída dinamicamente.
	 * JSP:
	 * /sigaa.war/infra_fisica/busca_geral/busca.jsp
	 * @return
	 */
	public String getUrl() {
		return requisitor.getUrl();
	}
	
	/**
	 * Valida o form da busca 
	 */
	private void validarParametrosBusca() {
		
		avisosAjax = new ArrayList<MensagemAviso>();
		
		if (restricoes.isNaoSelecionou())
			ValidatorUtil.addMensagemErro("Selecione algum criterio de busca", avisosAjax);
		
		if(restricoes.isBuscaCodigo())
			ValidatorUtil.validateRequired(parametros.getCodigo(), "Código", avisosAjax);
		
		if (restricoes.isBuscaCapacidade()) {
			if (isEmpty(parametros.getCapacidadeInicio()) && isEmpty(parametros.getCapacidadeFim()))
				ValidatorUtil.addMensagemErro("Capacidade: É necessário informar um valor mínimo ou máximo", avisosAjax);
		}
		
		if (restricoes.isBuscaLocalizacao())
			ValidatorUtil.validateRequired(parametros.getLocalizacao(), "Localização", avisosAjax);
		
		if (restricoes.isBuscaDescricao())
			ValidatorUtil.validateRequired(parametros.getDescricao(), "Descrição", avisosAjax);
		
		if (restricoes.isBuscaArea()) {
			if (isEmpty(parametros.getAreaInicio()) && isEmpty(parametros.getAreaFim()))
				ValidatorUtil.addMensagemErro("Área: É necessário informar um valor mínimo ou máximo", avisosAjax);
		}
	}	
		
	/**
	 * Esse método so é usado quando não esta usando a pagina de operações default.
	 * Ele devolve o controle para o mBean que requisitou a busca.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>/infra_fisica/busca_geral/default.jsp</li>
	 * </ul>
	 * 
	 * @see DadosRequisitor
	 * @return
	 * @throws ArqException
	 */
	public String escolherEspacoFisico() throws ArqException {
		RealizarBusca managerBean = injetarEspacoFisico();
		
		return managerBean.selecionaEspacoFisico();
	}

	/**
	 * Redireciona para uma página com o resumo das informações do espaço físico 
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/busca_geral/operacao.jsp</li>
	 * 	<li>sigaa.war/infra_fisica/busca_geral/default.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
		obj = (EspacoFisico) modelResultadoBusca.getRowData();
		
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), EspacoFisico.class);
		
		return forward("/infra_fisica/busca_geral/view.jsp");
	}		
	
	/**
	 * Injeta no mbean que requisitou a busca, o objeto (EspacoFisico) selecionado
	 * JSP: Não invocado por jsp
	 * @return
	 * @throws ArqException
	 */
	public RealizarBusca injetarEspacoFisico() throws ArqException {
		EspacoFisico espacoFisico = (EspacoFisico) modelResultadoBusca.getRowData();
		
		RealizarBusca managerBean = getMBean(requisitor.getMBean());
		managerBean.setEspacoFisico(espacoFisico);
		return managerBean;
	}

	public RestricoesBusca getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(RestricoesBusca restricoes) {
		this.restricoes = restricoes;
	}

	public ParametrosBusca getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosBusca parametros) {
		this.parametros = parametros;
	}

	public List<EspacoFisico> getResultadoBusca() {
		return resultadoBusca;
	}

	public void setResultadoBusca(List<EspacoFisico> resultadoBusca) {
		this.resultadoBusca = resultadoBusca;
	}

	public DataModel getModelResultadoBusca() {
		return modelResultadoBusca;
	}

	public void setModelResultadoBusca(DataModel modelResultadoBusca) {
		this.modelResultadoBusca = modelResultadoBusca;
	}

	public Collection<MensagemAviso> getAvisosAjax() {
		return avisosAjax;
	}

	public void setAvisosAjax(Collection<MensagemAviso> avisosAjax) {
		this.avisosAjax = avisosAjax;
	}

	public DadosRequisitor getRequisitor() {
		return requisitor;
	}

	public void setRequisitor(Integer requisitor) {
		this.requisitor = DadosRequisitor.info(requisitor);
	}
	
}
