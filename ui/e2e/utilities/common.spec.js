'use strict';

var Common = function () {
  var page = require('../page-object/utilities.po'),
      navigate = require('../utilities/navigation.spec'),
      nav = require('../page-object/navigation.po'),
      cg = require('../utilities/creative-groups.spec'),
      creativeGroupPage = require('../page-object/creative-groups.po');

  this.cancel = function(action) {
    browser.driver.wait(protractor.until.elementIsVisible(page.cancelBtn));
    expect(page.cancelBtn.isDisplayed()).toBe(true);
    page.cancelBtn.click();
    var el = element(by.css('button[data-ng-click="' + action + '()"]'));
    browser.wait(function() {
      return browser.isElementPresent(el);
    }).then(function(){
      expect(el.isDisplayed()).toBe(true);
      el.click();
    });
  };

    this.close = function(action) {
        browser.driver.wait(protractor.until.elementIsVisible(page.closeBtn));
        expect(page.closeBtn.isDisplayed()).toBe(true);
        page.closeBtn.click();
        var el = element(by.css('button[data-ng-click="' + action + '()"]'));
        browser.wait(function() {
            return browser.isElementPresent(el);
        }).then(function(){
            expect(el.isDisplayed()).toBe(true);
            el.click();
        });
    };

  this.deleteItem = function(action) {
    page.deleteBtn.click();
    browser.wait(function() {
      return browser.isElementPresent(by.css('button[data-ng-click="' + action + '()"]'));
    }).then(function(){
      element(by.css('button[data-ng-click="' + action + '()"]')).click();
    });
  };

  this.newCreativeGroup = function(campaignName, values) {
    navigate.newCreativeGroup(campaignName)
    for(var x in values){
      var el = element(by.id(x));
      var val = values[x];
      if(typeof val === 'boolean'){
        element(by.css('#' + x + ':first-of-type + span')).click();
      }
      else{
        el.clear();
        el.sendKeys(val);
      }
    }
    page.saveBtn.click();
    // expect(page.errorMsg.isPresent()).toBe(false);
  };

  this.newCreativeGroupFirstCampaign = function(values) {
    navigate.newCreativeGroupFirstCampaign()
    for(var x in values){
      var el = element(by.id(x));
      var val = values[x];
      if(typeof val === 'boolean'){
        element(by.css('#' + x + ':first-of-type + span')).click();
      }
      else{
        el.clear();
        el.sendKeys(val);
      }
    }
    page.saveBtn.click();
    expect(page.errorMsg.isPresent()).toBe(false);
    return {name:values.creativeGroupName}
  };

  this.addIo = function(campaignName, ioName) {
    navigate.newIO(campaignName);
    nav.ioNameField.sendKeys(ioName);
    nav.ioSave.click();
    expect(page.errorMsg.isPresent()).toBe(false);
  };

  this.deleteCreativeGroup = function(campaignName, creativeGroupName) {
    navigate.creativeGroupDetails(campaignName, creativeGroupName);
    this.deleteItem('yes');
    expect(page.errorMsg.isPresent()).toBe(false);
    browser.wait(function() {
       return creativeGroupPage.creativeGroupsGrid.isPresent();
    }, 10000, 'Creative Group not deleted within 10 seconds');
  };

  this.deleteIo = function(campaignName, ioName) {
    navigate.ioSummary(campaignName, ioName);
    this.deleteItem('yes');
    expect(page.errorMsg.isPresent()).toBe(false);
    browser.wait(function() {
       return page.creativeGroupsGrid.isPresent();
    }, 10000, 'IO not deleted within 10 seconds');
  };

  this.getRandomString = function (characterLength) {
    var randomText = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    for (var i = 0; i < characterLength; i++)
        randomText += possible.charAt(Math.floor(Math.random() * possible.length));
    return randomText;
  };

  this.getRandomStringSpecialChar = function (characterLength) {
    var randomText = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_!@#$%^&*()?<>~"
    for (var i = 0; i < characterLength; i++)
        randomText += possible.charAt(Math.floor(Math.random() * possible.length));
    return randomText;
  };

  /**
   * This function generates a random number within a specific length.
   * @param numberLength Expected length of the number.
   * @returns {string} This function returns a string in order to be directly compared with the expected input values.
   */
  this.getRandomNumberByLength = function (numberLength) {
    return String(Math.floor(Math.pow(10, numberLength - 1) + Math.random() * 9 * Math.pow(10, numberLength - 1)));
  };

  this.getRandomBoolean = function () {
    return Math.random()<.5
  };

  this.addThousandsSeparator = function (number) {
    var regexp=  /(\d+)(\d{3})/;
    return String(Number(number)).replace(/^\d+/, function(substr){
      while(regexp.test(substr)){
        substr = substr.replace(regexp, '$1,$2');
      }
      return substr;
    });
  };

};

module.exports = new Common;


