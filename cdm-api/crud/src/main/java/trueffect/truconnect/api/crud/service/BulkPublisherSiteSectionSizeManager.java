package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.dto.BulkPublisherSiteSectionSize;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.crud.dao.SiteDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.SizeDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.apache.ibatis.session.SqlSession;

/**
 * Created by richard.jaldin on 10/22/2015.
 */
public class BulkPublisherSiteSectionSizeManager extends AbstractGenericManager {

    private UserDao userDao;
    private PublisherDao publisherDao;
    private SiteDao siteDao;
    private PublisherManager publisherManager;
    private SiteManager siteManager;
    private SiteSectionManager siteSectionManager;
    private SizeManager sizeManager;

    public BulkPublisherSiteSectionSizeManager(UserDao userDao,
                                               PublisherDao publisherDao,
                                               SiteDao siteDao,
                                               ExtendedPropertiesDao extendedPropertiesDao,
                                               SiteSectionDao siteSectionDao,
                                               SizeDao sizeDao,
                                               AccessControl accessControl) {
        super(accessControl);
        this.userDao = userDao;
        this.publisherDao = publisherDao;
        this.siteDao = siteDao;
        this.publisherManager = new PublisherManager(publisherDao, accessControl);
        this.siteManager = new SiteManager(siteDao, extendedPropertiesDao, accessControl);
        this.siteSectionManager = new SiteSectionManager(siteSectionDao, accessControl);
        this.sizeManager = new SizeManager(sizeDao, accessControl);
    }

    public BulkPublisherSiteSectionSize createBulk(BulkPublisherSiteSectionSize bulk, Boolean ignoreDupSite, OauthKey key) throws Exception {
        //validations
        // Nullability checks
        if (bulk == null) {
            throw new IllegalArgumentException("BulkPublisherSiteSectionSize cannot be null");
        }
        if (ignoreDupSite == null) {
            ignoreDupSite = false;
        }
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        BulkPublisherSiteSectionSize result = new BulkPublisherSiteSectionSize();
        SqlSession session = publisherDao.openSession();
        try {
            boolean publisherCreated = false;
            boolean siteCreated = false;

            //Getting current user's agency Id
            Long agencyId = userDao.getAgencyIdByUser(key.getUserId(), session);

            // 1. Create the Publisher.
            if (bulk.getPublisher() != null) {
                bulk.getPublisher().setAgencyId(agencyId);
                bulk.getPublisher().setZipCode("00000");
                Publisher publisher = publisherManager.create(bulk.getPublisher(), key, session);
                result.setPublisher(publisher);
                publisherCreated = true;
            }

            // 2. Create the Site.
            // 2.1 Check if the site exist under other Publisher, if so return an error unless
            //      'ignoreDupSite' flag is enabled
            if (bulk.getSite() != null) {
                if (publisherCreated) {
                    bulk.getSite().setPublisherId(result.getPublisher().getId());
                }
                bulk.getSite().setPreferredTag("IFRAME");

                Site site = siteManager.create(bulk.getSite(), ignoreDupSite, key, session);
                result.setSite(site);
                siteCreated = true;
            }

            // 3. Create the SiteSection.
            if (bulk.getSection() != null) {
                if (siteCreated) {
                    bulk.getSection().setSiteId(result.getSite().getId());
                }
                SiteSection siteSection = siteSectionManager.create(bulk.getSection(), key, session);
                result.setSection(siteSection);
            }

            // 4. Create the Size.
            if (bulk.getSize() != null) {
                bulk.getSize().setAgencyId(agencyId);
                Size size = sizeManager.create(bulk.getSize(), key, session);
                result.setSize(size);
            }

            publisherDao.commit(session);
        } catch (Exception e) {
            publisherDao.rollback(session);
            throw e;
        } finally {
            publisherDao.close(session);
        }

        return result;
    }
}
