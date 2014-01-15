package br.ufrn.sigaa.test.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 *  Classe de teste para DAOs.
 *  @author Edson Alyppyo
 */
public class DiscenteDaoTest extends TestCase {
	
	/**Coleção de discentes*/
	Collection<Discente> collectionDiscente;
	/**Coleção genérica*/
	Collection<?> collectionAny;
	/**Curso usado nos testes.*/
	Curso cs = new Curso();
	/**Discente usado nos testes.*/
	Discente discente = new Discente();
	/**Polo usado nos testes*/
	Polo pl = new Polo();
	/**Dao para consultas de turma relacionadas a Discente.*/
	DiscenteDao dao = new DiscenteDao();
	/**Dao para consultas de turma relacionadas a DiscenteGraduacao.*/
	DiscenteGraduacaoDao dgdao = new DiscenteGraduacaoDao();
	/**Dao para consultas de turma relacionadas a Polo.*/
	PoloDao pldao = new PoloDao();
	/**Dao para consultas de turma relacionadas a Turma.*/
	TurmaDao tdao = new TurmaDao();
	/**Matrícula para teste de um discente ativo.*/
	public static int matriculaAtivo = 200506294;           // Discente: Alyppyo
	/**Matrícula para teste de um discente inativo.*/
	public static int matriculaInativo = 200027669;         // Discente: Gleydson
	/**Variável para teste que armazena a chave primária de uma gestora acadêmica*/
	public static int idGestoraAcademica = 605;
	/**Variável para teste que armazena a chave primária de um discente.*/
	public static int idDiscente = 89935;                   // Discente: Alyppyo
	/**Variávelo para teste que armazena a chave primária de um curso.*/
	public static int idCienComp = 2000013;					// ID do curso de Ciência da Computação
	/**Variável para teste que armazena um curso.*/
	public static Curso cursoCienComp = new Curso(idCienComp);					// curso de Ciência da Computação
	
	/**Variável para o teste com um nome único*/
	public static String nomeUnico = "EDSON ALYPPYO%"; 		// 1 Resultado
	/**Variável para o teste com um nome genérico*/
	public static String nomeGenerico = "JOSE%";
	/**Variável para o teste com um nome inexistente*/
	public static String nomeInexistente = "/**/**/**/";

	/** Método: findAlunosByAnoIngresso
	 *
	 * Retorna uma coleção de alunos que ingressaram no ano informado
	 *
	 * @param int Ano
	 * @param int unid
	 * @param char nivel
	 * @param PagingInformation paging
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 *
	 **/

	public void testFindByAnoIngresso() throws DAOException {
		// Informando dados válidos
		assertFalse (dao.findByAnoIngresso(2005, 605, 'G', null).isEmpty());

		// Informando dados inváldos
		assertEquals (dao.findByAnoIngresso(0, 1773, 'G', null).size(), 0);		// Ano inválido
		assertEquals(dao.findByAnoIngresso(1997, 9999, 'Z', null).size(), 0);	// Unidade inválida
		assertEquals(dao.findByAnoIngresso(1997, 1773, 'Z', null).size(), 0);	// Nível inválido
	}


	/** Método: findAlunosByMatricula
	 *
	 * @param Long cpf
	 * @param Long matricula
	 * @param String nome
	 * @param String nomeCurso
	 * @param Collection<Curso> cursos
	 * @param int[] statusValidos
	 * @param int[] tiposValidos
	 * @param int unidade
	 * @param char nivel
	 *
	 * @return Collection<? extends Discente>
	 *
	 * @throws DAOException, LimiteResultadosException
	 *
	 **/

	public void testFindOtimizado() throws DAOException, LimiteResultadosException {

		// Busca por CPF

		// Dados do Discente
		// Matrícula: 200506307
		// Nome: Jackson Douglas Vital dos Santos
		// Curso: Ciência da Computação
		// CPF: 060.019.644-59
		assertEquals(dao.findOtimizado(6001964459l, null, null, null, null, null, null, null, 0, '0', true).size(), 1);

		// Busca por Matrícula
		assertEquals(dao.findOtimizado(null, 200506307l, null, null, null, null, null, null, 0, '0', true).size(), 1);

		// Busca por nome
		assertTrue(dao.findOtimizado(null, null, "GLEYDSON%", null, null, null, null, null, 0, 'G', true).size() > 0);

		// Busca por curso e nome
		assertEquals(dao.findOtimizado(null, null, "EDSON ALY%", "CIENCIA DA COMPUTACAO", null, null, null, null, 0, 'G', true).size(), 1);

		// Busca por cursos e nome
		Collection<Curso> cursos = new ArrayList<Curso>();
		Curso c = new Curso(2000013);

		cursos.add(c);

		c = new Curso(2000026);
		cursos.add(c);

		assertTrue(dao.findOtimizado(null, null, "EDSON%", null, null, cursos, null, null, 0, 'G', true).size() > 0);

		// Teste de resultados nulos
		assertEquals(dao.findOtimizado(null, null, null, null, null, null, null, null, 0, 'Z', true).size(), 0);

	}


