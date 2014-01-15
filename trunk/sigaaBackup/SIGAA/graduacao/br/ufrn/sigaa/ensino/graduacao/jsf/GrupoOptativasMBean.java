/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 03/06/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.GrupoOptativasDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Managed bean para cadastro de grupos de componentes curriculares
 * optativos.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("session")
public class GrupoOptativasMBean extends SigaaAbstractController<GrupoOptativas> {

	/** Curso selecionado para buscar o currículo que será associado ao grupo de optativas */
	private Curso curso;
	
	/** Matriz curricular selecionada para buscar o currículo que será associado ao grupo de optativas */
	private MatrizCurricular matriz;
	
	/** Currículo a ser associado com o grupo de optativas */
	private Curriculo curriculo;
	
	/** Grupos de optativas encontrados para o currículo selecionado */
	private List<GrupoOptativas> grupos;
	
	/** Componentes optativos do currículo selecionado que fazem parte do grupo de optativas */
	private List<CurriculoComponente> componentesEscolhidos;
	
	/** Lista de todos os componentes optativos do currículo selecionado */
	private List<SelectItem> componentesOptativos;

	/** Lista de componentes obrigatórios do currículo que podem ser utilizados como componentes associados ao grupo de optativas */
	private List<ComponenteCurricular> componentes;
	
	/**
	 * Instancia os objetos e redireciona o usuário para a tela de seleção do currículo.
	 * <br />
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/graduacao/menus/cdp.jsp</li>
	 *   </ul>
	 */
	public void iniciar() {
		reset();
		telaSelecaoCurriculo();
	}
	
	/**
	 * Redireciona o usuário para a tela de seleção do currículo.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/graduacao/curriculo_optativos/grupos.jsp</li>
	 *   </ul>
	 */
	public void telaSelecaoCurriculo() {
		forward("/graduacao/curriculo_optativos/selecao_curriculo.jsp");
	}
	
	/**
	 * Redireciona o usuário para a listagem de grupos de optativas de um currículo.
	 * <br />
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/graduacao/curriculo_optativos/selecao_curriculo.jsp</li>
	 *   </ul>
	 * 
	 * @throws ArqException
	 */
	public void telaGrupos() throws ArqException {
		prepareMovimento(ArqListaComando.ALTERAR);
		forward(getListPage());
	}
	
	/**
	 * Redireciona o usuário para o formulário de criação de um novo grupos de optativas.
	 * Método não chamado por JSP.
	 */
	public void telaNovoGrupo() {
		forward(getFormPage());
	}
	
