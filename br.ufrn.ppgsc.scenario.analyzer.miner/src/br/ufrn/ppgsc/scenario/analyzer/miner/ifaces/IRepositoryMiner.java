package br.ufrn.ppgsc.scenario.analyzer.miner.ifaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;

public interface IRepositoryMiner {

	public void connect(String url, String user, String password);

	public void initialize(List<String> targetPaths, List<Long> startRevisions, List<Long> endRevisions);

	public Map<String, Collection<UpdatedMethod>> mine();

	public long getCommittedRevisionNumber(String path);

	public String getUrl();

	public String getUser();

	public String getPassword();

}
