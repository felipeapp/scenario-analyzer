package br.ufrn.sigaa.test.dao;

import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.LinhaListaCursos;

/**
 * 
 * Teste Unitário da classe CursoDao
 * 
 * @author Dalton
 *
 */
public class CursoDaoTest extends TestCase {
	
	CursoDao dao = new CursoDao();
	
	/**
	 * Busca os cursos ATIVOS por nome
	 * 
	 * Método: findByNome
	 * 
	 * @param nome
	 * @param unid
	 * @param cursoSubClasse
	 * @param nivel
	 * @param paging
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByNomeCursoAtivo() throws DAOException {
		Collection result = dao.findByNome("Administração", 1426, null, 'G', null);
		assertNotNull(result);
		assertEquals(result.size(), 1);
		
		Collection result1 = dao.findByNome("hjk", 7726, null, 'Z', null);
		assertEquals(result1.size(), 0);
		
	}
	
	/**
	 * Busca TODOS os cursos por nome
	 * 
	 * Método: findByNome
	 * 
	 * @param nome
	 * @param unid
	 * @param cursoSubClasse
	 * @param nivel
	 * @param ativo
	 * @param paging
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByNomeCurso() throws DAOException {
		Collection result = dao.findByNome("Administração", 1426, null, 'G', true, null);
		assertNotNull(result);
		assertEquals(result.size(), 1);
	}
	
	/**
	 * Busca TODOS os cursos de um convênio para determinado nível
	 * 
	 * Método: findByConvenioAcademico
	 * 
	 * @param convenio
	 * @param nivel
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByConvenioAcademico() throws DAOException {
		Collection result = dao.findByConvenioAcademico(1, 'G');
		//System.out.println("FindByConvenioAcademico: " + result.size());
		assertNotNull(result);
		assertTrue(result.size() > 0);
	}
	
	/**
	 * Busca TODOS os cursos de uma modalidade para determinado nível
	 * 
	 * Método: findByModalidadeEducacao
	 * 
	 * @param convenio
	 * @param nivel
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByModalidadeEducacao() throws DAOException {
		Collection result = dao.findByModalidadeEducacao(1, 'G');
		//System.out.println("FindByModalidadeEducacao: " + result.size());
		assertNotNull(result);
		assertTrue(result.size() > 0);		
	}
	
	/**
	 * Busca cursos ATIVOS por Código
	 * 
	 * Método: findByCodigo
	 * 
	 * @param codigo
	 * @param unid
	 * @param cursoSubClasse
	 * @param paging
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByCodigoCursoAtivo() throws DAOException {
		Collection result = dao.findByCodigo("01", 1426, Curso.class, null);
		//System.out.println("FindByCodigoCursoAtivo: " + result.size());
		assertNotNull(result);
		assertTrue(result.size() > 0);		
		
		// testa com valor nulo
		assertTrue(dao.findByCodigo("01", 1426, null, null).isEmpty());
	}
	
	/**
	 * Busca por TODOS os cursos por codigio
	 * 
	 * Método: findByCodigo
	 * 
	 * @param codigo
	 * @param unid
	 * @param cursoSubClasse
	 * @param ativo
	 * @param paging
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByCodigoCurso() throws DAOException {
		Collection result = dao.findByCodigo("01", 1426, Curso.class, true, null);
		//System.out.println("FindByCodigoCurso: " + result.size());
		assertNotNull(result);
		assertTrue(result.size() > 0);
		
		// testa com valor nulo
		assertTrue(dao.findByCodigo("01", 1426, null, true, null).isEmpty());
	}
	
	/**
	 * Busca todos os cursos ATIVOS
	 * 
	 * Método: findAll
	 * 
	 * @param unidId
	 * @param nivel
	 * @param cursoSubClasse
	 * @param paging
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindAllCursoAtivo() throws DAOException {
		Collection result = dao.findAll(1426, 'G', Curso.class, null);
		//System.out.println("FindAllCursoAtivo: " + result.size());
		assertNotNull(result);
		assertTrue(result.size() > 0);
		
		// testa com valor nulo
		assertTrue(dao.findAll(1426, 'G', null, null).isEmpty());
	}
	
	/**
	 * Busca por TODOS os cursos
	 * 
	 * Método: findAll
	 * 
	 * @param unidId
	 * @param nivel
	 * @param cursoSubClasse
	 * @param ativo
	 * @param paging
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindAllCurso() throws DAOException {
		Collection result = dao.findAll(1426, 'G', Curso.class, true, null);
		//System.out.println("FindAllCurso: " + result.size());
		assertNotNull(result);
		assertTrue(result.size() > 0);		
	}
	
	/**
	 * verifica se já existe um curso de graduação com um determinado nome,
	 * municipio e uma das unidades sedes passadas. o id do curso é passado para
	 * caso seja maior que zero, a verificação deve considerar que exista pelo
	 * menos um curso assim.
	 *
	 * Método: existeCursoGraduacao
	 *
	 * @param nome
	 * @param idMunicipio
	 * @param unidadesSedes
	 * @param cursoId
	 * 
	 * @return boolean
	 * @throws DAOException
	 * 
	 */
		public void testExisteCursoGraduacao() throws DAOException {
		Collection cursos = dao.findByCodigo("01", 1426, Curso.class, true, null);
		Curso curso = (Curso)cursos.iterator().next();
		Boolean result = dao.existeCursoGraduacao(curso);
		assertTrue(true);
		
		// testa com valor nulo
		assertTrue(dao.existeCursoGraduacao(null));
	}
	
