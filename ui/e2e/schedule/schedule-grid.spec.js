'use strict';

var scheduleGrid = (function (campaignName) {

  describe('Schedule Grid', function() {
    var CONSTANTS = require('../utilities/constants'),
        page = require('../page-object/schedule.po'),
        navigate = require('../utilities/navigation.spec'),
        util = require('../page-object/utilities.po'),
        creativeName = 'image-1-350x250',
        placementName = 'New Placement for Protractor',
        scheduleDetail = {
          scheduleName: 'Protractor Site'
        },
        campaignName = 'Protractor Test Campaign for Schedule';

      it('should navigate to schedule grid', function() {
          navigate.scheduleGrid(campaignName);
          expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
      });

    describe('Column headers', function() {
      page.sortableColumns.forEach(function(header, i) {
        it('should sort ' + header + ' in ascending order', function() {
          // Click on header to sort values in ascending order
          page.headerByNameScheduleGrid(header).click();
          var unsorted = page.textToSort(i);
          var sorted = page.sortedText(i);
          expect(unsorted).toEqual(sorted);
        });

        it('should sort ' + header + ' in descending order', function() {
          // Click on header to sort values in descending order
          page.headerByNameScheduleGrid(header).click();
          var unsorted = page.textToSort(i);
          var sorted = page.sortedText(i).then(function(sortedText) {
              return sortedText.reverse();
            });
          expect(unsorted).toEqual(unsorted);
        });
      });
    });

    it('should allow searching', function() {
      page.searchInputSchedule.clear();
      page.searchInputSchedule.sendKeys(placementName);
      page.searchButtonSchedule.click();
      page.scheduleDataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
      page.searchInputSchedule.clear();
      page.searchInputSchedule.sendKeys(CONSTANTS.defaultGroupName);
      page.searchButtonSchedule.click();
      page.scheduleDataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
      page.searchInputSchedule.clear();
      page.searchInputSchedule.sendKeys(creativeName);
      page.searchButtonSchedule.click();
      page.scheduleDataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
      page.searchInputSchedule.clear();
      page.searchInputSchedule.sendKeys(placementName);
      page.searchButtonSchedule.click();
      page.scheduleDataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
      page.searchInputSchedule.click();
      page.scheduleDataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
    });

    it('should search and expand rows for ' + scheduleDetail.scheduleName, function() {
      page.searchInputSchedule.clear();
      page.searchInputSchedule.sendKeys(scheduleDetail.scheduleName);
      page.searchButtonSchedule.click();
      expect(page.scheduleDataRows.count()).toEqual(1);
    });
  });
});

module.exports = scheduleGrid;
