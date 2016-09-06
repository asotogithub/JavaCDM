'use strict';

var PackagePlacementAssociation = (function (campaignName, ioName, packageName) {

    describe('Package - Placement Association', function () {
        var page = require('../page-object/media.po'),
            navigate = require('../utilities/navigation.spec'),
            common = require('../utilities/common.spec');

        it('should navigate to package summary page', function() {
            navigate.packageSummary(campaignName, ioName, packageName);
            expect(browser.getLocationAbsUrl()).toContain('/edit?from=placement-list');
        });

        it('should navigate to edit placement from package', function() {
            expect(page.placementAssociationsList.count()).toEqual(2);
            expect(browser.getLocationAbsUrl()).toContain('/edit?from=placement-list');
            page.placementAssociationsList.get(0).click();
            expect(browser.getLocationAbsUrl()).toContain('/edit?from=edit-package');
            expect(page.listOfPlacementBtn.getText()).toBe('Package Summary');
        });

        it('should disassociate placement from package', function() {
            expect(page.labelCostDetailsEmpty.isDisplayed()).toBe(true);
            expect(page.removeAssocPlcmntFromPackage.isDisplayed()).toBe(true);
            page.removeAssocPlcmntFromPackage.click();
            page.warningOkBtn.click();
            expect(page.labelCostDetailsEmpty.isDisplayed()).toBe(false);
            expect(page.removeAssocPlcmntFromPackage.isDisplayed()).toBe(false);
        });

        it('should navigate to package summary page', function() {
            expect(page.listOfPlacementBtn.getText()).toBe('Package Summary');
            page.listOfPlacementBtn.click();
            expect(browser.getLocationAbsUrl()).toContain('/edit?from=placement-list');
            expect(page.placementAssociationsList.count()).toEqual(1);
        });

        it('should disassociate second placement from package', function() {
            expect(page.placementAssociationsList.count()).toEqual(1);
            expect(browser.getLocationAbsUrl()).toContain('/edit?from=placement-list');
            page.placementAssociationsList.get(0).click();
            expect(browser.getLocationAbsUrl()).toContain('/edit?from=edit-package');
            expect(page.listOfPlacementBtn.getText()).toBe('Package Summary');
            expect(page.labelCostDetailsEmpty.isDisplayed()).toBe(true);
            expect(page.removeAssocPlcmntFromPackage.isDisplayed()).toBe(true);
            page.removeAssocPlcmntFromPackage.click();
            page.warningOkBtn.click();
            expect(page.labelCostDetailsEmpty.isDisplayed()).toBe(true);
            expect(page.removeAssocPlcmntFromPackage.isDisplayed()).toBe(true);
        });

    });

});

module.exports = PackagePlacementAssociation;

