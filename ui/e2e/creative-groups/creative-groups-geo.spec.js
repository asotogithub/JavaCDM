'use strict';

/*
All expects and tests that deal with the include and exclude buttons have been commented out due to US5485.
When the stories that allow anti-targeting are complete for the ad server and trafficking, we will be re-enabling
this functionality.
 */

var CreativeGroupsDetailGeo = (function () {

    describe('GeoTargeting', function() {

        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/creative-groups.po'),
            com = require('../page-object/utilities.po'),
            common = require('../utilities/common.spec'),
            nav = require('../page-object/navigation.po'),
            navigate = require('../utilities/navigation.spec'),
            campaignName = 'Protractor Campaign',
            creativeGroupName = new Date().getTime() + 'Protractor Geo Targeting Group';

        it('should display creative group geo targeting', function() {
            navigate.newCreativeGroup(campaignName);
        });

        it('should activate geo type tab when switching tabs', function() {
            expect(page.geoTab.isDisplayed()).toBe(true);
            page.geoTab.click();
            expect(page.countryTab.isDisplayed()).toBe(true);
            expect(page.countryTab.getAttribute('class')).toContain('active');
            //expect(page.includeCountryBtn.isEnabled()).toBe(false);
            //expect(page.excludeCountryBtn.isEnabled()).toBe(false);
            expect(page.countriesSearchInput.isEnabled()).toBe(false);
        });

        it('should enable geo targeting', function() {
            page.geoTab.click();
            expect(page.geoTarget.isEnabled()).toBe(true);
            expect(page.geoTarget.isSelected()).toBe(false);
            page.geoTargetSwitch.click();
            expect(page.geoTarget.isSelected()).toBe(true);
            //expect(page.includeCountryBtn.isEnabled()).toBe(true);
            //expect(page.excludeCountryBtn.isEnabled()).toBe(true);
            expect(page.countriesSearchInput.isEnabled()).toBe(true);
        });

        it('should display all fields on country tab', function() {
            page.countryTab.click();
            //expect(page.includeCountryBtn.isSelected()).toBe(true);
            //expect(page.excludeCountryBtn.isSelected()).toBe(false);
            expect(page.countryGrid.isEnabled()).toBe(true);
            expect(page.countryGridRows.count()).toBeGreaterThan(0);
            expect(page.countriesSearchInput.isDisplayed()).toBe(true);
            expect(page.countriesSummary.isDisplayed()).toBe(true);
            expect(page.stateSummary.isDisplayed()).toBe(true);
            expect(page.dmasSummary.isDisplayed()).toBe(true);
            expect(page.zipCodesSummary.isDisplayed()).toBe(true);
            expect(page.countriesSearchInput.isDisplayed()).toBe(true);
        });

        it('should select all/deselect all on country tab', function() {
            page.countriesSelectAllBtn.click();
            expect(page.countriesSummaryValues.count()).toEqual(CONSTANTS.CREATIVE_GROUPS.GEO.NUMBER_OF_COUNTRIES);
            page.countriesSelectAllBtn.click();
            expect(page.countriesSummaryValues.count()).toEqual(0);
        });

        it('should allow country search, select and deselect', function() {
            browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
            page.countriesSearchInput.sendKeys('republic of');
            expect(page.countryGridRows.count()).toEqual(6);
            page.countriesSelectAllBtn.click();
            expect(page.countriesSummaryValues.count()).toEqual(6)
            page.countriesSearchInput.clear();
            page.countriesSearchInput.sendKeys('cook island');
            page.countriesSelectAllBtn.click();
            expect(page.countriesSummaryValues.count()).toEqual(7);
            page.countryGridRows.get(0).click();
            expect(page.countriesSummaryValues.count()).toEqual(6);
            page.countriesSummaryRemoveByName('Moldova, Republic of').click();
            expect(page.countriesSummaryValues.count()).toEqual(5);
        });

        //it('should allow include or exclude of countries', function() {
        //    expect(page.countriesSummaryValues.get(0).getAttribute('class')).toContain('label-success');
        //    page.excludeCountryBtn.click();
        //    expect(page.countriesSummaryValues.get(0).getAttribute('class')).toContain('label-danger');
        //});

        it('should display all fields on state tab', function() {
            page.stateTab.click();
            //expect(page.includeStateBtn.isSelected()).toBe(true);
            //expect(page.excludeStateBtn.isSelected()).toBe(false);
            expect(page.stateGrid.isEnabled()).toBe(true);
            expect(page.stateGridRows.count()).toBeGreaterThan(0);
            expect(page.statesSearchInput.isDisplayed()).toBe(true);
            expect(page.countriesSummary.isDisplayed()).toBe(true);
            expect(page.stateSummary.isDisplayed()).toBe(true);
            expect(page.dmasSummary.isDisplayed()).toBe(true);
            expect(page.zipCodesSummary.isDisplayed()).toBe(true);
        });

        it('should select all/deselect all on state tab', function() {
            page.statesSelectAllBtn.click();
            expect(page.statesSummaryValues.count()).toEqual(75);
            page.statesSelectAllBtn.click();
            expect(page.statesSummaryValues.count()).toEqual(0);
        });

        it('should allow state search, select and deselect', function() {
            browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
            page.statesSearchInput.sendKeys('new');
            expect(page.stateGridRows.count()).toEqual(6);
            page.statesSelectAllBtn.click();
            expect(page.statesSummaryValues.count()).toEqual(6)
            page.statesSearchInput.clear();
            page.statesSearchInput.sendKeys('col');
            page.statesSelectAllBtn.click();
            expect(page.statesSummaryValues.count()).toEqual(9);
            page.stateGridRows.get(0).click();
            expect(page.statesSummaryValues.count()).toEqual(8);
            page.statesSummaryRemoveByName('Colorado').click();
            expect(page.statesSummaryValues.count()).toEqual(7);
        });

        //it('should allow include or exclude of states', function() {
        //    expect(page.statesSummaryValues.get(0).getAttribute('class')).toContain('label-success');
        //    page.excludeStateBtn.click();
        //    expect(page.statesSummaryValues.get(0).getAttribute('class')).toContain('label-danger');
        //});

        it('should display all fields on DMA tab', function() {
            page.dmaTab.click();
            //expect(page.includeDmaBtn.isSelected()).toBe(true);
            //expect(page.excludeDmaBtn.isSelected()).toBe(false);
            expect(page.dmaGrid.isEnabled()).toBe(true);
            expect(page.dmaGridRows.count()).toBeGreaterThan(0);
            expect(page.dmasSearchInput.isDisplayed()).toBe(true);
            expect(page.countriesSummary.isDisplayed()).toBe(true);
            expect(page.stateSummary.isDisplayed()).toBe(true);
            expect(page.dmasSummary.isDisplayed()).toBe(true);
            expect(page.zipCodesSummary.isDisplayed()).toBe(true);
        });

        it('should select all/deselect all on DMA tab', function() {
            page.dmasSelectAllBtn.click();
            expect(page.dmasSummaryValues.count()).toEqual(212);
            page.dmasSelectAllBtn.click();
            expect(page.dmasSummaryValues.count()).toEqual(0);
        });

        it('should allow DMA search, select and deselect', function() {
            browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
            page.dmasSearchInput.sendKeys('colorado');
            expect(page.dmaGridRows.count()).toEqual(3);
            page.dmasSelectAllBtn.click();
            expect(page.dmasSummaryValues.count()).toEqual(3)
            page.dmasSearchInput.clear();
            page.dmasSearchInput.sendKeys('alaska');
            page.dmasSelectAllBtn.click();
            expect(page.dmasSummaryValues.count()).toEqual(6);
            page.dmaGridRows.get(0).click();
            expect(page.dmasSummaryValues.count()).toEqual(5);
            page.dmasSummaryRemoveByName('Fairbanks, AK').click();
            expect(page.dmasSummaryValues.count()).toEqual(4);
        });

        //it('should allow include or exclude of DMAs', function() {
        //    expect(page.dmasSummaryValues.get(0).getAttribute('class')).toContain('label-success');
        //    page.excludeDmaBtn.click();
        //    expect(page.dmasSummaryValues.get(0).getAttribute('class')).toContain('label-danger');
        //});

        it('should display all fields on zip tab', function() {
            page.zipTab.click();

            browser.wait(function () { // NOTE: rlt 20150724 - The first load of the zip tab takes a while.
              return page.zipGrid.isPresent();
            });

            //expect(page.includeZipBtn.isSelected()).toBe(true);
            //expect(page.excludeZipBtn.isSelected()).toBe(false);
            expect(page.zipGrid.isEnabled()).toBe(true);
            expect(page.zipGridRows.count()).toBeGreaterThan(0);
            expect(page.zipsSearchInput.isDisplayed()).toBe(true);
            expect(page.countriesSummary.isDisplayed()).toBe(true);
            expect(page.stateSummary.isDisplayed()).toBe(true);
            expect(page.dmasSummary.isDisplayed()).toBe(true);
            expect(page.zipCodesSummary.isDisplayed()).toBe(true);
        });

        it('should allow zip search, select and deselect', function() {
            browser.executeScript('window.scrollTo(0,document.body.scrollHeight)');
            page.zipsSearchInput.sendKeys('broomfield');
            expect(page.zipGridRows.count()).toEqual(4);
            page.zipGridRows.get(0).click();
            page.zipGridRows.get(1).click();
            page.zipGridRows.get(2).click();
            page.zipGridRows.get(3).click();
            expect(page.zipsSummaryValues.count()).toEqual(4)
            page.zipsSearchInput.clear();
            page.zipsSearchInput.sendKeys('arvada');
            page.zipGridRows.get(0).click();
            expect(page.zipsSummaryValues.count()).toEqual(5);
            page.zipGridRows.get(0).click();
            expect(page.zipsSummaryValues.count()).toEqual(4);
            page.zipsSummaryRemoveByName('80020 - Broomfield - CO').click();
            expect(page.zipsSummaryValues.count()).toEqual(3);
        });

        //it('should allow include or exclude of zips', function() {
        //    expect(page.zipsSummaryValues.get(0).getAttribute('class')).toContain('label-success');
        //    page.excludeZipBtn.click();
        //    expect(page.zipsSummaryValues.get(0).getAttribute('class')).toContain('label-danger');
        //});

        it('should save creative group with all geo targeting configuration', function() {
            page.nameField.sendKeys(creativeGroupName);
            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName);
            page.geoTab.click();
            expect(page.geoTarget.isSelected()).toBe(true);
            expect(page.countriesSummaryByName('Congo, The Democratic Republic of the').isDisplayed()).toBe(true);
            //expect(page.countriesSummaryByName('Congo, The Democratic Republic of the').getAttribute('class')).toContain('label-danger');
            expect(page.countriesSummaryByName('Congo, The Democratic Republic of the').getAttribute('class')).toContain('label-success');
            expect(page.countriesSummaryByName('Iran, Islamic Republic of').isDisplayed()).toBe(true);
            expect(page.countriesSummaryByName("Korea, Democratic People's Republic of").isDisplayed()).toBe(true);
            expect(page.countriesSummaryByName('Korea, Republic of').isDisplayed()).toBe(true);
            expect(page.countriesSummaryByName('Tanzania, United Republic of').isDisplayed()).toBe(true);

            expect(page.statesSummaryByName('New Brunswick').isDisplayed()).toBe(true);
            //expect(page.statesSummaryByName('New Brunswick').getAttribute('class')).toContain('label-danger');
            expect(page.statesSummaryByName('New Brunswick').getAttribute('class')).toContain('label-success');
            expect(page.statesSummaryByName('New Hampshire').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('New Jersey').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('New Mexico').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('New York').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('Newfoundland').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('District of Columbia').isDisplayed()).toBe(true);

            expect(page.dmasSummaryByName('Colorado Springs, CO').isDisplayed()).toBe(true);
            //expect(page.dmasSummaryByName('Colorado Springs, CO').getAttribute('class')).toContain('label-danger');
            expect(page.dmasSummaryByName('Colorado Springs, CO').getAttribute('class')).toContain('label-success');
            expect(page.dmasSummaryByName('Denver, CO').isDisplayed()).toBe(true);
            expect(page.dmasSummaryByName('Grand Junction, CO').isDisplayed()).toBe(true);
            expect(page.dmasSummaryByName('Juneau, AK').isDisplayed()).toBe(true);

            expect(page.zipsSummaryByName('80021 - Broomfield - CO').isDisplayed()).toBe(true);
            //expect(page.zipsSummaryByName('80021 - Broomfield - CO').getAttribute('class')).toContain('label-danger');
            expect(page.zipsSummaryByName('80021 - Broomfield - CO').getAttribute('class')).toContain('label-success');
            expect(page.zipsSummaryByName('80023 - Broomfield - CO').isDisplayed()).toBe(true);
            expect(page.zipsSummaryByName('80038 - Broomfield - CO').isDisplayed()).toBe(true);
        });

        it('should update creative group with all geo targeting configuration', function() {
            page.nameField.sendKeys(' update');
            //page.includeCountryBtn.click();
            page.countriesSearchInput.sendKeys('zimbabwe');
            expect(page.countryGridRows.count()).toEqual(1);
            page.countryGridRows.get(0).click();
            expect(page.countriesSummaryValues.count()).toEqual(6);

            page.stateTab.click();
            //page.includeStateBtn.click();
            page.statesSearchInput.sendKeys('texas');
            expect(page.stateGridRows.count()).toEqual(1);
            page.stateGridRows.get(0).click();
            expect(page.statesSummaryValues.count()).toEqual(8);

            page.dmaTab.click();
            //page.includeDmaBtn.click();
            page.dmasSearchInput.sendKeys('hou');
            expect(page.dmaGridRows.count()).toEqual(1);
            page.dmaGridRows.get(0).click();
            expect(page.dmasSummaryValues.count()).toEqual(5);

            page.zipTab.click();
            //page.includeZipBtn.click();
            page.zipsSearchInput.sendKeys('77074');
            expect(page.zipGridRows.count()).toEqual(1);
            page.zipGridRows.get(0).click();
            expect(page.zipsSummaryValues.count()).toEqual(4);

            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName + ' update');
            expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName + ' update');
            page.geoTab.click();
            expect(page.geoTarget.isSelected()).toBe(true);
            expect(page.countriesSummaryByName('Congo, The Democratic Republic of the').isDisplayed()).toBe(true);
            expect(page.countriesSummaryByName('Congo, The Democratic Republic of the').getAttribute('class')).toContain('label-success');
            expect(page.countriesSummaryByName('Iran, Islamic Republic of').isDisplayed()).toBe(true);
            expect(page.countriesSummaryByName("Korea, Democratic People's Republic of").isDisplayed()).toBe(true);
            expect(page.countriesSummaryByName('Korea, Republic of').isDisplayed()).toBe(true);
            expect(page.countriesSummaryByName('Tanzania, United Republic of').isDisplayed()).toBe(true);
            expect(page.countriesSummaryByName('Zimbabwe').isDisplayed()).toBe(true);

            expect(page.statesSummaryByName('New Brunswick').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('New Brunswick').getAttribute('class')).toContain('label-success');
            expect(page.statesSummaryByName('New Hampshire').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('New Jersey').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('New Mexico').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('New York').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('Newfoundland').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('District of Columbia').isDisplayed()).toBe(true);
            expect(page.statesSummaryByName('Texas').isDisplayed()).toBe(true);

            expect(page.dmasSummaryByName('Colorado Springs, CO').isDisplayed()).toBe(true);
            expect(page.dmasSummaryByName('Colorado Springs, CO').getAttribute('class')).toContain('label-success');
            expect(page.dmasSummaryByName('Denver, CO').isDisplayed()).toBe(true);
            expect(page.dmasSummaryByName('Grand Junction, CO').isDisplayed()).toBe(true);
            expect(page.dmasSummaryByName('Juneau, AK').isDisplayed()).toBe(true);
            expect(page.dmasSummaryByName('Houston, TX').isDisplayed()).toBe(true);

            expect(page.zipsSummaryByName('80021 - Broomfield - CO').isDisplayed()).toBe(true);
            expect(page.zipsSummaryByName('80021 - Broomfield - CO').getAttribute('class')).toContain('label-success');
            expect(page.zipsSummaryByName('80023 - Broomfield - CO').isDisplayed()).toBe(true);
            expect(page.zipsSummaryByName('80038 - Broomfield - CO').isDisplayed()).toBe(true);
            expect(page.zipsSummaryByName('77074 - Houston - TX').isDisplayed()).toBe(true);
        });

        it('should delete newly created creative groups', function() {
          common.deleteCreativeGroup(campaignName, creativeGroupName);
        });

    });

});

module.exports = CreativeGroupsDetailGeo;

