'use strict';

var scheduleSitePivotAssociations = (function (campaignName, bootstrapData) {
    describe('Creative Association - Site Pivot', function() {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/schedule.po'),
            navigate = require('../utilities/navigation.spec'),
            util = require('../page-object/utilities.po');

        it('should delete a creative insertion to associate it later', function () {
            var EC = protractor.ExpectedConditions;

            navigate.scheduleGrid(campaignName);
            browser.driver.wait(function() {
                return page.scheduleGrid.isPresent();
            });
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            page.scheduleDataRows.get(0).click();
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutCheckboxes.get(2));
            }).then(function(){
                page.searchInputFlyout.clear();
                page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[3]);
                page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
                expect(page.flyoutDataRows.count()).toEqual(5);
                page.flyoutDataRows.get(4).click();
                util.ensureCheck(page.flyoutCheckboxes.get(4));
                page.flyoutDelete.click();
                expect(page.modalTitle.isDisplayed()).toBe(false);
                page.searchInputSchedule.clear();
                page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[3]);
                page.searchButtonSchedule.click();
                expect(page.scheduleDataRows.count()).toEqual(0);
            });
        });

        it('should search and open flyout on the site level', function() {
            var EC = protractor.ExpectedConditions;
            page.searchInputSchedule.clear();
            expect(page.scheduleDataRows.count()).toEqual(1);

            page.scheduleDataRows.get(0).click();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });

        it('should open associate creative to placement', function() {
            page.flyoutAddCreativesBySiteBtn.click();
            expect(page.scheduleAssignmentModal.isPresent()).toBe(true);
        });

        it('should select the creative and placement to be associated', function() {
            expect(page.pendingAssignGrid.isDisplayed()).toBe(false);
            page.scheduleAssignmentModalCreativeInput.sendKeys(bootstrapData.creativesDisplayName[1]);
            browser.actions().mouseMove(page.availCreativesCheckboxes.get(0)).perform();
            page.availCreativesCheckboxes.get(0).click();
            page.scheduleAssignmentModalPlacementsInput.sendKeys(bootstrapData.placements[0]);
            browser.actions().mouseMove(page.availPlacementsCheckboxes.get(0)).perform();
            page.availPlacementsCheckboxes.get(0).click();
            expect(page.pendingAssignGrid.isDisplayed()).toBe(true);
        });

        it('should associate the creative from the site level', function() {
            page.scheduleAssignmentSave.click();
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[1]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(5);
        });

        it('should search and open flyout on the placement level', function() {
            var EC = protractor.ExpectedConditions;
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.placements[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(3);

            page.rowPlacement.get(0).click();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });

        it('should open associate creative to placement', function() {
            page.flyoutAddCreativesByGroupBtn.click();
            expect(page.creativeAssignationModal.isPresent()).toBe(true);
        });

        it('should move all creatives to right container', function() {
            page.moveAllCreativeToRightBtn.click();
            expect(page.creativeAssignationLeftContainerRows.count()).toEqual(0);
            expect(page.creativeAssignationRightContainerRows.count()).toBeGreaterThan(0);
        });

        it('should move all creatives to Insertion List', function() {
            page.moveAllCreativeToInsertionList.click();
            expect(page.saveCreativeInsertionBtn.isEnabled()).toBe(true);
        });

        it('should remove creatives from Insertion List', function() {
            var index = 0,
                list = page.removeCreativeInsertionBtn;

            page.removeCreativeInsertionBtn.then(function(elements){
                index = elements.length - 1;
                while(index > 0){
                    browser.actions().mouseMove(list.get(index)).perform();
                    list.get(index).click();
                    index--;
                }
            });

            expect(page.saveCreativeInsertionBtn.isEnabled()).toBe(true);
        });

        it('should associate the creative from the placement level', function() {
            page.saveCreativeInsertionBtn.click();
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[2]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(5);
        });

        it('should search and open flyout on the group level', function() {
            var EC = protractor.ExpectedConditions;
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(CONSTANTS.defaultGroupName);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(6);

            page.rowCreativeGroup.get(0).click();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });

        it('should open associate creative group to placement dialog', function() {
            page.flyoutAddCreativesByGroupBtn.click();
            expect(page.creativeAssignationModal.isPresent()).toBe(true);
        });

        it('should move all creatives to right container', function() {
            page.moveAllCreativeToRightBtn.click();
            expect(page.creativeAssignationLeftContainerRows.count()).toEqual(0);
            expect(page.creativeAssignationRightContainerRows.count()).toBeGreaterThan(0);
        });

        it('should move all creatives to Insertion List', function() {
            page.moveAllCreativeToInsertionList.click();
            expect(page.saveCreativeInsertionBtn.isEnabled()).toBe(true);
        });

        it('should remove creatives from Insertion List', function() {
            var index = 0,
                list = page.removeCreativeInsertionBtn;

            page.removeCreativeInsertionBtn.then(function(elements){
                index = elements.length - 1;
                while(index > 0){
                    list.get(index).click();
                    index--;
                }
            });

            expect(page.saveCreativeInsertionBtn.isEnabled()).toBe(true);
        });

        it('should associate the creative from the group level', function() {
            page.saveCreativeInsertionBtn.click();
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[3]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(5);
            navigate.mediaTrafficWarning(campaignName);
            util.closeWarningMessage();
        });
    });
});

module.exports = scheduleSitePivotAssociations;
