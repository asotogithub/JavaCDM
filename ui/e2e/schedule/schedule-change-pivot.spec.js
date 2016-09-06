'use strict';

var scheduleChangePivot = (function (campaignName) {

    describe('Schedule Change Pivot', function() {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/schedule.po'),
            util = require('../utilities/util'),
            navigate = require('../utilities/navigation.spec');

        it('should expand/collapse all rows, SITE is Pivot', function() {
            navigate.scheduleGrid(campaignName);
            browser.driver.wait(function() {
                return page.scheduleGrid.isPresent();
            });
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.SITE);
            expect(page.scheduleDataRows.count()).toBe(1);
            for (var i = 0; i < 4; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toBeGreaterThan(3);
            for (var j = 3; j >= 0; j--) {
                util.click(page.rowCollapse.get(j));
            }
            expect(page.scheduleDataRows.count()).toBe(1);
        });

        it('should expand/collapse all rows, PLACEMENT is Pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);

            expect(page.scheduleDataRows.count()).toBe(2);
            for (var i = 0; i < 4; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toBeGreaterThan(3);
            for (var j = 3; j >= 0; j--) {
                util.click(page.rowCollapse.get(j));
            }
            expect(page.scheduleDataRows.count()).toBe(2);
        });

        it('should expand/collapse all rows, GROUP is Pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.GROUP);

            expect(page.scheduleDataRows.count()).toBe(1);
            for (var i = 0; i < 4; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toBeGreaterThan(3);
            for (var j = 3; j >= 0; j--) {
                util.click(page.rowCollapse.get(j));
            }
            expect(page.scheduleDataRows.count()).toBe(1);
        });

        it('should expand/collapse all rows, CREATIVE is Pivot', function() {
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.CREATIVE);

            expect(page.scheduleDataRows.count()).toBe(4);
            for (var i = 0; i < 4; i++) {
                util.click(page.rowExpand.get(0));
            }
            expect(page.scheduleDataRows.count()).toBeGreaterThan(3);
            for (var j = 3; j >= 0; j--) {
                util.click(page.rowCollapse.get(j));
            }
            expect(page.scheduleDataRows.count()).toBe(4);
        });
    });
});

module.exports = scheduleChangePivot;
