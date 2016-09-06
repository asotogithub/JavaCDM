'use strict';

var API_HOST = 'http://localhost';

beforeEach(function () {
    module(function ($provide) {
        $provide.constant('API_HOST', API_HOST);
        $provide.constant('PUBLIC_CONTEXT', '');
        $provide.constant('OAUTH_CONTEXT', '');
        $provide.constant('API_VERSION', '');
        $provide.constant('DEPLOY_ENV', 'unit-test');
        $provide.constant('LOGGLY_LEVEL', 'OFF');
    });
});
