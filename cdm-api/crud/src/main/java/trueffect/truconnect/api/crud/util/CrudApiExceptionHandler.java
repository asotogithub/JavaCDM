package trueffect.truconnect.api.crud.util;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.exceptions.PersistenceException;

public class CrudApiExceptionHandler {

	public static String getMessage(Exception e) {
        String str = null;
        
        if (e.getCause() != null) {
        	str = e.getCause().getMessage();
        } else {
        	str = e.getMessage();
        }
        if (e instanceof PersistenceException
        		|| e instanceof SQLException
        		|| str.indexOf("ORA") != -1) {
        	String[] parts = str.split("\n");
        	str = "Data base error: " + getORAMessage(parts[0]);
        }
        return str;
    }

    private static String getORAMessage(String str) {
        Pattern p = Pattern.compile("ORA\\-[0-9]+:\\s(.+)");
        Matcher matcher = p.matcher(str);
        if (matcher.matches()) {
            return  matcher.group(1);
        }
        return str;
    }
}
