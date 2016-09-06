'use strict';

var campaignsDetail = (function () {

  describe('Campaign Summary', function() {
    var page = require('../page-object/metrics.po'),
        campaign = require('../page-object/campaigns.po'),
        nav = require('../page-object/navigation.po');

    it('should display Campaigns metrics with correct metrics', function() {
      nav.campaignsItem.click();
      var campaignName = campaign.campaignsRow.get(0).getText();
      campaign.dataRows.get(0).click();
      expect(campaign.campaignDetailHeader.getText()).toBe(campaignName);
      expect(page.metricsGrid.isDisplayed()).toBe(true);
      page.allMetricsItems.map(function(item) {
        return item.getText();
        }).then(function(labels) {
          expect(labels).toEqual(page.campaignMetrics);
        });
    });

  });
});

module.exports = campaignsDetail;


