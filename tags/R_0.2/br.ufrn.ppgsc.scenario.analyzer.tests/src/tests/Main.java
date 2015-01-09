package tests;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario;

public class Main {
	
	public int convertToInt(String str) {
		int a = 0;
		
		try {
			a = 1 / 1;
		}
		catch (ArithmeticException e) {
			System.out.println("ArithmeticException: " + a);
		}
		
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			return -1;
		}
	}
	
	public int divide(int a, int b, int[] c) {
		return a / b;
	}
	
	public int nulltest(String str) {
		return str.length();
	}

	public static void main(String args[]) throws IOException {
		Main.run();
	}
	
	@Scenario(name="main")
	public static void run() throws IOException {
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
		
//		for (RuntimeScenario cg : RuntimeUtil.getCurrentExecution().getScenarios()) {
//			StringBuilder sb = new StringBuilder();
//			PrintUtil.printScenarioTree(cg, sb);
//			System.out.println(sb);
//		}
		
	} // Fim do main

} // Fim da classe
