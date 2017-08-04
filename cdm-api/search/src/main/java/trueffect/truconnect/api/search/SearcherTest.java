package trueffect.truconnect.api.search;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import trueffect.truconnect.api.commons.model.Publisher;

public class SearcherTest {
    protected static final String originalQuery = "city in [\"Seattle\", \"test\"]";	
    public static void main(String[] args) {
        ANTLRInputStream input = new ANTLRInputStream(originalQuery);
        SearchApiLexer lexer = new SearchApiLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SearchApiParser parser = new SearchApiParser(tokens);
        SearchApiErrorListener errorHandler = new SearchApiErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorHandler);
        ParseTree tree = parser.query();
        if(errorHandler.getException() != null) {
            System.out.println(errorHandler.getException());
        }
        SearchApiLoader<Publisher> listener = new SearchApiLoader<Publisher>(parser, Publisher.class);
        ParseTreeWalker.DEFAULT.walk(listener, tree);
        System.out.println(listener.getWhereSection());
        System.out.println(listener.getOrderBySection());
    }
}
