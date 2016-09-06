'use strict';

var CreativeGroupCreate = (function () {

  describe('New Creative Group', function() {
    var page = require('../page-object/creative-groups.po'),
        common = require('../utilities/common.spec'),
        cg = require('../utilities/creative-groups.spec'),
        navigate = require('../utilities/navigation.spec'),
        campaignName = 'Protractor',
        creativeGroupDetails = cg.creativeGroupSmoke;

    it('should create creative group with all values, ' + creativeGroupDetails.creativeGroupName, function() {
      common.newCreativeGroup(campaignName, creativeGroupDetails);
      navigate.creativeGroupDetails(campaignName, creativeGroupDetails.creativeGroupName);
      expect(page.nameField.getAttribute('value')).toEqual(creativeGroupDetails.creativeGroupName);
      expect(page.defaultGroup.isSelected()).toBe(false);
      expect(page.weightDist.isSelected()).toBe(true);
      expect(page.frequencyCap.isSelected()).toBe(true);
      expect(page.priority.isSelected()).toBe(true);
      page.cookieTab.click();
      expect(page.cookieTarget.isSelected()).toBe(false);
      page.dayPartTab.click();
      expect(page.dayPartTargetSwitch.isDisplayed()).toBe(true);
      expect(page.iabStandardsDayPartTarget.isDisplayed()).toBe(true);
      expect(page.customDayPartTarget.isDisplayed()).toBe(true);
      expect(page.weightDistInput.getAttribute('value')).toEqual(creativeGroupDetails.weight);
      expect(page.frequencyCapInput.getAttribute('value')).toEqual(creativeGroupDetails.frequencyCap);
      expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual(creativeGroupDetails.frequencyCapWindow);
      expect(page.priorityInput.getAttribute('value')).toEqual(creativeGroupDetails.priority);
    });

    it('should delete newly created creative groups, ' + creativeGroupDetails.creativeGroupName, function() {
      common.deleteCreativeGroup(campaignName, creativeGroupDetails.creativeGroupName);
    });

  });

});

module.exports = CreativeGroupCreate;


