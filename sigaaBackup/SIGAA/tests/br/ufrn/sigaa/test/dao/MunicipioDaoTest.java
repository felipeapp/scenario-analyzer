package br.ufrn.sigaa.test.dao;

import java.util.Collection;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.MunicipioDao;

/**
 * 
 * Teste Unitário da classe MunicipioDao
 * 
 * @author Dalton
 *
 */
public class MunicipioDaoTest extends TestCase {

	MunicipioDao dao = new MunicipioDao();
	
	
	/** 
	 * Retorna todos os municipios por UF
	 * 
	 * Método: findByUF
	 * 
	 * @param código da UF
	 * 
	 * @return Collection<Municipio>
	 * @throws DAOException
	 * 
	 */
	public void testFindByUF() throws DAOException {
		Collection result = dao.findByUF(24);
		assertNotNull(result);
		System.out.println(result.size());
		assertTrue(result.size() > 0);
		
		Collection result1 = dao.findByUF(999);
		System.out.println(result1.size());
		assertEquals(result1.size(), 0);
	}
	
	/** 
	 * Retorna todos os municipios por Nome
	 * 
	 * Método: findByNome
	 * 
	 * @param Nome do Município
	 * 
	 * @return Collection<Municipio>
	 * @throws DAOException
	 * 
	 */
	public void testFindByNome() throws DAOException {
		Collection result = dao.findByNome("Açu");
		assertNotNull(result);
		System.out.println(result.size());
		assertTrue(result.size() > 1);
		
		Collection result1 = dao.findByNome("grtpwrt");
		System.out.println(result1.size());
		assertEquals(result1.size(), 0);
	}
	
	/** 
	 * Retorna um município de um estado
	 * 
	 * Método: findUniqueByNome
	 * 
	 * @param nome do Município
	 * @param código da UF
	 * 
	 * @return Collection<Municipio>
	 * @throws DAOException
	 * 
	 */
	public void testFindUniqueByNome() throws DAOException {
		assertNotNull(dao.findUniqueByNome("Açu", "RN"));
		assertNull(dao.findUniqueByNome("Açu", "PB"));
		assertNull(dao.findUniqueByNome("Guarabira", "RN"));
	}
	
}
