package tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.db.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.db.GenericDAOHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.SystemExecution;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraphPrintUtil;

public class TestDBByID {

	public static void main(String[] args) throws IOException {
		long id = Long.parseLong(JOptionPane.showInputDialog("Digite o ID: "));
		showScenario(id);
	}

	public static void showScenario(long id) throws IOException {

		GenericDAO<RuntimeScenario> dao = new GenericDAOHibernateImpl<RuntimeScenario>();

		RuntimeScenario rs = dao.read(RuntimeScenario.class, id);

		System.out.println(rs.getId() + " - " + rs.getDate());

		PrintStream ps = new PrintStream(new FileOutputStream("scenario_from_db.txt"), true);
		RuntimeCallGraphPrintUtil.logScenarioTree(rs, ps);
		ps.close();

	}

	public static void showExecution(int id) throws IOException {

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
