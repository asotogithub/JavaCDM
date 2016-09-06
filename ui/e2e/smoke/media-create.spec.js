'use strict';

var mediaCreate = (function (campaignName, ioName) {

  describe('IO and Placement Create', function() {
    var page = require('../page-object/media.po'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        campaignUtil = require('../utilities/campaigns.js'),
        common = require('../utilities/common.spec'),
        util = require('../utilities/util'),
        size = '728x90',
        size2 = '300x250',
        placementName = 'Smoke Test Placement',
        placementName2 = 'HTML5 Placement';

    it('should create a new io, ' + ioName, function() {   
        common.addIo(campaignName, ioName);
    });

    it('should create 2 new placements', function() {
        var EC = protractor.ExpectedConditions;
        navigate.newPlacement(campaignName , ioName);
        page.placementNameField.get(0).sendKeys(placementName);
        util.click(page.siteAssocDropDown.get(0));
        var site = page.siteAssocDropDown.get(0).element(by.css('ul.chosen-results li:first-of-type')).getText();
        util.click(page.siteAssocDropDown.get(0).element(by.cssContainingText('li',site)));
        util.click(page.sectionDropDown.get(0));
        var section = page.sectionDropDown.get(0).element(by.css('ul.chosen-results li:first-of-type')).getText();
        util.click(page.sectionDropDown.get(0).element(by.cssContainingText('li', section)));
        util.click(page.sizeDropDown.get(0));
        util.click(page.sizeDropDown.get(0).element(by.cssContainingText('li', size)));
        expect(page.placementNameField.get(0).getAttribute('value')).toEqual(placementName);
        expect(page.siteAssocDropDown.get(0).element(by.css('span')).getText()).toEqual(site);
        expect(page.sectionDropDown.get(0).element(by.css('span')).getText()).toEqual(section);
        expect(page.sizeDropDown.get(0).element(by.css('span')).getText()).toEqual(size);
        util.click(page.addPlcmntRow);
        page.placementNameField.get(1).sendKeys(placementName2);
        util.click(page.siteAssocDropDown.get(1));
        util.click(page.siteAssocDropDown.get(1).element(by.cssContainingText('li', site)));
        util.click(page.sectionDropDown.get(1));
        util.click(page.sectionDropDown.get(1).element(by.cssContainingText('li', section)));
        util.click(page.sizeDropDown.get(1));
        util.click(page.sizeDropDown.get(1).element(by.cssContainingText('li', size2)));
        util.click(page.placementAddNextBtn);
        browser.wait(EC.visibilityOf(page.saveBtn), 5000);
        page.saveBtn.click();
        expect(page.getPlcmntGridRowData(placementName, 'Site').getText()).toEqual(site);
        expect(page.getPlcmntGridRowData(placementName, 'Size').getText()).toEqual(size);
        expect(page.getPlcmntGridRowData(placementName2, 'Site').getText()).toEqual(site);
        expect(page.getPlcmntGridRowData(placementName2, 'Size').getText()).toEqual(size2);
    });


    it('should activate placements', function() {
        expect(page.getPlcmntGridRowData(placementName, 'Status').getText()).toEqual('Planning');
        expect(page.getPlcmntGridRowData(placementName2, 'Status').getText()).toEqual('Planning');
        util.click(page.selectAll);
        util.click(page.activateBtn);
        util.click(page.ioEditSave);
        expect(page.getPlcmntGridRowData(placementName, 'Status').getText()).toEqual('Active');
        expect(page.getPlcmntGridRowData(placementName2, 'Status').getText()).toEqual('Active');
    });


  });
});

module.exports = mediaCreate;