describe('Tag Injection', function() {
    var common = require('../utilities/common.spec'),
        login = require('../utilities/login.spec'),
        logout = require('../utilities/logout.spec'),
        nav = require('../page-object/navigation.po'),
        tagInjectionGrid = require('./tag-injection-grid.spec'),
        tagAssociations = require('./tag-association.spec'),
        createTrackingTag = require('./create-tracking-tag.spec'),
        editTrackingTag = require('./edit-tracking-tag.spec'),
        deleteTrackingTag = require('./delete-tracking-tag.spec'),
        serverSideSearch = require('./server-side-search-placement.spec'),
        serverSideSearchFlyOut = require('./server-side-search-placement-fly-out.spec'),
        campaignTrackingTag = require('./tag-campaign.spec'),
        tagInjectionBootstrap = require('../utilities/tag-injection-bootstrap.spec'),
        users = require('../utilities/users.spec'),
        user = users.auto01,
        newTrackingTag = 'TagProtractor' + common.getRandomString(10),
        newCampaignTI = 'Campaign Tag Injection Protractor ' + common.getRandomString(5),
        bootstrapData = {
            io: {
                name: 'IO 01',
                number: 1
            },
            placement: [
                {
                    name: 'Placement 01'
                },
                {
                    name: 'Placement 02'
                }
            ],
            tagInjection: [
                {
                    name: 'TagInjection 01',
                    optOutUrl: 'http://www.protractor-test.com'
                },
                {
                    name: 'TagInjection 02',
                    optOutUrl: 'http://www.protractor-test.com'
                }
            ]
        };

    it('Execute Tag Injection Suite', function() {
        global.countCampaigns++;
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        }).then(function(){
            tagInjectionBootstrap.setupCreativeGroupData(
                newCampaignTI,
                bootstrapData.io,
                bootstrapData.placement,
                bootstrapData.tagInjection);
            login(tagInjectionBootstrap.getUserCredentials().userName, tagInjectionBootstrap.getUserCredentials().userPassword);
            tagInjectionGrid();
            serverSideSearch();
            createTrackingTag(newTrackingTag);
            tagAssociations(newTrackingTag);
            editTrackingTag(newTrackingTag);
            serverSideSearchFlyOut(newTrackingTag);
            deleteTrackingTag();
            campaignTrackingTag(newCampaignTI);
            logout();
        });
    });
});
