package fractus.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.PropertyModel;

public abstract class FractusPage extends WebPage {
	public FractusPage() {
		super();
        // UserPanel (authentication)
		Fragment userPanel = null;
		// Logged in:
		if (FractusSession.get().isAuthenticated()) {
			userPanel = new Fragment("userPanel", "logged-in", this);
			userPanel.add(new Label("username", new PropertyModel<FractusPage>(this, "session.user.username")));		
			userPanel.add(new BookmarkablePageLink<Logout>("logout", Logout.class));
		} else {
			// Not logged in
			userPanel = new Fragment("userPanel", "logged-out", this);
		}
		userPanel.setOutputMarkupId(true);
		userPanel.setMarkupId("userPanel");
		add(userPanel);
	}
}
