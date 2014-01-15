package br.ufrn.sigaa.test.dao.monitoria;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.monitoria.InscricaoSelecaoMonitoriaDao;
import br.ufrn.sigaa.arq.test.MassaTesteIds;
import br.ufrn.sigaa.monitoria.dominio.InscricaoSelecaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Testes de InscricaoSelecaoMonitoriaDao
 * 
 * @author David Pereira
 * 
 */
public class InscricaoSelecaoMonitoriaDaoTest extends MonitoriaTestCase {

	InscricaoSelecaoMonitoriaDao dao;
	
	public void testFindByDiscenteProjeto() throws DAOException {
		InscricaoSelecaoMonitoria insc = dao.findByDiscenteProjeto(new Discente(getIntMassa(MassaTesteIds.MON_DISCENTE)), new ProvaSelecao(getIntMassa(MassaTesteIds.PROVA_SELECAO)));
		assertNull(insc);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dao = new InscricaoSelecaoMonitoriaDao();
	}
	
	@Override
	protected void tearDown() throws Exception {
		dao.close();
	}
	
}
