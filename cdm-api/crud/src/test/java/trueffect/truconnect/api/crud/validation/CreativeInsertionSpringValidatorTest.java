package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.service.CreativeManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Unit Tests for CreativeInsertionSpringValidator
 * Created by marcelo.heredia on 2/17/2016.
 */
public class CreativeInsertionSpringValidatorTest {
    private CreativeInsertionSpringValidator validator;
    private CreativeInsertion creativeInsertion;
    private Creative creative;

    @Before
    public void setUp() {
        validator = new CreativeInsertionSpringValidator();
        creativeInsertion = EntityFactory.createCreativeInsertion();
        creative = EntityFactory.createCreative();
    }

    @Test
    public void testValidateValidClickthrough()  {
        for(String url: EntityFactory.VALID_CLICKTHROUGHS) {
            creativeInsertion.setClickthrough(url);
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    creativeInsertion,
                    creativeInsertion.getClass().getSimpleName());
            validator.validateClickthroughUpdate(creativeInsertion, errors);
            assertThat(String.format("Clickthrough URL [%s] is invalid", url), errors.hasErrors(), is(false));
        }
    }

    @Test
    public void testValidateWithValidClickthroughs()  {
        creativeInsertion.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creativeInsertion.setClickthroughs(EntityFactory.createClickthroughList(10));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                creativeInsertion.getClass().getSimpleName());
        validator.validateClickthroughUpdate(creativeInsertion, errors);
        assertThat(String.format("Clickthrough URLs [%s] are invalid",
                creativeInsertion.getClickthrough()), errors.hasErrors(), is(false));
    }

    @Test
    public void testValidateWithInvalidClickthrough()  {
        for(String url: EntityFactory.INVALID_CLICKTHROUGHS) {
            creativeInsertion.setClickthrough(url);
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    creativeInsertion,
                    creativeInsertion.getClass().getSimpleName());
            validator.validateClickthroughUpdate(creativeInsertion, errors);
            assertThat(errors.getErrorCount(), is(1));
            FieldError error = errors.getFieldErrors().get(0);
            assertThat(error.getField(), is("clickthrough"));
            assertThat(error.getDefaultMessage(),
                    is(String.format("URL '%s' is invalid; " +
                            "a valid URL should start with either http:// or https:// " +
                            "and it should be a well formed URL", creativeInsertion.getClickthrough())));
        }
    }

    @Test
    public void testValidateForUpdateWithInvalidClickthroughs() {
        creativeInsertion.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creativeInsertion.setClickthroughs(EntityFactory.createClickthroughList(EntityFactory.INVALID_CLICKTHROUGHS));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                creativeInsertion.getClass().getSimpleName());
        validator.validateClickthroughUpdate(creativeInsertion, errors);
        assertThat(errors.getErrorCount(), is(18));
        int i = 0;
        for(FieldError error : errors.getFieldErrors()){
            assertThat(error.getField(), is("clickthroughs[" + (i++) + "].url"));
            assertThat(error.getDefaultMessage(),
                    is(String.format("URL '%s' is invalid; " +
                            "a valid URL should start with either http:// or https:// " +
                            "and it should be a well formed URL", error.getRejectedValue())));
        }
    }

    @Test
    public void validateClickthroughIsUnallowedFor3rd() throws Exception {
        // Define invalid clickthrough for 3RD Creative Type
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creativeInsertion.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughUpdate(creativeInsertion, errors);
        // validateClickthroughUpdate
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("clickthrough"));
        assertThat(error.getDefaultMessage(),
                is("3rd Creatives cannot have either primary or additional Clickthroughs."));
    }

    @Test
    public void validateCreateJpgOk() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());
        creativeInsertion.setClickthrough(null);
        creative.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(0));
        assertThat(creativeInsertion.getClickthrough(), is(EntityFactory.VALID_CLICKTHROUGHS[0]));
    }

    @Test
    public void validateCreateJpgWithClickThroughsFail() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());
        creativeInsertion.setClickthrough(null);
        List<Clickthrough> clicks = EntityFactory.createClickthroughList(2);
        creativeInsertion.setClickthroughs(clicks);
        creative.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("clickthroughs"));
        assertThat(error.getDefaultMessage(),
                is("Only ZIP and HTML types allow additional Clickthroughs."));
    }
    
    @Test
    public void validateCreate3rdOk() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creativeInsertion.setClickthrough(null);
        creative.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative.setClickthrough(null);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(0));
    }

    @Test
    public void validateCreate3rdWithClickThroughFail() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creativeInsertion.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creativeInsertion.setClickthroughs(null);
        creative.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creative.setClickthroughs(null);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("clickthrough"));
        assertThat(error.getDefaultMessage(),
                is("3rd Creatives cannot have either primary or additional Clickthroughs."));
    }

    @Test
    public void validateCreate3rdWithClickThroughsFail() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creativeInsertion.setClickthrough(null);
        List<Clickthrough> clicks = EntityFactory.createClickthroughList(2);
        creativeInsertion.setClickthroughs(clicks);
        creative.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative.setClickthrough(null);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("clickthroughs"));
        assertThat(error.getDefaultMessage(),
                is("3rd Creatives cannot have either primary or additional Clickthroughs."));
    }
    
    @Test
    public void validateCreateZipOk() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creativeInsertion.setClickthrough(null);
        creative.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(0));
        assertThat(creativeInsertion.getClickthrough(), is(EntityFactory.VALID_CLICKTHROUGHS[0]));
    }
    
    @Test
    public void validateCreateZipSetClickthroughOk() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creativeInsertion.setClickthrough(null);
        creative.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(0));
        assertThat(creativeInsertion.getClickthrough(), is(EntityFactory.VALID_CLICKTHROUGHS[0]));
    }

    @Test
    public void validateCreateZipWithClickthroughNoClickthroughsOk() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creativeInsertion.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creativeInsertion.setClickthroughs(null);
        creative.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[1]);
        creative.setClickthroughs(null);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(0));
        assertThat(creativeInsertion.getClickthrough(), is(EntityFactory.VALID_CLICKTHROUGHS[0]));
    }
    
    @Test
    public void validateCreateZipWithClickthroughNoClickthroughsFail() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creativeInsertion.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creativeInsertion.setClickthroughs(null);
        creative.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[1]);
        List<Clickthrough> clicks = EntityFactory.createClickthroughList(2);
        creative.setClickthroughs(clicks);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("clickthroughs"));
        assertThat(error.getDefaultMessage(),
                is("Number of provided Clickthroughs mismatch number of Creative Clickthroughs."));

    }

    @Test
    public void validateCreateZipWithClickthroughsNoClickthroughFail() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creativeInsertion.setClickthrough(null);
        List<Clickthrough> clicks = EntityFactory.createClickthroughList(2);
        creativeInsertion.setClickthroughs(clicks);
        creative.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("clickthroughs"));
        assertThat(error.getDefaultMessage(),
                is("Number of provided Clickthroughs mismatch number of Creative Clickthroughs."));
    }
    
    @Test
    public void validateCreateZipWithClickthroughsAndClickthroughMismatchFail() throws Exception {
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creativeInsertion.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        List<Clickthrough> clicks = EntityFactory.createClickthroughList(5);
        creativeInsertion.setClickthroughs(clicks);
        creative.setCreativeType(CreativeManager.CreativeType.ZIP.getCreativeType());
        creative.setClickthrough(EntityFactory.VALID_CLICKTHROUGHS[0]);
        creativeInsertion.setClickthroughs(clicks.subList(0, 4));

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthroughCreate(creativeInsertion, creative, errors);
        // validateClickthroughCreate
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("clickthroughs"));
        assertThat(error.getDefaultMessage(),
                is("Number of provided Clickthroughs mismatch number of Creative Clickthroughs."));
    }
}
