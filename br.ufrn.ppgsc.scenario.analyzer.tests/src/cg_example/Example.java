package cg_example;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario;

public class Example {
	
	public static void main(String[] args) {
		new Example().root(0);
	}
	
	@Scenario(name = "root scenario")
	void root(int n) {
		A();
		B();
		
		for (int i = 0; i < n; i++)
			C();
	}

	void A() {  }
	void B() { D(); }
	void C() { E();	}
	void D() {	}
	void E() {	}

}
