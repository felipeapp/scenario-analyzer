/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/06/2011
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.negocio.dominio.ComponenteCurricularMov;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;

/**
 * MBean responsável pelas operações com Componente Curricular do nível médio.
 * 
 * @author Arlindo
 */
@Component("disciplinaMedioMBean") @Scope("request")
public class DisciplinaMedioMBean extends SigaaAbstractController<ComponenteCurricular> {
	
	/** Lista de componentes curriculares encontrados na busca por componentes curriculares. */
	private Collection<ComponenteCurricular> disciplinas = new HashSet<ComponenteCurricular>();
	
	/** Indica se será filtrado pelo código da disciplina */
	private boolean filtroCodigo;
	/** Indica se será filtrado pelo nome da disciplina */
	private boolean filtroNome;
	/** Código informado na busca */
	private String codigo;
	/** Nome informado na busca */
	private String nome;
	
	/** Expressão que define a equivalência do componente curricular. */
	private String equivalenciaForm;	
	
	/** Comando atual */
	private Comando comando;
	
	/** Construtor padrão. 
	 * @throws NegocioException */
	public DisciplinaMedioMBean()  {
		initObj();
	}

	/**
	 * Inicializa os campos do objeto que representa um componente para ser
	 * manipulado durante as operações.
	 */
	private void initObj() {
		obj = new ComponenteCurricular();		
		obj.setPrograma(null);
		obj.setComponentesCursoLato(null);
		obj.setCurso(new Curso());
		obj.setDetalhes(new ComponenteDetalhes());	
		obj.setTipoComponente(new TipoComponenteCurricular(TipoComponenteCurricular.DISCIPLINA));
		obj.setFormaParticipacao(null);
		obj.setSubUnidades(null);
		obj.setNivel(NivelEnsino.MEDIO);		
	}
	
