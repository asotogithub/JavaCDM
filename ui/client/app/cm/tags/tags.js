(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('tags', {
                parent: 'main',
                url: 'tags',
                templateUrl: 'app/cm/tags/tags.html',
                controller: 'TagsController',
                controllerAs: 'vmTags',
                redirectTo: 'tag-placement-list',
                ncyBreadcrumb: {
                    label: '{{"global.tags"| translate}}'
                }
            });
    }
})();

