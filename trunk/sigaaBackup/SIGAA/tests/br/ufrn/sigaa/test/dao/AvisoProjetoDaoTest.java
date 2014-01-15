package br.ufrn.sigaa.test.dao;

import java.util.Collection;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.projetos.AvisoProjetoDao;
/**
 * Teste Unitário da classe AvisoProjetoDao
 * 
 * @author Dalton
 *
 */
public class AvisoProjetoDaoTest extends TestCase {
	
	AvisoProjetoDao dao = new AvisoProjetoDao();
	
	/**
	 * Busca TODOS os avisos de um projeto
	 * 
	 * Método: findByProjeto
	 * 
	 * @param idprojeto
	 * @return Collection<AvisoProjeto>
	 * @throws DAOException
	 * 
	 */
	public void testFindByProjeto() throws DAOException {
		Collection result = dao.findByProjeto(909641);
		assertNotNull(result);
		assertEquals(result.size(), 1);
		
		
		// testa passando um projeto que não possui avisos
		Collection result1 = dao.findByProjeto(35650);
		assertNotNull(result1);
		assertEquals(result1.size(), 0);
		
	}
	
	
	
	
	
}
