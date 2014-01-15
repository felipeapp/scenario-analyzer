/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Controller respons�vel por opera��es de cadastro de {@link Enfase}.
 * @author �dipo Elder F. Melo
 */
@Component("enfase")
@Scope("request")
public class EnfaseMBean extends SigaaAbstractController<Enfase> {

	/** Lista de poss�veis matriz curriculares. */
	private List<SelectItem> possiveisMatrizes;
	/** Lista de poss�veis curr�culos. */
	private List<SelectItem> possiveisCurriculos;
	/** Curr�culo selecionado pelo usu�rio. */
	private Curriculo curriculo;
	/** Matriz curricular selecionada pelo usu�rio. */
	private MatrizCurricular matrizCurricular;
	/** Novo c�digo do curr�culo.*/
	private String novoCodigo;
	/** Cole��o de {@link SelectItem} de �nfases por curso para sele��o. */
	private List<SelectItem> enfases = new ArrayList<SelectItem>();
	/** Indica se o cadastro � apenas do nome da �nfase ou copiando uma matriz curricular. */
	private boolean cadastroSimples;
	/** Atributo respons�vel por verificar se a tela de origem do cadastro de �nfase foi originada da tela de cadastro de matriz.*/
	private boolean retornarCadastroMatriz = false;

	/** Construtor padr�o. */
	public EnfaseMBean() {
		initObj();
	}
	
	/** Inicializa os atributos do controller. */
	private void initObj() {
		obj = new Enfase();
		curriculo = new Curriculo();
		matrizCurricular = new MatrizCurricular();
		matrizCurricular.setCurso(new Curso());
		matrizCurricular.setEnfase(new Enfase());
		possiveisMatrizes = new ArrayList<SelectItem>(0);
		possiveisCurriculos = new ArrayList<SelectItem>(0);
	}
	
	/**
	 * Cadastra a �nfase, cadastrando uma nova matriz curricular e um novo
	 * curr�culo.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/enfase/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		checkChangeRole();
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {
			if (cadastroSimples) {
				checkOperacaoAtiva(ArqListaComando.ALTERAR.getId(), ArqListaComando.CADASTRAR.getId());
				ValidatorUtil.validateRequired(obj.getCurso(), "Curso", erros);
				ValidatorUtil.validateRequired(obj.getNome(), "Nome da �nfase", erros);
				if (hasErrors()) return null;
				try {
					if ("alterar".equalsIgnoreCase(getConfirmButton())) {
						MovimentoCadastro mov = new MovimentoCadastro();
						mov.setObjMovimentado(obj);
						mov.setCodMovimento(ArqListaComando.ALTERAR);
						execute(mov);
						addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "�nfase");
					} else {
						MovimentoCadastro mov = new MovimentoCadastro();
						mov.setObjMovimentado(obj);
						mov.setCodMovimento(ArqListaComando.CADASTRAR);
						execute(mov);
						addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "�nfase");
					}
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
					return null;
				} 
				removeOperacaoAtiva();
				if (retornarCadastroMatriz){
					return retornarCadastroMatrizEnfase();
				} else {
					return redirectJSF(getListPage());
				}	
			} else {
				checkOperacaoAtiva(SigaaListaComando.CADASTRAR_ENFASE.getId());
				validacaoDados(erros);
				if (hasErrors()) return null;
				try {
					if (obj.getId() == 0) {
						beforeCadastrarAfterValidate();
						MovimentoCadastro mov = new MovimentoCadastro();
						mov.setObjMovimentado(curriculo);
						// se possui curr�culo cadastrado, utiliza-o
						mov.setAcao(MovimentoCadastro.ACAO_CRIAR);
						mov.setCodMovimento(SigaaListaComando.CADASTRAR_ENFASE);
						execute(mov);
						addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "�nfase");
					} else {
						MovimentoCadastro mov = new MovimentoCadastro();
						mov.setObjMovimentado(obj);
						mov.setCodMovimento(ArqListaComando.ALTERAR);
						execute(mov);
						addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "�nfase");
					}
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
					return null;
				} 
				removeOperacaoAtiva();
				return cancelar();
			}
		}
	}
	
	/**
	 * M�todo respons�vel pela opera��o de cancelamento de cadastro de �nfase,
	 * tratando os redirecionamentos conforme a origem da solicita��o.
	 * @return
	 * @throws ArqException 
	 */
	public String cancelarEnfase() throws ArqException {
		if (retornarCadastroMatriz){
			return retornarCadastroMatrizEnfase();
		} else {
			return super.cancelar();
		}	
	}
	
