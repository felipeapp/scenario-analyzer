package br.ufrn.sigaa.test.dao.eleicao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.eleicao.CandidatoDao;

public class CandidatoDaoTest extends TestCase {

	CandidatoDao cdao = new CandidatoDao();	
	
	/** Método: findByChapaEleicao
	 * 
	 * Retorna um candidato dado a chapa e a eleicao
	 * 
	 * @param Integer chapa
	 * @param Integer idEleicao
	 * 
	 * @return Candidato
	 * 
	 * @throws DAOException
	 */
	
	public void testFindByChapaEleicao() throws DAOException {
		// Informando dados válidos
		assertNotNull(cdao.findByChapaEleicao(2, 1352270));
		
		// Informando dados inválidos
		assertNull(cdao.findByChapaEleicao(2005, 1352270)); // Chapa inválida
		assertNull(cdao.findByChapaEleicao(2, 999999999));  // idEleicao inválida
	}

}
