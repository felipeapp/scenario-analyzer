package tests;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.d.data.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.d.data.GenericDAOHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.d.util.PrintUtil;

public class TestDBByID {

	public static void main(String[] args) throws IOException {

		GenericDAO<Execution> dao = new GenericDAOHibernateImpl<Execution>();

		Execution e = dao.read(Execution.class, 2L);

		System.out.println(e.getId() + " - " + e.getDate());

		for (RuntimeScenario rs : e.getScenarios()) {
			StringBuilder sb = new StringBuilder();
			PrintUtil.printScenarioTree(rs, sb);
			System.out.println(sb);
		}

	}

}
