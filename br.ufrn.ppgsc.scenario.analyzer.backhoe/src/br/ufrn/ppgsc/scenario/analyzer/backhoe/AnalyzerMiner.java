package br.ufrn.ppgsc.scenario.analyzer.backhoe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.svn.core.SVNException;

import br.ufrn.backhoe.repminer.connector.Connector;
import br.ufrn.backhoe.repminer.connector.SubversionConnector;
import br.ufrn.backhoe.repminer.enums.ConnectorType;
import br.ufrn.backhoe.repminer.factory.connector.SubversionConnectorFactory;
import br.ufrn.backhoe.repminer.miner.Miner;

public abstract class AnalyzerMiner {

	public static Collection<UpdatedMethod> getUpdatedMethodsFromRepository(String host,
			String user, String password, String file_path, String old_workcopy, String new_workcopy) {

		long old_revision = 0, new_revision = 0;
		
		SubversionConnectorFactory svnFactory = new SubversionConnectorFactory();
		SubversionConnector svnConnector = svnFactory.createConnector(user, password, host);

		Map<ConnectorType, Connector> connectors = new HashMap<ConnectorType, Connector>();
		connectors.put(ConnectorType.SVN, svnConnector);
		
		try {
			old_revision = ChangedAssetsMinerUtil.getCommittedRevisionNumber(old_workcopy);
			new_revision = ChangedAssetsMinerUtil.getCommittedRevisionNumber(new_workcopy);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		System.out.println("Revisions: " + old_revision + " - " + new_revision);

		Map parameters = new HashMap<String, String>();
		parameters.put("target_path", file_path);
		parameters.put("start_revision", old_revision);
		parameters.put("end_revision", new_revision);

		Miner miner = new RevisionOfChangedAssetsMinerNoDB();
		miner.setConnectors(connectors);
		miner.setParameters(parameters);

		try {
			miner.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (Collection<UpdatedMethod>) parameters.get("result");
		
	}
	
	public static Collection<UpdatedMethod> getUpdatedMethodsFromRepository(String host,
			String user, String password, String file_path, long old_revision, long new_revision) {
		
		SubversionConnectorFactory svnFactory = new SubversionConnectorFactory();
		SubversionConnector svnConnector = svnFactory.createConnector(user, password, host);

		Map<ConnectorType, Connector> connectors = new HashMap<ConnectorType, Connector>();
		connectors.put(ConnectorType.SVN, svnConnector);

		Map parameters = new HashMap<String, String>();
		parameters.put("target_path", file_path);
		parameters.put("start_revision", old_revision);
		parameters.put("end_revision", new_revision);

		Miner miner = new RevisionOfChangedAssetsMinerNoDB();
		miner.setConnectors(connectors);
		miner.setParameters(parameters);

		try {
			miner.executeMining();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (Collection<UpdatedMethod>) parameters.get("result");
		
	}

}
