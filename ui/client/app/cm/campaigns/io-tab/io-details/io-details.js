(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-io-details', {
                parent: 'campaigns',
                url: '/:campaignId/io/:ioId',
                templateUrl: 'app/cm/campaigns/io-tab/io-details/io-details.html',
                controller: 'CampaignIoDetailsController',
                controllerAs: 'vm',
                redirectTo: 'campaign-io-edit',
                ncyBreadcrumb: {
                    label: '{{vm.ioName || ioName}}',
                    parent: 'insertion-order-tab'
                }
            });
    }
})();
