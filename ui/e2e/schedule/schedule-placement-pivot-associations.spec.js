'use strict';

var schedulePlacementPivotAssociations = (function (campaignName, bootstrapData) {

    describe('Creative Association - Placement Pivot', function() {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/schedule.po'),
            navigate = require('../utilities/navigation.spec'),
            util = require('../page-object/utilities.po'),
            searchText = 'image';

        it('should clear data to associate creative to placement', function () {
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
                util.ensureCheck(page.flyoutCheckboxes.get(4));
                util.ensureCheck(page.flyoutCheckboxes.get(5));
                util.ensureCheck(page.flyoutCheckboxes.get(6));
                page.flyoutDelete.click();
                expect(page.modalTitle.isDisplayed()).toBe(false);
            });
        });

        it('should associate creative to placement when placement pivot', function () {
            var EC = protractor.ExpectedConditions,
                index = 0,
                list;

            page.changePivotDropdown('Placement');
            browser.actions().mouseMove(page.scheduleDataRows.get(0)).perform();
            page.scheduleDataRows.get(0).click();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);

            page.flyoutAddCreativesByGroupBtn.click();
            expect(page.creativeAssignationModal.isPresent()).toBe(true);

            page.moveAllCreativeToRightBtn.click();
            expect(page.creativeAssignationRightContainerRows.count()).toBeGreaterThan(0);
            page.moveAllCreativeToInsertionList.click();
            expect(page.saveCreativeInsertionBtn.isEnabled()).toBe(true);

            list = page.removeCreativeInsertionBtn;
            page.removeCreativeInsertionBtn.then(function(elements){
                index = elements.length - 1;
                while(index > 0){
                    list.get(index).click();
                    index--;
                }
            });
            page.saveCreativeInsertionBtn.click();

            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.placements[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
            for (var i = 0; i < 2; i++) {
                page.rowExpand.get(0).click();
            }
            expect(page.scheduleDataRows.count()).toEqual(5);
        });

        it('should associate creative to group when placement pivot', function () {
            var EC = protractor.ExpectedConditions;
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(CONSTANTS.defaultGroupName);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
            page.scheduleDataRows.get(1).click();
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
            for (var i = 0; i < 2; i++) {
                page.rowExpand.get(0).click();
            }
            expect(page.scheduleDataRows.count()).toEqual(7);
        });

        it('should not show available associations when already associated', function () {
            var EC = protractor.ExpectedConditions;

            page.changePivotDropdown('Placement');
            page.scheduleDataRows.get(0).click();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);

            page.flyoutAddCreativesByGroupBtn.click();
            expect(page.creativeAssignationModal.isPresent()).toBe(true);

            expect(page.creativeAssignationLeftContainerRows.count()).toEqual(0);
            expect(page.creativeAssignationRightContainerRows.count()).toEqual(0);
            expect(page.saveCreativeInsertionBtn.isEnabled()).toBe(false);
            page.cancelBtn.click();
            expect(page.creativeAssignationModal.isPresent()).toBe(false);

            navigate.mediaTrafficWarning(campaignName);
            util.closeWarningMessage();
        });
    });
});

module.exports = schedulePlacementPivotAssociations;