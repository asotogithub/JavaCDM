'use strict';

var scheduleCreate = (function (campaignName) {

  describe('Create schedules and traffic', function() {
    var page = require('../page-object/schedule.po'),
        util = require('../utilities/util'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec');

    it('should add 2 schedules', function() {
      navigate.newScheduleAssignment(campaignName);
      expect(page.pendingAssignGrid.isDisplayed()).toBe(false);
      page.availPlacementsSelectAll.click();
      page.availCreativesSelectAll.click();
      expect(page.pendingAssignGrid.isDisplayed()).toBe(true);
      browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
      expect(page.pendingAssignValuesByHeader('Alias').count()).toEqual(2);
      page.scheduleAssignmentSave.click();
    });


    it('should expand all rows', function() {
      expect(page.scheduleDataRows.count()).toEqual(1);

      page.rowExpand.get(0).click();
      expect(page.scheduleDataRows.count()).toEqual(2);
      
      page.rowExpand.get(0).click();
      expect(page.scheduleDataRows.count()).toEqual(4);
      
      page.rowExpand.get(0).click();
      expect(page.scheduleDataRows.count()).toEqual(5);
      
      page.rowExpand.get(0).click();
      expect(page.scheduleDataRows.count()).toEqual(6);

      page.rowExpand.get(0).click();
      expect(page.scheduleDataRows.count()).toEqual(7);

      page.rowExpand.get(0).click();
      expect(page.scheduleDataRows.count()).toEqual(8);
    });

    it('should traffic', function() {
      util.click(page.trafficBtn);
      util.click(page.trafficConfirmBtn);
    });


  });
});

module.exports = scheduleCreate;


