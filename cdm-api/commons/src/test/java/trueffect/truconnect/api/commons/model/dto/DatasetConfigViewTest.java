package trueffect.truconnect.api.commons.model.dto;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


import com.trueffect.delivery.formats.adm.Cookie;
import com.trueffect.delivery.formats.adm.CookieDefault$;
import com.trueffect.delivery.formats.adm.CookieList;
import com.trueffect.delivery.formats.adm.DatasetConfig;
import com.trueffect.delivery.formats.adm.FailThroughDefaults;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;

import com.github.javafaker.Faker;
import org.joda.time.DateTime;
import org.junit.Test;

import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.immutable.Set;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by jgearheart on 11/11/15.
 */
public class DatasetConfigViewTest {

    private static Random random = new Random();
    private static Faker faker = new Faker(random);

    @Test
    public void correctlyPopulateBasedOnDatasetConfigWithAllFieldsPopulated() {
        String ftpAccount = "foo";
        String path = "Incoming/ADM";
        Set<String> contentChannels = JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSet();
        ArrayList<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie(faker.letterify("?????"), faker.letterify("?????")));
        cookies.add(new Cookie(faker.letterify("?????"), faker.letterify("?????")));
        cookies.add(new Cookie(faker.letterify("?????"), faker.letterify("?????")));
        DatasetConfig config = new DatasetConfig(
                UUID.randomUUID(),
                random.nextLong(),
                random.nextLong(),
                faker.internet().url(),
                faker.letterify("s3://ftp/" + ftpAccount + "/" + path),
                faker.letterify("?????"),
                random.nextInt(),
                JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq(),
                JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq(),
                random.nextLong(),
                new CookieList<>(random.nextBoolean(), JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq()),
                new CookieList<>(random.nextBoolean(), JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq()),
                Option.apply(faker.letterify("?????")),
                Option.apply(faker.letterify("?????")),
                random.nextBoolean(),
                Option.apply(DateTime.now()),
                contentChannels,
                new FailThroughDefaults(
                        random.nextBoolean(),
                        CookieDefault$.MODULE$,
                        Option.apply(faker.letterify("?????")),
                        JavaConversions.asScalaBuffer(cookies).toSeq()
                        )
        );

        DatasetConfigView datasetConfigView = new DatasetConfigView(config);
        assertThat(datasetConfigView.getDatasetId(), is(equalTo(config.id())));
        assertThat(datasetConfigView, is(notNullValue()));
        assertThat(datasetConfigView.getAgencyId(), is(equalTo(config.agencyId())));
        assertThat(datasetConfigView.getAdvertiserId(), is(equalTo(config.advertiserId())));
        assertThat(datasetConfigView.getDomain(), is(equalTo(config.domain())));
        assertThat(datasetConfigView.getPath(), is(equalTo(path)));
        assertThat(datasetConfigView.getFileNamePrefix(), is(equalTo(config.fileNamePrefix())));
        assertThat(datasetConfigView.getCookieExpirationDays(), is(equalTo(config.cookieExpirationDays())));

