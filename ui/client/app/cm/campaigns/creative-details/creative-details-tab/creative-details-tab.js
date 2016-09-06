(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = [
        '$stateProvider'
    ];

    function State(
        $stateProvider) {
        $stateProvider
            .state('campaign-creative-details-tab', {
                parent: 'campaign-creative-details',
                url: '/details',
                templateUrl: 'app/cm/campaigns/creative-details/creative-details-tab/creative-details-tab.html',
                controller: 'CampaignCreativeDetailsTabController',
                controllerAs: 'vmEdit',
                redirectTo: 'campaign-creative-details-click-through',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
