'use strict';

var tagAssociations = (function (newTagName) {

    describe('Tag Associations', function() {
        var nav = require('../page-object/navigation.po'),
            page = require('../page-object/tag-injection.po'),
            associatedTags,
            selectAdvertiser = 'Select Advertiser',
            selectBrand = 'Select Brand',
            constants = require('../utilities/constants'),
            util = require('../utilities/util'),
            EC = protractor.ExpectedConditions;

        it('should navigate to page and display Tracking Tags', function() {
            nav.tagInjectionItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            page.trackingTagRows.then(function(rows){
                expect(rows.length).toBeGreaterThan(0);
            });
        });

        it('should allow expand placements grid tree and show tags associations', function() {
            var expandIndex = 3;

            expect(page.placementDataRows.count()).toBe(1);
            for (var i = 0; i < expandIndex; i++) {
                util.click(page.placementRowExpand.get(0));
            }
            util.click(page.placementDataRows.get(0));
            expect(page.associationTagsDirect.count()).toBe(0);
            expect(page.associationTagsInherited.count()).toBe(0);
            util.click(page.placementDataRows.get(1));
            expect(page.associationTagsDirect.count()).toBe(0);
            expect(page.associationTagsInherited.count()).toBe(0);
            util.click(page.placementDataRows.get(2));
            expect(page.associationTagsDirect.count()).toBe(0);
            expect(page.associationTagsInherited.count()).toBe(0);
            util.click(page.placementDataRows.get(3));
            page.associationTagsDirect.count().then(function(associationCount) {
                associatedTags = associationCount;
                expect(page.associationTagsInherited.count()).toBe(0);
            });
        });

        it('should create a new tag association', function() {
            util.click(page.trackingTagToogleSearch);
            page.searchInputByGrid(page.trackingTagList).sendKeys(newTagName);
            expect(page.trackingTagRows.count()).toBe(1);
            page.dragAndDropWithOffset(page.trackingTagRows.get(0), page.placementDataRows.get(2), 25, 0);
            expect(page.associationTagsDirect.count()).toEqual(associatedTags + 1);
            util.click(page.placementDataRows.get(3));
            expect(page.associationTagsDirect.count()).toBe(0);
            expect(page.associationTagsInherited.count()).toEqual(associatedTags + 1);
            util.click(page.placementDataRows.get(4));
            expect(page.associationTagsDirect.count()).toBe(0);
            expect(page.associationTagsInherited.count()).toEqual(associatedTags + 1);
        });

        it('should auto-save the new tag association', function() {
            util.click(nav.campaignsItem);
            util.click(nav.tagInjectionItem);
            for (var i = 0; i < 3; i++) {
                util.click(page.placementRowExpand.get(0));
            }
            util.click(page.placementDataRows.get(2));
            expect(page.associationTagsDirect.count()).toBe(associatedTags + 1);
        });

        it('should delete an inherited association', function() {
            util.click(page.placementDataRows.get(4));
            expect(page.associationTagsInherited.count()).toEqual(1);
            util.click(page.getAssociationTagRemoveButton(page.associationTagsInherited.get(0)));
            expect(page.associationTagsInherited.count()).toEqual(0);
        });

        it('should delete a direct association', function() {
            util.click(page.placementDataRows.get(2));
            expect(page.associationTagsDirect.count()).toBe(associatedTags + 1);
            util.click(page.getAssociationTagRemoveButton(page.associationTagsDirect.get(0)));
            expect(page.associationTagsDirect.count()).toBe(associatedTags);
            util.click(page.placementDataRows.get(3));
            expect(page.associationTagsInherited.count()).toEqual(0);
        });

        it('should auto-save deleted associations', function() {
            util.click(nav.campaignsItem);
            util.click(nav.tagInjectionItem);
            expect(page.placementRowExpand.count()).toEqual(1);
            for (var i = 0; i < 3; i++) {
                util.click(page.placementRowExpand.get(0));
            }
            util.click(page.placementDataRows.get(2));
            expect(page.associationTagsDirect.count()).toEqual(0);
            util.click(page.placementDataRows.get(3));
            expect(page.associationTagsInherited.count()).toEqual(0);
            util.click(page.placementDataRows.get(4));
            expect(page.associationTagsInherited.count()).toEqual(0);
        });

        it('should associate the new tracking tag to a placement', function() {
            page.trackingTagToogleSearch.click();
            page.searchInputByGrid(page.trackingTagList).sendKeys(newTagName);
            page.dragAndDropWithOffset(page.trackingTagRows.get(0), page.placementDataRows.get(3), 25, 0);
            page.searchInputByGrid(page.trackingTagList).clear();
            page.trackingTagToogleSearch.click();
            util.click(page.placementDataRows.get(3));
            expect(page.associationTagsDirect.count()).toEqual(1);
        })
    });
});

module.exports = tagAssociations;
