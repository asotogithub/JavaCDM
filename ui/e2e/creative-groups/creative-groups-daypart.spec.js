'use strict';

var CreativeGroupsDayPart = (function () {

    describe('DayPartTargeting', function() {

        var page = require('../page-object/creative-groups.po'),
            com = require('../page-object/utilities.po'),
            common = require('../utilities/common.spec'),
            nav = require('../page-object/navigation.po'),
            navigate = require('../utilities/navigation.spec'),
            campaignName = 'Protractor Campaign',
            creativeGroupName = new Date().getTime() + 'Protractor Day Part Targeting Group';

        it('should activate day part type tab when switching tabs', function() {
            navigate.newCreativeGroup(campaignName);
            expect(page.dayPartTab.isDisplayed()).toBe(true);
            page.dayPartTab.click();
            expect(page.iabStandardsDayPartTarget.isDisplayed()).toBe(true);
            expect(page.customDayPartTarget.isDisplayed()).toBe(true);
        });

        it('should enable day part targeting', function() {
            page.dayPartTab.click();
            expect(page.dayPartTarget.isEnabled()).toBe(true);
            expect(page.dayPartTarget.isSelected()).toBe(false);
            page.dayPartTargetSwitch.click();
            expect(page.dayPartTarget.isSelected()).toBe(true);
            expect(page.iabStandardsDayPartTarget.isEnabled()).toBe(true);
            expect(page.customDayPartTarget.isEnabled()).toBe(true);
        });

        it('should select all/deselect all options on the IAB Standards', function() {
            page.dayPartTab.click();
            page.iabStandardsDayPartTarget.click();
            page.earlyMorning.click();
            expect(page.earlyMorning.isSelected()).toBe(true);
            page.daytime.click();
            expect(page.daytime.isSelected()).toBe(true);
            page.evening.click();
            expect(page.evening.isSelected()).toBe(true);
            page.lateNight.click();
            expect(page.lateNight.isSelected()).toBe(true);
            page.weekends.click();
            expect(page.weekends.isSelected()).toBe(true);
            page.earlyMorning.click();
            expect(page.earlyMorning.isSelected()).toBe(false);
            page.daytime.click();
            expect(page.daytime.isSelected()).toBe(false);
            page.evening.click();
            expect(page.evening.isSelected()).toBe(false);
            page.lateNight.click();
            expect(page.lateNight.isSelected()).toBe(false);
            page.weekends.click();
            expect(page.weekends.isSelected()).toBe(false);
            common.close('yes');
        });

        it('should save creative group with IAB standards day part targeting configuration', function() {
            navigate.newCreativeGroup(campaignName)
            page.nameField.sendKeys(creativeGroupName);
            page.dayPartTab.click();
            page.dayPartTargetSwitch.click();
            page.iabStandardsDayPartTarget.click();
            page.earlyMorning.click();
            page.weekends.click();
            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            page.dayPartTab.click();
            expect(page.iabStandardsDayPartTarget.isSelected()).toBe(true);
            expect(page.earlyMorning.isSelected()).toBe(true);
            expect(page.weekends.isSelected()).toBe(true);
        });

        it('should save creative group updates with IAB standards day part targeting configuration', function() {
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            page.dayPartTab.click();
            page.iabStandardsDayPartTarget.click();
            page.lateNight.click();
            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            page.dayPartTab.click();
            expect(page.iabStandardsDayPartTarget.isSelected()).toBe(true);
            expect(page.earlyMorning.isSelected()).toBe(true);
            expect(page.weekends.isSelected()).toBe(true);
            expect(page.lateNight.isSelected()).toBe(true);
        });

        it('should delete newly created creative groups', function() {
            common.deleteCreativeGroup(campaignName, creativeGroupName);
        });

       it('should add some day part custom times to the text box', function() {
            navigate.newCreativeGroup(campaignName);
            page.dayPartTab.click();
            page.dayPartTargetSwitch.click();
            page.customDayPartTarget.click();
            expect(page.addCustomTimesBtn.isEnabled()).toBe(false);

            page.startTimeHourUp.click();
            page.startTimeHourUp.click();
            page.startTimeHourUp.click();
            page.startTimeMinuteDown.click();
            page.startTimeMinuteDown.click();
            page.startTimeMinuteDown.click();
            page.startTimeAMPM.click();

            page.endTimeHourUp.click();
            page.endTimeHourUp.click();
            page.endTimeHourUp.click();
            page.endTimeMinuteDown.click();
            page.endTimeMinuteDown.click();
            page.endTimeMinuteDown.click();
            page.endTimeAMPM.click();

            page.daySelectTue.click();
            page.daySelectThu.click();
            page.addCustomTimesBtn.click();
            expect(page.timesSelected.get(0).getText()).toContain('Tue, 02:15pm — 02:15pm');
            expect(page.timesSelected.get(1).getText()).toContain('Thu, 02:15pm — 02:15pm');

            page.daySelectSun.click();
            page.addCustomTimesBtn.click();
            expect(page.timesSelected.get(0).getText()).toContain('Sun, 02:15pm — 02:15pm');
            expect(page.timesSelected.get(1).getText()).toContain('Tue, 02:15pm — 02:15pm');
            expect(page.timesSelected.get(2).getText()).toContain('Thu, 02:15pm — 02:15pm');

            expect(page.clearSelectedCustomDayPartTarget.isEnabled()).toBe(false);
            page.timesSelected.get(0).click();
            expect(page.clearSelectedCustomDayPartTarget.isEnabled()).toBe(true);
            page.clearSelectedCustomDayPartTarget.click();
            expect(page.timesSelected.get(0).getText()).toContain('Tue, 02:15pm — 02:15pm');
            expect(page.timesSelected.get(1).getText()).toContain('Thu, 02:15pm — 02:15pm');

            page.clearAllSelectedDayPartTargets.click();
            expect(page.timesSelected.count()).toBe(0);
            common.close('yes');
       });

       it('should save creative group with all custom day part targeting configuration', function() {
            navigate.newCreativeGroup(campaignName);
            page.nameField.sendKeys(creativeGroupName);
            page.dayPartTab.click();
            page.dayPartTargetSwitch.click();
            page.customDayPartTarget.click();
            page.startTimeHourUp.click();
            page.startTimeHourUp.click();
            page.startTimeHourUp.click();
            page.startTimeMinuteDown.click();
            page.startTimeMinuteDown.click();
            page.startTimeMinuteDown.click();
            page.startTimeAMPM.click();

            page.endTimeHourUp.click();
            page.endTimeHourUp.click();

            page.endTimeMinuteDown.click();
            page.endTimeMinuteDown.click();
            page.endTimeMinuteDown.click();
            page.endTimeAMPM.click();
            page.daySelectTue.click();
            page.daySelectThu.click();
            page.addCustomTimesBtn.click();
            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            page.dayPartTab.click();
            expect(page.timesSelected.get(0).getText()).toContain('Tue, 02:15pm — 02:15pm');
            expect(page.timesSelected.get(1).getText()).toContain('Thu, 02:15pm — 02:15pm');
       });


       it('should save updates to custom day part targeting configuration', function(){
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            page.dayPartTab.click();
            page.customDayPartTarget.click();
            page.startTimeHourUp.click();
            page.startTimeMinuteDown.click();
            page.endTimeHourUp.click();
            page.endTimeHourUp.click();
            page.daySelectWed.click();
            page.addCustomTimesBtn.click();
            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            page.dayPartTab.click();
            expect(page.timesSelected.get(0).getText()).toContain('Wed, 12:45am — 03:00am');
            expect(page.timesSelected.get(1).getText()).toContain('Tue, 02:15pm — 02:15pm');
            expect(page.timesSelected.get(2).getText()).toContain('Thu, 02:15pm — 02:15pm');
       })

       it('should prevent end time from being greater than start time', function() {
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            page.dayPartTab.click();
            page.customDayPartTarget.click();
            //Validate end time cannot be before start time
            page.startTimeHourUp.click();
            page.startTimeHourUp.click();
            expect(page.endTimeHour.getAttribute('value')).toEqual('02');
            page.endTimeHourDown.click();
            expect(page.endTimeHour.getAttribute('value')).toEqual('02');
            page.endTimeMinuteDown.click();
            expect(page.endTimeHour.getAttribute('value')).toEqual('02');
       })

       it('should delete newly created creative groups', function() {
            common.deleteCreativeGroup(campaignName, creativeGroupName);
       });

    });

});

module.exports = CreativeGroupsDayPart;
