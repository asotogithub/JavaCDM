package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static trueffect.truconnect.api.crud.EntityFactory.createCreativeGroup;
import static trueffect.truconnect.api.crud.EntityFactory.createGeoTarget;

import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.GeoTarget;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

import java.util.Collections;

public class CreativeGroupValidatorTest {

    private CreativeGroupValidator validator;
    private String objToValidateClassName;

    @Before
    public void init() {
        validator = new CreativeGroupValidator();
        objToValidateClassName = CreativeGroup.class.getSimpleName();
    }

    @Test
    public void testThatAGeoTargetIsProvidedIfGeoTargetingIsEnabled() {
        GeoTarget geoTarget = createGeoTarget();
        CreativeGroup creativeGroup = createCreativeGroup();
        creativeGroup.setDoGeoTargeting(1L);
        creativeGroup.setGeoTargets(Collections.singletonList(geoTarget));

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(creativeGroup, objToValidateClassName);

        validator.validatePUT(creativeGroup, creativeGroup.getId(), result);

        assertThat(result.getFieldErrorCount("geoTargets"), is(equalTo(0)));
    }

    @Test
    public void testThatAnErrorExistsWhenGeoTargetingIsEnabledAndNoGeoTargetIsProvided() {
        CreativeGroup creativeGroup = createCreativeGroup();
        creativeGroup.setDoGeoTargeting(1L);

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(creativeGroup, objToValidateClassName);

        validator.validatePUT(creativeGroup, creativeGroup.getId(), result);

        assertThat(result.getFieldErrorCount("geoTargets"), is(equalTo(1)));
    }

    @Test
    public void testCreativeGroupFail() throws Exception {
        CreativeGroup creativeGroup = createCreativeGroup();
        creativeGroup.setName("´fail");

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(creativeGroup, objToValidateClassName);

        validator.validatePUT(creativeGroup, creativeGroup.getId(), result);
        assertThat(result.getFieldErrorCount("name"), is(equalTo(1)));
    }

    @Test
    public void testCreativeGroupPass() throws Exception {
        CreativeGroup creativeGroup = createCreativeGroup();

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(creativeGroup, objToValidateClassName);

        validator.validatePUT(creativeGroup, creativeGroup.getId(), result);
        assertThat(result.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void testCreativeGroupNameFail() throws Exception {
        CreativeGroup creativeGroup = createCreativeGroup();

        creativeGroup.setId(null);
        creativeGroup.setName(EntityFactory.faker.lorem().fixedString(129));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeGroup,
                objToValidateClassName);
        ValidationUtils.invokeValidator(validator, creativeGroup, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("name"));
        assertThat(error.getDefaultMessage(), is("Invalid name, it supports characters up to 128."));
    }

    @Test
    public void testCreativeGroupNamePass() throws Exception {
        CreativeGroup creativeGroup = createCreativeGroup();

        creativeGroup.setId(null);
        creativeGroup.setName(EntityFactory.faker.lorem().fixedString(128));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeGroup,
                objToValidateClassName);
        ValidationUtils.invokeValidator(validator, creativeGroup, errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));
    }
}
