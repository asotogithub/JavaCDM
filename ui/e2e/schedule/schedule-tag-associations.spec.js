'use strict';

var scheduleTagAssociations = (function (campaignName, bootstrapData) {
    describe('Schedule Tracking Tag Associations', function () {
        var page = require('../page-object/schedule.po'),
            navigate = require('../utilities/navigation.spec'),
            CONSTANTS = require('../utilities/constants'),
            EC = protractor.ExpectedConditions;

        it('should navigate to schedule grid and change the pivot to PLACEMENT', function () {
            browser.wait(function () {
                return global.runSchedule;
            });

            navigate.scheduleGrid(campaignName);
            expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
            page.changePivotDropdown(CONSTANTS.SCHEDULE_PIVOT.PLACEMENT);
            expect(page.scheduleDataRows.count()).toBe(2);
        });

        it('should open flyout and expand the Tracking Tag Associations panel', function () {
            browser.actions().mouseMove(page.scheduleDataRows.get(0)).perform();
            page.scheduleDataRows.get(0).click();
            expect(page.modalTitle.isDisplayed()).toBe(true);
            expect(page.tagAssociationsPanel.isDisplayed()).toBe(true);
            expect(page.sectionTagAssocData.isDisplayed()).toBe(false);

            page.sectionTagAssoc.click();
            browser.wait(EC.invisibilityOf(page.sectionTwoData), CONSTANTS.defaultWaitInterval);
            expect(page.sectionTagAssocData.isDisplayed()).toBe(true);
            expect(page.tagAssociationsGridRows.count()).toBe(2);
        });

        it('should close the first popover by clicking on other view button', function () {
            browser.actions().mouseMove(page.getTrackingTagButtonByRowIndex(0, 'htmlContentBtn')).perform();
            page.getTrackingTagButtonByRowIndex(0, 'htmlContentBtn').click();
            expect(page.popover.isDisplayed()).toBe(true);

            browser.actions().mouseMove(page.getTrackingTagButtonByRowIndex(0, 'htmlSecureContentBtn')).perform();
            page.getTrackingTagButtonByRowIndex(0, 'htmlSecureContentBtn').click();
            expect(page.popover.isDisplayed()).toBe(true);
        });

        it('should open and close the popover clicking on view button in Content column', function () {
            browser.actions().mouseMove(page.getTrackingTagButtonByRowIndex(0, 'htmlContentBtn')).perform();
            page.getTrackingTagButtonByRowIndex(0, 'htmlContentBtn').click();
            expect(page.popover.isDisplayed()).toBe(true);

            browser.actions().mouseMove(page.getTrackingTagButtonByRowIndex(0, 'htmlContentBtn')).perform();
            page.getTrackingTagButtonByRowIndex(0, 'htmlContentBtn').click();
            expect(page.popover.isPresent()).toBe(false);
        });

        it('should close the popover in Secure Content column clicking somewhere else but view button', function () {
            browser.actions().mouseMove(page.getTrackingTagButtonByRowIndex(0, 'htmlContentBtn')).perform();
            page.getTrackingTagButtonByRowIndex(0, 'htmlContentBtn').click();
            expect(page.popover.isDisplayed()).toBe(true);

            page.expandLeft.click();
            expect(page.popover.isDisplayed()).toBe(false);
        });

        it('should try to delete an association and cancel the deletion process', function () {
            browser.actions().mouseMove(page.tagAssociationsGridRows.get(0)).perform();
            page.tagAssociationsGridRows.get(0).click();
            page.deleteTagAssociationButton.click();
            expect(page.cancelDialogButton.isPresent()).toBe(true);

            browser.actions().mouseMove(page.cancelDialogButton).perform();
            page.cancelDialogButton.click();
            browser.wait(EC.not(EC.presenceOf(page.cancelDialogButton)), CONSTANTS.defaultWaitInterval);
            expect(page.cancelDialogButton.isPresent()).toBe(false);
            expect(page.tagAssociationsGridRows.count()).toEqual(2);
        });

        it('should delete a tag association', function () {
            browser.actions().mouseMove(page.deleteTagAssociationButton).perform();
            page.deleteTagAssociationButton.click();
            expect(page.deleteDialogButton.isPresent()).toBe(true);

            browser.actions().mouseMove(page.deleteDialogButton).perform();
            page.deleteDialogButton.click();
            browser.wait(EC.not(EC.presenceOf(page.deleteDialogButton)), CONSTANTS.defaultWaitInterval);
            expect(page.deleteDialogButton.isPresent()).toBe(false);
            expect(page.tagAssociationsGridRows.count()).toEqual(1);
        });
    });
});

module.exports = scheduleTagAssociations;
