package trueffect.truconnect.api.standalone.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;

import trueffect.truconnect.api.publik.client.exceptions.NotFoundException;
import trueffect.truconnect.api.publik.client.factory.PublicServiceFactory;
import trueffect.truconnect.api.standalone.converter.LocalModelMapper;
import trueffect.truconnect.api.standalone.model.Agency;
import trueffect.truconnect.api.standalone.model.Campaign;
import trueffect.truconnect.api.standalone.model.Creative;
import trueffect.truconnect.api.standalone.model.CreativeGroup;
import trueffect.truconnect.api.standalone.model.CreativeGroupCreative;
import trueffect.truconnect.api.standalone.model.InsertionOrder;
import trueffect.truconnect.api.standalone.model.Placement;
import trueffect.truconnect.api.standalone.model.Publisher;
import trueffect.truconnect.api.standalone.model.ScheduleSet;
import trueffect.truconnect.api.standalone.model.Site;
import trueffect.truconnect.api.standalone.model.SiteSection;
import trueffect.truconnect.api.standalone.model.Size;

public class Exporter {

    private PublicServiceFactory proxiesFactory;
    private String pathToOutputFolder;
    private Long campaignId;

    public Exporter(String baseUrl, String version, String username, String password, String pathToOuputFolder, Long campaignId) {
        String serviceUrl = baseUrl + "cms/" + version;
        String authenticationUrl = baseUrl + "oauth/" + version;
        proxiesFactory = new PublicServiceFactory(serviceUrl, authenticationUrl, MediaType.TEXT_XML, username, password);
        this.pathToOutputFolder = pathToOuputFolder;
        this.campaignId = campaignId;
    }

    public void export() throws Exception {
        // 1. Create standalone own model (POJOs)
        // 2. Use clients to log in and consume get services
        // 3. Map Services responses to own model
        // 4. Write XML file
        Campaign campaign = createCampaignXml();

        // 5. Write Creative's files
        recoverCreativeFilesIntoAFolder(campaign.getCreatives(), "Campaign-" + campaign.getId());

        // 6. Compress the files into a ZIP file and send it to the output folder
        Utils.compressFiles(new File("Campaign-" + campaign.getId()), new File(pathToOutputFolder + "Campaign-" + campaign.getId() + ".zip"));

        // 7. Remove temporal files.
        Utils.cleanTheHouse(new File("Campaign-" + campaign.getId()));

        // 8. Return a success message
        System.out.println("Campaign exported successfuly!");
    }

