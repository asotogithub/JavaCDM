(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-io-list', {
                parent: 'insertion-order-tab',
                url: '/list',
                views: {
                    'io.list': {
                        templateUrl: 'app/cm/campaigns/io-tab/io-list/io-list.html',
                        controller: 'InsertionOrderListTabController',
                        controllerAs: 'vm'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
