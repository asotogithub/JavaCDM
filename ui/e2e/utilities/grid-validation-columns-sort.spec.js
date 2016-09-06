'use strict';

var gridValidationColumnsSort = (function (columnNames) {
    var page = require('../page-object/utilities.po');

    describe('Column headers', function () {
        columnNames.forEach(function (header) {
            it('should sort ' + header + ' in ascending order', function () {
                // Click on header to sort values in ascending order
                page.headerByName(header).click();
                var unsorted = page.textToSort(header);
                var sorted = page.sortedText(header);
                expect(unsorted).toEqual(sorted);
            });

            it('should sort ' + header + ' in descending order', function () {
                // Click on header to sort values in descending order
                page.headerByName(header).click();
                var unsorted = page.textToSort(header);
                var sorted = page.sortedText(header).then(function (sortedText) {
                    return sortedText.reverse();
                });
                expect(unsorted).toEqual(sorted);
            });
        });
    });
});

module.exports = gridValidationColumnsSort;
