package br.ufrn.ppgsc.scenario.analyzer.cdynamic.db;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraphPrintUtil;

public class GenericDAOFileImpl<T extends Serializable> implements GenericDAO<T> {

	private static int count = 0;

	@Override
	public void clearSession() {

	}

	@Override
	public T save(T instance) {
		RuntimeScenario rs = (RuntimeScenario) instance;

		try {
			File file = new File("scenario_" + ++count + "_" + rs.getRoot().getMemberSignature() + ".txt");
			System.out.println("Save file path: " + file.getAbsolutePath());

			PrintStream ps = new PrintStream(file);
			RuntimeCallGraphPrintUtil.logScenarioTree(rs, ps);

			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return instance;
	}

	@Override
	public T read(Class<T> clazz, long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> readAll(Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

}
