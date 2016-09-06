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
            .state('adm-details', {
                parent: 'adm',
                url: '/id/:id',
                templateUrl: 'app/cm/adm/adm-details/adm-details.html',
                controller: 'AdmDetailsController',
                controllerAs: 'vm',
                redirectTo: 'adm-details-tab',
                permission: CONSTANTS.PERMISSION.ADM.CONFIG,
                ncyBreadcrumb: {
                    label: '{{vm.adm.fileNamePrefix}}',
                    parent: 'adm-list'
                }
            });
    }
})();
