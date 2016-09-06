package trueffect.truconnect.api.commons.model.dto.adm;

import com.trueffect.delivery.formats.adm.DatasetConfig;

import org.apache.commons.lang.builder.ToStringBuilder;
import scala.Option;
import scala.collection.Iterator;
import scala.collection.JavaConversions;
import scala.collection.mutable.StringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * DTO that exposes a {@code DatasetConfig} through TruAdvertiser
 * Created by jgearheart on 11/3/15.
 * @author Jason Gearheart
 * @author Marcelo Heredia
 */
@XmlRootElement(name = "Dataset")
public class DatasetConfigView {
    private UUID datasetId;
    private Long agencyId;
    private Long advertiserId;
    // Advertiser Name
    private String advertiserName;
    private String domain;
    // File Name
    private String fileNamePrefix;
    // Cookie Expiration
    private Integer cookieExpirationDays;
    // File Frequency
    private Long ttlExpirationSeconds;
    // Extractable Cookies
    private CookieList cookiesToCapture;
    // Content Channels
    private List<String> contentChannels;
    // Key Type "Cookie"
    private String matchCookieName;
    // Alias
    private String alias;
    // Status
    private boolean active;
    // Cookie Overwrite Exceptions
    private CookieList durableCookies;
    // Fail-through Defaults - Cookies List
    private FailThroughDefaults failThroughDefaults;
    private String ftpAccount;
    private String path;
    private Date latestUpdate;

    public DatasetConfigView() {}

    public DatasetConfigView(DatasetConfig config) {
        this.ttlExpirationSeconds = config.ttlExpirationSeconds();
        this.advertiserId = config.advertiserId();
        this.agencyId = config.agencyId();
        this.cookieExpirationDays = config.cookieExpirationDays();
        if(config.cookiesToCapture() != null && config.cookiesToCapture().cookies() != null) {
            scala.collection.Iterator<String> iterator = config.cookiesToCapture().cookies().iterator();
            CookieList cookieList = new CookieList();
            cookieList.setEnabled(config.cookiesToCapture().isEnabled());
            cookieList.setCookies(new ArrayList<String>());
            while(iterator.hasNext()){
                cookieList.getCookies().add(iterator.next());
            }
            this.cookiesToCapture = cookieList;
        } else {
            this.cookiesToCapture = null;
        }
        this.datasetId = config.id();
        if(config.failThroughDefaults() != null) {
            Iterator<com.trueffect.delivery.formats.adm.Cookie> iterator = config.failThroughDefaults().defaultCookieList().iterator();
            List<Cookie> cookies = new ArrayList<>();
            while(iterator.hasNext()){
                com.trueffect.delivery.formats.adm.Cookie cookie = iterator.next();
                cookies.add(new Cookie(cookie.name(), cookie.value()));
            }

            Option<String> stringOption = config.failThroughDefaults().defaultKey();
            this.failThroughDefaults = new FailThroughDefaults(
                    config.failThroughDefaults().isEnabled(),
                    config.failThroughDefaults().defaultType().toString(),
                    stringOption.isDefined() ? stringOption.get() : null ,
                    cookies);
        }
        this.domain = config.domain();

        this.fileNamePrefix = config.fileNamePrefix();
        if(config.matchCookieName().isDefined()) {
            this.matchCookieName = config.matchCookieName().get();
        }

        if(config.durableCookies() != null && config.durableCookies().cookies() != null) {
            scala.collection.Iterator<String> iterator = config.durableCookies().cookies().iterator();
            CookieList cookieList = new CookieList();
            cookieList.setEnabled(config.durableCookies().isEnabled());
            cookieList.setCookies(new ArrayList<String>());
            while(iterator.hasNext()){
                cookieList.getCookies().add(iterator.next());
            }
            this.durableCookies = cookieList;
        } else {
            this.durableCookies = null;
        }

        String[] pathParts = config.path().split("/");
        this.ftpAccount = pathParts[3];
        StringBuilder pathBuilder = new StringBuilder();
        String separator = "";
        for(int i = 4; i < pathParts.length; i++) {
            pathBuilder.append(separator);
            pathBuilder.append(pathParts[i]);
            separator = "/";
        }
        this.path = pathBuilder.toString();
        if(config.alias().isDefined()) {
            this.alias = config.alias().get();
        }
        this.active = config.isActive();
        if(config.latestUpdate().isDefined()) {
            this.latestUpdate = config.latestUpdate().get().toDate();
        }
        if(config.contentChannels() != null && !config.contentChannels(). isEmpty()) {
            this.contentChannels = JavaConversions.seqAsJavaList(config.contentChannels().toSeq());
        }

    }

    public UUID getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(UUID datasetId) {
        this.datasetId = datasetId;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public Integer getCookieExpirationDays() {
        return cookieExpirationDays;
    }

    public void setCookieExpirationDays(Integer cookieExpirationDays) {
        this.cookieExpirationDays = cookieExpirationDays;
    }

    public Long getTtlExpirationSeconds() {
        return ttlExpirationSeconds;
    }

    public void setTtlExpirationSeconds(Long ttlExpirationSeconds) {
        this.ttlExpirationSeconds = ttlExpirationSeconds;
    }

    // TODO to be reenabled in next PR
//    @XmlElementWrapper(name = "cookiesToCapture")
//    @XmlElement(name = "cookieName", type = String.class)
    public CookieList getCookiesToCapture() {
        return cookiesToCapture;
    }

    public void setCookiesToCapture(CookieList cookiesToCapture) {
        this.cookiesToCapture = cookiesToCapture;
    }

    // TODO to be reenabled in next PR
//    @XmlElementWrapper(name = "durableCookies")
//    @XmlElement(name = "cookieName", type = String.class)
    public CookieList getDurableCookies() {
        return durableCookies;
    }

    public void setDurableCookies(CookieList durableCookies) {
        this.durableCookies = durableCookies;
    }

    public String getMatchCookieName() {
        return matchCookieName;
    }

    public void setMatchCookieName(String matchCookieName) {
        this.matchCookieName = matchCookieName;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public String getFtpAccount() {
        return ftpAccount;
    }

    public void setFtpAccount(String ftpAccount) {
        this.ftpAccount = ftpAccount;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(Date latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public List<String> getContentChannels() {
        return contentChannels;
    }

    public void setContentChannels(List<String> contentChannels) {
        this.contentChannels = contentChannels;
    }

    public FailThroughDefaults getFailThroughDefaults() {
        return failThroughDefaults;
    }

    public void setFailThroughDefaults(FailThroughDefaults failThroughDefaults) {
        this.failThroughDefaults = failThroughDefaults;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("datasetId", datasetId)
                .append("agencyId", agencyId)
                .append("advertiserName", advertiserName)
                .append("advertiserId", advertiserId)
                .append("domain", domain)
                .append("fileNamePrefix", fileNamePrefix)
                .append("cookieExpirationDays", cookieExpirationDays)
                .append("ttlExpirationSeconds", ttlExpirationSeconds)
                .append("cookiesToCapture", cookiesToCapture)
                .append("failThroughDefaults", failThroughDefaults)
                .append("durableCookies", durableCookies)
                .append("matchCookieName", matchCookieName)
                .append("ftpAccount", ftpAccount)
                .append("path", path)
                .append("alias", alias)
                .append("active", active)
                .append("latestUpdate", latestUpdate)
                .append("contentChannels", contentChannels)
                .toString();
    }
}
