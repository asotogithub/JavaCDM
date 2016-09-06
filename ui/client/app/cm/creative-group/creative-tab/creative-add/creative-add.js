(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = [
        '$stateProvider'
    ];

    function State(
        $stateProvider) {
        $stateProvider
            .state('creative-group-creative-add', {
                parent: 'creative-group-creative-tab',
                url: '/add',
                views: {
                    'creative.add': {
                        templateUrl: 'app/cm/creative-group/creative-tab/creative-add/creative-add.html',
                        controller: 'CreativeGroupCreativeAddController',
                        controllerAs: 'vmAdd'
                    }
                },
                ncyBreadcrumb: {
                    label: '{{"creative.addCreative"| translate}}',
                    parent: 'creative-group'
                }
            });
    }
})();
