package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IContentIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IRepositoryMiner;

public class SystemMetadataUtil {

	private static SystemMetadataUtil systemProperties = new SystemMetadataUtil();

	private Properties properties;

	private SystemMetadataUtil() {
		properties = new Properties();

		try {
			properties.load(new FileInputStream("resources/analyzer_miner.properties"));
			properties.load(new FileInputStream("resources/" + properties.getProperty("target_properties") + ".properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SystemMetadataUtil getInstance() {
		return systemProperties;
	}

	public String getStringProperty(String name) {
		return properties.getProperty(name);
	}

	public double getDoubleProperty(String name) {
		return Double.parseDouble(properties.getProperty(name));
	}

	public int getIntProperty(String name) {
		return Integer.parseInt(properties.getProperty(name));
	}
	
	public boolean getBooleanProperty(String name) {
		return Boolean.parseBoolean(properties.getProperty(name));
	}

	public IPathTransformer getPathTransformerObject() {
		IPathTransformer object = null;

		try {
			object = (IPathTransformer) Class.forName(properties.getProperty("path_transformer")).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return object;
	}

	public IQueryIssue getQueryIssueObject() {
		IQueryIssue object = null;

		try {
			object = (IQueryIssue) Class.forName(properties.getProperty("query_issue")).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return object;
	}

	public IContentIssue getContentIssueObject() {
		IContentIssue object = null;

		try {
			object = (IContentIssue) Class.forName(properties.getProperty("content_issue")).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return object;
	}
	
	public IRepositoryMiner getRepositoryMinerObject() {
		IRepositoryMiner object = null;

		try {
			object = (IRepositoryMiner) Class.forName(properties.getProperty("repository_miner")).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return object;
	}

}
