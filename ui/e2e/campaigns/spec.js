

describe('Campaigns', function() {
  var campaignsGrid = require('./campaigns-grid.spec'),
      campaignsDetails = require('./campaigns-detail.spec'),
      campaignsCreate = require('./campaigns-create.spec'),
      campaignsBootstrap = require('../utilities/campaigns-bootstrap.spec'),
      nav = require('../page-object/navigation.po');

  it('Execute campaigns suites', function() {
    global.countCampaigns++;
    browser.wait(function() {
      return nav.campaignsItem.isPresent();
    }).then(function () {
      campaignsBootstrap.setupCampaignData('2 Protractor Advertiser for filtering', '2Protractor Brand for filtering',
        'Protractor Test Campaign for Campaign list filter');
      campaignsCreate();
      campaignsDetails();
      campaignsGrid();
    });
  });

});
