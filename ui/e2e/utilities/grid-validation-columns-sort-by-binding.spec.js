'use strict';

var gridValidationColumnsSortByBinding = (function (columnNames) {
    var page = require('../page-object/utilities.po');

    describe('Column headers', function () {
        columnNames.forEach(function (header) {
            it('should sort ' + header.name + ' in ascending order', function () {
                // Click on header to sort values in ascending order
                page.headerByName(header.name).click();
                var unsorted = page.getValuesByBinding(header.binding);
                var sorted = page.sortValuesByBinding(header.binding);
                expect(unsorted).toEqual(sorted);
            });

            it('should sort ' + header.name + ' in descending order', function () {
                // Click on header to sort values in descending order
                page.headerByName(header.name).click();
                var unsorted = page.getValuesByBinding(header.binding);
                var sorted = page.sortValuesByBinding(header.binding).then(function (sortedText) {
                    sortedText.slice().reverse();
                    return sortedText;
                });
                expect(unsorted).toEqual(sorted);
            });
        });
    });
});

module.exports = gridValidationColumnsSortByBinding;
