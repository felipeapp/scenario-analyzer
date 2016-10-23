package tests;

public class TestTimeNano {

	public static void main(String[] args) throws InterruptedException {

		long begin = System.nanoTime();

		Thread.sleep(10000);

		long end = System.nanoTime();

		System.out.println((end - begin) / 1000000.0);

	}

}
