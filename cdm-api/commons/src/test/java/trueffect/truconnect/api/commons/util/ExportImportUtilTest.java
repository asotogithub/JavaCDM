package trueffect.truconnect.api.commons.util;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.importexport.Column;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawErrorDataView;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.XlsErrorTemplateDescriptor;

import com.github.javafaker.Faker;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Gustavo Claure
 */
public class ExportImportUtilTest {

    private Random random = new Random();
    private Faker faker = new Faker(random);
    private InputStream inp;
    private Class<? extends XLSTemplateDescriptor> type;
    private Class<? extends XlsErrorTemplateDescriptor> typeOfError;
    private List<CreativeInsertionRawDataView> list;
    private ExportImportUtil eiu = new ExportImportUtil();

    @Before
    public void init() throws Exception {
        //set default values
        inp = getClass().getResourceAsStream("/template/xlsx/Schedule Edit Template.xlsx");
        type = CreativeInsertionRawDataView.class;
        typeOfError = CreativeInsertionRawErrorDataView.class;
        list = createCreativeInsertionExportViewListForCampaign(100);
    }

    @After
    public void teardown() throws IOException {
        if(inp != null) {
            inp.close();
        }
    }

    @Test
    public void testPrepareWorkbook() throws Exception {
        SXSSFWorkbook test = eiu.fromList(inp, list, typeOfError);
        assertThat(test, is(notNullValue()));
        assertThat(test.getSheetAt(0), is(notNullValue()));
        assertThat(test.getSheetAt(1), is(notNullValue()));
    }

    @Test
    public void testParseToRegularFile() throws Exception {
        File file = new File(getClass().getResource("/template/xlsx/regular-media-file.xlsx").toURI());
        List<MediaRawDataView> records = eiu.parseTo(MediaRawDataView.class, file, ExportImportUtil.VALIDATE_HEADERS);
        assertThat(records, is(notNullValue()));
        assertThat(records.size(), is(15));
        assertThat(records.get(0).getReason(), is(""));
    }

