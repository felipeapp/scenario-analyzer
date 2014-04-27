package br.ufrn.ppgsc.scenario.analyzer.miner.svn;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.parser.MethodLimitBuilder;

public class SVNUpdatedMethodsMiner implements IRepositoryMiner {

	private final Logger logger = Logger.getLogger(SVNUpdatedMethodsMiner.class);
	
	private Map<String, Collection<UpdatedMethod>> changedMethods;
	private Map<String, SVNUpdatedLinesHandler> handlers;
	
	private String url;
    private String user;
    private String password;
    
    private List<String> targetPaths;
	private List<Long> startRevisions;
	private List<Long> endRevisions;
    
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
	
	public void initialize(List<String> targetPaths, List<Long> startRevisions, List<Long> endRevisions) {
		this.targetPaths = targetPaths;
		this.startRevisions = startRevisions;
		this.endRevisions = endRevisions;
		
		this.handlers = new HashMap<String, SVNUpdatedLinesHandler>();
		this.changedMethods = new HashMap<String, Collection<UpdatedMethod>>();
	}
	
	public Map<String, Collection<UpdatedMethod>> mine() {
		logger.info("starting mining...");
		
		try {
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
			int i = 0;
			for (String path : targetPaths) {
				if (!startRevisions.get(i).equals(endRevisions.get(i))) {
					logger.info("Running doAnnotate [" + startRevisions.get(i) + ", " + endRevisions.get(i) + "]");
					logger.info("Path (" + (i + 1) + "/" + targetPaths.size() + "):" + path);
					
					SVNUpdatedLinesHandler handler = handlers.get(path);
					
					if (handler == null) {
						handler = new SVNUpdatedLinesHandler(repository, path);
						
						client.getLogClient().doAnnotate(
								SVNURL.parseURIEncoded(url + path),
								null,
								SVNRevision.create(startRevisions.get(i)),
								SVNRevision.create(endRevisions.get(i)),
								handler);
						
						handlers.put(path, handler);
					}
					else {
						logger.info("Path previously analyzedPath: " + path);
					}
				}
				
				++i;
			}
		} catch (SVNException ex) {
			ex.printStackTrace();
		}
		
		for (String path : handlers.keySet()) {
			SVNUpdatedLinesHandler handler = handlers.get(path);
			
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

	// Path indica um caminho local
	public long getCommittedRevisionNumber(String path) {
		try {
			return SVNClientManager.newInstance()
					.getStatusClient()
					.doStatus(new File(path), false)
					.getCommittedRevision()
					.getNumber();
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		return -1;
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