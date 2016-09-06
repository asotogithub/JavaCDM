'use strict';

var schedulePaging = (function (campaignName) {
    var page = require('../page-object/schedule.po'),
        navigate = require('../utilities/navigation.spec'),
        constants = require('../utilities/constants');

    describe('Schedule View', function() {
        it('should have paging enabled on schedule view', function() {
            var EC = protractor.ExpectedConditions;

            navigate.scheduleGrid(campaignName);
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);

            browser.wait(EC.presenceOf(page.scheduleGridPaging), constants.defaultWaitInterval);
            expect(page.scheduleGridPaging.isPresent()).toBe(true);
        });
    });

    describe('Schedule Flyout', function() {
        it('should have paging enabled on schedule flyout', function() {
            var EC = protractor.ExpectedConditions;

            page.scheduleDataRows.get(0).click();
            browser.wait(EC.visibilityOf(page.modalTitle), constants.defaultWaitInterval);
            expect(page.modalTitle.isDisplayed()).toBe(true);

            browser.wait(EC.presenceOf(page.flyoutPaging), constants.defaultWaitInterval);
            expect(page.flyoutPaging.isPresent()).toBe(true);
        });
    });
});

module.exports = schedulePaging;
