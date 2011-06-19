package fractus.web;

public class InvalidAccountException extends Exception {
	private static final long serialVersionUID = 3116288479331422303L;

	public enum Reason {
		USERNAME_LENGTH,
		USERNAME_EXISTS,
		PASSWORD_COMPLEXITY,
		INVALID_EMAIL
	}
	
	private Reason reason;
	public Reason getReason() { return reason; }
	
	public InvalidAccountException() {
	}
	
	public InvalidAccountException(Reason reason) {
		this.reason = reason;
	}

	public InvalidAccountException(String arg0) {
		super(arg0);
	}

	public InvalidAccountException(Throwable arg0) {
		super(arg0);
	}

	public InvalidAccountException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
