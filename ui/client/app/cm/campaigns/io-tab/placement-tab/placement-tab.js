(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('placement-tab', {
                parent: 'campaign-io-details',
                url: '/placement',
                templateUrl: 'app/cm/campaigns/io-tab/placement-tab/placement-tab.html',
                redirectTo: 'placement-list',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
