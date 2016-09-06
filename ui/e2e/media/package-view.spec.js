'use strict';

var PackageView = (function (campaignName, ioName, packageName, placementName) {
    describe('Package List', function () {
        var page = require('../page-object/media.po'),
            nav = require('../page-object/navigation.po'),
            navigate = require('../utilities/navigation.spec'),
            utilPo = require('../page-object/utilities.po'),
            util = require('../utilities/util');

        it('should navigate to package grid from media view', function() {
            navigate.packageGrid(campaignName, ioName);
            expect(browser.getLocationAbsUrl()).toContain('/package/list');
            expect(page.packageList.grid.isDisplayed()).toBe(true);
            page.dataRows.then(function(rows){
                expect(rows.length).toBeGreaterThan(0);
            });
        });

        describe('Column headers', function() {
            page.columnNamesPackage.forEach(function(header, i) {
                if (header !== 'check') {
                    it('should sort ' + header + ' in ascending order', function () {
                        // Click on header to sort values in ascending order
                        utilPo.headerByName(header, '.te-tree-grid').click();
                        var unsorted = page.textToSort(i);
                        var sorted = page.sortedText(i);
                        expect(unsorted).toEqual(sorted);
                    });

                    it('should sort ' + header + ' in descending order', function () {
                        // Click on header to sort values in descending order
                        utilPo.headerByName(header, '.te-tree-grid').click();
                        var unsorted = page.textToSort(i);
                        var sorted = page.sortedText(i).then(function (sortedText) {
                            return sortedText.reverse();
                        });
                        expect(unsorted).toEqual(unsorted);
                    });
                }});
        });

        it('should expand Package rows', function() {
            page.dataRows.then(function(rows){
                var rowCount = rows.length;
                rows.forEach(function(){
                    util.click(page.packageList.ioExpand.get(0));
                });
                page.dataRows.then(function(endRowCount){
                    expect(endRowCount.length).toBeGreaterThan(rowCount);
                })
            });
        });

        it('should allow searching on IO Name and Placement', function() {
            page.searchInput.sendKeys(packageName);
            page.dataRows.then(function(rows){
                expect(rows.length).toBeGreaterThan(0);
            });
            page.searchInput.clear();
            page.searchInput.sendKeys(placementName);
            page.dataRows.then(function(rows){
                expect(rows.length).toBeGreaterThan(0);
            });
            page.searchClear.click();
            page.dataRows.then(function(rows){
                expect(rows.length).toBeGreaterThan(0);
            });
        });

    });

});

module.exports = PackageView;

