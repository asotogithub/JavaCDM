'use strict';

var scheduleServerSideSearch = (function (campaignName, bootstrapData) {
    var CONSTANTS = require('../utilities/constants'),
        page = require('../page-object/schedule.po'),
        navigate = require('../utilities/navigation.spec'),
        notExistentItem = 'ThisNameDoesntExist',
        util = require('../page-object/utilities.po'),
        startDate = '01/29/2050 10:00:00 AM',
        endDate = '01/30/2050 10:00:00 PM',
        ctrUrl = 'http://www.cnn.com',
        startDateCtrl = '01292050100000AM',
        endDateCtrl = '01302050100000PM';

    describe('Server Side Search on Schedule View', function() {
        it('should search creative when SITE is pivot', function() {
            navigate.scheduleGrid(campaignName);
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(5);
        });

        it('should open flyout on placement level', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            page.scheduleDataRows.get(2).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });

        it('should search group on the flyout when SITE is pivot', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutDataRows.get(0));
            }).then(function(){
                page.searchInputFlyout.clear();
                page.searchInputFlyout.sendKeys(CONSTANTS.defaultGroupName);
                page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
                expect(page.flyoutDataRows.count()).toEqual(2);
                expect(page.scheduleDataRows.count()).toEqual(5);
            });
        });

        it('should search creative on the flyout when SITE is pivot', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[3]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(3);
            expect(page.scheduleDataRows.count()).toEqual(5);
            page.modalClose.click();
        });

        it('should search placement when SITE is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.placements[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(3);
        });

        it('should search group when SITE is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(CONSTANTS.defaultGroupName);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(6);
        });

        it('should not find item if not exist when SITE is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(notExistentItem);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(0);
        });

        it('should search creative when PLACEMENT is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(3);
        });

        it('should open flyout on placement level', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            page.scheduleDataRows.get(0).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });

        it('should search group on the flyout when PLACEMENT is pivot', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutDataRows.get(0));
            }).then(function(){
                page.searchInputFlyout.clear();
                page.searchInputFlyout.sendKeys(CONSTANTS.defaultGroupName);
                page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
                expect(page.flyoutDataRows.count()).toEqual(2);
                expect(page.scheduleDataRows.count()).toEqual(3);
            });
        });

        it('should search creative on the flyout when PLACEMENT is pivot', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[3]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(3);
            expect(page.scheduleDataRows.count()).toEqual(3);
            page.modalClose.click();
        });

        it('should search placement when PLACEMENT is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.placements[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(1);
        });

        it('should search group when PLACEMENT is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(CONSTANTS.defaultGroupName);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(4);
        });

        it('should not find item if not exist when PLACEMENT is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(notExistentItem);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(0);
        });

        it('should search creative when GROUP is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(4);
        });

        it('should open flyout on site level', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            page.scheduleDataRows.get(1).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });

        it('should search placement on the flyout when GROUP is pivot', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutDataRows.get(0));
            }).then(function(){
                page.searchInputFlyout.clear();
                page.searchInputFlyout.sendKeys(bootstrapData.placements[0]);
                page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
                expect(page.flyoutDataRows.count()).toEqual(2);
                expect(page.scheduleDataRows.count()).toEqual(4);
            });
        });

        it('should search creative on the flyout when GROUP is pivot', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[3]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(3);
            expect(page.scheduleDataRows.count()).toEqual(4);
            page.modalClose.click();
        });

        it('should search placement when GROUP is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.placements[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(3);
        });

        it('should search group when GROUP is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(CONSTANTS.defaultGroupName);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(1);
        });

        it('should not find item if not exist when GROUP is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(notExistentItem);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(0);
        });

        it('should not find creative on PLACEMENT pivot when creative option is unchecked', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            page.searchInputSchedule.clear();
            page.checkUncheckScheduleSearchOptions(CONSTANTS.SCHEDULE_SEARCH_OPTION.CREATIVE);
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(0);
        });

        it('should not find creative on CREATIVE pivot when creative option is unchecked', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.CREATIVE);
            page.searchInputSchedule.clear();
            page.checkUncheckScheduleSearchOptions(CONSTANTS.SCHEDULE_SEARCH_OPTION.CREATIVE);
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(0);
        });

        it('should not find creative on SITE pivot when creative option is unchecked', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            page.searchInputSchedule.clear();
            page.checkUncheckScheduleSearchOptions(CONSTANTS.SCHEDULE_SEARCH_OPTION.CREATIVE);
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(0);
        });

        it('should not find creative on GROUP pivot when creative option is unchecked', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            page.searchInputSchedule.clear();
            page.checkUncheckScheduleSearchOptions(CONSTANTS.SCHEDULE_SEARCH_OPTION.CREATIVE);
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(0);
        });

        it('should search creative when CREATIVE is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.CREATIVE);
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(5);
        });

        it('should search creative on the flyout when CREATIVE is pivot', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();

            page.scheduleDataRows.get(0).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(5);
            page.modalClose.click();
        });

        it('should find group on CREATIVE pivot when creative option is unchecked', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.CREATIVE);
            page.searchInputSchedule.clear();
            page.checkUncheckScheduleSearchOptions(CONSTANTS.SCHEDULE_SEARCH_OPTION.CREATIVE);
            page.searchInputSchedule.sendKeys(CONSTANTS.defaultGroupName);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(16);
        });

        it('should find group on flyout for CREATIVE pivot when creative option is unchecked', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();

            page.scheduleDataRows.get(0).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(CONSTANTS.defaultGroupName);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(4);
            page.modalClose.click();
        });

        it('should keep previously checked elements, after a repeated searching', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            browser.actions().mouseMove(page.scheduleDataRows.get(0)).perform();
            page.scheduleDataRows.get(0).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);

            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            util.ensureCheck(page.flyoutCheckboxes.get(4));
            page.searchInputFlyout.clear();

            page.searchInputFlyout.sendKeys(CONSTANTS.defaultGroupName);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            util.ensureCheck(page.flyoutCheckboxes.get(3));
            page.searchInputFlyout.clear();

            page.searchInputFlyout.sendKeys(bootstrapData.placements[0]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            util.ensureCheck(page.flyoutCheckboxes.get(2));

            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.placements[0]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutCheckboxes.get(2).isSelected()).toBe(true);

            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(CONSTANTS.defaultGroupName);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutCheckboxes.get(3).isSelected()).toBe(true);

            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutCheckboxes.get(4).isSelected()).toBe(true);

            page.modalClose.click();
        });

        it('should bulk edit after a repeated searching', function() {
            page.searchInputSchedule.clear();
            for (var i = 0; i < 4; i++) {
                page.rowExpand.get(0).click();
            }

            page.scheduleDataRows.get(0).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);

            page.flyoutBulkEditBtn.click();
            expect(page.flyoutApplytoBtn.isDisplayed()).toBe(true);

            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[0]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            util.ensureCheck(page.flyoutCheckboxes.get(4));
            page.searchInputFlyout.clear();

            page.searchInputFlyout.sendKeys(bootstrapData.creativesDisplayName[3]);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            util.ensureCheck(page.flyoutCheckboxes.get(4));
            page.searchInputFlyout.clear();

            page.creativeWeigth.click();
            page.creativeWeigth.sendKeys(85);
            page.inputStartDate.sendKeys(startDateCtrl);
            page.inputEndDate.sendKeys(endDateCtrl);
            page.ctUrl.sendKeys(ctrUrl);
            page.flyoutApplytoBtn.click();
            expect(page.flyoutSave.isEnabled()).toBe(true);

            for (var i = 5; i<=6; i++) {
                page.flyoutDataRows.get(i).getText().then(function(text) {
                    var textArray = [],
                        creativeDesc = [],
                        weight = [];
                    textArray = text.split(" ");
                    creativeDesc = text.split("\n");
                    weight = creativeDesc[1].split(" ");
                    expect(weight[0]).toEqual('85');
                    expect(textArray[2] + " " + textArray[3] + " " + textArray[4]).toEqual(startDate);
                    expect(textArray[5] + " " + textArray[6] + " " + textArray[7]).toEqual(endDate);
                    expect(textArray[8]).toEqual(ctrUrl);
                });
            }
        });
    });
});

module.exports = scheduleServerSideSearch;
