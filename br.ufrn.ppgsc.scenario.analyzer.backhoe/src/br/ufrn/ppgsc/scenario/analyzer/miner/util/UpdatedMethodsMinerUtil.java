package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;

import br.ufrn.backhoe.repminer.connector.Connector;
import br.ufrn.backhoe.repminer.connector.SubversionConnector;
import br.ufrn.backhoe.repminer.enums.ConnectorType;
import br.ufrn.backhoe.repminer.factory.connector.SubversionConnectorFactory;
import br.ufrn.backhoe.repminer.miner.Miner;
import br.ufrn.ppgsc.scenario.analyzer.miner.backhoe.UpdatedMethodsMinerNoDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.MethodLimit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;

public abstract class UpdatedMethodsMinerUtil {
	
	private static final Logger logger = Logger.getLogger(UpdatedMethodsMinerUtil.class);
	
	public static long getTaskNumberFromLogMessage(String logMessage) {
		Scanner in = new Scanner(logMessage);
		long task_number = 0;
		
		while (!in.hasNextLong()) {
			try {
				in.next();
			} catch (NoSuchElementException e) {
				logger.warn("getTaskNumberFromLogMessage: NoSuchElementException");
				break;
			}
		}
		
		if (in.hasNextLong())
			task_number = in.nextLong();
		
		in.close();
		
		return task_number;
	}
	
	public static Collection<UpdatedMethod> filterChangedMethods(List<MethodLimit> limits, List<UpdatedLine> lines) {
		Map<String, UpdatedMethod> result = new HashMap<String, UpdatedMethod>();
		
		for (UpdatedLine l : lines) {
			for (MethodLimit m : limits) {
				if (l.getLineNumber() >= m.getStartLine() && l.getLineNumber() <= m.getEndLine()) {
					UpdatedMethod mu = result.get(m.getSignature());
					
					if (mu == null) {
						mu = new UpdatedMethod(m);
						result.put(m.getSignature(), mu);
					}
					
					mu.addUpdatedLine(l);
				}
			}
		}
		
		return Collections.unmodifiableCollection(result.values());
	}
	
	// path indica um caminho local
	public static long getCommittedRevisionNumber(String path) throws SVNException {
		return SVNClientManager.newInstance()
				.getStatusClient()
				.doStatus(new File(path), false)
				.getCommittedRevision()
				.getNumber();
	}
	
	// path indica um caminho local
	public static String getRepositoryRelativePath(String path) throws SVNException {
		return SVNClientManager.newInstance()
				.getStatusClient()
				.doStatus(new File(path), false)
				.getRepositoryRelativePath();
	}
	
	// path indica um caminho local
	public static String getDecodedRepositoryRootURL(String path) throws SVNException {
		return SVNClientManager.newInstance()
				.getStatusClient()
				.doStatus(new File(path), false)
				.getRepositoryRootURL().toDecodedString();
	}
	
	public static Map<String, Collection<UpdatedMethod>> getUpdatedMethodsFromRepository(String host, String user,
			String password, List<String> paths, List<String> old_workcopies, List<String> new_workcopies) {

		List<Long> old_revisions = new ArrayList<Long>();
		List<Long> new_revisions = new ArrayList<Long>();
		
		SubversionConnectorFactory svnFactory = new SubversionConnectorFactory();
		SubversionConnector svnConnector = svnFactory.createConnector(user, password, host);

		Map<ConnectorType, Connector> connectors = new HashMap<ConnectorType, Connector>();
		connectors.put(ConnectorType.SVN, svnConnector);
		
		try {
			for (int i = 0; i < paths.size(); i++) {
				old_revisions.add(UpdatedMethodsMinerUtil.getCommittedRevisionNumber(old_workcopies.get(i)));
				new_revisions.add(UpdatedMethodsMinerUtil.getCommittedRevisionNumber(new_workcopies.get(i)));
			}
		} catch (SVNException e) {
			e.printStackTrace();
		}

		Map parameters = new HashMap<String, String>();
		parameters.put("target_paths", paths);
		parameters.put("start_revisions", old_revisions);
		parameters.put("end_revisions", new_revisions);

		Miner miner = new UpdatedMethodsMinerNoDB();
		miner.setConnectors(connectors);
		miner.setParameters(parameters);

		try {
			miner.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (Map<String, Collection<UpdatedMethod>>) parameters.get("result");
	}
	
	public static Collection<UpdatedMethod> getUpdatedMethodsFromRepository(String host,
			String user, String password, String file_path, String old_workcopy, String new_workcopy) {

		long old_revision = 0, new_revision = 0;
		
		try {
			old_revision = UpdatedMethodsMinerUtil.getCommittedRevisionNumber(old_workcopy);
			new_revision = UpdatedMethodsMinerUtil.getCommittedRevisionNumber(new_workcopy);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		return getUpdatedMethodsFromRepository(host, user, password, file_path, old_revision, new_revision);
	}
	
	public static Collection<UpdatedMethod> getUpdatedMethodsFromRepository(String host,
			String user, String password, String file_path, long old_revision, long new_revision) {
		
		SubversionConnectorFactory svnFactory = new SubversionConnectorFactory();
		SubversionConnector svnConnector = svnFactory.createConnector(user, password, host);

		Map<ConnectorType, Connector> connectors = new HashMap<ConnectorType, Connector>();
		connectors.put(ConnectorType.SVN, svnConnector);

		Map parameters = new HashMap<String, String>();
		parameters.put("target_paths", Arrays.asList(new String[]{file_path}));
		parameters.put("start_revisions", Arrays.asList(new Long(old_revision)));
		parameters.put("end_revisions", Arrays.asList(new Long(new_revision)));

		Miner miner = new UpdatedMethodsMinerNoDB();
		miner.setConnectors(connectors);
		miner.setParameters(parameters);

		try {
			miner.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ((Map<String, Collection<UpdatedMethod>>) parameters.get("result")).get(file_path);
		
	}

}
