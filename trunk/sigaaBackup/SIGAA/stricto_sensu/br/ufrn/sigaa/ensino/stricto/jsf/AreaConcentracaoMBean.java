/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 05/03/2007
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.stricto.AreaConcentracaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;

/**
 * Managed bean para cadastro de Áreas de Concentração
 *
 * @author Leonardo
 *
 */
@Component("areaConcentracao")
@Scope("request")
public class AreaConcentracaoMBean extends SigaaAbstractController<AreaConcentracao> {

	/** Programa utilizado para filtrar as consultas. */
	private Unidade programa;
	
	/** Parâmetro utilizado na busca. */
	private String param;
	
	/** Objeto para armazenar os dados da busca. */
	private AreaConcentracao areaConcentracaoBusca;
	
	/** Link para a página de listagem de áreas de concentração. */
	private static final String JSP_LISTA = "/stricto/area_concentracao/lista.jsf";
	
	/** Construtor padrão. */
	public AreaConcentracaoMBean(){
		initObj();
		initDadosBusca();
	}

	/** Inicializa os atributos do objeto do controller. */
	private void initObj() {
		obj = new AreaConcentracao();
		obj.setPrograma(new Unidade());
		obj.setNivel(NivelEnsino.MESTRADO);
		obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
	}

	/** Inicializa os atributos do objeto relacionados à busca. */
	private void initDadosBusca() {
		areaConcentracaoBusca = new AreaConcentracao();
		areaConcentracaoBusca.setPrograma(new Unidade());
		areaConcentracaoBusca.setNivel(NivelEnsino.MESTRADO);
		areaConcentracaoBusca.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
	}

	/**
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/stricto/area_concentracao/form.jsp";
	}

	/**
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/stricto/area_concentracao/lista.jsf";
	}

	/**
	 * Direciona o usuário para a página de listagem.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/area_concentracao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String listPage() {
		return forward(JSP_LISTA);
	}
	
	/** Inicializa o processo de cadastro.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException {
		checkRole(SigaaPapeis.PPG);
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		obj.setNivel(NivelEnsino.MESTRADO);
		return forward(getFormPage());
	}
	
	/** Cadastra uma área de concentração.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/area_concentracao/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		if (!checkOperacaoAtiva(ArqListaComando.ALTERAR.getId(), ArqListaComando.CADASTRAR.getId(), ArqListaComando.REMOVER.getId()))
			return null;
		return super.cadastrar();
	}

	/** Redireciona após cadastrar.
	 * Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		if ("alterar".equalsIgnoreCase(getConfirmButton())) {
			return getListPage();
		}
		return super.forwardCadastrar();
	}
	
	/** Redireciona para a lista de áreas de concentração.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/area_concentracao/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		initObj();
		return super.listar();
	}

	/** Seta os atributos do objeto do controller.
	 * Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterCadastrar()
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		obj.setId(0);
		obj.setDenominacao(null);
		obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
	}

	/** Atualiza a área de concentração.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/area_concentracao/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		populateObj(true);
		if(isEmpty(obj)) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return cancelar();
		}
		prepareMovimento(ArqListaComando.ALTERAR);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		GenericDAO dao = getGenericDAO();
		setReadOnly(false);
		this.obj =  dao.findByPrimaryKey(obj.getId(), obj.getClass());
		if (obj.getAreaConhecimentoCnpq() == null)
			obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		afterAtualizar();
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}

	/** Seta o nível do objeto, após a atualização.
	 * Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterAtualizar()
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if (obj != null && obj.getId() > 0 && !NivelEnsino.isValido(obj.getNivel()))
			obj.setNivel(NivelEnsino.MESTRADO);
	}

	/** Inicia a operação de remoção de área de concentração.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/area_concentracao/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String preRemover() {
		try {
			checkRole(SigaaPapeis.PPG);
			setOperacaoAtiva(ArqListaComando.REMOVER.getId());
			return  super.preRemover();
		} catch (SegurancaException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
	}

	/** Busca por áreas de concentração, de acordo com os parâmetros especificados pelo usuário.
	 * <br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/area_concentracao/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws DAOException {
		param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}


		GenericDAO dao = getGenericDAO();
		dao.addOrder("denominacao", "asc");
		if ("programa".equalsIgnoreCase(param)){
			if(areaConcentracaoBusca.getPrograma().getId() <= 0){
				addMensagemErro("Selecione um Programa");
				return null;
			}
			setResultadosBusca( dao.findByExactField(AreaConcentracao.class, "programa.id", areaConcentracaoBusca.getPrograma().getId()) );
		} else if ("nome".equalsIgnoreCase(param)){
			if(areaConcentracaoBusca.getDenominacao().length() < 3){
				addMensagemErro("Informe um nome com no mínimo 3 caracteres para a busca");
				return null;
			}
			setResultadosBusca( dao.findByLikeField(AreaConcentracao.class, "denominacao", areaConcentracaoBusca.getDenominacao()) );
		} else if ("todos".equalsIgnoreCase(param))
			setResultadosBusca( dao.findAll(AreaConcentracao.class) );
		else
			setResultadosBusca(null);

		initObj();
		return null;
	}

	/**
	 * Retorna todas as areas de concentração do programa deste bean.
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllFromProgramaCombo() throws ArqException {
		if( isEmpty(programa) )
			return new ArrayList<SelectItem>();
		
		AreaConcentracaoDao dao = getDAO(AreaConcentracaoDao.class);
		return toSelectItems( dao.findByProgramaNivel(programa.getId(), null), "id", "denominacaoCompleta");
	}
	
	/** Retorna o programa utilizado para filtrar as consultas. 
	 * @return Programa utilizado para filtrar as consultas. 
	 */
	public Unidade getPrograma() {
		return programa;
	}

	/** Seta o programa utilizado para filtrar as consultas. 
	 * @param programa Programa utilizado para filtrar as consultas. 
	 */
	public void setPrograma(Unidade programa) {
		this.programa = programa;
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

	public AreaConcentracao getAreaConcentracaoBusca() {
		return areaConcentracaoBusca;
	}

	public void setAreaConcentracaoBusca(AreaConcentracao areaConcentracaoBusca) {
		this.areaConcentracaoBusca = areaConcentracaoBusca;
	}

}
