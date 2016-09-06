(function () {
    'use strict';

    angular
        .module('uiApp')
        .factory('ErrorRequestHandler', ErrorRequestHandler);

    ErrorRequestHandler.$inject = [
        '$log',
        'CONSTANTS',
        'DialogFactory'
    ];

    function ErrorRequestHandler($log,
                                 CONSTANTS,
                                 DialogFactory) {
        var service = {
            handle: handle,
            handleAndReject: handleAndReject
        };

        return service;

        function handle(logMessage, error, onSecurityRestriction) {
            $log.error(logMessage + ': ' + angular.toJson(error));

            if (error.status === 404) {
                if (onSecurityRestriction) {
                    onSecurityRestriction();
                }
            }
            else {
                DialogFactory.showDialog(CONSTANTS.DIALOG.TYPE.ERROR);
            }
        }

        function handleAndReject(logMessage, deferred, onSecurityRestriction) {
            return function (error) {
                $log.error(logMessage + ': ' + angular.toJson(error));

                if (error.status === 404) {
                    if (onSecurityRestriction) {
                        onSecurityRestriction();
                    }
                }
                else {
                    DialogFactory.showDialog(CONSTANTS.DIALOG.TYPE.ERROR);
                }

                if (deferred) {
                    deferred.reject(error);
                }
            };
        }
    }
})();
