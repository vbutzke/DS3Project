package app.exceptions;

public class DuplicateEntityException extends Exception{

	private static final long serialVersionUID = 1997753363232807009L;

	    public DuplicateEntityException() {
	    }

	    public DuplicateEntityException(String message) {
	        super(message);
	    }

	    public DuplicateEntityException(Throwable cause) {
	        super(cause);
	    }

	    public DuplicateEntityException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public DuplicateEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	        super(message, cause, enableSuppression, writableStackTrace);
	    }
	
}