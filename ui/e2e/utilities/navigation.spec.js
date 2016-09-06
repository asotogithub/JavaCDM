'use strict';

var Navigation = function () {
  var CONSTANTS = require('./constants'),
      nav = require('../page-object/navigation.po'),
      media = require('../page-object/media.po'),
      util = require('./util'),
      schedule = require('../page-object/schedule.po');

  this.admSummary = function(fileName) {
    util.click(nav.admItem);
    util.click(nav.gridRowByFileName(fileName));
    expect(browser.getLocationAbsUrl()).toContain('/adm/id');
  };

  this.campaignDetails = function(campaignName) {
    util.click(nav.campaignsItem);
    util.click(nav.gridRowByName(campaignName));
    expect(nav.campaignDetailHeader.getText()).toContain(campaignName);
  };

  this.campaignGrid = function() {
    nav.campaignsItem.click();
    expect(browser.getLocationAbsUrl()).toContain('/campaigns');
  };

  this.creativeGroupDetails = function(campaignName, creativeGroupName) {
    this.campaignDetails(campaignName);
    nav.creativeGroupTab.click();
    util.click(nav.gridRowByName(creativeGroupName));
    util.click(nav.editIcon);
    expect(nav.creativeGroupsHeader.getText()).toContain(creativeGroupName);
  };

  this.mediaGrid = function(campaignName) {
    this.campaignDetails(campaignName);
    nav.mediaTab.click();
    expect(browser.getLocationAbsUrl()).toContain('/io/list');
  };

  this.mediaTrafficWarning = function(campaignName) {
     this.campaignDetailsTrafficWarning(campaignName);
  };

  this.campaignDetailsTrafficWarning = function(campaignName) {
     nav.campaignsItem.click();
  };

  this.newIO = function(campaignName) {
    this.mediaGrid(campaignName);
    util.click(nav.addBtn);
  };

  this.ioSummary = function(campaignName, ioName) {
    this.mediaGrid(campaignName);
    util.click(nav.ioRowByName(ioName));
    util.click(nav.editIcon);
  };

  this.newPlacement = function(campaignName, ioName) {
    this.mediaGrid(campaignName);
    nav.ioRowByName(ioName).click();
    nav.editIcon.click();
    nav.placementsTab.click();
    browser.actions().mouseMove(nav.addBtn).perform();
    nav.addBtn.click();
  };

  this.placementGrid = function(campaignName, ioName) {
    this.mediaGrid(campaignName);
    nav.ioRowByName(ioName).click();
    nav.editIcon.click();
    nav.placementsTab.click();
  };

  this.placementSummary = function(campaignName, ioName, placementName) {
    this.mediaGrid(campaignName);
    nav.ioRowByName(ioName).click();
    nav.editIcon.click();
    nav.placementsTab.click();
    util.click(media.placementRowByName(placementName));
    nav.editIcon.click();
  };

  this.packageGrid = function(campaignName, ioName) {
      this.mediaGrid(campaignName);
      nav.ioRowByName(ioName).click();
      nav.editIcon.click();
      nav.packageTab.click();
  };

  this.packageSummary = function(campaignName, ioName, packageName) {
    this.mediaGrid(campaignName);
    nav.ioRowByName(ioName).click();
    nav.editIcon.click();
    nav.placementsTab.click();
    media.packageRowByName(packageName).click();
    nav.editIcon.click();
  };

  this.scheduleGrid = function(campaignName) {
    var EC = protractor.ExpectedConditions;
    this.campaignDetails(campaignName);
    nav.scheduleTab.click();
    browser.wait(EC.visibilityOf(schedule.scheduleTab), 5000);
    expect(browser.getLocationAbsUrl()).toContain('/schedule');
  };

  this.newCreativeGroup = function(campaignName) {
    this.campaignDetails(campaignName);
    nav.creativeGroupTab.click();
    browser.actions().mouseMove(nav.addBtn).perform();
    nav.addBtn.click();
    expect(browser.getLocationAbsUrl()).toContain('/creative-groups/new');
  };

  this.newCreative = function(campaignName) {
    var EC = protractor.ExpectedConditions;

    this.campaignDetails(campaignName);
    nav.creativeTab.click();
    browser.wait(EC.elementToBeClickable(nav.addBtn), CONSTANTS.defaultWaitInterval);
    nav.addBtn.click();
    expect(browser.getLocationAbsUrl()).toContain('/creative/add');
  };

  this.creativeGrid = function(campaignName) {
    this.campaignDetails(campaignName);
    nav.creativeTab.click();
    expect(browser.getLocationAbsUrl()).toContain('/creative/list');
  };

  this.creativeGroupGrid = function(campaignName) {
    this.campaignDetails(campaignName);
    nav.creativeGroupTab.click();
    expect(browser.getLocationAbsUrl()).toContain('/creative-groups');
  };

  this.newCreativeGroupFirstCampaign = function() {
    this.firstCampaign();
    nav.creativeGroupTab.click();
    nav.addBtn.click();
    expect(browser.getLocationAbsUrl()).toContain('/creative-groups/new');
  };

  this.newScheduleAssignment = function(campaignName) {
    this.campaignDetails(campaignName);
    nav.scheduleTab.click();
    nav.addBtn.click();
    expect(nav.scheduleAssignmentModal.isPresent()).toBe(true);
  };

  this.newCampaign = function() {
    nav.campaignsItem.click()
    nav.addCampaignBtn.click();
    expect(browser.getLocationAbsUrl()).toContain('/campaigns/add-campaign');
  };

  this.firstCampaign = function() {
    nav.campaignsItem.click();
    var campaignName = nav.campaignsRow.get(0).getText();
    nav.campaignDataRows.get(0).click();
    return campaignName;
  };

  this.firstCreativeGroup = function() {
    var campaignName = this.firstCampaign();
    nav.creativeGroupTab.click();
    var creativeGroupName = nav.creativeGroupsRow.get(0).getText();
    nav.creativeGroupDataRows.get(0).click();
    nav.editIcon.click();
    return {campaignName:campaignName, creativeGroupName:creativeGroupName};
  };

  this.firstIO = function() {
    var campaignName = this.firstCampaign();
    nav.mediaTab.click();
    browser.wait(function(){
      return nav.mediaDataRows.isDisplayed();
    });
    nav.mediaDataRows.get(0).click();
    nav.ioEditButton.click();
    var ioName = media.ioName.getText();
    return {campaignName:campaignName, ioName:ioName};
  };

  this.firstCreative = function() {
    var campaignName = this.firstCampaign();
    nav.creativeTab.click();
    var creativeName = nav.creativeRow.get(0).getText();
    nav.creativeDataRows.get(0).click();
    nav.creativeEditButton.click();
    return {campaignName:campaignName, creativeName:creativeName};
  };

};

module.exports = new Navigation;


