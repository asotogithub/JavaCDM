package trueffect.truconnect.api.crud.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gustavo Claure
 * @author Marcelo Heredia
 */
public class CreativeManagerBaseTest extends AbstractManagerTest {

    protected static Map<Long, Creative> existingCreatives;
    protected static CreativeManager creativeManager;
    protected Creative creative;
    protected Campaign campaign;

    protected CreativeDao creativeDao;
    protected CreativeGroupCreativeDao creativeGroupCreativeDao;
    protected CreativeGroupDao creativeGroupDao;
    protected CreativeInsertionDao creativeInsertionDao;
    protected CampaignDao campaignDao;
    protected UserDao userDao;
    protected ExtendedPropertiesDao extendedPropertiesDao;
    protected InputStream inputStream;

    private CreativeManager.UtilityWrapper utilityWrapper;
    private String testPath;

    @Before
    public void baseInit() throws Exception {
        creative = EntityFactory.createCreative();
        // Store creatives on mock persistence context
        campaign = EntityFactory.createCampaign();
        existingCreatives = new HashMap<Long, Creative>(){{
            Creative c1 = EntityFactory.createCreative();
            c1.setFilename(c1.getAlias() + "." + AdminFile.FileType.GIF.getFileType());
            c1.setCreativeType(AdminFile.FileType.GIF.getFileType());
            c1.setCampaignId(campaign.getId());
            put(c1.getId(), c1);
            Creative c2 = EntityFactory.createCreative();
            c2.setFilename(c2.getAlias() + "." + AdminFile.FileType.JPG.getFileType());
            c2.setCreativeType(AdminFile.FileType.JPG.getFileType());
            c2.setCampaignId(campaign.getId());
            put(c2.getId(), c2);
            Creative c3 = EntityFactory.createCreative();
            c3.setFilename(c3.getAlias() + "." + AdminFile.FileType.ZIP.getFileType());
            c3.setCreativeType(AdminFile.FileType.ZIP.getFileType());
            c3.setCampaignId(campaign.getId());
            put(c3.getId(), c3);
            Creative c4 = EntityFactory.createCreative();
            c4.setFilename(c4.getAlias() + "." + AdminFile.FileType.TXT.getFileType());
            c4.setCreativeType(AdminFile.FileType.TXT.getFileType());
            c4.setCampaignId(campaign.getId());
            put(c4.getId(), c4);
            Creative c5 = EntityFactory.createCreative();
            c5.setFilename(c5.getAlias() + "." + AdminFile.FileType.TRD.getFileType());
            c5.setCreativeType(AdminFile.FileType.TRD.getFileType());
            put(c5.getId(), c5);
        }
        };



        creativeDao = mock(CreativeDao.class);
        sqlSessionMock = mock(SqlSession.class);
        creativeGroupCreativeDao = mock(CreativeGroupCreativeDao.class);
        creativeGroupDao = mock(CreativeGroupDao.class);
        creativeDao = mock(CreativeDao.class);
        creativeInsertionDao = mock(CreativeInsertionDao.class);
        campaignDao = mock(CampaignDao.class);
        userDao = mock(UserDao.class);
        extendedPropertiesDao = mock(ExtendedPropertiesDao.class);

        utilityWrapper = mock(CreativeManager.UtilityWrapper.class);

        testPath = System.getProperty("java.io.tmpdir") + File.separator + "test";
        File f = new File(testPath);
        if(!f.exists() && !f.isDirectory()){
            f.mkdirs();
        }

        // Mock common behavior
        when(creativeDao.openSession()).thenReturn(sqlSessionMock);
        when(creativeDao.create(any(Creative.class), eq(key),
                eq(sqlSessionMock))).thenAnswer(saveCreative());
        when(campaignDao.get(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(campaign);
        when(creativeDao.get(anyLong(),
                any(SqlSession.class))).thenAnswer(
                getCreative());

        when(extendedPropertiesDao.updateExternalId(anyString(), anyString(), anyLong(),
                anyString(), any(SqlSession.class))).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                return (String) invocation.getArguments()[3];
            }
        });

        when(utilityWrapper.createFile(anyString())).thenReturn(new File("."));
        when(utilityWrapper.getPath(eq("image.path"))).thenReturn(testPath);
        when(utilityWrapper.getPath(eq("tmp.path"))).thenReturn(testPath);

        // Mock DAC
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), anyList(),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyList(),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE), anyList(),
                eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE_GROUP), anyList(),
                eq(key.getUserId()), any(SqlSession.class))).thenReturn(true);



        // manager
        creativeManager = new CreativeManager(creativeDao, creativeGroupDao,
                creativeGroupCreativeDao, creativeInsertionDao, campaignDao, userDao, extendedPropertiesDao,
                accessControlMockito);
        creativeManager.setUtilityWrapper(utilityWrapper);
    }

    @After
    public void teardown() throws IOException {
        // close inputStream
        if(inputStream != null) {
            inputStream.close();
        }

        //remove files finalCreativePath
        if (testPath != null && !testPath.isEmpty()) {
            AdminFile.deleteRecursively(testPath);
        }
    }

    private static Answer<Creative> saveCreative() {
        return new Answer<Creative>() {
            @Override
            public Creative answer(InvocationOnMock invocation) {
                Creative result = (Creative) invocation.getArguments()[0];
                result.setId(Math.abs(EntityFactory.random.nextLong()));
                result.setLogicalDelete("N");
                existingCreatives.put(result.getId(), result);
                return result;
            }
        };
    }

    private static Answer<Creative> getCreative() {
        return new Answer<Creative>() {
            public Creative answer(InvocationOnMock invocation) {
                Long id = (Long) invocation.getArguments()[0];
                return existingCreatives.get(id);
            }
        };
    }

}
