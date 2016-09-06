'use strict';

var adm = (function () {
    var page = require('../page-object/adm.po'),
        gridValidation = require('../utilities/grid-validation.spec'),
        gridColumnsSortByBindingValidation = require('../utilities/grid-validation-columns-sort-by-binding.spec'),
        nav = require('../page-object/navigation.po');

    describe('ADM Grid', function () {
        it('should navigate to ADM grid and display records', function () {
            nav.admItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/adm');
            expect(page.dataRows.count()).toBeGreaterThan(0);
        });

        gridValidation('Advertiser', page.columnNames);
        gridColumnsSortByBindingValidation(page.columnNames);
    });
});

module.exports = adm;
