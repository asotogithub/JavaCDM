'use strict';

var StatusPage = function () {
    this.statusHeader = element(by.cssContainingText('h3', 'Status Page'));
    this.cmDbConnectionValid = element(by.id('cmDbConnectionValid'));
    this.cmDbStatusMessage = element(by.id('cmDbStatusMessage'));
    this.metricsDbConnectionValid = element(by.id('metricsDbConnectionValid'));
    this.metricsDbStatusMessage = element(by.id('metricsDbStatusMessage'));
    this.apiVersion = element(by.id('apiVersion'));
    this.uiVersion = element(by.id('uiVersion'));
};

module.exports = new StatusPage();
