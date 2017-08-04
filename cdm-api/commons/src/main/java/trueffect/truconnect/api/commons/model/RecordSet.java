package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.model.delivery.Tag;
import trueffect.truconnect.api.commons.model.delivery.TagAttribute;
import trueffect.truconnect.api.commons.model.delivery.TagType;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.model.dto.CampaignDetailsDTO;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeAssociationsDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeView;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupDtoForCampaigns;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.dto.PackagePlacementView;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.dto.SiteContactView;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementCampaignDTO;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.dto.SmEventDTO;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.htmlinjection.AdChoicesHtmlInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.FacebookCustomTrackingInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.HtmlInjectionType;
import trueffect.truconnect.api.commons.model.importexport.Action;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Generic JAXB List to contain several types of POJOs
 * @author Richard Jaldin, Marcelo Heredia
 *
 */
@XmlRootElement(name = "RecordSet")
@XmlType(propOrder = {"startIndex", "pageSize", "totalNumberOfRecords", "records"})
public class RecordSet<T> {

    private Integer startIndex;
    private Integer pageSize;
    private Integer totalNumberOfRecords;
    private List<T> records;

    public RecordSet() {
        records = new ArrayList<>();
    }

    public RecordSet(Integer startIndex, Integer pageSize,
            Integer totalNumberOfRecords, List<T> records) {
        this.startIndex = startIndex;
        this.pageSize = pageSize;
        this.totalNumberOfRecords = totalNumberOfRecords;
        this.records = records;
    }

