'use strict';

var placementCost = (function (campaignName, bootstrapData) {

  describe('Placement Cost', function() {
    var page = require('../page-object/media.po'),
        navigate = require('../utilities/navigation.spec'),
        data = require('../utilities/mediaData'),
        rateType = ['CPA', 'CPC', 'CPL', 'CPM', 'FLT'],
        placementName = bootstrapData.placements[0],
        cpmInventory = Math.ceil((data.placementCost.adSpend/data.placementCost.rate)*1000).toString(),
        cpcInventory = Math.ceil(data.placementCost.adSpend/data.placementCost.rate).toString(),
        cpaInventory = Math.ceil(data.placementCost.adSpend/data.placementCost.rate).toString(),
        fltInventory = '1',
        cplInventory = Math.ceil(data.placementCost.adSpend/data.placementCost.rate).toString();

    it('should navigate to create placements wizard to add 5 new placements', function() {
        navigate.newPlacement(campaignName , bootstrapData.io);
        for (var i = 0; i < rateType.length; ++i) {
          page.placementNameField.get(i).sendKeys(placementName, rateType[i]);
          page.siteAssocDropDown.get(i).click();
          browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
          page.siteAssocDropDown.get(i).element(by.cssContainingText('li', data.placementCost.site)).click();
          browser.actions().mouseMove(page.sectionDropDown.get(i)).perform();
          page.sectionDropDown.get(i).click();
          page.sectionDropDown.get(i).element(by.cssContainingText('li', data.placementCost.section)).click();
          page.sizeDropDown.get(i).click();
          page.sizeDropDown.get(i).element(by.cssContainingText('li', data.placementCost.size)).click();
          expect(page.placementNameField.get(i).getAttribute('value')).toEqual(placementName + rateType[i]);
          expect(page.siteAssocDropDown.get(i).element(by.css('span')).getText()).toEqual(data.placementCost.site);
          expect(page.sectionDropDown.get(i).element(by.css('span')).getText()).toEqual(data.placementCost.section);
          expect(page.sizeDropDown.get(i).element(by.css('span')).getText()).toEqual(data.placementCost.size);
          page.addPlcmntRow.click();
        }
        page.deletePlacementRow.last().click();
        page.placementAddNextBtn.click();
    });

    it('should enter ad spend and rate values and apply to all', function() {
      page.applyToAdSpend.sendKeys(data.placementCost.adSpend);
      page.applyToRate.sendKeys(data.placementCost.rate);
      page.applyToBtn.click();
      for (var i = 0; i < rateType.length; ++i) {
        expect(page.getAdSpendColumn.get(i).getAttribute('value')).toEqual(data.placementCost.adSpend);
        expect(page.getRateColumn.get(i).getAttribute('value')).toEqual(data.placementCost.rate);
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

    it('should save placement data', function() {
      page.saveBtn.click();
      navigate.placementGrid(campaignName, bootstrapData.io);
      expect(browser.getLocationAbsUrl()).toContain('/placement/list');
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPM', 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.adSpend));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPM', 'Inventory').getText()).toEqual(commaSeparateNumber(cpmInventory));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPM', 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.rate));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPC', 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.adSpend));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPC', 'Inventory').getText()).toEqual(commaSeparateNumber(cpcInventory));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPC', 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.rate));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPA', 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.adSpend));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPA', 'Inventory').getText()).toEqual(commaSeparateNumber(cpaInventory));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPA', 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.rate));
      expect(page.getAssocPlcmntGridRowData(placementName + 'FLT', 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.adSpend));
      expect(page.getAssocPlcmntGridRowData(placementName + 'FLT', 'Inventory').getText()).toEqual(commaSeparateNumber(fltInventory));
      expect(page.getAssocPlcmntGridRowData(placementName + 'FLT', 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.rate));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPL', 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.adSpend));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPL', 'Inventory').getText()).toEqual(commaSeparateNumber(cplInventory));
      expect(page.getAssocPlcmntGridRowData(placementName + 'CPL', 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.placementCost.rate));
    });

    function commaSeparateNumber(val){
      while (/(\d+)(\d{3})/.test(val.toString())){
        val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
      }
      return val;
    }

  });

});

module.exports = placementCost;


