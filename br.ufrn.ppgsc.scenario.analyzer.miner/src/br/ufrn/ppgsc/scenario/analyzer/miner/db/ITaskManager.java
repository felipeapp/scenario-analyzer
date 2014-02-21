package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.IProjectTask;

public interface ITaskManager {

	public List<IProjectTask> getTasksByRevision(long revision);
	
	public IProjectTask getTaskByNumber(long task_number);
	
	public long getTaskNumberFromLogMessage(String logMessage);
	
}
