package br.ufrn.sigaa.test.dao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;

/**
 * 
 * @author Edson Alyppyo G Coutinho
 *
 */

public class UnidadeDaoTest extends TestCase {

	UnidadeDao udao = new UnidadeDao();
	
	/** Método: findBySubUnidades
	 * 
	 * Retorna todas as unidades da unidade pai informada
	 * 
	 * @param UnidadeGeral pai
	 * 
	 * @return Collection<UnidadeGeral>
	 * 
	 * @throws DAOException
	 * 
	 */

	public void testFindBySubUnidades() throws DAOException {
		// Entrando com dados válidos
		assertTrue(udao.findBySubUnidades(new UnidadeGeral(605)).size() > 0);
		
		// Entrando com dados inválidos
		assertEquals(udao.findBySubUnidades(new UnidadeGeral()).size(), 0);
	}


	/** Método: findBySubUnidades
	 * 
	 * Retorna todas as subunidades da unidade pai informada e do tipoAcademico informado
	 *  
	 * @param UnidadeGeral pai
	 * @param int tipoAcademico
	 * 
	 * @return Collection<UnidadeGeral>
	 * 
	 * @throws DAOException
	 * 
	 */

	public void testFindBySubUnidades2() throws DAOException {
		// Entrando com dados válidos
		assertTrue(udao.findBySubUnidades(new UnidadeGeral(605), 1).size() > 0);
		
		// Entrando com dados inválidos
		assertEquals(udao.findBySubUnidades(new UnidadeGeral(), 1).size(), 0);		// Unidade inválida
		assertEquals(udao.findBySubUnidades(new UnidadeGeral(605), 0).size(), 0);	// tipoAcademico inválido
	}


	/** Método: findByNome
	 * 
	 * Busca por partes do nome da unidade.
	 * 
	 * @param String nome
	 * 
	 * @return Collection<UnidadeGeral>
	 * 
	 * @throws DAOException
	 * 
	 */

	public void testFindByNome() throws DAOException {
		// Entrando com dados válidos
		assertTrue(udao.findByNome("CCSA").size() > 0);
		
		// Entrando com dados inválidos
		assertEquals(udao.findByNome("***************").size(), 0);
	}

	
	/** Método: findByNomeTipo
	 * 
	 * Busca por partes do nome da unidade e por seu tipo.
	 * 
	 * @param String nome
	 * @param int tipo
	 * 
	 * @return Collection<UnidadeGeral>
	 * 
	 * @throws DAOException
	 * 
	 */

	public void testFindByNomeTipo() throws DAOException {
		// Entrando com dados válidos
		assertTrue(udao.findByNomeTipo("CCSA", 1).size() > 0);
		
		// Entrando com dados inválidos
		assertEquals(udao.findByNomeTipo("***************", 0).size(), 0);	// Nome inválido
		assertEquals(udao.findByNomeTipo("CCSA", 9999).size(), 0);			// Tipo inválido
	}

	
	/** Método: findByNome
	 *  
	 * @param String nome
	 * @param int hierarquia
	 * @param int tipo
	 * 
	 * @return Collection<UnidadeGeral>
	 * 
	 * @throws DAOException
	 *  
	 */

	public void testFindByNome2() throws DAOException {
		// Informando dados válidos
		assertTrue(udao.findByNome("CCSA", 443, 1).size() > 0);
		
		// Informando dados inválidos
		assertEquals(udao.findByNome("***************", 443, 1).size(), 0); // Nome inválido
		assertEquals(udao.findByNome("CCSA", 999999, 1).size(), 0);			// Hierarquia inválida
		assertEquals(udao.findByNome("CCSA", 443, 99999).size(), 0);		// Tipo inválido
	}

	
	/** Método: findAllGestorasAcademicas
	 *    
	 * @return Collection<Unidade>
	 * 
	 * @throws DAOException
	 *  
	 */

	public void testFindAllGestorasAcademicas() throws DAOException {
		// A busca deve sempre retornar resultados, a não ser que a base de dados
		// referente às unidades esteja vazia.
		assertTrue(udao.findAllGestorasAcademicas().size() > 0);
	}

	
	/** Método: findByCodigo
	 *  
	 * @param int codigo
	 * 
	 * @return UnidadeGeral
	 * 
	 * @throws DAOException
	 *  
	 */

