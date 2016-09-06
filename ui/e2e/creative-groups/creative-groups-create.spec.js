'use strict';

var CreativeGroupCreate = (function () {

  describe('New Creative Group', function() {
    var page = require('../page-object/creative-groups.po'),
        com = require('../page-object/utilities.po'),
        common = require('../utilities/common.spec'),
        cg = require('../utilities/creative-groups.spec'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        longText = 'vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv',
        campaignName = 'Protractor Campaign',
        creativeGroupName = 'Protractor Creative Group Create';

    it('should display back button creative groups list', function () {
      navigate.newCreativeGroup(campaignName);
      expect(page.listOfCreativeGroupBtn.isDisplayed()).toBeTruthy();
    });

    it('should navigate to list of creative groups when \'creative groups list \' button is pressed', function () {
      page.listOfCreativeGroupBtn.click();
        expect(browser.getLocationAbsUrl()).toMatch(page.gridUrl);
    });

    it('should navigate to new creative group page', function() {
      navigate.newCreativeGroup(campaignName);
    });

    it('should display all fields and buttons', function() {
      expect(page.nameField.isDisplayed()).toBe(true);
      expect(page.defaultGroupSwitch.isDisplayed()).toBe(true);
      expect(page.weightDistSwitch.isDisplayed()).toBe(true);
      expect(page.weightDistInput.isDisplayed()).toBe(true);
      expect(page.frequencyCapSwitch.isDisplayed()).toBe(true);
      expect(page.frequencyCapInput.isDisplayed()).toBe(true);
      expect(page.frequencyCapWindowInput.isDisplayed()).toBe(true);
      expect(page.prioritySwitch.isDisplayed()).toBe(true);
      expect(page.priorityInput.isDisplayed()).toBe(true);
      expect(page.cookieTab.isDisplayed()).toBe(true);
      expect(page.geoTab.isDisplayed()).toBe(true);
      expect(page.dayPartTab.isDisplayed()).toBe(true);
      expect(page.cookieTargetSwitch.isDisplayed()).toBe(true);
      page.geoTab.click();
      expect(page.countryTab.isDisplayed()).toBe(true);
      page.dayPartTab.click();
      expect(page.dayPartTargetSwitch.isDisplayed()).toBe(true);
      expect(page.iabStandardsDayPartTarget.isDisplayed()).toBe(true);
      expect(page.customDayPartTarget.isDisplayed()).toBe(true);
      expect(com.closeBtn.isDisplayed()).toBe(true);
      expect(com.saveBtn.isDisplayed()).toBe(true);
    });

    it('should enable save button only when there are changes', function() {
      expect(com.saveBtn.isEnabled()).toBe(false);
      page.nameField.sendKeys('new');
      expect(com.saveBtn.isEnabled()).toBe(true);
      common.close('yes');
      nav.addBtn.click();
    });

    it('should display duplicate name validation message', function() {
      page.nameField.clear();
      page.nameField.sendKeys('default');
      com.saveBtn.click();
      expect(com.errorMsg.isDisplayed()).toBe(true);
      common.close('yes');
      nav.addBtn.click();
    });

    it('should display max validation messages and have save button disabled', function() {
      enableOptions();
      page.nameField.clear();
      page.nameField.sendKeys(longText);
      page.weightDistInput.clear();
      page.weightDistInput.sendKeys('101');
      page.frequencyCapInput.clear();
      page.frequencyCapInput.sendKeys('10000');
      page.frequencyCapWindowInput.clear();
      page.frequencyCapWindowInput.sendKeys('1000');
      page.priorityInput.clear();
      page.priorityInput.sendKeys('16');
      page.priorityInput.sendKeys(protractor.Key.TAB);
      expect(page.nameTooLongMsg.isDisplayed()).toBe(true);
      expect(page.weightDistMaxMsg.isDisplayed()).toBe(true);
      expect(page.frequencyCapMaxMsg.isDisplayed()).toBe(true);
      expect(page.frequencyCapWindowMaxMsg.isDisplayed()).toBe(true);
      expect(page.priorityMaxMsg.isDisplayed()).toBe(true);
      page.priorityInput.clear();
      page.priorityInput.sendKeys('1.1');
      expect(page.priorityInvalidMsg.isDisplayed()).toBe(true);
      expect(com.saveBtn.isEnabled()).toBe(false);
      common.close('yes');
      nav.addBtn.click();
    });

    it('should display required validation messages on fields and have save button disabled', function() {
      //Name, weight max, frequency cap, frequency hour, priority cookie targeting cannot be blank
      enableOptions();
      page.nameField.sendKeys(longText);
      page.nameField.clear();
      page.weightDistInput.clear();
      page.frequencyCapInput.clear();
      page.frequencyCapWindowInput.clear();
      page.priorityInput.clear();
      page.priorityInput.sendKeys(protractor.Key.TAB);
      expect(page.nameRequiredMsg.isDisplayed()).toBe(true);
      expect(page.weightDistRequired.isDisplayed()).toBe(true);
      expect(page.frequencyCapRequiredMsg.isPresent()).toBe(true);
      expect(page.frequencyCapWindowRequiredMsg.isPresent()).toBe(true);
      expect(page.priorityRequiredMsg.isPresent()).toBe(true);
      expect(com.saveBtn.isEnabled()).toBe(false);
      common.close('yes');
      nav.addBtn.click();
    });

    it('should require numeric values in delivery option fields', function() {
      expect(page.weightDistInput.getAttribute('type')).toEqual('number');
      expect(page.frequencyCapInput.getAttribute('type')).toEqual('number');
      expect(page.frequencyCapWindowInput.getAttribute('type')).toEqual('number');
      expect(page.priorityInput.getAttribute('type')).toEqual('number');
    });

    it('should disable fields when default group is switched on', function() {
      page.defaultGroup.isSelected().then(function(selected) {
         if ( selected == false ) {
            page.defaultGroupSwitch.click();
         }
      });
      expect(page.weightDist.isEnabled()).toBe(true);
      expect(page.weightDistInput.isEnabled()).toBe(false);
      expect(page.frequencyCap.isEnabled()).toBe(false);
      expect(page.frequencyCapInput.isEnabled()).toBe(false);
      expect(page.frequencyCapWindowInput.isEnabled()).toBe(false);
      expect(page.priority.isEnabled()).toBe(false);
      expect(page.priorityInput.isEnabled()).toBe(false);
      page.cookieTab.click();
      expect(page.cookieTarget.isEnabled()).toBe(false);
      page.geoTab.click();
      expect(page.geoTarget.isEnabled()).toBe(false);
      page.dayPartTab.click();
      expect(page.dayPartTarget.isEnabled()).toBe(false);

      //uncheck default group
      page.defaultGroupSwitch.click();
      expect(page.weightDist.isEnabled()).toBe(true);
      expect(page.frequencyCap.isEnabled()).toBe(true);
      page.cookieTab.click();
      expect(page.cookieTarget.isEnabled()).toBe(true);
      page.geoTab.click();
      expect(page.geoTarget.isEnabled()).toBe(true);
      page.dayPartTab.click();
      expect(page.dayPartTarget.isEnabled()).toBe(true);
    });

    it('should create creative group as a default group, ' + cg.creativeGroupDefault.creativeGroupName, function() {
      common.newCreativeGroup(campaignName, cg.creativeGroupDefault);
      navigate.creativeGroupDetails(campaignName, cg.creativeGroupDefault.creativeGroupName);
      expect(page.nameField.getAttribute('value')).toEqual(cg.creativeGroupDefault.creativeGroupName);
      expect(page.defaultGroup.isSelected()).toBe(true);
      expect(page.weightDist.isEnabled()).toBe(true);
      expect(page.frequencyCap.isEnabled()).toBe(false);
      expect(page.priority.isEnabled()).toBe(false);
      expect(page.cookieTarget.isEnabled()).toBe(false);
      page.dayPartTab.click();
      expect(page.dayPartTarget.isEnabled()).toBe(false);
      expect(page.weightDistInput.getAttribute('value')).toEqual('100');
      expect(page.frequencyCapInput.getAttribute('value')).toEqual('1');
      expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual('24');
      expect(page.priorityInput.getAttribute('value')).toEqual('0');
      expect(page.cookieTargetText.getAttribute('value')).toEqual('');
    });

    it('should delete newly created creative groups, ' + cg.creativeGroupDefault.creativeGroupName, function() {
      common.deleteCreativeGroup(campaignName, cg.creativeGroupDefault.creativeGroupName);
    });

  function enableOptions(){
    page.defaultGroup.isSelected().then(function(selected) {
       if ( selected == true ) {
          page.defaultGroupSwitch.click();
       }
    });
    page.weightDist.isSelected().then(function(selected) {
       if ( selected == false ) {
          page.weightDistSwitch.click();
       }
    });
    page.frequencyCap.isSelected().then(function(selected) {
       if ( selected == false ) {
          page.frequencyCapSwitch.click();
       }
    });
    page.priority.isSelected().then(function(selected) {
       if ( selected == false ) {
          page.prioritySwitch.click();
          expect(page.priorityInput.getAttribute('value')).toEqual('1');
       }
    });
    page.cookieTarget.isSelected().then(function(selected) {
       if ( selected == false ) {
          page.cookieTargetSwitch.click();
       }
    });
    page.dayPartTarget.isDisplayed().then(
        function(displayed){
          if(!displayed){
            page.dayPartTab.click();
          }
          page.dayPartTarget.isSelected().then(function (selected) {
            if (selected == false) {
              page.dayPartTargetSwitch.click();
            }
          });
        });
  };

  });

});

module.exports = CreativeGroupCreate;


