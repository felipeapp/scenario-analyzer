/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/12/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * MBean responsável pelo controle das operações dos parâmetros das gestoras acadêmicas.
 *
 * @author Andre
 *
 */
@Component("parametros")
@Scope("session")
public class ParametrosGestoraAcademicaMBean extends SigaaAbstractController<ParametrosGestoraAcademica> {

	/** Lista de SelecItems com possíveis valores para métodos de Avaliação. */
	private static List<SelectItem> metodosAvaliacao;

	/** Lista de SelecItems com possíveis valores para métodos de Avaliação do nível médio. */
	private static List<SelectItem> metodosAvaliacaoMedio;

	/** Lista de cursos que deverá ser escolhido para definir os parâmetros. */
	private List<SelectItem> comboCursos;

	/** Indica se o botão "<< Voltar" está habilitado no formulário. */
	private boolean habilitarVoltar = true;

	// carrega os valores possíveis para métodos de avaliação
	static {
		metodosAvaliacao = new ArrayList<SelectItem>();
		metodosAvaliacao.add(new SelectItem(MetodoAvaliacao.NOTA, "Nota"));
		metodosAvaliacao.add(new SelectItem(MetodoAvaliacao.COMPETENCIA, "Competência"));
		metodosAvaliacao.add(new SelectItem(MetodoAvaliacao.CONCEITO, "Conceito"));
		
		metodosAvaliacaoMedio = new ArrayList<SelectItem>();
		metodosAvaliacaoMedio.add(new SelectItem(MetodoAvaliacao.NOTA, "Nota"));
		metodosAvaliacaoMedio.add(new SelectItem(MetodoAvaliacao.COMPETENCIA, "Competência"));		
	}

	/** Construtor padrão. */
	public ParametrosGestoraAcademicaMBean() {
		initValores();
	}

	/** Inicializa os valores dos atributos. */
	private void initValores() {
		obj = new ParametrosGestoraAcademica();
		obj.setUnidade(new Unidade());
		obj.setModalidade(new ModalidadeEducacao());
		obj.setConvenio(new ConvenioAcademico());
		obj.setCurso(new Curso());
		comboCursos = new ArrayList<SelectItem>(0);
	}

	/**
	 * Inicia a configuração dos parâmetros do nível de ensino formação
	 * complementar.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/formacao_complementar/menus/curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarFormacaoComplementar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		initValores();
		
		UnidadeGeral unidadePapel = getUsuarioLogado().getPermissao(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR).iterator().next().getUnidadePapel();
		ParametrosGestoraAcademica param = getParametrosAcademicos();
		GenericDAO dao = getGenericDAO();
		
		if(!param.getNivel().equals(NivelEnsino.FORMACAO_COMPLEMENTAR) || param.getUnidade().getId() != unidadePapel.getId()){
			obj.setUnidade(dao.findByPrimaryKey(unidadePapel.getId(), Unidade.class));
			obj.setNivel(NivelEnsino.FORMACAO_COMPLEMENTAR);
		} else {
			obj = param;
			obj.setUnidade(dao.findByPrimaryKey(param.getUnidade().getId(), Unidade.class));
		}
		habilitarVoltar = false;
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return telaParametrosFormacaoComplementar();
	}
	
	/**
	 * Inicia a configuração dos parâmetros do nível de ensino técnico.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarTecnico() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_TECNICO);
		initValores();
		ParametrosGestoraAcademica param = getParametrosAcademicos();
		GenericDAO dao = getGenericDAO();
		if(!param.getNivel().equals(NivelEnsino.TECNICO) || param.getUnidade().getId() != getUsuarioLogado().getVinculoAtivo().getUnidade().getId()){
			obj.setUnidade(dao.findByPrimaryKey(getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), Unidade.class));
			obj.setNivel(NivelEnsino.TECNICO);
		} else {
			obj = param;
			obj.setUnidade(dao.findByPrimaryKey(param.getUnidade().getId(), Unidade.class));
		}
		habilitarVoltar = false;
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return telaParametrosTecnico();
	}
	
	/**
	 * Entrada do caso de uso com unidade e nível escolhido.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menus/permissao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarStrictoSensu() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_STRICTO);
		initValores();
		obj = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalStricto();
		habilitarVoltar = false;
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return telaValores();
	}

	/**
	 * Entrada do caso de uso com unidade e nível escolhido.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarDAE() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		initValores();
		obj = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao();
		habilitarVoltar = false;
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return telaValores();
	}
	
	/**
	 * Inicia a configuração dos parâmetros do nível de ensino técnico.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/medio/menus/curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarMedio() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MEDIO);
		initValores();
		initValoresMedio();
		ParametrosGestoraAcademica param = getParametrosAcademicos();
		GenericDAO dao = getGenericDAO();
		if(!param.getNivel().equals(NivelEnsino.MEDIO) || param.getUnidade().getId() != getUsuarioLogado().getVinculoAtivo().getUnidade().getId()){
			obj.setUnidade(dao.findByPrimaryKey(getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), Unidade.class));
			obj.setNivel(NivelEnsino.MEDIO);
		} else {
			obj = param;
			obj.setUnidade(dao.findByPrimaryKey(param.getUnidade().getId(), Unidade.class));
		}
		obj.setModalidade(null);
		obj.setConvenio(null);
		obj.setCurso(null);
		habilitarVoltar = false;
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return telaParametrosMedio();
	}

	/** Inicializa os valores dos atributos pertinentes ao nível médio. */
	private void initValoresMedio(){
		obj.setPercentualMaximoCumpridoTrancamento(new Float(0.0));
		obj.setMaxTrancamentos(new Integer(0));
		obj.setMaxTrancamentosMatricula(new Integer(0));
		obj.setQuantidadePeriodosRegulares(new Integer(0));
		obj.setMinCreditosExtra(new Integer(0));
		obj.setMaxCreditosExtra(new Integer(0));
	}
	
