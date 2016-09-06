(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('creative-list', {
                parent: 'creative-tab',
                url: '/list',
                views: {
                    'creative.list': {
                        templateUrl: 'app/cm/campaigns/creative-tab/creative-list/creative-list.html',
                        controller: 'CampaignCreativeListController',
                        controllerAs: 'vm'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
