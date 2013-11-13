package tests;

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
	public void A() {
		B();
		C();
		new Main2();
	}

	private int B() {
		C();
		return 0;
	}

	@Scenario(name = "Scenario C")
	public void C() {
		D();
	}

	public void D() {
		System.out.println("Aqui em D!");
	}

}
