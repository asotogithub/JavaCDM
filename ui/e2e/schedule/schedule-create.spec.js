'use strict';

var scheduleCreate = (function (campaignName) {

  describe('Schedule Create - Details', function() {
    var page = require('../page-object/schedule.po'),
        util = require('../page-object/utilities.po'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        constants = require('../utilities/constants');

    it('should navigate to Add Schedule Assigment modal', function() {
      navigate.newScheduleAssignment(campaignName);
    });

    it('should display available placements, available creatives and pending assignments', function() {
      expect(page.availPlacementsGrid.isDisplayed()).toBe(true);
      expect(page.availCreativesGrid.isDisplayed()).toBe(true);
      expect(page.pendingAssignSection.isDisplayed()).toBe(true);
    });

    it('should check all available placements and creatives', function() {
      expect(page.pendingAssignGrid.isDisplayed()).toBe(false);
      page.availPlacementsSelectAll.click();
      page.availCreativesSelectAll.click();
      expect(page.pendingAssignGrid.isDisplayed()).toBe(true);
    });

    it('should display pending schedule assigments in grid', function() {
      browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
      expect(page.pendingAssignValuesByHeader('Alias').count()).toBeGreaterThan(0);
    });

    it('should enable save button if there are changes', function() {
      expect(page.scheduleAssignmentSave.isEnabled()).toBe(true);
    });

    it('should be able to delete pending assignments', function() {
      expect(page.pendingAssignDelete.isEnabled()).toBe(false);
      var aliases = page.pendingAssignTextByHeader('Alias');
      page.pendingAssignValuesByHeader('Alias').map(function(element) {
        element.click();
      })
      expect(page.pendingAssignDelete.isEnabled()).toBe(true);
      page.pendingAssignDelete.click();
      var newAliases = page.pendingAssignTextByHeader('Alias');
      expect(aliases).not.toEqual(newAliases);
    });

    it('should close modal by clicking \'x\'', function() {
      var EC = protractor.ExpectedConditions;

      page.closeBtn.click();
      browser.wait(EC.not(EC.presenceOf(page.scheduleAssignmentModal)), constants.defaultWaitInterval);
      expect(page.scheduleAssignmentModal.isPresent()).toBe(false);
      nav.addBtn.click();
    });

    it('should be able to delete all pending assignments', function() {
      page.availPlacementsSelectAll.click();
      page.availCreativesSelectAll.click();
      expect(page.pendingAssignDelete.isEnabled()).toBe(false);
      expect(page.pendingAssignValuesByHeader('Alias').count()).toBeGreaterThan(0);
      page.pendingAssignSelectAll.click();
      expect(page.pendingAssignDelete.isEnabled()).toBe(true);
      page.pendingAssignDelete.click();
      expect(page.pendingAssignValuesByHeader('Alias').count()).toEqual(0);
    });

    it('should close modal by clicking the cancel button', function() {
      var EC = protractor.ExpectedConditions;

      page.cancelBtn.click();
      browser.wait(EC.not(EC.presenceOf(page.scheduleAssignmentModal)), constants.defaultWaitInterval);
      expect(page.scheduleAssignmentModal.isPresent()).toBe(false);
    });

  });
});

module.exports = scheduleCreate;


