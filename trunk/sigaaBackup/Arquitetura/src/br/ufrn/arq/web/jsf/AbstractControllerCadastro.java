/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.ALTERADO_COM_SUCESSO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.CADASTRADO_COM_SUCESSO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.validateEntity;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;

/**
 * Controlador abstrado para cadastro de entidades
 * 
 * @author Gleydson Lima
 * 
 * @param <T>
 */
public class AbstractControllerCadastro<T> extends AbstractController {

	protected T obj;

	private String confirmButton = "Cadastrar";

	private boolean readOnly = false;

	private static String disableClass = "disable";

	protected Collection<T> resultadosBusca;

	private String idCombo = "id";

	private String labelCombo = "denominacao";

	protected Collection<T> all;

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setConfirmButton(String confirmButton) {
		this.confirmButton = confirmButton;
	}

	/**
	 * Ação realizada antes de entrar no cadastrar
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	public String listar() throws ArqException {
		checkListRole();
		return forward(getListPage());
	}

	/**
	 * Método adicionado na inclusão de uma entidade
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		checkChangeRole();

		if (confirmButton.equalsIgnoreCase("remover")) {
			return remover();
		} else {

			// transforma para uma visão de objeto validável e chama a validação
			Validatable objValidavel = null;
			if (obj instanceof Validatable) {
				objValidavel = (Validatable) obj;
			}

			PersistDB obj = (PersistDB) this.obj;

			beforeCadastrarAndValidate();

			if (obj instanceof Validatable && objValidavel != null) {
				erros = new ListaMensagens();
				ListaMensagens lista = objValidavel.validate();

				if (lista != null && !lista.isEmpty()) {
					erros.addAll(lista.getMensagens());
				}
			}
			
			validateEntity(obj, erros);
			doValidate();

			String descDominio = ReflectionUtils.evalProperty(obj, "descricaoDominio");
			
			if (!hasErrors()) {
				beforeCadastrarAfterValidate();

				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);

				if (obj.getId() == 0) {
					
					mov.setCodMovimento(ArqListaComando.CADASTRAR);
					try {
						execute(mov);
						
						if (descDominio != null && !descDominio.equals("")) {
							addMensagem(CADASTRADO_COM_SUCESSO, descDominio);
						} else {
							addMensagem(OPERACAO_SUCESSO);
						}
						if (isReprepare())
							prepareMovimento(ArqListaComando.CADASTRAR);
					} catch (Exception e) {
						notifyError(e);
						addMensagemErroPadrao();
						e.printStackTrace();
						return forward(getFormPage());
					}

					afterCadastrar();

					String forward = forwardCadastrar();
					if (forward == null) {
						return redirectJSF(getCurrentURL());
					} else {
						return redirectJSF(forward);
					}

				} else {
					if( !checkOperacaoAtiva(ArqListaComando.ALTERAR.getId()) )
						return cancelar();
					
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					
					try {
						execute(mov);
						if (descDominio != null && !descDominio.equals("")) {
							addMensagem(ALTERADO_COM_SUCESSO, descDominio);
						} else {
							addMensagem(OPERACAO_SUCESSO);
						}
					} catch (Exception e) {
						notifyError(e);
						addMensagemErroPadrao();
						e.printStackTrace();
						return forward(getFormPage());
					}

					afterCadastrar();
					removeOperacaoAtiva();

					String forward = forwardCadastrar();
					if (forward == null) {
						return redirectJSF(getListPage());
					} else {
						return redirectJSF(forward);
					}
				}

			} else {
				return null;
			}
		}
	}

	protected void doValidate() throws ArqException {
		
	}

	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
	}

	protected void afterCadastrar() throws ArqException {
		resetBean();
	}

	/**
	 * Redefinição da página após o cadastrar
	 * 
	 * @return
	 */
	public String forwardCadastrar() {
		return null;
	}

	/**
	 * Método chamado para remover uma entidade
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String remover() throws ArqException {

		beforeRemover();

		PersistDB obj = (PersistDB) this.obj;
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.REMOVER);

		if (obj == null || obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return redirect(getListPage());
			} 

			setResultadosBusca(null);
			afterRemover();

			String forward = forwardRemover();
			if (forward == null) {
				return redirectJSF(getListPage());
			} else {
				return redirectJSF(forward);
			}

		}
	}

	/**
	 * Método chamado para remover uma entidade
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String inativar() throws ArqException, NegocioException {

		beforeInativar();

		PersistDB obj = (PersistDB) this.obj;
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
		setId();

		if (obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			try {
				execute(mov);
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

			setResultadosBusca(null);
			afterInativar();
			removeOperacaoAtiva();

			String forward = forwardInativar();
			if (forward == null) {
				return redirectJSF(getListPage());
			} else {
				return redirectJSF(forward);
			}

		}
	}

	protected String forwardInativar() {
		return null;
	}

	protected void afterInativar() {

	}

	protected void beforeInativar() {

	}

	/**
	 * Redefinição da página após o cadastrar
	 * 
	 * @return
	 */
	protected String forwardRemover() {
		return null;
	}

