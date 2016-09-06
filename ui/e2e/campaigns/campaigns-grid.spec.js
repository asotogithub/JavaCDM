'use strict';

var campaignsPage = (function () {

  var page = require('../page-object/campaigns.po'),
      gridValidation = require('../utilities/grid-validation.spec'),
      gridColumnsSortValidation = require('../utilities/grid-validation-columns-sort.spec'),
      nav = require('../page-object/navigation.po');

  describe('Campaigns Grid', function() {

    it('should navigate to page and display Campaigns', function() {
      nav.campaignsItem.click();
      expect(browser.getLocationAbsUrl()).toContain('/campaigns');
      page.dataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
    });

    gridValidation('Name', page.columnNames);
    gridColumnsSortValidation(page.columnNames);
  });

  describe('Filtering options', function() {
    it('should filter by Advertisers', function() {
      nav.campaignsItem.click();
      expect(browser.getLocationAbsUrl()).toContain('/campaigns');
      page.filterDropdown(0).click();
      page.filterDropdownOption(0, 0).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns - 1);
      page.filterDropdownOption(0, 0).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns);
      page.filterDropdownOption(0, 1).click();
      expect(page.dataRows.count()).toEqual(1);
      page.filterClearSelected(0).click();
      expect(page.dataRows.count()).toEqual(0);
      page.filterSelectAll(0).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns);
      page.filterDropdown(0).click();
    });

    it('should filter by Brand', function() {
      page.filterDropdown(1).click();
      page.filterDropdownOption(1, 0).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns - 1);
      page.filterDropdownOption(1, 0).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns);
      page.filterDropdownOption(1, 1).click();
      expect(page.dataRows.count()).toEqual(1);
      page.filterClearSelected(1).click();
      expect(page.dataRows.count()).toEqual(0);
      page.filterSelectAll(1).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns);
      page.filterDropdown(1).click();
    });

    it('should filter by Status', function() {
      page.filterDropdown(2).click();
      page.filterDropdownOption(2, 0).click();
      expect(page.dataRows.count()).toEqual(1);
      page.filterDropdownOption(2, 0).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns);
      page.filterDropdownOption(2, 1).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns - 1);
      page.filterClearSelected(2).click();
      expect(page.dataRows.count()).toEqual(0);
      page.filterSelectAll(2).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns);
      page.filterDropdown(2).click();
    });

    it('should keep my selected filters', function() {
      page.filterDropdown(0).click();
      page.filterDropdownOption(0, 1).click();
      expect(page.dataRows.count()).toEqual(1);
      page.filterDropdown(0).click();
      page.campaignsRow.get(0).click();
      page.listOfCampaignsBtn.click();
      expect(page.dataRows.count()).toEqual(1);
      page.filterDropdown(0).click();
      page.filterSelectAll(0).click();
      expect(page.dataRows.count()).toEqual(global.countCampaigns);
      page.filterDropdown(0).click();
    });
  });
});

module.exports = campaignsPage;
