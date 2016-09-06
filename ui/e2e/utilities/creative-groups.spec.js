'use strict';

var CreativeGroup = function() {
  this.creativeGroupDefault = {
    creativeGroupName: 'Protractor Creative Group Default' + new Date().getTime(),
    defaultGroup: true
  };

  this.creativeGroupAllOptions = {
    creativeGroupName: 'Non Default Creative Group' + new Date().getTime(),
    enableGroupWeight: true,
    weight: '55',
    enableFrequencyCap: true,
    frequencyCap: '55',
    frequencyCapWindow: '20',
    enablePriority: true,
    priority: '11'
  };

  this.creativeGroupSmoke = {
    creativeGroupName: 'Smoke Test Creative Group' + new Date().getTime(),
    enableGroupWeight: true,
    weight: '60',
    enableFrequencyCap: true,
    frequencyCap: '57',
    frequencyCapWindow: '21',
    enablePriority: true,
    priority: '12'
  };

};

module.exports = new CreativeGroup();