	/** Método: findAlunosByMatricula
	 *
	 * @param long matricula
	 * @param char nivel
	 *
	 * @return Discente
	 *
	 * @throws DAOException
	 *
	 **/

	public void testFindAtivosByMatricula() throws DAOException  {

		// Parametros: long matricula
		assertNull(dao.findAtivosByMatricula(matriculaInativo));
		assertNull(dao.findAtivosByMatricula(0l));
		assertNotNull(dao.findAtivosByMatricula(matriculaAtivo));

		// Parametros: long matricula, char nivel
		assertNull(dao.findAtivosByMatricula(matriculaInativo, 'G'));
		assertNotNull(dao.findAtivosByMatricula(matriculaAtivo, 'G'));

		// Parametros nulos
		assertNull(dao.findAtivosByMatricula(0l, 'G'));
		assertNull(dao.findAtivosByMatricula(matriculaAtivo, 'Z'));

	}


	/** Método: findAll
	 *
	 * @param int unid
	 * @param Class discenteSubClasse
	 * @param char nivel
	 * @param PagingInformation paging
	 *
	 * @return Collection<Discente>
	 * @throws DAOException
	 *
	 **/
	/*
	public void testFindAll() throws DAOException {
		// Método Original comentado
	}
	*/


	/** Método: findByNome
	 *
	 * @param String nome
	 * @param int unid
	 * @param char nivel
	 * @param PagingInformation paging
	 *
	 * @return Collection<Discente>
	 * @throws DAOException
	 *
	 **/

	public void testFindByNome() throws DAOException {
		// Busca com retorno único de resultado
		assertEquals (dao.findByNome(nomeUnico, 0, new char[]{'G'}, null, false, true, null).size(), 1);

		// Busca com vários resultados
		assertTrue (dao.findByNome(nomeGenerico, 0, new char[]{'G'}, null, false, true, null).size() > 0);
	}


	/** Método: findAtivosByMatriculaPrograma
	 *
	 * @param long mat
	 * @param Character nivel
	 * @param Integer idPrograma
	 *
	 * @return Discente
	 *
	 */

	public void testFindAtivosByMatriculaPrograma() throws DAOException {
		// Busca que retorna discente
		assertNotNull(dao.findAtivosByMatriculaPrograma(matriculaAtivo, idGestoraAcademica, 'G'));

		// Buscas que retornam valores nulos
		assertNull(dao.findAtivosByMatriculaPrograma(matriculaInativo, idGestoraAcademica, 'G'));
		assertNull(dao.findAtivosByMatriculaPrograma(matriculaAtivo, idGestoraAcademica, 'Z'));
		assertNull(dao.findAtivosByMatriculaPrograma(matriculaAtivo, 0, 'G'));
	}


	/** Método: findByMatricula
	 *
	 * @param long mat
	 *
	 * @return Discente
	 *
	 */

	public void testFindByMatricula() throws DAOException {
		// Buscas que retornam resultados
		assertNotNull(dao.findByMatricula(matriculaAtivo));
		assertNotNull(dao.findByMatricula(matriculaInativo));

		// Busca com retorno nulo
		assertNull(dao.findByMatricula(0l));
	}


	/** Método: findByMatricula
	 *
	 * @param long mat
	 * @param char nivel
	 *
	 * @return Discente
	 *
	 */

	public void testFindByMatricula2 () throws DAOException {
		// Buscas com retorno de resultado
		assertNotNull(dao.findByMatricula(matriculaAtivo, 'G'));
		assertNotNull(dao.findByMatricula(matriculaInativo, 'G'));

		// Buscas com retorno nulo
		assertNull(dao.findByMatricula(matriculaAtivo, 'Z'));
		assertNull(dao.findByMatricula(0l, 'G'));
	}


	/** Método: findTurmasMatriculadas
	 *
	 * Busca todas as turmas de um discente com matrículas com status MATRICULADA e EM_ESPERA.
	 * (( SEM OTIMIZAÇÃO ))
	 *
	 * @param int idDiscente
	 *
	 * @return Collection<Turma>
	 *
	 * @throws DAOException
	 */


	public void testFindTurmasMatriculadas() throws DAOException {
		// Informando valores válidos
		assertFalse(dao.findTurmasMatriculadas(idDiscente).isEmpty());

		// Informando valores inválidos
		assertTrue(dao.findTurmasMatriculadas(0).isEmpty());
	}


