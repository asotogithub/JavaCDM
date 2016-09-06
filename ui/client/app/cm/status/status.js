(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('status', {
                url: '/status',
                templateUrl: 'app/cm/status/status.html',
                controller: 'StatusController',
                controllerAs: 'vm',
                ncyBreadcrumb: {
                    skip: true
                },
                noAuth: true
            });
    }
})();
