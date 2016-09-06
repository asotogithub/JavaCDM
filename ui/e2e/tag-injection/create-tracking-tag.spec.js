'use strict';

var createTrackingTag = (function (newTagName) {

    describe('Create Tracking-Tag', function() {
        var page = require('../page-object/tag-injection.po'),
            nav = require('../page-object/navigation.po'),
            tagContent = '<SCRIPT SRC=\"http://www.google.com"></SCRIPT>',
            rowsCount;

        it('should navigate to page and display tracking tag creation wizard', function() {
            nav.tagInjectionItem.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            page.trackingTagRows.then(function(rows){
                rowsCount = rows.length;
                expect(rows.length).toBeGreaterThan(0);
            });

            page.addTrackingTagButton.click();
            expect(page.htmlTagTypeStep.isPresent()).toBe(true);
        });

        it('should select "Ad Choices (OBA)" and go to next step', function () {
            expect(page.htmlTagTypeDropdown.isPresent()).toBe(true);
            expect(page.htmlTagTypeStepInactive.isPresent()).toBe(true);

            page.selectDropdown(page.htmlTagTypeDropdown, 'Ad Choices (OBA)').click();
            expect(page.htmlTagTypeNext.isEnabled()).toBe(true);

            page.htmlTagTypeNext.click();
            expect(page.trackingTagNameStep.isPresent()).toBe(true);
            expect(page.trackingTagNameStepInactive.isPresent()).toBe(true);
        });

        it('should fill the fields and save button should enable', function () {
            expect(page.saveButton.getAttribute('class')).toContain('disabled');
            expect(page.trackingTagNameInput.isPresent()).toBe(true);
            expect(page.optOutURLInput.isPresent()).toBe(true);

            page.trackingTagNameInput.clear().sendKeys(newTagName);
            page.optOutURLInput.sendKeys('http://www.google.com');
            expect(page.saveButton.isEnabled()).toBe(true);
            expect(page.saveButton.getAttribute('class')).not.toContain('disabled');
        });

        it('should go to previous step and select "Custom" option', function () {
            page.trackingTagNamePrevious.click();
            expect(page.htmlTagTypeDropdown.isPresent()).toBe(true);

            page.selectDropdown(page.htmlTagTypeDropdown, 'Custom').click();
            page.htmlTagTypeNext.click();
            expect(page.trackingTagNameStep.isPresent()).toBe(true);
            expect(page.trackingTagNameInput.isPresent()).toBe(true);
            expect(page.htmlContentInput.isPresent()).toBe(true);
            expect(page.htmlSecureContentInput.isPresent()).toBe(true);
        });

        it('should have at least one valid html-content input filled', function () {
            page.trackingTagNameInput.clear().sendKeys(newTagName);
            expect(page.saveButton.getAttribute('class')).toContain('disabled');

            page.htmlContentInput.clear().sendKeys(tagContent);
            expect(page.saveButton.getAttribute('class')).not.toContain('disabled');

            page.htmlContentInput.clear();
            expect(page.saveButton.getAttribute('class')).toContain('disabled');

            page.htmlSecureContentInput.clear().sendKeys(tagContent);
            expect(page.saveButton.getAttribute('class')).not.toContain('disabled');
        });

        it('should go to previous step and select "Facebook Custom Tracking" option', function () {
            page.trackingTagNamePrevious.click();
            expect(page.htmlTagTypeDropdown.isPresent()).toBe(true);

            page.selectDropdown(page.htmlTagTypeDropdown, 'Facebook Custom Tracking').click();
            page.htmlTagTypeNext.click();
            expect(page.trackingTagNameStep.isPresent()).toBe(true);
            expect(page.trackingTagNameStepInactive.isPresent()).toBe(true);
        });

        it('should fill the fields and save button should enable', function () {
            expect(page.saveButton.getAttribute('class')).toContain('disabled');
            expect(page.trackingTagNameInput.isPresent()).toBe(true);
            expect(page.domainDropdown.isPresent()).toBe(true);

            page.trackingTagNameInput.clear().sendKeys(newTagName);
            page.selectDropdownByPosition(page.domainDropdown, 2).click();
            expect(page.saveButton.getAttribute('class')).not.toContain('disabled');
            expect(page.saveButton.isEnabled()).toBe(true);
        });

        it('should save the new created tracking-tag', function () {
            page.saveButton.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
            page.trackingTagToogleSearch.click();
            page.searchInputByGrid(page.trackingTagList).sendKeys(newTagName);
            expect(page.trackingTagRows.count()).toBe(1);
            page.searchInputByGrid(page.trackingTagList).clear();
            expect(page.trackingTagRows.count()).toBeGreaterThan(1);
        });

        it('should redirect to standard tab when hit cancel', function () {
            page.addTrackingTagButton.click();
            expect(page.htmlTagTypeStep.isPresent()).toBe(true);

            page.cancelButton.click();
            expect(browser.getLocationAbsUrl()).toContain('/tag-injection/standard');
        });
    });
});

module.exports = createTrackingTag;
