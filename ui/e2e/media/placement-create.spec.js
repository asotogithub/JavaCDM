'use strict';

var PlacementCreate = (function (campaignName, bootstrapData) {

  describe('New Placement', function () {
    var CONSTANTS = require('../utilities/constants'),
        page = require('../page-object/media.po'),
        navigate = require('../utilities/navigation.spec'),
        data = require('../utilities/mediaData'),
        utils = require('../utilities/common.spec'),
        util = require('../utilities/util'),
        today = new Date(),
        dd = today.getDate(),
        mm = today.getMonth() + 1, //January is 0!
        yyyy = today.getFullYear(),
        sectionName = utils.getRandomString(200),
        siteName = utils.getRandomString(200),
        sizeLabel = data.placementWithPkg.size,
        placementName = siteName + ' - ' + sectionName + ' - ' + sizeLabel;

    if (dd < 10) {
      dd = '0' + dd
    }
    if (mm < 10) {
      mm = '0' + mm
    }
    today = mm + '/' + dd + '/' + yyyy;

    it('should navigate to new io page', function() {
      navigate.newPlacement(campaignName , bootstrapData.io);
      expect(browser.getLocationAbsUrl()).toContain('/placement/add');
    });

    it('should have next button disabled until all required fields are populate', function() {
      expect(page.placementAddNextBtn.getAttribute('class')).toContain('disabled');
    });

    it('should display validation messages to placement name',function () {
      page.invalidNames.forEach(function(value, i){
          page.placementNameField.get(0).clear();
          page.placementNameField.get(0).sendKeys(value);
          expect(page.invalidPlacementName.isDisplayed()).toBe(true);
      });
      page.placementNameField.get(0).clear();
    });

    it('should display validation message on Add Placements Tab', function() {
      page.mediaPackageField.get(0).sendKeys(utils.getRandomString(260));
      page.placementNameField.get(0).sendKeys(utils.getRandomString(260));
      expect(page.mediaPackageNameMaxLength.isDisplayed()).toBe(true);
      expect(page.placementNameMaxLength.isDisplayed()).toBe(true);
    });

    it('should not enable Next button when there are validation errors', function() {
      util.click(page.siteAssocDropDown.get(0));
      util.click(page.siteAssocDropDown.get(0).element(by.cssContainingText('li', data.placementWithPkg.site)));
      util.click(page.sectionDropDown.get(0));
      util.click(page.sectionDropDown.get(0).element(by.cssContainingText('li', data.placementWithPkg.section)));
      util.click(page.sizeDropDown.get(0));
      util.click(page.sizeDropDown.get(0).element(by.cssContainingText('li', data.placementWithPkg.size)));
      expect(page.placementAddNextBtn.getAttribute('class')).toContain('disabled');
    });

    it('should create a site and a section with a valid long name', function() {
      page.sectionDropDown.get(0).element(by.css('a.search-choice-close')).click();
      page.addSiteSectionSize.get(0).click();
      expect(page.addSiteDropdown.isDisplayed()).toBe(true);
      expect(page.addSectionDropdown.isDisplayed()).toBe(true);
      page.addSiteDropdown.click();
      page.addSiteInput.sendKeys(siteName);
      page.addSectionDropdown.click();
      expect(page.addSiteSectionSizeButton.isEnabled()).toBe(true);
      page.addSectionDropdown.click();
      page.addSectionInput.sendKeys(sectionName);
      page.addSiteDropdown.click();
      page.addSiteDropdown.click();
      expect(page.addSiteSectionSizeButton.isEnabled()).toBe(true);
      page.addSiteSectionSizeButton.click();
    });

      it('should show validation message Site - Section name',function() {
          browser.actions().mouseMove(page.addSiteSectionSize.get(0)).perform();
          page.addSiteSectionSize.get(0).click();
          expect(page.addSiteDropdown.isDisplayed()).toBe(true);
          page.invalidNames.forEach(function(value, i){
              page.addSiteDropdown.click();
              page.addSiteInput.clear();
              page.addSiteInput.sendKeys(value);
              page.addSizeDropdown.click();
              expect(page.invalidSiteName.isDisplayed()).toBe(true);

          });
          expect(page.addSiteSectionSizeButton.isEnabled()).toBe(false);
          page.invalidNames.forEach(function(value, i){
              page.addSectionDropdown.click();
              page.addSectionInput.clear();
              page.addSectionInput.sendKeys(value);
              page.addSizeDropdown.click();
              expect(page.invalidSectionName.isDisplayed()).toBe(true);
          });
      });

      it('should have save button enabled with specials characters valid Site-Section name',function () {
          expect(page.addSiteSectionSizeButton.isEnabled()).toBe(false);
          page.addSiteDropdown.click();
          page.addSiteInput.clear();
          page.addSiteInput.sendKeys('.~/?%&hello!+*-$#hi@:;{}how[]>are<(you)');
          page.addSizeDropdown.click();
          page.addSectionDropdown.click();
          page.addSectionInput.clear();
          page.addSectionInput.sendKeys('.~/?%&hello!+*-$#hi@:;{}how[]>are<(you)');
          expect(page.addSiteSectionSizeButton.isEnabled()).toBe(true);
          page.addSiteSectionSizeCancel.click();
      });

    it('should truncate auto generated placement name when it is too long', function() {
      page.mediaPackageField.clear();
      page.placementNameField.clear();
      util.click(page.placementAddNextBtn);
      page.addedSectionPlacementName.getAttribute('value').then(function (value) {
        expect(value.length).toEqual(256);
      });
    });

    it('should display validation messages to placement name in cost details',function () {
      page.invalidNames.forEach(function(value, i){
          page.addedSectionPlacementName.clear();
          page.addedSectionPlacementName.sendKeys(value);
          expect(page.addedSectionInvalidPlacementName.isDisplayed()).toBe(true);
      });
    });

    it('should auto generate value if placement name is empty', function () {
      page.addedSectionPlacementName.clear();
      page.addedSectionPlacementName.getAttribute('value').then(function (value) {
        expect(value.length).toEqual(0);
      });
      page.applyToStartDate.click();
      page.addedSectionPlacementName.getAttribute('value').then(function (value) {
        expect(value.length).not.toEqual(0);
      });
      expect(page.addedSectionPlacementName.getAttribute('value')).toEqual(placementName.slice(0, 256));
    });

    it('should enter valid data into Add Placements Tab fields', function() {
      navigate.newPlacement(campaignName, bootstrapData.io);
      expect(browser.getLocationAbsUrl()).toContain('/placement/add');
      page.mediaPackageField.get(0).clear();
      page.mediaPackageField.get(0).sendKeys(data.placementWithPkg.packageName);
      page.placementNameField.get(0).clear();
      page.placementNameField.get(0).sendKeys(data.placementWithPkg.name);
      page.siteAssocDropDown.get(0).click();
      page.siteAssocDropDown.get(0).element(by.cssContainingText('li', data.placementWithPkg.site)).click();
      browser.actions().mouseMove(page.sectionDropDown.get(0)).perform();
      page.sectionDropDown.get(0).click();
      page.sectionDropDown.get(0).element(by.cssContainingText('li', data.placementWithPkg.section)).click();
      page.sizeDropDown.get(0).click();
      page.sizeDropDown.get(0).element(by.cssContainingText('li', data.placementWithPkg.size)).click();
      page.addPlcmntRow.click();
      page.placementNameField.get(1).sendKeys(data.placementNoPkg.name);
      page.siteAssocDropDown.get(1).click();
      page.siteAssocDropDown.get(1).element(by.cssContainingText('li', data.placementNoPkg.site)).click();
      browser.actions().mouseMove(page.sectionDropDown.get(1)).perform();
      page.sectionDropDown.get(1).click();
      page.sectionDropDown.get(1).element(by.cssContainingText('li', data.placementNoPkg.section)).click();
      page.sizeDropDown.get(1).click();
      page.sizeDropDown.get(1).element(by.cssContainingText('li', data.placementNoPkg.size)).click();
      expect(page.mediaPackageField.get(0).getAttribute('value')).toEqual(data.placementWithPkg.packageName);
      expect(page.placementNameField.get(0).getAttribute('value')).toEqual(data.placementWithPkg.name);
      expect(page.siteAssocDropDown.get(0).element(by.css('span')).getText()).toEqual(data.placementWithPkg.site);
      expect(page.sectionDropDown.get(0).element(by.css('span')).getText()).toEqual(data.placementWithPkg.section);
      expect(page.sizeDropDown.get(0).element(by.css('span')).getText()).toEqual(data.placementWithPkg.size);
      expect(page.placementNameField.get(1).getAttribute('value')).toEqual(data.placementNoPkg.name);
      expect(page.siteAssocDropDown.get(1).element(by.css('span')).getText()).toEqual(data.placementNoPkg.site);
      expect(page.sectionDropDown.get(1).element(by.css('span')).getText()).toEqual(data.placementNoPkg.section);
      expect(page.sizeDropDown.get(1).element(by.css('span')).getText()).toEqual(data.placementNoPkg.size);
    });

    it('should navigate to next tab, Placement Properties', function() {
      expect(page.placementAddNextBtn.getAttribute('class')).not.toContain('disabled');
      browser.actions().mouseMove(page.placementAddNextBtn).perform();
      page.placementAddNextBtn.click();
      expect(page.addPlcmntTab2.getAttribute('class')).toContain('active');
    });

    it('should navigate to previous tab and back', function() {
      browser.actions().mouseMove(page.addPlcmntPrevious).perform();
      page.addPlcmntPrevious.click();
      expect(page.addPlcmntTab1.getAttribute('class')).toContain('active');
      page.placementAddNextBtn.click();
    });

    it('should enter values and apply to all', function() {
      page.applyToAdSpend.sendKeys(data.placementWithPkg.adSpend);
      page.applyToRate.sendKeys(data.placementWithPkg.rate);
      browser.actions().mouseMove(page.applyToRateType).perform();
      page.applyToRateType.click();  
      page.applyToRateTypeByValue(data.placementWithPkg.rateType).click();
      page.applyToStartDate.clear();
      page.applyToStartDate.sendKeys(today);
      page.applyToEndDate.clear();
      page.applyToEndDate.sendKeys(data.placementWithPkg.endDate);
      page.applyToBtn.click();
      expect(page.getAdSpendColumn.get(0).getAttribute('value')).toEqual(data.placementWithPkg.adSpend);
      expect(page.getRateColumn.get(0).getAttribute('value')).toEqual(data.placementWithPkg.rate);
      expect(page.getRateTypeColumn.get(0).getAttribute('value')).toEqual(data.placementWithPkg.rateType);
      expect(page.getStartDateColumn.get(0).getAttribute('value')).toEqual(today);
      expect(page.getEndDateColumn.get(0).getAttribute('value')).toEqual(data.placementWithPkg.endDate);
      expect(page.getAdSpendColumn.get(1).getAttribute('value')).toEqual(data.placementWithPkg.adSpend);
      expect(page.getRateColumn.get(1).getAttribute('value')).toEqual(data.placementWithPkg.rate);
      expect(page.getRateTypeColumn.get(1).getAttribute('value')).toEqual(data.placementWithPkg.rateType);
      expect(page.getStartDateColumn.get(1).getAttribute('value')).toEqual(today);
      expect(page.getEndDateColumn.get(1).getAttribute('value')).toEqual(data.placementWithPkg.endDate);
    });

    it('should update values for ' + data.placementNoPkg.name, function() {
      page.getAdSpendColumn.get(0).clear();
      page.getAdSpendColumn.get(0).sendKeys(data.placementNoPkg.adSpend);
      page.getRateColumn.get(0).clear();
      page.getRateColumn.get(0).sendKeys(data.placementNoPkg.rate);
      page.getRateTypeColumn.get(0).click();
      page.getRateTypeColumn.get(0).element(by.cssContainingText('#rateTypeSelect option', data.placementNoPkg.rateType)).click();
      page.getStartDateColumn.get(0).clear();
      page.getStartDateColumn.get(0).sendKeys(data.placementNoPkg.startDate);
      page.getEndDateColumn.get(0).clear();
      page.getEndDateColumn.get(0).sendKeys(data.placementNoPkg.endDate);
      expect(page.getAdSpendColumn.get(0).getAttribute('value')).toEqual(data.placementNoPkg.adSpend);
      expect(page.getRateColumn.get(0).getAttribute('value')).toEqual(data.placementNoPkg.rate);
      expect(page.getRateTypeColumn.get(0).getAttribute('value')).toEqual(data.placementNoPkg.rateType);
      expect(page.getStartDateColumn.get(0).getAttribute('value')).toEqual(data.placementNoPkg.startDate);
      expect(page.getEndDateColumn.get(0).getAttribute('value')).toEqual(data.placementNoPkg.endDate);
    });

    it('should display correct inventory calculations', function() {
      expect(page.getInventoryColumn.get(0).getText()).toEqual(commaSeparateNumber(data.placementNoPkg.inventory));
      expect(page.getInventoryColumn.get(1).getText()).toEqual(commaSeparateNumber(data.placementWithPkg.inventory));
    });

    it('should save placement data', function() {
      page.saveBtn.click();
      expect(page.getPlcmntGridRowData(data.placementWithPkg.name, 'Site').getText()).toEqual(data.placementWithPkg.site);
      expect(page.getPlcmntGridRowData(data.placementWithPkg.name, 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber(data.placementWithPkg.adSpend));
      expect(page.getPlcmntGridRowData(data.placementWithPkg.name, 'Inventory').getText()).toEqual(commaSeparateNumber(data.placementWithPkg.inventory));
      expect(page.getPlcmntGridRowData(data.placementWithPkg.name, 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.placementWithPkg.rate));
      expect(page.getPlcmntGridRowData(data.placementWithPkg.name, 'Rate Type').getText()).toEqual(data.placementWithPkg.rateType);
      expect(page.getPlcmntGridRowData(data.placementWithPkg.name, 'Flight').getText()).toEqual(today + ' - ' + data.placementWithPkg.endDate);
      expect(page.getPlcmntGridRowData(data.placementNoPkg.name, 'Site').getText()).toEqual(data.placementNoPkg.site);
      expect(page.getPlcmntGridRowData(data.placementNoPkg.name, 'Size').getText()).toEqual(data.placementNoPkg.size);
      expect(page.getPlcmntGridRowData(data.placementNoPkg.name, 'Ad Spend').getText()).toEqual('$' + commaSeparateNumber(data.placementNoPkg.adSpend));
      expect(page.getPlcmntGridRowData(data.placementNoPkg.name, 'Inventory').getText()).toEqual(commaSeparateNumber((data.placementNoPkg.inventory)));
      expect(page.getPlcmntGridRowData(data.placementNoPkg.name, 'Rate').getText()).toEqual('$' + commaSeparateNumber(data.placementNoPkg.rate));
      expect(page.getPlcmntGridRowData(data.placementNoPkg.name, 'Rate Type').getText()).toEqual(data.placementNoPkg.rateType);
      expect(page.getPlcmntGridRowData(data.placementNoPkg.name, 'Flight').getText()).toEqual(data.placementNoPkg.startDate + ' - ' + data.placementNoPkg.endDate);
    });

    it('should navigate back to io grid when user clicks cancel', function() {
      navigate.newPlacement(campaignName, bootstrapData.io);
      expect(browser.getLocationAbsUrl()).toContain('/placement/add');
      cancel('yes');
      expect(browser.getLocationAbsUrl()).toContain('/placement/list');
    });

    function commaSeparateNumber(val){
      while (/(\d+)(\d{3})/.test(val.toString())){
        val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
      }
      return val;
    }

    var cancel = function(action) {
      browser.driver.wait(protractor.until.elementIsVisible(page.addPlcmntCancel));
      page.addPlcmntCancel.click();
      var el = element(by.css('button[data-ng-click="' + action + '()"]'));
      browser.wait(function() {
        return browser.isElementPresent(el);
      }).then(function(){
        expect(el.isDisplayed()).toBe(true);
        el.click();
      });
    };

  });

});

module.exports = PlacementCreate;


