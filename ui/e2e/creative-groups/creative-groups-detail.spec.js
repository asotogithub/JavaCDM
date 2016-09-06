'use strict';

var CreativeGroupDetail = (function () {
    describe('Details', function () {
        var page = require('../page-object/creative-groups.po'),
            com = require('../page-object/utilities.po'),
            common = require('../utilities/common.spec'),
            nav = require('../page-object/navigation.po'),
            navigate = require('../utilities/navigation.spec'),
            longText = 'vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv',
            dayPartValue = 'browserlocalday in (\'mon\',\'tue\',\'wed\',\'thu\',\'fri\') AND (browserlocaltime >= 0600 AND browserlocaltime < 0800)',
            cookieTargetValue = '[cp_type]=2',
            campaignName = 'Mixed Targeting',
            creativeGroupName = 'all targeting logic1';

        it('should display Campaigns grid with correct columns and rows', function () {
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
            browser.refresh();
            expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
        });

        it('should display all fields and buttons', function () {
            expect(page.nameField.isDisplayed()).toBe(true);
            expect(page.defaultGroupSwitch.isDisplayed()).toBe(true);
            expect(page.weightDistSwitch.isDisplayed()).toBe(true);
            expect(page.weightDistInput.isDisplayed()).toBe(true);
            expect(page.frequencyCapSwitch.isDisplayed()).toBe(true);
            expect(page.frequencyCapInput.isDisplayed()).toBe(true);
            expect(page.frequencyCapWindowInput.isDisplayed()).toBe(true);
            expect(page.prioritySwitch.isDisplayed()).toBe(true);
            expect(page.priorityInput.isDisplayed()).toBe(true);
            expect(page.cookieTargetSwitch.isDisplayed()).toBe(true);
            expect(page.cookieTargetText.isDisplayed()).toBe(true);
            expect(com.deleteBtn.isDisplayed()).toBe(true);
            expect(com.cancelBtn.isDisplayed()).toBe(true);
            expect(com.saveBtn.isDisplayed()).toBe(true);
        });

        it('should enable save button only when there are changes', function () {
            expect(com.saveBtn.isEnabled()).toBe(false);
            page.nameField.sendKeys('update');
            expect(com.saveBtn.isEnabled()).toBe(true);
            common.cancel('yes');
            nav.gridRowByName(creativeGroupName).click();

            expect(com.saveBtn.isEnabled()).toBe(false);
            page.defaultGroupSwitch.click();
            expect(com.saveBtn.isEnabled()).toBe(true);
            common.cancel('yes');
            nav.gridRowByName(creativeGroupName).click();
        });

        it('should display duplicate name validation message', function () {
            page.nameField.clear();
            page.nameField.sendKeys('default');
            com.saveBtn.click();
            expect(com.errorMsg.isDisplayed()).toBe(true);
            common.cancel('yes');
            nav.gridRowByName(creativeGroupName).click();
        });

        it('should display max validation messages and have save button disabled', function () {
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
            common.cancel('yes');
            nav.gridRowByName(creativeGroupName).click();
        });

        it('should display required validation messages on fields and have save button disabled', function () {
            //Name, weight max, frequency cap, frequency hour, priority cookie targeting cannot be blank
            enableOptions();
            page.nameField.clear();
            page.weightDistInput.clear();
            page.frequencyCapInput.clear();
            page.frequencyCapWindowInput.clear();
            page.priorityInput.clear();
            page.cookieTab.click();
            page.cookieTargetText.clear();
            page.dayPartTab.click();
            page.dayPartTargetText.clear();
            page.dayPartTargetText.sendKeys(protractor.Key.TAB);
            expect(page.nameRequiredMsg.isDisplayed()).toBe(true);
            expect(page.weightDistRequired.isDisplayed()).toBe(true);
            expect(page.frequencyCapRequiredMsg.isPresent()).toBe(true);
            expect(page.frequencyCapWindowRequiredMsg.isPresent()).toBe(true);
            expect(page.priorityRequiredMsg.isPresent()).toBe(true);
            expect(page.cookieTargetRequiredMsg.isPresent()).toBe(true);
            expect(page.dayPartTargetRequiredMsg.isPresent()).toBe(true);
            expect(com.saveBtn.isEnabled()).toBe(false);
            common.cancel('yes');
            nav.gridRowByName(creativeGroupName).click();
        });

        it('should display invalid validation messages on fields and have save button disabled', function () {
            enableOptions();
            page.weightDistInput.clear();
            page.weightDistInput.sendKeys('aa');
            page.frequencyCapInput.clear();
            page.frequencyCapInput.sendKeys('aa');
            page.frequencyCapWindowInput.clear();
            page.frequencyCapWindowInput.sendKeys('aa');
            page.priorityInput.clear();
            page.priorityInput.sendKeys('aa');
            page.priorityInput.sendKeys(protractor.Key.TAB);
            expect(page.weightDistRequired.isDisplayed()).toBe(true);
            expect(page.frequencyCapRequiredMsg.isPresent()).toBe(true);
            expect(page.frequencyCapWindowRequiredMsg.isPresent()).toBe(true);
            expect(page.priorityRequiredMsg.isPresent()).toBe(true);
            expect(com.saveBtn.isEnabled()).toBe(false);
            common.cancel('yes');
            nav.gridRowByName(creativeGroupName).click();
        });

        it('should disable fields when Default Group is switched on', function () {
            page.defaultGroup.isSelected().then(function (selected) {
                if (selected == false) {
                    page.defaultGroupSwitch.click();
                }
            });
            expect(page.weightDist.isEnabled()).toBe(true);
            expect(page.weightDistInput.isEnabled()).toBe(true);
            expect(page.frequencyCap.isEnabled()).toBe(false);
            expect(page.frequencyCapInput.isEnabled()).toBe(false);
            expect(page.frequencyCapWindowInput.isEnabled()).toBe(false);
            expect(page.priority.isEnabled()).toBe(false);
            expect(page.priorityInput.isEnabled()).toBe(false);
            expect(page.cookieTarget.isEnabled()).toBe(false);
            expect(page.cookieTargetText.isEnabled()).toBe(false);
            expect(page.dayPartTarget.isEnabled()).toBe(false);
            expect(page.dayPartTargetText.isEnabled()).toBe(false);

            //uncheck default group
            page.defaultGroupSwitch.click();
            expect(page.weightDist.isEnabled()).toBe(true);
            expect(page.frequencyCap.isEnabled()).toBe(true);
            expect(page.priority.isEnabled()).toBe(true);
            expect(page.cookieTarget.isEnabled()).toBe(true);
            expect(page.dayPartTarget.isEnabled()).toBe(true);
        });

        it('should update to default group but retain option values', function () {
            var weightValue = page.weightDistInput.getAttribute('value'),
                frequencyCapValue = page.frequencyCapInput.getAttribute('value'),
                frequencyCapWindowValue = page.frequencyCapWindowInput.getAttribute('value'),
                priorityValue = page.priorityInput.getAttribute('value'),
                cookieValue = page.cookieTargetText.getAttribute('value'),
                dayPartValue = page.dayPartTargetText.getAttribute('value');

            page.nameField.sendKeys('default');
            page.defaultGroup.isSelected().then(function (selected) {
                if (selected == false) {
                    page.defaultGroupSwitch.click();
                }
            });
            page.weightDist.isSelected().then(function (selected) {
                if (selected == true) {
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
            expect(page.cookieTarget.isEnabled()).toBe(false);
            expect(page.dayPartTarget.isEnabled()).toBe(false);
            expect(page.weightDistInput.getAttribute('value')).toEqual(weightValue);
            expect(page.frequencyCapInput.getAttribute('value')).toEqual(frequencyCapValue);
            expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual(frequencyCapWindowValue);
            expect(page.priorityInput.getAttribute('value')).toEqual(priorityValue);
            expect(page.cookieTargetText.getAttribute('value')).toEqual(cookieValue);
            expect(page.dayPartTargetText.getAttribute('value')).toEqual(dayPartValue);
        });

        it('should update from a default group to non default group', function () {
            //update values to previous values
            var weightValue = page.weightDistInput.getAttribute('value'),
                frequencyCapValue = page.frequencyCapInput.getAttribute('value'),
                frequencyCapWindowValue = page.frequencyCapWindowInput.getAttribute('value'),
                priorityValue = page.priorityInput.getAttribute('value'),
                cookieValue = page.cookieTargetText.getAttribute('value'),
                dayPartValue = page.dayPartTargetText.getAttribute('value');

            page.defaultGroup.isSelected().then(function (selected) {
                if (selected == true) {
                    page.defaultGroupSwitch.click();
                }
            });
            page.weightDist.isSelected().then(function (selected) {
                if (selected == false) {
                    page.weightDistSwitch.click();
                }
            });
            expect(page.frequencyCap.isSelected()).toBe(false);
            expect(page.frequencyCap.isEnabled()).toBe(true);
            expect(page.priority.isSelected()).toBe(false);
            expect(page.priority.isEnabled()).toBe(true);
            expect(page.cookieTarget.isSelected()).toBe(false);
            expect(page.cookieTarget.isEnabled()).toBe(true);
            expect(page.dayPartTarget.isSelected()).toBe(false);
            expect(page.dayPartTarget.isEnabled()).toBe(true);
            page.nameField.clear();
            page.nameField.sendKeys(creativeGroupName);
            com.saveBtn.click();
            expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName);
            expect(page.weightDist.isEnabled()).toBe(true);
            expect(page.frequencyCap.isEnabled()).toBe(true);
            expect(page.priority.isEnabled()).toBe(true);
            expect(page.cookieTarget.isEnabled()).toBe(true);
            expect(page.dayPartTarget.isEnabled()).toBe(true);
            expect(page.weightDistInput.getAttribute('value')).toEqual(weightValue);
            expect(page.frequencyCapInput.getAttribute('value')).toEqual(frequencyCapValue);
            expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual(frequencyCapWindowValue);
            expect(page.priorityInput.getAttribute('value')).toEqual(priorityValue);
            expect(page.cookieTargetText.getAttribute('value')).toEqual(cookieValue);
            expect(page.dayPartTargetText.getAttribute('value')).toEqual(dayPartValue);
        });

        it('should update and save all values', function () {
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
            page.cookieTab.click();
            page.cookieTargetText.clear();
            page.cookieTargetText.sendKeys('update');
            page.dayPartTab.click();
            page.dayPartTargetText.clear();
            page.dayPartTargetText.sendKeys('update');
            com.saveBtn.click();
            expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            expect(page.nameField.getAttribute('value')).toContain('update');
            expect(page.weightDistInput.getAttribute('value')).toEqual('50');
            expect(page.frequencyCapInput.getAttribute('value')).toEqual('50');
            expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual('50');
            expect(page.priorityInput.getAttribute('value')).toEqual('10');
            expect(page.cookieTargetText.getAttribute('value')).toContain('update');
            expect(page.dayPartTargetText.getAttribute('value')).toContain('update');
        });

        it('should update and save all values back to original', function () {
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
            page.cookieTargetText.clear();
            page.cookieTargetText.sendKeys(cookieTargetValue);
            page.dayPartTab.click();
            page.dayPartTargetText.clear();
            page.dayPartTargetText.sendKeys(dayPartValue);
            com.saveBtn.click();
            expect(browser.getLocationAbsUrl()).toMatch(page.detailsUrl);
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName);
            expect(page.weightDistInput.getAttribute('value')).toEqual('99');
            expect(page.frequencyCapInput.getAttribute('value')).toEqual('4');
            expect(page.frequencyCapWindowInput.getAttribute('value')).toEqual('23');
            expect(page.priorityInput.getAttribute('value')).toEqual('5');
            expect(page.cookieTargetText.getAttribute('value')).toEqual(cookieTargetValue);
            expect(page.dayPartTargetText.getAttribute('value')).toEqual(dayPartValue);
        });

        it('should allow adding countries to the preview pane', function () {

        });

        function enableOptions() {
            page.defaultGroup.isSelected().then(function (selected) {
                if (selected == true) {
                    page.defaultGroupSwitch.click();
                }
            });
            page.weightDist.isSelected().then(function (selected) {
                if (selected == false) {
                    page.weightDistSwitch.click();
                }
            });
            page.frequencyCap.isSelected().then(function (selected) {
                if (selected == false) {
                    page.frequencyCapSwitch.click();
                }
            });
            page.priority.isSelected().then(function (selected) {
                if (selected == false) {
                    page.prioritySwitch.click();
                }
            });
            page.cookieTarget.isSelected().then(function (selected) {
                if (selected == false) {
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

module.exports = CreativeGroupDetail;

