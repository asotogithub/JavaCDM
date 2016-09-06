'use strict';

var dataSetup = (function () {

  describe('Media Data Setup', function() {
    var page = require('../page-object/media.po'),
        campaign = require('../page-object/campaigns.po'),
        campaignUtil = require('../utilities/campaigns.js'),
        navigate = require('../utilities/navigation.spec'),
        nav = require('../page-object/navigation.po'),
        creativeGroup = require('../utilities/creative-groups.spec'),
        campaignName = 'Media Protractor Campaign',
        ioName = 'Protractor IO',
        ioName2 = 'Second IO',
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
    
    it('should create 2 new IOs', function() {
        common.addIo(campaignName, ioName);
        common.addIo(campaignName, ioName2);
        navigate.mediaGrid(campaignName);
        expect(page.ioRowByName(ioName).isDisplayed()).toBe(true);
        expect(page.ioRowByName(ioName2).isDisplayed()).toBe(true);
    });

  });
});

module.exports = dataSetup;