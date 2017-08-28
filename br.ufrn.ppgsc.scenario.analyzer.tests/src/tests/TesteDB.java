package tests;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.db.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.db.GenericDAOHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.SystemExecution;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraphPrintUtil;

public class TesteDB implements RowMapper<String> {

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

	public String mapRow(ResultSet arg0, int arg1) throws SQLException {
		return null;
	}

}
