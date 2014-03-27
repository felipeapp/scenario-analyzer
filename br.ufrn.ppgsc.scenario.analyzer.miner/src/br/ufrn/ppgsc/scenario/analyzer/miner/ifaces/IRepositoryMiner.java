package br.ufrn.ppgsc.scenario.analyzer.miner.ifaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;

public interface IRepositoryMiner {

	public void initialize(String url, String user, String password,
			List<String> targetPaths, List<Long> startRevisions,
			List<Long> endRevisions);

	public void configure();

	public Map<String, Collection<UpdatedMethod>> mine();

	public long getCommittedRevisionNumber(String path);

}
