'use strict';

var logout = (function () {

  describe('Logout', function() {
    var nav = require('../page-object/navigation.po');

    it('should log out', function() {
		var EC = protractor.ExpectedConditions;
    	var isClickable = EC.elementToBeClickable(nav.logoutIcon);
    	browser.wait(isClickable, 10000);
      	nav.logoutIcon.click();
      	nav.logout.click();
        browser.getLocationAbsUrl().then(function(url) {
            if (url.toString().indexOf('/login') < 0) {
                browser.refresh();
                nav.logoutIcon.click();
                nav.logout.click();
            }
            expect(browser.getLocationAbsUrl()).toContain('/login');
        });
    });
  });
});

module.exports = logout;


