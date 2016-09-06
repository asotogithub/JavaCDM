'use strict';

var gridValidation = (function (columnSortDefault, columnNames) {
    describe('Grid', function () {
        var page = require('../page-object/utilities.po'),
            util = require('../utilities/util');

        it('should display search field and search button', function () {
            expect(page.searchBtn.isDisplayed()).toBe(true);
            expect(page.searchInput.isDisplayed()).toBe(true);
            page.searchBtn.click();
            expect(page.searchDropdown.isDisplayed()).toBe(true);
            page.searchBtn.click();
            expect(page.searchDropdown.isDisplayed()).toBe(false);
        });

        it('should allow searching on ' + columnSortDefault, function () {
            var searchValue = page.getDataByRowColumn(0, columnSortDefault);
            var rowCount = page.dataRows.count();
            page.searchInput.sendKeys(searchValue);
            expect(page.searchClear.isDisplayed()).toBe(true);
            expect(page.getDataByRowColumn(0, columnSortDefault).getText()).toEqual(searchValue);
            expect(page.dataRows.count()).toBeGreaterThan(0)
            var dataValues = page.getAllDataByColumn(columnSortDefault);
            for (var i = 0; i < dataValues.length; i++) {
                expect(dataValues[i]).toContain(searchValue);
            }
            util.click(page.searchClear);
            expect(page.searchClear.isPresent()).toBe(false);
            expect(page.dataRows.count()).toBe(rowCount);
        });

        it('should display grid with correct columns', function () {
            page.columnHeaders.map(function (item) {
                return item.getText();
            }).then(function (labels) {
                if (columnNames[0].name) {
                    for (var i = 0; i < columnNames.length; i++) {
                        expect(labels[i]).toEqual(columnNames[i].name);
                    }
                }
                else {
                    expect(labels).toEqual(columnNames);
                }
            });
        });

        it('should be sorted alphabetically by ' + columnSortDefault, function () {
            var unsorted = page.textToSort(columnSortDefault);
            var sorted = page.sortedText(columnSortDefault);
            expect(unsorted).toEqual(sorted);

            //Sort Name column by descending order
            page.headerByName(columnSortDefault).click();
        });
    });
});

module.exports = gridValidation;
