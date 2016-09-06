package trueffect.truconnect.api.crud.service;

import static trueffect.truconnect.api.crud.EntityFactory.createCampaign;
import static trueffect.truconnect.api.crud.EntityFactory.createCreativeInsertionImportViewList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doAnswer;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.util.Either;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Asynchronous Unit tests for {@code CreativeInsertionManager} bulk import (TA-1225)
 * @author Marcelo Heredia
 */
public class CreativeInsertionBulkImportAsyncTest extends CreativeInsertionManagerBaseTest {

    private static final int NUMBER_OF_ROWS_PER_XLSX = 100;
    private static final int NUMBER_OF_ASYNC_PROCESSES = 20;
    private Map<Long, List<CreativeInsertionRawDataView>> inputDataMap;
    private Map<Long, CreativeInsertionRawDataView> outputDataMap;
    private Map<Long, Campaign> campaignMap;
    private Map<Long, String> filesMap;
    private ExecutorService executorService;

    @Before
    public void setUpImportFiles() throws Exception {
        inputDataMap = new HashMap<>(NUMBER_OF_ASYNC_PROCESSES);
        outputDataMap = new HashMap<>();
        campaignMap = new HashMap<>(NUMBER_OF_ASYNC_PROCESSES);
        filesMap = new HashMap<>(NUMBER_OF_ASYNC_PROCESSES);
        // Populating input data
        Campaign campaign;
        for(int i = 0; i < NUMBER_OF_ASYNC_PROCESSES; i++) {
            campaign = createCampaign();
            // Create XLSX file with 'NUMBER_OF_ROWS_PER_XLSX' number of rows
            List<CreativeInsertionRawDataView> creativeInsertionImportViewList = createCreativeInsertionImportViewList(NUMBER_OF_ROWS_PER_XLSX);
            // Update IDs to make them incremental
            inputDataMap.put(campaign.getId(), creativeInsertionImportViewList);
            campaignMap.put(campaign.getId(), campaign);
        }
        // Mock response of get Campaign
        when(campaignDao.get(anyLong(), any(SqlSession.class))).
                thenAnswer(new Answer<Campaign>() {
                    @Override
                    public Campaign answer(InvocationOnMock invocation) throws Throwable {
                        Long campaignId = (Long) invocation.getArguments()[0];
                        return campaignMap.get(campaignId);
                    }
                });

        when(creativeInsertionDao.getCreativeInsertionsToExport(
                anyLong(),
                any(SqlSession.class))).thenAnswer(
                    new Answer<List<CreativeInsertionRawDataView>>() {
                        @Override
                        public List<CreativeInsertionRawDataView> answer(InvocationOnMock invocation) throws Throwable {
                            Long campaignId = (Long) invocation.getArguments()[0];
                            return inputDataMap.get(campaignId);
                        }
                    });
        // Mock BATCH session creation
        SqlSession batchSession = mock(SqlSession.class);
        // Mock updateOnImport method to collect all CreativeInsertionRawDataView it receives
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                CreativeInsertionRawDataView ci = (CreativeInsertionRawDataView) invocation.getArguments()[0];
                CreativeInsertionRawDataView newCI = new CreativeInsertionRawDataView();
                BeanUtils.copyProperties(newCI, ci);
                // Cleaning up attributes we don't need, so that assertion of collections can be done
                newCI.setRowError("0");
                newCI.setCiPropsChanged(false);
                newCI.setGroupPropsChanged(false);
                newCI.setModifiedTpwsKey(null);
                newCI.setReason(null);
                synchronized (this) {
                    outputDataMap.put(Long.valueOf(newCI.getCreativeInsertionId()), newCI);
                }
                return null;
            }
        }).when(creativeInsertionDao).updateOnImport(
                any(CreativeInsertionRawDataView.class),
                eq(batchSession));
        when(creativeInsertionDao.getCreativeInsertionsByUserId(
                anyList(),
                eq(key.getUserId()),
                anyLong(),
                eq(sqlSessionMock))).
                thenAnswer(new Answer<List<CreativeInsertionRawDataView>>() {
                    @Override
                    public List<CreativeInsertionRawDataView> answer(InvocationOnMock invocation) throws Throwable {
                        Long campaignId = (Long) invocation.getArguments()[2];
                        List<CreativeInsertionRawDataView> insertionsToExport = inputDataMap.get(campaignId);
                        List<CreativeInsertionRawDataView> modifiedList = new ArrayList<>(insertionsToExport.size());
                        // Deep copy list to modify the result of this method to simulate changes on DB vs imported file
                        for(CreativeInsertionRawDataView ci : insertionsToExport) {
                            CreativeInsertionRawDataView newCI = new CreativeInsertionRawDataView();
                            BeanUtils.copyProperties(newCI, ci);
                            Long weight = Long.valueOf(ci.getCreativeWeight());
                            // Make sure to modify the Creative Weight only
                            newCI.setCreativeWeight(
                                    String.valueOf(
                                            (weight > Constants.CREATIVE_INSERTION_MAX_WEIGHT) ?
                                                    Constants.CREATIVE_INSERTION_DEFAULT_WEIGHT :
                                                    weight + 1L));
                            modifiedList.add(newCI);
                        }
                        return modifiedList;
                    }
                });
        // Make sure the session is a BATCH type
        when(creativeInsertionDao.openSession(eq(ExecutorType.BATCH))).thenReturn(batchSession);
        when(campaignDao.openSession()).thenReturn(sqlSessionMock);

        // XLSX exported files are saved on disk
        for (Long campaignId : inputDataMap.keySet()) {
            File file = importExportManager.exportByCampaign(campaignId,
                    ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW, null, key);
            assertThat(file, is(notNullValue()));
            // We need to move it to the import path in order to use import method
            Path fromPath = Paths.get(file.toURI());
            File toPathDir = new File(ImportExportManager.buildImportPath(campaignId, ImportExportManager.EXPORT_IMPORT_TYPE_CREATIVE_INSERTION_VIEW));
            if (!toPathDir.exists()) {
                toPathDir.mkdirs();
            }
            File targetFile = new File(toPathDir, file.getName());
            Path toPath = Paths.get(targetFile.toURI());
            // First, we make sure we have the toPath created

            Files.move(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            filesMap.put(campaignId, file.getName());
        }
        // Create the Executor Service
        executorService = Executors.newFixedThreadPool(NUMBER_OF_ASYNC_PROCESSES);
    }


    @After
    public void tearDown(){
        executorService.shutdown();
        AdminFile.deleteChildren(new File(ImportExportManager.SCHEDULES_IMPORT_PATH));
        AdminFile.deleteChildren(new File(ImportExportManager.SCHEDULE_EXPORT_PATH));
    }

    /**
     * Tests out if N multiple threads trying to asynchronously import an XLSX file ends up
     * with success individually. Each xlsx imported file belongs to a different campaign and in the end of the test
     * each Creative Insertion set matches the imported xlsx file.
     */
    @Test
    public void importCreativeInsertionsAsynchronouslyWithSuccess() throws InterruptedException, ExecutionException {

        List<ImportCallable> tasks = new ArrayList<>(NUMBER_OF_ASYNC_PROCESSES);
        for (Map.Entry<Long, String> entry : filesMap.entrySet()) {
            tasks.add(new ImportCallable(entry.getKey(), entry.getValue()));
        }
        List<Future<Either>> futures = executorService.invokeAll(tasks);
        List<Either> resultList = new ArrayList<>(futures.size());
        // Check for exceptions
        for (Future<Either> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get());
        }
        // Assertions
        assertThat(NUMBER_OF_ASYNC_PROCESSES, is(equalTo(futures.size())));
        assertThat(outputDataMap.size(), is(equalTo(NUMBER_OF_ASYNC_PROCESSES * NUMBER_OF_ROWS_PER_XLSX)));
        // Make sure that updated data reflects what the input data contains
        for (Map.Entry<Long, List<CreativeInsertionRawDataView>> entry : inputDataMap.entrySet()) {
            List<CreativeInsertionRawDataView> cisInput = entry.getValue();
            List<CreativeInsertionRawDataView> cisRemove = new ArrayList<>(cisInput.size());
            for (CreativeInsertionRawDataView ciInput : cisInput) {
                Long ciID = Long.valueOf(ciInput.getCreativeInsertionId());
                CreativeInsertionRawDataView ciUpdated = outputDataMap.get(ciID);
                assertThat(ciUpdated, is(equalTo(ciInput)));
                cisRemove.add(ciInput);
                outputDataMap.remove(ciID);
            }
            cisInput.removeAll(cisRemove);
            assertThat("List of elements do not match: " + cisInput, cisInput.size(), is(equalTo(0)));
        }
        assertThat(outputDataMap.size(), is(equalTo(0)));
    }

    class ImportCallable implements Callable<Either> {
        private String fileName;
        private Long campaignId;
        public ImportCallable(Long campaignId, String fileName) {
            this.campaignId = campaignId;
            this.fileName = fileName;
        }

        @Override
        public Either call() throws Exception {
            return importExportManager.importToUpdateSchedulesFromXLSXFile(
                    campaignId,
                    // We are not providing the UUID but using the already exported file, which is located in the same
                    // path as the regular upload process would place it
                    AdminFile.getFilenameWithoutExtension(fileName),
                    Boolean.FALSE,
                    key);
        }
    }
}
