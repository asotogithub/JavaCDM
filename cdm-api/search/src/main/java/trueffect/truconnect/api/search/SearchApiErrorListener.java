package trueffect.truconnect.api.search;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;

public class SearchApiErrorListener extends BaseErrorListener {

    private String errorMessage = "";
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line, int charPositionInLine,
            String msg,
            RecognitionException e)
    {
        List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
        Collections.reverse(stack);
        errorMessage += "line " + line + ":" + charPositionInLine + " " + msg + ". ";
    }
    
    public SearchApiException getException(){
        if("".equals(errorMessage)) {
            return null;
        }
        return new SearchApiException("The query does not match with the grammar. " + errorMessage);
    }
}
