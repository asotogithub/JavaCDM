package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.Schedule;
import trueffect.truconnect.api.commons.model.ScheduleEntry;
import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

/**
 *
 * @author Rambert Rioja
 */
public class ScheduleManager extends AbstractGenericManager {

    private CreativeInsertionDao creativeInsertionDao;
    private PlacementDao placementDao;
    private CreativeManager creativeManager;
    private CreativeGroupManager creativeGroupManager;
    private CreativeInsertionManager creativeInsertionManager;
    private UserDao userDao;
    private CreativeDao creativeDao;
    private ExtendedPropertiesDao extendedPropertiesDao;
    private CreativeGroupDao creativeGroupDao;
    private CreativeGroupCreativeDao creativeGroupCreativeDao;
    private CampaignDao campaignDao;

    private Logger log =  LoggerFactory.getLogger(this.getClass());

    public ScheduleManager(CreativeInsertionDao creativeInsertionDao, PlacementDao placementDao, 
            CreativeDao creativeDao, ExtendedPropertiesDao extendedPropertiesDao,
            CreativeGroupDao creativeGroupDao, CreativeGroupCreativeDao creativeGroupCreativeDao,
            CampaignDao campaignDao, UserDao userDao, AccessControl accessControl) {
        super(accessControl);
        this.creativeInsertionDao = creativeInsertionDao;
        this.userDao = userDao;
        this.placementDao = placementDao;
        this.creativeDao = creativeDao;
        this.extendedPropertiesDao = extendedPropertiesDao;
        this.creativeGroupDao = creativeGroupDao;
        this.creativeGroupCreativeDao = creativeGroupCreativeDao;
        this.campaignDao = campaignDao;
        
        this.creativeInsertionManager = new CreativeInsertionManager(creativeInsertionDao, 
                campaignDao, placementDao, creativeDao, creativeGroupDao, creativeGroupCreativeDao, accessControl);
        
        this.creativeGroupManager = new CreativeGroupManager(creativeGroupDao,
                                                             creativeGroupCreativeDao,
                                                             creativeInsertionDao,
                                                             creativeDao,
                                                             campaignDao,
                                                             userDao,
                                                             extendedPropertiesDao,
                                                             accessControl);
        this.creativeManager = new CreativeManager(creativeDao, creativeGroupDao, creativeGroupCreativeDao, 
                creativeInsertionDao, campaignDao, userDao, extendedPropertiesDao, accessControl);
    }
    
    /**
     * Updates ScheduleSet Details
     *
     * @param scheduleSet
     * @param key
     * @return
     * @throws Exception
     */
    public ScheduleSet updateSchedules(ScheduleSet scheduleSet, OauthKey key) throws Exception {
        if (!scheduleSet.getSchedules().isEmpty()) {
            Long cgId = scheduleSet.getSchedules().get(0).getCreativeGroupId();

            //Obtain session
            SqlSession session = this.creativeInsertionDao.openSession();
            List<CreativeInsertion> dbClicks = null;
            try {
                //check accces control
                if (!userValidFor(AccessStatement.CREATIVE_GROUP, Collections.singletonList(cgId), key.getUserId(), session)) {
                    throw new DataNotFoundForUserException("CreativeGroup: " + cgId + " Not found for User: " + key.getUserId());
                }
                dbClicks = creativeInsertionDao.getCreativeInsertionsByGroupId(cgId, key, session);
            } finally {
                creativeInsertionDao.close(session);
            }            
            
            List<CreativeInsertion> inputClicks;
            inputClicks = parseToCreativeInsertions(scheduleSet, key);
            List<CreativeInsertion> toBeUpdated = new ArrayList<CreativeInsertion>();
            List<CreativeInsertion> toBeDeleted = new ArrayList<CreativeInsertion>();
            List<CreativeInsertion> toBeCreated = new ArrayList<CreativeInsertion>();
            deltaCI(inputClicks, dbClicks, toBeUpdated, toBeDeleted, toBeCreated);
            deleteCreativeInsertions(toBeDeleted, key);
            checkCreativeRelationships(toBeCreated, toBeUpdated, toBeDeleted, key);
            toBeUpdated = updateCreativeInsertions(toBeUpdated, key);
            toBeCreated = createCreativeInsertions(toBeCreated, key);
            toBeUpdated.addAll(toBeCreated);
            ScheduleSet setResp = new ScheduleSet();
            setResp.setSchedules(parseToSchedules(toBeUpdated));
            log.info(key.toString() + " Updated "+ setResp);
            return setResp;
        }
        throw new ValidationException("No schedules to be updated.");
    }

