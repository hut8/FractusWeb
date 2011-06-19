package fractus.web;

public class Logout extends FractusPage {
	public Logout() {
		getSession().invalidate();
		setResponsePage(getApplication().getHomePage());
	}
}
