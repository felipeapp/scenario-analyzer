package br.ufrn.ppgsc.scenario.analyzer.miner.git;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IRepositoryMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class GitUpdatedMethodsMiner implements IRepositoryMiner {
	
	private Properties gitcommits;
	
	private static Map<String, GitUpdatedLinesHandler> handlers;
	
	private String url;
    private String user;
    private String password;
    
    public void connect(String url, String user, String password) {
    	this.url = url;
		this.user = user;
		this.password = password;
    }
    
    public void close() {
    	
    }
    
    public void initialize() {
		if (handlers == null)
			handlers = new HashMap<String, GitUpdatedLinesHandler>();
		
		this.gitcommits = new Properties();
		try {
			this.gitcommits.load(new FileInputStream(
					"resources/" + SystemMetadataUtil.getInstance().getStringProperty("commit_file")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object mine(String path, String startRevision, String endRevision) {
		GitUpdatedLinesHandler handler  = new GitUpdatedLinesHandler(
				startRevision,
				endRevision,
				url + path.substring(0, path.lastIndexOf('/')),
				path.substring(path.lastIndexOf('/') + 1));
		
		handler.calculateChangedLines();
		
		return handler;
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
	
	public Object getLinesHandler(String path) {
		return handlers.get(path);
	}

	public Object putLinesHandler(String path, Object handler) {
		return handlers.put(path, (GitUpdatedLinesHandler) handler);
	}
	
	public Set<String> getAllLinesHandlerKeys() {
		return handlers.keySet();
	}

	public List<UpdatedLine> getChangedLines(String path) {
		return handlers.get(path).getChangedLines();
	}

	public String getSourceCode(String path) {
		return handlers.get(path).getSourceCode();
	}

	public String getCommittedRevisionNumber(String path) {
		for (Object obj : gitcommits.keySet()) {
			String key = (String) obj;
			
			if (path.contains(key))
				return gitcommits.getProperty(key);
		}
		
		return null;
	}

}