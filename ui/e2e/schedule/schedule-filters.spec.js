'use strict';

var scheduleFilters = (function (campaignName) {
    describe('Schedule Flyout Filters', function() {
        var CONSTANTS = require('../utilities/constants'),
            EC = protractor.ExpectedConditions,
            navigate = require('../utilities/navigation.spec'),
            util = require('../utilities/util'),
            page = require('../page-object/schedule.po');

        it('should display filters in the correct order when SITE is pivot', function() {
            navigate.scheduleGrid(campaignName);
            page.scheduleDataRows.get(0).click();
            page.flyoutFilterBtn.click();
            expect(page.flyoutFilterDropdown(0).isDisplayed()).toBe(true);
            expect(page.flyoutFilterDropdown(1).isDisplayed()).toBe(true);
            expect(page.flyoutFilterDropdown(2).isDisplayed()).toBe(true);
            expect(page.flyoutFilterLabel(0).getText()).toContain('Placements');
            expect(page.flyoutFilterLabel(0).getAttribute('disabled')).toBeTruthy();
            expect(page.flyoutFilterLabel(1).getText()).toContain('Groups');
            expect(page.flyoutFilterLabel(1).getAttribute('disabled')).toBeTruthy();
            expect(page.flyoutFilterLabel(2).getText()).toContain('Creative');
            expect(page.flyoutFilterLabel(2).getAttribute('disabled')).toBeTruthy();
        });

        it('should hide/show an element according to the checkbox on the filter when SITE is pivot', function() {
            page.flyoutRowExpand.get(0).click();
            page.flyoutRowExpand.get(0).click();
            expect(page.flyoutFilterLabel(0).getAttribute('disabled')).toBeFalsy();
            expect(page.flyoutDataRows.count()).toEqual(4);
            page.flyoutFilterDropdown(0).click();
            page.flyoutFilterDropdownOption(0, 0).click();
            page.flyoutFilterDropdown(0).click();
            expect(page.flyoutDataRows.count()).toEqual(3);
            page.flyoutFilterDropdown(0).click();
            page.flyoutFilterDropdownOption(0, 0).click();
            page.flyoutFilterDropdown(0).click();
            expect(page.flyoutDataRows.count()).toEqual(4);
            page.modalClose.click();
            browser.wait(EC.not(EC.visibilityOf(page.modalTitle)), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(false);
        });

        it('should display filters in the correct order when PLACEMENT is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            util.click(page.scheduleDataRows.get(0));
            page.flyoutFilterBtn.click();
            expect(page.flyoutFilterDropdown(0).isDisplayed()).toBe(true);
            expect(page.flyoutFilterDropdown(1).isDisplayed()).toBe(true);
            expect(page.flyoutFilterLabel(0).getText()).toContain('Groups');
            expect(page.flyoutFilterLabel(0).getAttribute('disabled')).toBeTruthy();
            expect(page.flyoutFilterLabel(1).getText()).toContain('Creative');
            expect(page.flyoutFilterLabel(1).getAttribute('disabled')).toBeTruthy();
        });

        it('should hide/show an element according to the checkbox on the filter when PLACEMENT is pivot', function() {
            util.click(page.flyoutRowExpand.get(0));
            expect(page.flyoutFilterLabel(0).getAttribute('disabled')).toBeFalsy();
            expect(page.flyoutDataRows.count()).toEqual(2);
            page.flyoutFilterDropdown(0).click();
            page.flyoutFilterDropdownOption(0, 0).click();
            page.flyoutFilterDropdown(0).click();
            expect(page.flyoutDataRows.count()).toEqual(1);
            page.flyoutFilterDropdown(0).click();
            page.flyoutFilterDropdownOption(0, 0).click();
            page.flyoutFilterDropdown(0).click();
            expect(page.flyoutDataRows.count()).toEqual(2);
            page.modalClose.click();
            browser.wait(EC.not(EC.visibilityOf(page.modalTitle)), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(false);
        });

        it('should display filters in the correct order when GROUP is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);
            util.click(page.scheduleDataRows.get(0));
            page.flyoutFilterBtn.click();
            expect(page.flyoutFilterDropdown(0).isDisplayed()).toBe(true);
            expect(page.flyoutFilterDropdown(1).isDisplayed()).toBe(true);
            expect(page.flyoutFilterDropdown(2).isDisplayed()).toBe(true);
            expect(page.flyoutFilterLabel(0).getText()).toContain('Sites');
            expect(page.flyoutFilterLabel(0).getAttribute('disabled')).toBeTruthy();
            expect(page.flyoutFilterLabel(1).getText()).toContain('Placements');
            expect(page.flyoutFilterLabel(1).getAttribute('disabled')).toBeTruthy();
            expect(page.flyoutFilterLabel(2).getText()).toContain('Creative');
            expect(page.flyoutFilterLabel(2).getAttribute('disabled')).toBeTruthy();
        });

        it('should hide/show an element according to the checkbox on the filter when GROUP is pivot', function() {
            page.flyoutRowExpand.get(0).click();
            expect(page.flyoutFilterLabel(0).getAttribute('disabled')).toBeFalsy();
            expect(page.flyoutDataRows.count()).toEqual(2);
            page.flyoutFilterDropdown(0).click();
            page.flyoutFilterDropdownOption(0, 0).click();
            page.flyoutFilterDropdown(0).click();
            expect(page.flyoutDataRows.count()).toEqual(1);
            page.flyoutFilterDropdown(0).click();
            page.flyoutFilterDropdownOption(0, 0).click();
            page.flyoutFilterDropdown(0).click();
            expect(page.flyoutDataRows.count()).toEqual(2);
            page.modalClose.click();
            browser.wait(EC.not(EC.visibilityOf(page.modalTitle)), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(false);
        });

        it('should display filters in the correct order when CREATIVE is pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.CREATIVE);
            util.click(page.scheduleDataRows.get(0));
            page.flyoutFilterBtn.click();
            expect(page.flyoutFilterDropdown(0).isDisplayed()).toBe(true);
            expect(page.flyoutFilterDropdown(1).isDisplayed()).toBe(true);
            expect(page.flyoutFilterDropdown(2).isDisplayed()).toBe(true);
            expect(page.flyoutFilterLabel(0).getText()).toContain('Sites');
            expect(page.flyoutFilterLabel(0).getAttribute('disabled')).toBeTruthy();
            expect(page.flyoutFilterLabel(1).getText()).toContain('Placements');
            expect(page.flyoutFilterLabel(1).getAttribute('disabled')).toBeTruthy();
            expect(page.flyoutFilterLabel(2).getText()).toContain('Groups');
            expect(page.flyoutFilterLabel(2).getAttribute('disabled')).toBeTruthy();
        });

        it('should hide/show an element according to the checkbox on the filter when CREATIVE is pivot', function() {
            util.click(page.flyoutRowExpand.get(0));
            expect(page.flyoutFilterLabel(0).getAttribute('disabled')).toBeFalsy();
            expect(page.flyoutDataRows.count()).toEqual(2);
            page.flyoutFilterDropdown(0).click();
            page.flyoutFilterDropdownOption(0, 0).click();
            page.flyoutFilterDropdown(0).click();
            expect(page.flyoutDataRows.count()).toEqual(1);
            page.flyoutFilterDropdown(0).click();
            page.flyoutFilterDropdownOption(0, 0).click();
            page.flyoutFilterDropdown(0).click();
            expect(page.flyoutDataRows.count()).toEqual(2);
            page.modalClose.click();
            browser.wait(EC.not(EC.visibilityOf(page.modalTitle)), CONSTANTS.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(false);
        });
    });
});

module.exports = scheduleFilters;
