'use strict';

var admPage = function() {
    this.columnNames = [
        {
            name: 'Advertiser',
            binding: 'adm.advertiserName'
        },
        {
            name: 'File Name',
            binding: 'adm.fileNamePrefix'
        },
        {
            name: 'Alias',
            binding: 'adm.fileNamePrefix'
        },
        {
            name: 'Campaign Status',
            binding: 'adm.isActive'
        },
        {
            name: 'Domain',
            binding: 'adm.domain'
        },
        {
            name: 'Attempts',
            binding: 'adm.attempts'
        },
        {
            name: 'Match Rate',
            binding: 'adm.matchRate'
        },
        {
            name: 'Last File Update',
            binding: 'adm.latestUpdate'
        }
    ];
  this.dataRows = element.all(by.css('tr[data-ng-repeat$="$data"]'));
  this.advertiser = element(by.id('advertiser'));
  this.alias = element(by.id('alias'));
  this.aliasRequired = element(by.id('aliasRequired'));
  this.cookieExpiration = element(by.id('cookieExpiration'));
  this.cookieExpirationRequired = element(by.id('cookieExpirationRequired'));
  this.cookieExpirationMinRequired = element(by.id('cookieExpirationMin'));
  this.cookieExpirationMaxRequired = element(by.id('cookieExpirationMax'));
  this.displayCChannel = element(by.id('displayCChannel'));
  this.domain = element(by.id('domain'));
  this.emailCChannel = element(by.id('emailCChannel'));
  this.fileFrequency = element(by.id('fileFrequency'));
  this.fileFrequencyRequired = element(by.id('fileFrequencyRequired'));
  this.fileName = element(by.id('fileName'));
  this.siteCChannel = element(by.id('siteCChannel'));
  this.campaignStatus = element(by.id('campaignStatus'));
  this.summaryHeader = element(by.id('admHeader'));
  this.admDataVis = element(by.css('[data-ng-controller="AdmReporting as admReporting"]'));
  this.admChart = element(by.id('admChart'));
  this.listOfAdmBtn = element(by.id('listOfAdmBtn'));
  this.chart = this.admChart.element(by.css('[config="admReporting.chartConfig"]'));
  this.chartCollapse = this.admChart.element(by.css('.fa-minus'));
  this.chartExpand = this.admChart.element(by.css('.fa-plus'));
  this.startDate = element(by.id('startDate'));
  this.endDate = element(by.id('endDate'));
  this.lowerThan = element(by.id('lowerThanError'));

  //Summary Cookie Configuration
  this.keyTypeContainer = element(by.id('keyTypeContainer'));
  this.urlPath = element(by.id('urlPath'));
  this.cookie = element(by.id('cookie'));
  this.matchCookieName = element(by.id('matchCookieName'));

  this.extractableCookiesContainer = element(by.id('extractableCookiesContainer'));
  this.enableExtractableCookies = element(by.css('#enableExtractableCookies:first-of-type + span'));
  this.addExtractableCookie = element(by.id('addExtractableCookie'));
  this.allExtractableCookies = this.extractableCookiesContainer.all(by.css(
    'input.form-control[data-ng-model="item.cookieName"]'));
  this.allECDeleteButtons = this.extractableCookiesContainer.all(by.css(
    'button.btn[data-ng-click="vmConfig.removeCookie(vmConfig.admConfig.extractableCookiesArray, $index, vmConfig.extractableCookiesForm)"]'));

  this.failThroughContainer = element(by.id('failThroughContainer'));
  this.enableFailThroughDefaults = element(by.css('#enableFailThroughDefaults:first-of-type + span'));
  this.key = element(by.id('key'));
  this.cookie = element(by.id('cookie'));
  this.defaultKey = element(by.id('defaultKey'));
  this.failThroughCookies = element(by.id('failThroughCookies'));
  this.addFailThroughDefault = element(by.id('addFailThroughDefault'));
  this.allFailThroughDefaultsName = this.failThroughContainer.all(by.css(
    'input.form-control[data-ng-model="item.cookieName"]'));
  this.allFailThroughDefaultsValue = this.failThroughContainer.all(by.css(
    'input.form-control[data-ng-model="item.cookieValue"]'));
  this.allFTDDeleteButtons = this.failThroughContainer.all(by.css(
    'button.btn[data-ng-click="vmConfig.removeCookie(vmConfig.admConfig.failThroughDefaultsArray, $index, vmConfig.failThroughDefaultsForm)"]'));

  this.overwriteExceptionsContainer = element(by.id('overwriteExceptionsContainer'));
  this.enableOverwriteExceptions = element(by.css('#enableCookieOverwriteExceptions:first-of-type + span'));
  this.addOverwriteException = element(by.id('addOverwriteException'));
  this.allOverwriteExceptions = this.overwriteExceptionsContainer.all(by.css(
    'input.form-control[data-ng-model="item.cookieName"]'));
  this.allOEDeleteButtons = this.overwriteExceptionsContainer.all(by.css(
    'button.btn[data-ng-click="vmConfig.removeCookie(vmConfig.admConfig.cookieOverwriteExceptionsArray, $index, vmConfig.cookieOverwriteExceptionsForm);"]'));

  this.closeErrorPopup = element(by.id('btn-error-pop-close'));
};

module.exports = new admPage();
