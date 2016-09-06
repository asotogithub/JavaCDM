'use strict';

var PlacementUpdate = (function (campaignName, bootstrapData) {

    describe('Update Placement', function() {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/media.po'),
            com = require('../page-object/utilities.po'),
            nav = require('../page-object/navigation.po'),
            navigate = require('../utilities/navigation.spec'),
            data = require('../utilities/mediaData'),
            util = require('../utilities/util'),
            placementName = bootstrapData.placements[1],
            updateData = {
                name: 'Protractor Updated Placement',
                site: 'Protractor Site',
                section: 'Home',
                size: '350x250',
                adSpend: '60000.00',
                rate: '500.00',
                rateType: 'CPL',
                startDate: '11/12/2017',
                endDate: '12/12/2025',
                record2StartDate: '12/13/2025',
                inventory: '120',
                margin: '50.00'
            },
            extendedProperties = {
                extProp1: 'Extended Properties 1',
                extProp2: 'Extended Properties 2',
                extProp3: 'Extended Properties 3',
                extProp4: 'Extended Properties 4',
                extProp5: 'Extended Properties 5'
            };

        it('should navigate to placement summary page', function() {
            navigate.placementSummary(campaignName, bootstrapData.io, placementName);
            expect(browser.getLocationAbsUrl()).toContain('/edit?from=placement-list');
        });

        it('should display validation messages placement name and have save button disabled ',function () {
            page.invalidNames.forEach(function(value, i){
                page.placementNameField.clear();
                page.placementNameField.sendKeys(value);
                expect(page.invalidPlacementName.isDisplayed()).toBe(true);
                expect(com.saveBtn.isEnabled()).toBe(false);
            });
        });

        it('should have save button enabled when placement name are special characters ',function () {
            expect(com.saveBtn.isEnabled()).toBe(false);
            page.placementNameField.clear();
            page.placementNameField.sendKeys('.~/?%&hello!+*-$#hi@:;{}how[]');
            expect(com.saveBtn.isEnabled()).toBe(true);
            page.placementNameField.clear();
        });

         it('should have save button disabled until an update is made', function() {
             expect(com.saveBtn.isEnabled()).toBe(false);
             page.placementNameField.clear();
             page.placementNameField.sendKeys(updateData.name);
             expect(page.placementNameField.getAttribute('value')).toContain(updateData.name);
             expect(com.saveBtn.isEnabled()).toBe(true);
         });

         it('should update placement cost', function() {
             //update first cost record
             page.summaryMargin.get(0).clear();
             page.summaryMargin.get(0).sendKeys(updateData.margin);
             page.summaryAdSpend.get(0).clear();
             page.summaryAdSpend.get(0).sendKeys(updateData.adSpend);
             page.summaryRate.get(0).clear();
             page.summaryRate.get(0).sendKeys(updateData.rate);
             page.summaryRateType.get(0).click();
             page.summaryRateType.get(0).element(by.cssContainingText('#rateTypeSelect option', updateData.rateType)).click();
             page.summaryStartDate.get(0).clear();
             page.summaryStartDate.get(0).sendKeys(updateData.startDate);
             //update start date of second cost record
             page.summaryStartDate.get(1).clear();
             page.summaryStartDate.get(1).sendKeys(updateData.record2StartDate);
             page.summaryEndDate.get(0).clear(); // move this above after this is fixed
             page.summaryEndDate.get(0).sendKeys(updateData.endDate);
             //verify values
             expect(page.summaryMargin.get(0).getAttribute('value')).toEqual(updateData.margin);
             expect(page.summaryAdSpend.get(0).getAttribute('value')).toEqual(updateData.adSpend);
             expect(page.summaryInventory.get(0).getText()).toEqual(updateData.inventory);
             expect(page.summaryRate.get(0).getAttribute('value')).toEqual(updateData.rate);
             expect(page.summaryRateType.get(0).getAttribute('value')).toEqual(updateData.rateType);
             expect(page.summaryStartDate.get(0).getAttribute('value')).toEqual(updateData.startDate);
             expect(page.summaryEndDate.get(0).getAttribute('value')).toEqual(updateData.endDate);
             expect(page.summaryStartDate.get(1).getAttribute('value')).toEqual(updateData.record2StartDate);
         });

         it('should save updates', function() {
             browser.actions().mouseMove(com.saveBtn).perform();
             com.saveBtn.click();
             expect(page.placementNameField.getAttribute('value')).toContain(updateData.name);
             expect(page.summarySite.getText()).toEqual(data.placementNoPkg.site);
             expect(page.summarySection.getText()).toEqual(data.placementNoPkg.section);
             expect(page.summaryMargin.get(0).getAttribute('value')).toEqual(updateData.margin);
             expect(page.summaryAdSpend.get(0).getAttribute('value')).toEqual(updateData.adSpend);
             expect(page.summaryInventory.get(0).getText()).toEqual(updateData.inventory);
             expect(page.summaryRate.get(0).getAttribute('value')).toEqual(updateData.rate);
             expect(page.summaryRateType.get(0).getAttribute('value')).toEqual(updateData.rateType);
             expect(page.summaryStartDate.get(0).getAttribute('value')).toEqual(updateData.startDate);
             expect(page.summaryEndDate.get(0).getAttribute('value')).toEqual(updateData.endDate);
             expect(page.summaryStartDate.get(1).getAttribute('value')).toEqual(updateData.record2StartDate);
         });

         it('should update the name back to the original', function() {
             page.placementNameInput.clear();
             page.placementNameInput.sendKeys(placementName);
             com.saveBtn.click();
             expect(com.saveBtn.isEnabled()).toBe(false);
         });

         it('should set the IO status to inactive when all placements are updated to inactive', function() {
             util.click(nav.ioSummaryTab);
             expect(page.ioStatusField.getAttribute('value')).toEqual('Accepted');
             util.click(nav.placementsTab);
             util.click(page.selectAll);
             util.click(page.deactivateBtn);
             util.click(page.warningOkBtn);
             util.click(com.saveBtn);
             util.click(nav.ioSummaryTab);
             expect(page.ioStatusField.getAttribute('value')).toEqual('Rejected');
             util.click(nav.placementsTab);
         });

         it('should set placements to active and update the IO Status', function() {
             var EC = protractor.ExpectedConditions;

             util.click(nav.placementsTab);
             page.placementRowByName(placementName).click();
             page.activateBtn.click();
             util.click(nav.ioSummaryTab);
             expect(page.confirmationDialog.isPresent()).toBe(true);
             page.confirmationCancelButton.click();
             browser.wait(EC.not(EC.presenceOf(page.confirmationCancelButton)), CONSTANTS.defaultWaitInterval);
             expect(page.confirmationDialog.isPresent()).toBe(false);
             com.saveBtn.click();
             nav.ioSummaryTab.click();
             expect(page.ioStatusField.getAttribute('value')).toEqual('Accepted');
         });

         it('should save placement extended properties', function () {
             navigate.placementSummary(campaignName, bootstrapData.io, placementName);
             util.click(nav.placementExtendedPropTab);
             page.extProp1.sendKeys(extendedProperties.extProp1);
             page.extProp2.sendKeys(extendedProperties.extProp2);
             util.click(page.ioEditSave);
             expect(page.extProp1.isDisplayed()).toBeTruthy();
             expect(page.ioEditSave.isEnabled()).toBe(false);
         });

         it('should cancel changes made on Extended Properties tab', function () {
             page.extProp3.sendKeys(extendedProperties.extProp3);
             page.extProp4.sendKeys(extendedProperties.extProp4);
             page.extProp5.sendKeys(extendedProperties.extProp5);
             util.click(page.ioEditCancel);
             page.warningOkBtn.click();
             expect(page.plusBtn.isDisplayed()).toBeTruthy();
         });

        it('should navigate to list of placements when \'List of Placement\' button is pressed', function () {
            navigate.placementSummary(campaignName, bootstrapData.io, placementName);
            util.click(page.listOfPlacementBtn);
            expect(page.plusBtn.isDisplayed()).toBeTruthy();
        });

        it('should save changes on Placement Summary tab', function () {
             page.placementRowByName(placementName).click();
             util.click(nav.editIcon);
             util.click(page.placementStatusSelect);
             page.placementStatusSelect.element(by.css('option:last-of-type')).click();
             util.click(page.warningOkBtn);
             util.click(page.ioEditSave);
             expect(com.saveBtn.isEnabled()).toBe(false);
         });

        it('should cancel changes made on Placement Summary tab', function () {
            page.placementNameInput.sendKeys('Test');
            util.click(page.ioEditCancel);
            page.warningOkBtn.click();
            expect(page.plusBtn.isDisplayed()).toBeTruthy();
        });
    });

});

module.exports = PlacementUpdate;


