'use strict';

var rememberMe = (function () {

  describe('Remember Me Checkbox', function() {
    var page = require('../page-object/login.po'),
        nav = require('../page-object/navigation.po'),
        users = require('../utilities/users.spec'),
        user = users.auto01,
        user2 = users.auto02,
        invalidEmail = 'email@trueffect.com',
        invalidpassword = 'password4321';

    it('should remember on valid login', function() {
      browser.get('/');
      expect(browser.getLocationAbsUrl()).toContain('/login');
      expect(page.rememberMe.isSelected()).toBe(false);

      page.usernameEl.sendKeys(user.email);
      expect(page.getUsername()).toBe(user.email);
      page.passwordEl.sendKeys(user.password);
      expect(page.getPassword()).toBe(user.password);

      page.rememberMeCheckbox.click();
      expect(page.rememberMe.isSelected()).toBe(true);

      expect(page.submitButton.isEnabled()).toBe(true);
      page.submitButton.click();
      expect(nav.campaignsItem.isDisplayed()).toBe(true);

      nav.logoutIcon.click();
      nav.logout.click();
      expect(browser.getLocationAbsUrl()).toContain('/login');

      expect(page.rememberMe.isSelected()).toBeTruthy();

      expect(page.getUsername()).toBe(user.email);
    });

    it('should not remember invalid username', function() {
      page.usernameEl.clear();
      expect(page.getUsername()).toBe('');
      page.rememberMe.isSelected().then(function(result) {
        if (result == false) {
            page.rememberMeCheckbox.click();
        }
      });
      expect(page.rememberMe.isSelected()).toBe(true);

      page.usernameEl.sendKeys(invalidEmail);
      expect(page.getUsername()).toBe(invalidEmail);
      page.passwordEl.sendKeys(invalidpassword);
      expect(page.getPassword()).toBe(invalidpassword);

      expect(page.submitButton.isEnabled()).toBe(true);
      page.submitButton.click();
      expect(page.invalidLoginMsg.isDisplayed()).toBe(true);
      browser.refresh();
      expect(page.rememberMe.isSelected()).toBe(true);
      expect(page.getUsername()).toBe(user.email);
    });


    it('should remember new user login', function() {
      page.usernameEl.clear();
      expect(page.getUsername()).toBe('');
      page.rememberMe.isSelected().then(function(result) {
        if (result == false) {
            page.rememberMeCheckbox.click();
        }
      });

      page.usernameEl.sendKeys(user2.email);
      expect(page.getUsername()).toBe(user2.email);
      page.passwordEl.sendKeys(user2.password);
      expect(page.getPassword()).toBe(user2.password);

      expect(page.submitButton.isEnabled()).toBe(true);
      page.submitButton.click();
      expect(nav.campaignsItem.isDisplayed()).toBe(true);

      nav.logoutIcon.click();
      nav.logout.click();
      expect(browser.getLocationAbsUrl()).toContain('/login');
      expect(page.rememberMe.isSelected()).toBeTruthy();
      expect(page.getUsername()).toBe(user2.email);
    });

    it('should clear remember me when unchecked', function() {
      page.rememberMe.isSelected().then(function(result) {
        if (result == true) {
            page.rememberMeCheckbox.click();
        }
      });
      expect(page.rememberMe.isSelected()).toBe(false);
      page.usernameEl.clear();
      expect(page.getUsername()).toBe('');

      page.usernameEl.sendKeys(user.email);
      expect(page.getUsername()).toBe(user.email);
      page.passwordEl.sendKeys(user.password);
      expect(page.getPassword()).toBe(user.password);

      expect(page.submitButton.isEnabled()).toBe(true);
      page.submitButton.click();
      expect(nav.campaignsItem.isDisplayed()).toBe(true);

      nav.logoutIcon.click();
      nav.logout.click();
      expect(browser.getLocationAbsUrl()).toContain('/login');
      expect(page.rememberMe.isSelected()).toBe(false);
      expect(page.usernameEl.getText()).toBe('');
    });

  });

});

module.exports = rememberMe;


