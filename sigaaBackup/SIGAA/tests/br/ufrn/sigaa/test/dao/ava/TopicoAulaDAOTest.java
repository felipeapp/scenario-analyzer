package br.ufrn.sigaa.test.dao.ava;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * 
 * Teste Unitário da classe RegistroAtividadeDao
 * 
 * @author Dalton
 *
 */
public class TopicoAulaDAOTest extends TestCase {

	TopicoAulaDao daoTopicoAula = new TopicoAulaDao();

	/** 
	 * Método: findByTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TopicoAula>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindByTurma() throws DAOException {
		// buscar uma turma
		TurmaDao daoTurma = new TurmaDao();
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);
		
		assertTrue(daoTopicoAula.findByTurma(turma).size() > 0);
		
		assertTrue(daoTopicoAula.findByTurma(null).size() == 0);
		
	}

}
