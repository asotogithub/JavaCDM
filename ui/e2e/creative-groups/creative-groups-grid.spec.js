'use strict';

var creativeGroupsGrid = (function () {

  describe('Grid', function() {
    var page = require('../page-object/creative-groups.po'),
        login = require('../page-object/login.po'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        camp = require('../page-object/campaigns.po'),
        user = require('../utilities/users.spec'),
        util = require('../utilities/util'),
        totalCreativeGroups;

    it('should navigate to grid with correct columns and rows', function() {
      navigate.firstCampaign();
      nav.creativeGroupTab.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.gridUrl);
      page.columnHeader.map(function(item) {
        return item.getText();
        }).then(function(labels) {
          expect(labels).toEqual(page.columnNames);
        });
      page.dataRows.then(function(rows){
          totalCreativeGroups = rows.length;
        expect(rows.length).toBeGreaterThan(0);
      });
    });

    it('should display Default system creative group on the first row with default values', function() {
      var rowNumber = 0;
      expect(page.getDataByRowColumn(rowNumber, 'Name').getText()).toBe('Default');
      expect(page.getDataByRowColumn(rowNumber, 'Priority').getText()).toBe('');
      expect(page.getDataByRowColumn(rowNumber, 'CookieTargeting').element(by.css('i')).getAttribute('class')).toContain(page.xClass);
      expect(page.getDataByRowColumn(rowNumber, 'GeoTargeting').element(by.css('i')).getAttribute('class')).toContain(page.xClass);
      expect(page.getDataByRowColumn(rowNumber, 'DayPartTargeting').element(by.css('i')).getAttribute('class')).toContain(page.xClass);
      expect(page.getDataByRowColumn(rowNumber, 'GroupWeight').element(by.css('i')).getAttribute('class')).toContain(page.xClass);
      expect(page.getDataByRowColumn(rowNumber, 'Default').element(by.css('i')).getAttribute('class')).toContain(page.checkMarkClass);
    });

    it('should display legend Table',function() {
      expect(page.legendTable.isDisplayed()).toBe(true);
    });

    it('should display legend total Creative Groups',function() {
      page.dataRows.then(function(rows){
          expect(page.legendTable.getText()).toContain(rows.length);
      });
    });

    it('should be sorted by alphabetically by Creative Group name', function() {
      var unsorted = page.textToSortWithoutDefault('Name');
      var sorted = page.sortedText('Name');
      expect(unsorted).toEqual(sorted);

      //Sort Name column by descending order
      page.headerByName('Name').click();
    });

    describe('Column headers', function() {
      page.columnNames.forEach(function(header) {
        it('should sort ' +header+ ' in ascending order', function() {
          // Click on header to sort values in ascending order
          util.click(page.headerByName(header));
          var unsorted = page.textToSortWithoutDefault(header);
          var sorted = page.sortedText(header);
          expect(unsorted).toEqual(sorted);
        });

        it('should sort ' +header+ ' in descending order', function() {
          // Click on header to sort values in descending order
          util.click(page.headerByName(header));
          var unsorted = page.textToSortWithoutDefault(header);
          var sorted = page.sortedText(header).then(function(sortedText) {
              return sortedText.reverse();
            });
          expect(unsorted).toEqual(sorted);
        });
      });
    });

    it('should always display Default system creative group on the first row', function() {
      page.dataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(1);
      });
      var rowNumber = 0;
      expect(page.getDataByRowColumn(rowNumber, 'Name').getText()).toBe('Default');
      page.headerByName('Name').click();
      expect(page.getDataByRowColumn(rowNumber, 'Name').getText()).toBe('Default');
    });

    it('should display legend with expected information after perfoming a search',function() {
      page.searchInputCreativeGroups.sendKeys('no');
      page.dataRows.then(function(rows){
          expect(page.legendTable.getText()).toContain(rows.length);
          expect(page.legendTable.getText()).toContain(totalCreativeGroups);
      });
    });
  });
});

module.exports = creativeGroupsGrid;


