'use strict';

var dataSetup = (function () {

  describe('Creative Groups Data Setup', function() {
    var campaignUtil = require('../utilities/campaigns.js'),
        navigate = require('../utilities/navigation.spec'),
        creativeGroup = require('../utilities/creative-groups.spec'),
        campaign = require('../page-object/campaigns.po'),
        cg = require('../page-object/creative-groups.po'),
        nav = require('../page-object/navigation.po'),
        campaignName1 = 'Protractor Campaign',
        cg1 = creativeGroup.creativeGroupDefault,
        common = require('../utilities/common.spec');

    it('should create a new campaign: ' + campaignName1, function() {
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        });
        var campaign1 = campaignUtil.addCampaign(campaignName1);
        navigate.campaignGrid();
        expect(campaign.campaignRowByName(campaignName1).isDisplayed()).toBe(true);
        global.countCampaigns = global.countCampaigns + 1;
    });

    it('should create a creative group', function() {
        common.newCreativeGroup(campaignName1, cg1);
        navigate.creativeGroupGrid(campaignName1);
        expect(cg.cgRowByName(cg1.creativeGroupName).isDisplayed()).toBe(true);
    });

  });
});

module.exports = dataSetup;
