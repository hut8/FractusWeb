package fractus.web;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends FractusPage {
	private final static Logger log = LoggerFactory.getLogger(Login.class);
	
	public Login() {
		add(new LoginForm("loginForm"));
	}
	
	public class LoginForm extends StatelessForm<LoginForm> {
		private static final long serialVersionUID = -5665458525073848486L;
		
		private String username;
		private String password;
		
		public LoginForm(String id) {
			super(id);
			setModel(new CompoundPropertyModel<LoginForm>(this));
			add(new TextField<LoginForm>("username"));
			add(new PasswordTextField("password"));
			add(new FeedbackPanel("feedback"));
		}
	
		@Override
		protected void onSubmit() {
			if (this.username == null || this.password == null) {
				log.info("Username or password are null");
				return;
			}
			AccountManager accountManager = AccountManager.getInstance();
			User authUser = accountManager.authenticate(username, password);
			if (authUser == null) {
				log.info("Authentication failed for {}", this.username);
				error("Authentication failed");
				return;
			}
			log.info("Authentication succeeded for {}. Redirecting", this.username);
			setResponsePage(Account.class);
		}
	}
}