	/** Método: findQtdPeriodosCursados
	 *
	 * @param int idDiscente
	 * @param CalendarioAcademico calendarioAcademico
	 *
	 * @return long
	 *
	 * @throws DAOException
	 */

	public void testFindQtdPeriodosCursados() throws DAOException {
		// Busca com dados válidos
		assertTrue(dao.findQtdPeriodosCursados(idDiscente, new CalendarioAcademico()) > 6);

		// Busca com dados inválidos
		assertEquals(dao.findQtdPeriodosCursados(0, null), 0);
	}


	/** Método: findByCurso
	 *
	 * @param int idCurso
	 * @param int status
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 */

	public void testFindByCurso() throws DAOException {
		// Busca com informações válidas
		assertTrue(dao.findByCurso(idCienComp, 1).size() > 0);

		// Buscas sem retorno de resultado
		assertEquals(dao.findByCurso(0, 1).size(), 0);                // idCurso inválida
		assertEquals(dao.findByCurso(idCienComp, 78963).size(), 0);   // status inválido
	}


	/** Método: findByCursoNome
	 *
	 * @param int idCurso
	 * @param String nome
	 * @param PagingInformation paging
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 */

	public void testFindByCursoNome() throws DAOException {
		// Buscas com informações válidas
		assertEquals(dao.findByCursoNome(idCienComp, nomeUnico, null).size(), 1);        // Único resultado
		assertTrue(dao.findByCursoNome(idCienComp, nomeGenerico, null).size() > 0);      // Múltiplos resultados

		// Buscas sem retorno de resultado
		assertEquals(dao.findByCursoNome(idCienComp, nomeInexistente, null).size(), 0);  // Nome inválido
		assertEquals(dao.findByCursoNome(999999999, nomeGenerico, null).size(), 0);      // idCurso inválido
	}


	/** Método: findMatriculasByDiscente
	 *
	 * @param Discente discente
	 * @param int periodo
	 * @param int ano
	 *
	 * @return List<MatriculaComponente>
	 *
	 * @throws DAOException
	 *
	 */

	public void testFindMatriculasByDiscente() throws DAOException {
		// Busca com valores válidos
		assertNotNull(dao.findMatriculasByDiscente(new Discente(idDiscente), 2, 2007));

		// Buscas sem retorno de resultado
		assertEquals(dao.findMatriculasByDiscente(new Discente(), 2, 2007).size(), 0);             // Discente inválido
		assertEquals(dao.findMatriculasByDiscente(new Discente(idDiscente), 0, 2007).size(), 0);   // Período inválido
		assertEquals(dao.findMatriculasByDiscente(new Discente(idDiscente), 2, 0).size(), 0);      // Ano inválido
	}


	/** Método: findDisciplinasConcluidasMatriculadas
	 *
	 * Retorna a lista de disciplinas concluídas e matriculadas de um discente
	 *
	 * @param idDiscente
	 * @param trazerCancelados
	 *
	 * @return List<MatriculaComponente>
	 */

	public void testFindDisciplinasConcluidasMatriculadas() throws DAOException {
		// Buscas com retorno de resultado
		assertTrue(dao.findDisciplinasConcluidasMatriculadas(idDiscente, false).size() > 35);
		assertTrue(dao.findDisciplinasConcluidasMatriculadas(idDiscente, true).size() > 35);

		// Buscas sem retorno de resultado
		assertEquals(dao.findDisciplinasConcluidasMatriculadas(0, false).size(), 0);
		assertEquals(dao.findDisciplinasConcluidasMatriculadas(0, true).size(), 0);
	}


	/** Método: updateDiscente
	 *
	 * Método para ser usado todas as vezes que quiser atualizar apenas um campo
	 * da entidade Discente.
	 *
	 * @param Class classe
	 * @param Integer id
	 * @param String campo
	 * @param Object valor
	 *
	 * @return void
	 */

	public void testUpdateDiscente() throws DAOException {
		// Informando idDiscente válida
		dao.updateDiscente(discente.getClass(), 89948, "anoIngresso", 2006);        // Update no ano de ingresso do aluno

		assertEquals((int)dao.findByMatricula(200506307).getAnoIngresso(), 2006);   // Confirmação de que o ano de ingresso é o mesmo
																					// que foi informado acima

		// Informando idDiscente inválida
		try {
			dao.updateDiscente(discente.getClass(), 0, "anoIngresso", 2005);        // Update no ano de ingresso do aluno
		} catch (Exception e) {
			assertEquals((int)dao.findByMatricula(200506307).getAnoIngresso(), 2006);
		}
	}


	/** Método: updateDiscente
	 *
	 * Método para ser usado todas as vezes que quiser atualizar apenas um campo
	 * da entidade Discente.
	 *
	 * @param Integer id
	 * @param String campo
	 * @param Object valor
	 *
	 * @return void
	 */

