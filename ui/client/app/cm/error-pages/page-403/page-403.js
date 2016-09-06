(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('page-403', {
                parent: 'main',
                url: '403',
                templateUrl: 'app/cm/error-pages/page-403/page-403.html',
                controller: 'Page403Controller',
                controllerAs: 'vm',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
