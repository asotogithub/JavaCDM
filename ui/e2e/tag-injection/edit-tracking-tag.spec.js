'use strict';

var editTrackingTag = (function (editTrackingTag) {

    describe('Edit Tracking-Tag', function() {
        var page = require('../page-object/tag-injection.po'),
            common = require('../utilities/common.spec'),
            nav = require('../page-object/navigation.po'),
            invalidMaxLengthTagName = '123456789012345678901234567890',
            validTagName = 'TagProtractor' + common.getRandomString(10),
            invalidTags = [
                'qwerty<>',
                '</>',
                '<tag/tag>',
                '123456789012345678901234567890',
                '<<>test<<>'
            ],
            validTag = [
                '<html>test</html>',
                '<html>',
                '</html>',
                'tagContent<html>',
                '<html/>'
            ],
            editValues = {
                tagName: 'EditTagName',
                content: '<html>test</html>',
                secureContent: '<html>test</html>'
            };

        it('should navigate to page and display Tracking Tags', function() {
            nav.tagInjectionItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            page.trackingTagRows.then(function(rows){
                expect(rows.length).toBeGreaterThan(0);
            });
        });

        it('should select tracking tag and Open Fly-Out', function() {
            expect(page.flyOutTrackingTag.isPresent()).toBe(false);
            expect(page.editTrackingTagButton.isEnabled()).toBe(false);
            page.trackingTagRows.get(0).click();
            page.trackingTagRows.get(1).click();
            page.trackingTagRows.get(2).click();
            expect(page.editTrackingTagButton.isEnabled()).toBe(false);
            page.trackingTagRows.get(0).click();
            page.trackingTagRows.get(1).click();
            page.trackingTagRows.get(2).click();
            page.trackingTagToogleSearch.click();
            page.searchInputByGrid(page.trackingTagList).sendKeys(editTrackingTag);
            page.trackingTagRowByName(editTrackingTag).click();
            expect(page.editTrackingTagButton.isEnabled()).toBe(true);
            page.editTrackingTagButton.click();
            expect(page.flyOutTrackingTag.isPresent()).toBe(true);
        });

        it('should edit Tag Name', function() {
            expect(page.flyOutTrackingTagElement.tagName.isPresent()).toBe(true);
            page.flyOutTrackingTagElement.tagName.clear().sendKeys(validTagName);
            expect(page.flyOutTrackingTagElement.saveButton.isEnabled()).toBe(true);
            page.flyOutTrackingTagElement.tagName.clear().sendKeys(invalidMaxLengthTagName);
            expect(page.flyOutTrackingTagElement.saveButton.isEnabled()).toBe(false);
            page.flyOutTrackingTagElement.tagName.clear().sendKeys(validTagName);
            expect(page.flyOutTrackingTagElement.saveButton.isEnabled()).toBe(true);
        });

        it('should display error message for invalid content tag', function() {
            for(var i=0; i<invalidTags.length; i++) {
                page.flyOutTrackingTagElement.tagContent.clear().sendKeys(invalidTags[i]);
                expect(page.flyOutTrackingTagElement.tagContentInvalid.isDisplayed()).toBeTruthy();
                expect(page.flyOutTrackingTagElement.saveButton.isEnabled()).toBeFalsy();
            }
        });

        it('should not display error message for valid content tag', function() {
            for(var i=0; i<validTag.length; i++) {
                page.flyOutTrackingTagElement.tagContent.clear().sendKeys(validTag[i]);
                expect(page.flyOutTrackingTagElement.tagContentInvalid.isPresent()).toBeFalsy();
                expect(page.flyOutTrackingTagElement.saveButton.isEnabled()).toBeTruthy();
            }
        });

        it('should display error message for invalid secure content tag', function() {
            for(var i=0; i<invalidTags.length; i++) {
                page.flyOutTrackingTagElement.tagSecureContent.clear().sendKeys(invalidTags[i]);
                expect(page.flyOutTrackingTagElement.tagSecureContentInvalid.isDisplayed()).toBeTruthy();
                expect(page.flyOutTrackingTagElement.saveButton.isEnabled()).toBeFalsy();
            }
        });

        it('should not display error message for valid secure content tag', function() {
            for(var i=0; i<validTag.length; i++) {
                page.flyOutTrackingTagElement.tagSecureContent.clear().sendKeys(validTag[i]);
                expect(page.flyOutTrackingTagElement.tagSecureContentInvalid.isPresent()).toBeFalsy();
                expect(page.flyOutTrackingTagElement.saveButton.isEnabled()).toBeTruthy();
            }
        });

        it('should filter tag associations list by campaign, site and placement', function () {
            var placementCount = 1,
                placementFilteredCount = 0;

            for (var i = 0; i < 3; i++) {
                expect(page.flyOutTrackingTagAssociations.count()).toEqual(placementCount);
                page.filterDropdownByTable(page.flyOutAssociationsTable, i).click();
                page.filterDropdownOptionByTable(page.flyOutAssociationsTable, i, 0).click();
                expect(page.flyOutTrackingTagAssociations.count()).toEqual(placementFilteredCount);
                page.filterDropdownOptionByTable(page.flyOutAssociationsTable, i, 0).click();
                expect(page.flyOutTrackingTagAssociations.count()).toEqual(placementCount);
                page.filterClearSelectedByTable(page.flyOutAssociationsTable, i).click();
                expect(page.flyOutTrackingTagAssociations.count()).toEqual(placementFilteredCount);
                for (var j = i + 1; j < 3; j++) {
                    expect(page.filterDropdownButtonByTable(page.flyOutAssociationsTable, j)
                        .getAttribute('disabled')).toBeTruthy();
                }
                page.filterSelectAllByTable(page.flyOutAssociationsTable, i).click();
                page.filterDropdownByTable(page.flyOutAssociationsTable, i).click();
                expect(page.flyOutTrackingTagAssociations.count()).toEqual(placementCount);
            }
        });

        it('should update the tracking tags', function() {
            page.flyOutTrackingTagElement.tagName.clear().sendKeys(editValues.tagName);
            page.flyOutTrackingTagElement.tagContent.clear().sendKeys(editValues.content);
            page.flyOutTrackingTagElement.tagSecureContent.clear().sendKeys(editValues.secureContent);
            page.flyOutTrackingTagElement.isActiveInput.click();
            page.flyOutTrackingTagElement.saveButton.click();
            expect(page.flyOutTrackingTag.isPresent()).toBe(false);
            expect(page.editTrackingTagButton.isEnabled()).toBe(true);
            page.editTrackingTagButton.click();
            expect(page.flyOutTrackingTag.isPresent()).toBe(true);
            expect(page.flyOutTrackingTagElement.tagName.getAttribute('value')).toEqual(editValues.tagName);
            expect(page.flyOutTrackingTagElement.tagContent.getAttribute('value')).toEqual(editValues.content);
            expect(page.flyOutTrackingTagElement.tagSecureContent.getAttribute('value')).toEqual(editValues.secureContent);
            page.flyOutTrackingTagElement.tagName.clear().sendKeys(editTrackingTag);
            page.flyOutTrackingTagElement.saveButton.click();
            expect(page.flyOutTrackingTag.isPresent()).toBe(false);
            nav.campaignsItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/campaigns');
        });
    });
});

module.exports = editTrackingTag;
