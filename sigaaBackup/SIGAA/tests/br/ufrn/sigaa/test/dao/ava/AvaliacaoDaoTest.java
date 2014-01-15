package br.ufrn.sigaa.test.dao.ava;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;

public class AvaliacaoDaoTest extends TestCase {

	AvaliacaoDao adao = new AvaliacaoDao();
	
	/** M�todo: findAvaliacoesData
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
		/** O m�todo alvo deste teste n�o est� implementado corretamente */
	}

	
	/** M�todo: findByNotaUnidade
	 * 
	 * @param int idNotaUnidade
	 * 
	 * @return List<Avaliacao>
	 * 
	 * @throws DAOException
	 * 
	 */
	
	public void testFindByNotaUnidade() throws DAOException {
		// Informando id v�lida
		assertTrue(adao.findByNotaUnidade(285758).size() > 0);
		
		// Informando id inv�lida
		assertEquals(adao.findByNotaUnidade(-1).size(), 0);
	}


	/** M�todo: findAvaliacoes
	 * 
	 * @param Turma turma
	 * 
	 * @return List<Avaliacao>
	 * 
	 * @throws DAOException
	 * 
	 */
	 

	public void testFindAvaliacoes() throws DAOException {
		// Informando turma v�lida - new Turma(1141900)
		TurmaDao daoTurma = new TurmaDao();
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1141900);
		assertTrue(adao.findAvaliacoes(turma).size() > 0);
		
		// Informando turma inv�lida
		turma = daoTurma.findByPrimaryKeyOtimizado(986362);
		assertEquals(adao.findAvaliacoes(turma).size(), 0);
		
		// Informando Turma nula
		assertEquals(adao.findAvaliacoes(null).size(), 0);
	}


	/** M�todo: findNotasByMatricula
	 * 
	 * @param MatriculaComponente matricula
	 * 
	 * @return Collection<NotaUnidade>
	 * 
	 * @throws DAOException
	 * 
	 */
	 

	public void testFindNotasByMatricula() throws DAOException {
		// Informando dados v�lidos
		assertTrue(adao.findNotasByMatricula(new MatriculaComponente(7803647)).size() > 0);
		
		// Informando dados inv�lidos
		assertEquals(adao.findNotasByMatricula(new MatriculaComponente()).size(), 0);
		
		// Informando dados nulos
		assertEquals(adao.findNotasByMatricula(null).size(), 0);
	}

	
	/** M�todo: findNotasByTurma
	 * 
	 * @param Turma t
	 * 
	 * @return Collection<NotaUnidade>
	 * 
	 * @throws DAOException
	 * 
	 */
	 
	
	public void testFindNotasByTurma() throws DAOException {
		// Informando dados v�lidos
		assertTrue(adao.findNotasByTurma(new Turma(1140832)).size() > 0);
		
		// Informando dados inv�lidos
		assertEquals(adao.findNotasByTurma(new Turma()).size(), 0);
		
		// Informando dados nulos
		assertEquals(adao.findNotasByTurma(null).size(), 0);
	}

}