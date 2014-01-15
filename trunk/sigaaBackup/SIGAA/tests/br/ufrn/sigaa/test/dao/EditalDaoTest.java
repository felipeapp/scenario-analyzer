package br.ufrn.sigaa.test.dao;

/**
 * 
 * Teste Unit�rio da classe EditalDao
 * 
 * @author Dalton
 * 
 * =====> Com erros
 * 
 */
import java.util.Collection;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.projetos.dominio.Edital;

public class EditalDaoTest extends TestCase {
	
	EditalDao dao = new EditalDao();

	/**
	 * Retorna o edital mais recentemente aberto dado o tipo de classe passado
	 * 
	 * M�todo: findMaisRecente
	 * 
	 * @param classe
	 * 
	 * @return <T extends Edital>
	 * @throws DAOException
	 * 
	 */
	public void testFindMaisRecente() throws DAOException {

		Edital result = dao.findMaisRecente(Edital.PESQUISA);
		assertEquals(result.getTipo(), 'P');
		
		// testa com valor nulo
		assertTrue(dao.findMaisRecente(null).equals(null));

	}
	
	/**
	 * Retorna uma cole��o de Editais de acordo com o limite de registros passado.
	 * 
	 * M�todo: findAllOtimizado
	 * 
	 * @param limit
	 * 
	 * @return Collection<Edital>
	 * @throws DAOException
	 * 
	 */
	public void testFindAllOtimizado() throws DAOException {
		
		Collection<Edital> result = dao.findAllOtimizado(3);
		System.out.println("Edital - 2:" + result.size());
		assertEquals(result.size(), 3);
		
	}
	
	/**
	 * Retorna a quantidade de Editais
	 *
	 * M�todo: getTotal
	 *
	 * @return int
	 * @throws DAOException
	 * 
	 */
	public void testGetTotal() throws DAOException {
		
		int result = dao.getTotal();
		System.out.println("Edital - 3:" + result);
		assertEquals(result, 7);
	}
	
	/**
	 * 	 * Teste deu errado, n�o retornou nenhum registro
	 *
	 * M�todo: findAbertos
	 * 
	 * Busca todos os editais cujo per�odo de submiss�o est� aberto
	 *
	 * @param limite
	 * @param tipo M - monitoria, P - Pesquisa e E - Extensao
	 * 
	 * @return Collection<Edital>
	 * @throws DAOException
	 * 
	 */
	public void testFindAbertos() throws DAOException {
		Collection<Edital> result = dao.findAbertos(2, 'P');
		System.out.println("resulteditais1: " + result.size());
		assertEquals(result.size(), 1);
	}
	
	/**
	 * Busca TODOS os editais em aberto
	 * 
	 * M�todo: findAbertos
	 * 
	 * @param 
	 * 
	 * @return Collection<Edital>
	 * @throws DAOException
	 * 
	 */
	public void testFindAbertosTodos() throws DAOException {
		Collection<Edital> result = dao.findAbertos();
		System.out.println("editais abertos: " + result.size());
		assertTrue(result.size() > 0);
	}
	
	/**
	 * Busca editais em aberto do tipo informado
	 * 
	 * M�todo: findAbertos
	 * 
	 * @param tipo M - monitoria, P - Pesquisa e E - Extensao
	 * 
	 * @return Collection<Edital>
	 * @throws DAOException
	 * 
	 */
	public void testFindAbertosPorTipo() throws DAOException {
		Collection<Edital> result = dao.findAbertos('P');
		assertEquals(result.size(), 1);
	}
	
	/**
	 * Busca editais n�o finalizados passando um limite de registros de retorno
	 * 
	 * M�todo: findNaoFinalizados
	 * 
	 * @param limite
	 * 
	 * @return Collection<Edital>
	 * @throws DAOException
	 * 
	 */
	public void testFindNaoFinalizados() throws DAOException {
		Collection<Edital> result = dao.findNaoFinalizados(3);
		//System.out.println("editais finalizados: " + result.size());
		assertTrue(result.size() > 0);
	}
	
	/**
	 * Busca todos os editais n�o finalizados
	 * 
	 * M�todo: findnaoFinalizados
	 * 
	 * @return Collection<Edital>
	 * @throws DAOException
	 * 
	 */
	public void testFindNaoFinalizadosTodos() throws DAOException {
		Collection<Edital> result = dao.findNaoFinalizados();
		//System.out.println("editais finalizados: " + result.size());
		assertTrue(result.size() > 0);
	}
	
	/**
	 * Busca todos os editais n�o finalizados
	 * 
	 * M�todo: findnaoFinalizados
	 * 
	 * @return Collection<Edital>
	 * @throws DAOException
	 * 
	 */
	public void testGetNumeroBolsasEditais() throws DAOException {
		double result = dao.getNumeroBolsasEditais(1, 904090);
		System.out.println("N�mero de Bolsas: " + result);
		assertTrue(result > 0);
	}
}
