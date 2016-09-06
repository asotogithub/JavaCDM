'use strict';

var scheduleGroupPivotAssociations = (function (campaignName, bootstrapData) {
    describe('Creative Association - Group Pivot', function() {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/schedule.po'),
            navigate = require('../utilities/navigation.spec'),
            searchText = 'image',
            util = require('../utilities/util'),
            utilPo = require('../page-object/utilities.po');

        it('should delete a creative insertion to associate it later at group level', function () {
            var EC = protractor.ExpectedConditions;

            navigate.newScheduleAssignment(campaignName);
            expect(page.scheduleAssignmentModal.isPresent()).toBe(true);
            expect(page.availPlacementsGrid.isDisplayed()).toBe(true);
            expect(page.availCreativesGrid.isDisplayed()).toBe(true);
            expect(page.pendingAssignSection.isDisplayed()).toBe(true);
            expect(page.pendingAssignGrid.isDisplayed()).toBe(false);

            page.scheduleAssignmentModalPlacementsInput.sendKeys(bootstrapData.placements[0]);
            page.availPlacementsCheckboxes.get(0).click();
            page.availCreativesSelectAll.click();
            expect(page.pendingAssignGrid.isDisplayed()).toBe(true);

            page.scheduleAssignmentSave.click();
            page.dismissDuplicateDialog.click();
            expect(page.scheduleAssignmentModal.isPresent()).toBe(false);

            browser.driver.wait(function() {
                return page.scheduleGrid.isPresent();
            });
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            page.scheduleDataRows.get(0).click();
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutCheckboxes.get(0));
            }).then(function(){
                page.searchInputFlyout.clear();
                page.searchInputFlyout.sendKeys(searchText);
                page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
                expect(page.flyoutDataRows.count()).toBeGreaterThan(7);
                page.flyoutDataRows.get(4).click();
                utilPo.ensureCheck(page.flyoutCheckboxes.get(4));
                utilPo.ensureCheck(page.flyoutCheckboxes.get(5));
                utilPo.ensureCheck(page.flyoutCheckboxes.get(6));
                page.flyoutDelete.click();
                expect(page.modalTitle.isDisplayed()).toBe(false);
            });
        });

        it('should associate the creative from the group level', function() {
            var EC = protractor.ExpectedConditions;

            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            util.click(page.scheduleDataRows.get(0));
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);
            util.click(page.flyoutAddCreativesBySiteBtn);
            expect(page.scheduleAssignmentModal.isPresent()).toBe(true);

            expect(page.pendingAssignGrid.isDisplayed()).toBe(false);
            page.scheduleAssignmentModalCreativeInput.sendKeys(bootstrapData.creativesDisplayName[2]);
            page.availCreativesCheckboxes.get(0).click();
            page.scheduleAssignmentModalPlacementsInput.sendKeys(bootstrapData.placements[0]);
            page.availPlacementsCheckboxes.get(0).click();
            expect(page.pendingAssignGrid.isDisplayed()).toBe(true);

            page.scheduleAssignmentSave.click();
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[2]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(4);
        });

        it('should associate the creative from the site level', function() {
            var EC = protractor.ExpectedConditions;

            page.searchInputSchedule.clear();
            util.click(page.rowExpand.get(0));
            page.scheduleDataRows.get(1).click();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);
            page.flyoutAddCreativesBySiteBtn.click();
            expect(page.scheduleAssignmentModal.isPresent()).toBe(true);
            expect(page.pendingAssignGrid.isDisplayed()).toBe(false);

            page.scheduleAssignmentModalCreativeInput.sendKeys(bootstrapData.creativesDisplayName[1]);
            page.availCreativesCheckboxes.get(0).click();
            page.scheduleAssignmentModalPlacementsInput.sendKeys(bootstrapData.placements[0]);
            page.availPlacementsCheckboxes.get(0).click();
            expect(page.pendingAssignGrid.isDisplayed()).toBe(true);

            page.scheduleAssignmentSave.click();
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[1]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(4);
        });

        it('should associate the creative from the placement level', function() {
            var EC = protractor.ExpectedConditions;

            page.searchInputSchedule.clear();
            util.click(page.rowExpand.get(0));
            page.rowExpand.get(0).click();
            page.scheduleDataRows.get(2).click();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);

            page.flyoutAddCreativesByGroupBtn.click();
            expect(page.creativeAssignationModal.isPresent()).toBe(true);

            page.moveAllCreativeToRightBtn.click();
            expect(page.creativeAssignationRightContainerRows.count()).toBeGreaterThan(0);
            page.moveAllCreativeToInsertionList.click();
            expect(page.saveCreativeInsertionBtn.isEnabled()).toBe(true);
            page.saveCreativeInsertionBtn.click();

            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.placements[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
            page.rowExpand.get(0).click();
            expect(page.scheduleDataRows.count()).toEqual(8);


            navigate.mediaTrafficWarning(campaignName);
            utilPo.closeWarningMessage();
        });
    });
});

module.exports = scheduleGroupPivotAssociations;
