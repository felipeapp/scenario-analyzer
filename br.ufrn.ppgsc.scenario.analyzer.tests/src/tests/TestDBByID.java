package tests;

import java.io.IOException;

import javax.swing.JOptionPane;

import br.ufrn.ppgsc.scenario.analyzer.runtime.db.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.runtime.db.GenericDAOHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.SystemExecution;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeCallGraphPrintUtil;

public class TestDBByID {

	public static void main(String[] args) throws IOException {

		long id = Long.parseLong(JOptionPane.showInputDialog("Digite o ID: "));

		GenericDAO<SystemExecution> dao = new GenericDAOHibernateImpl<SystemExecution>();

		SystemExecution e = dao.read(SystemExecution.class, id);

		System.out.println(e.getId() + " - " + e.getDate());

		for (RuntimeScenario rs : e.getScenarios()) {
			StringBuilder sb = new StringBuilder();
			RuntimeCallGraphPrintUtil.printScenarioTree(rs, sb);
			System.out.println(sb);
		}

	}

}
