package br.ufrn.sigaa.test.dao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;

public class MatriculaDaoTest extends TestCase {

	DiscenteDao dao = new DiscenteDao();

	public void test1() throws DAOException {
		dao.findByAnoIngresso(2006, 1, 'G', null);
	}

	public void test2() throws DAOException {
		dao.findByAnoIngresso(2006, 1, 'G', null);
	}

	public void test3() throws DAOException {

	}

	public void test4() throws DAOException {

	}

}
