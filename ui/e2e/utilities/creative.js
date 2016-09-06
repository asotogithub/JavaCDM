'use strict';

var Creative = function() {

    var path = require('path');

    this.downloadFile = function (fileName, elementLinkName, serverPath) {
        var auxPath = serverPath && serverPath !== null ? serverPath + '/' : '';

        browser.driver.executeScript('var link = document.createElement("a");' +
            'var text = document.createTextNode("' + elementLinkName + '");' +
            'link.appendChild(text);' +
            'link.setAttribute("id", "' + elementLinkName + '");' +
            'link.setAttribute("target", "_self");' +
            'link.setAttribute("download", "' + fileName + '");' +
            'link.setAttribute("href", "/templates/' + auxPath + fileName + '");' +
            'document.body.appendChild(link);');
    };

    this.getPathFile = function(config, fs, file) {
        var filePath = path.resolve(
            config.capabilities.chromeOptions.prefs.download.default_directory,
            file.name);

        if (config.seleniumAddress !== config.env.browserstack.seleniumAddress) {
            browser.driver.wait(function () {
                try {
                    fs.statSync(filePath);
                    return true;
                }
                catch (error) {
                    return false;
                }
            }).then(function () {
                expect(fs.existsSync(filePath)).toBeTruthy();
                file.path = filePath;
            });
        }
    };
};

module.exports = new Creative();