	public void testUpdateDiscente2() throws DAOException {
		// Informando idDiscente válida
		dao.updateDiscente(discente.getClass(), 89948, "anoIngresso", 2005);        // Update no ano de ingresso do aluno

		assertEquals((int)dao.findByMatricula(200506307).getAnoIngresso(), 2005);   // Confirmação de que o ano de ingresso
																					// foi alterado realmente

		// Informando idDiscente inválida
		try {
			dao.updateDiscente(discente.getClass(), 0, "anoIngresso", 2006);        // Tentativa de update de ano de ingresso
		} catch (Exception e) {														// informando idDiscente inexistente
			assertEquals((int)dao.findByMatricula(200506307).getAnoIngresso(), 2005);
		}
	}


	/** Método: findComponentesCurricularesConcluidos
	 *
	 * Retorna todos os componentes de turmas aprovadas (ou aproveitadas) de um discente
	 *
	 * @param discente
	 *
	 * @return Collection<ComponenteCurricular>
	 *
	 * @throws DAOException
	 */

	public void testFindComponentesCurricularesConcluidos() throws DAOException {
		// Entrando com um discente válido
		assertTrue(dao.findComponentesCurricularesConcluidos(new Discente(idDiscente)).size() > 0);

		// Entrando com um discente inválido
		assertEquals(dao.findComponentesCurricularesConcluidos(new Discente(0)).size(), 0);
	}


	/** Método: findComponentesCurriculares
	 *
	 * @param Discente discente
	 * @param Collection<SituacaoMatricula> situacoesMatricula
	 * @param String[] tipoIntegralizacao
	 *
	 * @return Collection<ComponenteCurricular>
	 *
	 * @throws DAOException
	 */

	public void testFindComponentesCurriculares() throws DAOException {
		Collection<SituacaoMatricula> situacoesMatricula = new ArrayList<SituacaoMatricula>();
		String[] tipoIntegralizacao = new String[2];

		// Adicionando situações de matrícula para a busca
		situacoesMatricula.add(SituacaoMatricula.APROVADO);
		situacoesMatricula.add(SituacaoMatricula.TRANCADO);

		// Adicioand
		tipoIntegralizacao[0] = "OP";
		tipoIntegralizacao[1] = "OB";

		// Entrando com um discente válido
		assertTrue(dao.findComponentesCurriculares(new Discente(idDiscente), situacoesMatricula, tipoIntegralizacao).size() > 0);

		// Informando dados inválidos
		assertEquals(dao.findComponentesCurriculares(new Discente(0), situacoesMatricula, tipoIntegralizacao).size(), 0);    // idDiscente inválida
		assertTrue(dao.findComponentesCurriculares(new Discente(idDiscente), situacoesMatricula, null).size() > 0);          // Tipos de integralização inválidas

		//--------------------------------------------------------------------------
		// DAOException caso se informe null ao invés de Situações de Matrícula inválidas:
		// br.ufrn.arq.erros.DAOException at br.ufrn.sigaa.arq.dao.DiscenteDao.findComponentesCurriculares(DiscenteDao.java:686)
		// at br.ufrn.sigaa.test.dao.DiscenteDaoTest.testFindComponentesCurriculares(DiscenteDaoTest.java:685)
		//--------------------------------------------------------------------------
		assertEquals(dao.findComponentesCurriculares(new Discente(idDiscente), null, tipoIntegralizacao).size(), 0);       // Situações de Matrícula inválidas

	}


	/** Método: findComponentesCurriculares
	 *
	 * @param Discente discente
	 * @param SituacaoMatricula... situacoes
	 *
	 * @return Collection<ComponenteCurricular>
	 *
	 * @throws DAOException
	 */

	public void testFindComponentesCurriculares2() throws DAOException {
		// Informando dados válidos
		assertTrue(dao.findComponentesCurriculares(new Discente(idDiscente), SituacaoMatricula.APROVADO, SituacaoMatricula.TRANCADO).size() > 0);   // Informando situações específicas

		// Entrando com um discente inválido
		assertEquals(dao.findComponentesCurriculares(new Discente(0), SituacaoMatricula.APROVADO, SituacaoMatricula.TRANCADO).size(), 0);

		//--------------------------------------------------------------------------
		// DAOException caso não se informe Situações de Matrícula:
		// br.ufrn.arq.erros.DAOException: unexpected end of subtree [select mc.componente.id, mc.componente.detalhes.equivalencia, mc.componente.tipoComponente.id from br.ufrn.sigaa.ensino.dominio.MatriculaComponente mc where mc.discente.id = :discente and mc.situacaoMatricula.id in  (  )]
		// at br.ufrn.sigaa.arq.dao.DiscenteDao.findComponentesCurriculares(DiscenteDao.java:711)
		// at br.ufrn.sigaa.test.dao.DiscenteDaoTest.testFindComponentesCurriculares2(DiscenteDaoTest.java:711)
		//--------------------------------------------------------------------------
		assertTrue(dao.findComponentesCurriculares(new Discente(idDiscente)).size() > 0);															// Buscando todas as situações possíveis
	}


