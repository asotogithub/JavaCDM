'use strict';

var creativeUpload = (function (campaignName, files) {

  describe('Upload creatives', function() {
    var page = require('../page-object/creatives.po'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec'),
        campaignUtil = require('../utilities/campaigns.js'),
        creativeUtil = require('../utilities/creative.js'),
        util = require('../utilities/util.js'),
        common = require('../utilities/common.spec'),
        creativeGroupName = 'Smoke Test Creative Group',
        path = require('path'),
        fs = require('fs'),
        browserName,
        seleniumAddress,
        config;

    it('should navigate to creative tab', function() {
        navigate.creativeGrid(campaignName);
    });

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

    it('should download files to test import', function () {
        creativeUtil.downloadFile(files.gifFile.name, 'downloadGifFile');
        util.click(page.downloadGifFile);

        creativeUtil.downloadFile(files.html5file.name, 'downloadHtml5File');
        util.click(page.downloadHtml5File);
    });

    it('should upload 2 creatives', function() {
        var filename,
            filename2,
            fs = require('fs');

        navigate.newCreative(campaignName);
        
        if (config.seleniumAddress === config.env.browserstack.seleniumAddress && config.capabilities.os === 'WINDOWS' && config.capabilities.browserName === 'firefox'){
            filename = config.env.browserstack.downloadDirectoryWindowsFirefox + files.gifFile.name;
            filename2 = config.env.browserstack.downloadDirectoryWindowsFirefox + files.html5file.name;
        }
        else if (config.seleniumAddress === config.env.browserstack.seleniumAddress && config.capabilities.os === 'WINDOWS' && config.capabilities.browserName === 'chrome'){
            filename = config.env.browserstack.downloadDirectoryWindowsChrome + files.gifFile.name;
            filename2 = config.env.browserstack.downloadDirectoryWindowsChrome + files.html5file.name;
        }
        else if (config.seleniumAddress === config.env.browserstack.seleniumAddress && config.capabilities.os === 'OS X' && config.capabilities.browserName === 'chrome') {
            filename = path.resolve(config.env.browserstack.downloadDirectory, files.gifFile.name);
            filename2 = path.resolve(config.env.browserstack.downloadDirectory, files.html5file.name); 
        }
        else if (config.seleniumAddress === config.env.browserstack.seleniumAddress && config.capabilities.os === 'OS X' && config.capabilities.browserName === 'firefox') {
            filename = path.resolve(config.env.browserstack.downloadDirectoryFirefox, files.gifFile.name);
            filename2 = path.resolve(config.env.browserstack.downloadDirectoryFirefox, files.html5file.name); 
        }
        else {
            filename = path.resolve(path.resolve(__dirname, '../../server/templates/' + files.gifFile.name));
            filename2 = path.resolve(path.resolve(__dirname, '../../server/templates/' + files.html5file.name));
        }

        browser.executeAsyncScript(function(callback) {
          document.querySelectorAll('input[type="file"]')[0]
              .style.visibility = 'visible';
          document.querySelectorAll('input[type="file"]')[0]
              .style.width = '1px';
          document.querySelectorAll('input[type="file"]')[0]
              .style.height = '1px';
          callback();
        });

        page.upload.sendKeys(filename);
        page.upload.sendKeys(filename2);

        util.click(page.nextBtn);
        util.click(page.checkboxByGroupName(creativeGroupName));
        util.click(page.saveBtn);
        expect(page.getCreativeGridRowData(files.gifFile.name, 'Width').getText()).toEqual(files.gifFile.width);
        expect(page.getCreativeGridRowData(files.gifFile.name, 'Height').getText()).toEqual(files.gifFile.height);
        expect(page.getCreativeGridRowData(files.gifFile.name, 'Type').getText()).toEqual(files.gifFile.type);
        expect(page.getCreativeGridRowData(files.html5file.name, 'Width').getText()).toEqual(files.html5file.width);
        expect(page.getCreativeGridRowData(files.html5file.name, 'Height').getText()).toEqual(files.html5file.height);
        expect(page.getCreativeGridRowData(files.html5file.name, 'Type').getText()).toEqual(files.html5file.type);
    });

  });
});

module.exports = creativeUpload;