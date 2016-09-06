'use strict';

var IOUpdate= (function (campaignName, bootstrapData) {

  describe('Update IO', function() {
    var page = require('../page-object/media.po'),
        com = require('../page-object/utilities.po'),
        common = require('../utilities/common.spec'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        util = require('../utilities/util');

    it('should navigate to new io page', function() {
      navigate.ioSummary(campaignName, bootstrapData.io);
      expect(browser.getLocationAbsUrl()).toContain('/io');
    });

    it('should enable save button only when there are changes', function() {
      expect(page.ioEditSave.isEnabled()).toBe(false);
      page.ioNameField.sendKeys('new');
      expect(page.ioEditSave.isEnabled()).toBe(true);
      page.ioEditCancel.click();
    });

    it('should display required validation messages on fields and have save button disabled', function() {
      navigate.ioSummary(campaignName, bootstrapData.io);
      page.ioNameField.sendKeys(common.getRandomString(129));
      expect(page.ioNameMaxLength.isDisplayed()).toBe(true);
      page.ioNameField.clear();
      expect(page.ioNameRequired.isDisplayed()).toBe(true);
      page.ioEditCancel.click();
    });

    it('should update IO', function() {
      navigate.ioSummary(campaignName, bootstrapData.io);
      page.ioNameField.sendKeys('update');
      page.ioNumberField.sendKeys('999');
      page.ioNoteField.sendKeys('update');
      util.click(page.ioEditSave);
      navigate.ioSummary(campaignName, bootstrapData.io + 'update');
      expect(page.ioNameField.getAttribute('value')).toEqual(bootstrapData.io + 'update');
      expect(page.ioNumberField.getAttribute('value')).toContain('999');
      expect(page.ioNoteField.getAttribute('value')).toContain('update');
    });
  });

});

module.exports = IOUpdate;


