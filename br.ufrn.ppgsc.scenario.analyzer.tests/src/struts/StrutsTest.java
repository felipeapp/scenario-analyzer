package struts;

import org.apache.struts.actions.DispatchAction;

public class StrutsTest extends DispatchAction {

	public static void main(String[] args) {
		System.out.println("No main!");
		new StrutsTest().methodtest();
	}

	public void methodtest() {
		System.out.println("Teste!");
	}

	public StrutsTest() {
		System.out.println("No construtor!");
	}

}
