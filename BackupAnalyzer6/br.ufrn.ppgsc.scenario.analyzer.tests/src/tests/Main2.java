package tests;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Reliability;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Security;
import br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario;

public class Main2 {

	public static void main(String[] args) {
		Main2 o = new Main2();

		o.B();
		o.A();
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
	@Scenario(name = "Scenario B")
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
	@Security(name = "qa_d_security")
	public void D() {
		try {
			System.out.println(1 / 0);
		} catch (ArithmeticException e) {
			System.out.println("Exceção em D!");
		}
		
		System.out.println("Aqui em D!");
	}

}