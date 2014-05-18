package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IRepositoryMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;

public class RepositoryManager {

	private IRepositoryMiner miner;

	public RepositoryManager(String url, String user, String password) {
		miner = SystemMetadataUtil.getInstance().newObjectFromProperties(IRepositoryMiner.class);
		miner.connect(url, user, password);
	}

	public Map<String, Collection<UpdatedMethod>> getUpdatedMethodsFromRepository(
			List<String> paths, List<String> old_workcopies,
			List<String> new_workcopies) {

		List<String> old_revisions = new ArrayList<String>();
		List<String> new_revisions = new ArrayList<String>();

		for (int i = 0; i < paths.size(); i++) {
			old_revisions.add(miner.getCommittedRevisionNumber(old_workcopies.get(i)));
			new_revisions.add(miner.getCommittedRevisionNumber(new_workcopies.get(i)));
			
//			System.out.println("Path: " + paths.get(i));
//			System.out.println("Old: " + old_revisions.get(i));
//			System.out.println("New: " + old_revisions.get(i));
		}

		miner.initialize(paths, old_revisions, new_revisions);

		return miner.mine();
	}

	public Collection<UpdatedMethod> getUpdatedMethodsFromRepository(
			String file_path, String old_workcopy, String new_workcopy) {

		return getUpdatedMethodsFromRepository(file_path,
				miner.getCommittedRevisionNumber(old_workcopy),
				miner.getCommittedRevisionNumber(new_workcopy));
	}

	public Collection<UpdatedMethod> getUpdatedMethodsFromRepository(
			String file_path, long old_revision, long new_revision) {

		miner.initialize(Arrays.asList(new String[] { file_path }),
				Arrays.asList(new String[] { String.valueOf(old_revision) }),
				Arrays.asList(new String[] { String.valueOf(new_revision) }));

		return miner.mine().get(file_path);
	}

}