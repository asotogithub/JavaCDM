package trueffect.truconnect.api.publik.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import trueffect.truconnect.api.commons.model.NestedDto;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.TestDto;
import trueffect.truconnect.api.commons.model.TpTag;
import trueffect.truconnect.api.publik.provider.JAXBContextResolver;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.resource.Singleton;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.inmemory.InMemoryTestContainerFactory;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Test of JSON Natural notation
 */
public class JsonNaturalNotationServiceTest extends JerseyTest {

    private static TestDto expectedTestDto;
    private static RecordSet<Size> expectedRecordSetSize;
    private static RecordSet<TpTag> expectedRecordSetTpTag;

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new InMemoryTestContainerFactory();
    }

    @Before
    public void init(){
        // Prepare TestDto
        List<NestedDto> nestedList = new ArrayList<NestedDto>();
        NestedDto nestedDto = new NestedDto();
        nestedDto.setName("name");
        nestedDto.setValue(1000);
        nestedList.add(nestedDto);
        // Expected TestDto
        expectedTestDto = new TestDto(10, 10L, 10.00, "10",
                                      Arrays.asList("10", "10"),
                                      Arrays.asList(10, 20),
                                      Arrays.asList(10L, 20L),
                                      Arrays.asList(10.01, 20.01),
                                      Arrays.asList(100),
                                      Arrays.asList(100L),
                                      Arrays.asList(100.001),
                                      nestedList,
                                        TestDto.Type.FIRST);
        // Expected RecordSet with Size elements
        List<Size> list = new ArrayList<Size>();
        Size size = new Size();
        long n = 1000L;
        size.setAgencyId(n);
        size.setCreatedDate(new Date(0L));
        size.setHeight(n);
        size.setId(n);
        size.setLabel("Label_" + n);
        size.setModifiedDate(new Date(0L));
        size.setWidth(n);
        list.add(size);
        expectedRecordSetSize = new RecordSet<Size>(0, 10, list.size(), list);
        // Expected RecordSet with TpTag elements
        List<TpTag> listT = new ArrayList<TpTag>();
        TpTag tpTag = new TpTag();
        tpTag.setTagName("tagname");
        tpTag.setTpTagId(10L);
        listT.add(tpTag);
        expectedRecordSetTpTag = new RecordSet<TpTag>(0, 10, listT.size(), listT);
    }

    @Override
    protected AppDescriptor configure() {
        ResourceConfig rc = new DefaultResourceConfig();
        rc.getClasses().add(TestResource.class);
        // To remove the Natural notation and revert back to Mapped just comment out below line
        rc.getClasses().add(JAXBContextResolver.class);
        return new LowLevelAppDescriptor.Builder(rc).
            contextPath("context").
            build();
    }

    @Test
    public void testGetSingleTpTagRecordSetRepresentationAsString() {
        // Make the request for a single element
        WebResource r = resource().path("RecordSet").path("TpTag").path("single");
        // Assert String representation
        String expected = "{\"startIndex\":0,\"pageSize\":10,\"totalNumberOfRecords\":1,\"records\":[{\"TpTag\":[{\"tagName\":\"tagname\",\"tpTagId\":10}]}]}";
        String s = r.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        assertEquals(expected, s);
    }

    @Test
    public void testGetSingleTpTagRecordSetDeserializationAsJson() {
        // Make the request for a single element
        WebResource r = resource().path("RecordSet").path("TpTag").path("single");
        // Check deserialization
        RecordSet<TpTag> actual = r.accept(MediaType.APPLICATION_JSON_TYPE).get(RecordSet.class);
        assertNotNull(actual);
        assertNotNull(actual.getRecords());
        assertTrue(actual.getRecords().size() == 1);
        assertTrue(expectedRecordSetTpTag.getPageSize() == actual.getPageSize());
        assertTrue(expectedRecordSetTpTag.getStartIndex() == actual.getStartIndex());
        assertTrue(expectedRecordSetTpTag.getTotalNumberOfRecords() == actual.getTotalNumberOfRecords());
    }

    @Test
    public void testGetSingleSizeRecordSetDeserializationAsJson() {
        // Make the request for a single element
        WebResource r = resource().path("RecordSet").path("Size").path("single");
        // Check deserialization
        RecordSet<Size> actual = r.accept(MediaType.APPLICATION_JSON_TYPE).get(RecordSet.class);
        assertNotNull(actual);
        assertNotNull(actual.getRecords());
        assertTrue(actual.getRecords().size() == 1);
        assertTrue(expectedRecordSetSize.getPageSize() == actual.getPageSize());
        assertTrue(expectedRecordSetSize.getStartIndex() == actual.getStartIndex());
        assertTrue(expectedRecordSetSize.getTotalNumberOfRecords() == actual.getTotalNumberOfRecords());
    }
    @Test
    public void testGetSingleSizeRecordSetDeserializationAsXmlFromStringRepresentaion() {
        // Make the request for a single element
        WebResource r = resource().path("RecordSet").path("Size").path("Backwards").path("single");
        // Check deserialization
        RecordSet<Size> actual = r.accept(MediaType.TEXT_XML_TYPE).get(RecordSet.class);
        assertNotNull(actual);
        assertNotNull(actual.getRecords());
        assertTrue(actual.getRecords().size() == 2);
        assertTrue(actual.getRecords().iterator().next() instanceof Size);
    }

    @Test
    public void testGetMultipleSizeRecordSetDeserializationAsJson() {
        int recordsNumber = 2;
        // Make the request
        WebResource r = resource().path("RecordSet").path("Size").path("" + recordsNumber);
        // Check deserialization
        RecordSet<Size> actual = r.accept(MediaType.APPLICATION_JSON_TYPE).get(RecordSet.class);
        assertRecordSet(actual, recordsNumber);
    }

    @Test
    public void testGetMultipleSizeRecordSetDeserializationAsXml() {
        int recordsNumber = 2;
        // Make the request
        WebResource r = resource().path("RecordSet").path("Size").path("" + recordsNumber);
        // Check deserialization using the FitNesse way to consume a request
        ClientResponse actualClientResponse = r.accept(MediaType.TEXT_XML).get(ClientResponse.class);
        RecordSet<Size> actual = actualClientResponse.getEntity(RecordSet.class);
        assertRecordSet(actual, recordsNumber);
    }

    @Test
    public void testGetSingleTestDtoRepresentationAsString() {
        WebResource resource = resource().path("/CustomDto/single");
        String expectedRepresentation = "{\"doubleList\":[10.01,20.01],\"doubleValue\":10.0," +
                "\"intList\":[10,20],\"intValue\":10,\"longList\":[10,20],\"longValue\":10," +
                "\"nestedList\":[{\"name\":\"name\",\"value\":1000}]," +
                "\"singleDoubleList\":[100.001],\"singleIntList\":[100]," +
                "\"singleLongList\":[100],\"stringList\":[\"10\",\"10\"]," +
                "\"stringValue\":\"10\"," +
                "\"type\":\"first\"}";
        String actualRepresentation = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(
            String.class);
        assertNotNull(actualRepresentation);
        assertEquals(expectedRepresentation, actualRepresentation);
    }
    @Test
    public void testGetSingleTestDtoDeserializationAsJson() {
        WebResource resource = resource().path("/CustomDto/single");
        TestDto actual = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(TestDto.class);
        assertNotNull(actual);
        assertEquals(expectedTestDto.getDoubleValue(), actual.getDoubleValue(), 0.0);
        assertEquals(expectedTestDto.getIntValue(), actual.getIntValue());
        assertEquals(expectedTestDto.getLongValue(), actual.getLongValue());
        assertEquals(expectedTestDto.getStringValue(), actual.getStringValue());
        assertEquals(expectedTestDto.getType(), actual.getType());
        assertNotNull(actual.getDoubleList());
        assertNotNull(actual.getDoubleList());
        assertNotNull(actual.getIntList());
        assertNotNull(actual.getLongList());
        assertNotNull(actual.getSingleDoubleList());
        assertNotNull(actual.getSingleIntList());
        assertNotNull(actual.getSingleLongList());
        assertLists(expectedTestDto.getDoubleList(), actual.getDoubleList());
        assertLists(expectedTestDto.getIntList(), actual.getIntList());
        assertLists(expectedTestDto.getLongList(), actual.getLongList());
        assertLists(expectedTestDto.getSingleDoubleList(), actual.getSingleDoubleList());
        assertLists(expectedTestDto.getSingleIntList(), actual.getSingleIntList());
        assertLists(expectedTestDto.getSingleLongList(), actual.getSingleLongList());
        assertLists(expectedTestDto.getStringList(), actual.getStringList());
    }

    private void assertLists(List<?> expectedList, List<?> actualList){
        assertEquals(expectedList.size(), actualList.size());
        for(int i = 0; i < expectedList.size(); i++){
            Object expected = expectedList.get(i);
            Object actual = actualList.get(i);
            if(expected instanceof Size && actual instanceof Size){
                Size expectedSize = (Size) expected;
                Size actualSize = (Size) actual;
                // Running assertions
                assertEquals(expectedSize.getAgencyId(), actualSize.getAgencyId());
                assertEquals(expectedSize.getCreatedDate(), actualSize.getCreatedDate());
                assertEquals(expectedSize.getHeight(), actualSize.getHeight());
                assertEquals(expectedSize.getId(), actualSize.getId());
                assertEquals(expectedSize.getLabel(), actualSize.getLabel());
                assertEquals(expectedSize.getModifiedDate(), actualSize.getModifiedDate());
                assertEquals(expectedSize.getWidth(), actualSize.getWidth());
            }else{
                assertEquals(expected, actual);
            }
        }
    }

    private void assertRecordSet(RecordSet<?> actual, int recordsNumber){
        assertNotNull(actual);
        assertNotNull(actual.getRecords());
        assertTrue(actual.getRecords().size() == recordsNumber);
        for(int i = 0; i < recordsNumber; i++){
            Object o = actual.getRecords().get(i);
            if(o instanceof Size){
                Size s = (Size) o;
                assertNotNull(s.getLabel());
                assertTrue(s.getId() >= 0);
            } else if(o instanceof TpTag){
                TpTag t = (TpTag) o;
                assertNotNull(t.getTagName());
                assertTrue(t.getTpTagId() > 0);
            }
        }
    }

    @Path("/")
    @Singleton
    public static class TestResource {
        @GET
        @Path("/RecordSet/TpTag/single")
        @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
        public Response getSingleRecordSet2() {
            return Response.ok(expectedRecordSetTpTag).build();
        }

        @GET
        @Path("/RecordSet/Size/single")
        @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
        public Response getSingleRecordSet() {
            return Response.ok(expectedRecordSetSize).build();
        }

        @GET
        @Path("/RecordSet/Size/Backwards/single")
        @Produces({MediaType.TEXT_XML})
        public Response getSingleRecordSetFromStringRepresentation() {
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                         + "<RecordSet>"
                         + "  <startIndex>0</startIndex>"
                         + "  <pageSize>10</pageSize>"
                         + "  <totalNumberOfRecords>2</totalNumberOfRecords>"
                         + "  <records>"
                         + "    <Size>"
                         + "      <agencyId>0</agencyId>"
                         + "      <createdDate>1969-12-31T20:00:00-04:00</createdDate>"
                         + "      <height>0</height>"
                         + "      <id>0</id>"
                         + "      <label>Label_0</label>"
                         + "      <modifiedDate>1969-12-31T20:00:00-04:00</modifiedDate>"
                         + "      <width>0</width>"
                         + "    </Size>"
                         + "    <Size>"
                         + "      <agencyId>1000</agencyId>"
                         + "      <createdDate>1969-12-31T20:00:00-04:00</createdDate>"
                         + "      <height>1000</height>"
                         + "      <id>1</id>"
                         + "      <label>Label_1</label>"
                         + "      <modifiedDate>1969-12-31T20:00:00-04:00</modifiedDate>"
                         + "      <width>1000</width>"
                         + "    </Size>"
                         + "  </records>"
                         + "</RecordSet>";
            return Response.ok(xml).build();
        }

        @GET
        @Path("/RecordSet/Size/{records}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
        public Response getMultipleRecordSet(@PathParam("records") int records) {
            List<Size> list = new ArrayList<Size>();
            for(int i = 0; i < records;  i++){
                Size size = new Size();
                long n = (long) i * 1000;
                size.setAgencyId(n);
                size.setCreatedDate(new Date(0L));
                size.setHeight(n);
                size.setId((long) i);
                size.setLabel("Label_" + i);
                size.setModifiedDate(new Date(0L));
                size.setWidth(n);
                list.add(size);
            }
            RecordSet<Size> result = new RecordSet<Size>(0, 10, list.size(), list);
            return Response.ok(result ).build();
        }

        @GET
        @Path("/CustomDto/single")
        @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
        public Response getSingleTestDto() {
            return Response.ok(expectedTestDto).build();
        }
    }

}