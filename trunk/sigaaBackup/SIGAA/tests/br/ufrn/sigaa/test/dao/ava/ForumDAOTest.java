package br.ufrn.sigaa.test.dao.ava;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.ForumDao;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * 
 * Teste Unit�rio da classe ForumDao
 * 
 * @author Dalton
 *
 */
public class ForumDAOTest extends TestCase {

	ForumDao daoForum = new ForumDao();
	
	// buscar uma turma
	TurmaDao daoTurma = new TurmaDao();

	/** 
	 * M�todo: findMensagensByTopico()
	 * 
	 * @param idTopico (int)
	 * @param pagina (int)
	 * 
	 * @return List<ForumMensagem>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindMensagensByTopico() throws DAOException {
		// Informando valores v�lidos
		assertTrue(daoForum.findMensagensByTopico(1, 0, 0).size() > 0);
		
		// Informando valores inv�lidos
		assertTrue(daoForum.findMensagensByTopico(-1, 0, 0).size() == 0);
	}

	/** 
	 * M�todo: findTopicosByForum()
	 * 
	 * @param idForum (int)
	 * @param pagina (int)
	 * 
	 * @return List<ForumMensagem>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindTopicosByForum() throws DAOException {
		// Informando valores v�lidos
//		assertTrue(daoForum.findTopicosByForum(1, 1).size() > 0);
		
		// Informando valores inv�lidos
//		assertTrue(daoForum.findTopicosByForum(-1, -1).size() == 0);
	}

	/** 
	 * M�todo: findByTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<Forum>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindByTurma() throws DAOException {
		// buscar turma
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);
		
		// Informar valores v�lidos
		assertTrue(daoForum.findByTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoForum.findByTurma(null).size() == 0);
		
	}

	/** 
	 * M�todo: findMuralByTurma()
	 * 
	 * @param turma
	 * 
	 * @return Forum
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindMuralByTurma() throws DAOException {
		// buscar turma
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);
		
		// Informar valores v�lidos
		assertNotNull(daoForum.findByTurma(turma));
		
		// informar valores nulos
		assertNull(daoForum.findByTurma(null));
	}

	/** 
	 * M�todo: findMensagensByForum()
	 * 
	 * @param idForum (int)
	 * @param pagina (int)
	 * 
	 * @return List<ForumMensagem>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindMensagensByForum() throws DAOException {
		// Informar dados v�lidos
//		assertTrue(daoForum.findMensagensByForum(1, 1).size() > 0);
		
		//Informar dados inv�lidos
//		assertTrue(daoForum.findMensagensByForum(-1, -1).size() == 0);
	}

	/** 
	 * M�todo: findCountMensagensByTopico()
	 * 
	 * @param idTopico (int)
	 * 
	 * @return inteiro
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindCountMensagensByTopico() throws DAOException {
		// Informar dados v�lidos
		assertTrue(daoForum.findCountMensagensByTopico(1) > 0);
		
		// Informar dados inv�lidos
		assertTrue(daoForum.findCountMensagensByTopico(-1) == 0);
		
	}


	/** 
	 * M�todo: findMensagesRespostasPorForum()
	 * 
	 * @param id_forumTeste (int)
	 * 
	 * @return Collection<ForumMensagem>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindMensagesRespostasPorForum() throws DAOException {
		// Informando valores v�lidos
		assertTrue(daoForum.findMensagesRespostasPorForum(1).size() > 0);
		
		// Informando valores inv�lidos
		assertTrue(daoForum.findMensagesRespostasPorForum(-1).size() == 0);
	}


	/** 
	 * M�todo: findListaMensagensForumByIDForum()
	 * 
	 * @param idForum (int)
	 * 
	 * @return Collection<ForumMensagem>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindListaMensagensForumByIDForum() throws DAOException {
		// Informar dados v�lidos
		assertTrue(daoForum.findListaMensagensForumByIDForum(1).size() > 0);
		
		//Informar dados inv�lidos
		assertTrue(daoForum.findListaMensagensForumByIDForum(-1).size() == 0);
	}

	/** 
	 * M�todo: findForumMensagensByIDCurso()
	 * 
	 * @param idCursoCoordenador (int)
	 * 
	 * @return Forum
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindForumMensagensByIDCurso() throws DAOException {
		// Informar valores v�lidos
		assertNotNull(daoForum.findForumMensagensByIDCurso(1));
		
		// informar valores inv�lidos
		assertNull(daoForum.findForumMensagensByIDCurso(-1));
	}

	/** 
	 * M�todo: findForumMensagensByID()
	 * 
	 * @param idMensagemForum (int)
	 * @param pagina (int)
	 * 
	 * @return ForumMensagem
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindForumMensagensByID() throws DAOException {
		// Informar valores v�lidos
		assertNotNull(daoForum.findForumMensagensByID(1));
		
		// informar valores inv�lidos
		assertNull(daoForum.findForumMensagensByID(-1));
	}

	/** 
	 * M�todo: findNomeCursoByID()
	 * 
	 * @param idCurso (int)
	 * 
	 * @return ArrayList<Forum>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindNomeCursoByID() throws DAOException {
		// Informar dados v�lidos
		assertTrue(daoForum.findNomeCursoByID(1).size() > 0);
		
		//Informar dados inv�lidos
		assertTrue(daoForum.findNomeCursoByID(-1).size() == 0);
	}

	/** 
	 * M�todo: findForunsDeCursoByIDCurso()
	 * 
	 * @param idCurso (int)
	 * 
	 * @return ArrayList<Forum>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindForunsDeCursoByIDCurso() throws DAOException {
		// Informar dados v�lidos
		assertTrue(daoForum.findForunsDeCursoByIDCurso(1).size() > 0);
		
		//Informar dados inv�lidos
		assertTrue(daoForum.findForunsDeCursoByIDCurso(-1).size() == 0);
	}

}
