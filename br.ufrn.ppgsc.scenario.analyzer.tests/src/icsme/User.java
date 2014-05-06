package icsme;

public class User {
	/*...*/
	public User(boolean o, boolean f, boolean a)
	{ overdue = o; fine = f; active = a; }
	
	private boolean overdue, fine, active;
	public boolean isOverdue() { return overdue; }
	public boolean hasFine() { return fine; }
	public boolean isActive() {	return active; }
	/*...*/
}
