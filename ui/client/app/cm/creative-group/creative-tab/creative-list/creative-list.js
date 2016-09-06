(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('creative-group-creative-list', {
                parent: 'creative-group-creative-tab',
                url: '/list',
                views: {
                    'creative.list': {
                        templateUrl: 'app/cm/creative-group/creative-tab/creative-list/creative-list.html',
                        controller: 'CreativeGroupCreativeListController',
                        controllerAs: 'vm'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
