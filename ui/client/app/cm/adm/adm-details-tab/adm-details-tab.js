(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('adm-details-tab', {
                parent: 'adm-details',
                url: '',
                templateUrl: 'app/cm/adm/adm-details-tab/adm-details-tab.html',
                controller: 'AdmDetailsTabController',
                controllerAs: 'admDetails',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
