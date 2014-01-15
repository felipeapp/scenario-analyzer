/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/11/2009
 *
 */
package br.ufrn.sigaa.test.dao;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;

/** 
 * Classe de teste unitário da classe {@link MovimentacaoAlunoDao}. Feita para testar 
 * os métodos da classe MovimentacaoAlunoDao
 *   
 * @author Édipo Elder F. Melo
 *
 */
public class MovimentacaoAlunoDaoTest extends TestCase {
	
	private static SessionFactory sessionFactory;
	private static MovimentacaoAlunoDao dao;
	
	static {
		dao = new MovimentacaoAlunoDao();
		dao.setSistema(Sistema.SIGAA);
		try {
			DAOFactory df = DAOFactory.getInstance();
			sessionFactory = df.getSessionFactory(Sistema.SIGAA);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao.setSession(getSessionFactory().getCurrentSession());
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	private final int idDiscenteGraduacaoConcluido = 62103; // matrícula 200010413
	
	/** Teste da classe {@link MovimentacaoAlunoDao#findByDiscenteOrTipoMovimentacao}. 
	 * @throws DAOException */
	public void testFindByDiscenteOrTipoMovimentacao() throws DAOException {
		assertNotNull(dao.findByDiscenteOrTipoMovimentacao(idDiscenteGraduacaoConcluido, TipoMovimentacaoAluno.CONCLUSAO, true, 0, 'G', null));
	}

	public void testFindAfastamentosByDiscente() {
		fail("Not yet implemented");
	}

	public void testFindAfastamentosFuturosByDiscente() {
		fail("Not yet implemented");
	}

	public void testFindTrancamentosFuturosByDiscente() {
		fail("Not yet implemented");
	}

	public void testFindByDiscenteIntBoolean() {
		fail("Not yet implemented");
	}

	public void testFindByDiscenteSemRetorno() {
		fail("Not yet implemented");
	}

	public void testFindTrancamentosByDiscenteIntIntIntBoolean() {
		fail("Not yet implemented");
	}

	public void testFindTracamentoByDiscente() {
		fail("Not yet implemented");
	}

	public void testFindTrancamentosByDiscentes() {
		fail("Not yet implemented");
	}

	public void testFindTrancamentosByDiscenteIntIntChar() {
		fail("Not yet implemented");
	}

	public void testFindUltimoAfastamentoByDiscente() {
		fail("Not yet implemented");
	}

	public void testCountProrrogacoesByDiscente() {
		fail("Not yet implemented");
	}

	public void testFindProrrogacoesByDiscente() {
		fail("Not yet implemented");
	}

	public void testFindByDiscenteTipoMovimentacao() {
		fail("Not yet implemented");
	}

	public void testFindByDiscenteDiscenteIntIntTipoMovimentacaoAluno() {
		fail("Not yet implemented");
	}

	public void testFindConclusaoByDiscente() {
		fail("Not yet implemented");
	}

	public void testFindByDiscenteRetornoAtivo() {
		fail("Not yet implemented");
	}

}
