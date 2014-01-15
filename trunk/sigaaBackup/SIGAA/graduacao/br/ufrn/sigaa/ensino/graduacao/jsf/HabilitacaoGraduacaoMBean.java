/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.HabilitacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.AreaSesu;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.vestibular.dominio.LinguaEstrangeira;

/** Controller responsável pelo cadastro de habilitações dos cursos de graduação.
 * @author Édipo Elder F. Melo
 */
@Component("habilitacaoGrad") @Scope("request")
public class HabilitacaoGraduacaoMBean extends SigaaAbstractController<Habilitacao> {

	/** Endereço da página do menu de graduação. */
	public static final String JSP_MENU_GRADUACAO = "menuGraduacao";

	/** Construtor padrão. */
	public HabilitacaoGraduacaoMBean() {
		initObj();
	}

	/** Inicializa os atributos do controller. */
	private void initObj() {
		obj = new Habilitacao();
		obj.setAreaSesu(new AreaSesu());
		obj.setCurso( new Curso() );
		if (ValidatorUtil.isEmpty(obj.getLinguaObrigatoriaVestibular()))
			obj.setLinguaObrigatoriaVestibular(new LinguaEstrangeira());
		setResultadosBusca(new ArrayList<Habilitacao>());
	}

	/** Retorna uma coleção de SelectItem de habilitações.
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(Habilitacao.class, "id", "nome");
	}

	/** Reinicializa os atributos do controller após o cadastro.
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterCadastrar()
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		initObj();
	}

	/** Retorna o formulário de cadastro de habilitação.
	 * Método não invocado por JSP 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/graduacao/habilitacao/form.jsf";
	}

	/** Retorna a lista de habilitações cadastradas.
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/graduacao/habilitacao/lista.jsf";
	}
	
	
	/**
	 * Redireciona para Consulta de Habilitações de Graduação. 
	 * Chamado por:
	 * sigaa.war/graduacao/habilitacao/lista.jsp
	 */
	public String listar() throws ArqException {
		initObj();
		return forward(getListPage());
	}
	
	

	/** Cancela a operação corrente.
	 * Chamado por /graduacao/habilitacao/form.jsp
	 * Chamado por /graduacao/habilitacao/lista.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		super.cancelar();
		if (obj.getId() == 0)
			return JSP_MENU_GRADUACAO;
		else
			return forward(getListPage());
	}

	/** Realiza uma busca de habilitações dado os parâmetros nome, código ou todos.
	 * Chamado por /graduacao/habilitacao/lista.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws DAOException {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}

		HabilitacaoDao dao = getDAO(HabilitacaoDao.class);
		if ("nome".equalsIgnoreCase(param)){
			if( obj.getNome() == null || obj.getNome().trim().length() == 0 ){
				addMensagemErro("Informe o critério de busca.");
				return null;
			}
			setResultadosBusca(dao.findByNome(obj.getNome(), null));
		}else if ("codigo".equalsIgnoreCase(param)){
			if( obj.getCodigoIes() == null || obj.getCodigoIes().trim().length() == 0 ){
				addMensagemErro("Informe o critério de busca.");
				return null;
			}
			setResultadosBusca(dao.findByExactField(Habilitacao.class, "codigoIes", obj.getCodigoIes()));
		}else if ("todos".equalsIgnoreCase(param)){
			setResultadosBusca(dao.findAll(Habilitacao.class, "nome", "asc"));
		}else
			setResultadosBusca(null);

		
		if((getResultadosBusca()!=null && getResultadosBusca().isEmpty()) || getResultadosBusca()==null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		
		
		return null;
	}

	/** Seta para null os atributos com ID = 0.
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAndValidate()
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		if (obj.getAreaSesu().getId() == 0)
			obj.setAreaSesu(null);
		if (obj.getLinguaObrigatoriaVestibular().getId() == 0) {
			obj.setLinguaObrigatoriaVestibular(null);
		}
	}

	/** Cadastra/altera a habilitação
	 * Chamado por /graduacao/habilitacao/form.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws NegocioException, ArqException {
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			obj.setAreaSesu(null);
			return remover();
		}
		return super.cadastrar();
	}

	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, 
				SegurancaException, DAOException {
			obj.setCodigoIes(null);
		super.beforeCadastrarAfterValidate();
	}
	
	/** Prepara para cadastrar uma habilitação.
	 * Chamado por /graduacao/menus/cdp.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws SegurancaException  {
		checkChangeRole();
		return forward(getFormPage());
	}

	/** Remove a habilitação.
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#remover()
	 */
	@Override
	public String remover() throws ArqException {
		if (obj.getLinguaObrigatoriaVestibular().getId() == 0)
			obj.setLinguaObrigatoriaVestibular(null);
		super.remover();
		obj = new Habilitacao();
		return forward(getListPage());
	}

	/**
	 * Define o identificador do objeto para remoção.
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeRemover()
	 */
	@Override
    public void beforeRemover() throws DAOException {
        try {
        	prepareMovimento(ArqListaComando.REMOVER);

			setId();
			PersistDB obj = this.obj;

			this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), Habilitacao.class);
        } catch (ArqException e) {
            e.printStackTrace();
        }
    }
	
	/** Prepara para remover uma habilitação.
	 * Chamado por /graduacao/habilitacao/lista.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String preRemover() {
		try {
			checkChangeRole();
		} catch (SegurancaException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return forward(getListPage());
		}
		return super.preRemover();
	}

	/** Prepara para atualizar uma habilitação.
	 * Chamado por /graduacao/habilitacao/lista.jsp  
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		try {
			checkChangeRole();
		} catch (SegurancaException e) {
			notifyError(e);
			addMensagemErroPadrao();
			return forward(getListPage());
		}
		String atualizar = super.atualizar();
		if (ValidatorUtil.isEmpty(obj.getLinguaObrigatoriaVestibular()))
			obj.setLinguaObrigatoriaVestibular(new LinguaEstrangeira());
		return atualizar;
	}
	
	/** Verifica os papéis: CDP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.CDP);
	}
}
