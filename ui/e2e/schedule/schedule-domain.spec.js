'use strict';

var scheduleDomain = (function (campaignName) {

    describe('Traffic modal window', function () {
        var CONSTANTS = require('../utilities/constants'),
            creativeName = 'creative-gif-350x250',
            navigate = require('../utilities/navigation.spec'),
            page = require('../page-object/schedule.po'),
            util = require('../utilities/util');

        it('should click on Dropdown menu and change Domain', function () {
            navigate.scheduleGrid(campaignName);
            page.trafficBtn.click();
            expect(page.trafficSelectDomainsList.isDisplayed()).toBeTruthy();
            page.trafficSelectDomainsList.click();
            page.domainList.count().then(function (result) {
                var index = Math.random() * (result);
                index = Math.floor(index);
                page.domainList.get(index).click();
                expect(page.domainList.get(index).isSelected()).toBeTruthy();
            });
        });

        it('should show the primary contact as disabled', function () {
            expect(page.checkBoxesList.get(0).isEnabled()).toBeFalsy();
        });

        it('should display a list of active users', function () {
            expect(page.internalList.count()).toBeGreaterThan(0);
        });

        it('should display internal contacts as not selected ', function () {
            page.checkBoxesList.count().then(function (result) {
                for (var i = 1; i < result; i++) {
                    expect(page.checkBoxesList.get(i).isSelected()).toBeFalsy();
                }
            });
        });

        it('should display internal list sorted alphabetically', function () {
            var sorted = [] , unSorted = [], i = 0;
            page.checkBoxesListText.each(function (eachName) {
                eachName.getText().then(function (name) {
                    unSorted[i] = name;
                    i++;
                });
            }).then(function () {
                sorted = unSorted.slice();
                sorted.sort();
                if (unSorted.length > 0) {
                    var head = unSorted[0];
                    var index = sorted.indexOf(head);
                    if (index > -1) {
                        sorted.splice(index, 1);
                        unSorted.shift();
                    }
                }

                expect(sorted).toEqual(unSorted);
            });
        });

        it('should check/un-check an internal contact item', function () {
            page.checkBoxesList.count().then(function (result) {
                var index = Math.floor(Math.random() * (result - 2))  + 1;
                page.checkBoxesList.get(index).click();
                expect(page.checkBoxesList.get(index).isSelected()).toBeTruthy();
                page.checkBoxesList.get(index).click();
            });
        });

        it('should navigate to trafficking contacts tab', function () {
            page.traffickingContactList.click();
            expect(page.trafficSiteContactTable.isDisplayed()).toBeTruthy();
        });

        it('should show primary site trafficking contact as disabled', function () {
            expect(page.trafficSiteContactCheckboxesList.get(0).isEnabled()).toBeFalsy();
        });

        it('should check/un-check if there are available site trafficking contact items', function () {
            expect(page.trafficSiteContactCheckboxesList.count()).toBeGreaterThan(0);
            page.trafficSiteContactCheckboxesList.count().then(function (result) {
                if (result > 1) {
                    var index = Math.floor(Math.random() * (result - 2))  + 1;
                    page.trafficSiteContactCheckboxesList.get(index).click();
                    expect(page.trafficSiteContactCheckboxesList.get(index).isSelected()).toBeTruthy();
                    page.trafficSiteContactCheckboxesList.get(index).click();
                    expect(page.trafficSiteContactCheckboxesList.get(index).isSelected()).toBeFalsy();
                }
            });
        });

        it('should check main checkbox when all sites checkboxes are clicked', function () {
            page.trafficSiteContactCheckboxesList.each(function (element, index) {
                if (index > 0) {
                    element.click();
                }
            });
            expect(page.trafficSiteContactMainCheckbox.isSelected()).toBeTruthy();
            page.trafficSiteContactMainCheckbox.click();
        });

        it('should click on all checkboxes checkbox', function () {
            page.trafficSiteContactMainCheckbox.click();
            page.trafficSiteContactCheckboxesList.each(function (element, index) {
                var isChecked = true;
                if (index > 0) {
                    element.click();
                    if (!element.isSelected()) {
                        isChecked = isChecked && false;
                    }
                }
                expect(isChecked).toBeTruthy();
            });
        });


        it('should enable save button if there are changes', function () {
            page.trafficDialogCancel.click();
            page.trafficDialogOkButton.click();
            page.changePivotDropdown('Site');
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(creativeName);
            page.searchButtonSchedule.click();
            page.scheduleDataRows.get(4).click();
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutDataRows.get(0));
            }).then(function(){
                util.click(page.flyoutDataRows.get(0));
                expect(page.flyoutSave.isEnabled()).toBe(false);
                actions.sendKeys(protractor.Key.F2);
                actions.sendKeys(protractor.Key.ENTER);
                actions.perform();
                browser.wait(EC.elementToBeClickable(page.flyoutSave), CONSTANTS.defaultWaitInterval);
                expect(page.flyoutSave.isEnabled()).toBe(true);
            });
        });

        it('should show \'* Taffic\' as label on traffic button', function () {
            page.flyoutSave.click();
            expect(page.trafficBtn.getText()).toEqual('(*) Traffic');
        });

        it('should show Warning popup after changing tab', function () {
            navigate.mediaTrafficWarning(campaignName);
            expect(page.btnModalYes.isDisplayed()).toBeTruthy();
        });

        it('should show traffic modal window after Traffic Now button is clicked', function () {
            page.btnModalYes.click();
            expect(page.trafficDialogCancel.isDisplayed()).toBeTruthy();
        });

        it('should navigate to media tab after Traffic Later button is clicked', function () {
            var EC = protractor.ExpectedConditions;

            page.trafficDialogCancel.click();
            page.trafficDialogOkButton.click();
            browser.wait(EC.not(EC.presenceOf(page.trafficDialogOkButton)), CONSTANTS.defaultWaitInterval);
            navigate.mediaTrafficWarning(campaignName);
            page.trafficDoNotShowAgain.click();
            page.btnModalNo.click();
        });

        it('should not show traffic modal warning', function () {
            navigate.scheduleGrid(campaignName);
            page.changePivotDropdown('Site');
            page.searchInputSchedule.clear();
            page.searchInputSchedule.sendKeys(creativeName);
            page.searchButtonSchedule.click();
            page.scheduleDataRows.get(4).click();
            var EC = protractor.ExpectedConditions,
                actions = browser.actions();
            browser.wait(EC.visibilityOf(page.modalTitle), CONSTANTS.defaultWaitInterval);
            browser.driver.wait(function() {
                return EC.elementToBeClickable(page.flyoutDataRows.get(0));
            }).then(function(){
                util.click(page.flyoutDataRows.get(0));
                expect(page.flyoutSave.isEnabled()).toBe(false);
                actions.sendKeys(protractor.Key.F2);
                actions.sendKeys(protractor.Key.ENTER);
                actions.perform();
                browser.wait(EC.elementToBeClickable(page.flyoutSave), CONSTANTS.defaultWaitInterval);
                expect(page.flyoutSave.isEnabled()).toBe(true);
            });
            page.flyoutSave.click();
            navigate.mediaTrafficWarning(campaignName);
            expect(page.plusBtn.isDisplayed()).toBeTruthy();
        });
    });
});

module.exports = scheduleDomain;