	/**
	 * Inicia o cadastro de disciplinas do nível médio
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		
		GenericDAO dao = getGenericDAO();
		try {
			obj.setTipoComponente(dao.refresh(obj.getTipoComponente()));			
			obj.setUnidade(dao.findByPrimaryKey(getUnidadeGestora(), Unidade.class, "id", "nome"));
			obj.setModalidadeEducacao(new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL));
		} finally {
			if (dao != null)
				dao.close();
		}
		comando = SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR;
		prepareMovimento(comando);
		setConfirmButton("Cadastrar");
		return telaCadastro();
	}
	
	/** 
	 * Inicia o fomulário de cadastro
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/resumo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaCadastro() throws ArqException{
		return forward(getFormPage());		
	}
	
	/**
	 * Redireciona para a tela de resumo 
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String submeter() throws DAOException{
		
		// valida formulário
		erros = new ListaMensagens();
		ValidatorUtil.validateRequired(obj.getCurso(), "Curso", erros);
		erros.addAll(obj.validate().getMensagens());
		
		if (hasErrors()) 
			return null;	
		
		return forward(getViewPage());	
	}
	
	/**
	 * Preenche os campos de expressões de equivalência com 
	 * os códigos dos componentes das expressões.
	 * @param erros
	 * @throws DAOException
	 */
	private void preencherExpressoes(ListaMensagens erros) throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);		
		try {
			ArvoreExpressao.fromExpressao(equivalenciaForm);
			ExpressaoUtil.buildExpressaoToDB(getObj(), dao, true);
		} catch (Exception e) {
			addMensagem(MensagensGraduacao.EXPRESSAO_EQUIVALENCIA_MAL_FORMADA);
		}				
	}	
	
	/**
	 * Inicia a alteração dos dados da disciplina selecionada
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String alterar() throws ArqException {
		
		loadObj();	
		
		if (hasErrors())
			return null;
		
		if (!StringUtils.isEmpty(obj.getEquivalencia())) {
			ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
			try{				
				String expE = ExpressaoUtil.buildExpressaoFromDB(obj.getEquivalencia(), dao, false);
				obj.setEquivalencia(expE);
				
				equivalenciaForm = obj.getEquivalencia() != null ? obj.getEquivalencia() : "";				
			} catch (Exception e) {
				addMensagemWarning("Equivalência mal formado.");
			} finally {
				if (dao != null)
					dao.close();
			}
		}		
		
		comando = SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR;
		prepareMovimento(comando);
		setConfirmButton("Alterar");
		return telaCadastro();
	}
	
	/**
	 * Remove a disciplina selecionada
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String remove() throws ArqException, NegocioException {
		
		loadObj();	
		
		if (hasErrors())
			return null;
		
		comando = SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR;
		prepareMovimento(comando);
		
		return cadastrar();
	}	

	/**
	 * Carrega o objeto selecionado
	 * @throws DAOException
	 */
	private void loadObj() throws DAOException {
		populateObj();
		
		if (ValidatorUtil.isEmpty(obj))
			addMensagemErro("Disciplina não selecionada!");
		
		if (ValidatorUtil.isEmpty(obj.getCurso()))
			obj.setCurso(new Curso());		
		else
			obj.setCurso(getGenericDAO().refresh(obj.getCurso()));
	}
	
	/**
	 * Cadastra a disciplina 
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/resumo.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {		
		
		// valida formulário
		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());	
		
		// Transformar expressões informadas para o formato de armazenamento
		obj.setEquivalencia(equivalenciaForm);
		if (!ValidatorUtil.isEmpty(obj.getEquivalencia())) 
			preencherExpressoes(erros);
		
		if (hasErrors()) 
			return null;		
		
		try {
			
			obj.setSubUnidades(null);
			
			ComponenteCurricularMov mov = new ComponenteCurricularMov();
			mov.setProcessarExpressoes(false);
			mov.setCodMovimento(comando);
			mov.setObjMovimentado(obj);
			
			execute(mov);
			
			if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR))
				addMessage("Disciplina cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
			else if (getUltimoComando().equals(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR)){ 
				addMessage("Disciplina alterada com sucesso!", TipoMensagemUFRN.INFORMATION);
				return buscar();
			} else if (getUltimoComando().equals(SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR)){ 
				addMessage("Disciplina removida com sucesso!", TipoMensagemUFRN.INFORMATION);
				return buscar();
			}

		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			return null;
		} 		
		return cancelar();		
	}
	
	/**
	 * Inicia a busca de disciplinas do nível médio
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/curso.jsp</li>
	 * </ul>
	 */
	public String listar(){
		return forward(getListPage());
	}
	
	/**
	 *  Realiza buscas de disciplinas através dos parâmetros informados.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/lista.jsp</li>
	 * </ul>
	 */
	public String buscar() throws ArqException {
		
		if (!isFiltroCodigo() && !isFiltroNome())
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		
		if (isFiltroCodigo() && ValidatorUtil.isEmpty(codigo))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Código");
		
		if (isFiltroNome() && ValidatorUtil.isEmpty(nome))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");		

		if (hasErrors())
			return null;
		
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		try {
			disciplinas = dao.findCompleto((isFiltroCodigo() ? codigo : null), (isFiltroNome() ? nome : null), 
					null, null, null, null, null, null, null, null, 
					NivelEnsino.MEDIO, false, false, false, TipoComponenteCurricular.DISCIPLINA);

			if (ValidatorUtil.isEmpty(disciplinas)) 
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			
		} finally {
			if (dao != null)
				dao.close();
		}

		return forward(getListPage());
	}	
	
	/**
	 * Visualiza os dados da disciplina selecionada.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {

		loadObj();	
		
		if (hasErrors())
			return null;
		
		comando = null;		
		return forward(getViewPage());
	}
	
	/**
	 * Retorna os possíveis números máximos de avaliações que a disciplina pode ter.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNumUnidadesPossiveis() throws DAOException {
		ArrayList<SelectItem> selects = new ArrayList<SelectItem>(0);
		ParametrosGestoraAcademica params = getParametrosAcademicos();
		for (int i = params.getQtdAvaliacoes(); i > 0; i--) {
			selects.add(new SelectItem(i + "", i + ""));
		}
		return selects;
	}	
	
	/**
	 * Retorna o cálculo da carga horária total da disciplina via ajax.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public void calcularCHTotal(ActionEvent e){
		obj.getDetalhes().calcularCHTotal();
	}
	
	/**
	 * Caminho do form de cadastro
	 */
	@Override
	public String getFormPage() {
		return "/medio/disciplina/form.jsp";
	}
	
	/**
	 * Caminho da listagem
	 */
	@Override
	public String getListPage() {
		return "/medio/disciplina/lista.jsp";
	}
	
	/** Caminho da tela de resumo */
	@Override
	public String getViewPage() {
		return "/medio/disciplina/resumo.jsp";
	}
	
	/**
	 * Verifica se exibe o botão de cadastrar na tela de resumo
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/resumo.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isExibeBotaoCadastrar(){
		return (comando != null && 
				(comando.equals(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR) || 
						comando.equals(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR)));
	}
	
	/**
	 * Verifica se o usuário pode alterar/remover a disciplina
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/medio/disciplina/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPodeAlterar() {
		return isUserInRole(SigaaPapeis.GESTOR_MEDIO);
	}

	public Collection<ComponenteCurricular> getDisciplinas() {
		return disciplinas;
	}

	public void setDisciplinas(Collection<ComponenteCurricular> disciplinas) {
		this.disciplinas = disciplinas;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEquivalenciaForm() {
		return equivalenciaForm;
	}

	public void setEquivalenciaForm(String equivalenciaForm) {
		this.equivalenciaForm = equivalenciaForm;
	}	
}
