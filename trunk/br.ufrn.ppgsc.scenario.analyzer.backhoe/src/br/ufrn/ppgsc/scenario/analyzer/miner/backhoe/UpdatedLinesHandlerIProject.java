package br.ufrn.ppgsc.scenario.analyzer.miner.backhoe;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.IProjectDAO;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.IProjectTask;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.UpdatedMethodsMinerUtil;

public class UpdatedLinesHandlerIProject implements ISVNAnnotateHandler {

	private final Logger logger = Logger.getLogger(UpdatedLinesHandlerIProject.class);
	
	private Map<Long, List<IProjectTask>> revisionList;
	private List<UpdatedLine> changedLines;
	private StringBuilder sourceCode;
	private IProjectDAO ipdao;
	
	private String path;
	private SVNRepository repository;
	
	public UpdatedLinesHandlerIProject(SVNRepository repository, String path) {
		revisionList = new HashMap<Long, List<IProjectTask>>();
		changedLines = new ArrayList<UpdatedLine>();
		sourceCode = new StringBuilder();
		ipdao = new IProjectDAO();
		
		this.path = path;
		this.repository = repository;
	}
	
	public List<UpdatedLine> getChangedLines() {
		return Collections.unmodifiableList(changedLines);
	}
	
	public String getSourceCode() {
		return sourceCode.toString();
	}
	
	public void handleEOF() throws SVNException {
		System.out.println("EOF has just been found!");
	}

	public void handleLine(Date date, long revision, String author, String line) throws SVNException {
		handleLine(date, revision, author, line, null, -1, null, null, -1);
	}

	public void handleLine(Date date, long revision, String author, String line, Date mergedDate,
			long mergedRevision, String mergedAuthor, String mergedPath, int lineNumber) throws SVNException {
		
		sourceCode.append(line + "\n");
		
		if (revision != -1) {
			List<IProjectTask> tasks = revisionList.get(revision);
			
			if (tasks == null) {
				logger.info("Inside handler, getting tasks to revision " + revision);

				IProjectTask task = null;
				String logMessage = repository.getRevisionPropertyValue(revision, SVNRevisionProperty.LOG).getString();
				long task_number = UpdatedMethodsMinerUtil.getTaskNumberFromLogMessage(logMessage);
				
				if (task_number < 0) {
					logger.warn("Path: " + path + ", revision = " + revision + ", task number = " + task_number);
					task = new IProjectTask();
					task.setNumber(-1);
				}
				else {
					task = ipdao.getTaskByNumber(task_number);
				}
				
				tasks = new ArrayList<IProjectTask>();
				tasks.add(task);
				
				revisionList.put(revision, tasks);
			}
			
			changedLines.add(new UpdatedLine(date, revision, tasks, author, line, lineNumber));
		}
	}

	public boolean handleRevision(Date date, long revision, String author, File contents) throws SVNException {
//		System.out.println("date: " + date);
//		System.out.println("revision: " + revision);
//		System.out.println("author: " + author);
//		System.out.println("contents: " + contents.getName());
//		System.out.println("************************");
		return false;
	}

}
