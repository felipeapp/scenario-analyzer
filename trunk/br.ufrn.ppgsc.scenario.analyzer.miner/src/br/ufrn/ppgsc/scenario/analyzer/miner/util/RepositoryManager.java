package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IRepositoryMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.parser.MethodLimitBuilder;

public class RepositoryManager {

	private final Logger logger = Logger.getLogger(RepositoryManager.class);
	
	private IRepositoryMiner miner;

	public RepositoryManager(String url, String user, String password) {
		miner = SystemMetadataUtil.getInstance().newObjectFromProperties(IRepositoryMiner.class);
		miner.connect(url, user, password);
	}

	public Map<String, Collection<UpdatedMethod>> getUpdatedMethodsFromRepository(List<String> target_paths,
			List<String> old_workcopies, List<String> new_workcopies) {
		logger.info("starting mining...");
		
		Map<String, Collection<UpdatedMethod>> changedMethods = new HashMap<String, Collection<UpdatedMethod>>();
		
		List<String> old_revisions = new ArrayList<String>();
		List<String> new_revisions = new ArrayList<String>();

		miner.initialize();
		
		for (int i = 0; i < target_paths.size(); i++) {
			old_revisions.add(miner.getCommittedRevisionNumber(old_workcopies.get(i)));
			new_revisions.add(miner.getCommittedRevisionNumber(new_workcopies.get(i)));
		}
		
		for (int i = 0; i < target_paths.size(); ++i) {
			if (!old_revisions.get(i).equals(new_revisions.get(i))) {
				logger.info("Running doAnnotate [" + old_revisions.get(i) + ", " + new_revisions.get(i) + "]");
				logger.info("Path (" + (i + 1) + "/" + target_paths.size() + "):" + target_paths.get(i));
				
				Object handler = miner.getLinesHandler(target_paths.get(i));
				
				if (handler == null) {
					handler = miner.mine(target_paths.get(i), old_revisions.get(i), new_revisions.get(i));
					miner.putLinesHandler(target_paths.get(i), handler);
				}
				else {
					logger.info("Path previously analyzedPath: " + target_paths.get(i));
				}
			}
		}
		
		for (String key : miner.getAllLinesHandlerKeys()) {
			// Pega as linhas modificadas
			List<UpdatedLine> lines = miner.getChangedLines(key);
			
			// Parse da classe buscando os métodos (linha inicial, final, nome)
			MethodLimitBuilder builder = new MethodLimitBuilder(miner.getSourceCode(key));
			
			// Pega os métodos mudados verificando as linhas mudadas e os limites dos métodos
			changedMethods.put(key, builder.filterChangedMethods(lines));
		}
		
		return changedMethods;
	}

	public Collection<UpdatedMethod> getUpdatedMethodsFromRepository(
			String file_path, String old_workcopy, String new_workcopy) {
		return getUpdatedMethodsFromRepository(file_path,
				miner.getCommittedRevisionNumber(old_workcopy),
				miner.getCommittedRevisionNumber(new_workcopy));
	}

}
