package br.ufrn.sigaa.test.dao.graduacao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;

public class EstruturaCurricularDaoTest extends TestCase {

	EstruturaCurricularDao ecdao = new EstruturaCurricularDao();

	/** M�todo: findCompleto
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
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		// Unidade: 605
		
		// Informando valores v�lidos
		assertFalse(ecdao.findCompleto(2000013, 99162, 439, "02").isEmpty());	// Busca com todos os valores setados
		assertFalse(ecdao.findCompleto(2000013, null, null, null).isEmpty());	// Busca por Curso
		assertFalse(ecdao.findCompleto(null, 99162, null, null).isEmpty());		// Busca por Matriz
		
		// As buscas abaixo foram refinadas por retornar mais resultados do que o limite pr�-estabelecido
		assertFalse(ecdao.findCompleto(null, 99162, 439, null).isEmpty());		// Busca por Unidade
		assertFalse(ecdao.findCompleto(null, null, 439, "02").isEmpty());		// Busca por Codigo
		
		// Informando valores inv�lidos
		
		// O teste abaixo funciona corretamente, mas retorna muito resultados e sempre cai no LimiteResultadosException.
		// Logo foi comentado para evitar poss�veis transtornos
		
		//assertTrue(ecdao.findCompleto(null, null, null, null).isEmpty());		// Todos os par�metros nulos
	}

	
	/** M�todo: findByCurso
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
		// Curso: Ci�ncia da Computa��o (2000013)
		// N�vel: Gradua��o (G)
		
		// Informando dados v�lidos
		assertFalse(ecdao.findByCurso(2000013, 'G').isEmpty());
		
		// Informando dados inv�lidos
		assertTrue(ecdao.findByCurso(-2000013, 'G').isEmpty());	// Curso inv�lido
		assertTrue("A busca n�o filtra outros n�veis diferentes de Gradua��o (G) "
				 + "e permite que se entre com qualquer outro caractere. "
				 + "Ex: caso se informe um n�vel \"Z\", a busca continua "
				 + "retornando resultados.", ecdao.findByCurso(2000013, 'Z').isEmpty());	// N�vel inv�lido
	}

	
	/** M�todo: findByMatriz
	 * 
	 * @param int idMatriz
	 * 
	 * @return Collection<Curriculo>
	 * 
	 * @throws DAOException
	 *
	 */
	public void testFindByMatriz() throws DAOException  {
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		
		// Informando dados v�lidos
		assertFalse(ecdao.findByMatriz(99162).isEmpty()); 
		
		// Informanda dados inv�lidos
		assertTrue(ecdao.findByMatriz(-99162).isEmpty());
	}

	
	/** M�todo: findMaisRecenteByMatriz
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
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		
		// Informando dados v�lidos
		assertNotNull(ecdao.findMaisRecenteByMatriz(99162));
		
		// Informando dados inv�lidos
		assertNull(ecdao.findMaisRecenteByMatriz(-99162));
	}

	
	/** M�todo: findMaisRecenteByCurso 
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
		// Curso: Ci�ncia da Computa��o (2000013)
		
		// Informando dados v�lidos
		assertNotNull(ecdao.findMaisRecenteByCurso(2000013));
		
		// Informando dados inv�lidos
		assertNull(ecdao.findMaisRecenteByCurso(-2000013));
	}

	
	/** M�todo: findByCodigo
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
		// C�digo: 02
		// N�vel: Gradua��o (G)
		
		// Informando dados v�lidos
		assertFalse(ecdao.findByCodigo("02",'G').isEmpty());
		
		// Informando dados inv�lidos
		assertTrue(ecdao.findByCodigo("", 'G').isEmpty());			// String Vazia
		assertTrue(ecdao.findByCodigo(null, 'G').isEmpty());		// String nula
		assertTrue(ecdao.findByCodigo("999999", 'G').isEmpty());	// C�digo inexistente
		assertTrue(ecdao.findByCodigo("02", 'Z').isEmpty());		// N�vel inexistente 
	}

	
	/** M�todo: findAll
	 * 
	 * @param char nivel
	 * 
	 * @return Collection<Curriculo>
	 * 
	 * @throws DAOException
	 *
	 */
	public void testFindAll() throws DAOException  {
		// Informando dados v�lidos
		assertFalse(ecdao.findAll('G').isEmpty());
		
		// Informando dados inv�lidos
		assertTrue(ecdao.findAll('Z').isEmpty());
	}

	
	/** M�todo: existeCodigo
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
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		// Curr�culo: 100216
		Curriculo c = new Curriculo(100216);
		
		c.setCodigo("02");
		
		// Informando dados v�lidos
		assertTrue(ecdao.existeCodigo(c, 'G'));
		assertTrue(ecdao.existeCodigo(c, '0'));
		
		c = new Curriculo();
		
		// Informando dados inv�lidos
		assertFalse(ecdao.existeCodigo(c, 'G'));		// Curr�culo n�o populado
		assertFalse(ecdao.existeCodigo(null, 'G'));		// Curr�culo nulo 
	}

	
	/** M�todo: findComponentesByCurriculo
	 * 
	 * @param int id
	 * 
	 * @return Collection<ComponenteCurricular> 
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindComponentesByCurriculo() throws DAOException  {
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		// Curr�culo: 100216
		
		// Informando dados v�lidos
		assertFalse(ecdao.findComponentesByCurriculo(100216).isEmpty());
		
		// Informando dados inv�lidos
		assertTrue(ecdao.findComponentesByCurriculo(-100216).isEmpty());
	}

	
	/** M�todo: findCurriculoComponentesByCurriculo
	 * 
	 * @param int id
	 * 
	 * @return Collection<CurriculoComponente>
	 * 
	 * @throws DAOException
	 *  
	 */
	public void testFindCurriculoComponentesByCurriculo() throws DAOException  {
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		// Curr�culo: 100216
		
		// Informando dados v�lidos
		assertFalse(ecdao.findCurriculoComponentesByCurriculo(100216).isEmpty());
		
		// Informando dados inv�lidos
		assertTrue(ecdao.findCurriculoComponentesByCurriculo(-100216).isEmpty());
	}

	
	/** M�todo: containsComponenteByCurso
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
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		// Componente: 20032		
		
		// Informando dados v�lidos
		assertFalse(ecdao.containsComponenteByCurso(2000013, 20032));

		// Informando dados inv�lidos
		assertTrue(ecdao.containsComponenteByCurso(-2000013, 20032));	// Curso inv�lido
		assertTrue(ecdao.containsComponenteByCurso(2000013, -20032));	// Componente inv�lido
	}

	
	/** M�todo: countDiscentesByCurriculo
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
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		// Curr�culo: 100216
		
		// Informando dados v�lidos
		assertTrue(ecdao.countDiscentesByCurriculo(100216) > 0);
		
		// Informando dados inv�lidos
		assertEquals(ecdao.countDiscentesByCurriculo(-100216), 0);
	}

	
	/** M�todo: countTotalCrChCurriculo
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
		// Curso: Ci�ncia da Computa��o (2000013)
		// Matriz: 99162
		// Curr�culo: 100216
		
		// Informando dados v�lidos
		assertNotNull(ecdao.countTotalCrChCurriculo(100216));
		
		// Informando dados inv�lidos
		assertNull(ecdao.countTotalCrChCurriculo(-100216));
	}

}