	/**
	 * Busca por nivel com Cursos Ativos
	 * 
	 * Método: findByNivel
	 * 
	 * @param n - nível
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByNivelCursoAtivo() throws DAOException {
		Collection result = dao.findByNivel('G');
		assertNotNull(result);
		assertTrue(result.size() > 0);		
	}
	
	/**
	 * Busca por nivel com todos os Cursos de um Centro
	 * 
	 * Método: findByNivel
	 * 
	 * @param n - nível
	 * @param centro
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByNivelCursoCentro() throws DAOException {
		UnidadeDao unidadeDao = new UnidadeDao();
		Unidade unidade = unidadeDao.findByPrimaryKey(439, Unidade.class);
		System.out.println(unidade.getCodigoNome());
		
		Collection result = dao.findByNivel('G', unidade);
		assertNotNull(result);
		assertTrue(result.size() > 0);
	}

	/**
	 * Busca por nivel com todos os Cursos com Convênio
	 * 
	 * Método: findByNivel
	 * 
	 * @param n - nível
	 * @param ativo
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByNivelCurso() throws DAOException {
		Collection result = dao.findByNivel('G', true);
		assertNotNull(result);
		assertTrue(result.size() > 0);
	}
	
	/**
	 * Busca por nivel considerando ou não os cursos com convênio
	 * 
	 * Método: findByNivel
	 * 
	 * @param n
	 * @param ativo
	 * @param convenio
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByNivelCursoConvenio() throws DAOException {
		Collection result = dao.findByNivel('G', true, true);
		assertNotNull(result);
		assertTrue(result.size() > 0);
	}
	
	/**
	 * Busca por nivel considerando ou não os cursos com convênio e por Centro
	 * 
	 * Método: findByNivel
	 * 
	 * @param n
	 * @param ativo
	 * @param convenio
	 * @param centro (unidade)
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByNivelCursoConvenioCentro() throws DAOException {
		UnidadeDao unidadeDao = new UnidadeDao();
		Unidade unidade = unidadeDao.findByPrimaryKey(439, Unidade.class);
		
		Collection result = dao.findByNivel('G', true, true, unidade);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		
		// testa com valor nulo
		assertTrue(dao.findByNivel('G', true, true, null).size() > 0);
	}
	
	
	/**
	 * retorna todos os cursos de um centro
	 * 
	 * Método: findByCentro
	 * 
	 * @param idUnidade
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByCentro() throws DAOException {
		Collection result = dao.findByCentro(1426);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		
		assertTrue((dao.findByCentro(8888)).size() == 0);
	}
	
	/**
	 * retorna os cursos de um centro para um determinado nível
	 * 
	 * Método: findByCentro
	 * 
	 * @param idUnidade
	 * @param nivel
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByCentroNivel() throws DAOException {
		Collection result = dao.findByCentro(1426, 'G');
		assertNotNull(result);
		assertTrue(result.size() > 0);	
		
		assertTrue((dao.findByCentro(8888, 'x')).size() == 0);
	}
	
	/**
	 * retorna os cursos de um centro para um determinado nível
	 * 
	 * Método: findByUnidade
	 * 
	 * @param idUnidade
	 * @param nivel
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindByUnidade() throws DAOException {
		Collection result = dao.findByUnidade(1426, 'G');
		assertNotNull(result);
		assertTrue(result.size() > 0);
		
		assertTrue((dao.findByUnidade(8888, 'x')).size() == 0);
	}
	
	/**
	 * 
	 * ================Teste com ERRO.
	 * 
	 * Busca cursos por centro de acordo com o nível.
	 * 
	 * Método: findCursosCentro
	 * 
	 * @param nivel
	 * 
	 * @return Map<Unidade, LinhaListaCursos>
	 * @throws DAOException
	 * 
	 */
	public void testFindCursosCentro() throws DAOException {
		
		Map<Unidade, LinhaListaCursos> result = dao.findCursosCentro('G');
		assertTrue(!result.isEmpty());
		
		Map<Unidade, LinhaListaCursos> result1 = dao.findCursosCentro('Z');
		assertTrue(result1.isEmpty());
	}
	
