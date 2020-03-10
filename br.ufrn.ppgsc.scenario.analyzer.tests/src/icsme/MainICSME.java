package icsme;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario;

public class MainICSME {

	public static void main(String[] args) {
		User u = new User(true, false, true);
		MainICSME m = new MainICSME(u);
		System.out.println(m.checkUser(10));
	}

	public MainICSME(User u) {
		this.u = u;
	}

	private User u;

	@Scenario(name = "Check User Status")
	public boolean checkUser(long id) {
		u = retrieveUserInformation(id);
		return !hasIrregularity() && u.isActive();
	}

	public boolean hasIrregularity() {
		return u.isOverdue() && u.hasFine();
	}

	@Performance(name = "Alerto for retrieveUserInformation", limitTime = 100)
	public User retrieveUserInformation(long id) {
		return null;
	}

}
