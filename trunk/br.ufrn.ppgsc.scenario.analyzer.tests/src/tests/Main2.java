package tests;

import junit.framework.TestCase;

import org.junit.Test;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario;


public class Main2 extends TestCase {

	public static void main(String[] args) {
		Main2 o = new Main2();

		o.testB();
		o.testA();
	}

	private Main2() {

	}

//	@Scenario(name = "Scenario A")
	@Test
//	@Performance(name = "qa_a_performance", limitTime = 10)
	public void testA() {
		testB();
		C();
		new Main2();
	}

//	@Performance(name = "qa_b_performance", limitTime = 4)
//	@Reliability(name = "qa_b_reliability", failureRate = 0.1)
	@Test
	@Scenario(name = "Scenario B")
	private int testB() {
		C();
		return 0;
	}

//	@Scenario(name = "Scenario C")
	@Test
	public void C() {
		D();
	}

//	@Performance(name = "qa_d_performance", limitTime = 5)
//	@Reliability(name = "qa_d_reliability", failureRate = 0.01)
//	@Security(name = "qa_d_security")
	public void D() {
		try {
			System.out.println(1 / 0);
		} catch (ArithmeticException e) {
			System.out.println("Exceção em D!");
		}
		
		System.out.println("Aqui em D!");
	}

}
