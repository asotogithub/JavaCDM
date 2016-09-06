(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('creative-group-creative-tab', {
                parent: 'creative-group',
                url: 'creative',
                templateUrl: 'app/cm/creative-group/creative-tab/creative-tab.html',
                redirectTo: 'creative-group-creative-list',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
