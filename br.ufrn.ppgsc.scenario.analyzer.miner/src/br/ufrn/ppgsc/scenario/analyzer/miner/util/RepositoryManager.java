package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IRepositoryMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Commit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.parser.MethodLimitParser;

public class RepositoryManager {

	private final Logger logger = Logger.getLogger(RepositoryManager.class);
	
	private IRepositoryMiner miner;
	private String url;
	private String user;
	private String password;
	
	/* 
	 * Every commit that modified classes (any member) that were executed during dynamic analysis.
	 * Here we have even the commits not responsible for performance deviation. 
	 */
	private static Set<Commit> set_of_all_commits = new HashSet<Commit>();
	
	/* 
	 * Every commit that modified methods that were executed during dynamic analysis.
	 * Here we have even the commits not responsible for performance deviation. 
	 */
	private static Set<String> set_of_commits_from_changed_methods = new HashSet<String>();

	public RepositoryManager(String url, String user, String password) {
		miner = SystemMetadataUtil.getInstance().newObjectFromProperties(IRepositoryMiner.class);
		
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	public Set<Commit> getAllCommits() {
		return Collections.unmodifiableSet(set_of_all_commits);
	}
	
	public Set<String> getCommitsFromChangedMethods() {
		return Collections.unmodifiableSet(set_of_commits_from_changed_methods);
	}

	public Map<String, Collection<UpdatedMethod>> getUpdatedMethodsFromRepository(List<String> target_paths,
			List<String> old_workcopies, List<String> new_workcopies) {
		logger.info("Starting release mining, open connection now...");
		miner.connect(url, user, password);
		
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
				logger.info("[R1 != R2] Path (" + (i + 1) + "/" + target_paths.size() + "):" + target_paths.get(i));

				Object handler = miner.getLinesHandler(target_paths.get(i));
				
				if (handler == null) {
					logger.info("\tRunning doAnnotate [" + old_revisions.get(i) + ", " + new_revisions.get(i) + "]");
					handler = miner.mine(target_paths.get(i), old_revisions.get(i), new_revisions.get(i));
					miner.putLinesHandler(target_paths.get(i), handler);
				}
				else {
					logger.info("\tPath previously analyzed");
				}
			}
			else {
				logger.info("[R1 = R2] Path (" + (i + 1) + "/" + target_paths.size() + "):" + target_paths.get(i));
			}
		}
		
		logger.info("Finishing mining, close connection now...");
		miner.close();
		
		for (String key : miner.getAllLinesHandlerKeys()) {
			// Pega as linhas modificadas
			List<UpdatedLine> lines = miner.getChangedLines(key);
			
			// Parse da classe buscando os métodos (linha inicial, final, nome)
			MethodLimitParser builder = new MethodLimitParser(miner.getSourceCode(key));
			
			// Pega os métodos mudados verificando as linhas mudadas e os limites dos métodos
			changedMethods.put(key, builder.filterChangedMethods(lines));
			
			/*
			 * Pega os commits que modificaram as linhas e salva na coleção. 
			 * Tem que ser feito por último, pois depende do filter acima.
			 */
			for (UpdatedLine upline : lines)
				set_of_all_commits.add(upline.getCommit());
		}
		
		// Pega os commits de métodos modificados
		set_of_commits_from_changed_methods.addAll(AnalyzerCollectionUtil.getCommitCodes(changedMethods));
		
		return changedMethods;
	}

	public Collection<UpdatedMethod> getUpdatedMethodsFromRepository(
			String file_path, String old_workcopy, String new_workcopy) {
		return getUpdatedMethodsFromRepository(file_path,
				miner.getCommittedRevisionNumber(old_workcopy),
				miner.getCommittedRevisionNumber(new_workcopy));
	}

}
