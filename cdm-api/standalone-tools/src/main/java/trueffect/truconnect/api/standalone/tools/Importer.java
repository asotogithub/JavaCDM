package trueffect.truconnect.api.standalone.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Brand;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.CookieDomain;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.publik.client.exceptions.ConflictException;
import trueffect.truconnect.api.publik.client.factory.PublicServiceFactory;
import trueffect.truconnect.api.standalone.converter.ServiceModelMapper;
import trueffect.truconnect.api.standalone.model.Agency;

public class Importer {

    private PublicServiceFactory proxiesFactory;
    private Long agencyId;
    private String basePathToZipFile;
    private String zipFileName;

    public Importer(String baseUrl, String version, Long agencyId, String username, String password, String basePathToZipFile, String zipFileName) {
        String serviceUrl = baseUrl + "cms/" + version;
        String authenticationUrl = baseUrl + "oauth/" + version;
        proxiesFactory = new PublicServiceFactory(serviceUrl, authenticationUrl, MediaType.TEXT_XML, username, password);
        this.agencyId = agencyId;
        this.basePathToZipFile = basePathToZipFile;
        this.zipFileName = zipFileName;
    }

    public void importCampaign() {
        // 1. Read ZipFile and uncompress into a Temporal folder.
        try {
            Utils.uncompressFile(new File(basePathToZipFile + zipFileName + ".zip"), new File(zipFileName));
        } catch (IOException e1) { System.out.println("Bad ZIP file"); }

        // 2. Read the XML into a POJOs.
        Agency agency = readCampaignHierarchyIntoAObject();

        try {
            // 3. Process and save data using the clients.
            saveCampaignHierarchy(agency);

            // 4. Remove the temporal folder and its files.
            Utils.cleanTheHouse(new File(zipFileName));

            // 5. Return message of success
            System.out.println("Campaign imported successfuly!");
        } catch (Exception e) {
            System.out.println("Duplicated Campaign hierarchy. It cannot be imported!");
        }
    }

