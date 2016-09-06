'use strict';

var MediaData = function() {
    this.placementWithPkg = {
      campaignName: 'Media Protractor Campaign',
      ioName: 'Protractor IO',
      packageName: 'Package 1',
      name: 'Placement with package',
      site: 'Protractor Site',
      section: 'Home',
      size: '350x250',
      adSpend: '5000.00',
      rate: '111.00',
      rateType: 'CPA',
      endDate: '12/31/2020',
      inventory: '46'
    };

    this.placementNoPkg = {
      campaignName: 'Media Protractor Campaign',
      ioName: 'Protractor IO',
      name: 'Placement without package',
      site: 'Protractor Site',
      section: 'Home',
      size: '350x250',
      adSpend: '222.00',
      rate: '5001.00',
      rateType: 'CPM',
      startDate: '11/11/2017',
      endDate: '11/11/2018',
      inventory: '45'
    };

    this.placementCost = {
      campaignName: 'Media Protractor Campaign',
      ioName: 'Protractor IO',
      name: 'Placement with cost',
      site: 'Protractor Site',
      section: 'Home',
      size: '350x250',
      adSpend: '555555.00',
      rate: '0.45'
    };

    this.packageCost = {
      campaignName: 'Media Protractor Campaign',
      ioName: 'Protractor IO',
      packageName: 'Cost package',
      name: 'Placement with cost',
      site: 'Protractor Site',
      section: 'Home',
      size: '350x250',
      adSpend: '555555.00',
      rate: '0.45'
    };

};

module.exports = new MediaData();
