'use strict';

var campaignsCreate = (function (campaignName) {

  describe('Campaign Create', function() {
    var page = require('../page-object/campaigns.po'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        campaignUtil = require('../utilities/campaigns.js'),
        util = require('../utilities/util'),
        budget = '12345.00',
        advertiser = 'Protractor Advertiser',
        brand = 'Protractor Brand',
        browserName,
        seleniumAddress,
        config;

    function getCookieDomain(){
        var cookieDomain;
        if(browser.baseUrl == 'https://my-stg.trueffect.com'){
            cookieDomain = 'ext.adlegend.net';
            return cookieDomain
        }
        else if(browser.baseUrl == 'https://my.trueffect.com'){
            cookieDomain = 'media.trueffect.co.uk';
            return cookieDomain
        }
        else if(browser.baseUrl == 'https://my-tst.trueffect.com'){
            cookieDomain = 'media.adlegend.net';
            return cookieDomain
        }
        else{
            cookieDomain = 'extdev.adlegend.net';
            return cookieDomain
        }
    };

    it('should get the environment configurations', function() {    
        browser.getCapabilities().then(function (capabilities) {
            browserName = capabilities.caps_.browserName;
        });
        browser.getProcessedConfig().then(function (config) {
            seleniumAddress = config.seleniumAddress;
        });
        browser.getProcessedConfig().then(function (configuration) {
            config = configuration;
        });
    });

    it('should create a new campaign, ' + campaignName, function() {   
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        });
        navigate.newCampaign();
        page.newCampaignName.sendKeys(campaignName);
        util.click(page.newCampaignAdvertiser);
        page.newCampaignAdvertiser.element(by.css('[label*="' + advertiser + '"]')).click();
        util.click(page.newCampaignBrand);
        page.newCampaignBrand.element(by.css('[label*="' + brand + '"]')).click();
        util.click(page.nameNext);
        util.click(page.firstPartyDomain);
        util.click(page.firstPartyDropdown);
        page.firstPartyDropdown.element(by.css('[label="' + getCookieDomain() + '"]')).click();
        util.click(page.domainNext);
        if (browserName === 'firefox') {
            util.click(page.domainNext);
        }
        util.click(page.budget);
        browser.actions().mouseMove(page.budget).perform();
        page.budget.click();
        browser.actions().sendKeys(protractor.Key.LEFT).perform();
        browser.actions().sendKeys(protractor.Key.LEFT).perform();
        browser.actions().sendKeys(protractor.Key.LEFT).perform();
        page.budget.sendKeys(budget);
        expect(page.createSave.getAttribute('class')).not.toContain('disabled');
        util.click(page.createSave);
        expect(page.errorMsg.isPresent()).toBe(false);
        expect(page.campaignNameField.getAttribute('value')).toEqual(campaignName);
        expect(page.brandField.getText()).toContain(brand);
        expect(page.advertiserField.getText()).toContain(advertiser);
        expect(page.domainField.$('option:checked').getText()).toContain(getCookieDomain());
        expect(page.budget.getAttribute('value')).toEqual(budget);
    });

  });
});

module.exports = campaignsCreate;