package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trueffect.truconnect.api.crud.EntityFactory.createGeoLocation;
import static trueffect.truconnect.api.crud.EntityFactory.faker;
import static trueffect.truconnect.api.crud.EntityFactory.random;

import trueffect.truconnect.api.commons.model.GeoLocation;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.enums.LocationType;
import trueffect.truconnect.api.crud.dao.GeoLocationDao;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoLocationManagerTest extends AbstractManagerTest {
    private GeoLocationDao geoLocationDaoMock;
    private GeoLocationManager geoLocationManager;
    private List<GeoLocation> zips;
    private List<GeoLocation> dmas;
    private List<GeoLocation> states;
    private List<GeoLocation> countries;
    private Map<LocationType, Map<String, GeoLocation>> parents;

    @Before
    public void init() throws Exception {
        geoLocationDaoMock = mock(GeoLocationDao.class);
        geoLocationManager = new GeoLocationManager(geoLocationDaoMock, accessControlMockito);

        // Countries
        countries = createItems(LocationType.COUNTRY, null, 2);

        // States
        long stateId = random.nextLong();
        List<GeoLocation> states1 = createItems(LocationType.STATE, countries.get(0), 2, stateId);
        List<GeoLocation> states2 = createItems(LocationType.STATE, countries.get(1), 2, stateId);

        // DMAs
        long dmaId = random.nextLong();
        List<GeoLocation> dmas1_1 = createItems(LocationType.DMA, states1.get(0), 2, dmaId);
        List<GeoLocation> dmas1_2 = createItems(LocationType.DMA, states1.get(1), 2, dmaId);
        List<GeoLocation> dmas2_1 = createItems(LocationType.DMA, states2.get(0), 2, dmaId);
        List<GeoLocation> dmas2_2 = createItems(LocationType.DMA, states2.get(1), 2, dmaId);

        // Zips
        long zipId = random.nextLong();
        List<GeoLocation> zips1_1_1 = createZips(dmas1_1.get(0), states1.get(0), 2, zipId);
        List<GeoLocation> zips1_1_2 = createZips(dmas1_1.get(1), states1.get(0), 2, zipId);
        List<GeoLocation> zips1_2_1 = createZips(dmas1_2.get(0), states1.get(1), 2, zipId);
        List<GeoLocation> zips1_2_2 = createZips(dmas1_2.get(1), states1.get(1), 2, zipId);
        List<GeoLocation> zips2_1_1 = createZips(dmas2_1.get(0), states2.get(0), 2, zipId);
        List<GeoLocation> zips2_1_2 = createZips(dmas2_1.get(1), states2.get(0), 2, zipId);
        List<GeoLocation> zips2_2_1 = createZips(dmas2_2.get(0), states2.get(1), 2, zipId);
        List<GeoLocation> zips2_2_2 = createZips(dmas2_2.get(1), states2.get(1), 2, zipId);

        states = new ArrayList<>();
        states.addAll(states1);
        states.addAll(states2);

        dmas = new ArrayList<>();
        dmas.addAll(dmas1_1);
        dmas.addAll(dmas1_2);
        dmas.addAll(dmas2_1);
        dmas.addAll(dmas2_2);

        zips = new ArrayList<>();
        zips.addAll(zips1_1_1);
        zips.addAll(zips1_1_2);
        zips.addAll(zips1_2_1);
        zips.addAll(zips1_2_2);
        zips.addAll(zips2_1_1);
        zips.addAll(zips2_1_2);
        zips.addAll(zips2_2_1);
        zips.addAll(zips2_2_2);

        parents = new HashMap<>();
        parents.put(LocationType.COUNTRY, GeoLocationManager.codeMap(countries));
        parents.put(LocationType.STATE, GeoLocationManager.codeMap(states));
        parents.put(LocationType.DMA, GeoLocationManager.codeMap(dmas));
        parents.put(LocationType.ZIP, GeoLocationManager.codeMap(zips));

        when(geoLocationDaoMock.openSession()).thenReturn(sqlSessionMock);
        when(geoLocationDaoMock.getLocations(LocationType.COUNTRY, sqlSessionMock)).thenReturn(countries);
        when(geoLocationDaoMock.getLocations(LocationType.STATE, sqlSessionMock)).thenReturn(states);
        when(geoLocationDaoMock.getLocations(LocationType.DMA, sqlSessionMock)).thenReturn(dmas);
        when(geoLocationDaoMock.getLocations(LocationType.ZIP, sqlSessionMock)).thenReturn(zips);
    }

    @Test
    public void testGettingCountries() throws Exception {
        when(geoLocationDaoMock.openSession()).thenReturn(sqlSessionMock);
        when(geoLocationDaoMock.getLocations(LocationType.COUNTRY, sqlSessionMock)).thenReturn(countries);

        RecordSet<GeoLocation> returnedCountries = geoLocationManager.getCountries();
        assertThat(returnedCountries, is(notNullValue()));
        assertThat(returnedCountries.getRecords(), hasSize(greaterThan(0)));
        for(GeoLocation country : returnedCountries.getRecords()) {
            assertThat("Incorrect country label", country.getCountryLabel(), is(equalTo(country.getLabel())));
            assertThat("Incorrect state label", country.getStateLabel(), is(nullValue()));
            assertThat("Incorrect dma label", country.getDmaLabel(), is(nullValue()));
            assertThat("Incorrect zip label", country.getZipLabel(), is(nullValue()));
        }
    }
    
    @Test
    public void testGettingStates() throws Exception {
        when(geoLocationDaoMock.openSession()).thenReturn(sqlSessionMock);
        when(geoLocationDaoMock.getLocations(LocationType.STATE, sqlSessionMock)).thenReturn(states);

        RecordSet<GeoLocation> returnedStates = geoLocationManager.getStates();
        assertThat(returnedStates, is(notNullValue()));
        assertThat(returnedStates.getRecords(), hasSize(greaterThan(0)));
        for(GeoLocation state : returnedStates.getRecords()) {
            assertThat("Incorrect country label", state.getCountryLabel(), is(notNullValue()));
            assertThat("Incorrect state label", state.getStateLabel(), is(equalTo(state.getLabel())));
            assertThat("Incorrect dma label", state.getDmaLabel(), is(nullValue()));
            assertThat("Incorrect zip label", state.getZipLabel(), is(nullValue()));
        }
    }
    
    @Test
    public void testGettingDMAs() throws Exception {
        when(geoLocationDaoMock.openSession()).thenReturn(sqlSessionMock);
        when(geoLocationDaoMock.getLocations(LocationType.DMA, sqlSessionMock)).thenReturn(dmas);

        RecordSet<GeoLocation> returnedDMAs = geoLocationManager.getDMAs();
        assertThat(returnedDMAs, is(notNullValue()));
        assertThat(returnedDMAs.getRecords(), hasSize(greaterThan(0)));
        for(GeoLocation dma : returnedDMAs.getRecords()) {
            assertThat("Incorrect country label", dma.getCountryLabel(), is(notNullValue()));
            assertThat("Incorrect state label", dma.getStateLabel(), is(notNullValue()));
            assertThat("Incorrect dma label", dma.getDmaLabel(), is(equalTo(dma.getLabel())));
            assertThat("Incorrect zip label", dma.getZipLabel(), is(nullValue()));
        }
    }
    
    @Test
    public void testGettingZipCodes() throws Exception {
        when(geoLocationDaoMock.openSession()).thenReturn(sqlSessionMock);
        when(geoLocationDaoMock.getLocations(LocationType.ZIP, sqlSessionMock)).thenReturn(zips);

        RecordSet<GeoLocation> returnedZips = geoLocationManager.getZipCodes();
        assertThat(returnedZips, is(notNullValue()));
        assertThat(returnedZips.getRecords(), hasSize(greaterThan(0)));
        for(GeoLocation zip : returnedZips.getRecords()) {
            assertThat("Incorrect country label", zip.getCountryLabel(), is(notNullValue()));
            assertThat("Incorrect state label", zip.getStateLabel(), is(notNullValue()));
            assertThat("Incorrect dma label", zip.getDmaLabel(), is(notNullValue()));
            assertThat("Incorrect zip label", zip.getZipLabel(), is(equalTo(zip.getLabel())));
        }
    }

    @Test
    public void testParentRelationship() throws Exception {
        countries = createItems(LocationType.COUNTRY, null, 1);
        GeoLocation country = countries.get(0);
        states = createItems(LocationType.STATE, country, 1);
        GeoLocation state = states.get(0);
        dmas = createItems(LocationType.DMA, state, 1);
        GeoLocation dma = dmas.get(0);
        zips = createZips(dma, state, 1);
        GeoLocation zip = zips.get(0);


        when(geoLocationDaoMock.getLocations(LocationType.COUNTRY, sqlSessionMock)).thenReturn(countries);
        when(geoLocationDaoMock.getLocations(LocationType.STATE, sqlSessionMock)).thenReturn(states);
        when(geoLocationDaoMock.getLocations(LocationType.DMA, sqlSessionMock)).thenReturn(dmas);
        when(geoLocationDaoMock.getLocations(LocationType.ZIP, sqlSessionMock)).thenReturn(zips);

        RecordSet<GeoLocation> zipCodes = geoLocationManager.getZipCodes();
        assertThat(zipCodes.getRecords(), hasSize(1));
        GeoLocation zipCode = zipCodes.getRecords().get(0);
        assertThat("Incorrect country label", zipCode.getCountryLabel(), is(equalTo(country.getLabel())));
        assertThat("Incorrect state label", zipCode.getStateLabel(), is(equalTo(state.getLabel())));
        assertThat("Incorrect dma label", zipCode.getDmaLabel(), is(equalTo(dma.getLabel())));
        assertThat("Incorrect zip label", zipCode.getZipLabel(), is(equalTo(zip.getLabel())));
    }

    @Test
    public void testGettingParentCodeWithNullAttribute() {
        GeoLocation geoLocation = createGeoLocation();
        String expectedCode = faker.name().suffix();
        String testLabel = random.nextInt() + " - " + faker.name().lastName() + " - " + expectedCode;
        geoLocation.setLabel(testLabel);
        geoLocation.setParentCode(null);
        geoLocation.setLocationType(LocationType.ZIP);
        String parentCode = GeoLocationManager.parentCode(geoLocation);
        assertThat(parentCode, is(equalTo(expectedCode)));
    }

    @Test
    public void testGettingParentCodeWithNullAttributeAndBadFormat() {
        GeoLocation geoLocation = createGeoLocation();
        String testLabel = random.nextInt() + " : " + faker.name().lastName() + " : " + faker.name().suffix();
        geoLocation.setLabel(testLabel);
        geoLocation.setParentCode(null);
        geoLocation.setLocationType(LocationType.ZIP);
        String parentCode = GeoLocationManager.parentCode(geoLocation);
        assertThat(parentCode, is(nullValue()));
    }

    @Test
    public void testGettingParentCodeWithNonNullAttribute() {
        GeoLocation geoLocation = createGeoLocation();
        String expectedCode = faker.name().suffix();
        String testLabel = random.nextInt() + " - " + faker.name().lastName() + " - " + faker.name().suffix();
        geoLocation.setLabel(testLabel);
        geoLocation.setParentCode(expectedCode);
        geoLocation.setLocationType(LocationType.ZIP);
        String parentCode = GeoLocationManager.parentCode(geoLocation);
        assertThat(parentCode, is(equalTo(expectedCode)));
    }

    @Test
    public void testGettingParentsForAZipThatDoesNotBelongToDMA() throws Exception {
        countries = createItems(LocationType.COUNTRY, null, 1);
        GeoLocation country = countries.get(0);
        states = createItems(LocationType.STATE, country, 1);
        GeoLocation state = states.get(0);
        dmas = createItems(LocationType.DMA, state, 1);
        zips = createZips(null, state, 1);
        GeoLocation zip = zips.get(0);

        when(geoLocationDaoMock.getLocations(LocationType.COUNTRY, sqlSessionMock)).thenReturn(countries);
        when(geoLocationDaoMock.getLocations(LocationType.STATE, sqlSessionMock)).thenReturn(states);
        when(geoLocationDaoMock.getLocations(LocationType.DMA, sqlSessionMock)).thenReturn(dmas);
        when(geoLocationDaoMock.getLocations(LocationType.ZIP, sqlSessionMock)).thenReturn(zips);

        RecordSet<GeoLocation> zipCodes = geoLocationManager.getZipCodes();
        assertThat("Incorrect size", zipCodes.getRecords(), hasSize(1));
        GeoLocation zipCode = zipCodes.getRecords().get(0);
        assertThat("Incorrect country label", zipCode.getCountryLabel(), is(equalTo(country.getLabel())));
        assertThat("Incorrect state label", zipCode.getStateLabel(), is(equalTo(state.getLabel())));
        assertThat("Incorrect dma label", zipCode.getDmaLabel(), is(nullValue()));
        assertThat("Incorrect zip label", zipCode.getZipLabel(), is(equalTo(zip.getLabel())));
    }

    @Test
    public void testCodeMap() {
        Map<String, GeoLocation> codeMap = GeoLocationManager.codeMap(countries);
        assertThat(codeMap.keySet(), hasSize(2));
    }

    @Test
    public void testGettingADirectParent() {
        countries = createItems(LocationType.COUNTRY, null, 1);
        GeoLocation country = countries.get(0);
        states = createItems(LocationType.STATE, country, 1);
        GeoLocation state = states.get(0);
        dmas = createItems(LocationType.DMA, state, 1);
        GeoLocation dma = dmas.get(0);
        zips = createZips(dma, state, 1);
        GeoLocation zip = zips.get(0);

        parents.put(LocationType.COUNTRY, GeoLocationManager.codeMap(countries));
        parents.put(LocationType.STATE, GeoLocationManager.codeMap(states));
        parents.put(LocationType.DMA, GeoLocationManager.codeMap(dmas));
        parents.put(LocationType.ZIP, GeoLocationManager.codeMap(zips));

        when(geoLocationDaoMock.getLocations(LocationType.COUNTRY, sqlSessionMock)).thenReturn(countries);
        when(geoLocationDaoMock.getLocations(LocationType.STATE, sqlSessionMock)).thenReturn(states);
        when(geoLocationDaoMock.getLocations(LocationType.DMA, sqlSessionMock)).thenReturn(dmas);
        when(geoLocationDaoMock.getLocations(LocationType.ZIP, sqlSessionMock)).thenReturn(zips);

        GeoLocation parent = GeoLocationManager.getParent(LocationType.ZIP, zip, parents);
        assertThat(parent, is(equalTo(dma)));
    }

    @Test
    public void testGettingAnIndirectParent() {
        countries = createItems(LocationType.COUNTRY, null, 1);
        GeoLocation country = countries.get(0);
        states = createItems(LocationType.STATE, country, 1);
        GeoLocation state = states.get(0);
        dmas = createItems(LocationType.DMA, state, 1);
        zips = createZips(null, state, 1);
        GeoLocation zip = zips.get(0);
        zip.setLocationType(LocationType.ZIP);

        parents.put(LocationType.COUNTRY, GeoLocationManager.codeMap(countries));
        parents.put(LocationType.STATE, GeoLocationManager.codeMap(states));
        parents.put(LocationType.DMA, GeoLocationManager.codeMap(dmas));
        parents.put(LocationType.ZIP, GeoLocationManager.codeMap(zips));

        when(geoLocationDaoMock.getLocations(LocationType.COUNTRY, sqlSessionMock)).thenReturn(countries);
        when(geoLocationDaoMock.getLocations(LocationType.STATE, sqlSessionMock)).thenReturn(states);
        when(geoLocationDaoMock.getLocations(LocationType.DMA, sqlSessionMock)).thenReturn(dmas);
        when(geoLocationDaoMock.getLocations(LocationType.ZIP, sqlSessionMock)).thenReturn(zips);

        GeoLocation parent = GeoLocationManager.getParent(LocationType.ZIP, zip, parents);
        assertThat(parent, is(equalTo(state)));
    }

    private List<GeoLocation> createItems(LocationType type, GeoLocation parent, int size, Long typeId, String ancestor) {
        List<GeoLocation> locations = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            GeoLocation location = createGeoLocation();
            location.setTypeId(typeId);
            if(parent == null) {
                location.setParentCode(null);
            } else {
                location.setParentCode(parent.getCode());
            }
            if(type == LocationType.ZIP) {
                location.setLabel(random.nextInt(100000) + " - " + faker.name().lastName() + " - " + ancestor);
            }
            locations.add(location);
        }
        return locations;
    }

    private List<GeoLocation> createZips(GeoLocation dma, GeoLocation state, int size, Long typeId) {
        return createItems(LocationType.ZIP, dma, size, typeId, state.getCode());
    }

    private List<GeoLocation> createZips(GeoLocation dma, GeoLocation state, int size) {
        Long typeId = random.nextLong();
        return createZips(dma, state, size, typeId);
    }

    private List<GeoLocation> createItems(LocationType type, GeoLocation parent, int size) {
        Long typeId = random.nextLong();
        return createItems(type, parent, size, typeId);
    }

    private List<GeoLocation> createItems(LocationType type, GeoLocation parent, int size, Long typeId) {
        return createItems(type, parent, size, typeId, "");
    }
}
