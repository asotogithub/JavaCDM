(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('MediaBuyService', MediaBuyService);

    MediaBuyService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler'
    ];

    function MediaBuyService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler) {
        var mediaBuyResource = $resource(API_SERVICE + 'MediaBuys/byCampaign/:campaignId'),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getMediaBuyByCampaign: function (campaignId) {
                var mediaBuyDetails = $q.defer();

                mediaBuyResource.get({
                    campaignId: campaignId
                }).$promise
                    .then(getMediaBuyByCampaign)
                    .catch(errorHandler('Cannot get mediaBuy details', mediaBuyDetails));

                function getMediaBuyByCampaign(response) {
                    mediaBuyDetails.resolve(response.toJSON());
                }

                return mediaBuyDetails.promise;
            }
        };
    }
})();
