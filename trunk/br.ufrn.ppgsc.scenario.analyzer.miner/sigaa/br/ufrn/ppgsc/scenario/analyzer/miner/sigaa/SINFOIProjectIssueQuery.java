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

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;

public class SINFOIProjectIssueQuery implements IQueryIssue {
	
	private static final Logger logger = Logger.getLogger(SINFOIProjectIssueQuery.class);
	
	private static Connection connection;
	
	public SINFOIProjectIssueQuery() {
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(
						"jdbc:postgresql://localhost:5432/sistemas_comum",
						"postgres", "1234");
			}
		} catch (SQLException e) {
			logger.warn(e.getMessage());
		}
	}
	
	public Issue getIssueByNumber(long taskNumber) {
		Issue task = new Issue();
		
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
				task.setIssueId(rs.getLong("id"));
				task.setNumber(rs.getLong("numero"));
//				task.setIdType(rs.getLong("id_tipo"));
				task.setIssueType(rs.getString("tipo_denominacao"));
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
	// Este método não é mais usado, mas o select ficou tão bonito :-)!
	public List<Issue> getIssuesByRevision(long revision) {
		List<Issue> tasks = new ArrayList<Issue>();
		
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
				Issue issue = new Issue();
				
				issue.setIssueId(rs.getLong("id"));
				issue.setNumber(rs.getLong("numero"));
				issue.setIssueTypeId(rs.getLong("id_tipo"));
				issue.setIssueType(rs.getString("tipo_denominacao"));
				
				tasks.add(issue);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		
		return tasks;
	}
	
	public List<Long> getIssueNumbersFromMessageLog(String messageLog) {
		Scanner in = new Scanner(messageLog);
		
		String task_word = in.next();
		String task_value = in.next().replaceAll("[^0-9]", "");

		in.close();
		
		List<Long> issuesId = new ArrayList<Long>();
		
		if (task_word.equalsIgnoreCase("commit")) {
			logger.info("Task word commit was found! Setting task number to -2!");
			issuesId.add(-2L);
		}
		else if (task_word.equalsIgnoreCase("tarefa") || task_word.equals("#")) {
			logger.info("Task word was found! [" + task_word + "] Setting task number to " + task_value + "!");
			issuesId.add(Long.parseLong(task_value));
		}
		else if (task_word.matches("#[0-9]+")) {
			task_value = task_word.replaceAll("#", "");
			logger.info("Task word is task value! [" + task_word + "] Setting task number to " + task_value + "!");
			issuesId.add(Long.parseLong(task_value));
		}
		else {
			logger.warn("Task word unknown [" + task_word + "]!\n" + messageLog);
			issuesId.add(-1L);
		}
		
		return issuesId;
	}
	
	public static void main(String[] args) {
		SINFOIProjectIssueQuery dao = new SINFOIProjectIssueQuery();
		
		System.out.println(dao.getIssueByNumber(124277).getIssueId());
		System.out.println(dao.getIssueByNumber(124787).getIssueId());
		
		for (Issue t : dao.getIssuesByRevision(70315))
			System.out.println(t.getNumber());
	}
	
}
