package trueffect.truconnect.api.crud;

import java.util.HashMap;

/**
 *
 * @author Richard Jaldin
 */
public class ResponseConstants {
    // T-uplas for errors

    public static final HashMap<String, String> WSR_OK = new HashMap<String, String>() {
        {
            put("code", "200");
            put("status", "OK");
            put("description", "No error, operation successful.");
        }
    };
    public static final HashMap<String, String> WSR_CREATED = new HashMap<String, String>() {
        {
            put("code", "201");
            put("status", "Created");
            put("description", "Successful creation of a resource.");
        }
    };
    public static final HashMap<String, String> WSR_ACCEPTED = new HashMap<String, String>() {
        {
            put("code", "202");
            put("status", "Accepted");
            put("description", "The request was received.");
        }
    };
    public static final HashMap<String, String> WSR_NO_CONTENT = new HashMap<String, String>() {
        {
            put("code", "204");
            put("status", "No Content");
            put("description", "The request was processed successfully, but no response body is needed.");
        }
    };
    public static final HashMap<String, String> WSR_BAD_REQUEST = new HashMap<String, String>() {
        {
            put("code", "400");
            put("status", "Bad Request");
            put("description", "Malformed syntax or a bad query.");
        }
    };
    public static final HashMap<String, String> WSR_UNAUTHORIZED = new HashMap<String, String>() {
        {
            put("code", "401");
            put("status", "Unauthorized");
            put("description", "Action requires user authentication.");
        }
    };
    public static final HashMap<String, String> WSR_FORBIDDEN = new HashMap<String, String>() {
        {
            put("code", "403");
            put("status", "Forbidden");
            put("description", "Authentication failure or invalid Application ID.");
        }
    };
    public static final HashMap<String, String> WSR_NOT_FOUND = new HashMap<String, String>() {
        {
            put("code", "404");
            put("status", "Not Found");
            put("description", "Resource not found.");
        }
    };
    public static final HashMap<String, String> WSR_NOT_ALLOWED = new HashMap<String, String>() {
        {
            put("code", "405");
            put("status", "Not Allowed");
            put("description", "Method not allowed on resource.");
        }
    };
    public static final HashMap<String, String> WSR_SERVER_ERROR = new HashMap<String, String>() {
        {
            put("code", "500");
            put("status", "Server Error");
            put("description", "Internal server error.");
        }
    };
}
