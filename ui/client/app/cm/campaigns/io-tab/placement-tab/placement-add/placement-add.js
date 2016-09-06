(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('add-placement', {
                parent: 'placement-tab',
                url: '/add',
                views: {
                    'placement.add': {
                        templateUrl: 'app/cm/campaigns/io-tab/placement-tab/placement-add/placement-add.html',
                        controller: 'AddPlacementController',
                        controllerAs: 'vmAdd'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
