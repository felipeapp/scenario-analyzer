package br.ufrn.ppgsc.scenario.analyzer.miner.ifaces;

import java.util.List;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;

public interface IRepositoryMiner {

	public void connect(String url, String user, String password);

	public void initialize();
	
	public void close();

	public Object mine(String path, String startRevision, String endRevision);

	public String getCommittedRevisionNumber(String path);

	public Object getLinesHandler(String path);

	public Object putLinesHandler(String path, Object handler);

	public Set<String> getAllLinesHandlerKeys();

	public List<UpdatedLine> getChangedLines(String path);

	public String getSourceCode(String path);

	// public String getUrl();
	//
	// public String getUser();
	//
	// public String getPassword();

}
