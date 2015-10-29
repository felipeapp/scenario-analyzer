package mbeans;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.SystemExecution;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraph;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.util.RuntimeCallGraphPrintUtil;

public class MainMBean {

	public static void main(String[] args) {

		TestMBean bean = new TestMBean();

		for (int i = 0; i < 2; i++) {
			bean.setAtivo(true);
			bean.imprimir();
			bean.notificar();
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
