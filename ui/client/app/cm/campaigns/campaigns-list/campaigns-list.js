(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaigns-list', {
                parent: 'campaigns',
                url: '/',
                templateUrl: 'app/cm/campaigns/campaigns-list/campaigns-list.html',
                controller: 'CampaignsListController',
                controllerAs: 'vm',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
