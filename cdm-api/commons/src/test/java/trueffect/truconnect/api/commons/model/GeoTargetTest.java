package trueffect.truconnect.api.commons.model;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.enums.LocationType;

import com.github.javafaker.Faker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeoTargetTest {

    public static final Random random = new Random();
    private static final Faker faker = new Faker(random);

    static private Long get0or1() {
        return random.nextBoolean() ? 1L : 0L;
    }

    static private GeoTarget createGeoTarget() {
        GeoTarget geoTarget = new GeoTarget();
        int pick = random.nextInt(LocationType.values().length);
        geoTarget.setTypeCode(LocationType.values()[pick].getCode());
        geoTarget.setAntiTarget(get0or1());
        return geoTarget;
    }

    static private GeoLocation createCountryLocation() {
        GeoLocation location = new GeoLocation();
        location.setCode(faker.lorem().fixedString(5));
        location.setId(random.nextLong());
        location.setLabel(faker.lorem().fixedString(10));
        location.setTypeId(random.nextLong());
        return location;
    }
    @Test
    public void addingTargetWhenTargetsNotInitialized() {
        GeoTarget geoTarget = new GeoTarget();
        assertThat(geoTarget.getTargets(), is(nullValue()));
        GeoLocation geoLocation = createCountryLocation();
        geoTarget.addTarget(geoLocation);
        assertThat(geoTarget.getTargets(), hasSize(1));
    }

    @Test
    public void addingTargetWhenTargetsAlreadyExist() {
        GeoTarget geoTarget = new GeoTarget();
        geoTarget.addTarget(createCountryLocation());
        assertThat(geoTarget.getTargets(), hasSize(1));
        geoTarget.addTarget(createCountryLocation());
        assertThat(geoTarget.getTargets(), hasSize(2));
    }

    @Test
    public void buildingCreativeGroupTargetsWhenTargetsIsEmpty() {
        GeoTarget geoTarget = createGeoTarget();
        geoTarget.setTargets(new ArrayList<GeoLocation>(0));
        List<CreativeGroupTarget> creativeGroupTargets = geoTarget.getCreativeGroupTargets();
        assertThat(creativeGroupTargets, is(empty()));
    }

    @Test
    public void buildingCreativeGroupTargetsWhenTargetsIsNull() {
        GeoTarget geoTarget = createGeoTarget();
        geoTarget.setTargets(null);
        List<CreativeGroupTarget> creativeGroupTargets = geoTarget.getCreativeGroupTargets();
        assertThat(creativeGroupTargets, is(empty()));
    }

    @Test
    public void buildingCreativeGroupTargetsWhenTargetsIsNotEmpty() {
        GeoTarget geoTarget = createGeoTarget();
        GeoLocation countryLocation = createCountryLocation();
        geoTarget.addTarget(countryLocation);
        List<CreativeGroupTarget> creativeGroupTargets = geoTarget.getCreativeGroupTargets();
        assertThat(creativeGroupTargets, hasSize(1));
        CreativeGroupTarget expectedCreativeGroupTarget = new CreativeGroupTarget();
        expectedCreativeGroupTarget.setTypeCode(geoTarget.getTypeCode());
        expectedCreativeGroupTarget.setValueId(countryLocation.getId());
        expectedCreativeGroupTarget.setAntiTarget(geoTarget.getAntiTarget());
        expectedCreativeGroupTarget.setTargetLabel(countryLocation.getLabel());
        assertThat(creativeGroupTargets, hasItem(expectedCreativeGroupTarget));
    }
}