	/**
	 * Inicia a edição de parâmetros.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
		initValores();
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return telaFiltro();
	}

	/**
	 * Redireciona o usuário para o formulário de parâmetros filtrado por nível
	 * de ensino, unidade, etc. <br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 */
	public String telaFiltro() {		
		return forward("/administracao/parametros/filtro.jsp");
	}

	/** Redireciona o usuário para o formulário de edição dos valores para os parâmetros.
	 * de ensino, unidade, etc. <br/>
	 * Método não invocado por JSP´s.
	 * @return
	 * @throws DAOException 
	 */
	public String telaValores() throws DAOException {
		if(!ValidatorUtil.isEmpty(obj.getUnidade())) {
			Unidade unidade = getGenericDAO().findByPrimaryKey(obj.getUnidade().getId(), Unidade.class, "id", "nome");
			obj.setUnidade(unidade);
		}
		return forward("/administracao/parametros/valores.jsp");
	}
	
	/**
	 * Redireciona o usuário para o formulário de edição dos valores dos
	 * parâmetros para o nível de ensino formação complementar. <br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String telaParametrosFormacaoComplementar() throws DAOException {
		return forward("/administracao/parametros/parametros_formacao_complementar.jsp");
	}
	
	/**
	 * Redireciona o usuário para o formulário de edição dos valores dos
	 * parâmetros para o nível de ensino formação complementar. <br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String telaParametrosTecnico() throws DAOException {
		return forward("/administracao/parametros/parametros_tecnico.jsp");
	}
	
	/**
	 * Redireciona o usuário para o formulário de edição dos valores dos
	 * parâmetros para o nível de ensino médio. <br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String telaParametrosMedio() throws DAOException {
		return forward("/administracao/parametros/parametros_medio.jsp");
	}

	/**
	 * Redireciona o usuário para a visualização de valores de parâmetros.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/filtro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String verValores() throws DAOException  {
		if (obj.getUnidade().getId() == 0 && !NivelEnsino.isValido( obj.getNivel()) && obj.getModalidade().getId() == 0
				&& obj.getConvenio().getId() == 0 && obj.getCurso().getId() == 0)  {
			addMensagemErro("Preencha alguma das opções");
			return null;
		}

		ParametrosGestoraAcademicaDao dao = getDAO(ParametrosGestoraAcademicaDao.class);
		if (obj.getCurso().getId() > 0) {
			obj.setUnidade(null);
			obj.setModalidade(null);
			obj.setConvenio(null);
			obj.setNivel(' ');
			obj.setCurso(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));
		} else if (obj.getModalidade().getId() > 0 || obj.getConvenio().getId() > 0) {
			obj.setCurso(null);
			obj.setUnidade(dao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
			if (obj.getModalidade().getId() > 0) {
				obj.setConvenio(null);
				obj.setModalidade(dao.findByPrimaryKey(obj.getModalidade().getId(), ModalidadeEducacao.class));
			} else {
				obj.setModalidade(null);
				obj.setConvenio(dao.findByPrimaryKey(obj.getConvenio().getId(), ConvenioAcademico.class));
			}
		} else {
			obj.setModalidade(null);
			obj.setConvenio(null);
			obj.setCurso(null);
			obj.setUnidade(dao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
		}

		// carrega valores
		ParametrosGestoraAcademica params = dao.findByParametros(obj.getUnidade(), obj.getNivel(), obj.getCurso(), obj.getModalidade(), obj.getConvenio());

		if (params!= null && params.getId() > 0) {
			obj = params;
			setConfirmButton("ALTERAR PARÂMETROS");
		} else  {
			setConfirmButton("CADASTRAR PARÂMETROS");
		}
		return telaValores();
	}

	/**
	 * Cadastra/Altera os valores dos parâmetros.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/valores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String confirmar() {
		checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		if( hasErrors() ) {
			return null;
		}
		
		erros = obj.validate();
		
		if( hasErrors() ) {
			return null;
		}
		
		Comando comando = null;
		String msg = "Parâmetros Alterados com sucesso!";
		if (obj.getId() == 0) {
			comando = ArqListaComando.CADASTRAR;
			msg = "Parâmetros Criados com sucesso!";
		} else {
			comando = ArqListaComando.ALTERAR;
		}
		try {
			prepareMovimento(comando);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(comando);
			executeWithoutClosingSession(mov, (HttpServletRequest) FacesContext.getCurrentInstance()
					.getExternalContext().getRequest());
			addMessage(msg, TipoMensagemUFRN.INFORMATION);
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
			e.printStackTrace();
			return null;
		}
		return cancelar();
	}


	/** Retorna a lista de SelecItems com possíveis valores para métodos de Avaliação. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/valores.jsp</li>
	 * <li>/sigaa.war/administracao/parametros/parametros_formacao_complementar.jsp</li>
	 * <li>/sigaa.war/administracao/parametros/parametros_tecnico.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getMetodosAvaliacao() {
		return metodosAvaliacao;
	}
	
	/** Retorna a lista de SelecItems com possíveis valores para métodos de Avaliação do nível médio. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/parametros_medio.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getMetodosAvaliacaoMedio() {
		return metodosAvaliacaoMedio;
	}

	/** Carrega os possíveis cursos de acordo com a modalidade escolhida pelo usuário.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/valores.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarCursosPorModalidade(ValueChangeEvent e) throws DAOException {
		obj.setCurso(new Curso());
		obj.setConvenio(new ConvenioAcademico());
		comboCursos = new ArrayList<SelectItem>(0);
		if (e.getNewValue() == null) {
			return;
		}
		obj.setModalidade(new ModalidadeEducacao((Integer) e.getNewValue()));
		CursoDao dao = getDAO(CursoDao.class);
		comboCursos = toSelectItems(dao.findByModalidadeEducacao((Integer) e.getNewValue(), obj.getNivel()),
				"id", "descricao");
	}

	/** Carreta os possíveis cursos de acordo com o convênio escolhido pelo usuário.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/valores.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarCursosPorConvenio(ValueChangeEvent e) throws DAOException {
		if(!checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId()))
			return;
		obj.setCurso(new Curso());
		obj.setModalidade(new ModalidadeEducacao());
		comboCursos = new ArrayList<SelectItem>(0);
		if (e.getNewValue() == null) {
			return;
		}
		obj.setConvenio(new ConvenioAcademico((Integer) e.getNewValue()));
		CursoDao dao = getDAO(CursoDao.class);
		comboCursos = toSelectItems(dao.findByConvenioAcademico((Integer) e.getNewValue(), obj.getNivel()),
				"id", "descricao");
	}

	/** Listener responsável por redefinir o curso, modalidade, convênio e lista de cursos quando o usuário escolhe o nível de ensino.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/valores.jsp</li>
	 * </ul>
	 * @param e
	 */
	public void selecionarNivel(ValueChangeEvent e) {
		if (e.getNewValue() == null)
			addMensagemErro("Escolha um Nível de Ensino");
		else
			obj.setNivel(e.getNewValue().toString().charAt(0));

		obj.setCurso(new Curso());
		obj.setModalidade(new ModalidadeEducacao());
		obj.setConvenio(new ConvenioAcademico());
		comboCursos = new ArrayList<SelectItem>(0);
	}

