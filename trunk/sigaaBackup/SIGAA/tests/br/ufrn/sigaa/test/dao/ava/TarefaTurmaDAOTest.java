package br.ufrn.sigaa.test.dao.ava;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.TarefaTurmaDao;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Retorna as Tarefas de uma turma, só as que não estão com o campo Ativo = false.
 * 
 * Teste Unitário da classe RegistroAtividadeDao
 * 
 * @author Dalton
 *
 */
public class TarefaTurmaDAOTest extends TestCase {

	/**busca tarefa */
	TarefaTurmaDao daoTarefaTurma = new TarefaTurmaDao();
	
	/** busca usuário */
	UsuarioDao daoUsuario = new UsuarioDao();

	/** buscar uma turma*/
	TurmaDao daoTurma = new TurmaDao();
	
	/** 
	 * Método: findByTurma()
	 * 
	 * @param turma
	 * 
	 * @return List<TarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindByTurma() throws DAOException {
		// buscar uma turma
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);
		
		// Informar valores válidos
		assertTrue(daoTarefaTurma.findByTurma(turma,true).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTarefaTurma.findByTurma(null,true).size() == 0);
	}

	/** 
	 * Método: findTarefaTurma()
	 * 
	 * @param tarefa
	 * @param usuario
	 * 
	 * @return RespostaTarefaTurma
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindTarefaTurma() throws DAOException {
		// Informar valores válidos
		assertNotNull(daoTarefaTurma.findTarefaTurma(1));
		
		// informar valores inválidos
		assertNull(daoTarefaTurma.findTarefaTurma(-1));
	}

	/** 
	 * Método: findTarefaTurma()
	 * 
	 * @param tarefa
	 * @param usuario
	 * 
	 * @return RespostaTarefaTurma
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindRespostaByTarefaAluno() throws DAOException {
		// busca usuário
		Usuario usuario = (Usuario)daoUsuario.findByLogin("alyppyo"); 
		
		// busca tarefa
		TarefaTurma tarefa = daoTarefaTurma.findTarefaTurma(1);
		
		// testando com valores válidos
		assertNotNull(daoTarefaTurma.findRespostaByTarefaAluno(tarefa, usuario));
		
		// testando com valores nulos
		assertNotNull(daoTarefaTurma.findRespostaByTarefaAluno(null, null));
	}

	/** 
	 * Método: findRespostaByAluno()
	 * 
	 * @param tarefa
	 * @param usuario
	 * 
	 * @return boolean
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindRespostaByAluno() throws DAOException {
		// busca usuário
		Usuario usuario = (Usuario)daoUsuario.findByLogin("alyppyo"); 
		
		// busca tarefa
		TarefaTurma tarefa = daoTarefaTurma.findTarefaTurma(1);
		
		// testando com valores válidos
		assertTrue(daoTarefaTurma.findRespostaByAluno(usuario, tarefa));
		
		// testando com valores nulos
		assertFalse(daoTarefaTurma.findRespostaByAluno(null, null));
		
	}

	/** 
	 * Método: findRespostasNaoAvaliadasByTarefa()
	 * 
	 * @param tarefa
	 * 
	 * @return List<RespostaTarefaTurma>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testFindRespostasNaoAvaliadasByTarefa() throws DAOException {
		// busca tarefa
		// buscar uma turma
		Turma turma = daoTurma.findByPrimaryKeyOtimizado(1143552);
		
		TarefaTurma tarefa = daoTarefaTurma.findTarefaTurma(1);
		
		// testando com valores válidos
		assertNotNull(daoTarefaTurma.findRespostasNaoAvaliadasByTarefa(tarefa,turma));
		
		// testando com valores nulos
		assertNotNull(daoTarefaTurma.findRespostasNaoAvaliadasByTarefa(null,turma));
	}

	/** 
	 * Método: removerRespostaByTarefaAluno()
	 * 
	 * @param tarefa
	 * @param usuario
	 * 
	 * @return RespostaTarefaTurma
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testRemoverRespostaByTarefaAluno() throws DAOException{
		
		// busca usuário
		Usuario usuario = (Usuario)daoUsuario.findByLogin("alyppyo"); 
		
		// busca tarefa
		TarefaTurma tarefa = daoTarefaTurma.findTarefaTurma(1);
		
		// pega o total de respostas de uma tarefa de um usuário
		boolean temTarefa = daoTarefaTurma.findRespostaByAluno(usuario, tarefa);
		
		//remove uma resposta
		if (temTarefa) {
			daoTarefaTurma.removerRespostaByTarefaAluno(1);
			assertFalse(daoTarefaTurma.findRespostaByAluno(usuario, tarefa));
		}
		
		
	}

}
