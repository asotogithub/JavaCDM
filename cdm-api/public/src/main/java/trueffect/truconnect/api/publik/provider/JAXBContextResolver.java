package trueffect.truconnect.api.publik.provider;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Custom JAXBContext resolver that uses Natural notation for JSON representations
 * Created by marcelo.heredia on 6/3/2015.
 *
 * @author Marcelo Heredia
 */

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

    private static final String PACKAGE_TO_SCAN = "trueffect.truconnect.api.commons.model";
    private JAXBContext context;
    private Set<Class> types;

    public JAXBContextResolver() throws JAXBException {
        types = new HashSet<>();
        // Note. Reflections is used in favor of constructor:
        // public JSONJAXBContext(JSONConfiguration config, java.lang.String contextPath) throws javax.xml.bind.JAXBException
        // as it requires the existence of a ObjectFactory or 'jaxb.index' file which will have to be maintained
        // with all JAXB annotated classes.
        // Thus, I switched to Reflections to take care of classes scanning automatically.
        Reflections reflections = new Reflections(PACKAGE_TO_SCAN);
        Set<Class<?>> annotatedXmlRoot = reflections.getTypesAnnotatedWith(XmlRootElement.class);
        types.addAll(annotatedXmlRoot);
        Set<Class<?>> annotatedXmlType = reflections.getTypesAnnotatedWith(XmlType.class);
        types.addAll(annotatedXmlType);
        this.context = new JSONJAXBContext(JSONConfiguration.natural().build(),
                                           types.toArray(new Class[types.size()]));
    }

    public JAXBContext getContext(Class<?> objectType) {
        for (Class type : types) {
            if (type == objectType) {
                return context;
            }
        }
        return null;
    }
}