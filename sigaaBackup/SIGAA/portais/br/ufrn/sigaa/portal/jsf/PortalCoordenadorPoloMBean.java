/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 15/08/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.arq.dao.ead.PoloCursoDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dao.ead.TutoriaAlunoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.SemanaAvaliacao;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.jsf.FichaAvaliacaoEadMBean;
import br.ufrn.sigaa.ead.jsf.TutorOrientadorMBean;
import br.ufrn.sigaa.ead.negocio.EADHelper;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Managed-Bean principal do portal do coordenador de pólo de ensino
 *
 * @author David Pereira
 *
 */
//@SuppressWarnings("unchecked")
@Component(value="portalCoordPolo")
@Scope(value="session")
public class PortalCoordenadorPoloMBean extends SigaaAbstractController<Object> {

	/** Link para o portal do coordenador de pólo. */
	public static final String PORTAL_COORDENADOR_POLO = "/portais/cpolo/cpolo.jsp";

	/** Lista das semanas de avaliação abertas por tutor. */
	private List<SemanaAvaliacao> semanas;

	/** Lista de alunos orientados por um tutor. */
	private Collection<DiscenteGraduacao> alunosOrientados;

	/** Armazena o {@link PerfilPessoa} do coordenador de pólo. */
	private PerfilPessoa perfil;

	/** Coordenador do pólo logado. */
	private CoordenacaoPolo coordPolo;
	
	/** Armazena um {@link TutorOrientador}. */
	private TutorOrientador tutorOrientador = new TutorOrientador();
	
	/** Armazena um {@link Curso}. */
	private Curso curso = new Curso (-1);
	
	/** Coleções de tutores associados ao pólo. */
	private Collection<TutorOrientador> listagemTutores = new ArrayList<TutorOrientador>();
	
	/** Coleção de cursos associados ao pólo. */
	private List <Curso> listagemCursos = new ArrayList <Curso> ();

	public PortalCoordenadorPoloMBean() throws DAOException, NegocioException {
		if (getCoordPolo() == null)
			throw new NegocioException("O usuário logado não é um coordenador de pólo.");
			
		if (getCoordPolo().getIdPerfil() != null)
			perfil = PerfilPessoaDAO.getDao().get(getCoordPolo().getIdPerfil());
	}
	
	public Collection<TutorOrientador> getListagemTutores() {
		return listagemTutores;
	}

	public void setListagemTutores(Collection<TutorOrientador> listagemTutores) {
		this.listagemTutores = listagemTutores;
	}

	public TutorOrientador getTutorOrientador() {
		return tutorOrientador;
	}

	public void setTutorOrientador(TutorOrientador tutorOrientador) {
		this.tutorOrientador = tutorOrientador;
	}

	/**
	 * Mostra a ficha de avaliação do curso associado ao tutor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void fichaAvaliacao() throws DAOException {
		
		TutorOrientadorDao dao = getDAO(TutorOrientadorDao.class);
		tutorOrientador = dao.findByPrimaryKey(tutorOrientador.getId(), TutorOrientador.class);
		
		FichaAvaliacaoEadMBean fichaAvaliacaoEadMBean = (FichaAvaliacaoEadMBean) getMBean("fichaAvaliacaoEad");
		fichaAvaliacaoEadMBean.fichaDiscenteCoordenadorPolo( tutorOrientador.getPoloCurso() );
	}
	
	/**
	 * Direciona o usuário para a página de definição de horário.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/menu_cpolo.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void definirHorarioTutorPeloCoordenadorPolo() throws ArqException, NegocioException {
		
		getTutorPorCoordenador();
		
		if (tutorOrientador == null || tutorOrientador.getId() == 0)
			throw new NegocioException("Selecione um tutor");
	
		TutorOrientadorMBean tutorOrientadorMBean = (TutorOrientadorMBean)getMBean("tutorOrientador");	
		tutorOrientadorMBean.abrirPaginaHorarioCoordenadorPolo(tutorOrientador.getId());
	}
	
	/**
	 * Direciona o usuário em questão para a tela do relatório aluno polo.  
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>/sigaa.war/portais/cpolo/menu_cpolo.jsp</li>
	 * </ul>
	 */
	public String relatorioAlunosPoloForm() {
		 return forward("/ead/relatorios/relatorio_alunos_polo_form.jsf"); 
	}

	/**
	 * Direciona o usuário para a tela dos Horários de Tutores e Discentes.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/cpolo/menu_cpolo.jsp</li>
	 * </ul>
	 */
	public String relatorioHorariosTutoresDiscentes() {
		return forward("/ead/relatorios/relatorio_horario_form.jsf");
	}

