package trueffect.truconnect.api.commons.exceptions;

@SuppressWarnings("serial")
public class SearchApiException extends Exception {

	protected final static String HEADER_MESSAGE = "Exception on Search API query.\n";
	
	public SearchApiException() {
		super(HEADER_MESSAGE);
	}

	public SearchApiException(String message) {
		super(HEADER_MESSAGE + message);
	}

}
