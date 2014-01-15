/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 22/02/2010 
 *
 */

package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.ForumCursoDocenteDao;
import br.ufrn.sigaa.ava.dominio.ForumCursoDocente;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.pessoa.dominio.Docente;

/**
 * <p>
 * Managed Bean que lista os cursos de gradua��o que o docente logado atua e todos os outros,
 * para que este docente tenha acesso ao f�rum de cada curso.
 * </p>
 * 
 * @author R�mulo Augusto
 *
 */
@Component @Scope("request")
public class ListagemCursosForumDocenteMBean extends SigaaAbstractController<Curso> {
	
	/** Armazena os cursos nos quais o docente logado tem participa��o, ou seja, ministra para alguma turma. */
	private Collection<Curso> cursosAtuadosPeloDocente = new ArrayList<Curso>();
	
	/** Armazena todos os cursos nos quais o docente logado n�o tem participa��o. */
	private Collection<Curso> cursosNaoAtuadosPeloDocente = new ArrayList<Curso>();
	
	/** Armazena o ano e o per�odo de todos os cursos em que o docente logado j� teve participa��o. */
	private List <SelectItem> anoPeriodoComCursos = new ArrayList <SelectItem> ();
	
	/**Indica o ano e o per�odo que se deseja listar os f�runs. Por default tem o valor do ano e per�odo vigentes. */
	private String anoPeriodo;
	
	/** Armazena o ano atual. */
	private int ano = CalendarUtils.getAnoAtual();
	
	/** Armazena o per�odo atual. */
	private int periodo = getPeriodoAtual();
	
	/** Lista de F�runs Acess�veis pelo Docente */
	private List<ForumCursoDocente> forunsAcessiveis = new ArrayList<ForumCursoDocente>();

	/** Construtor padr�o */
	public ListagemCursosForumDocenteMBean() {}
	
	/**
	 * Retorna todos os cursos em que o docente logado tem participa��o, ou seja, cursos 
	 * nos quais existem turmas que ele ministre aula.
	 * 
	 * <br />
	 * M�todo chamado pela JSP:
	 * <ul>
	 * <li>sigaa.war/portais/docente/cursos_forum.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<Curso> getCursosAtuadosPeloDocente() throws ArqException {

		CursoDao dao = null;
		
		if( isAllEmpty(cursosAtuadosPeloDocente) ){
			try{
				if( anoPeriodo!=null)
				{
					ano = Integer.parseInt(anoPeriodo.substring(0,4));
					periodo = Integer.parseInt(anoPeriodo.substring(5));
				}
	
				Docente docente = (getServidorUsuario() != null) ? getServidorUsuario() : getDocenteExternoUsuario();
	
				dao = getDAO(CursoDao.class);
				cursosAtuadosPeloDocente = dao.findAllCursosByDocente(docente,ano,periodo);
			}finally
			{
				if(dao!=null)
					dao.close();
			}
		}
		return cursosAtuadosPeloDocente;
	}
	
	public void recarregaListaForunsCurso(ValueChangeEvent e){
		cursosAtuadosPeloDocente = new ArrayList<Curso>();
	}
	
	
	public void setForunsAcessiveis(List<ForumCursoDocente> forunsAcessiveis) {
		this.forunsAcessiveis = forunsAcessiveis;
	}

	/**
	 * Retorna os f�runs que o docente possui permiss�o
	 * <br />
	 * M�todo chamado pela JSP:
	 * <ul>
	 * 	 <li>/sigaa.war/portais/docente/cursos_forum.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<ForumCursoDocente> getForunsAcessiveis() throws HibernateException, DAOException{
		if (ValidatorUtil.isEmpty( forunsAcessiveis )){
			Docente docente = (getServidorUsuario() != null) ? getServidorUsuario() : getDocenteExternoUsuario();
			if (!ValidatorUtil.isEmpty(docente))
				forunsAcessiveis = getDAO(ForumCursoDocenteDao.class).findByDocente( docente.getId() );					
		} 
		return forunsAcessiveis;
	}
	
	/**
	 * Retorna todos os cursos em que o docente logado <strong>n�o</strong> tem participa��o, 
	 * ou seja, qualquer outro curso que ele n�o ministre aulas.
	 * 
	 * <br />
	 * M�todo n�o chamado por JSPs.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<Curso> getCursosNaoAtuadosPeloDocente() throws ArqException {
		
		CursoDao dao = null;
		
		try{
			if (ValidatorUtil.isEmpty(cursosNaoAtuadosPeloDocente)) {

				Docente docente = (getServidorUsuario() != null) ? getServidorUsuario() : getDocenteExternoUsuario();

				dao = getDAO(CursoDao.class);
				cursosNaoAtuadosPeloDocente = dao.findCursosReservaNaGraduacaoSemParticipacaoDoDocenteNoAnoPeriodo(docente, CalendarUtils.getAnoAtual(), getPeriodoAtual());
			}
		}
		finally{
			if( dao != null )
				dao.close();
		}
		return cursosNaoAtuadosPeloDocente;
	}
	
	/**
	 * Encontra o ano e o per�odo de todos os cursos em que o docente logado j� teve participa��o.
	 * <br/><br/> 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String findAnoPeriodoCursosAnteriores () throws ArqException 
	{
		CursoDao dao = null;
		TurmaDao tDao = null;
		
		try{		
			dao = getDAO(CursoDao.class);
			tDao = getDAO(TurmaDao.class);
			

			Docente docente = (getServidorUsuario() != null) ? getServidorUsuario() : getDocenteExternoUsuario();
			
			List<String> anoPeriodoDasTurmasDoDocente = tDao.findAnoPeriodoOfTurmasByDocente(docente);
			
			Collection<Curso> periodoAtual = dao.findAllCursosByDocente(docente,CalendarUtils.getAnoAtual(),getPeriodoAtual());
			
			//Caso o ano e per�odo atuais ainda n�o tenham cursos cadastrados.
			if( periodoAtual.isEmpty() )
			{	
				String item = Integer.toString(CalendarUtils.getAnoAtual()) + "." + Integer.toString(getPeriodoAtual());
				anoPeriodoComCursos.add(new SelectItem (item,item));
			}
			
			for ( String anoPeriodo : anoPeriodoDasTurmasDoDocente )
					anoPeriodoComCursos.add(new SelectItem (anoPeriodo,anoPeriodo));
			
		}finally
		{
			if(dao!=null)
				dao.close();
		}
		return listar();
		
	}
	
	public List<SelectItem> getAnoPeriodoComCursos() {
		return anoPeriodoComCursos;
	}

	public void setAnoPeriodoComCursos(List<SelectItem> anoPeriodoComCursos) {
		this.anoPeriodoComCursos = anoPeriodoComCursos;
	}
	
	public String getAnoPeriodo() {
		return anoPeriodo;
	}

	public void setAnoPeriodo(String anoPeriodo){
		this.anoPeriodo = anoPeriodo;
	}
	
	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	
	@Override
	public String getListPage() {
		return "/portais/docente/cursos_forum.jsp";
	}
}
