package fractus.web;

import java.util.Date;

public class User {
	private String username;
	private String emailAddress;
	private Date creationDate;
	
	public User(AccountData accountData) {
		this.username = accountData.getUsername();
		this.emailAddress = accountData.getEmail();
		this.creationDate = accountData.getCreationDate();
	}

	public String getUsername() {
		return username;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public Date getCreationDate() {
		return creationDate;
	}
}
