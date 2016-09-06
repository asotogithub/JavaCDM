'use strict';

var creativeVideoCreative = (function (campaignName, videoFiles) {

    describe('Creative page', function() {
        var page = require('../page-object/creatives.po'),
            navigate = require('../utilities/navigation.spec'),
            path = require('path');

        it('should load video vast file', function() {
            var EC = protractor.ExpectedConditions;

            navigate.creativeGrid(campaignName);
            browser.wait(EC.visibilityOf(page.getCreativeGridRowData(videoFiles.vast.displayName, 'Alias')), 2000);
            page.getCreativeGridRowData(videoFiles.vast.displayName, 'Alias').click();
            page.creativeGridEditButton.click();
            browser.wait(EC.visibilityOf(page.videoPreview), 2000);
            page.videoPreview.getAttribute('src').then(function(attr){
                expect(attr).toEqual(videoFiles.vast.src);
            });
        });

        it('should load video vmap file', function() {
            var EC = protractor.ExpectedConditions;

            navigate.creativeGrid(campaignName);
            browser.wait(EC.visibilityOf(page.getCreativeGridRowData(videoFiles.vmap.displayName, 'Alias')), 2000);
            page.getCreativeGridRowData(videoFiles.vmap.displayName, 'Alias').click();
            page.creativeGridEditButton.click();
            browser.wait(EC.visibilityOf(page.videoPreview), 2000);
            page.videoPreview.getAttribute('src').then(function(attr){
                expect(attr).toEqual(videoFiles.vmap.src);
            });
        });
    });
});

module.exports = creativeVideoCreative;
