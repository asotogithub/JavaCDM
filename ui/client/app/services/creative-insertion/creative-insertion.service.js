(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('CreativeInsertionService', CreativeInsertionService);

    CreativeInsertionService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'CONSTANTS',
        'ErrorRequestHandler'
    ];

    function CreativeInsertionService(
        $q,
        $resource,
        API_SERVICE,
        CONSTANTS,
        ErrorRequestHandler) {
        var creativeInsertionResource = $resource(API_SERVICE + 'CreativeInsertions/:id/:criteria',
                {
                    id: '@id',
                    criteria: '@criteria'
                },
                {
                    update: {
                        method: 'PUT'
                    }
                }),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            bulkUpdate: function (ciList) {
                var ciListUpdated = $q.defer();

                creativeInsertionResource.update({
                    criteria: 'bulkUpdate'
                }, ciList).$promise
                    .then(bulkUpdateComplete)
                    .catch(errorHandler('Error while performing update creative insertion bulk request',
                        ciListUpdated));

                function bulkUpdateComplete(response) {
                    ciListUpdated.resolve(response);
                }

                return ciListUpdated.promise;
            }
        };
    }
})();
