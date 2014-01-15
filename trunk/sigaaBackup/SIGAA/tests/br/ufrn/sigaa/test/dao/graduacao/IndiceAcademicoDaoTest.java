/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2010
 */
package br.ufrn.sigaa.test.dao.graduacao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;

/**
 * Testes para a classe {@link IndiceAcademicoDao}.
 * 
 * @author David Pereira
 *
 */
public class IndiceAcademicoDaoTest extends TestCase {

	public static int idDiscente = 89935;  
	private IndiceAcademicoDao dao;
	
	@Override
	protected void setUp() throws Exception {
		dao = new IndiceAcademicoDao();
	}
	
	@Override
	protected void tearDown() throws Exception {
		dao.close();
	}
	
	
	/** M�todo: calculaIraDiscente
	 *
	 * Retorna a m�dia final de um discente.
	 *
	 * Considera todas as disciplinas e todas as atividades do tipo
	 * est�gio e trabalho de conclus�o de curso, exceto aquelas que
	 * tem carga hor�ria zero. S�o exclu�das todas as atividades do
	 * tipo "atividade complementar".
	 *
	 * @param int idDiscente
	 *
	 * @return float
	 *
	 * @throws DAOException
	 */

	public void testCalculaIraDiscente() throws DAOException {
		// Informando idDiscente v�lida
		assertTrue(Math.round(dao.calculaIraDiscente(idDiscente)*1000) > 7406);

		// Informando idDiscente inv�lida
		assertEquals(Math.round(dao.calculaIraDiscente(0)), 0);
	}
	
}
