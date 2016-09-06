'use strict';

var scheduleModal = (function (campaignName) {

  describe('Schedule Modal', function() {
    var CONSTANTS = require('../utilities/constants'),
        page = require('../page-object/schedule.po'),
        navigate = require('../utilities/navigation.spec'),
        scheduleDetail = {
          scheduleName: 'Protractor Site',
          creativeName: 'image-1-350x250'
        },
        clickThrough = 'http://www.update.com',
        originalClickThrough = 'http://www.amazon.com',
        constants = require('../utilities/constants');

    it('should navigate to schedule grid', function() {
      navigate.scheduleGrid(campaignName);
      expect(page.scheduleDataRows.count()).toBeGreaterThan(0);
    });

    it('should search and click on site ' + scheduleDetail.scheduleName + ' to display modal', function() {
      page.searchInputSchedule.clear();
      page.searchInputSchedule.sendKeys(scheduleDetail.scheduleName);
      page.searchButtonSchedule.click();
      page.scheduleDataRows.get(0).click();
      expect(page.modalTitle.isDisplayed()).toBe(true);
      expect(page.accordian.isDisplayed()).toBe(true);
      expect(page.expandLeft.isDisplayed()).toBe(true);
      expect(page.modalClose.isDisplayed()).toBe(true);
    });

    it('should flyout legend be displayed', function() {
      expect(page.modalTitle.isDisplayed()).toBe(true);
      expect(page.flyoutLegend.isDisplayed()).toBe(true);
      expect(page.modalClose.isDisplayed()).toBe(true);
    });

    it('should expand and minimize modal', function() {
      var EC = protractor.ExpectedConditions;
      page.expandLeft.click();
      expect(page.expandRight.isDisplayed()).toBe(true);
      browser.wait(EC.elementToBeClickable(page.expandRight), constants.defaultWaitInterval);
      page.expandRight.click();
      expect(page.expandLeft.isDisplayed()).toBe(true);
      browser.wait(EC.elementToBeClickable(page.expandLeft), constants.defaultWaitInterval);
    });

    it('should expand all accordian sections', function() {
      var EC = protractor.ExpectedConditions;
      page.sectionOne.click();
      browser.wait(EC.invisibilityOf(page.sectionTwoData), constants.defaultWaitInterval);
      expect(page.sectionOneData.isDisplayed()).toBe(true);
      expect(page.sectionTwoData.isDisplayed()).toBe(false);
      page.sectionTwo.click();
      browser.wait(EC.invisibilityOf(page.sectionOneData), constants.defaultWaitInterval);
      expect(page.sectionOneData.isDisplayed()).toBe(false);
      expect(page.sectionTwoData.isDisplayed()).toBe(true);
    });

    it('should close modal', function() {
      var EC = protractor.ExpectedConditions;

      page.modalClose.click();
      browser.wait(EC.not(EC.visibilityOf(page.modalTitle)), constants.defaultWaitInterval);
      expect(page.modalTitle.isDisplayed()).toBe(false);
    });

    it('should display delete icon and allow checking checkboxes', function() {
      page.searchInputSchedule.clear();
      browser.actions().mouseMove(page.scheduleDataRows.get(0)).perform();
      page.scheduleDataRows.get(0).click();
      expect(page.sectionTwoData.isDisplayed()).toBe(true);

      //check that delete button displays and is disabled
      expect(page.flyoutDelete.isDisplayed()).toBe(true);
      expect(page.flyoutDelete.isEnabled()).toBe(false);

      //count checkboxes
      expect( page.flyoutCheckboxes.count()).toEqual(1);

      //expand site, select checkbox on site and count checkboxes
      page.rowExpand.get(0).click();
      page.flyoutCheckboxes.get(0).click();
      expect( page.flyoutCheckboxes.count()).toEqual(2);

      //expand more rows and count checkboxes
      page.rowExpand.get(0).click();
      page.rowExpand.get(0).click();
      page.rowExpand.get(0).click();
      expect( page.flyoutCheckboxes.count()).toEqual(8);

      //count selected checkboxes
      expect( page.flyoutCheckboxes.isSelected().count()).toEqual(8);

      //delete button should be enabled at this point
      expect(page.flyoutDelete.isEnabled()).toBe(true);
    });

    it('should filter on creative ' + scheduleDetail.creativeName, function() {
      expect(page.flyoutDataRows.count()).toEqual(8);
      page.expandLeft.click();
      page.searchInputFlyout.clear();
      page.searchInputFlyout.sendKeys(scheduleDetail.creativeName);
      page.searchInputFlyout.sendKeys(protractor.Key.ENTER);
      expect(page.flyoutDataRows.count()).toEqual(5);
    });

    it('should close modal when cancel is clicked', function() {
      var EC = protractor.ExpectedConditions;

      page.flyoutCancelBtn.click();
      browser.wait(EC.not(EC.visibilityOf(page.modalTitle)), constants.defaultWaitInterval);
      expect(page.modalTitle.isDisplayed()).toBe(false);
    });

  });
});

module.exports = scheduleModal;


