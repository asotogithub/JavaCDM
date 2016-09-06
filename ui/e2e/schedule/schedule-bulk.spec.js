'use strict';

var scheduleGrid = (function (campaignName) {

  describe('Export', function() {
    var browserName,
        config,
        fileInvalidImport = 'invalid-import.xlsx',
        navigate = require('../utilities/navigation.spec'),
        path = require('path'),
        page = require('../page-object/schedule.po');

    it('should get the environment configurations', function() {
      browser.getCapabilities().then(function (capabilities) {
        browserName = capabilities.caps_.browserName;
      });
      browser.getProcessedConfig().then(function (configuration) {
        config = configuration;
      });
    });

    it('should download *.xlsx file to test import', function () {
      //This test only runs on Chrome browser, because the selenium Chrome driver is the only one that supports
      //configuration to avoid the download prompt showing up and blocking the browser
      if (browserName === 'chrome') {
        //Injecting an element to click it via protractor and download the file to be used on the tests.
        browser.driver.executeScript('var link = document.createElement("a");' +
          'var text = document.createTextNode(".");' +
          'link.appendChild(text);' +
          'link.setAttribute("id", "downloadInvalidFile");' +
          'link.setAttribute("href", "/templates/invalid-import.xlsx");' +
          'document.getElementsByTagName("h3")[0].appendChild(link);').then(
          function () {
            page.downloadInvalidFile.click();
          }
        );
      }
    });

    it('should navigate to schedule grid', function() {
      navigate.scheduleGrid(campaignName);
      expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
    });

    it('should open/close the bulk dropdown', function() {
      expect(page.bulkDropdownList.isDisplayed()).toBeFalsy();
      page.bulkDropdown.click();
      expect(page.bulkDropdownList.isDisplayed()).toBeTruthy();
      page.bulkDropdown.click();
      expect(page.bulkDropdownList.isDisplayed()).toBeFalsy();
    });

    it('should download the exported *.xlsx file', function() {
      expect(page.downloadSuccessful.evaluate('vm.downloadSuccessful')).toBeFalsy();
      if (browserName === 'chrome') {
        page.bulkDropdown.click();
        page.exportAll.click().then(function(){
          expect(page.downloadSuccessful.evaluate('vmList.downloadSuccessful')).toBeTruthy();
        });
      }
    });

    it('should show a popup that contains the errors when importing',function (){
      if (browserName === 'chrome') {
        page.bulkDropdown.click();
        page.importAll.click();
        expect(page.processImportBtn.isEnabled()).toBe(false);
        if (config.seleniumAddress === config.env.browserstack.seleniumAddress) {
          page.upload.sendKeys(config.env.browserstack.downloadDirectory + fileInvalidImport);
        }
        else {
          var filename = path.resolve(path.resolve(__dirname, fileInvalidImport));
          page.upload.sendKeys(filename);
        }
        expect(page.processImportBtn.isEnabled()).toBe(true);
        page.processImportBtn.click();
        expect(page.issuesRows.count()).toEqual(2);
        expect(page.rowsErrors.getText()).toEqual('2');
        expect(page.rowsUpdate.getText()).toEqual('0');
        expect(page.totalRowsImport.getText()).toEqual('2');
      }
    });

    it('should export an *.xlsx file containing the rows with issues',function (){
      expect(page.exportIssuesSuccessful.evaluate('vm.exportIssuesSuccessful')).toBeFalsy();
      if (browserName === 'chrome') {
        page.exportIssuesBtn.click().then(function(){
          expect(page.exportIssuesSuccessful.evaluate('vm.exportIssuesSuccessful')).toBeTruthy();
        });
        page.cancelImportErrorsBtn.click();
      }
    });
  });
});

module.exports = scheduleGrid;


