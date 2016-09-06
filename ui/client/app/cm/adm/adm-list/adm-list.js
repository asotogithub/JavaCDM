(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = [
        '$stateProvider',
        'CONSTANTS'
    ];

    function State(
        $stateProvider,
        CONSTANTS) {
        $stateProvider
            .state('adm-list', {
                parent: 'adm',
                url: '/',
                templateUrl: 'app/cm/adm/adm-list/adm-list.html',
                controller: 'AdmListController',
                controllerAs: 'vm',
                permission: CONSTANTS.PERMISSION.ADM.LIST,
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
