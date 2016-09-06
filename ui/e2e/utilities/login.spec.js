'use strict';

var login = (function (email, password) {

  describe('Login', function() {
    var page = require('../page-object/login.po'),
        nav = require('../page-object/navigation.po');

    it('should log in as ' +email, function() {
      var EC = protractor.ExpectedConditions;
      browser.get('/');
      page.usernameEl.isPresent().then(function(present) {
         if (present == false) {
            nav.logoutIcon.click();
            nav.logout.click();
            expect(browser.getLocationAbsUrl()).toContain('/login');
         }
      });
      expect(browser.getLocationAbsUrl()).toContain('/login');
      page.usernameEl.clear();
      page.usernameEl.sendKeys(email);
      page.passwordEl.sendKeys(password);
      expect(page.submitButton.isEnabled()).toBe(true);
      page.submitButton.click();
      browser.wait(EC.visibilityOf(nav.campaignsItem), 10000);

      expect(nav.campaignsItem.isDisplayed()).toBe(true);
      expect(nav.userLogInName.getText()).toEqual(email);
    });

  });

});

module.exports = login;


