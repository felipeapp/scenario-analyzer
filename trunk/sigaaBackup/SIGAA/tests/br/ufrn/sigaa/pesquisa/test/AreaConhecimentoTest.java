package br.ufrn.sigaa.pesquisa.test;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

public class AreaConhecimentoTest  extends TestCase {

	AreaConhecimentoCnpqDao dao = new AreaConhecimentoCnpqDao();

	public void testRecuperaGrandesAreas() throws DAOException {
		assertTrue(dao.findGrandeAreasConhecimentoCnpq().size() > 0 );
	}

	public void testAreaByGrandeArea() throws DAOException {
		assertTrue(dao.findAreas(new AreaConhecimentoCnpq(10000003)).size() > 0);
	}
}
