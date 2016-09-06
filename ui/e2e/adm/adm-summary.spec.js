'use strict';

var adm = (function () {

  describe('ADM Summary', function() {
    var CONSTANTS = require('../utilities/constants'),
        page = require('../page-object/adm.po'),
        navigate = require('../utilities/navigation.spec'),
        com = require('../page-object/utilities.po'),
        common = require('../utilities/common.spec'),
        fileName = 'protractor',
        startDateCtrl = '11/11/2019',
        endDateCtrl = '11/11/2017',
        advertiserName = 'Mixed Targeting Advertiser',
        alias = common.getRandomStringSpecialChar(10),
        cookieExpirationInvalid = common.getRandomNumberByLength(6),
        cookieExpirationValid = common.getRandomNumberByLength(2),
        cookieExpirationLessThanMin = '0',
        cookieExpirationMoreThanMax = '293',
        cookieOverWrite1 = common.getRandomString(9),
        cookieOverWrite2 = common.getRandomString(8),
        cookieOverWrite3 = common.getRandomString(7),
        defaultKey = common.getRandomString(10),
        displayCChannel = common.getRandomBoolean(),
        emailCChannel = common.getRandomBoolean(),
        extractableCookie1 = common.getRandomString(9),
        extractableCookie2 = common.getRandomString(8),
        failThroughDefaultName1 = common.getRandomString(9),
        failThroughDefaultName2 = common.getRandomString(8),
        failThroughDefaultName3 = common.getRandomString(7),
        failThroughDefaultValue1 = common.getRandomString(3),
        failThroughDefaultValue2 = common.getRandomString(3),
        failThroughDefaultValue3 = common.getRandomString(3),
        fileFrequency = common.getRandomNumberByLength(6),
        matchCookieName = common.getRandomString(10),
        siteCChannel = !displayCChannel && !emailCChannel,
        status = common.getRandomBoolean();

    function getDomain(){
      if(browser.baseUrl == 'https://my-stg.trueffect.com'){
          return 'ext.adlegend.net';
      }
      else{
          return 'extdev.adlegend.net';
      }
    };

    function getAdvertiserId(){
      if(browser.baseUrl == 'https://my-stg.trueffect.com'){
          return '5975520';
      }
      else{
          return '9073761';
      }
    };

    it('should navigate to ADM summary for ' + fileName + ' file', function() {
      var EC = protractor.ExpectedConditions;

      navigate.admSummary(fileName);
      browser.wait(EC.visibilityOf(page.summaryHeader), CONSTANTS.defaultWaitInterval);
      page.summaryHeader.getText().then(function (text) {
        expect(page.alias.getAttribute('value')).toEqual(text);
      });
      expect(page.listOfAdmBtn.isDisplayed()).toBe(true);
    });

    it('should display data visualization', function() {
      expect(page.admDataVis.isDisplayed()).toBe(true);
      expect(page.admChart.isDisplayed()).toBe(true);
    });

    it('should validate start Date and end Date', function() {
        page.startDate.click();
        page.startDate.sendKeys(startDateCtrl);
        expect(page.admDataVis.isDisplayed()).toBe(true);
        expect(page.admChart.isDisplayed()).toBe(true);
        page.endDate.click();
        page.endDate.sendKeys(endDateCtrl);
        expect(page.lowerThan.isDisplayed()).toBe(true);
    });

    it('should allow expand and collapse of chart', function() {
      var EC = protractor.ExpectedConditions;

      expect(page.chart.isDisplayed()).toBe(true);
      browser.driver.wait(function() {
          return EC.presenceOf(page.chartCollapse);
      }).then(function(){
          page.chartCollapse.click();
          browser.wait(EC.not(EC.visibilityOf(page.chart)), CONSTANTS.defaultWaitInterval);
          expect(page.chart.isDisplayed()).toBe(false);

          browser.driver.wait(function() {
              return EC.presenceOf(page.chartCollapse);
          }).then(function(){
              page.chartExpand.click();
              browser.wait(EC.visibilityOf(page.chart), CONSTANTS.defaultWaitInterval);
              expect(page.chart.isDisplayed()).toBe(true);
          });
      });
    });

    it('should contain correct readonly data', function() {
      expect(page.advertiser.getText()).toEqual(advertiserName);
      expect(page.domain.getText()).toEqual(getDomain());
      expect(page.fileName.getText()).toEqual(fileName);
    });

    it('should display button save', function() {
       expect(com.saveBtn.isDisplayed()).toBe(true);
    });

    it('should enable save button only when there are changes', function() {
      expect(com.saveBtn.isEnabled()).toBe(false);
      page.alias.sendKeys('update');
      expect(com.saveBtn.isEnabled()).toBe(true);
      browser.refresh();

      expect(com.saveBtn.isEnabled()).toBe(false);
      page.fileFrequency.sendKeys('1');
      expect(com.saveBtn.isEnabled()).toBe(true);
      browser.refresh();
    });

      it('should support special characters in alias text input and show required field warning', function() {
          expect(page.aliasRequired.isPresent()).toBe(false);
          page.alias.clear();
          expect(page.aliasRequired.isDisplayed()).toBe(true);
          page.alias.sendKeys(alias);
          expect(com.saveBtn.isEnabled()).toBe(true);
      });


      it('should show commas for thousands separation in numeric inputs and show required field warnings', function() {
          expect(page.fileFrequencyRequired.isPresent()).toBe(false);
          com.clearInput(page.fileFrequency);
          expect(page.fileFrequencyRequired.isDisplayed()).toBe(true);
          page.fileFrequency.sendKeys(fileFrequency);
          expect(page.fileFrequency.getAttribute('value')).toEqual(common.addThousandsSeparator(fileFrequency));
          expect(page.cookieExpirationRequired.isPresent()).toBe(false);
          com.clearInput(page.cookieExpiration);
          expect(page.cookieExpirationRequired.isDisplayed()).toBe(true);
          page.cookieExpiration.sendKeys(cookieExpirationInvalid);
          expect(page.cookieExpiration.getAttribute('value')).toEqual(common.addThousandsSeparator(cookieExpirationInvalid));
          expect(com.saveBtn.isEnabled()).toBe(false);
      });

      it('should show min and max number warning', function() {
          expect(page.cookieExpirationRequired.isPresent()).toBe(false);
          com.clearInput(page.cookieExpiration);
          page.cookieExpiration.sendKeys(cookieExpirationLessThanMin);
          expect(page.cookieExpiration.getAttribute('value')).toEqual(cookieExpirationLessThanMin);
          expect(page.cookieExpirationMinRequired.isDisplayed()).toBe(true);
          expect(com.saveBtn.isEnabled()).toBe(false);
          com.clearInput(page.cookieExpiration);
          page.cookieExpiration.sendKeys(cookieExpirationMoreThanMax);
          expect(page.cookieExpiration.getAttribute('value')).toEqual(cookieExpirationMoreThanMax);
          expect(page.cookieExpirationMaxRequired.isDisplayed()).toBe(true);
          expect(com.saveBtn.isEnabled()).toBe(false);

          // set valid cookieExpiration
          com.clearInput(page.cookieExpiration);
          page.cookieExpiration.sendKeys(cookieExpirationValid);
          expect(page.cookieExpiration.getAttribute('value')).toEqual(cookieExpirationValid);
          expect(com.saveBtn.isEnabled()).toBe(true);
      });

      it('should modify Key Type configurations', function() {
          page.urlPath.click();
          expect(page.matchCookieName.isEnabled()).toBeFalsy();
          page.cookie.click();
          expect(page.matchCookieName.isEnabled()).toBeTruthy();
          page.matchCookieName.clear();
          page.matchCookieName.sendKeys(matchCookieName);
          expect(com.saveBtn.isEnabled()).toBe(true);
      });

    it('should display error when trying add duplicate cookie names', function() {
        page.failThroughCookies.click();
        expect(page.defaultKey.isEnabled()).toBeFalsy();


        page.allFailThroughDefaultsName.count().then(function (originalCount) {
            for (var i = originalCount -1; i > 0; i--) {
                page.allFTDDeleteButtons.get(i).click();
            }
            expect(page.allFailThroughDefaultsName.count()).toEqual(1);
            page.addFailThroughDefault.click();
        });

      expect(page.allFailThroughDefaultsName.get(1).isEnabled()).toBeTruthy();
      page.allFailThroughDefaultsName.get(1).clear();
      page.allFailThroughDefaultsName.get(1).sendKeys(failThroughDefaultName1);
      com.clearInput(page.allFailThroughDefaultsValue.get(1));
      page.allFailThroughDefaultsValue.get(1).sendKeys(failThroughDefaultValue1);
      expect(page.addFailThroughDefault.isEnabled()).toBeTruthy();
      page.addFailThroughDefault.click();
      expect(page.allFailThroughDefaultsName.count()).toEqual(3);
      expect(page.allFailThroughDefaultsValue.count()).toEqual(3);
      page.allFailThroughDefaultsName.get(2).sendKeys(failThroughDefaultName1);
      page.allFailThroughDefaultsValue.get(2).sendKeys(failThroughDefaultValue1);
      expect(com.saveBtn.isEnabled()).toBeFalsy();
      page.allFTDDeleteButtons.get(2).click();
      expect(com.saveBtn.isEnabled()).toBeTruthy();
    });


    it('should check/uncheck the email content channel checkbox', function() {
      page.emailCChannel.click();
      page.emailCChannel.isSelected().then(function (selected) {
        if (selected !== emailCChannel) {
          page.emailCChannel.click();
        }
        expect(page.emailCChannel.isSelected()).toEqual(emailCChannel);
      });
    });

    it('should check/uncheck the display content channel checkbox', function() {
      page.displayCChannel.click();
      page.displayCChannel.isSelected().then(function (selected) {
        if (selected !== displayCChannel) {
          page.displayCChannel.click();
        }
        expect(page.displayCChannel.isSelected()).toEqual(displayCChannel);
      });
    });

    it('should check/uncheck the site content channel checkbox', function() {
      page.siteCChannel.click();
      page.siteCChannel.isSelected().then(function (selected) {
        if (selected !== siteCChannel) {
          page.siteCChannel.click();
        }
        expect(page.siteCChannel.isSelected()).toEqual(siteCChannel);
      });
    });

    it('should disable/enable Extractable Cookies configuration', function() {
      page.enableExtractableCookies.click();
      expect(page.allExtractableCookies.get(0).isEnabled()).toBeFalsy();
      page.enableExtractableCookies.click();
      expect(page.allExtractableCookies.get(0).isEnabled()).toBeTruthy();
    });

    it('should modify Extractable Cookies configuration', function() {
      page.allExtractableCookies.count().then(function (originalCount) {
          for (var i = originalCount -1; i > 0; i--) {
              page.allECDeleteButtons.get(i).click();
          }
          expect(page.allExtractableCookies.count()).toEqual(1);
      });
      page.addExtractableCookie.click();
      expect(page.allExtractableCookies.count()).toEqual(2);
      expect(page.allECDeleteButtons.count()).toEqual(2);
      page.allExtractableCookies.get(0).clear();
      page.allExtractableCookies.get(0).sendKeys(extractableCookie1);
      expect(page.addExtractableCookie.isEnabled()).toBeFalsy();
      page.allExtractableCookies.get(1).sendKeys(extractableCookie2);
      expect(page.addExtractableCookie.isEnabled()).toBeTruthy();
      page.allECDeleteButtons.get(1).click();
      expect(page.allExtractableCookies.count()).toEqual(1);
      expect(page.allECDeleteButtons.count()).toEqual(0);
      expect(page.addExtractableCookie.isEnabled()).toBeTruthy();
      page.allExtractableCookies.count().then(function (originalCount) {
          for (var i = originalCount -1; i > 0; i--) {
              page.allECDeleteButtons.get(i).click();
          }
          expect(page.allExtractableCookies.count()).toEqual(1);
      });
    });

    it('should disable/enable Fail-Through Defaults configuration', function() {
      page.enableFailThroughDefaults.click();
      expect(page.key.isEnabled()).toBeFalsy();
      page.enableFailThroughDefaults.click();
      expect(page.key.isEnabled()).toBeTruthy();
    });

    it('should modify Fail-Through Defaults configuration', function() {
      page.key.click();
      expect(page.defaultKey.isEnabled()).toBeTruthy();
      expect(page.allFailThroughDefaultsName.get(0).isEnabled()).toBeFalsy();
      page.defaultKey.clear();
      page.defaultKey.sendKeys(defaultKey);
      page.failThroughCookies.click();
      expect(page.defaultKey.isEnabled()).toBeFalsy();

        page.allFailThroughDefaultsName.count().then(function (originalCount) {
            for (var i = originalCount -1; i > 0; i--) {
                page.allFTDDeleteButtons.get(i).click();
            }
            expect(page.allFailThroughDefaultsName.count()).toEqual(1);
        });
      page.addFailThroughDefault.click();
      expect(page.allFailThroughDefaultsName.get(0).isEnabled()).toBeTruthy();
      page.allFailThroughDefaultsName.get(0).clear();
      page.allFailThroughDefaultsName.get(0).sendKeys(failThroughDefaultName1);
      com.clearInput(page.allFailThroughDefaultsValue.get(0));
      page.allFailThroughDefaultsValue.get(0).sendKeys(failThroughDefaultValue1);
      expect(page.allFailThroughDefaultsName.count()).toEqual(2);
      expect(page.allFailThroughDefaultsValue.count()).toEqual(2);
      expect(page.allFTDDeleteButtons.count()).toEqual(2);
      page.allFailThroughDefaultsName.get(1).clear();
      page.allFailThroughDefaultsName.get(1).sendKeys(failThroughDefaultName2);
      com.clearInput(page.allFailThroughDefaultsValue.get(1));
      page.allFailThroughDefaultsValue.get(1).sendKeys(failThroughDefaultValue2);
      expect(page.addFailThroughDefault.isEnabled()).toBeTruthy();
      page.addFailThroughDefault.click();
      expect(page.allFailThroughDefaultsName.count()).toEqual(3);
      expect(page.allFailThroughDefaultsValue.count()).toEqual(3);
      expect(page.allFTDDeleteButtons.count()).toEqual(3);
      expect(page.addFailThroughDefault.isEnabled()).toBeFalsy();
      page.allFailThroughDefaultsName.get(2).sendKeys(failThroughDefaultName3);
      page.allFailThroughDefaultsValue.get(2).sendKeys(failThroughDefaultValue3);
      expect(page.addFailThroughDefault.isEnabled()).toBeTruthy();
      page.allFTDDeleteButtons.get(1).click();
      expect(page.allFailThroughDefaultsName.count()).toEqual(2);
      expect(page.allFailThroughDefaultsValue.count()).toEqual(2);
      expect(page.allFTDDeleteButtons.count()).toEqual(2);
      expect(page.addFailThroughDefault.isEnabled()).toBeTruthy();

        page.allFailThroughDefaultsName.count().then(function (originalCount) {
            for (var i = originalCount -1; i > 0; i--) {
                page.allFTDDeleteButtons.get(i).click();
            }
            expect(page.allFailThroughDefaultsName.count()).toEqual(1);
        });
    });

    it('should disable/enable Cookie Overwrite Exceptions configuration', function() {
      page.enableOverwriteExceptions.click();
      expect(page.allOverwriteExceptions.get(0).isEnabled()).toBeFalsy();
      page.enableOverwriteExceptions.click();
      expect(page.allOverwriteExceptions.get(0).isEnabled()).toBeTruthy();
    });

    it('should modify Cookie Overwrite Exceptions configuration', function() {
      page.allOverwriteExceptions.count().then(function (originalCount) {
          for (var i = originalCount -1; i > 0; i--) {
              page.allOEDeleteButtons.get(i).click();
          }
          expect(page.allOverwriteExceptions.count()).toEqual(1);
      });
      expect(page.allOEDeleteButtons.count()).toEqual(0);
      page.allOverwriteExceptions.get(0).clear();
      page.allOverwriteExceptions.get(0).sendKeys(cookieOverWrite1);
      expect(page.addOverwriteException.isEnabled()).toBeTruthy();
      page.addOverwriteException.click();
      page.allOverwriteExceptions.get(1).sendKeys(cookieOverWrite2);
      page.addOverwriteException.click();
      page.allOverwriteExceptions.get(2).sendKeys(cookieOverWrite3);
      expect(page.allOverwriteExceptions.count()).toEqual(3);
      expect(page.allOEDeleteButtons.count()).toEqual(3);
      expect(page.addOverwriteException.isEnabled()).toBeTruthy();
      expect(page.allOverwriteExceptions.count()).toEqual(3);
      page.allOEDeleteButtons.get(2).click();
      expect(page.allOEDeleteButtons.count()).toEqual(2);
      expect(page.addOverwriteException.isEnabled()).toBeTruthy();
      page.allOverwriteExceptions.count().then(function (originalCount) {
        for (var i = originalCount -1; i > 0; i--) {
            page.allOEDeleteButtons.get(i).click();
        }
        expect(page.allOverwriteExceptions.count()).toEqual(1);
      });
    });

    it('should save record', function() {
      com.saveBtn.click();
      browser.refresh();
      expect(page.alias.getAttribute('value')).toEqual(alias);
      expect(page.emailCChannel.isSelected()).toEqual(emailCChannel);
      expect(page.displayCChannel.isSelected()).toEqual(displayCChannel);
      expect(page.siteCChannel.isSelected()).toEqual(siteCChannel);
      expect(page.fileFrequency.getAttribute('value')).toEqual(common.addThousandsSeparator(fileFrequency));
      expect(page.cookieExpiration.getAttribute('value')).toEqual(common.addThousandsSeparator(cookieExpirationValid));
      expect(page.matchCookieName.getAttribute('value')).toEqual(matchCookieName);
      expect(page.allExtractableCookies.get(0).getAttribute('value')).toEqual(extractableCookie1);
      expect(page.defaultKey.getAttribute('value')).toEqual(defaultKey);

      expect(page.allFailThroughDefaultsName.get(0).getAttribute('value')).toEqual(failThroughDefaultName1);
      expect(page.allFailThroughDefaultsValue.get(0).getAttribute('value'))
          .toEqual(failThroughDefaultValue1);
      expect(page.allOverwriteExceptions.get(0).getAttribute('value')).toEqual(cookieOverWrite1);
    });

    it('should delete all the cookie names, disable toggles and save', function() {
      browser.refresh();
      expect(com.saveBtn.isEnabled()).toBe(false);
      page.allOverwriteExceptions.get(0).clear();
      page.allExtractableCookies.get(0).clear();
      page.allFailThroughDefaultsName.get(0).clear();
      page.enableExtractableCookies.click();
      page.enableFailThroughDefaults.click();
      page.enableOverwriteExceptions.click();
      expect(com.saveBtn.isEnabled()).toBe(true);
    })
  });
});

module.exports = adm;


