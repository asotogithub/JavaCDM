'use strict';

var TagsPage = function() {
    this.dataRows = element.all(by.css('tr.actionable-row'));
    this.editBtn = element(by.id('tagsPlacementEditBtnId'));
    this.legendTable = element(by.id('legend'));
    this.tagsPlacementsGrid = element(by.id('tagsPlacementsGrid'));
    this.sendBtn = element(by.id('placementListSendBtn'));
    this.searchInputByGrid = function(parentEl){
        return parentEl.element(by.model('$table.searchTerm'));
    }
    this.searchInputTagPlacement= this.searchInputByGrid(this.tagsPlacementsGrid);
    this.advertiserDropdown = element(by.model('vmTagPlacement.filtersParents.advertiser'));
    this.brandDropdown = element(by.model('vmTagPlacement.filtersParents.brand'));

    this.selectDropdown = function(dropDown, option) {
        return dropDown.element(by.css('option[label="' + option + '"]'));
    };
    //Filtering
    this.filterDropdown = function(index) {
        return element.all(by.css('.multiselect-parent')).get(index);
    };
    this.filterSelectAll = function(filterIndex) {
        return this.filterDropdown(filterIndex).element(by.id('multicheckbox-select-all'));
    };
    this.filterClearSelected = function(filterIndex) {
        return this.filterDropdown(filterIndex).element(by.id('multicheckbox-clear-selected'));
    };
    this.filterDropdownOption = function(filterIndex, optionIndex) {
        return this.filterDropdown(filterIndex).all(by.css('li.multicheckbox-option a')).get(optionIndex);
    }

    // Ad Tags Send
    this.adTagsModal = element(by.id('sendAdTagsModal'));
    this.adTagsModalCloseButton = element(by.id('cancelButton'));
    this.adTagsModalRecipientsDataRows = element.all(by.css('[data-model="vmSendAdTags.recipientsList"] tbody tr'));
    this.adTagsModalRecipientsCheckedDataRows = element.all(
        by.css('[data-model="vmSendAdTags.recipientsList"] tbody tr [checked="checked"]'));
    this.adTagsModalClearAllLink = element(by.id('clearAll'));
    this.adTagsModalEmailList = element.all(by.css('[data-ng-model="vmSendAdTags.emailList"] li'));
    this.adTagsModalEmailListClose = element.all(by.css('[data-ng-model="vmSendAdTags.emailList"] li a'));
    this.adTagsModalEmailListInput = element.all(by.css('[data-ng-model="vmSendAdTags.emailList"] input'));
    this.adTagsModalFormatInput = element(by.model('vmSendAdTags.tagsList[site.siteId].format'));
    this.adTagsModalIndividualRecipientsInput = element.all(by.css('[data-ng-model="vmSendAdTags.tagsList[site.siteId].emailList"] input'));
    this.adTagsModalIndividualRecipientsList = element.all(by.css('[data-ng-model="vmSendAdTags.tagsList[site.siteId].emailList"] li'));
    this.adTagsModalSendButton = element(by.id('sendButton'));

    this.getAddTagsModalDataRowByEmail = function(email) {
        return element.all(by.css('[data-model="vmSendAdTags.recipientsList"] tbody tr [title="'+email+'"]'));
    };

    // Ad Tags Details
    this.adTagsDetails = element(by.id('fly-out-tag-details'));
    this.adTagsDetailsSendBtn = element(by.id('tagsDetailsSendBtnId'));
    this.adTagsDetailsCloseBtn = element(by.id('expandoClose'));
};

module.exports = new TagsPage();
