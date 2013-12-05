package br.ufrn.ppgsc.scenario.analyzer.backhoe.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class IProjectDAO {
	
	private final Logger logger = Logger.getLogger(IProjectDAO.class);
	
	private Connection connection;
	
	public IProjectDAO() {
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://bddesenv1.info.ufrn.br/sistemas_comum_20130604",
					"comum_user",
					"comum_user");
		} catch (SQLException e) {
			logger.warn(e.getMessage());
		}
	}
	
	public List<Long> getTaskNumberByRevision(long revision) {
		List<Long> task_numbers = new ArrayList<Long>();
		
		// Retorna a lista vazia não conectou.
		if (connection == null)
			return task_numbers;
		
		try {
			// TODO: testar
			/* SELECT id_tarefa FROM log_tarefa WHERE revision = '123' OR EXISTS
			 * (SELECT regexp_matches(log, '(^([ ]|[\n])*(Revisão|revisão|Revisao|revisao)[:| ]+123)'));
			 */
			
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT DISTINCT tarefa.numtarefa FROM iproject.log_tarefa INNER JOIN iproject.tarefa "
					+ "ON tarefa.id_tarefa = log_tarefa.id_tarefa AND "
					+ "(log_tarefa.revision = ? OR EXISTS "
					+ "(SELECT regexp_matches(log_tarefa.log, '(Revisão|revisão|Revisao|revisao)[:| ]+" + revision + "[ |.|,|\n]')))");
			
			stmt.setString(1, String.valueOf(revision));
			
			ResultSet rs = stmt.executeQuery();
			
			/* Coloquei em um While, mas não vejo como iria retornar
			 * mais de uma buscando pela revisão. Suponho que uma tarefa
			 * pode envolver várias revisões, mas uma revisão é gerado devido
			 * uma única tarefa, então o resultado deveria ser um conjunto de
			 * um elemento.
			 * 
			 * Isso não é verdade, curiosamente a revisão pode estar associada
			 * com mais de uma tarefa, pensebi quando testei para a revisão 70315  
			 */
			while (rs.next())
				task_numbers.add(Long.valueOf(rs.getString(1)));
		} catch (SQLException e) {
			logger.warn(e.getMessage());
		}
		
		return task_numbers;
	}
	
	public static void main(String[] args) {
		IProjectDAO dao = new IProjectDAO();
		for (long number : dao.getTaskNumberByRevision(70315))
			System.out.println(number);
	}
	
}
