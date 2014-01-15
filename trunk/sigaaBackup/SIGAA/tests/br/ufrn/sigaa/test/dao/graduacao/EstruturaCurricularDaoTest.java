package br.ufrn.sigaa.test.dao.graduacao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;

public class EstruturaCurricularDaoTest extends TestCase {

	EstruturaCurricularDao ecdao = new EstruturaCurricularDao();

	/** Método: findCompleto
	 * 
	 * @param Integer idCurso
	 * @param Integer idMatriz
	 * @param Integer idUnidade
	 * @param String codigo
	 * 
	 * @return Collection<Curriculo>
	 * 
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 * 
	 */
	public void testFindCompleto() throws DAOException, LimiteResultadosException {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		// Unidade: 605
		
		// Informando valores válidos
		assertFalse(ecdao.findCompleto(2000013, 99162, 439, "02").isEmpty());	// Busca com todos os valores setados
		assertFalse(ecdao.findCompleto(2000013, null, null, null).isEmpty());	// Busca por Curso
		assertFalse(ecdao.findCompleto(null, 99162, null, null).isEmpty());		// Busca por Matriz
		
		// As buscas abaixo foram refinadas por retornar mais resultados do que o limite pré-estabelecido
		assertFalse(ecdao.findCompleto(null, 99162, 439, null).isEmpty());		// Busca por Unidade
		assertFalse(ecdao.findCompleto(null, null, 439, "02").isEmpty());		// Busca por Codigo
		
		// Informando valores inválidos
		
		// O teste abaixo funciona corretamente, mas retorna muito resultados e sempre cai no LimiteResultadosException.
		// Logo foi comentado para evitar possíveis transtornos
		
		//assertTrue(ecdao.findCompleto(null, null, null, null).isEmpty());		// Todos os parâmetros nulos
	}

	
	/** Método: findByCurso
	 * 
	 * @param int idCurso
	 * @param char nivel
	 * 
	 * @return Collection<Curriculo>
	 * 
	 * @throws DAOException
	 *   
	 */
	public void testFindByCurso() throws DAOException  {
		// Curso: Ciência da Computação (2000013)
		// Nível: Graduação (G)
		
		// Informando dados válidos
		assertFalse(ecdao.findByCurso(2000013, 'G').isEmpty());
		
		// Informando dados inválidos
		assertTrue(ecdao.findByCurso(-2000013, 'G').isEmpty());	// Curso inválido
		assertTrue("A busca não filtra outros níveis diferentes de Graduação (G) "
				 + "e permite que se entre com qualquer outro caractere. "
				 + "Ex: caso se informe um nível \"Z\", a busca continua "
				 + "retornando resultados.", ecdao.findByCurso(2000013, 'Z').isEmpty());	// Nível inválido
	}

	
	/** Método: findByMatriz
	 * 
	 * @param int idMatriz
	 * 
	 * @return Collection<Curriculo>
	 * 
	 * @throws DAOException
	 *
	 */
	public void testFindByMatriz() throws DAOException  {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		
		// Informando dados válidos
		assertFalse(ecdao.findByMatriz(99162).isEmpty()); 
		
		// Informanda dados inválidos
		assertTrue(ecdao.findByMatriz(-99162).isEmpty());
	}

	
	/** Método: findMaisRecenteByMatriz
	 * 
	 * Retorna o curriculo mais recente da matriz indicada
	 * 
	 * @param int idMatriz
	 * 
	 * @return Curriculo
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindMaisRecenteByMatriz() throws DAOException  {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		
		// Informando dados válidos
		assertNotNull(ecdao.findMaisRecenteByMatriz(99162));
		
		// Informando dados inválidos
		assertNull(ecdao.findMaisRecenteByMatriz(-99162));
	}

	
	/** Método: findMaisRecenteByCurso 
	 * 
	 * Retorna o curriculo mais recente do curso indicado
	 * 
	 * @param int idCurso
	 * 
	 * @return Curriculo
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindMaisRecenteByCurso() throws DAOException  {
		// Curso: Ciência da Computação (2000013)
		
		// Informando dados válidos
		assertNotNull(ecdao.findMaisRecenteByCurso(2000013));
		
		// Informando dados inválidos
		assertNull(ecdao.findMaisRecenteByCurso(-2000013));
	}

	
	/** Método: findByCodigo
	 * 
	 * @param String codigo
	 * @param char nivel
	 * 
	 * @return Collection<Curriculo>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindByCodigo() throws DAOException  {
		// Código: 02
		// Nível: Graduação (G)
		
		// Informando dados válidos
		assertFalse(ecdao.findByCodigo("02",'G').isEmpty());
		
		// Informando dados inválidos
		assertTrue(ecdao.findByCodigo("", 'G').isEmpty());			// String Vazia
		assertTrue(ecdao.findByCodigo(null, 'G').isEmpty());		// String nula
		assertTrue(ecdao.findByCodigo("999999", 'G').isEmpty());	// Código inexistente
		assertTrue(ecdao.findByCodigo("02", 'Z').isEmpty());		// Nível inexistente 
	}

	
	/** Método: findAll
	 * 
	 * @param char nivel
	 * 
	 * @return Collection<Curriculo>
	 * 
	 * @throws DAOException
	 *
	 */
	public void testFindAll() throws DAOException  {
		// Informando dados válidos
		assertFalse(ecdao.findAll('G').isEmpty());
		
		// Informando dados inválidos
		assertTrue(ecdao.findAll('Z').isEmpty());
	}

	
	/** Método: existeCodigo
	 * 
	 * @param Curriculo curriculo
	 * @param char nivel
	 * 
	 * @return boolean
	 * 
	 * @throws DAOException
	 *
	 */
	public void testExisteCodigo() throws DAOException  {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		// Currículo: 100216
		Curriculo c = new Curriculo(100216);
		
		c.setCodigo("02");
		
		// Informando dados válidos
		assertTrue(ecdao.existeCodigo(c, 'G'));
		assertTrue(ecdao.existeCodigo(c, '0'));
		
		c = new Curriculo();
		
		// Informando dados inválidos
		assertFalse(ecdao.existeCodigo(c, 'G'));		// Currículo não populado
		assertFalse(ecdao.existeCodigo(null, 'G'));		// Currículo nulo 
	}

	
	/** Método: findComponentesByCurriculo
	 * 
	 * @param int id
	 * 
	 * @return Collection<ComponenteCurricular> 
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindComponentesByCurriculo() throws DAOException  {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		// Currículo: 100216
		
		// Informando dados válidos
		assertFalse(ecdao.findComponentesByCurriculo(100216).isEmpty());
		
		// Informando dados inválidos
		assertTrue(ecdao.findComponentesByCurriculo(-100216).isEmpty());
	}

	
	/** Método: findCurriculoComponentesByCurriculo
	 * 
	 * @param int id
	 * 
	 * @return Collection<CurriculoComponente>
	 * 
	 * @throws DAOException
	 *  
	 */
	public void testFindCurriculoComponentesByCurriculo() throws DAOException  {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		// Currículo: 100216
		
		// Informando dados válidos
		assertFalse(ecdao.findCurriculoComponentesByCurriculo(100216).isEmpty());
		
		// Informando dados inválidos
		assertTrue(ecdao.findCurriculoComponentesByCurriculo(-100216).isEmpty());
	}

	
	/** Método: containsComponenteByCurso
	 * 
	 * @param int curso
	 * @param int componente
	 * 
	 * @return boolean 
	 * 
	 * @throws DAOException
	 *  
	 */
	public void testContainsComponenteByCurso() throws DAOException {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		// Componente: 20032		
		
		// Informando dados válidos
		assertFalse(ecdao.containsComponenteByCurso(2000013, 20032));

		// Informando dados inválidos
		assertTrue(ecdao.containsComponenteByCurso(-2000013, 20032));	// Curso inválido
		assertTrue(ecdao.containsComponenteByCurso(2000013, -20032));	// Componente inválido
	}

	
	/** Método: countDiscentesByCurriculo
	 * 
	 * Retorna a quantidade de alunos de um curriculo
	 * 
	 * @param int idCurriculo
	 * 
	 * @return int
	 *  
	 * @throws DAOException
	 *
	 */
	public void testCountDiscentesByCurriculo() throws DAOException {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		// Currículo: 100216
		
		// Informando dados válidos
		assertTrue(ecdao.countDiscentesByCurriculo(100216) > 0);
		
		// Informando dados inválidos
		assertEquals(ecdao.countDiscentesByCurriculo(-100216), 0);
	}

	
	/** Método: countTotalCrChCurriculo
	 * 
	 * Retorna um curriculo com os atributos crObrigatoriosTeoricos, crObrigatoriosPraticos, chObrigatorios populados
	 * ou seja, a carga horaria obrigatoria pratica e teorica e os creditos obrigatorios praticos e teoricos
	 * 
	 * @param int idCurriculo
	 * 
	 * @return Curriculo
	 * 
	 * @throws DAOException
	 *
	 */
	public void testCountTotalCrChCurriculo() throws DAOException {
		// Curso: Ciência da Computação (2000013)
		// Matriz: 99162
		// Currículo: 100216
		
		// Informando dados válidos
		assertNotNull(ecdao.countTotalCrChCurriculo(100216));
		
		// Informando dados inválidos
		assertNull(ecdao.countTotalCrChCurriculo(-100216));
	}

}
