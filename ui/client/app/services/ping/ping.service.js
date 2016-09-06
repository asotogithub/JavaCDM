(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('PingService', PingService);

    PingService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler'
    ];

    function PingService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler) {
        var pingResource = $resource(API_SERVICE + 'SiteMeasurementEventPings/:id/:criteria',
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
            delete: function (idPing) {
                var deferred = $q.defer();

                pingResource.update({
                    criteria: 'deletePingEvents'
                }, {
                    records: {
                        Long: idPing
                    }
                }).$promise
                    .then(deletePingComplete)
                    .catch(errorHandler('Error while performing delete request', deferred));

                function deletePingComplete(response) {
                    deferred.resolve(response);
                }

                return deferred.promise;
            },

            save: function (pings) {
                var deferred = $q.defer();

                pingResource.save({
                    criteria: 'bulkCreate'
                }, {
                    records: [
                        {
                            SmEventDTO: pings
                        }
                    ]
                }).$promise.then(savePingComplete)
                    .catch(savePingFailed);

                function savePingComplete(response) {
                    deferred.resolve(response);
                }

                function savePingFailed(error) {
                    deferred.reject(error);
                }

                return deferred.promise;
            },

            update: function (pings) {
                var deferred = $q.defer();

                pingResource.update({
                    criteria: 'bulkUpdate'
                }, {
                    records: [
                        {
                            SmEventDTO: pings
                        }
                    ]
                }).$promise.then(updatePingComplete)
                    .catch(updatePingFailed);

                function updatePingComplete(response) {
                    deferred.resolve(response);
                }

                function updatePingFailed(error) {
                    deferred.reject(error);
                }

                return deferred.promise;
            }
        };
    }
})();
