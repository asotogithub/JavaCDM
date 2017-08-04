package trueffect.truconnect.api.search;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.util.TableMappingUtil;
import trueffect.truconnect.api.search.SearchApiParser.OrderTermContext;
import trueffect.truconnect.api.search.SearchApiParser.RelationContext;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class SearchApiLoader<T> extends SearchApiBaseListener {

    private List<String> ruleNames;
    private StringBuilder conditionBuilder;
    private StringBuilder orderBuilder;
    private HashMap<String, String> terminalEquivalents;
    private List<String> likeConditions;
    private List<String> ids;
    private SearchApiException exception;

    private Class<T> klass;

    public SearchApiLoader(Parser parser, Class<T> klass) {
        this(Arrays.asList(parser.getRuleNames()));
        this.klass = klass;
    }

    public SearchApiLoader(List<String> ruleNames) {
        this.ruleNames = ruleNames;
        conditionBuilder = new StringBuilder();
        orderBuilder = new StringBuilder();
        terminalEquivalents = new HashMap<String, String>();
        terminalEquivalents.put("(", "(");
        terminalEquivalents.put(")", ")");
        terminalEquivalents.put("or", "or");
        terminalEquivalents.put("and", "and");
        terminalEquivalents.put("[", "(");
        terminalEquivalents.put(",", ",");
        terminalEquivalents.put("]", ")");
        terminalEquivalents.put("not equals to", "!=");
        terminalEquivalents.put("equals to", "=");
        terminalEquivalents.put("greater than or equals to", ">=");
        terminalEquivalents.put("greater than", ">");
        terminalEquivalents.put("less than or equals to", "<=");
        terminalEquivalents.put("less than", "<");
        terminalEquivalents.put("is", "is");
        terminalEquivalents.put("is not", "is not");
        terminalEquivalents.put("=", "=");
        terminalEquivalents.put("!=", "!=");
        terminalEquivalents.put(">", ">");
        terminalEquivalents.put(">=", ">=");
        terminalEquivalents.put("<", "<");
        terminalEquivalents.put("<=", "<=");
        terminalEquivalents.put("between", "between");
        terminalEquivalents.put("not between", "not between");
        terminalEquivalents.put("inside", "in");
        terminalEquivalents.put("not inside", "not in");
        terminalEquivalents.put("in", "in");
        terminalEquivalents.put("not in", "not in");

        // for Ordering
        terminalEquivalents.put("ordering", "order by");
        terminalEquivalents.put("sorting", "order by");
        terminalEquivalents.put("asc", "asc");
        terminalEquivalents.put("desc", "desc");

        likeConditions = new ArrayList<String>();
        likeConditions.add("does not contain");
        likeConditions.add("contains");
        likeConditions.add("does not start with");
        likeConditions.add("starts with");
        likeConditions.add("does not end with");
        likeConditions.add("ends with");
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        if (conditionBuilder.length() > 0) {
            conditionBuilder.append(' ');
        }

        // 1.- Check for Terminal values on no RELATION rules
        ParserRuleContext parent = null;
        if (node.getParent() instanceof ParserRuleContext) {
            parent = (ParserRuleContext) node.getParent();
        }
        String ruleName = getRuleName(parent);

        String nodeText = Trees.getNodeText(node, ruleNames);
        if ("order".equals(ruleName)) {
            orderBuilder.append(Utils.escapeWhitespace(getTerminalEquivalent(nodeText), false));
        } else if (!"relation".equals(ruleName) && !"orderTerm".equals(ruleName)) {
            conditionBuilder.append(Utils.escapeWhitespace(getTerminalEquivalent(nodeText), false));
        }
    }

    private String getTerminalEquivalent(String nodeText) {
        String result = terminalEquivalents.get(nodeText);
        if (StringUtils.isBlank(result)) {
            setException("The query does not match with the grammar.");
        }
        return !StringUtils.isBlank(result) ? result : "";
    }

    @Override
    public void enterOrderTerm(@NotNull OrderTermContext ctx) {
        if (orderBuilder.length() > 0) {
            orderBuilder.append(' ');
        }

        String identifier = ctx.getChild(0).getText();
        orderBuilder.append(Utils.escapeWhitespace(getIdentifierEquivalent(identifier), false));
        orderBuilder.append(' ');
        if (ctx.getChildCount() > 1) {
            orderBuilder
                    .append(Utils.escapeWhitespace(getTerminalEquivalent(ctx.getChild(1).getText()),
                                                   false));
            orderBuilder.append(' ');
        }
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        if (conditionBuilder.length() > 0) {
            conditionBuilder.append(' ');
        }
        conditionBuilder.append(Utils.escapeWhitespace(Trees.getNodeText(node, ruleNames), false));
    }

    @Override
    public void enterRelation(@NotNull RelationContext ctx) {
        if (conditionBuilder.length() > 0) {
            conditionBuilder.append(' ');
        }

        String identifier = ctx.getChild(0).getText();
        String equivalentIdentifier = Utils.escapeWhitespace(getIdentifierEquivalent(identifier),
                                                             false);
        if (ctx.getChildCount() == 3) { // Basic Relations
            if (likeConditions.contains(ctx.getChild(1).getText())) { // LIKE conditions
                createLikeCondition(equivalentIdentifier, ctx.getChild(1).getText(),
                                    getValueEquivalent(identifier, ctx.getChild(2).getText()));
            } else {
                conditionBuilder.append(equivalentIdentifier);
                conditionBuilder.append(' ');
                conditionBuilder.append(Utils.escapeWhitespace(
                        getTerminalEquivalent(ctx.getChild(1).getText()), false));
                conditionBuilder.append(' ');
                conditionBuilder.append(Utils.escapeWhitespace(
                        getValueEquivalent(identifier, ctx.getChild(2).getText()), false));
            }
        } else {
            conditionBuilder.append(equivalentIdentifier);
            conditionBuilder.append(' ');
            conditionBuilder.append(Utils.escapeWhitespace(
                    getTerminalEquivalent(ctx.getChild(1).getText()), false));
            conditionBuilder.append(' ');
            if ("between".equals(ctx.getChild(1).getText()) || "not between"
                    .equals(ctx.getChild(1).getText())) {
                conditionBuilder.append(Utils.escapeWhitespace(
                        getValueEquivalent(identifier, ctx.getChild(2).getText()), false));
                conditionBuilder.append(' ');
                conditionBuilder.append(Utils.escapeWhitespace(
                        getTerminalEquivalent(ctx.getChild(3).getText()), false));
                conditionBuilder.append(' ');
                conditionBuilder.append(Utils.escapeWhitespace(
                        getValueEquivalent(identifier, ctx.getChild(4).getText()), false));
            } else {
                // Check for the list INSIDE
                int total = ctx.getChildCount();
                ids = new ArrayList<String>();
                conditionBuilder.append(Utils.escapeWhitespace(
                        getTerminalEquivalent(ctx.getChild(2).getText()), false));
                conditionBuilder.append(' ');
                for (int i = 3; i < total - 1; i++) {
                    if (i % 2 != 0) {
                        ids.add(getValueEquivalent(identifier, ctx.getChild(i).getText()));
                        conditionBuilder.append(Utils.escapeWhitespace(
                                getValueEquivalent(identifier, ctx.getChild(i).getText()), false));
                    } else {
                        conditionBuilder.append(Utils.escapeWhitespace(
                                getTerminalEquivalent(ctx.getChild(i).getText()), false));
                    }
                    conditionBuilder.append(' ');
                }
                conditionBuilder.append(Utils.escapeWhitespace(
                        getTerminalEquivalent(ctx.getChild(total - 1).getText()), false));
                conditionBuilder.append(' ');
            }
        }
    }

    private void createLikeCondition(String identifier, String operator, String param) {
        param = param.substring(1, param.length() - 1);
        boolean
                hasNegation =
                "does not contain".equals(operator) || "does not start with".equals(operator)
                || "does not end with".equals(operator);
        String searchParam = "";
        if ("does not contain".equals(operator) || "contains".equals(operator)) {
            searchParam = Utils.escapeWhitespace("'" + param + "'", false);
        } else if ("does not start with".equals(operator) || "starts with".equals(operator)) {
            searchParam = Utils.escapeWhitespace("'^" + param + "'", false);
        } else if ("does not end with".equals(operator) || "ends with".equals(operator)) {
            searchParam = Utils.escapeWhitespace("'" + param + "$'", false);
        }

        if (hasNegation) {
            conditionBuilder.append(" NOT ");
        }
        conditionBuilder.append(" REGEXP_LIKE(");
        conditionBuilder.append(identifier);
        conditionBuilder.append(", ");
        conditionBuilder.append(searchParam);
        conditionBuilder.append(", 'i') ");
    }

    /**
     * Creates a SELECT clause with the fields described on the current Class<T>.
     *
     * @return A SELECT clause.
     */
    public String getSelectSection() {
        StringBuilder res = new StringBuilder();
        TableMappingUtil<T> util = new TableMappingUtil<T>(klass);
        res.append(" SELECT ");

        HashMap<String, String> columns = util.getColumns();
        boolean isFirst = true;
        for (Entry<String, String> item : columns.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                res.append(", ");
            }
            res.append(item.getValue() + " as " + item.getKey());
        }

        return res.toString();
    }

    /**
     * Creates a FROM clause based on the Tables described on current Class<T>.
     *
     * @return A FROM clause.
     */
    public String getFromSection() {
        StringBuilder res = new StringBuilder();
        TableMappingUtil<T> util = new TableMappingUtil<T>(klass);
        res.append(" FROM ");

        boolean isFirst = true;
        for (String table : util.getTables()) {
            if (isFirst) {
                isFirst = false;
            } else {
                res.append(", ");
            }
            res.append(table);
        }

        return res.toString();
    }

    public String getWhereSection() {
        StringBuilder res = new StringBuilder();
        res.append(" WHERE ");
        res.append(this.getWheresCondition());
        return res.toString().replaceAll("\"", "'");
    }

    public String getWheresCondition() {
        return conditionBuilder.toString().replaceAll("\"", "'");
    }

    public String getOrderBySection() {
        return orderBuilder.toString().replaceAll("\"", "'");
    }

    public String getEntireQuery() {
        StringBuilder res = new StringBuilder();
        res.append(this.getSelectSection());
        res.append(" ");
        res.append(this.getFromSection());
        res.append(" ");
        res.append(this.getWhereSection());
        return res.toString().replaceAll("\"", "'");
    }

    public List<String> getIds() {
        return this.ids;
    }

    private String getRuleName(ParserRuleContext rule) {
        int ruleIndex = rule.getRuleIndex();
        String ruleName;
        if (ruleIndex >= 0 && ruleIndex < ruleNames.size()) {
            ruleName = ruleNames.get(ruleIndex);
        } else {
            ruleName = Integer.toString(ruleIndex);
        }
        return ruleName;
    }

    private String getIdentifierEquivalent(String identifier) {
        TableMappingUtil<T> util = new TableMappingUtil<T>(klass);
        try {
            return util.getEquivalentFieldName(identifier);
        } catch (SearchApiException e) {
            setException(e);
        }
        return "";
    }

    private String getValueEquivalent(String identifier, String value) {
        TableMappingUtil<T> util = new TableMappingUtil<T>(klass);
        try {
            return util.getEquivalentValueForFieldName(identifier, value);
        } catch (SearchApiException e) {
            setException(e);
        }
        return "";
    }

    public SearchApiException getException() {
        return exception;
    }

    protected void setException(String message) {
        this.exception = new SearchApiException(message);
    }

    protected void setException(SearchApiException exception) {
        this.exception = exception;
    }
}
