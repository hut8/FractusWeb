package fractus.web;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fractus.web.InvalidAccountException.Reason;

public class RegisterAccount extends FractusPage {
	private final static Logger log = LoggerFactory.getLogger(RegisterAccount.class);

	public RegisterAccount() {
		add(new CreateAccountForm("createAccountForm"));
	}

	public final class CreateAccountForm extends Form<CreateAccountForm> {
		private static final long serialVersionUID = -3160313842208440610L;

		private static final String FIELD_USERNAME = "username";
		private static final String FIELD_PASSWORD = "password";
		private static final String FIELD_PASSWORD_CONFIRM = "passwordConfirm";
		private static final String FIELD_EMAIL = "email";
		private static final String FIELD_CAPTCHA = "captcha";

		// Model
		private String username;
		private String password;
		private String email;
		private String captcha;

		public CreateAccountForm(String id) {
			super(id);
			setModel(new Model<CreateAccountForm>(this));
			setMarkupId("createAccountForm");
			add(new TextField<String>(FIELD_USERNAME,
					new PropertyModel<String>(this, "username"))
					.setRequired(true)
					.add(StringValidator.lengthBetween(3, 32))
					.add(new PatternValidator("^\\w+$")));
			PasswordTextField pw = new PasswordTextField(FIELD_PASSWORD,
					new PropertyModel<String>(this, "password"));
			PasswordTextField pwc = new PasswordTextField(FIELD_PASSWORD_CONFIRM,
					new PropertyModel<String>(this, "password"));
			add(pw);
			add(pwc);
			add(new EqualPasswordInputValidator(pw,pwc));
			
			TextField<String> emailField = new TextField<String>(FIELD_EMAIL,
					new PropertyModel<String>(this, "email"));
					emailField.add(new IValidator<String>() {
						private static final long serialVersionUID = 1L;
						public void validate(IValidatable<String> validatable) {
							if (validatable.getValue() != null && !"".equals(validatable.getValue())) {
								EmailAddressValidator.getInstance().validate(validatable);
							}
						}
					});
			
			add(emailField);
			add(new TextField<String>(FIELD_CAPTCHA,
					new PropertyModel<String>(this, "captcha")));
			add(new FeedbackPanel("feedbackPanel"));
		}

		@Override
		protected void onSubmit() {	
			AccountManager am = AccountManager.getInstance();
			AccountData accountData = null;
			try {
				accountData = am.registerAccount(username, password, email);
			} catch (NoSuchAlgorithmException e) {
				log.warn("Could not register account: SHA-512 gone!",e);
			} catch (InvalidAccountException e) {
				log.warn("Invalid account request: {}", e.getReason().toString());
				switch (e.getReason()) {
				case INVALID_EMAIL:
					error("Invalid email address");
					break;
				case PASSWORD_COMPLEXITY:
					error("Password fails complexity requirements");
					break;
				case USERNAME_EXISTS:
					error("That username already exists");
					break;
				case USERNAME_LENGTH:
					error("Username must be between 3 and 32 characters");
					break;
				}
			} catch (SQLException e) {
				throw new WicketRuntimeException(e);
			}
			if (accountData != null) {
				FractusSession.get().setUser(new User(accountData));
				setResponsePage(HomePage.class);
			}
		}
	}
}
