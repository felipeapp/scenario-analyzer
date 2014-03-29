package mbeans;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.runtime.model.SystemExecution;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeCallGraphPrintUtil;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeCallGraph;

public class MainMBean {

	public static void main(String[] args) {

		TestMBean bean = new TestMBean();

		for (int i = 0; i < 100; i++) {
			bean.setAtivo(true);
			bean.imprimir();
			bean.getIdade();
		}

		SystemExecution e = RuntimeCallGraph.getInstance().getCurrentExecution();

		System.out.println(e.getId() + " - " + e.getDate());

		for (RuntimeScenario rs : e.getScenarios()) {
			StringBuilder sb = new StringBuilder();

			try {
				RuntimeCallGraphPrintUtil.printScenarioTree(rs, sb);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			System.out.println(sb);
		}

	}

}
