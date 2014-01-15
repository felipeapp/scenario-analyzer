/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 26/08/2008 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.jsf.EstruturaCurricularMBean;
import br.ufrn.sigaa.ensino.stricto.dao.RecomendacaoDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.Recomendacao;
import br.ufrn.sigaa.ensino.tecnico.dao.EstruturaCurricularTecDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * MBean utilizado nas consultas publicas dos cursos cadastrados
 * 
 * @author Ricardo Wendell
 * @author Edson Anibal (ambar@info.ufrn.br)
 * 
 */
@Component(value = "consultaPublicaCursos")
@Scope("request")
public class ConsultaPublicaCursosMBean extends SigaaAbstractController<Curso> {

	/** Indica se ser� filtrado pelo nome do curso */
	private boolean filtroNome;
	/** Indica se ser� filtrado pela modalidade */
	private boolean filtroModalidade;
	/** Indica que ser� filtrado pelo n�vel */
	private boolean filtroNivel;

	/**
	 * Diz se o usu�rio clicou no Link cursos na jsp do programa de
	 * P�s-gradua��o (programa.jsp), para que a jsp somente exiba os cursos.
	 */
	private boolean clicouEmCursos;
	/**
	 * Diz se o usu�rio clicou no Link P�gina inicial na jsp do programa de
	 * P�s-gradua��o (programa.jsp), para que a jsp somente exiba o conte�do da
	 * p�gina inicial.
	 */
	private boolean clicouEmPaginaInicial;
	/**
	 * Diz se o usu�rio clicou em um dos cursos listados na jsp do programa de
	 * P�s-gradua��o (programa.jsp), para que a jsp somente exiba os detalhes do
	 * curso.
	 */
	private boolean clicouEmDetalharCurso;

	/**
	 * Guarda o programa de stricto que o usu�rio est� visualizando no momento,
	 * na jsp que detalha o programa.
	 */
	private Unidade programa;
	/**
	 * Guarda as linhas de pesquisa do programa de stricto que o usu�rio est�
	 * visualizando no momento, na jsp que detalha o programa.
	 */
	private Collection<LinhaPesquisaStricto> linhasDePesquisa;

	/** Guarda os cursos que ser�o listados na JSP */
	public Collection<Curso> cursos;

	/** Guarda o curso que est� sendo detalhado na JSP do programa de p�s */
	public Curso curso;

	/**
	 * Guarda os curr�culos de um determinado curso que est� sendo detalhado na
	 * JSP do programa de p�s
	 */
	public Collection<Curriculo> curriculos;

	/** Lista de Curr�culos */
	private Set<EstruturaCurricularTecnica> curriculosTecnico;
	/** Estrutura curricular t�cnica selecionada */
	private EstruturaCurricularTecnica estruturaCurricularTecnica;
	/** Oferta de Est�gio Selecionada */
	private Recomendacao recomendacao;
	//--------------------------------------------------------------------------
	// ---------------//

	/**
	 * M�todo Construtor
	 * 
	 * @throws DAOException
	 */
	public ConsultaPublicaCursosMBean() throws DAOException {
		this.obj = new Curso();
		obj.setModalidadeEducacao(new ModalidadeEducacao());
		obj.setNivel(getParameterChar("nivel"));
		recomendacao = new Recomendacao();

		// Se estiver na p�gina publica do programa de P�s-gradua��o
		if (getParameter("idPrograma") != null)
			setaPrograma();
	}

	/**
	 * M�todo utilizado na consulta p�blica de cursos
	 * <br>JSP: /SIGAA/app/sigaa.ear/sigaa.war/public/curso/resumo_curriculo_tecnico.jsp
	 */
	public String consultar() throws DAOException {
		buscarCursos();
		if (cursos.isEmpty())
			addMensagemWarning("N�o foram encontrados cursos cadastrados de acordo com os crit�rios de consulta informados");
		return forward("/public/curso/lista.jsp");
	}

	/**
	 * realiza a consulta dos cursos e popula
	 * <br>JSP: N�o � chamado na JSP.
	 * @throws DAOException
	 */
	public void buscarCursos() throws DAOException {
		
		CursoDao cursoDao = getDAO(CursoDao.class);
		cursos = cursoDao.findConsultaPublica(programa!=null?programa.getId():null ,obj.getNivel(), filtroNome ? obj
				.getNome() : null, filtroModalidade ? obj
				.getModalidadeEducacao() : null);
	
	}

