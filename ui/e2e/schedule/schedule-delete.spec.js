'use strict';

var scheduleDelete = (function (campaignName, bootstrapData) {
    describe('Schedule Flyout', function () {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/schedule.po'),
            navigate = require('../utilities/navigation.spec'),
            util = require('../utilities/util'),
            utilPo = require('../page-object/utilities.po');

        it('should navigate to Add Schedule Assignment modal and create associations', function () {
            navigate.newScheduleAssignment(campaignName);
            expect(page.scheduleAssignmentModal.isPresent()).toBe(true);
            expect(page.availPlacementsGrid.isDisplayed()).toBe(true);
            expect(page.availCreativesGrid.isDisplayed()).toBe(true);
            expect(page.pendingAssignSection.isDisplayed()).toBe(true);
            expect(page.pendingAssignGrid.isDisplayed()).toBe(false);

            util.click(page.availPlacementsSelectAll);
            util.click(page.availCreativesSelectAll);
            expect(page.pendingAssignGrid.isDisplayed()).toBe(true);

            util.click(page.scheduleAssignmentSave);
            util.click(page.dismissDuplicateDialog);
            expect(page.scheduleAssignmentModal.isPresent()).toBe(false);
        });

        it('should delete a creative insertion when PLACEMENT is Pivot and no dialog should be shown', function () {
            var EC = protractor.ExpectedConditions;
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            for (var i = 0; i < 2; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(10);

            util.click(page.scheduleDataRows.get(0));
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(2)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(2));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(2));
            util.click(page.flyoutDelete);
            expect(page.modalTitle.isDisplayed()).toBe(false);

            browser.wait(EC.elementToBeClickable(page.scheduleDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            for (var j = 0; j < 2; j++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(9);
        });

        it('should delete a creative insertion when GROUP is Pivot and no dialog should be shown', function () {
            var EC = protractor.ExpectedConditions;
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            for (var i = 0; i < 3; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(10);

            util.click(page.scheduleDataRows.get(2));
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(2)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(2));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(2));
            util.click(page.flyoutDelete);
            expect(page.modalTitle.isDisplayed()).toBe(false);

            browser.wait(EC.elementToBeClickable(page.scheduleDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            for (var j = 0; j < 4; j++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(14);
        });

        it('should delete a group insertion when PLACEMENT is Pivot and a dialog should be shown', function () {
            var EC = protractor.ExpectedConditions;
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            for (var i = 0; i < 2; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(8);

            util.click(page.scheduleDataRows.get(0));
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(1)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(1));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(1));
            util.click(page.flyoutDelete);
            expect(page.modalTitle.isDisplayed()).toBeTruthy();

            util.click(page.trafficDialogOkButton);
            expect(page.scheduleDataRows.count()).toEqual(3);
        });

        it('should delete a placement insertion when GROUP is Pivot and a dialog should be shown', function () {
            var EC = protractor.ExpectedConditions;
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            for (var i = 0; i < 3; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(10);

            util.click(page.scheduleDataRows.get(2));
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(0));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(0));
            util.click(page.flyoutDelete);
            expect(page.modalTitle.isDisplayed()).toBeTruthy();

            util.click(page.trafficDialogOkButton);
            for (var j = 0; j < 2; j++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(4);
        });

        it('should delete a creative insertion when CREATIVE is Pivot and a dialog should be shown', function () {
            var EC = protractor.ExpectedConditions;
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.CREATIVE);
            expect(page.scheduleDataRows.count()).toEqual(6);

            util.click(page.scheduleDataRows.get(0));
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            utilPo.ensureCheck(page.flyoutCheckboxes.get(0));
            util.click(page.flyoutDelete);
            expect(page.modalTitle.isDisplayed()).toBeTruthy();

            util.click(page.trafficDialogOkButton);
            expect(page.scheduleDataRows.count()).toEqual(5);
        });

        it('should delete a creative insertion within a search context when SITE is pivot', function () {
            var EC = protractor.ExpectedConditions;
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            for (var i = 0; i < 4; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(9);

            util.click(page.scheduleDataRows.get(3));
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(2);
            utilPo.ensureCheck(page.flyoutCheckboxes.get(1));
            util.click(page.flyoutDelete);
            expect(page.modalTitle.isDisplayed()).toBe(false);

            browser.wait(EC.elementToBeClickable(page.scheduleDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            for (var j = 0; j < 4; j++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(8);
        });

        it('should delete a creative insertion within a search context when PLACEMENT is pivot', function () {
            var EC = protractor.ExpectedConditions;
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            for (var j = 0; j < 2; j++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(6);

            util.click(page.scheduleDataRows.get(1));
            browser.wait(EC.elementToBeClickable(page.searchInputFlyout), CONSTANTS.defaultWaitInterval);
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[1]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(2);
            utilPo.ensureCheck(page.flyoutCheckboxes.get(1));
            util.click(page.flyoutDelete);
            expect(page.modalTitle.isDisplayed()).toBe(false);

            browser.wait(EC.elementToBeClickable(page.scheduleDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            for (var j = 0; j < 2; j++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(5);
        });

        it('should delete a creative insertion within a search context when GROUP is pivot', function () {
            var EC = protractor.ExpectedConditions;
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            for (var i = 0; i < 3; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(6);

            util.click(page.scheduleDataRows.get(2));
            browser.wait(EC.elementToBeClickable(page.searchInputFlyout), CONSTANTS.defaultWaitInterval);
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[2]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(2);
            utilPo.ensureCheck(page.flyoutCheckboxes.get(1));
            util.click(page.flyoutDelete);
            expect(page.modalTitle.isDisplayed()).toBe(false);

            browser.wait(EC.elementToBeClickable(page.scheduleDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            for (var j = 0; j < 3; j++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toEqual(5);
        });

    });
});

module.exports = scheduleDelete;
