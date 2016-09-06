(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('placement-list', {
                parent: 'placement-tab',
                url: '/list',
                views: {
                    'placement.list': {
                        templateUrl: 'app/cm/campaigns/io-tab/placement-tab/placement-list/placement-list.html',
                        controller: 'PlacementListController',
                        controllerAs: 'vm'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
