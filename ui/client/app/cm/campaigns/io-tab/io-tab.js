(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('insertion-order-tab', {
                parent: 'campaign-details',
                url: 'io',
                templateUrl: 'app/cm/campaigns/io-tab/io-tab.html',
                redirectTo: 'campaign-io-list',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
