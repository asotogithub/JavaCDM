'use strict';

var forbidden = (function () {

  describe('Forbidden Page', function() {
    var page = require('../page-object/navigation.po'),
        campaign = require('../page-object/campaigns.po');

    it('should navigate to 403 page', function() {
      browser.get('/#/403');
      expect(browser.getLocationAbsUrl()).toContain('/403');
      expect(page.permissionMsg.isDisplayed()).toBe(true);
    });

    it('should redirect to 403 from site-measurements when user doesn\'t have permissions', function() {
      browser.get('/#/site-measurements');
      expect(browser.getLocationAbsUrl()).toContain('/403');
    });

    it('should redirect to 403 when user doesn\'t have permissions to add a campaign', function() {
      browser.get('/#/campaigns/add-campaign');
      expect(browser.getLocationAbsUrl()).toContain('/403');
    });

  });
});

module.exports = forbidden;


