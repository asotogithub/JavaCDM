'use strict';

var sideNav = (function () {

  describe('Side Navigation', function() {
    var page = require('../page-object/navigation.po'),
        campaign = require('../page-object/campaigns.po');

    it('should have Campaigns item selected', function() {
      page.campaignsItem.click();
      expect(browser.getLocationAbsUrl()).toContain('/campaigns');
      expect(campaign.campaignsHeader.isDisplayed()).toBe(true);
      expect(page.campaignsSelector.getText()).toBe('Campaigns');
    });

    it('should reload Campaigns grid when page is refreshed', function() {
      browser.refresh();
      expect(browser.getLocationAbsUrl()).toContain('/campaigns');
      expect(campaign.campaignsHeader.isDisplayed()).toBe(true);
      expect(page.campaignsSelector.getText()).toBe('Campaigns');
    });

    it('should reload Campaigns grid when Campaigns item is clicked', function() {
      page.campaignsItem.click();
      expect(browser.getLocationAbsUrl()).toContain('/campaigns');
      expect(campaign.campaignsHeader.isDisplayed()).toBe(true);
      expect(page.campaignsSelector.getText()).toBe('Campaigns');
    });

    it('should click away from Campaigns page and back again', function() {
      page.siteMeasurementItem.click();
      expect(browser.getLocationAbsUrl()).toContain('/site-measurements');
      page.campaignsItem.click();
      expect(browser.getLocationAbsUrl()).toContain('/campaigns');
      expect(campaign.campaignsHeader.isDisplayed()).toBe(true);
      expect(page.campaignsSelector.getText()).toBe('Campaigns');
    });

  });
});

module.exports = sideNav;


