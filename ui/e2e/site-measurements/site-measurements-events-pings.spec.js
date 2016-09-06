'use strict';

var siteMeasurementsEventsPings = (function () {

  describe('Site Measurements Events & Pings', function() {
      var CONSTANTS = require('../utilities/constants'),
          page = require('../page-object/site-measurements.po'),
          nav = require('../page-object/navigation.po'),
          totalEvents,
          totalPings,
          utilities = require('../page-object/utilities.po'),
          siteMeasurement = {
              campaign: 'Protractor SM 01',
              eventType: 'Measured',
              group: 'Protractor_G_01',
              invalidName: ' an invalid name',
              newName: ' aNewName',
              standardEvent: 'Protractor_SE_01',
              trueTagEvent: 'Protractor_TT_01',
              description: 'a description',
              searchList: ['Img', 'Tag'],
              ImgTypeCount: 1,
              TagTypeCount: 1
          };

      it('should navigate to Site Measurements page', function() {
          nav.siteMeasurementItem.click();
          expect(browser.getLocationAbsUrl()).toContain('/site-measurements');
      });

      describe('Events & Pings Tab', function() {
          it('should open Events & Pings tab', function() {
              expect(page.dataRows.count()).toBeGreaterThan(0);
              utilities.searchInput.sendKeys(siteMeasurement.campaign);
              expect(page.dataRows.count()).toBeGreaterThan(0);
              page.dataRows.get(0).click();
              expect(page.summaryContainer.isPresent()).toBe(true);
              page.eventsPingsTab.click();
              expect(page.eventsPingsContainer.isPresent()).toBe(true);
              expect(page.eventsPingslegend.isDisplayed()).toBe(true);
              totalEvents = page.eventsPingsDataRows.count();
              expect(totalEvents).toBeGreaterThan(0);
          });
      });

      describe('Filtering options', function() {
          it('should display legend with expected information after perfoming a search',function() {
              utilities.searchInput.sendKeys('Jason');
              page.eventsPingsDataRows.then(function(rows){
                  expect(page.eventsPingslegend.getText()).toContain(rows.length);
              });
              utilities.searchClear.click();
          });

          it('should filter by Event Type', function() {
              page.filterDropdown(CONSTANTS.SM_EVENTS_PINGS_FILTERS.EVENT_TYPE).click();
              page.filterClearSelected(CONSTANTS.SM_EVENTS_PINGS_FILTERS.EVENT_TYPE).click();
              expect(page.eventsPingsDataRows.count()).toEqual(0);
              page.filterSelectAll(CONSTANTS.SM_EVENTS_PINGS_FILTERS.EVENT_TYPE).click();
              expect(page.eventsPingsDataRows.count()).toEqual(totalEvents);
              page.filterDropdown(CONSTANTS.SM_EVENTS_PINGS_FILTERS.EVENT_TYPE).click();
          });

          it('should filter by Group', function() {
              page.filterDropdown(CONSTANTS.SM_EVENTS_PINGS_FILTERS.GROUP).click();
              page.filterClearSelected(CONSTANTS.SM_EVENTS_PINGS_FILTERS.GROUP).click();
              expect(page.eventsPingsDataRows.count()).toEqual(0);
              page.filterSelectAll(CONSTANTS.SM_EVENTS_PINGS_FILTERS.GROUP).click();
              expect(page.eventsPingsDataRows.count()).toEqual(totalEvents);
              page.filterDropdown(CONSTANTS.SM_EVENTS_PINGS_FILTERS.GROUP).click();
          });

          it('should filter by Tag Type', function() {
              page.filterDropdown(CONSTANTS.SM_EVENTS_PINGS_FILTERS.TAG_TYPE).click();
              page.filterClearSelected(CONSTANTS.SM_EVENTS_PINGS_FILTERS.TAG_TYPE).click();
              expect(page.eventsPingsDataRows.count()).toEqual(0);
              page.filterSelectAll(CONSTANTS.SM_EVENTS_PINGS_FILTERS.TAG_TYPE).click();
              expect(page.eventsPingsDataRows.count()).toEqual(totalEvents);
              page.filterDropdown(CONSTANTS.SM_EVENTS_PINGS_FILTERS.TAG_TYPE).click();
          });
      });

      describe('Fly-out', function() {
          it('should open Standard Event Fly-out',function() {
              utilities.searchInput.sendKeys(siteMeasurement.standardEvent);
              expect(page.dataRows.count()).toBeGreaterThan(0);
              page.dataRows.get(0).click();
              page.eventsEditButton.click();
              expect(page.flyOutEventSummaryPanel.isPresent()).toBe(true);
              expect(page.flyOutEventPingTable.isPresent()).toBe(false);
          });

          it('should enable save button when modify event type',function() {
              expect(page.flyOutEventSummaryPanel.isPresent()).toBe(true);
              expect(page.flyOutEventSaveButtom.isEnabled()).toBe(false);
              utilities.selectDropdown(page.flyOutEventDropdown, siteMeasurement.eventType).click();
              expect(page.flyOutEventSaveButtom.isEnabled()).toBe(true);
          });

          it('should discard changes and close Fly-out', function() {
              expect(page.flyOutEventSaveButtom.isEnabled()).toBe(true);
              page.flyOutEventCloseIcon.click();
              expect(page.confirmationDialog.isPresent()).toBe(true);
              page.confirmationDiscardButton.click();
              expect(page.flyOutEventSummaryPanel.isPresent()).toBe(false);
          });

          it('should open True Tag Event Fly-out',function() {
              expect(page.flyOutEventSummaryPanel.isPresent()).toBe(false);
              page.dataRows.get(0).click();
              utilities.searchClear.click();
              utilities.searchInput.sendKeys(siteMeasurement.trueTagEvent);
              expect(page.dataRows.count()).toBeGreaterThan(0);
              page.dataRows.get(0).click();
              expect(page.eventsEditButton.isEnabled()).toBe(true);
              page.eventsEditButton.click();
              expect(page.flyOutEventSummaryPanel.isPresent()).toBe(true);
              expect(page.flyOutEventPingTable.isPresent()).toBe(true);
          });

          it('should add a new Selective ping',function() {
              expect(page.pingsAddButton.isEnabled()).toBe(true);
              totalPings = page.allPings.count();
              expect(totalPings).toBeGreaterThan(0);
              page.pingsAddButton.click();
              page.pingsAddSelectiveButton.click();
              expect(page.allPings.count()).toBeGreaterThan(totalPings);
              expect(page.pingBoxLabel(page.allPings.get(0)).getText()).
                  toEqual(CONSTANTS.SM_EVENTS_PING_CARD.SELECTIVE);
          });

          it('should remove Selective ping',function() {
              var selectivePing = page.allPings.get(0);
              expect(page.deletePingButton(selectivePing).isEnabled()).toBe(true);
              page.deletePingButton(selectivePing).click();
              expect(page.confirmationDialog.isPresent()).toBe(true);
              page.confirmationDeleteButton.click();
              expect(page.allPings.count()).toBe(totalPings);
          });

          it('should add a new Broadcast ping',function() {
              expect(page.pingsAddButton.isEnabled()).toBe(true);
              page.pingsAddButton.click();
              page.pingsAddBroadcastButton.click();
              expect(page.allPings.count()).toBeGreaterThan(totalPings);
              expect(page.pingBoxLabel(page.allPings.get(0)).getText()).
                  toEqual(CONSTANTS.SM_EVENTS_PING_CARD.BROADCAST);
          });

          it('should display warning message when try close Fly-out', function() {
              expect(page.pingsAddButton.isEnabled()).toBe(false);
              page.flyOutEventCloseIcon.click();
              expect(page.confirmationDialog.isPresent()).toBe(true);
              page.confirmationCancelButton.click();
              expect(page.allPings.count()).toBeGreaterThan(totalPings);
          });

          it('should remove Broadcast ping',function() {
              var broadcastPing = page.allPings.get(0);
              expect(page.deletePingButton(broadcastPing).isEnabled()).toBe(true);
              page.deletePingButton(broadcastPing).click();
              expect(page.confirmationDialog.isPresent()).toBe(true);
              page.confirmationDeleteButton.click();
              expect(page.allPings.count()).toBe(totalPings);
          });

          it('should edit description field',function() {
              var pingCard = page.allPings.get(0);
              expect(page.editPingButton(pingCard).isPresent()).toBe(true);
              page.editPingButton(pingCard).click();
              expect(page.descriptionPingInput(pingCard).isEnabled()).toBe(true);
              expect(page.savePingButton(pingCard).isPresent()).toBe(true);
              page.descriptionPingInput(pingCard).clear();
              page.descriptionPingInput(pingCard).sendKeys(siteMeasurement.description);
              page.descriptionPingInput(pingCard).click();
              expect(page.savePingButton(pingCard).isPresent()).toBe(true);
          });

          it('should save when click outside of box',function() {
              var pingCard = page.allPings.get(0);
              expect(page.flyOutEventDropdown.isEnabled()).toBe(true);
              page.flyOutEventDropdown.click();
              expect(page.editPingButton(pingCard).isPresent()).toBe(true);
              expect(page.savePingButton(pingCard).isPresent()).toBe(false);
          });

          it('should search by tag type IMG',function() {
              expect(page.pingTableSearchOptionBtn.isEnabled()).toBe(true);
              page.checkUncheckSearchOptions(CONSTANTS.SM_EVENTS_PING_SEARCH_OPTIONS.PING);
              page.checkUncheckSearchOptions(CONSTANTS.SM_EVENTS_PING_SEARCH_OPTIONS.SITE);
              page.pingTableSearchInput.clear();
              page.pingTableSearchInput.sendKeys(siteMeasurement.searchList[0]);
              expect(page.allPings.count()).toBe(siteMeasurement.ImgTypeCount);
          });

          it('should search by tag type TAG',function() {
              page.pingTableSearchInput.clear();
              expect(page.allPings.count()).toBeGreaterThan(siteMeasurement.ImgTypeCount);
              page.pingTableSearchInput.sendKeys(siteMeasurement.searchList[0]);
              expect(page.allPings.count()).toBe(siteMeasurement.TagTypeCount);
          });

          it('should close Fly-out', function() {
              expect(page.flyOutEventSummaryPanel.isPresent()).toBe(true);
              page.flyOutEventCloseIcon.click();
              expect(page.flyOutEventSummaryPanel.isPresent()).toBe(false);
          });
      });

      describe('Add Event', function() {
          it('should open add event wizard and step 1', function() {
              page.eventsAddButton.click();
              expect(page.addEventWizardStep1Panel.isPresent()).toBe(true);
              expect(page.addEventCancelButton.isEnabled()).toBe(true);
          });

          it('should validate event name for the step 1', function() {
              page.addEventNameInput.sendKeys(siteMeasurement.standardEvent);
              page.addEventDescriptionInput.click();
              expect(page.addEventNameErrorDuplicated.isPresent()).toBe(true);
              page.addEventNameInput.clear();
              page.addEventNameInput.sendKeys(siteMeasurement.invalidName);
              page.addEventDescriptionInput.click();
              expect(page.addEventNameErrorInvalid.isPresent()).toBe(true);
              page.addEventNameInput.clear();
              page.addEventNameInput.sendKeys(siteMeasurement.newName);
              page.addEventDescriptionInput.click();
              expect(page.addEventNameErrorDuplicated.isPresent()).toBe(false);
              expect(page.addEventNameErrorInvalid.isPresent()).toBe(false);
          });

          it('should load event type into dropdown for the step 1',function() {
              page.selectDropdown(page.addEventTypeInput, CONSTANTS.SITE_MEASUREMENT_EVENT_TYPE.STANDARD).click();
              page.selectDropdown(page.addEventTypeInput, CONSTANTS.SITE_MEASUREMENT_EVENT_TYPE.TRU_TAG).click();
              page.addEventTypeInput.getText().then(function (text) {
                  var options = text.split(/\r\n|\r|\n/g).join('');

                  expect(options).toEqual(CONSTANTS.SITE_MEASUREMENT_EVENT_TYPE.SELECT +
                      CONSTANTS.SITE_MEASUREMENT_EVENT_TYPE.STANDARD +
                      CONSTANTS.SITE_MEASUREMENT_EVENT_TYPE.TRU_TAG);
              });
          });

          it('should load event tag type into dropdown for the step 1',function() {
              page.selectDropdown(page.addEventTagTypeInput, CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.CONVERSION).click();
              page.selectDropdown(page.addEventTagTypeInput, CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.CONVERSION_REVENUE).click();
              page.selectDropdown(page.addEventTagTypeInput, CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.MEASURED).click();
              page.selectDropdown(page.addEventTagTypeInput, CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.OTHER).click();
              page.addEventTagTypeInput.getText().then(function (text) {
                  var options = text.split(/\r\n|\r|\n/g).join('');

                  expect(options).toEqual(CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.SELECT +
                      CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.CONVERSION +
                      CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.CONVERSION_REVENUE +
                      CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.MEASURED +
                      CONSTANTS.SITE_MEASUREMENT_EVENT_TAG_TYPE.OTHER);
              });
              expect(page.addEventNextButton.isEnabled()).toBe(true);
          });

          it('should open event wizard\'s step 2', function() {
              page.addEventNextButton.click();
              expect(page.addEventWizardStep2Panel.isPresent()).toBe(true);
              expect(page.addEventCancelButton.isEnabled()).toBe(true);
          });

          it('should select existing group and enable save button for the step 2', function() {
              page.selectDropdown(page.addEventGroupNameSelect, siteMeasurement.group).click();
              expect(page.addEventSaveButton.isEnabled()).toBe(true);
          });

          it('should create a new group and enable save button for the step 2', function() {
              page.addEventCreateGroupOption.click();
              page.addEventGroupNameInput.sendKeys(siteMeasurement.group);
              page.addEventGroupNameInput.sendKeys(protractor.Key.TAB);
              expect(page.addEventGroupNameErrorDuplicated.isPresent()).toBe(true);
              page.addEventGroupNameInput.clear();
              page.addEventGroupNameInput.sendKeys(siteMeasurement.invalidName);
              page.addEventGroupNameInput.sendKeys(protractor.Key.TAB);
              expect(page.addEventGroupNameErrorInvalid.isPresent()).toBe(true);
              page.addEventGroupNameInput.clear();
              page.addEventGroupNameInput.sendKeys(siteMeasurement.newName);
              page.addEventGroupNameInput.sendKeys(protractor.Key.TAB);
              expect(page.addEventGroupNameErrorDuplicated.isPresent()).toBe(false);
              expect(page.addEventGroupNameErrorInvalid.isPresent()).toBe(false);
              expect(page.addEventSaveButton.isEnabled()).toBe(true);
          });

          it('should close add event wizard', function() {
              page.addEventCancelButton.click();
              expect(page.confirmationDialog.isPresent()).toBe(true);
              page.confirmationDiscardButton.click();
              expect(page.addEventWizardStep2Panel.isPresent()).toBe(false);
          });
      });
  });
});

module.exports = siteMeasurementsEventsPings;
