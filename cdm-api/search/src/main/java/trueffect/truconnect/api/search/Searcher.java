package trueffect.truconnect.api.search;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang.StringUtils;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;

public class Searcher<T> {

    private String query;
    private Class<T> klass;
    private SearchApiLoader<T> parserLoader;

    public Searcher(String query, Class<T> klass) throws SearchApiException {
	this.query = query;
	this.klass = klass;
	if(!StringUtils.isBlank(query)){
	    initParsing();
	}
    }

    /**
     * This should start the parsing process and create the query for the database 
     * @throws SearchApiException 
     */
    public void initParsing() throws SearchApiException {
	ANTLRInputStream input = new ANTLRInputStream(this.query);
	SearchApiLexer lexer = new SearchApiLexer(input);
	CommonTokenStream tokens = new CommonTokenStream(lexer);
	SearchApiParser parser = new SearchApiParser(tokens);
	SearchApiErrorListener errorHandler = new SearchApiErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorHandler);
	ParseTree tree = parser.query();
	
	if(errorHandler.getException() != null) { // Errors on syntax compilation.
            throw errorHandler.getException();
        }

	parserLoader = new SearchApiLoader<T>(parser, klass);
	ParseTreeWalker.DEFAULT.walk(parserLoader, tree);
	if(parserLoader.getException() != null){
	    throw parserLoader.getException();
	}
    }

    /**
     * Returns the created entire query for the current Class<T>
     */
    public String getMybatisEntireQuery() {
	return !StringUtils.isBlank(query) ? parserLoader.getEntireQuery() : null;
    }

    public String getMybatisSelectSection() {
	return !StringUtils.isBlank(query) ? parserLoader.getSelectSection() : null;
    }

    public String getMybatisFromSection() {
	return !StringUtils.isBlank(query) ? parserLoader.getFromSection() : null;
    }

    public String getMybatisWheresCondition() {
	return !StringUtils.isBlank(query) ? parserLoader.getWheresCondition() : null;
    }

    public String getMybatisOrderBySection() {
	return !StringUtils.isBlank(query) ? parserLoader.getOrderBySection() : null;
    }
}
