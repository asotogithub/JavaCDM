'use strict';

var mediaGrid = (function (campaignNameMedia, bootstrapData) {

  describe('Media Grid', function() {
    var CONSTANTS = require('../utilities/constants'),
        page = require('../page-object/media.po'),
        path = require('path'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        utilPo = require('../page-object/utilities.po'),
        util = require('../utilities/util');

    it('should navigate to media grid', function() {
      navigate.campaignDetails(campaignNameMedia);
      nav.sideNavCollapse.click();
      nav.mediaTab.click();
      expect(browser.getLocationAbsUrl()).toContain('/io/list');
      expect(page.mediaGrid.isDisplayed()).toBe(true);
      page.dataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
    });

    it('should be sorted alphabetically by Status', function() {
      var unsorted = page.textToSort(0);
      var sorted = page.sortedText(0);
      expect(unsorted).toEqual(sorted);

      //Sort Name column by descending order
      utilPo.headerByName('Status', '.te-tree-grid').click();
    });

    describe('Column headers', function() {
      page.columnNames.forEach(function(header, i) {
        if (header !== 'check') {
            it('should sort ' + header + ' in ascending order', function () {
                // Click on header to sort values in ascending order
                utilPo.headerByName(header, '.te-tree-grid').click();
                var unsorted = page.textToSort(i);
                var sorted = page.sortedText(i);
                expect(unsorted).toEqual(sorted);
            });

            it('should sort ' + header + ' in descending order', function () {
                // Click on header to sort values in descending order
                utilPo.headerByName(header, '.te-tree-grid').click();
                var unsorted = page.textToSort(i);
                var sorted = page.sortedText(i).then(function (sortedText) {
                    return sortedText.reverse();
                });
                expect(unsorted).toEqual(unsorted);
            });
        }});
    });

      describe('Bulk options', function() {
          var config,
              browserName;

          browser.getProcessedConfig().then(function (configuration) {
              config = configuration;
          });

          browser.getCapabilities().then(function (capabilities) {
              browserName = capabilities.caps_.browserName;
          });

          it('should have download link displayed in bulk button list', function() {
              expect(page.ioBulkDropDownButton.isDisplayed()).toBe(true);
              page.ioBulkDropDownButton.click();
              expect(page.ioDownloadTemplateLink.isDisplayed()).toBe(true);
          });

          it('should download media import template', function() {
              var fs = require('fs'),
                  fileName;

              page.ioDownloadTemplateLink.click();

              if (browserName === 'chrome') {
                  if (config.seleniumAddress === config.env.browserstack.seleniumAddress) {
                      expect(page.ioDownloadTemplateLink.isDisplayed()).toBe(false);
                  }
                  else {
                      fileName = path.resolve(config.capabilities.chromeOptions.prefs.download.default_directory,
                                              CONSTANTS.MEDIA.IMPORT_TEMPLATE_NAME);

                      browser.driver.wait(function() {
                          try {
                              fs.statSync(fileName);
                              return true;
                          }
                          catch (error){
                              return false;
                          }
                      }).then(function() {
                          expect(fs.existsSync(fileName)).toBeTruthy();
                      });
                  }
              }
          });
      });

    it('should expand IOs', function() {
      page.dataRows.then(function(rows){
        var rowCount = rows.length;
        rows.forEach(function(){
          util.click(page.ioExpand.get(0));
        });
        page.dataRows.then(function(endRowCount){
          expect(endRowCount.length).toBeGreaterThan(rowCount);
        })
      });
    });

    it('should allow searching on IO Name and Placement', function() {
      page.searchInput.sendKeys(bootstrapData.io);
      page.dataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
      page.searchInput.clear();
      page.searchInput.sendKeys(bootstrapData.placements[0]);
      page.dataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
      page.searchClear.click();
      page.dataRows.then(function(rows){
        expect(rows.length).toBeGreaterThan(0);
      });
    });

    it('should display legend with expected information after perfoming a search',function() {
        expect(page.legendMedia.isDisplayed()).toBe(true);
        page.searchInputMedia.sendKeys('IO');
        page.dataRows.then(function(rows){
            expect(page.legendMedia.getText()).toContain(rows.length-1);
        });
        nav.sideNavCollapse.click();
        expect(nav.campaignsItem.isDisplayed()).toBe(true);
    });
  });
});

module.exports = mediaGrid;


