(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaigns', {
                parent: 'main',
                url: 'campaigns',
                templateUrl: 'app/cm/campaigns/campaigns.html',
                controller: 'CampaignsController',
                controllerAs: 'vm',
                redirectTo: 'campaigns-list',
                ncyBreadcrumb: {
                    label: '{{"nav.campaigns"| translate}}'
                }
            });
    }
})();
