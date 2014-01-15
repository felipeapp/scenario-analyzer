/* 
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
 *
 * Created on 05/03/2007
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;

/**
 * Managed bean para cadastro de Linhas de Pesquisa
 * 
 * @author Leonardo
 */
@Component("linhaPesquisa") @Scope("session")
public class LinhaPesquisaStrictoMBean extends SigaaAbstractController<LinhaPesquisaStricto> {

	/** Lista das áreas relacionadas ao programa. */
	private List<SelectItem> possiveisAreas = new ArrayList<SelectItem>(0);

	/** Parâmetro utilizado na busca. */
	private String param;
	
	/** Construtor padrão. */
	public LinhaPesquisaStrictoMBean(){
		initObj();
	}

	/** Inicializa os atributos do controller. 
	 * <br/><br/>
	 * Método não invocado por JSP
	 * 
	 * */
	private void initObj() {
		obj = new LinhaPesquisaStricto();
		obj.setPrograma( new Unidade() );
		obj.setArea( new AreaConcentracao() );
		possiveisAreas = new ArrayList<SelectItem>();
	}

	@Override
	public String getFormPage() {
		return "/stricto/linha_pesquisa/form.jsp";
	}

	@Override
	public String getListPage() {
		return "/stricto/linha_pesquisa/lista.jsf";
	}

	/** Prepara para atualizar uma linha de pesquisa.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> /sigaa.war/stricto/linha_pesquisa/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.PPG);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		GenericDAO dao = getGenericDAO();
		
		dao.setSistema(getSistema());
		dao.setSession(getSessionRequest());
		
		int id = getParameterInt("id", 0);
		obj = dao.findByPrimaryKey(id, LinhaPesquisaStricto.class);
		if (obj == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "Objeto não encontrado");
		setReadOnly(false);
		setConfirmButton("Alterar");
		afterAtualizar();
		carregarAreas(obj.getPrograma().getId());
		return forward(getFormPage());
	}

	/**
	 * É executado após chamar o método atualizar.
	 * <br/><br/>
	 * Método não invocado por JSP
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterAtualizar()
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if (obj != null && obj.getId() > 0) {
			if (obj.getArea() == null || obj.getArea().getId() == 0)
				obj.setArea(new AreaConcentracao());
			carregarAreas(null);
		}
	}
	
	/**
	 * É executado após remover.
	 * <br/><br/>
	 * Método não invocado por JSP
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterRemover()
	 */
	@Override
	public void afterRemover() {
		initObj();
	}

	/**
	 * Prepara para remover uma lista de pesquisa.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> /sigaa.war/stricto/linha_pesquisa/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String preRemover() {
		try {
			checkRole(SigaaPapeis.PPG);
			initObj();
			String ret = super.preRemover();
			if (obj != null && obj.getId() > 0) {
				carregarAreas(null);
			}
			return ret;
		} catch (SegurancaException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (DAOException e) {
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
	}
	
	/** Prepara para cadastrar uma nova linha de pesquisa.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException {
		resetBean();
		checkRole(SigaaPapeis.PPG);
		prepareMovimento(ArqListaComando.CADASTRAR);
		initObj();
		return forward(getFormPage());
	}

	/**
	 * Método não invocado por JSP
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAfterValidate()
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,	SegurancaException, DAOException {
		super.beforeCadastrarAfterValidate();
		if( obj.getArea().getId() == 0 )
			obj.setArea(null);
	}

	/**
	 * Método não invocado por JSP
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		if ("alterar".equalsIgnoreCase(getConfirmButton())) {
			return getListPage();
		}
		return super.forwardCadastrar();
	}

	/**
	 * Método não invocado por JSP
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterCadastrar()
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		initObj();
	}
	
	/**
	 * Retorna uma lista de linhas de pesquisas dado os critérios de busca.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/stricto/linha_pesquisa/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca.");
			return null;
		}
	
		//adicionar paginação
		GenericDAO dao = getGenericDAO();
		dao.addOrder("denominacao", "asc");
		if ("programa".equalsIgnoreCase(param)) {
			setResultadosBusca( dao.findByExactField(LinhaPesquisaStricto.class, "programa.id", obj.getPrograma().getId()));
		} else if ("nome".equalsIgnoreCase(param)) {
			setResultadosBusca( dao.findByLikeField(LinhaPesquisaStricto.class, "denominacao", obj.getDenominacao()) );
		} else if ("todos".equalsIgnoreCase(param))
			setResultadosBusca( dao.findAll(LinhaPesquisaStricto.class) );
		else
			setResultadosBusca(null);

		return null;
	}
	
	/** 
	 * Lista as linhas de pesquisa cadastradas.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		initObj();
		return super.listar();
	}

	/** Carrega a lista de áreas relacionadas ao programa. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/stricto/linha_pesquisa/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarAreas(ValueChangeEvent e) throws DAOException {
		int idPrograma = 0;
		if (e == null || e.getNewValue() == null)
			return;
		else {
			idPrograma = (Integer)e.getNewValue();
			carregarAreas(idPrograma);
			forward(getFormPage());
		}
	}
	
	/**
	 * Carrega a lista de áreas relacionadas ao programa.
	 * <br/><br/>
	 * Método não invocado por JSP
	 * 
	 * @param idPrograma
	 * @throws DAOException
	 */
	private void carregarAreas(int idPrograma) throws DAOException {
		obj.getPrograma().setId(idPrograma);
		if (obj != null)
			idPrograma = obj.getPrograma().getId();
		possiveisAreas = new ArrayList<SelectItem>();
		for (AreaConcentracao area : getGenericDAO().findByExactField(AreaConcentracao.class, "programa.id", idPrograma, "asc", "denominacao")) {
			String id = area.getId()+"";
			String showText = area.getDenominacao() + " (" + area.getNivelDesc() + ")";
			possiveisAreas.add(new SelectItem(id, showText));
		}
	}

	/** 
	 * Retorna a lista das áreas relacionadas ao programa.
	 * <br/><br/>
	 * Método não invocado por JSP
	 *  
	 * @return
	 */
	public List<SelectItem> getPossiveisAreas() {
		return possiveisAreas;
	}

	/** 
	 * Seta a lista das áreas relacionadas ao programa.
	 * <br/><br/>
	 * Método não invocado por JSP
	 * 
	 * @param possiveisAreas
	 */
	public void setPossiveisAreas(List<SelectItem> possiveisAreas) {
		this.possiveisAreas = possiveisAreas;
	}
	
	/**
	 * Cancela a operação.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * <li>/sigaa.war/stricto/linha_pesquisa/form.jsp</li>
	 * <li>/sigaa.war/stricto/linha_pesquisa/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		obj = null;
		return redirectJSF(getSubSistema().getLink());
	}
	
	/** Retorna o parâmetro utilizado na busca. 
	 * @return
	 */
	public String getParam() {
		return param;
	}

	/** Seta o parâmetro utilizado na busca.
	 * @param param
	 */
	public void setParam(String param) {
		this.param = param;
	}

}