	/** Método: findByDisciplinasCurricularesPendentes
	 *
	 * @param int idDiscente
	 * @param List<MatriculaComponente> disciplinas
	 * @param List<TipoGenerico> equivalenciasDiscente
	 *
	 * @return Collection<ComponenteCurricular>
	 */

	public void testFindByDisciplinasCurricularesPendentes() throws DAOException {
		List<MatriculaComponente> disciplinas = new ArrayList<MatriculaComponente>();

		disciplinas.add(new MatriculaComponente(8314498));
		disciplinas.add(new MatriculaComponente(7750420));
		disciplinas.add(new MatriculaComponente(7750417));

		// Informando dados válidos
		assertTrue(dao.findByDisciplinasCurricularesPendentes(idDiscente, disciplinas, new ArrayList<TipoGenerico>()).size() > 0);

		// Informando dados inválidos
		assertEquals(dao.findByDisciplinasCurricularesPendentes(0, disciplinas, new ArrayList<TipoGenerico>()), 0);
		assertEquals(dao.findByDisciplinasCurricularesPendentes(idDiscente, null, new ArrayList<TipoGenerico>()), 0);
		assertEquals(dao.findByDisciplinasCurricularesPendentes(idDiscente, disciplinas, new ArrayList<TipoGenerico>()), 0);
	}


	/** Método: findComponenteNomeChById
	 *
	 * @param int id
	 *
	 * @return ComponenteCurricular
	 */

	public void testFindComponenteNomeChById() throws DAOException {
		// Busca com uma id válida
		assertNotNull(dao.findComponenteNomeChById(26906));

		// Busca com uma id inválida
		assertNull(dao.findComponenteNomeChById(0));
	}


	/** Método: findBySolicitacoesTrancamentoPendentesEspecial
	 *
	 * Retorna as solicitacoes de trancamento pendentes realizada por alunos especiais (que nao tem curso)
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 */

	public void testFindBySolicitacoesTrancamentoPendentesEspecial() throws DAOException{
		// A busca abaixo sempre deve retornar resultado, exceto no caso de
		// não haver solicitações de trancamento por alunos especiais
		assertEquals(dao.findBySolicitacoesTrancamentoPendentesEspecial().size(), 0);   // Destinada ao portal do coordenador
	}

	/** Método: findDadosReconhecimentoCurso
	 *
	 * @param int id
	 *
	 * @return Object[]
	 *
	 * @throws DAOException
	 *
	 */

	public void testFindDadosReconhecimentoCurso() throws DAOException {
		// Informando valores válidos
		assertNotNull(dao.findDadosReconhecimentoCurso(idDiscente));

		// Informando valores inválidos
		assertNull(dao.findDadosReconhecimentoCurso(0));
	}


	/** Método: findDadosAutorizacaoCurso
	 *
	 * @param int id
	 *
	 * @return Object[]
	 *
	 * @throws DAOException
	 *
	 */

	public void testFindDadosAutorizacaoCurso() throws DAOException {
		// Busca com retorno de resultado
		assertNotNull(dao.findDadosAutorizacaoCurso(idCienComp));

		// Busca sem retorno de resultado
		assertNull(dao.findDadosAutorizacaoCurso(0));
	}


	/** Método: findRelatorioNotasAluno
	 *
	 * Retorna a lista de matrículas de um discente.
	 *
	 * @param Discente discente - Discente
	 * @param boolean completo - Se a listagem vai ser de todos os anos-periodos ou apenas de um
	 * @param int ano - O ano selecionado
	 * @param int periodo - O Período selecionado
	 *
	 * @return List<MatriculaComponente>
	 *
	 * @throws DAOException
	 */

	public void testFindRelatorioNotasAluno() throws DAOException {
		discente = dao.findByMatricula(200506294);

		// Busca com dados válidos
		assertTrue(dao.findRelatorioNotasAluno(discente, false, 0, 0).size() > 0);		    // Todos os anos-períodos
		assertTrue(dao.findRelatorioNotasAluno(discente, true, 2007, 2).size() > 0);	    // Ano-período especificado

		// Busca com dados inválidos
		assertEquals(dao.findRelatorioNotasAluno(new Discente(), false, 0, 0).size(), 0);	// Informando discente inválido e não setando o boolean
		assertEquals(dao.findRelatorioNotasAluno(new Discente(), true, 2007, 2).size(), 0); // Informando discente inválido e setando o boolean
		assertEquals(dao.findRelatorioNotasAluno(discente, true, 0, 2).size(), 0);			// Informando discente e período válidos, mas ano inválido
		assertEquals(dao.findRelatorioNotasAluno(discente, true, 2007, 0).size(), 0);		// Informando discente e ao válidos, mas período inválido
	}


