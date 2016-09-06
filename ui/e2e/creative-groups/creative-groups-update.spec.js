'use strict';

var CreativeGroupDetail = (function () {

  describe('Details', function() {
    var page = require('../page-object/creative-groups.po'),
        com = require('../page-object/utilities.po'),
        common = require('../utilities/common.spec'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        longText = 'vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv',
        campaignName = 'Protractor Campaign',
        creativeGroupName = 'Protractor Creative Group';

    it('should display back button creative groups list', function () {
      navigate.firstCampaign();
      nav.creativeGroupTab.click();
      browser.actions().mouseMove(nav.creativeGroupDataRows.get(1)).perform();
      nav.creativeGroupDataRows.get(1).click();
      nav.editIcon.click();
      expect(page.listOfCreativeGroupBtn.isDisplayed()).toBeTruthy();
    });

    it('should navigate to list of creative groups when \'creative groups list \' button is pressed', function () {
      page.listOfCreativeGroupBtn.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.gridUrl);
    });

    it('should display Campaigns grid with correct columns and rows', function() {
      nav.creativeGroupDataRows.get(1).click();
      nav.editIcon.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
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
      expect(com.deleteBtn.isDisplayed()).toBe(true);
      expect(page.detailsCancelBtn.isDisplayed()).toBe(true);
      expect(com.saveBtn.isDisplayed()).toBe(true);
    });

    it('should enable save button only when there are changes', function() {
      expect(com.saveBtn.isEnabled()).toBe(false);
      page.nameField.sendKeys('update');
      expect(com.saveBtn.isEnabled()).toBe(true);
      page.detailsCancel();
      nav.gridRowByName(creativeGroupName).click();
      nav.editIcon.click();

      expect(com.saveBtn.isEnabled()).toBe(false);
      page.defaultGroupSwitch.click();
      expect(com.saveBtn.isEnabled()).toBe(true);
      page.detailsCancel();
      nav.gridRowByName(creativeGroupName).click();
      nav.editIcon.click();
    });

    it('should display duplicate name validation message', function() {
      page.nameField.clear();
      page.nameField.sendKeys('default');
      com.saveBtn.click();
      expect(com.errorMsg.isDisplayed()).toBe(true);
      page.detailsCancel();
      nav.gridRowByName(creativeGroupName).click();
      nav.editIcon.click();
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
      expect(com.saveBtn.isEnabled()).toBe(false);
      page.detailsCancel();
      nav.gridRowByName(creativeGroupName).click();
      nav.editIcon.click();
    });

    it('should display required validation messages on fields and have save button disabled', function() {
      //Name, weight max, frequency cap, frequency hour, priority cannot be blank
      enableOptions();
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
      page.detailsCancel();
      nav.gridRowByName(creativeGroupName).click();
      nav.editIcon.click();
    });

    it('should require numeric values in delivery option fields', function() {
      expect(page.weightDistInput.getAttribute('type')).toEqual('number');
      expect(page.frequencyCapInput.getAttribute('type')).toEqual('number');
      expect(page.frequencyCapWindowInput.getAttribute('type')).toEqual('number');
      expect(page.priorityInput.getAttribute('type')).toEqual('number');
    });

    it('should disable fields when Default Group is switched on', function() {
      page.defaultGroup.isSelected().then(function(selected) {
         if (selected == false) {
            page.defaultGroupSwitch.click();
         }
      });
      page.weightDist.isSelected().then(function(selected) {
         if (selected == false) {
            page.weightDistSwitch.click();
         }
      });
      expect(page.weightDist.isEnabled()).toBe(true);
      expect(page.weightDistInput.isEnabled()).toBe(true);
      expect(page.frequencyCap.isEnabled()).toBe(false);
      expect(page.frequencyCapInput.isEnabled()).toBe(false);
      expect(page.frequencyCapWindowInput.isEnabled()).toBe(false);
      expect(page.priority.isEnabled()).toBe(false);
      expect(page.priorityInput.isEnabled()).toBe(false);
      page.geoTab.click();
      expect(page.geoTarget.isEnabled()).toBe(false);
      page.dayPartTab.click();
      expect(page.dayPartTarget.isEnabled()).toBe(false);

      //uncheck default group
      page.defaultGroupSwitch.click();
      expect(page.weightDist.isEnabled()).toBe(true);
      expect(page.frequencyCap.isEnabled()).toBe(true);
      expect(page.priority.isEnabled()).toBe(true);
      expect(page.cookieTab.isEnabled()).toBe(true);
      expect(page.geoTab.isEnabled()).toBe(true);
      expect(page.dayPartTab.isEnabled()).toBe(true);
    });

    it('should update to default group but retain option values', function() {
      var weightValue = page.weightDistInput.getAttribute('value'),
          frequencyCapValue = page.frequencyCapInput.getAttribute('value'),
          frequencyCapWindowValue = page.frequencyCapWindowInput.getAttribute('value'),
          priorityValue = page.priorityInput.getAttribute('value');
      page.nameField.sendKeys('default');
      page.defaultGroup.isSelected().then(function(selected) {
         if ( selected == false ) {
            page.defaultGroupSwitch.click();
         }
      });
      page.weightDist.isSelected().then(function(selected) {
         if ( selected == true ) {
            page.weightDistSwitch.click();
         }
      });
      com.saveBtn.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
      expect(com.errorMsg.isPresent()).toBe(false);
      navigate.creativeGroupDetails(campaignName, creativeGroupName);
      expect(page.nameField.getAttribute('value')).toContain('default');
      expect(page.weightDist.isEnabled()).toBe(true);
      expect(page.frequencyCap.isEnabled()).toBe(false);
      expect(page.priority.isEnabled()).toBe(false);
      expect(page.weightDistInput.getAttribute('value')).toEqual(weightValue);
      expect(page.frequencyCapInput.getAttribute('value')).toEqual(frequencyCapValue);
      expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual(frequencyCapWindowValue);
      expect(page.priorityInput.getAttribute('value')).toEqual(priorityValue);
    });

    it('should update from a default group to non default group', function() {
      //update values to previous values
      var weightValue = page.weightDistInput.getAttribute('value'),
          frequencyCapValue = page.frequencyCapInput.getAttribute('value'),
          frequencyCapWindowValue = page.frequencyCapWindowInput.getAttribute('value'),
          priorityValue = page.priorityInput.getAttribute('value');
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
      expect(page.frequencyCap.isSelected()).toBe(false);
      expect(page.frequencyCap.isEnabled()).toBe(true);
      expect(page.priority.isSelected()).toBe(false);
      expect(page.priority.isEnabled()).toBe(true);
      page.nameField.clear();
      page.nameField.sendKeys(creativeGroupName);
      com.saveBtn.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
      expect(com.errorMsg.isPresent()).toBe(false);
      navigate.creativeGroupDetails(campaignName, creativeGroupName);
      expect(page.nameField.getAttribute('value')).toContain(creativeGroupName);
      expect(page.weightDist.isEnabled()).toBe(true);
      expect(page.frequencyCap.isEnabled()).toBe(true);
      expect(page.priority.isEnabled()).toBe(true);
      expect(page.weightDistInput.getAttribute('value')).toEqual(weightValue);
      expect(page.frequencyCapInput.getAttribute('value')).toEqual(frequencyCapValue);
      expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual(frequencyCapWindowValue);
      expect(page.priorityInput.getAttribute('value')).toEqual(priorityValue);
    });

    it('should update and save all values', function() {
      enableOptions();
      page.nameField.sendKeys('update');
      page.weightDistInput.clear();
      page.weightDistInput.sendKeys('50');
      page.frequencyCapInput.clear();
      page.frequencyCapInput.sendKeys('50');
      page.frequencyCapWindowInput.clear();
      page.frequencyCapWindowInput.sendKeys('50');
      page.priorityInput.clear();
      page.priorityInput.sendKeys('10');
      com.saveBtn.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
      expect(com.errorMsg.isPresent()).toBe(false);
      navigate.creativeGroupDetails(campaignName, creativeGroupName + 'update');
      expect(page.nameField.getAttribute('value')).toContain('update');
      expect(page.weightDistInput.getAttribute('value')).toEqual('50');
      expect(page.frequencyCapInput.getAttribute('value')).toEqual('50');
      expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual('50');
      expect(page.priorityInput.getAttribute('value')).toEqual('10');
    });

    it('should update and save all values back to original', function() {
      //update values to previous values
      page.nameField.clear();
      page.nameField.sendKeys(creativeGroupName);
      page.weightDistInput.clear();
      page.weightDistInput.sendKeys('99');
      page.frequencyCapInput.clear();
      page.frequencyCapInput.sendKeys('4');
      page.frequencyCapWindowInput.clear();
      page.frequencyCapWindowInput.sendKeys('23');
      page.priorityInput.clear();
      page.priorityInput.sendKeys('5');
      com.saveBtn.click();
      expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
      expect(com.errorMsg.isPresent()).toBe(false);
      navigate.creativeGroupDetails(campaignName, creativeGroupName);
      expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName);
      expect(page.weightDistInput.getAttribute('value')).toEqual('99');
      expect(page.frequencyCapInput.getAttribute('value')).toEqual('4');
      expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual('23');
      expect(page.priorityInput.getAttribute('value')).toEqual('5');
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
       }
    });
  };

  });

});

module.exports = CreativeGroupDetail;


