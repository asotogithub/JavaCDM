'use strict';

var metricsCampaignsGrid = (function () {

  describe('Metrics on Campaign Grid', function() {
    var page = require('../page-object/metrics.po'),
        nav = require('../page-object/navigation.po');

    it('should display metrics grid with correct metrics', function() {
      nav.campaignsItem.click();
      expect(page.metricsGrid.isDisplayed()).toBe(true);
      page.allMetricsItems.map(function(item) {
        return item.getText();
        }).then(function(labels) {
          expect(labels).toEqual(page.campaignMetrics);
        });
    });

    it('should display metrics chart', function() {
      nav.campaignsItem.click();
      expect(page.campaignsChart.isDisplayed()).toBe(true);
      expect(page.chartMenu.isDisplayed()).toBe(true);
    });

  });
});

module.exports = metricsCampaignsGrid;


