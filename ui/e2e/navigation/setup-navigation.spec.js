'use strict';

var dataSetup = function (campaignName, ioName, creativeGroup) {

  describe('Navigation Data Setup', function() {
    var page = require('../page-object/media.po'),
        campaign = require('../page-object/campaigns.po'),
        campaignUtil = require('../utilities/campaigns.js'),
        navigate = require('../utilities/navigation.spec'),
        nav = require('../page-object/navigation.po'),
        common = require('../utilities/common.spec');

    it('should create a new campaign: ' +  campaignName, function() {    
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        });
        var campaign1 = campaignUtil.addCampaign(campaignName);
        navigate.campaignGrid();
        expect(campaign.campaignRowByName(campaignName).isDisplayed()).toBe(true);
        global.countCampaigns++;
    });
    
    it('should create a new IOs', function() {  
        common.addIo(campaignName, ioName);
        navigate.mediaGrid(campaignName);
        expect(page.ioRowByName(ioName).isDisplayed()).toBe(true);
    });

    it('should create a new creative group', function() {  
        common.newCreativeGroup(campaignName, creativeGroup);
    });

  });
};

module.exports = dataSetup;