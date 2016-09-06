'use strict';

var campaignsCreate = (function () {

  describe('Campaign Create', function() {
    var page = require('../page-object/campaigns.po'),
        nav = require('../page-object/navigation.po'),
        campaignUtil = require('../utilities/campaigns.js');

    it('should create a new campaign', function() {
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        }); 
        var campaign = campaignUtil.addCampaign('Protractor Create Campaign');
        expect(page.campaignNameField.getAttribute('value')).toEqual(campaign.campaignName);
        expect(page.brandField.getText()).toContain(campaign.brandName);
        expect(page.advertiserField.getText()).toContain(campaign.advertiserName);
        expect(page.domainField.$('option:checked').getText()).toContain(campaign.domainName);
        expect(page.budget.getAttribute('value')).toEqual(campaign.budget);
        global.countCampaigns++;
    });

    it('should create a second new campaign', function() {    
        var campaign = campaignUtil.addCampaign('Protractor Create Campaign2');
        expect(page.campaignNameField.getAttribute('value')).toEqual(campaign.campaignName);
        expect(page.brandField.getText()).toContain(campaign.brandName);
        expect(page.advertiserField.getText()).toContain(campaign.advertiserName);
        expect(page.domainField.$('option:checked').getText()).toContain(campaign.domainName);
        expect(page.budget.getAttribute('value')).toEqual(campaign.budget);
        global.countCampaigns++;
    });

    it('should navigate back to list of campaigns', function() {
        expect(page.listOfCampaignsBtn.isPresent()).toBe(true);
        page.listOfCampaignsBtn.click();
        expect(browser.getLocationAbsUrl()).toContain('/campaigns');
        expect(page.campaignsRow.get(0).isDisplayed()).toBe(true);
        expect(page.listOfCampaignsBtn.isPresent()).toBe(false);
    });
  });
});

module.exports = campaignsCreate;