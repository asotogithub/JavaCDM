'use strict';

var tagCampaign = (function (campaignName) {

    describe('Tag Campaign', function() {
        var navigate = require('../utilities/navigation.spec'),
            page = require('../page-object/tag-injection.po');

        it('should navigate to schedule grid', function() {
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            navigate.campaignGrid();
            expect(browser.getLocationAbsUrl()).toContain('/campaigns');
            navigate.scheduleGrid(campaignName);
            expect(browser.getLocationAbsUrl()).toContain('/schedule');
        });

        it('should navigate to tracking tag view from schedule view', function() {
            expect(page.goToTrackingTagView.isPresent()).toBe(true);
            page.goToTrackingTagView.click();
            expect(page.goToTrackingTagView.isPresent()).toBe(false);
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
        });

        it('should display the selected campaign: ' + campaignName, function() {
            expect(page.placementDataRows.count()).toBe(1);
            expect(page.placementDataRows.getText(0)).toContain(campaignName);
            expect(page.listOfCampaignBtn.isPresent()).toBe(true);
        });

        it('should navigate to schedule view from tracking tag view', function() {
            page.listOfCampaignBtn.click();
            expect(page.goToTrackingTagView.isPresent()).toBe(true);
            expect(browser.getLocationAbsUrl()).toContain('/schedule');
        });
    });
});

module.exports = tagCampaign;