    private Campaign createCampaignXml() throws Exception {
        //Getting the Campaign
        trueffect.truconnect.api.commons.model.Campaign campaignAux = proxiesFactory.getCampaignProxy().getById(campaignId);
        Campaign campaign = LocalModelMapper.toLocalModel(campaignAux);
        Agency agency = null;
        if(campaign != null) {
            //Getting Cookie Domain for the campaign
            campaign.setCookieDomain(LocalModelMapper.toLocalModel(
                    proxiesFactory.getCookieDomainProxy().getById(campaignAux.getCookieDomainId())));
            //Getting Agency's hierarchy
            agency = LocalModelMapper.toLocalModel(
                    proxiesFactory.getAgencyProxy().getById(campaignAux.getAgencyId()));
            agency.setAdvertiser(LocalModelMapper.toLocalModel(
                    proxiesFactory.getAdvertiserProxy().getById(campaignAux.getAdvertiserId())));
            agency.getAdvertiser().setBrand(LocalModelMapper.toLocalModel(
                    proxiesFactory.getBrandProxy().getById(campaignAux.getBrandId())));
            agency.getAdvertiser().getBrand().setCampaign(campaign);

            //Getting Creatives
            List<trueffect.truconnect.api.commons.model.Creative> creativesAux = 
                    proxiesFactory.getCreativeProxy().find("campaignId equals to " + campaign.getId()).getRecords();
            if(creativesAux != null){
                List<Creative> creatives = new ArrayList<Creative>();
                for (trueffect.truconnect.api.commons.model.Creative creative : creativesAux) {
                    creatives.add(LocalModelMapper.toLocalModel(creative));
                }
                campaign.setCreatives(creatives);
            }

            //Getting CreativeGroups
            List<trueffect.truconnect.api.commons.model.CreativeGroup> creativeGroupsAux = 
                    proxiesFactory.getCreativeGroupProxy().find("campaignId equals to " + campaign.getId()).getRecords();
            if(creativeGroupsAux != null) {
                List<CreativeGroup> creativeGroups = new ArrayList<CreativeGroup>();
                for (trueffect.truconnect.api.commons.model.CreativeGroup cg : creativeGroupsAux) {
                    creativeGroups.add(LocalModelMapper.toLocalModel(cg));
                }
                campaign.setCreativeGroups(creativeGroups);
                
              //Getting CreativeGroupCreatives
                List<CreativeGroupCreative> creativeGroupCreatives = new ArrayList<CreativeGroupCreative>();
                for (CreativeGroup creativeGroup : creativeGroups) {
                    List<trueffect.truconnect.api.commons.model.CreativeGroupCreative> creativeGroupCreativesAux = 
                            proxiesFactory.getCreativeGroupProxy().getCreativeGroupCreatives(creativeGroup.getId()).getRecords();
                    for (trueffect.truconnect.api.commons.model.CreativeGroupCreative cgc : creativeGroupCreativesAux) {
                        creativeGroupCreatives.add(LocalModelMapper.toLocalModel(cgc));
                    }
                }
                campaign.setCreativeGroupCreatives(creativeGroupCreatives);
            }

            //Getting MediaBuy
            campaign.setMediaBuy(LocalModelMapper.toLocalModel(
                    proxiesFactory.getMediaBuyProxy().getByCampaign(campaign.getId())));

            //Getting InsertionOrders
            List<Long> publisherIds = new ArrayList<Long>();
            List<trueffect.truconnect.api.commons.model.InsertionOrder> ioAux = 
                    proxiesFactory.getInsertionOrderProxy().find("mediaBuyId equals to " + campaign.getMediaBuy().getId()).getRecords();
            if(ioAux != null) {
                List<InsertionOrder> ios = new ArrayList<InsertionOrder>();
                for (trueffect.truconnect.api.commons.model.InsertionOrder insertionOrder : ioAux) {
                    ios.add(LocalModelMapper.toLocalModel(insertionOrder));
                    if(insertionOrder.getPublisherId() != null && !publisherIds.contains(insertionOrder.getPublisherId())) {
                        publisherIds.add(insertionOrder.getPublisherId());
                    }
                }
                campaign.getMediaBuy().setInsertionOrders(ios);
            }

            //Getting Placements
            List<trueffect.truconnect.api.commons.model.Placement> placementsAux = 
                    proxiesFactory.getPlacementProxy().find("campaignId equals to " + campaign.getId()).getRecords();
            if(placementsAux != null){
                List<Placement> placements = new ArrayList<Placement>();
                for (trueffect.truconnect.api.commons.model.Placement placement : placementsAux) {
                    placements.add(LocalModelMapper.toLocalModel(placement));
                }
                campaign.setPlacements(placements);

                //Getting Sizes, Sites and SiteSections.
                List<Size> sizes = new ArrayList<Size>();
                HashMap<String, Size> sizesMap = new HashMap<String, Size>();
                List<Site> sites = new ArrayList<Site>();
                HashMap<String, Site> sitesMap = new HashMap<String, Site>();
                for (Placement placement : placements) {
                    if(placement.getSizeId() != null && !sizesMap.containsKey(placement.getSizeId().toString())) {
                        sizesMap.put(placement.getSizeId().toString(), 
                                LocalModelMapper.toLocalModel(proxiesFactory.getSizeProxy().getById(placement.getSizeId())));
                    }
                    if(!sitesMap.containsKey(placement.getSiteId().toString())) {
                        Site site = LocalModelMapper.toLocalModel(proxiesFactory.getSiteProxy().getById(placement.getSiteId()));
                        sitesMap.put(placement.getSiteId().toString(), site);
                        if(site.getPublisherId() != null && !publisherIds.contains(site.getPublisherId())) {
                            publisherIds.add(site.getPublisherId());
                        }
                    }

                    //Getting SiteSections
                    trueffect.truconnect.api.commons.model.SiteSection serviceSection = proxiesFactory.getSiteSectionProxy().getById(placement.getSiteSectionId());
                    SiteSection section = LocalModelMapper.toLocalModel(serviceSection);
                    Site site = sitesMap.get(serviceSection.getSiteId().toString());
                    if(site.getSections() == null) {
                        site.setSections(new ArrayList<SiteSection>());
                    }
                    boolean alreadyExists = false;
                    for (SiteSection siteSection : site.getSections()) {
                        alreadyExists = alreadyExists || section.getId().compareTo(siteSection.getId()) == 0;
                    }
                    if(!alreadyExists) {
                        site.getSections().add(section);
                    }
                }
                sizes.addAll(sizesMap.values());
                agency.setSizes(sizes);
                sites.addAll(sitesMap.values());

                //Getting Publishers
                List<Publisher> publishers = new ArrayList<Publisher>();
                for (Long publisherId : publisherIds) {
                    Publisher publisher = LocalModelMapper.toLocalModel(proxiesFactory.getPublisherProxy().getById(publisherId));
                    for (Site site : sites) {
                        if(site.getPublisherId().compareTo(publisher.getId()) == 0){
                            if(publisher.getSites() == null) {
                                publisher.setSites(new ArrayList<Site>());
                            }
                            publisher.getSites().add(site);
                        }
                    }
                    publishers.add(publisher);
                }
                agency.setPublishers(publishers);
            }

            //Getting Schedules
            //Schedules got based on CreativeGroupId
            for (CreativeGroup cg : campaign.getCreativeGroups()) {
                ScheduleSet schedules = null;
                try {
                    schedules = LocalModelMapper.toLocalModel(proxiesFactory.getScheduleProxy().getById(cg.getId()));
                } catch (NotFoundException e) { }
                cg.setScheduleSet(schedules);
            }
        }
        writeCampaign(agency, "Campaign-" + campaign.getId(), "Campaign-" + campaign.getId() + ".xml");
        return campaign;
    }

    private void writeCampaign(Agency agency, String tmpFolderName, String filename) {
        try {
            File file = new File(tmpFolderName + "/" + filename);
            File parent = file.getParentFile();
            if(!parent.exists() && !parent.mkdirs()){
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(Agency.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(agency, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void recoverCreativeFilesIntoAFolder(List<Creative> creatives, String tmpFolderName) throws Exception {
        if(creatives != null){
            for (Creative creative : creatives) {
                File tmpFile = proxiesFactory.getCreativeProxy().getFile(creative.getId());

                File file = new File(tmpFolderName + "/creatives/" + creative.getFilename());
                File parent = file.getParentFile();
                if(!parent.exists() && !parent.mkdirs()){
                    throw new IllegalStateException("Couldn't create dir: " + parent);
                }

                FileUtils.copyFile(tmpFile, file);
            }
        }

    }

}
