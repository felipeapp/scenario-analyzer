/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/11/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;

/**
 * Managed-Bean com operações gerais de Lato-Sensu.
 * 
 * @author Gleydson
 * @author Leonardo Campos
 */

@Component("lato") @Scope("session")
public class LatoMBean extends SigaaAbstractController {
	
	/** Curso Lato manipulado. */
	private CursoLato curso = new CursoLato();
	
	/** Id do curso Lato manipulado. */
	private int idCursoSessao;

	public Collection<SelectItem> getAllCursosPreInscricao() throws DAOException {
		CursoDao cursoDao = getDAO(CursoDao.class);
		return toSelectItems(cursoDao.findAll(CursoLato.class), "id", "nome");

	}
	
	public boolean getGestorPodeCadastrarProposta(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosLatoSensu.PERMITE_GESTOR_CADASTRAR_PROPOSTA_CURSO_LATO);
	}

	
	/**
	 * Retorna uma coleção de todos os Cursos Lato ativos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/lato/especializacoes.jsp</li>
	 *	</ul>
	 */
	public Collection<CursoLato> getAllAtivos() throws DAOException {
		return getGenericDAO().findAll(CursoLato.class, "nome", "asc");
	}

	/**
	 * Retorna uma coleção de todos os Cursos Lato ativos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/coordenacao_curso/form.jsp</li>
	 *		<li>sigaa.war/ensino/coordenacao_curso/lista.jsp</li>
	 *		<li>sigaa.war/lato/conclusao_curso/form.jsp</li>
	 *		<li>sigaa.war/lato/prorrogacao_prazo/form.jsp</li>
	 *	</ul>
	 */
	public Collection<SelectItem> getAllCombo() throws DAOException {
		//return toSelectItems(getGenericDAO().findAll(CursoLato.class, "nome", "asc"), "id", "nome");
		CursoLatoDao cursoLatoDAO = new CursoLatoDao();
		return toSelectItems(cursoLatoDAO.findAllOtimizado(), "id", "nome");
	}
	
	/**
	 * Visualiza curso que está na parte pública.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/lato/especializacoes.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String verCurso() throws DAOException {

		GenericDAO dao = getGenericDAO();

		String id = FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterMap().get("idCurso");
		curso.setId( Integer.parseInt(id) );

		curso = dao.findByPrimaryKey(curso.getId(), CursoLato.class);

		return forward(ConstantesNavegacao.VER_CURSO);

	}

	/**
	 * Visualiza especializações por centro.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String verEspecializacoes() throws DAOException {
		return forward(ConstantesNavegacao.VER_ESPECIALIZACOES);
	}

	public void gerenciarCurso(ValueChangeEvent evt){
		setIdCursoSessao( (Integer) evt.getNewValue() );
	}
	
	/**
	 * Retorna todos os cursos coordenados pelo usuário.
	 *  
	 * @return
	 */
	public Collection<SelectItem> getAllCursosCoordenadorCombo() {
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		try {
			return toSelectItems(dao.findAllCoordenadoPor(getUsuarioLogado().getServidor().getId()), "id", "nome");
		} catch (Exception e) {
			notifyError(e);
			return new ArrayList<SelectItem>();
		}
	}

	/**
	 * Método utilizado para corrigir os horários duplicados no lato sensu.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String corrigeDescricaoHorario() throws DAOException {
		TurmaDao dao = getDAO(TurmaDao.class);
		Collection<Turma> turmas = dao.findByExactField(Turma.class, "disciplina.nivel", "L", null);
		String descricao;
		int updated = 0;
		for(Turma t: turmas){
			descricao = HorarioTurmaUtil.formatarCodigoHorarios(t);
			if(!descricao.equals(t.getDescricaoHorario())){
				dao.updateField(Turma.class, t.getId(), "descricaoHorario", descricao);
				updated++;
			}
		}
		addMensagemWarning(updated + " turmas atualizadas.");
		return null;
	}
	
	/**
	 * Retorna todas as turmas com a situação "ABERTA" e "A_DEFINIR_DOCENTE" que pertencem ao Curso Lato informado.
	 * 
	 * @return
	 */
	public List<SelectItem> getTurmasMatricula() {
		TurmaDao dao = getDAO(TurmaDao.class);
		try {
			Collection<Turma> turmas = null;
			turmas = dao.findByCursoLatoSituacao(getCursoAtualCoordenacao().getId(), SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE);
			return toSelectItems(turmas, "id", "descricaoSemDocente");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		}
	}
	
	public CursoLato getCurso() throws DAOException{
		if(curso.getId() == 0)
			curso = getGenericDAO().findByPrimaryKey(idCursoSessao, CursoLato.class);
		return curso;
	}

	public void setCurso(CursoLato curso) {
		this.curso = curso;
	}
	
	public int getIdCursoSessao() {
		CursoLato c = (CursoLato) getCurrentSession().getAttribute("cursoAtual");
		if( c != null )
			idCursoSessao = c.getId();
		return idCursoSessao;
	}

	public void setIdCursoSessao(int idCursoSessao) {
		this.idCursoSessao = idCursoSessao;
		getCurrentSession().setAttribute("idCursoLato", idCursoSessao);
	}
}