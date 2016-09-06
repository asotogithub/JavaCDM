(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('AdvertiserService', AdvertiserService);

    AdvertiserService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function AdvertiserService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var advertiserResource = $resource(API_SERVICE + 'Advertisers/:id/:criteria',
            {
                id: '@id',
                criteria: '@criteria'
            }, {
                get: {
                    method: 'GET'
                }
            }),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getAdvertiserBrands: function (advertiserId) {
                var advertiserBrands = $q.defer();

                advertiserResource.get({
                    id: advertiserId,
                    criteria: 'brands'
                }).$promise
                    .then(getAdvertiserBrandsComplete)
                    .catch(errorHandler('Cannot get Advertiser Brands', advertiserBrands));

                function getAdvertiserBrandsComplete(response) {
                    advertiserBrands.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].Brand)));
                }

                return advertiserBrands.promise;
            }
        };
    }
})();
