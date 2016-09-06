'use strict';

var CampaignsPage = function() {
  this.advertiserLabel = element(by.id('advertiserLabel'));
  this.advertiserField = element(by.id('advertiserName'));
  this.brandLabel = element(by.id('brandLabel'));
  this.brandField = element(by.id('brandName'));
  this.campaignDetailHeader = element(by.id('campaignsHeader'));
  this.campaignsHeader = element(by.cssContainingText('h3', 'Campaigns'));
  this.campaignsRow = element.all(by.css('tr.actionable-row [data-title-text="Name"]'));
  this.campaignRowByName = function(campaign) {
      return element(by.cssContainingText('tr.actionable-row [data-title-text="Name"]', campaign));
  }
  this.campaignIdField = element(by.id('campaignId'));
  this.campaignNameField = element(by.id('campaignName'));
  this.columnNames = ["Name","Advertiser","Brand","Domain","Status","Cost","Conversions","eCPA"];
  this.dataRows = element.all(by.css('tr.actionable-row'));
  this.detailsUrl = '/campaigns/id/[-+]?[0-9]*\.?[0-9]*/details';
  this.domainFieldReadOnly = element(by.id('domain'));
  this.domainField = element(by.id('domainSelect'));
  this.endDateField = element(by.id('endDate'));
  this.errorMsg = element(by.id('errorMessageText'));
  this.isActive = element(by.id('isActiveInput'));
  this.isActiveSwitch = element(by.css('#isActiveSwitch span'));
  this.detailsPageHeading = element(by.id('campaignsHeading'));
  this.requiredMsg = element(by.id('requiredMsg'));
  this.saveConfirmation = element(by.cssContainingText('div','The operation was completed successfully'));
  this.saveBtn = element(by.id('saveButton'));
  this.startDateField = element(by.id('startDate'));
  this.tooLongMsg = element(by.id('campaignNameTooLong'));

  //new campaign
  this.newCampaignName = element(by.id('name'));
  this.newCampaignAdvertiser = element(by.id('advertiserSelect'));
  this.newCampaignBrand = element(by.id('brandSelect'));
  this.nameStepDesc = element(by.css('[data-ng-class="{\'active\':wizard.active(1)}"]'));
  this.domainStepDesc = element(by.css('[data-ng-class="{\'active\':wizard.active(2)}"]'));
  this.datesStepDesc = element(by.css('[data-ng-class="{\'active\':wizard.active(3)}"]'));
  this.nameStep = element(by.css('[data-ng-show="wizard.active(1)"]'));
  this.domainStep = element(by.css('[data-ng-show="wizard.active(2)"]'));
  this.datesStep = element(by.css('[data-ng-show="wizard.active(3)"]'));
  this.newNameCancel = this.nameStep.element(by.css('[data-ng-click="vmCampaign.cancel()"]'));
  this.nameNext = this.nameStep.element(by.css('[data-ng-click="vmCampaign.activateStep(vmCampaign.STEP.DOMAIN); wizard.go(2)"]'));
  this.firstPartyDomain = element(by.id('firstPartyDomain'));
  this.thirdPartyDomain = element(by.id('thirdPartyDomain'));
  this.firstPartyDropdown = element(by.model('vm.campaign.domain'));
  this.selectDomainByName = function(domain) {
      return this.firstPartyDropdown.element(by.css('option[label="' + domain + '"]'));
  }
  this.domainNext = this.domainStep.element(by.css('[data-ng-click="vmCampaign.activateStep(vmCampaign.STEP.DATES_BUDGET); wizard.go(3)"]'));
  this.startDate = element(by.id('startDate'));
  this.endDate = element(by.id('endDate'));
  this.budget = element(by.id('budget'));
  this.createSave = element(by.css('[data-ng-click="vmCampaign.save()"]'));
  this.listOfCampaignsBtn = element(by.id('listOfCampaignsBtn'));

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
};

module.exports = new CampaignsPage();
