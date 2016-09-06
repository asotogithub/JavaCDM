(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('main', {
                url: '/',
                templateUrl: 'app/cm/main/main.html',
                controller: 'MainController',
                controllerAs: 'mainVm',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
