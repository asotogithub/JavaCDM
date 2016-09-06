

describe('Restrictions', function() {
    var restrictionsBootstrap = require('../utilities/restrictions-bootstrap.spec'),
        nav = require('../page-object/navigation.po'),
        login = require('../utilities/login.spec'),
        logout = require('../utilities/logout.spec'),
        securityOverride = require('./security-override.spec'),
        bootstrapData = {
            advertiserNames: ['Protractor Restriction Advertiser 1', 'Protractor Restriction Advertiser 2'],
            brandNames: ['Protractor Restriction Brand 1', 'Protractor Restriction Brand 2'],
            campaignNames: ['Protractor Restriction Campaign 1', 'Protractor Restriction Campaign 2']
        };

    it('Execute restriction suites', function() {
        global.countCampaigns++;
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        }).then(function () {
            restrictionsBootstrap.setupData(bootstrapData);
            login(restrictionsBootstrap.getUserCredentials().userName,
                  restrictionsBootstrap.getUserCredentials().userPassword);
            securityOverride(bootstrapData, restrictionsBootstrap);
            logout();
        });
    });

});