package threads;

//import org.springframework.stereotype.Component;

import br.ufrn.ppgsc.scenario.analyzer.annotations.arq.Scenario;

//@Component
public class MainThread {

	private long time;
	
	public MainThread(long time) {
		this.time = time;
	}
	
	@Scenario(name = "Method A")
	public void methodA() {
		System.out.println(Thread.currentThread().getId() + " in Method A");
		
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		methodB();
		methodC();
	}

	@Scenario(name = "Method B")
	public void methodB() {
		System.out.println(Thread.currentThread().getId() + " in Method B");
		
		methodC();

//		try {
//			Thread.sleep(time);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	@Scenario(name = "Method C")
	public void methodC() {
		System.out.println(Thread.currentThread().getId() + " in Method C");

//		try {
//			Thread.sleep(time);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		try {
			throw new NullPointerException();
		} catch (Exception e) {

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
		
//		for (int i = 0; i < 10; ++i)
//			new Thread(new Runnable() {
//				public void run() {
//					new MainThread(500).methodA();
//				}
//			}).start();

		new Thread(new Runnable() {
			public void run() {
				for (int i = 10; i > 0; i--) {
					new MainThread(0).methodA();
					System.out.println("id: " + Thread.currentThread().getId());
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				for (int i = 1; i <= 10; i++) {
					new MainThread(0).methodA();
					System.out.println("id: " + Thread.currentThread().getId());
				}
			}
		}).start();
		
//		new Thread(new Runnable() {
//			public void run() {
//				new MainThread(0).methodA();
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			public void run() {
//				new MainThread(0).methodA();
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			public void run() {
//				new MainThread(0).methodA();
//			}
//		}).start();

	}

}