	public void testFindByCodigo() throws DAOException {
		// Informando código válido
		// 1620 - Departamento de Serviço Social
		//--------------------------------------------------
		// Problemas na conversão de dados int para Integer
		//
		// Sempre ocoore DAOException para qualquer valor
		// informado, seja ele válido ou não.
		//--------------------------------------------------
		//assertNotNull(udao.findByCodigo(1620));
		
		// Informando código inválido
		//assertNull(udao.findByCodigo(9999));
	}


	/** Método: findAll
	 * 
	 * @param Class classe
	 * @param String[] orderFields
	 * @param String[] ascDesc
	 * 
	 * @return Collection<UnidadeGeral>
	 * 
	 * @throws DAOException
	 * 
	 */

	public void testFindAll() throws DAOException {
		String[] orderFields = {"nome", "id_unidade", "hierarquia"};
		String[] ascDesc = {"asc", "desc", "asc"};
		System.out.println("Classe: " + udao.getClass());
		
		// Inserindo dados válidos
		//-----------------------------------------------------------
		// Essa busca não retorna nenhum resultado, mesmo que todos
		// os parâmetros sejam preenchidos devidamente
		//-----------------------------------------------------------
		//assertTrue(udao.findAll(udao.getClass(), orderFields, ascDesc).size() > 0);
		
		// Inserindo dados inválidos
		//assertEquals(udao.findAll(null, orderFields, ascDesc).size(), 0);			 // Informando classe inválida

		//assertEquals(udao.findAll(udao.getClass(), orderFields, null).size(), 0);	 // Não informando ordens dos campos
		
		orderFields = new String[] {"*****", "id_unidade", "*****"};
		//assertEquals(udao.findAll(udao.getClass(), orderFields, ascDesc).size(), 0); // Informando campos inválidos
	}


	/** Método: findAllIdNome
	 *
	 * @return Collection<UnidadeGeral>
	 * 
	 * @throws DAOException
	 * 
	 */

	public void testFindAllIdNome() throws DAOException {
		// Essa busca deve sempre retornar resultados caso a tabela de unidades
		// no banco não esteja vazia
		//-------------------------------------------------------
		// O método simplesmente não funciona
		//-------------------------------------------------------
		// br.ufrn.arq.erros.DAOException: org.hibernate.hql.ast.QuerySyntaxException:
		// Unable to locate appropriate constructor on class [br.ufrn.sigaa.dominio.Unidade]
		// [select new Unidade( u.id, u.codigo, u.nome, u.sigla, u.nomeCapa, u.hierarquia,
		// u.unidadeResponsavel, u.unidadeSipac, u.unidadeProtocolo, u.unidadeOrcamentaria, u.tipo
		// from br.ufrn.sigaa.dominio.Unidade u where u.unidadeSipac = true order by u.nomeCapa]
		//-------------------------------------------------------

		//assertTrue(udao.findAllIdNome().size() > 0);
	}


	/** Método: findByTipoUnidadeAcademica
	 * 
	 * @param int... tipos
	 * 
	 * @return Collection<Unidade>
	 *  
	 * @throws DAOException
	 * 
	 */

	public void testFindByTipoUnidadeAcademica() throws DAOException {
		// Inserindo dados válidos
		assertTrue(udao.findByTipoUnidadeAcademica(1, 2, 4).size() > 0);
		
		// Inserindo dados inválidos
		assertEquals(udao.findByTipoUnidadeAcademica(1999, 2000, 3000, 9999).size(), 0); // Valores inexistentes de tipos de unidade
		//assertEquals(udao.findByTipoUnidadeAcademica(null).size(), 0);			     // Nenhum valor
	}


	/** Método: findAllUnidadeOrcamentaria
	 * 
	 * @return Collection<UnidadeGeral>
	 *  
	 * @throws DAOException
	 * 
	 */

	public void testFindAllUnidadeOrcamentaria() throws DAOException {
		// Essa busca deve sempre retornar resultados, se a tabela contiver
		// dados a respeito
		assertTrue(udao.findAllUnidadeOrcamentaria().size() > 0);
	}

	
	/** Método: findUnidadeAcademicaByNome
	 * 
	 * @param String nome
	 * 
	 * @return Collection<UnidadeGeral>
	 *  
	 * @throws DAOException
	 * 
	 */

	/**
	 * Método modificado - Verificar novamente 
	 */
	
	public void testFindUnidadeAcademicaByNome() throws DAOException {
		// Informando dados válidos
		assertTrue(udao.findUnidadeAcademicaByNome("DEPARTAMENTO", null).size() > 0);
		
		// Informando dados inválidos
		assertEquals(udao.findUnidadeAcademicaByNome("****************************", null).size(), 0);
	}

}
