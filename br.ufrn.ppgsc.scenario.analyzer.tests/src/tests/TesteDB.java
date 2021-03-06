package tests;

import java.io.IOException;
import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.db.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.db.GenericDAOHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.SystemExecution;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraphPrintUtil;

public class TesteDB {

	public static void main(String[] args) throws IOException {

		GenericDAO<SystemExecution> dao = new GenericDAOHibernateImpl<SystemExecution>();

		List<SystemExecution> objs = dao.readAll(SystemExecution.class);
		
		System.out.println("-----------------------------------");
		
		for (SystemExecution e : objs) {
			System.out.println(e.getDate());
			
			for (RuntimeScenario rs : e.getScenarios()) {
				StringBuilder sb = new StringBuilder();
				RuntimeCallGraphPrintUtil.printScenarioTree(rs, sb);
				System.out.println(sb);
			}
		}

	}

}
