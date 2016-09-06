'use strict';

var TagInjectionPage = function() {
    this.dragAndDropWithOffset = function (element, destiny, offsetX, offsetY) {
        browser.actions()
            .mouseMove(element)
            .mouseDown()
            .mouseMove(destiny)
            .mouseMove({x: offsetX, y: offsetY})
            .mouseUp()
            .perform();
    };
    this.columnNames = ["Tracking Tags"];
    this.trackingTagList = element(by.id('trackingTagList'));
    this.trackingTagRows = element.all(by.css('tr.actionable-row [data-title-text="Tracking Tags"]'));
    this.trackingTagToogleSearch = this.trackingTagList.element(by.id('toggleSearchCollapse'));
    this.associationTagsDirect = element.all(by.css('#directAssociations .label'));
    this.associationTagsInherited = element.all(by.css('#inheritedAssociations .label'));
    this.getAssociationTagRemoveButton = function(tagElement) {
        return tagElement.element(by.css('span'));
    };
    this.placementsTree = element(by.id('placementsTree'));
    this.placementDataRows = element.all(by.css('#placementsTree div.angular-ui-tree-handle')).filter(function(link) {
        return link.isDisplayed();
    });
    this.placementRowCollapse = element.all(by.css('#placementsTree div.te-tree-row > span.te-tree-expand'));
    this.placementRowExpand = element.all(by.css('#placementsTree div.te-tree-row > span.te-tree-collapse'));
    this.advertiserDropdown = element(by.model('vmTagInjectionTab.advertiser'));
    this.brandDropdown = element(by.model('vmTagInjectionTab.brand'));
    this.selectDropdown = function(dropDown, option) {
        return dropDown.element(by.css('option[label="' + option + '"]'));
    };
    this.selectDropdownByPosition = function ( dropDown, optionNum ) {
        return dropDown.element(by.css('select option:nth-child(' + optionNum + ')'));
    };

    this.trackingTagRowByName = function(tag) {
        return element(by.cssContainingText('tr.actionable-row [data-title-text="Tracking Tags"]', tag));
    };
    this.searchInputByGrid = function(parentEl){
        return parentEl.element(by.model('$table.searchTerm'));
    };
    this.editTrackingTagButton = element(by.css('[data-ng-click="vmTrackingTags.editTrackingTag()"]'));
    this.addTrackingTagButton = element(by.css('[data-te-table-btn-click="vmTrackingTags.addTrackingTag()"]'));
    this.htmlTagTypeStep = element(by.css('[data-ng-show="wizard.active(1)"]'));
    this.trackingTagNameStep = element(by.css('[data-ng-show="wizard.active(2)"]'));
    this.htmlTagTypeStepInactive = element(by.css('[data-ng-class="{\'active\':wizard.active(1)}"]'));
    this.trackingTagNameStepInactive = element(by.css('[data-ng-class="{\'active\':wizard.active(2)}"]'));
    this.trackingTagNamePrevious = element(by.css('[data-ng-click="wizard.go(1)"]'));
    this.htmlTagTypeNext = this.htmlTagTypeStep.element(by.css('[data-ng-click="vmAddTrackingTag.activateStep(vmAddTrackingTag.STEP.NAME); wizard.go(2)"]'));
    this.saveButton = this.trackingTagNameStep.element(by.css('[data-ng-click="vmAddTrackingTag.save()"]'));
    this.cancelButton = this.htmlTagTypeStep.element(by.css('[data-ng-click="vmAddTrackingTag.cancel()"]'));
    this.modalDialog = element(by.css('div.modal div.modal-dialog div.modal-content'));
    this.btnModalYes = element(by.css('.modal-content button[data-ng-click="yes()"]'));
    this.btnModalNo = element(by.css('.modal-content button[data-ng-click="no()"]'));
    this.htmlTagTypeDropdown = element(by.id('htmlTagTypeSelect'));
    this.domainDropdown = element(by.id('domainSelect'));
    this.trackingTagNameInput = element(by.id('trackingTagName'));
    this.htmlContentInput = element(by.id('tagContent'));
    this.htmlSecureContentInput = element(by.id('tagSecureContent'));
    this.optOutURLInput = element(by.id('optOutURL'));
    this.growlMessage = element(by.css('div.growl-message.ng-binding'));

    this.deleteTrackingTagButton = element(by.css('[data-te-table-btn-click="vmTrackingTags.deleteTrackingTag($selection)"]'));
    this.trackingTagsCheckboxes = element.all(by.css('[class="te-table-select-checkbox"]'));
    this.modalTitle = element(by.css('.modal-title'));
    this.cancelDialogButton = element(by.css('.modal-content button[data-ng-click="no()"]'));
    this.deleteDialogButton = element(by.css('.modal-content button[data-ng-click="yes()"]'));

    this.flyOutAssociationsTable = element(by.id('fly-out-associations'));
    this.flyOutTrackingTag = element(by.id('fly-out-tag-injection'));
    this.flyOutTrackingTagAssociations = this.flyOutTrackingTag.all(by.css('div.te-table tr.actionable-row'));
    this.flyOutTrackingTagElement = {
        tagName: element(by.id('tagName')),
        tagContent: element(by.id('tagContent')),
        tagSecureContent: element(by.id('tagSecureContent')),
        isActiveInput: element(by.id('isActiveSwitch')),
        tagContentInvalid: element(by.id('tagContentHtmlContent')),
        tagSecureContentInvalid: element(by.id('tagSecureContentHtmlContent')),
        cancelButton: element(by.css('[data-ng-click="vmTIFlyOut.close()"]')),
        saveButton: element(by.css('[data-ng-click="vmTIFlyOut.updateTagInjection()"]'))
    };

    this.teTree = {
        search: {
            input: element(by.id('search-te-tree')),
            button: element(by.id('search-custom-btn')),
            clear: element(by.css('[data-ng-click="vm.clearSearch(); vm.onClearSearchField();"]'))
        }
    };

    this.placementListAssociations = {
        id: element(by.id('fly-out-associations')),
        search: {
            button: element(by.css('[data-ng-click="$table.customSearch()"]')),
            clear: element(by.id('fly-out-associations')).element(by.model('$table.searchTerm'))
        }
    };

    this.goToTrackingTagView = element(by.id('tagInjectionFromCampaign'));
    this.listOfCampaignBtn = element(by.id('listOfCampaignBtn'));
    this.itemSelected = element(by.css('.te-tree-body li > .selected'));

    //Flyout Filtering
    this.filterDropdownByTable = function(parent, index) {
        return parent.all(by.css('.multiselect-parent')).get(index);
    };
    this.filterDropdownButtonByTable = function(parent, index) {
        return parent.all(by.css('.multiselect-parent div.btn.dropdown-toggle')).get(index);
    };
    this.filterSelectAllByTable = function(parent, filterIndex) {
        return this.filterDropdownByTable(parent, filterIndex).element(by.id('multicheckbox-select-all'));
    };
    this.filterClearSelectedByTable = function(parent, filterIndex) {
        return this.filterDropdownByTable(parent, filterIndex).element(by.id('multicheckbox-clear-selected'));
    };
    this.filterDropdownOptionByTable = function(parent, filterIndex, optionIndex) {
        return this.filterDropdownByTable(parent, filterIndex).all(by.css('li.multicheckbox-option a')).get(optionIndex);
    }
};

module.exports = new TagInjectionPage();
