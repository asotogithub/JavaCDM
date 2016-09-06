(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('io-add', {
                parent: 'insertion-order-tab',
                url: '/add',
                views: {
                    'io.add': {
                        templateUrl: 'app/cm/campaigns/io-tab/io-add/io-add.html',
                        controller: 'IoAddController',
                        controllerAs: 'vmAddIO'
                    }
                },
                ncyBreadcrumb: {
                    label: '{{"insertionOrder.createInsertionOrder"| translate}}',
                    parent: 'insertion-order-tab'
                }
            });
    }
})();
