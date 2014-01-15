package br.ufrn.sigaa.test.dao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.TipoArtisticoDao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

public class TipoArtisticoDaoTeste extends TestCase {

	TipoArtisticoDao tipoArtisticoDao = new TipoArtisticoDao();
	
	/**
	 * Retorna o TipoArtistico de uma determinada Producao Intelectual.
	 * 
	 * @param Produ��o Intelectual.
	 * 
	 * @return Tipo Art�stico
	 * @throws DAOException
	 * 
	 * @Autor Paulo Ricardo / Dalton
	 */
	public void testFindTipoArtisticoByProducao() throws DAOException {

		TipoProducao tipoProducao = new TipoProducao();

		//Um tipo de produ��o existente no Bd
		tipoProducao.setId(9);
	    tipoProducao.setDescricao("Montagens");
	   
		assertNotNull(tipoArtisticoDao.findTipoArtisticoByProducao(tipoProducao));
		
		//Um tipo de produ��o n�o existente no bd
		tipoProducao.setId(3216546);
	    tipoProducao.setDescricao("Este tipo nunca vai existir");
		
	    assertNull(tipoArtisticoDao.findTipoArtisticoByProducao(tipoProducao));
		
	    // passando valor nulo
	    assertTrue(tipoArtisticoDao.findTipoArtisticoByProducao(null).size() == 0);
	    
	}

}
