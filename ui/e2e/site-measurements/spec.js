

describe('Site Measurements', function() {
  var siteMeasurementsPage = require('./site-measurements-grid.spec'),
      siteMeasurementsDetail = require('./site-measurements-detail.spec'),
      siteMeasurementsEventsPings = require('./site-measurements-events-pings.spec'),
      siteMeasurementsCampaignAssociations = require('./site-measurements-campaign-associations.spec'),
      nav = require('../page-object/navigation.po'),
      login = require('../utilities/login.spec'),
      logout = require('../utilities/logout.spec'),
      users = require('../utilities/users.spec'),
      user = users.auto01;

	it('Execute site measurement suite', function() {
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        }).then(function() {
            login(user.email, user.password);
            siteMeasurementsPage();
            siteMeasurementsDetail();
            siteMeasurementsEventsPings();
            siteMeasurementsCampaignAssociations();
            logout();
        });
	 });

});
