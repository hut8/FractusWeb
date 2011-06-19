package fractus.web;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public final class FractusDB {
	// Singleton
	private static FractusDB instance;
	static {
		instance = new FractusDB();
	}
	public static FractusDB getInstance() {
		return instance;
	}
	private FractusDB() { }
	
	private final static Logger log = LoggerFactory.getLogger(FractusDB.class);

	private BoneCPConfig poolConfig;
	private BoneCP connectionPool;
	private String url;

	public void init()
	throws ClassNotFoundException, SQLException {
		this.url = "jdbc:mysql://localhost:3306/fractus";
		log.info("Loading MySQL driver");
		Class.forName("com.mysql.jdbc.Driver");
		log.info("Configuring connection pool to {}", this.url);
		this.poolConfig = new BoneCPConfig();
		this.poolConfig.setJdbcUrl(url);
		this.poolConfig.setUsername("fractus"); 
		this.poolConfig.setPassword("fractus"); // Security = localhost only
		this.poolConfig.setMinConnectionsPerPartition(5);
		this.poolConfig.setMaxConnectionsPerPartition(10);
		this.poolConfig.setPartitionCount(1);
		log.info("Creating connection pool");
		this.connectionPool = new BoneCP(poolConfig);
	}
	
	public AccountData getAccountData(String username)
	throws SQLException {
		log.debug("Trying to authenticate {}", username);
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL GetAccountData_prc(?)");
			sth.setString(1, username);
			ResultSet accountDataRS = sth.executeQuery();
			if (!accountDataRS.next()) {
				log.debug("Username {} does not exist", username);
				return null;
			}
			AccountData accountData = new AccountData();
			accountData.setUsername(username);
			accountData.setEmail(accountDataRS.getString("EMAIL"));
			accountData.setCreationDate(accountDataRS.getDate("REGISTEREDTIME"));
			accountData.setPassword(accountDataRS.getBytes("PASSWORD"));
			accountData.setSalt(accountDataRS.getBytes("SALT"));
			return accountData;
		} finally {
			if (sth != null)
				sth.close();
			conn.close();
		}
	}
	
	public boolean registerAccount(AccountData accountData)
	throws SQLException {
		log.debug("Trying to register {}", accountData.getUsername());
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL RegisterAccount_prc(?,?,?,?,?)");
			sth.setString(1, accountData.getUsername());
			sth.setBytes(2, accountData.getPassword());
			sth.setString(3, accountData.getEmail());
			Calendar c = Calendar.getInstance();
			c.setTime(accountData.getCreationDate());
			sth.setDate(4, new Date(c.getTimeInMillis()));
			sth.setBytes(5, accountData.getSalt());
			int rowAffected = sth.executeUpdate();
			return rowAffected > 0;
		} finally {
			if (sth != null)
				sth.close();
			conn.close();
		}
	}
}
