package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.negocio.ListaDiscentesCalcular;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CoordenadorPoloIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CronogramaExecucaoAulasDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.LancamentoFreqEncontroDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.RelatoriosCoordenadorPoloIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CoordenadorPoloIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CronogramaExecucaoAulas;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.DadosTurmaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.tecnico.dao.RelatoriosTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * 
 * Entidade responsável pela manipulação dos relatórios do coordenador de pólo do IMD.
 * 
 * 
 * @author Rafael Barros
 *
 */

@Scope("request")
@Component("relatoriosCoordenadorPoloIMD")
public class RelatoriosCoordenadorPoloIMDMBean extends SigaaAbstractController {

	/** Constante de visualização da view do quantitativo dos alunos Tecnico */
	public final static String JSP_QUANT_ALUNOS_TECNICO = "/metropole_digital/relatorios/coordenador_polo/relatorio_listagem_alunos.jsp";
	
	/** Constante de visualização da view da listagem dos alunos por curso */
	public final static String JSP_LISTAGEM_ALUNOS_CURSO = "/metropole_digital/relatorios/coordenador_polo/alunos_curso.jsp";
	
	/** Constante de visualização da view de seleção dos abandonos  */
	public final static String JSP_SELECIONA_ABANDONO = "/metropole_digital/relatorios/seleciona_abandono.jsp";
	
	/** Coleção de objetos da classe Pólo que armazenará os pólos que possuem vínculo com o coordenador de pólo logado **/
	private Collection<Polo> listaPolos = new ArrayList<Polo>();

	/** List que armazena o resultado das consultas */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	/** Lista que armazenará a consulta geral de alunos do relatório selecionado */
	private List<DiscenteTecnico> listaGeralDiscentes = new ArrayList<DiscenteTecnico>();
	
	/** Objeto que armazenará o pólo selecionado no qual o relatório será gerado **/
	private Polo poloSelecionado = new Polo();
	
	/** ID do pólo selecionado no qual o relatório será gerado **/
	private Integer idPoloSelecionado = null;
	
	/** Entidade que corresponde ao curso que será utilizado para pesquisar as turmas **/
	private CursoTecnico curso;
	
	/** Entidade que corresponde ao modulo que será utilizado para pesquisar as turmas **/
	private Modulo modulo;
	
	/** Coleção de turmas de entrada do IMD */
	private List<TurmaEntradaTecnico> listaTurmas = new ArrayList<TurmaEntradaTecnico>();
	
	/** Coleção de selectItem de cursos do Instituto Metrópole Digital. */
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>(0);
	
	/** Coleção de selectItem de módulos */
	private List<SelectItem> modulosCombo = new ArrayList<SelectItem>(0);
	
	/** ID de uma turma selecionada para o relatorios */
	private int idTurmaEntradaSelecionada;

	/** Entidade turma selecionada para o relatorios */
	private TurmaEntradaTecnico turmaEntradaSelecionada = new TurmaEntradaTecnico();
	
	/** Representa os discentes que pertencem a turma de entrada selecionada para  o relatorio de lista de discentes */
	private Collection<DiscenteTecnico> discentesTurma = new ArrayList<DiscenteTecnico>();
	
	/** Representa a quantidade de registros exibidos no relatorio de lista de discentes **/
	private int qtdDiscentes;
	
	
	public Collection<Polo> getListaPolos() {
		return listaPolos;
	}

	
	public void setListaPolos(Collection<Polo> listaPolos) {
		this.listaPolos = listaPolos;
	}
	

