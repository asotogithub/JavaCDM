(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('creative-tab', {
                parent: 'campaign-details',
                url: 'creative',
                templateUrl: 'app/cm/campaigns/creative-tab/creative-tab.html',
                redirectTo: 'creative-list',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
