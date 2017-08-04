package trueffect.truconnect.api.commons.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import trueffect.truconnect.api.commons.annotations.FieldValueMapping;
import trueffect.truconnect.api.commons.annotations.TableFieldMapping;
import trueffect.truconnect.api.commons.exceptions.SearchApiException;

public class TableMappingUtil <T> {
    private Class<T> pojoClass;

    public TableMappingUtil(Class<T> klass) {
        pojoClass = klass;
    }

    public String getEquivalentFieldName(String pojoFieldName) throws SearchApiException {
        String res = null;
        for(Field field : pojoClass.getDeclaredFields()) {
            TableFieldMapping mappingAnnotation = (TableFieldMapping)field.getAnnotation(TableFieldMapping.class);
            if(mappingAnnotation != null && field.getName().equals(pojoFieldName)) {
                res = mappingAnnotation.table() + "." + mappingAnnotation.field();
                break;
            }
        }
        if(res == null) 
            throw new SearchApiException("Invalid field name: " + pojoFieldName + " for class: " + pojoClass);

        return res;
    }

    public String getEquivalentValueForFieldName(String pojoFieldName, String inputValue) throws SearchApiException {
        String res = inputValue;
        for(Field field : pojoClass.getDeclaredFields()) {
            TableFieldMapping mappingAnnotation = (TableFieldMapping)field.getAnnotation(TableFieldMapping.class);
            if(mappingAnnotation != null && field.getName().equals(pojoFieldName)) {
                // Get equivalent value
                FieldValueMapping[] mappingValues = mappingAnnotation.valueMappings();
                for (FieldValueMapping valueMapping : mappingValues) {
                    if(valueMapping.input().equals(inputValue)) {
                        res = valueMapping.output();
                        break;
                    }
                }
                
                //Checking if value is a valid data for the field unless the value is a CONSTANT.
                List<String> constants = Arrays.asList(new String[]{"null", "true", "false"});
                if(!constants.contains(inputValue)){
                    Class<?> type = field.getType();
                    try {
                        if (type.getSimpleName().compareTo("Long") == 0) {
                            Long.parseLong(res);
                        }
                        else if (type.getSimpleName().compareTo("Date") == 0) {
                            DateConverter.tryParseDate(res);
                        }
                        else if (type.getSimpleName().compareTo("String") == 0) {
                            if (!res.matches("\"([^\"]*)\"")) {
                                throw new Exception();
                            }
                        }
                        else if (type.getSimpleName().compareTo("Byte") == 0) {
                            Byte.valueOf(res);
                        }
                        else if (type.getSimpleName().compareTo("Character") == 0) {
                            Character.valueOf(res.charAt(0));
                        }
                    } catch (Exception e) {
                        throw new SearchApiException("The input value is not valid for \"" + pojoFieldName + "\" field.");
                    }
                }
            }
        }
        // For dates we should add " symbols.
        String dateTimeFullPattern = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(-|\\+)\\d{2}:\\d{2})";
        String dateTimePattern = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})";
        String datePattern = "(\\d{4}-\\d{2}-\\d{2})";
        if (inputValue.matches(dateTimeFullPattern)) {
            res = res.replaceAll("T", " ");
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
            Date date = null;
            try {
                date = parser.parse(res);
            } catch (ParseException e) {	}
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            res = formatter.format(date);
            res = "TO_DATE(\"" + res + "\", \"yyyy-mm-dd HH24:MI:SS\")";
        }
        else if (inputValue.matches(dateTimePattern)) {
            res = "TO_DATE(\"" + res.replaceAll("T", " ") + "\", \"yyyy-mm-dd HH24:MI:SS\")";
        }
        else if (inputValue.matches(datePattern)) {
            res = "TO_DATE(\"" + res.replaceAll("T", " ") + "\", \"yyyy-mm-dd\")";
        }

        return res;
    }

    public HashMap<String, String> getColumns() {
        HashMap<String, String> res = new HashMap<String, String>();
        for(Field field : pojoClass.getDeclaredFields()) {
            TableFieldMapping mappingAnnotation = (TableFieldMapping)field.getAnnotation(TableFieldMapping.class);
            res.put(field.getName(), mappingAnnotation.table() + "." + mappingAnnotation.field());
        }
        return res;
    }

    public List<String> getTables() {
        List<String> res = new ArrayList<String>();
        for(Field field : pojoClass.getDeclaredFields()) {
            TableFieldMapping mappingAnnotation = (TableFieldMapping)field.getAnnotation(TableFieldMapping.class);
            if(!res.contains(mappingAnnotation.table())) {
                res.add(mappingAnnotation.table());
            }
        }
        return res;
    }
}
