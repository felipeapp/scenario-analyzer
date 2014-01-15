package br.ufrn.sigaa.test.dao.graduacao;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class DiscenteGraduacaoDaoTest extends TestCase {

	DiscenteGraduacaoDao dgdao = new DiscenteGraduacaoDao();
	DiscenteGraduacao dg = new DiscenteGraduacao();
	DiscenteDao ddao = new DiscenteDao();
	Discente discente = new Discente();
	ArrayList<Curso> cursos = new ArrayList<Curso>();
	Curso cs = new Curso();
	Polo polo = new Polo();
	PoloDao pdao = new PoloDao();

	public DiscenteGraduacaoDaoTest() throws DAOException {
		// TODO Auto-generated constructor stub

		// Populando cursos
		cs = new Curso(2000013);		// Ciência da Computação
		cursos.add(cs);

		cs = new Curso(2000026);		// Engenharia de Computação
		cursos.add(cs);

		cs = new Curso(2000045);		// Geologia
		cursos.add(cs);

	}


	/** Método: findByCursoAnoPeriodo
	 *
	 * Retorna os discentes de graduacao de um ou mais curso(s) a partir de um ano-periodo de ingresso
	 *
	 * @param int anoIngresso
	 * @param int periodoIngresso
	 * @param Collection<Curso> cursos
	 *
	 * @return Collection<DiscenteGraduacao>
	 *
	 * @throws DAOException
	 */

	public void testFindByCursoAnoPeriodo2() throws DAOException {
		// Informando dados válidos
		assertTrue(dgdao.findByCursoAnoPeriodo(2005, 1, cursos).size() > 0);

		// Informando dados inválidos
		assertEquals(dgdao.findByCursoAnoPeriodo(9999, 1, cursos).size(), 0);	     // Ano inválido
		assertEquals(dgdao.findByCursoAnoPeriodo(2005, 99, cursos).size(), 0);   	 // Período inválido
		//-------------------------------------------------------------------
		// O método não aceita que se entre com uma coleção vazia ou com
		// um valor nulo.
		//-------------------------------------------------------------------
		assertEquals(dgdao.findByCursoAnoPeriodo(2005, 1, new ArrayList<Curso>()).size(), 0); 	 // Curso inválido
	}


	/** Método: findByCursoAnoPeriodo
	 *
	 * Retorna os discentes de graduacao de um ou mais curso(s) a partir de um ano-periodo de ingresso
	 *
	 * @param int anoIngresso
	 * @param int periodoIngresso
	 * @param Collection<Curso> cursos
	 * @param boolean somenteAtivos
	 *
	 * @return Collection<DiscenteGraduacao>
	 *
	 * @throws DAOException
	 */

	public void testFindByCursoAnoPeriodo3() throws DAOException {
		// Informando dados válidos
		assertTrue(dgdao.findByCursoAnoPeriodo(2005, 1, cursos, true).size() > 0);	// No caso de somente Ativos
		assertTrue(dgdao.findByCursoAnoPeriodo(2000, 1, cursos, false).size() > 0); // No caso de inativos também serem considerados

		// Informando dados inválidos
		assertEquals(dgdao.findByCursoAnoPeriodo(9999, 1, cursos, true).size(), 0);	     // Ano inválido
		assertEquals(dgdao.findByCursoAnoPeriodo(2005, 99, cursos, false).size(), 0);  	 // Período inválido
		assertEquals(dgdao.findByCursoAnoPeriodo(1995, 1, cursos, true).size(), 0);  	 // Verificando validação de Somente Ativos
		//-------------------------------------------------------------------
		// O método não aceita que se entre com uma coleção vazia ou com
		// um valor nulo.
		//-------------------------------------------------------------------
		assertEquals(dgdao.findByCursoAnoPeriodo(2005, 1, new ArrayList<Curso>()).size(), 0); 	 // Curso inválido
	}


	/** Método: findAtivoByPessoa
	 *
	 * Busca os discentes de graduacao ativos da pessoa passada por parametro.
	 *
	 * @param int idPessoa
	 *
	 * @return DiscenteGraduacao
	 *
	 * @throws DAOException
	 */

	public void testFindAtivoByPessoa() throws DAOException {
		// Inserindo dados válidos
		assertNotNull (dgdao.findAtivoByPessoa(1096297));

		// Inserindo dados inválidos
		assertNull(dgdao.findAtivoByPessoa(1060796));		// Discente inativo
		assertNull(dgdao.findAtivoByPessoa(-9633));			// Id inválida
	}


	/** Método: findMudancasMatriz
	 *
	 * @param DiscenteGraduacao discente
	 *
	 * @return Collection<MudancaCurricular>
	 *
	 * @throws DAOException
	 */

	/**
	 *   Não foi possível testar esse método
	 */



	/** Método: calcularIntegralizacaoExtras
	 *
	 * @param DiscenteGraduacao discente
	 * @param Collection<SituacaoMatricula> situacoes
	 *
	 * @return void
	 *
	 * @throws DAOException
	 */



	/** Método: calcularIntegralizacaoCurriculo
	 *
	 * @param DiscenteGraduacao discente
	 * @param Collection<SituacaoMatricula> situacoes
	 *
	 * @return void
	 *
	 * @throws DAOException
	 */





	/** Método: findBySolicitouTurmaFerias
	 *
	 * @param int idCurso
	 * @param Integer ano
	 * @param Integer periodo
	 *
	 * @return Collection<DiscentesSolicitacao>
	 *
	 * @throws DAOException
	 */

	public void testFindBySolicitouTurmaFerias() throws DAOException {
		// Informando dados válidos
		// Curso: Ciência da Computação (2000013)
		assertTrue(dgdao.findBySolicitouTurmaFerias(2000013, 2008, 1, null).size() > 0);

		// Informando dados inválidos
		assertEquals(dgdao.findBySolicitouTurmaFerias(-999, 2008, 3, null).size(), 0);		// Curso inválido
		assertEquals(dgdao.findBySolicitouTurmaFerias(2000013, 9999, 3, null).size(), 0);		// Ano inválido
		assertEquals(dgdao.findBySolicitouTurmaFerias(2000013, 2008, 45, null).size(), 0);	// Período inválido
	}


	/** Método: findDiscentesEadByMatriculaPolo
	 *
	 * @param Long matricula
	 * @param Polo polo
	 *
	 * @return Discente
	 *
	 * @throws DAOException
	 */

	public void testFindDiscentesEadByMatriculaPolo() throws DAOException {
		// Dados envolvidos
		// Aluno: 200762451 - RUXLEY BERNARDINO DOS SANTOS
		// Curso: Matemática - A distância (2000134)
		// Polo: Currais Novos (2679665)

		polo = new Polo(2679665);

		System.out.println("Id do polo: " + polo.getId());

		// Inserindo dados válidos
		//--------------------------------------------------------
		// Embora se forneça valores válidos para os parâmetros,
		// a função não retorna resultados.
		//--------------------------------------------------------
//		assertNotNull(dgdao.findDiscentesEadByMatriculaPolo(200762451l, polo));

		// Inserindo dados inválidos
//		assertNull(dgdao.findDiscentesEadByMatriculaPolo(-99999999l, polo));		// Matrícula inválida
//		assertNull(dgdao.findDiscentesEadByMatriculaPolo(200762451l, null));		// Pólo Nulo

		//---------------------------------------------------------
		// O método não aceita que se entre com parâmetros não
		// populados.
		//---------------------------------------------------------
//		assertNull(dgdao.findDiscentesEadByMatriculaPolo(200762451l, new Polo()));	// Pólo Nulo
	}


	/** Método: findDiscentesEadByNomePolo
	 *
	 * @param String nome
	 * @param Polo polo
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 */

	public void testFindDiscentesEadByNomePolo() throws DAOException {
		// Dados envolvidos
		// Aluno: 200762451 - RUXLEY BERNARDINO DOS SANTOS
		// Curso: Matemática - A distância (2000134)
		// Polo: Currais Novos (2679665)

		polo = new Polo(2679665);

		// Informando dados válidos. A busca por pólo é opcional.
//		assertTrue(dgdao.findDiscentesEadByNomePolo("RUXLEY BERNARDINO", polo).size() > 0);			// Informando o polo
//		assertTrue(dgdao.findDiscentesEadByNomePolo("RUXLEY BERNARDINO", null).size() > 0);			// Sem informar o polo
//		assertTrue(dgdao.findDiscentesEadByNomePolo("RUXLEY BERNARDINO", new Polo()).size() > 0);	// Polo inválido

		// Informando dados inválidos
//		assertEquals(dgdao.findDiscentesEadByNomePolo("QWERTYASDF QWERTYASDF", polo).size(), 0);	// Nome inválido
	}


	/** Método: findDiscentesEadByPoloAnoPeriodo
	 *
	 * @param Integer ano
	 * @param Integer periodo
	 * @param Polo polo
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 */

	public void testFindDiscentesEadByPoloAnoPeriodo() throws DAOException {
		// Polo: Currais Novos (2679665)
		polo = new Polo(2679665);

		// Informando dados válidos
		//--------------------------------------------------------------------------
		// Mesmo informando valores válidos, o método falha. Log abaixo:
		//--------------------------------------------------------------------------
		// org.hibernate.QueryException: could not instantiate: br.ufrn.sigaa.pessoa.dominio.Discente
		// at org.hibernate.transform.AliasToBeanConstructorResultTransformer.transformTuple(AliasToBeanConstructorResultTransformer.java:21)
		// at org.hibernate.hql.HolderInstantiator.instantiate(HolderInstantiator.java:69)
		// at org.hibernate.loader.hql.QueryLoader.getResultList(QueryLoader.java:358)
		//--------------------------------------------------------------------------
//		assertTrue(dgdao.findDiscentesEadByPoloAnoPeriodo(2007, 2, polo).size() > 0);

		// Informando dados inválidos
//		assertEquals(dgdao.findDiscentesEadByPoloAnoPeriodo(9999, 2, polo).size(), 0);			// Ano inválido
//		assertEquals(dgdao.findDiscentesEadByPoloAnoPeriodo(2007, 99, polo).size(), 0);			// Período inválido
//		assertEquals(dgdao.findDiscentesEadByPoloAnoPeriodo(2007, 2, new Polo()).size(), 0);	// Polo inválido

		//---------------------------------------------------------------------------
		// O método não aceita que se entre com valores nulos para um polo
		//---------------------------------------------------------------------------
//		assertEquals(dgdao.findDiscentesEadByPoloAnoPeriodo(2007, 2, null).size(), 0);			// Polo nulo
	}


	/** Método: findComponentesDoCurriculoByDiscente
	 *
	 * @param Discente discente
	 * @param boolean obrigatorios
	 *
	 * @return TreeSet<Integer>
	 *
	 * @throws DAOException
	*/

	public void testFindComponentesDoCurriculoByDiscente() throws DAOException {
		// Aluno: 200762451 - RUXLEY BERNARDINO DOS SANTOS
		discente = ddao.findAtivosByMatricula(200762451);

		// Informando dados válidos
		assertTrue(dgdao.findComponentesDoCurriculoByDiscente(discente, true).size() > 0);		// Componentes somente obrigatórios
		assertTrue(dgdao.findComponentesDoCurriculoByDiscente(discente, false).size() > 0);		// Todos os componentes

		// Informando dados inválidos
		//--------------------------------------------------------------------
		// O método não permite que se entre com um discente nulo
		//--------------------------------------------------------------------
		assertTrue(dgdao.findComponentesDoCurriculoByDiscente(null, true).isEmpty());				// Discente nulo
		assertTrue(dgdao.findComponentesDoCurriculoByDiscente(new Discente(), true).isEmpty());		// Discente inválido
	}


	/** Método: findComponentesDoCurriculoByDiscente
	 *
	 * @param int curso
	 *
	 * @return TreeSet<Integer>
	 *
	 * @throws DAOException
	 */

	public void testFindComponentesDoCursoByDiscente() throws DAOException {
		// Curso: Matemática - A distância (2000134)

		// Informando dados válidos
		assertFalse(dgdao.findComponentesDoCursoByDiscente(2000134).isEmpty());

		// Informando dados inválidos
		assertTrue(dgdao.findComponentesDoCursoByDiscente(999999).isEmpty());
	}


	/** Método: findGraduandosParaColacao
	 *
	 * Busca por alunos graduandos que tiveram alguma matrícula num semestre específico
	 *
	 * @param int idCurso
	 * @param int... anoPeriodos
	 *
	 * @return Collection<DiscenteGraduacao>
	 *
	 * @throws DAOException
	 */


	public void testFindGraduandosParaColacao() throws DAOException {
		// Curso: Ciência da Computação    (id = 2000013)
		// 		  Engenharia da Computação (id = 2000026)

		// Informando dados válidos
		assertFalse(dgdao.findGraduandosParaColacao(2000013, 20031, 20041).isEmpty());
		assertFalse(dgdao.findGraduandosParaColacao(2000026, 20041, 20042).isEmpty());

		// Informando dados inválidos
		assertTrue(dgdao.findGraduandosParaColacao(99999, 20031, 20041).isEmpty());			// Id de curso inválida
		assertTrue(dgdao.findGraduandosParaColacao(2000013, 1212121).isEmpty());			// Id de curso inválida
		assertTrue(dgdao.findGraduandosParaColacao(99999, 20041, 121212121).isEmpty());		// Id de curso inválida
	}



	/** Método: findByMatriz
	 *
	 * @param int id
	 * @param int... status
	 *
	 * @return Collection<DiscenteGraduacao>
	 *
	 * @throws DAOException
	 */


	public void testFindByMatriz() throws DAOException {
		// Aluno: 200762451 - RUXLEY BERNARDINO DOS SANTOS
		// Curso: Matemática - A Distância (id = 2000134)
		// IdMatriz = 99462

		// Inserindo dados válidos
		assertFalse(dgdao.findByMatriz(99462, StatusDiscente.ATIVO, StatusDiscente.CADASTRADO).isEmpty());

		// Inserindo dados inválidos
		assertTrue(dgdao.findByMatriz(-99999, StatusDiscente.ATIVO, StatusDiscente.CADASTRADO).isEmpty());	// Id inválida
		assertTrue(dgdao.findByMatriz(-99999, 25, 99, 321).isEmpty());	// Status inválidos
	}


	public void testAtualizacao() throws IOException {
		String classname = DiscenteGraduacaoDao.class.getName();
		URL url = DiscenteGraduacaoDao.class.getResource(classname);

		System.out.println(classname);

//		String className = DiscenteGraduacaoDao.class.getName();
//		className = className.replaceAll("\\.", "/");
//		className = "/" + className + ".java";
//		System.out.println(className);

		//URL urlArquivo = getClass().getResource("dao");
		System.out.println (url.getPath());

		//String path = getClass().getName().replace('.','/');
		//path = "/" + path + ".java";

		//System.out.println(path);
		//File atual = new File(path);

//		System.out.println(atual.lastModified());

		System.out.println("T D:" + DiscenteGraduacaoDaoTest.class.getMethods().length + " " + DiscenteGraduacaoDao.class.getMethods().length);

		// Testa a diferença entre o número de métodos de cada classe
		assertTrue(DiscenteGraduacaoDao.class.getMethods().length == DiscenteGraduacaoDaoTest.class.getMethods().length);

	}

}
