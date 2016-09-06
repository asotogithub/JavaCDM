'use strict';

var Metrics = function() {
  this.metricsGrid = element(by.css('te-metrics'));
  this.campaignMetrics = ["Impressions","CTR","Cost","Conversions","eCPA"]
  this.allMetricsItems = element.all(by.css('te-metrics p'));
  this.metricsItemByName = function(name) {
    return this.metricsGrid.element(by.cssContainingText('p', name));
  };
  this.campaignsChart = element(by.id('campaignsChart'));
  this.chartMenu = element(by.css('.highcharts-button'));
};

module.exports = new Metrics();
