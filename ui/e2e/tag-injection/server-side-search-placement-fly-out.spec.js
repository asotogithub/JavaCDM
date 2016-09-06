'use strict';

var serverSideSearch = (function (editTrackingTag) {

    describe('Server Side Search On Fly Out', function() {
        var page = require('../page-object/tag-injection.po'),
            common = require('../utilities/common.spec'),
            nav = require('../page-object/navigation.po'),
            siteSearch = 'Protractor Site',
            sectionSearch = 'Home',
            placementSearch = 'Placement 01 - 350x250 - 01/01/2040 - 01/01/2041 (367 days)';

        it('should navigate to page and display Tracking Tags', function() {
            nav.tagInjectionItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            page.trackingTagRows.then(function(rows){
                expect(rows.length).toBeGreaterThan(0);
            });
        });

        it('should create a new tag association', function() {
            var expandIndex = 3;

            expect(page.placementDataRows.count()).toBe(1);
            for (var i = 0; i < expandIndex; i++) {
                page.placementRowExpand.get(0).click();
            }

            page.trackingTagToogleSearch.click();
            page.searchInputByGrid(page.trackingTagList).sendKeys(editTrackingTag);
            expect(page.trackingTagRows.count()).toBe(1);
            page.dragAndDropWithOffset(page.trackingTagRows.get(0), page.placementDataRows.get(2), 25, 0);
        });

        it('should select tracking tag and Open Fly-Out', function() {
            expect(page.flyOutTrackingTag.isPresent()).toBe(false);
            expect(page.editTrackingTagButton.isEnabled()).toBe(false);
            page.searchInputByGrid(page.trackingTagList).clear().sendKeys(editTrackingTag);
            page.trackingTagRowByName(editTrackingTag).click();
            expect(page.editTrackingTagButton.isEnabled()).toBe(true);
            page.editTrackingTagButton.click();
            expect(page.flyOutTrackingTag.isPresent()).toBe(true);
        });

        it('should press the enter key on search', function() {
            expect(page.flyOutTrackingTagAssociations.count()).toEqual(2);
            page.searchInputByGrid(page.placementListAssociations.id).sendKeys(protractor.Key.ENTER);
            expect(page.flyOutTrackingTagAssociations.count()).toEqual(2);
        });

        it('should search a placement associated', function() {
            expect(page.flyOutTrackingTagAssociations.count()).toEqual(2);
            page.searchInputByGrid(page.placementListAssociations.id).sendKeys('Placement 01');
            page.placementListAssociations.search.button.click();
            expect(page.flyOutTrackingTagAssociations.count()).toEqual(1);
            page.searchInputByGrid(page.placementListAssociations.id).clear();
            expect(page.flyOutTrackingTagAssociations.count()).toEqual(2);
        });

        it('should search a data that does not exist', function() {
            expect(page.flyOutTrackingTagAssociations.count()).toEqual(2);
            page.searchInputByGrid(page.placementListAssociations.id).sendKeys(editTrackingTag + '123456');
            page.placementListAssociations.search.button.click();
            expect(page.flyOutTrackingTagAssociations.count()).toEqual(0);
            page.flyOutTrackingTagElement.cancelButton.click();
            expect(page.flyOutTrackingTag.isPresent()).toBe(false);
            nav.campaignsItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/campaigns');
        });
    });
});

module.exports = serverSideSearch;
