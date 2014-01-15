package br.ufrn.sigaa.test.dao.ava;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;

public class AvaliacaoDaoTest extends TestCase {

	AvaliacaoDao adao = new AvaliacaoDao();
	
	/** Método: findAvaliacoesData
	 * 
	 * @param int dias
	 * @param Discente discente
	 * @param int ano
	 * @param int semestre
	 * 
	 * @return Collection<DataAvaliacao>
	 * 
	 * @throws DAOException
	 * 
	 */
	
	public void testFindAvaliacoesData() throws DAOException {
		/** O método alvo deste teste não está implementado corretamente */
	}

	
	/** Método: findByNotaUnidade
	 * 
	 * @param int idNotaUnidade
	 * 
	 * @return List<Avaliacao>
	 * 
	 * @throws DAOException
	 * 
	 */
	
	public void testFindByNotaUnidade() throws DAOException {
		// Informando id válida
		assertTrue(adao.findByNotaUnidade(285758).size() > 0);
		
		// Informando id inválida
		assertEquals(adao.findByNotaUnidade(-1).size(), 0);
	}


	/** Método: findAvaliacoes
	 * 
	 * @param Turma turma
	 * 
	 * @return List<Avaliacao>
	 * 
	 * @throws DAOException
	 * 
	 */
	 

	public void testFindAvaliacoes() throws DAOException {
		// Informando turma válida - new Turma(1141900)
		TurmaDao daoTurma = new TurmaDao();
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1141900);
		assertTrue(adao.findAvaliacoes(turma).size() > 0);
		
		// Informando turma inválida
		turma = daoTurma.findByPrimaryKeyOtimizado(986362);
		assertEquals(adao.findAvaliacoes(turma).size(), 0);
		
		// Informando Turma nula
		assertEquals(adao.findAvaliacoes(null).size(), 0);
	}


	/** Método: findNotasByMatricula
	 * 
	 * @param MatriculaComponente matricula
	 * 
	 * @return Collection<NotaUnidade>
	 * 
	 * @throws DAOException
	 * 
	 */
	 

	public void testFindNotasByMatricula() throws DAOException {
		// Informando dados válidos
		assertTrue(adao.findNotasByMatricula(new MatriculaComponente(7803647)).size() > 0);
		
		// Informando dados inválidos
		assertEquals(adao.findNotasByMatricula(new MatriculaComponente()).size(), 0);
		
		// Informando dados nulos
		assertEquals(adao.findNotasByMatricula(null).size(), 0);
	}

	
	/** Método: findNotasByTurma
	 * 
	 * @param Turma t
	 * 
	 * @return Collection<NotaUnidade>
	 * 
	 * @throws DAOException
	 * 
	 */
	 
	
	public void testFindNotasByTurma() throws DAOException {
		// Informando dados válidos
		assertTrue(adao.findNotasByTurma(new Turma(1140832)).size() > 0);
		
		// Informando dados inválidos
		assertEquals(adao.findNotasByTurma(new Turma()).size(), 0);
		
		// Informando dados nulos
		assertEquals(adao.findNotasByTurma(null).size(), 0);
	}

}