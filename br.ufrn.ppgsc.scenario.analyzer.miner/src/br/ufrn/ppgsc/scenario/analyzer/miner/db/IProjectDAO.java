package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.IProjectTask;

public class IProjectDAO {
	
	private static final Logger logger = Logger.getLogger(IProjectDAO.class);
	
	private static Connection connection;
	
	public IProjectDAO() {
		try {
//			if (connection == null) {
//				connection = DriverManager.getConnection(
//						"jdbc:postgresql://bddesenv1.info.ufrn.br:5432/sistemas_comum_20131009",
//						"comum_user", "comum_user");
//			}
			
			if (connection == null) {
				connection = DriverManager.getConnection(
						"jdbc:postgresql://localhost:5432/sistemas_comum",
						"postgres", "1234");
			}
		} catch (SQLException e) {
			logger.warn(e.getMessage());
		}
	}
	
	public IProjectTask getTaskByNumber(long task_number) {
		IProjectTask task = new IProjectTask();
		
		// Retorna o objeto vazio caso não tenha conectado.
		if (connection == null)
			return task;
		
		try {
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT tarefa.id_tarefa id, tarefa.numtarefa numero, tarefa.id_tipo_tarefa id_tipo, tipo_tarefa.denominacao tipo_denominacao"
					+ " FROM iproject.tarefa INNER JOIN iproject.tipo_tarefa"
					+ " ON tarefa.id_tipo_tarefa = tipo_tarefa.id_tipo_tarefa AND tarefa.numtarefa = ?");
			
			stmt.setLong(1, task_number);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				task.setId(rs.getLong("id"));
				task.setNumber(rs.getLong("numero"));
				task.setIdType(rs.getLong("id_tipo"));
				task.setTypeName(rs.getString("tipo_denominacao"));
			}
			else {
				logger.error("Task number " + task_number + " wasn't found.");
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Task number " + task_number + "\n" + e.getMessage());
		}
		
		return task;
	}
	
	public List<IProjectTask> getTasksByRevision(long revision) {
		List<IProjectTask> tasks = new ArrayList<IProjectTask>();
		
		// Retorna a lista vazia não conectou.
		if (connection == null)
			return tasks;
		
		try {
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT DISTINCT tarefa.id_tarefa id, tarefa.numtarefa numero, tarefa.id_tipo_tarefa id_tipo, tipo_tarefa.denominacao tipo_denominacao"
					+ " FROM iproject.log_tarefa INNER JOIN iproject.tarefa"
					+ " ON tarefa.id_tarefa = log_tarefa.id_tarefa AND"
					+ " (log_tarefa.revision = ? OR EXISTS (SELECT regexp_matches(log_tarefa.log, '(Revisão|revisão|Revisao|revisao)[:| ]*" + revision + "[^0-9]')))"
					+ " inner join iproject.tipo_tarefa on tarefa.id_tipo_tarefa = tipo_tarefa.id_tipo_tarefa");
			
			stmt.setString(1, String.valueOf(revision));
			
			ResultSet rs = stmt.executeQuery();
			
			/* 
			 * A revisão pode estar associada com mais de uma tarefa,
			 * pensebi quando testei para a revisão 70315.  
			 */
			while (rs.next()) {
				IProjectTask t = new IProjectTask(
						rs.getLong("id"),
						rs.getLong("numero"),
						rs.getLong("id_tipo"),
						rs.getString("tipo_denominacao"));
				
				tasks.add(t);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		
		return tasks;
	}
	
	public static void main(String[] args) {
		IProjectDAO dao = new IProjectDAO();
		
		System.out.println(dao.getTaskByNumber(124277).getId());
		System.out.println(dao.getTaskByNumber(124787).getId());
		
		for (IProjectTask t : dao.getTasksByRevision(70315))
			System.out.println(t.getNumber());
	}
	
}
