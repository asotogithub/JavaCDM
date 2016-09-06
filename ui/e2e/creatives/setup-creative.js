'use strict';

var dataSetup = (function (campaignName, files) {

    describe('Creative Data Setup', function() {
        var seleniumAddress,
            page = require('../page-object/creatives.po'),
            campaign = require('../page-object/campaigns.po'),
            campaignUtil = require('../utilities/campaigns.js'),
            creativeUtil = require('../utilities/creative.js'),
            navigate = require('../utilities/navigation.spec'),
            nav = require('../page-object/navigation.po'),
            util = require('../utilities/util'),
            config,
            fs = require('fs'),
            path = require('path');

        it('should create a new campaign: ' +  campaignName, function() {
            browser.wait(function() {
                return nav.campaignsItem.isPresent();
            });

            campaignUtil.addCampaign(campaignName);
            navigate.campaignGrid();
            expect(campaign.campaignRowByName(campaignName).isDisplayed()).toBe(true);
        });


        it('should navigate to creative grid', function() {
            navigate.campaignGrid();
            expect(campaign.campaignRowByName(campaignName).isDisplayed()).toBe(true);
        });

        it('should get the environment configurations', function () {
            browser.getProcessedConfig().then(function (configuration) {
                config = configuration;
                expect(config.hasOwnProperty('env')).toBe(true);
                expect(config.hasOwnProperty('seleniumAddress')).toBe(true);
            });
        });

        it('should download files to test import', function () {
            creativeUtil.downloadFile(files.gifFile.name, 'downloadGifFile');
            util.click(page.downloadGifFile);
            creativeUtil.getPathFile(config, fs, files.gifFile);

            creativeUtil.downloadFile(files.html5file.name, 'downloadHtml5File');
            util.click(page.downloadHtml5File);
            creativeUtil.getPathFile(config, fs, files.html5file);

            creativeUtil.downloadFile(files.txtFile.txt.name, 'downloadTxtFile');
            util.click(page.downloadTxtFile);
            creativeUtil.getPathFile(config, fs, files.txtFile.txt);

            creativeUtil.downloadFile(files.txtFile.emptyTxt.name, 'downloadEmptyTxtFile');
            util.click(page.downloadEmptyTxtFile);
            creativeUtil.getPathFile(config, fs, files.txtFile.emptyTxt);

            creativeUtil.downloadFile(files.xmlFile.vast.name, 'downloadVideoVastFile');
            util.click(page.downloadVideoVastFile);
            creativeUtil.getPathFile(config, fs, files.xmlFile.vast);

            creativeUtil.downloadFile(files.xmlFile.vmap.name, 'downloadVideoVmapFile');
            util.click(page.downloadVideoVmapFile);
            creativeUtil.getPathFile(config, fs, files.xmlFile.vmap);

            creativeUtil.downloadFile(files.thirdPartyFile.name, 'downloadThirdPartyFile');
            util.click(page.downloadThirdPartyFile);
            creativeUtil.getPathFile(config, fs, files.thirdPartyFile);
        });
    });
});

module.exports = dataSetup;
