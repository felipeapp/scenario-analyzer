/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/10/2011
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.CotaBolsasDao;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Controlador respons�vel por registrar as informa��es referentes � vig�ncia
 * das distribui��es de cotas de bolsas gerenciadas pelo m�dulo de pesquisa.
 * 
 * @author Leonardo Campos
 * 
 */
@Component
@Scope("request")
public class CotaBolsasMBean extends SigaaAbstractController<CotaBolsas> {

	/** Cole��o representando o modelo dos itens selecionados na view. */
	private Collection<String> selectedItems = new ArrayList<String>();
	
	/** Mapa de modalidades do �rg�o financiador selecionado na view. 
	 * Armazena o id do tipo de bolsa com sua respectiva descri��o. 
	 */
	private Map<Integer, String> modalidadesOrgaoFinanciador = new HashMap<Integer, String>();

	/** Armazenas as cotas de bolsas cadastradas */
	private Collection<CotaBolsas> cotas = null;
	
	/** Construtor padr�o. */
	public CotaBolsasMBean() {
		clear();
	}

	/**
	 * Inicializa os atributos utilizados na opera��o.
	 */
	private void clear() {
		obj = new CotaBolsas();
		obj.setEntidadeFinanciadora(new EntidadeFinanciadora());
	}
	
	/**
	 * Retorna a p�gina do formul�rio.
	 * N�o invocado por JSP.
	 */
	@Override
	public String getFormPage() {
		return ConstantesNavegacaoPesquisa.COTABOLSAS_FORM;
	}
	
	/**
	 * Retorna a p�gina da listagem.
	 * N�o invocado por JSP.
	 */
	@Override
	public String getListPage() {
		return ConstantesNavegacaoPesquisa.COTABOLSAS_LISTA;
	}

	/**
	 * Retora a cole��o de registros armazenados no banco por ordem decrescente de data fim do per�odo de validade da cota.
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/CotaBolsas/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public Collection<CotaBolsas> getAllPaginado() throws ArqException {
		CotaBolsasDao dao = getDAO(CotaBolsasDao.class);
		if ( cotas == null ) {
			try {
				cotas = new ArrayList<CotaBolsas>();
				cotas = dao.findByExactField(CotaBolsas.class, "ativo", Boolean.TRUE, getPaginacao(), "desc", "fim");
			} finally {
				dao.close();
			}
		}
		return cotas;
	}
	
	/**
	 * Carrega as modalidades de acordo com o �rg�o financiador selecionado.
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/CotaBolsas/form.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws ArqException
	 */
	public void changeOrgaoFinanciador(ValueChangeEvent evt) throws ArqException {
		Integer idOrgao = (Integer) evt.getNewValue();
		if(idOrgao != null){
			selectedItems = new ArrayList<String>();
			modalidadesOrgaoFinanciador = getDAO(TipoBolsaPesquisaDao.class).findByOrgaoFinanciador(idOrgao);
			if(modalidadesOrgaoFinanciador.isEmpty())
				addMensagemWarning("O �rg�o financiador selecionado n�o possui modalidades vinculadas. " +
						"Selecione outro �rg�o financiador ou vincule antes as modalidades ao �rg�o financiador desejado em Cadastros -> Tipos de Bolsa -> Listar/Alterar.");
		}
	}

	/**
	 * Retorna uma lista de itens selecion�veis na view contendo as modalidades
	 * de bolsas do �rg�o financiador.
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/CotaBolsas/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getModalidadesCombo() {
		return toSelectItems(modalidadesOrgaoFinanciador);
	}
	
	/**
	 * Serve para exibir ou n�o o label modalidade na view, � exibido apenas
	 * se o �rg�o Financiador, possuir modalidade associdada.
	 * 
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/CotaBolsas/form.jsp</li>
	 * </ul>
	 */
	public boolean isExibeModalidade(){
		return getModalidadesCombo().size() > 0;
	}
	
	/**
	 * Opera��es realizadas antes de efetuar a valida��o e o cadastro da cota de bolsas.
	 * N�o invocado por JSP.
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		if(!ValidatorUtil.isEmpty(selectedItems))
			obj.setModalidades(UFRNUtils.gerarStringIn(selectedItems).replaceAll("[()']", "").trim());
		else
			obj.setModalidades(null);
	}
	
	@Override
	protected void beforeInativar() {
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	/**
	 * Carrega as informa��es da cota de bolsa selecionada e encaminha para o formul�rio de atualiza��o. 
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/CotaBolsas/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {

		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);

		GenericDAO dao = getGenericDAO();
		setId();
		setReadOnly(false);

		this.obj = dao.findAndFetch(obj.getId(), CotaBolsas.class, "entidadeFinanciadora");
		
		if (obj == null || obj.getId() == 0 || !obj.isAtivo() ) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, TipoMensagemUFRN.ERROR);
			return null;
		}
		
		if(ValidatorUtil.isEmpty(obj.getEntidadeFinanciadora()))
			obj.setEntidadeFinanciadora(new EntidadeFinanciadora());
		else
			modalidadesOrgaoFinanciador = getDAO(TipoBolsaPesquisaDao.class).findByOrgaoFinanciador(obj.getEntidadeFinanciadora().getId());

		String[] modalidades = obj.getModalidades() != null ? obj.getModalidades().split(",") : new String[]{};
		selectedItems = new ArrayList<String>();
		for (int i = 0; i < modalidades.length; i++){
			selectedItems.add(modalidades[i]);
		}
		
		setConfirmButton("Alterar");
		afterAtualizar();

		return forward(getFormPage());
	}
	
	/**
	 * Serve para Inativar um cota de bolsa.
	 * 
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/CotaBolsas/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), CotaBolsas.class);
		if ( obj == null || obj.getId() == 0 || !obj.isAtivo() ) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO, TipoMensagemUFRN.ERROR);
			return forward(getListPage());
		} else {
			prepareMovimento( ArqListaComando.DESATIVAR );
			super.inativar();
			PagingInformation paginacao = (PagingInformation) getMBean("paginacao");
			paginacao.setPaginaAtual(0);
			return listar();
		}
	}
	
	/**
	 * Remove o bean da sess�o e redireciona o usu�rio para
	 * a p�gina inicial do subsistema atual.
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/CotaBolsas/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		// removendo da sessao
		resetBean();
		
		try {
			if(!getUltimoComando().equals(ArqListaComando.CADASTRAR))
				return listar();
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		
		return null;
	}

	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		setLabelCombo("descricao");
		return super.getAllCombo();
	}
	
	public Collection<String> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(Collection<String> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public Map<Integer, String> getModalidadesOrgaoFinanciador() {
		return modalidadesOrgaoFinanciador;
	}

	public void setModalidadesOrgaoFinanciador(
			Map<Integer, String> modalidadesOrgaoFinanciador) {
		this.modalidadesOrgaoFinanciador = modalidadesOrgaoFinanciador;
	}

	public Collection<CotaBolsas> getCotas() {
		return cotas;
	}

	public void setCotas(Collection<CotaBolsas> cotas) {
		this.cotas = cotas;
	}
	
}