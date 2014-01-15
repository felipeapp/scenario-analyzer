package br.ufrn.sigaa.test.dao.eleicao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.eleicao.CandidatoDao;

public class CandidatoDaoTest extends TestCase {

	CandidatoDao cdao = new CandidatoDao();	
	
	/** M�todo: findByChapaEleicao
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
		// Informando dados v�lidos
		assertNotNull(cdao.findByChapaEleicao(2, 1352270));
		
		// Informando dados inv�lidos
		assertNull(cdao.findByChapaEleicao(2005, 1352270)); // Chapa inv�lida
		assertNull(cdao.findByChapaEleicao(2, 999999999));  // idEleicao inv�lida
	}

}
