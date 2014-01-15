package br.ufrn.sigaa.test.dao;

import java.util.Collection;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;

/**
 * 
 * Teste Unitário da classe InstituicoesEnsinoDao
 * 
 * @author Dalton
 *
 */
public class InstituicoesEnsinoDaoTest extends TestCase {

	InstituicoesEnsinoDao dao = new InstituicoesEnsinoDao();
	
	/**
	 * DAO para consulta especifica de instituicoes de ensino
	 * 
	 * Método: findByNome
	 * 
	 * @param String nome
	 * 
	 * @return Collection<InstituicoesEnsino>
	 * @throws DAOException
	 *
	 */
	public void testInstituicoesEnsinoDAO() throws DAOException {
		Collection result = dao.findByNome("Universidade");
		assertNotNull(result);
		
		Collection result1 = dao.findByNome("uhyt");
		assertEquals(result1.size(), 0);
	}

}