	public List<Map<String, Object>> getLista() {
		return lista;
	}


	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}


	public Polo getPoloSelecionado() {
		return poloSelecionado;
	}


	public void setPoloSelecionado(Polo poloSelecionado) {
		this.poloSelecionado = poloSelecionado;
	}
	


	public Integer getIdPoloSelecionado() {
		return idPoloSelecionado;
	}


	public void setIdPoloSelecionado(Integer idPoloSelecionado) {
		this.idPoloSelecionado = idPoloSelecionado;
	}
	

	public CursoTecnico getCurso() {
		return curso;
	}


	public void setCurso(CursoTecnico curso) {
		this.curso = curso;
	}


	public Modulo getModulo() {
		return modulo;
	}


	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}


	public List<TurmaEntradaTecnico> getListaTurmas() {
		return listaTurmas;
	}


	public void setListaTurmas(List<TurmaEntradaTecnico> listaTurmas) {
		this.listaTurmas = listaTurmas;
	}


	public List<SelectItem> getCursosCombo() {
		return cursosCombo;
	}


	public void setCursosCombo(List<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}


	public List<SelectItem> getModulosCombo() {
		return modulosCombo;
	}


	public void setModulosCombo(List<SelectItem> modulosCombo) {
		this.modulosCombo = modulosCombo;
	}


	public int getIdTurmaEntradaSelecionada() {
		return idTurmaEntradaSelecionada;
	}


	public void setIdTurmaEntradaSelecionada(int idTurmaEntradaSelecionada) {
		this.idTurmaEntradaSelecionada = idTurmaEntradaSelecionada;
	}


	public TurmaEntradaTecnico getTurmaEntradaSelecionada() {
		return turmaEntradaSelecionada;
	}


	public void setTurmaEntradaSelecionada(TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}


	public Collection<DiscenteTecnico> getDiscentesTurma() {
		return discentesTurma;
	}


	public void setDiscentesTurma(Collection<DiscenteTecnico> discentesTurma) {
		this.discentesTurma = discentesTurma;
	}


	public int getQtdDiscentes() {
		return qtdDiscentes;
	}


	public void setQtdDiscentes(int qtdDiscentes) {
		this.qtdDiscentes = qtdDiscentes;
	}

	
	public List<DiscenteTecnico> getListaGeralDiscentes() {
		return listaGeralDiscentes;
	}


	public void setListaGeralDiscentes(List<DiscenteTecnico> listaGeralDiscentes) {
		this.listaGeralDiscentes = listaGeralDiscentes;
	}


	public RelatoriosCoordenadorPoloIMDMBean() throws DAOException, ArqException {
		obj = new CoordenadorPoloIMD();

		curso = new CursoTecnico();
		modulo = new Modulo();
		
		qtdDiscentes = 0;
		idTurmaEntradaSelecionada = 0;

		Unidade imd = getGenericDAO().findByPrimaryKey(ParametroHelper.getInstance().getParametroInt(
		ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL), Unidade.class);
		
		//cursosCombo = toSelectItems(cursoDao.findByNivel(getNivelEnsino(), true, null, new Unidade(getUnidadeGestora())), "id", "descricao");
		cursosCombo = toSelectItems(getDAO(CursoDao.class).findByNivel(getNivelEnsino(), true, null, imd), "id", "descricao");
	}
	
	
	/**
	 * Lista as turmas cadastradas de acordo com os critérios de pesquisa
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/menus/coordenador_polo.jsp</li>
	 *	</ul>
	 *
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException
	 */
	public String listarTurmas() throws DAOException {
		
		CoordenadorPoloIMDDao coordDao = new CoordenadorPoloIMDDao();
		
		try {
			if(listaPolos.isEmpty()){
				listaPolos = coordDao.findPolosByCoordenador(getUsuarioLogado().getPessoa().getId());
			}
			
			if (modulo.getId() != 0) {
				listaTurmas = getDAO(TutoriaIMDDao.class).findTurmasByModuloAndPolos(modulo.getId(), listaPolos);
				if (listaTurmas.isEmpty()) {
					addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
				}
			} else {
				if (curso.getId() > 0) {
					listaTurmas = getDAO(TutoriaIMDDao.class).findTurmasByCursoAndPolos(curso.getId(), listaPolos);
					if (listaTurmas.isEmpty()) {
						addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
					}
				} else {
					addMensagem(
							MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
							"Curso");
				}
			}
			//Collections.sort(listaTurmas);
			return null;
		}
		finally {
			coordDao.close();
		}
	}
	
	
	/**
	 * Efetua o preenchimento dos dados do relatorio e faz o redirecionamento da
	 * pagina
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/menus/coordenador_polo.jsp</li>
	 *	</ul>
	 *
	 * @param
	 * @return página na qual a JSP sera redirecionada
	 * @throws DAOException
	 */
	public String gerarRelatorioListaDiscentes() throws DAOException {
		int id = getParameterInt("id");

		if (id > 0) {
			idTurmaEntradaSelecionada = id;
		}

		turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(idTurmaEntradaSelecionada, TurmaEntradaTecnico.class);
		findDiscentesTurma();
		
		if(qtdDiscentes > 0) {
			return forward("/metropole_digital/relatorios/coordenador_polo/relatorio_discentes_turma.jsp");
		} else {
			addMensagemErro("Nenhum discente encontrado.");
			return null;
		}
		
		
	}
	
	
	/**
	 * Efetua o preenchimento dos dados dos discentes da turma selecionada
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/menus/coordenador_polo.jsp</li>
	 *	</ul>
	 *
	 * @param
	 * @return
	 * @throws DAOException
	 */
	public void findDiscentesTurma() throws DAOException {

		LancamentoFreqEncontroDao lancamentoFreqDao = new LancamentoFreqEncontroDao();

		try {
			discentesTurma = lancamentoFreqDao
					.findDiscentesByTurmaEntrada(idTurmaEntradaSelecionada);
			qtdDiscentes = discentesTurma.size();
		} finally {
			lancamentoFreqDao.close();
		}
	}
	
	
	// AJAX:
	/**
	 * Carrega os módulos do curso escolhido
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/menus/coordenador_polo.jsp</li>
	 *	</ul>
	 *
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public void carregarModulos(ValueChangeEvent e) throws DAOException {
		if ((Integer) e.getNewValue() == 0) {
			modulosCombo = Collections.emptyList();
		} else {
			curso.setId((Integer) e.getNewValue());
			modulosCombo = toSelectItems(
					getDAO(CronogramaExecucaoAulasDao.class)
							.findByCursoTecnico((Integer) e.getNewValue()),
					"id", "descricao");
		}
		
	}
	
	
	/**
	 * Redireciona o usuário para a pagina listagem das turmas IMD
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/menus/coordenador_polo.jsp</li>
	 *	</ul>
	 *
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws
	 */
	public String selecionarTurma() {
		return forward("/metropole_digital/relatorios/coordenador_polo/selecao_turma.jsf");
	}
	

	/**
	 * Gera um Relatório quantitativo dos alunos reprovados por nota e falta, agrupando os mesmo por curso
	 * tendo com base os dados informados na busca_relatorio.jsp 
	 * <br>
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/menus/coordenador_polo.jsp</li>
	 *	</ul>
	 *
	 *
	 * @return
	 * @throws DAOException, ArqException
	 */
	public String gerarListagemTodosAlunos() throws DAOException, ArqException {
		
		CoordenadorPoloIMDDao coordDao = new CoordenadorPoloIMDDao();
		try {
		
			if(listaPolos.isEmpty()){
				listaPolos = coordDao.findPolosByCoordenador(getUsuarioLogado().getPessoa().getId());
			}
			
			if(listaPolos.size() > 1) {
				RelatoriosCoordenadorPoloIMDDao dao = getDAO(RelatoriosCoordenadorPoloIMDDao.class);
				listaGeralDiscentes = (ArrayList<DiscenteTecnico>) dao.findListaGeralAlunos(listaPolos);
				return forward(JSP_QUANT_ALUNOS_TECNICO);
			} else {
				addMensagemErro("Nenhum discente encontrado.");
				return null;
			}
			
		} finally {
			coordDao.close();
		}
		
		
	}
	
	
	/**
	 * Lista todos os discentes cadastrados em uma determinada unidade.
	 * <br>
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/menus/coordenador_polo.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarListagemAlunosCadastrados() throws DAOException, ArqException{
		
		CoordenadorPoloIMDDao coordDao = new CoordenadorPoloIMDDao();
		try {
		
			if(listaPolos.isEmpty()){
				listaPolos = coordDao.findPolosByCoordenador(getUsuarioLogado().getPessoa().getId());
			}
			
			if(listaPolos.size() > 1) {
				RelatoriosCoordenadorPoloIMDDao dao = getDAO(RelatoriosCoordenadorPoloIMDDao.class);
				listaGeralDiscentes = (ArrayList<DiscenteTecnico>)dao.findListaGeralAlunosCadastrados(listaPolos);
				return forward(JSP_LISTAGEM_ALUNOS_CURSO);
			} else {
				addMensagemErro("Nenhum discente encontrado.");
				return null;
			}
			
		} finally {
			coordDao.close();
		}
	}
	
}
