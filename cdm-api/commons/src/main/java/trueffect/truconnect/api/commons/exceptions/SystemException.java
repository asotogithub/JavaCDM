package trueffect.truconnect.api.commons.exceptions;

import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * Runtime Exception used to generalize different types of Error situations.
 * <p>
 * The advantage of using this unchecked exception is that it doesn't require
 * client methods to throw an exception or define try/catch blocks.
 * <p>
 * This exception will be used mainly for unrecoverable situations related to business logic in the application
 * @author Marcelo Heredia
 */
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private ErrorCode errorCode;
    private final Map<String,Object> properties = new TreeMap<String,Object>();

    public static SystemException wrap(Throwable exception, ErrorCode errorCode) {
        if (exception instanceof SystemException) {
            SystemException se = (SystemException)exception;
        	if (errorCode != null && errorCode != se.getErrorCode()) {
                return new SystemException(exception.getMessage(), exception, errorCode);
			}
			return se;
        } else {
            return new SystemException(exception.getMessage(), exception, errorCode);
        }
    }

    public static SystemException wrap(Throwable exception) {
    	return wrap(exception, null);
    }


    public SystemException(ErrorCode errorCode) {
        super(getMessage(errorCode));
        this.errorCode = errorCode;
	}

    public SystemException(ErrorCode errorCode, Object... params) {
        super(getMessage(errorCode, params));
        this.errorCode = errorCode;
	}

	public SystemException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public SystemException(Throwable cause, ErrorCode errorCode) {
        super(getMessage(errorCode), cause);
		this.errorCode = errorCode;
	}

	public SystemException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
        return errorCode;
    }
	
	public SystemException setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
    @SuppressWarnings("unchecked")
	public <T> T get(String name) {
        return (T)properties.get(name);
    }
	
    public SystemException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }
    
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            printStackTrace(new PrintWriter(s));
        }
    }

    public String toString(){
        return super.toString() + "\n" + getExtraDetails();
    }

    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            s.println(this);
            StackTraceElement[] trace = getStackTrace();
            for (int i=0; i < trace.length; i++)
                s.println("\tat " + trace[i]);

            Throwable ourCause = getCause();
            if (ourCause != null) {
                ourCause.printStackTrace(s);
            }
            s.flush();
        }
    }

    private String getExtraDetails(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t-------------------------------\n");
        if (errorCode != null) {
            sb.append("\t" + errorCode + ":" + errorCode.getClass().getName() + "\n");
        }
        for (String key : properties.keySet()) {
            sb.append("\t" + key + "=[" + properties.get(key) + "]\n");
        }
        sb.append("\t-------------------------------\n");
        return sb.toString();
    }

    private static String getMessage(ErrorCode errorCode, Object... args){
        return ResourceBundleUtil.getString(errorCode.getClass().getSimpleName() + "." + errorCode, args);
    }
}
