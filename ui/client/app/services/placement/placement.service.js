(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('PlacementService', PlacementService);

    PlacementService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function PlacementService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var placementResource = $resource(API_SERVICE + 'Placements/:id/:criteria', {
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
            getAdTags: function (placementId) {
                var adTagsDetails = $q.defer();

                placementResource.get({
                    id: placementId,
                    criteria: 'getAdTagsByPlacement'
                }).$promise
                    .then(getAdTagsDetails)
                    .catch(errorHandler('Cannot get Ad-Tag Details by placement', adTagsDetails));

                function getAdTagsDetails(response) {
                    adTagsDetails.resolve(response);
                }

                return adTagsDetails.promise;
            },

            getStandAlonePlacements: function (campaignId) {
                var placements = $q.defer();

                placementResource.get({
                    query: 'campaignId=' + campaignId + ' and packageId is null'
                }).$promise
                    .then(getStandAlonePlacementsComplete)
                    .catch(errorHandler('Cannot get placement list', placements));

                function getStandAlonePlacementsComplete(response) {
                    placements.resolve([].concat(lodash.result(response, 'records[0].Placement', [])));
                }

                return placements.promise;
            },

            getPlacement: function (placementId) {
                var placement = $q.defer();

                placementResource.get({
                    id: placementId
                }).$promise
                    .then(getPlacementComplete)
                    .catch(errorHandler('Error while performing get placement request', placement));
                function getPlacementComplete(response) {
                    placement.resolve(response);
                }

                return placement.promise;
            },

            updatePlacement: function (placement) {
                var placementDefer = $q.defer();

                placementResource.update({
                    id: placement.id
                }, placement).$promise
                    .then(updatePlacementComplete)
                    .catch(updatePlacementFailed);

                function updatePlacementComplete(response) {
                    placementDefer.resolve(response);
                }

                function updatePlacementFailed(error) {
                    placementDefer.reject(error);
                }

                return placementDefer.promise;
            },

            updatePlacementsStatus: function (ioId, placements) {
                var placementsDefer = $q.defer();

                placementResource.update({
                    ioId: ioId,
                    criteria: 'status'
                }, {
                    records: {
                        Placement: placements
                    }
                }).$promise
                    .then(updatePlacementsStatusComplete)
                    .catch(errorHandler('Error while performing update placements request', placementsDefer));

                function updatePlacementsStatusComplete(response) {
                    placementsDefer.resolve(response);
                }

                return placementsDefer.promise;
            },

            saveBulkPlacements: function (ioId, packageId, placements) {
                var placementsDefer = $q.defer();

                placementResource.save({
                    ioId: ioId,
                    packageId: packageId,
                    criteria: 'bulk'
                }, {
                    records: {
                        Placement: placements
                    }
                }).$promise
                    .then(saveBulkPlacementsComplete)
                    .catch(errorHandler('Error while performing save placements request', placementsDefer));

                function saveBulkPlacementsComplete(response) {
                    placementsDefer.resolve(response);
                }

                return placementsDefer.promise;
            },

            getHtmlInjectionTags: function (placementId) {
                var deferred = $q.defer();

                placementResource.get({
                    id: placementId,
                    criteria: 'htmlInjectionTags'
                }).$promise
                    .then(getHtmlInjectionTagsComplete)
                    .catch(errorHandler('Cannot get associated Tracking Tags', deferred));

                function getHtmlInjectionTagsComplete(response) {
                    deferred.resolve([].concat(lodash.result(response, 'records[0].HtmlInjectionTags', [])));
                }

                return deferred.promise;
            },

            sendTagEmail: function (tagEmails) {
                var sendTagEmailDefer = $q.defer();

                placementResource
                    .save({
                        criteria: 'sendTagEmail'
                    },
                    tagEmails)
                    .$promise
                    .then(sendTagEmailComplete)
                    .catch(errorHandler('Error while performing send tag email request', sendTagEmailDefer));

                function sendTagEmailComplete(response) {
                    sendTagEmailDefer.resolve(response);
                }

                return sendTagEmailDefer.promise;
            },

            disassociateFromPackage: function (placementId) {
                var disassociateDetails = $q.defer();

                placementResource.update({
                    id: placementId,
                    criteria: 'disassociateFromPackage'
                }).$promise
                    .then(disassociateFromPackageDetails)
                    .catch(function (error) {
                        disassociateDetails.reject(error);
                    });

                function disassociateFromPackageDetails(response) {
                    disassociateDetails.resolve(response);
                }

                return disassociateDetails.promise;
            },

            deleteHtmlInjectionTagsBulk: function (placementId, tagList) {
                var deferred = $q.defer();

                placementResource.update({
                    id: placementId,
                    criteria: 'deleteHtmlInjectionTagsBulk'
                }, {
                    records: {
                        Long: tagList
                    }
                }).$promise
                    .then(deleteHtmlInjectionTagsBulkComplete)
                    .catch(errorHandler('Error while performing delete request', deferred));

                function deleteHtmlInjectionTagsBulkComplete(response) {
                    deferred.resolve(response);
                }

                return deferred.promise;
            }
        };
    }
})();
