/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 09/11/2006
 *
 */
package br.ufrn.sigaa.test.dao.monitoria;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.test.MassaTesteIds;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 * Teste de MembroComissaoDao
 *
 * @author David Ricardo
 *
 */
public class MembroComissaoDaoTest extends MonitoriaTestCase {

	MembroComissaoDao dao;
	
	public void testIsInComissao() throws DAOException {
		Usuario usr = new Usuario(getIntMassa(MassaTesteIds.SERVIDOR_MONITORIA_1));
		boolean result = dao.isMembroComissaoMonitoria(usr.getServidor());
		assertFalse(result);
	}

	public void testFindByUsuario() throws DAOException {
		Usuario usr1 = new Usuario();
		usr1.setServidor(new Servidor(getIntMassa(MassaTesteIds.SERVIDOR_MONITORIA_1)));
		MembroComissao result1 = dao.findByUsuario(usr1, MembroComissao.MEMBRO_COMISSAO_MONITORIA);
		assertNull(result1);
		
		Usuario usr2 = new Usuario();
		usr2.setServidor(new Servidor(getIntMassa(MassaTesteIds.SERVIDOR_MONITORIA_2)));
		MembroComissao result2 = dao.findByUsuario(usr2, MembroComissao.MEMBRO_COMISSAO_MONITORIA);
		assertNotNull(result2);
		assertEquals(975610, result2.getId());
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dao = new MembroComissaoDao();
	}
	
	@Override
	protected void tearDown() throws Exception {
		dao.close();
	}
	
}
