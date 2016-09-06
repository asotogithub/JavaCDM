'use strict';

var campaignsDetail = (function () {

  describe('Details', function() {
    var CONSTANTS = require('../utilities/constants'),
        nav = require('../page-object/navigation.po'),
        page = require('../page-object/campaigns.po');

    it('should display Campaigns grid with correct columns and rows', function() {
      nav.campaignsItem.click();
      var campaignName = page.campaignsRow.get(0).getText();
      page.dataRows.get(0).click();
      expect(page.campaignDetailHeader.getText()).toBe(campaignName);
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
      browser.refresh();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
    });

    it('should display all fields', function() {
      expect(page.advertiserField.isDisplayed()).toBe(true);
      expect(page.brandField.isDisplayed()).toBe(true);
      expect(page.campaignIdField.isDisplayed()).toBe(true);
      expect(page.campaignNameField.isDisplayed()).toBe(true);
      expect(page.domainField.isDisplayed()).toBe(true);
      expect(page.startDateField.isDisplayed()).toBe(true);
      expect(page.endDateField.isDisplayed()).toBe(true);
      expect(page.isActive.isEnabled()).toBe(true);
    });

    it('should enable save button only when there are changes', function() {
      expect(page.saveBtn.isEnabled()).toBe(false);
      page.campaignNameField.sendKeys('update');
      expect(page.saveBtn.isEnabled()).toBe(true);

      browser.refresh();
      expect(page.saveBtn.isEnabled()).toBe(false);
      page.isActiveSwitch.click();
      expect(page.saveBtn.isEnabled()).toBe(true);
      browser.refresh();
    });

    it('should display error messages on Campaign Name validations', function() {
      //Campaign name max length validation
      expect(page.campaignNameField.getAttribute('data-ng-maxlength'))
          .toEqual(String(CONSTANTS.CAMPAIGN.NAME.MAX_LENGTH));

      //Unique name validation
      nav.campaignsItem.click();
      var campaignName = page.campaignsRow.get(0).getText(),
          campaign2Name = page.campaignsRow.get(1).getText();
      page.campaignRowByName(campaignName).click();
      page.campaignNameField.clear();
      page.campaignNameField.sendKeys(campaign2Name);
      page.saveBtn.click();
      expect(page.errorMsg.isDisplayed()).toBe(true);
      browser.refresh();
    });

    it('should save record', function() {
      var nameValue = page.campaignNameField.getAttribute('value');
      page.campaignNameField.sendKeys('update');
      page.saveBtn.click();
      expect(page.errorMsg.isPresent()).toBe(false);
      expect(page.saveConfirmation.isDisplayed()).toBe(true);
      expect(page.campaignNameField.getAttribute('value')).toContain('update');
      page.campaignNameField.clear();
      page.campaignNameField.sendKeys(nameValue);
      page.saveBtn.click();
      expect(page.errorMsg.isPresent()).toBe(false);
      expect(page.saveConfirmation.isDisplayed()).toBe(true);
      browser.wait(protractor.ExpectedConditions.stalenessOf(page.saveConfirmation));
      expect(page.campaignNameField.getAttribute('value')).toBe(nameValue);
    });

    it('should listOfCampaignsBtn be visible on all campaign tabs', function() {
      expect(page.listOfCampaignsBtn.isPresent()).toBe(true);
      nav.mediaTab.click();
      expect(page.listOfCampaignsBtn.isPresent()).toBe(true);
      nav.creativeGroupTab.click();
      expect(page.listOfCampaignsBtn.isPresent()).toBe(true);
      nav.creativeTab.click();
      expect(page.listOfCampaignsBtn.isPresent()).toBe(true);
      nav.scheduleTab.click();
      expect(page.listOfCampaignsBtn.isPresent()).toBe(true);
    });

    // Commenting this out until there's an good way to test this without a preexisting trafficked campaign
    // it('should have text only Domain value for trafficked campaign', function() {
    //   nav.campaignsItem.click();
    //   page.campaignRowByName(traffickedCampaign).click();
    //   expect(page.campaignDetailHeader.getText()).toBe(traffickedCampaign);
    //   expect(page.domainFieldReadOnly.isDisplayed()).toBe(true);
    // });

  });
});

module.exports = campaignsDetail;


