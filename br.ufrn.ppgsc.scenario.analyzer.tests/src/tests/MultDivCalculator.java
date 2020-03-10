package tests;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Reliability;
import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Component;

@Component(name = "cp - MultDivCalculator")
public class MultDivCalculator implements IMultDivCalculator {

	public float mult(float a, float b) {
		return a * b;
	}

	@Reliability(name = "reli", failureRate = 0.5)
	public float div(float a, float b) {
		return a / b;
	}

}
