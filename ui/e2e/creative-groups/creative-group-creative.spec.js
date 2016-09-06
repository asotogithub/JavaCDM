'use strict';

var creativeGroupCreative = (function (campaignName) {
    describe('Creative on Creative Group', function() {
        var page = require('../page-object/creative-groups.po'),
            navigate = require('../utilities/navigation.spec'),
            nav = require('../page-object/navigation.po'),
            campaign = require('../page-object/campaigns.po'),
            creative = require('../page-object/creatives.po'),
            common = require('../utilities/common.spec'),
            totalCreatives;

        it('should navigate to campaign: ' + campaignName, function() {
            navigate.campaignGrid();
            expect(campaign.campaignRowByName(campaignName).isDisplayed()).toBe(true);
            navigate.creativeGroupGrid(campaignName);
        });

        it('should navigate to default creative groups', function() {
            navigate.creativeGroupDetails(campaignName, 'Default');
            expect(page.nameField.isPresent()).toBe(false);
            expect(page.nameReadOnly.getText()).toEqual('Default');
        });

        it('should navigate to creative list', function() {
            nav.creativeTab.click();
            expect(browser.getLocationAbsUrl()).toContain('/creative/list');
            expect(creative.creativesRow.get(0).isDisplayed()).toBe(true);
        });

        it('should display the creatives counter', function() {
            page.dataRows.then(function(rows){
                totalCreatives = rows.length;
                expect(page.legendTable.isDisplayed()).toBe(true);
                expect(page.legendTable.getText()).toContain(totalCreatives);
            });
        });

        it('should update the creatives counter on search context',function() {
            page.searchInputByGrid(page.creativeGroupsCreativeList).sendKeys('image-1');
            page.dataRows.then(function(rows){
                expect(page.legendTable.getText()).toContain(rows.length);
                expect(page.legendTable.getText()).toContain(totalCreatives);
            });
        });

        it('should navigate to creative details', function() {
            expect(creative.editCreativeBtn.isEnabled()).toBe(false);
            creative.creativesRow.get(0).click();
            expect(creative.editCreativeBtn.isEnabled()).toBe(true);
            creative.editCreativeBtn.click();
            expect(browser.getLocationAbsUrl()).toContain('/details/click-through');
        });

        it('should return to creative list from creative group', function() {
            expect(creative.backToCreativeList.isDisplayed()).toBe(true);
            creative.backToCreativeList.click();
            expect(browser.getLocationAbsUrl()).toContain('/creative/list');
            expect(creative.creativesRow.get(0).isDisplayed()).toBe(true);
        });
    });
});

module.exports = creativeGroupCreative;
