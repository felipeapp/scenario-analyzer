package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerReportUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.RepositoryManager;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class AnalyzerMinerFull {

	private static final String repository_url = SystemMetadataUtil.getInstance().getStringProperty("repository_url");
	private static final String repository_user = SystemMetadataUtil.getInstance().getStringProperty("repository_user");
	private static final String repository_password = SystemMetadataUtil.getInstance().getStringProperty("repository_password");
	private static final String strdate = SystemMetadataUtil.getInstance().getStringProperty("date");
	private static final String system_id = SystemMetadataUtil.getInstance().getStringProperty("system_id");
	private static final String repository_prefix = SystemMetadataUtil.getInstance().getStringProperty("repository_prefix");
	private static final String workcopy_prefix_r1 = SystemMetadataUtil.getInstance().getStringProperty("workcopy_prefix_r1");
	private static final String workcopy_prefix_r2 = SystemMetadataUtil.getInstance().getStringProperty("workcopy_prefix_r2");

	private static final IPathTransformer transformer = SystemMetadataUtil.getInstance().newObjectFromProperties(IPathTransformer.class);

	// TODO: How can I move this to report util?
	private static String getDAFilePath(String partial_name) {
		return "reports/degradation_analysis/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}

	// TODO: How can I move this to report util?
	private static String getRMFilePath(String partial_name) {
		return "reports/repository_mining/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}

	public static void main(String[] args) throws IOException {
		// Configuração básica para o log4j
		BasicConfigurator.configure();

		@SuppressWarnings("unchecked")
		List<Logger> loggers = Collections.list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for (Logger logger : loggers)
			logger.setLevel(Level.OFF);
		
		Logger.getLogger("br.ufrn.ppgsc.scenario.analyzer.miner").setLevel(Level.ALL);

		// It connects to the repository at the time it is created
		RepositoryManager repository = new RepositoryManager(repository_url, repository_user, repository_password);

		List<String> repository_paths = new ArrayList<String>();
		List<String> old_workcopy_paths = new ArrayList<String>();
		List<String> new_workcopy_paths = new ArrayList<String>();

		Collection<String> full_signatures = new ArrayList<String>();

		System.out.println(AnalyzerReportUtil.loadCollection(full_signatures, getDAFilePath("kept_methods")));
		System.out.println(AnalyzerReportUtil.loadCollection(full_signatures, getDAFilePath("added_methods")));
		System.out.println(AnalyzerReportUtil.loadCollection(full_signatures, getDAFilePath("removed_methods")));

		for (String signature : full_signatures) {
			String paths[] = transformer.convert(signature, repository_prefix, workcopy_prefix_r1, workcopy_prefix_r2);

			if (paths == null) {
				System.out.println("Ignoring " + signature + " because paths is null (Is it right?).");
				continue;
			}

			boolean ignore_file = false;
			boolean file_exists_r1 = new File(paths[1]).exists();
			boolean file_exists_r2 = new File(paths[2]).exists();

			if (!file_exists_r1 && !file_exists_r2) {
				throw new FileNotFoundException("\n" + paths[1] + "\n" + paths[2]);
				// System.err.println("\n" + paths[1] + "\n" + paths[2]);
			}

			if (!file_exists_r1) {
				System.out.println("Not found in R1 (Is it right?): " + paths[1]);
				ignore_file = true;
			}

			if (!file_exists_r2) {
				System.out.println("Not found in R2 (Is it right?): " + paths[2]);
				ignore_file = true;
			}

			if (!ignore_file) {
				repository_paths.add(paths[0]);
				old_workcopy_paths.add(paths[1]);
				new_workcopy_paths.add(paths[2]);
			}
		}

		System.out.println("Getting updated methods from repository of " + repository_paths.size() + " paths...");

		if (!full_signatures.isEmpty())
			repository.getUpdatedMethodsFromRepository(repository_paths, old_workcopy_paths, new_workcopy_paths);

		// Save the list of commits found during the mining phase
		AnalyzerReportUtil.saveCollection("# List of all commits from classes found during the repository mining phase (blamed or not)",
			getRMFilePath("all_commits_classes"), AnalyzerCollectionUtil.getCommitProperties(repository.getAllCommits(), ","));
		
		AnalyzerReportUtil.saveCollection("# List of all commits from changed methods found during the repository mining phase (blamed or not)",
			getRMFilePath("all_commits_changed_methods"), AnalyzerCollectionUtil.getCommitProperties(repository.getCommitsFromChangedMethods(), ","));
		
	}

}
