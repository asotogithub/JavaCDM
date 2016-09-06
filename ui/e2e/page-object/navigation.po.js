'use strict';

var Navigation = function() {
  this.activeLink = element(by.css('.nav li.active'));
  this.addBtn = element(by.css('.fa-plus'));
  this.campaignDetailHeader = element(by.id('campaignsHeader'));
  this.campaignsHeader = element(by.cssContainingText('h3', 'Campaigns'));
  this.campaignsItem = element(by.cssContainingText('ul.nav span.ng-binding', 'Campaigns'));
  this.creativeGroupsHeader = element(by.id('title-page-creative-group'));
  this.creativeGroupTab = element(by.css('[id="creativeGroupsTab"] a'));
  this.campaignsRow = element.all(by.css('tr.actionable-row [data-title-text="Name"]'));
  this.campaignsSelector = element(by.css('.sidebar-subnav > li.active a'));
  this.campaignDataRows = element.all(by.css('tr.actionable-row'));
  this.creativeGroupsRow = element.all(by.css('tr[data-ng-repeat$="$data"] [data-title-text="Name"]'));
  this.creativeGroupDataRows = element.all(by.css('tr[data-ng-repeat$="$data"]'));
  this.creativeGroupDetailsUrl = '/campaigns/id/[-+]?[0-9]*\.?[0-9]*/creative-groups/id/[-+]?[0-9]*\.?[0-9]*/details';
  this.mediaDataRows = element.all(by.css('[role="row"]'));
  this.creativeRow = element.all(by.css('tr[data-ng-repeat="creative in $data"] [data-title-text="Alias"]'));
  this.creativeDataRows = element.all(by.css('tr[data-ng-repeat="creative in $data"]'));
  this.creativeEditButton = element(by.css('te-table-btns .btn-default'));
  this.siteMeasurementRow = element.all(by.css('tr[data-ng-repeat="siteMeasurement in $data"] [data-title-text="Name"]'));
  this.siteMeasurementDataRows = element.all(by.css('tr[data-ng-repeat="siteMeasurement in $data"]'));
  this.gridRowByName = function(name) {
      return element(by.cssContainingText('tr[data-ng-repeat$="$data"] [data-title-text="Name"]', name));
  };
  this.gridRowByFileName = function(fileName) {
      return element(by.cssContainingText('tr[data-ng-repeat$="$data"] [data-title-text="File Name"]', fileName));
  };
  this.mediaTab = element(by.id('insertionOrderListTab'));
  this.ioSummaryTab = element(by.css('#campaign-io-details-tab a'));
  this.placementsTab = element(by.css('#campaign-io-placements-tab a'));
  this.packageTab = element(by.css('#campaign-io-package-tab a'));
  this.ioName = element(by.id('creativeDetailsHeader'));
  this.ioEditButton = element(by.css('te-btns .btn-default'));
  this.creativeTab = element(by.css('[id="creativeListTab"] a'));
  this.scheduleTab = element(by.css('#scheduleTab a'));
  this.scheduleAssignmentModal = element(by.id('addScheduleAssignments'));
  this.permissionMsg = element(by.cssContainingText('p', 'Oops, It looks like you don\'t have permission to open that page!'));
  this.sideNavCollapse = element(by.css('.nav.navbar-nav .fa.fa-navicon'));
  this.siteMeasurementItem = element(by.cssContainingText('ul.nav span.ng-binding', 'Site Measurement'));
  this.logoutIcon = element(by.css('.fa-sign-out'));
  this.logout = element(by.id('logout'));
  this.userLogInName = element(by.id('userLoginName'));
  this.goHome = element(by.css('[data-ng-click="vm.goHome($event)"]'));

  this.tagInjectionItem = element(by.cssContainingText('ul.nav span.ng-binding', 'Tag Injection'));
  this.tagsItem = element(by.cssContainingText('ul.nav span.ng-binding', 'Tags'));

  //breadcrumbs
  this.breadcrumb = element(by.css('.breadcrumb'));
  this.getAllBreadcrumbLinks = element.all(by.css('.breadcrumb a'));
  this.breadcrumbCurrentPage = this.breadcrumb.element(by.css('span'));
  this.ioNameField = element(by.id('ioName'));
  this.ioSave = element(by.css('[data-ng-click="vmAddIO.save()"]'));

  this.ioRowByName = function(ioName) {
    return element(by.cssContainingText('[role="row"] td:nth-child(3)', ioName));
  };
  this.editIcon = element(by.css('.fa-pencil'));
  this.addCampaignBtn = element(by.css('[data-model="vm.campaigns"] .fa-plus'));
  this.admItem = element(by.cssContainingText('ul.nav span.ng-binding', 'ADM'));
  this.placementExtendedPropTab = element(by.id('placementExtendedPropTab'));
  this.placementSummaryTab = element(by.id('placementSummaryTab'));
};

module.exports = new Navigation();
