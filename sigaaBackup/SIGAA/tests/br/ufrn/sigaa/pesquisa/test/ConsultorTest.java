package br.ufrn.sigaa.pesquisa.test;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultorDao;
import br.ufrn.sigaa.arq.test.SigaaTestCase;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

public class ConsultorTest extends SigaaTestCase {

	ConsultorDao dao = new ConsultorDao();

	public ConsultorTest() {
		setName("Teste de Consultores");
	}

	public void testFindByCodigoAcesso() throws DAOException {
		assertNotNull(dao.findByCodigoAcesso(getIntMassa("codigoConsultor")));
	}

	public void testValidateAcesso() throws DAOException {
		assertNotNull(dao.validateAcesso(getIntMassa("codigoConsultor"), getParamMassa("senhaConsultor")));
	}

	public void testFindByNomeTipo() throws DAOException {
		assertTrue(dao.findByNomeAndTipo(getParamMassa("nomeConsultor"), false).size() > 0);
	}

	public void testFindByAreaConhecimentoCNPQ() throws DAOException {
		assertTrue(dao.findByAreaConhecimentoCnpq(new AreaConhecimentoCnpq(getIntMassa("areaComConsultor")), 10).size() > 0);
	}

}