	/** Inicia o cadastramento de �nfase.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_ENFASE.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_ENFASE);
		setConfirmButton("Cadastrar");
		initObj();
		cadastroSimples = false;
		retornarCadastroMatriz = true;
		carregarEnfases(obj.getCurso().getId(), null);
		return forward(getFormPage());
	}
	
	/** Duplica a matriz curricular e associa a �nfase.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAfterValidate()
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
		curriculo.setCodigo(novoCodigo);
		curriculo.setMatriz(matrizCurricular);
	}

	/** Redireciona para a lista de �nfase ap�s o cadastro.
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	/** Retorna o link para o formul�rio de cadastro da �nfase.
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/graduacao/enfase/form.jsf";
	}

	/** Retorna o link para a lista de �nfase.
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/graduacao/enfase/lista.jsf";
	}

	/** Verifica os pap�is do usu�rio: SigaaPapeis.CDP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.CDP);
	}
	
	/** Carrega a lista de matrizes curriculares de um curso.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/enfase/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws ArqException 
	 */
	public void carregaListaMatrizes(ValueChangeEvent e) throws ArqException {
		possiveisMatrizes = new ArrayList<SelectItem>(0);
		if (e.getNewValue() != null) {
			int idCurso = (Integer) e.getNewValue();
			if (idCurso > 0) {
				MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
				possiveisMatrizes = toSelectItems(dao.findByCurso(idCurso, true), "id", "descricao");
				carregarEnfases(idCurso, null);
			}
		}
		novoCodigo = null;
	}
	
	/**
	 * Carregar a lista de selectItem com as �nfases do curso selecionado no formul�rio,
	 * removendo a �nfase da matr�z curricular de origem.
	 * @param idCurso
	 * @throws ArqException
	 */
	private void carregarEnfases(int idCurso, Enfase enfaseMatrizOrigem) throws ArqException{
		MatrizCurricularMBean mcBean = new MatrizCurricularMBean();
		mcBean.carregarEnfasesByCurso(idCurso, true);
		setEnfases(mcBean.getEnfases());
		if ( enfaseMatrizOrigem != null ){
			for (Iterator<SelectItem> iterator = getEnfases().iterator(); iterator
			.hasNext();) {
				SelectItem enfase = iterator.next();
				if (Integer.parseInt(enfase.getValue().toString()) == enfaseMatrizOrigem.getId()) {
					iterator.remove();
				}
			}
		}
	}
	
	/** Carrega a lista de curr�culos de uma matriz curricular.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/enfase/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws ArqException 
	 */
	public void carregaListaCurriculos(ValueChangeEvent e) throws ArqException {
		possiveisCurriculos = new ArrayList<SelectItem>(0);
		if (e.getNewValue() != null) {
			int idMatriz = (Integer) e.getNewValue();
			if (idMatriz > 0) {
				EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
				MatrizCurricular matriz = dao.findByPrimaryKey(idMatriz, MatrizCurricular.class);
				Collection<Curriculo> curriculos = dao.findByMatriz(matriz.getId());
				possiveisCurriculos = toSelectItems(curriculos, "id", "matrizDescricao");
				carregarEnfases(matriz.getCurso().getId(), matriz.getEnfase());
			}
		}
	}
	
	/** Atualiza os dados da �nfase.<br>
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		populateObj(true);
		setConfirmButton("Alterar");
		cadastroSimples = true;
		return forward("/graduacao/enfase/form_simples.jsp");
	}
	
	/** Inicia o cadastramento de uma nova �nfase, sem duplicar curr�culo.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 */
	public String preCadastrarSimples() throws ArqException {
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		prepareMovimento(ArqListaComando.CADASTRAR);
		populateObj(true);
		setConfirmButton("Cadastrar");
		cadastroSimples = true;
		if (matrizCurricular.getCurso() != null){
			obj = new Enfase();
			obj.setCurso(matrizCurricular.getCurso());
			
		}	
		return forward("/graduacao/enfase/form_simples.jsp");
	}
	
