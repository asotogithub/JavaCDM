'use strict';

var Breadcrumb = function (campaignName, ioName, creativeGroup) {

    describe('Breadcrumb', function (){

		var page = require('../page-object/navigation.po'),
			creativeGroupName = 'all targeting logic1',
			navigate = require('../utilities/navigation.spec'),
			media = require('../page-object/media.po');

        //validate breadcrumbs exist on the campaign details page
		it ('should exist on the campaign details page', function(){
            navigate.campaignDetails(campaignName);
		    expect(page.getAllBreadcrumbLinks.get(0).getText()).toEqual('Campaigns');
		    expect(page.breadcrumbCurrentPage.getText()).toEqual(campaignName);
		});

        //validate breadcrumbs exist on the creative group details page
		it ('should exist on the creative group details page', function(){
            navigate.creativeGroupDetails(campaignName, creativeGroup.creativeGroupName);
		    expect(page.getAllBreadcrumbLinks.get(0).getText()).toEqual('Campaigns');
		    expect(page.getAllBreadcrumbLinks.get(1).getText()).toEqual(campaignName);
		    expect(page.breadcrumbCurrentPage.getText()).toEqual(creativeGroup.creativeGroupName);
		});

        //validate clicking on breadcrumbs on the creative group details page is successful
		it ('should navigate back to creative group list', function(){
            navigate.creativeGroupDetails(campaignName, creativeGroup.creativeGroupName);
		    page.getAllBreadcrumbLinks.get(1).click();
		    expect(page.getAllBreadcrumbLinks.get(0).getText()).toEqual('Campaigns');
		    expect(page.breadcrumbCurrentPage.getText()).toEqual(campaignName);
		});

		it ('should navigate back to the campaigns list', function(){
            navigate.creativeGroupDetails(campaignName, creativeGroup.creativeGroupName);
		    page.getAllBreadcrumbLinks.get(0).click();
		    expect(page.breadcrumbCurrentPage.getText()).toEqual('Campaigns');
		});

        //validate breadcrumbs still exist after reloading the page
		it ('should be available after reloading the page', function(){
            navigate.creativeGroupDetails(campaignName, creativeGroup.creativeGroupName);
		    browser.refresh
		    expect(page.getAllBreadcrumbLinks.get(0).getText()).toEqual('Campaigns');
		    expect(page.getAllBreadcrumbLinks.get(1).getText()).toEqual(campaignName);
		    expect(page.breadcrumbCurrentPage.getText()).toEqual(creativeGroup.creativeGroupName);
		});

        //validate breadcrumbs exist on the io details page
        it ('should exist on the io details tab', function(){
            navigate.ioSummary(campaignName, ioName);
            expect(page.getAllBreadcrumbLinks.get(0).getText()).toEqual('Campaigns');
            expect(page.getAllBreadcrumbLinks.get(1).getText()).toEqual(campaignName);
            expect(page.breadcrumbCurrentPage.getText()).toEqual(ioName);
        });


        // commenting out until we get more automated data setup

        // //validate breadcrumbs exist on the creative details page
        // it ('should exist on the creative details tab', function(){
        //     var obj = navigate.firstCreative();
        //     expect(page.getAllBreadcrumbLinks.get(0).getText()).toEqual('Campaigns');
        //     expect(page.getAllBreadcrumbLinks.get(1).getText()).toEqual(obj.campaignName);
        //     expect(page.breadcrumbCurrentPage.getText()).toEqual(obj.creativeName);
        // });

        // //validate breadcrumbs exist on site measurement page
        // it ('should exist on the site measurement page', function(){
        //     page.siteMeasurementItem.click();
        //     var campaignName = page.siteMeasurementRow.get(0).getText();
        //     page.siteMeasurementDataRows.get(0).click();
        //     expect(page.getAllBreadcrumbLinks.get(0).getText()).toEqual('Site Measurement');
        //     expect(page.breadcrumbCurrentPage.getText()).toEqual(campaignName);
        // });

        // //validate clicking on breadcrumbs on site measurement page is successful
        // it ('should navigate back to the site measurement list', function(){
        //     page.siteMeasurementItem.click();
        //     page.siteMeasurementDataRows.get(0).click();
        //     page.getAllBreadcrumbLinks.get(0).click();
        //     expect(page.breadcrumbCurrentPage.getText()).toEqual('Site Measurement');
        // });

        //validate deep linking with breadcrumbs
        it ('should still display breadcrumbs on new browser page', function(){
            var obj = navigate.firstCreativeGroup();
            var currentUrl = '';
            browser.getLocationAbsUrl().then(function(url){
                browser.get('/#' + url);
                expect(page.getAllBreadcrumbLinks.get(0).getText()).toEqual('Campaigns');
                expect(page.getAllBreadcrumbLinks.get(1).getText()).toEqual(obj.campaignName);
                expect(page.breadcrumbCurrentPage.getText()).toEqual(obj.creativeGroupName);
            });
        });
    });
};

module.exports = Breadcrumb;
