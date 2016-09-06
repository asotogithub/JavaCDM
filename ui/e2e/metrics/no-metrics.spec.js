'use strict';

var noMetrics = (function () {

  describe('Campaign with no metrics', function() {
    var page = require('../page-object/metrics.po'),
        nav = require('../page-object/navigation.po');

    it('should not display metrics grid', function() {
      nav.campaignsItem.click();
      expect(page.metricsGrid.isPresent()).toBe(false);
    });

    it('should not display metrics chart', function() {
      nav.campaignsItem.click();
      expect(page.campaignsChart.isPresent()).toBe(false);
      expect(page.chartMenu.isPresent()).toBe(false);
    });

  });
});

module.exports = noMetrics;


