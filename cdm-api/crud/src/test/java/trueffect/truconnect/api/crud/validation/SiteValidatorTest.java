package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;


/**
 * Unit Tests for {@code SiteValidator} class
 * <p>
 * Created by marcelo.heredia on 4/26/2016.
 * @author Marcelo Heredia
 */
public class SiteValidatorTest {
    private SiteValidator validator;
    private Site site;
    private MediaRawDataView mediaRawDataView;
    private BeanPropertyBindingResult siteErrors;
    private BeanPropertyBindingResult mrdvErrors;

    @Before
    public void setUp() throws Exception {
        validator = new SiteValidator();
        site = EntityFactory.createSite();
        mediaRawDataView = EntityFactory.createMediaRawDataView();
        siteErrors = new BeanPropertyBindingResult(site,
                site.getClass().getSimpleName());
        mrdvErrors = new BeanPropertyBindingResult(mediaRawDataView,
                mediaRawDataView.getClass().getSimpleName());
    }

    @Test
    public void testSuccessSiteValidationForCreationWithAllCorrectFields() {
        site.setId(null);
        ValidationUtils.invokeValidator(validator, site, siteErrors);
        assertThat(siteErrors, is(notNullValue()));
        assertThat(siteErrors + "", siteErrors.hasErrors(), is(false));
        assertThat(siteErrors.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void testSuccessSiteValidationWithNoDefaultValues() {
        site.setId(null);
        site.setRichMedia(Constants.YES_FLAG);
        site.setAcceptsFlash(Constants.YES_FLAG);
        site.setClickTrack(Constants.YES_FLAG);
        site.setEncode(Constants.YES_FLAG);
        site.setPreferredTag(SiteValidator.TagType.SCRIPT.toString());
        ValidationUtils.invokeValidator(validator, site, siteErrors);
        assertThat(siteErrors, is(notNullValue()));
        assertThat(siteErrors + "", siteErrors.hasErrors(), is(false));
        assertThat(siteErrors.getErrorCount(), is(equalTo(0)));
        assertThat(site.getRichMedia(), is(equalTo(Constants.YES_FLAG)));
        assertThat(site.getAcceptsFlash(), is(equalTo(Constants.YES_FLAG)));
        assertThat(site.getClickTrack(), is(equalTo(Constants.YES_FLAG)));
        assertThat(site.getEncode(), is(equalTo(Constants.YES_FLAG)));
        assertThat(site.getPreferredTag(), is(equalTo(SiteValidator.TagType.SCRIPT.toString())));
    }

    @Test
    public void testSuccessSiteValidationAndSetDefaultValues() {
        site.setId(null);
        site.setRichMedia(null);
        site.setAcceptsFlash(null);
        site.setClickTrack(null);
        site.setEncode(null);
        site.setTargetWin(null);

        ValidationUtils.invokeValidator(validator, site, siteErrors);
        assertThat(siteErrors, is(notNullValue()));
        assertThat(siteErrors + "", siteErrors.hasErrors(), is(false));
        assertThat(siteErrors.getErrorCount(), is(equalTo(0)));
        assertThat(site.getRichMedia(), is(equalTo(Constants.NO_FLAG)));
        assertThat(site.getAcceptsFlash(), is(equalTo(Constants.NO_FLAG)));
        assertThat(site.getClickTrack(), is(equalTo(Constants.NO_FLAG)));
        assertThat(site.getEncode(), is(equalTo(Constants.NO_FLAG)));
        assertThat(site.getTargetWin(), is(equalTo(SiteValidator.Target._BLANK.name().toLowerCase())));
    }

    @Test
    public void testFailedSiteValidationForCreationWithAllIncorrectFields() {
        site.setId(EntityFactory.random.nextLong());
        site.setPublisherId(null);
        site.setName(null);
        site.setPublisherNotes(null);
        site.setUrl(EntityFactory.faker.lorem().fixedString(257));
        site.setPreferredTag(EntityFactory.faker.letterify("????"));
        site.setClickTrack(EntityFactory.faker.letterify("????"));
        site.setRichMedia(EntityFactory.faker.letterify("????"));
        site.setAcceptsFlash(EntityFactory.faker.letterify("acceptsFlash"));
        site.setTargetWin(EntityFactory.faker.letterify("acceptsFlash"));
        site.setEncode(EntityFactory.faker.letterify("????"));
        ValidationUtils.invokeValidator(validator, site, siteErrors);
        assertThat(siteErrors, is(notNullValue()));
        assertThat(siteErrors.hasErrors(), is(true));
        assertThat(siteErrors + "", siteErrors.getErrorCount(), is(equalTo(10)));

        FieldError fieldError = siteErrors.getFieldError("id");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Site should not have the id field. If you are trying to update an existing Site use PUT service.")));

        fieldError = siteErrors.getFieldError("publisherId");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid publisherId, it cannot be empty.")));

        fieldError = siteErrors.getFieldError("name");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid name, it cannot be empty.")));

        fieldError = siteErrors.getFieldError("url");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid url, it supports characters up to 256.")));

        fieldError = siteErrors.getFieldError("preferredTag");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid preferredTag, it should be one of [IFRAME, SCRIPT, IMAGE, HREF].")));

        fieldError = siteErrors.getFieldError("clickTrack");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid clickTrack, it should be one of [Y, N].")));

        fieldError = siteErrors.getFieldError("richMedia");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid richMedia, it should be one of [Y, N].")));

        fieldError = siteErrors.getFieldError("acceptsFlash");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid acceptsFlash, it should be one of [Y, N].")));

        fieldError = siteErrors.getFieldError("encode");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid encode, it should be one of [Y, N].")));

        fieldError = siteErrors.getFieldError("targetWin");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid targetWin, it should be one of [_top, _blank].")));
    }

    @Test
    public void testSuccessSiteValidationForImport() {
        mediaRawDataView.setSite(EntityFactory.faker.letterify("??????"));
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(false));
    }

    @Test
    public void testFailedSiteValidationForImportNameRequired() {
        mediaRawDataView.setSite(null);
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = mrdvErrors.getFieldError("site");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Site field is required")));
    }

    @Test
    public void testFailedSiteValidationForImportNameMaxLength() {
        mediaRawDataView.setSite(EntityFactory.faker.lorem().fixedString(257));
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = mrdvErrors.getFieldError("site");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid Site. Max character limit is 256")));
    }

    @Test
    public void testSuccessSiteApplyDefaults() {
        site = new Site();
        site.setName(EntityFactory.faker.letterify("??????"));
        site = validator.applyDefaults(site);

        assertThat(site, is(notNullValue()));
        assertThat(site.getId(), is(nullValue()));
        assertThat(site.getPublisherId(), is(nullValue()));
        assertThat(site.getName(), is(notNullValue()));
        assertThat(site.getDupName(), is(nullValue()));
        assertThat(site.getUrl(), is(nullValue()));
        assertThat(site.getPreferredTag(), is(equalTo("IFRAME")));
        assertThat(site.getRichMedia(), is("N"));
        assertThat(site.getAcceptsFlash(), is("N"));
        assertThat(site.getClickTrack(), is("N"));
        assertThat(site.getEncode(), is("N"));
        assertThat(site.getTargetWin(), is("_blank"));
        assertThat(site.getAgencyNotes(), is(nullValue()));
        assertThat(site.getPublisherNotes(), is(nullValue()));
        assertThat(site.getLogicalDelete(), is(nullValue()));
        assertThat(site.getCreatedTpwsKey(), is(nullValue()));
        assertThat(site.getModifiedTpwsKey(), is(nullValue()));
        assertThat(site.getCreatedDate(), is(nullValue()));
        assertThat(site.getModifiedDate(), is(nullValue()));
        assertThat(site.getExternalId(), is(nullValue()));
        assertThat(site.getPublisherName(), is(nullValue()));
    }

    @Test
    public void testFailedValidationInvalidCharactersOnName() {

        site.setId(null);
        site.setName("Site (1) Name(ñ");

        ValidationUtils.invokeValidator(validator, site, siteErrors);

        assertThat(siteErrors, is(notNullValue()));
        assertThat(siteErrors.hasErrors(), is(true));
        assertThat(siteErrors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = siteErrors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getField(), is(equalTo("name")));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid characters on Site name")));
    }
}