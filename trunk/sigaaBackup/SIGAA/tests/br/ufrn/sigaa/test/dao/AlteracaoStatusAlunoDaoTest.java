package br.ufrn.sigaa.test.dao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.AlteracaoStatusAlunoDao;

/**
 * Teste Unit�rio da classe AlteracaoStatusAlunoDao
 * 
 * @author Dalton
 *
 */
public class AlteracaoStatusAlunoDaoTest extends TestCase{

	AlteracaoStatusAlunoDao dao = new AlteracaoStatusAlunoDao();
	
	
	/**
	 * Busca pela �ltima altera��o de um determinado discente
	 * 
	 * M�todo: findUltimaAlteracaoByDiscente
	 * 
	 * @param id_discente
	 * @return AlteracaoStatusAluno
	 * @throws DAOException
	 * 
	 */
	public void testFindUltimaAlteracaoByDiscente() throws DAOException {
		// Busca pelo discente de matr�cula: 200506294 (EDSON ALYPPYO GOMES COUTINHO) e id_discente: 89935
		assertNull(dao.findUltimaAlteracaoByDiscente(89935));
		
		// Busca pelo discente de matr�cula: 200418629 (REBECCA BETWEL SANTOS) e id_discente: 84383
		assertNotNull(dao.findUltimaAlteracaoByDiscente(84383));
	}
	
}
