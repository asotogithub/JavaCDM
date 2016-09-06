(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignDetailsController', CampaignDetailsController);

    CampaignDetailsController.$inject = [
        '$rootScope',
        '$state',
        '$stateParams',
        'CampaignsService'
    ];

    function CampaignDetailsController(
        $rootScope,
        $state,
        $stateParams,
        CampaignsService) {
        var vm = this,
            campaignId = parseInt($stateParams.campaignId);

        vm.goToTagInjection = goToTagInjection;

        function goToTagInjection() {
            var params = {
                campaign: {
                    id: campaignId,
                    brandId: $rootScope.campaign.brand.id,
                    advertiserId: $rootScope.campaign.advertiser.id,
                    currentState: $state.current.name
                }
            };

            $state.go('tag-injection-standard',
                params
            );
        }

        CampaignsService.getCampaignMetrics(campaignId).then(function (metrics) {
            vm.metrics = CampaignsService.buildKpiDisplay(metrics);
        });
    }
})();
