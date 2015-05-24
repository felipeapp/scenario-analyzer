package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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
	
	public long getLongProperty(String name) {
		return Long.parseLong(properties.getProperty(name));
	}

	public boolean getBooleanProperty(String name) {
		return Boolean.parseBoolean(properties.getProperty(name));
	}

	public <T> T newObjectFromProperties(Class<T> clazz) {
		T object = null;
		String name = null;

		if (clazz.equals(IPathTransformer.class))
			name = "path_transformer";
		else if (clazz.equals(IQueryIssue.class))
			name = "query_issue";
		else if (clazz.equals(IRepositoryMiner.class))
			name = "repository_miner";

		try {
			object = clazz.cast(Class.forName(properties.getProperty(name)).newInstance());
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
