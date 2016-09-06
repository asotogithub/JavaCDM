(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('tag-placement-list', {
                parent: 'tags',
                url: '/',
                templateUrl: 'app/cm/tags/tag-placement-list/tag-placement-list.html',
                controller: 'TagPlacementListController',
                controllerAs: 'vmTagPlacement',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
