package br.ufrn.ppgsc.scenario.analyzer.miner.git;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IRepositoryMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.parser.MethodLimitBuilder;

public class GitUpdatedMethodsMiner implements IRepositoryMiner {

	private final Logger logger = Logger.getLogger(GitUpdatedMethodsMiner.class);
	
	private Map<String, Collection<UpdatedMethod>> changedMethods;
	private Map<String, GitUpdatedLinesHandler> handlers;
	
	private String url;
    private String user;
    private String password;
    
    private List<String> targetPaths;
	private List<String> startRevisions;
	private List<String> endRevisions;
    
	public void connect(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	public void initialize(List<String> targetPaths, List<String> startRevisions, List<String> endRevisions) {
		this.targetPaths = targetPaths;
		this.startRevisions = startRevisions;
		this.endRevisions = endRevisions;
		
		this.handlers = new HashMap<String, GitUpdatedLinesHandler>();
		this.changedMethods = new HashMap<String, Collection<UpdatedMethod>>();
	}
	
	public Map<String, Collection<UpdatedMethod>> mine() {
		logger.info("starting mining...");
		
		int i = 0;
		for (String path : targetPaths) {
			if (!startRevisions.get(i).equals(endRevisions.get(i))) {
				logger.info("Running doAnnotate [" + startRevisions.get(i) + ", " + endRevisions.get(i) + "]");
				logger.info("Path (" + (i + 1) + "/" + targetPaths.size() + "):" + path);
				
				GitUpdatedLinesHandler handler = handlers.get(path);
				
				if (handler == null) {
					handler = new GitUpdatedLinesHandler(
							startRevisions.get(i),
							endRevisions.get(i),
							url + targetPaths.get(i).substring(0, targetPaths.get(i).lastIndexOf('/')),
							targetPaths.get(i).substring(targetPaths.get(i).lastIndexOf('/') + 1));
					
					handler.calculateChangedLines();
					
					handlers.put(path, handler);
				}
				else {
					logger.info("Path previously analyzedPath: " + path);
				}
			}
			
			++i;
		}
		
		for (String path : handlers.keySet()) {
			GitUpdatedLinesHandler handler = handlers.get(path);
			
			// Pega as linhas modificadas
			List<UpdatedLine> lines = handler.getChangedLines();
			
			// Parse da classe buscando os métodos (linha inicial, final, nome)
			MethodLimitBuilder builder = new MethodLimitBuilder(handler.getSourceCode());
			
			// Pega os métodos mudados verificando as linhas mudadas e os limites dos métodos
			changedMethods.put(path, builder.filterChangedMethods(lines));
		}
		
		return changedMethods;
	}
	
	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getCommittedRevisionNumber(String path) {
		if (path.contains("netty-4.0.0.Final"))
			return "8acba6063eabd9fafec57c5abcfa3dd79285bbdf";
		else if (path.contains("netty-4.0.19.Final"))
			return "f696c98ff8fe23dce4f2046cdb7d8483fc24cc8a";
		
		return null;
	}

}