	/** Método: findComponentesPendentesByDiscente
	 *
	 * @param Discente discente
	 * @param boolean somenteDoCurriculo
	 * @param boolean somenteObrigatorias
	 *
	 * @return Collection<CurriculoComponente>
	 *
	 * @throws DAOException
	 */

	public void testFindComponentesPendentesByDiscente() throws DAOException {
		discente = dao.findByMatricula(200506294);

		// Entrando com um discente válido
		assertTrue(dao.findComponentesPendentesByDiscente(discente, true, true).size() > 0);      // Busca por componentes obrigatórias do currículo
		assertTrue(dao.findComponentesPendentesByDiscente(discente, true, false).size() > 0);	  // Buscas por disciplinas do curriculo
		assertTrue(dao.findComponentesPendentesByDiscente(discente, false, true).size() > 0);     // Busca por disciplinas obrigatórias
		assertTrue(dao.findComponentesPendentesByDiscente(discente, false, false).size() > 0);    // Busca por disciplinas que não são obrigatórias nem do currículo

		// Entrando com discente inválido
		//--------------------------------------------------------------------------
		// Erro ao se informar um discente inválido: java.lang.NullPointerException
		//--------------------------------------------------------------------------
		assertFalse(dao.findComponentesPendentesByDiscente(new Discente(), true, true).size() > 0);
	}




	/** Método: findTrancamentosByDiscente
	 *
	 * @param Discente discente
	 * @param Boolean otimizado
	 *
	 * @return Collection<MovimentacaoAluno>
	 *
	 * @throws DAOException
	 */

	public void testFindTrancamentosByDiscente() throws DAOException {
		// Informando discentes válidos
		assertTrue(dao.findTrancamentosByDiscente(dao.findByMatricula(199912860), true).size() > 0);		// Otimizado
		assertEquals(dao.findTrancamentosByDiscente(dao.findByMatricula(200506294), false).size(), 0);      // Não-otimizado

		// Informando discente inválido
		assertEquals(dao.findTrancamentosByDiscente(new Discente(), true).size(), 0);		// Otimizado
		assertEquals(dao.findTrancamentosByDiscente(new Discente(), false).size(), 0);     // Não-otimizado
	}


	/** Método: findTrancamentosByDiscente
	 *
	 * @param Discente discente
	 *
	 * @return Collection<MovimentacaoAluno>
	 *
	 * @throws DAOException
	 */

	public void testFindTrancamentosByDiscente2 () throws DAOException {
		// Informando discente válido
		assertTrue(dao.findTrancamentosByDiscente(dao.findByMatricula(199912860)).size() > 0);		// Otimizado
		assertEquals(dao.findTrancamentosByDiscente(dao.findByMatricula(200506294)).size(), 0);     // Não-otimizado

		// Informando discente inválido
		assertEquals(dao.findTrancamentosByDiscente(new Discente()).size(), 0);		// Otimizado
	}


	/** Método: findObservacoesDiscente
	 *
	 * @param Discente discente
	 *
	 * @return List<ObservacaoDiscente>
	 *
	 * @throws DAOException
	 */

	public void testFindObservacoesDiscente() throws DAOException {
		// Entrando com discente válido
		assertNotNull(dao.findObservacoesDiscente(new DiscenteGraduacao(idDiscente)));

		// Entrando com discente inválido
		//--------------------------------------------------------------------------
		// DAOException ao se informar um discente inválido
		//--------------------------------------------------------------------------
		assertNotNull(dao.findObservacoesDiscente(null));
	}


	/** Método: findByDadosPessoaisMesmoNivel
	 *
	 * @param Discente discente
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 */

	public void testFindByDadosPessoaisMesmoNivel() throws DAOException {
		discente = dao.findByMatricula(200506294);

		// Entrando com discente válido
		//--------------------------------------------------------------------------
		// Mesmo informando um discente válido, a busca não retorna resultado
		//--------------------------------------------------------------------------
		assertEquals(dao.findByDadosPessoaisMesmoNivel(discente).size(), 0);

		// Entrando com discente inválido
		//--------------------------------------------------------------------------
		// DAOException ao se informar um discente inválido
		//--------------------------------------------------------------------------
		assertNotNull(dao.findByDadosPessoaisMesmoNivel(null));
	}


