package trueffect.truconnect.api.crud.util;

import java.io.IOException;
import java.util.HashMap;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author gustavo.claure
 */
public class QueryParser {

    static String regex = "([a-z])([A-Z])";
    static String replacement = "$1_$2";
    static String separator = "<>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>";
    static String separatorF = "  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>  <>";

    public static void selectOneParser(String name, Long id, SqlSession session) {
        System.out.println(separator);
        Configuration configuration = session.getConfiguration();
        MappedStatement ms = configuration.getMappedStatement(name);
        BoundSql boundSql = ms.getBoundSql(id);
        String query = boundSql.getSql();
        System.out.println("1--------------------------------------------------------------------------------------1");
        System.out.println("\t\t\t" + name);
        System.out.println("1--------------------------------------------------------------------------------------1");
        if (query.contains("?")) {
            query = query.replace("?", id.toString());
        }
        System.out.println(query);
        System.out.println("1--------------------------------------------------------------------------------------1");
        System.out.println(separatorF);
    }

    public static void selectOneParser(String name, String value, SqlSession session) {
        System.out.println(separator);
        Configuration configuration = session.getConfiguration();
        MappedStatement ms = configuration.getMappedStatement(name);
        BoundSql boundSql = ms.getBoundSql(value);
        String query = boundSql.getSql();

        System.out.println("2--------------------------------------------------------------------------------------2");
        System.out.println("\t\t\t" + name);
        System.out.println("2--------------------------------------------------------------------------------------2");
        System.out.println(query);
        System.out.println("2--------------------------------------------------------------------------------------2");
        System.out.println(separatorF);
    }

    public static void callProcedurePlSqlParser(String name, HashMap<String, Object> parameter, SqlSession session) throws IOException {
        System.out.println(separator);
        Configuration configuration = session.getConfiguration();
        MappedStatement ms = configuration.getMappedStatement(name);
        BoundSql boundSql = ms.getBoundSql(parameter);
        String query = boundSql.getSql();

        System.out.println("3--------------------------------------------------------------------------------------3");
        System.out.println("\t\t\t" + name);
        System.out.println("3--------------------------------------------------------------------------------------3");
        System.out.println("***************************************************************************************");
        for (String key : parameter.keySet()) {
            String parameterKey = key.replaceAll(regex, replacement).toUpperCase();
            query = query.replace(parameterKey + " = ?", parameterKey + " = " + parameter.get(key).toString());
        }
        System.out.println("***************************************** END *****************************************");
        System.out.println("Parameter:\t" + parameter);
        System.out.println("Query:\t" + query);
        System.out.println("Name:\t" + name);
        System.out.println("3--------------------------------------------------------------------------------------3");
        System.out.println(separatorF);
    }

    public static void selectOneParser(String name, HashMap<String, Object> parameter, SqlSession session) throws IOException {
        System.out.println(separator);
        Configuration configuration = session.getConfiguration();
        MappedStatement ms = configuration.getMappedStatement(name);
        BoundSql boundSql = ms.getBoundSql(parameter);
        String query = boundSql.getSql();

        System.out.println("4--------------------------------------------------------------------------------------4");
        System.out.println("\t\t\t" + name);
        System.out.println("4--------------------------------------------------------------------------------------4");
        System.out.println("***************************************************************************************");
        for (String key : parameter.keySet()) {
            String parameterKey = key.replaceAll(regex, replacement).toUpperCase();
            query = query.replace(parameterKey + " = ?", parameterKey + " = " + parameter.get(key).toString());
        }
        System.out.println("***************************************** END *****************************************");
        System.out.println("Parameter:\t" + parameter);
        System.out.println("Query:\t" + query);
        System.out.println("Name:\t" + name);
        System.out.println("4--------------------------------------------------------------------------------------4");
        System.out.println(separatorF);
    }
}