    private Agency readCampaignHierarchyIntoAObject() {
        Agency result = null;
        try {
            File file = new File(zipFileName + "/" + zipFileName + ".xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Agency.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            result = (Agency) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) { System.out.println("Bad XML for Campaign hierarchy."); }
        return result;
    }

    private void saveCampaignHierarchy(Agency agency) throws Exception {
        //1. Save Advertiser
        Advertiser advertiser = ServiceModelMapper.toServiceModel(agency.getAdvertiser(), agencyId);
        try {
            advertiser = proxiesFactory.getAdvertiserProxy().create(advertiser);
        } catch (ConflictException e) {
            try {
                System.out.println("Using existing Advertiser.");
                RecordSet<Advertiser> advertisers = proxiesFactory.getAdvertiserProxy().find("name equals to \"" + advertiser.getName() + "\"");
                advertiser = advertisers.getRecords().get(0);
            } catch (Exception e1) {  }
        } catch (Exception e) { }

        //2. Save Brand
        Brand brand = ServiceModelMapper.toServiceModel(agency.getAdvertiser().getBrand(), advertiser.getId());
        try {
            brand = proxiesFactory.getBrandProxy().create(brand);
        } catch (ConflictException e) {
            try {
                System.out.println("Using existing Brand.");
                RecordSet<Brand> brands = proxiesFactory.getBrandProxy().find("name equals to \"" + brand.getName() + "\"");
                brand = brands.getRecords().get(0);
            } catch (Exception e1) {    }
        } catch (Exception e) { }

        //3. Save CookieDomain
        CookieDomain domain = ServiceModelMapper.toServiceModel(agency.getAdvertiser().getBrand().getCampaign().getCookieDomain(), agencyId);
        domain = proxiesFactory.getCookieDomainProxy().create(domain);

        //4. Save Campaign
        trueffect.truconnect.api.standalone.model.Campaign oldCampaign = agency.getAdvertiser().getBrand().getCampaign();
        Campaign campaign = ServiceModelMapper.toServiceModel(agency.getAdvertiser().getBrand().getCampaign(), agencyId, advertiser.getId(), brand.getId(), domain.getId());
        campaign = proxiesFactory.getCampaignProxy().create(campaign);

        //5. Save Sizes and keep its new ids in the form [(oldId, newSizeObject)].
        HashMap<Long, Size> newSizes = new HashMap<Long, Size>();
        if(agency.getSizes() != null){
            for (trueffect.truconnect.api.standalone.model.Size oldSize : agency.getSizes()) {
                Size size = ServiceModelMapper.toServiceModel(oldSize, agencyId);
                size = proxiesFactory.getSizeProxy().create(size);
                newSizes.put(oldSize.getId(), size);
            }
        }

        //6. Save Publishers and keep its new ids in the form [(oldId, newPublisherObject)].
        HashMap<Long, Publisher> newPublishers = new HashMap<Long, Publisher>();
        if(agency.getPublishers() != null){
            for (trueffect.truconnect.api.standalone.model.Publisher oldPublisher : agency.getPublishers()) {
                Publisher publisher = ServiceModelMapper.toServiceModel(oldPublisher, agencyId);
                publisher = proxiesFactory.getPublisherProxy().create(publisher);
                newPublishers.put(oldPublisher.getId(), publisher);
            }
        }

        //7. Get default MediaBuy, it should be the equivalent of exporter MediaBuy [(oldId, newMediaBuyObject)].
        MediaBuy mediaBuy = null;
        try {
            mediaBuy = proxiesFactory.getMediaBuyProxy().getByCampaign(campaign.getId());
        } catch (Exception e) { e.printStackTrace(); }

        //8. Save Creatives and keep its new ids in the form [(oldId, newCreativeObject)].
        HashMap<Long, Creative> newCreatives = new HashMap<Long, Creative>();
        if(oldCampaign.getCreatives() != null){
            for (trueffect.truconnect.api.standalone.model.Creative oldCreative : oldCampaign.getCreatives()) {
                // Use files to create Creative and then update it
                Creative creative = proxiesFactory.getCampaignProxy().createCreativeImage(campaign.getId(), zipFileName + "/creatives/", oldCreative.getFilename(), false, 0L, 0L);

                creative = ServiceModelMapper.toServiceModel(creative, oldCreative, agencyId, campaign.getId());
                creative = proxiesFactory.getCreativeProxy().update(creative.getId(), creative);
                newCreatives.put(oldCreative.getId(), creative);
            }
        }

        //9. Save CreativeGroups and keep its new ids in the form [(oldId, newCreativeGroupObject)] (take care of the default CG).
        HashMap<Long, CreativeGroup> newCreativeGroups = new HashMap<Long, CreativeGroup>();
        if(oldCampaign.getCreativeGroups() != null){
            for (trueffect.truconnect.api.standalone.model.CreativeGroup oldCreativeGroup : oldCampaign.getCreativeGroups()) {
                CreativeGroup cg = null;
                if(!oldCreativeGroup.getName().equals("Default")){
                    cg = ServiceModelMapper.toServiceModel(oldCreativeGroup, campaign.getId());
                    cg = proxiesFactory.getCreativeGroupProxy().create(cg);
                } else {
                    cg = proxiesFactory.getCreativeGroupProxy().find("name = \"Default\"").getRecords().get(0);
                }
                newCreativeGroups.put(oldCreativeGroup.getId(), cg);
            }
        }

        //10. Save CreativeGroupCreatives using the new ids, keep its new ids in the form [(oldId, newCreativeGroupCreativeObject)].
        if(oldCampaign.getCreativeGroupCreatives() != null){
            HashMap<Long, List<Creative>> cgcs = new HashMap<Long, List<Creative>>();
            for (trueffect.truconnect.api.standalone.model.CreativeGroupCreative oldCreativeGroupCreative : oldCampaign.getCreativeGroupCreatives()) {
                if(!cgcs.containsKey(oldCreativeGroupCreative.getCreativeGroupId())) {
                    cgcs.put(oldCreativeGroupCreative.getCreativeGroupId(), new ArrayList<Creative>());
                }
                cgcs.get(oldCreativeGroupCreative.getCreativeGroupId()).add(newCreatives.get(oldCreativeGroupCreative.getCreativeId()));
            }
            for (Entry<Long, List<Creative>> entry : cgcs.entrySet()) {
                CreativeGroupCreative cgc = new CreativeGroupCreative();
                cgc.setCreativeGroupId(newCreativeGroups.get(entry.getKey()).getId());
                cgc.setCreatives(entry.getValue());
                cgc = proxiesFactory.getCreativeGroupProxy().updateCreativeGroupCreativesList(
                        newCreativeGroups.get(entry.getKey()).getId(), cgc);
            }
        }

        //11. Save InsertionOrders using MediaBuy and Publishers' new ids, keep its new ids in the form [(oldId, newInsertionOrderObject)].
        HashMap<Long, InsertionOrder> newInsertionOrders = new HashMap<Long, InsertionOrder>();
        if(oldCampaign.getMediaBuy().getInsertionOrders() != null){
            for (trueffect.truconnect.api.standalone.model.InsertionOrder oldInsertionOrder : oldCampaign.getMediaBuy().getInsertionOrders()) {
                Long ioId = oldInsertionOrder.getPublisherId() != null ? oldInsertionOrder.getPublisherId() : null;
                InsertionOrder io = ServiceModelMapper.toServiceModel(oldInsertionOrder, mediaBuy.getId(), ioId);
                io = proxiesFactory.getInsertionOrderProxy().create(io);
                newInsertionOrders.put(oldInsertionOrder.getId(), io);
            }
        }

        //12. Save Sites, keep its new ids in the form [(oldId, newSiteObject)].
        HashMap<Long, Site> newSites = new HashMap<Long, Site>();
        if(agency.getPublishers() != null){
            for (trueffect.truconnect.api.standalone.model.Publisher oldPublisher : agency.getPublishers()) {
                if(oldPublisher.getSites() != null){
                    for (trueffect.truconnect.api.standalone.model.Site oldSite : oldPublisher.getSites()) {
                        Site site = ServiceModelMapper.toServiceModel(oldSite, newPublishers.get(oldSite.getPublisherId()).getId());
                        site = proxiesFactory.getSiteProxy().create(site);
                        newSites.put(oldSite.getId(), site);
                    }
                }
            }
        }

        //13. Save Placements, keep its new ids in the form [(oldId, newPlacementObject)].
        HashMap<Long, Placement> newPlacements = new HashMap<Long, Placement>();
        if(oldCampaign.getPlacements() != null){
            for (trueffect.truconnect.api.standalone.model.Placement oldPlacement : oldCampaign.getPlacements()) {
                Placement placement = ServiceModelMapper.toServiceModel(oldPlacement,
                        newSites.get(oldPlacement.getSiteId()).getId(),
                        newInsertionOrders.get(oldPlacement.getIoId()).getId(),
                        newSizes.get(oldPlacement.getSizeId()),
                        campaign.getId());
                placement = proxiesFactory.getPlacementProxy().create(placement);
                newPlacements.put(oldPlacement.getId(), placement);
            }
        }

        //14. Save Schedules
        if(oldCampaign.getCreativeGroups() != null){
            for (trueffect.truconnect.api.standalone.model.CreativeGroup oldCreativeGroup : oldCampaign.getCreativeGroups()) {
                if(oldCreativeGroup.getScheduleSet() != null) {
                    ScheduleSet schedules = ServiceModelMapper.toServiceModel(oldCreativeGroup.getScheduleSet(), newCreatives, newCreativeGroups, newPlacements);
                    proxiesFactory.getScheduleProxy().update(schedules);
                }
            }
        }
    }
}
