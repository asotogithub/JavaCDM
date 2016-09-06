(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('package-tab', {
                parent: 'campaign-io-details',
                url: '/package',
                templateUrl: 'app/cm/campaigns/io-tab/package-tab/package-tab.html',
                redirectTo: 'package-list',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
