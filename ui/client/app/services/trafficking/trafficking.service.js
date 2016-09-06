(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('TraffickingService', TraffickingService);

    TraffickingService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE'
    ];

    function TraffickingService(
        $q,
        $resource,
        API_SERVICE) {
        var traffickingResource = $resource(API_SERVICE + 'Trafficking/:criteria', {
                criteria: '@criteria'
            });

        return {
            validateCampaign: function (campaignId) {
                var validate = $q.defer();

                traffickingResource.get({
                    criteria: 'validate',
                    campaignId: campaignId
                }).$promise
                    .then(validateCampaignComplete)
                    .catch(validateCampaignFailed);

                function validateCampaignComplete(response) {
                    validate.resolve(response);
                }

                function validateCampaignFailed(error) {
                    validate.reject(error);
                }

                return validate.promise;
            },

            trafficking: function (traffic) {
                var traffickingDefer = $q.defer();

                traffickingResource
                    .save(traffic)
                    .$promise
                    .then(traffickingComplete)
                    .catch(traffickingFailed);

                function traffickingComplete(response) {
                    traffickingDefer.resolve(response);
                }

                function traffickingFailed(error) {
                    traffickingDefer.reject(error);
                }

                return traffickingDefer.promise;
            }
        };
    }
})();
