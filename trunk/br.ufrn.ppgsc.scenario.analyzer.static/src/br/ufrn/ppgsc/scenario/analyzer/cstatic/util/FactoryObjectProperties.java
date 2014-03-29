package br.ufrn.ppgsc.scenario.analyzer.cstatic.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl.IDataStructure;

public abstract class FactoryObjectProperties {

	private static Properties properties = new Properties();

	public static void load(String path) {
		try {
			properties.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void clear() {
		properties.clear();
	}

	public static IDataStructure newDataStructureObject() {
		IDataStructure object = null;

		try {
			object = (IDataStructure) Class.forName(properties.getProperty("cg_builder")).newInstance();
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
