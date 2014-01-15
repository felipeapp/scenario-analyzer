package br.ufrn.sigaa.graduacao.test;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;

/**
 *
 * @author Andre M Dantas
 *
 */
public class DiscenteGraduacaoTest extends AbstractGraduacaoTestCase {

	DiscenteDao ddao = new DiscenteDao();

	DiscenteGraduacaoDao dao = new DiscenteGraduacaoDao();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

	public void testBuscaDiscentePorCurso() throws Exception {
		/*assertFalse(dao.findByCursoAnoPeriodo(cursoAtivo.getId(), getParametrosGraduacao().getAnoAtual(),
				getParametrosGraduacao().getPeriodoAtual(), null).isEmpty());*/
	}

	public void testBuscaDiscentesAtivosPorPessoa() throws Exception {
		assertNotNull(dao.findAtivoByPessoa(discenteAtivo.getPessoa().getId()));
	}

	public void testMudancasMatrizDeUmDiscente() throws Exception {
		Collection res = dao.findMudancasMatriz(discenteAtivo);
		assertTrue(res == null || res.isEmpty());
	}



	public void testBuscaHistorico() throws DAOException {
//		assertTrue(ddao.findByDisciplinasCurricularesPendentes(discenteAtivo.getId()).isEmpty());
	}

}