	/**
	 * Detalha o curso selecionado na JSP do programa de P�s
	 * <br>JSP: /SIGAA/app/sigaa.ear/sigaa.war/public/stricto/portal/programa.jsp
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @return
	 * @throws DAOException
	 */
	public String detalharCurso() throws DAOException {
		clicouEmDetalharCurso = true;

		int idCurso = getParameterInt("idCurso");
		CursoDao cursoDao = getDAO(CursoDao.class);
		curso = cursoDao.findByPrimaryKey(idCurso, Curso.class);

		EstruturaCurricularDao curriculoDao = getDAO(EstruturaCurricularDao.class);
		curriculos = curriculoDao.findByCurso(curso.getId(), curso.getNivel());

		return redirectMesmaPagina();
	}

	/**
	 * Consultar detalhes de curso
	 * <br>
	 * JSPs: <ul><li>/SIGAA/app/sigaa.ear/sigaa.war/public/curso/resumo_curso.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/public/programa/cursos.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/public/stricto/portal/programa.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String detalhes() {

		try {
			if (obj == null) // Evita nullPoint na JSP de listagem publica de
				// Stricto Sensu
				obj = new Curso();

			// Popular dados do curso
			//populateObj(true);
			
			obj = getGenericDAO().findByPrimaryKey(getParameterInt("id"), Curso.class);			
	
			Collection<CoordenacaoCurso> coordenacoes = 
				getDAO(CoordenacaoCursoDao.class).findCoordViceByCursoNivel(
						(NivelEnsino.isAlgumNivelStricto(obj.getNivel()) ? obj.getUnidade().getId():
							obj.getId()), obj.getNivel());
				Set<CoordenacaoCurso> coords = new HashSet<CoordenacaoCurso>();

			for (CoordenacaoCurso coordenacaoCurso : coordenacoes) {
				coords.add(coordenacaoCurso);
			}
			obj.setCoordenacoesCursos(coords);
			
			obj.getCoordenacoesCursos();
			
			//Popula coordena��o atual
			CoordenacaoCurso coordenacaoAtual = getDAO(CoordenacaoCursoDao.class).findUltimaByCurso(obj);
			obj.setCoordenacaoAtual(coordenacaoAtual);
			
			// Popular curr�culos
			EstruturaCurricularDao curriculoDao = getDAO(EstruturaCurricularDao.class);
			curriculos = curriculoDao.findByCurso(obj.getId(), obj.getNivel());

			if (isTecnico()) {
				curriculosTecnico = ((CursoTecnico) obj).getEstruturasCurriculares();
			}
			
			if( isStricto() ){
				recomendacao = getDAO(RecomendacaoDAO.class).findbyCurso(obj.getId());
			}
		} catch (DAOException e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErro("Um erro foi encontrado na consulta das informa��es deste curso. "
					+ "Os administradores do sistema j� foram notificados do problema e em breve ele ser� solucionado.");
			return null;
		}

		return forward("/public/curso/resumo_curso.jsp");
	}

	public Set<EstruturaCurricularTecnica> getCurriculosTecnico() {
		return curriculosTecnico;
	}

	public void setCurriculosTecnico(
			Set<EstruturaCurricularTecnica> curriculosTecnico) {
		this.curriculosTecnico = curriculosTecnico;
	}

	/**
	 * Exibe os Detalhes do Curr�culo.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/public/curso/curriculo.jsp</li>
	 *   <li>/sigaa.war/public/curso/resumo_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String detalhesCurriculo() throws ArqException {
		getCurrentSession().setAttribute("nivel", (getParameter("nivel") != null ? getParameter("nivel").charAt(0) : ' '));
		EstruturaCurricularMBean estruturaMBean = (EstruturaCurricularMBean) getMBean("curriculo");
		estruturaMBean.setObj(new Curriculo());
		estruturaMBean.popularCurriculo();
		return forward("/public/curso/resumo_curriculo.jsp");
	}
	
	/**
	 * Redireciona para visualiza��o dos dados do curr�culo do curso.
	 * <br>JSP's chamadas: <ul><li>\SIGAA\app\sigaa.ear\sigaa.war\public\curso\curriculo.jsp</li>
	 * 					<li>\SIGAA\app\sigaa.ear\sigaa.war\public\curso\resumo_curso.jsp</li></ul>
	 * @return
	 */
	public String relatorioCurriculo() {
		getCurrentSession().setAttribute("nivel", (getParameter("nivel") != null ? getParameter("nivel").charAt(0) : ' '));
		return redirectSemContexto("/sigaa/link/public/curso/curriculo/"+getParameterInt("id"));
	}	

