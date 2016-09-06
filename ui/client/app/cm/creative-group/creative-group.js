(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('creative-group', {
                parent: 'campaigns',
                url: '/id/:campaignId/creative-groups/id/:creativeGroupId/',
                templateUrl: 'app/cm/creative-group/creative-group.html',
                controller: 'CreativeGroupController',
                controllerAs: 'vm',
                redirectTo: 'creative-group-details-tab',
                ncyBreadcrumb: {
                    label: '{{vm.crtvGrpName || creativeGroupName || "creativeGroup.newCreativeGroup" | translate}}',
                    parent: 'campaign-details'
                }
            });
    }
})();
