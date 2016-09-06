'use strict';

var scheduleEditCTUrl = (function (campaignName) {
    describe('Schedule Edit Click Through', function() {
        var page = require('../page-object/schedule.po'),
            navigate = require('../utilities/navigation.spec'),
            creativeName = 'creative-gif-350x250',
            singleCTCreativeName = 'image-1-350x250',
            newClickThrough = 'http://www.newcturl.com',
            CTSupportedSpecialChars = 'http://WWW.FOO!@#$%^&*=+{}[]|?~-_;:.BAR!@#$%^&*=+{}[]|?~-_;:?' +
                                      'Q=TEST%20URL-ENCODED%20STUFF@example.com/!#$%^*()=+{}[]|~<>-_;:' +
                                      '@?!#$%^*()=+{}[]|~<>-_;:@&!#$%^*()=+{}[]|~<>-_;:@',
            constants = require('../utilities/constants'),
            util = require('../utilities/util'),
            EC = protractor.ExpectedConditions,
            thirdPartyCreativeName = 'third-party-creative',
            invalidUrls = ['http://!@#$%^&*()=+{}[]|?~<>-_;:.google.com',
                'http://www.!@#$%^&*()=+{}[]|?~<>-_;:.com',
                'http://!@#$%^&*()=+{}[]|?~<>-_;:.!@#$%^&*()=+{}[]|?~<>-_;:.com',
                'http://www.example.com/wpstyle/?p=36,4',
                'http://www.goo(>gle.com'];

        it('should navigate to schedule grid', function() {
            browser.wait(function() {
                return global.runSchedule;
            });
            navigate.scheduleGrid(campaignName);
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
        });

        it('should open flyout for ' + singleCTCreativeName, function() {
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(singleCTCreativeName);
            page.searchButtonSchedule.click()
            page.scheduleDataRows.get(4).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });


        it('should select click through url row on flyout to edit', function () {
            var actions = browser.actions();
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutDataRows.get(0));
            }).then(function(){
                util.click(page.flyoutDataRows.get(0));
                expect(page.flyoutSave.isEnabled()).toBe(false);
                actions.sendKeys(protractor.Key.F2);
                actions.sendKeys(protractor.Key.TAB);
                actions.perform();
                browser.wait(page.flyoutSave.isEnabled(), constants.defaultWaitInterval);
                expect(page.flyoutSave.isEnabled()).toBe(true);
            });
        });

        it('should support click through url with special chars in flyout edit', function () {
            var actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), constants.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(true);
            actions.sendKeys(protractor.Key.F2);
            actions.sendKeys(protractor.Key.TAB);
            actions.sendKeys(protractor.Key.TAB);
            actions.sendKeys(protractor.Key.TAB);
            actions.perform();

            browser.driver.wait(function() {
                return page.flyoutCreativeInsertionEditCT.isPresent();
            }).then(function(){
                page.flyoutCreativeInsertionEditCT.clear();
                page.flyoutCreativeInsertionEditCT.sendKeys(CTSupportedSpecialChars);
                page.flyoutCreativeInsertionEditCT.sendKeys(protractor.Key.ENTER);
                expect(page.flyoutSave.isEnabled()).toBe(true);
                page.modalClose.click();
            });
        });

        it('should support click through url with special chars in flyout bulk edit', function() {
            var EC = protractor.ExpectedConditions;
            util.click(page.scheduleDataRows.get(3));
            browser.wait(EC.visibilityOf(page.modalTitle), constants.defaultWaitInterval);
            browser.wait(EC.elementToBeClickable(page.flyoutRowExpand.get(0)), constants.defaultWaitInterval);
            page.flyoutRowExpand.get(0).click();
            page.flyoutBulkEditBtn.click();
            browser.wait(EC.visibilityOf(page.ctUrl), constants.defaultWaitInterval);
            page.ctUrl.sendKeys(CTSupportedSpecialChars);
            page.ctUrl.sendKeys(protractor.Key.TAB);
            expect(page.flyoutApplytoBtn.isEnabled()).toBe(true);
            page.modalClose.click();
        });

        it('should open flyout for ' + thirdPartyCreativeName, function() {
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(thirdPartyCreativeName);
            page.searchButtonSchedule.click();
            expect(page.lastCtrUrlSchedule.getText()).toEqual('');
            page.scheduleDataRows.get(4).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });


        it('should not display CTR URL for 3rd party creative', function () {
            var actions = browser.actions();
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutDataRows.get(0));
            }).then(function(){
                util.click(page.flyoutDataRows.get(0));
                expect(page.flyoutSave.isEnabled()).toBe(false);
                actions.sendKeys(protractor.Key.F2);
                actions.sendKeys(protractor.Key.TAB);
                actions.perform();
                browser.wait(page.flyoutSave.isEnabled(), constants.defaultWaitInterval);
                expect(page.flyoutLastCtrUrl.getText()).toEqual('');
                expect(page.flyoutSave.isEnabled()).toBe(true);
            });
        });

        it('should not be able to edit a 3rd party creative\'s CTR URL', function () {
            var actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), 2000);
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(true);
            actions.sendKeys(protractor.Key.F2);
            actions.sendKeys(protractor.Key.TAB);
            actions.sendKeys(protractor.Key.TAB);
            actions.sendKeys(protractor.Key.TAB);
            actions.perform();
            expect(page.editCTUrl.isPresent()).toBe(false);
            page.modalClose.click();
        });

        it('should open flyout for ' + creativeName, function() {
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(creativeName);
            page.searchButtonSchedule.click();
            page.scheduleDataRows.get(4).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
        });

        it('should select click through url row ', function () {
            var actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), constants.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(false);
            actions.sendKeys(protractor.Key.F2);
            actions.sendKeys(protractor.Key.TAB);
            actions.perform();
        });

        it('should open multiple click through url edition popup ', function () {
            var actions = browser.actions();
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(true);
            actions.sendKeys(protractor.Key.F2);
            actions.sendKeys(protractor.Key.TAB);
            actions.sendKeys(protractor.Key.TAB);
            actions.sendKeys(protractor.Key.TAB);
            actions.perform();
            browser.wait(EC.presenceOf(page.editCTUrl), constants.defaultWaitInterval);
            expect(page.editCTUrl.isDisplayed()).toBe(true);
            expect(page.clickthroughs.count()).toBe(1);
        });

        it('should not enable save button when add invalid click through urls', function () {
            page.editCTUrlAddBtn.click();
            expect(page.editCTUrlSaveBtn.isEnabled()).toBe(false);

            for (var i =0; i < invalidUrls.length; i++) {
                page.newCTUrlField.sendKeys(invalidUrls[i]);
                expect(page.editCTUrlSaveBtn.isEnabled()).toBe(false);
                page.newCTUrlField.clear();
            }

            page.editCTUrlRemoveBtn.click();
            expect(page.clickthroughs.count()).toBe(1);
        });

        it('should add a new click through url row ', function () {
            page.editCTUrlAddBtn.click();
            expect(page.editCTUrlSaveBtn.isEnabled()).toBe(false);

            page.newCTUrlField.sendKeys(newClickThrough);
            expect(page.editCTUrlSaveBtn.isEnabled()).toBe(true);
            expect(page.clickthroughs.count()).toBe(2);

            page.editCTUrlSaveBtn.click();
            browser.wait(EC.not(EC.presenceOf(page.editCTUrl)), constants.defaultWaitInterval);
            expect(page.editCTUrl.isPresent()).toBe(false);
        });

        it('should remove a last click through url row ', function () {
            var actions = browser.actions();
            browser.wait(EC.elementToBeClickable(page.flyoutDataRows.get(0)), constants.defaultWaitInterval);
            util.click(page.flyoutDataRows.get(0));
            expect(page.flyoutSave.isEnabled()).toBe(true);
            actions.sendKeys(protractor.Key.F2);
            actions.sendKeys(protractor.Key.TAB);
            actions.sendKeys(protractor.Key.TAB);
            actions.sendKeys(protractor.Key.TAB);
            actions.perform();
            expect(page.editCTUrl.isDisplayed()).toBe(true);
            expect(page.clickthroughs.count()).toBe(2);

            page.editCTUrlRemoveBtn.click();
            expect(page.clickthroughs.count()).toBe(1);

            page.editCTUrlSaveBtn.click();
            browser.wait(EC.not(EC.presenceOf(page.editCTUrl)), constants.defaultWaitInterval);
            expect(page.editCTUrl.isPresent()).toBe(false);
        });
    });
});

module.exports = scheduleEditCTUrl;
