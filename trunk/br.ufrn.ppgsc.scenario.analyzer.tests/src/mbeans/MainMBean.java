package mbeans;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.runtime.model.Execution;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.PrintUtil;
import br.ufrn.ppgsc.scenario.analyzer.runtime.util.RuntimeUtil;

public class MainMBean {

	public static void main(String[] args) {

		TestMBean bean = new TestMBean();

		for (int i = 0; i < 100; i++) {
			bean.setAtivo(true);
			bean.imprimir();
			bean.getIdade();
		}

		Execution e = RuntimeUtil.getInstance().getCurrentExecution();

		System.out.println(e.getId() + " - " + e.getDate());

		for (RuntimeScenario rs : e.getScenarios()) {
			StringBuilder sb = new StringBuilder();

			try {
				PrintUtil.printScenarioTree(rs, sb);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			System.out.println(sb);
		}

	}

}