    private List<CreativeInsertion> parseToCreativeInsertions(ScheduleSet scheduleSet, OauthKey key) throws Exception {
        List<CreativeInsertion> cis = new ArrayList<CreativeInsertion>();
        Set<Long> placementMatches = new HashSet<Long>();
        Set<Long> placementNotMatches = new HashSet<Long>();
        Set<Long> creativeMatches = new HashSet<Long>();
        Set<Long> creativeNotMatches = new HashSet<Long>();
        for (Schedule schedule : scheduleSet.getSchedules()) {
            if (validSchedule(schedule.getPlacementId(), schedule.getCreativeId(), schedule.getEntries(), key)) {
                placementMatches.add(schedule.getPlacementId());
                creativeMatches.add(schedule.getCreativeId());
                placementNotMatches.remove(schedule.getPlacementId());
                creativeNotMatches.remove(schedule.getCreativeId());
                for (ScheduleEntry entry : schedule.getEntries()) {
                    CreativeInsertion ci = new CreativeInsertion();
                    Long creativeGroupId = schedule.getCreativeGroupId();
                    ci.setId(entry.getId());
                    ci.setPlacementId(schedule.getPlacementId());
                    ci.setCreativeGroupId(creativeGroupId);
                    ci.setCreativeId(schedule.getCreativeId());
                    ci.setWeight(entry.getWeight());
                    ci.setTimeZone(entry.getTimeZone());
                    ci.setSequence(entry.getSequence());
                    ci.setReleased(getReleased(entry.getIsReleased()));
                    ci.setStartDate(entry.getStartDate());
                    ci.setEndDate(entry.getEndDate());
                    List<Clickthrough> clicks = toCreativeInsertionClickthroughs(entry);
                    if (clicks != null && !clicks.isEmpty()) {
                        ci.setClickthrough(clicks.remove(0).getUrl());
                        ci.setClickthroughs(clicks);
                    }
                    Long campaignId = creativeInsertionDao.getCampaingIdByGreativeGroupId(creativeGroupId);
                    if (StringUtils.isBlank(ci.getCreatedTpwsKey())) {
                        ci.setCreatedTpwsKey("000000");
                    }
                    ci.setCampaignId(campaignId);
                    cis.add(ci);
                }
            } else {
                if (!placementMatches.contains(schedule.getPlacementId())) {
                    placementNotMatches.add(schedule.getPlacementId());
                }
                if (!creativeMatches.contains(schedule.getCreativeId())) {
                    creativeNotMatches.add(schedule.getCreativeId());
                }
            }
        }
        if (!placementNotMatches.isEmpty()) {
            throw new ValidationException("The are no Creatives matching the "
                    + "dimensions of Placement (" + placementNotMatches + ")");
        }
        if (!creativeNotMatches.isEmpty()) {
            throw new ValidationException("The are no Placements matching the "
                    + "dimensions of Creative (" + creativeNotMatches + ")");
        }
        return cis;
    }

    private List<Schedule> parseToSchedules(List<CreativeInsertion> cis) {
        List<Schedule> results = new ArrayList<Schedule>();
        for (CreativeInsertion ci : cis) {
            Schedule schedule = new Schedule();
            schedule.setId(ci.getCreativeInsertionRootId());
            schedule.setPlacementId(ci.getPlacementId());
            schedule.setCreativeGroupId(ci.getCreativeGroupId());
            schedule.setCreativeId(ci.getCreativeId());
            ScheduleEntry entry = new ScheduleEntry();
            entry.setId(ci.getId());
            entry.setStartDate(ci.getStartDate());
            entry.setEndDate(ci.getEndDate());
            entry.setModifiedDate(ci.getModifiedDate());
            entry.setCreatedDate(ci.getCreatedDate());
            entry.setTimeZone(ci.getTimeZone());
            entry.setSequence(ci.getSequence());
            entry.setWeight(ci.getWeight());
            entry.setIsReleased(getIsReleased(ci.getReleased()));
            entry.setClickthroughs(toScheduleClickthroughs(ci));
            schedule.setEntries(Arrays.asList(entry));
            results.add(schedule);
        }
        return results;

    }

