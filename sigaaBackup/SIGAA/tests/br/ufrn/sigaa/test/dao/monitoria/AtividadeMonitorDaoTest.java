package br.ufrn.sigaa.test.dao.monitoria;

import java.util.List;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.monitoria.AtividadeMonitorDao;
import br.ufrn.sigaa.arq.test.MassaTesteIds;
import br.ufrn.sigaa.monitoria.dominio.AtividadeMonitor;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Teste Unitário de AtividadeMonitorDao
 * @author David Pereira
 *
 */
public class AtividadeMonitorDaoTest extends MonitoriaTestCase {
	
	private AtividadeMonitorDao dao;
	
	public void testFindDiscenteMonitoriaMesCorrente() throws DAOException {
		AtividadeMonitor am = dao.findByDiscenteMonitoriaMesCorrente(dm1, ef.getMes(), ef.getAno());
		assertEquals(getIntMassa(MassaTesteIds.ATIVIDADE_MONITORIA_DISCENTE), am.getId());
	}

	public void testFindByDiscente() throws DAOException{
		List<AtividadeMonitor> list = dao.findByDiscente(new Discente(getIntMassa(MassaTesteIds.MON_DISCENTE)));
		assertNotNull(list);
		assertFalse(list.isEmpty());
	}

	public void testFindByOrientador() throws DAOException{
		//Collection<AtividadeMonitor> list = dao.findByOrientador(servidor, ef.getMes(), ef.getAno());
		//assertNotNull(list);
		//assertTrue(list.isEmpty());
	}

	
	public void testFilter() throws HibernateException, DAOException {
		/*Collection<AtividadeMonitor> list = dao.filter(getIntMassa(MassaTesteIds.ANO_ATIVIDADE_MONITORIA), getIntMassa(MassaTesteIds.MES_ATIVIDADE_MONITORIA), 
				getIntMassa(MassaTesteIds.MON_DISCENTE), getIntMassa(MassaTesteIds.PROJETO_MONITORIA), null, null);
		assertNotNull(list);
		assertFalse(list.isEmpty()); */
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dao = new AtividadeMonitorDao();
	}
	
	public void tearDown() throws Exception {
		dao.close();
	}
	
}
