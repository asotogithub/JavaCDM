'use strict';

var SiteMeasurementsPage = function() {
    this.columnNames = ["Name","ID","Advertiser","Brand","Domain","Events","Status","Trafficked"];
    this.dataRows = element.all(by.css('tr[data-ng-repeat$="$data"]'));
    this.legend = element(by.id('legend'));
    this.addSiteMeasurementBtn = element(by.id('addSiteMeasurementBtn'));
    this.siteMeasurementTitle = element(by.id('title-page-site-measurements'));

    //Add Campaign
    this.addCampaignAdvertiserInput = element(by.id('advertiserSelect'));
    this.addCampaignBrandInput = element(by.id('brandSelect'));
    this.addCampaignCampaignDescriptionInput = element(by.id('campaignDescription'));
    this.addCampaignCancelButton = element(by.id('cancelBtn'));
    this.addCampaignCookieDomainInput = element(by.id('cookieDomain'));
    this.addCampaignNameInput = element(by.id('campaignName'));
    this.addCampaignNextButton = element(by.id('nextBtn'));
    this.addCampaignSaveButton = element(by.id('saveBtn'));
    this.addCampaignStatusInput = element(by.id('status'));
    this.addCampaignWizardStep1Panel = element(by.id('wizardStep1'));
    this.addCampaignWizardStep2Panel = element(by.id('wizardStep2'));

    // SM Details
    this.campaignsButton = element(by.id('listOfCampaignsBtn'));

    // SM Details - Summary Tab
    this.summaryContainer = element(by.id('smDetailsPanel'));
    this.summaryDescriptionInput = element(by.id('smDescription'));
    this.summarySaveButton = element(by.id('smDetailsSaveButton'));
    this.summaryCloseButton = element(by.id('smDetailsCloseButton'));

    // SM Events & Pings
    this.eventsPingsTab = element(by.id('smEventsTab'));
    this.eventsPingsContainer = element(by.id('smEventsPanel'));
    this.eventsPingslegend = element(by.id('eventPingCounter'));
    this.eventsPingsDataRows = element.all(by.css('tr[data-ng-repeat$="$data"]'));
    this.eventsEditButton = element(by.id('eventEditBtn'));
    this.eventsAddButton = element(by.id('eventAddBtn'));

    // SM Campaign Associations
    this.campaignAssociationsTab = element(by.id('smCampaignAssociationsTab'));
    this.campaignAssociationsAssignedRows = element.all(by.css('[data-model="outputModel"] tbody tr'));
    this.campaignAssociationsAssociateAllButton = element(by.css('[data-ng-click="addAllAssociation()"]'));
    this.campaignAssociationsDissociateAllButton = element(by.css('[data-ng-click="removeAllAssociation()"]'));
    this.campaignAssociationsSaveButton = element(by.id('saveBtn'));
    this.campaignAssociationsUnassignedRows = element.all(by.css('[data-model="inputModel"] tbody tr'));


    // Confirmation Dialog
    this.confirmationDialog = element(by.css('.modal-content'));
    this.confirmationDiscardButton = element(by.css('.modal-content button[data-ng-click="yes()"]'));
    this.confirmationCancelButton = element(by.css('.modal-content button[data-ng-click="no()"]'));

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
    };

    //Fly-out
    this.flyOutEventDropdown = element(by.id('smFlyOutEventTypeDropdown'));
    this.flyOutEventCloseIcon = element(by.id('expandoClose'));
    this.flyOutEventPingTable = element(by.id('smFlyOutPingTable'));
    this.flyOutEventSaveButtom = element(by.id('smFlyOutSaveButton'));
    this.flyOutEventSummaryPanel = element(by.id('fly-out-event-manage'));

    //Pings
    this.pingsAddButton = element(by.id('add-dropdown'));
    this.pingsAddBroadcastButton = element(by.id('addBroadcast'));
    this.pingsAddSelectiveButton = element(by.id('addSelective'));
    this.allPings = element.all(by.css('[name="vmPingbox.pingBoxForm"]'));
    this.confirmationDeleteButton = element(by.css('.modal-content button[data-ng-click="yes()"]'));
    this.pingTableSearchOptionBtn = element(by.css('#smFlyOutPingTable .fa.fa-search'));
    this.pingTableSearchInput = element(by.css('[data-ng-model="$tablePing.searchTerm"]'));

    this.pingBoxLabel = function(parentEl) {
        return parentEl.element(by.css('h4'));
    };

    this.deletePingButton = function(parentEl) {
        return parentEl.element(by.css('[data-ng-click="vmPingbox.deletePing()"]'));
    };

    this.editPingButton = function(parentEl) {
        return parentEl.element(by.css('[data-ng-click="vmPingbox.editPing()"]'));
    };

    this.savePingButton = function(parentEl) {
        return parentEl.element(by.css('[data-ng-click="vmPingbox.savePing()"]'));
    };

    this.descriptionPingInput = function(parentEl) {
        return parentEl.element(by.css('[data-ng-model="vmPingbox.model.description"]'));
    };
    this.checkUncheckSearchOptions = function(searchOption) {
        this.pingTableSearchOptionBtn.click();
        element.all(by.cssContainingText('#smFlyOutPingTable .dropdown-menu label',searchOption)).each(function(foundElem){
            foundElem.getText().then(function (text) {
                if(text === searchOption) {
                    foundElem.click();
                }
            });
        });
        return this.pingTableSearchOptionBtn.click();
    }

    //Add Event
    this.addEventCancelButton = element(by.id('cancelBtn'));
    this.addEventCreateGroupOption = element(by.id('createGroup'));
    this.addEventDescriptionInput = element(by.id('eventDescription'));
    this.addEventGroupNameErrorDuplicated = element(by.id('eventGroupNameError'));
    this.addEventGroupNameErrorInvalid = element(by.id('eventGroupNamePattern'));
    this.addEventGroupNameInput = element(by.id('eventGroupCreate'));
    this.addEventGroupNameSelect = element(by.id('eventGroupExisting'));
    this.addEventNameErrorDuplicated = element(by.id('eventNameError'));
    this.addEventNameErrorInvalid = element(by.id('eventNamePattern'));
    this.addEventNameInput = element(by.id('eventName'));
    this.addEventNextButton = element(by.id('nextBtn'));
    this.addEventSaveButton = element(by.id('saveBtn'));
    this.addEventTagTypeInput = element(by.id('eventTagType'));
    this.addEventTypeInput = element(by.id('eventType'));
    this.addEventWizardStep1Panel = element(by.id('wizardStep1'));
    this.addEventWizardStep2Panel = element(by.id('wizardStep2'));

    this.selectDropdown = function(dropDown, option) {
        return dropDown.element(by.css('option[label="' + option + '"]'));
    };
    this.selectDropdownByPosition = function (dropDown, optionNum) {
        return dropDown.element(by.css('option:nth-child(' + optionNum + ')'));
    };
};

module.exports = new SiteMeasurementsPage();
