package br.ufrn.ppgsc.scenario.analyzer.miner.backhoe;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import br.ufrn.backhoe.repminer.connector.SubversionConnector;
import br.ufrn.backhoe.repminer.enums.ConnectorType;
import br.ufrn.backhoe.repminer.miner.Miner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.MethodLimit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.UpdatedMethodsMinerUtil;

public class UpdatedMethodsMinerNoDB extends Miner {

	private final Logger logger = Logger.getLogger(UpdatedMethodsMinerNoDB.class);
	
	private Map<String, Collection<UpdatedMethod>> changedMethods;
	private Map<String, UpdatedLinesHandlerIProject> handlers;
	
	public void performSetup() {
		logger.info("performSetup...");
		
		List<String> targetPaths = (List<String>) parameters.get("target_paths");
		List<Long> startRevisions = (List<Long>) parameters.get("start_revisions");
		List<Long> endRevisions = (List<Long>) parameters.get("end_revisions");
		
		SubversionConnector svnConnector = (SubversionConnector) connectors.get(ConnectorType.SVN);
		SVNRepository repository = svnConnector.getEncapsulation();
		
		SVNClientManager client = SVNClientManager.newInstance();
		client.setAuthenticationManager(repository.getAuthenticationManager());
		
		handlers = new HashMap<String, UpdatedLinesHandlerIProject>();
		
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
				logger.info("Running doAnnotate [" + startRevisions.get(i) + ", " + endRevisions.get(i) + "]");
				logger.info("Path (" + (i + 1) + "/" + targetPaths.size() + "):" + path);
				
				UpdatedLinesHandlerIProject handler = handlers.get(path);
				
				if (handler == null) {
					handler = new UpdatedLinesHandlerIProject(repository, path);
					
					client.getLogClient().doAnnotate(
							SVNURL.parseURIEncoded(svnConnector.getUrl() + path),
							null,
							SVNRevision.create(startRevisions.get(i)),
							SVNRevision.create(endRevisions.get(i)),
							handler);
					
					handlers.put(path, handler);
				}
				
				++i;
			}
		} catch (SVNException ex) {
			ex.printStackTrace();
		}
	}

	public void performMining() {
		logger.info("performMining...");
		
		changedMethods = new HashMap<String, Collection<UpdatedMethod>>();
		
		for (String path : handlers.keySet()) {
			UpdatedLinesHandlerIProject handler = handlers.get(path);
			
			// Pega as linhas modificadas
			List<UpdatedLine> lines = handler.getChangedLines();
			
			// Pega o limite dos métodos (linha inicial e final)
			List<MethodLimit> limits = new MethodLimitBuilder(handler.getSourceCode()).getMethodLimits();
			
			// Pega os métodos mudados verificando as linhas mudadas e os limites dos métodos
			changedMethods.put(path, UpdatedMethodsMinerUtil.filterChangedMethods(limits, lines));
			
			// Usei este artifício para desenvolver o resultado da mineração
			parameters.put("result", changedMethods);
		}
	}
	
	public void validateParameter() throws Exception {
		// Não sei o que esse método do backhoe deveria fazer!
		logger.info("validateParameter...");
	}

}