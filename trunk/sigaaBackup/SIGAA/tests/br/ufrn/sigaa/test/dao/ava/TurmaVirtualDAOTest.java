package br.ufrn.sigaa.test.dao.ava;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Teste Unitário da classe RegistroAtividadeDao
 * 
 * @author Dalton
 *
 */
public class TurmaVirtualDAOTest extends TestCase {

	TurmaVirtualDao daoTurmaVirtual = new TurmaVirtualDao();
	
	// busca turma
	TurmaDao daoTurma = new TurmaDao();
	
	// busca discente
	DiscenteDao daoDiscente = new DiscenteDao();
	
	// busca turma
	PessoaDao daopessoa = new PessoaDao();
	
	public static int ID_TAREFA = 2633550;  // tarefa com respostas
	public static int ID_TURMA = 1140100;   // id_disciplina: 24770 ==> codigo: 'DEQ0331' ==> nome: Ciências do Ambiente
	
	/** 
	 * Método: buscarDatasAvaliacao()
	 * 
	 * @param turma
	 * 
	 * @return List<DataAvaliacao>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testBuscarDatasAvaliacao() throws DAOException {
		// buscar uma turma
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(ID_TURMA);
		
		// Informar valores válidos
		assertTrue(daoTurmaVirtual.buscarDatasAvaliacao(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.buscarDatasAvaliacao(null).size() == 0);
	}

	/** 
	 * Método: findNoticiasByTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<NoticiaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindNoticiasByTurma() throws DAOException {
		// buscar uma turma
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(ID_TURMA);
		
		// Informar valores válidos
		assertTrue(daoTurmaVirtual.findNoticiasByTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findNoticiasByTurma(null).size() == 0);
	}

	/** 
	 * Método: findAulasByTurma()
	 * 
	 * @param turma
	 * 
	 * @return Collection<TopicoAula>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindAulasByTurma() throws DAOException {
		// buscar uma turma
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(ID_TURMA);
		
		// Informar valores válidos
		assertTrue(daoTurmaVirtual.findAulasByTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findAulasByTurma(null).size() == 0);
	}

	/** 
	 * Método: findLinksByTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<IndicacaoReferencia>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindLinksByTurma() throws DAOException{
		// buscar uma turma
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(ID_TURMA);
		
		// Informar valores válidos
		assertTrue(daoTurmaVirtual.findLinksByTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findLinksByTurma(null).size() == 0);
		
	}

	/** 
	 * Método: findAtividadesTurmas()
	 * 
	 * @param discente
	 * @param ano (int)
	 * @param semestre (int)
	 * 
	 * @return Collection<RegistroAtividadeTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindAtividadesTurmas() throws DAOException {
		// buscar discente
		Discente discente = daoDiscente.findByMatricula(200506294); // matrícula de Alyppyo
		
		// Informar valores válidos
		assertTrue(daoTurmaVirtual.findAtividadesTurmas(discente, 2008, 1).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findAtividadesTurmas(null, 0, 0).size() == 0);
	}

	/** 
	 * Método: findConteudoTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<ConteudoTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindConteudoTurma() throws DAOException {
		// buscar uma turma
		// ID_TURMA = 1140818 - turma com conteúdo e tópico de aula
		// id_disciplina: 21775 ==> codigo: 'DIM0430' ==> nome: LÓGICA APLICADA A COMPUTAÇÃO
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1140818); 
		
		// Informar valores válidos
		assertTrue(daoTurmaVirtual.findConteudoTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findConteudoTurma(null).size() == 0);
	}


	/** 
	 * Método: findAvaliacaoDataByTurma()
	 * 
	 * @param idTurma
	 * 
	 * @return List<DataAvaliacao>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindAvaliacaoDataByTurma() throws DAOException {
		// valores válidos
		assertTrue(daoTurmaVirtual.findAvaliacaoDataByTurma(ID_TURMA).size() > 0);
		
		// valores inválidos
		assertTrue(daoTurmaVirtual.findAvaliacaoDataByTurma(0).size() == 0);
	}

	/** 
	 * Método: findArquivosByTurma()
	 * 
	 * @param idTurma
	 * 
	 * @return List<ArquivoTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindArquivosByTurma() throws DAOException {
		// ID_TURMA = 987597 - turma com conteúdo e tópico de aula
		// id_disciplina: 18715 ==> codigo: 'MUT800' ==> nome: INSTRUMENTO I 
		
		// Informar valores válidos
		assertTrue(daoTurmaVirtual.findArquivosByTurma(987597).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findArquivosByTurma(0).size() == 0);
	}

	/** 
	 * Método: findRespostasByTarefa()
	 * 
	 * @param idTarefa
	 * 
	 * @return List<RespostaTarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindRespostasByTarefa() throws DAOException {
		//tarefa com respostas
		assertTrue(daoTurmaVirtual.findRespostasByTarefa(2633550).size() > 0);
		
		// tarefa que não existe
		assertTrue(daoTurmaVirtual.findRespostasByTarefa(0).size() == 0);
	}

	/** 
	 * Método: findPermissaoByPessoaTurma()
	 * 
	 * @param pessoa
	 * @param turma
	 * 
	 * @return PermissaoAva
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindPermissaoByPessoaTurma() {
		
	}

	/** 
	 * Método: findPermissoesByTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindPermissoesByTurma() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findTopicosAulaByTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindTopicosAulaByTurma() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findTurmasHabilitadasByPessoa()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindTurmasHabilitadasByPessoa() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findVotosByEnquete()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindVotosByEnquete() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: removerEnquete()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testRemoverEnquete() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findEnqueteComVotos()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindEnqueteComVotos() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findUltimasAtividades()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindUltimasAtividades() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findTurmasAnteriores()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindTurmasAnteriores() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findReferenciasTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindReferenciasTurma() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: buscarAulasExtra()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testBuscarAulasExtra() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findConfiguracoes()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindConfiguracoes() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: buscarPlanoEnsino()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testBuscarPlanoEnsino() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: findMateriaisByTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindMateriaisByTurma() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: isDiscenteTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testIsDiscenteTurma() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: isDocenteTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testIsDocenteTurma() {
		fail("Not yet implemented");
	}

	/** 
	 * Método: isDocenteExternoTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testIsDocenteExternoTurma() {
		fail("Not yet implemented");
	}

}
