package br.ufrn.sigaa.test.dao.monitoria;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.monitoria.ItemAvaliacaoDao;
import br.ufrn.sigaa.arq.test.MassaTesteIds;
import br.ufrn.sigaa.monitoria.dominio.GrupoItemAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.ItemAvaliacaoMonitoria;

public class ItemAvaliacaoDaoTest extends MonitoriaTestCase {

	ItemAvaliacaoDao dao;
	
	public void testFindByGrupo() throws DAOException {
		GrupoItemAvaliacao grupo = new GrupoItemAvaliacao();
		grupo.setId(getIntMassa(MassaTesteIds.GRUPO_ITEM_AVALIACAO));
		Collection<ItemAvaliacaoMonitoria> list = dao.findByGrupo(grupo);
		assertNotNull(list);
		assertFalse(list.isEmpty());
	}
	
	public void testFindNotaTotalAtivos() throws DAOException {
		double nota = dao.findNotaTotalAtivos();
		assertTrue(nota > 0);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dao = new ItemAvaliacaoDao();
	}
	
	@Override
	protected void tearDown() throws Exception {
		dao.close();
	}
	
}
