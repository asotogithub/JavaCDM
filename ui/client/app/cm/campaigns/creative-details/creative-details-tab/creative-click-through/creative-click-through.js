(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-creative-details-click-through', {
                parent: 'campaign-creative-details-tab',
                url: '/click-through',
                templateUrl: 'app/cm/campaigns/creative-details/creative-details-tab/' +
                             'creative-click-through/creative-click-through.html',
                controller: 'CampaignCreativeEditClickThroughController',
                controllerAs: 'vmCT',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
