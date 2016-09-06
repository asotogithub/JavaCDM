

describe('Creative Groups', function() {
  var create = require('./creative-groups-create.spec'),
      defaultGroup = require('./creative-groups-default.spec'),
      update = require('./creative-groups-update.spec'),
      grid = require('./creative-groups-grid.spec'),
      geo = require('./creative-groups-geo.spec'),
      cookie = require('./creative-groups-cookie.spec'),
      createSmoke = require('./smoke-create.spec'),
      dayPart = require('./creative-groups-daypart.spec'),
      dataSetup = require('../creative-groups/setup-creative-group'),
      creativeGroupCreative = require('./creative-group-creative.spec'),
      creativeGroupCreativeDelete = require('./creative-group-creative-delete.spec'),
      nav = require('../page-object/navigation.po'),
      creativeGroupBootstrap = require('../utilities/creative-group-bootstrap.spec'),
      campaignNameCreativeGroup = 'Protractor Test Campaign for Creative Group',
      bootstrapData = {
          creatives : ['image-1-350x250.jpg','image-2-350x250.jpg', 'image-3-350x250.jpg', 'image-4-350x250.jpg'],
          creativeGroup: 'Protractor Test CG 01',
          io: {
              name: 'IO 01',
              number: 1
          },
          placement: [
              {
                  name: 'Placement 01'
              }
          ]
      };

  it('Execute creative groups suites', function() {
      global.countCampaigns++;
      browser.wait(function() {
          return nav.campaignsItem.isPresent();
      }).then(function(){
          creativeGroupBootstrap.setupCreativeGroupData(
              campaignNameCreativeGroup,
              bootstrapData.io,
              bootstrapData.placement,
              bootstrapData.creatives,
              bootstrapData.creativeGroup
          );
          dataSetup();
          grid();
          update();
          defaultGroup();
          create();
          geo();
          cookie();
          dayPart();
          createSmoke();
          creativeGroupCreative(campaignNameCreativeGroup);
          creativeGroupCreativeDelete(campaignNameCreativeGroup, bootstrapData.creativeGroup);
      });
  });
});