	/**
	 * Instancia os objetos e redireciona o usuário para o formulário de criação de um novo grupos de optativas.
	 * <br />
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/graduacao/curriculo_optativos/grupos.jsp</li>
	 *   </ul>
	 * @throws ArqException
	 */
	public void novoGrupo() throws ArqException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		obj = new GrupoOptativas();
		obj.setComponente(new ComponenteCurricular());
		componentesEscolhidos = null;
		componentes = null;
		carregarComponentesOptativos();
		telaNovoGrupo();
	}

	/**
	 * Carrega todos os componentes optativos de um currículo para a seleção dos componentes que 
	 * farão parte do grupo de optativas.
	 * @throws DAOException
	 */
	private void carregarComponentesOptativos() throws DAOException {
		componentesOptativos = toSelectItems(getDAO(ComponenteCurricularDao.class).findOptativasByCurriculo(curriculo), "descricao");
	}

	@Override
	public void afterAtualizar() throws ArqException {
		componentesEscolhidos = obj.getComponentes();
		carregarComponentesOptativos();
		componentes = null;
		if (obj.getComponente() == null)
			obj.setComponente(new ComponenteCurricular());
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		obj.setComponentes(componentesEscolhidos);
		obj.setCurriculo(curriculo);
		obj.calculaChTotal();
		
		if (!isEmpty(componentesEscolhidos)) {
			List<Map<String, Object>> interseccao = getDAO(GrupoOptativasDao.class).findComponentePertencemOutroGrupo(obj, curriculo, componentesEscolhidos);
			
			if (!isEmpty(interseccao)) {
				String msg = "Não é possível cadastrar o grupo de optativas porque há conflito com outros grupos: <br/>";
				for (Map<String, Object> conflito : interseccao) {
					msg += "- O componente " + conflito.get("nome") + " já existe no grupo " + conflito.get("descricao") + ".<br/>";
				}
				addMensagemErro(msg);
				return null;
			}
		}
		
		if( obj.getComponente() != null && obj.getComponente().getId() == 0 )
			obj.setComponente(null);
		
		super.cadastrar();		
		
		if( obj.getComponente() == null )
			obj.setComponente( new ComponenteCurricular() );
		
		return null;
	}
	
	/**
	 * Seleciona um currículo e carrega os grupos de optativas do mesmo.
	 * <br />
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/graduacao/curriculo_optativos/selecao_curriculo.jsp</li>
	 *   </ul>
	 */
	public void selecionarCurriculo() throws ArqException {
		if (isEmpty(curriculo)) {
			addMensagemErro("Escolha um currículo válido");
			return;
		}
		
		curriculo = getGenericDAO().findByPrimaryKey(curriculo.getId(), Curriculo.class);
		carregaGrupos();
		telaGrupos();
	}

	/**
	 * Seleciona um currículo e carrega os grupos de optativas do mesmo.
	 */
	private void carregaGrupos() throws DAOException {
		grupos = (List<GrupoOptativas>) getGenericDAO().findByExactField(GrupoOptativas.class, "curriculo.id", curriculo.getId(), "asc", "descricao");
	}
	
	@Override
	public void afterCadastrar() throws ArqException {
		carregaGrupos();
	}

	/**
	 * Limpa os dados informados nos objetos do managed bean para uma próxima execução da operação.
	 */
	private void reset() {
		obj = new GrupoOptativas();
		obj.setComponente(new ComponenteCurricular());
		curso = new Curso();
		matriz = new MatrizCurricular();
		curriculo = new Curriculo();
		componentesOptativos = null;
		componentesEscolhidos = null;
		componentes = null;
	}
	
	/**
	 * Retorna a lista de componentes obrigatórios do currículo para a seleção
	 * do componente equivalente ao grupo de optativas.
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getComponentes() throws DAOException {
		if (componentes == null)
			componentes = getDAO(GrupoOptativasDao.class).findComponentesObrigatoriosByCurriculo(curriculo);
		return toSelectItems(componentes, "id", "codigoNome");
	}
	
	/**
	 * Retorna a lista de matrizes curriculares de um curso.
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getMatrizesCurriculares() throws DAOException {
		List<SelectItem> matrizesCurriculares = new ArrayList<SelectItem>();
		matrizesCurriculares.add(new SelectItem("0", "--> SELECIONE <--"));
		if (curso.getId() > 0) {
			matrizesCurriculares.addAll(toSelectItems(getDAO(MatrizCurricularDao.class).findAtivasByCurso(curso.getId()), "id", "descricaoMin"));
		}
		
		return matrizesCurriculares;
	}
	
	/**
	 * Retorna a lista de currículos de uma matriz curricular.
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getCurriculos() throws DAOException {
		List<SelectItem> curriculos = new ArrayList<SelectItem>();
		curriculos.add(new SelectItem("0", "--> SELECIONE <--"));
		if (matriz.getId() > 0) {
			Collection<Curriculo> col = getDAO(EstruturaCurricularDao.class).findByMatriz(matriz.getId());
			if (!isEmpty(col))
				curriculos.addAll(toSelectItems(col, "id", "descricao"));
		}	
		
		return curriculos;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public MatrizCurricular getMatriz() {
		return matriz;
	}

	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	public Curriculo getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	public List<GrupoOptativas> getGrupos() {
		return grupos;
	}

	public List<SelectItem> getComponentesOptativos() {
		return componentesOptativos;
	}

	public List<CurriculoComponente> getComponentesEscolhidos() {
		return componentesEscolhidos;
	}

	public void setComponentesEscolhidos(List<CurriculoComponente> componentesEscolhidos) {
		this.componentesEscolhidos = componentesEscolhidos;
	}
	
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	public String getFormPage() {
		return "/graduacao/curriculo_optativos/novo_grupo.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/graduacao/curriculo_optativos/grupos.jsp";
	}
	
	@Override
	public String remover() throws ArqException {

		prepareMovimento(ArqListaComando.REMOVER);
		
		GenericDAO dao = getGenericDAO();
		
		int id = getParameterInt("id");		
		obj = dao.findByPrimaryKey(id, GrupoOptativas.class);
		dao.detach(obj);
		
		super.remover();	
		return null;
	}
	
	@Override
	public void afterRemover() {
		try {
			carregaGrupos();
		} catch (DAOException e) {
			addMensagemErro("Erro ao carregar grupo de optativas");
			e.printStackTrace();
			notifyError(e);
			return;
		}
	}
	
	/**
	 * Redireciona para a tela de visualização de um grupo de optativas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/graduacao/curriculo_optativos/grupos.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException
	 */
	public String visualizar() throws DAOException {
		int id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id, GrupoOptativas.class);
		return forward("/graduacao/curriculo_optativos/info_grupo.jsp");
	}
	
}
