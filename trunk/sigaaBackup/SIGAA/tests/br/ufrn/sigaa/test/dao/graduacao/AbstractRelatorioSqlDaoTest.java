package br.ufrn.sigaa.test.dao.graduacao;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;


/**
 * 
 * Testes unit�rios realizados na classe AbstractRelatorioSqlDao
 * 
 * @author Edson Alyppyo G Coutinho
 *
 */

public class AbstractRelatorioSqlDaoTest extends TestCase {

	AbstractRelatorioSqlDao absdao = new AbstractRelatorioSqlDao();
	
	/** M�todo: executeSql
	 * 
	 * M�todo que realiza a consulta sql para um relat�rio, e retorna uma Lista
	 * das linhas da consulta
	 *
	 * @param String consultaSql
	 * 
	 * @return List<HashMap<String, Object>>
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SQLException
	 * 
	 */
	public void testExecuteSql() throws SQLException, HibernateException, DAOException {
		// Consulta com resultado
		assertFalse(absdao.executeSql("SELECT * FROM discente WHERE id_discente = 89935").isEmpty());
		
		// Consulta sem resultado
		assertTrue(absdao.executeSql("SELECT * FROM discente WHERE id_discente = -89935").isEmpty());
	}
	
	
	/** M�todo: getAnos
	 * 
	 * @param void
	 * 
	 * @return Collection<Integer>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testGetAnos() throws DAOException {
		// Esse m�todo sempre deve retornar resultados caso haja alguma turma cadastrada
		assertFalse(absdao.getAnos().isEmpty());
	}

	/** M�todo: getAnosConclusao
	 * 
	 * @param void
	 * 
	 * @return Collection<String>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testGetAnosConclusao() throws DAOException {
		// Esse m�todo sempre deve retornar resultados
		assertFalse(absdao.getAnosConclusao().isEmpty());
	}

	
	/** M�todo: getPeriodos
	 * 
	 * @param void
	 * 
	 * @return Collection<Integer>
	 * 
	 * @throws DAOException
	 *
	 */
	public void testGetPeriodos() throws DAOException {
		// Esse m�todo sempre deve retornar resultados
		assertFalse(absdao.getPeriodos().isEmpty());
	}

	/** M�todo: 
	 * 
	 * @param void
	 * 
	 * @return Collection<String>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testGetPeriodosConclusao() throws DAOException {
		// Esse m�todo sempre deve retornar resultados
		assertFalse(absdao.getPeriodos().isEmpty());
	}

}
