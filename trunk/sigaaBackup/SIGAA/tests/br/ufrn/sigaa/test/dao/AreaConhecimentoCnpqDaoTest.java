package br.ufrn.sigaa.test.dao;

import java.util.Collection;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * Teste Unit·rio da classe AreaConhecimentoCnpqDao
 * 
 * @author Dalton
 *
 */
public class AreaConhecimentoCnpqDaoTest extends TestCase {

	AreaConhecimentoCnpqDao dao = new AreaConhecimentoCnpqDao();
	
	public static int CODIGO_SUBAREA = 10100008;
	
	public static int CODIGO_SUBAREA_SEM_ESPECIALIDADE = 20500009;
	
	public static int CODIGO_SUBAREA_INEXISTENTE = 22222222;
	
	/**
	 * Busca TODAS as Grandes ¡reas do CNPQ
	 * 
	 * MÈtodo: findGrandeAreasConhecimentoCnpq
	 * 
	 * @return Collection<AreaConhecimentoCnpq>
	 * @throws DAOException
	 * 
	 */
	public void testFindGrandeAreasConhecimentoCnpq() throws DAOException {
		Collection<AreaConhecimentoCnpq> result = dao.findGrandeAreasConhecimentoCnpq();
		assertNotNull(result);
		assertTrue(result.size() > 0);
	}
	
	/**
	 * Busca TODAS as ¡reas de uma Grande ¡rea
	 * 
	 * MÈtodo: findAreas
	 * 
	 * @param grandeArea
	 * @return Collection<AreaConhecimentoCnpq>
	 * @throws DAOException
	 * 
	 */
	public void testFindAreas() throws DAOException {
		
		Collection<AreaConhecimentoCnpq> grandes = dao.findGrandeAreasConhecimentoCnpq();
		AreaConhecimentoCnpq grande = grandes.iterator().next();
		
		Collection<AreaConhecimentoCnpq> result = dao.findAreas(grande);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		
		assertTrue(dao.findAreas(null).size() > 0);
	}

	/**
	 * Busca TODAS as Sub¡reas de uma ¡rea
	 * 
	 * MÈtodo: findSubAreas
	 * 
	 * @param codigo // codigo da ·rea
	 * @return Collection<AreaConhecimentoCnpq>
	 * @throws DAOException
	 * 
	 */
	public void testFindSubAreas() throws DAOException {
		Collection<AreaConhecimentoCnpq> result = dao.findSubAreas(CODIGO_SUBAREA);
		assertNotNull(result);
		assertTrue(result.size()> 0);
		
		// Testa para buscar uma as sub ·reas de uma ·rea que n„o existe.
		Collection<AreaConhecimentoCnpq> result1 = dao.findSubAreas(CODIGO_SUBAREA_INEXISTENTE);
		assertEquals(result1.size(), 0);
	}
	
	/**
	 * Busca TODAS as Especilidades de uma Sub¡rea
	 * 
	 * MÈtodos: findEspecialidade
	 * 
	 * @param subArea
	 * @return Collection<AreaConhecimentoCnpq>
	 * @throws DAOException
	 */
	public void testFindEspecialidade() throws DAOException {
		
		Collection<AreaConhecimentoCnpq> subAreas = dao.findSubAreas(CODIGO_SUBAREA);
		AreaConhecimentoCnpq subArea = subAreas.iterator().next();

		Collection<AreaConhecimentoCnpq> result = dao.findEspecialidade(subArea);
		//System.out.println("FindEspecialidade - result: " + result.size() + "subArea - " + subArea.getId() + "-" + subArea.getCodigo());
		assertNotNull(result);
		assertTrue(result.size()> 0);
		
		
		// Testa para uma sub ·rea que n„o tem especialidades
		Collection<AreaConhecimentoCnpq> subAreas1 = dao.findSubAreas(CODIGO_SUBAREA_SEM_ESPECIALIDADE);
		AreaConhecimentoCnpq subArea1 = subAreas1.iterator().next();

		Collection<AreaConhecimentoCnpq> result1 = dao.findEspecialidade(subArea1);
		//System.out.println("FindEspecialidade - result1: " + result1.size() );
		assertEquals(result1.size(), 0);//
		
		
		// testa sub ·rea passando valor nulo
		assertTrue(dao.findEspecialidade(null).isEmpty());
	}
	
}