	/**
	 * Popula os detalhes do curr�culo t�cnico
	 * <br>JSP: /SIGAA/app/sigaa.ear/sigaa.war/public/curso/resumo_curriculo_tecnico.jsp
	 * @return
	 * @throws DAOException
	 * @throws NumberFormatException
	 */
	public String detalhesCurriculoTecnico() throws DAOException,
			NumberFormatException {
		EstruturaCurricularTecDao dao = getDAO(EstruturaCurricularTecDao.class);
		estruturaCurricularTecnica = dao.findByPrimaryKey(Integer
				.parseInt(getParameter("id")), EstruturaCurricularTecnica.class);
		return forward("/public/curso/resumo_curriculo_tecnico.jsp");
	}

	public EstruturaCurricularTecnica getEstruturaCurricularTecnica() {
		return estruturaCurricularTecnica;
	}

	public void setEstruturaCurricularTecnica(
			EstruturaCurricularTecnica estruturaCurricularTecnica) {
		this.estruturaCurricularTecnica = estruturaCurricularTecnica;
	}

	/**
	 * Retorna todos os cursos de Stricto Sensu, usado na JSP para exibir tudo
	 * assim que a JSP for aberta
	 * 
	 * @Author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Collection<Curso> getAllCursos() throws DAOException {
		if (cursos == null) {
			buscarCursos();
		}
		return cursos;
	}

	// ---//

	/**
	 * Mant�m o programa de p�s sempre em request (claro que deve haver um input
	 * hidden idPrograma na JSP)
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException
	 */
	public void setaPrograma() throws DAOException {
		int idPrograma = getParameterInt("idPrograma");
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		programa = uniDao.findByPrimaryKey(idPrograma, Unidade.class);
	}

	// ---//

