package threads;

import br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.d.util.PrintUtil;
import br.ufrn.ppgsc.scenario.analyzer.d.util.RuntimeUtil;

public class MainThread {
	
	@Scenario(name="Method A")
	public void methodA() {
		methodB();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void methodB() {
		methodC();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void methodC() {
		System.out.println("Method C");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			throw new NullPointerException();
		} catch(Exception e) {
			
		}
		
		try {
			divide(1, 0);
		} catch (ArithmeticException ex) {
			
		}
		
	}
	
	public int divide(int a, int b) {
		return a / b;
	}
	
	public static void main(String[] args) throws Exception {

		new MyThread().start();
		
		for (int i = 0; i < 5; ++i)
			new Thread(new Runnable() {
				public void run() {
					new MainThread().methodA();
				}
			}).start();
			
		new MainThread().methodA();
		
		for (RuntimeScenario cg : RuntimeUtil.getCurrentExecution().getScenarios()) {
			StringBuilder sb = new StringBuilder();
			PrintUtil.printScenarioTree(cg, sb);
			System.out.println(sb);
		}
		
	}

}
