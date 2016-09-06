'use strict';

var siteMeasurementsCampaignAssociations = (function () {

  describe('Site Measurements Campaign Associations', function() {
      var CONSTANTS = require('../utilities/constants'),
          page = require('../page-object/site-measurements.po'),
          nav = require('../page-object/navigation.po'),
          utilities = require('../page-object/utilities.po'),
          siteMeasurementCampaign = 'Protractor SM 01',
          totalAssociatedCampaigns,
          totalUnassociatedCampaigns;

      it('should navigate to Site Measurements page', function() {
          nav.siteMeasurementItem.click();
          expect(browser.getLocationAbsUrl()).toContain('/site-measurements');
      });

      describe('Campaign Associations Tab', function() {
          it('should open Campaign Associations tab', function() {
              expect(page.dataRows.count()).toBeGreaterThan(0);
              utilities.searchInput.sendKeys(siteMeasurementCampaign);
              expect(page.dataRows.count()).toBeGreaterThan(0);
              page.dataRows.get(0).click();
              expect(page.summaryContainer.isPresent()).toBe(true);
              page.campaignAssociationsTab.click();
              expect(page.campaignAssociationsAssociateAllButton.isEnabled()).toBe(true);

              totalAssociatedCampaigns = page.campaignAssociationsAssignedRows.count();
              expect(totalAssociatedCampaigns).toBeGreaterThan(0);
              totalUnassociatedCampaigns = page.campaignAssociationsUnassignedRows.count();
              expect(totalUnassociatedCampaigns).toBeGreaterThan(0);
          });
      });

      describe('Filtering options', function() {
          it('should filter by Status for unassociated campaigns', function() {
              page.filterDropdown(CONSTANTS.SM_CAMPAIGN_ASSOCIATIONS_FILTERS.STATUS_UNASSOCIATED).click();
              page.filterClearSelected(CONSTANTS.SM_CAMPAIGN_ASSOCIATIONS_FILTERS.STATUS_UNASSOCIATED).click();
              expect(page.campaignAssociationsUnassignedRows.count()).toEqual(0);
              page.filterSelectAll(CONSTANTS.SM_CAMPAIGN_ASSOCIATIONS_FILTERS.STATUS_UNASSOCIATED).click();
              expect(page.campaignAssociationsUnassignedRows.count()).toEqual(totalUnassociatedCampaigns);
              page.filterDropdown(CONSTANTS.SM_CAMPAIGN_ASSOCIATIONS_FILTERS.STATUS_UNASSOCIATED).click();
          });

          it('should filter by Status for associated campaigns', function() {
              page.filterDropdown(CONSTANTS.SM_CAMPAIGN_ASSOCIATIONS_FILTERS.STATUS_ASSOCIATED).click();
              page.filterClearSelected(CONSTANTS.SM_CAMPAIGN_ASSOCIATIONS_FILTERS.STATUS_ASSOCIATED).click();
              expect(page.campaignAssociationsAssignedRows.count()).toEqual(0);
              page.filterSelectAll(CONSTANTS.SM_CAMPAIGN_ASSOCIATIONS_FILTERS.STATUS_ASSOCIATED).click();
              expect(page.campaignAssociationsAssignedRows.count()).toEqual(totalAssociatedCampaigns);
              page.filterDropdown(CONSTANTS.SM_CAMPAIGN_ASSOCIATIONS_FILTERS.STATUS_ASSOCIATED).click();
          });
      });

      describe('Campaign Associations', function() {
          it('should associate campaign', function() {
              expect(page.campaignAssociationsSaveButton.isEnabled()).toBe(false);
              page.campaignAssociationsAssociateAllButton.click();
              expect(page.campaignAssociationsSaveButton.isEnabled()).toBe(true);
              page.campaignAssociationsDissociateAllButton.click();
              expect(page.campaignAssociationsSaveButton.isEnabled()).toBe(true);
          });
      });
  });
});

module.exports = siteMeasurementsCampaignAssociations;
