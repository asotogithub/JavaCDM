(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('creative-groups-tab', {
                parent: 'campaign-details',
                url: 'creative-groups',
                templateUrl: 'app/cm/campaigns/creative-groups-tab/creative-groups-tab.html',
                controller: 'CreativeGroupsTabController',
                controllerAs: 'vmCrtvGrpsTab',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
