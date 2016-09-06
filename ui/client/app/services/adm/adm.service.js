(function () {
    'use strict';

    angular.module('uiApp')
      .service('AdmService', AdmService);

    AdmService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'UserService',
        'lodash'
    ];

    function AdmService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        UserService,
        lodash) {
        var admListResource = $resource(API_SERVICE + 'Agencies/:agencyId/datasets'),
            admResource = $resource(API_SERVICE + 'Datasets/:id',
            {
                id: '@id'
            },
            {
                get: {
                    method: 'GET'
                },
                update: {
                    method: 'PUT'
                }
            }),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getADMDatasetConfigs: function () {
                var dataSets = $q.defer();

                UserService.getUser()
                    .then(function (user) {
                        return admListResource.get({
                            agencyId: user.agencyId
                        }).$promise;
                    })
                    .then(getADMDatasetConfigsComplete)
                    .catch(errorHandler('Cannot get ADM data sets', dataSets));

                function getADMDatasetConfigsComplete(listData) {
                    var response = listData,
                        _dataSets = lodash.compact([].concat(
                            response &&
                            response.records &&
                            response.records[0] &&
                            response.records[0].Dataset));

                    dataSets.resolve(_dataSets);
                }

                return dataSets.promise;
            },

            getAdmDetails: function (dataId) {
                var admDetails = $q.defer();

                admResource.get({
                    id: dataId
                }).$promise
                    .then(getAdmDetailsComplete)
                    .catch(errorHandler('Cannot get ADM details', admDetails));

                function getAdmDetailsComplete(response) {
                    admDetails.resolve(response);
                }

                return admDetails.promise;
            },

            updateAdmDetails: function (admData) {
                var admUpdated = $q.defer();

                admResource.update({
                    id: admData.datasetId
                }, admData).$promise
                    .then(updateAdmDetailsComplete)
                    .catch(errorHandler('Cannot update ADM details', admUpdated));

                function updateAdmDetailsComplete(response) {
                    admUpdated.resolve(response);
                }

                return admUpdated.promise;
            }
        };
    }
}());
