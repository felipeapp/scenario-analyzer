package tests;

import java.io.IOException;
import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.d.data.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.d.data.GenericDAOHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.d.util.DataUtil;

public class TesteDB {

	public static void main(String[] args) throws IOException {

		GenericDAO<Execution> dao = new GenericDAOHibernateImpl<Execution>();

		List<Execution> objs = dao.readAll(Execution.class);
		
		System.out.println("-----------------------------------");
		
		for (Execution e : objs) {
			System.out.println(e.getDate());
			
			for (RuntimeScenario rs : e.getScenarios()) {
				StringBuilder sb = new StringBuilder();
				DataUtil.printScenarioTree(rs, sb);
				System.out.println(sb);
			}
		}

	}

}
