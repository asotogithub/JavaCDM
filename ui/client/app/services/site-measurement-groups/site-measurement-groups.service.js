(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('SiteMeasurementGroupsService', SiteMeasurementGroupsService);

    SiteMeasurementGroupsService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function SiteMeasurementGroupsService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var siteMeasurementGroupsResource = $resource(API_SERVICE + 'SiteMeasurementGroups/:id/:criteria',
                {
                    id: '@id',
                    criteria: '@criteria'
                }, {
                    get: {
                        method: 'GET'
                    },
                    update: {
                        method: 'PUT'
                    },
                    delete: {
                        method: 'DELETE'
                    }
                }
            ),
            siteMeasurementsResource = $resource(API_SERVICE + 'SiteMeasurements/:id/:criteria/:criteriaId'),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            checkGroupNameUnique: function (groupName) {
                var measurementsDefer = $q.defer();

                siteMeasurementGroupsResource.get({
                    query: groupName
                }).$promise
                    .then(checkGroupNameUniqueComplete)
                    .catch(errorHandler('Cannot check if group name is unique', measurementsDefer));

                function checkGroupNameUniqueComplete(response) {
                    measurementsDefer.resolve(response);
                }

                return measurementsDefer.promise;
            },

            saveEvent: function (event) {
                var measurementsDefer = $q.defer();

                siteMeasurementGroupsResource.save(event).$promise
                    .then(saveEventComplete)
                    .catch(errorHandler('Error while performing save event request', measurementsDefer));

                function saveEventComplete(eventData) {
                    measurementsDefer.resolve(eventData.toJSON());
                }

                return measurementsDefer.promise;
            },

            getGroupList: function (siteMeasurementId) {
                var measurementsDefer = $q.defer();

                siteMeasurementsResource.get({
                    id: siteMeasurementId,
                    criteria: 'groups'
                }).$promise
                    .then(getGroupsComplete)
                    .catch(errorHandler('Cannot get Groups', measurementsDefer));

                function getGroupsComplete(response) {
                    measurementsDefer.resolve(
                        [].concat(lodash.result(response, 'records[0].SmGroup', []))
                    );
                }

                return measurementsDefer.promise;
            }
        };
    }
})();
