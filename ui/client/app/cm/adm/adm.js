(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('adm', {
                parent: 'main',
                url: 'adm',
                templateUrl: 'app/cm/adm/adm.html',
                controller: 'admController',
                controllerAs: 'vm',
                redirectTo: 'adm-list',
                ncyBreadcrumb: {
                    label: '{{"global.adm"| translate}}'
                }
            });
    }
})();
