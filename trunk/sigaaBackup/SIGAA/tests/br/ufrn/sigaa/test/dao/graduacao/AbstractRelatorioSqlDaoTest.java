package br.ufrn.sigaa.test.dao.graduacao;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;


/**
 * 
 * Testes unitários realizados na classe AbstractRelatorioSqlDao
 * 
 * @author Edson Alyppyo G Coutinho
 *
 */

public class AbstractRelatorioSqlDaoTest extends TestCase {

	AbstractRelatorioSqlDao absdao = new AbstractRelatorioSqlDao();
	
	/** Método: executeSql
	 * 
	 * Método que realiza a consulta sql para um relatório, e retorna uma Lista
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
	
	
	/** Método: getAnos
	 * 
	 * @param void
	 * 
	 * @return Collection<Integer>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testGetAnos() throws DAOException {
		// Esse método sempre deve retornar resultados caso haja alguma turma cadastrada
		assertFalse(absdao.getAnos().isEmpty());
	}

	/** Método: getAnosConclusao
	 * 
	 * @param void
	 * 
	 * @return Collection<String>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testGetAnosConclusao() throws DAOException {
		// Esse método sempre deve retornar resultados
		assertFalse(absdao.getAnosConclusao().isEmpty());
	}

	
	/** Método: getPeriodos
	 * 
	 * @param void
	 * 
	 * @return Collection<Integer>
	 * 
	 * @throws DAOException
	 *
	 */
	public void testGetPeriodos() throws DAOException {
		// Esse método sempre deve retornar resultados
		assertFalse(absdao.getPeriodos().isEmpty());
	}

	/** Método: 
	 * 
	 * @param void
	 * 
	 * @return Collection<String>
	 * 
	 * @throws DAOException
	 * 
	 */
	public void testGetPeriodosConclusao() throws DAOException {
		// Esse método sempre deve retornar resultados
		assertFalse(absdao.getPeriodos().isEmpty());
	}

}
