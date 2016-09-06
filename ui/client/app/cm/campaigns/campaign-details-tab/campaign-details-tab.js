(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-details-tab', {
                parent: 'campaign-details',
                url: 'details',
                templateUrl: 'app/cm/campaigns/campaign-details-tab/campaign-details-tab.html',
                controller: 'CampaignDetailsTabController',
                controllerAs: 'vmDetails',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
