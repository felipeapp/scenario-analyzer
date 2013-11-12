package threads;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario;
import br.ufrn.ppgsc.scenario.analyzer.d.data.DataUtil;
import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;

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
		
		for (RuntimeScenario cg : Execution.getInstance().getAllRuntimeCallGraph()) {
			StringBuilder sb = new StringBuilder();
			DataUtil.printScenarioTree(cg, sb);
			System.out.println(sb);
		}
		
	}

}
