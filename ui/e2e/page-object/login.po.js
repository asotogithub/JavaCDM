'use strict';

var LoginPage = function() {
  this.contactUsLink = element(by.cssContainingText('a', 'Contact Us'));
  this.helpLink = element(by.cssContainingText('a', 'Help'));
  this.invalidLoginMsg = element(by.cssContainingText('span', 'The email or password you entered is incorrect'));
  this.passwordEl = element(by.model('credentials.password'));
  this.privacyLink = element(by.cssContainingText('a', 'Privacy Policy'));
  this.rememberMe = element(by.css('[data-ng-model="credentials.remember"]'));
  this.rememberMeCheckbox = element(by.css('[data-ng-model="credentials.remember"]:first-of-type + span'));
  this.submitButton = element(by.css('#btn-login'));
  this.usernameEl = element(by.model('credentials.username'));

  this.getUsername = function () {
    return this.usernameEl.getAttribute('value');
  };

  this.getPassword = function () {
    return this.passwordEl.getAttribute('value');
  };
};

module.exports = new LoginPage();

