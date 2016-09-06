'use strict';

var statusPage = (function () {
    describe('Status Page', function () {
        var page = require('../page-object/status.po');

        it('should display the status page', function () {
            browser.get('/#/status');
            expect(browser.getLocationAbsUrl()).toContain('/status');
            expect(page.statusHeader.isDisplayed()).toBe(true);
            expect(page.cmDbConnectionValid.getText()).toEqual('true');
            expect(page.cmDbStatusMessage.getText()).toEqual('Success');
            expect(page.metricsDbConnectionValid.getText()).toEqual('true');
            expect(page.metricsDbStatusMessage.getText()).toEqual('Success');
        });
    });
});

module.exports = statusPage;
