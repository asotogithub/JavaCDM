package trueffect.truconnect.api.commons.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableFieldMapping {
	String table() default "";
	String field() default "";
	FieldValueMapping[] valueMappings() default {};
}