	/**
	 * Método chamado para entrar no modo de remoção
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String preRemover() {

		try {
			prepareMovimento(ArqListaComando.REMOVER);

			GenericDAO dao = new GenericDAOImpl(getSistema(), getSessionRequest());
			setId();
			PersistDB obj = (PersistDB) this.obj;

			this.obj = (T) dao.findByPrimaryKey(obj.getId(), obj.getClass());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}

		afterPreRemover();
		setReadOnly(true);

		setConfirmButton("Remover");

		return forward(getFormPage());

	}

	/**
	 * Busca no banco
	 * 
	 * @param parameterId
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public void populateObj(boolean parameterId) throws DAOException {
		try {
			GenericDAO dao = new GenericDAOImpl(getSistema(),
					getSessionRequest());
			if (parameterId) {
				setId();
			}
			this.obj = (T) dao.refresh((PersistDB) this.obj);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
	}

	public void populateObj() throws DAOException {
		populateObj(false);
	}

	public void afterPreRemover() {

	}

	/**
	 * template methd executado antes de remover
	 * 
	 */
	public void beforeRemover() throws DAOException {

	}

	/**
	 * template methd executado após remover
	 * 
	 */
	public void afterRemover() {

		resetBean();
	}

	/**
	 * Atualizar adaptado para usar em commandLinks
	 * 
	 * @param evt
	 * @return
	 * @throws ArqException
	 */
	public String atualizar(ActionEvent evt) throws ArqException {
		return atualizar();
	}

	@SuppressWarnings("unchecked")
	public String atualizar() throws ArqException {

		try {

			beforeAtualizar();
			
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);

			GenericDAO dao = new GenericDAOImpl(getSistema(),
					getSessionRequest());
			setId();
			PersistDB obj = (PersistDB) this.obj;
			setReadOnly(false);

			this.obj = (T) dao.findByPrimaryKey(obj.getId(), obj.getClass());

			setConfirmButton("Alterar");
			afterAtualizar();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}

