package compsac;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario;


public class MainCompSac {

	public static void main(String[] args) {
		User u = new User(true, false, true);
		MainCompSac m = new MainCompSac(u);
		System.out.println(m.selectedUser());
	}
	
	public MainCompSac(User u) {
		this.u = u;
	}
	
	/*...*/
	private User u;
	
	@Scenario(name = "Check User Status")
	public boolean selectedUser()
	{ return !hasIrregularity() && u.isActive(); }

	public boolean hasIrregularity()
	{ return u.isOverdue() && u.hasFine(); }
	/*...*/

}
