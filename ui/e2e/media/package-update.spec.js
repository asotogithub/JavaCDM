'use strict';

var packageUpdate = (function (campaignName, bootstrapData) {

  describe('Update Package', function() {
    var page = require('../page-object/media.po'),
        com = require('../page-object/utilities.po'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        data = require('../utilities/mediaData'),
        util = require('../utilities/util'),
        packageName = data.placementNoPkg.name,
        placementName = bootstrapData.placements[1],
        rateType = 'CPA',
        updateData = {
          packageName: 'Package 1 Updated Data 123!@$',
          site: 'Protractor Site',
          section: 'Home',
          size: '350x250',
          adSpend: '2000.00',
          rate: '0.05',
          rateType: 'CPC',
          startDate: '11/11/2017',
          endDate: '11/11/2025',
          record2StartDate: '11/12/2025',
          record2EndDate: '12/13/2025',
          inventory: '40000',
          margin: '50.00'
        },
        assocPlacement = {
          name: 'New Associated Placement',
          site: 'Protractor Site',
          section: 'Home',
          size: '350x250',
        };

    it('should create a new package', function() {
        navigate.newPlacement(campaignName , bootstrapData.io);
        page.mediaPackageField.last().sendKeys(data.packageCost.packageName, rateType);
        page.placementNameField.last().sendKeys(data.packageCost.name, rateType);
        page.siteAssocDropDown.last().click();
        browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
        page.siteAssocDropDown.last().element(by.cssContainingText('li', data.packageCost.site)).click();
        browser.actions().mouseMove(page.sectionDropDown.last()).perform();
        page.sectionDropDown.last().click();
        page.sectionDropDown.last().element(by.cssContainingText('li', data.packageCost.section)).click();
        browser.actions().mouseMove(page.sizeDropDown.last()).perform();
        page.sizeDropDown.last().click();
        page.sizeDropDown.last().element(by.cssContainingText('li', data.packageCost.size)).click();
        expect(page.mediaPackageField.last().getAttribute('value')).toEqual(data.packageCost.packageName + rateType);
        expect(page.placementNameField.last().getAttribute('value')).toEqual(data.packageCost.name + rateType);
        expect(page.siteAssocDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.site);
        expect(page.sectionDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.section);
        expect(page.sizeDropDown.last().element(by.css('span')).getText()).toEqual(data.packageCost.size);
        page.placementAddNextBtn.click();
        util.click(page.saveBtn);
    });

    it('should navigate to placement summary page', function() {
      navigate.packageSummary(campaignName, bootstrapData.io, data.packageCost.packageName);
      expect(browser.getLocationAbsUrl()).toContain('/edit?from=placement-list');
    });

    it('should have save button disabled until an update is made', function() {
      expect(com.saveBtn.isEnabled()).toBe(false);
      page.summaryPackageName.clear();
      page.summaryPackageName.sendKeys(updateData.packageName);
      expect(page.summaryPackageName.getAttribute('value')).toContain(updateData.packageName);
      expect(com.saveBtn.isEnabled()).toBe(true);
    });

    it('should update package cost', function() {
      //update first cost record
      page.summaryMargin.get(0).clear();
      page.summaryMargin.get(0).sendKeys(updateData.margin);
      page.summaryAdSpend.get(0).clear();
      page.summaryAdSpend.get(0).sendKeys(updateData.adSpend);
      page.summaryRate.get(0).clear();
      page.summaryRate.get(0).sendKeys(updateData.rate);
      page.summaryRateType.get(0).click();
      page.summaryRateType.get(0).element(by.cssContainingText('#rateTypeSelect option', updateData.rateType)).click();
      page.summaryStartDate.get(0).clear();
      page.summaryStartDate.get(0).sendKeys(updateData.startDate);
      page.summaryEndDate.get(0).clear();
      page.summaryEndDate.get(0).sendKeys(updateData.endDate);
      //update start date of second cost record 
      page.summaryStartDate.get(1).clear();
      page.summaryStartDate.get(1).sendKeys(updateData.record2StartDate);
      page.summaryEndDate.get(1).clear(); 
      page.summaryEndDate.get(1).sendKeys(updateData.record2EndDate);
      page.summaryEndDate.get(1).sendKeys(protractor.Key.TAB);
      //verify values
      browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
      expect(page.summaryMargin.get(0).getAttribute('value')).toEqual(updateData.margin);
      expect(page.summaryAdSpend.get(0).getAttribute('value')).toEqual(updateData.adSpend);
      expect(page.summaryInventory.get(0).getText()).toEqual(commaSeparateNumber(updateData.inventory));
      expect(page.summaryRate.get(0).getAttribute('value')).toEqual(updateData.rate);
      expect(page.summaryRateType.get(0).getAttribute('value')).toEqual(updateData.rateType);
      expect(page.summaryStartDate.get(0).getAttribute('value')).toEqual(updateData.startDate);
      expect(page.summaryEndDate.get(0).getAttribute('value')).toEqual(updateData.endDate);
      expect(page.summaryStartDate.get(1).getAttribute('value')).toEqual(updateData.record2StartDate);
      expect(page.summaryEndDate.get(1).getAttribute('value')).toEqual(updateData.record2EndDate);
      expect(page.summaryMargin.get(2).isDisplayed()).toBe(true);
    });

    it('should save updates', function() {
      browser.actions().mouseMove(com.saveBtn).perform();
      com.saveBtn.click();
      page.packageRowByName(updateData.packageName).click();
      nav.editIcon.click();
      expect(page.summaryPackageName.getAttribute('value')).toContain(updateData.packageName);
      expect(page.summaryMargin.get(0).getAttribute('value')).toEqual(updateData.margin);
      expect(page.summaryAdSpend.get(0).getAttribute('value')).toEqual(updateData.adSpend);
      expect(page.summaryInventory.get(0).getText()).toEqual(commaSeparateNumber(updateData.inventory));
      expect(page.summaryRate.get(0).getAttribute('value')).toEqual(updateData.rate);
      expect(page.summaryRateType.get(0).getAttribute('value')).toEqual(updateData.rateType);
      expect(page.summaryStartDate.get(0).getAttribute('value')).toEqual(updateData.startDate);
      expect(page.summaryEndDate.get(0).getAttribute('value')).toEqual(updateData.endDate);
      expect(page.summaryStartDate.get(1).getAttribute('value')).toEqual(updateData.record2StartDate);
      expect(page.summaryEndDate.get(1).getAttribute('value')).toEqual(updateData.record2EndDate);
      expect(page.summaryMargin.get(2).isDisplayed()).toBe(true);
    });

    it('should add new associated placement', function() {
      page.assocPlcmntsAddBtn.click();
      expect(page.addPlacements.isSelected()).toBe(true);
      page.addAssocPlcmntNext.click();
      page.addAssocPlacementName.sendKeys(assocPlacement.name);
      page.addAssocSite.click();
      page.addAssocSite.element(by.cssContainingText('li', assocPlacement.site)).click();
      browser.actions().mouseMove(page.addAssocSection).perform();
      page.addAssocSection.click();
      page.addAssocSection.element(by.cssContainingText('li', assocPlacement.section)).click();
      page.addAssocSize.click();
      page.addAssocSize.element(by.cssContainingText('li', assocPlacement.size)).click();
      page.addAssocToList.click();
      page.addAssocSave.click();
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Site').getText()).toEqual(assocPlacement.site);
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Section').getText()).toEqual(assocPlacement.section);
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Size').getText()).toEqual(assocPlacement.size);
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Status').getText()).toEqual('Planning');
    });

    it('should navigate to add existing placement as an associated placement', function() {
      browser.actions().mouseMove(page.assocPlcmntsAddBtn).perform();
      util.click(page.assocPlcmntsAddBtn);
      util.click(page.fromExistingPlacements);
      expect(page.fromExistingPlacements.isSelected()).toBe(true);
      page.addAssocPlcmntNext.click();
     });

    it('should add all and remove all using add all/remove all buttons', function() {
      expect(page.addAssocInputGridRows.count()).toBeGreaterThan(0);
      page.addAssocAddAll.click();
      expect(page.addAssocOuputGridRows.count()).toBeGreaterThan(0);
      page.addAssocRemoveAll.click();
      expect(page.addAssocOuputGridRows.count()).toBe(0);
    });

    it('should associate an existing placement as an associated placement', function() {
      var assocCount = page.addAssocCurrentGridRows.count(); 
      com.searchInput.sendKeys(placementName);
      util.click(page.addAssocPlacementRowByName(placementName));
      page.addAssocAdd.click();
      expect(page.addAssocOuputGridRows.count()).toBe(1);
      page.addToAssociatedList.click();
      page.warningOkBtn.click();
      browser.executeScript('window.scrollTo(0,document.body.scrollHeight)'); 
      expect(page.addAssocCurrentGridRows.count()).toBeGreaterThan(assocCount);
      page.addAssocSave.click();
      expect(page.editPackageCurrentPlacementRowByName(placementName).isDisplayed()).toBe(true);
      navigate.placementGrid(campaignName, bootstrapData.io);
      expect(browser.getLocationAbsUrl()).toContain('/placement/list');
      expect(page.getAssocPlcmntGridRowData(placementName, 'Ad Spend').getText()).toEqual('$666.67');
      expect(page.getAssocPlcmntGridRowData(placementName, 'Inventory').getText()).toEqual(commaSeparateNumber('13334'));
      expect(page.getAssocPlcmntGridRowData(placementName, 'Rate').getText()).toEqual('$0.05');
      expect(page.getAssocPlcmntGridRowData(placementName, 'Media Package').getText()).toEqual(updateData.packageName);
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Ad Spend').getText()).toEqual('$666.67');
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Inventory').getText()).toEqual(commaSeparateNumber('13334'));
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Rate').getText()).toEqual('$0.05');
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Media Package').getText()).toEqual(updateData.packageName);
    });

    it('should remove a placement association', function() {
      navigate.placementSummary(campaignName, bootstrapData.io, placementName);
      page.removeAssocPlcmnt(placementName).click(); 
      page.warningOkBtn.click();
      browser.actions().mouseMove(page.editPackageSaveBtn).perform();
      page.editPackageSaveBtn.click();
      expect(page.getAssocPlcmntGridRowData(placementName, 'Media Package').getText()).toEqual('');
      expect(page.getAssocPlcmntGridRowData(placementName, 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber(updateData.adSpend));
      expect(page.getAssocPlcmntGridRowData(placementName, 'Inventory').getText()).toEqual(commaSeparateNumber(updateData.inventory));
      expect(page.getAssocPlcmntGridRowData(placementName, 'Rate').getText()).toEqual('$' + commaSeparateNumber(updateData.rate));
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Ad Spend').getText()).toEqual('$1,000.00');
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Inventory').getText()).toEqual(commaSeparateNumber('20000'));
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Rate').getText()).toEqual('$0.05');
      expect(page.getAssocPlcmntGridRowData(assocPlacement.name, 'Media Package').getText()).toEqual(updateData.packageName);
    });

    function commaSeparateNumber(val){
      while (/(\d+)(\d{3})/.test(val.toString())){
        val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
      }
      return val;
    }

  });

});

module.exports = packageUpdate;


