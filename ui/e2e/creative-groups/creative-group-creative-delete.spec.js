'use strict';

var deleteCreativeGroupCreative = (function (campaignName, creativeGroupCustom) {
    describe('Creative on Creative Group', function() {
        var page = require('../page-object/creative-groups.po'),
            navigate = require('../utilities/navigation.spec'),
            nav = require('../page-object/navigation.po'),
            campaign = require('../page-object/campaigns.po'),
            creative = require('../page-object/creatives.po'),
            common = require('../utilities/common.spec'),
            CONSTANTS = require('../utilities/constants');

        it('should navigate to creative list', function() {
            navigate.campaignGrid();
            navigate.creativeGroupGrid(campaignName);
            navigate.creativeGroupDetails(campaignName, 'Default');
            nav.creativeTab.click();
            expect(browser.getLocationAbsUrl()).toContain('/creative/list');
            expect(creative.creativesRow.get(0).isDisplayed()).toBe(true);
        });

        it('should delete a creative from multiple creative group and 0 associations', function() {
            expect(creative.creativesRow.count()).toBe(4);
            expect(page.deleteCreativeBtn.isDisplayed()).toBe(true);
            creative.creativesRow.get(0).click();
            expect(page.deleteCreativeBtn.isEnabled()).toBe(true);
            page.deleteCreativeBtn.click();
            expect(page.deleteWarningPopup.isDisplayed()).toBe(true);
            page.deleteWarningPopupDelete.click();
            expect(creative.creativesRow.count()).toBe(3);
        });

        it('should show warning message when trying to delete a creative from multiple creative group and 1 associations', function() {
            expect(creative.creativesRow.count()).toBe(3);
            expect(page.deleteCreativeBtn.isDisplayed()).toBe(true);
            creative.creativesRow.get(0).click();
            expect(page.deleteCreativeBtn.isEnabled()).toBe(true);
            page.deleteCreativeBtn.click();
            expect(page.deleteWarningPopup.isDisplayed()).toBe(true);
            page.deleteWarningPopupDelete.click();
            expect(page.deleteWarningPopup.isPresent()).toBe(true);
            page.deleteWarningPopupDelete.click();
            expect(creative.creativesRow.count()).toBe(3);
        });


        it('should show warning message when trying to delete a creative from 1 creative group and 1 associations', function() {
            expect(creative.creativesRow.count()).toBe(3);
            expect(page.deleteCreativeBtn.isDisplayed()).toBe(true);
            creative.creativesRow.get(1).click();
            expect(page.deleteCreativeBtn.isEnabled()).toBe(true);
            page.deleteCreativeBtn.click();
            expect(page.deleteWarningPopup.isDisplayed()).toBe(true);
            page.deleteWarningPopupDelete.click();
            expect(page.deleteWarningPopup.isPresent()).toBe(true);
            page.deleteWarningPopupDelete.click();
            expect(creative.creativesRow.count()).toBe(3);
        });

        it('should show warning message when trying to delete a creative from 1 creative group and 0 associations', function() {
            expect(creative.creativesRow.count()).toBe(3);
            expect(page.deleteCreativeBtn.isDisplayed()).toBe(true);
            creative.creativesRow.get(2).click();
            expect(page.deleteCreativeBtn.isEnabled()).toBe(true);
            page.deleteCreativeBtn.click();
            expect(page.deleteWarningPopup.isDisplayed()).toBe(true);
            page.deleteWarningPopupDelete.click();
            expect(page.deleteWarningPopup.isPresent()).toBe(false);
            expect(creative.creativesRow.count()).toBe(2);
        });

        it('should navigate to creative list from Creative Group: ' + creativeGroupCustom, function() {
            navigate.creativeGroupDetails(campaignName, creativeGroupCustom);
            nav.creativeTab.click();
            expect(browser.getLocationAbsUrl()).toContain('/creative/list');
            expect(creative.creativesRow.get(0).isDisplayed()).toBe(true);
        });

        it('should delete a creative from creative group and 0 associations', function() {
            expect(creative.creativesRow.count()).toBe(2);
            expect(page.deleteCreativeBtn.isDisplayed()).toBe(true);
            creative.creativesRow.get(0).click();
            expect(page.deleteCreativeBtn.isEnabled()).toBe(true);
            page.deleteCreativeBtn.click();
            expect(page.deleteWarningPopup.isDisplayed()).toBe(true);
            page.deleteWarningPopupDelete.click();
            expect(creative.creativesRow.count()).toBe(1);
        });

        it('should delete a creative from multiple creative group and 1 associations from other creative group', function() {
            expect(creative.creativesRow.count()).toBe(1);
            expect(page.deleteCreativeBtn.isDisplayed()).toBe(true);
            creative.creativesRow.get(0).click();
            expect(page.deleteCreativeBtn.isEnabled()).toBe(true);
            page.deleteCreativeBtn.click();
            expect(page.deleteWarningPopup.isDisplayed()).toBe(true);
            page.deleteWarningPopupDelete.click();
            expect(creative.creativesRow.count()).toBe(0);
        });
    });
});

module.exports = deleteCreativeGroupCreative;
