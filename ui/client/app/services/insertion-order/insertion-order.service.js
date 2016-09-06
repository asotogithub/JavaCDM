(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('InsertionOrderService', InsertionOrderService);

    InsertionOrderService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function InsertionOrderService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var insertionOrderResource = $resource(API_SERVICE + 'InsertionOrders/:id/:criteria', {
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
            getList: function (campaignId) {
                var list = $q.defer();

                insertionOrderResource.get({
                    query: 'campaignId=' + campaignId
                }).$promise
                    .then(getListComplete)
                    .catch(errorHandler('Cannot get insertion order list', list));

                function getListComplete(response) {
                    list.resolve([].concat(lodash.result(response, 'records[0].InsertionOrder', [])));
                }

                return list.promise;
            },

            getInsertionOrder: function (ioId) {
                var insertionOrder = $q.defer();

                insertionOrderResource.get({
                    id: ioId
                }).$promise
                    .then(getInsertionOrderComplete)
                    .catch(errorHandler('Error while performing get insertion order request', insertionOrder));

                function getInsertionOrderComplete(response) {
                    insertionOrder.resolve(response.toJSON());
                }

                return insertionOrder.promise;
            },

            getPackagePlacements: function (ioId) {
                var packagePlacements = $q.defer();

                insertionOrderResource.get({
                    id: ioId,
                    criteria: 'packagePlacementView'
                }).$promise
                    .then(getPackagePlacementsComplete)
                    .catch(errorHandler('Cannot get package placements list', packagePlacements));

                function getPackagePlacementsComplete(response) {
                    packagePlacements.resolve(
                        [].concat(lodash.result(response, 'records[0].PackagePlacementView', [])));
                }

                return packagePlacements.promise;
            },

            updateInsertionOrder: function (insertionOrder) {
                var ioDefer = $q.defer();

                insertionOrderResource.update({
                    id: insertionOrder.id
                }, insertionOrder).$promise
                    .then(updateInsertionOrderComplete)
                    .catch(errorHandler('Error while performing update insertion order request', ioDefer));

                function updateInsertionOrderComplete(response) {
                    ioDefer.resolve(response.toJSON());
                }

                return ioDefer.promise;
            },

            saveInsertionOrder: function (ioDetails) {
                var insertionOrder = $q.defer();

                insertionOrderResource.save(ioDetails).$promise
                    .then(saveInsertionOrderComplete)
                    .catch(errorHandler('Error while performing save insertion order request', insertionOrder));

                function saveInsertionOrderComplete(ioData) {
                    insertionOrder.resolve(ioData.toJSON());
                }

                return insertionOrder.promise;
            },

            saveBulkPackagePlacement: function (ioId, bulkPackagePlacement) {
                var packagePlacement = $q.defer();

                insertionOrderResource.save({
                    id: ioId,
                    criteria: 'bulkPackagePlacement'
                }, bulkPackagePlacement).$promise
                    .then(saveBulkPackagePlacementComplete)
                    .catch(saveBulkPackagePlacementFailed);

                function saveBulkPackagePlacementComplete(response) {
                    packagePlacement.resolve(response);
                }

                function saveBulkPackagePlacementFailed(error) {
                    packagePlacement.reject(error);
                }

                return packagePlacement.promise;
            }
        };
    }
})();
