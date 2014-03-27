package br.ufrn.ppgsc.scenario.analyzer.miner.svn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IRepositoryMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class RepositoryManager {

	private IRepositoryMiner miner;

	public RepositoryManager() {
		miner = SystemMetadataUtil.getInstance().getRepositoryMinerObject();
	}
	
	public Map<String, Collection<UpdatedMethod>> getUpdatedMethodsFromRepository(
			String host, String user, String password, List<String> paths,
			List<String> old_workcopies, List<String> new_workcopies) {

		List<Long> old_revisions = new ArrayList<Long>();
		List<Long> new_revisions = new ArrayList<Long>();

		for (int i = 0; i < paths.size(); i++) {
			old_revisions.add(miner.getCommittedRevisionNumber(old_workcopies
					.get(i)));
			new_revisions.add(miner.getCommittedRevisionNumber(new_workcopies
					.get(i)));
		}

		miner.initialize(host, user, password, paths, old_revisions,
				new_revisions);
		miner.configure();

		return miner.mine();
	}

	public Collection<UpdatedMethod> getUpdatedMethodsFromRepository(
			String host, String user, String password, String file_path,
			String old_workcopy, String new_workcopy) {

		return getUpdatedMethodsFromRepository(host, user, password, file_path,
				miner.getCommittedRevisionNumber(old_workcopy),
				miner.getCommittedRevisionNumber(new_workcopy));
	}

	public Collection<UpdatedMethod> getUpdatedMethodsFromRepository(
			String host, String user, String password, String file_path,
			long old_revision, long new_revision) {

		miner.initialize(host, user, password,
				Arrays.asList(new String[] { file_path }),
				Arrays.asList(new Long(old_revision)),
				Arrays.asList(new Long(new_revision)));

		miner.configure();

		return miner.mine().get(file_path);
	}

}
