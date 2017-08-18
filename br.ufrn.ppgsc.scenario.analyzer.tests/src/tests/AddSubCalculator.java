package tests;

import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Security;
import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Component;
import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario;

@Component(name = "cp - AddSubCalculator")
public class AddSubCalculator implements IAddSubCalculator {

	@Scenario(name = "sub in add")
	public float add(float a, float b) {
		test(null);
		return a + b;
	}

	@Scenario(name = "subsub in test")
	@Security(name = "sec in test")
	public void test(java.util.List<java.lang.Integer> param) {
		System.out.println(param);
	}

	public void test2(List<Integer> param) {
		System.out.println(param);
	}

	public float sub(float a, float b) {
		try {
			Thread.sleep(2);
			test2(null);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return a - b;
	}

}
