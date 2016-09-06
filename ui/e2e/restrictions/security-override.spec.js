'use strict';

var securityOverride = (function (bootstrapData, restrictionsBootstrap) {
    describe('Security Override', function() {
        var CONSTANTS = require('../utilities/constants'),
            campaignsPage = require('../page-object/campaigns.po'),
            navigationPage = require('../page-object/navigation.po'),
            navigate = require('../utilities/navigation.spec');

        it('should open campaign details', function() {
            browser.wait(function() {
                return global.restrictions.runRestrictions;
            });
            navigate.campaignDetails(bootstrapData.campaignNames[0]);
        });

        it('should redirect to 404 page', function() {
            restrictionsBootstrap.limitUser(true);

            browser.driver.wait(function() {
                return global.restrictions.limitUser;
            }).then(function(){
                navigationPage.scheduleTab.click();
                expect(browser.getLocationAbsUrl()).toContain('/404');
            });
        });

        it('should show zero campaigns', function() {
            navigationPage.goHome.click();
            expect(campaignsPage.dataRows.count()).toEqual(0);
        });

        it('should display campaigns when restriction is removed', function() {
            restrictionsBootstrap.limitUser(false);

            browser.driver.wait(function() {
                return !global.restrictions.limitUser;
            }).then(function(){
                browser.refresh();
                expect(campaignsPage.dataRows.count()).toBeGreaterThan(0);
            });
        });

        it('should go to campaign details page', function() {
            navigate.campaignDetails(bootstrapData.campaignNames[0]);
        });

        it('should redirect to 404 page after limit user and restrict advertiser', function() {
            restrictionsBootstrap.setAdvertiser();

            browser.driver.wait(function() {
                return global.restrictions.setAdvertisers;
            }).then(function(){
                restrictionsBootstrap.limitUser(true);

                browser.driver.wait(function() {
                    return global.restrictions.limitUser;
                }).then(function(){
                    navigationPage.scheduleTab.click();
                    expect(browser.getLocationAbsUrl()).toContain('/404');
                });
            });
        });

        it('should only show campaigns associated to advertisers that are not restricted', function() {
            navigationPage.goHome.click();
            expect(navigationPage.gridRowByName(bootstrapData.campaignNames[0]).isPresent()).toBe(false);
            expect(navigationPage.gridRowByName(bootstrapData.campaignNames[1]).isPresent()).toBe(true);
        });

        it('should display all campaigns', function() {
            restrictionsBootstrap.limitUser(false);

            browser.driver.wait(function() {
                return !global.restrictions.limitUser;
            }).then(function(){
                browser.refresh();
                expect(navigationPage.gridRowByName(bootstrapData.campaignNames[0]).isPresent()).toBe(true);
                expect(navigationPage.gridRowByName(bootstrapData.campaignNames[1]).isPresent()).toBe(true);
            });
        });

    });
});

module.exports = securityOverride;
