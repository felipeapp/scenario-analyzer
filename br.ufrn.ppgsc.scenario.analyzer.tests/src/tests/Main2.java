package tests;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Reliability;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Robustness;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Security;
import br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario;

public class Main2 {

	public static void main(String[] args) {
		Main2 o = new Main2();
		
		o.B();
		o.A();
		
//		Execution e = RuntimeUtil.getCurrentExecution();
//
//		System.out.println(e.getId() + " - " + e.getDate());
//
//		for (RuntimeScenario rs : e.getScenarios()) {
//			StringBuilder sb = new StringBuilder();
//			
//			try {
//				PrintUtil.printScenarioTree(rs, sb);
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//			
//			System.out.println(sb);
//		}
//		
//		DatabaseService.saveResults(e);
	}
	
	private Main2() {
		
	}

	@Scenario(name = "Scenario A")
	@Performance(name = "qa_a_performance", limitTime = 10)
	public void A() {
		B();
		C();
		new Main2();
	}

	@Performance(name = "qa_b_performance", limitTime = 4)
	@Reliability(name = "qa_b_reliability", failureRate = 0.1)
	private int B() {
		C();
		return 0;
	}

	@Scenario(name = "Scenario C")
	public void C() {
		D();
	}

	@Performance(name = "qa_d_performance", limitTime = 5)
	@Reliability(name = "qa_d_reliability", failureRate = 0.01)
	@Robustness(name = "qa_d_robustness")
	@Security(name = "qa_d_security")
	public void D() {
		System.out.println("Aqui em D!");
	}

}
