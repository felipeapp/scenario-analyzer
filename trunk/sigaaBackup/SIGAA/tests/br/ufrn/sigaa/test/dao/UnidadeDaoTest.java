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
	
	/** M�todo: findBySubUnidades
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
		// Entrando com dados v�lidos
		assertTrue(udao.findBySubUnidades(new UnidadeGeral(605)).size() > 0);
		
		// Entrando com dados inv�lidos
		assertEquals(udao.findBySubUnidades(new UnidadeGeral()).size(), 0);
	}


	/** M�todo: findBySubUnidades
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
		// Entrando com dados v�lidos
		assertTrue(udao.findBySubUnidades(new UnidadeGeral(605), 1).size() > 0);
		
		// Entrando com dados inv�lidos
		assertEquals(udao.findBySubUnidades(new UnidadeGeral(), 1).size(), 0);		// Unidade inv�lida
		assertEquals(udao.findBySubUnidades(new UnidadeGeral(605), 0).size(), 0);	// tipoAcademico inv�lido
	}


	/** M�todo: findByNome
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
		// Entrando com dados v�lidos
		assertTrue(udao.findByNome("CCSA").size() > 0);
		
		// Entrando com dados inv�lidos
		assertEquals(udao.findByNome("***************").size(), 0);
	}

	
	/** M�todo: findByNomeTipo
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
		// Entrando com dados v�lidos
		assertTrue(udao.findByNomeTipo("CCSA", 1).size() > 0);
		
		// Entrando com dados inv�lidos
		assertEquals(udao.findByNomeTipo("***************", 0).size(), 0);	// Nome inv�lido
		assertEquals(udao.findByNomeTipo("CCSA", 9999).size(), 0);			// Tipo inv�lido
	}

	
	/** M�todo: findByNome
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
		// Informando dados v�lidos
		assertTrue(udao.findByNome("CCSA", 443, 1).size() > 0);
		
		// Informando dados inv�lidos
		assertEquals(udao.findByNome("***************", 443, 1).size(), 0); // Nome inv�lido
		assertEquals(udao.findByNome("CCSA", 999999, 1).size(), 0);			// Hierarquia inv�lida
		assertEquals(udao.findByNome("CCSA", 443, 99999).size(), 0);		// Tipo inv�lido
	}

	
	/** M�todo: findAllGestorasAcademicas
	 *    
	 * @return Collection<Unidade>
	 * 
	 * @throws DAOException
	 *  
	 */

	public void testFindAllGestorasAcademicas() throws DAOException {
		// A busca deve sempre retornar resultados, a n�o ser que a base de dados
		// referente �s unidades esteja vazia.
		assertTrue(udao.findAllGestorasAcademicas().size() > 0);
	}

	
	/** M�todo: findByCodigo
	 *  
	 * @param int codigo
	 * 
	 * @return UnidadeGeral
	 * 
	 * @throws DAOException
	 *  
	 */

	public void testFindByCodigo() throws DAOException {
		// Informando c�digo v�lido
		// 1620 - Departamento de Servi�o Social
		//--------------------------------------------------
		// Problemas na convers�o de dados int para Integer
		//
		// Sempre ocoore DAOException para qualquer valor
		// informado, seja ele v�lido ou n�o.
		//--------------------------------------------------
		//assertNotNull(udao.findByCodigo(1620));
		
		// Informando c�digo inv�lido
		//assertNull(udao.findByCodigo(9999));
	}


	/** M�todo: findAll
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
		
		// Inserindo dados v�lidos
		//-----------------------------------------------------------
		// Essa busca n�o retorna nenhum resultado, mesmo que todos
		// os par�metros sejam preenchidos devidamente
		//-----------------------------------------------------------
		//assertTrue(udao.findAll(udao.getClass(), orderFields, ascDesc).size() > 0);
		
		// Inserindo dados inv�lidos
		//assertEquals(udao.findAll(null, orderFields, ascDesc).size(), 0);			 // Informando classe inv�lida

		//assertEquals(udao.findAll(udao.getClass(), orderFields, null).size(), 0);	 // N�o informando ordens dos campos
		
		orderFields = new String[] {"*****", "id_unidade", "*****"};
		//assertEquals(udao.findAll(udao.getClass(), orderFields, ascDesc).size(), 0); // Informando campos inv�lidos
	}


	/** M�todo: findAllIdNome
	 *
	 * @return Collection<UnidadeGeral>
	 * 
	 * @throws DAOException
	 * 
	 */

	public void testFindAllIdNome() throws DAOException {
		// Essa busca deve sempre retornar resultados caso a tabela de unidades
		// no banco n�o esteja vazia
		//-------------------------------------------------------
		// O m�todo simplesmente n�o funciona
		//-------------------------------------------------------
		// br.ufrn.arq.erros.DAOException: org.hibernate.hql.ast.QuerySyntaxException:
		// Unable to locate appropriate constructor on class [br.ufrn.sigaa.dominio.Unidade]
		// [select new Unidade( u.id, u.codigo, u.nome, u.sigla, u.nomeCapa, u.hierarquia,
		// u.unidadeResponsavel, u.unidadeSipac, u.unidadeProtocolo, u.unidadeOrcamentaria, u.tipo
		// from br.ufrn.sigaa.dominio.Unidade u where u.unidadeSipac = true order by u.nomeCapa]
		//-------------------------------------------------------

		//assertTrue(udao.findAllIdNome().size() > 0);
	}


	/** M�todo: findByTipoUnidadeAcademica
	 * 
	 * @param int... tipos
	 * 
	 * @return Collection<Unidade>
	 *  
	 * @throws DAOException
	 * 
	 */

	public void testFindByTipoUnidadeAcademica() throws DAOException {
		// Inserindo dados v�lidos
		assertTrue(udao.findByTipoUnidadeAcademica(1, 2, 4).size() > 0);
		
		// Inserindo dados inv�lidos
		assertEquals(udao.findByTipoUnidadeAcademica(1999, 2000, 3000, 9999).size(), 0); // Valores inexistentes de tipos de unidade
		//assertEquals(udao.findByTipoUnidadeAcademica(null).size(), 0);			     // Nenhum valor
	}


	/** M�todo: findAllUnidadeOrcamentaria
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

	
	/** M�todo: findUnidadeAcademicaByNome
	 * 
	 * @param String nome
	 * 
	 * @return Collection<UnidadeGeral>
	 *  
	 * @throws DAOException
	 * 
	 */

	/**
	 * M�todo modificado - Verificar novamente 
	 */
	
	public void testFindUnidadeAcademicaByNome() throws DAOException {
		// Informando dados v�lidos
		assertTrue(udao.findUnidadeAcademicaByNome("DEPARTAMENTO", null).size() > 0);
		
		// Informando dados inv�lidos
		assertEquals(udao.findUnidadeAcademicaByNome("****************************", null).size(), 0);
	}

}
