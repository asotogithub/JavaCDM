

describe('Creative Groups', function() {
  var createSmoke = require('./smoke-create.spec'),
      dataSetup = require('../creative-groups/setup-creative-group');

  it('Execute creative groups smoke suites', function() {
  	dataSetup();
  	createSmoke();
  });

});
