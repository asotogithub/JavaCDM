

describe('ADM', function() {
  var admGrid = require('./adm-grid.spec'),
  	  summary = require('./adm-summary.spec'),
      login = require('../utilities/login.spec'),
      logout = require('../utilities/logout.spec'),
      users = require('../utilities/users.spec'),
      user = users.auto01;

	it('Execute ADM suite', function() {
	  login(user.email, user.password);
	  admGrid();
	  summary();
	  logout();
	 });

});
