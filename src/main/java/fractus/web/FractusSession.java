package fractus.web;

import java.util.Locale;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.RequestLogger.ISessionLogInfo;
import org.apache.wicket.protocol.http.WebSession;

public class FractusSession extends WebSession
implements ISessionLogInfo {
	private static final long serialVersionUID = 1L;

	// BALLER CONTRAVARIANCE.
	public static FractusSession get() {
		return (FractusSession) Session.get();
	}
	
	private User user;
	
	public FractusSession(Request request) {
		super(request);
		setLocale(Locale.ENGLISH);
	}

	public synchronized boolean isAuthenticated() {
		return this.user != null;
	}
	
	public synchronized Object getSessionInfo() {
		return this.user != null ? "[" + this.user.getUsername() + "]" : "[N/A]";
	}
	
	public synchronized User getUser() {
		return user;
	}

	public synchronized void setUser(User user) {
		this.user = user;
		dirty();
	}

}
