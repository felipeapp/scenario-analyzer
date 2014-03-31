package tests;

import java.io.IOException;

import javax.swing.JOptionPane;

import br.ufrn.ppgsc.scenario.analyzer.runtime.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.GenericDAOHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.PrintUtil;

public class TestDBByID {

	public static void main(String[] args) throws IOException {

		long id = Long.parseLong(JOptionPane.showInputDialog("Digite o ID: "));

		GenericDAO<Execution> dao = new GenericDAOHibernateImpl<Execution>();

		Execution e = dao.read(Execution.class, id);

		System.out.println(e.getId() + " - " + e.getDate());

		for (RuntimeScenario rs : e.getScenarios()) {
			StringBuilder sb = new StringBuilder();
			PrintUtil.printScenarioTree(rs, sb);
			System.out.println(sb);
		}

	}

}