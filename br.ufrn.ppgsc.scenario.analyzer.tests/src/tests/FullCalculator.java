package tests;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario;

//@Component(name = "cp - FullCalculator")
public class FullCalculator implements IFullCalculator {

	private IAddSubCalculator add_sub;
	private IMultDivCalculator mult_div;

	public FullCalculator() {
		add_sub = new AddSubCalculator();
		mult_div = new MultDivCalculator();
		// throw new NullPointerException();
	}

	@Scenario(name = "SC-FullCalculator-add")
	@Performance(name = "PM-FullCalculator-add", limitTime = 1)
	public float add(float a, float b) {
		add_sub.sub(a, b);

		System.out.println("teste");

		if (add_sub.add(a, b) > 5)
			return add(a - 1, b);

		return add_sub.add(a, b);
	}

	@Scenario(name = "SC-FullCalculator-sub")
	@Performance(name = "PM-FullCalculator-sub", limitTime = 1)
	public float sub(float a, float b) {
		return add_sub.sub(a, b);
	}

	@Scenario(name = "SC-FullCalculator-mult")
	@Performance(name = "PM-FullCalculator-mult")
	public float mult(float a, float b) {
		return mult_div.mult(a, b);
	}

	@Scenario(name = "SC-FullCalculator-div")
	@Performance(name = "PM-FullCalculator-div")
	public float div(float a, float b) {
		return mult_div.div(a, b);
	}

}
