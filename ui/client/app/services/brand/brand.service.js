(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('BrandService', BrandService);

    BrandService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler'
    ];

    function BrandService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler) {
        var brandResource = $resource(API_SERVICE + 'Brands/:id/:criteria', {
                id: '@id',
                criteria: '@criteria'
            }),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getPlacements: function (brandId) {
                var placements = $q.defer();

                brandResource.get({
                    id: brandId,
                    criteria: 'placements'
                }).$promise
                    .then(getPlacementBrand)
                    .catch(errorHandler('Cannot get placement list of brand', placements));

                function getPlacementBrand(response) {
                    placements.resolve(response);
                }

                return placements.promise;
            }
        };
    }
})();