    @Test
    public void testParseToFileWithErrorColumns() throws Exception {
        File file = new File(getClass().getResource("/template/xlsx/media-file-with-errors.xlsx").toURI());
        List<MediaRawDataView> records = eiu.parseTo(MediaRawDataView.class, file, ExportImportUtil.VALIDATE_HEADERS);
        assertThat(records, is(notNullValue()));
        assertThat(records.size(), is(15));
        assertThat(records.get(0).getReason(), is("Error of some type"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrepareWorkbookFailInput() throws Exception {
        SXSSFWorkbook test = eiu.fromList(null, list, typeOfError);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrepareWorkbookFailMap() throws Exception {
        SXSSFWorkbook test = eiu.fromList(inp, null, typeOfError);
    }

    @Test
    public void testMapFromObj() throws Exception {
        Map<String, Column> test = type.newInstance().getTemplateMapping();
        for (Map.Entry<String, Column> entrySet : test.entrySet()) {
            assertThat(entrySet.getValue(), is(notNullValue()));
        }
        assertThat(test, is(notNullValue()));
        assertThat(test.size(), is(11));

        test = typeOfError.newInstance().getTemplateMapping();
        for (Map.Entry<String, Column> entrySet : test.entrySet()) {
            assertThat(entrySet.getValue(), is(notNullValue()));
        }
        assertThat(test, is(notNullValue()));
        assertThat(test.size(), is(13));
    }

    private List<CreativeInsertionRawDataView> createCreativeInsertionExportViewListForCampaign(int size) {
        List<CreativeInsertionRawDataView> creativeInsertionExportViews = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CreativeInsertionRawDataView createCreativeInsertionExportView = createCreativeInsertionExportView();
            creativeInsertionExportViews.add(createCreativeInsertionExportView);
        }
        return creativeInsertionExportViews;
    }

    private CreativeInsertionRawDataView createCreativeInsertionExportView() {
        CreativeInsertion creativeInsertion = createCreativeInsertion();
        CreativeGroup creativeGroup = createCreativeGroup();
        Creative creative = createCreative();
        Placement placement = createPlacement();
        Site site = createSite();
        CreativeInsertionRawDataView creativeInsertionExportView = new CreativeInsertionRawDataView();
        creativeInsertionExportView.setCreativeClickThroughUrl("clickthroug");
        creativeInsertionExportView.setCreativeGroupId(creativeGroup.getId());
        creativeInsertionExportView.setCreativeGroupName(creativeGroup.getName());
        creativeInsertionExportView.setCreativeInsertionId(creativeInsertion.getId().toString());
        creativeInsertionExportView.setCreativeStartDate(new Date().toString());
        creativeInsertionExportView.setCreativeEndDate(new Date().toString());
        creativeInsertionExportView.setCreativeWeight(creativeGroup.getWeight()+"");
        creativeInsertionExportView.setPlacementCreativeName(creative.getFilename());
        creativeInsertionExportView.setPlacementName(placement.getName());
        creativeInsertionExportView.setSiteName(site.getName());
        return creativeInsertionExportView;
    }

    private Site createSite() {
        String text = "Site " + (new Date().getTime());
        Site site = new Site();
        site.setId(random.nextLong());
        site.setPublisherId(random.nextLong());
        site.setName(text);
        site.setUrl(faker.internet().url());
        site.setRichMedia("false");
        site.setAcceptsFlash("false");
        site.setClickTrack("false");
        site.setEncode("false");
        site.setTargetWin("_blank");
        site.setPreferredTag("IMAGE");
        site.setAgencyNotes(text);
        site.setPublisherNotes(text);
        site.setPublisherId(random.nextLong());
        site.setPublisherName("Publisher-" + faker.name().firstName());
        return site;
    }

    private Placement createPlacement() {
        String text = "Placement " + faker.name().name() + (new Date().getTime());
        Placement result = new Placement();
        result.setSiteId(random.nextLong());
        result.setSiteSectionId(random.nextLong());
        result.setIoId(random.nextLong());
        result.setSizeId(random.nextLong());
        result.setIsSecure(0L);
        result.setIsTrafficked(0L);
        result.setResendTags(0L);
        result.setUtcOffset(0L);
        result.setSmEventId(-1L);
        result.setCountryCurrencyId(1L);
        result.setName(text);
        result.setWidth(100L);
        result.setHeight(80L);
        result.setCampaignId(random.nextLong());
        result.setStatus("New");
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

    private Creative createCreative() {
        Creative creative = new Creative();
        creative.setId(random.nextLong());
        creative.setAgencyId(random.nextLong());
        creative.setCampaignId(random.nextLong());
        creative.setOwnerCampaignId(creative.getCampaignId());
        String filename = faker.name().firstName();
        creative.setFilename(filename + "." + AdminFile.FileType.JPG.getFileType());
        creative.setAlias(filename);
        creative.setCreativeType(AdminFile.FileType.JPG.getFileType());
        creative.setPurpose("Dev Test");
        creative.setWidth(1L);
        creative.setHeight(1L);
        creative.setClickthrough("https://DevTest.com");
        creative.setLogicalDelete("N");
        creative.setExtProp1("Dev Test");
        creative.setExtProp2("Dev Test");
        creative.setExtProp3("Dev Test");
        creative.setExtProp4("Dev Test");
        creative.setExtProp5("Dev Test");
        creative.setRichMediaId(1L);
        creative.setFileSize(1L);
        creative.setSwfClickCount(1L);
        creative.setIsExpandable(1L);
        return creative;
    }

    private CreativeGroup createCreativeGroup() {
        CreativeGroup creativeGroup = new CreativeGroup();
        creativeGroup.setCampaignId(random.nextLong());
        creativeGroup.setClickthroughCap(random.nextLong());
        creativeGroup.setDoCookieTargeting(0L);
        creativeGroup.setDoDaypartTargeting(0L);
        creativeGroup.setDoGeoTargeting(0L);
        creativeGroup.setDoOptimization(0L);
        creativeGroup.setDoStoryboarding(0L);
        creativeGroup.setEnableFrequencyCap(0L);
        creativeGroup.setEnableGroupWeight(0L);
        creativeGroup.setId(random.nextLong());
        creativeGroup.setIsDefault(0L);
        creativeGroup.setIsReleased(0L);
        creativeGroup.setLogicalDelete("N");
        creativeGroup.setImpressionCap(1L);
        creativeGroup.setRotationType("Weighted");
        return creativeGroup;
    }

    private CreativeInsertion createCreativeInsertion() {
        CreativeInsertion ci = new CreativeInsertion();
        ci.setCampaignId(random.nextLong());
        ci.setClickthrough(faker.internet().url());
        ci.setCreatedDate(new Date());
        ci.setCreativeGroupId(random.nextLong());
        ci.setCreativeId(random.nextLong());
        ci.setCreativeInsertionRootId(random.nextLong());
        ci.setId(random.nextLong());
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
        ci.setWeight(Constants.CREATIVE_INSERTION_MIN_WEIGHT);
        return ci;
    }

    private Long get0or1() {
        return random.nextBoolean() ? 1L : 0L;
    }
}
