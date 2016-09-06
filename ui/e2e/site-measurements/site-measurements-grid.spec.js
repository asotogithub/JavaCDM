'use strict';

var siteMeasurements = (function () {

    describe('Site Measurements Grid', function() {
        var page = require('../page-object/site-measurements.po'),
            gridValidation = require('../utilities/grid-validation.spec'),
            gridColumnsSortValidation = require('../utilities/grid-validation-columns-sort.spec'),
            nav = require('../page-object/navigation.po'),
            campaign = {
                newName: ' A New Name',
                newDescription: ' A New Description'
            },
            utilities = require('../page-object/utilities.po');

        it('should navigate to page and display Site Measurements', function() {
            nav.siteMeasurementItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/site-measurements');
            page.dataRows.then(function(rows){
            expect(rows.length).toBeGreaterThan(0);
            });
        });

        it('should show add button', function () {
            expect(page.addSiteMeasurementBtn.isDisplayed()).toBe(true);
        });

        it('should display legend Table', function () {
            expect(page.legend.isDisplayed()).toBe(true);
        });

        it('should display legend with expected information after perfoming a search',function() {
            utilities.searchInput.sendKeys('#R_...$@~.;dSD');
            page.dataRows.then(function(rows){
                expect(page.legend.getText()).toContain(rows.length);
            });
            utilities.searchClear.click();
        });

        gridValidation('Name', page.columnNames);
        gridColumnsSortValidation(page.columnNames);

        describe('Add Campaign', function() {
            it('should open add campaign wizard and step 1', function() {
                page.addSiteMeasurementBtn.click();
                expect(page.addCampaignWizardStep1Panel.isPresent()).toBe(true);
                expect(page.addCampaignCancelButton.isEnabled()).toBe(true);
            });

            it('should fill required fields for the step 1', function() {
                page.addCampaignNameInput.sendKeys(campaign.newName);
                page.selectDropdownByPosition(page.addCampaignAdvertiserInput, 2).click();
                expect(page.addCampaignBrandInput.isEnabled()).toBe(true);
                page.selectDropdownByPosition(page.addCampaignBrandInput, 2).click();
                expect(page.addCampaignNextButton.isEnabled()).toBe(true);
            });

            it('should open event wizard\'s step 2', function() {
                page.addCampaignNextButton.click();
                expect(page.addCampaignWizardStep2Panel.isPresent()).toBe(true);
                expect(page.addCampaignCancelButton.isEnabled()).toBe(true);
            });

            it('should fill required fields for the step 2', function() {
                page.selectDropdownByPosition(page.addCampaignCookieDomainInput, 2).click();
                page.selectDropdownByPosition(page.addCampaignStatusInput, 2).click();
                page.addCampaignCampaignDescriptionInput.sendKeys(campaign.newDescription);
                expect(page.addCampaignSaveButton.isEnabled()).toBe(true);
            });

            it('should close add event wizard', function() {
                page.addCampaignCancelButton.click();
                expect(page.confirmationDialog.isPresent()).toBe(true);
                page.confirmationDiscardButton.click();
                expect(page.addCampaignWizardStep2Panel.isPresent()).toBe(false);
            });
        });
    });
});

module.exports = siteMeasurements;
