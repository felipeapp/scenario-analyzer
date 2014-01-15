package br.ufrn.sigaa.test.dao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.PortaArquivosDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * 
 * @author Rafael Buriti / Dalton
 *
 */
public class PortaArquivosDaoTest extends TestCase{
	
	PortaArquivosDao p = new PortaArquivosDao();
	
	UsuarioDao usuarioDao = new UsuarioDao();
	
	/**
	 * Busca os arquivos do usuario passado como parametro
	 * 
	 * M�todo: findPastasByUsuario
	 * 
	 * @param usuario
	 * 
	 * @return List<PastaArquivos>
	 * @throws DAOException
	 */

	public void testFindPastasByUsuario() throws DAOException{
		// o arquivo PastaArquivos, no banco est� vazio.
		
		Usuario usuario = usuarioDao.findByPrimaryKey(4236);
		
		assertNotNull(p.findPastasByUsuario(usuario));
		assertTrue((p.findPastasByUsuario(usuario)).size() > 0);
		
		// passando valor nulo
		assertTrue(p.findPastasByUsuario(null).size() == 0);
		
	}
	
	/**
	 * Busca os arquivos do usuario passado como parametro que est�o na turma passada como parametro
	 * 
	 * M�todo: findPastasByUsuario
	 * 
	 * @param usuario
	 * @param turma
	 * 
	 * @return List<PastaArquivos>
	 * @throws DAOException
	 */
	public void testFindPastasByUsuario2() throws DAOException{

		TurmaDao turmaDao = new TurmaDao();
		
		Usuario usuario = usuarioDao.findByPrimaryKey(4236);
		Turma turma = turmaDao.findByPrimaryKey(57232018, Turma.class);
		
		assertNotNull(p.findPastasByUsuario(usuario, turma));
		System.out.println("N�MERO DE PASTAS: " + (p.findPastasByUsuario(usuario, turma)).size());
		assertTrue((p.findPastasByUsuario(usuario, turma)).size() > 0);
		
		// passando valores nulos
		assertTrue(p.findPastasByUsuario(null, null).size() == 0);
	}
	
	/**
	 * Busca os arquivos do usuario passado como parametro que est�o na pasta passa como parametro, 
	 * se for passada a pasta com o id = -1, retornar� os arquivos do usuario q n�o tenham pasta.
	 * 
	 * M�todo: findArquivosByPasta
	 * 
	 * @param idPasta
	 * @param idUsuario
	 * 
	 * @return List<ArquivoUsuario>
	 * @throws DAOException
	 */
	public void testFindArquivosByPasta() throws DAOException{
		
		assertNotNull(p.findArquivosByPasta(-1, 4236));
		assertTrue((p.findArquivosByPasta(-1, 4236)).size() == 1);
		
		assertNotNull(p.findArquivosByPasta(2580140, 4236));
		assertTrue((p.findArquivosByPasta(2580140, 4236)).size() == 10);

		// passando par�metros inv�lidos
		assertTrue(p.findArquivosByPasta(-1, -1).size() == 0);
		
	}
	
	/**
	 * Busca o espaco ocupado dos arquivos do usuario passado como parametro
	 * 
	 * M�todo: findTotalOcupadoByUsuario
	 * 
	 * @param idUsuario
	 * 
	 * @return long
	 * @throws DAOException
	 */
	public void testFindTotalOcupadoByUsuario() throws DAOException{
		
		assertNotNull(p.findTotalOcupadoByUsuario(4236));
		assertTrue(p.findTotalOcupadoByUsuario(4236) == Long.valueOf("61803040"));
		
		//passando idUsuario = 0
		assertTrue(p.findTotalOcupadoByUsuario(0) == 0);
		
	}
	
	/**
	 * Busca os arquivos do usuario passado como parametro que tenham o respectivo nome 
	 * tambem passado como parametro. E se est� em uma pasta ou n�o.
	 * 
	 * M�todo: findPastaByUsuarioNome
	 * 
	 * @param usuario
	 * @param nome (String)
	 * @param pai (PastaArquivos)
	 * 
	 * @return PastaArquivos
	 * @throws DAOException
	 */
	public void testFindPastaByUsuarioNome() throws DAOException{

		Usuario usuario = usuarioDao.findByPrimaryKey(3424);
		PastaArquivos pastaArquivo = p.findByPrimaryKey(2642546, PastaArquivos.class);
	
		assertNotNull(p.findPastaByUsuarioNome(usuario, "CORES", pastaArquivo));
		assertTrue((p.findPastaByUsuarioNome(usuario, "CORES", pastaArquivo)).getId() == 2814313);
		
		// passando valores nulos
		assertNull(p.findPastaByUsuarioNome(null, "", null));
		
	}
	
	/**
	 * Retorna o arquivo da turna passando como parametro o arquivo do usu�rio
	 * 
	 * M�todo: findArquivosTurmaByArquivo
	 * 
	 * @param arquivo (ArquivoUsuario)
	 * 
	 * @return List<ArquivoTurma>
	 * @throws DAOException
	 */
//	
	public void testFindArquivosTurmaByArquivo() throws DAOException{
		
		GenericSigaaDAO genDAO = new GenericSigaaDAO();
		try{
		
			ArquivoUsuario arquivo = genDAO.findByPrimaryKey(2580131, ArquivoUsuario.class);
		
			assertNotNull(p.findArquivosTurmaByArquivo(arquivo));
			assertTrue((p.findArquivosTurmaByArquivo(arquivo)).size() == 1);
			
			assertNull(p.findArquivosTurmaByArquivo(null));
			
		}finally{
			genDAO.close();
		}
		
	}
}
