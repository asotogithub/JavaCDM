(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('page-404', {
                parent: 'main',
                url: '404',
                templateUrl: 'app/cm/error-pages/page-404/page-404.html',
                controller: 'Page404Controller',
                controllerAs: 'vm',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
