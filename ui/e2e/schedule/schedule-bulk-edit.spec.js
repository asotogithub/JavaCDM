'use strict'

var scheduleBulkEdit = (function(campaignName) {
    describe('Schedule Bulk Edit', function (){
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/schedule.po'),
            navigate = require('../utilities/navigation.spec'),
            utilPo = require('../page-object/utilities.po'),
            util = require('../utilities/util'),
            startDate = '01/29/2050 10:00:00 AM',
            endDate = '01/30/2050 10:00:00 PM',
            ctrUrl = 'http://www.cnn.com',
            startDateCtrl = '01292050100000AM',
            endDateCtrl = '01302050100000PM';

        it('should click on bulk edit button, expand row and and fields should keep changes', function () {
            var EC = protractor.ExpectedConditions;
            navigate.scheduleGrid(campaignName);
            for (var i = 0; i < 3; i++) {
                util.click(page.rowExpand.get(0));
            }
            util.click(page.scheduleDataRows.get(0));

            util.click(page.flyoutBulkEditBtn);
            expect(page.flyoutApplytoBtn.isDisplayed()).toBe(true);
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(3)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(3));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(3));
            util.click(page.creativeWeigth);
            page.creativeWeigth.sendKeys(85);
            util.click(page.flyoutApplytoBtn);
            expect(page.btnModalYes.isDisplayed()).toBeTruthy();
            util.click(page.btnModalYes);
            util.click(page.flyoutDataRows.get(3));

            page.flyoutDataRows.get(3).getText().then(function(text) {
                var textArray = [],
                    creativeDesc = [],
                    weight = [];
                textArray = text.split(" ");
                creativeDesc = text.split("\n");
                weight = creativeDesc[1].split(" ");
                expect(weight[0]).toEqual('85');
            });

            page.modalClose.click();
        });

        it('should click on bulk edit button and bulk edit fields are displayed', function () {
            navigate.scheduleGrid(campaignName);
            for (var i = 0; i < 4; i++) {
                util.click(page.rowExpand.get(0));
            }
            util.click(page.scheduleDataRows.get(0));
            util.click(page.flyoutBulkEditBtn);
            expect(page.flyoutApplytoBtn.isDisplayed()).toBe(true);
        });

        it('should select two different creatives', function () {
            var EC = protractor.ExpectedConditions;
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(4)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(4));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(4));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(5));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(6));
            expect(page.flyoutCheckboxes.isSelected().count()).toBeGreaterThan(1);
        });

        it('should bulk edit inputs have to be enable', function(){
            expect(page.ctUrl.isEnabled()).toBe(true);
            expect(page.inputStartDate.isEnabled()).toBe(true);
            expect(page.inputEndDate.isEnabled()).toBe(true);
            expect(page.creativeWeigth.isEnabled()).toBe(true);
        });

        it('should update checked creative with bulk edit values', function() {
            util.click(page.creativeWeigth);
            page.creativeWeigth.sendKeys(85);
            page.inputStartDate.sendKeys(startDateCtrl);
            page.inputEndDate.sendKeys(endDateCtrl);
            page.ctUrl.sendKeys(ctrUrl);
            util.click(page.flyoutApplytoBtn);
            expect(page.flyoutSave.isEnabled()).toBe(true);
        });

        it('should checked creative have to be updated it', function() {
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

        it('should perform a search and select two different creatives', function () {
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(CONSTANTS.defaultGroupName);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(6);
            util.click(page.flyoutRowExpand.get(0));
            expect(page.flyoutDataRows.count()).toEqual(9);
            util.click(page.flyoutDataRows.get(4));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(4));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(5));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(6));
            expect(page.flyoutCheckboxes.isSelected().count()).toBeGreaterThan(1);
        });

        it('should bulk edit inputs have to be enabled within search context', function(){
            expect(page.ctUrl.isEnabled()).toBe(true);
            expect(page.inputStartDate.isEnabled()).toBe(true);
            expect(page.inputEndDate.isEnabled()).toBe(true);
            expect(page.creativeWeigth.isEnabled()).toBe(true);
        });

        it('should update checked creative with bulk edit values within search context', function() {
            util.click(page.creativeWeigth);
            page.creativeWeigth.clear();
            page.creativeWeigth.sendKeys(85);
            page.inputStartDate.clear();
            page.inputStartDate.sendKeys(startDateCtrl);
            page.inputEndDate.clear();
            page.inputEndDate.sendKeys(endDateCtrl);
            page.ctUrl.clear();
            page.ctUrl.sendKeys(ctrUrl);
            util.click(page.flyoutApplytoBtn);
            expect(page.flyoutSave.isEnabled()).toBe(true);
        });

        it('should checked creative have to be updated it within search context', function() {
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
            util.click(page.modalClose);
        });

        it ('should group weight input field be displayed', function() {
            navigate.scheduleGrid(campaignName);
            util.click(page.rowExpand.get(0));
            util.click(page.rowExpand.get(0));
            util.click(page.rowExpand.get(0));
            util.click(page.scheduleDataRows.get(0));
            util.click(page.flyoutBulkEditBtn);
            expect(page.creativeWeigth.isDisplayed()).toBe(true);
        });

        it ('should select different shared creative groups', function () {
            utilPo.ensureCheck(page.flyoutCheckboxes.get(2));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(3));
            expect(page.flyoutCheckboxes.isSelected().count()).toBeGreaterThan(0);
        });

        it ('should show Warning popup when trying to update shared creative groups', function () {
            util.click(page.creativeWeigth);
            page.creativeWeigth.clear();
            page.creativeWeigth.sendKeys(85);
            util.click(page.flyoutApplytoBtn);
            expect(page.btnModalYes.isDisplayed()).toBeTruthy();
        });

        it ('should update shared creative group', function () {
            util.click(page.btnModalYes);
            expect(page.flyoutSave.isEnabled()).toBe(true);
            page.flyoutDataRows.get(3).getText().then(function(text) {
                var textArray = [];
                textArray = text.split("\n");
                expect(textArray[1]).toEqual('85');

            });
        });

        it ('should perform a search and select shared creative groups', function () {
            page.searchInputFlyout.clear();
            page.searchInputFlyout.sendKeys(CONSTANTS.defaultGroupName);
            page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutDataRows.count()).toEqual(6);
            utilPo.ensureCheck(page.flyoutCheckboxes.get(2));
            utilPo.ensureCheck(page.flyoutCheckboxes.get(3));
            expect(page.flyoutCheckboxes.isSelected().count()).toBeGreaterThan(0);
        });

        it ('should show Warning popup when trying to update shared creative groups within a search context', function () {
            util.click(page.creativeWeigth);
            page.creativeWeigth.clear();
            page.creativeWeigth.sendKeys(95);
            util.click(page.flyoutApplytoBtn);
            expect(page.btnModalYes.isDisplayed()).toBeTruthy();
        });

        it ('should update shared creative group within a search context', function () {
            util.click(page.btnModalYes);
            expect(page.flyoutSave.isEnabled()).toBe(true);
            page.flyoutDataRows.get(3).getText().then(function(text) {
                var textArray = [];
                textArray = text.split("\n");
                expect(textArray[1]).toEqual('95');

            });
        });
    });
});

module.exports = scheduleBulkEdit;
