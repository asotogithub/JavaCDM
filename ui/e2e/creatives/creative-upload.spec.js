'use strict';

var creativeUpload = (function (campaignName, files) {

    describe('Creative page', function () {
        var CONSTANTS = require('../utilities/constants.js'),
            config,
            common = require('../utilities/common.spec'),
            creativeGroupName = 'Default',
            creativesUploaded = 0,
            navigate = require('../utilities/navigation.spec'),
            page = require('../page-object/creatives.po'),
            path = require('path');

        it('should get the environment configurations', function () {
            browser.getProcessedConfig().then(function (configuration) {
                config = configuration;
                expect(config.hasOwnProperty('env')).toBe(true);
                expect(config.hasOwnProperty('seleniumAddress')).toBe(true);
            });
        });

        it('should upload image type GIF, and allow the user to edit the alias', function () {
            var newAlias = 'CustomAlias';

            navigate.newCreative(campaignName);
            expect(page.uploadCreativeRowsByTable(page.newCreativeTable).count()).toEqual(0);
            expect(page.uploadCreativeRowsByTable(page.versionedCreativeTable).count()).toEqual(0);
            page.uploadFile(config, files.gifFile.path, files.gifFile.name);
            expect(page.uploadCreativeRowsByTable(page.newCreativeTable).count()).toEqual(1);
            expect(page.uploadCreativeRowsByTable(page.versionedCreativeTable).count()).toEqual(0);
            page.aliasInputByTable(page. newCreativeTable, 0).clear();
            page.aliasInputByTable(page.newCreativeTable, 0).sendKeys(newAlias);
            page.nextBtn.click();
            page.checkboxByGroupName(creativeGroupName).click();
            page.saveBtn.click();
            creativesUploaded++;
            expect(page.getCreativeGridRowData(files.gifFile.name, 'Alias').getText()).toEqual(newAlias);
            expect(page.getCreativeGridRowData(files.gifFile.name, 'Width').getText()).toEqual(files.gifFile.width);
            expect(page.getCreativeGridRowData(files.gifFile.name, 'Height').getText()).toEqual(files.gifFile.height);
            expect(page.getCreativeGridRowData(files.gifFile.name, 'Type').getText()).toEqual(files.gifFile.type);
        });

        it('should upload a new version of GIF image', function () {
            navigate.newCreative(campaignName);
            expect(page.uploadCreativeRowsByTable(page.newCreativeTable).count()).toEqual(0);
            expect(page.uploadCreativeRowsByTable(page.versionedCreativeTable).count()).toEqual(0);
            page.uploadFile(config, files.gifFile.path, files.gifFile.name);
            expect(page.uploadCreativeRowsByTable(page.newCreativeTable).count()).toEqual(0);
            expect(page.uploadCreativeRowsByTable(page.versionedCreativeTable).count()).toEqual(1);
        });

        it('should validate alias as a required field before allowing the user to associate/save', function () {
            var expectedAlias = files.gifFile.name.slice(0, -4);

            expect(page.aliasInputByTable(page.versionedCreativeTable, 0).getAttribute('value')).toEqual(expectedAlias);
            page.aliasInputByTable(page.versionedCreativeTable, 0).clear();
            expect(page.nextBtn.getAttribute('a-disabled')).toBeTruthy();
            expect(page.errorMessageByTable(page.versionedCreativeTable).isDisplayed()).toBe(true);
            page.aliasInputByTable(page.versionedCreativeTable, 0).sendKeys(expectedAlias);
            page.nextBtn.click();
            expect(page.informationDiv.isDisplayed()).toBe(true);
            page.saveBtn.click();
            expect(page.getCreativeGridRowData(files.gifFile.name, 'Width').getText()).toEqual(files.gifFile.width);
            expect(page.getCreativeGridRowData(files.gifFile.name, 'Height').getText()).toEqual(files.gifFile.height);
            expect(page.getCreativeGridRowData(files.gifFile.name, 'Type').getText()).toEqual(files.gifFile.type);
        });

        it('should upload file type HTML5', function () {
            navigate.newCreative(campaignName);
            page.uploadFile(config, files.html5file.path, files.html5file.name);
            page.nextBtn.click();
            page.checkboxByGroupName(creativeGroupName).click();
            page.saveBtn.click();
            creativesUploaded++;
            expect(page.getCreativeGridRowData(files.html5file.name, 'Width').getText()).toEqual(files.html5file.width);
            expect(page.getCreativeGridRowData(files.html5file.name, 'Height').getText()).toEqual(files.html5file.height);
            expect(page.getCreativeGridRowData(files.html5file.name, 'Type').getText()).toEqual(files.html5file.type);
        });

        it('should upload file type TXT', function () {
            navigate.newCreative(campaignName);
            page.uploadFile(config, files.txtFile.txt.path, files.txtFile.txt.name);
            page.nextBtn.click();
            page.checkboxByGroupName(creativeGroupName).click();
            page.saveBtn.click();
            creativesUploaded++;
            expect(page.getCreativeGridRowData(files.txtFile.txt.name, 'Width').getText()).toEqual(files.txtFile.txt.width);
            expect(page.getCreativeGridRowData(files.txtFile.txt.name, 'Height').getText()).toEqual(files.txtFile.txt.height);
            expect(page.getCreativeGridRowData(files.txtFile.txt.name, 'Type').getText()).toEqual(files.txtFile.txt.type);
        });

        it('should upload empty file type TXT', function () {
            navigate.newCreative(campaignName);
            page.uploadFile(config, files.txtFile.emptyTxt.path, files.txtFile.emptyTxt.name);
            page.nextBtn.click();
            page.checkboxByGroupName(creativeGroupName).click();
            page.saveBtn.click();
            creativesUploaded++;
            expect(page.getCreativeGridRowData(files.txtFile.emptyTxt.name, 'Width').getText()).toEqual(files.txtFile.emptyTxt.width);
            expect(page.getCreativeGridRowData(files.txtFile.emptyTxt.name, 'Height').getText()).toEqual(files.txtFile.emptyTxt.height);
            expect(page.getCreativeGridRowData(files.txtFile.emptyTxt.name, 'Type').getText()).toEqual(files.txtFile.emptyTxt.type);
        });

        it('should upload video type vast', function () {
            navigate.newCreative(campaignName);
            page.uploadFile(config, files.xmlFile.vast.path, files.xmlFile.vast.name);
            page.nextBtn.click();
            page.checkboxByGroupName(creativeGroupName).click();
            page.saveBtn.click();
            creativesUploaded++;
            expect(page.getCreativeGridRowData(files.xmlFile.vast.displayName, 'Width').getText()).toEqual(files.xmlFile.vast.width);
            expect(page.getCreativeGridRowData(files.xmlFile.vast.displayName, 'Height').getText()).toEqual(files.xmlFile.vast.height);
            expect(page.getCreativeGridRowData(files.xmlFile.vast.displayName, 'Type').getText()).toEqual(files.xmlFile.vast.displayType);
        });

        it('should upload video type vmap', function () {
            navigate.newCreative(campaignName);
            page.uploadFile(config, files.xmlFile.vmap.path, files.xmlFile.vmap.name);
            page.nextBtn.click();
            page.checkboxByGroupName(creativeGroupName).click();
            page.saveBtn.click();
            creativesUploaded++;
            expect(page.getCreativeGridRowData(files.xmlFile.vmap.displayName, 'Width').getText()).toEqual(files.xmlFile.vmap.width);
            expect(page.getCreativeGridRowData(files.xmlFile.vmap.displayName, 'Height').getText()).toEqual(files.xmlFile.vmap.height);
            expect(page.getCreativeGridRowData(files.xmlFile.vmap.displayName, 'Type').getText()).toEqual(files.xmlFile.vmap.displayType);
        });

        //Disabled until TruAdvertiser supports uploading 3rd party creatives
        xit('should upload creatives type 3rd', function () {
            navigate.newCreative(campaignName);
            page.uploadFile(config, files.thirdPartyFile.path, files.thirdPartyFile.name);
            page.nextBtn.click();
            page.checkboxByGroupName(creativeGroupName).click();
            page.saveBtn.click();
            creativesUploaded++;
            expect(page.getCreativeGridRowData(files.thirdPartyFile.name, 'Width').getText()).toEqual(files.thirdPartyFile.width);
            expect(page.getCreativeGridRowData(files.thirdPartyFile.name, 'Height').getText()).toEqual(files.thirdPartyFile.height);
            expect(page.getCreativeGridRowData(files.thirdPartyFile.name, 'Type').getText()).toEqual(files.thirdPartyFile.type);
            expect(page.clickThroughTab.isPresent()).toBe(false);
        });

        it('should display the creatives counter', function () {
            navigate.creativeGrid(campaignName);
            expect(page.creativeCounter.isDisplayed()).toBe(true);
            expect(page.creativeCounter.getText()).toContain(creativesUploaded);
        });

        it('should update the creatives counter on search context', function () {
            page.searchInput.sendKeys('creative');
            page.dataRows.then(function (rows) {
                expect(page.creativeCounter.getText()).toContain(rows.length);
                expect(page.creativeCounter.getText()).toContain(creativesUploaded);
            });
        });
    });
});

module.exports = creativeUpload;
