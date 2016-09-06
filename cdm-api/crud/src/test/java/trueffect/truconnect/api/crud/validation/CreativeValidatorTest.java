package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.service.CreativeManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

/**
 * Unit Tests for CreativeValidator
 * Created by marcelo.heredia on 2/17/2016.
 */
public class CreativeValidatorTest {

    private CreativeValidator validator;
    private Creative creative;

    @Before
    public void setUp() {
        validator = new CreativeValidator();
        creative = EntityFactory.createCreative();
    }

    @Test
    public void testValidateForUpdateWithValidClickthrough()  {
        for(String url: EntityFactory.VALID_CLICKTHROUGHS) {
            creative.setClickthrough(url);
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    creative,
                    creative.getClass().getSimpleName());
            validator.validateForUpdate(creative, creative.getId(), errors);
            assertThat(String.format("Clickthrough URL [%s] is invalid", url), errors.hasErrors(), is(false));
        }
    }

    @Test
    public void testValidateForUpdateWithValidClickthroughs()  {
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creative.setClickthroughs(EntityFactory.createClickthroughList(10));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
            creative,
            creative.getClass().getSimpleName());
        validator.validateForUpdate(creative, creative.getId(), errors);
        assertThat(String.format("Clickthrough URLs [%s] are invalid", creative.getClickthroughs()), errors.hasErrors(), is(false));
    }

    @Test
    public void testValidateForUpdateWithInvalidClickthrough()  {

        for(String url: EntityFactory.INVALID_CLICKTHROUGHS) {
            creative.setClickthrough(url);
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    creative,
                    creative.getClass().getSimpleName());
            validator.validateForUpdate(creative, creative.getId(), errors);
            assertThat(errors.getErrorCount(), is(1));
            FieldError error = errors.getFieldErrors().get(0);
            assertThat(error.getField(), is("clickthrough"));
            assertThat(error.getDefaultMessage(), is("A valid clickthrough should start with either http:// or https://."));
        }
    }

    @Test
    public void testValidateForUpdateWithInvalidClickthroughs() {
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creative.setClickthroughs(EntityFactory.createClickthroughList(EntityFactory.INVALID_CLICKTHROUGHS));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creative,
                creative.getClass().getSimpleName());
        validator.validateForUpdate(creative, creative.getId(), errors);
        assertThat(errors.getErrorCount(), is(18));
        int i = 0;
        for(FieldError error : errors.getFieldErrors()){
            assertThat(error.getField(), is("clickthroughs[" + (i++) + "].url"));
            assertThat(error.getDefaultMessage(), is("A valid clickthrough should start with either http:// or https://."));
        }
    }

    @Test
    public void validateClickthroughIsUnallowedFor3rd() throws Exception {
        // Define invalid clickthrough for 3RD Creative Type
        creative.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creative,
                creative.getClass().getSimpleName());
        validator.validateForUpdate(creative,  creative.getId(), errors);

        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("clickthrough"));
        assertThat(error.getDefaultMessage(),
                is("Clickthrough should be empty to update 3rd type Creative or CreativeInsertion."));
    }
}