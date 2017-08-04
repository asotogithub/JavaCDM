package trueffect.truconnect.api.commons.exceptions;

@SuppressWarnings("serial")
public class DataNotFoundForUserException extends Exception {

    @Deprecated
    public final static String HEADER_MESSAGE = "Data not found for User: ";
    public final static String NOT_FOUND_MESSAGE = "Not found for User: ";

    public DataNotFoundForUserException(String message) {
        super(message);
    }

}
