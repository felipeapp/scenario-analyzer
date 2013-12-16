package tests;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Robustness;
import br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.d.util.PrintUtil;
import br.ufrn.ppgsc.scenario.analyzer.d.util.RuntimeUtil;

public class Main {
	
	@Robustness(name="robustness_convert")
	public int convertToInt(String str) {
		try {
			int a = 1 / 1;
		}
		catch (ArithmeticException e) {
			
		}
		
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			return -1;
		}
	}
	
	@Robustness(name="robstness_divide")
	public int divide(int a, int b, int[] c) {
		return a / b;
	}
	
	public int nulltest(String str) {
		return str.length();
	}

	@Scenario(name="main")
	public static void main(String args[]) throws IOException {
		Main m = new Main();
		
		System.out.println("mais uma linha");
		System.out.println(m.convertToInt("ss10felipe"));
		
		try {
			System.out.println(m.divide(1, 0, null));
		} catch (ArithmeticException e) {
			System.out.println("main também pega");
		}
		
		try {
			System.out.println(m.nulltest(null));
		} catch (NullPointerException e) {
			System.out.println("main também pega");
		}
		
		System.out.println(m.convertToInt("10"));
		
		System.out.println("--------------------------------------------------");
		
		IFullCalculator calc = new FullCalculator();

		System.out.println(calc.add(6, 2));
		System.out.println(calc.sub(6, 2));
		System.out.println(calc.mult(6, 2));
		System.out.println(calc.div(6, 2));
		
		for (RuntimeScenario cg : RuntimeUtil.getCurrentExecution().getScenarios()) {
			StringBuilder sb = new StringBuilder();
			PrintUtil.printScenarioTree(cg, sb);
			System.out.println(sb);
		}
		
	} // Fim do main

} // Fim da classe
