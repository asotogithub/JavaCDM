

describe('Login Functionality', function() {
  var loginPage = require('./login-page.spec.js'),
      rememberMe = require('./remember-me.spec.js');


  	it('Execute login suites', function() {
	  loginPage();
	  rememberMe();
	});

});
