package trueffect.truconnect.api.crud.validation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;

/**
 * Created by marcelo.heredia on 6/27/2016.
 */
public class MediaRawDataViewValidatorTest {

    private BeanPropertyBindingResult errors;
    private MediaRawDataViewValidator validator;


    @Before
    public void setUp() throws Exception {
        validator = new MediaRawDataViewValidator(
                new PublisherValidator(),
                new InsertionOrderValidator(),
                new SiteValidator(),
                new SiteSectionValidator(),
                new SizeValidator(),
                new PackageValidator(),
                new PlacementValidator(),
                new CostDetailImportValidator()
        );
    }

    @Test
    public void testValidate() throws Exception {

    }
}