'use strict';

var scheduleCreativeInsertionWeight = (function (campaignName) {
    describe('Schedule Creative Insertion Weight', function() {
        var CONSTANTS = require('../utilities/constants'),
            maxCreativeWeight = 10000,
            notAllowedCreativeWeight = 999999,
            navigate = require('../utilities/navigation.spec'),
            page = require('../page-object/schedule.po'),
            scheduleDetail = {
                scheduleName: 'New Placement for Protractor'
            },
            util = require('../utilities/util');

        it('should navigate to schedule grid', function() {
            navigate.scheduleGrid(campaignName);
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
        });

        it('should open flyout with creative insertion', function() {
            var EC = protractor.ExpectedConditions,
                elementIndex = 0;

            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(scheduleDetail.scheduleName);
            page.searchButtonSchedule.click();
            expect(page.scheduleDataRows.count()).toEqual(3);

            page.rowExpand.get(0).click();
            expect(page.scheduleDataRows.count()).toEqual(4);

            page.rowExpand.get(0).click();
            expect(page.scheduleDataRows.count()).toEqual(7);

            page.scheduleDataRows.get(4).click();

            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });

        it('should enable save button after open creative weight on edit mode', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(false);
            actions.sendKeys(protractor.Key.F2);
            actions.sendKeys(protractor.Key.TAB);
            actions.perform();
            browser.wait(page.flyoutSave.isEnabled(), CONSTANTS.defaultWaitInterval);
            expect(page.flyoutSave.isEnabled()).toBe(true);
        });

        it('should allow set max creative weight value', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(true);
            actions.sendKeys(protractor.Key.F2);
            actions.perform();

            browser.wait(EC.visibilityOf(page.flyoutCreativeInsertionEditWeight), CONSTANTS.defaultWaitInterval);
            page.flyoutCreativeInsertionEditWeight.clear();
            page.flyoutCreativeInsertionEditWeight.sendKeys(maxCreativeWeight);
            page.flyoutCreativeInsertionEditWeight.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutSave.isEnabled()).toBe(true);
        });

        it('should close and open flyout', function() {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            page.modalClose.click();
            browser.wait(EC.not(EC.visibilityOf(page.modalTitle)), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(false);
            page.scheduleDataRows.get(4).click();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(false);
            actions.sendKeys(protractor.Key.F2);
            actions.sendKeys(protractor.Key.TAB);
            actions.perform();
            browser.wait(page.flyoutSave.isEnabled(), CONSTANTS.defaultWaitInterval);
            expect(page.flyoutSave.isEnabled()).toBe(true);
        });

        it('should show validation error when value exceeds creative weight value', function () {
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), CONSTANTS.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(true);
            actions.sendKeys(protractor.Key.F2);
            actions.perform();
            browser.wait(EC.visibilityOf(page.flyoutCreativeInsertionEditWeight), CONSTANTS.defaultWaitInterval);
            page.flyoutCreativeInsertionEditWeight.clear();
            page.flyoutCreativeInsertionEditWeight.sendKeys(notAllowedCreativeWeight);
            page.flyoutCreativeInsertionEditWeight.sendKeys(protractor.Key.ENTER);
            expect(page.flyoutValidationMessage.isPresent()).toBe(true);
            expect(page.flyoutSave.isEnabled()).toBe(false);
        });
    });
});

module.exports = scheduleCreativeInsertionWeight;
