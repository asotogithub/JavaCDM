(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('new-creative-group', {
                parent: 'campaigns',
                url: '/id/:campaignId/creative-groups/new',
                templateUrl: 'app/cm/creative-group/new-creative-group/new-creative-group.html',
                controller: 'NewCreativeGroupController',
                controllerAs: 'vm',
                ncyBreadcrumb: {
                    label: '{{"creativeGroup.newCreativeGroup"| translate}}',
                    parent: 'campaign-details'
                }
            });
    }
})();
