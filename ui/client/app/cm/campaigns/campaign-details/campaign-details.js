(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-details', {
                parent: 'campaigns',
                url: '/id/:campaignId/',
                templateUrl: 'app/cm/campaigns/campaign-details/campaign-details.html',
                controller: 'CampaignDetailsController',
                controllerAs: 'vm',
                redirectTo: 'campaign-details-tab',
                ncyBreadcrumb: {
                    label: '{{vm.campaignName || campaignName}}',
                    parent: 'campaigns-list'
                },
                params: {
                    campaign: {
                        id: '',
                        brandId: '',
                        advertiserId: ''
                    }
                }
            });
    }
})();
