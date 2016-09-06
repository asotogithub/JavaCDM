(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('PackageService', PackageService);

    PackageService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler'
    ];

    function PackageService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler) {
        var packageResource = $resource(API_SERVICE + 'Packages/:id/:criteria', {
                id: '@id',
                criteria: '@criteria'
            }, {
                update: {
                    method: 'PUT'
                }
            }),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getPackage: function (packageId) {
                var packageDefer = $q.defer();

                packageResource.get({
                    id: packageId
                }).$promise
                    .then(getPackageComplete)
                    .catch(errorHandler('Cannot get placement list', packageDefer));

                function getPackageComplete(response) {
                    packageDefer.resolve(response);
                }

                return packageDefer.promise;
            },

            updatePackage: function (packageId, pack) {
                var packageDefer = $q.defer();

                packageResource.update({
                    id: packageId
                }, pack).$promise
                    .then(updatePackageComplete)
                    .catch(errorHandler('Error while performing update placements request', packageDefer));
                function updatePackageComplete(response) {
                    packageDefer.resolve(response);
                }

                return packageDefer.promise;
            },

            addAllowRemoveProperty: function (placementList) {
                var placementsLength = placementList.length,
                    p;

                for (p = 0; p < placementsLength; p++) {
                    placementList[p].allowRemove = true;
                }
            }
        };
    }
})();
