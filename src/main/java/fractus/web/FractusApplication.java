package fractus.web;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;

public class FractusApplication extends WebApplication
{    
    /**
     * Constructor
     */
	public FractusApplication()
	{
	}
	
	@Override
	protected void init() {
		try {
			FractusDB database = FractusDB.getInstance();
			database.init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// Configuration
		getRequestLoggerSettings().setRequestLoggerEnabled(true);
		getMarkupSettings().setStripWicketTags(true);
		getMarkupSettings().setStripComments(true);
		getMarkupSettings().setDefaultAfterDisabledLink("");
		getMarkupSettings().setDefaultBeforeDisabledLink("");
	}
	
	@Override
	public Session newSession(Request request, Response response) {
		return new FractusSession(request);
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}

}
