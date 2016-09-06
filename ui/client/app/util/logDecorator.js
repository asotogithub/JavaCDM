(function () {
    'use strict';

    angular
        .module('logDecorator', ['logglyLogger', 'ngLodash'])
        .config(LogDecoratorConfig);

    LogDecoratorConfig.$inject = [
        'LogglyLoggerProvider',
        'DEPLOY_ENV',
        'LOGGLY_LEVEL',
        'lodash'
    ];

    function LogDecoratorConfig(LogglyLoggerProvider, DEPLOY_ENV, LOGGLY_LEVEL, lodash) {
        // See https://github.com/ajbrown/angular-loggly-logger#Configuration
        var tagTemplate = lodash.template('truadvertiser-<%= deployEnv %>');

        if (!LOGGLY_LEVEL || LOGGLY_LEVEL === 'OFF') {
            return;
        }

        LogglyLoggerProvider
            .level(LOGGLY_LEVEL)
            .inputToken('ec5455b8-06a5-4f50-a92c-309b355c2651')
            .useHttps(true)
            .includeUrl(true)
            .includeTimestamp(true)
            .inputTag(tagTemplate(
                {
                    deployEnv: DEPLOY_ENV
                }
            ))
            .sendConsoleErrors(true);
    }
})();
