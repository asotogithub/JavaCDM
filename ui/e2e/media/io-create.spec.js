'use strict';

var IOCreate = (function (campaignName) {

  describe('New IO', function() {
    var page = require('../page-object/media.po'),
        com = require('../page-object/utilities.po'),
        common = require('../utilities/common.spec'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        longText = '123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456ABC',
        ioBigNumber = '99999999999999',
        ioName = 'Protractor Create IO',
        ioNumber = '123',
        ioNote = 'Protractor IO note',
        constants = require('../utilities/constants'),
        EC = protractor.ExpectedConditions;

    it('should navigate to new io page', function() {
      navigate.newIO(campaignName);
      expect(browser.getLocationAbsUrl()).toContain('/io/add');
    });

    it('should enable save button only when there are changes', function() {
      expect(page.ioSave.getAttribute('class')).toContain('disabled');
      page.ioNameField.sendKeys('new');
      expect(page.ioSave.getAttribute('class')).not.toContain('disabled');
      cancel('yes');
      nav.addBtn.click();
    });

    it('should display validation messages and have save button disabled', function() {
      page.ioNameField.sendKeys(longText);
      page.ioNumberField.sendKeys(ioBigNumber);
      expect(page.ioNumberMax.isDisplayed()).toBe(true);
      expect(page.ioNameMaxLength.isDisplayed()).toBe(true);
      page.ioNumberField.clear();
      page.ioNumberField.sendKeys('e1.-');
      expect(page.ioNumberOnlyNumber.isDisplayed()).toBe(true);
      expect(page.ioSave.getAttribute('class')).toContain('disabled');
      cancel('yes');
      nav.addBtn.click();
    });

    it('should display required validation messages on fields and have save button disabled', function() {
      page.ioNameField.sendKeys(longText);
      page.ioNameField.clear();
      expect(page.ioNameRequired.isDisplayed()).toBe(true);
      cancel('yes');
      nav.addBtn.click();
    });

    it('should create new IO: ' + ioName, function() {
      page.ioNameField.sendKeys(ioName);
      page.ioNumberField.sendKeys(ioNumber);
      page.ioNoteField.clear();
      page.ioNoteField.sendKeys(ioNote);
      page.ioSave.click();
      navigate.ioSummary(campaignName, ioName);
      expect(page.ioNameField.getAttribute('value')).toEqual(ioName);
      expect(page.ioNumberField.getAttribute('value')).toEqual(ioNumber);
      expect(page.ioNoteField.getAttribute('value')).toEqual(ioNote);
    });

    function cancel(action) {


      browser.driver.wait(protractor.until.elementIsVisible(page.ioCancel));
      expect(page.ioCancel.isDisplayed()).toBe(true);
      page.ioCancel.click();
      browser.wait(EC.presenceOf(page.globalValidationWarningPopup), constants.defaultWaitInterval);
      var el = element(by.css('button[data-ng-click="' + action + '()"]'));
      browser.wait(function() {
        return browser.isElementPresent(el);
      }).then(function(){
        expect(el.isDisplayed()).toBe(true);
        el.click();
      });

      browser.wait(EC.not(EC.presenceOf(page.globalValidationWarningPopup)), constants.defaultWaitInterval);
    }

  });

});

module.exports = IOCreate;


