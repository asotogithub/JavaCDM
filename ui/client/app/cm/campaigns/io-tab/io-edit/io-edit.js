(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-io-edit', {
                parent: 'campaign-io-details',
                url: '/edit',
                templateUrl: 'app/cm/campaigns/io-tab/io-edit/io-edit.html',
                controller: 'CampaignIoEditController',
                controllerAs: 'vmEdit',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
