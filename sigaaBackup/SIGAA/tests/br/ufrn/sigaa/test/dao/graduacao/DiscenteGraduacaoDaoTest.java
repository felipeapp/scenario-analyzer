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
		cs = new Curso(2000013);		// Ci�ncia da Computa��o
		cursos.add(cs);

		cs = new Curso(2000026);		// Engenharia de Computa��o
		cursos.add(cs);

		cs = new Curso(2000045);		// Geologia
		cursos.add(cs);

	}


	/** M�todo: findByCursoAnoPeriodo
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
		// Informando dados v�lidos
		assertTrue(dgdao.findByCursoAnoPeriodo(2005, 1, cursos).size() > 0);

		// Informando dados inv�lidos
		assertEquals(dgdao.findByCursoAnoPeriodo(9999, 1, cursos).size(), 0);	     // Ano inv�lido
		assertEquals(dgdao.findByCursoAnoPeriodo(2005, 99, cursos).size(), 0);   	 // Per�odo inv�lido
		//-------------------------------------------------------------------
		// O m�todo n�o aceita que se entre com uma cole��o vazia ou com
		// um valor nulo.
		//-------------------------------------------------------------------
		assertEquals(dgdao.findByCursoAnoPeriodo(2005, 1, new ArrayList<Curso>()).size(), 0); 	 // Curso inv�lido
	}


	/** M�todo: findByCursoAnoPeriodo
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
		// Informando dados v�lidos
		assertTrue(dgdao.findByCursoAnoPeriodo(2005, 1, cursos, true).size() > 0);	// No caso de somente Ativos
		assertTrue(dgdao.findByCursoAnoPeriodo(2000, 1, cursos, false).size() > 0); // No caso de inativos tamb�m serem considerados

		// Informando dados inv�lidos
		assertEquals(dgdao.findByCursoAnoPeriodo(9999, 1, cursos, true).size(), 0);	     // Ano inv�lido
		assertEquals(dgdao.findByCursoAnoPeriodo(2005, 99, cursos, false).size(), 0);  	 // Per�odo inv�lido
		assertEquals(dgdao.findByCursoAnoPeriodo(1995, 1, cursos, true).size(), 0);  	 // Verificando valida��o de Somente Ativos
		//-------------------------------------------------------------------
		// O m�todo n�o aceita que se entre com uma cole��o vazia ou com
		// um valor nulo.
		//-------------------------------------------------------------------
		assertEquals(dgdao.findByCursoAnoPeriodo(2005, 1, new ArrayList<Curso>()).size(), 0); 	 // Curso inv�lido
	}


	/** M�todo: findAtivoByPessoa
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
		// Inserindo dados v�lidos
		assertNotNull (dgdao.findAtivoByPessoa(1096297));

		// Inserindo dados inv�lidos
		assertNull(dgdao.findAtivoByPessoa(1060796));		// Discente inativo
		assertNull(dgdao.findAtivoByPessoa(-9633));			// Id inv�lida
	}


	/** M�todo: findMudancasMatriz
	 *
	 * @param DiscenteGraduacao discente
	 *
	 * @return Collection<MudancaCurricular>
	 *
	 * @throws DAOException
	 */

	/**
	 *   N�o foi poss�vel testar esse m�todo
	 */



	/** M�todo: calcularIntegralizacaoExtras
	 *
	 * @param DiscenteGraduacao discente
	 * @param Collection<SituacaoMatricula> situacoes
	 *
	 * @return void
	 *
	 * @throws DAOException
	 */



	/** M�todo: calcularIntegralizacaoCurriculo
	 *
	 * @param DiscenteGraduacao discente
	 * @param Collection<SituacaoMatricula> situacoes
	 *
	 * @return void
	 *
	 * @throws DAOException
	 */





	/** M�todo: findBySolicitouTurmaFerias
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
		// Informando dados v�lidos
		// Curso: Ci�ncia da Computa��o (2000013)
		assertTrue(dgdao.findBySolicitouTurmaFerias(2000013, 2008, 1, null).size() > 0);

		// Informando dados inv�lidos
		assertEquals(dgdao.findBySolicitouTurmaFerias(-999, 2008, 3, null).size(), 0);		// Curso inv�lido
		assertEquals(dgdao.findBySolicitouTurmaFerias(2000013, 9999, 3, null).size(), 0);		// Ano inv�lido
		assertEquals(dgdao.findBySolicitouTurmaFerias(2000013, 2008, 45, null).size(), 0);	// Per�odo inv�lido
	}


	/** M�todo: findDiscentesEadByMatriculaPolo
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
		// Curso: Matem�tica - A dist�ncia (2000134)
		// Polo: Currais Novos (2679665)

		polo = new Polo(2679665);

		System.out.println("Id do polo: " + polo.getId());

		// Inserindo dados v�lidos
		//--------------------------------------------------------
		// Embora se forne�a valores v�lidos para os par�metros,
		// a fun��o n�o retorna resultados.
		//--------------------------------------------------------
//		assertNotNull(dgdao.findDiscentesEadByMatriculaPolo(200762451l, polo));

		// Inserindo dados inv�lidos
//		assertNull(dgdao.findDiscentesEadByMatriculaPolo(-99999999l, polo));		// Matr�cula inv�lida
//		assertNull(dgdao.findDiscentesEadByMatriculaPolo(200762451l, null));		// P�lo Nulo

		//---------------------------------------------------------
		// O m�todo n�o aceita que se entre com par�metros n�o
		// populados.
		//---------------------------------------------------------
//		assertNull(dgdao.findDiscentesEadByMatriculaPolo(200762451l, new Polo()));	// P�lo Nulo
	}


	/** M�todo: findDiscentesEadByNomePolo
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
		// Curso: Matem�tica - A dist�ncia (2000134)
		// Polo: Currais Novos (2679665)

		polo = new Polo(2679665);

		// Informando dados v�lidos. A busca por p�lo � opcional.
//		assertTrue(dgdao.findDiscentesEadByNomePolo("RUXLEY BERNARDINO", polo).size() > 0);			// Informando o polo
//		assertTrue(dgdao.findDiscentesEadByNomePolo("RUXLEY BERNARDINO", null).size() > 0);			// Sem informar o polo
//		assertTrue(dgdao.findDiscentesEadByNomePolo("RUXLEY BERNARDINO", new Polo()).size() > 0);	// Polo inv�lido

		// Informando dados inv�lidos
//		assertEquals(dgdao.findDiscentesEadByNomePolo("QWERTYASDF QWERTYASDF", polo).size(), 0);	// Nome inv�lido
	}


	/** M�todo: findDiscentesEadByPoloAnoPeriodo
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

		// Informando dados v�lidos
		//--------------------------------------------------------------------------
		// Mesmo informando valores v�lidos, o m�todo falha. Log abaixo:
		//--------------------------------------------------------------------------
		// org.hibernate.QueryException: could not instantiate: br.ufrn.sigaa.pessoa.dominio.Discente
		// at org.hibernate.transform.AliasToBeanConstructorResultTransformer.transformTuple(AliasToBeanConstructorResultTransformer.java:21)
		// at org.hibernate.hql.HolderInstantiator.instantiate(HolderInstantiator.java:69)
		// at org.hibernate.loader.hql.QueryLoader.getResultList(QueryLoader.java:358)
		//--------------------------------------------------------------------------
//		assertTrue(dgdao.findDiscentesEadByPoloAnoPeriodo(2007, 2, polo).size() > 0);

		// Informando dados inv�lidos
//		assertEquals(dgdao.findDiscentesEadByPoloAnoPeriodo(9999, 2, polo).size(), 0);			// Ano inv�lido
//		assertEquals(dgdao.findDiscentesEadByPoloAnoPeriodo(2007, 99, polo).size(), 0);			// Per�odo inv�lido
//		assertEquals(dgdao.findDiscentesEadByPoloAnoPeriodo(2007, 2, new Polo()).size(), 0);	// Polo inv�lido

		//---------------------------------------------------------------------------
		// O m�todo n�o aceita que se entre com valores nulos para um polo
		//---------------------------------------------------------------------------
//		assertEquals(dgdao.findDiscentesEadByPoloAnoPeriodo(2007, 2, null).size(), 0);			// Polo nulo
	}


	/** M�todo: findComponentesDoCurriculoByDiscente
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

		// Informando dados v�lidos
		assertTrue(dgdao.findComponentesDoCurriculoByDiscente(discente, true).size() > 0);		// Componentes somente obrigat�rios
		assertTrue(dgdao.findComponentesDoCurriculoByDiscente(discente, false).size() > 0);		// Todos os componentes

		// Informando dados inv�lidos
		//--------------------------------------------------------------------
		// O m�todo n�o permite que se entre com um discente nulo
		//--------------------------------------------------------------------
		assertTrue(dgdao.findComponentesDoCurriculoByDiscente(null, true).isEmpty());				// Discente nulo
		assertTrue(dgdao.findComponentesDoCurriculoByDiscente(new Discente(), true).isEmpty());		// Discente inv�lido
	}


	/** M�todo: findComponentesDoCurriculoByDiscente
	 *
	 * @param int curso
	 *
	 * @return TreeSet<Integer>
	 *
	 * @throws DAOException
	 */

	public void testFindComponentesDoCursoByDiscente() throws DAOException {
		// Curso: Matem�tica - A dist�ncia (2000134)

		// Informando dados v�lidos
		assertFalse(dgdao.findComponentesDoCursoByDiscente(2000134).isEmpty());

		// Informando dados inv�lidos
		assertTrue(dgdao.findComponentesDoCursoByDiscente(999999).isEmpty());
	}


	/** M�todo: findGraduandosParaColacao
	 *
	 * Busca por alunos graduandos que tiveram alguma matr�cula num semestre espec�fico
	 *
	 * @param int idCurso
	 * @param int... anoPeriodos
	 *
	 * @return Collection<DiscenteGraduacao>
	 *
	 * @throws DAOException
	 */


	public void testFindGraduandosParaColacao() throws DAOException {
		// Curso: Ci�ncia da Computa��o    (id = 2000013)
		// 		  Engenharia da Computa��o (id = 2000026)

		// Informando dados v�lidos
		assertFalse(dgdao.findGraduandosParaColacao(2000013, 20031, 20041).isEmpty());
		assertFalse(dgdao.findGraduandosParaColacao(2000026, 20041, 20042).isEmpty());

		// Informando dados inv�lidos
		assertTrue(dgdao.findGraduandosParaColacao(99999, 20031, 20041).isEmpty());			// Id de curso inv�lida
		assertTrue(dgdao.findGraduandosParaColacao(2000013, 1212121).isEmpty());			// Id de curso inv�lida
		assertTrue(dgdao.findGraduandosParaColacao(99999, 20041, 121212121).isEmpty());		// Id de curso inv�lida
	}



	/** M�todo: findByMatriz
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
		// Curso: Matem�tica - A Dist�ncia (id = 2000134)
		// IdMatriz = 99462

		// Inserindo dados v�lidos
		assertFalse(dgdao.findByMatriz(99462, StatusDiscente.ATIVO, StatusDiscente.CADASTRADO).isEmpty());

		// Inserindo dados inv�lidos
		assertTrue(dgdao.findByMatriz(-99999, StatusDiscente.ATIVO, StatusDiscente.CADASTRADO).isEmpty());	// Id inv�lida
		assertTrue(dgdao.findByMatriz(-99999, 25, 99, 321).isEmpty());	// Status inv�lidos
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

		// Testa a diferen�a entre o n�mero de m�todos de cada classe
		assertTrue(DiscenteGraduacaoDao.class.getMethods().length == DiscenteGraduacaoDaoTest.class.getMethods().length);

	}

}
