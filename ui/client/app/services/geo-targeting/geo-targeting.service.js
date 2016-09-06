(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('GeoTargetingService', GeoTargetingService);

    GeoTargetingService.$inject = ['$q', '$resource', 'API_SERVICE', 'ErrorRequestHandler', 'lodash'];

    function GeoTargetingService($q, $resource, API_SERVICE, ErrorRequestHandler, lodash) {
        var geos = $resource(
                API_SERVICE + 'GeoLocations/:locationType',
                {
                    locationType: '@locationType'
                },
                {
                    get: {
                        cache: true,
                        method: 'GET'
                    }
                }
            ),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        lodash.extend(this, {
            getCountries: function () {
                var countries = $q.defer();

                geos.get({
                    locationType: 'countries'
                }).$promise
                    .then(getCountriesComplete)
                    .catch(errorHandler('Cannot get countries', countries));

                function getCountriesComplete(data) {
                    countries.resolve(data.records[0].GeoLocation);
                }

                return countries.promise;
            },

            getStates: function () {
                var states = $q.defer();

                geos.get({
                    locationType: 'states'
                }).$promise
                    .then(getStatesComplete)
                    .catch(errorHandler('Cannot get states', states));

                function getStatesComplete(data) {
                    states.resolve(data.records[0].GeoLocation);
                }

                return states.promise;
            },

            getDMAs: function () {
                var dmas = $q.defer();

                geos.get({
                    locationType: 'dmas'
                }).$promise
                    .then(getDMAsComplete)
                    .catch(errorHandler('Cannot get DMAs', dmas));

                function getDMAsComplete(data) {
                    dmas.resolve(data.records[0].GeoLocation);
                }

                return dmas.promise;
            },

            getZips: function () {
                var zips = $q.defer();

                geos.get({
                    locationType: 'zips'
                }).$promise
                    .then(getZipsComplete)
                    .catch(errorHandler('Cannot get zips', zips));

                function getZipsComplete(data) {
                    zips.resolve(data.records[0].GeoLocation);
                }

                return zips.promise;
            }
        });
    }
})();
