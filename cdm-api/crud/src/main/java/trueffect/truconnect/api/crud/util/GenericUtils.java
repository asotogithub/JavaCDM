package trueffect.truconnect.api.crud.util;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by jfrylings on 6/27/16.
 */
public class GenericUtils {

    /**
     * This takes the non-null fields from the source object and copies them over the values in the destination object
     * @param destinationObj
     * @param sourceObj
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copyOnlyPopulatedFields(Object destinationObj, Object sourceObj) throws IllegalAccessException, InvocationTargetException {
        new BeanUtilsBean() {
            @Override
            public void copyProperty(Object destinationObj, String name, Object sourceValue)
                    throws IllegalAccessException, InvocationTargetException {
                if(sourceValue != null) {
                    super.copyProperty(destinationObj, name, sourceValue);
                }
            }
        }.copyProperties(destinationObj, sourceObj);
    }
}
