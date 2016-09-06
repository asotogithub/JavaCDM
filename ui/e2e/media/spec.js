

describe('Media', function() {
  	var bootstrapData = {
            creatives : ['image-1-350x250.jpg','image-2-350x250.jpg', 'image-3-350x250.jpg',
                'image-4-350x250.jpg', 'creative-gif-350x250.zip', 'third-party-creative.3rd'],
            creativesDisplayName : ['image-1-350x250','image-2-350x250', 'image-3-350x250',
                'image-4-350x250', 'creative-gif-350x250'],
            placements : ['Media Placement for Protractor', 'Protractor Placement 2',
                'Protractor Placement 3', 'Third Party Placement'],
            io : ['New IO for Media Protractor'],
            sites : ['Protractor Site'],
            tagInjection: [
                {
                    name: 'TagSchedule 01',
                    optOutUrl: 'http://www.protractor-test.com'
                },
                {
                    name: 'TagSchedule 02',
                    optOutUrl: 'http://www.protractor-test.com'
                }
            ],
            package: {
                name: 'Package 01',
                placement: [
                    {
                        name: 'Placement 01'
                    },
                    {
                        name: 'Placement 02'
                    }
                ]
            }
        },
        campaignNameMedia = 'Protractor Test Campaign for Media',
        mediaGrid = require('./media-grid.spec'),
      	dataSetup = require('./setup-media'),
      	ioCreate = require('./io-create.spec'),
      	ioUpdate = require('./io-update.spec'),
        plcmntCreate = require('./placement-create.spec'),
        plcmntUpdate = require('./placement-update.spec'),
        plcmntCost = require('./placement-cost.spec'),
        packageUpdate = require('./package-update.spec'),
        packageCost = require('./package-cost.spec'),
        ioBulk = require('./io-bulk.spec'),
        nav = require('../page-object/navigation.po'),
        login = require('../utilities/login.spec'),
        logout = require('../utilities/logout.spec'),
        mediaBootstrap = require('../utilities/media-bootstrap'),
        PackagePlacementAssociation = require('./package-placement-association.spec'),
        PackageListView = require('./package-view.spec');


    it('should execute Media tests', function() {
      browser.wait(function() {
              return nav.campaignsItem.isPresent();
          }).then(function(){
              mediaBootstrap.setupMediaData(campaignNameMedia, bootstrapData.creatives, bootstrapData.io,
                  111111, bootstrapData.package, bootstrapData.placements, bootstrapData.tagInjection);
              global.countCampaigns++;
              login(mediaBootstrap.getUserCredentials().userName, mediaBootstrap.getUserCredentials().userPassword);
              mediaGrid(campaignNameMedia, bootstrapData);
              ioCreate(campaignNameMedia);
              ioUpdate(campaignNameMedia, bootstrapData);
              plcmntCreate(campaignNameMedia, bootstrapData);
              plcmntUpdate(campaignNameMedia, bootstrapData);
              packageUpdate(campaignNameMedia, bootstrapData);
              plcmntCost(campaignNameMedia, bootstrapData);
              packageCost(campaignNameMedia, bootstrapData);
              PackagePlacementAssociation(campaignNameMedia, bootstrapData.io[0], bootstrapData.package.name);
              PackageListView(campaignNameMedia,
                  bootstrapData.io[0],
                  bootstrapData.package.name,
                  bootstrapData.package.placement[0].name);
              // Commenting out ioBulk suite until it can be run locally
              // ioBulk(campaignNameMedia);
              logout();
          });
  });

});
