'use strict';

var CreativeGroupDefault = (function () {

  describe('System Default Creative Group', function() {
    var page = require('../page-object/creative-groups.po'),
        com = require('../page-object/utilities.po'),
        common = require('../utilities/common.spec'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        campaignName = 'Protractor Campaign';

    it('should not have an input field for name field', function() {
      navigate.creativeGroupDetails(campaignName, 'Default');
      expect(page.nameField.isPresent()).toBe(false);
      expect(page.nameReadOnly.getText()).toEqual('Default')
    });

    it('should be marked as default with inability to update', function() {
      expect(page.defaultGroup.isEnabled()).toBe(false);
      expect(page.frequencyCap.isEnabled()).toBe(false);
      expect(page.frequencyCapInput.isEnabled()).toBe(false);
      expect(page.frequencyCapWindowInput.isEnabled()).toBe(false);
      expect(page.priority.isEnabled()).toBe(false);
      expect(page.priorityInput.isEnabled()).toBe(false);
      expect(page.cookieTarget.isEnabled()).toBe(false);
      expect(page.cookieTargetText.isEnabled()).toBe(false);
      page.dayPartTab.click();
      expect(page.dayPartTarget.isEnabled()).toBe(false);
    });

    it('should allow weight distribution to be updated to 0', function() {
      expect(page.weightDist.isEnabled()).toBe(true);
      page.weightDist.isSelected().then(function(selected) {
         if ( selected == false ) {
            page.weightDistSwitch.click();
         }
      });
      expect(page.weightDistInput.isEnabled()).toBe(true);
      page.weightDistInput.clear();
      page.weightDistInput.sendKeys('0');
      com.saveBtn.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
      expect(com.errorMsg.isPresent()).toBe(false);
      navigate.creativeGroupDetails(campaignName, 'Default');
      expect(page.weightDistInput.getAttribute('value')).toEqual('0');
      expect(page.weightDist.isSelected()).toBe(true);
    });

    it('should disable weight distribution and save', function() {
      expect(page.weightDist.isEnabled()).toBe(true);
      page.weightDist.isSelected().then(function(selected) {
         if ( selected == true ) {
            page.weightDistSwitch.click();
         }
      });
      expect(page.weightDistInput.isEnabled()).toBe(false);
      com.saveBtn.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
      expect(com.errorMsg.isPresent()).toBe(false);
      navigate.creativeGroupDetails(campaignName, 'Default');
      expect(page.weightDist.isSelected()).toBe(false);
    });

    it('should set weight distribution back to 100', function() {
      expect(page.weightDist.isEnabled()).toBe(true);
      page.weightDist.isSelected().then(function(selected) {
         if ( selected == false ) {
            page.weightDistSwitch.click();
         }
      });
      page.weightDistInput.clear();
      page.weightDistInput.sendKeys('100');
      com.saveBtn.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
      expect(com.errorMsg.isPresent()).toBe(false);
      navigate.creativeGroupDetails(campaignName, 'Default');
      expect(page.weightDistInput.getAttribute('value')).toEqual('100');
      expect(page.weightDist.isSelected()).toBe(true);
    });

  });

});

module.exports = CreativeGroupDefault;