	/**
	 * Seta a lista de cursos de um determinado programa de p�s gradua��o. usado
	 * na jsp p�blica do programa de p�s (programa.jsp)
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException
	 */
	public String verCursosDoPrograma() throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		setCursos(dao.findByCentro(programa.getId()));
		return null;
	}

	// ---//

	/**
	 * Seta a lista de linhas de pesquisas que um determinado programa de
	 * p�s-gradua��o tem. usado na jsp p�blica do programa de p�s (programa.jsp)
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException
	 */
	public String verLinhasPesquisa() throws DAOException {
		GenericDAO genDao = getDAO(GenericDAOImpl.class);
		linhasDePesquisa = genDao.findByExactField(LinhaPesquisaStricto.class,
				"programa", programa.getId());
		return null;
	}

	/**
	 * Redireciona para a p�gina p�blica do Programa de P�s-gradua��o
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException
	 */
	public String redirectPaginaPrograma() throws DAOException {
		clicouEmPaginaInicial = true;

		verCursosDoPrograma(); // <-- Aten��o: Caso n�o seja mais exibido os
		// cursos do programa na p�gina inicial do
		// Programa (programa.jsp) assim que entrar na
		// p�gina (sem clicar no bot�o especifico de
		// exibir os cursos), Favor retirar daqui, pois
		// s� � usado quando se deseja exibir os cursos
		// logo no in�cio.
		verLinhasPesquisa(); // <-- Aten��o: Caso n�o seja mais exibido os
		// cursos do programa na p�gina inicial do
		// Programa (programa.jsp) assim que entrar na
		// p�gina (sem clicar no bot�o especifico de
		// exibir os cursos), Favor retirar daqui, pois
		// s� � usado quando se deseja exibir os cursos
		// logo no in�cio.

		return forward("/public/stricto/portal/programa.jsp");
	}

	// ---//

	/**
	 * Usado na p�gina p�blica do programa de p�s-gradua��o para quando o
	 * usu�rio clicar no Link 'Cursos' a jsp (programa.jsp) exibir somente essas
	 * informa��es.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/public/stricto/portal/programa.jsp</li>
	 * </ul>
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException
	 */
	public String exibirCursosDoPrograma() throws DAOException {
		clicouEmCursos = true;
		verCursosDoPrograma();
		return forward("/public/stricto/portal/programa.jsp");
	}

	// ---//
	/**
	 * retorna as especializa��es - usado geralmente para m�sica
	 * <br>JSP: N�o foi encontrado classe ou jsp que utilize o m�todo
	 * @author �dipo Elder F. de Melo
	 */
	public Set<String> getEspecializacaoTurmaEntrada() {
		Set<String> turmas = new HashSet<String>();
		if (obj instanceof CursoTecnico) {
			for (EstruturaCurricularTecnica estrutura : ((CursoTecnico) obj)
					.getEstruturasCurriculares()) {
				for (TurmaEntradaTecnico turma : estrutura
						.getTurmasEntradaTecnico()) {
					if (turma.getEspecializacao() != null)
						turmas.add(turma.getEspecializacao().getDescricao());
				}
			}
		}
		return turmas;
	}

	// -----------------------------------------------------------------//
	public boolean isFiltroNome() {
		return this.filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroModalidade() {
		return this.filtroModalidade;
	}

	public void setFiltroModalidade(boolean filtroModalidade) {
		this.filtroModalidade = filtroModalidade;
	}

	/** Retorna a descri��o do n�vel atual */
	public String getDescricaoNivel() {
		return NivelEnsino.getDescricao(obj.getNivel());
	}

	/** Indica se o n�vel atual � Gradua��o */
	public boolean isGraduacao() {
		Character nivel = obj.getNivel();
		return nivel != null && nivel.equals(NivelEnsino.GRADUACAO);
	}

	/** Indica se o n�vel atual � Lato-Sensu */
	public boolean isLato() {
		Character nivel = obj.getNivel();
		return nivel != null && nivel.equals(NivelEnsino.LATO);
	}

	/** Indica se o n�vel atual � Strict-Sensu */
	public boolean isStricto() {
		Character nivel = obj.getNivel();
		return nivel != null
				&& (nivel.equals(NivelEnsino.STRICTO)
						|| nivel.equals(NivelEnsino.MESTRADO) || nivel
						.equals(NivelEnsino.DOUTORADO));
	}

	/** Indica se o n�vel atual � T�cnico */
	public boolean isTecnico() {
		Character nivel = obj.getNivel();
		return nivel != null && nivel.equals(NivelEnsino.TECNICO);
	}

	public boolean isFiltroNivel() {
		return filtroNivel;
	}

	public void setFiltroNivel(boolean filtroNivel) {
		this.filtroNivel = filtroNivel;
	}

	public Collection<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	public Collection<LinhaPesquisaStricto> getLinhasDePesquisa() {
		return linhasDePesquisa;
	}

	public void setLinhasDePesquisa(
			Collection<LinhaPesquisaStricto> linhasDePesquisa) {
		this.linhasDePesquisa = linhasDePesquisa;
	}

	public boolean isClicouEmCursos() {
		return clicouEmCursos;
	}

	public void setClicouEmCursos(boolean clicouEmCursos) {
		this.clicouEmCursos = clicouEmCursos;
	}

	public boolean isClicouEmPaginaInicial() {
		return clicouEmPaginaInicial;
	}

	public void setClicouEmPaginaInicial(boolean clicouEmPaginaInicial) {
		this.clicouEmPaginaInicial = clicouEmPaginaInicial;
	}

	public boolean isClicouEmDetalharCurso() {
		return clicouEmDetalharCurso;
	}

	public void setClicouEmDetalharCurso(boolean clicouEmDetalharCurso) {
		this.clicouEmDetalharCurso = clicouEmDetalharCurso;
	}

	public Collection<Curriculo> getCurriculos() {
		return curriculos;
	}

	public void setCurriculos(Collection<Curriculo> curriculos) {
		this.curriculos = curriculos;
	}

	public Recomendacao getRecomendacao() {
		return recomendacao;
	}
}