	/** Método: findByPK
	 *
	 * @param int id
	 *
	 * @return Discente
	 *
	 * @throws DAOException
	 */

	public void testFindByPK() throws DAOException {
		// Entrando com discente válido
		assertNotNull(dao.findByPK(idDiscente));

		// Entrando com discente inválido
		//--------------------------------------------------------------------------
		// DAOException ao se informar um discente inválido
		//--------------------------------------------------------------------------
		assertNull(dao.findByPK(0));
	}


	/** Método: findByDadosPessoais
	 *
	 * @param Discente discente
	 * @param char nivel
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 */

	public void testFindByDadosPessoais() throws DAOException {
		discente = dao.findByMatricula(200506294);

		// Informando dados válidos
		//--------------------------------------------------------------------------
		// Caso se informe o mesmo nível do discente, a busca não retorna
		// resultado
		//--------------------------------------------------------------------------
		assertTrue(dao.findByDadosPessoais(discente, 'G').size() > 0);
		assertEquals(dao.findByDadosPessoais(discente, 'T').size(), 0);

		// Informando dados inválidos
		assertEquals(dao.findByDadosPessoais(discente, 'Z').size(), 0);			// Informando nível inválido
		assertEquals(dao.findByDadosPessoais(new Discente(), 'T').size(), 0);	// Informando discente inválido
	}


	/** Método: findDiscentesNaoMatriculados
	 *
	 * @param int ano
	 * @param int periodo
	 *
	 * @return List<Discente>
	 *
	 * @throws DAOException
	 */

	public void testFindDiscentesNaoMatriculados() throws DAOException {
		// Informando dados válidos
		/*assertTrue(dao.findDiscentesNaoMatriculados(Collections.singletonList(new Integer[] { 2008, 1 }), 2008, 2).size() > 0);

		// Informando dados inválidos
		assertEquals(dao.findDiscentesNaoMatriculados(Collections.singletonList(new Integer[] { 2008, 0 }), 2008, 2).size(), 0);	// Informando período inválido
		assertEquals(dao.findDiscentesNaoMatriculados(Collections.singletonList(new Integer[] { 0, 1 }), 2008, 2).size(), 0);		// Informando ano inválido
*/	}


	/** Método: findQuantitativoAlunosMatriculadosPlanejamento
	 *
	 * Busca os quantitativos dos diversos tipos de alunos matriculados.
	 * Utilizado no relatório do portal do planejamento.
	 *
	 * @param int ano
	 * @param int periodo
	 *
	 * @return TreeMap<String, Map<String, Integer>>
	 *
	 * @throws DAOException
	 */

	public void testFindQuantitativoAlunosMatriculadosPlanejamento() throws DAOException {
		//--------------------------------------------------------------------------
		// Retornando sempre 3 resultados, mesmo com dados válidos ou com um ano
		// ou um período inválido.
		//--------------------------------------------------------------------------

		// Informando dados válidos
		assertEquals(dao.findQuantitativoAlunosMatriculadosPlanejamento(2007, 2).size(), 3);

		// Informando dados inválidos
		assertEquals(dao.findQuantitativoAlunosMatriculadosPlanejamento(0, 2).size(), 0);		// Informando ano inválido
		assertEquals(dao.findQuantitativoAlunosMatriculadosPlanejamento(2007, 0).size(), 0);	// Informando período inválido
	}

	/** Método: findQuantitativoAlunosAtivosPlanejamento
	 *
	 * @return TreeMap<String, Map<String, Integer>>
	 *
	 * @throws DAOException
	 */

	public void testFindQuantitativoAlunosAtivosPlanejamento() throws DAOException {
		// A busca abaixo sempre deve retornar resultados
		assertTrue(dao.findQuantitativoAlunosAtivosPlanejamento().size() > 0);
	}


	/** Método: findDiscentesByPoloCurso
	 *
	 * @param Polo polo
	 * @param Curso curso
	 * @param Integer ano
	 * @param Integer periodo
	 * @param boolean matriculados
	 * @param boolean agrupar
	 *
	 * @return List<DiscenteGraduacao>
	 *
	 * @throws DAOException
	 *
	 */

	public void testfindDiscentesByPoloCurso() throws DAOException {
		pl = pldao.findByCurso(2671321).listIterator().next();
		cs = pldao.findCursosByPolo(pl).iterator().next();

		// Entrando com dados válidos
		assertTrue(dao.findDiscentesByPoloCurso(pl, cs, 2007, 2, false, false, false).size() > 0);

		// Entrando com dados inválidos
		assertEquals(dao.findDiscentesByPoloCurso(new Polo(), new Curso(idCienComp), 2007, 2, false, false, false).size(), 0);	 			// Pólo inválido
		assertEquals(dao.findDiscentesByPoloCurso(new Polo(2526660), new Curso(), 2007, 2, false, false, false).size(), 0);				// Curso inválido
		assertEquals(dao.findDiscentesByPoloCurso(new Polo(2526660), new Curso(idCienComp), 0, 2, false, false, false).size(), 0);			// Pólo inválido
		assertEquals(dao.findDiscentesByPoloCurso(new Polo(2526660), new Curso(idCienComp), 2007, 0, false, false, false).size(), 0);		// Pólo inválido
	}


