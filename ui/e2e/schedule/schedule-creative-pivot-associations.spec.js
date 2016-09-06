'use strict';

var scheduleCreativePivotAssociations = (function (campaignName, bootstrapData) {
    describe('Creative Association - Creative Pivot', function() {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/schedule.po'),
            navigate = require('../utilities/navigation.spec'),
            searchText = 'image',
            utilPo = require('../page-object/utilities.po'),
            util = require('../utilities/util');

        it('should delete a creative insertion to associate it later at creative level', function () {
            var EC = protractor.ExpectedConditions;

            navigate.newScheduleAssignment(campaignName);
            expect(page.scheduleAssignmentModal.isPresent()).toBe(true);
            expect(page.availPlacementsGrid.isDisplayed()).toBe(true);
            expect(page.availCreativesGrid.isDisplayed()).toBe(true);
            expect(page.pendingAssignSection.isDisplayed()).toBe(true);
            expect(page.pendingAssignGrid.isDisplayed()).toBe(false);

            page.scheduleAssignmentModalPlacementsInput.sendKeys(bootstrapData.placements[0]);
            util.click(page.availPlacementsCheckboxes.get(0));
            util.click(page.availCreativesSelectAll);
            expect(page.pendingAssignGrid.isDisplayed()).toBe(true);

            util.click(page.scheduleAssignmentSave);
            util.click(page.dismissDuplicateDialog);
            expect(page.scheduleAssignmentModal.isPresent()).toBe(false);

            browser.driver.wait(function() {
                return page.scheduleGrid.isPresent();
            });
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            util.click(page.scheduleDataRows.get(0));
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutCheckboxes.get(0));
            }).then(function(){
                page.searchInputFlyout.clear();
                page.searchInputFlyout.sendKeys(searchText);
                page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
                expect(page.flyoutDataRows.count()).toBeGreaterThan(7);
                util.click(page.flyoutDataRows.get(4));
                utilPo.ensureCheck(page.flyoutCheckboxes.get(4));
                utilPo.ensureCheck(page.flyoutCheckboxes.get(5));
                utilPo.ensureCheck(page.flyoutCheckboxes.get(6));
                util.click(page.flyoutDelete);
                expect(page.modalTitle.isDisplayed()).toBe(false);
            });
        });

        it('should associate the creative from the creative level', function() {
            var EC = protractor.ExpectedConditions;

            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.CREATIVE);
            util.click(page.scheduleDataRows.get(0));
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);
            util.click(page.flyoutAddCreativesBySiteBtn);
            expect(page.scheduleAssignmentModal.isPresent()).toBe(true);

            expect(page.pendingAssignGrid.isDisplayed()).toBe(false);
            page.scheduleAssignmentModalCreativeInput.sendKeys(bootstrapData.creativesDisplayName[4]);
            util.click(page.availCreativesCheckboxes.get(0));
            page.scheduleAssignmentModalPlacementsInput.sendKeys(bootstrapData.placements[2]);
            util.click(page.availPlacementsCheckboxes.get(0));
            expect(page.pendingAssignGrid.isDisplayed()).toBe(true);

            util.click(page.scheduleAssignmentSave);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[4]);
            util.click(page.searchButtonSchedule);
            expect(page.scheduleDataRows.count()).toBeGreaterThan(4);
        });

        it('should associate the creative from the site level', function() {
            var EC = protractor.ExpectedConditions;

            page.searchInputSchedule.clear();
            util.click(page.rowExpand.get(0));
            util.click(page.scheduleDataRows.get(1));
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);
            util.click(page.flyoutAddCreativesBySiteBtn);
            expect(page.scheduleAssignmentModal.isPresent()).toBe(true);
            expect(page.pendingAssignGrid.isDisplayed()).toBe(false);

            page.scheduleAssignmentModalCreativeInput.sendKeys(bootstrapData.creativesDisplayName[4]);
            util.click(page.availCreativesCheckboxes.get(0));
            page.scheduleAssignmentModalPlacementsInput.sendKeys(bootstrapData.placements[1]);
            util.click(page.availPlacementsCheckboxes.get(0));
            expect(page.pendingAssignGrid.isDisplayed()).toBe(true);

            util.click(page.scheduleAssignmentSave);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[4]);
            util.click(page.searchButtonSchedule);
            expect(page.scheduleDataRows.count()).toBeGreaterThan(4);

            navigate.mediaTrafficWarning(campaignName);
            utilPo.closeWarningMessage();
        });
    });
});

module.exports = scheduleCreativePivotAssociations;
