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
 * Teste Unit�rio da classe RegistroAtividadeDao
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
	public static int ID_TURMA = 1140100;   // id_disciplina: 24770 ==> codigo: 'DEQ0331' ==> nome: Ci�ncias do Ambiente
	
	/** 
	 * M�todo: buscarDatasAvaliacao()
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
		
		// Informar valores v�lidos
		assertTrue(daoTurmaVirtual.buscarDatasAvaliacao(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.buscarDatasAvaliacao(null).size() == 0);
	}

	/** 
	 * M�todo: findNoticiasByTurma()
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
		
		// Informar valores v�lidos
		assertTrue(daoTurmaVirtual.findNoticiasByTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findNoticiasByTurma(null).size() == 0);
	}

	/** 
	 * M�todo: findAulasByTurma()
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
		
		// Informar valores v�lidos
		assertTrue(daoTurmaVirtual.findAulasByTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findAulasByTurma(null).size() == 0);
	}

	/** 
	 * M�todo: findLinksByTurma()
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
		
		// Informar valores v�lidos
		assertTrue(daoTurmaVirtual.findLinksByTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findLinksByTurma(null).size() == 0);
		
	}

	/** 
	 * M�todo: findAtividadesTurmas()
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
		Discente discente = daoDiscente.findByMatricula(200506294); // matr�cula de Alyppyo
		
		// Informar valores v�lidos
		assertTrue(daoTurmaVirtual.findAtividadesTurmas(discente, 2008, 1).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findAtividadesTurmas(null, 0, 0).size() == 0);
	}

	/** 
	 * M�todo: findConteudoTurma()
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
		// ID_TURMA = 1140818 - turma com conte�do e t�pico de aula
		// id_disciplina: 21775 ==> codigo: 'DIM0430' ==> nome: L�GICA APLICADA A COMPUTA��O
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1140818); 
		
		// Informar valores v�lidos
		assertTrue(daoTurmaVirtual.findConteudoTurma(turma).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findConteudoTurma(null).size() == 0);
	}


	/** 
	 * M�todo: findAvaliacaoDataByTurma()
	 * 
	 * @param idTurma
	 * 
	 * @return List<DataAvaliacao>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindAvaliacaoDataByTurma() throws DAOException {
		// valores v�lidos
		assertTrue(daoTurmaVirtual.findAvaliacaoDataByTurma(ID_TURMA).size() > 0);
		
		// valores inv�lidos
		assertTrue(daoTurmaVirtual.findAvaliacaoDataByTurma(0).size() == 0);
	}

	/** 
	 * M�todo: findArquivosByTurma()
	 * 
	 * @param idTurma
	 * 
	 * @return List<ArquivoTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindArquivosByTurma() throws DAOException {
		// ID_TURMA = 987597 - turma com conte�do e t�pico de aula
		// id_disciplina: 18715 ==> codigo: 'MUT800' ==> nome: INSTRUMENTO I 
		
		// Informar valores v�lidos
		assertTrue(daoTurmaVirtual.findArquivosByTurma(987597).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTurmaVirtual.findArquivosByTurma(0).size() == 0);
	}

	/** 
	 * M�todo: findRespostasByTarefa()
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
		
		// tarefa que n�o existe
		assertTrue(daoTurmaVirtual.findRespostasByTarefa(0).size() == 0);
	}

	/** 
	 * M�todo: findPermissaoByPessoaTurma()
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
	 * M�todo: findPermissoesByTurma()
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
	 * M�todo: findTopicosAulaByTurma()
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
	 * M�todo: findTurmasHabilitadasByPessoa()
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
	 * M�todo: findVotosByEnquete()
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
	 * M�todo: removerEnquete()
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
	 * M�todo: findEnqueteComVotos()
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
	 * M�todo: findUltimasAtividades()
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
	 * M�todo: findTurmasAnteriores()
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
	 * M�todo: findReferenciasTurma()
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
	 * M�todo: buscarAulasExtra()
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
	 * M�todo: findConfiguracoes()
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
	 * M�todo: buscarPlanoEnsino()
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
	 * M�todo: findMateriaisByTurma()
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
	 * M�todo: isDiscenteTurma()
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
	 * M�todo: isDocenteTurma()
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
	 * M�todo: isDocenteExternoTurma()
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
