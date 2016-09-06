package trueffect.truconnect.api.crud;

import com.trueffect.delivery.formats.adm.CookieDefault$;
import com.trueffect.delivery.formats.adm.DatasetConfig;
import com.trueffect.delivery.formats.adm.FailThroughDefaults;
import com.trueffect.delivery.formats.adm.KeyDefault$;
import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.ImportExportCellError;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CampaignCreatorContact;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.CookieTargetTemplate;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.CreativeGroupTarget;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.CreativeVersion;
import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.GeoTarget;
import trueffect.truconnect.api.commons.model.HtmlInjectionTagAssociation;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.PlacementStatus;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.Schedule;
import trueffect.truconnect.api.commons.model.ScheduleEntry;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.SiteContact;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.SmGroup;
import trueffect.truconnect.api.commons.model.Trafficking;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.UserDomain;
import trueffect.truconnect.api.commons.model.dto.BulkPublisherSiteSectionSize;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeView;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupDtoForCampaigns;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionBulkUpdate;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionExtendedView;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.dto.SiteContactView;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.commons.model.dto.adm.Cookie;
import trueffect.truconnect.api.commons.model.dto.adm.CookieList;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.model.enums.GeneralStatusEnum;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.model.enums.LocationType;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventPingTagType;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventPingType;
import trueffect.truconnect.api.commons.model.importexport.CostDetailRawDataView;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.crud.service.CreativeManager;
import trueffect.truconnect.api.crud.validation.DatasetConfigViewValidator;

import com.github.javafaker.Faker;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.immutable.Set;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/**
 *
 * Created by jgearheart on 6/2/15.
 */
public class EntityFactory {

    public static final Random random = new Random();
    public static final Faker faker = new Faker(random);
    public static final String[] INVALID_CLICKTHROUGHS = new String[]{
            "I am not a valid URL",
            "www.domain.com",
            "https://",
            "http://",
            "ftp://valid-ftp.com",
            "my@email.com",
            "123",
            // More URLs with Macro chars in hostname and domain (TA-4310) which are invalid
            "http://!@#$%^&*()=+{}[]|?~<>-_;:.google.com",
            "http://www.!@#$%^&*()=+{}[]|?~<>-_;:.com",
            "http://!@#$%^&*()=+{}[]|?~<>-_;:.!@#$%^&*()=+{}[]|?~<>-_;:.com",
            "http://www.goo(>gle.com",
            // More URLs with ',' in the resource path section (TA-3138)
            "http://www.example.com/wpstyle/?p=36,4",
            "http://www.google.com/abc,",
            "http://www.google.com/ab,c",
            "http://www.google.com/,abc",
            "https://us,er@test.com:test.com.ar/path",
            "https://user@te,st.com:test.com.ar/path",
            "https://us,er@te,st.com:test.com.ar/path"
    };

    public static final String[] VALID_CLICKTHROUGHS = new String[]{
            "http://foo.com/blah_blah",
            "http://foo.com/blah_blah/",
            "http://foo.com/blah_blah_(wikipedia)",
            "http://foo.com/blah_blah_(wikipedia)_(again)",
            "http://www.example.com/wpstyle/?p=364",
            "https://www.example.com/foo/?bar=baz&inga=42&quux",
            "http://✪df.ws/123",
            "http://userid:password@example.com:8080",
            "http://userid:password@example.com:8080/",
            "http://userid@example.com",
            "http://userid@example.com/",
            "http://userid@example.com:8080",
            "http://userid@example.com:8080/",
            "http://userid:password@example.com",
            "http://userid:password@example.com/",
            "http://142.42.1.1/",
            "http://142.42.1.1:8080/",
            "http://➡.ws/䨹",
            "http://⌘.ws",
            "http://⌘.ws/",
            "http://foo.com/blah_(wikipedia)#cite-1",
            "http://foo.com/blah_(wikipedia)_blah#cite-1",
            "http://foo.com/unicode_(✪)_in_parens",
            "http://foo.com/(something)?after=parens",
            "http://☺.damowmow.com/",
            "http://code.google.com/events/#&product=browser",
            "http://j.mp",
            "http://foo.bar/?q=Test%20URL-encoded%20stuff",
            "http://مثال.إختبار",
            "http://例子.测试",
            "http://उदाहरण.परीक्षा",
            "http://1337.net",
            "http://a.b-c.de",
            "http://223.255.255.254",
            "http://u--serid:password@example.com/",
            "http://a.b--c.de/",
            "http://www.foo.bar./",
            // URLs with Macro chars (TA-1424 )
            "http://www.foo.com/path?target=_blank&@CPSC@=",
            "http://www.foo.com/@@x?PlacementID@@",
            "http://www.foo.com/path?param=1#@@x?PlacementID@@",
            // More URLs wil Macro chars in hostname and domain (TA-4310)
            "http://www.host.domain@@MACRO@@.com/path?param=1#@@x?PlacementID@@",
            "http://www.host@@MACRO@@.domain.com/path?param=1#@@x?PlacementID@@",
            "http://!@#$%^&*=+{}[]|?~-_;:.google.com",
            "http://www.!@#$%^&*=+{}[]|?~-_;:.com",
            "http://!@#$%^&*=+{}[]|?~-_;:.!@#$%^&*=+{}[]|?~-_;:.com",
            //mixed uppercase
            "HTTP://www.foo.bar/",
            "http://WWW.foo.bar/",
            "http://www.FOO.bar/",
            "http://www.foo.BAR/",
            "HTTP://WWW.FOO.BAR/"
    };

    private EntityFactory() {
    }

    static public DatasetConfigView createDatasetConfigView() {
        DatasetConfigView result = new DatasetConfigView();
        result.setTtlExpirationSeconds(Math.abs(random.nextLong()));
        result.setAdvertiserId(Math.abs(random.nextLong()));
        result.setAdvertiserName(faker.letterify("??????"));
        result.setAgencyId(Math.abs(random.nextLong()));
        result.setAlias(faker.letterify("???????"));
        result.setCookieExpirationDays(random.nextInt(291)+1);
        List<String> cookieNames = new ArrayList<>();
        cookieNames.add(faker.letterify("?????"));
        cookieNames.add(faker.letterify("??????"));
        cookieNames.add(faker.letterify("???????"));
        result.setCookiesToCapture(new CookieList(random.nextBoolean(), cookieNames));
        result.setContentChannels(DatasetConfigViewValidator.SUPPORTED_CONTENT_CHANNELS);
        result.setDatasetId(UUID.randomUUID());
        result.setDomain(faker.internet().url());
        cookieNames = new ArrayList<>();
        cookieNames.add(faker.letterify("?????"));
        cookieNames.add(faker.letterify("??????"));
        cookieNames.add(faker.letterify("???????"));
        CookieList durableCookies = new CookieList(random.nextBoolean(), cookieNames);
        result.setDurableCookies(durableCookies);
        result.setFileNamePrefix(faker.letterify("???????"));
        result.setFtpAccount(faker.letterify("??????"));
        result.setMatchCookieName(faker.letterify("????"));
        result.setPath(faker.letterify("?????/?????"));
        result.setLatestUpdate(new Date());
        result.setFailThroughDefaults(new trueffect.truconnect.api.commons.model.dto.adm.FailThroughDefaults(
                random.nextBoolean(),
                KeyDefault$.MODULE$.productPrefix(),
                faker.letterify("??"),
                new ArrayList<Cookie>()
        ));
        return result;
    }

    static public DatasetConfig createDatasetConfig(Long agencyId) {
        String ftpAccount = "foo";
        String path = "Incoming/ADM";
        Set<String> objectSet = JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSet();
        ArrayList<com.trueffect.delivery.formats.adm.Cookie> cookies = new ArrayList<>();
        cookies.add(new com.trueffect.delivery.formats.adm.Cookie(faker.letterify("?????"), faker.letterify("?????")));
        cookies.add(new com.trueffect.delivery.formats.adm.Cookie(faker.letterify("?????"), faker.letterify("?????")));
        cookies.add(new com.trueffect.delivery.formats.adm.Cookie(faker.letterify("?????"), faker.letterify("?????")));

        FailThroughDefaults failThroughDefaults = new FailThroughDefaults(
                random.nextBoolean(),
                CookieDefault$.MODULE$,
                Option.apply(faker.letterify("???")),
                JavaConversions.asScalaBuffer(cookies).toSeq()
        );

        DatasetConfig config = new DatasetConfig(
                UUID.randomUUID(),
                agencyId,
                random.nextLong(),
                faker.internet().url(),
                faker.letterify("s3://ftp/" + ftpAccount + "/" + path),
                faker.letterify("?????"),
                random.nextInt(),
                JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq(),
                JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq(),
                random.nextLong(),
                new com.trueffect.delivery.formats.adm.CookieList<>(random.nextBoolean(), JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq()),
                new com.trueffect.delivery.formats.adm.CookieList<>(random.nextBoolean(), JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq()),
                Option.apply(faker.letterify("?????")),
                Option.apply(faker.letterify("?????")),
                random.nextBoolean(),
                Option.apply(DateTime.now()),
                objectSet,
                failThroughDefaults
        );
        return config;
    }

