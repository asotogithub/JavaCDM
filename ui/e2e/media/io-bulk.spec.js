'use strict';

var IOBulk = (function (campaignName) {
    describe('IO Bulk', function() {
        var CONSTANTS = require('../utilities/constants'),
            browserName,
            campaign = require('../page-object/campaigns.po'),
            campaignUtil = require('../utilities/campaigns.js'),
            common = require('../utilities/common.spec'),
            config,
            fileInvalidImportPath,
            fileInvalidImportFormatPath,
            fileValidImportPath,
            fs = require('fs'),
            ioName = 'Protractor Media Import',
            nav = require('../page-object/navigation.po'),
            navigate = require('../utilities/navigation.spec'),
            page = require('../page-object/media.po'),
            path = require('path'),
            util = require('../utilities/util');

        it('should create a new campaign: ' + campaignName + ' and navigate to an empty media grid', function () {
            nav.campaignsItem.click();
            campaignUtil.addCampaign(campaignName);
            navigate.campaignGrid();
            expect(campaign.campaignRowByName(campaignName).isDisplayed()).toBe(true);
            global.countCampaigns++;
            navigate.mediaGrid(campaignName);
            expect(page.mediaGrid.isDisplayed()).toBe(true);
            expect(page.noDataToDisplay.isDisplayed()).toBe(true);
        });

        it('should get the environment configurations', function () {
            browser.getCapabilities().then(function (capabilities) {
                browserName = capabilities.caps_.browserName;
            });
            browser.getProcessedConfig().then(function (configuration) {
                config = configuration;
                expect(config.hasOwnProperty('env')).toBe(true);
                expect(config.hasOwnProperty('seleniumAddress')).toBe(true);
            });
        });

        it('should download *.xlsx file to test import Media', function () {
            //This test only runs on Chrome browser, because the selenium Chrome driver is the only one that supports
            //configuration to avoid the download prompt showing up and blocking the browser
            //Here we are injecting an element to click it via protractor and download the file to be used on the tests.
            browser.driver.executeScript('var link = document.createElement("a");' +
                'var text = document.createTextNode(".");' +
                'link.appendChild(text);' +
                'link.setAttribute("id", "downloadInvalidFile");' +
                'link.setAttribute("href", "/templates/' + CONSTANTS.MEDIA.INVALID_IMPORT_NAME + '");' +
                'document.getElementsByTagName("h3")[0].appendChild(link);').then(
                function () {
                    browser.driver.executeScript('var link = document.createElement("a");' +
                        'var text = document.createTextNode(".");' +
                        'link.appendChild(text);' +
                        'link.setAttribute("id", "downloadInvalidFormatFile");' +
                        'link.setAttribute("href", "/templates/' + CONSTANTS.MEDIA.INVALID_IMPORT_FORMAT_NAME + '");' +
                        'document.getElementsByTagName("h3")[0].appendChild(link);').then(
                        function () {
                            page.downloadInvalidFile.click();
                            browser.driver.executeScript('var link = document.createElement("a");' +
                                'var text = document.createTextNode(".");' +
                                'link.appendChild(text);' +
                                'link.setAttribute("id", "downloadValidFile");' +
                                'link.setAttribute("href", "/templates/' + CONSTANTS.MEDIA.VALID_IMPORT_NAME + '");' +
                                'document.getElementsByTagName("h3")[0].appendChild(link);').then(
                                function () {
                                    page.downloadValidFile.click();
                                    fileInvalidImportPath = path.resolve(
                                        config.capabilities.chromeOptions.prefs.download.default_directory,
                                        CONSTANTS.MEDIA.INVALID_IMPORT_NAME);
                                    fileInvalidImportFormatPath = path.resolve(
                                        config.capabilities.chromeOptions.prefs.download.default_directory,
                                        CONSTANTS.MEDIA.INVALID_IMPORT_FORMAT_NAME);
                                    fileValidImportPath = path.resolve(
                                        config.capabilities.chromeOptions.prefs.download.default_directory,
                                        CONSTANTS.MEDIA.VALID_IMPORT_NAME);
                                    if (config.seleniumAddress !== config.env.browserstack.seleniumAddress) {
                                        browser.driver.wait(function () {
                                            try {
                                                fs.statSync(fileInvalidImportPath);
                                                return true;
                                            }
                                            catch (error) {
                                                return false;
                                            }
                                        }).then(function () {
                                            expect(fs.existsSync(fileInvalidImportPath)).toBeTruthy();
                                            browser.driver.wait(function () {
                                                try {
                                                    fs.statSync(fileInvalidImportFormatPath);
                                                    return true;
                                                }
                                                catch (error) {
                                                    return false;
                                                }
                                            }).then(function () {
                                                expect(fs.existsSync(fileInvalidImportFormatPath)).toBeTruthy();
                                                browser.driver.wait(function () {
                                                    try {
                                                        fs.statSync(fileValidImportPath);
                                                        return true;
                                                    }
                                                    catch (error) {
                                                        return false;
                                                    }
                                                }).then(function () {
                                                    expect(fs.existsSync(fileValidImportPath)).toBeTruthy();
                                                });
                                            });
                                        });
                                    }
                                }
                            );
                        }
                    );
                }
            );
        });

        it('should open/close the Media bulk dropdown', function () {
            expect(page.bulkDropdownList.isDisplayed()).toBeFalsy();
            util.click(page.bulkDropdown);
            expect(page.bulkDropdownList.isDisplayed()).toBeTruthy();
            util.click(page.bulkDropdown);
            expect(page.bulkDropdownList.isDisplayed()).toBeFalsy();
        });

        it('should show a popup that lists the errors when importing an invalid Media file', function () {
            util.click(page.bulkDropdown);
            util.click(page.importAll);
            expect(page.processImportBtn.isEnabled()).toBe(false);
            page.uploadFile(config, fileInvalidImportPath, CONSTANTS.MEDIA.INVALID_IMPORT_NAME);
            expect(page.processImportBtn.isEnabled()).toBe(true);
            util.click(page.processImportBtn);
            expect(page.rowsIssues.getText()).toEqual('3');
            expect(page.errorRows.count()).toEqual(1);
            util.click(page.warningsTab);
            expect(page.warningRows.count()).toEqual(1);
        });

        it('should show actions tab that contain actions dropdown', function () {
            expect(page.actionsTab.isDisplayed()).toBe(true);
            util.click(page.actionsTab);
            expect(page.actionsRows.count()).toEqual(1);
            util.click(page.actionSelect.get(0));
            expect(page.completeImportBtn.isEnabled()).toBe(true);
            page.getOptionByIndex(page.actionSelect.get(0), 2).getText().then(function (text) {
                page.getOptionByIndex(page.actionSelect.get(0), 2).click();
                expect(page.getChecked(page.actionSelect.get(0)).getText()).toEqual(text);
            });
        });

        it('should export a *.xls file with the errors found', function () {
            expect(page.exportIssuesSuccessful.evaluate('vm.exportIssuesSuccessful')).toBeFalsy();
            browser.isElementPresent(page.exportIssuesBtn).then(function(result) {
                if (result) {
                    expect(page.exportIssuesBtn.isEnabled()).toBe(true);
                    util.click(page.exportIssuesBtn);
                    expect(page.exportIssuesSuccessful.evaluate('vm.exportIssuesSuccessful')).toBeTruthy();
                    util.click(page.cancelImportErrorsBtn);
                }
                else {
                    page.cancelImportErrorsBtn.click();
                }
            });
        });

        it('should import a valid Media file', function () {
            expect(page.noDataToDisplay.isDisplayed()).toBe(true);
            util.click(page.bulkDropdown);
            util.click(page.importAll);
            expect(page.processImportBtn.isEnabled()).toBe(false);
            page.uploadFile(config, fileValidImportPath, CONSTANTS.MEDIA.VALID_IMPORT_NAME);
            expect(page.processImportBtn.isEnabled()).toBe(true);
            util.click(page.processImportBtn);
            expect(page.dataRows.count()).toEqual(2);
            util.click(nav.ioRowByName(ioName));
            util.click(page.editIcon);
            expect(page.ioNameField.getAttribute('value')).toEqual(ioName);
        });

        it('should show a global validation warning dialog', function () {
            util.click(page.ioEditCancel);
            expect(page.mediaGrid.isDisplayed()).toBe(true);
            util.click(page.bulkDropdown);
            util.click(page.importAll);
            expect(page.processImportBtn.isEnabled()).toBe(false);
            page.uploadFile(config, fileInvalidImportFormatPath, CONSTANTS.MEDIA.INVALID_IMPORT_FORMAT_NAME);
            expect(page.globalValidationWarningPopup.isDisplayed()).toBe(true);
            util.click(page.warningOkBtn);
            util.click(page.cancelImportBtn);
        });

        it('should download the exported *.xlsx file', function() {
            expect(page.downloadSuccessful.evaluate('vm.downloadSuccessful')).toBeFalsy();
            if (browserName === 'chrome') {
                util.click(page.bulkDropdown);
                page.exportAll.click().then(function(){
                    expect(page.downloadSuccessful.evaluate('vm.downloadSuccessful')).toBeTruthy();
                });
            }
        });
    });
});

module.exports = IOBulk;

