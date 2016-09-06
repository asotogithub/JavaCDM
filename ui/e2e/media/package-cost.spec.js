'use strict';

var packageCost = (function (campaignName, bootstrapData) {

  describe('Package Cost', function() {
    var page = require('../page-object/media.po'),
        navigate = require('../utilities/navigation.spec'),
        data = require('../utilities/mediaData'),
        util = require('../utilities/util'),
        rateType = ['CPA', 'CPC', 'CPL', 'CPM', 'FLT'],
        cpmInventory = Math.ceil((data.packageCost.adSpend/data.packageCost.rate)*1000).toString(),
        cpcInventory = Math.ceil(data.packageCost.adSpend/data.packageCost.rate).toString(),
        cpaInventory = Math.ceil(data.packageCost.adSpend/data.packageCost.rate).toString(),
        fltInventory = '1',
        cplInventory = Math.ceil(data.packageCost.adSpend/data.packageCost.rate).toString();

    it('should fill out rows to add packages for each rate type', function() {
        navigate.newPlacement(campaignName , bootstrapData.io);
        for (var i = 0; i < rateType.length; ++i) {
          page.mediaPackageField.last().sendKeys(data.packageCost.packageName, rateType[i]);
          page.placementNameField.last().sendKeys(data.packageCost.name, rateType[i]);
          page.siteAssocDropDown.last().click();
          browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
          page.siteAssocDropDown.last().element(by.cssContainingText('li', data.packageCost.site)).click();
          browser.actions().mouseMove(page.sectionDropDown.last()).perform();
          page.sectionDropDown.last().click();
          page.sectionDropDown.last().element(by.cssContainingText('li', data.packageCost.section)).click();
          browser.actions().mouseMove(page.sizeDropDown.last()).perform();
          page.sizeDropDown.last().click();
          page.sizeDropDown.last().element(by.cssContainingText('li', data.packageCost.size)).click();
          expect(page.mediaPackageField.last().getAttribute('value')).toEqual(data.packageCost.packageName + rateType[i]);
          expect(page.placementNameField.last().getAttribute('value')).toEqual(data.packageCost.name + rateType[i]);
          expect(page.siteAssocDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.site);
          expect(page.sectionDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.section);
          expect(page.sizeDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.size);
          page.addPlcmntRow.click();
        };
    });

    it('should fill out rows to add second placement to each rate type package', function() {
        for (var i = 0; i < rateType.length; ++i) {
          page.mediaPackageField.last().sendKeys(data.packageCost.packageName, rateType[i]);
          page.placementNameField.last().sendKeys(data.packageCost.name, rateType[i] + '2');
          page.siteAssocDropDown.last().click();
          browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
          page.siteAssocDropDown.last().element(by.cssContainingText('li', data.packageCost.site)).click();
          browser.actions().mouseMove(page.sectionDropDown.last()).perform();
          page.sectionDropDown.last().click();
          page.sectionDropDown.last().element(by.cssContainingText('li', data.packageCost.section)).click();
          browser.actions().mouseMove(page.sizeDropDown.last()).perform();
          page.sizeDropDown.last().click();
          page.sizeDropDown.last().element(by.cssContainingText('li', data.packageCost.size)).click();
          expect(page.mediaPackageField.last().getAttribute('value')).toEqual(data.packageCost.packageName + rateType[i]);
          expect(page.placementNameField.last().getAttribute('value')).toEqual(data.packageCost.name + rateType[i] + '2');
          expect(page.siteAssocDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.site);
          expect(page.sectionDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.section);
          expect(page.sizeDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.size);
          browser.actions().mouseMove(page.addPlcmntRow).perform();
          page.addPlcmntRow.click();
        };
        page.placementAddNextBtn.click();
    });

    it('should enter ad spend and rate values and apply to all', function() {
      page.applyToAdSpend.sendKeys(data.packageCost.adSpend);
      page.applyToRate.sendKeys(data.packageCost.rate);
      browser.actions().mouseMove(page.applyToBtn).perform();
      page.applyToBtn.click();
      for (var i = 0; i < rateType.length; ++i) {
        expect(page.getAdSpendColumn.get(i).getAttribute('value')).toEqual(data.packageCost.adSpend);
        expect(page.getRateColumn.get(i).getAttribute('value')).toEqual(data.packageCost.rate);
      };
    });

    it('should update rate types', function() {
      for (var i = 0; i < rateType.length; ++i) {
        page.getRateTypeColumn.get(i).click();
        page.getRateTypeColumn.get(i).element(by.cssContainingText('#rateTypeSelect option', rateType[i])).click();
        expect(page.getRateTypeColumn.get(i).getAttribute('value')).toEqual(rateType[i]);
      };
    });

    it('should calculate inventory based on rate type', function() {
      expect(page.getInventoryColumn.get(0).getText()).toEqual(commaSeparateNumber(cpaInventory));
      expect(page.getInventoryColumn.get(1).getText()).toEqual(commaSeparateNumber(cpcInventory));
      expect(page.getInventoryColumn.get(2).getText()).toEqual(commaSeparateNumber(cplInventory));
      expect(page.getInventoryColumn.get(3).getText()).toEqual(commaSeparateNumber(cpmInventory));
      expect(page.getInventoryColumn.get(4).getText()).toEqual(commaSeparateNumber(fltInventory));
    });

    it('should save package data', function() {
      util.click(page.saveBtn);
      navigate.placementGrid(campaignName, bootstrapData.io);
      expect(browser.getLocationAbsUrl()).toContain('/placement/list');
      var cpmPackageInventory = Math.ceil(((data.packageCost.adSpend/data.packageCost.rate)*1000)/2).toString(),
          cpcPackageInventory = Math.ceil((data.packageCost.adSpend/data.packageCost.rate)/2).toString(),
          cpaPackageInventory = Math.ceil((data.packageCost.adSpend/data.packageCost.rate)/2).toString(),
          fltPackageInventory = '1',
          cplPackageInventory = Math.ceil((data.packageCost.adSpend/data.packageCost.rate)/2).toString();
      page.placementSearchInput.sendKeys(data.packageCost.name + 'CPM' + 2);
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPM' + 2, 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber((data.packageCost.adSpend/2).toFixed(2)));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPM' + 2, 'Inventory').getText()).toEqual(commaSeparateNumber(cpmPackageInventory));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPM' + 2, 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.packageCost.rate));
      page.placementSearchClear.click();
      page.placementSearchInput.sendKeys(data.packageCost.name + 'CPC' + 2);
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPC' + 2, 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber((data.packageCost.adSpend/2).toFixed(2)));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPC' + 2, 'Inventory').getText()).toEqual(commaSeparateNumber(cpcPackageInventory));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPC' + 2, 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.packageCost.rate));
      page.placementSearchClear.click();
      page.placementSearchInput.sendKeys(data.packageCost.name + 'CPA' + 2);
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPA' + 2, 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber((data.packageCost.adSpend/2).toFixed(2)));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPA' + 2, 'Inventory').getText()).toEqual(commaSeparateNumber((cpaPackageInventory)));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPA' + 2, 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.packageCost.rate));
      page.placementSearchClear.click();
      page.placementSearchInput.sendKeys(data.packageCost.name + 'FLT' + 2);
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'FLT' + 2, 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber((data.packageCost.adSpend/2).toFixed(2)));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'FLT' + 2, 'Inventory').getText()).toEqual(commaSeparateNumber(fltPackageInventory));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'FLT' + 2, 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.packageCost.rate));
      page.placementSearchClear.click();
      page.placementSearchInput.sendKeys(data.packageCost.name + 'CPL' + 2);
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPL' + 2, 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber((data.packageCost.adSpend/2).toFixed(2)));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPL' + 2, 'Inventory').getText()).toEqual(commaSeparateNumber(cplPackageInventory));
      expect(page.getAssocPlcmntGridRowData(data.packageCost.name + 'CPL' + 2, 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.packageCost.rate));
    });

    function commaSeparateNumber(val){
      while (/(\d+)(\d{3})/.test(val.toString())){
        val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
      }
      return val;
    }

  });

});

module.exports = packageCost;