    public RecordSet(List<T> records) {
        if(records == null) {
            this.startIndex = 0;
            this.pageSize = 0;
            this.totalNumberOfRecords = 0;
            this.records = Collections.<T>emptyList();
        } else {
            this.startIndex = 0;
            this.pageSize = records.size();
            this.totalNumberOfRecords = this.pageSize;
            this.records = records;
        }
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    public void setTotalNumberOfRecords(Integer totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }

    @XmlElementWrapper(name = "records")
    @XmlElements({
         @XmlElement(name = "Action", type = Action.class),
         @XmlElement(name = "AdChoicesHtmlInjectionType", type = AdChoicesHtmlInjectionType.class),
         @XmlElement(name = "Advertiser", type = Advertiser.class),
         @XmlElement(name = "Agency", type = Agency.class),
         @XmlElement(name = "AgencyContact", type = AgencyContact.class),
         @XmlElement(name = "AgencyUser", type = AgencyUser.class),
         @XmlElement(name = "Authentication", type = Authentication.class),
         @XmlElement(name = "BooleanResponse", type = BooleanResponse.class),
         @XmlElement(name = "Brand", type = Brand.class),
         @XmlElement(name = "Campaign", type = Campaign.class),
         @XmlElement(name = "CampaignCreatorContact", type = CampaignCreatorContact.class),
         @XmlElement(name = "CampaignDetails", type = CampaignDetailsDTO.class),
         @XmlElement(name = "CampaignDTO", type = CampaignDTO.class),
         @XmlElement(name = "Clickthrough", type = Clickthrough.class),
         @XmlElement(name = "Column", type = Column.class),
         @XmlElement(name = "Columns", type = Columns.class),
         @XmlElement(name = "Contact", type = Contact.class),
         @XmlElement(name = "CookieDomain", type = CookieDomain.class),
         @XmlElement(name = "CookieDomainDTO", type = CookieDomainDTO.class),
         @XmlElement(name = "CookieOperation", type = CookieOperation.class),
         @XmlElement(name = "CookieOperationValRef", type = CookieOperationValRef.class),
         @XmlElement(name = "CookieOperationValue", type = CookieOperationValue.class),
         @XmlElement(name = "CookieTargetTemplate", type = CookieTargetTemplate.class),
         @XmlElement(name = "CostDetail", type = CostDetail.class),
         @XmlElement(name = "Creative", type = Creative.class),
         @XmlElement(name = "CreativeAssociationsDTO", type = CreativeAssociationsDTO.class),
         @XmlElement(name = "CreativeGroup", type = CreativeGroup.class),
         @XmlElement(name = "CreativeGroupCreative", type = CreativeGroupCreative.class),
         @XmlElement(name = "CreativeGroupCreativeView", type = CreativeGroupCreativeView.class),
         @XmlElement(name = "CreativeGroupDtoForCampaigns", type = CreativeGroupDtoForCampaigns.class),
         @XmlElement(name = "CreativeGroupTarget", type = CreativeGroupTarget.class),
         @XmlElement(name = "CreativeInsertion", type = CreativeInsertion.class),
         @XmlElement(name = "CreativeInsertionFilterParam", type = CreativeInsertionFilterParam.class),
         @XmlElement(name = "CreativeInsertionRawDataView", type = CreativeInsertionRawDataView.class),
         @XmlElement(name = "CreativeInsertionView", type = CreativeInsertionView.class),
         @XmlElement(name = "Dataset", type = DatasetConfigView.class),
         @XmlElement(name = "Error", type = Error.class),
         @XmlElement(name = "Errors", type = Errors.class),
         @XmlElement(name = "ExtendedProperties", type = ExtendedProperties.class),
         @XmlElement(name = "FacebookCustomTrackingInjectionType", type = FacebookCustomTrackingInjectionType.class),
         @XmlElement(name = "GeoLocation", type = GeoLocation.class),
         @XmlElement(name = "GeoTarget", type = GeoTarget.class),
         @XmlElement(name = "HtmlInjectionTags", type = HtmlInjectionTags.class),
         @XmlElement(name = "HtmlInjectionTagAssociation", type = HtmlInjectionTagAssociation.class),
         @XmlElement(name = "HtmlInjectionType", type = HtmlInjectionType.class),
         @XmlElement(name = "InsertionOrder", type = InsertionOrder.class),
         @XmlElement(name = "InsertionOrderStatus", type = InsertionOrderStatus.class),
         @XmlElement(name = "Long", type = Long.class),
         @XmlElement(name = "MediaBuy", type = MediaBuy.class),
         @XmlElement(name = "MediaBuyCampaign", type = MediaBuyCampaign.class),
         @XmlElement(name = "Metrics", type = Metrics.class),
         @XmlElement(name = "OauthKey", type = OauthKey.class),
         @XmlElement(name = "Organization", type = Organization.class),
         @XmlElement(name = "Package", type = Package.class),
         @XmlElement(name = "PackagePlacementView", type = PackagePlacementView.class),
         @XmlElement(name = "Placement", type = Placement.class),
         @XmlElement(name = "PlacementActionTagAssocParam", type = PlacementActionTagAssocParam.class),
         @XmlElement(name = "PlacementCostDetail", type = PlacementCostDetail.class),
         @XmlElement(name = "PlacementStatus", type = PlacementStatus.class),
         @XmlElement(name = "PlacementView", type = PlacementView.class),
         @XmlElement(name = "Publisher", type = Publisher.class),
         @XmlElement(name = "RecordSet", type = RecordSet.class),
         @XmlElement(name = "RolePermission", type = RolePermission.class),
         @XmlElement(name = "Schedule", type = Schedule.class),
         @XmlElement(name = "ScheduledPlacement", type = ScheduledPlacement.class),
         @XmlElement(name = "ScheduleEntry", type = ScheduleEntry.class),
         @XmlElement(name = "ScheduleSet", type = ScheduleSet.class),
         @XmlElement(name = "SearchCriteria", type = SearchCriteria.class),
         @XmlElement(name = "ServiceResponse", type = ServiceResponse.class),
         @XmlElement(name = "Site", type = Site.class),
         @XmlElement(name = "SiteContact", type = SiteContact.class),
         @XmlElement(name = "SiteContactView", type = SiteContactView.class),
         @XmlElement(name = "SiteMeasurement", type = SiteMeasurement.class),
         @XmlElement(name = "SiteMeasurementCampaign", type = SiteMeasurementCampaign.class),
         @XmlElement(name = "SiteMeasurementCampaignDTO", type = SiteMeasurementCampaignDTO.class),
         @XmlElement(name = "SiteMeasurementDTO", type = SiteMeasurementDTO.class),
         @XmlElement(name = "SiteSection", type = SiteSection.class),
         @XmlElement(name = "Size", type = Size.class),
         @XmlElement(name = "SmEvent", type = SmEvent.class),
         @XmlElement(name = "SmEventDTO", type = SmEventDTO.class),
         @XmlElement(name = "SmEventPing", type = SmEventPing.class),
         @XmlElement(name = "SmGroup", type = SmGroup.class),
         @XmlElement(name = "SuccessResponse", type = SuccessResponse.class),
         @XmlElement(name = "Tag", type = Tag.class),
         @XmlElement(name = "TagAttribute", type = TagAttribute.class),
         @XmlElement(name = "TagType", type = TagType.class),
         @XmlElement(name = "TpTag", type = TpTag.class),
         @XmlElement(name = "Trafficking", type = Trafficking.class),
         @XmlElement(name = "TraffickingArrayOfint", type = TraffickingArrayOfint.class),
         @XmlElement(name = "User", type = User.class),
         @XmlElement(name = "UserAdvertiser", type = UserAdvertiser.class),
         @XmlElement(name = "UserDomain", type = UserDomain.class),
         @XmlElement(name = "UserView", type = UserView.class)
     })
    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        // Modified to avoid showing records
        return "RecordSet [startIndex=" + startIndex + ", pageSize=" + pageSize
                + ", totalNumberOfRecords=" + totalNumberOfRecords;
    }
}
