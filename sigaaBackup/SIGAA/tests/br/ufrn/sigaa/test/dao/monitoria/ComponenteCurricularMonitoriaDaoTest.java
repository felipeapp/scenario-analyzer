/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Mar 19, 2007
 *
 */
package br.ufrn.sigaa.test.dao.monitoria;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.monitoria.ComponenteCurricularMonitoriaDao;
import br.ufrn.sigaa.arq.test.MassaTesteIds;


/**
 * Teste de ComponenteCurricularMonitoriaDao
 * 
 * @author David Pereira
 *
 */
public class ComponenteCurricularMonitoriaDaoTest extends MonitoriaTestCase {

	ComponenteCurricularMonitoriaDao dao;
	
	public void testFindQtdComponentesMonitoriaObrigatoriasByProjeto() throws HibernateException, DAOException {
		int qtd = dao.findQtdComponentesMonitoriaObrigatoriasByProva(getIntMassa(MassaTesteIds.PROVA_SELECAO),true);
		assertTrue(qtd == 0);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dao = new ComponenteCurricularMonitoriaDao(); 
	}
	
	@Override
	protected void tearDown() throws Exception {
		dao.close();
	}
	
}
