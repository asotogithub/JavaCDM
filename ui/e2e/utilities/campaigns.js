'use strict';

var Campaigns = function() {
	var page = require('../page-object/campaigns.po'),
		  navigate = require('../utilities/navigation.spec'),
                  util = require('../utilities/util');

	this.addCampaign = function(campaignName) {
		var budget = '12345.00';
        navigate.newCampaign();
        page.newCampaignName.sendKeys(campaignName);
        page.newCampaignAdvertiser.click();
        page.newCampaignAdvertiser.element(by.css('option:nth-child(2)')).click();
        var advertiserName = page.newCampaignAdvertiser.$('option:checked').getText();
        browser.actions().mouseMove(page.newCampaignBrand).perform();
        page.newCampaignBrand.click();
        page.newCampaignBrand.element(by.css('option:nth-child(2)')).click();
        var brandName = page.newCampaignBrand.$('option:checked').getText();
        page.nameNext.click();
        browser.actions().mouseMove(page.firstPartyDomain).perform();
        page.firstPartyDomain.click();
        page.firstPartyDropdown.click();
        page.firstPartyDropdown.element(by.css('option:last-of-type')).click();
        var domainName = page.firstPartyDropdown.$('option:checked').getText();
        util.click(page.domainNext);
        page.budget.click();
        page.budget.sendKeys(budget);
        expect(page.createSave.getAttribute('class')).not.toContain('disabled');
        page.createSave.click();
        expect(page.errorMsg.isPresent()).toBe(false);
        return {campaignName:campaignName, advertiserName:advertiserName, brandName:brandName, budget:budget, domainName:domainName};
	};


};

module.exports = new Campaigns();
