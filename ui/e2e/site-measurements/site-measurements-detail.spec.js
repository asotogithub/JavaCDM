'use strict';

var siteMeasurementsDetail = (function () {

    describe('Site Measurements Detail', function() {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/site-measurements.po'),
            nav = require('../page-object/navigation.po'),
            utilities = require('../page-object/utilities.po'),
            descriptionText = 'Description Text';

        it('should navigate to Site Measurements page', function() {
            nav.siteMeasurementItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/site-measurements');
        });

        describe('Summary Tab', function() {
            it('should open summary tab', function() {
                expect(page.dataRows.count()).toBeGreaterThan(0);
                page.dataRows.get(0).click();
                expect(page.summaryContainer.isPresent()).toBe(true);
            });

            it('should enable Save button when update description field', function() {
                expect(page.summarySaveButton.isEnabled()).toBe(false);
                page.summaryDescriptionInput.clear();
                page.summaryDescriptionInput.sendKeys(descriptionText);
                expect(page.summarySaveButton.isEnabled()).toBe(true);
            });

            it('should show warning message when try to close tab', function() {
                var EC = protractor.ExpectedConditions;

                expect(page.summarySaveButton.isEnabled()).toBe(true);
                page.summaryCloseButton.click();
                expect(page.confirmationDialog.isPresent()).toBe(true);
                page.confirmationCancelButton.click();
                browser.wait(EC.not(EC.presenceOf(page.confirmationCancelButton)), CONSTANTS.defaultWaitInterval);
                expect(page.confirmationDialog.isPresent()).toBe(false);
            });

            it('should discard changes and go to SM Campaign list', function() {
                expect(page.summarySaveButton.isEnabled()).toBe(true);
                page.campaignsButton.click();
                expect(page.confirmationDialog.isPresent()).toBe(true);
                page.confirmationDiscardButton.click();
                expect(page.siteMeasurementTitle.isPresent()).toBe(true);
            });
        });
    });
});

module.exports = siteMeasurementsDetail;
