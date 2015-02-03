package statistical;

import java.io.IOException;
import java.util.Map;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDBHibernateImpl;

public class TestAVG {

	public static void main(String[] args) throws IOException {

		GenericDB dao = new GenericDBHibernateImpl("hibernate.cfg.xml");

		long time = System.currentTimeMillis();

		Map<String, Double> avgs = dao.getExecutionTimeAverageOfMembers();

		System.out.println("Time: " + (System.currentTimeMillis() - time));
		System.out.println("Size: " + avgs.size());

	}

}