	/** Calcula o novo c�digo do curr�culo.
	 * @return
	 * @throws DAOException
	 */
	public String getNovoCodigo() throws DAOException {
		// se o c�digo for null, sugere um novo c�digo
		if (novoCodigo == null) {
			String codigo = "";
			if (curriculo.getId() > 0) {
				curriculo = getGenericDAO().refresh(curriculo);
				codigo = curriculo.getCodigo().toUpperCase();
				char ultimo = codigo.charAt(codigo.length() - 1);
				if (ultimo >= 'A' && ultimo <='Y') {
					ultimo++;
					codigo = codigo.substring(0, codigo.length() - 1) + ultimo;
				} else {
					ultimo = 'A';
					codigo = codigo + ultimo;
				}
			}
			novoCodigo = codigo;
		}
		return novoCodigo;
	}
	
	/** Valida os dados para o cadastro.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ValidatorUtil.validateRequired(this.matrizCurricular.getCurso(), "Curso", mensagens);
		ValidatorUtil.validateRequired(this.matrizCurricular, "Matriz Curricular", mensagens);
		ValidatorUtil.validateRequired(this.curriculo, "Curr�culo", mensagens);
		ValidatorUtil.validateRequired(this.novoCodigo, "Novo C�digo do Curr�culo", mensagens);
		ValidatorUtil.validateRequired(this.matrizCurricular.getEnfase(), "�nfase", erros);
		MatrizCurricular matriz;
		try {
			matriz = getGenericDAO().refresh(this.matrizCurricular);
			if (matriz.getHabilitacao() != null)
				mensagens.addErro("� vedada a cria��o de �nfases em cursos que possuam habilita��es ativas.");
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		return !mensagens.isErrorPresent();
	} 

	/**
	 * Inativa uma �nfase.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/enfase/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String inativar() throws ArqException, NegocioException {
		setId();
		if (obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			prepareMovimento(ArqListaComando.DESATIVAR);
			MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
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
			removeOperacaoAtiva();
			return redirectJSF(getListPage());
		}
	}
	
	/** Retorna uma lista de selectItem de �nfases ativas
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getGenericDAO().findAllAtivos(Enfase.class, "nome"), "id", "nome");
	}
	
	@Override
	public Collection<Enfase> getAll() throws ArqException {
		return getAllObj(Enfase.class, "curso.nome, nome");
	}
	
	/**
	 * M�todo respons�vel por retornar o usu�rio para o cadastro de matriz curricular com �nfase.
	 * @return
	 * @throws ArqException
	 */
	private String retornarCadastroMatrizEnfase() throws ArqException{
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_ENFASE.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_ENFASE);
		setConfirmButton("Cadastrar");
		cadastroSimples = false;
		carregarEnfases(obj.getCurso().getId(), null);
		matrizCurricular.setEnfase(obj);
		obj = new Enfase();
		return forward(getFormPage());
	}	
	
	/** Retorna a lista de poss�veis matriz curriculares. 
	 * @return
	 */
	public List<SelectItem> getPossiveisMatrizes() {
		return possiveisMatrizes;
	}

	/** Seta a lista de poss�veis matriz curriculares.
	 * @param possiveisMatrizes
	 */
	public void setPossiveisMatrizes(List<SelectItem> possiveisMatrizes) {
		this.possiveisMatrizes = possiveisMatrizes;
	}

	/** Retorna a lista de poss�veis curr�culos. 
	 * @return
	 */
	public List<SelectItem> getPossiveisCurriculos() {
		return possiveisCurriculos;
	}

	/** Seta a lista de poss�veis curr�culos.
	 * @param possiveisCurriculos
	 */
	public void setPossiveisCurriculos(List<SelectItem> possiveisCurriculos) {
		this.possiveisCurriculos = possiveisCurriculos;
	}

	/** Retorna o curr�culo selecionado pelo usu�rio. 
	 * @return
	 */
	public Curriculo getCurriculo() {
		return curriculo;
	}

	/** Seta o curr�culo selecionado pelo usu�rio.
	 * @param curriculo
	 */
	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	/** Retorna a matriz curricular selecionada pelo usu�rio.
	 * @return
	 */
	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	/** Seta a matriz curricular selecionada pelo usu�rio.
	 * @param matrizCurricular
	 */
	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}


	/** Seta o c�digo do novo curr�culo.
	 * @param novoCodigo
	 */
	public void setNovoCodigo(String novoCodigo) {
		this.novoCodigo = novoCodigo;
	}

	public List<SelectItem> getEnfases() {
		return enfases;
	}

	public void setEnfases(List<SelectItem> enfases) {
		this.enfases = enfases;
	}
	
}
