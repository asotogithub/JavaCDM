'use strict';

var loginPage = (function () {

  describe('Login Page', function() {
    var page = require('../page-object/login.po'),
        users = require('../utilities/users.spec'),
        user = users.auto01,
        invalidEmail = 'email@trueffect.com',
        invalidPassword = 'password4321',
        login = require('../page-object/login.po'),
        nav = require('../page-object/navigation.po');

    it('should load', function() {
      login.usernameEl.isPresent().then(function(present) {
        if (present == false) {
          nav.logoutIcon.click();
          nav.logout.click();
        }
        expect(browser.getLocationAbsUrl()).toContain('/login');
      });
    });

    it('should display login page elements', function() {
      expect(page.usernameEl.isDisplayed()).toBe(true);
      expect(page.passwordEl.isDisplayed()).toBe(true);
      expect(page.rememberMe.isPresent()).toBe(true);
      expect(page.submitButton.isDisplayed()).toBe(true);
      expect(page.helpLink.isDisplayed()).toBe(true);
      expect(page.privacyLink.isDisplayed()).toBe(true);
      expect(page.contactUsLink.isDisplayed()).toBe(true);
    });

    it('should have disabled Login button until an email address and password is entered', function() {
      page.usernameEl.sendKeys(invalidEmail);
      expect(page.usernameEl.getAttribute('value')).toBe(invalidEmail);
      expect(page.submitButton.isEnabled()).toBe(false);

      page.passwordEl.sendKeys(invalidPassword);
      expect(page.passwordEl.getAttribute('value')).toBe(invalidPassword);
      expect(page.submitButton.isEnabled()).toBe(true);

      page.usernameEl.clear();
      expect(page.usernameEl.getAttribute('value')).toBe('');
      expect(page.submitButton.isEnabled()).toBe(false);

      page.usernameEl.sendKeys(invalidEmail);
      expect(page.usernameEl.getAttribute('value')).toBe(invalidEmail);
      page.passwordEl.clear();
      expect(page.passwordEl.getAttribute('value')).toBe('');
      expect(page.submitButton.isEnabled()).toBe(false);

      page.passwordEl.sendKeys(invalidPassword);
      expect(page.passwordEl.getAttribute('value')).toBe(invalidPassword);
      expect(page.submitButton.isEnabled()).toBe(true);
    });

    it('should display an invalid login message', function() {
      browser.get('/#/login');
      expect(browser.getLocationAbsUrl()).toContain('/login');

      page.usernameEl.sendKeys(invalidEmail);
      expect(page.usernameEl.getAttribute('value')).toBe(invalidEmail);
      page.passwordEl.sendKeys(invalidPassword);
      expect(page.passwordEl.getAttribute('value')).toBe(invalidPassword);

      expect(page.submitButton.isEnabled()).toBe(true);
      page.submitButton.click();
      expect(page.invalidLoginMsg.isDisplayed()).toBe(true);

      page.usernameEl.clear();
      expect(page.usernameEl.getAttribute('value')).toBe('');
      page.passwordEl.clear();
      expect(page.passwordEl.getAttribute('value')).toBe('');

      page.usernameEl.sendKeys(user.email);
      expect(page.usernameEl.getAttribute('value')).toBe(user.email);
      page.passwordEl.sendKeys(invalidPassword);
      expect(page.passwordEl.getAttribute('value')).toBe(invalidPassword);

      expect(page.submitButton.isEnabled()).toBe(true);
      page.submitButton.click();
      expect(page.invalidLoginMsg.isDisplayed()).toBe(true);
    });

  });

});

module.exports = loginPage;


