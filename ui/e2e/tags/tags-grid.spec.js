'use strict';

var tagsGrid = (function () {
    var CONSTANTS = require('../utilities/constants'),
        page = require('../page-object/tags.po'),
        gridValidation = require('../utilities/grid-validation.spec'),
        gridColumnsSortValidation = require('../utilities/grid-validation-columns-sort.spec'),
        nav = require('../page-object/navigation.po'),
        totalTagPlacement,
        filters = {
            advertiser: ['Mixed Targeting Advertiser', 'Select Advertiser'],
            brand: 'Mixed Targeting Brand'
        },
        searchText = 'AOL - Home - 1';

    describe('Tags - Placement Grid',function(){
        it('should navigate to page and display Tags - Placements', function() {
            nav.tagsItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tags');
            page.selectDropdown(page.advertiserDropdown, filters.advertiser[0]).click();
            page.selectDropdown(page.brandDropdown, filters.brand).click();
            totalTagPlacement = page.dataRows.count();
            expect(totalTagPlacement).toBeGreaterThan(0);
        });

        it('should display legend Table',function() {
            expect(page.legendTable.isDisplayed()).toBe(true);
        });

        it('should display legend total Tag - Placements',function() {
            page.selectDropdown(page.advertiserDropdown, filters.advertiser[1]).click();
            expect(page.legendTable.getText()).toEqual('0 Placements');
            page.selectDropdown(page.advertiserDropdown, filters.advertiser[0]).click();
            page.selectDropdown(page.brandDropdown, filters.brand).click();
            totalTagPlacement = page.dataRows.count();
            expect(totalTagPlacement).toBeGreaterThan(0);
        });
    });

    describe('Filtering options', function() {
        it('should filter by Campaign', function() {
            page.filterDropdown(CONSTANTS.AD_TAGS_FILTERS.CAMPAIGN).click();
            page.filterClearSelected(CONSTANTS.AD_TAGS_FILTERS.CAMPAIGN).click();
            expect(page.dataRows.count()).toEqual(0);
            page.filterSelectAll(CONSTANTS.AD_TAGS_FILTERS.CAMPAIGN).click();
            expect(page.dataRows.count()).toEqual(totalTagPlacement);
            page.filterDropdown(CONSTANTS.AD_TAGS_FILTERS.CAMPAIGN).click();
        });

        it('should filter by Site', function() {
            page.filterDropdown(CONSTANTS.AD_TAGS_FILTERS.SITE).click();
            page.filterClearSelected(CONSTANTS.AD_TAGS_FILTERS.SITE).click();
            expect(page.dataRows.count()).toEqual(0);
            page.filterSelectAll(CONSTANTS.AD_TAGS_FILTERS.SITE).click();
            expect(page.dataRows.count()).toEqual(totalTagPlacement);
            page.filterDropdown(CONSTANTS.AD_TAGS_FILTERS.SITE).click();
        });

        it('should filter by Trafficked', function() {
            page.filterDropdown(CONSTANTS.AD_TAGS_FILTERS.TRAFFICKED).click();
            page.filterClearSelected(CONSTANTS.AD_TAGS_FILTERS.TRAFFICKED).click();
            expect(page.dataRows.count()).toEqual(0);
            page.filterSelectAll(CONSTANTS.AD_TAGS_FILTERS.TRAFFICKED).click();
            expect(page.dataRows.count()).toEqual(totalTagPlacement);
            page.filterDropdown(CONSTANTS.AD_TAGS_FILTERS.TRAFFICKED).click();
        });

        it('should have send button disabled', function() {
            expect(page.sendBtn.isEnabled()).toBe(false);
        });

        it('should display legend with expected information after perfoming a search',function() {
            page.searchInputTagPlacement.sendKeys(searchText);
            expect(page.legendTable.getText()).toContain(page.dataRows.count());
            page.searchInputTagPlacement.clear();
        });

        it('should change advertiser and brand drop down',function() {
            page.selectDropdown(page.advertiserDropdown, filters.advertiser[1]).click();
            page.selectDropdown(page.advertiserDropdown, filters.advertiser[0]).click();
            page.selectDropdown(page.brandDropdown, filters.brand).click();
            expect(totalTagPlacement).toBeGreaterThan(0);
        });


    });
});

module.exports = tagsGrid;