	/**
	 * Retorna uma coleção de tutores de acordo com o coordenador de pólo definido.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<TutorOrientador> getTutorPorCoordenador() throws DAOException {
		TutorOrientadorDao coordenadorDao = getDAO(TutorOrientadorDao.class);
		
		ArrayList<TutorOrientador> lista = (ArrayList<TutorOrientador>) coordenadorDao.findByPolo(getCoordPolo().getPolo().getId());
		
		if (!isEmpty(lista)) {
			TutoriaAlunoDao dao = getDAO(TutoriaAlunoDao.class);
			alunosOrientados = EADHelper.findDiscentesByTutor(lista.get(0).getId(),lista.get(0).getPessoa().getId(),null,null);
			tutorOrientador = dao.findByPrimaryKey(lista.get(0).getId(), TutorOrientador.class);
		} else {
			alunosOrientados = null;
			tutorOrientador =null;
		}
		return lista;
	}
	
	/**
	 * Retorna os cursos associados ao coordenador.
	 * <br/>
	 * Método não invocado JSP(s):
	 */
	public List <Curso> getCursosPorCoordenador() throws DAOException {
		PoloCursoDao pcDao = null;
		TutoriaAlunoDao dao = null;
		
		try {
			pcDao = getDAO(PoloCursoDao.class);
			dao = getDAO(TutoriaAlunoDao.class);
		
			if (listagemCursos.isEmpty())
				listagemCursos = (ArrayList <Curso>) pcDao.findCursosByPolo(getCoordPolo().getPolo().getId());
			
			if (!isEmpty(listagemCursos)) {
				if (listagemCursos.get(0).getId() != curso.getId()){
					alunosOrientados = dao.findDiscentesByCurso(listagemCursos.get(0));
					curso = dao.findByPrimaryKey(listagemCursos.get(0).getId(), Curso.class);
				}
			} else {
				alunosOrientados = null;
				curso = new Curso(-1);
			}
			return listagemCursos;
		} finally {
			if (pcDao != null)
				pcDao.close();
			
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Direciona o usuário para a Listagem dos tutores.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/cpolo/menu_cpolo.jsp</li>
	 * </ul>
	 */
	public String listagemTurores() throws DAOException {
		listagemTutores = getTutorPorCoordenador();
		return forward("/portais/cpolo/listagem_tutores_coordenador_polo.jsp");
	}
	
	/**
	 * Retorna uma coleção de {@link SelectItem} contendo todos os tutores 
	 * de acordo com o coordenador do pólo.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTutoresCombo() throws DAOException {
		return toSelectItems(getTutorPorCoordenador(), "id", "nome");
	}
	
	/**
	 * Retorna uma coleção de {@link SelectItem} contendo todos os cursos 
	 * de acordo com o coordenador do pólo.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAllCursosCombo () throws DAOException {
		List <SelectItem> ss = new ArrayList<SelectItem>();
		ss.add(new SelectItem("0"," -- SELECIONE -- "));
		ss.addAll(toSelectItems (getCursosPorCoordenador(), "id", "nome"));
		return ss;
	}
	
	/**
	 * Listener para mudança do curso selecionado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws ArqException
	 */
	public void changeCurso (ValueChangeEvent e) throws ArqException {
		
		Integer idCurso = Integer.valueOf( e.getNewValue().toString() );	
		curso = getGenericDAO().findByPrimaryKey(idCurso, Curso.class);
		
		TutoriaAlunoDao dao = null;
		
		if (curso != null)
			try {
				dao = getDAO(TutoriaAlunoDao.class);
				alunosOrientados = dao.findDiscentesByCursoPolo(curso,getCoordPolo().getPolo());
			} finally {
				if (dao != null)
					dao.close();
			}
		else {
			alunosOrientados.clear();
			curso = new Curso(-1);
		}
	}

	/**
	 * Popula e retorna a lista de {@link #semanas}.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SemanaAvaliacao> getAvaliacoesAbertas() throws DAOException {
		if (semanas == null) {
			//FichaAvaliacaoEadDao dao = (FichaAvaliacaoEadDao) getDAO(FichaAvaliacaoEadDao.class);
			//semanas = dao.findSemanasAvaliacaoAbertasPorTutor(getTutorUsuario());
		}

		return semanas;
	}

	/**
	 * Retorna a quantidade de alunos orientados por um tutor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/tutor/tutor.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public int getQtdAlunosOrientandos() throws DAOException {
		return 0;
	}

	/**
	 * Direciona o usuário para a tela de visualização dos alunos.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>Não invocado por jsp, nem por métodos</li>
	 * </ul>
	 */
	public String verAlunos() {
		return forward("/portais/tutor/alunos.jsp");
	}

	/**
	 * Retorna a lista de alunos orientados {@link #alunosOrientados}.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> getAlunosOrientados() throws DAOException {
		if (alunosOrientados == null) {
		}
		return alunosOrientados;
	}

	/**
	 * Consultar turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @param evt
	 * @throws DAOException
	 * @throws IOException
	 */
	public void consultaTurma(ActionEvent evt) throws DAOException, IOException {
		forward("/ensino/turma/busca_turma.jsp");
	}

	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}
	
	public boolean isAdministracao() throws DAOException {
		return false;
	}

	public void setAlunosOrientados(Collection<DiscenteGraduacao> alunosOrientados) {
		this.alunosOrientados = alunosOrientados;
	}

	public CoordenacaoPolo getCoordPolo() {
		coordPolo = getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo();
		return coordPolo;
	}

	public void setCoordPolo(CoordenacaoPolo coordPolo) {
		this.coordPolo = coordPolo;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

}