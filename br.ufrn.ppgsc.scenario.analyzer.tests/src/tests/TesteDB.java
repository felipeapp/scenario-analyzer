package tests;

import java.io.IOException;
import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.runtime.db.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.runtime.db.GenericDAOHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.Execution;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.PrintUtil;

public class TesteDB {

	public static void main(String[] args) throws IOException {

		GenericDAO<Execution> dao = new GenericDAOHibernateImpl<Execution>();

		List<Execution> objs = dao.readAll(Execution.class);
		
		System.out.println("-----------------------------------");
		
		for (Execution e : objs) {
			System.out.println(e.getDate());
			
			for (RuntimeScenario rs : e.getScenarios()) {
				StringBuilder sb = new StringBuilder();
				PrintUtil.printScenarioTree(rs, sb);
				System.out.println(sb);
			}
		}

	}

}