    static public List<DatasetConfig> createDatasetConfigList(int size) {
        List<DatasetConfig> views = new ArrayList<>(size);
        Long agencyId = random.nextLong();
        for (int i = 0; i < size; i++) {
            DatasetConfig datasetConfig = createDatasetConfig(agencyId);
            views.add(datasetConfig);
        }
        return views;
    }

    static public DatasetConfig createDatasetConfig() {
        return createDatasetConfig(random.nextLong());
    }

    static public List<Clickthrough> createClickthroughList(int size) {
        List<Clickthrough> clickthroughs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            clickthroughs.add(createClickthrough());
        }
        return clickthroughs;
    }

    static public List<Clickthrough> createClickthroughList(String[] URLS) {
        if(URLS == null) {
            return null;
        }
        List<Clickthrough> clickthroughs = new ArrayList<>(URLS.length);
        for (int i = 0; i < URLS.length; i++) {
            clickthroughs.add(createClickthrough(URLS[i]));
        }
        return clickthroughs;
    }

    static public Clickthrough createClickthrough() {
        Clickthrough c = new Clickthrough();
        c.setSequence(random.nextLong());
        c.setUrl(VALID_CLICKTHROUGHS[random.nextInt(VALID_CLICKTHROUGHS.length)]);
        return c;
    }

    static public Clickthrough createClickthrough(String url) {
        Clickthrough c = new Clickthrough();
        c.setSequence(random.nextLong());
        c.setUrl(url);
        return c;
    }

    static public String createEmailAddress() {
        return faker.letterify("????") + "." + faker.letterify("????") + "@" + faker.letterify("??????") + "." + faker.options().option(new String[]{"com", "net", "edu", "tv"});
    }

    static public List<PlacementView> createPlacementViewListForCampaign(Long campaignId, int size) {
        List<PlacementView> placementViews = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            placementViews.add(createPlacementView());
        }
        return placementViews;
    }

    static public List<PlacementView> createPlacementViewListForCampaign(Long campaignId, Long siteId, int size) {
        List<PlacementView> placementViews = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            PlacementView placement = createPlacementView();
            placement.setSiteId(siteId);
            placementViews.add(placement);
        }
        return placementViews;
    }

    static public PlacementFilterParam createPlacementFilterParam(PlacementFilterParamLevelTypeEnum levelType) {

        PlacementFilterParam result = new PlacementFilterParam();
        result.setLevelType(levelType.toString().toLowerCase());

        List<PlacementFilterParamLevelTypeEnum> hierarchyLevelTypes = PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_TO_LEVEL.get(levelType);
        for (PlacementFilterParamLevelTypeEnum typeEnum : hierarchyLevelTypes) {
            switch (typeEnum) {
                case CAMPAIGN:
                    result.setCampaignId(Math.abs(random.nextLong()));
                    break;
                case SITE:
                    result.setSiteId(Math.abs(random.nextLong()));
                    break;
                case SECTION:
                    result.setSectionId(Math.abs(random.nextLong()));
                    break;
                case PLACEMENT:
                    result.setPlacementId(Math.abs(random.nextLong()));
                    break;
            }
        }
        return result;
    }

    static public List<PlacementFilterParam> createPlacementFilterParam(int counter) {

        List<PlacementFilterParam> result = new ArrayList<>();
        List<PlacementFilterParamLevelTypeEnum> hierarchyLevel = PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_TO_LEVEL.
                get(PlacementFilterParamLevelTypeEnum.PLACEMENT);
        PlacementFilterParamLevelTypeEnum levelType;
        for (int i = 0; i < counter; i++) {
            levelType = hierarchyLevel.get(i % hierarchyLevel.size());
            result.add(createPlacementFilterParam(levelType));
        }
        return result;
    }

    static public PlacementActionTagAssocParam createPlacementActionTagAssocParam(
            PlacementFilterParamLevelTypeEnum levelType, String action ) {

        PlacementActionTagAssocParam result = new PlacementActionTagAssocParam();
        result.setLevelType(levelType.toString().toLowerCase());
        result.setHtmlInjectionId(Math.abs(random.nextLong()));
        result.setAction(action);

        List<PlacementFilterParamLevelTypeEnum> hierarchyLevelTypes =
                PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_TO_LEVEL.get(levelType);
        for (PlacementFilterParamLevelTypeEnum typeEnum : hierarchyLevelTypes) {
            switch (typeEnum) {
                case CAMPAIGN:
                    result.setCampaignId(Math.abs(random.nextLong()));
                    break;
                case SITE:
                    result.setSiteId(Math.abs(random.nextLong()));
                    break;
                case SECTION:
                    result.setSectionId(Math.abs(random.nextLong()));
                    break;
                case PLACEMENT:
                    result.setPlacementId(Math.abs(random.nextLong()));
                    break;
            }
        }
        return result;
    }

    static public List<PlacementActionTagAssocParam> createPlacementCreateTagAssocParam(int counter, String action) {

        List<PlacementActionTagAssocParam> result = new ArrayList<>();
        List<PlacementFilterParamLevelTypeEnum> hierarchyLevel =
                PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_TO_LEVEL.
                        get(PlacementFilterParamLevelTypeEnum.PLACEMENT);
        PlacementFilterParamLevelTypeEnum levelType;
        for (int i = 0; i < counter; i++) {
            levelType = hierarchyLevel.get(i % hierarchyLevel.size());
            result.add(EntityFactory.createPlacementActionTagAssocParam(levelType, action));
        }
        return result;
    }

    static public CreativeInsertionFilterParam createCreativeInsertionFilterForPivotAndLevelTypes(CreativeInsertionFilterParamTypeEnum pivotType, CreativeInsertionFilterParamTypeEnum levelType) {
        
        CreativeInsertionFilterParam result = new CreativeInsertionFilterParam();
        result.setType(levelType.toString());
        result.setPivotType(pivotType.toString());
        List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelTypes = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.get(pivotType).get(levelType);
        for (CreativeInsertionFilterParamTypeEnum typeEnum : hierarchyLevelTypes) {
            switch (typeEnum){
                case SITE:
                    result.setSiteId(Math.abs(random.nextLong()));
                    break;
                case SECTION:
                    result.setSectionId(Math.abs(random.nextLong()));
                    break;
                case PLACEMENT:
                    result.setPlacementId(Math.abs(random.nextLong()));
                    break;
                case GROUP:
                    result.setGroupId(Math.abs(random.nextLong()));
                    break;
                case CREATIVE:
                    result.setCreativeId(Math.abs(random.nextLong()));
                    break;
                case SCHEDULE:
                    result.setCreativeId(Math.abs(random.nextLong()));
                    break;
            }
        }
        return result;
    }
    
    static public List<CreativeInsertionFilterParam> createCreativeInsertionFilterForTypeAndPivot(int counter) {

        List<CreativeInsertionFilterParam> result = new ArrayList<>();
        List<CreativeInsertionFilterParamTypeEnum> listPivotTypes = CreativeInsertionFilterParamTypeEnum.PIVOT_TYPES;

        for (int i = 0; (i < counter && result.size() < counter); i++) {
            CreativeInsertionFilterParamTypeEnum pivotType = listPivotTypes.get(i%listPivotTypes.size());
            CreativeInsertionFilterParamTypeEnum lastLevelType = CreativeInsertionFilterParamTypeEnum.SCHEDULE;

            List<CreativeInsertionFilterParamTypeEnum> hierarchyLevel =
                    CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL
                            .get(pivotType).get(lastLevelType);
            CreativeInsertionFilterParamTypeEnum levelType;
            for (int j = 0; j < counter / listPivotTypes.size(); j++) {
                levelType = hierarchyLevel.get(j % hierarchyLevel.size());
                result.add(
                        createCreativeInsertionFilterForPivotAndLevelTypes(pivotType, levelType));
                if (result.size() == counter) {
                    break;
                }
            }
        }
        return result;
    }

    static public List<CreativeInsertionView> createCreativeInsertionViewListForCampaign(Long campaignId, int size, int sizeAdditionalClickthrough) {
        List<CreativeInsertionView> placementViews = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CreativeInsertionView civ = createCreativeInsertionView();
            civ.setCampaignId(campaignId);
            civ.setAdditionalClickthroughs(createClickthroughList(sizeAdditionalClickthrough));
            placementViews.add(civ);
        }
        return placementViews;
    }

    static public List<CreativeInsertionRawDataView> createCreativeInsertionImportViewList(int size) {
        List<CreativeInsertionRawDataView> cis = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            cis.add(createCreativeInsertionImportView());
        }
        return cis;
    }

    static public Map<String, CreativeInsertionRawDataView> createMapCreativeInsertionImportViewList(int size) {
        Map<String, CreativeInsertionRawDataView> result = new HashMap<>();
        for (int i = 0; i < size; i++) {
            CreativeInsertionRawDataView data = createCreativeInsertionImportView();
            data.setCreativeGroupId(Math.abs(random.nextLong()));
            result.put(data.getCreativeInsertionId(), data);
        }
        return result;
    }

    static public Agency createAgency() {
        Agency agency = new Agency();
        agency.setAddress1(faker.address().streetAddress(false));
        agency.setAddress2(faker.address().secondaryAddress());
        agency.setCity(faker.address().cityPrefix() + faker.address().citySuffix());
        agency.setContactDefault(faker.internet().emailAddress());
        agency.setCountry(faker.address().country());
        agency.setCreatedDate(new Date());
        agency.setDomainId(random.nextLong());
        agency.setEnableHtmlInjection(random.nextBoolean());
        agency.setFaxNumber(faker.phoneNumber().phoneNumber());
        agency.setId(random.nextLong());
        agency.setIsActive(true);
        agency.setModifiedDate(new Date());
        agency.setName(faker.name().name());
        agency.setNotes(faker.lorem().paragraph());
        agency.setPhoneNumber(faker.phoneNumber().phoneNumber());
        agency.setState(faker.address().stateAbbr());
        agency.setUrl(faker.internet().url());
        agency.setZipCode(faker.address().zipCode());
        return agency;
    }

    static public Contact createContact() {
        Contact contact = new Contact();
        contact.setAddress1(faker.address().streetAddress(false));
        contact.setAddress2(faker.address().secondaryAddress());
        contact.setCity(faker.address().cityPrefix() + faker.address().citySuffix());
        contact.setCountry(faker.address().country());
        contact.setCreatedDate(new Date());
        contact.setId(random.nextLong());
        contact.setModifiedDate(new Date());
        contact.setNotes(faker.lorem().paragraph());
        contact.setState(faker.address().stateAbbr());
        contact.setCreatedTpwsKey("000000");
        contact.setEmail(faker.internet().emailAddress());
        contact.setFax(faker.phoneNumber().phoneNumber());
        contact.setFirstName(faker.name().firstName());
        contact.setLastName(faker.name().lastName());
        contact.setLogicalDelete("N");
        contact.setModifiedTpwsKey("000000");
        contact.setPhone(faker.phoneNumber().phoneNumber());
        contact.setZip(faker.address().zipCode());
        return contact;
    }

    static public AgencyContact createAgencyContact() {
        AgencyContact agencyContact = new AgencyContact();
        agencyContact.setAgencyId(random.nextLong());
        agencyContact.setContactId(random.nextLong());
        agencyContact.setCreatedTpwsKey("000000");
        agencyContact.setLogicalDelete("N");
        agencyContact.setModifiedTpwsKey("000000");
        agencyContact.setTypeId(random.nextLong());
        return agencyContact;
    }

    static public SiteContact createSiteContact() {
        SiteContact siteContact = new SiteContact();
        siteContact.setSiteId(random.nextLong());
        siteContact.setContactId(random.nextLong());
        siteContact.setCreatedTpwsKey("000000");
        siteContact.setLogicalDelete("N");
        siteContact.setModifiedTpwsKey("000000");
        siteContact.setTypeId(random.nextLong());
        return siteContact;
    }

    static public SiteContactView createSiteContactView() {
        SiteContactView view = new SiteContactView();
        view.setContactId(random.nextLong());
        view.setContactName(faker.name().firstName());
        view.setContactEmail(faker.internet().emailAddress());
        view.setPublisherId(random.nextLong());
        view.setPublisherName(faker.name().firstName());
        view.setSiteId(random.nextLong());
        view.setSiteName(faker.name().firstName());
        return view;
    }

    static public PlacementView createPlacementView() {
        Placement placement = createPlacement();
        Site site = createSite();
        SiteSection siteSection = createSiteSection();
        Size size = createSize();

        PlacementView pv = new PlacementView();
        pv.setId(placement.getId());
        pv.setStartDate(placement.getStartDate());
        pv.setEndDate(placement.getEndDate());
        pv.setName(placement.getName());
        pv.setStatus(placement.getStatus());
        pv.setSiteId(site.getId());
        pv.setSiteName(site.getName());
        pv.setSiteSectionId(siteSection.getId());
        pv.setSiteSectionName(siteSection.getName());
        pv.setSizeId(size.getId());
        pv.setSizeName(size.getLabel());
        pv.setHeight(size.getHeight());
        pv.setWidth(size.getWidth());

        return pv;
    }

    static public CreativeInsertionRawDataView createCreativeInsertionImportView() {
        CreativeInsertionRawDataView ci = new CreativeInsertionRawDataView();
        ci.setCreativeInsertionId("" + Math.abs(random.nextLong()));
        ci.setCreativeClickThroughUrl(createFakeURL());
        Calendar calendar = Calendar.getInstance();
        ci.setCreativeStartDate(
                DateConverter.importExportFormat(DateConverter.startDate(calendar.getTime())));
        // Adding a day for End Date
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        ci.setCreativeEndDate(
                DateConverter.importExportFormat(DateConverter.endDate(calendar.getTime())));
        ci.setCreativeGroupId(Math.abs(random.nextLong()));
        ci.setCreativeGroupName(faker.letterify("??????????"));
        ci.setCreativeWeight("" + Math
                .abs(random.nextInt((int) (Constants.CREATIVE_INSERTION_MAX_WEIGHT - 1)) + 1));
        ci.setGroupWeight(
                "" + Math.abs(random.nextInt((int) (Constants.CREATIVE_GROUP_MAX_WEIGHT - 1)) + 1));
        ci.setPlacementName(faker.letterify("??????????"));
        ci.setPlacementCreativeName(faker.letterify("??????????"));
        ci.setSiteName(faker.internet().url());
        ci.setCreativeType(AdminFile.FileType.JPG.getFileType());
        return ci;
    }

    static public CreativeInsertionBulkUpdate createCreativeInsertionBulkUpdate(int numCreativeInsertions, int numCreativeGroups) {
        CreativeInsertionBulkUpdate cibu = new CreativeInsertionBulkUpdate();
        List<CreativeInsertion> creativeInsertions = new ArrayList<>(numCreativeInsertions);
        List<CreativeGroup> creativeGroups = new ArrayList<>(numCreativeGroups);
        for (int i = 0; i < numCreativeInsertions; i++) {
            creativeInsertions.add(createCreativeInsertion());
        }
        for (int i = 0; i < numCreativeGroups; i++) {
            creativeGroups.add(createCreativeGroup());
        }
        cibu.setCreativeGroups(creativeGroups);
        cibu.setCreativeInsertions(creativeInsertions);
        return cibu;
    }

    static public CreativeInsertionView createCreativeInsertionView() {
        CreativeInsertion creativeInsertion = createCreativeInsertion();
        Campaign campaign = createCampaign();
        Creative creative = createCreative();
        CreativeGroup creativeGroup = createCreativeGroup();
        Placement placement = createPlacement();
        Site site = createSite();
        SiteSection siteSection = createSiteSection();

        CreativeInsertionView civ = new CreativeInsertionView();
        civ.setCampaignId(campaign.getId());
        civ.setCreatedDate(creativeInsertion.getCreatedDate());
        civ.setCreatedTpwsKey(creativeInsertion.getCreatedTpwsKey());
        civ.setCreativeAlias(creative.getAlias());
        civ.setPrimaryClickthrough(creative.getClickthrough());
        civ.setCreativeGroupId(creativeGroup.getId());
        civ.setCreativeGroupName(creativeGroup.getName());
        civ.setCreativeGroupWeight(creativeGroup.getWeight());
        civ.setCreativeGroupWeightEnabled(creativeGroup.getEnableGroupWeight());
        civ.setCreativeId(creative.getId());
        civ.setCreativeInsertionRootId(creativeInsertion.getCreativeInsertionRootId());
        civ.setEndDate(creativeInsertion.getEndDate());
        civ.setId(creativeInsertion.getId());
        civ.setLogicalDelete(creativeInsertion.getLogicalDelete());
        civ.setModifiedDate(creativeInsertion.getModifiedDate());
        civ.setModifiedTpwsKey(creativeInsertion.getModifiedTpwsKey());
        civ.setPlacementEndDate(placement.getEndDate());
        civ.setPlacementId(placement.getId());
        civ.setPlacementName(placement.getName());
        civ.setPlacementStartDate(placement.getStartDate());
        civ.setPlacementStatus(placement.getStatus());
        civ.setReleased(creativeInsertion.getReleased());
        civ.setSequence(creativeInsertion.getSequence());
        civ.setSiteId(site.getId());
        civ.setSiteName(site.getName());
        civ.setSiteSectionId(siteSection.getId());
        civ.setSiteSectionName(siteSection.getName());
        civ.setStartDate(creativeInsertion.getStartDate());
        civ.setTimeZone(creativeInsertion.getTimeZone());
        civ.setWeight(creativeInsertion.getWeight());

        return civ;
    }

    static public CreativeInsertionExtendedView createCreativeInsertionExtendedView() {
        CreativeInsertionExtendedView view = new CreativeInsertionExtendedView();
        try {
            BeanUtils.copyProperties(view, createCreativeInsertionView());
            view.setCreativeType("jpg");
        } catch (Exception e) {
            view = null;
        }
        return view;
    }

    static public CreativeInsertion createCreativeInsertion() {
        CreativeInsertion ci = new CreativeInsertion();
        ci.setId(Math.abs(random.nextLong()));
        ci.setCampaignId(Math.abs(random.nextLong()));
        ci.setClickthrough(createFakeURL());
        ci.setCreatedDate(new Date());
        ci.setCreativeGroupId(Math.abs(random.nextLong()));
        ci.setCreativeId(Math.abs(random.nextLong()));
        ci.setCreativeInsertionRootId(random.nextLong());
        ci.setId(Math.abs(random.nextLong()));
        ci.setLogicalDelete("N");
        ci.setModifiedDate(new Date());
        ci.setPlacementId(random.nextLong());
        ci.setReleased(get0or1());
        ci.setSequence(random.nextLong());
        ci.setSetCookieString(faker.lorem().fixedString(15));
        ci.setStartDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(ci.getStartDate());
        cal.add(Calendar.DAY_OF_YEAR, 30);
        ci.setEndDate(cal.getTime());
        ci.setTimeZone(faker.lorem().fixedString(3));
        ci.setWeight(Constants.CREATIVE_INSERTION_MAX_WEIGHT);
        ci.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());
        return ci;
    }
    
    static public Schedule createSchedule(Long insertionId, Long placementId, Long groupId, Long creativeId) {
        Schedule result = new Schedule();
        result.setId(insertionId);
        result.setPlacementId(placementId);
        result.setCreativeGroupId(groupId);
        result.setCreativeId(creativeId);
        List<ScheduleEntry> entries = new ArrayList<>();

        ScheduleEntry entry = new ScheduleEntry();
        entry.setId(result.getId());
        entry.setStartDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(entry.getStartDate());
        cal.add(Calendar.DAY_OF_YEAR, 30);
        entry.setEndDate(cal.getTime());
        entry.setTimeZone(Constants.DEFAULT_TIMEZONE);
        entry.setWeight(Constants.CREATIVE_INSERTION_MAX_WEIGHT);
        entry.setSequence(0L);
        entry.setIsReleased(Boolean.FALSE);
        entries.add(entry);
        
        result.setEntries(entries);
        return result;
    }

    static public CreativeGroup createCreativeGroup() {
        CreativeGroup creativeGroup = new CreativeGroup();
        creativeGroup.setCampaignId(Math.abs(random.nextLong()));
        creativeGroup.setName(faker.name().firstName());
        creativeGroup.setWeight(50L);
        creativeGroup.setDoCookieTargeting(0L);
        creativeGroup.setDoDaypartTargeting(0L);
        creativeGroup.setDoGeoTargeting(0L);
        creativeGroup.setDoOptimization(0L);
        creativeGroup.setDoStoryboarding(0L);
        creativeGroup.setEnableFrequencyCap(0L);
        creativeGroup.setEnableGroupWeight(0L);
        creativeGroup.setId(Math.abs(random.nextLong()));
        creativeGroup.setIsDefault(0L);
        creativeGroup.setIsReleased(0L);
        creativeGroup.setLogicalDelete("N");
        creativeGroup.setImpressionCap(1L);
        creativeGroup.setRotationType("Weighted");
        creativeGroup.setClickthroughCap(random.nextLong());
        return creativeGroup;
    }

    static public List<CreativeGroup> createCreativeGroupsFromIds(List<Long> creativeGroupIds, Long campaignId) {
        List<CreativeGroup> result = new ArrayList<>();
        for (Long id : creativeGroupIds) {
            CreativeGroup creativeGroup = createCreativeGroup();
            creativeGroup.setId(id);
            creativeGroup.setCampaignId(campaignId);
            result.add(creativeGroup);
        }
        return result;
    }

    static public GeoTarget createGeoTarget() {
        GeoTarget geoTarget = new GeoTarget();
        int pick = random.nextInt(LocationType.values().length);
        geoTarget.setTypeCode(LocationType.values()[pick].getCode());
        geoTarget.setAntiTarget(get0or1());
        return geoTarget;
    }

    static public CreativeGroupTarget createCreativeGroupTarget() {
        CreativeGroupTarget creativeGroupTarget = new CreativeGroupTarget();
        creativeGroupTarget.setAntiTarget(get0or1());
        creativeGroupTarget.setCreativeGroupId(random.nextLong());
        creativeGroupTarget.setLogicalDelete("N");
        creativeGroupTarget.setTargetLabel(faker.lorem().fixedString(10));
        int pick = random.nextInt(LocationType.values().length);
        creativeGroupTarget.setTypeCode(LocationType.values()[pick].getCode());
        creativeGroupTarget.setValueId(random.nextLong());
        return creativeGroupTarget;
    }

    static public GeoLocation createGeoLocation() {
        GeoLocation location = new GeoLocation();
        location.setCode(faker.letterify("??") + random.nextInt(999));
        location.setId(random.nextLong());
        location.setLabel(faker.lorem().fixedString(10));
        location.setTypeId(random.nextLong());
        location.setParentCode(faker.lorem().fixedString(5));
        return location;
    }

    static public User createUser() {
        User user = new User();
        user.setId(Math.abs(random.nextLong()));
        user.setUserName(faker.internet().emailAddress());
        user.setAgencyId(Math.abs(random.nextLong()));
        user.setContactId(Math.abs(random.nextLong()));
        user.setLogicalDelete("N");
        user.setIsAppAdmin("N");
        user.setIsClientAdmin("N");
        user.setLimitDomains("N");
        user.setLimitAdvertisers("N");
        user.setIsSysAdmin("N");
        user.setIsDisabled("N");
        user.setIsTraffickingContact("Y");
        return user;
    }

    static public Campaign createCampaign() {
        Campaign campaign = new Campaign();
        campaign.setId(Math.abs(random.nextLong()));
        campaign.setAgencyId(Math.abs(random.nextLong()));
        campaign.setAdvertiserId(Math.abs(random.nextLong()));
        campaign.setBrandId(Math.abs(random.nextLong()));
        campaign.setCookieDomainId(Math.abs(random.nextLong()));
        campaign.setName(faker.name().firstName());
        campaign.setLogicalDelete("N");
        campaign.setDescription(faker.lorem().sentence(100).substring(0, 100));
        campaign.setOverallBudget(new Double(100));
        return campaign;
    }

    static public CampaignCreatorContact createCampaignCreatorContact() {
        CampaignCreatorContact ccc = new CampaignCreatorContact();
        ccc.setContactId(Math.abs(random.nextLong()));
        ccc.setContactEmail(faker.internet().emailAddress());
        ccc.setContactName(faker.name().fullName());
        return ccc;
    }

    static public Advertiser createAdvertiser() {
        Advertiser advertiser = new Advertiser();
        advertiser.setId(random.nextLong());
        advertiser.setName(faker.name().name());
        advertiser.setAgencyId(random.nextLong());
        advertiser.setAddress1(faker.address().streetAddress(false));
        advertiser.setCity(faker.address().cityPrefix());
        advertiser.setCountry(faker.address().country());
        advertiser.setUrl("http://www.test.com");
        advertiser.setIsHidden("N");
        advertiser.setContactDefault(faker.name().fullName());
        advertiser.setNotes("Notes Test Advertiser");
        advertiser.setEnableHtmlTag(1L);
        return advertiser;
    }

    static public Brand createBrand() {
        Brand brand = new Brand();
        brand.setId(random.nextLong());
        brand.setName(faker.name().name());
        return brand;
    }

    static public CookieDomain createCookieDomain() {
        CookieDomain cookieDomain = new CookieDomain();
        cookieDomain.setId(random.nextLong());
        cookieDomain.setDomain(faker.internet().url());
        return cookieDomain;
    }

    static public SmGroup createSmGroup() {
        SmGroup smGroup = new SmGroup();
        smGroup.setCreatedDate(new Date());
        smGroup.setGroupName(StringUtils.remove(faker.name().firstName(), ' '));
        smGroup.setId(random.nextLong());
        smGroup.setLogicalDelete("N");
        return smGroup;
    }

    static private Long get0or1() {
        return random.nextBoolean() ? 1L : 0L;
    }

    static public CreativeGroupDtoForCampaigns createCreativeGroupDtoForCampaigns() {
        CreativeGroupDtoForCampaigns creativeGroup = new CreativeGroupDtoForCampaigns();
        creativeGroup.setCampaignId(random.nextLong());
        creativeGroup.setDoDaypartTargeting(get0or1());
        creativeGroup.setDoCookieTargeting(get0or1());
        creativeGroup.setDoGeoTargeting(get0or1());
        creativeGroup.setId(random.nextLong());
        creativeGroup.setName(faker.name().name());
        creativeGroup.setNumberOfCreativesInGroup(random.nextInt());
        creativeGroup.setPriority(random.nextLong());
        creativeGroup.setIsDefault(get0or1());
        creativeGroup.setEnableGroupWeight(get0or1());
        return creativeGroup;
    }

    static public Creative createCreative() {
        Creative creative = new Creative();
        creative.setId(Math.abs(Math.abs(random.nextLong())));
        creative.setAgencyId(Math.abs(random.nextLong()));
        creative.setCampaignId(Math.abs(random.nextLong()));
        creative.setOwnerCampaignId(creative.getCampaignId());
        String filename = faker.name().firstName();
        creative.setFilename(filename + "." + AdminFile.FileType.JPG.getFileType());
        creative.setAlias(filename);
        creative.setCreativeType(AdminFile.FileType.JPG.getFileType());
        creative.setPurpose("Purpose");
        creative.setWidth(1L);
        creative.setHeight(1L);
        creative.setClickthrough(createFakeURL());
        creative.setLogicalDelete("N");
        creative.setExtProp1("Dev Test1");
        creative.setExtProp2("Dev Test2");
        creative.setExtProp3("Dev Test3");
        creative.setExtProp4("Dev Test4");
        creative.setExtProp5("Dev Test5");
        creative.setRichMediaId(1L);
        creative.setFileSize(1L);
        creative.setSwfClickCount(1L);
        creative.setIsExpandable(1L);
        return creative;
    }
    
    static public List<Creative> createCreativesFromIds(List<Long> creativeIds, Long campaignId){
        List<Creative> result = new ArrayList<>();
        for (Long id : creativeIds) {
            Creative creative = createCreative();
            creative.setId(id);
            creative.setCampaignId(campaignId);
            result.add(creative);
        }
        return result;
    }

    static public List<CreativeVersion> createCreativeVersions(
            Long creativeId,
            Long campaignId,
            int n){
        List<CreativeVersion> versions = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            CreativeVersion cv = new CreativeVersion(creativeId,
                    (long) (i + 1),
                    faker.letterify("???????????"),
                    new Date(),
                    1L,
                    campaignId
                    );
            versions.add(cv);
        }
        return versions;
    }

    static public CreativeGroupCreativeDTO createCreativeGroupCreativeDTO() {
        CreativeGroupCreativeDTO cgcDTO = new CreativeGroupCreativeDTO();
        cgcDTO.setCampaignId(random.nextLong());

        List<Long> groupIds = new ArrayList<>();
        groupIds.add(random.nextLong());
        cgcDTO.setCreativeGroupIds(groupIds);

        List<Creative> creatives = new ArrayList<>();
        Creative creative = new Creative();
        creative.setId(random.nextLong());
        creative.setFilename(faker.name().firstName() + "." + AdminFile.FileType.JPG.getFileType());
        creative.setCampaignId(cgcDTO.getCampaignId());
        creatives.add(creative);
        cgcDTO.setCreatives(creatives);
        return cgcDTO;
    }

    static public CreativeGroupCreative createCreativeGroupCreative() {
        CreativeGroupCreative cgc = new CreativeGroupCreative();
        cgc.setCreativeGroupId(Math.abs(random.nextLong()));
        cgc.setCreativeId(Math.abs(random.nextLong()));
        cgc.setDisplayOrder(0L);
        cgc.setDisplayQuantity(0L);
        return cgc;
    }

    static public List<CreativeGroupDtoForCampaigns> createListForCreativeGroupDtoForCampaigns(Long campaignId, int num) {
        List<CreativeGroupDtoForCampaigns> groups = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            CreativeGroupDtoForCampaigns creativeGroup = createCreativeGroupDtoForCampaigns();
            creativeGroup.setCampaignId(campaignId);
            groups.add(creativeGroup);
        }
        return groups;
    }

    static public CreativeGroupCreativeView createCreativeGroupCreativeView() {
        CreativeGroupCreativeView cgcsa = new CreativeGroupCreativeView();
        Creative creative = createCreative();
        CreativeGroup creativeGroup = createCreativeGroup();
        cgcsa.setCampaignId(random.nextLong());
        cgcsa.setCreativeAlias(creative.getAlias());
        cgcsa.setCreativeFileName(creative.getFilename());
        cgcsa.setCreativeHeight(creative.getHeight());
        cgcsa.setCreativeWidth(creative.getWidth());
        cgcsa.setCreativeId(creative.getId());
        cgcsa.setCreativeGroupId(creativeGroup.getId());
        cgcsa.setCreativeGroupName(creativeGroup.getName());
        cgcsa.setCreativeDefaultClickthrough(creative.getClickthrough());
        cgcsa.setCreativeGroupWeight(creativeGroup.getWeight());
        cgcsa.setCreativeGroupWeightEnabled(creativeGroup.getEnableGroupWeight());

        return cgcsa;
    }

    static public List<CreativeGroupCreativeView> createListForCreativeGroupCreativeViewForCampaign(Long campaignId, Long width, Long height, int num) {
        List<CreativeGroupCreativeView> cgcs = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            CreativeGroupCreativeView cgc = createCreativeGroupCreativeView();
            cgc.setCampaignId(campaignId);
            cgc.setCreativeWidth(width);
            cgc.setCreativeHeight(height);
            cgcs.add(cgc);
        }
        return cgcs;
    }

    static public SmEvent createSmEvent() {
        SmEvent smEvent = new SmEvent();
        smEvent.setId(random.nextLong());
        smEvent.setCreatedDate(new Date());
        smEvent.setEventName(faker.letterify("???????"));
        smEvent.setEventType(random.nextLong());
        smEvent.setSmEventType(random.nextLong());
        smEvent.setLogicalDelete("N");
        return smEvent;
    }

    public static BulkPublisherSiteSectionSize createBulkPublisherSiteSectionSize() {
        BulkPublisherSiteSectionSize result = new BulkPublisherSiteSectionSize();

        Publisher publisher = createPublisher();
        publisher.setId(null);
        publisher.setAgencyId(null);
        result.setPublisher(publisher);

        Site site = createSite();
        site.setId(null);
        site.setPublisherId(null);
        result.setSite(site);

        SiteSection section = createSiteSection();
        section.setId(null);
        section.setSiteId(null);
        result.setSection(section);

        Size size = createSize();
        size.setId(null);
        size.setAgencyId(null);
        result.setSize(size);
        return result;
    }

    public static Publisher createPublisher() {
        String text = "Publisher " + (new Date().getTime());
        Publisher publisher = new Publisher();
        publisher.setId(Math.abs(random.nextLong()));
        publisher.setAgencyId(Math.abs(random.nextLong()));
        publisher.setName(text);
        publisher.setUrl(faker.internet().url());
        publisher.setAddress1(faker.address().streetAddress(false));
        publisher.setAddress2(faker.address().secondaryAddress());
        publisher.setCountry(faker.address().country());
        publisher.setCity(faker.address().cityPrefix());
        publisher.setState(faker.address().stateAbbr());
        publisher.setZipCode(faker.address().zipCode());
        publisher.setAgencyNotes(text);
        return publisher;
    }

    public static Site createSite() {
        String text = "Site " + (new Date().getTime());
        Site site = new Site();
        site.setId(Math.abs(random.nextLong()));
        site.setPublisherId(Math.abs(random.nextLong()));
        site.setName(text);
        site.setUrl(faker.internet().url());
        site.setRichMedia("N");
        site.setAcceptsFlash("N");
        site.setClickTrack("N");
        site.setEncode("N");
        site.setTargetWin("_blank");
        site.setPreferredTag("IMAGE");
        site.setAgencyNotes(text);
        site.setPublisherNotes(text);
        site.setPublisherName("Publisher-" + faker.name().firstName());
        return site;
    }

    public static SiteMeasurementDTO createSiteMeasurement() {
        String text = "Site Measurement " + (new Date().getTime());
        SiteMeasurementDTO sm = new SiteMeasurementDTO();
        sm.setAdvertiserId(random.nextLong());
        sm.setAdvertiserName(text);
        sm.setAssocMethod("FIRST");
        sm.setBrandId(random.nextLong());
        sm.setBrandName(text);
        sm.setClWindow(24L);
        sm.setCookieDomainId(random.nextLong());
        sm.setCreatedDate(new Date());
        sm.setCreatedTpwsKey(text);
        sm.setDomain(text);
        sm.setDupName(text.toUpperCase());
        sm.setExpirationDate(new Date());
        sm.setLogicalDelete("N");
        sm.setModifiedDate(new Date());
        sm.setModifiedTpwsKey(text);
        sm.setName(text);
        sm.setNotes(text);
        sm.setResourcePathId(random.nextLong());
        sm.setSiteId(random.nextLong());
        sm.setState(3L);
        sm.setTtVer(1L);
        sm.setVtWindow(1L);
        return sm;
    }

    public static SiteSection createSiteSection() {
        long timeMillis = System.currentTimeMillis();
        String text = "Site Section " + timeMillis;
        SiteSection siteSection = new SiteSection();
        siteSection.setAgencyNotes(text);
        siteSection.setCreatedDate(new Date());
        siteSection.setExtProp1("extProp_1_" + timeMillis);
        siteSection.setExtProp2("extProp_2_" + timeMillis);
        siteSection.setExtProp3("extProp_3_" + timeMillis);
        siteSection.setExtProp4("extProp_4_" + timeMillis);
        siteSection.setExtProp5("extProp_5_" + timeMillis);
        siteSection.setId(Math.abs(random.nextLong()));
        siteSection.setModifiedDate(new Date());
        siteSection.setName("SC_Name " + timeMillis);
        siteSection.setPublisherNotes("Publisher Notes " + timeMillis);
        siteSection.setSiteId(Math.abs(random.nextLong()));

        return siteSection;
    }

    static public SiteMeasurementCampaignDTO createSiteMeasurementCampaign() {
        String text = "Site Measurement " + (new Date().getTime());
        SiteMeasurementCampaignDTO smc = new SiteMeasurementCampaignDTO();
        smc.setMeasurementId(random.nextLong());
        smc.setCampaignId(random.nextLong());
        smc.setLogicalDelete("N");
        smc.setCreatedDate(new Date());
        smc.setCreatedTpwsKey(text);
        smc.setModifiedDate(new Date());
        smc.setModifiedTpwsKey(text);

        smc.setMeasurementName("Measurement " + faker.name().name() + (new Date().getTime()));
        smc.setCampaignName("Campaign " + faker.name().name() + (new Date().getTime()));
        smc.setAdvertiserId(random.nextLong());
        smc.setAdvertiserName("Advertiser " + faker.name().name() + (new Date().getTime()));
        smc.setBrandId(random.nextLong());
        smc.setBrandName("Brand " + faker.name().name() + (new Date().getTime()));
        return smc;
    }

    static public InsertionOrder createInsertionOrder() {
        String text = "Insertion Order " + faker.name().name() + (new Date().getTime());
        InsertionOrder result = new InsertionOrder();
        result.setMediaBuyId(Math.abs(random.nextLong()));
        result.setPublisherId(Math.abs(random.nextLong()));
        result.setIoNumber(Math.abs(random.nextInt()));
        result.setName(text);
        result.setNotes(text);
        result.setStatus(InsertionOrderStatusEnum.NEW.getName());
        return result;
    }

    static public Package createPackage() {
        String text = "Package " + faker.name().name() + (new Date().getTime());
        Package result = new Package();
        result.setId(Math.abs(random.nextLong()));
        result.setName(text);
        result.setCountryCurrencyId(1L);
        result.setCampaignId(random.nextLong());
        result.setExternalId(String.valueOf(Math.abs(random.nextLong())));
        result.setDescription(text);
        List<Placement> placements = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Placement pl = createPlacement();
            placements.add(pl);
        }
        result.setPlacements(placements);
        List<CostDetail> costDetails = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CostDetail cd = createCostDetail();
            costDetails.add(cd);
        }
        result.setCostDetails(costDetails);
        return result;
    }
    
    static public CostDetail createCostDetail() {
        CostDetail result = new CostDetail();
        result.setForeignId(Math.abs(random.nextLong()));
        result.setRateType(RateTypeEnum.CPA.getCode());
        result.setPlannedGrossAdSpend(random.nextDouble());
        result.setPlannedGrossRate(random.nextDouble());
        result.setStartDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getStartDate());
        cal.add(Calendar.DAY_OF_YEAR, 30);
        result.setEndDate(cal.getTime());
        return result;
    }
    
    static public MediaBuy createMediaBuy(){
        MediaBuy result = new MediaBuy();
        result.setId(Math.abs(random.nextLong()));
        result.setAgencyId(Math.abs(random.nextLong()));
        result.setName("MediaBuy-" + faker.name().firstName());
        result.setState(GeneralStatusEnum.NEW.getStatusCode());
        result.setOverallBudget(Math.abs(random.nextLong()));
        result.setAgencyNotes("Notes-" + faker.lorem().fixedString(10));
        result.setCreatedDate(new Date());
        result.setModifiedDate(new Date());
        return result;
    }

    static public PlacementStatus createPlacementStatus(){
        PlacementStatus placementStatus = new PlacementStatus();
        placementStatus.setContactId(random.nextLong());
        placementStatus.setCreatedDate(new Date());
        placementStatus.setLogicalDelete("N");
        placementStatus.setModifiedDate(new Date());
        placementStatus.setPlacementId(random.nextLong());
        placementStatus.setStatusDate(new Date());
        placementStatus.setStatusId(3L);
        placementStatus.setStatusName("New");
        return placementStatus;
    }

    static public List<Placement> createPlacements(int numberOfPlacements) {
        List<Placement> placements = new ArrayList<>();
        for (int i = 0; i < numberOfPlacements; i++) {
            Placement placement = createPlacement();
            placement.setId(Math.abs(random.nextLong()));
            placements.add(placement);
        }
        return placements;
    }

    static public Placement createPlacement() {
        String text = "Placement " + faker.name().name() + (new Date().getTime());
        Placement result = new Placement();
        result.setSiteId(Math.abs(random.nextLong()));
        result.setSiteSectionId(Math.abs(random.nextLong()));
        result.setIoId(Math.abs(random.nextLong()));
        result.setSizeId(Math.abs(random.nextLong()));
        result.setIsSecure(0L);
        result.setIsTrafficked(0L);
        result.setResendTags(0L);
        result.setUtcOffset(0L);
        result.setSmEventId(-1L);
        result.setCountryCurrencyId(1L);
        result.setName(text);
        result.setWidth(100L);
        result.setHeight(80L);
        result.setCampaignId(Math.abs(random.nextLong()));
        result.setStatus(InsertionOrderStatusEnum.NEW.getName());
        result.setSiteName("Site_for " + text);
        result.setSizeName(result.getWidth() + "x" + result.getHeight());
        result.setAdSpend(0.0);
        result.setRate(0.0);
        result.setRateType(RateTypeEnum.CPM.toString());
        result.setStartDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getStartDate());
        cal.add(Calendar.DAY_OF_YEAR, 30);
        result.setEndDate(cal.getTime());
        return result;
    }

    static public List<Placement> createPlacementsFromIds(List<Long> placementIds, Long campaignId){
        List<Placement> result = new ArrayList<>();
        for (Long id : placementIds) {
            Placement placement = createPlacement();
            placement.setId(id);
            placement.setStatus("Accepted");
            placement.setCampaignId(campaignId);
            result.add(placement);
        }
        return result;
    }
    
    static public Size createSize() {
        Size result = new Size();
        result.setId(Math.abs(random.nextLong()));
        result.setAgencyId(random.nextLong());
        result.setWidth(100L);
        result.setHeight(80L);
        result.setLabel(result.getWidth() + "x" + result.getHeight());
        return result;
    }

    static public Trafficking createTrafficking() {
        Trafficking result = new Trafficking();
        result.setCampaignId(Math.abs(random.nextLong()));
        result.setTimeZoneOffset(0L);
        result.setDomain(createFakeDomain());
        List<Integer> agencyContacts = new ArrayList<>();
        agencyContacts.add(Math.abs(random.nextInt()));
        result.setAgencyContacts(agencyContacts);
        List<Integer> siteContacts = new ArrayList<>();
        siteContacts.add(Math.abs(random.nextInt()));
        result.setSiteContacts(siteContacts);
        return result;
    }
    
    static public AgencyUser createAgencyUser() {
        AgencyUser result = new AgencyUser();
        result.setUserId(faker.internet().emailAddress());
        result.setAgencyId(Math.abs(random.nextLong()));
        result.setIsAppAdmin("N");
        result.setIsClientAdmin("N");
        result.setLimitDomains("N");
        result.setLimitAdvertisers("N");
        result.setId(Math.abs(random.nextLong()));
        result.setIsTraffickingContact("Y");
        result.setIsDisabled("N");
        result.setLname(faker.name().lastName());
        result.setFname(faker.name().firstName());
        result.setEmail(faker.internet().emailAddress());
        result.setAgencyName(faker.name().name());
        result.setContactId(Math.abs(random.nextLong()));
        return result;
    }
    
    static public CookieTargetTemplate createCookieTargetTemplate() {
        CookieTargetTemplate result = new CookieTargetTemplate();
        result.setCookieName(faker.letterify("????????????????????"));
        result.setContentPossibleValues(faker.lorem().fixedString(10));
        result.setCookieDomainId(Math.abs(random.nextLong()));
        return result;
    }
    
    static public String createFakeDomain(){
        return "www." + faker.letterify("????????????????????") + ".com";
    }
    
    static public String createFakeURL(){
        String proto = (random.nextInt(3) % 2 == 0 ? "http://" : "https://");
        return proto + createFakeDomain();
    }

    static public CampaignDTO createCampaignDto() {
        CampaignDTO result = new CampaignDTO();
        result.setId(random.nextLong());
        result.setAdvertiserId(random.nextLong());
        result.setBrandId(random.nextLong());
        result.setCookieDomainId(random.nextLong());
        result.setName(faker.name().name());
        result.setLogicalDelete("N");
        result.setDescription(faker.lorem().sentence(100).substring(0, 100));
        result.setOverallBudget(new Double(100));
        result.setAdvertiserName(faker.name().name());
        result.setBrandName(faker.name().name());
        result.setDomain(createFakeDomain());
        return result;
    }

    static public List<CostDetail> createCostDetailList(int number){
        if(number <= 0) {
            throw new IllegalArgumentException("Number cannot be lower than or equals to zero");
        }
        List<CostDetail> costDetails = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        // We're going to start the generation almost in the end of the 2015 year
        String date = "2015-12-28";
        DateFormat f = new SimpleDateFormat("YYYY-mm-dd");
        try {
            Date d = f.parse(date);
            cal.setTime(d);
        } catch (ParseException e) {}
        int i;
        // Generate the half of the records with same days intervals
        boolean stop = false;
        if(number == 1) {
            number = 2;
            stop = true;
        }
        // First group are continuous intervals
        for (i = 0; i < number/2; i++) {
            CostDetail costDetail = new CostDetail();
            costDetail.setId((long) i);
            costDetail.setRateType(RateTypeEnum.CPM.getCode());
            costDetail.setStartDate(DateConverter.startDate(cal.getTime()));
            costDetail.setEndDate(DateConverter.endDate(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, 1);
            costDetails.add(costDetail);
        }
        if(stop) {
           return costDetails;
        }
        // Second group are dis-continuous intervals
        for (; i < number; i++) {
            CostDetail costDetail = new CostDetail();
            costDetail.setId((long) i);
            costDetail.setRateType(RateTypeEnum.CPM.getCode());
            costDetail.setStartDate(DateConverter.startDate(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, 7);
            costDetail.setEndDate(DateConverter.endDate(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, 1);
            costDetails.add(costDetail);
        }
        return costDetails;
    }
    
    static public List<UserView> createTraffickingUsers() {
        List<UserView> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserView user = createTraffickingUser();
            users.add(user);
        }
        return users;
    }

    static public UserView createTraffickingUser() {
        UserView user = new UserView();
        user.setId(random.nextLong());
        user.setUserName(faker.internet().emailAddress());
        user.setRealName(faker.name().firstName() + " " + faker.name().lastName());
        user.setContactId(random.nextLong());
        return user;
    }
    
    static public List<UserDomain> createUserDomains(String userId) {
        List<UserDomain> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserDomain user = createUserDomain();
            user.setUserId(userId);
            users.add(user);
        }
        return users;
    }
    
    static public UserDomain createUserDomain() {
        UserDomain userDomain = new UserDomain();
        userDomain.setDomainId(random.nextLong());
        userDomain.setUserId(faker.internet().emailAddress());
        return userDomain;
    }

    static public MediaRawDataView createMediaRawDataView() {
        MediaRawDataView mediaRow = new MediaRawDataView();
        mediaRow.setFieldsWithFormulaError(new ArrayList<String>());
        mediaRow = fillInPlacementData(mediaRow, false);
        mediaRow = fillInCostData(mediaRow, new Date());
        mediaRow.setOrderName(faker.letterify("??????"));
        mediaRow.setOrderNumber(faker.numerify("######"));
        mediaRow.setIssues(new HashMap<String, List<ImportExportCellError>>());
        return mediaRow;
    }

    static public List<MediaRawDataView> createMediaRawDataViewList(int numberOfPackages,
                                                                    int numberOfPlacementsPerPackage,
                                                                    int numberOfStandalonePlacements,
                                                                    int numberOfCostDetails,
                                                                    boolean isUpdateData){
        List<MediaRawDataView> lastCosts = new ArrayList<>(numberOfCostDetails);
        int packageNumber = 1;
        int lastCostIndex = 0;
        String lastPackage = "";
        String lastPackageId = "";
        int lastIndex = numberOfPackages * numberOfPlacementsPerPackage * numberOfCostDetails ;
        List<MediaRawDataView> list = new ArrayList<>(lastIndex);
        String orderName = "Order-" + faker.letterify("????");
        String orderNumber = String.valueOf(Math.abs(random.nextInt()));
        Date lastDate = new Date();
        Calendar calendar = Calendar.getInstance();
        // Packages with Placements
        for (int i = 0; i < lastIndex; i++) {
            MediaRawDataView row = new MediaRawDataView();
            row.setRowError(String.valueOf(i));
            row.setIssues(new HashMap<String, List<ImportExportCellError>>());
            // Fill in Package data
            if(i % ( numberOfPlacementsPerPackage * numberOfCostDetails ) == 0) {
                lastPackage ="PK-" + packageNumber + "_" + faker.letterify("???");
                lastPackageId = isUpdateData ? String.valueOf(Math.abs(random.nextLong())) : null;
                lastCosts.clear();
                packageNumber++;
            }
            // Fill in Placement data
            if(i % numberOfCostDetails == 0) {
                // Fill in Insertion Order data
                row.setOrderName(orderName);
                row.setOrderNumber(orderNumber);
                fillInPlacementData(row, isUpdateData);
                row.setMediaPackageName(lastPackage);
                row.setMediaPackageId(lastPackageId);
                lastCostIndex = 0;
            }
            // Fill in Cost Detail data
            if(lastCosts.size() < numberOfCostDetails) {
                fillInCostData(row, lastDate);
                lastCosts.add(row);
                try {
                    calendar.setTime(DateConverter.parseForImport(row.getEndDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(Calendar.SECOND, 1);
                lastDate = calendar.getTime();
            }
            else {
                lastCosts.get(lastCosts.size() - 1).setEndDate(null);
                copyCosts(row, lastCosts.get(lastCostIndex));
                lastCostIndex++;
            }
            list.add(row);
        }
        // Standalone Placements
        lastIndex = numberOfStandalonePlacements * numberOfCostDetails;

        for(int i = 0; i < lastIndex; i++) {
            MediaRawDataView row = new MediaRawDataView();
            row.setIssues(new HashMap<String, List<ImportExportCellError>>());
            // Fill in Package data
            if(i % numberOfCostDetails == 0) {
                // Fill in Insertion Order data
                row.setOrderName(orderName);
                row.setOrderNumber(orderNumber);
                fillInPlacementData(row, isUpdateData);
            }
            fillInCostData(row, lastDate);
            try {
                calendar.setTime(DateConverter.parseForImport(row.getEndDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.add(Calendar.SECOND, 1);
            lastDate = calendar.getTime();
            list.add(row);
        }
        // Remove last end date
        list.get(list.size() - 1).setEndDate(null);
        return list;
    }

    private static MediaRawDataView fillInPlacementData(MediaRawDataView row, boolean isUpdateData) {
        row.setExtPlacementId(faker.numerify("######"));
        row.setPlacementName("PL-" + faker.letterify("???"));
        String placementId = isUpdateData ? String.valueOf(Math.abs(random.nextLong())) : null;
        row.setPlacementId(placementId);
        row.setAdHeight(String.valueOf(Math.abs(random.nextInt(9998) + 1)));
        row.setAdWidth(String.valueOf(Math.abs(random.nextInt(9998) + 1)));
        row.setPublisher("PB-" + faker.letterify("??????"));
        row.setSite("ST-" + faker.letterify("??????"));
        row.setSection("SC-" + faker.letterify("??????"));
        String name = faker.letterify("??????");
        row.setPlacementProp1(name + "_01");
        row.setPlacementProp2(name + "_02");
        row.setPlacementProp3(name + "_03");
        row.setPlacementProp4(name + "_04");
        row.setPlacementProp5(name + "_05");
        name = faker.letterify("??????");
        row.setSectionProp1(name + "_01");
        row.setSectionProp2(name + "_02");
        row.setSectionProp3(name + "_03");
        row.setSectionProp4(name + "_04");
        row.setSectionProp5(name + "_05");
        return row;
    }

    private static MediaRawDataView fillInCostData(MediaRawDataView row, Date lastDate) {
        row.setPlannedAdSpend(faker.numerify("#####"));
        row.setRate(faker.numerify("######"));
        row.setRateType(getRandomCostType());
        row.setStartDate(
                DateConverter.importExportFormat(DateConverter.startDate(lastDate)));
        row.setEndDate(
                DateConverter.importExportFormat(DateConverter.endDate(lastDate)));
        return row;
    }

    /**
     * Utility method to render a flat List of {@code MediaRawDataView}
     * @param rows The List of {@code MediaRawDataView} to render
     * @return A String representation of the List
     */
    static public String renderFlatMediaRawDataView(List<MediaRawDataView> rows) {
        if(rows == null || rows.isEmpty()) {
            return "";
        }
        String out = "";
        String rowTemplate =
                "|%10s |%10s |%10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %25s | %25s | %7s |\n";
        out = String.format(rowTemplate,
                "IOName",
                "IONumb",
                "PKName",
                "PLName",
                "PLExtId",
                "Site",
                "Sect",
                "Size",
                "AdSpend",
                "Rate",
                "RType",
                "Ivt",
                "SDate",
                "EDate",
                "#Costs");
        for(MediaRawDataView row: rows){
            out+= String.format(rowTemplate,
                    row.getOrderName(),
                    row.getOrderNumber(),
                    row.getMediaPackageName(),
                    row.getPlacementName(),
                    row.getExtPlacementId(),
                    row.getSite(),
                    row.getSection(),
                    row.getAdWidth() + "x" + row.getAdHeight(),
                    row.getPlannedAdSpend(),
                    row.getRate(),
                    row.getRateType(),
                    row.getInventory(),
                    row.getStartDate(),
                    row.getEndDate(),
                    row.getCostDetails().size());
        }
        return out;
    }

    /**
     * Utility method to render a List of {@code MediaRawDataView}
     * @param rows The List of {@code MediaRawDataView} to render
     * @return A String representation of the List
     */
    static public String renderMediaRawDataView(List<MediaRawDataView> rows) {
        if(rows == null || rows.isEmpty()) {
            return "";
        }
        String out = "";
        String rowTemplate =
                "|%10s |%10s |%10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %10s | %25s | %25s | %10s |\n";
        out = String.format(rowTemplate,
                "IOName",
                "IONumb",
                "PKName",
                "PLName",
                "PLExtId",
                "Site",
                "Sect",
                "Size",
                "AdSpend",
                "Rate",
                "RType",
                "Ivt",
                "SDate",
                "EDate",
                "Iss");
        String empty = "";
        for(MediaRawDataView row: rows){
            out += String.format(rowTemplate,
                    row.getOrderName(),
                    row.getOrderNumber(),
                    row.getMediaPackageName(),
                    row.getPlacementName(),
                    row.getExtPlacementId(),
                    row.getSite(),
                    row.getSection(),
                    row.getAdWidth() + "x" + row.getAdHeight(),
                    row.getPlannedAdSpend(),
                    row.getRate(),
                    row.getRateType(),
                    row.getInventory(),
                    row.getStartDate(),
                    row.getEndDate(),
                    row.getIssues().size());
            for(CostDetailRawDataView costDetail : row.getCostDetails()) {
                out += String.format(rowTemplate,
                        empty,
                        empty,
                        empty,
                        empty,
                        empty,
                        empty,
                        empty,
                        empty,
                        costDetail.getPlannedAdSpend(),
                        costDetail.getRate(),
                        costDetail.getRateType(),
                        costDetail.getInventory(),
                        costDetail.getStartDate(),
                        costDetail.getEndDate(),
                        empty);
            }
        }
        return out;
    }

    static public String getRandomCostType() {
        return EntityFactory.faker.options().option(new String[]{
                String.valueOf(RateTypeEnum.CPA.name()),
                String.valueOf(RateTypeEnum.CPC.name()),
                String.valueOf(RateTypeEnum.CPL.name()),
                String.valueOf(RateTypeEnum.CPM.name()),
                String.valueOf(RateTypeEnum.FLT.name())});
    }

    private static void copyCosts(MediaRawDataView to, MediaRawDataView from) {
        to.setPlannedAdSpend(from.getPlannedAdSpend());
        to.setInventory(from.getInventory());
        to.setRate(from.getRate());
        to.setRateType(from.getRateType());
        to.setStartDate(from.getStartDate());
        to.setEndDate(from.getEndDate());
    }

    static public HtmlInjectionTagAssociation createHtmlInjectionTagAssociation() {
        HtmlInjectionTagAssociation assoc = new HtmlInjectionTagAssociation();
        assoc.setId(Math.abs(random.nextLong()));
        assoc.setCampaignId(Math.abs(random.nextLong()));
        assoc.setHtmlInjectionId(Math.abs(random.nextLong()));
        assoc.setEntityType(PlacementFilterParamLevelTypeEnum.PLACEMENT.getCode());
        assoc.setEntityType(Math.abs(random.nextLong()));
        assoc.setIsEnabled(Math.abs(random.nextLong()));
        assoc.setHtmlInjectionName(faker.letterify("??????????"));
        assoc.setIsInherited(Constants.DISABLED);
        return assoc;
    }

    static public HtmlInjectionTags createHtmlInjectionTag() {
        HtmlInjectionTags result = new HtmlInjectionTags();
        result.setId(Math.abs(random.nextLong()));
        result.setAgencyId(random.nextLong());
        result.setHtmlContent(
                "<div style=\"position: relative; left: 0; top: 0;\"><a title=\"Proudly Supporting Consumer Ad Choices\" target=\"_blank\" href=\"http://www.aaa.com\"><img width=\"15\" height=\"15\" style=\"position: absolute; z-index: 100;border: none; top: 3px; padding: 0px;background:transparent; left: 4px;\" src=\"http://ad.adlegend.com/cdn/trueffect/adchoices/15x15.png\"></a></div>");
        result.setIsEnabled(1L);
        result.setIsVisible(1L);
        result.setName("Tag"+faker.letterify("????????????"));
        result.setSecureHtmlContent(
                "<div style=\"position: relative; left: 0; top: 0;\"><a title=\"Proudly Supporting Consumer Ad Choices\" target=\"_blank\" href=\"http://www.aaa.com\"><img width=\"15\" height=\"15\" style=\"position: absolute; z-index: 100;border: none; top: 3px; padding: 0px;background:transparent; left: 4px;\" src=\"https://ad.adlegend.com/cdn/trueffect/adchoices/15x15.png\"></a></div>");
        return result;
    }

    static public List<Long> getLongList(int quantity) {
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            result.add(Math.abs(random.nextLong()));
        }
        return result;
    }

    static public SmPingEventDTO createSmPingEvent() {
        SmPingEventDTO ping = new SmPingEventDTO();
        ping.setId(random.nextLong()); // event id
        ping.setPingId(random.nextLong());
        ping.setPingContent(createFakeURL()); // depends on ping tag type
        ping.setDescription(faker.letterify("???????"));
        ping.setSiteId(random.nextLong());
        ping.setSiteName(faker.letterify("???????"));
        ping.setPingType(Long.valueOf(SiteMeasurementEventPingType.BROADCAST.value()));
        ping.setPingTagType(Long.valueOf(SiteMeasurementEventPingTagType.IMG.ordinal()));
        return ping;
    }
}