        assertThat(datasetConfigView.getTtlExpirationSeconds(), is(equalTo(config.ttlExpirationSeconds())));
        assertThat(datasetConfigView.getCookiesToCapture().getCookies(),
                is(equalTo(JavaConversions.seqAsJavaList(config.cookiesToCapture().cookies()))));
        assertThat(datasetConfigView.getDurableCookies().getCookies(),
                is(equalTo(JavaConversions.seqAsJavaList(config.durableCookies().cookies()))));
        assertThat(datasetConfigView.getMatchCookieName(),
                is(equalTo(config.matchCookieName().get())));
        assertThat(datasetConfigView.getAlias(),
                is(equalTo(config.alias().get())));
        assertThat(datasetConfigView.isActive(),
                is(equalTo(config.isActive())));
        assertThat(datasetConfigView.getLatestUpdate(),
                is(equalTo(config.latestUpdate().get().toDate())));
        assertThat(datasetConfigView.getContentChannels(),
                is(equalTo(JavaConversions.seqAsJavaList(config.contentChannels().toSeq()))));
        assertThat(datasetConfigView.getFailThroughDefaults(),
                is(notNullValue()));
        assertThat(datasetConfigView.getFailThroughDefaults().getDefaultType(),
                is(equalTo(config.failThroughDefaults().defaultType().toString())));
        assertThat(datasetConfigView.getFailThroughDefaults().getDefaultKey(),
                is(equalTo(config.failThroughDefaults().defaultKey().get())));
        assertThat(datasetConfigView.getFailThroughDefaults().getDefaultCookieList(),
                is(notNullValue()));
        assertThat(datasetConfigView.getFailThroughDefaults().getDefaultCookieList().size(),
                is(equalTo(config.failThroughDefaults().defaultCookieList().size())));
        int i = 0;
        for(trueffect.truconnect.api.commons.model.dto.adm.Cookie cookie : datasetConfigView.getFailThroughDefaults().getDefaultCookieList()) {
            assertThat(cookie.getName(),
                    is(equalTo(config.failThroughDefaults().defaultCookieList().toSeq().apply(i).name())));
            assertThat(cookie.getValue(),
                    is(equalTo(config.failThroughDefaults().defaultCookieList().toSeq().apply(i++).value())));
        }

        assertThat(datasetConfigView.getAdvertiserName(), is(nullValue()));
        assertThat(datasetConfigView.getFtpAccount(), is(equalTo(ftpAccount)));
    }

    @Test
    public void correctlyPopulateBasedOnDatasetConfigWithOptionalFieldsNone() {
        String ftpAccount = "foo";
        String path = "Incoming/ADM";
        DatasetConfig config = new DatasetConfig(
                UUID.randomUUID(),
                random.nextLong(),
                random.nextLong(),
                faker.internet().url(),
                faker.letterify("s3://ftp/" + ftpAccount + "/" + path),
                null,
                random.nextInt(),
                JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq(),
                JavaConversions.asScalaBuffer(faker.lorem().words(3)).toSeq(),
                random.nextLong(),
                null,
                null,
                Option.apply((String)null),
                Option.apply((String)null),
                random.nextBoolean(),
                Option.apply((DateTime)null),
                null,
                null
        );


        DatasetConfigView datasetConfigView = new DatasetConfigView(config);
        assertThat(datasetConfigView, is(notNullValue()));
        assertThat(datasetConfigView.getTtlExpirationSeconds(), is(equalTo(config.ttlExpirationSeconds())));
        assertThat(datasetConfigView.getAdvertiserId(), is(equalTo(config.advertiserId())));
        assertThat(datasetConfigView.getAdvertiserName(), is(nullValue()));
        assertThat(datasetConfigView.getDomain(), is(equalTo(config.domain())));
        assertThat(datasetConfigView.getAgencyId(), is(equalTo(config.agencyId())));
        assertThat(datasetConfigView.getCookieExpirationDays(), is(equalTo(config.cookieExpirationDays())));
        assertThat(datasetConfigView.getCookiesToCapture(), is(nullValue()));
        assertThat(datasetConfigView.getDatasetId(), is(equalTo(config.id())));
        assertThat(datasetConfigView.getDurableCookies(), is(nullValue()));
        assertThat(datasetConfigView.getAlias(), is(nullValue()));
        assertThat(datasetConfigView.getFileNamePrefix(), is(nullValue()));
        assertThat(datasetConfigView.getMatchCookieName(), is(nullValue()));
        assertThat(datasetConfigView.getLatestUpdate(), is(nullValue()));
        assertThat(datasetConfigView.getContentChannels(), is(nullValue()));
        assertThat(datasetConfigView.getFtpAccount(), is(equalTo(ftpAccount)));
        assertThat(datasetConfigView.getPath(), is(equalTo(path)));
    }
}
