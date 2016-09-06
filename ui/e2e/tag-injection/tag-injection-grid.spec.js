'use strict';

var tagInjectionGrid = (function () {

    describe('Tag Injection Grid', function() {
        var page = require('../page-object/tag-injection.po'),
            gridValidation = require('../utilities/grid-validation.spec'),
            gridColumnsSortValidation = require('../utilities/grid-validation-columns-sort.spec'),
            nav = require('../page-object/navigation.po'),
            util = require('../utilities/util');

        it('should navigate to page and display Tracking Tags', function() {
            browser.wait(function() {
                return global.runTagInjection;
            });

            nav.tagInjectionItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            page.placementDataRows.then(function(rows){
                expect(rows.length).toBeGreaterThan(0);
            });
        });

        it('should allow expand and collapse placements grid tree', function() {
            var expandIndex = 3,
                collapseIndex = expandIndex - 1;

            expect(page.searchInputByGrid(page.trackingTagList).isDisplayed()).toBe(false);
            page.trackingTagToogleSearch.click();
            expect(page.searchInputByGrid(page.trackingTagList).isDisplayed()).toBe(true);
            expect(page.placementDataRows.count()).toBe(1);
            for (var i = 0; i < expandIndex; i++) {
                util.click(page.placementRowExpand.get(0));
            }
            expect(page.placementDataRows.count()).toBeGreaterThan(expandIndex);
            for (var j = collapseIndex; j >= 0; j--) {
                util.click(page.placementRowCollapse.get(j));
            }
            expect(page.placementDataRows.count()).toBe(1);
        });

        it('should filter placements tree by campaign', function () {
            expect(page.placementDataRows.count()).toEqual(1);
            page.filterDropdownByTable(page.placementsTree, 0).click();
            page.filterDropdownOptionByTable(page.placementsTree, 0, 0).click();
            expect(page.placementDataRows.count()).toEqual(0);
            page.filterDropdownOptionByTable(page.placementsTree, 0, 0).click();
            expect(page.placementDataRows.count()).toEqual(1);
            page.filterClearSelectedByTable(page.placementsTree, 0).click();
            expect(page.placementDataRows.count()).toEqual(0);
            page.filterSelectAllByTable(page.placementsTree, 0).click();
            page.filterDropdownByTable(page.placementsTree, 0).click();
            expect(page.placementDataRows.count()).toEqual(1);
        });

        gridValidation('Tracking Tags', page.columnNames);
        gridColumnsSortValidation(page.columnNames);
    });
});

module.exports = tagInjectionGrid;
