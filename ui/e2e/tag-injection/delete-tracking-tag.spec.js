'use strict';

var deleteTrackingTag = (function () {

    describe('Delete Tracking-Tag', function() {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/tag-injection.po'),
            nav = require('../page-object/navigation.po'),
            common = require('../utilities/common.spec'),
            util = require('../page-object/utilities.po'),
            rowsCount;

        it('should navigate to page and display tracking tag creation wizard', function() {
            nav.tagInjectionItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            page.trackingTagRows.then(function(rows){
                rowsCount = rows.length;
                expect(rows.length).toBeGreaterThan(0);
            });
        });

        it('should show confirmation dialog and cancel the deletion process', function() {
            page.addTrackingTagButton.click();
            page.selectDropdown(page.htmlTagTypeDropdown, 'Ad Choices (OBA)').click();
            page.htmlTagTypeNext.click();
            page.trackingTagNameInput.sendKeys('New Tag ' + common.getRandomString(10));
            page.optOutURLInput.sendKeys('http://www.google.com');
            page.saveButton.click();

            page.trackingTagToogleSearch.click();
            page.searchInputByGrid(page.trackingTagList).sendKeys('New Tag');
            page.trackingTagRows.get(0).click();
            page.deleteTrackingTagButton.click();
            expect(page.modalTitle.isPresent()).toBe(true);

            page.cancelDialogButton.click();
            expect(page.modalTitle.isDisplayed()).toBe(false);
            expect(page.trackingTagRows.count()).toEqual(1);
        });

        it('should delete a tracking tag', function() {
            page.searchInputByGrid(page.trackingTagList).sendKeys('New Tag');
            page.deleteTrackingTagButton.click();
            expect(page.modalTitle.isPresent()).toBe(true);

            page.deleteDialogButton.click();
            expect(page.modalTitle.isDisplayed()).toBe(false);
            expect(page.trackingTagRows.count()).toEqual(0);
        });

        it('should create two new tracking tags and make a bulk delete', function() {
            page.addTrackingTagButton.click();
            page.selectDropdown(page.htmlTagTypeDropdown, 'Ad Choices (OBA)').click();
            page.htmlTagTypeNext.click();
            page.trackingTagNameInput.sendKeys('New Tag ' + common.getRandomString(10));
            page.optOutURLInput.sendKeys('http://www.google.com');
            page.saveButton.click();

            page.addTrackingTagButton.click();
            page.selectDropdown(page.htmlTagTypeDropdown, 'Facebook Custom Tracking').click();
            page.htmlTagTypeNext.click();
            page.trackingTagNameInput.sendKeys('New Tag ' + common.getRandomString(10));
            page.selectDropdownByPosition(page.domainDropdown, 2).click();
            page.saveButton.click();

            page.trackingTagToogleSearch.click();
            page.searchInputByGrid(page.trackingTagList).sendKeys('New Tag');
            page.trackingTagRows.get(0).click();
            page.trackingTagRows.get(1).click();
            page.deleteTrackingTagButton.click();
            page.deleteDialogButton.click();
            page.searchInputByGrid(page.trackingTagList).clear();
            expect(page.trackingTagRows.count()).toEqual(3);
        });
    });
});

module.exports = deleteTrackingTag;
