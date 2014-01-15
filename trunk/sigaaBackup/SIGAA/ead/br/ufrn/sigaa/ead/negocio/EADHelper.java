/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 15/08/2012
 */
package br.ufrn.sigaa.ead.negocio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dao.ead.TutoriaAlunoDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.dominio.TutoriaAluno;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosEAD;

/**
 * Classe com métodos utilitários para EAD, utilizado inicialmente pra decidir se um tutor irá 
 * possuir tutoria ou não, e assim escolher quais métodos acessar.
 *
 * @author Diego Jácome
 *
 */
public class EADHelper {

	/**
	 * Escolhe entre buscar o tutor através do pólo e curso do aluno ou através da relação de tutoria.
	 */
	public static ArrayList<TutorOrientador> findTutoresByAluno(DiscenteAdapter d) throws DAOException{
		
		TutorOrientadorDao toDao = null;
		TutoriaAlunoDao taDao = null;
		
		try {
			boolean permiteTutoria = ParametroHelper.getInstance().getParametroBoolean(ParametrosEAD.UTILIZAR_TUTORIA_EAD);
			ArrayList<TutorOrientador> t = null;
			
			if (!permiteTutoria){
				toDao = getDAO(TutorOrientadorDao.class);		
				t = toDao.findTutoresByDiscente(d);
				return t;
			}
			if (permiteTutoria){
				taDao = getDAO(TutoriaAlunoDao.class);
				TutoriaAluno ta = taDao.findUltimoByAluno(d.getId());
				if (ta != null && ta.getTutor()!=null){
					t = new ArrayList<TutorOrientador>();
					t.add(ta.getTutor());
				}
				return t;
			}
			return t;
		} finally {
			if (toDao!=null)
				toDao.close();
			if (taDao!=null)
				taDao.close();
		}
	}

	/**
	 * Escolhe entre buscar os discentes através do tutor, pólo ou curso do aluno ou através da relação de tutoria.
	 */
	public static Collection<DiscenteGraduacao> findDiscentesByTutor(Integer idTutor, Integer idPessoa, Integer idPolo, Integer idCurso) throws DAOException{
		
		TutorOrientadorDao toDao = null;
		TutoriaAlunoDao taDao = null;
		
		try {
			boolean permiteTutoria = ParametroHelper.getInstance().getParametroBoolean(ParametrosEAD.UTILIZAR_TUTORIA_EAD);
			
			if (idTutor != null && !permiteTutoria){
				toDao = getDAO(TutorOrientadorDao.class);
				Collection<DiscenteGraduacao> discentes = toDao.findDiscentesByTutor(idTutor, idPolo, idCurso);
				return discentes;
			}
			if (idPessoa != null && permiteTutoria){
				taDao = getDAO(TutoriaAlunoDao.class);
				Collection<DiscenteGraduacao> discentes = taDao.findDiscentesByTutor(idPessoa);
				return discentes;
			}
			return null;
		} finally {
			if (toDao!=null)
				toDao.close();
			if (taDao!=null)
				taDao.close();
		}	
	}

	/**
	 * Escolhe entre buscar os discentes através do tutor, pólo ou curso do aluno ou através da relação de tutoria.
	 */
	public static Collection<DiscenteGraduacao> findDiscentesParaLogarComo(Long matricula, String nome, String curso, Usuario usuario) throws DAOException{
		
		TutorOrientadorDao toDao = null;
		TutoriaAlunoDao taDao = null;
		
		try {
			boolean permiteTutoria = ParametroHelper.getInstance().getParametroBoolean(ParametrosEAD.UTILIZAR_TUTORIA_EAD);
			
			if (!permiteTutoria){
				toDao = getDAO(TutorOrientadorDao.class);
				Collection<DiscenteGraduacao> discentes = toDao.findDiscentesByMatriculaNomeCursoOuTutor( matricula, nome, curso, usuario == null ? null : usuario.getPessoa() );
				return discentes;
			}
			if (permiteTutoria){
				taDao = getDAO(TutoriaAlunoDao.class);
				Collection<DiscenteGraduacao> discentes = taDao.findDiscentesByMatriculaNomeCursoOuTutor( matricula, nome, curso, usuario == null ? null : usuario.getPessoa() );
				return discentes;
			}
			return null;
		} finally {
			if (toDao!=null)
				toDao.close();
			if (taDao!=null)
				taDao.close();
		}	
	}
	
	/**
	 * Retorna um dao.
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}
	
}
