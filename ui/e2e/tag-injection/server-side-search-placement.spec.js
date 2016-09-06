'use strict';

var serverSideSearch = (function () {

    describe('Server Side Search', function() {
        var page = require('../page-object/tag-injection.po'),
            common = require('../utilities/common.spec'),
            nav = require('../page-object/navigation.po'),
            siteSearch = 'Protractor Site',
            sectionSearch = 'Home',
            placementSearch = 'Placement 01 - 350x250 - 01/01/2040 - 01/01/2041 (367 days)';

        it('should navigate to page and display Data on placement tree', function() {
            nav.tagInjectionItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            expect(page.placementDataRows.count()).toBe(1);
        });

        it('should search a Site row', function() {
            expect(page.placementDataRows.count()).toBe(1);
            page.teTree.search.input.sendKeys(siteSearch);
            page.teTree.search.button.click();
            expect(page.placementDataRows.count()).toBe(2);
            page.teTree.search.clear.click();
            expect(page.placementDataRows.count()).toBe(1);
        });

        it('should search a Section row', function() {
            expect(page.placementDataRows.count()).toBe(1);
            page.teTree.search.input.sendKeys(sectionSearch);
            page.teTree.search.button.click();
            expect(page.placementDataRows.count()).toBe(3);
            page.teTree.search.clear.click();
            expect(page.placementDataRows.count()).toBe(1);
        });

        it('should search a Placement row', function() {
            expect(page.placementDataRows.count()).toBe(1);
            page.teTree.search.input.sendKeys(placementSearch);
            page.teTree.search.button.click();
            expect(page.placementDataRows.count()).toBe(4);
            page.teTree.search.clear.click();
            expect(page.placementDataRows.count()).toBe(1);
        });
    });
});

module.exports = serverSideSearch;
