package br.ufrn.sigaa.test.dao.ava;

import java.util.List;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.EnqueteDao;
import br.ufrn.sigaa.ava.dominio.Enquete;
import br.ufrn.sigaa.ava.dominio.EnqueteResposta;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * 
 * Teste Unitário da classe EnqueteDao
 * 
 * @author Dalton
 *
 */

public class EnqueteDaoTest extends TestCase{
	
	EnqueteDao dao = new EnqueteDao();

	TurmaDao daoTurma = new TurmaDao();
	
	/** Método: findEnquetesByTurma
	 * 
	 * @param turma
	 * 
	 * @return List<Enquete>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindEnquetesByTurma() throws DAOException {

		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);  
		//System.out.println("Turma: " + turma.getCodigo() + " anosem: " + turma.getAnoPeriodo() + " - " + turma.getCodigo());
		List<Enquete> result = dao.findEnquetesByTurma(turma);
		System.out.println("total de enquetes: " + result.size());
		assertNotNull(result);
		assertTrue(result.size() > 0);  // uma enquete
		
		// passando valores nulos
		assertEquals(0, dao.findEnquetesByTurma(null).size());
	}

	/** 
	 * Método: findEnqueteMaisAtualByTurma
	 * 
	 * @param turma
	 * 
	 * @return Enquete
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindEnqueteMaisAtualByTurma() throws DAOException {

		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);  
		//System.out.println("Turma: " + turma.getCodigo() + " anosem: " + turma.getAnoPeriodo() + " - " + turma.getCodigo());
		
		Enquete enquete = dao.findEnqueteMaisAtualByTurma(turma, true);
		System.out.println("Enquete: " + enquete.getId() + " - " + enquete.getPergunta());
		
		assertNotNull(enquete);
		
		// passando valores nulos
		assertNull(dao.findEnqueteMaisAtualByTurma(null, true));
	}

	/**
	 * Verifica se um determinado usuario já votou na enquete
	 * 
	 *  Método: getRespostaUsuarioEnquete
	 * 
	 * @param usuario
	 * @param enquete
	 * 
	 * @return EnqueteResposta
	 * 
	 * @throws DAOException
	 * 
	 */ 
	 
	public void testGetRespostaUsuarioEnquete() throws DAOException {
		UsuarioDao daoUsuario = new UsuarioDao();
		Usuario usuario = (Usuario)daoUsuario.findByPrimaryKey(3634);
		//System.out.println("Usuário: " + usuario.getLogin());
		
		TurmaDao daoTurma = new TurmaDao();
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);  
		//System.out.println("Turma: " + turma.getCodigo() + " anosem: " + turma.getAnoPeriodo() + " - " + turma.getCodigo());
		
		Enquete enquete = dao.findEnqueteMaisAtualByTurma(turma, true);
		//System.out.println("Enquete: " + enquete.getPergunta());
		
		EnqueteResposta enqueteResposta = dao.getRespostaUsuarioEnquete(usuario, enquete);
		//System.out.println("Resposta da Enquete: " + enqueteResposta.getResposta());
		
		assertNotNull(enqueteResposta);
		
		// passando valores nulos
		assertNull(dao.getRespostaUsuarioEnquete(null, null));
		
		
	}

	/**
	 * Faz uma estatistica de quantos votos cada opção da enquete tem e
	 * retorna varios objetos EnqueteResposta com a propriedade TotalVotos setada.
	 * 
	 * 
	 *  Método: findEstatisticaDeVotosbyEnquete
	 * 
	 * @param enquete
	 * 
	 * @return List<EnqueteResposta>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindEstatisticaDeVotosbyEnquete() throws DAOException {

		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);
		
		
		Enquete enquete = dao.findEnqueteMaisAtualByTurma(turma, true);
			
		List<EnqueteResposta> listaEnquete = dao.findEstatisticaDeVotosbyEnquete(enquete);
		
		//for (EnqueteResposta enqueteResposta : listaEnquete) {
		//	System.out.println(enqueteResposta.getResposta() + " - " + enqueteResposta.getTotalVotos());
		//}
		
		assertNotNull(listaEnquete);
		assertTrue(listaEnquete.size() > 0);
		
		// passando valores nulos
		assertEquals(0, dao.findEstatisticaDeVotosbyEnquete(null).size());
		
	}

	/**
	 * Diz quantos votos uma enquete teve
	 * 
	 *  Método: findTotalVotosbyEnquete
	 * 
	 * @param enquete
	 * 
	 * @return int
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindTotalVotosbyEnquete() throws DAOException {

		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);
		
		Enquete enquete = dao.findEnqueteMaisAtualByTurma(turma, true);
		
		int result = Integer.parseInt(dao.findTotalVotosbyEnquete(enquete));
		System.out.println("Total de votos da Enquete (" + enquete.getId() + "): " + result);
		
		assertTrue(result > 0);

		// passando valores nulos
		assertEquals(0, dao.findTotalVotosbyEnquete(null));
	}

	/**
	 *  Método: findRespostasByEnquete
	 * 
	 * @param idEnquete
	 * 
	 * @return List<EnqueteResposta>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindRespostasByEnquete() throws DAOException {
	
		List<EnqueteResposta> result = dao.findRespostasByEnquete(2622890);
		
		EnqueteResposta er = result.iterator().next();
		System.out.println("Tamanho da lista de respostas: " + result.size() + "er_id: " + er.getId());
		
		assertNotNull(result);
		assertTrue(result.size() > 0);
		
		// passando valores inválidos
		assertEquals(0, dao.findRespostasByEnquete(-1).size());
	}
	
	/**
	 *  Método: findResposta
	 * 
	 * @param idEnqueteResposta
	 * 
	 * @return EnqueteResposta
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindResposta() throws DAOException {
		assertNotNull(dao.findResposta(2622900));
		
		// passando dados inválidos
		assertNull(dao.findResposta(-1));
	}

}
