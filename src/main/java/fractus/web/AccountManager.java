package fractus.web;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fractus.web.InvalidAccountException.Reason;

public class AccountManager {
	private static AccountManager instance = new AccountManager();
	public static AccountManager getInstance() { return instance; }
	private SecureRandom secureRandom;
	private final static Logger log = LoggerFactory.getLogger(AccountManager.class);
	
	private AccountManager() {
		secureRandom = new SecureRandom();
	}
	
	private final static Pattern RFC2822_PATTERN = Pattern.compile(
	        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
	);
	
	public User authenticate(String username, String password) {
		AccountData accountData = null;
		try {
			accountData = FractusDB.getInstance().getAccountData(username);
		} catch (SQLException e) {
			log.warn("Could not authenticate due to SQL Error",e);
		}
		if (accountData == null) {
			log.info("No such account: {}", username);
			return null;
		}
		
		byte[] candidatePassword = null;
		try {
			 candidatePassword = derivePassword(password, accountData.getSalt());
		} catch (NoSuchAlgorithmException e) {
			log.warn("Could not authenticate - lost SHA-512",e);
		}
		if (Arrays.equals(candidatePassword, accountData.getPassword())) {
			log.info("Authenticated {}", username);
			User user = new User(accountData);
			FractusSession.get().setUser(user);
			return user;
		}
		log.info("Authenticate failed for {}", username);
		return null;
	}
	
	public static byte[] derivePassword(String password, byte[] salt)
	throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		byte[] passwordEncoded;
		try {
			passwordEncoded = password.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Lost my UTF-8!",e);
			throw new Error(e);
		}
		digest.update(passwordEncoded, 0, passwordEncoded.length);
		digest.update(salt, 0, salt.length);
		byte[] passwordDigest = digest.digest();
		return passwordDigest;
	}
		
	public AccountData registerAccount(String username, String password, String emailAddress)
	throws NoSuchAlgorithmException, InvalidAccountException, SQLException {
		byte[] salt = new byte[512/8];
		secureRandom.nextBytes(salt);
		byte[] passwordHash = derivePassword(password, salt);
		
		if (username.length() > 32 || username.length() < 3) {
			throw new InvalidAccountException(Reason.USERNAME_LENGTH);
		}
		
//		if (!validatePassword(password)) {
//			throw new InvalidAccountException(Reason.PASSWORD_COMPLEXITY);
//		}
		
		if (emailAddress != null && !"".matches(emailAddress) 
				&& !RFC2822_PATTERN.matcher(emailAddress).matches()) {
			throw new InvalidAccountException(Reason.INVALID_EMAIL);
		}
		
		AccountData accountData = new AccountData();
		accountData.setUsername(username);
		accountData.setPassword(passwordHash);
		if (!"".equals(emailAddress)) {
			accountData.setEmail(emailAddress);
		}
		accountData.setSalt(salt);
		
		if (FractusDB.getInstance().registerAccount(accountData)) {
			return accountData;
		} else {
			throw new InvalidAccountException(Reason.USERNAME_EXISTS);
		}
	}
	
	public void deleteAccount(String username, String password) {
		
	}
}
