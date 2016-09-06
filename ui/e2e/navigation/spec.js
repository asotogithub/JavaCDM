describe('Navigation', function() {
  var forbiddenPage = require('./forbidden.spec.js'),
      login = require('../utilities/login.spec'),
      logout = require('../utilities/logout.spec'),
      sideNav = require('./side-nav.spec.js'),
      users = require('../utilities/users.spec'),
      breadcrumb = require('./breadcrumb.spec'),
      dataSetup = require('./setup-navigation.spec'),
      user = users.auto01,
      limitedUser = users.auto03,
      campaignName = 'Navigation Protractor Campaign',
      creativeGroup = require('../utilities/creative-groups.spec'),
      ioName = 'Protractor IO';

  it('Execute navigation suite', function() {
    dataSetup(campaignName, ioName, creativeGroup.creativeGroupDefault);
    sideNav();
    breadcrumb(campaignName, ioName, creativeGroup.creativeGroupDefault);

    login(limitedUser.email, limitedUser.password);
    forbiddenPage();
    logout();
  });

});
