package br.ufrn.ppgsc.scenario.analyzer.miner.svn;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IRepositoryMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;

public class SVNUpdatedMethodsMiner implements IRepositoryMiner {
	
	private static Map<String, SVNUpdatedLinesHandler> handlers;
    
	private String url;
    private String user;
    private String password;
	
    private SVNRepository repository;
    private SVNClientManager client;
    
    public void connect(String url, String user, String password) {
    	this.url = url;
		this.user = user;
		this.password = password;
		
		DAVRepositoryFactory.setup();

        try {
			this.repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		} catch (SVNException e) {
			e.printStackTrace();
		}
        
        this.client = SVNClientManager.newInstance();
        
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, password);
        repository.setAuthenticationManager(authManager);
		client.setAuthenticationManager(authManager);
    }
    
    public void close() {
    	repository.closeSession();
    }
    
	public void initialize() {
		if (handlers == null)
			handlers = new HashMap<String, SVNUpdatedLinesHandler>();
	}
	
	public Object mine(String path, String startRevision, String endRevision) {
		/* Acha quem foi a última pessoa que alterou cada linha.
		 * Para isso doAnnotate olha um conjunto de revisões anteriores e diz quem
		 * foi a última pessoa que alterou a linha antes de chegar na revisão final.
		 * Se a revisão que alterou aquela linha não está no intervalo de revisões considerado,
		 * ela é considerada como não alterada, retornando -1 para o número da revisão
		 * e null para os outros elementos.
		 * 
		 * O número da linha retornada é referente ao arquivo da versão final (arquivo de referência)
		 * 
		 * Ainda não entendi o que é a pegRevision, não vi diferença mudando o valor passado,
		 * então estou passando null e funciona!
		 */
		SVNUpdatedLinesHandler handler = new SVNUpdatedLinesHandler(repository, path);
		
		try {
			client.getLogClient().doAnnotate(
					SVNURL.parseURIEncoded(url + path),
					null,
					SVNRevision.create(Long.parseLong(startRevision)),
					SVNRevision.create(Long.parseLong(endRevision)),
					handler);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
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
		return handlers.put(path, (SVNUpdatedLinesHandler) handler);
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
	
	// Path indica um caminho local
	public String getCommittedRevisionNumber(String path) {
		long revision = -1;
		
		try {
			revision = SVNClientManager.newInstance()
					.getStatusClient()
					.doStatus(new File(path), false)
					.getCommittedRevision()
					.getNumber();
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		return String.valueOf(revision);
	}
	
	/* 
	 * TODO: Atualmente não é usado
	 * Path indica um caminho local
	 */
	public String getRepositoryRelativePath(String path) throws SVNException {
		return SVNClientManager.newInstance()
				.getStatusClient()
				.doStatus(new File(path), false)
				.getRepositoryRelativePath();
	}
	
	/* 
	 * TODO: Atualmente não é usado
	 * Path indica um caminho local
	 */
	public String getDecodedRepositoryRootURL(String path) throws SVNException {
		return SVNClientManager.newInstance()
				.getStatusClient()
				.doStatus(new File(path), false)
				.getRepositoryRootURL().toDecodedString();
	}

}