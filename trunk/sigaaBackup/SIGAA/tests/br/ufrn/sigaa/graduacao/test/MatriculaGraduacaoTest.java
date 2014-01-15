package br.ufrn.sigaa.graduacao.test;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.MatriculaGraduacaoDao;

public class MatriculaGraduacaoTest extends AbstractGraduacaoTestCase {

	MatriculaGraduacaoDao daoMatricula = new MatriculaGraduacaoDao();

	public void testComponentesAprovadosDiscente() throws DAOException {
		//assertFalse(daoMatricula.findComponentesAprovadosByDiscente(discenteAtivo).isEmpty());
	}

	public void testSugestaoMatricula() throws DAOException {
//		assertFalse(daoMatricula.findSugestoesMatricula(discenteAtivo , getParametrosGraduacao()).isEmpty());
	}


}
