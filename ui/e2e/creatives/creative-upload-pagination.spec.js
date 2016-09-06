'use strict';

var creativeUploadPagination = (function (campaignName) {

    describe('Creative page', function () {
        var CONSTANTS = require('../utilities/constants'),
            page = require('../page-object/creatives.po'),
            navigate = require('../utilities/navigation.spec'),
            common = require('../utilities/common.spec'),
            creativeUtil = require('../utilities/creative.js'),
            path = require('path'),
            config,
            filesServerPath = '.tmp',
            files = [
                {
                    name: 'image-1-350x250.jpg',
                    download: 'downloadImage1',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-2-350x250.jpg',
                    download: 'downloadImage2',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-3-350x250.jpg',
                    download: 'downloadImage3',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-4-350x250.jpg',
                    download: 'downloadImage4',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-5-350x250.jpg',
                    download: 'downloadImage5',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-6-350x250.jpg',
                    download: 'downloadImage6',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-7-350x250.jpg',
                    download: 'downloadImage7',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-8-350x250.jpg',
                    download: 'downloadImage8',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-9-350x250.jpg',
                    download: 'downloadImage9',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-10-350x250.jpg',
                    download: 'downloadImage10',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                },
                {
                    name: 'image-11-350x250.jpg',
                    download: 'downloadImage11',
                    width: '350',
                    height: '250',
                    type: 'jpg',
                    path: ''
                }
            ];

        browser.getProcessedConfig().then(function (configuration) {
            config = configuration;
        });

        it('should upload more than 10 files and show pagination control', function () {
            var EC = protractor.ExpectedConditions,
                filename,
                fs = require('fs');

            navigate.campaignGrid();
            files.forEach(function (file) {
                creativeUtil.downloadFile(file.name, file.download, filesServerPath);
                page.hyperlinkDownloadById(file.download).click();
                creativeUtil.getPathFile(config, fs, file);
            });

            navigate.newCreative(campaignName);

            browser.executeScript('window.scrollTo(0,document.body.scrollHeight)').then(function () {
                files.forEach(function (file) {
                    page.uploadFile(config, file.path, file.name);
                });

                browser.wait(EC.presenceOf(page.paginationControl), CONSTANTS.defaultWaitInterval);
                expect(page.paginationControl.isDisplayed()).toBe(true);
            });
        });
    });
});

module.exports = creativeUploadPagination;
