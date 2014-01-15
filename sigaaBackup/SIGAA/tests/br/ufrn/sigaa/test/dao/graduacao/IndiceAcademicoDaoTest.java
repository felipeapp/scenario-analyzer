/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
	
	
	/** Método: calculaIraDiscente
	 *
	 * Retorna a média final de um discente.
	 *
	 * Considera todas as disciplinas e todas as atividades do tipo
	 * estágio e trabalho de conclusão de curso, exceto aquelas que
	 * tem carga horária zero. São excluídas todas as atividades do
	 * tipo "atividade complementar".
	 *
	 * @param int idDiscente
	 *
	 * @return float
	 *
	 * @throws DAOException
	 */

	public void testCalculaIraDiscente() throws DAOException {
		// Informando idDiscente válida
		assertTrue(Math.round(dao.calculaIraDiscente(idDiscente)*1000) > 7406);

		// Informando idDiscente inválida
		assertEquals(Math.round(dao.calculaIraDiscente(0)), 0);
	}
	
}