		return forward(getFormPage());
	}

	protected void beforeAtualizar() throws ArqException {
		
	}

	public void afterAtualizar() throws ArqException {

	}

	/**
	 * Método que deve ser sobrescrito para verificar se o usuário
	 * logado tem permissão para realizar alterações nos dados do cadastro,
	 * isto é, alterar ou cadastrar novos dados.
	 * 
	 * @throws SegurancaException
	 */
	public void checkChangeRole() throws SegurancaException {

	}

	/**
	 * Método que deve ser sobrescrito para verificar se o usuário
	 * logado tem permissão para ver a listagem de dados do cadastro. 
	 * 
	 * @throws SegurancaException
	 */
	public void checkListRole() throws SegurancaException {

	}

	public String list() {
		return null;
	}

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	public Collection<T> getResultadosBusca() {
		return resultadosBusca;
	}

	public void setResultadosBusca(Collection<T> resultadosBusca) {
		this.resultadosBusca = resultadosBusca;
	}

	/**
	 * Retorna todos os itens de uma determinada entidade
	 * 
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> getAll() throws ArqException {
		if (all == null) {
			GenericDAO dao = null;
			dao = new GenericDAOImpl(getSistema(), getSessionRequest());
			all = (Collection<T>) dao.findAll(obj.getClass());
		}
		return all;
	}

	/**
	 * Retorna todos os itens de uma determinada entidade considerando o
	 * atributo ativo
	 * 
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> getAllAtivos() throws ArqException {
		if (all == null) {
			GenericDAO dao = null;
			dao = new GenericDAOImpl(getSistema(), getSessionRequest());
			all = (Collection<T>) dao.findByExactField(obj.getClass(), "ativo",
					Boolean.TRUE);
		}
		return all;
	}

	/**
	 * Retorna todos os itens de uma determinada entidade
	 * 
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> getAllPaginado() throws ArqException {
		GenericDAO dao = null;
		dao = new GenericDAOImpl(getSistema(), getSessionRequest());
		if (getAtributoOrdenacao() == null) {
			return (Collection<T>) dao.findAll(obj.getClass(), getPaginacao());
		} else {
			return (Collection<T>) dao.findAll(obj.getClass(), getPaginacao(),
					getAtributoOrdenacao(), "asc");
		}
	}

	public String getAtributoOrdenacao() {
		return null;
	}

	public void setId() {

		/**
		 * Evitar NullPointer
		 */
		int id = getParameterInt("id", 0);
		Integer idInt = new Integer(id);
		((PersistDB) obj).setId(idInt);

	}

	public String getConfirmButton() {
		return confirmButton;
	}

	/**
	 * Página do formulário
	 * 
	 * @return
	 */
	public String getFormPage() {
		return getDirBase() + "/form.jsf";
	}

	/**
	 * Página de visualização
	 * 
	 * @return
	 */
	public String getViewPage() {
		return getDirBase() + "/view.jsf";
	}

	/**
	 * Retorna o diretório base
	 * 
	 * @return
	 */
	public String getDirBase() {

		String dirBase = "";

		if (getCurrentSession().getAttribute("dirBase") != null) {
			dirBase = (String) getCurrentSession().getAttribute("dirBase");
		} else {
			if (getCurrentRequest().getAttribute("dirBase") != null) {
				dirBase = (String) getCurrentRequest().getAttribute("dirBase");
			} else {
				dirBase = getSubSistema().getDirBase();
			}
		}

		String mBeanClassName = obj.getClass().toString();
		String mBean = mBeanClassName
				.substring(mBeanClassName.lastIndexOf(".") + 1);
		if (dirBase.equals("")) {
			return dirBase;
		} else {
			return dirBase + mBean;
		}
	}

	/**
	 * Diretório base da lista
	 * 
	 * @return
	 */
	public String getListPage() {

		return getDirBase() + "/lista.jsf";
	}

	//
	//
	/**
	 * método chamado antes do cadastrar, reimplementado pelo filho se ele
	 * desejar fazer algo antes de cadatrar
	 */
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {

	}

	public String getDisableClass() {
		return disableClass;
	}

	/**
	 * Usado para reornar para a lista que originou o formulário
	 * 
	 * @return
	 */
	public String backList() {
		return forward(getListPage());
	}

	// Retorna o findAll do DAO
	public <U> Collection<U> getAllObj(Class<U> classe) {
		GenericDAO dao = null;
		try {
			dao = new GenericDAOImpl(getSistema(), getSessionRequest());
			return dao.findAll(classe);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			return new ArrayList<U>();
		}
	}

	// Retorna o findAll do DAO ordenado
	public <U> Collection<U> getAllObj(Class<U> classe, String orderField) {
		GenericDAO dao = null;
		try {
			dao = new GenericDAOImpl(getSistema(), getSessionRequest());
			return dao.findAll(classe, orderField, "asc");
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			return new ArrayList<U>();
		}
	}

	public String buscar() throws Exception {
		return null;
	}

	public boolean validacaoDados(ListaMensagens mensagens) {

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return true;
		}
		return false;
	}
	
	@Deprecated
	public boolean validacaoDados(Collection<MensagemAviso> mensagens) {

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return true;
		}
		return false;
	}
	
	/**
	 * Gera uma emissão de documento para o tipo informado e a identificação
	 * 
	 * @param tipoDocumento
	 * @param ident
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws NoSuchAlgorithmException
	 */
	public EmissaoDocumentoAutenticado geraEmissao(int tipoDocumento,
			String ident, String semente, String complemento, Integer subTipo, boolean controleNumero )
			throws ArqException, NegocioException {

		EmissaoDocumentoAutenticado emissao = new EmissaoDocumentoAutenticado();
		emissao.setDataEmissao(new Date());
		emissao.setIdentificador(ident);
		emissao.setTipoDocumento(tipoDocumento);
		if ( !controleNumero )
			emissao.setPrng(UFRNUtils.toSHA1Digest(String.valueOf(Math.random())));
		else {
			ResourceBundle bundle = ResourceBundle.getBundle("br.ufrn.arq.seguranca.autenticacao.validadores");
			emissao.setPrng(bundle.getString("prng_documento").trim());
		}
		
		emissao.setCodigoSeguranca(AutenticacaoUtil.geraCodigoValidacao(emissao, semente));
		emissao.setDadosAuxiliares(complemento);
		emissao.setSubTipoDocumento(subTipo);
		emissao.setEmissaoDocumentoComNumero(controleNumero);

		MovimentoCadastro cad = new MovimentoCadastro();
		cad.setCodMovimento(ArqListaComando.GERAR_EMISSAO_DOCUMENTO_AUTENTICADO);
		cad.setObjMovimentado(emissao);
		cad.setRegistroEntrada(getRegistroEntrada());
		cad.setRegistroAcessoPublico(getAcessoPublico());
		cad.setSistema(Sistema.COMUM);

		prepareMovimento(ArqListaComando.GERAR_EMISSAO_DOCUMENTO_AUTENTICADO);

		// chama o execute do controlador filho
		Object sistemaSolicitante = getCurrentRequest().getAttribute("sistema");
		getCurrentRequest().setAttribute("sistema", Sistema.COMUM);
		getCurrentRequest().setAttribute("sistema", sistemaSolicitante);
		
		return (EmissaoDocumentoAutenticado) execute(cad, getCurrentRequest());

	}

	/**
	 * Gera uma emissão de documento para o subtipo informado
	 * 
	 * @param tipoDocumento
	 * @param ident
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws NoSuchAlgorithmException
	 */
	public EmissaoDocumentoAutenticado geraEmissao(int tipoDocumento,
			int subTipoDocumento, String ident, String semente, String complemento)
			throws ArqException, NegocioException {

		EmissaoDocumentoAutenticado emissao = new EmissaoDocumentoAutenticado();
		emissao.setDataEmissao(new Date());
		emissao.setIdentificador(ident);
		emissao.setSubTipoDocumento(subTipoDocumento);
		emissao.setTipoDocumento(tipoDocumento);
		emissao.setPrng(UFRNUtils.toSHA1Digest(String.valueOf(Math.random())));
		emissao.setDadosAuxiliares(complemento);
		emissao.setCodigoSeguranca(AutenticacaoUtil.geraCodigoValidacao(
				emissao, semente));
		MovimentoCadastro cad = new MovimentoCadastro();
		cad.setCodMovimento(ArqListaComando.GERAR_EMISSAO_DOCUMENTO_AUTENTICADO);
		cad.setObjMovimentado(emissao);
		cad.setRegistroEntrada(getRegistroEntrada());
		cad.setRegistroAcessoPublico(getAcessoPublico());
		cad.setSistema(Sistema.COMUM);

		prepareMovimento(ArqListaComando.GERAR_EMISSAO_DOCUMENTO_AUTENTICADO);

		// chama o execute do controlador filho
		Object sistemaSolicitante = getCurrentRequest().getAttribute("sistema");
		getCurrentRequest().setAttribute("sistema", Sistema.COMUM);
		execute(cad, getCurrentRequest());

		getCurrentRequest().setAttribute("sistema", sistemaSolicitante);

		return (EmissaoDocumentoAutenticado) cad.getObjMovimentado();

	}

	/**
	 * Retorna um combo com projeção dos dados
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllCombo() throws ArqException {
		GenericDAOImpl dao = getDAO(GenericDAOImpl.class, getSistema());
		Collection<?> retorno = dao.findAllCombo(getObj().getClass(),
				getLabelCombo(), "asc", getIdCombo(), getLabelCombo());

		ArrayList<SelectItem> combo = new ArrayList<SelectItem>();
		for (Object name : retorno) {
			Object[] linha = (Object[]) name;
			combo.add(new SelectItem(linha[0].toString(), linha[1].toString()));
		}
		return combo;

	}

	public String getIdCombo() {
		return idCombo;
	}

	public void setIdCombo(String idCombo) {
		this.idCombo = idCombo;
	}

	public String getLabelCombo() {
		return labelCombo;
	}

	public void setLabelCombo(String labelCombo) {
		this.labelCombo = labelCombo;
	}

	/**
	 * Transforma uma colecao qualquer em ArrayList
	 * @deprecated Usar CollectionUtils.toArrayList()
	 * @param col
	 * @return
	 */
	@Deprecated
	public <U> ArrayList<U> toArrayList(Collection<U> col) {
		return CollectionUtils.toArrayList(col);
	}

	/**
	 * Método utilizado para definir se a ação de CADASTRAR deve ser
	 * re-preparada automaticamente. (o comportamento padrão é para re-preparar,
	 * deve ser sobrescrito nas subclasses que não desejarem isso)
	 * 
	 * @return
	 */
	public boolean isReprepare() {
		return true;
	}

	
	public void setAll(Collection<T> all) {
		this.all = all;
	}
	
}