	/** Método: findDadosExportacaoAlunosEad
	 *
	 * @param Polo polo
	 * @param Curso curso
	 * @param int ano
	 * @param int periodo
	 *
	 * @return List<DiscenteGraduacao>
	 *
	 * @throws DAOException
	 *
	 */

	public void testFindDadosExportacaoAlunosEad() throws DAOException {
		// Informando dados válidos
		assertTrue(dao.findDadosExportacaoAlunosEad(null, null, 2007, 2).size() > 0);								// Sem informações sobre pólo e curso
		assertTrue(dao.findDadosExportacaoAlunosEad(new Polo(2526671), null, 2007, 2).size() > 0);					// Sem informações sobre curso
		assertTrue(dao.findDadosExportacaoAlunosEad(null, new Curso(2000124), 2007, 2).size() > 0);					// Sem informações sobre pólo
		assertTrue(dao.findDadosExportacaoAlunosEad(new Polo(2526671), new Curso(2000124), 2007, 2).size() > 0);	// Informações completas

		// Informando dados inválidos
		assertTrue(dao.findDadosExportacaoAlunosEad(null, new Curso(), 2007, 2).size() > 0);	// Curso inválido
		assertTrue(dao.findDadosExportacaoAlunosEad(new Polo(), null, 2007, 2).size() > 0);		// Polo inválido
		assertEquals(dao.findDadosExportacaoAlunosEad(null, null, 0, 2).size(), 0);				// Ano inválido
		assertEquals(dao.findDadosExportacaoAlunosEad(null, null, 2007, 0).size(), 0);			// Periodo inválido
	}


	/** Método: findByCursoAnoPeriodo
	 *
	 * Retorna os discente do curso/ano/periodo informados
	 *
	 * @param int anoIngresso
	 * @param int periodoIngresso
	 * @param Collection<Curso> cursos
	 * @param boolean somenteAtivos
	 *
	 * @return Collection<Discente>
	 *
	 * @throws DAOException
	 *
	 */

	public void testFindByCursoAnoPeriodo() throws DAOException {
		Collection<Curso> cursos = new ArrayList<Curso>();
		Curso c = new Curso(2000013);

		cursos.add(c);

		// Entrando com dados válidos
		assertTrue(dao.findByCursoAnoPeriodo(2005, 1, cursos, true).size() > 0);
		assertTrue(dao.findByCursoAnoPeriodo(2005, 1, cursos, false).size() > 0);

		// Entrando com dados inválidos
		assertEquals(dao.findByCursoAnoPeriodo(0, 1, cursos, true).size(), 0);
		assertEquals(dao.findByCursoAnoPeriodo(2005, 0, cursos, true).size(), 0);
		assertEquals(dao.findByCursoAnoPeriodo(1996, 1, cursos, true).size(), 0);

		cursos = new ArrayList<Curso>(); // Zerando o array de cursos

		//--------------------------------------------------------------------------
		// Caso se entre com um array de cursos vazio, DAOException fim inesperado
		// de sub-árvore.
		// Caso se entre com um valor nulo para os cursos, DAOException Null Pointer
		//--------------------------------------------------------------------------
		assertTrue(dao.findByCursoAnoPeriodo(2005, 1, null, false).size() > 0);
	}


	/** Método: findTrabalhoConclusaoCurso
	 *
	 * @param Discente discente
	 *
	 * @return TrabalhoFimCurso
	 *
	 * @throws DAOException
	 */

	public void testFindTrabalhoConclusaoCurso() throws DAOException {
		// Entrando com discente válido
		assertNotNull(dao.findTrabalhoConclusaoCurso(dao.findByMatricula(200126268)));

		// Entrando com discente inválido
		assertNull(dao.findTrabalhoConclusaoCurso(null));
	}


	/** Método: isAlunoConcluidoCreditoPendente
	 *
	 * @param Discente discente
	 *
	 * @return boolean
	 *
	 * @throws DAOException
	 */

	public void testIsAlunoConcluidoCreditoPendente() throws DAOException {
		// Entrando com discente válido
		assertFalse(dao.isAlunoConcluidoCreditoPendente(dao.findByMatricula(200126268)));

		// Entrando com discente inválido
		assertTrue(dao.isAlunoConcluidoCreditoPendente(dao.findByPK(98580)));
	}

}