	/**
	 * retorna todos os cursos à distância
	 * 
	 * Método: findAllCursosADistancia
	 * 
	 * @return List<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindAllCursosADistancia() throws DAOException {
		Collection result = dao.findAllCursosADistancia();
		assertNotNull(result);
		assertTrue(result.size() > 0);
	}
	
	/**
	 * Teste retorna nada.
	 * 
	 * retorna os coordenadores atuais dos cursos de acordo com o nível
	 * 
	 * Método: findAllOtimizado
	 * 
	 * @param nivel
	 * 
	 * @return Collection<Curso>
	 * @throws DAOException
	 * 
	 */
	public void testFindAllOtimizado() throws DAOException {
		Collection result = dao.findAllOtimizado('G', null);
		//System.out.println("DD1 - " + result.size());
		assertNotNull(result);
		assertTrue(result.size() > 0);
		//System.out.println("DD - " + dao.findAllOtimizado('x'));
		assertTrue((dao.findAllOtimizado('x', null)).size() == 0);
	}
	
	/**
	 * Teste não retorna nada
	 * 
	 * retorna os cursos de Stricto Senso ativos
	 * 
	 * Método: findByPrograma
	 * 
	 * @param idUnidade
	 * @return Collection<Curso>
	 * @throws DAOException
	 */
	public void testFindByPrograma() throws DAOException {
		Collection result = dao.findByPrograma(2598);
		assertTrue(result.size()==0);
	}
	
	/**
	 * retorna os cursos de Stricto Senso ativos
	 * 
	 * Método: findConsultaPublica
	 * 
	 * @param idUnidade
	 * @return Collection<Curso>
	 * @throws DAOException
	 */
	public void testFindConsultaPublica() throws DAOException {
		ModalidadeEducacao modalidade = new ModalidadeEducacao();
		modalidade.setId(1);
		Collection result = dao.findConsultaPublica(null,'G', "ENGENHARIA CIVIL", modalidade);
		assertTrue(result.size() > 0);
		
		// testa com valor nulo
		assertTrue(dao.findConsultaPublica(null,'G', "ENGENHARIA CIVIL", null).size() > 0);
	}
	
}
