package br.ufrn.ppgsc.scenario.analyzer.miner.sigaa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IContentIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;

public class SINFOIProjectIssueQuery implements IQueryIssue {
	
	private static final Logger logger = Logger.getLogger(SINFOIProjectIssueQuery.class);
	
	private static Connection connection;
	
	public SINFOIProjectIssueQuery() {
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
	
	public IContentIssue getIssueByNumber(long taskNumber) {
		IContentIssue task = new SINFOIProjectIssue();
		
		// Retorna o objeto vazio caso não tenha conectado.
		if (connection == null)
			return task;
		
		try {
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT tarefa.id_tarefa id, tarefa.numtarefa numero, tarefa.id_tipo_tarefa id_tipo, tipo_tarefa.denominacao tipo_denominacao"
					+ " FROM iproject.tarefa INNER JOIN iproject.tipo_tarefa"
					+ " ON tarefa.id_tipo_tarefa = tipo_tarefa.id_tipo_tarefa AND tarefa.numtarefa = ?");
			
			stmt.setLong(1, taskNumber);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				task.setId(rs.getLong("id"));
				task.setNumber(rs.getLong("numero"));
				task.setIdType(rs.getLong("id_tipo"));
				task.setTypeName(rs.getString("tipo_denominacao"));
			}
			else {
				logger.error("Task number " + taskNumber + " wasn't found.");
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Task number " + taskNumber + "\n" + e.getMessage());
		}
		
		return task;
	}
	
	// TODO: Remover depois! 
	// Este método não é mais usado!
	public List<SINFOIProjectIssue> getTasksByRevision(long revision) {
		List<SINFOIProjectIssue> tasks = new ArrayList<SINFOIProjectIssue>();
		
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
				SINFOIProjectIssue t = new SINFOIProjectIssue(
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
	
	public long getIssueNumberFromMessageLog(String messageLog) {
		Scanner in = new Scanner(messageLog);
		
		String task_word = in.next();
		String task_value = in.next().replaceAll("[^0-9]", "");

		in.close();
		
		long task_number;
		
		if (task_word.equalsIgnoreCase("commit")) {
			logger.info("Task word commit was found! Setting task number to -2!");
			task_number = -2;
		}
		else if (task_word.equalsIgnoreCase("tarefa") || task_word.equals("#")) {
			logger.info("Task word was found! [" + task_word + "] Setting task number to " + task_value + "!");
			task_number = Long.parseLong(task_value);
		}
		else if (task_word.matches("#[0-9]+")) {
			task_value = task_word.replaceAll("#", "");
			logger.info("Task word is task value! [" + task_word + "] Setting task number to " + task_value + "!");
			task_number = Long.parseLong(task_value);
		}
		else {
			logger.warn("Task word unknown [" + task_word + "]!\n" + messageLog);
			task_number = -1;
		}
		
		return task_number;
	}
	
	public static void main(String[] args) {
		SINFOIProjectIssueQuery dao = new SINFOIProjectIssueQuery();
		
		System.out.println(dao.getIssueByNumber(124277).getId());
		System.out.println(dao.getIssueByNumber(124787).getId());
		
		for (SINFOIProjectIssue t : dao.getTasksByRevision(70315))
			System.out.println(t.getNumber());
	}
	
}
