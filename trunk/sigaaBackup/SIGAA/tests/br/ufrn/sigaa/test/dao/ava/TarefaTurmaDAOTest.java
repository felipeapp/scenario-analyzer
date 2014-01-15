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
 * Retorna as Tarefas de uma turma, s� as que n�o est�o com o campo Ativo = false.
 * 
 * Teste Unit�rio da classe RegistroAtividadeDao
 * 
 * @author Dalton
 *
 */
public class TarefaTurmaDAOTest extends TestCase {

	/**busca tarefa */
	TarefaTurmaDao daoTarefaTurma = new TarefaTurmaDao();
	
	/** busca usu�rio */
	UsuarioDao daoUsuario = new UsuarioDao();

	/** buscar uma turma*/
	TurmaDao daoTurma = new TurmaDao();
	
	/** 
	 * M�todo: findByTurma()
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
		
		// Informar valores v�lidos
		assertTrue(daoTarefaTurma.findByTurma(turma,true).size() > 0);
		
		// informar valores nulos
		assertTrue(daoTarefaTurma.findByTurma(null,true).size() == 0);
	}

	/** 
	 * M�todo: findTarefaTurma()
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
		// Informar valores v�lidos
		assertNotNull(daoTarefaTurma.findTarefaTurma(1));
		
		// informar valores inv�lidos
		assertNull(daoTarefaTurma.findTarefaTurma(-1));
	}

	/** 
	 * M�todo: findTarefaTurma()
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
		// busca usu�rio
		Usuario usuario = (Usuario)daoUsuario.findByLogin("alyppyo"); 
		
		// busca tarefa
		TarefaTurma tarefa = daoTarefaTurma.findTarefaTurma(1);
		
		// testando com valores v�lidos
		assertNotNull(daoTarefaTurma.findRespostaByTarefaAluno(tarefa, usuario));
		
		// testando com valores nulos
		assertNotNull(daoTarefaTurma.findRespostaByTarefaAluno(null, null));
	}

	/** 
	 * M�todo: findRespostaByAluno()
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
		// busca usu�rio
		Usuario usuario = (Usuario)daoUsuario.findByLogin("alyppyo"); 
		
		// busca tarefa
		TarefaTurma tarefa = daoTarefaTurma.findTarefaTurma(1);
		
		// testando com valores v�lidos
		assertTrue(daoTarefaTurma.findRespostaByAluno(usuario, tarefa));
		
		// testando com valores nulos
		assertFalse(daoTarefaTurma.findRespostaByAluno(null, null));
		
	}

	/** 
	 * M�todo: findRespostasNaoAvaliadasByTarefa()
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
		
		// testando com valores v�lidos
		assertNotNull(daoTarefaTurma.findRespostasNaoAvaliadasByTarefa(tarefa,turma));
		
		// testando com valores nulos
		assertNotNull(daoTarefaTurma.findRespostasNaoAvaliadasByTarefa(null,turma));
	}

	/** 
	 * M�todo: removerRespostaByTarefaAluno()
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
		
		// busca usu�rio
		Usuario usuario = (Usuario)daoUsuario.findByLogin("alyppyo"); 
		
		// busca tarefa
		TarefaTurma tarefa = daoTarefaTurma.findTarefaTurma(1);
		
		// pega o total de respostas de uma tarefa de um usu�rio
		boolean temTarefa = daoTarefaTurma.findRespostaByAluno(usuario, tarefa);
		
		//remove uma resposta
		if (temTarefa) {
			daoTarefaTurma.removerRespostaByTarefaAluno(1);
			assertFalse(daoTarefaTurma.findRespostaByAluno(usuario, tarefa));
		}
		
		
	}

}
