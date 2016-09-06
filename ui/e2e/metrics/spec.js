

describe('Metrics', function() {
  var campaignGrid = require('./metrics-campaigns-grid.spec'),
      campaignSummary = require('./metrics-campaigns-summary.spec'),
      noMetrics = require('./no-metrics.spec'),
      login = require('../utilities/login.spec'),
      logout = require('../utilities/logout.spec'),
      users = require('../utilities/users.spec'),
      user = users.metrics,
      noMetricsUser = users.auto01;

  it('Execute metrics suites', function() {
      login(user.email, user.password);
      campaignGrid();
      campaignSummary();
      logout();

      login(noMetricsUser.email, noMetricsUser.password);
      noMetrics();  
      logout();
  });

});