    public Boolean getIsReleased(Long released) {
        if (released == null || released.equals(0L)) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public Long getReleased(Boolean isReleased) {
        if (isReleased == null || !isReleased) {
            return 0L;
        } else {
            return 1L;
        }
    }

    private void deltaCI(List<CreativeInsertion> inputCIs, List<CreativeInsertion> dbCIs,
            List<CreativeInsertion> toBeUpdated, List<CreativeInsertion> toBeDeleted,
            List<CreativeInsertion> toBeCreated) {
        toBeDeleted.addAll(dbCIs);
        toBeCreated.addAll(inputCIs);
        for (CreativeInsertion ci : inputCIs) {
            for (CreativeInsertion cidb : dbCIs) {
                if (ci.getCreativeGroupId().equals(cidb.getCreativeGroupId())
                        && ci.getPlacementId().equals(cidb.getPlacementId())
                        && ci.getCreativeId().equals(cidb.getCreativeId())) {
                    ci.setId(cidb.getId());
                    toBeUpdated.add(ci);
                    toBeDeleted.remove(cidb);
                    toBeCreated.remove(ci);
                }
            }
        }
    }
    
    private void deleteCreativeInsertions(List<CreativeInsertion> toBeDeleted, OauthKey key) throws Exception {
        for (CreativeInsertion ci : toBeDeleted) {
            log.info(key.toString()+ " Deleted "+ toBeDeleted);
            this.creativeInsertionManager.remove(ci.getId(), key);
        }
    }

    private List<CreativeInsertion> updateCreativeInsertions(
            List<CreativeInsertion> toBeupdated, OauthKey key) throws Exception {
        List<CreativeInsertion> results = new ArrayList<CreativeInsertion>();
        for (CreativeInsertion ci : toBeupdated) {
            CreativeInsertion insertion = this.creativeInsertionManager.update(ci.getId(), ci, key);
            results.add(insertion);
            log.info(key.toString()+ " Updated "+ toBeupdated);
        }
        return results;
    }

    private List<CreativeInsertion> createCreativeInsertions(
            List<CreativeInsertion> toBeCreated, OauthKey key) throws Exception {
        List<CreativeInsertion> results = new ArrayList<CreativeInsertion>();
        for (CreativeInsertion ci : toBeCreated) {
            CreativeInsertion insertion = this.creativeInsertionManager.createFromSchedulesContext(ci, key);
            results.add(insertion);
            log.info(key.toString()+ " Saved "+ toBeCreated);
        }
        return results;
    }

    private List<Clickthrough> toScheduleClickthroughs(CreativeInsertion ci) {
        List<Clickthrough> result;
        if (ci.getClickthroughs() != null && !ci.getClickthroughs().isEmpty()) {
            result = ci.getClickthroughs();
        } else {
            result = new ArrayList<Clickthrough>();
        }
        Clickthrough click = new Clickthrough();
        click.setSequence(1L);
        click.setUrl(ci.getClickthrough());
        result.add(0, click);
        return result;
    }

    private List<Clickthrough> toCreativeInsertionClickthroughs(ScheduleEntry entry) {
        List<Clickthrough> clicks = entry.getClickthroughs();
        if (clicks != null && !clicks.isEmpty()) {
            for (int i = 0; i < clicks.size(); i++) {
                Clickthrough click = clicks.get(i);
                if (click.getSequence().equals(1L)) {
                    click = clicks.remove(i);
                    clicks.add(0, click);
                    break;
                }
            }
        }
        return clicks;
    }

    private boolean validSchedule(Long placementId, Long creativeId, List<ScheduleEntry> entries, OauthKey key) throws Exception {
        SqlSession session = placementDao.openSession();
        Placement placement;
        try {
            if (!userValidFor(AccessStatement.PLACEMENT, Collections.singletonList(placementId), key.getUserId(), session)) {
                throw new DataNotFoundForUserException(ResourceBundleUtil.getString("PlacementId: " + placementId + " Not found for User: " + key.getUserId()));
            }
            placement = this.placementDao.get(placementId, session);
        } finally {
            placementDao.close(session);
        }
        //TODO: review this session, send the correct session once we refactor this to use a single session. To cover in US5196
        Creative creative = this.creativeManager.getCreative(creativeId, key);

        // Placements
        //      start & end dates
        if(placement.getEndDate().compareTo(placement.getStartDate()) <= 0){
            throw new ValidationException("The Placement is missing or requires "
                    + "corrections to be made to the startDate/endDate. endDate cannot be less than startDate."
                    + " Please correct the details for the information provided.");
        }

        // Creative
        //      FileName
        if (StringUtils.contains(creative.getFilename(), ' ')) {
            throw new ValidationException("The Creative is missing or requires "
                    + "corrections to be made to the filename. It cannot contain spaces. Please correct the details for the information provided.");
        }
        // Creative
        //      click through url

        if (creative.getCreativeType().equalsIgnoreCase(AdminFile.FileType.TRD.getFileType())) {
            if (!StringUtils.isBlank(creative.getClickthrough())) {
                throw new ValidationException(ResourceBundleUtil.getString("creativeInsertion.error.clickthroughNotRequired"));
            }
        } else {
            if (StringUtils.isBlank(creative.getClickthrough())) {
                throw new ValidationException(ResourceBundleUtil.getString("creativeInsertion.error.clickthroughRequired"));
            } else {
                String trimmedUrl = creative.getClickthrough().trim();
                Matcher matcher = ValidationConstants.PATTERN_CLICKTHROUGH_URL.matcher(trimmedUrl);
                if (!matcher.matches()) {
                    throw new ValidationException(ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthroughLegacy2"));
                } else {
                    creative.setClickthrough(trimmedUrl);
                    if (creative.getClickthroughs() != null) {
                        for (Clickthrough ct : creative.getClickthroughs()) {
                            trimmedUrl = ct.getUrl().trim();
                            matcher = ValidationConstants.PATTERN_CLICKTHROUGH_URL.matcher(trimmedUrl);
                            if (!matcher.matches()) {
                                throw new ValidationException(ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthroughLegacy2"));
                            } else {
                                ct.setUrl(trimmedUrl);
                            }
                        }
                    }
                }
            }
        }
        //DE872: Add validations for clickthrough not have spaces
       /* if (StringUtils.contains(creative.getClickthrough()," ")) {
            throw new ValidationException("A valid clickthrough is required to update Creative. It should not have spaces.");
        }*/

        // CreativeInsertion
        //      start & end date
        //      weight
        //      click through url
        if(entries != null) {
            for (ScheduleEntry entry : entries) {
                if(entry.getEndDate()== null || entry.getStartDate() == null){
                    throw new ValidationException("The Schedule is missing or requires "
                            + "corrections to be made to the startDate/endDate. startDate and endDate cannot be blank."
                            + " Please correct the details for the information provided.");
                }else if(entry.getEndDate().compareTo(entry.getStartDate()) <= 0){
                    throw new ValidationException("The Schedule is missing or requires "
                            + "corrections to be made to the startDate/endDate. endDate cannot be less than startDate."
                            + " Please correct the details for the information provided.");
                }
                if(entry.getWeight() == null || entry.getWeight().longValue() < 0){
                    throw new ValidationException("The Schedule is missing or requires "
                            + "corrections to be made to the weight. Weight cannnot be blank."
                            + " Please correct the details for the information provided.");
                }
                if(entry.getClickthroughs() != null) {
                    for (Clickthrough click : entry.getClickthroughs()) {
                        if (StringUtils.isBlank(click.getUrl()) || (!StringUtils.startsWith(click.getUrl(), "http://")
                                && !StringUtils.startsWith(click.getUrl(), "https://"))) {
                            throw new ValidationException("A valid clickthrough is required for schedule to be updated. It should start with either http:// or https://.");
                        }
                    }
                }
            }
        }

        if (placement != null && creative != null) {
            if (placement.getStatus().equals("Accepted")) {
                log.debug("Placement dimensions: H -> " + placement.getHeight() + ", W -> " + placement.getWidth());
                log.debug("Creative dimensions: H -> " + creative.getHeight() + ", W -> " + creative.getWidth());
                if (placement.getWidth().longValue() == creative.getWidth().longValue()
                        && placement.getHeight().longValue() == creative.getHeight().longValue()) {
                    return true;
                }
                return false;
            } else {
                throw new ValidationException("Placement " + placement.getId()
                        + " cannot be scheduled as it is not in Accepted status.");
            }
        }
        throw new ValidationException("Placement or Creative does not exist");
    }
    
    private void checkCreativeRelationships(List<CreativeInsertion> toBeCreated, 
            List<CreativeInsertion> toBeUpdated, List<CreativeInsertion> toBeDeleted, OauthKey key) throws Exception {
        List<Creative> creatives = new ArrayList<Creative>();
        List<CreativeInsertion> cis = new ArrayList<CreativeInsertion>();
        cis.addAll(toBeCreated);
        cis.addAll(toBeUpdated);
        cis.addAll(toBeDeleted);
        Long cgId = null;
        for (CreativeInsertion ci : cis) {
            cgId = ci.getCreativeGroupId();
            Creative item = new Creative();
            item.setId(ci.getCreativeId());
            creatives.add(item);
        }
        CreativeGroupCreative cgc = new CreativeGroupCreative();
        cgc.setCreativeGroupId(cgId);
        cgc.setCreatives(creatives);
        creativeGroupManager.updateCreativeGroupCreatives(cgId, cgc, key);
    }
}