(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(States);

    States.$inject = ['$stateProvider'];

    function States($stateProvider) {
        $stateProvider
            .state('creative-group-details-tab', {
                parent: 'creative-group',
                url: 'details',
                templateUrl: 'app/cm/creative-group/creative-group-details-tab/creative-group-details-tab.html',
                controller: 'CreativeGroupDetailsTabController',
                controllerAs: 'vmDetails',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