	/** Retorna uma lista de SelectItem com os possíveis níveis de ensino.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/filtro.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getComboNiveis() {
		return Arrays.asList(NivelEnsino.getNiveisCombo());
	}

	/** Retorna uma lista de cursos que deverá ser escolhido para definir os parâmetros. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/parametros/filtro.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getComboCursos() {
		return comboCursos;
	}

	/** Seta uma lista de cursos que deverá ser escolhido para definir os parâmetros.
	 * <br />
	 * Método não invocado por JSPs.
	 * @param comboCursos
	 */
	public void setComboCursos(List<SelectItem> comboCursos) {
		this.comboCursos = comboCursos;
	}

	/** Indica se o botão "<< Voltar" está habilitado no formulário.
	 * @return
	 */
	public boolean isHabilitarVoltar() {
		return habilitarVoltar;
	}

	/** Seta se o botão "<< Voltar" está habilitado no formulário.
	 * @param habilitarVoltar
	 */
	public void setHabilitarVoltar(boolean habilitarVoltar) {
		this.habilitarVoltar = habilitarVoltar;
	}
	/**
	 * Indica se o método de avaliação é por nota.
	 * @return
	 */
	public boolean isNota() {
		return obj.getMetodoAvaliacao() != null && obj.getMetodoAvaliacao().intValue() == MetodoAvaliacao.NOTA; 
	}
	
	/**
	 * Indica se o método de avaliação é por conceito.
	 * @return
	 */
	public boolean isConceito() {
		return obj.getMetodoAvaliacao() != null && obj.getMetodoAvaliacao().intValue() == MetodoAvaliacao.CONCEITO; 
	}
	
	/**
	 * Indica se o método de avaliação é por competência.
	 * @return
	 */
	public boolean isCompetencia() {
		return obj.getMetodoAvaliacao() != null && obj.getMetodoAvaliacao().intValue() == MetodoAvaliacao.COMPETENCIA; 
	}
	
}
